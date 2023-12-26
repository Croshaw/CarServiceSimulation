package me.croshaw.carservicesimulation.simulation.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import me.croshaw.carservicesimulation.drawers.WorkshopDrawer;
import me.croshaw.carservicesimulation.simulation.core.service.Service;
import me.croshaw.carservicesimulation.simulation.core.util.CreatedInfo;
import me.croshaw.carservicesimulation.simulation.core.util.DurationHelper;
import me.croshaw.carservicesimulation.simulation.core.util.Pair;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

public class Workshop extends CreatedInfo {
    private final Service service;
    private WorkshopDrawer drawer;
    private final LinkedList<Pair<Double, Double>> coordinatesForMasters;
    private final LinkedList<Pair<Double, Double>> coordinatesForWorkServicing;
    private final String name;
    private final HashSet<Master> masters;
    private final Deque<WorkServicing> queue;
    private final HashMap<LocalDate, Pair<Integer, Integer>> queueLength;
    private final HashMap<LocalDate, ArrayList<WorkServicing>> droppedWorkServicingMap;
    private final Duration maxServicingDuration;
    private final Duration maxWaitingDuration;
    private Duration currentLifeDuration;
    private final double minSalary;
    private final float profitShare;
    private final int maxMasters = 7;
    public Workshop(Service service, String name, LocalDateTime dateTimeOfCreation, Duration maxServicingDuration, Duration maxWaitingDuration, double minSalary, float profitShare) {
        super(dateTimeOfCreation);
        this.service = service;
        this.name = name;
        this.masters = new HashSet<>();
        this.queue = new ArrayDeque<>();
        this.queueLength = new HashMap<>();
        this.droppedWorkServicingMap = new HashMap<>();
        this.maxServicingDuration = maxServicingDuration;
        this.maxWaitingDuration = maxWaitingDuration;
        this.minSalary = minSalary;
        this.profitShare = profitShare;
        currentLifeDuration = Duration.ZERO;
        coordinatesForMasters = new LinkedList<>();
        coordinatesForWorkServicing = new LinkedList<>();
    }
    public boolean isDrawerSetup() {
        return drawer != null;
    }
    public void setupMasterCoordinates(double newX, double newY) {
        if(isDrawerSetup()) {
            coordinatesForMasters.clear();
            double mW = drawer.getWidth() / maxMasters;
            double mH = drawer.getHeight() / maxMasters;
            double xOffset = mW / 5;
            double yOffset = mH / 10;
            double mX = xOffset;
            double mY = yOffset;
            for (int i = 0; i < maxMasters; i++) {
                if (mX + mW > drawer.getWidth()) {
                    mX = xOffset;
                    mY += mH + yOffset;
                }
                var c = drawer.getLastMove();
                coordinatesForMasters.addLast(new Pair<>(newX + mX, newY + mY));
                mX += mW + xOffset;
            }
        }
    }
    public void setupWorkServicingCoordinates(double newX, double newY) {
        if(isDrawerSetup()) {
            coordinatesForWorkServicing.clear();
            double mW = (drawer.getWidth() / maxMasters) / 2;
            double mH = (drawer.getHeight() / maxMasters) / 2;
            double xOffset = mW / 5;
            double yOffset = mH / 5;
            double mX = xOffset;
            double mY = yOffset;
            for (int i = 0; i < 99; i++) {
                if (mX + mW > drawer.getWidth()) {
                    mX = xOffset;
                    mY += mH + yOffset;
                }
                coordinatesForWorkServicing.addLast(new Pair<>(newX + mX, newY + drawer.getHeight()/2 + mY));
                mX += mW + xOffset;
            }
        }
    }
    public void setDrawer(double x, double y, double width, double height) {
        drawer = new WorkshopDrawer(this, x, y, width, height);
        setupMasterCoordinates(x, y);
        setupWorkServicingCoordinates(x, y);
    }
    public void setSpeed(double value) {
        if(isDrawerSetup())
            drawer.setSpeed(value);
        queue.forEach(q->q.setSpeed(value));
        masters.forEach(m -> m.setSpeed(value));
    }
    public void move(double x, double y) {
        if(drawer != null) {
            drawer.move(x, y);
        }
        setupMasterCoordinates(x, y);
        setupWorkServicingCoordinates(x, y);
    }
    public void draw(GraphicsContext g) {
        if(drawer != null){
            drawer.draw(g);
        }
        queue.forEach(q -> q.draw(g));
        masters.forEach(m -> m.draw(g));
    }
    public boolean mouseMovedEvent(MouseEvent event) {
        drawer.drawTooltip = false;
        for(var m : masters) {
            if(m.mouseMovedEvent(event)) {
                return true;
            }
        }
        for(var q : queue) {
            if(q.mouseMovedEvent(event)) {
                return true;
            }
        }
        if(drawer == null)
            return false;
        return drawer.mouseMovedEvent(event);
    }
    public void drawTooltip(GraphicsContext g) {
        if(drawer != null){
            drawer.drawTooltip(g);
        }
        queue.forEach(q -> q.drawTooltip(g));
        masters.forEach(m -> m.drawTooltip(g));
    }
    public boolean isFixit() {
        boolean res = true;
        for(var q : queue) {
            res = res && q.isFixit();
        }
        return (drawer == null || drawer.isFixit()) && res;
    }
    public void balanceMastersCoordinates() {
        int i = 0;
        if(coordinatesForMasters.isEmpty())
            setupMasterCoordinates(drawer.getX(), drawer.getY());
        for(var master : masters) {
            var p = coordinatesForMasters.get(i++);
            master.move(p.getFirst(), p.getSecond());
        }
    }
    public void balanceWorkServicingCoordinates() {
        int i = 0;
        if(coordinatesForWorkServicing.isEmpty())
            setupWorkServicingCoordinates(drawer.getX(), drawer.getY());
        try {
            for (var q : queue) {
                var p = coordinatesForWorkServicing.get(i++);
                q.move(p.getFirst(), p.getSecond());
            }
        }
        catch (Exception e) {
            System.out.println("qSi: %d curI: %d".formatted(queue.size(), i));
        }
    }
    public boolean hire(Master master) {
        if(masters.size() >= maxMasters)
            return false;
        if (drawer != null) {
            double width = drawer.getWidth() / maxMasters;
            double height = drawer.getHeight() / maxMasters;
            double x = drawer.getX();
            double y = drawer.getY();
            var p = coordinatesForMasters.get(masters.size());
            if (!master.isDrawerSetup()) {
                master.setDrawer(x, y, width, height);
                master.setSpeed(drawer.getSpeed());
                master.move(p.getFirst(), p.getSecond());
            }
        }
        return masters.add(master);
    }
    public boolean hire(String surname, String name, String patronymic) {
        return hire(new Master(surname, name, patronymic, maxServicingDuration, maxWaitingDuration));
    }
    public boolean fire(Master master) {
        boolean res = masters.remove(master);
        balanceMastersCoordinates();
        return res;
    }
    public boolean registerWorkServicing(WorkServicing workServicing) {
        if(queue.contains(workServicing) || queue.size() >= 99)
            return false;
        if(queue.offer(workServicing)) {
            if(!queueLength.containsKey(getCurrentDate()))
                queueLength.put(getCurrentDate(), new Pair<>(Integer.MAX_VALUE, Integer.MIN_VALUE));
            var pair = queueLength.get(getCurrentDate());
            if(pair.getFirst() > queue.size())
                pair.setFirst(queue.size());
            if(pair.getSecond() < queue.size())
                pair.setSecond(queue.size());
            if(isDrawerSetup()) {
                if (!workServicing.isDrawerSetup()) {
                    double mW = (drawer.getWidth() / maxMasters) / 2;
                    double mH = (drawer.getHeight() / maxMasters) / 2;
                    workServicing.setDrawer(drawer.getX(), drawer.getY(), mW, mH);
                    workServicing.setSpeed(drawer.getSpeed());
                }
                balanceWorkServicingCoordinates();
            }
            return true;
        }
        return false;
    }
    public void checkToDrop() {
        LinkedList<WorkServicing> toRem = new LinkedList<>();
        for(var q : queue) {
            if(q.getWaitingDuration().compareTo(maxWaitingDuration) >= 0
                    || q.getWorkDuration().compareTo(maxServicingDuration) >= 0) {
                q.drop();
                if(!droppedWorkServicingMap.containsKey(getCurrentDate()))
                    droppedWorkServicingMap.put(getCurrentDate(), new ArrayList<>());
                droppedWorkServicingMap.get(getCurrentDate()).add(q);
                toRem.add(q);
            }
        }
        toRem.forEach(queue::remove);
        for (Master master : masters) {
            master.checkToDrop(getCurrentDate());
        }
    }
    public boolean tryToTakeTaskByMaster(Master master) {
        if(!master.isWork()) {
            if(!queue.isEmpty()) {
                var task = queue.removeFirst();
                LinkedList<WorkServicing> temp = new LinkedList<>();
                while (task.isCarBusy() && !queue.isEmpty()) {
                    temp.addFirst(task);
                    task = queue.removeFirst();
                }
                if (!temp.isEmpty())
                    queue.addFirst(temp.removeFirst());
                if (task != null) {
                    master.setTask(task);
                    return true;
                }
            }
        }
        return false;
    }
    public void work(long secondsStep) {
        for(var master : masters) {
            long seconds = secondsStep;
            tryToTakeTaskByMaster(master);
            while (seconds != 0) {
                seconds = master.work(secondsStep, getCurrentDate());
                currentLifeDuration = currentLifeDuration.plusSeconds(secondsStep - seconds);
                if(seconds != 0 && !tryToTakeTaskByMaster(master))
                    break;
            }
            currentLifeDuration = currentLifeDuration.minusSeconds(secondsStep);
        }
    }
    public void life(long secondsStep, boolean isWork) {
        if(isWork) {
            //queue.forEach(x -> x.waiting(secondsStep));
            work(secondsStep);
        }
        currentLifeDuration = currentLifeDuration.plusSeconds(secondsStep);
    }
    public Master[] getMasters() {
        return masters.toArray(Master[]::new);
    }
    public Duration getAverageWaitingDuration() {
        Duration avg = Duration.ZERO;
        for(var master : masters)
            avg = avg.plus(master.getAverageWaitingDuration());
        if(masters.isEmpty())
            return Duration.ZERO;
        return avg.dividedBy(masters.size());
    }
    public Duration getAverageEmployementDuration() {
        Duration avg = Duration.ZERO;
        for(var master : masters)
            avg = avg.plus(master.getAverageEmploymentDuration());
        if(masters.isEmpty())
            return Duration.ZERO;
        return avg.dividedBy(masters.size());
    }
    public int getNumberOfCompletedWorkServicing() {
        int n = 0;
        for(var master : masters)
            n += master.getNumberOfCompletedWorkServicing();
        return n;
    }
    public int getNumberOfDroppedWorkServicing() {
        int n = 0;
        for(var master : masters)
            n += master.getNumberOfDroppedWorkServicing();
        return n;
    }
    public double getProfitByDate(LocalDate date) {
        double profit = 0;
        for(var master : masters)
            profit += master.getProfitByDate(date);
        return profit;
    }
    public double getMastersSalaryByDate(LocalDate date) {
        double salary = 0;
        for(var master : masters)
            salary += master.getSalaryByDate(date);
        return salary;
    }
    public double getTotalProfit() {
        double profit = 0;
        for(var master : masters)
            profit += master.getTotalProfit();
        return profit;
    }
    public double getAverageSalary() {
        double avg = 0;
        for(var master : masters) {
            avg += master.getAverageSalary();
        }
        if(masters.isEmpty())
            return 0;
        return avg / masters.size();
    }
    public double getTotalSalary() {
        double salary = 0;
        for(var master : masters)
            salary += master.getTotalSalary();
        return salary;
    }
    public LocalDateTime getCurrentDateTime() {
        return getDateTimeOfCreation().plus(currentLifeDuration);
    }
    public LocalDate getCurrentDate() {
        return getCurrentDateTime().toLocalDate();
    }
    public LocalTime getCurrentTime() {
        return getCurrentDateTime().toLocalTime();
    }
    public int getAverageQueueLengthByDate(LocalDate date) {
        int len = 0;
        if(!queueLength.containsKey(date))
            return 0;
        var pair = queueLength.get(date);
        return (pair.getFirst() + pair.getSecond())/2;
    }
    public int getAverageQueueLength() {
        int len = 0;
        for(var date : queueLength.keySet()) {
            len += getAverageQueueLengthByDate(date);
        }
        if(queueLength.isEmpty())
            return len;
        return len / queueLength.size();
    }
    public int getMaxQueueLengthByDate(LocalDate date) {
        if(!queueLength.containsKey(date))
            return 0;
        return queueLength.get(date).getSecond();
    }
    public int getMinQueueLengthByDate(LocalDate date) {
        if(!queueLength.containsKey(date))
            return 0;
        return queueLength.get(date).getFirst();
    }
    public int getMaxQueueLength() {
        int max = 0;
        for(var date : queueLength.keySet())
            max = Math.max(max, getMaxQueueLengthByDate(date));
        return max;
    }
    public int getMinQueueLength() {
        int min = 0;
        for(var date : queueLength.keySet())
            min = Math.min(min, getMinQueueLengthByDate(date));
        return min;
    }
    public Service getService() {
        return service;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Цех \"%s\"\n".formatted(name));
        sb.append("Среднее время ожидания: %s\n".formatted(DurationHelper.toString(getAverageWaitingDuration())));
        sb.append("Среднее время работы: %s\n".formatted(DurationHelper.toString(getAverageEmployementDuration())));
        sb.append("Кол-во выполненых услуг: %d\n".formatted(getNumberOfCompletedWorkServicing()));
        sb.append("Кол-во не выполненых услуг: %d\n".formatted(getNumberOfDroppedWorkServicing()));
        sb.append("Средняя зарплата мастеров: %.2f\n".formatted(getAverageSalary()));
        //sb.append("\n");
        sb.append("Прибыль цеха: %.2f\n".formatted(getTotalSalary()));
        sb.append("Средняя длина очереди: %d\n".formatted(getAverageQueueLength()));
        sb.append("Максимальная длина очереди: %d\n".formatted(getMaxQueueLength()));
        sb.append("Минимальная длина очереди: %d".formatted(getMinQueueLength()));
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public void load() {
        if(drawer != null)
            drawer.setSource(this);
        masters.forEach(Master::load);
        queue.forEach(WorkServicing::load);
    }
}

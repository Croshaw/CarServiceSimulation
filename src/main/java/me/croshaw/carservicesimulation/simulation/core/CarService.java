package me.croshaw.carservicesimulation.simulation.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import me.croshaw.carservicesimulation.drawers.CarServiceDrawer;
import me.croshaw.carservicesimulation.simulation.core.service.Service;
import me.croshaw.carservicesimulation.simulation.core.util.CreatedInfo;
import me.croshaw.carservicesimulation.simulation.core.util.DurationHelper;
import me.croshaw.carservicesimulation.simulation.core.util.Pair;

import java.time.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class CarService extends CreatedInfo {
    private CarServiceDrawer drawer;
    private final LinkedList<Pair<Double, Double>> coordinatesForWorkshops;
    private final HashSet<Workshop> workshops;
    private final HashMap<DayOfWeek, Pair<LocalTime, Duration>> workSchedule;
    private final HashMap<LocalDate, ArrayList<Request>> completedRequestsMap;
    private final HashSet<Request> queue;
    private final HashMap<LocalDate, ArrayList<Request>> dropRequestsMap;
    private final HashMap<LocalDate, Pair<Integer, Integer>> queueLength;
    private Duration currentLifeDuration;
    private final HashSet<Service> services;
    private final Duration maxServicingDuration;
    private final Duration maxWaitingDuration;
    private final double minSalary;
    private final float profitShare;
    private float reputation;
    public CarService(LocalDateTime dateTimeOfCreation, Duration maxServicingDuration, Duration maxWaitingDuration, double minSalary, float profitShare) {
        super(dateTimeOfCreation);
        this.maxServicingDuration = maxServicingDuration;
        this.maxWaitingDuration = maxWaitingDuration;
        this.minSalary = minSalary;
        this.profitShare = profitShare;
        workshops = new HashSet<>();
        workSchedule = new HashMap<>();
        workSchedule.put(DayOfWeek.MONDAY, new Pair<>(LocalTime.of(9, 0, 0), Duration.ofHours(8)));
        workSchedule.put(DayOfWeek.TUESDAY, new Pair<>(LocalTime.of(9, 0, 0), Duration.ofHours(8)));
        workSchedule.put(DayOfWeek.WEDNESDAY, new Pair<>(LocalTime.of(9, 0, 0), Duration.ofHours(8)));
        workSchedule.put(DayOfWeek.THURSDAY, new Pair<>(LocalTime.of(9, 0, 0), Duration.ofHours(8)));
        workSchedule.put(DayOfWeek.FRIDAY, new Pair<>(LocalTime.of(9, 0, 0), Duration.ofHours(8)));
        workSchedule.put(DayOfWeek.SATURDAY, new Pair<>(LocalTime.of(10, 0, 0), Duration.ofHours(6)));
        queue = new HashSet<>();
        completedRequestsMap = new HashMap<>();
        dropRequestsMap = new HashMap<>();
        queueLength = new HashMap<>();
        services = new HashSet<>();
        reputation = 1.f;
        currentLifeDuration = Duration.ZERO;
        coordinatesForWorkshops = new LinkedList<>();
    }
    public void load() {
        if(drawer != null)
            drawer.setSource(this);
        workshops.forEach(Workshop::load);
    }
    public double getSpeed() {
        if(isDrawerSetup())
            return drawer.getSpeed();
        return 0;
    }
    public void setYOffset(double h) {
        if(isDrawerSetup())
            drawer.moveByY(h);
        workshops.forEach(x->x.setYOffset(h));
    }
    public boolean isDrawerSetup() {
        return drawer != null;
    }
    private void setupWorkshopCoordinates() {
        if (isDrawerSetup()) {
            coordinatesForWorkshops.clear();
            double mW = drawer.getWidth() / 5;
            double mH = drawer.getHeight() / 5;
            double xOffset = mW / 5;
            double yOffset = mH / 10;
            double mX = xOffset;
            double mY = yOffset;
            for (int i = 0; i < 4; i++) {
                if (mX + mW > drawer.getWidth()) {
                    mX = xOffset;
                    mY += mH + yOffset;
                }
                coordinatesForWorkshops.addLast(new Pair<>(drawer.getX() + mX, drawer.getY() + mY));
                mX += mW + xOffset;
            }
        }
    }
    public void setDrawer(double x, double y, double width, double height) {
        drawer = new CarServiceDrawer(this, x, y, width, height);
        setupWorkshopCoordinates();
    }
    public void setSpeed(double value) {
        if(isDrawerSetup())
            drawer.setSpeed(value);
        workshops.forEach(w -> w.setSpeed(value));
    }
    public void draw(GraphicsContext g) {
        if(drawer != null){
            drawer.draw(g);
        }
        workshops.forEach(w -> w.draw(g));
    }
    public boolean mouseMovedEvent(MouseEvent event) {
        drawer.drawTooltip = false;
        for(var w : workshops) {
            if(w.mouseMovedEvent(event)) {
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
        workshops.forEach(w -> w.drawTooltip(g));
    }
    public boolean isFixit() {
        boolean res = true;
        for(var w : workshops) {
            res = res && w.isFixit();
        }
        return (drawer == null || drawer.isFixit()) && res;
    }

    public Workshop registerService(Service service) {
        if(services.add(service)) {
            Workshop workshop = new Workshop(service, service.getName(), getCurrentDateTime(), maxServicingDuration, maxWaitingDuration, minSalary, profitShare);
            double mW = drawer.getWidth() / 5;
            double mH = drawer.getHeight() / 5;
            workshop.setDrawer(drawer.getX(), drawer.getY(), mW, mH);
            workshop.setSpeed(drawer.getSpeed());
            var p = coordinatesForWorkshops.get(workshops.size());
            workshop.move(p.getFirst(), p.getSecond());
            if(workshops.add(workshop)) {
                return workshop;
            }
        }
        return null;
    }
    public boolean registerRequest(Request request) {
        if(queue.add(request)) {
            if(!queueLength.containsKey(getCurrentDate()))
                queueLength.put(getCurrentDate(), new Pair<>(Integer.MAX_VALUE, Integer.MIN_VALUE));
            var pair = queueLength.get(getCurrentDate());
            if(pair.getFirst() > queue.size())
                pair.setFirst(queue.size());
            if(pair.getSecond() < queue.size())
                pair.setSecond(queue.size());
            var list = request.getWorkServicing();
            for(var workServicing : list) {
                var workshop = getWorkshopByService(workServicing.getService());
                if(workshop == null)
                    return false;
                workshop.registerWorkServicing(workServicing);
            }
            return true;
        }
        return false;
    }
    public void checkToDrop() {
        workshops.forEach(Workshop::checkToDrop);
        LinkedList<Request> toRem = new LinkedList<>();
        for(var q : queue) {
            if(q.isDropped()) {
                if(!dropRequestsMap.containsKey(getCurrentDate()))
                    dropRequestsMap.put(getCurrentDate(), new ArrayList<>());
                dropRequestsMap.get(getCurrentDate()).add(q);
                toRem.add(q);
            }
        }
        toRem.forEach(queue::remove);
    }
    public void checkToComplete() {
        for(var request : queue) {
            if(request.isDone()) {
                if(!completedRequestsMap.containsKey(getCurrentDate()))
                    completedRequestsMap.put(getCurrentDate(), new ArrayList<>());
                completedRequestsMap.get(getCurrentDate()).add(request);
            }
        }
        for(var request : completedRequestsMap.get(getCurrentDate())) {
            queue.remove(request);
        }
    }
    public void life(long secondsStep) {
        LocalDateTime prevDateTime = getCurrentDateTime();
        currentLifeDuration = currentLifeDuration.plusSeconds(secondsStep);
        LocalDateTime curDateTime = getCurrentDateTime();
        checkToDrop();
        if(isWork(prevDateTime) && isWork(curDateTime)) {
            workshops.forEach(x-> x.life(secondsStep, true));
        } else if(isWork(prevDateTime) && !isWork(curDateTime)) {
            var tempSchedule = workSchedule.get(prevDateTime.getDayOfWeek());
            long dif = Math.abs(Duration.between(tempSchedule.getFirst().plus(tempSchedule.getSecond()), prevDateTime).toSeconds());
            workshops.forEach(x-> x.life(dif, true));
            workshops.forEach(x-> x.life(secondsStep - dif, false));
        } else if(!isWork(prevDateTime) && isWork(curDateTime)) {
            var tempSchedule = workSchedule.get(prevDateTime.getDayOfWeek());
            long dif = Math.abs(Duration.between(tempSchedule.getFirst(), prevDateTime).toSeconds());
            workshops.forEach(x-> x.life(dif, true));
            workshops.forEach(x-> x.life(secondsStep - dif, false));
        } else {
            workshops.forEach(x-> x.life(secondsStep, false));
        }
    }
    public Duration getAverageWaitingDuration() {
        Duration avg = Duration.ZERO;
        for(var workshop : workshops)
            avg = avg.plus(workshop.getAverageWaitingDuration());
        if(workshops.isEmpty())
            return Duration.ZERO;
        return avg.dividedBy(workshops.size());
    }
    public Duration getAverageEmployementDuration() {
        Duration avg = Duration.ZERO;
        for(var workshop : workshops)
            avg = avg.plus(workshop.getAverageEmployementDuration());
        if(workshops.isEmpty())
            return Duration.ZERO;
        return avg.dividedBy(workshops.size());
    }
    public int getNumberOfCompletedRequests() {
        int n = 0;
        for(var list : completedRequestsMap.values())
            n += list.size();
        return n;
    }
    public int getNumberOfCompletedWorkServicing() {
        int n = 0;
        for(var workshop : workshops)
            n += workshop.getNumberOfCompletedWorkServicing();
        return n;
    }
    public int getNumberOfDroppedRequests() {
        int n = 0;
        for(var list : dropRequestsMap.values())
            n += list.size();
        return n;
    }
    public int getNumberOfDroppedWorkServicing() {
        int n = 0;
        for(var workshop : workshops)
            n += workshop.getNumberOfDroppedWorkServicing();
        return n;
    }
    public double getProfitByDate(LocalDate date) {
        double profit = 0;
        for (Workshop workshop : workshops) {
            profit += workshop.getProfitByDate(date);
        }
        return profit;
    }
    public double getTotalProfit() {
        double profit = 0;
        for (Workshop workshop : workshops) {
            profit += workshop.getTotalProfit();
        }
        return profit;
    }
    public double getMastersSalaryByDate(LocalDate date) {
        double salary = 0;
        for (Workshop workshop : workshops) {
            salary += workshop.getMastersSalaryByDate(date);
        }
        return salary;
    }
    public double getTotalMastersSalary() {
        double salary = 0;
        for (Workshop workshop : workshops) {
            salary += workshop.getTotalSalary();
        }
        return salary;
    }
    public double getAverageMastersSalary() {
        double salary = 0;
        for (Workshop workshop : workshops) {
            salary += workshop.getAverageSalary();
        }
        if(workshops.isEmpty())
            return 0;
        return salary / workshops.size();
    }
    public Workshop getWorkshopByService(Service service) {
        for(var workshop : workshops) {
            if(workshop.getService().getName().equals(service.getName())) {
                return workshop;
            }
        }
        return null;
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
    public LocalDateTime getCurrentDateTime() {
        return getDateTimeOfCreation().plus(currentLifeDuration);
    }
    public LocalDate getCurrentDate() {
        return getCurrentDateTime().toLocalDate();
    }
    public LocalTime getCurrentTime() {
        return getCurrentDateTime().toLocalTime();
    }
    //!Дописать
    public float getReputation() {
        return reputation;
    }
    public boolean isWork() {
        return isWork(getCurrentDateTime());
    }
    public boolean isWork(LocalDateTime dateTime) {
        if(!workSchedule.containsKey(dateTime.getDayOfWeek()))
            return false;
        var tempSchedule = workSchedule.get(dateTime.getDayOfWeek());
        return dateTime.toLocalTime().isAfter(tempSchedule.getFirst()) && dateTime.toLocalTime().isBefore(tempSchedule.getFirst().plus(tempSchedule.getSecond()));
    }
    //!Дописать
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Общее число обслуженный автомобилей: %s\n".formatted(getNumberOfCompletedRequests()));

        sb.append("Предоставленных услуг разного вида:\n");
        workshops.forEach(x-> sb.append("\t%s: %s\n".formatted(x.getName(), x.getNumberOfCompletedWorkServicing())));

        sb.append("Среднее время обслуживания автомобилей: %s\n".formatted(DurationHelper.toString(getAverageEmployementDuration())));

        sb.append("Средняя длина очередей:\n");
        workshops.forEach(x-> sb.append("\tЦех \"%s\": %s\n".formatted(x.getName(), x.getAverageQueueLength())));

        sb.append("Средняя занятость рабочих:\n");
        workshops.forEach(x-> sb.append("\tЦех \"%s\": %s\n".formatted(x.getName(), DurationHelper.toString(x.getAverageEmployementDuration()))));

        sb.append("Средняя зарплата рабочих:\n");
        workshops.forEach(x-> sb.append("\tЦех \"%s\": %.2f руб.\n".formatted(x.getName(), x.getAverageSalary())));

        sb.append("Общая прибыль автосервиса: %.2f руб.".formatted(getTotalProfit()));
        return sb.toString();
    }

    public Service[] getServices() {
        return services.toArray(Service[]::new);
    }

    public Duration getLifeDuration() {
        return currentLifeDuration;
    }
}

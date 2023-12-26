package me.croshaw.carservicesimulation.simulation.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import me.croshaw.carservicesimulation.drawers.MasterDrawer;
import me.croshaw.carservicesimulation.simulation.core.util.DurationHelper;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Master extends People {
    private WorkServicing currentTask;
    private MasterDrawer drawer;
    private final Duration maxServicingDuration;
    private final Duration maxWaitingDuration;
    private double minSalary;
    private float profitShare;
    private final HashMap<LocalDate, ArrayList<WorkServicing>> completedWorkServicingMap;
    private final HashMap<LocalDate, ArrayList<WorkServicing>> droppedWorkServicingMap;
    private final HashMap<LocalDate, Duration> employmentMap;
    public Master(String surname, String name, String patronymic, Duration maxServicingDuration, Duration maxWaitingDuration) {
        super(surname, name, patronymic);
        this.maxServicingDuration = maxServicingDuration;
        this.maxWaitingDuration = maxWaitingDuration;
        this.minSalary = 0;
        this.profitShare = 0;
        completedWorkServicingMap = new HashMap<>();
        droppedWorkServicingMap = new HashMap<>();
        employmentMap = new HashMap<>();
    }
    public boolean isDrawerSetup() {
        return drawer != null;
    }
    public void setDrawer(double x, double y, double width, double height) {
        drawer = new MasterDrawer(this, x, y, width, height);
    }
    public void setSpeed(double value) {
        if(isDrawerSetup()) {
            drawer.setSpeed(value);
        }
        if(currentTask != null && currentTask.isDrawerSetup())
            currentTask.setSpeed(value);
    }
    public void move(double x, double y) {
        if(drawer != null) {
            drawer.move(x, y);
        }
    }
    public void draw(GraphicsContext g) {
        if(drawer != null){
            drawer.draw(g);
        }
        if(currentTask != null) {
            currentTask.draw(g);
        }
    }
    public boolean mouseMovedEvent(MouseEvent event) {
        if(drawer == null)
            return false;
        drawer.drawTooltip = false;
        if(currentTask != null && currentTask.mouseMovedEvent(event))
            return true;
        return drawer.mouseMovedEvent(event);
    }
    public void drawTooltip(GraphicsContext g) {
        if(drawer != null){
            drawer.drawTooltip(g);
        }
        if(currentTask != null)
            currentTask.drawTooltip(g);
    }
    public boolean isFixit() {
        return (drawer == null || drawer.isFixit()) && (currentTask == null || currentTask.isFixit());
    }
    public void setTask(WorkServicing workServicing) {
        currentTask = workServicing;
        if(currentTask != null) {
            double width = drawer.getWidth()/6;
            double height = drawer.getHeight()/6;
            double x = drawer.getX() + drawer.getWidth()/2 - drawer.getWidth() / 4;
            double y = drawer.getY() + drawer.getHeight() - drawer.getHeight() / 2;
            currentTask.setSpeed(drawer.getSpeed());
            if(currentTask.isDrawerSetup()) {
                currentTask.move(x, y);
            } else {
                currentTask.setDrawer(x, y, width, height);
            }
        }
    }
    public void checkToDrop(LocalDate date) {
        if(currentTask != null && (currentTask.getWorkDuration().compareTo(maxServicingDuration) >= 0
                || currentTask.getWaitingDuration().compareTo(maxWaitingDuration) >= 0)) {
            if (!droppedWorkServicingMap.containsKey(date))
                droppedWorkServicingMap.put(date, new ArrayList<>());
            droppedWorkServicingMap.get(date).add(currentTask);
            currentTask = null;
        }
    }
    public long work(long secondsStep, LocalDate date) {
        if(currentTask == null)
            return secondsStep;
        long dif = currentTask.service(secondsStep);
        employmentMap.put(date, employmentMap.getOrDefault(date, Duration.ZERO).plusSeconds(secondsStep - dif));
        if(currentTask.isDone()) {
            if(!completedWorkServicingMap.containsKey(date))
                completedWorkServicingMap.put(date, new ArrayList<>());
            completedWorkServicingMap.get(date).add(currentTask);
            currentTask = null;
        }
        return dif;
    }
    public Duration getAverageWaitingDuration() {
        Duration averageWaitingDuration = Duration.ZERO;
        int count = 0;
        for(var list : completedWorkServicingMap.values()) {
            count += list.size();
            for(var val : list) {
                averageWaitingDuration = averageWaitingDuration.plus(val.getWaitingDuration());
            }
        }
        if(count == 0)
            return Duration.ZERO;
        return averageWaitingDuration.dividedBy(count);
    }
    public Duration getAverageEmploymentDuration() {
        Duration averageEmployementDuration = Duration.ZERO;
        for(var dur : employmentMap.values()) {
            averageEmployementDuration = averageEmployementDuration.plus(dur);
        }
        if(employmentMap.isEmpty())
            return Duration.ZERO;
        return averageEmployementDuration.dividedBy(employmentMap.size());
    }
    public int getNumberOfCompletedWorkServicing() {
        int count = 0;
        for(var list : completedWorkServicingMap.values()) {
            count += list.size();
        }
        return count;
    }
    public int getNumberOfDroppedWorkServicing() {
        int count = 0;
        for(var list : droppedWorkServicingMap.values()) {
            count += list.size();
        }
        return count;
    }
    public double getProfitByDate(LocalDate date) {
        double profit = 0;
        if(!completedWorkServicingMap.containsKey(date))
            return profit;
        for(var val : completedWorkServicingMap.get(date))
            profit += val.getPrice();
        return profit;
    }
    public double getSalaryByDate(LocalDate date) {
        double salary = 0;
        if(!completedWorkServicingMap.containsKey(date))
            return minSalary;
        for(var val : completedWorkServicingMap.get(date))
            salary += val.getPrice() * profitShare;
        salary = Math.max(1500, salary);
        return salary;
    }
    public double getAverageSalary() {
        if(completedWorkServicingMap.isEmpty())
            return 1500;
        double value = getTotalSalary() / completedWorkServicingMap.size();
        return Math.max(1500, value);
    }
    public double getTotalProfit() {
        double profit = 0;
        for(var date : completedWorkServicingMap.keySet())
            profit += getProfitByDate(date);
        return profit;
    }
    public double getTotalSalary() {
        double salary = 0;
        for(var date : completedWorkServicingMap.keySet())
            salary += getSalaryByDate(date);
        return salary;
    }
    public boolean isWork() {
        return currentTask != null;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Мастер: %s\n".formatted(super.toShortString()));
        sb.append("Среднее время ожидания: %s\n".formatted(DurationHelper.toString(getAverageWaitingDuration())));
        sb.append("Среднее время работы: %s\n".formatted(DurationHelper.toString(getAverageEmploymentDuration())));
        sb.append("Кол-во предоставленных услуг: %d\n".formatted(getNumberOfCompletedWorkServicing()));
        sb.append("Кол-во отменённых услуг: %d\n".formatted(getNumberOfDroppedWorkServicing()));
        sb.append("Принесённая прибыль: %.2f\n".formatted(getTotalProfit()));
        sb.append("Зарплата за все дни: %.2f\n".formatted(getTotalSalary()));
        sb.append("Минимальная зарплата: %.2f\n".formatted(minSalary));
        sb.append("Средняя зарплата: %.2f\n".formatted(getTotalSalary()/getNumberOfCompletedWorkServicing()));
        sb.append("Текущая работа:\n\t%s".formatted(currentTask == null ? "отдыхает" : currentTask.toString().replace("\n", "\n\t")));
        return sb.toString();
    }

    public WorkServicing getCurrentTask() {
        return currentTask;
    }

    public void load() {
        if(isDrawerSetup())
            drawer.setSource(this);
        if(currentTask != null)
            currentTask.load();
    }
}

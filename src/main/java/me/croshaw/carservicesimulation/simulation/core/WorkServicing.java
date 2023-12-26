package me.croshaw.carservicesimulation.simulation.core;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import me.croshaw.carservicesimulation.drawers.Drawer;
import me.croshaw.carservicesimulation.drawers.WorkServicingDrawer;
import me.croshaw.carservicesimulation.simulation.core.service.Service;
import me.croshaw.carservicesimulation.simulation.core.util.CreatedInfo;
import me.croshaw.carservicesimulation.simulation.core.util.DurationHelper;

import java.time.Duration;
import java.time.LocalDateTime;

public class WorkServicing extends CreatedInfo {
    private final Service service;
    private WorkServicingDrawer drawer;
    private final Car car;
    private Master master;
    private final Duration expectedServiceDuration;
    private Duration workDuration;
    private Duration waitingDuration;
    private boolean isDropped;
    public WorkServicing(LocalDateTime dateTimeOfCreation, Service service, Car car, Duration expectedServiceDuration) {
        super(dateTimeOfCreation);
        this.service = service;
        this.car = car;
        this.expectedServiceDuration = expectedServiceDuration;
        workDuration = Duration.ZERO;
        waitingDuration = Duration.ZERO;
    }
    public boolean isDrawerSetup() {
        return drawer != null;
    }
    public void setDrawer(double x, double y, double width, double height) {
        drawer = new WorkServicingDrawer(this, x, y, width, height);
    }
    public void setSpeed(double value) {
        if(isDrawerSetup())
            drawer.setSpeed(value);
    }
    public void move(double x, double y) {
        if(isDrawerSetup()) {
            drawer.move(x, y);
        }
    }
    public void draw(GraphicsContext g) {
        if(isDrawerSetup()){
            drawer.draw(g);
        }
    }
    public boolean mouseMovedEvent(MouseEvent event) {
        if(!isDrawerSetup())
            return false;
        return drawer.mouseMovedEvent(event);
    }
    public void drawTooltip(GraphicsContext g) {
        if(isDrawerSetup()){
            drawer.drawTooltip(g);
        }
    }
    public boolean isFixit() {
        return drawer == null || drawer.isFixit();
    }
    public void setMaster(Master master) {
        this.master = master;
    }
    public Master getMaster() {
        return master;
    }
    public long service(long secondsStep) {
        if(isWaiting())
            waitingDuration = waitingDuration.minusSeconds(secondsStep);
        workDuration = workDuration.plusSeconds(secondsStep);
        if(workDuration.compareTo(expectedServiceDuration) > 0) {
            long dif = workDuration.minus(expectedServiceDuration).toSeconds();
            workDuration = expectedServiceDuration;
            return dif;
        }
        return 0;
    }
    public void setYOffset(double h) {
        if(isDrawerSetup())
            drawer.moveByY(h);
    }
    public void waiting(long secondsStep) {
        waitingDuration = waitingDuration.plusSeconds(secondsStep);
    }
    public double getPrice() {
        return service.getPrice();
    }
    public Duration getWorkDuration() {
        return workDuration;
    }
    public Duration getWaitingDuration() {
        return waitingDuration;
    }
    public Duration getExpectedServiceDurationDuration() {
        return expectedServiceDuration;
    }
    public boolean isDone() {
        return expectedServiceDuration.compareTo(workDuration) <= 0;
    }
    public boolean isWaiting() {
        return workDuration.compareTo(Duration.ZERO) == 0;
    }
    public boolean isCarBusy() {
        return car.isBusy();
    }
    public boolean isDropped() {
        return isDropped;
    }
    public void drop() {
        isDropped = true;
    }
    public Service getService() {
        return service;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Услуга: %s\n".formatted(service.getName()));
        sb.append("Мастер: %s\n".formatted(master == null ? "нет" : master.toShortString()));
        sb.append("Время работы: %s\n".formatted(DurationHelper.toString(getWorkDuration())));
        sb.append("Время ожидания: %s\n".formatted(DurationHelper.toString(getExpectedServiceDurationDuration())));
        sb.append("Стоимость: %.2f\n".formatted(getPrice()));
        sb.append(isDone() ? "Завершена" : "В процессе обслуживания");
        return sb.toString();
    }

    public void load() {
        if(isDrawerSetup())
            drawer.setSource(this);
    }
}

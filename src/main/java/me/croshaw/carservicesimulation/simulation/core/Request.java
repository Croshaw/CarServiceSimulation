    package me.croshaw.carservicesimulation.simulation.core;

    import me.croshaw.carservicesimulation.simulation.core.service.Service;
    import me.croshaw.carservicesimulation.simulation.core.util.CreatedInfo;
    import me.croshaw.carservicesimulation.simulation.core.util.DurationHelper;

    import java.time.Duration;
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.Random;

    public class Request extends CreatedInfo {
    private final ArrayList<WorkServicing> workServicingList;
    private final Car car;
    public Request(LocalDateTime dateTimeOfCreation, Car car) {
        this(dateTimeOfCreation, car, new ArrayList<>());
    }
    public Request(LocalDateTime dateTimeOfCreation, Car car, ArrayList<WorkServicing> workServicingList) {
        super(dateTimeOfCreation);
        this.car = car;
        this.workServicingList = workServicingList;
    }
    public boolean addWorkServicing(Service service, Random random) {
        return !isServiceAlreadyExists(service) && workServicingList.add(new WorkServicing(getDateTimeOfCreation(), service, car, Service.getRandomDurationOfService(service, random)));
    }
    public boolean addWorkServicing(WorkServicing workServicing) {
        return workServicingList.add(workServicing);
    }
    public void waiting(long secondsStep) {
        workServicingList.forEach(x-> {
            if(x.isWaiting())
                x.waiting(secondsStep);
        });
    }
    public boolean isServiceAlreadyExists(Service service) {
        for(var t : workServicingList) {
            if(t.getService() == service)
                return true;
        }
        return false;
    }
    public WorkServicing removeWorkServicing(int id) {
        return workServicingList.remove(id);
    }
    public boolean removeWorkServicing(WorkServicing workServicing) {
        return workServicingList.remove(workServicing);
    }
    public Car getCar() {
        return car;
    }
    public Duration getWaitingDuration() {
        Duration waitingDur = Duration.ZERO;
        for(var workServ : workServicingList)
            waitingDur = waitingDur.plus(workServ.getWaitingDuration());
        return waitingDur;
    }
    public Duration getExpectedServiceDuration() {
        Duration expectedDur = Duration.ZERO;
        for(var workServ : workServicingList)
            expectedDur = expectedDur.plus(workServ.getExpectedServiceDurationDuration());
        return expectedDur;
    }
    public Duration getWorkDuration() {
        Duration workDur = Duration.ZERO;
        for(var workServ : workServicingList)
            workDur = workDur.plus(workServ.getWorkDuration());
        return workDur;
    }
    public ArrayList<WorkServicing> getWorkServicing() {
        return workServicingList;
    }
    public boolean isDone() {
        for(var workServ : workServicingList)
            if(!workServ.isDone())
                return false;
        return true;
    }
    public boolean isDropped() {
        boolean res = false;
        for(var t : workServicingList) {
            res = res || t.isDropped();
        }
        return res;
    }
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Заявка от %s\n".formatted(getDateOfCreation().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))));
        sb.append("Машина: %s".formatted(car.toString()));
        sb.append("Ожидаемое время обслуживания: %s\n".formatted(DurationHelper.toString(getExpectedServiceDuration())));
        sb.append("Время обслуживания: %s\n".formatted(DurationHelper.toString(getWorkDuration())));
        sb.append("Время ожидания: %s\n".formatted(DurationHelper.toString(getWaitingDuration())));
        sb.append(isDone() ? "Завершена" : "На обслуживании");
        return sb.toString();
    }
    }

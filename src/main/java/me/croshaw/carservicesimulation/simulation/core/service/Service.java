package me.croshaw.carservicesimulation.simulation.core.service;

import me.croshaw.carservicesimulation.simulation.core.util.ValueRange;

import java.time.Duration;
import java.util.Random;

public class Service {
    private final String name;
    private final Duration averageDurationOfService;
    private final ValueRange<Long> offsetServicingRange;
    private final double price;
    public Service(String name, Duration averageDurationOfService, ValueRange<Long> offsetServicingRange, double price) {
        this.name = name;
        this.averageDurationOfService = averageDurationOfService;
        this.offsetServicingRange = offsetServicingRange;
        this.price = price;
    }
    public String getName() {
        return name;
    }
    public Duration getAverageDurationOfService() {
        return averageDurationOfService;
    }
    public ValueRange<Long> getOffsetServicingRange() {
        return offsetServicingRange;
    }
    public double getPrice() {
        return price;
    }
    public static Duration getRandomDurationOfService(Service service, Random random) {
        return service.getAverageDurationOfService().plusSeconds(service.getOffsetServicingRange().getRandomValueFromRange(random));
    }
    @Override
    public String toString() {
        return "Услуга: %s\nСтоимость: %.2f".formatted(name, price);
    }
}

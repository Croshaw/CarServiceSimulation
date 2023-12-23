package me.croshaw.carservicesimulation.simulation.base.service;

import me.croshaw.carservicesimulation.simulation.base.util.ValueRange;

import java.time.Duration;

public class BodyRepairService extends Service {
    public BodyRepairService() {
        super("Кузовной ремонт", Duration.ofDays(1), new ValueRange<>(3600*24*-1L, 3600*24L), 30000);
    }
}

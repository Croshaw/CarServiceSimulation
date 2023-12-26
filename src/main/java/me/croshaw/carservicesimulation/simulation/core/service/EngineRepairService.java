package me.croshaw.carservicesimulation.simulation.core.service;

import me.croshaw.carservicesimulation.simulation.core.util.ValueRange;

import java.time.Duration;

public class EngineRepairService extends Service {
    public EngineRepairService() {
        super("Ремонт двигателя", Duration.ofDays(2), new ValueRange<>(3600*24*-1L, 3600*24L), 45000);
    }
}

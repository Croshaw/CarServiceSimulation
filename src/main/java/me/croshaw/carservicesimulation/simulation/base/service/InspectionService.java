package me.croshaw.carservicesimulation.simulation.base.service;

import me.croshaw.carservicesimulation.simulation.base.util.ValueRange;

import java.time.Duration;

public class InspectionService extends Service {
    public InspectionService() {
        super("Техосмотр", Duration.ofHours(6), new ValueRange<>(3600*-1L, 3600L), 15000);
    }
}

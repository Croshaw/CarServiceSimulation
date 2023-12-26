package me.croshaw.carservicesimulation.simulation.core.service;

import me.croshaw.carservicesimulation.simulation.core.util.ValueRange;

import java.time.Duration;

public class TireService extends Service {
    public TireService() {
        super("Шиномонтаж", Duration.ofHours(4), new ValueRange<>(3600*-1L, 3600L), 15000);
    }
}

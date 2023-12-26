package me.croshaw.carservicesimulation.simulation.core.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CreatedInfo {
    private final LocalDateTime dateTimeOfCreation;

    public CreatedInfo() {
        this(LocalDateTime.of(LocalDate.now(), LocalTime.of(0,0,0)));
    }
    public CreatedInfo(LocalDateTime dateTimeOfCreation) {
        this.dateTimeOfCreation = dateTimeOfCreation;
    }

    public LocalDateTime getDateTimeOfCreation() {
        return dateTimeOfCreation;
    }
    public LocalDate getDateOfCreation() {
        return dateTimeOfCreation.toLocalDate();
    }
    public LocalTime getTimeOfCreation() {
        return dateTimeOfCreation.toLocalTime();
    }
    @Override
    public String toString() {
        return "Дата создания: %s".formatted(dateTimeOfCreation.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }
}

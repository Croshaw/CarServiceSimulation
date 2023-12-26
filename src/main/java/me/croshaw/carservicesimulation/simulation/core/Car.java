package me.croshaw.carservicesimulation.simulation.core;

public class Car {
    private final Brand brand;
    private final String model;
    private final People owner;
    private boolean isBusy;

    public Car(Brand brand, String model, People owner) {
        this.brand = brand;
        this.model = model;
        this.owner = owner;
    }

    public Brand getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public People getOwner() {
        return owner;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setBusy(boolean busy) {
        isBusy = busy;
    }

    @Override
    public String toString() {
        return "Машина: %s %s\nВладелец: %s\n%s".formatted(brand.getName(), model, owner.toShortString(), (isBusy() ? "На обслуживании" : "Свободна"));
    }
}

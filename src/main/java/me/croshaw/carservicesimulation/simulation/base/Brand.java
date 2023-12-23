package me.croshaw.carservicesimulation.simulation.base;

import java.util.ArrayList;
import java.util.Random;

public class Brand {
    private final String name;
    private final ArrayList<String> models;

    public Brand(String name) {
        this.name = name;
        models = new ArrayList<>();
    }
    public String getName() {
        return name;
    }
    public void addModel(String name) {
        models.add(name);
    }
    public ArrayList<String> getModels() {
        return models;
    }
    public String getRandomModel(Random random) {
        if(models.isEmpty())
            return null;
        return models.get(random.nextInt(0, models.size()));
    }
}

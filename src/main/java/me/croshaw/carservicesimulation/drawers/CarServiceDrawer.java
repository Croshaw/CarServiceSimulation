package me.croshaw.carservicesimulation.drawers;

import javafx.scene.paint.Color;
import me.croshaw.carservicesimulation.simulation.core.CarService;

public class CarServiceDrawer extends Drawer<CarService> {
    public CarServiceDrawer(CarService source, double x, double y, double width, double height) {
        super(source, x, y, width, height, Color.LIGHTBLUE);
    }
}

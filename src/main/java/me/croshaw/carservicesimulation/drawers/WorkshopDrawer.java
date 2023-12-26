package me.croshaw.carservicesimulation.drawers;

import javafx.scene.paint.Color;
import me.croshaw.carservicesimulation.simulation.core.Workshop;

public class WorkshopDrawer extends Drawer<Workshop> {
    public WorkshopDrawer(Workshop source, double x, double y, double width, double height) {
        super(source, x, y, width, height, Color.AQUA);
    }
}

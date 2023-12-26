package me.croshaw.carservicesimulation.drawers;

import javafx.scene.paint.Color;
import me.croshaw.carservicesimulation.simulation.core.WorkServicing;

public class WorkServicingDrawer extends Drawer<WorkServicing> {
    public WorkServicingDrawer(WorkServicing source, double x, double y, double width, double height) {
        super(source, x, y, width, height, Color.BLACK);
    }
}

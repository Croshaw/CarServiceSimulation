package me.croshaw.carservicesimulation.drawers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import me.croshaw.carservicesimulation.simulation.core.Master;

public class MasterDrawer extends Drawer<Master> {
    public MasterDrawer(Master source, double x, double y, double width, double height) {
        super(source, x, y, width, height, Color.RED);
    }
}

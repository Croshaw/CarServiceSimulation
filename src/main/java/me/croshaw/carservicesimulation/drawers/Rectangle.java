package me.croshaw.carservicesimulation.drawers;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

public class Rectangle {
    private double x;
    private double y;
    private double width;
    private double height;
    private Color color;
    public Rectangle(double x, double y, double width, double height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
    }
    public void draw(GraphicsContext g) {
        if(color != null)
            g.setFill(color);
        g.fillRect(x, y, width, height);
    }
    public void moveByX(double x) {
        this.x += x;
    }
    public void moveByY(double y) {
        this.y += y;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setWidth(double width) {
        this.width = width;
    }
    public void setHeight(double height) {
        this.height = height;
    }
    public void setLocation(double x, double y) {
        setX(x);
        setY(y);
    }
    public void setSize(double width, double height) {
        setWidth(width);
        setHeight(height);
    }
    public void setBounds(double x, double y, double width, double height) {
        setLocation(x, y);
        setSize(width, height);
    }
    public void setColor(Color color) {
        this.color = color;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public boolean isInBounds(double x, double y) {
        return x >= this.x && x <= this.x + width && y >= this.y && y <= this.y + height;
    }

    public Paint getColor() {
        return color;
    }
}

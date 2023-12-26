package me.croshaw.carservicesimulation.drawers;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import me.croshaw.carservicesimulation.simulation.core.util.Pair;

import java.util.ArrayDeque;
import java.util.LinkedList;

public class Drawer<T> {
    protected transient T source;
    protected final Rectangle rectangle;
    private final ArrayDeque<Pair<Double, Double>> moves;
    private double speed;
    public boolean drawTooltip;
    private double mouseX;
    private double mouseY;
    public Drawer(T source, double x, double y, double width, double height, Color color) {
        this.source = source;
        this.rectangle = new Rectangle(x, y, width, height, color);
        moves = new ArrayDeque<>();
    }
    public void setSource(T source) {
        this.source = source;
    }
    public boolean mouseMovedEvent(MouseEvent event) {
        drawTooltip = isHover(event.getX(), event.getY());
        if(drawTooltip) {
            mouseX = event.getX();
            mouseY = event.getY();
        }
        return drawTooltip;
    }
    public void setSpeed(double value) {
        this.speed = value;
    }
    public void draw(GraphicsContext g) {
        rectangle.draw(g);
        if(!moves.isEmpty()) {
            var pair = moves.getFirst();
            boolean isDelete = true;
            if(Math.abs(rectangle.getX() - pair.getFirst()) > 1) {
                rectangle.moveByX(speed * (pair.getFirst() - rectangle.getX()));
                isDelete = false;
            }
            else
                rectangle.setX(pair.getFirst());
            if(Math.abs(rectangle.getY() - pair.getSecond()) > 1) {
                rectangle.moveByY(speed * (pair.getSecond() - rectangle.getY()));
                isDelete = false;
            }
            else
                rectangle.setY(pair.getSecond());
            if(isDelete)
                moves.removeFirst();
        }
    }
    public void drawTooltip(GraphicsContext g) {
        if(source != null && drawTooltip) {
            drawTextOnBackground(g, mouseX, mouseY, Color.BLACK, Color.WHITE, source.toString());
        }
    }
    public void move(double x, double y) {
        moves.addLast(new Pair<>(x, y));
    }
    public Pair<Double, Double> getLastMove() {
        if(moves.isEmpty())
            return null;
        return moves.getLast();
    }
    public void moveByY(double value) {
        moves.forEach(x-> {
            x.setSecond(x.getSecond() + value);
        });
        rectangle.moveByY(value);
    }
    public boolean isFixit() {
        return moves.isEmpty();
    }
    public boolean isHover(double x, double y) {
        return rectangle.isInBounds(x, y);
    }
    public double getX() {
        return rectangle.getX();
    }
    public double getY() {
        return rectangle.getY();
    }
    public double getWidth() {
        return rectangle.getWidth();
    }
    public double getHeight() {
        return rectangle.getHeight();
    }
    public double getSpeed() {
        return speed;
    }
    public static void drawTextOnBackground(GraphicsContext g, double x, double y,Color foreColor, Color background, String text) {
        double xx = x + 10;
        double yy = y + 10;

        double width = computeTextWidth(g.getFont(), text)+15;
        double height = computeTextHeight(g.getFont(), text) + 10;

        Canvas c = g.getCanvas();
        if(xx + width > c.getWidth()) {
            xx = x - width - 10;
        }
        if(yy + height > c.getHeight()) {
            yy = y - height;
        }
        g.setFill(background);
        g.fillRoundRect(xx, yy, width, height, 20, 20);

        g.setFill(foreColor);
        g.fillText(text, xx + 15/2, yy+15);
    }
    public static double computeTextWidth(Font font, String text) {
        javafx.scene.text.Text textNode = new javafx.scene.text.Text(text);
        textNode.setFont(font);
        return textNode.getLayoutBounds().getWidth();
    }
    public static double computeTextHeight(Font font, String text) {
        javafx.scene.text.Text textNode = new javafx.scene.text.Text(text);
        textNode.setFont(font);
        return textNode.getLayoutBounds().getHeight();
    }

    public Paint getColor() {
        return rectangle.getColor();
    }
}

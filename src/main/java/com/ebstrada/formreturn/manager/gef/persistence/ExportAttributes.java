package com.ebstrada.formreturn.manager.gef.persistence;

import java.io.Serializable;

public class ExportAttributes implements Serializable {

    private static final long serialVersionUID = 1798853148314292588L;

    private int x;

    private int y;

    private int width;

    private int height;

    private String textAlignment;

    private String verticalAlignment;

    private String id;

    private String foregroundColor;

    private String backgroundColor;

    private String band;

    private String mode;

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public ExportAttributes() {
        x = 0;
        y = 0;
        width = 10;
        height = 10;
        textAlignment = "Left";
        verticalAlignment = "Top";
        foregroundColor = "#000000";
        backgroundColor = "#FFFFFF";
        mode = "Transparent";
        band = "detail";
    }

    public void setBand(String newBand) {
        band = newBand;
    }

    public String getBand() {
        return band;
    }

    public void setX(int newX) {
        x = newX;
    }

    public void setY(int newY) {
        y = newY;
    }

    public void setWidth(int newWidth) {
        width = newWidth;
    }

    public void setHeight(int newHeight) {
        height = newHeight;
    }

    public void setTextAlignment(String newTextAlignment) {
        textAlignment = newTextAlignment;
    }

    public void setVerticalAlignment(String newVerticalAlignment) {
        verticalAlignment = newVerticalAlignment;
    }

    public void setID(String newID) {
        id = newID;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public String getTextAlignment() {
        return textAlignment;
    }

    public String getVerticalAlignment() {
        return verticalAlignment;
    }

    public String getID() {
        return id;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    public String getForegroundColor() {
        return foregroundColor;
    }

}

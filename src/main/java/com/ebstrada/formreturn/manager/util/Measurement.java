package com.ebstrada.formreturn.manager.util;

public class Measurement {

    public static final int PIXELS = 1;
    public static final int MILLIMETERS = 2;
    public static final int CENTIMETERS = 3;
    public static final int INCHES = 4;

    public static final double DPI = 72.0d;
    public static final double MILLIMETERS_IN_AN_INCH = 25.4d;

    private int from;
    private int to;

    public Measurement() {
        this.from = PIXELS;
        this.to = MILLIMETERS;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public int getTo() {
        return to;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public double convert(double input) {

        // formula:
        // pixels = (mm * dpi) / 25.4
        // inches = pixels / 72

        double output = 0.0d;
        switch (this.from) {
            case PIXELS:
                switch (this.to) {
                    case PIXELS:
                        output = input;
                        break;
                    case MILLIMETERS:
                        output = (input * MILLIMETERS_IN_AN_INCH) / DPI;
                        break;
                    case CENTIMETERS:
                        output = ((input * MILLIMETERS_IN_AN_INCH) / DPI) / 10.0d;
                        break;
                    case INCHES:
                        output = input / DPI;
                        break;
                }
                break;
            case MILLIMETERS:
                switch (this.to) {
                    case PIXELS:
                        output = (input * DPI) / MILLIMETERS_IN_AN_INCH;
                        break;
                    case MILLIMETERS:
                        output = input;
                        break;
                    case CENTIMETERS:
                        output = input / 10.0d;
                        break;
                    case INCHES:
                        output = input / MILLIMETERS_IN_AN_INCH;
                        break;
                }
                break;
            case CENTIMETERS:
                switch (this.to) {
                    case PIXELS:
                        output = ((input * 10.0d) * DPI) / MILLIMETERS_IN_AN_INCH;
                        break;
                    case MILLIMETERS:
                        output = input * 10.0d;
                        break;
                    case CENTIMETERS:
                        output = input;
                        break;
                    case INCHES:
                        output = (input * 10.0d) / MILLIMETERS_IN_AN_INCH;
                        break;
                }
                break;
            case INCHES:
                switch (this.to) {
                    case PIXELS:
                        output = input * DPI;
                        break;
                    case MILLIMETERS:
                        output = input * MILLIMETERS_IN_AN_INCH;
                        break;
                    case CENTIMETERS:
                        output = (input * MILLIMETERS_IN_AN_INCH) / 10.0d;
                        break;
                    case INCHES:
                        output = input;
                        break;
                }
                break;
        }

        return output;

    }

}

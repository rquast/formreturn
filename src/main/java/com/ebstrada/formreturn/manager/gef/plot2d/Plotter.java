package com.ebstrada.formreturn.manager.gef.plot2d;

import java.awt.Color;

public interface Plotter {
    /**
     * Paint this line object.
     */
    public abstract void drawLine(Object graphicsContext, float lineWidth, Color lineColor, int x1,
        int y1, int x2, int y2, boolean dashed, float[] dashes, int dashPeriod);

    public abstract int drawDashedLine(Object graphicsContext, float lineWidth, int x1, int y1,
        int x2, int y2, int phase, float[] dashes, int dashPeriod);

    public abstract void drawOval(Object graphicsContext, boolean filled, Color fillColor,
        Color lineColor, float lineWidth, boolean dashed, int x, int y, int w, int h);

    public abstract void drawRect(Object graphicsContext, boolean filled, Color fillColor,
        float lineWidth, Color lineColor, int x, int y, int w, int h, boolean dashed,
        float dashes[], int dashPeriod);

    public abstract void drawStraight(Object g, Color lineColor, int xKnots[], int yKnots[]);
}

package com.ebstrada.formreturn.manager.gef.plot2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

/**
 * A Java2d implementation of the Plotter interface.
 */
public class Java2d implements Plotter {

    /**
     * The size of the dashes drawn when the Fig is dashed.
     */
    private static final String[] DASHED_CHOICES =
        {"Solid", "Dashed", "LongDashed", "Dotted", "DotDash"};
    private static final float[][] DASH_ARRAYS =
        {null, {5.0f, 5.0f}, {15.0f, 5.0f}, {3.0f, 10.0f}, {3.0f, 6.0f, 10.0f, 6.0f}}; // opaque,
    // transparent,
    // [opaque,
    // transparent]
    private static final int[] DASH_PERIOD = {0, 10, 20, 13, 25,}; // the
    // sum
    // of
    // each
    // subarray

    /**
     *
     */
    public Java2d() {
    }

    /**
     * Paint this line object.
     */
    public void drawLine(Object graphicsContext, int lineWidth, Color lineColor, int x1, int y1,
        int x2, int y2, boolean dashed, float[] dashes, int dashPeriod) {
        if (lineWidth <= 0) {
            return;
        }

        Graphics g = (Graphics) graphicsContext;
        if (dashed) {
            g.setColor(lineColor);
            drawDashedLine(g, lineWidth, x1, y1, x2, y2, 0, dashes, dashPeriod);
        } else {
            g.setColor(lineColor);
            g.drawLine(x1, y1, x2, y2);
        }
    }

    public int drawDashedLine(Object graphicsContext, int lineWidth, int x1, int y1, int x2, int y2,
        int phase, float[] dashes, int dashPeriod) {
        if (graphicsContext instanceof Graphics2D) {
            return drawDashedLineG2D((Graphics2D) graphicsContext, lineWidth, phase, x1, y1, x2, y2,
                dashes, dashPeriod);
        }
        Graphics g = (Graphics) graphicsContext;
        // Fall back on the old inefficient method of drawing dashed
        // lines. This is required until SVGWriter is converted to
        // extend Graphics2D
        int segStartX;
        int segStartY;
        int segEndX;
        int segEndY;
        int dxdx = (x2 - x1) * (x2 - x1);
        int dydy = (y2 - y1) * (y2 - y1);
        int length = (int) Math.sqrt(dxdx + dydy);
        int numDashes = dashes.length;
        int d;
        int dashesDist = 0;
        for (d = 0; d < numDashes; d++) {
            dashesDist += dashes[d];
            // find first partial dash?
        }

        d = 0;
        int i = 0;
        while (i < length) {
            segStartX = x1 + ((x2 - x1) * i) / length;
            segStartY = y1 + ((y2 - y1) * i) / length;
            i += dashes[d];
            d = (d + 1) % numDashes;
            if (i >= length) {
                segEndX = x2;
                segEndY = y2;
            } else {
                segEndX = x1 + ((x2 - x1) * i) / length;
                segEndY = y1 + ((y2 - y1) * i) / length;
            }

            g.drawLine(segStartX, segStartY, segEndX, segEndY);
            i += dashes[d];
            d = (d + 1) % numDashes;
        }

        // needs-more-work: phase not taken into account
        return (length + phase) % dashesDist;
    }

    private int drawDashedLineG2D(Graphics2D g, int lineWidth, int phase, int x1, int y1, int x2,
        int y2, float[] dashes, int dashPeriod) {
        int dxdx = (x2 - x1) * (x2 - x1);
        int dydy = (y2 - y1) * (y2 - y1);
        int length = (int) (Math.sqrt(dxdx + dydy) + 0.5); // This causes a
        // smaller rounding
        // error of
        // 0.5pixels max. .
        // Seems acceptable.
        Graphics2D g2D = g;
        Stroke OriginalStroke = g2D.getStroke(); // we need this to restore
        // the original stroke
        // afterwards

        BasicStroke DashedStroke =
            new BasicStroke(lineWidth, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f,
                dashes, phase);
        // (float width, int cap, int join, float miterlimit, float[] dash,
        // float dash_phase)
        g2D.setStroke(DashedStroke);
        g2D.drawLine(x1, y1, x2, y2);
        g2D.setStroke(OriginalStroke); // undo the manipulation of g

        return (length + phase) % dashPeriod;
    }

    // Taken from FigCircle

    public void drawOval(Object graphicsContext, boolean filled, Color fillColor, Color lineColor,
        int lineWidth, boolean dashed, int x, int y, int w, int h) {
        if (dashed && (graphicsContext instanceof Graphics2D)) {
            Graphics2D g2d = (Graphics2D) graphicsContext;
            Stroke oldStroke = g2d.getStroke();
            float[] dash = {10.0f, 10.0f};
            Stroke stroke =
                new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash,
                    0.0f);
            g2d.setStroke(stroke);
            if (filled && fillColor != null) {
                g2d.setColor(fillColor);
                g2d.fillOval(x, y, w, h);
            }

            if (lineWidth > 0 && lineColor != null) {
                g2d.setColor(lineColor);
                g2d.drawOval(x, y, w - lineWidth, h - lineWidth);
            }

            g2d.setStroke(oldStroke);
        } else if (filled && fillColor != null) {
            Graphics g = (Graphics) graphicsContext;
            if (lineWidth > 0 && lineColor != null) {
                g.setColor(lineColor);
                g.fillOval(x, y, w, h);
            }

            if (!fillColor.equals(lineColor)) {
                g.setColor(fillColor);
                g.fillOval(x + lineWidth, y + lineWidth, w - (lineWidth * 2), h - (lineWidth * 2));
            }
        } else if (lineWidth > 0 && lineColor != null) {
            Graphics g = (Graphics) graphicsContext;
            g.setColor(lineColor);
            g.drawOval(x, y, w, h);
        }
    }

    // Taken from FigRect

    public void drawRect(Object graphicsContext, boolean filled, Color fillColor, int lineWidth,
        Color lineColor, int x, int y, int w, int h, boolean dashed, float dashes[],
        int dashPeriod) {
        Graphics g = (Graphics) graphicsContext;
        if (filled && fillColor != null) {
            int xx = x;
            int yy = y;
            int ww = w;
            int hh = h;
            if (lineColor != null) {
                if (lineWidth > 1 && !dashed) {
                    int lineWidth2 = lineWidth * 2;
                    g.setColor(lineColor);
                    g.fillRect(xx, yy, ww, hh);
                    xx += lineWidth;
                    yy += lineWidth;
                    ww -= lineWidth2;
                    hh -= lineWidth2;
                }
            }
            g.setColor(fillColor);
            g.fillRect(xx, yy, ww, hh);
            if (lineColor != null) {
                if (lineWidth == 1 || dashed) {
                    paintRectLine(g, xx, yy, ww, hh, lineWidth, lineColor, dashed, dashes,
                        dashPeriod);
                }
            }
        } else {
            paintRectLine(g, x, y, w, h, lineWidth, lineColor, dashed, dashes, dashPeriod);
        }
    }

    /**
     * Paint the line of a rectangle without any fill. Manages line width and
     * dashed lines.
     *
     * @param g      The Graphics object
     * @param x      The x co-ordinate of the rectangle
     * @param y      The y co-ordinate of the rectangle
     * @param w      The width of the rectangle
     * @param h      The height of the rectangle
     * @param lwidth The linewidth of the rectangle
     */
    private void paintRectLine(Graphics g, int x, int y, int w, int h, int lineWidth,
        Color lineColor, boolean dashed, float dashes[], int dashPeriod) {
        if (lineWidth > 0 && lineColor != null) {
            g.setColor(lineColor);
            if (lineWidth == 1) {
                paintRectLine(g, x, y, w, h, dashed, lineWidth, dashes, dashPeriod);
            } else {
                int xx = x;
                int yy = y;
                int hh = h;
                int ww = w;

                for (int i = 0; i < lineWidth; ++i) {
                    paintRectLine(g, xx++, yy++, ww, hh, dashed, lineWidth, dashes, dashPeriod);
                    ww -= 2;
                    hh -= 2;
                }
            }
        }
    }

    private void paintRectLine(Graphics g, int x, int y, int w, int h, boolean dashed,
        int lineWidth, float dashes[], int dashPeriod) {
        if (!dashed) {
            g.drawRect(x, y, w, h);
        } else {
            drawDashedRectangle(g, 0, x, y, w, h, lineWidth, dashes, dashPeriod);
        }
    }

    private void drawDashedRectangle(Graphics g, int phase, int x, int y, int w, int h,
        int lineWidth, float dashes[], int dashPeriod) {

        phase = drawDashedLine(g, lineWidth, x, y, x + w, y, phase, dashes, dashPeriod);
        phase = drawDashedLine(g, lineWidth, x + w, y, x + w, y + h, phase, dashes, dashPeriod);
        phase = drawDashedLine(g, lineWidth, x + w, y + h, x, y + h, phase, dashes, dashPeriod);
        phase = drawDashedLine(g, lineWidth, x, y + h, x, y, phase, dashes, dashPeriod);
    }

    public void drawStraight(Object graphicContext, Color lineColor, int xKnots[], int yKnots[]) {
        Graphics g = (Graphics) graphicContext;
        g.setColor(lineColor);
        g.drawLine(xKnots[0], yKnots[0], xKnots[1], yKnots[1]);
    }

    // From FigRRect

    /**
     * Paint this FigRRect. Dashed lines aren't currently handled.
     */
    public void drawRRect(Object graphicsContext, boolean filled, Color fillColor, int lineWidth,
        Color lineColor, int x, int y, int w, int h, int radius) {
        Graphics g = (Graphics) graphicsContext;
        if (filled && fillColor != null) {
            if (lineColor != null && lineWidth > 1) {
                drawFilledRRectWithWideLine(g, fillColor, lineWidth, lineColor, x, y, w, h, radius);
            } else {
                drawFilledRRect(g, fillColor, lineWidth, lineColor, x, y, w, h, radius);
            }
        } else if (lineColor != null && lineWidth > 0) {
            drawEmptyRRectWithWideLine(g, lineWidth, lineColor, x, y, w, h, radius);
        } else {
            drawEmptyRRect(g, lineWidth, lineColor, x, y, w, h, radius);
        }
    }

    /**
     * Paint a filled rounded rectangle (with a narrow line or no line)
     *
     * @param g
     */
    private void drawFilledRRect(Graphics g, Color fillColor, int lineWidth, Color lineColor, int x,
        int y, int w, int h, int radius) {
        // assert _lineWidth == 0 || _lineWidth == 1 || _lineColor == null
        // assert filled && filledColor != null
        // Do the actual fill color
        g.setColor(fillColor);
        g.fillRoundRect(x, y, w, h, radius, radius);

        if (lineColor != null && lineWidth == 1) {
            // If we're filled with a narrow border then draw
            // the border over the already filled area.
            g.setColor(lineColor);
            g.drawRoundRect(x, y, w, h, radius, radius);
        }
    }

    /**
     * Paint a filled rounded rectangle with a wide line
     *
     * @param g
     */
    private void drawFilledRRectWithWideLine(Graphics g, Color fillColor, int lineWidth,
        Color lineColor, int x, int y, int w, int h, int radius) {
        // assert _lineWidth > 1 && _lineColor != null
        // assert filled && filledColor != null
        // If we're filled with a wide border then fill
        // the entire rectangle with the border color and then
        // recalculate area for the actual fill.
        int lineWidth2 = lineWidth * 2;
        g.setColor(lineColor);
        g.fillRoundRect(x, y, w, h, radius, radius);

        // Do the actual fill color
        g.setColor(fillColor);
        g.fillRoundRect(x + lineWidth, y + lineWidth, w - lineWidth2, h - lineWidth2, radius,
            radius);
    }

    /**
     * Paint an unfilled rounded rectangle (with a narrow line or no line)
     *
     * @param g
     */
    private void drawEmptyRRect(Graphics g, int lineWidth, Color lineColor, int x, int y, int w,
        int h, int radius) {
        // If there's no fill but a narrow line then just
        // draw that line.
        if (lineColor != null && lineWidth == 1) {
            g.setColor(lineColor);
            g.drawRoundRect(x, y, w, h, radius, radius);
        }
    }

    /**
     * Paint an unfilled rounded rectangle with a wide line
     *
     * @param g
     */
    private void drawEmptyRRectWithWideLine(Graphics g, int lineWidth, Color lineColor, int x,
        int y, int w, int h, int radius) {
        // If there's no fill but a wide line then draw repeated
        // rounded rectangles in ever decreasing size.
        if (lineColor != null && lineWidth > 1) {
            int xx = x;
            int yy = y;
            int ww = w;
            int hh = h;
            g.setColor(lineColor);
            for (int i = 0; i < lineWidth; ++i) {
                g.drawRoundRect(xx++, yy++, ww, hh, radius, radius);
                ww -= 2;
                hh -= 2;
            }
        }
    }

    public int drawDashedLine(Object graphicsContext, float lineWidth, int x1, int y1, int x2,
        int y2, int phase, float[] dashes, int dashPeriod) {
        return drawDashedLine(graphicsContext, (int) lineWidth, x1, y1, x2, y2, phase, dashes,
            dashPeriod);
    }

    public void drawLine(Object graphicsContext, float lineWidth, Color lineColor, int x1, int y1,
        int x2, int y2, boolean dashed, float[] dashes, int dashPeriod) {
        drawLine(graphicsContext, (int) lineWidth, lineColor, x1, y1, x2, y2, dashed, dashes,
            dashPeriod);

    }

    public void drawOval(Object graphicsContext, boolean filled, Color fillColor, Color lineColor,
        float lineWidth, boolean dashed, int x, int y, int w, int h) {
        drawOval(graphicsContext, filled, fillColor, lineColor, (int) lineWidth, dashed, x, y, w,
            h);
    }

    public void drawRect(Object graphicsContext, boolean filled, Color fillColor, float lineWidth,
        Color lineColor, int x, int y, int w, int h, boolean dashed, float[] dashes,
        int dashPeriod) {
        drawRect(graphicsContext, filled, fillColor, (int) lineWidth, lineColor, x, y, w, h, dashed,
            dashes, dashPeriod);
    }
}

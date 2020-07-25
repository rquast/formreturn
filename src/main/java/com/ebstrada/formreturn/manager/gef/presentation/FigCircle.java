package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;

import com.ebstrada.formreturn.manager.ui.editor.panel.FigCirclePanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Primitive Fig for displaying circles and ovals.
 */

@XStreamAlias("circle") public class FigCircle extends Fig implements NoObfuscation {

    private static final long serialVersionUID = 1L;

    /**
     * Used as a percentage tolerance for making it easier for the user to
     * select a hollow circle with the mouse. Needs-More-Work: This is bad
     * design that needs to be changed. Should use just GRIP_FACTOR.
     */
    public static final double CIRCLE_ADJUST_RADIUS = 0.1;

    protected boolean _isDashed = false;

    /**
     * Construct a new FigCircle with the given position, size, and attributes.
     */
    public FigCircle(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    /**
     * Construct a new FigCircle with the given position, size, line color, and
     * fill color
     */
    public FigCircle(int x, int y, int w, int h, Color lColor, Color fColor) {
        super(x, y, w, h, lColor, fColor);
    }

    /**
     * Construct a new FigCircle w/ the given position and size.
     */
    public FigCircle(int x, int y, int w, int h, boolean resizable) {
        super(x, y, w, h);
        setResizable(resizable);
    }

    /**
     * Construct a new FigCircle w/ the given position, size, line color, and
     * fill color.
     */
    public FigCircle(int x, int y, int w, int h, boolean resizable, Color lColor, Color fColor) {
        super(x, y, w, h, lColor, fColor);
        setResizable(resizable);
    }

    @Override public void paint(Object g) {
        paint(g, false);
    }

    @Override public void paint(Object g, boolean includeMargins) {

        if (includeMargins == false) {
            setMarginOffset(0, 0);
        }
        Graphics2D g2D = (Graphics2D) g;

        Color OriginalColor = g2D.getColor();
        Stroke OriginalStroke = g2D.getStroke();

        if (_filled) {
            g2D.setColor(_fillColor);
            g2D.fillOval(getX(), getY(), _w, _h);
        }

        g2D.setColor(_lineColor);

        g2D.setStroke(getStroke());

        if (_dashStyle == 3) {
            g2D.drawOval(getX() - (int) Math.floor(getLineWidth() / 3.0),
                getY() - (int) Math.floor(getLineWidth() / 3.0),
                _w + (int) Math.floor(2.0 * getLineWidth() / 3.0),
                _h + (int) Math.floor(2.0 * getLineWidth() / 3.0));
            g2D.drawOval(getX() + (int) Math.floor(getLineWidth() / 3.0),
                getY() + (int) Math.floor(getLineWidth() / 3.0),
                _w - (int) Math.floor(2.0 * getLineWidth() / 3.0),
                _h - (int) Math.floor(2.0 * getLineWidth() / 3.0));
        } else {
            g2D.drawOval(getX(), getY(), _w, _h);
        }

        g2D.setColor(OriginalColor);
        g2D.setStroke(OriginalStroke);

    }

    /**
     * Reply true if the given coordinates are inside the circle.
     */
    @Override public boolean contains(int x, int y) {
        if (!super.contains(x, y)) {
            return false;
        }

        double dx = (double) (_x + _w / 2 - x) * 2 / _w;
        double dy = (double) (_y + _h / 2 - y) * 2 / _h;
        double distSquared = dx * dx + dy * dy;
        return distSquared <= 1.01;
    }

    @Override public EditorPanel getEditorPanel() {
        return new FigCirclePanel();
    }

}

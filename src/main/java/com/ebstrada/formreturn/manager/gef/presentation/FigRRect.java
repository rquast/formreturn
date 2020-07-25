package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;

import com.ebstrada.formreturn.manager.gef.properties.PropCategoryManager;
import com.ebstrada.formreturn.manager.ui.editor.panel.FigRRectPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.FigRectPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Primitive Fig to paint rounded rectangles on a LayerDiagram.
 */

@XStreamAlias("roundRectangle") public class FigRRect extends FigRect implements NoObfuscation {

    private static final long serialVersionUID = 1L;

    @XStreamAlias("radius") protected double _radius = 16;

    // //////////////////////////////////////////////////////////////
    // static initializer
    static {
        PropCategoryManager.categorizeProperty("Geometry", "cornerRadius");
    }

    // //////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a new FigRRect w/ the given position and size
     */
    public FigRRect(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    /**
     * Construct a new FigRRect w/ the given position, size, line color, and
     * fill color
     */
    public FigRRect(int x, int y, int w, int h, Color lineColor, Color fillColor) {
        super(x, y, w, h, lineColor, fillColor);
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * get and set the "roundness" of the corners. USED by PGML.tee
     */
    public double getCornerRadius() {
        return _radius;
    }

    public void setCornerRadius(double r) {
        _radius = r;
    }

    // //////////////////////////////////////////////////////////////
    // / painting methods

    @Override public void paint(Object g) {
        paint(g, false);
    }

    @Override public void paint(Object graphicContext, boolean includeMargins) {

        if (includeMargins == false) {
            setMarginOffset(0, 0);
        }

        Graphics g = (Graphics) graphicContext;

        Graphics2D g2D = (Graphics2D) g;

        Color OriginalColor = g2D.getColor();
        Stroke OriginalStroke = g2D.getStroke();

        if (_filled) {
            g2D.setColor(_fillColor);
            g2D.fillRoundRect(getX(), getY(), _w, _h, (int) _radius, (int) _radius);
        }

        g2D.setColor(_lineColor);

        g2D.setStroke(getStroke());

        if (_dashStyle == 3) {
            g2D.drawRoundRect(getX() - (int) Math.floor(getLineWidth() / 3.0),
                getY() - (int) Math.floor(getLineWidth() / 3.0),
                _w + (int) Math.floor(2.0 * getLineWidth() / 3.0),
                _h + (int) Math.floor(2.0 * getLineWidth() / 3.0), (int) _radius, (int) _radius);
            g2D.drawRoundRect(getX() + (int) Math.floor(getLineWidth() / 3.0),
                getY() + (int) Math.floor(getLineWidth() / 3.0),
                _w - (int) Math.floor(2.0 * getLineWidth() / 3.0),
                _h - (int) Math.floor(2.0 * getLineWidth() / 3.0), (int) _radius, (int) _radius);
        } else {
            g2D.drawRoundRect(getX(), getY(), _w, _h, (int) _radius, (int) _radius);
        }

        g2D.setColor(OriginalColor);
        g2D.setStroke(OriginalStroke);

    }

    @Override public EditorPanel getEditorPanel() {
        return new FigRRectPanel();
    }

}

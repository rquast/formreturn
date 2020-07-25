package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.io.Serializable;

import com.ebstrada.formreturn.manager.ui.editor.panel.FigRectPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Primitive Fig to paint rectangles on a LayerDiagram.
 */

@XStreamAlias("rectangle") public class FigRect extends Fig implements Serializable, NoObfuscation {

    // //////////////////////////////////////////////////////////////
    // constructors

    /**
     *
     */
    private static final long serialVersionUID = 6509977011412816644L;

    /**
     * Construct a new resizable FigRect with the given position and size.
     */
    public FigRect(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    /**
     * Construct a new resizable FigRect with the given position, size, line
     * color, and fill color.
     */
    public FigRect(int x, int y, int w, int h, Color lColor, Color fColor) {
        super(x, y, w, h, lColor, fColor);
    }

    /**
     * Construct a new FigRect w/ the given position and size.
     */
    public FigRect(int x, int y, int w, int h, boolean resizable) {
        super(x, y, w, h);
        setResizable(resizable);
    }

    /**
     * Construct a new FigRect w/ the given position, size, line color, and fill
     * color.
     */
    public FigRect(int x, int y, int w, int h, boolean resizable, Color lColor, Color fColor) {
        super(x, y, w, h, lColor, fColor);
        setResizable(resizable);
    }

    // //////////////////////////////////////////////////////////////
    // painting methods

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
            g2D.fillRect(getX(), getY(), _w, _h);
        }

        g2D.setColor(_lineColor);

        g2D.setStroke(getStroke());

        if (_dashStyle == 3) {
            g2D.drawRect(getX() - (int) Math.floor(getLineWidth() / 3.0),
                getY() - (int) Math.floor(getLineWidth() / 3.0),
                _w + (int) Math.floor(2.0 * getLineWidth() / 3.0),
                _h + (int) Math.floor(2.0 * getLineWidth() / 3.0));
            g2D.drawRect(getX() + (int) Math.floor(getLineWidth() / 3.0),
                getY() + (int) Math.floor(getLineWidth() / 3.0),
                _w - (int) Math.floor(2.0 * getLineWidth() / 3.0),
                _h - (int) Math.floor(2.0 * getLineWidth() / 3.0));
        } else {
            g2D.drawRect(getX(), getY(), _w, _h);
        }

        g2D.setColor(OriginalColor);
        g2D.setStroke(OriginalStroke);

    }

    @Override public EditorPanel getEditorPanel() {
        return new FigRectPanel();
    }

}

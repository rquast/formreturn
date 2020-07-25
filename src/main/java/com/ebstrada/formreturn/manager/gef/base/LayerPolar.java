package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.List;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;

/**
 * This class is an example of the power of the Layer-based approach. This is a
 * kind of background drawing guide (like LayerGrid) that emphasizes polar
 * coordinates (instead of rectangular coordinates).
 */

public class LayerPolar extends Layer {

    private static final long serialVersionUID = -7584346767965616421L;

    /**
     * The origin of this coordinate system
     */
    private int _originX = 0, _originY = 0;

    /**
     * The spacing between the lines
     */
    private int _spacing = 32;

    /**
     * The painting stlye that should be used. See predefined styles below.
     */
    private int _style = 0;
    private int NUM_STYLES = 5;

    /**
     * The color of the grid lines
     */
    protected Color _lineColor = new Color(55, 55, 255);

    /**
     * The color of the space between the lines.
     * <p>
     * Needs-More-Work: null should mean the space between the lines is
     * transparent. But that does not work with image stamps.
     */
    protected Color _bgColor = null;

    // //////////////////////////////////////////////////////////////
    // Constructors

    /**
     * Construct a new LayerPolar and name it "Grid". Needs-More-Work: maybe the
     * 'grid' should be named 'drawing guide' instead of 'grid' since 'grid'
     * implies rectilinearity.
     */
    public LayerPolar() {
        super("Grid");
    }

    /**
     * Make a new LayerPolar with the given origin and spacing
     */
    public LayerPolar(int x, int y, int s) {
        super("Grid");
        origin(x, y);
        spacing(s);
    }

    // //////////////////////////////////////////////////////////////
    // Accessors

    /**
     * Set the origin
     */
    public void origin(int x, int y) {
        _originX = x;
        _originY = y;
    }

    /**
     * Set the spacing between lines
     */
    public void spacing(int s) {
        _spacing = s;
    }

    /**
     * return the integer distance between a given point and the origin
     */
    public int dist(int x, int y) {
        int sqrd = (_originX - x) * (_originX - x) + (_originY - y) * (_originY - y);
        return (int) Math.round(Math.sqrt(sqrd));
    }

    // needs-more-work: get/set naming convention
    public void lineColor(Color c) {
        _lineColor = c;
    }

    public Color lineColor() {
        return _lineColor;
    }

    public void bgColor(Color c) {
        _bgColor = c;
    }

    public Color bgColor() {
        return _bgColor;
    }

    @Override public List getContents() {
        return null;
    }

    @Override public Fig presentationFor(Object obj) {
        return null;
    }

    // //////////////////////////////////////////////////////////////
    // Painting methods

    /**
     * Paint concentric circles around the origin with each circle a certain
     * spacing from the previous one
     */
    @Override public void paintContents(Graphics g) {
        Rectangle clip = g.getClipBounds();
        int clipBot = clip.y + clip.height;
        int clipRight = clip.x + clip.width;

        if (_bgColor != null) {
            g.setColor(_bgColor);
            g.fillRect(clip.x, clip.y, clip.width, clip.height);
        }

        int d1 = dist(clip.x, clip.y);
        int d2 = dist(clip.x, clipBot);
        int d3 = dist(clipRight, clip.y);
        int d4 = dist(clipRight, clipBot);
        int maxDist = Math.max(Math.max(d1, d2), Math.max(d3, d4));

        maxDist = maxDist / _spacing * _spacing + 1;

        g.setColor(_lineColor);

        int startX = _originX;
        int startY = _originY;
        int size = 0;
        int limit = 2 * maxDist;
        while (size <= limit) {
            g.drawOval(startX, startY, size, size);
            startX -= _spacing;
            startY -= _spacing;
            size += _spacing;
            size += _spacing;
        }
    }

    /**
     * Change the appearance of this layer: vary the spacing and origin.
     * Needs-More-Work: should put up a dialog to ask the user to specify some
     * parameters
     */
    @Override public void adjust() {
        // in the future, open a dialog box with lots of options, for now,
        // just cycle though some examples
        _style = (_style + 1) % NUM_STYLES;
        switch (_style) {
            case 0:
                setHidden(false);
                origin(0, 0);
                spacing(32);
                break;
            case 1:
                setHidden(false);
                origin(0, 0);
                spacing(16);
                break;
            case 2:
                setHidden(false);
                origin(50, 50);
                spacing(16);
                break;
            case 3:
                setHidden(false);
                Editor ce = Globals.curEditor();
                if (ce != null) {
                    Dimension d = ce.getJComponent().getSize();
                    origin(d.width / 2, d.height / 2);
                } else {
                    origin(100, 100);
                }
                spacing(16);
                break;
            case 4:
                setHidden(true);
                break;
        }
    }
} /* end class LayerPolar */

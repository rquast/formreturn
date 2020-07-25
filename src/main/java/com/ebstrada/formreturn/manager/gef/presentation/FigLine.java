package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;

import com.ebstrada.formreturn.manager.gef.base.Layer;
import com.ebstrada.formreturn.manager.gef.graph.MutableGraphSupport;
import com.ebstrada.formreturn.manager.gef.undo.Memento;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.gef.undo.memento.FigAddMemento;
import com.ebstrada.formreturn.manager.ui.editor.panel.FigLinePanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Class to display lines in diagrams.
 */

@XStreamAlias("line") public class FigLine extends Fig implements NoObfuscation {

    private static final long serialVersionUID = 1L;

    /**
     * Coordinates of the start and end points of the line. Note: _x, _y, _w,
     * and _h from class Fig are always updated by calcBounds() whenever _x1,
     * _y1, _x2, or _y2 change.
     */
    @XStreamAlias("x1") protected int _x1;

    @XStreamAlias("y1") protected int _y1;

    @XStreamAlias("x2") protected int _x2;

    @XStreamAlias("y2") protected int _y2;

    // //////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a new FigLine with the given coordinates and color.
     */
    public FigLine(int x1, int y1, int x2, int y2, Color lineColor) {
        super();
        setX1(x1);
        setY1(y1);
        setX2(x2);
        setY2(y2);
        setLineColor(lineColor);
        calcBounds();
    }

    /**
     * Construct a new FigLine with the given coordinates and attributes.
     */
    public FigLine(int x1, int y1, int x2, int y2) {
        super();
        setX1(x1);
        setY1(y1);
        setX2(x2);
        setY2(y2);
        calcBounds();
    }

    /**
     * A convenient constructor for PGMLStackParser to create FigLines by
     * reflection. Do not consider this part of the normal API.
     */
    public FigLine() {
        super();
        setX1(0);
        setY1(0);
        setX2(0);
        setY2(10);
        calcBounds();
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * Set both end points. Fires PropertyChange with "bounds".
     */
    public final void setShape(Point p1, Point p2) {
        setShape(p1.x, p1.y, p2.x, p2.y);
    }

    /**
     * Set both end points. Fires PropertyChange with "bounds".
     */
    public void setShape(int x1, int y1, int x2, int y2) {
        _x1 = x1;
        _y1 = y1;
        _x2 = x2;
        _y2 = y2;
        calcBounds();
        firePropChange("bounds", null, null);
    }

    public int getX1() {
        return _x1 + getXOffset() + getLeftMarginOffset();
    }

    public int getY1() {
        return _y1 + getYOffset() + getTopMarginOffset();
    }

    public int getX2() {
        return _x2 + getXOffset() + getLeftMarginOffset();
    }

    public int getY2() {
        return _y2 + getYOffset() + getTopMarginOffset();
    }

    /**
     * Set one of the end point coordinates. Each of these methods fires
     * PropertyChange with "bounds".
     */
    public void setX1(int x1) {
        _x1 = x1;
        calcBounds();
        firePropChange("bounds", null, null);
    }

    public void setY1(int y1) {
        _y1 = y1;
        calcBounds();
        firePropChange("bounds", null, null);
    }

    public void setX2(int x2) {
        _x2 = x2;
        calcBounds();
        firePropChange("bounds", null, null);
    }

    public void setY2(int y2) {
        _y2 = y2;
        calcBounds();
        firePropChange("bounds", null, null);
    }

    // //////////////////////////////////////////////////////////////
    // Fig API

    /**
     * Lines can be reshaped, but not resized or rotated (for now).
     */
    @Override public boolean isResizable() {
        return false;
    }

    @Override public boolean isReshapable() {
        return true;
    }

    @Override public boolean isRotatable() {
        return false;
    }

    /**
     * Sets both endpoints of a line. Length of array must be 2. Fires
     * PropertyChange with "bounds".
     */
    @Override public void setPoints(Point[] ps) {
        if (ps.length != 2) {
            throw new IllegalArgumentException("FigLine must have exactly 2 points");
        }
        _x1 = ps[0].x;
        _y1 = ps[0].y;
        _x2 = ps[1].x;
        _y2 = ps[1].y;
        calcBounds();
        firePropChange("bounds", null, null);
    }

    /**
     * returns an array of lenfth 2 that has the line's endpoints.
     */
    @Override public Point[] getPoints() {
        Point[] ps = new Point[2];
        ps[0] = new Point(_x1, _y1);
        ps[1] = new Point(_x2, _y2);
        return ps;
    }

    /**
     * Move point i to location (x, y). Argument i must be 0 or 1. Fires
     * PropertyChange with "bounds".
     */
    public void setPoints(int i, int x, int y) {
        if (i == 0) {
            _x1 = x;
            _y1 = y;
        } else if (i == 1) {
            _x2 = x;
            _y2 = y;
        } else {
            throw new IndexOutOfBoundsException("FigLine has exactly 2 points");
        }
        calcBounds();
        firePropChange("bounds", null, null);
    }

    /**
     * Move point i to location (x, y). Argument i must be 0 or 1. Fires
     * PropertyChange with "bounds".
     */
    @Override public void setPoint(int i, int x, int y) {
        if (i == 0) {
            _x1 = x;
            _y1 = y;
        } else if (i == 1) {
            _x2 = x;
            _y2 = y;
        } else {
            throw new IndexOutOfBoundsException("FigLine has exactly 2 points");
        }
        calcBounds();
        firePropChange("bounds", null, null);
    }

    /**
     * returns the ith point. Argument i must be 0 or 1.
     */
    @Override public Point getPoint(int i) {
        if (i == 0) {
            return new Point(_x1, _y1);
        } else if (i == 1) {
            return new Point(_x2, _y2);
        }
        throw new IndexOutOfBoundsException("FigLine has exactly 2 points");
    }

    /**
     * Returns the number of points. Lines always have 2 points.
     */
    @Override public int getNumPoints() {
        return 2;
    }

    /**
     * Returns an array of the X coordinates of all (2) points.
     */
    @Override public int[] getXs() {
        int[] xs = new int[2];
        xs[0] = _x1;
        xs[1] = _x2;
        return xs;
    }

    /**
     * Returns an array of the Y coordinates of all (2) points.
     */
    @Override public int[] getYs() {
        int[] ys = new int[2];
        ys[0] = _y1;
        ys[1] = _y2;
        return ys;
    }

    /**
     * return the approximate arc length of the path in pixel units
     */
    @Override public int getPerimeterLength() {
        int dxdx = (_x2 - _x1) * (_x2 - _x1);
        int dydy = (_y2 - _y1) * (_y2 - _y1);
        return (int) Math.sqrt(dxdx + dydy);
    }

    /**
     * return a point that is dist pixels along the path
     */
    @Override public void stuffPointAlongPerimeter(int dist, Point res) {
        int len = getPerimeterLength();
        if (dist <= 0) {
            res.x = _x1;
            res.y = _y1;
            return;
        }
        if (dist >= len) {
            res.x = _x2;
            res.y = _y2;
            return;
        }
        res.x = _x1 + ((_x2 - _x1) * dist) / len;
        res.y = _y1 + ((_y2 - _y1) * dist) / len;
    }

    /**
     * Sets the bounds of the line. The line is scaled to fit within the new
     * bounding box. Fires PropertyChange with "bounds".
     */
    @Override protected void setBoundsImpl(int x, int y, int w, int h) {
        _x1 = (_w == 0) ? x : x + ((_x1 - _x) * w) / _w;
        _y1 = (_h == 0) ? y : y + ((_y1 - _y) * h) / _h;
        _x2 = (_w == 0) ? x : x + ((_x2 - _x) * w) / _w;
        _y2 = (_h == 0) ? y : y + ((_y2 - _y) * h) / _h;
        calcBounds(); // _x = x; _y = y; _w = w; _h = h;
        firePropChange("bounds", null, null);
    }

    /**
     * Translate this Fig. Fires PropertyChange with "bounds".
     *
     * @param dx the x offset
     * @param dy the y offset
     */
    @Override protected void translateImpl(int dx, int dy) {
        _x1 += dx;
        _y1 += dy;
        _x2 += dx;
        _y2 += dy;
        _x += dx;
        _y += dy; // dont calcBounds because _w and _h are unchanged
        firePropChange("bounds", null, null);
    }

    /**
     * Update the bounding box so that it encloses (_x1, _y1)--(_x2, _y2).
     */
    @Override public void calcBounds() {
        if (_x1 < _x2) {
            _x = _x1;
            _w = _x2 - _x1;
        } else {
            _x = _x2;
            _w = _x1 - _x2;
        }

        if (_y1 < _y2) {
            _y = _y1;
            _h = _y2 - _y1;
        } else {
            _y = _y2;
            _h = _y1 - _y2;
        }
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

        g2D.setColor(_lineColor);

        g2D.setStroke(getStroke());

        if (_dashStyle == 3) {
            g2D.translate(0, 0.5 - getLineWidth() / 3);
            g2D.drawLine(getX1(), getY1(), getX2(), getY2());
            g2D.translate(0, 2 * getLineWidth() / 3);
            g2D.drawLine(getX1(), getY1(), getX2(), getY2());
            g2D.translate(0, -0.5 - getLineWidth() / 3);
        } else {
            g2D.drawLine(getX1(), getY1(), getX2(), getY2());
        }

        g2D.setColor(OriginalColor);
        g2D.setStroke(OriginalStroke);

    }

    /**
     * Reply true if the given point is "near" the line. Nearness allows the
     * user to more easily select the line with the mouse. Needs-More-Work: I
     * should probably have two functions contains() which gives a strict
     * geometric version, and hit() which is for selection by mouse clicks.
     */
    @Override public boolean hit(Rectangle r) {
        return intersects(r);
    }

    /**
     * Resize the object for drag on creation. It bypasses the things done in
     * resize so that the position of the object can be kept as the anchor
     * point. Fires PropertyChange with "bounds".
     */
    @Override public void createDrag(int anchorX, int anchorY, int x, int y, int snapX, int snapY,
        boolean released) {
        _x2 = snapX;
        _y2 = snapY;
        calcBounds();

        if (released) {
            MutableGraphSupport.enableSaveAction();
            if (UndoManager.getInstance().isGenerateMementos()) {
                UndoManager.getInstance().startChain();
                UndoManager.getInstance().addMemento(new FigAddMemento(this));
            }

        }

        firePropChange("bounds", null, null);
    }

    /**
     * Tests if the given rectangle intersects with the perimeter of this
     * polygon. For this implementation the polygon is just a 2 dimensional
     * straight line. So this method is the same as intersects.
     *
     * @param rect The rectangle to be tested.
     * @return True, if the rectangle intersects the perimeter, otherwise false.
     */
    @Override public boolean intersectsPerimeter(Rectangle rect) {
        return intersects(rect);
    }

    /**
     * Tests, if the given rectangle intersects with this line
     *
     * @param rect The rectangle to be tested.
     * @return True, if the rectangle intersects the perimeter, otherwise false.
     */
    @Override public boolean intersects(Rectangle rect) {
        return rect.intersectsLine(_x1, _y1, _x2, _y2);
    }

    @Override public EditorPanel getEditorPanel() {
        return new FigLinePanel();
    }

}

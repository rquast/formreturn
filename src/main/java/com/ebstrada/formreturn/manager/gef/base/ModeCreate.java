package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebstrada.formreturn.manager.gef.di.GraphElement;
import com.ebstrada.formreturn.manager.gef.graph.MutableGraphSupport;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;

/**
 * Abstract superclass for all Mode's that create new Figs. This class factors
 * our shared code that would otherwise be duplicated in its subclasses. On a
 * mouse down the new item is created in memory. On mouse drag the new item is
 * resized via its createDrag() method. On mouse up the new item is officially
 * added to the Layer being edited in the parent Editor, the item is selected,
 * and the Editor is placed in the next Mode (usually ModeSelect). Subclasses
 * override various of these event handlers to give specific behaviors, for
 * example, ModeCreateEdge handles dragging differently.
 *
 * @see ModeCreateFigRect
 * @see ModeCreateFigRRect
 * @see ModeCreateFigLine
 * @see ModeCreateFigCircle
 * @see ModeCreateFigText
 * @see ModeCreateFigImage
 */

public abstract class ModeCreate extends FigModifyingModeImpl {
    // //////////////////////////////////////////////////////////////
    // static variables

    /**
     * The default size of a Fig if the user simply clicks instead of dragging
     * out a size.
     */
    protected int _defaultWidth = 32;
    protected int _defaultHeight = 32;

    // //////////////////////////////////////////////////////////////
    // instance variables

    /**
     * Original mouse down event coordinates
     */
    protected int anchorX, anchorY;

    /**
     * This holds the Fig to be added to the parent Editor.
     */
    protected Fig _newItem;

    private static Log LOG = LogFactory.getLog(ModeCreate.class);

    // //////////////////////////////////////////////////////////////
    // constructors

    public ModeCreate(Editor par) {
        super(par);
        // System.out.println("Created mode " + this + " on editor " + par);
    }

    public ModeCreate() {
        super();
        // System.out.println("Created mode " + this);
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * By default all creation modes use CROSSHAIR_CURSOR.
     */
    @Override public Cursor getInitialCursor() {
        return Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);
    }

    // //////////////////////////////////////////////////////////////
    // event handlers

    private Point snapPt = new Point(0, 0);

    /**
     * On mouse down, make a new Fig in memory.
     */
    @Override public void mousePressed(MouseEvent me) {
        createFig(me);
        if (!(_newItem instanceof GraphElement) && editor
            .getGraphModel() instanceof MutableGraphSupport) {
            ((MutableGraphSupport) (editor.getGraphModel())).fireGraphChanged();
        }
    }

    protected void createFig(MouseEvent me) {
        if (me.isConsumed()) {
            return;
        }
        start();
        synchronized (snapPt) {
            snapPt.setLocation(me.getX(), me.getY());
            editor.snap(snapPt);
            anchorX = snapPt.x;
            anchorY = snapPt.y;
        }
        _newItem = createNewItem(me, anchorX, anchorY);
        me.consume();
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
    }

    /**
     * On mouse drag, resize the new item as the user moves the mouse. Maybe the
     * Fig createDrag() method should be removed and I should call dragHandle().
     * That would elimiate one method from each oif several classes, but
     * dragging during creation is not really the same thing as dragging after
     * creation....
     * <p>
     * <p>
     * Note: _newItem has not been added to any Layer yet. So you cannot use
     * _newItem.damage(), instead use editor.damageAll(_newItem).
     */
    @Override public void mouseDragged(MouseEvent me) {
        if (me.isConsumed()) {
            return;
        }
        if (_newItem != null) {
            editor.damageAll();

            int x2 = me.getX();
            boolean x2flag = isFigX2InPageBoundary(me.getX());
            if (!x2flag) {
                x2 = getPageBoundaryWidth(me.getX());
            }

            int y2 = me.getY();
            boolean y2flag = isFigY2InPageBoundary(me.getY());
            if (!y2flag) {
                y2 = getPageBoundaryHeight(me.getY());
            }
            creationDrag(x2, y2, false);
            editor.damageAll();
        }

        me.consume();
    }

    /**
     * On mouse up, officially add the new item to the parent Editor and select
     * it. Then exit this mode.
     */
    @Override public void mouseReleased(MouseEvent me) {
        if (me.isConsumed()) {
            // return;
        }
        if (_newItem != null) {
            editor.damageAll();

            int x2 = me.getX();
            boolean x2flag = isFigX2InPageBoundary(me.getX());
            if (!x2flag) {
                x2 = getPageBoundaryWidth(me.getX());
            }

            int y2 = me.getY();
            boolean y2flag = isFigY2InPageBoundary(me.getY());
            if (!y2flag) {
                y2 = getPageBoundaryHeight(me.getY());
            }
            creationDrag(x2, y2, true);

            editor.add(_newItem);
            editor.getSelectionManager().select(_newItem);
            _newItem = null;
        }
        done();
        if (!(me.isConsumed())) {
            me.consume();
        }
    }

    @Override public void keyTyped(KeyEvent ke) {
        if (ke.getKeyChar() == KeyEvent.VK_ESCAPE) {
            LOG.debug("ESC pressed");
            leave();
        }
    }

    /**
     * Update the new item to reflect the new mouse position. By default let the
     * new item set its size, subclasses may override. If the user simply clicks
     * instead of dragging then use the default size. If the user actually drags
     * out a Fig, then use its size as the new default size.
     *
     * @see ModeCreateFigLine#creationDrag
     */
    protected void creationDrag(int x, int y, boolean released) {
        if (_newItem == null) {
            return;
        }
        int snapX, snapY;
        synchronized (snapPt) {
            snapPt.setLocation(x, y);
            editor.snap(snapPt);
            snapX = snapPt.x;
            snapY = snapPt.y;
        }
        if (anchorX == snapX && anchorY == snapY) {
            (_newItem).createDrag(anchorX, anchorY, x + _defaultWidth, y + _defaultHeight,
                snapX + _defaultWidth, snapY + _defaultHeight, released);
        } else {
            (_newItem).createDrag(anchorX, anchorY, x, y, snapX, snapY, released);
            _defaultWidth = snapX - anchorX;
            _defaultHeight = snapY - anchorY;
        }
    }

    // //////////////////////////////////////////////////////////////
    // painting methods

    /**
     * Paint this mode by painting the new item. This is the only feedback that
     * the user will get since the new item is not officially added to the
     * Editor's document yet.
     */
    @Override public void paint(Graphics g) {
        if (null != _newItem) {
            // Graphics g = (Graphics)graphicsContext;
            _newItem.paint(g);
        }
    }

    // //////////////////////////////////////////////////////////////
    // ModeCreate API

    /**
     * Abstact method to construct a new Fig to be added to the Editor.
     * Typically, subclasses will make a new instance of some Fig based on the
     * given mouse down event and the state of the parent Editor (specifically,
     * its default graphical attributes).
     */
    public abstract Fig createNewItem(MouseEvent me, int snapX, int snapY);

    public int getDefaultWidth() {
        return _defaultWidth;
    }

    public void setDefaultWidth(int width) {
        _defaultWidth = width;
    }

    public int getDefaultHeight() {
        return _defaultHeight;
    }

    public void setDefaultHeight(int height) {
        _defaultHeight = height;
    }
} /* end class ModeCreate */

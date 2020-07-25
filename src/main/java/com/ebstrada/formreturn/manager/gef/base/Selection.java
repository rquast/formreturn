package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.Handle;

/**
 * This class represents the "selection" object that is used when you select one
 * or more Figs in the drawing window. Selections handle the display of handles
 * or whatever graphics indicate that something is selected, and they process
 * events to manipulate the selected Fig.
 *
 * @author jrobbins
 * @see Fig
 */

public abstract class Selection
    implements Serializable, MouseListener, MouseMotionListener, KeyListener {

    /**
     * The size of the little handle boxes.
     */
    public static final double HAND_SIZE = 10d;

    /**
     * The margin between the contents bbox and the frame
     */
    public static final double BORDER_WIDTH = 4d;

    /**
     * The Fig that is selected.
     */
    private Fig content;

    /**
     * Construct a new selection. Empty, subclases can override
     */
    public Selection(Fig f) {
        if (null == f) {
            throw new IllegalArgumentException("Cannot place a null Fig inside a Selection");
        }
        if (!f.isSelectable()) {
            throw new IllegalArgumentException("The given Fig cannot be selected");
        }
        content = f;
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * Get the Fig that is selected
     */
    public Fig getContent() {
        return content;
    }

    public boolean getLocked() {
        return getContent().getLocked();
    }

    /**
     * Reply true if this selection contains the given Fig
     */
    public boolean contains(Fig f) {
        return f == content;
    }

    // //////////////////////////////////////////////////////////////
    // display methods

    /**
     * Do nothing. Selections shoudl not appear in print outs.
     */
    public void print(Graphics g) {
    }

    /**
     * Abstract method to display the selection handles. TODO: make this method
     * abstract
     */
    public void paint(Graphics g) {

    }

    /**
     * Tell the content to end a transaction that causes damage
     */
    public void endTrans() {
        getContent().endTrans();
    }

    /**
     * Reply the position of the Selection. That is defined to be the upper left
     * corner of my bounding box.
     */
    public Point getLocation() {
        return content.getLocation();
    }

    /**
     * This selection object needs to be redrawn, register its damaged area
     * within the given Editor
     */
    public void damage() {
        content.damage();
    }

    /**
     * Reply true if the given point is inside this selection
     */
    public final boolean contains(Point pnt) {
        return contains(pnt.x, pnt.y);
    }

    public boolean contains(int x, int y) {
        if (content.contains(x, y)) {
            return true;
        }
        Handle h = new Handle(-1);
        hitHandle(x, y, 0, 0, h);
        return (h.index != -1);
    }

    /**
     * Reply true if the given Rectangle is inside or overlapps me
     */
    public boolean hit(Rectangle r) {
        if (content.hit(r)) {
            return true;
        }
        Handle h = new Handle(-1);
        hitHandle(r, h);
        return (h.index != -1);
    }

    /**
     * Find which handle the user clicked on, or return -1 if none.
     */
    // public abstract int hitHandle(Rectangle r);
    public abstract void hitHandle(Rectangle r, Handle h);

    public final void hitHandle(int x, int y, int w, int h, Handle hdl) {
        hitHandle(new Rectangle(x, y, w, h), hdl);
    }

    /**
     * Tell the selected Fig to move to front or back, etc.
     */
    public void reorder(int func, Layer lay) {
        lay.reorder(content, func);
    }

    // /** Do nothing because alignment only makes sense for multiple
    // * selections */
    // public void align(int direction) { /* do nothing */ }

    // public void align(Rectangle r, int direction, Editor ed) {
    // _content.align(r, direction, ed);
    // }

    /**
     * When the selection is told to move, move the selected Fig
     */
    public void translate(int dx, int dy) {
        content.translate(dx, dy);
    }

    /**
     * The bounding box of the selection is the bbox of the contained Fig with
     * added space for the handles.
     */
    public Rectangle getBounds() {
        return new Rectangle(content.getX() - getHandSize() / 2, content.getY() - getHandSize() / 2,
            content.getWidth() + getHandSize(), content.getHeight() + getHandSize());
    }

    public int getHandSize() {
        double scale = content.getGraph().getEditor().getScale();
        return (int) Math.ceil(HAND_SIZE / scale);
    }

    public int getBorderWidth() {
        double scale = content.getGraph().getEditor().getScale();
        return (int) Math.ceil(BORDER_WIDTH / scale);
    }

    /**
     * Returns my bounding box in the given Rectangle. This avoids memory
     * allocation.
     */
    public Rectangle getBounds(Rectangle r) {
        if (r == null) {
            return getBounds();
        }
        r.setBounds(content.getX() - getHandSize() / 2, content.getY() - getHandSize() / 2,
            content.getWidth() + getHandSize(), content.getHeight() + getHandSize());
        return r;
    }

    /**
     * If the selection is being deleted, the selected object must be deleted
     * also. This is different from just deselecting the selected Fig, to do
     * that use one of the deselect operations in SelectionManager.
     *
     * @see SelectionManager#deselect
     */
    public void delete() {
        content.removeFromDiagram();
    }

    public void dispose() {
        content.deleteFromModel();
    }

    /**
     * Move one of the handles of a selected Fig.
     */
    public abstract void dragHandle(int mx, int my, int an_x, int an_y, Handle h);

    /**
     * Reply the bounding box of the selected Figs, does not include space used
     * by handles.
     */
    public Rectangle getContentBounds() {
        return content.getBounds();
    }

    // //////////////////////////////////////////////////////////////
    // event handlers

    /**
     * Pass any events along to the selected Fig. Subclasses of Selection may
     * reimplement this to add functionality.
     */
    public void keyTyped(KeyEvent ke) {
        if (content instanceof KeyListener) {
            ((KeyListener) content).keyTyped(ke);
        }
    }

    public void keyPressed(KeyEvent ke) {
        if (content instanceof KeyListener) {
            ((KeyListener) content).keyPressed(ke);
        }
    }

    public void keyReleased(KeyEvent ke) {
        if (content instanceof KeyListener) {
            ((KeyListener) content).keyReleased(ke);
        }
    }

    public void mouseMoved(MouseEvent me) {
        if (content instanceof MouseMotionListener) {
            ((MouseMotionListener) content).mouseMoved(me);
        }
    }

    public void mouseDragged(MouseEvent me) {
        if (content instanceof MouseMotionListener) {
            ((MouseMotionListener) content).mouseDragged(me);
        }
    }

    public void mousePressed(MouseEvent me) {
        if (content instanceof MouseListener) {
            ((MouseListener) content).mousePressed(me);
        }
    }

    public void mouseReleased(MouseEvent me) {
        if (content instanceof MouseListener) {
            ((MouseListener) content).mouseReleased(me);
        }
    }

    public void mouseClicked(MouseEvent me) {
        if (content instanceof MouseListener) {
            ((MouseListener) content).mouseClicked(me);
        }
    }

    public void mouseEntered(MouseEvent me) {
        if (content instanceof MouseListener) {
            ((MouseListener) content).mouseEntered(me);
        }
    }

    public void mouseExited(MouseEvent me) {
        if (content instanceof MouseListener) {
            ((MouseListener) content).mouseExited(me);
        }
    }
} /* end class Selection */

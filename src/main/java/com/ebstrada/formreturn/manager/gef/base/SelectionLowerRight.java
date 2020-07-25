package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.Handle;

/**
 * A Selection class to represent selections on Figs that present resize
 * handles. The selected Fig can be moved or resized. Figrect, FigRRect,
 * FigCircle, and FigGroup are some of the Figs that normally use this
 * Selection. The selected Fig is told it's new bounding box, and some Figs
 * (like FigGroup or FigPoly) do calculations to scale themselves.
 */

public class SelectionLowerRight extends Selection {

    private static final long serialVersionUID = 434028821570397508L;

    /**
     * Construct a new SelectionLowerRight for the given Fig
     */
    public SelectionLowerRight(Fig f) {
        super(f);
    }

    /**
     * Return a handle ID for the handle under the mouse, or -1 if none.
     * Needs-More-Work: in the future, return a Handle instance or null.
     *
     *
     * <pre>
     *   0-------1-------2
     *   |               |
     *   3               4
     *   |               |
     *   5-------6-------7
     * </pre>
     */
    @Override public void hitHandle(Rectangle r, Handle h) {
        int cx = getContent().getX();
        int cy = getContent().getY();
        int cw = getContent().getWidth();
        int ch = getContent().getHeight();
        Rectangle testRect = new Rectangle(cx + cw, cy + ch, getHandSize(), getHandSize());
        if (r.intersects(testRect)) {
            h.index = 7;
            h.instructions = "Resize object";
        } else {
            h.index = -1;
        }
    }

    /**
     * Paint the handles at the four corners and midway along each edge of the
     * bounding box.
     */
    @Override public void paint(Graphics g) {
        int cx = getContent().getX();
        int cy = getContent().getY();
        int cw = getContent().getWidth();
        int ch = getContent().getHeight();
        g.setColor(Globals.getPrefs().handleColorFor(getContent()));

        // dashed
        g.drawRect(cx - getHandSize() / 2, cy - getHandSize() / 2, cw + getHandSize() - 1,
            ch + getHandSize() - 1);
        g.fillRect(cx + cw, cy + ch, getHandSize(), getHandSize());
    }

    /**
     * Change some attribute of the selected Fig when the user drags one of its
     * handles. Needs-More-Work: someday I might implement resizing that
     * maintains the aspect ratio.
     */
    @Override public void dragHandle(int mX, int mY, int anX, int anY, Handle hand) {
        int x = getContent().getX(), y = getContent().getY();
        int w = getContent().getWidth(), h = getContent().getHeight();
        int newX = x, newY = y, newW = w, newH = h;
        Dimension minSize = getContent().getMinimumSize();
        int minWidth = minSize.width, minHeight = minSize.height;
        switch (hand.index) {
            case -1:
                getContent().translate(anX - mX, anY - mY);
                return;
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                newW = mX - x;
                newW = (newW < minWidth) ? minWidth : newW;
                newH = mY - y;
                newH = (newH < minHeight) ? minHeight : newH;
                break;
            default:
                System.out.println("invalid handle number");
                break;
        }
        getContent().setBounds(newX, newY, newW, newH);
    }
} /* end class SelectionLowerRight */

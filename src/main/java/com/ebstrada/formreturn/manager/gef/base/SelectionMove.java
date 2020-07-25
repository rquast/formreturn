package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Graphics;
import java.awt.Rectangle;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.Handle;

/**
 * Selection object that allows the user to move the selected Fig, but not to
 * resize it.
 */

public class SelectionMove extends Selection {

    private static final long serialVersionUID = 2136083601083895759L;

    /**
     * Construct a new SelectionMove around the given DiagramElement
     */
    public SelectionMove(Fig f) {
        super(f);
    }

    /**
     * Paint the selection.
     */
    @Override public void paint(Graphics g) {
        Fig fig = getContent();
        int x = fig.getX();
        int y = fig.getY();
        int w = fig.getWidth();
        int h = fig.getHeight();
        g.setColor(Globals.getPrefs().handleColorFor(fig));
        g.drawRect(x - getBorderWidth(), y - getBorderWidth(), w + getBorderWidth() * 2 - 1,
            h + getBorderWidth() * 2 - 1);
        g.drawRect(x - getBorderWidth() - 1, y - getBorderWidth() - 1,
            w + getBorderWidth() * 2 + 2 - 1, h + getBorderWidth() * 2 + 2 - 1);
        g.fillRect(x - getHandSize(), y - getHandSize(), getHandSize(), getHandSize());
        g.fillRect(x + w, y - getHandSize(), getHandSize(), getHandSize());
        g.fillRect(x - getHandSize(), y + h, getHandSize(), getHandSize());
        g.fillRect(x + w, y + h, getHandSize(), getHandSize());
    }

    /**
     * SelectionMove is used when there are no handles, so dragHandle does
     * nothing. Actually, hitHandle always returns -1 , so this method should
     * never even get called.
     */
    @Override public void dragHandle(int mx, int my, int an_x, int an_y, Handle h) {
        /* do nothing */
    }

    /**
     * Return -1 as a special code to indicate that the user clicked in the body
     * of the Fig and wants to drag it around.
     */
    @Override public void hitHandle(Rectangle r, Handle h) {
        h.index = -1;
        h.instructions = "Move Object(s)";
    }

    /**
     * The bounding box of the selection is the bbox of the contained Fig with
     * added space for the handles. For SelectionMove this is larger than normal
     * so that the edges of the selection box don't touch the edges of the
     * contents.
     */
    @Override public Rectangle getBounds() {
        return new Rectangle(getContent().getX() - getBorderWidth(),
            getContent().getY() - getBorderWidth(),
            getContent().getWidth() + getBorderWidth() * 2 + 2,
            getContent().getHeight() + getBorderWidth() * 2 + 2);
    }
} /* end class SelectionMove */

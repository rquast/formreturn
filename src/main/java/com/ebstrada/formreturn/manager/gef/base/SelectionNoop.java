package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Graphics;
import java.awt.Rectangle;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.Handle;

/**
 * Selection object that does not allow the user to do anything. This might be
 * useful for some special Figs. it is not used by the Figs provided by GEF.
 */

public class SelectionNoop extends Selection {

    private static final long serialVersionUID = -8883328105257650811L;

    /**
     * Construct a new SelectionNoop around the given DiagramElement
     */
    public SelectionNoop(Fig f) {
        super(f);
    }

    /**
     * Paint the selection.
     */
    @Override public void paint(Graphics g) {
        int x = getContent().getX();
        int y = getContent().getY();
        int w = getContent().getWidth();
        int h = getContent().getHeight();
        g.setColor(Globals.getPrefs().handleColorFor(getContent()));
        g.drawRect(x - getBorderWidth(), y - getBorderWidth(), w + getBorderWidth() * 2 - 1,
            h + getBorderWidth() * 2 - 1);
        g.drawRect(x - getBorderWidth() - 1, y - getBorderWidth() - 1,
            w + getBorderWidth() * 2 + 2 - 1, h + getBorderWidth() * 2 + 2 - 1);
        g.fillOval(x - getHandSize(), y - getHandSize(), getHandSize(), getHandSize());
        g.fillOval(x + w, y - getHandSize(), getHandSize(), getHandSize());
        g.fillOval(x - getHandSize(), y + h, getHandSize(), getHandSize());
        g.fillOval(x + w, y + h, getHandSize(), getHandSize());
    }

    /**
     * SelectionNoop is used when there are no handles, so dragHandle does
     * nothing. Actually, hitHandle always returns -1 , so this method should
     * never even get called.
     */
    @Override public void dragHandle(int mx, int my, int an_x, int an_y, Handle h) {
        /* do nothing */
    }

    /**
     * Returns -2 as a special code to indicate that the Fig cannot be moved.
     */
    @Override public void hitHandle(Rectangle r, Handle h) {
        h.index = -2;
        h.instructions = "Object cannot be moved or resized";
    }
} /* end class SelectionNoop */

package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigLine;
import com.ebstrada.formreturn.manager.gef.presentation.Handle;

/**
 * A Selection that allows the user to reshape the selected Fig. This is used
 * with FigLine. One handle is drawn over each point on the
 * Fig.
 *
 * @see FigLine
 */

public class SelectionReshape extends Selection implements KeyListener {

    private static final long serialVersionUID = 2204649413528863935L;

    private int selectedHandle = -1;

    /**
     * Construct a new SelectionReshape for the given Fig
     */
    public SelectionReshape(Fig f) {
        super(f);
    }

    /**
     * Return a handle ID for the handle under the mouse, or -1 if none.
     */
    @Override public void hitHandle(Rectangle r, Handle h) {
        Fig fig = getContent();
        int npoints = fig.getNumPoints();
        int[] xs = fig.getXs();
        int[] ys = fig.getYs();
        for (int i = 0; i < npoints; ++i) {
            if (r.contains(xs[i], ys[i])) {
                selectedHandle = i;
                h.index = i;
                h.instructions = "Move point";
                return;
            }
        }

        selectedHandle = -1;
        h.index = -1;
        h.instructions = "Move object(s)";
    }

    /**
     * Paint the handles at the four corners and midway along each edge of the
     * bounding box.
     */
    @Override public void paint(Graphics g) {
        Fig fig = getContent();
        int npoints = fig.getNumPoints();
        int[] xs = fig.getXs();
        int[] ys = fig.getYs();
        g.setColor(Globals.getPrefs().handleColorFor(fig));
        for (int i = 0; i < npoints; ++i) {
            g.fillRect(xs[i] - getHandSize() / 2, ys[i] - getHandSize() / 2, getHandSize(),
                getHandSize());
        }
        if (selectedHandle != -1) {
            g.drawRect(xs[selectedHandle] - getHandSize() / 2 - 2,
                ys[selectedHandle] - getHandSize() / 2 - 2, getHandSize() + 3, getHandSize() + 3);
        }
    }

    /**
     * Change some attribute of the selected Fig when the user drags one of its
     * handles.
     */
    @Override public void dragHandle(int mX, int mY, int anX, int anY, Handle h) {
        final Fig selectedFig = getContent();
        selectedFig.setPoint(h, mX, mY);
    }

    // //////////////////////////////////////////////////////////////
    // event handlers

    @Override public void keyPressed(KeyEvent ke) {
        if (ke.isConsumed()) {
            return;
        }
        if (getContent() instanceof KeyListener) {
            ((KeyListener) getContent()).keyPressed(ke);
        }
    }

    @Override public void keyReleased(KeyEvent ke) {
        if (ke.isConsumed()) {
            return;
        }
        if (getContent() instanceof KeyListener) {
            ((KeyListener) getContent()).keyReleased(ke);
        }
    }
} /* end class SelectionReshape */

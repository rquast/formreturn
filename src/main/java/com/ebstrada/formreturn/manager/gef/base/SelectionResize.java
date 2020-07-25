package com.ebstrada.formreturn.manager.gef.base;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigBarcode;
import com.ebstrada.formreturn.manager.gef.presentation.FigCircle;
import com.ebstrada.formreturn.manager.gef.presentation.FigImage;
import com.ebstrada.formreturn.manager.gef.presentation.FigText;
import com.ebstrada.formreturn.manager.gef.presentation.Handle;

/**
 * A Selection class to represent selections on Figs that present resize
 * handles. The selected Fig can be moved or resized. Figrect, FigRRect,
 * FigCircle, and FigGroup are some of the Figs that normally use this
 * Selection. The selected Fig is told it's new bounding box, and some Figs
 * (like FigGroup or FigPoly) do calculations to scale themselves.
 */

public class SelectionResize extends Selection {

    private static final long serialVersionUID = 1996301098909656022L;

    private int cx;
    private int cy;
    private int cw;
    private int ch;

    private static Log log = LogFactory.getLog(SelectionResize.class);

    // //////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a new SelectionResize for the given Fig
     */
    public SelectionResize(Fig f) {
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
        if (getContent().isResizable()) {

            updateHandleBox();
            Rectangle testRect = new Rectangle(0, 0, 0, 0);
            testRect.setBounds(cx - getHandSize() / 2, cy - getHandSize() / 2, getHandSize(),
                ch + getHandSize() / 2);
            boolean leftEdge = r.intersects(testRect);
            testRect.setBounds(cx + cw - getHandSize() / 2, cy - getHandSize() / 2, getHandSize(),
                ch + getHandSize() / 2);
            boolean rightEdge = r.intersects(testRect);
            testRect
                .setBounds(cx - getHandSize() / 2, cy - getHandSize() / 2, cw + getHandSize() / 2,
                    getHandSize());
            boolean topEdge = r.intersects(testRect);
            testRect.setBounds(cx - getHandSize() / 2, cy + ch - getHandSize() / 2,
                cw + getHandSize() / 2, getHandSize());
            boolean bottomEdge = r.intersects(testRect);
            // needs-more-work: midpoints for side handles
            if (leftEdge && topEdge) {
                h.index = Handle.NORTHWEST;
                h.instructions = "Resize top left";
            } else if (rightEdge && topEdge) {
                h.index = Handle.NORTHEAST;
                h.instructions = "Resize top right";
            } else if (leftEdge && bottomEdge) {
                h.index = Handle.SOUTHWEST;
                h.instructions = "Resize bottom left";
            } else if (rightEdge && bottomEdge) {
                h.index = Handle.SOUTHEAST;
                h.instructions = "Resize bottom right";
            }
            // needs-more-work: side handles
            else {
                h.index = -1;
                h.instructions = "Move object(s)";
            }
        } else {
            h.index = -1;
            h.instructions = "Move object(s)";
        }

    }

    /**
     * Update the private variables cx etc. that represent the rectangle on
     * whose corners handles are to be drawn.
     */
    private void updateHandleBox() {
        final Rectangle cRect = getContent().getHandleBox();
        cx = cRect.x;
        cy = cRect.y;
        cw = cRect.width;
        ch = cRect.height;
    }

    /**
     * Paint the handles at the four corners and midway along each edge of the
     * bounding box.
     */
    @Override public void paint(Graphics g) {
        final Fig fig = getContent();
        if (getContent().isResizable()) {

            updateHandleBox();
            Color OriginalColor = g.getColor();
            g.setColor(Globals.getPrefs().handleColorFor(fig));

            if (fig instanceof FigText || fig instanceof FigBarcode || fig instanceof FigImage) {
                Stroke thindashed =
                    new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f,
                        new float[] {8.0f, 3.0f, 2.0f, 3.0f}, 0.0f);
                ((Graphics2D) g).setStroke(thindashed);
                ((Graphics2D) g).drawRect(fig.getX() - 2, fig.getY() - 2, fig.getWidth() + 4,
                    fig.getHeight() + 4);
                ((Graphics2D) g).setStroke(new BasicStroke());
            }

            g.fillRect(cx - getHandSize() / 2, cy - getHandSize() / 2, getHandSize(),
                getHandSize());
            g.fillRect(cx + cw - getHandSize() / 2, cy - getHandSize() / 2, getHandSize(),
                getHandSize());
            g.fillRect(cx - getHandSize() / 2, cy + ch - getHandSize() / 2, getHandSize(),
                getHandSize());
            g.fillRect(cx + cw - getHandSize() / 2, cy + ch - getHandSize() / 2, getHandSize(),
                getHandSize());

            g.setColor(OriginalColor);

        } else {
            final int x = fig.getX();
            final int y = fig.getY();
            final int w = fig.getWidth();
            final int h = fig.getHeight();
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
    }

    /**
     * Change some attribute of the selected Fig when the user drags one of its
     * handles. Needs-More-Work: someday I might implement resizing that
     * maintains the aspect ratio.
     */
    @Override public void dragHandle(int mX, int mY, int anX, int anY, Handle hand) {
        final Fig fig = getContent();
        if (!fig.isResizable()) {
            if (log.isDebugEnabled()) {
                log.debug("Handle " + hand + " dragged but no action as fig is not resizable");
            }
            return;
        }

        updateHandleBox();

        final int x = cx;
        final int y = cy;
        final int w = cw;
        final int h = ch;
        int newX = x, newY = y, newWidth = w, newHeight = h;
        Dimension minSize = fig.getMinimumSize();
        int minWidth = minSize.width, minHeight = minSize.height;
        switch (hand.index) {
            case -1:
                fig.translate(anX - mX, anY - mY);
                return;
            case Handle.NORTHWEST:
                newWidth = x + w - mX;
                newWidth = (newWidth < minWidth) ? minWidth : newWidth;
                newHeight = y + h - mY;
                newHeight = (newHeight < minHeight) ? minHeight : newHeight;
                newX = x + w - newWidth;
                newY = y + h - newHeight;
                fig.setHandleBox(newX, newY, newWidth, newHeight);
                if ((newX + newWidth) != (x + w)) {
                    newX += (newX + newWidth) - (x + w);
                }
                if ((newY + newHeight) != (y + h)) {
                    newY += (newY + newHeight) - (y + h);
                }
                fig.setHandleBox(newX, newY, newWidth, newHeight);
                return;
            case Handle.NORTH:
                break;
            case Handle.NORTHEAST:
                newWidth = mX - x;
                newWidth = (newWidth < minWidth) ? minWidth : newWidth;
                newHeight = y + h - mY;
                newHeight = (newHeight < minHeight) ? minHeight : newHeight;
                newY = y + h - newHeight;
                fig.setHandleBox(newX, newY, newWidth, newHeight);
                if ((newY + newHeight) != (y + h)) {
                    newY += (newY + newHeight) - (y + h);
                }
                fig.setHandleBox(newX, newY, newWidth, newHeight);
                break;
            case Handle.WEST:
                break;
            case Handle.EAST:
                break;
            case Handle.SOUTHWEST:
                newWidth = x + w - mX;
                newWidth = (newWidth < minWidth) ? minWidth : newWidth;
                newHeight = mY - y;
                newHeight = (newHeight < minHeight) ? minHeight : newHeight;
                newX = x + w - newWidth;
                fig.setHandleBox(newX, newY, newWidth, newHeight);
                if ((newX + newWidth) != (x + w)) {
                    newX += (newX + newWidth) - (x + w);
                }
                fig.setHandleBox(newX, newY, newWidth, newHeight);
                break;
            case Handle.SOUTH:
                break;
            case Handle.SOUTHEAST:
                newWidth = mX - x;
                newWidth = (newWidth < minWidth) ? minWidth : newWidth;
                newHeight = mY - y;
                newHeight = (newHeight < minHeight) ? minHeight : newHeight;
                fig.setHandleBox(newX, newY, newWidth, newHeight);
                break;
            default:
                log.error("invalid handle number for resizing fig");
                break;
        }
    }

} /* end class SelectionResize */

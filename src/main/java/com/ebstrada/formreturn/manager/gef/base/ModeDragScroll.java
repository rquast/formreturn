package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JViewport;

/**
 * A Mode that allows the user to scroll the Editor by clicking and dragging
 * with the middle mouse button.
 *
 * @author Sean Chen, schen@webex.net
 * @see Mode
 * @see Editor
 */
public class ModeDragScroll extends FigModifyingModeImpl implements ActionListener {

    private static final long serialVersionUID = -3744868964626889125L;

    private Dimension _viewportExtent;

    // attributes for autoscrolling...
    private boolean autoscroll = false;
    private javax.swing.Timer autoTimer;
    private int recentX, recentY;
    private final static int AUTOSCROLL_DELAY = 200;
    private static final int SCROLL_INCREMENT = 10;

    private boolean _isScrolling = false;
    private JViewport _viewport = null;
    private Cursor _oldCursor = null;
    private JComponent _component = null;
    private Dimension componentSize = null;
    private Point viewPosition = new Point();
    private int deltaX;
    private int deltaY;
    private int lastX;
    private int lastY;

    private boolean simpleDrag = false;

    // //////////////////////////////////////////////////////////////
    // constructors and related methods

    /**
     * Construct a new ModeDragScroll with the given parent.
     *
     * @param editor The Editor this Mode will drag
     */
    public ModeDragScroll(Editor editor) {
        super(editor);
        autoTimer = new javax.swing.Timer(AUTOSCROLL_DELAY, this);
    }

    /**
     * Construct a new ModeDragScroll instance. Its parent must be set before
     * this instance can be used.
     */
    public ModeDragScroll() {
        this(null);
    }

    /**
     * Always false since this mode can never be exited.
     */
    @Override public boolean canExit() {
        return false;
    }

    /**
     * Instructions for the user.
     */
    @Override public String instructions() {
        return "Drag with mouse to scroll, hold down SHIFT to speed up movement";
    }

    /**
     * Grabs component to begin scrolling. Will turn cursor into a hand.
     *
     * @param me
     */
    @Override public void mousePressed(MouseEvent me) {
        boolean isAltDown = (me.isAltDown() || me.isAltGraphDown());
        boolean isOtherDown = me.isMetaDown() || me.isControlDown(); // SHIFT
        // speeds
        // up
        // movement
        boolean button1 = ((me.getModifiers() & InputEvent.BUTTON1_MASK) != 0);
        boolean button2 = ((me.getModifiers() & InputEvent.BUTTON2_MASK) != 0);

        // Note JDK bug: for middle mouse button isAltDown() always returns
        // true.
        // (JDK 1.4 introduced ALT_DOWN_MASK to fix the bug.)
        boolean buttonCondition =
            (button1 && isAltDown && !isOtherDown) || (button2 && !isOtherDown);

        // if only mouse button1 is pressed, activate the auto scrolling
        simpleDrag = !buttonCondition && button1;

        if (!buttonCondition) {
            // if (LOG.isDebugEnabled()) LOG.debug("MousePressed detected but
            // with wrong button condition for scrolling");
            return;
        }

        // get the component ...
        _component = editor.getJComponent();
        if (_component == null) {
            // if (LOG.isDebugEnabled()) LOG.debug("MousePressed detected but no
            // component to scrolling");
            return;
        }

        // ... and the viewport
        Container parent = _component.getParent();
        if (!(parent instanceof JViewport)) {
            // if (LOG.isDebugEnabled()) LOG.debug("MousePressed detected but no
            // viewport to scrolling");
            return;
        }

        // ok, ready to scroll
        _isScrolling = true;
        me = editor.retranslateMouseEvent(me);
        _viewport = (JViewport) parent;

        viewPosition = _viewport.getViewPosition();
        _viewportExtent = _viewport.getExtentSize();

        componentSize = _component.getSize();
        deltaX = 0;
        deltaY = 0;
        lastX = me.getX();
        lastY = me.getY();

        _oldCursor = _component.getCursor();
        _component.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));

        me.consume();
        editor.translateMouseEvent(me);
        // stop auto timer
        if (simpleDrag) {
            autoTimer.stop();
            autoscroll = false;
            // _simpleDrag = false;
        }
        // if (LOG.isDebugEnabled()) LOG.debug("MousePressed detected scrolling
        // started and event consumed");
    }

    /**
     * If mouse is outside the component, begins autoscrolling or speeds it up.
     * Otherwise will just scroll.
     *
     * @param me
     */
    @Override public void mouseDragged(MouseEvent me) {

        if (simpleDrag) {
            // examine viewport for auto scrolling
            me = editor.retranslateMouseEvent(me);
            int mouseX = me.getX();
            int mouseY = me.getY();
            recentX = mouseX;
            recentY = mouseY;
            // scroll if mouse is outside the component
            JComponent jComponent = editor.getJComponent();
            if (jComponent != null && jComponent.getParent() instanceof JViewport) {
                boolean ok = doScroll(jComponent, mouseX, mouseY);
                if (ok && !autoscroll) {
                    autoscroll = true;
                    autoTimer.start();
                } else if (!ok) {
                    autoscroll = false;
                    autoTimer.stop();
                }
            }

            // if (LOG.isDebugEnabled()) LOG.debug("MouseDragged detected and
            // simple drag took place");
        } else {

            if (!_isScrolling) {
                // if (LOG.isDebugEnabled()) LOG.debug("MouseDragged detected bu
                // not in scrolling mode");
                return;
            }

            me = editor.retranslateMouseEvent(me);
            int x = me.getX();
            int y = me.getY();
            // System.out.println("[MOdeDragScroll] x,y: " + x +"," +y);

            int factor = (me.isShiftDown() ? 4 : 1);

            deltaX = factor * (lastX - x);
            deltaY = factor * (lastY - y);

            deltaX = Math.max(-viewPosition.x, deltaX);
            deltaX =
                Math.min(componentSize.width - (viewPosition.x + _viewportExtent.width), deltaX);

            deltaY = Math.max(-viewPosition.y, deltaY);
            deltaY =
                Math.min(componentSize.height - (viewPosition.y + _viewportExtent.height), deltaY);

            viewPosition.x += deltaX;
            viewPosition.y += deltaY;
            _viewport.setViewPosition(viewPosition);

            if (deltaX != 0) {
                lastX = x + deltaX;
            }
            if (deltaY != 0) {
                lastY = y + deltaY;
            }
            me.consume();
            editor.translateMouseEvent(me);
            // if (LOG.isDebugEnabled()) LOG.debug("MouseDragged detected,
            // viewport moved and event consumed");
        }
    }

    private final boolean doScroll(JComponent jComponent, int mouseX, int mouseY) {
        if (jComponent != null && jComponent.getParent() instanceof JViewport) {
            Dimension componentSize = jComponent.getSize();
            JViewport view = (JViewport) jComponent.getParent();
            Rectangle viewRect = view.getViewRect();
            int viewRight = viewRect.x + viewRect.width;
            int viewY = viewRect.y + viewRect.height;
            // test, if the mouse moves out of the viewport
            // Then auto scrolling is activated but only within the component
            // boundaries

            if (mouseX > viewRight && !(viewRight > (componentSize.width - SCROLL_INCREMENT))) {
                // mouse moves right out of the view -> scroll to right
                view.setViewPosition(new Point(viewRect.x + SCROLL_INCREMENT, viewRect.y));
                return true;
            } else if (mouseX < viewRect.x && !(viewRect.x - SCROLL_INCREMENT < 0)) {
                // mouse moves left out of the viewport -> scroll to left
                view.setViewPosition(new Point(viewRect.x - SCROLL_INCREMENT, viewRect.y));
                return true;
            } else if (mouseY > viewY && !(viewY > (componentSize.height - SCROLL_INCREMENT))) {
                view.setViewPosition(new Point(viewRect.x, viewRect.y + SCROLL_INCREMENT));
                return true;
            } else if (mouseY < viewRect.y && !(viewRect.y - SCROLL_INCREMENT < 0)) {
                view.setViewPosition(new Point(viewRect.x, viewRect.y - SCROLL_INCREMENT));
                return true;
            }
        }
        return false;
    }

    /**
     * Stops scrolling, clears all references
     *
     * @param me
     */
    @Override public void mouseReleased(MouseEvent me) {
        // stop autoscrolling
        if (autoscroll) {
            autoTimer.stop();
            autoscroll = false;
            simpleDrag = false;
        }

        if (!_isScrolling) {
            // if (LOG.isDebugEnabled()) LOG.debug("MouseReleased detected but
            // not in scrolling mode");
            return;
        }
        _isScrolling = false;
        viewPosition = null;
        _component.setCursor(_oldCursor);
        _component = null;
        componentSize = null;
        _viewport = null;
        _oldCursor = null;
        me.consume();
        // if (LOG.isDebugEnabled()) LOG.debug("MouseReleased detected so ending
        // scroll and event consumed");
    }

    /**
     * Interface ActionListener: Simulate mouse dragging
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        MouseEvent me = new MouseEvent(getEditor().getJComponent(), Event.MOUSE_DRAG, 0,
            InputEvent.BUTTON1_MASK, recentX, recentY, 0, false);
        getEditor().mouseDragged(me);
    }

}

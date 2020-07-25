package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Graphics;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.event.EventListenerList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebstrada.formreturn.manager.gef.event.ModeChangeEvent;
import com.ebstrada.formreturn.manager.gef.event.ModeChangeListener;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;

/**
 * ModeManager keeps track of all the Modes for a given Editor. Events are
 * passed to the Modes for handling. The submodes are prioritized according to
 * their order on a stack, i.e., the last Mode added gets the first chance to
 * handle an Event. The Modes must be of type FigModifyingMode, because Editor
 * can only deal with such Modes.
 */

public class ModeManager implements Serializable, MouseListener, MouseMotionListener, KeyListener {

    private static final long serialVersionUID = 3180158274454415153L;

    /**
     * The stack of Mode's that are all active simultainously, the order of
     * Mode's on the stack is their priority, i.e., the topmost Mode gets the
     * first chance to handle an incoming Event. Needs-More-Work: this is a time
     * critical part of the system and should be faster, use an array instead of
     * a Vector.
     */
    private Vector _modes = new Vector();

    /**
     * The Editor that owns this ModeManager.
     */
    public Editor editor;

    protected EventListenerList _listeners = new EventListenerList();

    private static Log LOG = LogFactory.getLog(ModeManager.class);

    // //////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a ModeManager with no modes.
     */
    public ModeManager(Editor ed) {
        editor = ed;
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * Set the parent Editor of this ModeManager
     */
    public void setEditor(Editor w) {
        editor = w;
    }

    /**
     * Get the parent Editor of this ModeManager
     */
    public Editor getEditor() {
        return editor;
    }

    /**
     * Reply the top (first) Mode.
     */
    public FigModifyingMode top() {
        if (_modes.isEmpty()) {
            return null;
        } else {
            return (FigModifyingMode) _modes.lastElement();
        }
    }

    /**
     * Add the given Mode to the stack if another instance of the same class is
     * not already on the stack.
     */
    public void push(FigModifyingMode newMode) {
        if (!includes(newMode.getClass())) {
            _modes.addElement(newMode);
            // fireModeChanged();
        }
    }

    /**
     * Remove the topmost Mode if it can exit.
     */
    public FigModifyingMode pop() {
        if (_modes.isEmpty()) {
            return null;
        }
        FigModifyingMode res = top();
        if (res.canExit()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Removing mode " + res);
            }
            _modes.removeElement(res);
            fireModeChanged();
        }
        return res;
    }

    /**
     * Remove all Modes that can exit.
     */
    public void popAll() {
        while (!_modes.isEmpty() && top().canExit()) {
            _modes.removeElement(top());
        }
    }

    public boolean includes(Class modeClass) {
        Enumeration subs = _modes.elements();
        while (subs.hasMoreElements()) {
            FigModifyingMode m = (FigModifyingMode) subs.nextElement();
            if (m.getClass() == modeClass) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finish all modes immediately.
     */
    public void leaveAll() {
        for (int i = _modes.size() - 1; i >= 0; --i) {
            FigModifyingModeImpl m = ((FigModifyingModeImpl) _modes.get(i));
            m.leave();
        }
    }

    // //////////////////////////////////////////////////////////////
    // event handlers

    /**
     * Pass events to all modes in order, until one consumes it.
     */
    public void keyTyped(KeyEvent ke) {
        checkModeTransitions(ke);
        for (int i = _modes.size() - 1; i >= 0 && !ke.isConsumed(); --i) {
            FigModifyingModeImpl m = ((FigModifyingModeImpl) _modes.get(i));
            m.keyTyped(ke);
        }
    }

    /**
     * Pass events to all modes in order, until one consumes it.
     */
    public void keyReleased(KeyEvent ke) {
        for (int i = _modes.size() - 1; i >= 0 && !ke.isConsumed(); --i) {
            FigModifyingModeImpl m = ((FigModifyingModeImpl) _modes.get(i));
            m.keyReleased(ke);
        }
    }

    /**
     * Pass events to all modes in order, until one consumes it.
     */
    public void keyPressed(KeyEvent ke) {
        // Executing keyPressed of a Mode may in fact remove other modes
        // from the stack. So it is neccessary each time to check that a mode
        // is still on the stack before calling it.
        Vector modes = (Vector) _modes.clone();
        for (int i = modes.size() - 1; i >= 0 && !ke.isConsumed(); --i) {
            FigModifyingModeImpl m = ((FigModifyingModeImpl) modes.get(i));
            if (_modes.contains(m)) {
                m.keyPressed(ke);
            }
        }
    }

    /**
     * Pass events to all modes in order, until one consumes it.
     */
    public void mouseMoved(MouseEvent me) {
        for (int i = _modes.size() - 1; i >= 0; --i) { // && !me.isConsumed()
            FigModifyingModeImpl m = ((FigModifyingModeImpl) _modes.get(i));
            m.mouseMoved(me);
        }
    }

    /**
     * Pass events to all modes in order, until one consumes it.
     */
    public void mouseDragged(MouseEvent me) {
        for (int i = _modes.size() - 1; i >= 0; --i) { // && !me.isConsumed()
            FigModifyingModeImpl m = ((FigModifyingModeImpl) _modes.get(i));
            m.mouseDragged(me);
        }
    }

    /**
     * Pass events to all modes in order, until one consumes it.
     */
    public void mouseClicked(MouseEvent me) {
        checkModeTransitions(me);
        for (int i = _modes.size() - 1; i >= 0 && !me.isConsumed(); --i) {
            FigModifyingModeImpl m = ((FigModifyingModeImpl) _modes.get(i));
            m.mouseClicked(me);
        }
    }

    /**
     * Pass events to all modes in order, until one consumes it.
     */
    public void mousePressed(MouseEvent me) {
        checkModeTransitions(me);
        for (int i = _modes.size() - 1; i >= 0; --i) { // && !me.isConsumed()
            if (LOG.isDebugEnabled()) {
                LOG.debug("MousePressed testing mode " + _modes.get(i).getClass().getName());
            }
            FigModifyingModeImpl m = ((FigModifyingModeImpl) _modes.get(i));
            m.mousePressed(me);
        }
    }

    /**
     * Pass events to all modes in order, until one consumes it.
     */
    public void mouseReleased(MouseEvent me) {
        checkModeTransitions(me);
        for (int i = _modes.size() - 1; i >= 0; --i) { // && !me.isConsumed()
            FigModifyingModeImpl m = ((FigModifyingModeImpl) _modes.get(i));
            m.mouseReleased(me);
        }
        // fireModeChanged();
    }

    /**
     * Pass events to all modes in order, until one consumes it.
     */
    public void mouseEntered(MouseEvent me) {
        for (int i = _modes.size() - 1; i >= 0 && !me.isConsumed(); --i) {
            FigModifyingModeImpl m = ((FigModifyingModeImpl) _modes.get(i));
            m.mouseEntered(me);
        }
    }

    /**
     * Pass events to all modes in order, until one consumes it.
     */
    public void mouseExited(MouseEvent me) {
        for (int i = _modes.size() - 1; i >= 0 && !me.isConsumed(); --i) {
            FigModifyingModeImpl m = ((FigModifyingModeImpl) _modes.get(i));
            m.mouseExited(me);
        }
    }

    // //////////////////////////////////////////////////////////////
    // mode transitions

    /**
     * Check for events that should cause transitions from one Mode to another
     * or otherwise change the ModeManager. Really this should be specified in a
     * subclass of ModeManager, because ModeManager should not make assumptions
     * about the look-and-feel of all future applications. Needs-More-Work: I
     * would like to put the transition from ModeSelect to ModeModify here, but
     * there are too many interactions, so that code is still in ModeSelect.
     */
    public void checkModeTransitions(InputEvent ie) {
        if (!top().canExit() && ie.getID() == MouseEvent.MOUSE_PRESSED) {
            MouseEvent me = (MouseEvent) ie;
            int x = me.getX(), y = me.getY();
            Fig underMouse = editor.hit(x, y);
            if (LOG.isDebugEnabled()) {
                LOG.debug("ModeManager mousepressed detected but not on a port dragable node");
            }
        }
    }

    // //////////////////////////////////////////////////////////////
    // mode events

    public void addModeChangeListener(ModeChangeListener listener) {
        _listeners.add(ModeChangeListener.class, listener);
    }

    public void removeModeChangeListener(ModeChangeListener listener) {
        _listeners.remove(ModeChangeListener.class, listener);
    }

    protected void fireModeChanged() {
        Object[] listeners = _listeners.getListenerList();
        ModeChangeEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ModeChangeListener.class) {
                if (e == null) {
                    e = new ModeChangeEvent(editor, _modes);
                }
                // needs-more-work: should copy vector, use JGraph as src?
                ((ModeChangeListener) listeners[i + 1]).modeChange(e);
            }
        }
    }

    // //////////////////////////////////////////////////////////////
    // painting methods

    /**
     * Paint each mode in the stack: bottom to top.
     */
    public void paint(Graphics g) {
        Enumeration modes = _modes.elements();
        while (modes.hasMoreElements()) {
            FigModifyingMode m = (FigModifyingMode) modes.nextElement();
            m.paint(g);
        }
    }

}

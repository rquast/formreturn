package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;
import java.util.Hashtable;

/**
 * This is the default implementation for the basic interface of all modes. It
 * provides basic functionality for initializing the mode and handling its
 * parameters. All of the methods can be overwritten, but this is not always
 * necessary.
 *
 * @see Mode
 * @see FigModifyingModeImpl
 */

public class ModeImpl
    implements Mode, Serializable, KeyListener, MouseListener, MouseMotionListener {

    private static final long serialVersionUID = -3707221186028816573L;

    /**
     * Arguments to this mode. These are usually set just after the mode is
     * created via the init method and used later.
     */
    protected Hashtable _args = new Hashtable();

    // //////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a new Mode instance with the given parameters as its initial
     * parameters
     */
    public ModeImpl(Hashtable parameters) {
        setArgs(parameters);
    }

    /**
     * Construct a new ModeImpl instance without any parameters. This
     * constructor is needed because some Cmd-Classes can only call
     * Class.newInstance which does not pass constructor arguments.
     *
     * @see CmdSetMode
     */
    public ModeImpl() {
    }

    // //////////////////////////////////////////////////////////////
    // Arguments

    public void setArgs(Hashtable args) {
        _args = args;
    }

    public void setArg(String key, Object value) {
        if (_args == null) {
            _args = new Hashtable();
        }
        _args.put(key, value);
    }

    public Hashtable getArgs() {
        return _args;
    }

    public Object getArg(String s) {
        if (_args == null) {
            return null;
        }
        return _args.get(s);
    }

    // //////////////////////////////////////////////////////////////
    // methods related to transitions among modes

    /**
     * When a Mode handles a certain event that indicates that the user wants to
     * exit that Mode (e.g., a mouse up event after a drag in ModeCreateEdge)
     * the Mode calls done to make switching to another Mode possible.
     */
    public void done() {
    }

    /**
     * When the user performs the first AWT Event that indicate that they want
     * to do some work in this mode, then change the global next mode.
     */
    public void start() {
        Globals.nextMode();
    }

    /**
     * Some Mode's should never be exited, but by default any Mode can exit.
     * Mode's which return false for canExit() will not be popped from a
     * ModeManager.
     *
     * @see ModeManager
     */
    public boolean canExit() {
        return true;
    }

    /**
     * Modes may need some parameters in order to work properly. With this
     * method, a Mode can be inititalized with a unspecified number of
     * parameters. Call this method first, before using a Mode.
     */
    public void init(Hashtable parameters) {
        setArgs(parameters);
    }

    /**
     * Modes can be finished before completed for some reasons. This method lets
     * the mode be finished from any state it is in.
     */
    public void leave() {
        Globals.setSticky(false);
        done();
        Globals.nextMode();
        Editor editor = Globals.curEditor();
        if (editor != null) {
            editor.finishMode();
        }
    }

    // //////////////////////////////////////////////////////////////
    // event handlers

    public void keyPressed(KeyEvent ke) {
    }

    public void keyReleased(KeyEvent ke) {
    }

    public void keyTyped(KeyEvent ke) {
    }

    public void mouseMoved(MouseEvent me) {
    }

    public void mouseDragged(MouseEvent me) {
    }

    public void mouseClicked(MouseEvent me) {
    }

    public void mousePressed(MouseEvent me) {
    }

    public void mouseReleased(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
    }

    public void mouseEntered(MouseEvent me) {
    }

} /* end class Mode */

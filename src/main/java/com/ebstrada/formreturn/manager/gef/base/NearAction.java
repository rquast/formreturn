package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * Action to SelectNear Figs by a small distance. This is useful when you want
 * to get diagrams to look just right and you are not to steady with the mouse.
 * Also allows user to keep hands on keyboard.
 */
public class NearAction extends UndoableAction {

    private static final long serialVersionUID = -4295539588596245495L;

    // //////////////////////////////////////////////////////////////
    // constants
    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int UP = 3;
    public static final int DOWN = 4;

    // //////////////////////////////////////////////////////////////
    // instance variables
    private int direction;
    private int magnitude;

    // //////////////////////////////////////////////////////////////
    // constructor

    /**
     * Creates a new NearAction
     *
     * @param name      The name of the action
     * @param direction The direction of the nudge
     */
    public NearAction(String name, int direction) {
        this(name, direction, 1, false);
    }

    /**
     * Creates a new NearAction
     *
     * @param name      The name of the action
     * @param direction The direction of the nudge
     * @param magnitude The magnitude of the nudge
     */
    public NearAction(String name, int direction, int magnitude) {
        this(name, direction, magnitude, false);
    }

    /**
     * Creates a new NearAction
     *
     * @param name      The name of the action
     * @param icon      The icon of the action
     * @param direction The direction of the nudge
     */
    public NearAction(String name, Icon icon, int direction) {
        this(name, icon, direction, 1, false);
    }

    /**
     * Creates a new NearAction
     *
     * @param name      The name of the action
     * @param icon      The icon of the action
     * @param direction The direction of the nudge
     * @param magnitude The magnitude of the nudge
     */
    public NearAction(String name, Icon icon, int direction, int magnitude) {
        this(name, icon, direction, magnitude, false);
    }

    /**
     * Creates a new NearAction
     *
     * @param name      The name of the action
     * @param direction The direction of the nudge
     * @param localize  Whether to localize the name or not
     */
    public NearAction(String name, int direction, boolean localize) {
        this(name, direction, 1, localize);
    }

    /**
     * Creates a new NearAction
     *
     * @param name      The name of the action
     * @param direction The direction of the nudge
     * @param magnitude The magnitude of the nudge
     * @param localize  Whether to localize the name or not
     */
    public NearAction(String name, int direction, int magnitude, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
        this.direction = direction;
        this.magnitude = magnitude;
    }

    /**
     * Creates a new NearAction
     *
     * @param name      The name of the action
     * @param icon      The icon of the action
     * @param direction The direction of the nudge
     * @param localize  Whether to localize the name or not
     */
    public NearAction(String name, Icon icon, int direction, boolean localize) {
        this(name, icon, direction, 1, localize);
    }

    /**
     * Creates a new NearAction
     *
     * @param name      The name of the action
     * @param icon      The icon of the action
     * @param direction The direction of the nudge
     * @param magnitude The magnitude of the nudge
     * @param localize  Whether to localize the name or not
     */
    public NearAction(String name, Icon icon, int direction, int magnitude, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
        this.direction = direction;
        this.magnitude = magnitude;
    }

    // //////////////////////////////////////////////////////////////
    // Action API

    /**
     * Move the selected items a few pixels in the given direction. Note that
     * the sign convention is the opposite of ScrollAction.
     */
    @Override public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Editor ce = Globals.curEditor();
        SelectionManager sm = ce.getSelectionManager();
        if (sm.getLocked()) {
            Globals.showStatus("Cannot Modify Locked Objects");
            return;
        }

        int dx = 0, dy = 0;
        switch (direction) {
            case LEFT:
                dx = 0 - magnitude;
                break;
            case RIGHT:
                dx = magnitude;
                break;
            case UP:
                dy = 0 - magnitude;
                break;
            case DOWN:
                dy = magnitude;
                break;
        }
        // Should I move it so that it aligns with the next grid?
        sm.translate(dx, dy);
        sm.endTrans();
    }
}

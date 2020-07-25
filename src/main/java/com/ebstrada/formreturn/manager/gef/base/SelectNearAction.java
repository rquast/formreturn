package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class SelectNearAction extends UndoableAction {

    private static final long serialVersionUID = -7302592306721995290L;

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

    /**
     * Creates a new SelectNearAction
     *
     * @param name The name of the action
     * @param dir  The direction of the selection
     */
    public SelectNearAction(int dir) {
        this(dir, 1);
    }

    /**
     * Creates a new SelectNearAction
     *
     * @param name The name of the action
     * @param dir  The direction of the selection
     */
    public SelectNearAction(int dir, int mag) {
        super("SelectNear" + wordFor(dir)); // needs-more-work: direction
        direction = dir;
        magnitude = mag;
    }

    private static String wordFor(int d) {
        switch (d) {
            case LEFT:
                return "Left";
            case RIGHT:
                return "Right";
            case UP:
                return "Up";
            case DOWN:
                return "Down";
        }
        return "";
    }

    /**
     * Creates a new SelectNearAction
     *
     * @param name The name of the action
     * @param dir  The direction of the selection
     */
    public SelectNearAction(String name, int dir) {
        this(name, dir, 1, false);
    }

    /**
     * Creates a new SelectNearAction
     *
     * @param name The name of the action
     * @param dir  The direction of the selection
     * @param mag  The magnitude of the selection
     */
    public SelectNearAction(String name, int dir, int mag) {
        this(name, dir, mag, false);
    }

    /**
     * Creates a new SelectNearAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     * @param dir  The direction of the selection
     */
    public SelectNearAction(String name, Icon icon, int dir) {
        this(name, icon, dir, 1, false);
    }

    /**
     * Creates a new SelectNearAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     * @param dir  The direction of the selection
     * @param mag  The magnitude of the selection
     */
    public SelectNearAction(String name, Icon icon, int dir, int mag) {
        this(name, icon, dir, mag, false);
    }

    /**
     * Creates a new SelectNearAction
     *
     * @param name     The name of the action
     * @param dir      The direction of the selection
     * @param localize Whether to localize the name or not
     */
    public SelectNearAction(String name, int dir, boolean localize) {
        this(name, dir, 1, false);
    }

    /**
     * Creates a new SelectNearAction
     *
     * @param name     The name of the action
     * @param dir      The direction of the selection
     * @param mag      The magnitude of the selection
     * @param localize Whether to localize the name or not
     */
    public SelectNearAction(String name, int dir, int mag, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
        this.direction = dir;
        this.magnitude = mag;
    }

    /**
     * Creates a new SelectNearAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param dir      The direction of the selection
     * @param localize Whether to localize the name or not
     */
    public SelectNearAction(String name, Icon icon, int dir, boolean localize) {
        this(name, icon, dir, 1, false);
    }

    /**
     * Creates a new SelectNearAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param dir      The direction of the selection
     * @param mag      The magnitude of the selection
     * @param localize Whether to localize the name or not
     */
    public SelectNearAction(String name, Icon icon, int dir, int mag, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
        this.direction = dir;
        this.magnitude = mag;
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

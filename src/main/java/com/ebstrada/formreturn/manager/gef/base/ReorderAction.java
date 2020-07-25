package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * Cmd to change the back-to-front ordering of Fig's.
 *
 * @see LayerDiagram#reorder
 */
public class ReorderAction extends UndoableAction {
    private static final long serialVersionUID = -2190865994915716779L;

    // //////////////////////////////////////////////////////////////
    // constants
    public static final int SEND_TO_BACK = 1;
    public static final int BRING_TO_FRONT = 2;
    public static final int SEND_BACKWARD = 3;
    public static final int BRING_FORWARD = 4;

    // //////////////////////////////////////////////////////////////
    // instance variables
    private int function;

    // //////////////////////////////////////////////////////////////
    // constructor

    /**
     * Construct a new ReorderAction with the given reordering constraint (see
     * above)
     *
     * @param name     The name of the action
     * @param function The function of the reorder
     */
    public ReorderAction(String name, int function) {
        this(name, function, false);
    }

    /**
     * Construct a new ReorderAction with the given reordering constraint (see
     * above)
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param function The function of the reorder
     */
    public ReorderAction(String name, Icon icon, int function) {
        this(name, icon, function, false);
    }

    /**
     * Construct a new ReorderAction with the given reordering constraint (see
     * above)
     *
     * @param name     The name of the action
     * @param function The function of the reorder
     * @param localize Whether to localize the name or not
     */
    public ReorderAction(String name, int function, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
        this.function = function;
    }

    /**
     * Construct a new ReorderAction with the given reordering constraint (see
     * above)
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param function The function of the reorder
     * @param localize Whether to localize the name or not
     */
    public ReorderAction(String name, Icon icon, int function, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
        this.function = function;
    }

    // //////////////////////////////////////////////////////////////
    // Action API

    @Override public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Editor ce = Globals.curEditor();
        LayerManager lm = ce.getLayerManager();
        SelectionManager sm = ce.getSelectionManager();
        sm.reorder(function, lm.getActiveLayer());
        sm.endTrans();
        // ce.repairDamage();
    }
}

package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;

import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class ShowGridAction extends UndoableAction {

    private static final long serialVersionUID = 1L;

    public ShowGridAction() {
        super();
    }

    /**
     * Creates a new AdjustGridAction
     *
     * @param name The name of the action
     */
    public ShowGridAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new AdjustGridAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public ShowGridAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new AdjustGridAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public ShowGridAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new AdjustGridAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public ShowGridAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    @Override public void actionPerformed(ActionEvent event) {
        super.actionPerformed(event);
        Editor ce = Globals.curEditor();
        if (event.getSource() instanceof JCheckBoxMenuItem) {
            JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem) event.getSource();
            if (menuItem.isSelected()) {
                ce.setGridHidden(false);
            } else {
                ce.setGridHidden(true);
            }
        }
        ce.damageAll();
    }

}

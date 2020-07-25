package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigGroup;
import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * Action to group all the Fig's selected in the current editor into a single
 * FigGroup.
 *
 * @see FigGroup
 * @see GroupAction
 */
public class GroupAction extends UndoableAction {

    private static final long serialVersionUID = -648389660425356608L;

    /**
     * Creates a new GroupAction
     */
    public GroupAction() {
        super();
    }

    /**
     * Creates a new GroupAction
     *
     * @param name The name of the action
     */
    public GroupAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new GroupAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public GroupAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new GroupAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public GroupAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new GroupAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public GroupAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    @Override public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Editor ce = Globals.curEditor();
        Vector selectedFigs = ce.getSelectionManager().getFigs();
        FigGroup _newItem = new FigGroup();
        Enumeration eachDE = selectedFigs.elements();
        while (eachDE.hasMoreElements()) {
            Object o = eachDE.nextElement();
            if (o instanceof Fig) {
                Fig f = (Fig) o;
                _newItem.addFig(f);
            }
        }
        eachDE = selectedFigs.elements();
        while (eachDE.hasMoreElements()) {
            Object o = eachDE.nextElement();
            if (o instanceof Fig) {
                Fig f = (Fig) o;
                ce.remove(f);
            }
        }
        ce.add(_newItem);
        ce.getSelectionManager().deselectAll();
        ce.getSelectionManager().select(_newItem);
    }
}

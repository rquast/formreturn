package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * Action to delete Figs from view. This does not do anything to any underlying
 * Net or other model, it is strictly a manipulation of graphical objects.
 * Normally DeleteFromModelAction is the command users will want to execute.
 *
 * @see DeleteFromModelAction
 * @see Editor
 * @see LayerDiagram
 */
public class RemoveFromGraphAction extends UndoableAction {

    private static final long serialVersionUID = -4537382217248274321L;

    /**
     * Creates a new RemoveFromGraphAction
     *
     * @param name The name of the action
     */
    public RemoveFromGraphAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new RemoveFromGraphAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public RemoveFromGraphAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new RemoveFromGraphAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public RemoveFromGraphAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new RemoveFromGraphAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public RemoveFromGraphAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    /**
     * Tell the selected Figs to remove themselves from the the diagram it is in
     * (and thus all editors).
     */
    @Override public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UndoManager.getInstance().startChain();
                Editor ce = Globals.curEditor();
                SelectionManager sm = ce.getSelectionManager();
                sm.removeFromGraph();
            }
        });
    }

}

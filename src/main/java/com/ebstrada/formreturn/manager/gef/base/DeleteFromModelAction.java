package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * Action to delete the Figs selected in the current editor, and dispose any
 * underlying Net stuctures. This will also remove from all other views.
 */
public class DeleteFromModelAction extends UndoableAction {

    private static final long serialVersionUID = -6433353066464208028L;

    private static Log LOG = LogFactory.getLog(DeleteFromModelAction.class);

    /**
     * Creates a new DeleteFromModelAction
     *
     * @param name The name of the action
     */
    public DeleteFromModelAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new DeleteFromModelAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public DeleteFromModelAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new DeleteFromModelAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public DeleteFromModelAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new DeleteFromModelAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public DeleteFromModelAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    @Override public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        LOG.debug("Executing DeleteFromModelAction");
        Editor ce = Globals.curEditor();
        if (ce != null) {
            SelectionManager sm = ce.getSelectionManager();
            if (sm != null) {
                sm.deleteFromModel();
                sm.deselectAll();
            }
        }
    }
}

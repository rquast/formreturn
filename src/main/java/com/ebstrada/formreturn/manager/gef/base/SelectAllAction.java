package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * Action to select all the Figs in the editor's current view.
 */
public class SelectAllAction extends AbstractAction {

    private static final long serialVersionUID = 318997315984793176L;

    /**
     * Creates a new SelectAllAction
     */
    public SelectAllAction() {
        super();
    }

    /**
     * Creates a new SelectAllAction
     *
     * @param name The name of the action
     */
    public SelectAllAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new SelectAllAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public SelectAllAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new SelectAllAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public SelectAllAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new SelectAllAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public SelectAllAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    public void actionPerformed(ActionEvent e) {
        Editor ce = Globals.curEditor();
        Collection diagramContents = ce.getLayerManager().getContents();
        ce.getSelectionManager().select(diagramContents);
    }

}

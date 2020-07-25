package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * Cmd to select all the Figs in the editor's current view that were not
 * previously selected.
 */
public class SelectInvertAction extends AbstractAction {

    private static final long serialVersionUID = -2819300880348344879L;

    /**
     * Creates a new SelectInvertAction
     */
    public SelectInvertAction() {
        super();
    }

    /**
     * Creates a new SelectInvertAction
     *
     * @param name The name of the action
     */
    public SelectInvertAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new SelectInvertAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public SelectInvertAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new SelectInvertAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public SelectInvertAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new SelectInvertAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public SelectInvertAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    public void actionPerformed(ActionEvent e) {
        Editor ce = Globals.curEditor();
        List selected = ce.getSelectionManager().getFigs();
        List diagramContents = ce.getLayerManager().getContents();
        List inverse = new ArrayList(diagramContents.size());

        Iterator it = diagramContents.iterator();
        while (it.hasNext()) {
            Object dc = it.next();
            if (!selected.contains(dc)) {
                inverse.add(dc);
            }
        }
        ce.getSelectionManager().select(inverse);
    }
}

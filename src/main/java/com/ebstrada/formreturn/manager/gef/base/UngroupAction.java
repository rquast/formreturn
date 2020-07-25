package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigGroup;
import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * Action to ungroup a selected group object.
 *
 * @see GroupAction
 * @see FigGroup
 */
public class UngroupAction extends UndoableAction {

    private static final long serialVersionUID = -5373541224263019450L;

    /**
     * Creates a new UngroupAction
     */
    public UngroupAction() {
        super();
    }

    /**
     * Creates a new UngroupAction
     *
     * @param name The name of the action
     */
    public UngroupAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new UngroupAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public UngroupAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new UngroupAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public UngroupAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new UngroupAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public UngroupAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    @Override public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Vector ungroupedItems = new Vector();
        Editor currentEditor = Globals.curEditor();
        Vector selectedFigs = currentEditor.getSelectionManager().getFigs();
        Enumeration eachDE = selectedFigs.elements();
        while (eachDE.hasMoreElements()) {
            Object o = eachDE.nextElement();
            if (o instanceof FigGroup) {
                FigGroup fg = (FigGroup) o;
                Iterator it = fg.getFigs().iterator();
                while (it.hasNext()) {
                    Fig f = (Fig) it.next();
                    currentEditor.add(f);
                    ungroupedItems.addElement(f);
                }
                currentEditor.remove(fg);
            }
        } /* end while each selected object */
        currentEditor.getSelectionManager().deselectAll();
        currentEditor.getSelectionManager().select(ungroupedItems);
    }
}

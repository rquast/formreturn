package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class CutAction extends UndoableAction {

    private static final long serialVersionUID = -5008218014474881077L;

    /**
     * Creates a new CutAction
     *
     * @param name The name of the action
     */
    public CutAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new CutAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public CutAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new CutAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public CutAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new CutAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public CutAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    @Override public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Editor ce = Globals.curEditor();
                SelectionManager selectionManager = ce.getSelectionManager();
                Vector copiedElements = selectionManager.selections();
                Vector figs = new Vector();
                Enumeration copies = copiedElements.elements();
                while (copies.hasMoreElements()) {
                    Selection s = (Selection) copies.nextElement();
                    Fig f = s.getContent();
                    f = (Fig) f.clone();
                    figs.addElement(f);
                }
                Globals.clipBoard = figs;
                selectionManager.removeFromGraph();
            }
        });
    }
}

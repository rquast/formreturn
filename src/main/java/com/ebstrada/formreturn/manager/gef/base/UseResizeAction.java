package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * Set the current editor to use a SelectionResize on its current selections.
 *
 * @see Editor
 * @see Selection
 * @see SelectionResize
 */
public class UseResizeAction extends UndoableAction {

    private static final long serialVersionUID = 5338653001956870781L;

    public UseResizeAction() {
        super();
    }

    /**
     * Creates a new UseResizeAction
     *
     * @param name The name of the action
     */
    public UseResizeAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new UseResizeAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public UseResizeAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new UseResizeAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public UseResizeAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new UseResizeAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public UseResizeAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    @Override public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Editor ce = Globals.curEditor();
        SelectionManager sm = ce.getSelectionManager();
        Enumeration sels = ((Vector) sm.selections().clone()).elements();
        while (sels.hasMoreElements()) {
            Selection s = (Selection) sels.nextElement();
            if (s instanceof Selection && !(s instanceof SelectionResize)) {
                Fig f = s.getContent();
                if (f.isReshapable()) {
                    ce.damaged(s);
                    sm.removeSelection(s);
                    SelectionResize sr = new SelectionResize(f);
                    sm.addSelection(sr);
                    ce.damaged(sr);
                }
            }
        }
    }

}

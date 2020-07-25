package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * An Action to modify the way that the PageBreaks Layer of the current document
 * looks. For now it just cycles among a few predefined looks. Needs-More-Work:
 * Should put up a PageBreaks preference dialog box or use the property sheet.
 */
public class AdjustPageBreaksAction extends UndoableAction {

    private static final long serialVersionUID = -159480249557144602L;

    public AdjustPageBreaksAction() {
        super();
    }

    /**
     * Creates a new AdjustPageBreaksAction
     *
     * @param name The name of the action
     */
    public AdjustPageBreaksAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new AdjustPageBreaksAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public AdjustPageBreaksAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new AdjustPageBreaksAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public AdjustPageBreaksAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new AdjustPageBreaksAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public AdjustPageBreaksAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    @Override public void actionPerformed(ActionEvent event) {
        super.actionPerformed(event);
        Editor ce = Globals.curEditor();
        Layer pageBreaks = ce.getLayerManager().findLayerNamed("PageBreaks");
        if (pageBreaks != null) {
            pageBreaks.adjust();
        }
    }
}

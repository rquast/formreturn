package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * An Action to modify the way that the Guides constrain the mouse points
 * entered by the user. This does not change the apperance of the LayerGrid.
 * Needs-More-Work: Should put up a grid preference dialog box or use the
 * property sheet.
 */
public class AdjustGuideAction extends AbstractAction {

    private static final long serialVersionUID = -7779055134388973203L;

    public AdjustGuideAction() {
        super();
    }

    /**
     * Creates a new AdjustGuideAction
     *
     * @param name The name of the action
     */
    public AdjustGuideAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new AdjustGuideAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public AdjustGuideAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new AdjustGuideAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public AdjustGuideAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new AdjustGuideAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public AdjustGuideAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    public void actionPerformed(ActionEvent event) {
        Editor ce = Globals.curEditor();
        Guide guide = ce.getGuide();
        if (guide != null) {
            guide.adjust();
        }
    }
}

package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;

import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * An Action to modify the way that the grid layer of the current document
 * looks. For now it just cycles among a few predefined looks. Needs-More-Work:
 * should put up a grid preference dialog box or use property sheet.
 */
public class AdjustGridAction extends UndoableAction {

    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3761403097309984825L;

    public AdjustGridAction() {
        super();
    }

    /**
     * Creates a new AdjustGridAction
     *
     * @param name The name of the action
     */
    public AdjustGridAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new AdjustGridAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public AdjustGridAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new AdjustGridAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public AdjustGridAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new AdjustGridAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public AdjustGridAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    @Override public void actionPerformed(ActionEvent event) {
        super.actionPerformed(event);
        Editor ce = Globals.curEditor();
        Layer grid = ce.getLayerManager().findLayerNamed("Grid");
        if (grid != null) {
            grid.adjust();
            Guide guide = ce.getGuide();
            if (guide != null && guide instanceof GuideGrid) {
                if (grid instanceof LayerGrid) {
                    ((GuideGrid) guide).gridSize(((LayerGrid) grid).getSpacing());
                }
            }
        }
    }

}

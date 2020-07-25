package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * Action to select the next (or previous) Fig in the editor's current view.
 * This is very convienent for moving among lots of small Figs. It also provides
 * a simple example of an Action that is bound to a key.
 *
 * @see com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph#initKeys()
 */
public class SelectNextAction extends AbstractAction {

    private static final long serialVersionUID = 1725046263612956528L;

    // //////////////////////////////////////////////////////////////
    // variables
    private String direction;

    // //////////////////////////////////////////////////////////////
    // constants

    public static final String DIR = "Direction";

    public static final String DIR_NEXT = "Next";

    public static final String DIR_PREV = "Previous";

    // //////////////////////////////////////////////////////////////
    // constructors

    /**
     * Creates a new SelectNextAction (direction is next, by default)
     */
    public SelectNextAction() {
        this(null, true);
    }

    /**
     * Creates a new SelectNextAction (direction is next, by default)
     *
     * @param name The name of the action
     */
    public SelectNextAction(String name) {
        this(name, true);
    }

    /**
     * Creates a new SelectNextAction
     *
     * @param name The name of the action
     * @param next Whether the direction is next or previous
     */
    public SelectNextAction(String name, boolean next) {
        this(name, next ? DIR_NEXT : DIR_PREV, false);
    }

    /**
     * Creates a new SelectNextAction
     *
     * @param name      The name of the action
     * @param direction The direction of the selection
     */
    public SelectNextAction(String name, String direction) {
        this(name, direction, false);
    }

    /**
     * Creates a new SelectNextAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public SelectNextAction(String name, Icon icon) {
        this(name, icon, DIR_NEXT);
    }

    /**
     * Creates a new SelectNextAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     * @param next Whether the direction is next or previous
     */
    public SelectNextAction(String name, Icon icon, boolean next) {
        this(name, icon, next ? DIR_NEXT : DIR_PREV, false);
    }

    /**
     * Creates a new SelectNextAction
     *
     * @param name      The name of the action
     * @param icon      The icon of the action
     * @param direction The direction of the selection
     */
    public SelectNextAction(String name, Icon icon, String direction) {
        this(name, icon, direction, false);
    }

    /**
     * Creates a new SelectNextAction
     *
     * @param name      The name of the action
     * @param direction The direction of the selection
     * @param localize  Whether to localize the name or not
     */
    public SelectNextAction(String name, String direction, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new SelectNextAction
     *
     * @param name      The name of the action
     * @param icon      The icon of the action
     * @param direction The direction of the selection
     * @param localize  Whether to localize the name or not
     */
    public SelectNextAction(String name, Icon icon, String direction, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    // //////////////////////////////////////////////////////////////
    // Action API

    public void actionPerformed(ActionEvent e) {

        System.out.println("select next action performed");

        Selection curSel;
        Fig newFig = null;
        int offset = 1;
        // String dir = (String)getArg(DIR);
        // if(DIR_PREV.equals(dir)) {
        if (DIR_PREV.equals(direction)) {
            offset = -1;
        }
        Editor ce = Globals.curEditor();
        SelectionManager sm = ce.getSelectionManager();
        List diagramContents = ce.getLayerManager().getContents();
        int diagramSize = diagramContents.size();
        int newIndex = diagramSize + 1;

        if (sm.size() == 0) {
            newIndex = 0;
        } else if (sm.size() == 1) {
            Fig curFig;
            curSel = (Selection) sm.selections().firstElement();
            curFig = curSel.getContent();
            int curIndex = diagramContents.indexOf(curFig);
            newIndex = (curIndex + offset + diagramSize) % diagramSize;
        }
        if (diagramSize > newIndex) {
            newFig = (Fig) diagramContents.get(newIndex);
        }
        if (newFig != null) {
            ce.getSelectionManager().select(newFig);
        }
    }

}

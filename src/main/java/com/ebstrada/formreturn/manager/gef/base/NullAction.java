package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * Action to do nothing. This might make some other code simpler. For example,
 * keybinding query funcitons can return a "real" Action if there is one bound
 * to the given key, and an instance of ActionNull if there is not. The
 * alternative would be to return null and force the caller to check for null.
 */
public class NullAction extends AbstractAction {

    private static final long serialVersionUID = -1522361361333044248L;

    /**
     * Creates a new NullAction
     */
    public NullAction() {
        super();
    }

    /**
     * Creates a new NullAction
     *
     * @param name The name of the action
     */
    public NullAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new NullAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public NullAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new NullAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public NullAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    /**
     * Creates a new NullAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public NullAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Do nothing
     */
    public void actionPerformed(ActionEvent e) {
    }

}

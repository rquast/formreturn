package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * Zoom the view.
 */

public class ZoomAction extends AbstractAction {

    private static final long serialVersionUID = -952853421722122499L;
    private double _magnitude;

    // //////////////////////////////////////////////////////////////
    // constructor

    /**
     * Default behaviour is to restore scaling to 1.0 (1 to 1)
     */
    public ZoomAction() {
        this(0);
    }

    /**
     * Each time <code>doIt()</code> is invoked, adjust scaling by a factor of
     * <code>magnitude</code>.
     *
     * @param magnitude the factor by which to adjust the Editor's scaling. Must
     *                  be greater than or equal to zero. If zero, resets the
     *                  Editor's scale factor to 1.
     */
    public ZoomAction(double magnitude) {
        super(wordFor(magnitude));
        _magnitude = magnitude;
    }

    /**
     * Convert the zoom magnitude to an English description.
     */
    protected static String wordFor(double magnitude) {
        if (magnitude < 0) {
            throw new IllegalArgumentException("Zoom magnitude cannot be less than 0");
        } else if (magnitude == 0.0) {
            return Localizer.localize("GefBase", "ZoomReset");
        } else if (magnitude > 1.0) {
            return Localizer.localize("GefBase", "ZoomIn");
        } else if (magnitude < 1.0) {
            return Localizer.localize("GefBase", "ZoomOut");
        } else {
            return Localizer.localize("GefBase", "DoNothing"); // Not a very
            // useful option
        }
    }

    public void actionPerformed(ActionEvent e) {
        Editor ed = Globals.curEditor();
        if (ed == null) {
            return;
        }

        if (_magnitude > 0.0) {
            ed.setScale(_magnitude);
        } else {
            ed.setScale(1.0);
        }
        ed.damageAll();
    }
} /* end class CmdZoom */

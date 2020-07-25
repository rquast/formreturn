package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.MouseEvent;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigCircle;

/**
 * A Mode to interpert user input while creating a FigCircle. All of the actual
 * event handling is inherited from ModeCreate. This class just implements the
 * differences needed to make it specific to circles.
 */

public class ModeCreateFigCircle extends ModeCreate {

    private static final long serialVersionUID = 2100068733524473429L;

    @Override public String instructions() {
        return "Drag to define a circle";
    }

    // //////////////////////////////////////////////////////////////
    // ModeCreate API

    /**
     * Create a new FigCircle instance based on the given mouse down event and
     * the state of the parent Editor.
     */
    @Override public Fig createNewItem(MouseEvent me, int snapX, int snapY) {
        return new FigCircle(snapX, snapY, 0, 0);
    }
} /* end class ModeCreateFigCircle */

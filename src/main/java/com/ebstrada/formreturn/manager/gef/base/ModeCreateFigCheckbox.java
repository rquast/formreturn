package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.MouseEvent;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigCheckbox;

/**
 * A Mode to interpert user input while creating a FigCheckbox. All of the
 * actual event handling is inherited from ModeCreate. This class just
 * implements the differences needed to make it specific to rectangles.
 */

public class ModeCreateFigCheckbox extends ModeCreate {
    // //////////////////////////////////////////////////////////////
    // Mode API

    /**
     *
     */
    private static final long serialVersionUID = 438243694048603188L;

    @Override public String instructions() {
        return "Drag to define a rectangle";
    }

    // //////////////////////////////////////////////////////////////
    // ModeCreate API

    /**
     * Create a new FigCheckbox instance based on the given mouse down event and
     * the state of the parent Editor.
     */
    @Override public Fig createNewItem(MouseEvent me, int snapX, int snapY) {
        return new FigCheckbox(snapX, snapY, 0, 0);
    }
} /* end class ModeCreateFigCheckbox */

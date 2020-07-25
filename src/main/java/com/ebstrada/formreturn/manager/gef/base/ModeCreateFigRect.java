package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.MouseEvent;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigRect;

/**
 * A Mode to interpert user input while creating a FigRect. All of the actual
 * event handling is inherited from ModeCreate. This class just implements the
 * differences needed to make it specific to rectangles.
 */

public class ModeCreateFigRect extends ModeCreate {
    private static final long serialVersionUID = 2881047174758002300L;

    @Override public String instructions() {
        return "Drag to define a rectangle";
    }

    /**
     * Create a new FigRect instance based on the given mouse down event and the
     * state of the parent Editor.
     */
    @Override public Fig createNewItem(MouseEvent me, int snapX, int snapY) {
        return new FigRect(snapX, snapY, 0, 0);
    }
} /* end class ModeCreateFigRect */

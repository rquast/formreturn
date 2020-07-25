package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.MouseEvent;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigRRect;

/**
 * A Mode to interpert user input while creating a FigRRect. All of the actual
 * event handling is inherited from ModeCreate. This class just implements the
 * differences needed to make it specific to RRects.
 */

public class ModeCreateFigRRect extends ModeCreate {

    private static final long serialVersionUID = 7375344602565163799L;

    @Override public String instructions() {
        return "Drag to define a rounded rectangle";
    }

    /**
     * Create a new FigRect instance based on the given mouse down event and the
     * state of the parent Editor.
     */
    @Override public Fig createNewItem(MouseEvent me, int snapX, int snapY) {
        return new FigRRect(snapX, snapY, 0, 0);
    }
} /* end class ModeCreateFigRRect */

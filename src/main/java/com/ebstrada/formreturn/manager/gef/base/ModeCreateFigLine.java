package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.MouseEvent;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigLine;

/**
 * A Mode to interpert user input while creating a FigLine. All of the actual
 * event handling is inherited from ModeCreate. This class just implements the
 * differences needed to make it specific to lines.
 */

public class ModeCreateFigLine extends ModeCreate {
    private static final long serialVersionUID = -6899160824566397778L;

    @Override public String instructions() {
        return "Drag to define a line";
    }

    /**
     * Make a new FigLine based on the given mouse down event and the parent
     * Editor's default graphical attributes.
     */
    @Override public Fig createNewItem(MouseEvent me, int snapX, int snapY) {
        return new FigLine(snapX, snapY, snapX, snapY);
    }
} /* end class ModeCreateFigLine */

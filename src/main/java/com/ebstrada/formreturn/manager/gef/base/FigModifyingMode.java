package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Cursor;
import java.awt.Graphics;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;

/**
 * This is the basic interface for all Modes that are manipulating the visual
 * representation of the underlying model. It is a subclass of Mode.
 *
 * @see Mode
 * @see FigModifyingModeImpl
 * @see Editor
 */

public interface FigModifyingMode extends Mode {
    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * Set the parent Editor of this Mode
     */
    public void setEditor(Editor w);

    /**
     * Get the parent Editor of this Mode
     */
    public Editor getEditor();

    /**
     * Returns the cursor that should be shown when this Mode starts.
     */
    public Cursor getInitialCursor();

    // //////////////////////////////////////////////////////////////
    // feedback to the user

    /**
     * Reply a string of instructions that should be shown in the statusbar when
     * this mode starts.
     */
    public String instructions();

    /**
     * Set the mouse cursor to some appropriate for this mode.
     */
    public void setCursor(Cursor c);

    // //////////////////////////////////////////////////////////////
    // painting methods

    /**
     * Modes can paint themselves to give the user feedback. For example,
     * ModePlace paints the object being placed. Mode's are drawn on top of
     * (after) the Editor's current view and on top of any selections.
     */
    public void paint(Graphics g);

    /**
     * Just calls paint(g) bt default.
     */
    public void print(Graphics g);

    /**
     * Tests, if the actually handled fig is contained in the one given as
     * parameter.
     */
    public boolean isFigEnclosedIn(Fig testedFig, Fig enclosingFig);

} /* end interface FigModifyingMode */

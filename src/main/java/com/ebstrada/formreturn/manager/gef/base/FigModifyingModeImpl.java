package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Hashtable;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.ui.Main;

/**
 * This is the default implementation for all Modes that are manipulating the
 * visual representation of the underlying model. It is a subclass of ModeImpl
 * and implements FigModifyingMode. The provide simple functionality and some
 * instance variables only. Although this class can be instantiated, it is not
 * designed to be used as an independent Mode. Any FigModifyingMode that is tend
 * to be used by the system should be designed as a subclass of this class and
 * overwrite the methods if necessary.
 *
 * @see Editor
 * @see FigModifyingMode
 * @see ModeImpl
 */

public class FigModifyingModeImpl extends ModeImpl implements FigModifyingMode {
    // //////////////////////////////////////////////////////////////
    // instance variables

    /**
     * The Editor that is in this mode. Each Mode instance belongs to exactly
     * one Editor instance.
     */
    protected Editor editor;

    // //////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a new Mode instance with the given Editor as its editor
     */
    public FigModifyingModeImpl(Editor par) {
        setEditor(par);
    }

    /**
     * Constructs a new Mode instance with some initial parameters. At least a
     * parameter representing the the Editor, this Mode belongs to, should be
     * provided.
     */
    public FigModifyingModeImpl(Hashtable parameters) {
        init(parameters);
    }

    /**
     * Construct a new Mode instance without any Editor as its parent, the
     * parent must be filled in before the instance is actually used. This
     * constructor is needed because CmdSetMode can only call Class.newInstance
     * which does not pass constructor arguments. A call to init is necessary in
     * order to let this instance work properly.
     */
    public FigModifyingModeImpl() {
    }

    // //////////////////////////////////////////////////////////////
    // methods related to transistions among modes

    @Override public void done() {
        setCursor(Cursor.getDefaultCursor());
        editor.finishMode();
        UndoManager.getInstance().removeMementoLock(this);
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * Set the parent Editor of this Mode
     */
    public void setEditor(Editor w) {
        editor = w;
        setCursor(getInitialCursor());
    }

    /**
     * Get the parent Editor of this Mode
     */
    public Editor getEditor() {
        return editor;
    }

    /**
     * Returns the cursor that should be shown when this Mode starts.
     */
    public Cursor getInitialCursor() {
        return Cursor.getDefaultCursor();
    }

    // //////////////////////////////////////////////////////////////
    // feedback to the user

    /**
     * Reply a string of instructions that should be shown in the statusbar when
     * this mode starts.
     */
    public String instructions() {
        return "";
    }

    /**
     * Set the mouse cursor to some appropriate for this mode.
     */
    public void setCursor(Cursor c) {
        if (editor != null) {
            editor.setCursor(c);
        }
    }

    // //////////////////////////////////////////////////////////////
    // painting methods

    /**
     * Modes can paint themselves to give the user feedback. For example,
     * ModePlace paints the object being placed. Mode's are drawn on top of
     * (after) the Editor's current view and on top of any selections.
     */
    public void paint(Graphics g) {
        paint((Object) g);
    }

    /**
     * Just calls paint(g) bt default.
     */
    public void print(Graphics g) {
        print((Object) g);
    }

    /**
     * Modes can paint themselves to give the user feedback. For example,
     * ModePlace paints the object being placed. Mode's are drawn on top of
     * (after) the Editor's current view and on top of any selections.
     */
    final public void paint(Object graphicsContext) {
    }

    /**
     * Just calls paint(g) bt default.
     */
    final public void print(Object graphicsContext) {
        paint(graphicsContext);
    }

    public boolean isFigEnclosedIn(Fig testedFig, Fig enclosingFig) {
        Rectangle bbox = testedFig.getBounds();
        Rectangle trap = enclosingFig.getTrapRect();
        if (trap != null && (trap.contains(bbox.x, bbox.y) && trap
            .contains(bbox.x + bbox.width, bbox.y + bbox.height))) {
            return true;
        }
        return false;
    }

    public int getPageBoundaryHeight(int y) {
        PageAttributes currentPageAttributes = editor.getPageAttributes();
        if (y <= 0) {
            return 0;
        }
        return currentPageAttributes.getCroppedHeight();
    }

    public int getPageBoundaryWidth(int x) {
        PageAttributes currentPageAttributes = editor.getPageAttributes();
        if (x <= 0) {
            return 0;
        }
        return currentPageAttributes.getCroppedWidth();
    }


    public boolean isFigX1InPageBoundary(int x1) {
        PageAttributes currentPageAttributes = editor.getPageAttributes();
        int croppedWidth = currentPageAttributes.getCroppedWidth();
        if (x1 > croppedWidth || x1 < 0) {
            return false;
        }
        return true;
    }

    public boolean isFigX2InPageBoundary(int x2) {
        PageAttributes currentPageAttributes = editor.getPageAttributes();
        int croppedWidth = currentPageAttributes.getCroppedWidth();
        if (x2 > croppedWidth || x2 < 0) {
            return false;
        }
        return true;
    }

    public boolean isFigY1InPageBoundary(int y1) {
        PageAttributes currentPageAttributes = editor.getPageAttributes();
        int croppedHeight = currentPageAttributes.getCroppedHeight();
        if (y1 > croppedHeight || y1 < 0) {
            return false;
        }
        return true;
    }

    public boolean isFigY2InPageBoundary(int y2) {
        PageAttributes currentPageAttributes = editor.getPageAttributes();
        int croppedHeight = currentPageAttributes.getCroppedHeight();
        if (y2 > croppedHeight || y2 < 0) {
            return false;
        }
        return true;
    }

    static final long serialVersionUID = 7960954871341784898L;
} /* end class FigModifyingModeImpl */

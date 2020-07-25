package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.List;

import com.ebstrada.formreturn.manager.gef.graph.MutableGraphSupport;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * Cmd to Nudge Figs by a small distance. This is useful when you want to get
 * diagrams to look just right and you are not to steady with the mouse. Also
 * allows user to keep hands on keyboard.
 *
 * @see com.ebstrada.formreturn.manager.gef.presentation.Fig
 */

public class NudgeAction extends UndoableAction {

    private static final long serialVersionUID = 2121611741541853360L;

    public static final int LEFT = 1;
    public static final int RIGHT = 2;
    public static final int UP = 3;
    public static final int DOWN = 4;

    private int _direction;
    private int _magnitude;

    public NudgeAction(int dir) {
        this(dir, 1);
    }

    public NudgeAction(int dir, int mag) {
        super(Localizer.localize("GefBase", "Nudge" + wordFor(dir))); // needs-more-work:
        // direction
        _direction = dir;
        _magnitude = mag;
    }

    protected static String wordFor(int d) {
        switch (d) {
            case LEFT:
                return "Left";
            case RIGHT:
                return "Right";
            case UP:
                return "Up";
            case DOWN:
                return "Down";
        }
        return "";
    }

    /**
     * Move the selected items a few pixels in the given direction. Note that
     * the sign convention is the opposite of CmdScroll.
     */
    @Override public void actionPerformed(ActionEvent e) {

        super.actionPerformed(e);

        Editor ce = Globals.curEditor();
        SelectionManager sm = ce.getSelectionManager();
        if (sm.getLocked()) {
            Globals.showStatus("Cannot Modify Locked Objects");
            return;
        }

        int dx = 0, dy = 0;
        switch (_direction) {
            case LEFT:
                dx = 0 - _magnitude;
                break;
            case RIGHT:
                dx = _magnitude;
                break;
            case UP:
                dy = 0 - _magnitude;
                break;
            case DOWN:
                dy = _magnitude;
                break;
        }
        // Should I move it so that it aligns with the next grid?

        List figs = sm.getFigs();
        int figCount = figs.size();
        Rectangle figBounds = new Rectangle();
        Fig selectedFig = null;
        int newdx = dx;
        int newdy = dy;
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            selectedFig = (Fig) figs.get(figIndex);
            selectedFig.getBounds(figBounds);
            figBounds.x += dx;
            figBounds.y += dy;
            boolean x2flag = isFigX2InPageBoundary(figBounds.x + figBounds.width, ce);
            if (!x2flag) {
                newdx = 0;
            }
            boolean y2flag = isFigY2InPageBoundary(figBounds.y + figBounds.height, ce);
            if (!y2flag) {
                newdy = 0;
            }
        }

        sm.translate(newdx, newdy);
        MutableGraphSupport.enableSaveAction();
        sm.endTrans();
    }

    public boolean isFigX2InPageBoundary(int x2, Editor editor) {
        PageAttributes currentPageAttributes = editor.getPageAttributes();
        int croppedWidth = currentPageAttributes.getCroppedWidth();
        if (x2 > croppedWidth || x2 < 0) {
            return false;
        }
        return true;
    }

    public boolean isFigY2InPageBoundary(int y2, Editor editor) {
        PageAttributes currentPageAttributes = editor.getPageAttributes();
        int croppedHeight = currentPageAttributes.getCroppedHeight();
        if (y2 > croppedHeight || y2 < 0) {
            return false;
        }
        return true;
    }

    public void undoIt() {
        System.out.println("Cannot undo CmdNudge, yet.");
    }
} /* end class CmdNudge */

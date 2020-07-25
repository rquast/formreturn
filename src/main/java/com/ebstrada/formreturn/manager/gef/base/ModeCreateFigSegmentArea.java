package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Point;
import java.awt.event.MouseEvent;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegmentArea;
import com.ebstrada.formreturn.manager.ui.reprocessor.frame.ReprocessorFrame;

public class ModeCreateFigSegmentArea extends ModeCreate {

    private static final long serialVersionUID = 1L;

    // //////////////////////////////////////////////////////////////
    // Mode API

    @Override public String instructions() {
        return "Click to place the segment area";
    }

    // //////////////////////////////////////////////////////////////
    // ModeCreate API

    /**
     * Create a new FigSegmentArea instance based on the given mouse down event and
     * the state of the parent Editor.
     */
    @Override public Fig createNewItem(MouseEvent me, int snapX, int snapY) {
        FigSegmentArea fsa = getFirstSegmentArea();
        if (fsa == null) {
            fsa = new FigSegmentArea(snapX, snapY);
        }
        return fsa;
    }

    private FigSegmentArea getFirstSegmentArea() {
        ReprocessorFrame rf = editor.getReprocessorFrame();
        if (rf != null) {
            return rf.getNextUnusedSegmentArea();
        } else {
            return null;
        }
    }

    // //////////////////////////////////////////////////////////////
    // event handlers

    /**
     * When the mouse enters an Editor, create the FigSegmentArea and place it at the
     * mouse position.
     */
    @Override public void mouseEntered(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        start();
        anchorX = x;
        anchorY = y;
        Point snapPt = new Point(x, y);
        editor.snap(snapPt);
        if (null == _newItem) {
            _newItem = createNewItem(me, snapPt.x, snapPt.y);
        }
        me.consume();
    }

    /**
     * When the mouse exits the editor, clean up the display a little.
     */
    @Override public void mouseExited(MouseEvent me) {
        editor.damageAll();
        me.consume();
    }

    /**
     * On mouse down, do nothing.
     */
    @Override public void mousePressed(MouseEvent me) {
        me.consume();
    }

    /**
     * Whem the user drags or moves the mouse, move the FigSegmentArea to the current
     * mouse position.
     */
    @Override public void mouseMoved(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        if (_newItem == null) {
            me.consume();
            return;
        }
        editor.damageAll();
        Point snapPt = new Point(x, y);
        editor.snap(snapPt);
        _newItem.setLocation(snapPt.x, snapPt.y);
        editor.damageAll(); /* needed? */
        me.consume();
    }

    /**
     * Exactly the same as mouseMove.
     */
    @Override public void mouseDragged(MouseEvent me) {
        mouseMoved(me);
    }
} /* end class ModeCreateFigSegmentArea */

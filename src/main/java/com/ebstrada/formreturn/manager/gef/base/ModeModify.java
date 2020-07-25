package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;

import com.ebstrada.formreturn.manager.gef.graph.GraphModel;
import com.ebstrada.formreturn.manager.gef.graph.MutableGraphSupport;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.Handle;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.ui.Main;

/**
 * A Mode to process events from the Editor when the user is modifying a Fig.
 * Right now users can drag one or more Figs around the drawing area, or they
 * can move a handle on a single Fig.
 *
 * @see Fig
 * @see Selection
 */
public class ModeModify extends FigModifyingModeImpl {
    private static final long serialVersionUID = -914125238898272775L;

    /**
     * Minimum amount that the user must move the mouse to indicate that she
     * really wants to modify something.
     */
    private static final int MIN_DELTA = 4;
    private double degrees45 = Math.PI / 4;

    /**
     * drag in process
     */
    private boolean _dragInProcess = false;

    /**
     * The current position of the mouse during a drag operation.
     */
    private Point newMousePosition = new Point(0, 0);

    /**
     * The point at which the mouse started a drag operation.
     */
    private Point dragStartMousePosition = new Point(0, 0);

    /**
     * The location of the selection when the drag was started.
     */
    private Point dragStartSelectionPosition = null;

    /**
     * The index of the handle that the user is dragging
     */
    private Handle _curHandle = new Handle(-1);
    private Rectangle _highlightTrap = null;
    private int _deltaMouseX;
    private int _deltaMouseY;

    private GraphModel graphModel;

    // private ModifyCommand modifyCommand;
    //    

    /**
     * Construct a new ModeModify with the given parent, and set the Anchor
     * point to a default location (the _anchor's proper position will be
     * determioned on mouse down).
     */
    public ModeModify(Editor par) {
        super(par);
    }

    // //////////////////////////////////////////////////////////////
    // user feedback

    /**
     * Reply a string of instructions that should be shown in the statusbar when
     * this mode starts.
     */
    @Override public String instructions() {
        return "Modify selected objects";
    }

    // //////////////////////////////////////////////////////////////
    // event handlers

    /**
     * When the user drags the mouse two things can happen: (1) if the user is
     * dragging the body of one or more Figs then they are all moved around the
     * drawing area, or (2) if the user started dragging on a handle of one Fig
     * then the user can drag the handle around the drawing area and the Fig
     * reacts to that.
     */
    @Override public void mouseDragged(MouseEvent mouseEvent) {
        if (mouseEvent.isConsumed()) {
            return;
        }

        mouseEvent.consume();
        Point p = mouseEvent.getPoint();
        getEditor().snap(p); // only allow movement on snap positions
        newMousePosition.x = p.x;
        newMousePosition.y = p.y;
        _deltaMouseX = p.x - dragStartMousePosition.x;
        _deltaMouseY = p.y - dragStartMousePosition.y;
        if (!_dragInProcess && Math.abs(_deltaMouseX) < MIN_DELTA
            && Math.abs(_deltaMouseY) < MIN_DELTA) {
            return;
        }

        if (!_dragInProcess) {
            _dragInProcess = true;
            UndoManager.getInstance().startChain();
            graphModel = editor.getGraphModel();
            if (graphModel instanceof MutableGraphSupport) {
                ((MutableGraphSupport) graphModel).fireGraphChanged();
            }
        }

        boolean restrict45 = mouseEvent.isControlDown();
        handleMouseDragged(restrict45);
    }

    /**
     * Check if a drag operation is in progress and if the key event changes the
     * restriction of horizontal/vertical movement. If so, update the
     * selection's position.
     *
     * @param keyEvent
     */
    private void updateMouseDrag(KeyEvent keyEvent) {
        if (_dragInProcess) {
            boolean restrict45 = keyEvent.isControlDown();
            handleMouseDragged(restrict45);
        }
    }

    @Override public void keyPressed(KeyEvent keyEvent) {
        super.keyPressed(keyEvent);
        updateMouseDrag(keyEvent);
    }

    @Override public void keyReleased(KeyEvent keyEvent) {
        super.keyReleased(keyEvent);
        updateMouseDrag(keyEvent);
    }

    /**
     * Like handleMouseDragged(MouseEvent) but takes only delta mouse position
     * as arguments. Is also called when control is pressed or released during
     * the drag.
     */
    private void handleMouseDragged(boolean restrict45) {
        int deltaMouseX = _deltaMouseX;
        int deltaMouseY = _deltaMouseY;
        if (restrict45 && deltaMouseY != 0) {
            double degrees = Math.atan2(deltaMouseY, deltaMouseX);
            degrees = degrees45 * Math.round(degrees / degrees45);
            double r = Math.sqrt(deltaMouseX * deltaMouseX + deltaMouseY * deltaMouseY);
            deltaMouseX = (int) (r * Math.cos(degrees));
            deltaMouseY = (int) (r * Math.sin(degrees));
        }

        SelectionManager selectionManager = getEditor().getSelectionManager();
        if (selectionManager.getLocked()) {
            Globals.showStatus("Cannot Modify Locked Objects");
            return;
        }

        if (dragStartSelectionPosition == null) {
            selectionManager.startDrag();
        }

        Point selectionCurrentPosition = null;
        if (selectionManager.size() == 1 && (_curHandle.index > 0)) {
            selectionCurrentPosition = new Point(dragStartMousePosition);
        } else {
            selectionCurrentPosition = selectionManager.getDragLocation();
        }

        if (dragStartSelectionPosition == null) {
            dragStartSelectionPosition = selectionCurrentPosition;
        }

        Point selectionNewPosition = new Point(dragStartSelectionPosition);
        selectionNewPosition.translate(deltaMouseX, deltaMouseY);
        getEditor().snap(selectionNewPosition);
        selectionNewPosition.x = Math.max(0, selectionNewPosition.x);
        selectionNewPosition.y = Math.max(0, selectionNewPosition.y);

        int deltaSelectionX = selectionNewPosition.x - selectionCurrentPosition.x;
        int deltaSelectionY = selectionNewPosition.y - selectionCurrentPosition.y;
        if (deltaSelectionX != 0 || deltaSelectionY != 0) {
            if (_curHandle.index == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                legal(deltaSelectionX, deltaSelectionY, selectionManager);
            } else {
                if (_curHandle.index >= 0) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

                    int x2 = newMousePosition.x;
                    boolean x2flag = isFigX2InPageBoundary(newMousePosition.x);
                    if (!x2flag) {
                        x2 = getPageBoundaryWidth(newMousePosition.x);
                    }

                    int y2 = newMousePosition.y;
                    boolean y2flag = isFigY2InPageBoundary(newMousePosition.y);
                    if (!y2flag) {
                        y2 = getPageBoundaryHeight(newMousePosition.y);
                    }

                    selectionManager
                        .dragHandle(x2, y2, dragStartMousePosition.x, dragStartMousePosition.y,
                            _curHandle);
                    selectionManager.endTrans();
                }
            }
            // Note: if _curHandle.index == -2 then do nothing
        }
    }

    /**
     * When the user presses the mouse button on a Fig, this Mode starts
     * preparing for future drag events by finding if a handle was clicked on.
     * This event is passed from ModeSelect.
     */
    @Override public void mousePressed(MouseEvent me) {
        if (me.isConsumed()) {
            return;
        }

        int x = me.getX();
        int y = me.getY();
        start();
        SelectionManager selectionManager = getEditor().getSelectionManager();
        if (selectionManager.size() == 0) {
            done();
        }

        if (selectionManager.getLocked()) {
            Globals.showStatus("Cannot Modify Locked Objects");
            me.consume();
            return;
        }

        dragStartMousePosition = me.getPoint();
        dragStartSelectionPosition = null;
        selectionManager.hitHandle(new Rectangle(x - 4, y - 4, 8, 8), _curHandle);
        Globals.showStatus(_curHandle.instructions);
        selectionManager.endTrans();

    }

    /**
     * On mouse up the modification interaction is done.
     */
    @Override public void mouseReleased(MouseEvent me) {
        _dragInProcess = false;
        if (me.isConsumed()) {
            return;
        }

        done();
        me.consume();
        SelectionManager sm = editor.getSelectionManager();
        sm.stopDrag();
        List figs = sm.getFigs();
        int figCount = figs.size();
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig selectedFig = (Fig) figs.get(figIndex);
            selectedFig.endTrans();
        }
    }

    @Override public void done() {
        super.done();
        SelectionManager sm = getEditor().getSelectionManager();
        sm.cleanUp();
        if (_highlightTrap != null) {
            editor.damaged(_highlightTrap);
            _highlightTrap = null;
        }
    }

    @Override public void paint(Graphics g) {
        super.paint(g);
        if (_highlightTrap != null) {
            // Graphics g = (Graphics)graphicsContext;
            Color selectRectColor = Globals.getPrefs().getRubberbandColor();
            g.setColor(selectRectColor);
            g.drawRect(_highlightTrap.x - 1, _highlightTrap.y - 1, _highlightTrap.width + 1,
                _highlightTrap.height + 1);
            g.drawRect(_highlightTrap.x - 2, _highlightTrap.y - 2, _highlightTrap.width + 3,
                _highlightTrap.height + 3);
        }
    }

    private void damageHighlightTrap() {
        if (_highlightTrap == null) {
            return;
        }
        Rectangle r = new Rectangle(_highlightTrap);
        r.x -= 2;
        r.y -= 2;
        r.width += 4;
        r.height += 4;
        editor.damaged(r);
    }

    private void legal(int dx, int dy, SelectionManager selectionManager) {

        List figs = selectionManager.getFigs();
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

            boolean x2flag = isFigX2InPageBoundary(figBounds.x + figBounds.width);
            if (!x2flag) {
                newdx = 0;
            }

            boolean y2flag = isFigY2InPageBoundary(figBounds.y + figBounds.height);
            if (!y2flag) {
                newdy = 0;
            }

        }

        selectionManager.drag(newdx, newdy);

    }
}

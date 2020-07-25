package com.ebstrada.formreturn.manager.gef.base;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigGroup;
import com.ebstrada.formreturn.manager.gef.presentation.Handle;

/**
 * This class implements a Mode that interprets user input as selecting one or
 * more Figs. Clicking on a Fig will select it. Shift-clicking will toggle
 * whether it is selected. Dragging in open space will draw a selection
 * rectangle. Dragging on a Fig will switch to ModeModify. Dragging from a port
 * will switch to ModeCreateEdge. ModeSelect paints itself by displaying its
 * selection rectangle if any.
 * <p>
 * <p>
 * Needs-More-Work: this mode has more responsibility than just making
 * selections, it has become the "main mode" of the editor and it has taken
 * resposibility for switching to other modes. I should probably implement a
 * "UIDialog" class that would have a state machine that describes the various
 * transitions between UI modes.
 * <p>
 *
 * @see ModeCreateEdge
 * @see ModeModify
 * @see Fig
 * @see Editor
 */
public class ModeSelect extends FigModifyingModeImpl {
    private static final long serialVersionUID = 2412264848254549816L;

    /**
     * If the user drags a selection rectangle, this is the first corner.
     */
    private Point selectAnchor = new Point(0, 0);

    /**
     * This is the seclection rectangle.
     */
    private Rectangle selectRect = new Rectangle(0, 0, 0, 0);

    /**
     * True when the selection rectangle should be painted.
     */
    private boolean showSelectRect = false;

    /**
     * True when the user holds the shift key to toggle selections.
     */
    private boolean toggleSelection = false;

    private static Log LOG = LogFactory.getLog(ModeSelect.class);

    // //////////////////////////////////////////////////////////////
    // constructors and related methods

    /**
     * Construct a new ModeSelect with the given parent.
     */
    public ModeSelect(Editor parent) {
        super(parent);
    }

    /**
     * Construct a new ModeSelect instance. Its parent must be set before this
     * instance can be used.
     */
    public ModeSelect() {
    }

    /**
     * Always false because I never want to get out of selection mode.
     */
    @Override public boolean canExit() {
        return false;
    }

    // //////////////////////////////////////////////////////////////
    // event handlers

    /**
     * Handle mouse down events by preparing for a drag. If the mouse down event
     * happens on a handle or an already selected object, and the shift key is
     * not down, then go to ModeModify. If the mouse down event happens on a
     * port, go to ModeCreateEdge.
     */
    @Override public void mousePressed(MouseEvent me) {
        if (me.isConsumed()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("MousePressed but rejected as already consumed");
            }
            return;
        }

        if (me.isAltDown()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("MousePressed but rejected as alt key pressed");
            }
            return;
        }

        if (me.getModifiers() == InputEvent.BUTTON3_MASK) {
            selectAnchor = new Point(me.getX(), me.getY());
            if (LOG.isDebugEnabled()) {
                LOG.debug("MousePressed detected button 3 so setting anchor point");
            }
            // TODO should we not consume here?
            return;
        }

        if (me.isShiftDown()) {
            gotoBroomMode(me);
            if (LOG.isDebugEnabled()) {
                LOG.debug("MousePressed with shift key so gone to broom mode");
            }
            // TODO should we not consume here?
            return;
        }

        int x = me.getX();
        int y = me.getY();
        selectAnchor = new Point(x, y);
        selectRect.setBounds(x, y, 0, 0);
        toggleSelection = (me.isControlDown() && !me.isPopupTrigger()) || me.isMetaDown();
        SelectionManager sm = editor.getSelectionManager();
        Rectangle hitRect = new Rectangle(x - 4, y - 4, 8, 8);

        /* Check if multiple things are selected and user clicked one of them. */
        Fig underMouse = editor.hit(selectAnchor);
        Rectangle smallHitRect = new Rectangle(x - 1, y - 1, 3, 3);
        if (underMouse instanceof FigGroup) {
            underMouse = ((FigGroup) underMouse).deepSelect(smallHitRect);
        }

        if (underMouse == null && !sm.hit(hitRect)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("MousePressed but rejected as no fig found");
            }
            return;
        }

        Handle h = new Handle(-1);
        sm.hitHandle(new Rectangle(x - 4, y - 4, 8, 8), h);
        if (h.index >= 0) {
            gotoModifyMode(me);
            me.consume();
            if (LOG.isDebugEnabled()) {
                LOG.debug("MousePressed with hit handle, going to Modify mode and consumed event");
            }
            return;
        }

        if (underMouse != null) {
            if (toggleSelection) {
                sm.toggle(underMouse);
            } else if (!sm.containsFig(underMouse)) {
                sm.select(underMouse);
            }
        }

        if (sm.hit(hitRect)) {
            gotoModifyMode(me);
        }

        me.consume();
        if (LOG.isDebugEnabled()) {
            LOG.debug("MousePressed selection changed and consumed event");
        }
    }

    /**
     * On mouse dragging, stretch the selection rectangle.
     */
    @Override public void mouseDragged(MouseEvent me) {
        if (me.isConsumed() || me.isAltDown() || me.isMetaDown()) {
            return;
        }
        editor.translateMouseEvent(me);
        int x = me.getX();
        int y = me.getY();

        showSelectRect = true;

        int boundX = Math.min(selectAnchor.x, x);
        int boundY = Math.min(selectAnchor.y, y);
        int boundW = Math.max(selectAnchor.x, x) - boundX;
        int boundH = Math.max(selectAnchor.y, y) - boundY;

        double scale = editor.getScale();

        scaleDamage(scale, selectRect);

        selectRect.setBounds(boundX, boundY, boundW, boundH);

        scaleDamage(scale, selectRect);

        editor.scrollToShow(x, y);

        me.consume();
    }

    /**
     * Damage the area of the rect after scaling
     *
     * @param scale
     * @param rect
     */
    private void scaleDamage(double scale, Rectangle rect) {
        int newX = (int) (rect.x * scale) - 1;
        int newY = (int) (rect.y * scale) - 1;
        int newWidth = (int) (((rect.width + 2)) * scale) + 2;
        int newHeight = (int) (((rect.height + 2)) * scale) + 2;
        editor.damaged(newX, newY, newWidth, newHeight);
    }

    /**
     * On mouse up, select or toggle the selection of items under the mouse or
     * in the selection rectangle.
     */
    @Override public void mouseReleased(MouseEvent me) {
        if (me.isConsumed()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("MouseReleased but rejected as already consumed");
            }
            return;
        }

        if (me.isMetaDown()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("MouseReleased but rejected as meta key down");
            }
            return;
        }
        // editor.translateMouseEvent(me);

        int x = me.getX();
        int y = me.getY();
        showSelectRect = false;
        Vector selectList = new Vector();
        Rectangle hitRect = new Rectangle(x - 4, y - 4, 8, 8);
        Enumeration figs = editor.figs();
        while (figs.hasMoreElements()) {
            Fig f = (Fig) figs.nextElement();
            if (f.isSelectable() && ((!toggleSelection && selectRect.isEmpty() && f.hit(hitRect))
                || (!selectRect.isEmpty() && f.within(selectRect)))) {
                selectList.addElement(f);
            }
        }

        if (!selectRect.isEmpty() && selectList.isEmpty()) {
            figs = editor.figs();
            while (figs.hasMoreElements()) {
                Fig f = (Fig) figs.nextElement();
                if (f.isSelectable() && f.intersects(selectRect)) {
                    selectList.addElement(f);
                }
            }
        }

        if (toggleSelection) {
            editor.getSelectionManager().toggle(selectList);
        } else {
            editor.getSelectionManager().select(selectList);
        }

        selectRect.grow(1, 1); /* make sure it is not empty for redraw */
        editor.scaleRect(selectRect);
        editor.damaged(selectRect);
        if (me.getModifiers() == InputEvent.BUTTON3_MASK) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("MouseReleased button 3 detected so not consumed");
            }
            return;
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("MouseReleased and consumed");
        }
        me.consume();
    }

    // //////////////////////////////////////////////////////////////
    // user feedback methods

    /**
     * Reply a string of instructions that should be shown in the statusbar when
     * this mode starts.
     */
    @Override public String instructions() {
        return "  ";
    }

    // //////////////////////////////////////////////////////////////
    // painting methods

    /**
     * Paint this mode by painting the selection rectangle if appropriate.
     */
    @Override public void paint(Graphics g) {
        if (showSelectRect) {
            Color selectRectColor = Globals.getPrefs().getRubberbandColor();
            Color color = new Color(selectRectColor.getRed(), selectRectColor.getGreen(),
                selectRectColor.getBlue(), 50);
            ((Graphics2D) g).setPaint(color);
            ((Graphics2D) g).fill(
                new Rectangle(selectRect.x, selectRect.y, selectRect.width, selectRect.height));
            g.setColor(selectRectColor);
            g.drawRect(selectRect.x, selectRect.y, selectRect.width, selectRect.height);
        }
    }

    // //////////////////////////////////////////////////////////////
    // methods related to transitions among modes

    /**
     * Set the Editor's Mode to ModeModify. Needs-More-Work: This should not be
     * in ModeSelect, I wanted to move it to ModeModify, but it is too tighly
     * integrated with ModeSelect.
     */
    protected void gotoModifyMode(MouseEvent me) {
        FigModifyingModeImpl nextMode = new ModeModify(editor);
        editor.pushMode(nextMode);
        nextMode.mousePressed(me);
    }

    protected void gotoBroomMode(MouseEvent me) {
        FigModifyingModeImpl nextMode = new ModeBroom(editor);
        editor.pushMode(nextMode);
        nextMode.mousePressed(me);
    }

    /**
     * Determine if a mouse event was to toggle selection of multiple items. On
     * a Mac this is by Command-Click. On a non-mac this is by Ctrl-Click. There
     * seems to be no platform independant way of determining this.
     */
    private boolean isMultiSelectTrigger(MouseEvent me) {
        // If the control key is down and this is not a popup trigger then
        // this cannot be a mac and will return true.
        // If the meta key is down then this can only be a mac and will return
        // true
        return (me.isControlDown() && !me.isPopupTrigger()) || me.isMetaDown();
    }
} /* end class ModeSelect */

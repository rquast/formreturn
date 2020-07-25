package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.List;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * A Cmd to align 2 or more objects relative to each other.
 */

public class DistributeAction extends UndoableAction {

    private static final long serialVersionUID = 3630014084522093432L;
    /**
     * Constants specifying the type of distribution requested.
     */
    public static final int H_SPACING = 0;
    public static final int H_CENTERS = 1;
    public static final int H_PACK = 2;
    public static final int V_SPACING = 4;
    public static final int V_CENTERS = 5;
    public static final int V_PACK = 6;

    /**
     * Specification of the type of distribution requested
     */
    private int _request;
    private Rectangle _bbox = null;
    private List figs;
    private Integer gap;

    /**
     * Construct a new CmdDistribute.
     *
     * @param r The desired alignment direction, one of the constants
     *          listed above.
     */
    public DistributeAction(int r) {
        super("Distribute" + wordFor(r));
        _request = r;
    }

    public DistributeAction(int r, List figs) {
        this(r);
        this.figs = figs;
    }

    private static String wordFor(int r) {
        switch (r) {
            case H_SPACING:
                return "HorizontalSpacing";
            case H_CENTERS:
                return "HorizontalCenters";
            case H_PACK:
                return "Leftward";
            case V_SPACING:
                return "VerticalSpacing";
            case V_CENTERS:
                return "VerticalCenters";
            case V_PACK:
                return "Upward";
        }
        return "";
    }

    public void setBoundingBox(Rectangle bbox) {
        _bbox = bbox;
    }

    public void setGap(Integer gap) {
        this.gap = gap;
    }

    @Override public void actionPerformed(ActionEvent e) {

        super.actionPerformed(e);

        super.actionPerformed(e);
        Editor ce = Globals.curEditor();
        int packGap = 8;
        if (gap != null) {
            packGap = gap.intValue();
        }

        SelectionManager sm = ce.getSelectionManager();
        if (sm.getLocked()) {
            Globals.showStatus("Cannot Modify Locked Objects");
            return;
        }
        figs = sm.getFigs();

        int leftMostCenter = 0, rightMostCenter = 0;
        int topMostCenter = 0, bottomMostCenter = 0;
        int size = figs.size();
        if (size == 0) {
            return;
        }

        // find the bbox of all selected objects
        Fig f = (Fig) figs.get(0);
        _bbox = f.getBounds();
        leftMostCenter = _bbox.x + _bbox.width / 2;
        rightMostCenter = _bbox.x + _bbox.width / 2;
        topMostCenter = _bbox.y + _bbox.height / 2;
        bottomMostCenter = _bbox.y + _bbox.height / 2;
        for (int i = 1; i < size; i++) {
            f = (Fig) figs.get(i);
            Rectangle r = f.getBounds();
            _bbox.add(r);
            leftMostCenter = Math.min(leftMostCenter, r.x + r.width / 2);
            rightMostCenter = Math.max(rightMostCenter, r.x + r.width / 2);
            topMostCenter = Math.min(topMostCenter, r.y + r.height / 2);
            bottomMostCenter = Math.max(bottomMostCenter, r.y + r.height / 2);
        }

        // find the sum of the widths and heights of all selected objects
        int totalWidth = 0, totalHeight = 0;
        for (int i = 0; i < size; i++) {
            f = (Fig) figs.get(i);
            totalWidth += f.getWidth();
            totalHeight += f.getHeight();
        }

        float gap = 0, oncenter = 0;
        float xNext = 0, yNext = 0;

        switch (_request) {
            case H_SPACING:
                xNext = _bbox.x;
                gap = (_bbox.width - totalWidth) / Math.max(size - 1, 1);
                break;
            case H_CENTERS:
                xNext = leftMostCenter;
                oncenter = (rightMostCenter - leftMostCenter) / Math.max(size - 1, 1);
                break;
            case H_PACK:
                xNext = _bbox.x;
                gap = packGap;
                break;
            case V_SPACING:
                yNext = _bbox.y;
                gap = (_bbox.height - totalHeight) / Math.max(size - 1, 1);
                break;
            case V_CENTERS:
                yNext = topMostCenter;
                oncenter = (bottomMostCenter - topMostCenter) / Math.max(size - 1, 1);
                break;
            case V_PACK:
                yNext = _bbox.y;
                gap = packGap;
                break;
        }

        // sort top-to-bottom or left-to-right, this maintains visual order
        // when we set the coordinates
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                Fig fi = (Fig) figs.get(i);
                Fig fj = (Fig) figs.get(j);
                if (_request == H_SPACING || _request == H_CENTERS || _request == H_PACK) {
                    if (fi.getX() > fj.getX()) {
                        swap(figs, i, j);
                    }
                } else if (fi.getY() > fj.getY()) {
                    swap(figs, i, j);
                }
            }
        }

        for (int i = 0; i < size; i++) {
            f = (Fig) figs.get(i);
            switch (_request) {
                case H_SPACING:
                case H_PACK:
                    f.setLocation((int) xNext, f.getY());
                    xNext += f.getWidth() + gap;
                    break;
                case H_CENTERS:
                    f.setLocation((int) xNext - f.getWidth() / 2, f.getY());
                    xNext += oncenter;
                    break;
                case V_SPACING:
                case V_PACK:
                    f.setLocation(f.getX(), (int) yNext);
                    yNext += f.getHeight() + gap;
                    break;
                case V_CENTERS:
                    f.setLocation(f.getX(), (int) yNext - f.getHeight() / 2);
                    yNext += oncenter;
                    break;
            }
            f.endTrans();
        }
    }

    public void undoIt() {
    }

    protected void swap(List v, int i, int j) {
        Object temp = v.get(i);
        v.set(i, v.get(j));
        v.set(j, temp);
    }

    public Rectangle getBoundingBox() {
        return _bbox;
    }
}

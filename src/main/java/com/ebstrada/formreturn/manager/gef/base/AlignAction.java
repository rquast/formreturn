package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

/**
 * An Cmd to align 2 or more objects relative to each other.
 */

public class AlignAction extends UndoableAction {

    private static final long serialVersionUID = 4982051206522858526L;

    /**
     * Constants specifying the type of alignment requested.
     */
    public static final int ALIGN_TOPS = 0;
    public static final int ALIGN_BOTTOMS = 1;
    public static final int ALIGN_LEFTS = 2;
    public static final int ALIGN_RIGHTS = 3;

    public static final int ALIGN_CENTERS = 4;
    public static final int ALIGN_H_CENTERS = 5;
    public static final int ALIGN_V_CENTERS = 6;

    public static final int ALIGN_TO_GRID = 7;

    private List figs;

    /**
     * Specification of the type of alignment requested
     */
    private int direction;

    private Map boundsByFig;

    /**
     * Construct a new CmdAlign.
     *
     * @param dir The desired alignment direction, one of the constants
     *            listed above.
     */
    public AlignAction(int dir) {
        super("Align" + wordFor(dir)); // needs-more-work:
        // direction
        direction = dir;
    }

    public AlignAction(int dir, List figs) {
        super("Align" + wordFor(dir)); // needs-more-work:
        // direction
        direction = dir;
        this.figs = figs;
    }

    private static String wordFor(int d) {
        switch (d) {
            case ALIGN_TOPS:
                return "Tops";
            case ALIGN_BOTTOMS:
                return "Bottoms";
            case ALIGN_LEFTS:
                return "Lefts";
            case ALIGN_RIGHTS:
                return "Rights";

            case ALIGN_CENTERS:
                return "Centers";
            case ALIGN_H_CENTERS:
                return "HorizontalCenters";
            case ALIGN_V_CENTERS:
                return "VerticalCenters";

            case ALIGN_TO_GRID:
                return "ToGrid";
        }
        return "";
    }

    @Override public void actionPerformed(ActionEvent e) {

        super.actionPerformed(e);

        Editor ce = Globals.curEditor();
        SelectionManager sm = ce.getSelectionManager();
        if (sm.getLocked()) {
            Globals.showStatus("Cannot Modify Locked Objects");
            return;
        }
        figs = sm.getFigs();

        int size = figs.size();
        if (size == 0) {
            return;
        }
        Rectangle bbox = ((Fig) figs.get(0)).getBounds();
        for (int i = 1; i < size; i++) {
            bbox.add(((Fig) figs.get(i)).getBounds());
        }

        boundsByFig = new HashMap(size);
        for (int i = 0; i < size; i++) {
            Fig f = (Fig) figs.get(i);
            boundsByFig.put(f, f.getBounds());
            f.align(bbox, direction, ce);
            f.endTrans();
        }
    }
} /* end class AlignAction */

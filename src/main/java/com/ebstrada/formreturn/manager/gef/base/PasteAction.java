package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.beans.VetoableChangeListener;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import com.ebstrada.formreturn.manager.gef.undo.memento.FigAddMemento;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigCheckbox;
import com.ebstrada.formreturn.manager.gef.presentation.FigImage;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegment;
import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.gef.util.VetoableChangeEventSource;

/**
 * Paste action.
 */
public class PasteAction extends UndoableAction {

    private static final long serialVersionUID = 1306168450357555809L;

    /**
     * Creates a new PasteAction
     *
     * @param name The name of the action
     */
    public PasteAction(String name) {
        this(name, false);
    }

    /**
     * Creates a new PasteAction
     *
     * @param name The name of the action
     * @param icon The icon of the action
     */
    public PasteAction(String name, Icon icon) {
        this(name, icon, false);
    }

    /**
     * Creates a new PasteAction
     *
     * @param name     The name of the action
     * @param localize Whether to localize the name or not
     */
    public PasteAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    /**
     * Creates a new PasteAction
     *
     * @param name     The name of the action
     * @param icon     The icon of the action
     * @param localize Whether to localize the name or not
     */
    public PasteAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    // needs-more-work: if the Fig was removed from the model, then I would
    // need to create a new owner.

    @Override public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                int documentType =
                    Globals.curEditor().getGraph().getDocumentAttributes().getDocumentType();
                SelectionManager sm = Globals.curEditor().getSelectionManager();
                Vector<Fig> figs = new Vector<Fig>();

                if (Globals.clipBoard == null) {
                    return;
                }

                if (Globals.clipBoard.elements() == null) {
                    return;
                }

                int lowestX = Integer.MAX_VALUE;
                int lowestY = Integer.MAX_VALUE;

                for (Object obj : Globals.clipBoard) {

                    Fig f = (Fig) obj;

                    Point p = f.getLocation();

                    if (p.x < lowestX) {
                        lowestX = p.x;
                    }

                    if (p.y < lowestY) {
                        lowestY = p.y;
                    }

                }

                Editor ce = Globals.curEditor();
                Point mp = ce.getLastMousePosition();

                double scaledX = (double) mp.x / ce.getScale();
                double scaledY = (double) mp.y / ce.getScale();


                int xOffset = (int) scaledX - lowestX;
                int yOffset = (int) scaledY - lowestY;

                for (Object obj : Globals.clipBoard) {

                    Fig f = (Fig) obj;

                    // never paste a checkbox to a form
                    if (f instanceof FigCheckbox && documentType == DocumentAttributes.FORM) {
                        continue;
                    }

                    // never paste a segment area to a segment
                    if (f instanceof FigSegment && documentType == DocumentAttributes.SEGMENT) {
                        continue;
                    }

                    // never paste an image to another editor
                    if (f instanceof FigImage) {
                        if (f.getGraph().getEditor() != ce) {
                            continue;
                        }
                    }

                    int linearPosition = (int) mp.getX();

                    if (f.getLastLinearPosition() <= 0
                        || f.getLastLinearPosition() != linearPosition) {
                        f.setCopyY(0);
                    }

                    Fig fclone = (Fig) f.clone();

                    Object owner = fclone.getOwner();
                    if (owner instanceof VetoableChangeEventSource
                        && fclone instanceof VetoableChangeListener) {
                        ((VetoableChangeEventSource) owner)
                            .addVetoableChangeListener((VetoableChangeListener) fclone);
                    }

                    fclone.translate(xOffset, yOffset + (int) f.getCopyY());
                    f.setCopyY(f.getCopyY() + f.getHeight());

                    f.setLastLinearPosition(linearPosition);

                    ce.add(fclone);

                    if (UndoManager.getInstance().isGenerateMementos()) {
                        UndoManager.getInstance().addMemento(new FigAddMemento(fclone));
                    }

                    figs.addElement(fclone);
                    fclone.firePropChange("paste", null, null);

                }
                sm.deselectAll();
                sm.select(figs);

            }
        });

    }
}

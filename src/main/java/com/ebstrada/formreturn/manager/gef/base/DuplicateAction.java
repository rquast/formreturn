package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.beans.VetoableChangeListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;

import com.ebstrada.formreturn.manager.gef.graph.MutableGraphSupport;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigCheckbox;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegmentArea;
import com.ebstrada.formreturn.manager.gef.undo.Memento;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.gef.undo.UndoableAction;
import com.ebstrada.formreturn.manager.gef.undo.memento.FigAddMemento;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.gef.util.VetoableChangeEventSource;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.dialog.DuplicateElementDialog;
import com.ebstrada.formreturn.manager.ui.editor.persistence.FieldnameDuplicatePresets;
import com.ebstrada.formreturn.manager.util.Misc;

public class DuplicateAction extends UndoableAction {

    private static final long serialVersionUID = 1L;

    public DuplicateAction(String name) {
        this(name, false);
    }

    public DuplicateAction(String name, Icon icon) {
        this(name, icon, false);
    }

    public DuplicateAction(String name, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name);
    }

    public DuplicateAction(String name, Icon icon, boolean localize) {
        super(localize ? Localizer.localize("GefBase", name) : name, icon);
    }

    @Override public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Editor ce = Globals.curEditor();
        if (ce == null) {
            return;
        }
        SelectionManager selectionManager = ce.getSelectionManager();
        Vector copiedElements = selectionManager.selections();
        Enumeration copies = copiedElements.elements();

        if (copiedElements.size() <= 0) {
            Misc.showErrorMsg(ce.getGraph().getRootPane(),
                Localizer.localize("GefBase", "DuplicateActionInvalidNumberOfElements"));
            return;
        }

        int maxX = 0;
        int maxY = 0;
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;

        while (copies.hasMoreElements()) {
            Selection s = (Selection) copies.nextElement();
            Fig f = s.getContent();

            if (f instanceof FigSegmentArea) {
                Misc.showErrorMsg(ce.getGraph().getRootPane(),
                    Localizer.localize("GefBase", "DuplicateActionFigSegmentAreaNotAllowed"));
                return;
            }

            if (f.getWidth() + f.getX() > maxX) {
                maxX = f.getWidth() + f.getX();
            }

            if (f.getHeight() + f.getY() > maxY) {
                maxY = f.getHeight() + f.getY();
            }

            if (f.getX() < minX) {
                minX = f.getX();
            }

            if (f.getY() < minY) {
                minY = f.getY();
            }

        }

        DuplicateElementDialog ded =
            new DuplicateElementDialog((Frame) ce.getGraph().getTopLevelAncestor());
        String daFieldnamePrefix =
            ce.getGraph().getDocumentAttributes().getDefaultCapturedDataFieldname();
        int daCDFNIncrementor = ce.getGraph().getDocumentAttributes().getDefaultCDFNIncrementor();

        if (daFieldnamePrefix != null) {
            ded.setFieldnamePrefix(daFieldnamePrefix);
        }

        MutableGraphSupport.enableSaveAction();
        if (UndoManager.getInstance().isGenerateMementos()) {
            UndoManager.getInstance().startChain();
        }

        ded.setFieldnameCounter(daCDFNIncrementor);
        ded.setDocumentAttributes(ce.getGraph().getDocumentAttributes());
        ded.setModal(true);
        ded.setVisible(true);
        if (ded.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {

            if (Misc.validateFieldname(ded.getFieldnamePrefix()) == false) {
                String msg = Localizer.localize("GefBase", "DuplicateActionInvalidFieldName");
                Misc.showErrorMsg(ce.getGraph().getRootPane(), msg);
                return;
            }

            int counterStart = ded.getCounterStart();

            int counter = counterStart + 0;

            String fieldnamePrefix = ded.getFieldnamePrefix();
            int verticalSpacing = ded.getVerticalSpacing();
            int horizontalSpacing = ded.getHorizontalSpacing();
            int verticalDuplicates = ded.getVerticalDuplicates();
            int horizontalDuplicates = ded.getHorizontalDuplicates();

            int namingDirection = ded.getNamingDirection();

            int duplicateMaxX =
                minX + (((maxX - minX) + horizontalSpacing) * (horizontalDuplicates - 1)) + (maxX
                    - minX);
            int duplicateMaxY =
                minY + (((maxY - minY) + verticalSpacing) * (verticalDuplicates - 1)) + (maxY
                    - minY);

            int graphWidth = ce.getGraph().getWidth();
            int graphHeight = ce.getGraph().getHeight();

            if (duplicateMaxX > graphWidth) {
                Misc.showErrorMsg(ce.getGraph().getRootPane(),
                    Localizer.localize("GefBase", "DuplicateActionTooWide"));
                return;
            }

            if (duplicateMaxY > graphHeight) {
                Misc.showErrorMsg(ce.getGraph().getRootPane(),
                    Localizer.localize("GefBase", "DuplicateActionTooHigh"));
                return;
            }

            if (namingDirection
                == FieldnameDuplicatePresets.DIRECTION_LEFT_TO_RIGHT_TOP_TO_BOTTOM) {

                for (int i = 0; i < verticalDuplicates; i++) {

                    for (int j = 0; j < horizontalDuplicates; j++) {

                        // skip the first one (its the same position)
                        if (i == 0 && j == 0) {
                            continue;
                        }

                        copies = copiedElements.elements();
                        while (copies.hasMoreElements()) {
                            Selection s = (Selection) copies.nextElement();
                            Fig f = s.getContent();

                            Fig cloneFig = (Fig) f.clone();

                            int x = cloneFig.getX();
                            int y = cloneFig.getY();
                            cloneFig.setX(x + (((maxX - minX) + horizontalSpacing) * j));
                            cloneFig.setY(y + (((maxY - minY) + verticalSpacing) * i));

                            Object owner = cloneFig.getOwner();
                            if (owner instanceof VetoableChangeEventSource
                                && cloneFig instanceof VetoableChangeListener) {
                                ((VetoableChangeEventSource) owner)
                                    .addVetoableChangeListener((VetoableChangeListener) cloneFig);
                            }
                            if (cloneFig instanceof FigCheckbox) {
                                ((FigCheckbox) cloneFig)
                                    .setDefaultFieldname(fieldnamePrefix, counter, true);
                                ++counter;
                            }
                            ce.add(cloneFig);

                            if (UndoManager.getInstance().isGenerateMementos()) {
                                UndoManager.getInstance().addMemento(new FigAddMemento(cloneFig));
                            }
                        }

                    }

                }


            } else {
                // top to bottom left to right
                for (int i = 0; i < horizontalDuplicates; i++) {

                    for (int j = 0; j < verticalDuplicates; j++) {

                        // skip the first one (its the same position)
                        if (i == 0 && j == 0) {
                            continue;
                        }

                        copies = copiedElements.elements();
                        while (copies.hasMoreElements()) {
                            Selection s = (Selection) copies.nextElement();
                            Fig f = s.getContent();

                            Fig cloneFig = (Fig) f.clone();

                            int x = cloneFig.getX();
                            int y = cloneFig.getY();
                            cloneFig.setX(x + (((maxX - minX) + horizontalSpacing) * i));
                            cloneFig.setY(y + (((maxY - minY) + verticalSpacing) * j));

                            Object owner = cloneFig.getOwner();
                            if (owner instanceof VetoableChangeEventSource
                                && cloneFig instanceof VetoableChangeListener) {
                                ((VetoableChangeEventSource) owner)
                                    .addVetoableChangeListener((VetoableChangeListener) cloneFig);
                            }
                            if (cloneFig instanceof FigCheckbox) {
                                ((FigCheckbox) cloneFig)
                                    .setDefaultFieldname(fieldnamePrefix, counter, true);
                                ++counter;
                            }
                            ce.add(cloneFig);

                            if (UndoManager.getInstance().isGenerateMementos()) {
                                UndoManager.getInstance().addMemento(new FigAddMemento(cloneFig));
                            }
                        }

                    }

                }
            }



        }

        ded.dispose();

    }
}

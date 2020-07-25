package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.swing.SwingUtilities;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.structure.BarcodeRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.SegmentRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentBarcode;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.ui.reprocessor.dialog.SegmentStencilEditorDialog;
import com.ebstrada.formreturn.manager.ui.reprocessor.panel.FigSegmentAreaPanel;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.NoObfuscation;

public class FigSegmentArea extends Fig
    implements NoObfuscation, MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 1L;

    private int barcodeOne;

    private int barcodeTwo;

    private long segmentId;

    private int fragmentPadding = 1;
    private int markThreshold = 40;
    private int luminanceThreshold = 200;

    private SegmentRecognitionStructure segmentRecognitionStructure;

    public FigSegmentArea(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public FigSegmentArea(int x, int y) {
        this(x, y, 0, 0);
        setSize(145, 95);
    }

    @Override public void createDrag(int anchorX, int anchorY, int x, int y, int snapX, int snapY,
        boolean released) {
        setLocation(snapX, snapY);
    }

    @Override public void paint(Object g) {
        paint(g, false);
    }

    @Override public void paint(Object graphicContext, boolean includeMargins) {

        if (includeMargins == false) {
            setMarginOffset(0, 0);
        }

        Graphics g = (Graphics) graphicContext;

        if (segmentRecognitionStructure != null) {

            Map<String, OMRRecognitionStructure> omrrs =
                segmentRecognitionStructure.getOMRRecognitionStructures();
            Map<String, BarcodeRecognitionStructure> bcrs =
                segmentRecognitionStructure.getBarcodeRecognitionStructures();

            for (OMRRecognitionStructure omrs : omrrs.values()) {

                Rectangle2D segmentBoundary =
                    new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());

                Rectangle2D fragmentBoundary =
                    omrs.getRecognitionArea(segmentBoundary, fragmentPadding);

                g.setColor(Color.RED);
                g.drawRect((int) fragmentBoundary.getX(), (int) fragmentBoundary.getY(),
                    (int) fragmentBoundary.getWidth(), (int) fragmentBoundary.getHeight());

            }

            for (BarcodeRecognitionStructure bcs : bcrs.values()) {

                Rectangle2D segmentBoundary =
                    new Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());

                Rectangle2D fragmentBoundary =
                    bcs.getRecognitionArea(segmentBoundary, fragmentPadding);

                g.setColor(Color.RED);
                g.drawRect((int) fragmentBoundary.getX(), (int) fragmentBoundary.getY(),
                    (int) fragmentBoundary.getWidth(), (int) fragmentBoundary.getHeight());

            }

        }

        g.setColor(Color.GREEN);
        g.drawRect(getX(), getY(), getWidth(), getHeight());

    }

    @Override public EditorPanel getEditorPanel() {
        return new FigSegmentAreaPanel(getGraph().getEditor().getReprocessorFrame());
    }

    public void rebuildSegment() {

        if (segmentId <= 0) {
            return;
        }

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return;
        }

        try {

            Segment segment = entityManager.find(Segment.class, segmentId);
            segmentRecognitionStructure = new SegmentRecognitionStructure();
            segmentRecognitionStructure
                .setBarcodeOneValue(Misc.parseIntegerString(segment.getBarcodeOne()));
            segmentRecognitionStructure
                .setBarcodeTwoValue(Misc.parseIntegerString(segment.getBarcodeTwo()));

            for (FragmentOmr fragmentOmr : segment.getFragmentOmrCollection()) {

                OMRRecognitionStructure omrrs = new OMRRecognitionStructure(fragmentOmr);

                segmentRecognitionStructure.addFragment(omrrs.getFieldName(), omrrs);

            }

            for (FragmentBarcode fragmentBarcode : segment.getFragmentBarcodeCollection()) {

                BarcodeRecognitionStructure bcrs = new BarcodeRecognitionStructure();
                bcrs.setOrderIndex((int) fragmentBarcode.getOrderIndex());
                bcrs.setPercentX1(fragmentBarcode.getX1Percent());
                bcrs.setPercentX2(fragmentBarcode.getX2Percent());
                bcrs.setPercentY1(fragmentBarcode.getY1Percent());
                bcrs.setPercentY2(fragmentBarcode.getY2Percent());
                bcrs.setFieldName(fragmentBarcode.getCapturedDataFieldName());
                bcrs.setBarcodeType(fragmentBarcode.getBarcodeType());
                bcrs.setReconciliationKey(
                    (fragmentBarcode.getReconciliationKey() == 1) ? true : false);

                segmentRecognitionStructure.addFragment(bcrs.getFieldName(), bcrs);

            }


        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            segmentRecognitionStructure = null;
            return;
        } finally {
            if (entityManager.isOpen()) {
                entityManager.close();
            }
        }

    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) {
            final FigSegmentArea thisInstance = this;
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    SegmentStencilEditorDialog sasd;
                    try {
                        sasd =
                            new SegmentStencilEditorDialog((Frame) getGraph().getTopLevelAncestor(),
                                thisInstance);
                        sasd.setTitle(Localizer.localize("UI", "SegmentStencilEditorDialogTitle"));
                        sasd.setModal(true);
                        sasd.setVisible(true);
                        sasd.dispose();
                    } catch (Exception e) {
                    }
                }
            });
            e.consume();
        }
    }

    public int getBarcodeOne() {
        return barcodeOne;
    }

    public void setBarcodeOne(int barcodeOne) {
        this.barcodeOne = barcodeOne;
    }

    public int getBarcodeTwo() {
        return barcodeTwo;
    }

    public void setBarcodeTwo(int barcodeTwo) {
        this.barcodeTwo = barcodeTwo;
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseDragged(MouseEvent me) {
        int x = me.getX();
        int y = me.getY();
        getGraph().getEditor().scrollToShow(x, y);
    }

    public long getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(long segmentId) {
        this.segmentId = segmentId;
        rebuildSegment();
    }

    public SegmentRecognitionStructure getSegmentRecognitionStructure() {
        return segmentRecognitionStructure;
    }

    public void setSegmentRecognitionStructure(
        SegmentRecognitionStructure segmentRecognitionStructure) {
        this.segmentRecognitionStructure = segmentRecognitionStructure;
    }

    public int getFragmentPadding() {
        return fragmentPadding;
    }

    public void setFragmentPadding(int fragmentPadding) {
        this.fragmentPadding = fragmentPadding;
    }

    public double getMarkThreshold() {
        return (1 + (((double) markThreshold) / 100.0));
    }

    public int getRealMarkThreshold() {
        return markThreshold;
    }

    public void setMarkThreshold(int markThreshold) {
        this.markThreshold = markThreshold;
    }

    public int getLuminanceThreshold() {
        return luminanceThreshold;
    }

    public void setLuminanceThreshold(int luminanceThreshold) {
        this.luminanceThreshold = luminanceThreshold;
    }

    public void mouseMoved(MouseEvent e) {
    }

}

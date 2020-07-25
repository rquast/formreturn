package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import com.ebstrada.formreturn.manager.gef.base.Editor;
import com.ebstrada.formreturn.manager.gef.font.CachedFontManager;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.ui.SegmentContainer;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.publish.PDFDocumentExporter;
import com.ebstrada.formreturn.manager.logic.recognition.structure.SegmentRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.persistence.xstream.Page;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.dialog.FigSegmentSetup;
import com.ebstrada.formreturn.manager.ui.editor.panel.FigSegmentPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.ebstrada.formreturn.manager.util.RandomGUID;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("segmentArea") public class FigSegment extends Fig
    implements Serializable, NoObfuscation, MouseListener, RecognitionStructureFig {

    private static final long serialVersionUID = 1L;

    private SegmentContainer segmentContainer = new SegmentContainer();

    private String formGUID;

    private String segmentAreaGUID;

    private transient Font font;

    private transient Image segmentImage;

    private transient Graphics segmentGraphics;

    private transient SegmentRecognitionStructure segmentRecognitionStructure;

    private transient String workingDirName;

    private transient Integer barcodeOneValue;

    private transient Map<String, String> recordMap;

    private transient int selectedSegmentIndex = 0;

    public FigSegment(int x, int y, int w, int h, String formGUID) {
        super(x, y, w, h);
        getSegmentAreaGUID();
        this.formGUID = formGUID;
        updateSegmentBounds();
    }

    public void setWidth(int w) {
        if (isResizable()) {
            setBounds(_x, _y, w, _h);
        }
    }

    public void setHeight(int h) {
        if (isResizable()) {
            setBounds(_x, _y, _w, h);
        }
    }

    @Override public JGraph getGraph() {
        return ((Editor) getLayer().getEditors().get(0)).getGraph();
    }

    public void setFont(Font font) {
        this.font = font;
    }

    private void setDefaultFont() {
        CachedFontManager cachedFontManager = Main.getCachedFontManager();
        Font defaultFont = cachedFontManager.getDefaultFont();
        setFont(defaultFont.deriveFont(10.0f));
    }

    public void setSegmentAttributes(SegmentContainer segmentAttributes) {
        this.segmentContainer = segmentAttributes;
        updateSegmentBounds();
    }

    public SegmentContainer getSegmentAttributes() {
        return segmentContainer;
    }

    public void updateSegmentBounds() {

        Document documentContainer = null;

        if (segmentContainer.getNumberOfSegments() > 0) {
            documentContainer = segmentContainer.getSegment(getSelectedSegmentIndex());
        }

        if (documentContainer == null) {
            segmentImage = null;
            setResizable(true);
            damage();
            return;
        }

        setResizable(false);

        PageAttributes pageAttributes =
            documentContainer.getPageByPageNumber(1).getPageAttributes();

        setBounds(_x, _y, pageAttributes.getCroppedWidth(), _h);
        setBounds(_x, _y, _w, pageAttributes.getCroppedHeight());

        damage();
    }

    public void removeFromDiagram() {

        // remove from the page model.
        int numberOfPages = getGraph().getDocument().getNumberOfPages();
        for (int i = 1; i <= numberOfPages; i++) {
            Page page = getGraph().getDocument().getPageByPageNumber(i);
            page.removeFig(this);
        }
        super.removeFromDiagram();
    }

    public void paintPDFDocumentExporter(PDFDocumentExporter g) {

        Document segment = getSegmentContainer().getSegment(getSelectedSegmentIndex());
        for (Page page : segment.getPages().values()) {
            for (Fig fsf : page.getFigs()) {

                // set the segment's barcode one and two values
                if (fsf instanceof FigBarcode) {
                    FigBarcode figBarcode = (FigBarcode) fsf;
                    if (figBarcode.getRecognitionMarkerType() == FigBarcode.MARKER_TOP_RIGHT) {
                        figBarcode.setRenderableBarcodeValue("0" + getBarcodeOneValue());
                        figBarcode.setRevalidate(true);
                        try {
                            figBarcode.resetBarcode();
                        } catch (Exception e) {
                            Misc.printStackTrace(e);
                        }
                    } else if (figBarcode.getRecognitionMarkerType()
                        == FigBarcode.MARKER_BOTTOM_LEFT) {
                        figBarcode.setRenderableBarcodeValue("0" + (getBarcodeOneValue() + 1));
                        figBarcode.setRevalidate(true);
                        try {
                            figBarcode.resetBarcode();
                        } catch (Exception e) {
                            Misc.printStackTrace(e);
                        }
                    }
                }

                if (fsf instanceof FigImage) {
                    FigImage figImage = (FigImage) fsf;
                    figImage.setWorkingDirName(getWorkingDirName());
                }
                fsf.setPageAttributes(page.getPageAttributes());
                fsf.setOffset(getX(), getY());
                fsf.postLoad();
                if (fsf instanceof FigImage) {
                    ((FigImage) fsf).paint(g, false, false);
                } else {
                    fsf.paint(g);
                }
            }
        }

    }

    public String getWorkingDirName() {
        if (this.workingDirName != null) {
            return this.workingDirName;
        } else {
            return getGraph().getDocumentPackage().getWorkingDirName();
        }
    }

    public void setWorkingDirName(String workingDirName) {
        this.workingDirName = workingDirName;
    }

    @Override public void paint(Object g) {
        paint(g, false);
    }

    @Override public void paint(Object g, boolean includeMargins) {

        if (includeMargins == false) {
            setMarginOffset(0, 0);
        }

        if (font == null) {
            setDefaultFont();
            ((Graphics) g).setFont(font);
        } else {
            ((Graphics) g).setFont(font);
        }

        FontMetrics fontMetrics = ((Graphics) g).getFontMetrics();

        if (g instanceof PDFDocumentExporter) {
            paintPDFDocumentExporter((PDFDocumentExporter) g);
        } else {

            if (segmentContainer.getNumberOfSegments() <= 0) {

                Fig.plotter.drawRect(g, _filled, new Color(200, 200, 255, 50), getLineWidth(),
                    new Color(200, 200, 255), _x, _y, _w, _h, getDashed(), _dashes, _dashPeriod);

                String character = Localizer.localize("UI", "EmptySegmentArea");
                int characterWidth = fontMetrics.stringWidth(character);
                int characterHeight = fontMetrics.getHeight();

                int characterX = _x + (_w / 2) - (characterWidth / 2);
                int characterY = _y + (_h / 2) + (characterHeight / 2);

                if (getWidth() > characterWidth && getHeight() > characterHeight) {

                    ((Graphics) g).setColor(new Color(150, 150, 255));
                    ((Graphics) g).drawString(character, characterX, characterY);
                    ((Graphics) g).setColor(Color.black);

                }

            } else {

                Document documentContainer = null;

                if (segmentContainer.getNumberOfSegments() > 0) {
                    documentContainer = segmentContainer.getSegment(getSelectedSegmentIndex());
                }

                if (documentContainer == null) {
                    segmentImage = null;
                    setResizable(true);
                    damage();
                    return;
                }

                setResizable(false);

                for (Page page : documentContainer.getPages().values()) {
                    for (Fig fsf : page.getFigs()) {

                        // set the segment's barcode one and two values
                        if (fsf instanceof FigBarcode) {
                            FigBarcode figBarcode = (FigBarcode) fsf;
                            if (figBarcode.getRecognitionMarkerType()
                                == FigBarcode.MARKER_TOP_RIGHT) {
                                figBarcode.setRenderableBarcodeValue("0" + getBarcodeOneValue());
                                figBarcode.setRevalidate(true);
                                try {
                                    figBarcode.resetBarcode();
                                } catch (Exception e) {
                                    Misc.printStackTrace(e);
                                }
                            } else if (figBarcode.getRecognitionMarkerType()
                                == FigBarcode.MARKER_BOTTOM_LEFT) {
                                figBarcode
                                    .setRenderableBarcodeValue("0" + (getBarcodeOneValue() + 1));
                                figBarcode.setRevalidate(true);
                                try {
                                    figBarcode.resetBarcode();
                                } catch (Exception e) {
                                    Misc.printStackTrace(e);
                                }
                            }
                        }

                        if (fsf instanceof FigImage) {
                            FigImage figImage = (FigImage) fsf;
                            figImage.setWorkingDirName(getWorkingDirName());
                        }
                        fsf.setPageAttributes(page.getPageAttributes());
                        fsf.setOffset(_x + getXOffset(), _y + getYOffset());
                        fsf.setMarginOffset(getLeftMarginOffset(), getTopMarginOffset());
                        fsf.postLoad();

                        if (fsf instanceof FigImage) {
                            ((FigImage) fsf).paint(g, includeMargins, false);
                        } else {
                            fsf.paint(g, includeMargins);
                        }
                    }
                }

            }

        }

    }

    public int getSelectedSegmentIndex() {
        return this.selectedSegmentIndex;
    }

    public void setSelectedSegmentIndex(int selectedSegmentIndex) {
        this.selectedSegmentIndex = selectedSegmentIndex;
    }

    public Map<String, String> getRecordMap() {
        return recordMap;
    }

    public void setRecordMap(Map<String, String> recordMap) {
        this.recordMap = recordMap;
    }

    public SegmentRecognitionStructure getSegmentRecognitionStructure() {
        return this.segmentRecognitionStructure;
    }

    public int getSegmentIndexByValue(String fieldValue) {
        return segmentContainer.getLinkID(fieldValue);
    }

    public int getDefaultSegmentIndex() {
        return getSegmentIndexByValue(segmentContainer.getDefaultSelectedSegment());
    }

    @Override public EditorPanel getEditorPanel() {
        return new FigSegmentPanel();
    }

    public SegmentContainer getSegmentContainer() {
        return segmentContainer;
    }


    public void newSegmentAreaGUID() {
        segmentAreaGUID = (new RandomGUID()).toString();
    }

    public String getSegmentAreaGUID() {
        if (segmentAreaGUID == null) {
            newSegmentAreaGUID();
        }
        return segmentAreaGUID;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) {
            e.consume();
            FigSegmentSetup fss = new FigSegmentSetup(this);
            fss.setTitle(Localizer.localize("UI", "SegmentSetupDialogTitle"));
            fss.setModal(true);
            fss.setVisible(true);
            fss.dispose();
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void setBarcodeOneValue(Integer barcodeOneValue) {
        this.barcodeOneValue = barcodeOneValue;
    }

    public Integer getBarcodeOneValue() {
        if (this.barcodeOneValue == null) {
            return 1;
        }
        return barcodeOneValue;
    }

    public void addRecognitionStructure(SegmentRecognitionStructure segmentRecognitionStructure) {
        this.segmentRecognitionStructure = segmentRecognitionStructure;
    }

    public int getRandomSegmentIndex() {
        if (this.getSegmentContainer().getListSize() <= 0) {
            return 0;
        }
        Random random = new Random();
        return random.nextInt(this.getSegmentContainer().getListSize());
    }

}

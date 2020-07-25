package com.ebstrada.formreturn.manager.logic.publish;

import java.awt.Graphics2D;
import java.util.List;
import java.util.Map;

import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigBarcode;
import com.ebstrada.formreturn.manager.gef.presentation.FigImage;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegment;
import com.ebstrada.formreturn.manager.gef.presentation.FigText;
import com.ebstrada.formreturn.manager.gef.presentation.RecognitionStructureFig;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.logic.recognition.structure.SegmentRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.persistence.xstream.Page;
import com.ebstrada.formreturn.manager.util.Misc;

public class FormRenderer {

    private Graphics2D graphics;

    private int barcodeOneValue = 1;

    private int leftMarginOffset;

    private int topMarginOffset;

    private PageAttributes pageAttributes;

    private String workingDirName;

    private Map<String, String> recordMap;

    public FormRenderer() {
        // reset the counter to the start.
        setBarcodeOneValue(1);
    }

    public Graphics2D getGraphics() {
        return graphics;
    }

    public void setGraphics(Graphics2D graphcs) {
        this.graphics = graphcs;
    }

    public int getBarcodeOneValue() {
        return barcodeOneValue;
    }

    public void setBarcodeOneValue(int barcodeOneValue) {
        this.barcodeOneValue = barcodeOneValue;
    }

    public void paintPage(Page pageContainer, String barcodeValue, FormPage formPage) {
        List<Fig> figs = pageContainer.getFigs();
        if (figs != null) {
            for (Fig fig : figs) {
                fig.setMarginOffset(getLeftMarginOffset(), getTopMarginOffset());
                fig.setPageAttributes(pageAttributes);

                parseFig(fig, barcodeValue, recordMap);

                if (fig instanceof FigSegment) {

                    FigSegment figSegment = (FigSegment) fig;
                    figSegment.setWorkingDirName(workingDirName);
                    figSegment.setBarcodeOneValue(getBarcodeOneValue());
                    figSegment.setRecordMap(recordMap);
                    Document documentContainer =
                        getSegment(figSegment, recordMap, formPage, barcodeOneValue);

                    if (documentContainer == null) {
                        continue;
                    }

                    setBarcodeOneValue(barcodeOneValue + 2);

                    for (Page page : documentContainer.getPages().values()) {
                        for (Fig fsf : page.getFigs()) {
                            parseFig(fsf, null, recordMap);
                        }
                    }

                } else if (fig instanceof FigImage) {
                    ((FigImage) fig).setWorkingDirName(workingDirName);
                }

                fig.postLoad();

                fig.paint(getGraphics(), true);
            }
        }
    }

    private Document getSegment(FigSegment figSegment, Map<String, String> recordMap,
        FormPage formPage, int barcodeOneValue) {

        Segment seg = Misc.getSegment(formPage, getBarcodeOneValue());
        if (seg == null) {
            return null;
        }
        return Misc.getSelectedSegmentContainer(figSegment, recordMap, seg);

    }

    public void setLeftMarginOffset(int leftMarginOffset) {
        this.leftMarginOffset = leftMarginOffset;
    }

    public void setTopMarginOffset(int topMarginOffset) {
        this.topMarginOffset = topMarginOffset;
    }

    public PageAttributes getPageAttributes() {
        return pageAttributes;
    }

    public void setPageAttributes(PageAttributes pageAttributes) {
        this.pageAttributes = pageAttributes;
    }

    public String getWorkingDirName() {
        return workingDirName;
    }

    public void setWorkingDirName(String workingDirName) {
        this.workingDirName = workingDirName;
    }

    public Map<String, String> getRecordMap() {
        return recordMap;
    }

    public void setRecordMap(Map<String, String> recordMap) {
        this.recordMap = recordMap;
    }

    // this is where we set the form page data
    public static void parseFig(Fig fig, String barcodeValue, Map<String, String> recordMap) {

        if (fig instanceof FigBarcode) {

            FigBarcode figBarcode = (FigBarcode) fig;

            // clear the byte array cache barcode svg (from the last time it was viewed)
            figBarcode.clearRender();

            if (figBarcode.getBarcodeType().equalsIgnoreCase("Form ID")) {
                // if the barcode is a form ID barcode, set the value!!
                figBarcode.setRenderableBarcodeValue(barcodeValue);
            } else {

                if (figBarcode.getRecognitionMarkerType() == FigBarcode.NOT_MARKER) {

                    // regular barcode, parse it first for TVR data.
                    figBarcode.setRenderableBarcodeValue(
                        Misc.parseFields(figBarcode.getBarcodeValue(), recordMap));

                }

            }

        } else if (fig instanceof FigText) {

            FigText figText = (FigText) fig;

            // parse the text for any TVR data!!
            figText.setRenderableText(Misc.parseFields(figText.getText(), recordMap));

        }

    }

    public int getFullHeight() {
        int height = pageAttributes.getCroppedHeight();
        int fullHeight = height + pageAttributes.getTopMargin() + pageAttributes.getBottomMargin();
        return fullHeight;
    }

    public int getFullWidth() {
        int width = pageAttributes.getCroppedWidth();
        int fullWidth = width + pageAttributes.getLeftMargin() + pageAttributes.getRightMargin();
        return fullWidth;
    }

    public void paintGraph(JGraph graph) {
        setBarcodeOneValue(1);
        List<Fig> figs = graph.getEditor().getLayerManager().getActiveLayer().getContents();
        if (figs != null) {
            for (Fig fig : figs) {
                if (fig instanceof FigSegment) {
                    FigSegment figSegment = (FigSegment) fig;
                    figSegment.setBarcodeOneValue(getBarcodeOneValue());
                    setBarcodeOneValue(this.barcodeOneValue + 2);
                }
                fig.paint(getGraphics(), true);
            }
        }
    }

    public int getLeftMarginOffset() {
        return leftMarginOffset;
    }

    public int getTopMarginOffset() {
        return topMarginOffset;
    }

    public void setStructureData(JGraph graph, String barcodeValue) {
        List<Fig> figs = graph.getEditor().getLayerManager().getActiveLayer().getContents();
        if (figs != null) {
            for (Fig fig : figs) {

                fig.setPageAttributes(pageAttributes);
                fig.setMarginOffset(getLeftMarginOffset(), getTopMarginOffset());

                parseFig(fig, barcodeValue, recordMap);

                if (fig instanceof FigSegment) {

                    // this code is only used for form previews
                    FigSegment figSegment = (FigSegment) fig;
                    figSegment.setRecordMap(recordMap);
                    figSegment.setWorkingDirName(workingDirName);
                    figSegment.setBarcodeOneValue(getBarcodeOneValue());

                    Document documentContainer = null;

                    documentContainer = Misc.getSelectedSegmentContainer(figSegment, recordMap);

                    if (documentContainer == null) {
                        continue;
                    }

                    SegmentRecognitionStructure segmentRecognitionStructure =
                        new SegmentRecognitionStructure();
                    segmentRecognitionStructure.setBarcodeOneValue(getBarcodeOneValue());
                    segmentRecognitionStructure.setBarcodeTwoValue(getBarcodeOneValue() + 1);

                    graph.getFormRecognitionStructure()
                        .addSegmentRecognitionStructure(getBarcodeOneValue(),
                            segmentRecognitionStructure);
                    figSegment.addRecognitionStructure(segmentRecognitionStructure);

                    setBarcodeOneValue(getBarcodeOneValue() + 2);

                    for (Page page : documentContainer.getPages().values()) {

                        segmentRecognitionStructure
                            .setWidth(page.getPageAttributes().getCroppedWidth());
                        segmentRecognitionStructure
                            .setHeight(page.getPageAttributes().getCroppedHeight());

                        for (Fig fsf : page.getFigs()) {

                            parseFig(fsf, null, recordMap);

                            if (fsf instanceof RecognitionStructureFig
                                && figSegment.getSegmentRecognitionStructure() != null) {
                                RecognitionStructureFig rsf = (RecognitionStructureFig) fsf;
                                rsf.addRecognitionStructure(
                                    figSegment.getSegmentRecognitionStructure());
                            }

                        }
                    }

                } else if (fig instanceof RecognitionStructureFig && !(fig instanceof FigSegment)) {

                    // this code is only used for segment previews
                    RecognitionStructureFig rsf = (RecognitionStructureFig) fig;
                    rsf.addRecognitionStructure(graph.getFormRecognitionStructure()
                        .getSegmentRecognitionStructure(getBarcodeOneValue()));

                } else if (fig instanceof FigImage) {

                    ((FigImage) fig).setWorkingDirName(workingDirName);

                }

                fig.postLoad();

            }
        }
    }

}

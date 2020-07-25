package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebstrada.formreturn.manager.gef.font.CachedFontManager;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.persistence.ExportAttributes;
import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.gef.undo.memento.CheckboxMemento;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.structure.CheckBoxRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FormRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRMatrix;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.SegmentRecognitionStructure;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.dialog.FigCheckboxProperties;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorMultiPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.FigCheckboxMultiPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.FigCheckboxPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("checkbox") public class FigCheckbox extends Fig
    implements Serializable, NoObfuscation, MouseListener, RecognitionStructureFig {

    private static final long serialVersionUID = 1L;

    public static final int READ_STRING_LEFT_TO_RIGHT = 0;

    public static final int READ_STRING_TOP_TO_BOTTOM = 1;

    private static Log log = LogFactory.getLog(FigCheckbox.class);

    private int boxWidth = 16;

    private int boxHeight = 8;

    private float boxWeight = 1.0f;

    private int widthRoundness = 85;

    private int heightRoundness = 85;

    private int horizontalSpace = 12;

    private int verticalSpace = 10;

    private int fontDarkness = 140;

    private transient Font font;

    private float fontSize = 6.0f;

    public String fontName;

    private String fieldname = Localizer.localize("UI", "QuestionPrefix");

    private int fieldnameOrderIndex = 0;

    private String markFieldname;

    private int markFieldnameOrderIndex = 0;

    private String aggregationRule =
        Localizer.localize("GefBase", "CheckboxDefaultAggregationRule");

    private boolean showText = true;

    private boolean combineColumnCharacters = false;

    private boolean reconciliationKey = false;

    private int readDirection = OMRMatrix.READ_STRING_LEFT_TO_RIGHT;

    @XStreamAlias("checkboxValues") private String checkboxValues[][];

    private transient PageAttributes pageAttributes;

    // //////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a new resizable FigCheckbox with the given position and size.
     */
    public FigCheckbox(int x, int y, int w, int h) {
        super(x, y, w, h);
        setDefaultCheckboxValues();
        setLineWidth(boxWeight);
        setDefaultFont();
        setDefaultFieldname();
    }

    @Override public Object clone() {
        Object obj = super.clone();

        /*
         * get a unique fieldname on paste
         */
        if (obj instanceof FigCheckbox) {
            ((FigCheckbox) obj).setDefaultFieldname();
        }

        return obj;
    }

    public void setDefaultFieldname() {
        JGraph graph = getGraph();
        DocumentAttributes documentAttributes = graph.getDocumentAttributes();
        String defaultCDFN = documentAttributes.getDefaultCapturedDataFieldname();
        int defaultCDFNIncrementor = documentAttributes.getDefaultCDFNIncrementor();
        setDefaultFieldname(defaultCDFN, defaultCDFNIncrementor, false);
        setNextOrderIndex();
    }

    public void setNextOrderIndex() {

        if (getGraph() != null) {

            JGraph graph = getGraph();

            if (graph != null) {

                int maximumOrderIndex = 0;

                List layerContents = graph.getEditor().getLayerManager().getContents();
                for (int j = 0; j < layerContents.size(); j++) {
                    Fig fig = (Fig) layerContents.get(j);
                    if (fig instanceof FigBarcodeReader) {
                        if (((FigBarcodeReader) fig).getFieldnameOrderIndex() > maximumOrderIndex) {
                            maximumOrderIndex = ((FigBarcodeReader) fig).getFieldnameOrderIndex();
                        }
                    } else if (fig instanceof FigCheckbox) {
                        if (((FigCheckbox) fig).getFieldnameOrderIndex() > maximumOrderIndex) {
                            maximumOrderIndex = ((FigCheckbox) fig).getFieldnameOrderIndex();
                        }
                        if (((FigCheckbox) fig).getMarkFieldnameOrderIndex() > maximumOrderIndex) {
                            maximumOrderIndex = ((FigCheckbox) fig).getMarkFieldnameOrderIndex();
                        }
                    }
                }

                setFieldnameOrderIndex(maximumOrderIndex + 1);
                setMarkFieldnameOrderIndex(maximumOrderIndex + 2);

            }
        }

    }

    public void setDefaultFieldname(String defaultCDFN, int defaultCDFNIncrementor,
        boolean skipThis) {

        if (getGraph() != null) {

            JGraph graph = getGraph();

            if (graph != null) {

                for (int i = defaultCDFNIncrementor; i < 9999999; i++) {

                    String defaultFieldName = defaultCDFN + i + "";

                    boolean nomatch = true;

                    List layerContents = graph.getEditor().getLayerManager().getContents();
                    for (int j = 0; j < layerContents.size(); j++) {
                        Fig fig = (Fig) layerContents.get(j);
                        if (fig instanceof FigBarcodeReader) {
                            if (skipThis == false && fig == this) {
                                continue;
                            }
                            if (((FigBarcodeReader) fig).getFieldname()
                                .equalsIgnoreCase(defaultFieldName)) {
                                nomatch = false;
                            }
                        } else if (fig instanceof FigCheckbox) {
                            if (skipThis == false && fig == this) {
                                continue;
                            }
                            if (((FigCheckbox) fig).getFieldname()
                                .equalsIgnoreCase(defaultFieldName)) {
                                nomatch = false;
                            }
                        }
                    }

                    if (nomatch) {
                        this.fieldname = defaultFieldName;
                        this.markFieldname =
                            defaultFieldName + Localizer.localize("UICDM", "MarkColumnNameSuffix");
                        break;
                    }

                }


            }
        }

    }

    private void setDefaultFont() {
        CachedFontManager cachedFontManager = Main.getCachedFontManager();
        Font defaultFont = cachedFontManager.getDefaultFont();
        setFont(defaultFont.deriveFont(fontSize));
    }

    public void setDefaultCheckboxValues() {
        checkboxValues = new String[1][5];
        checkboxValues[0] = new String[] {Localizer.localize("GefBase", "CheckboxDefaultValue1"),
            Localizer.localize("GefBase", "CheckboxDefaultValue2"),
            Localizer.localize("GefBase", "CheckboxDefaultValue3"),
            Localizer.localize("GefBase", "CheckboxDefaultValue4"),
            Localizer.localize("GefBase", "CheckboxDefaultValue5")};
    }

    public void setBoxWidth(int newBoxSize) {
        boxWidth = newBoxSize;
    }

    public void setBoxHeight(int boxHeight) {
        this.boxHeight = boxHeight;
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public int getBoxHeight() {
        return this.boxHeight;
    }

    public void setBoxWeight(float newBoxWeight) {
        boxWeight = newBoxWeight;
        setLineWidth(newBoxWeight);
    }

    public float getBoxWeight() {
        return boxWeight;
    }

    public void setHorizontalSpace(int newHorizontalSpace) {
        horizontalSpace = newHorizontalSpace;
    }

    public int getHorizontalSpace() {
        return horizontalSpace;
    }

    public void setVerticalSpace(int newVerticalSpace) {
        verticalSpace = newVerticalSpace;
    }

    public int getVerticalSpace() {
        return verticalSpace;
    }

    public void setFont(Font newFont) {
        firePropChange("font", font, newFont);
        font = newFont;
        fontName = font.getFontName();
        fontSize = font.getSize2D();
    }

    public int getRowCount() {
        if (checkboxValues == null) {
            return 0;
        }
        return checkboxValues.length;
    }

    public int getColumnCount() {
        if (checkboxValues == null) {
            return 0;
        }
        if (checkboxValues[0] == null) {
            return 0;
        }
        return checkboxValues[0].length;
    }

    public PageAttributes getPageAttributes() {
        if (this.pageAttributes != null) {
            return this.pageAttributes;
        } else {
            return getGraph().getPageAttributes();
        }
    }

    public void setPageAttributes(PageAttributes pageAttributes) {
        this.pageAttributes = pageAttributes;
    }

    @Override public void paint(Object g) {
        paint(g, false);
    }

    @Override public void paint(Object g, boolean includeMargins) {

        if (includeMargins == false) {
            setMarginOffset(0, 0);
        }

        if (g instanceof Graphics2D) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        }

        if (font == null) {
            setDefaultFont();
        }

        if (font != null) {
            ((Graphics) g).setFont(font.deriveFont(getFontSize()));
        }

        FontMetrics fontMetrics = ((Graphics) g).getFontMetrics();

        // this code below is to pin the barcode to the top right (for the marker type barcodes)
        PageAttributes currentPageAttributes = getPageAttributes();

        int croppedWidth = currentPageAttributes.getCroppedWidth();
        int croppedHeight = currentPageAttributes.getCroppedHeight();

        int padding = Math.round(getBoxWeight());

        double arcWidth;
        double arcHeight;

        if (boxWidth > boxHeight) {

            arcWidth = ((double) widthRoundness / 100.0d) * (double) boxHeight;
            arcHeight = ((double) heightRoundness / 100.0d) * (double) boxHeight;

        } else {

            arcWidth = ((double) widthRoundness / 100.0d) * (double) boxWidth;
            arcHeight = ((double) heightRoundness / 100.0d) * (double) boxWidth;

        }

        int columnCount = getColumnCount();
        int rowCount = getRowCount();

        _w = (boxWidth * columnCount) + (horizontalSpace * (columnCount - 1)) + (padding * 2);
        _h = (boxHeight * rowCount) + (verticalSpace * (rowCount - 1)) + (padding * 2);


        if ((getWidth() + _x) > croppedWidth) {
            setX(croppedWidth - getWidth());
        }

        if ((getHeight() + _y) > croppedHeight) {
            setY(croppedHeight - getHeight());
        }

        double boxWidthDouble = (double) boxWidth;

        for (int rowNumber = 1; rowNumber <= rowCount; rowNumber++) {

            for (int columnNumber = 1; columnNumber <= columnCount; columnNumber++) {

                String text = checkboxValues[rowNumber - 1][columnNumber - 1];

                final Rectangle2D bounds = fontMetrics.getStringBounds(text, (Graphics) g);

                double baseline = fontMetrics.getMaxAscent() + (
                    ((float) boxHeight - (fontMetrics.getAscent() + fontMetrics.getMaxDescent()))
                        / 2.0d);

                float x = getX() + padding + (boxWidth * (columnNumber - 1)) + (horizontalSpace * (
                    columnNumber - 1));
                float y =
                    getY() + padding + (boxHeight * (rowNumber - 1)) + (verticalSpace * (rowNumber
                        - 1));

                double inset = (boxWidthDouble / 2.0d) - (bounds.getWidth() / 2.0d);

                float characterX = x + (float) inset;
                float characterY = y + (float) baseline;

                ((Graphics) g).setColor(Color.BLACK);
                Stroke originalStroke = ((Graphics2D) g).getStroke();
                ((Graphics2D) g).setStroke(getStroke());
                ((Graphics) g).drawRoundRect(Math.round(x), Math.round(y), boxWidth, boxHeight,
                    (int) Math.round(arcWidth), (int) Math.round(arcHeight));
                ((Graphics2D) g).setStroke(originalStroke);
                ((Graphics) g).setColor(new Color(fontDarkness, fontDarkness, fontDarkness));

                if (isShowText()) {
                    ((Graphics2D) g).drawString(text, characterX, characterY);
                }
                ((Graphics) g).setColor(Color.black);

            }
        }
    }

    public void addRecognitionStructure(SegmentRecognitionStructure segmentRecognitionStructure) {

        int padding = Math.round(getBoxWeight());
        int columnCount = getColumnCount();
        int rowCount = getRowCount();

        OMRRecognitionStructure fragmentRecognitionStructure = new OMRRecognitionStructure();
        fragmentRecognitionStructure.setX(_x - padding);
        fragmentRecognitionStructure.setY(_y - padding);
        fragmentRecognitionStructure.setWidth(_w + (padding * 2));
        fragmentRecognitionStructure.setHeight(_h + (padding * 2));

        fragmentRecognitionStructure
            .setCheckBoxRecognitionStructures(getCheckBoxRecognitionStructures());
        fragmentRecognitionStructure.setRowCount(rowCount);
        fragmentRecognitionStructure.setColumnCount(columnCount);

        fragmentRecognitionStructure.setMarkFieldName(getMarkFieldname());
        fragmentRecognitionStructure.setMarkOrderIndex(getMarkFieldnameOrderIndex());
        fragmentRecognitionStructure.setOrderIndex(getFieldnameOrderIndex());
        fragmentRecognitionStructure.calculateX(segmentRecognitionStructure.getWidth());
        fragmentRecognitionStructure.setAggregationRule(getAggregationRule());
        fragmentRecognitionStructure.setReadDirection(getReadDirection());

        fragmentRecognitionStructure.calculateY(segmentRecognitionStructure.getHeight());
        fragmentRecognitionStructure.setFieldName(fieldname);
        fragmentRecognitionStructure.setCombineColumnCharacters(combineColumnCharacters);
        fragmentRecognitionStructure.setReconciliationKey(reconciliationKey);

        segmentRecognitionStructure.addFragment(fieldname, fragmentRecognitionStructure);

    }

    public int getReadDirection() {
        return readDirection;
    }

    public void setReadDirection(int readDirection) {
        this.readDirection = readDirection;
    }

    public ArrayList<CheckBoxRecognitionStructure> getCheckBoxRecognitionStructures() {

        int padding = Math.round(getBoxWeight());
        int columnCount = getColumnCount();
        int rowCount = getRowCount();

        int maxXCentroid = 0;
        int maxYCentroid = 0;
        int minYCentroid = Integer.MAX_VALUE;
        int minXCentroid = Integer.MAX_VALUE;

        int[][][] centroidPoints = new int[rowCount][columnCount][2]; // 2 = X,Y
        ArrayList<CheckBoxRecognitionStructure> checkBoxRecognitionStructures =
            new ArrayList<CheckBoxRecognitionStructure>();

        for (int rowNumber = 0; rowNumber < rowCount; rowNumber++) {

            for (int columnNumber = 0; columnNumber < columnCount; columnNumber++) {

                int x = padding + (boxWidth * columnNumber) + (horizontalSpace * columnNumber);
                int y = padding + (boxHeight * rowNumber) + (verticalSpace * rowNumber);

                int xCentroid = (int) (x + Math.round(((double) boxWidth) / 2.0d));
                centroidPoints[rowNumber][columnNumber][0] = xCentroid;
                if (xCentroid < minXCentroid) {
                    minXCentroid = xCentroid;
                }
                if (maxXCentroid < xCentroid) {
                    maxXCentroid = xCentroid;
                }

                int yCentroid = (int) (y + Math.round(((double) boxHeight) / 2.0d));
                centroidPoints[rowNumber][columnNumber][1] = yCentroid;
                if (yCentroid < minYCentroid) {
                    minYCentroid = yCentroid;
                }
                if (maxYCentroid < yCentroid) {
                    maxYCentroid = yCentroid;
                }

            }
        }

        int distance = 0;
        if ((maxXCentroid - minXCentroid) > (maxYCentroid - minYCentroid)) {
            distance = maxXCentroid - minXCentroid;
        } else {
            distance = maxYCentroid - minYCentroid;
        }

        for (int rowNumber = 0; rowNumber < rowCount; rowNumber++) {

            for (int columnNumber = 0; columnNumber < columnCount; columnNumber++) {

                int xCentroid = centroidPoints[rowNumber][columnNumber][0];
                int yCentroid = centroidPoints[rowNumber][columnNumber][1];

                double xDivisor = ((double) xCentroid - (double) minXCentroid);
                double yDivisor = ((double) yCentroid - (double) minYCentroid);

                double scaledYCentroid = 0.0d;
                double scaledXCentroid = 0.0d;
                if (xDivisor != 0) {
                    scaledXCentroid = (xDivisor / ((double) distance));
                }
                if (yDivisor != 0) {
                    scaledYCentroid = (yDivisor / ((double) distance));
                }

                CheckBoxRecognitionStructure cbrs = new CheckBoxRecognitionStructure();

                cbrs.setFragmentXRatio(scaledXCentroid);
                cbrs.setFragmentYRatio(scaledYCentroid);
                cbrs.setRow((short) rowNumber);
                cbrs.setColumn((short) columnNumber);
                cbrs.setCheckBoxValue(checkboxValues[rowNumber][columnNumber]);

                checkBoxRecognitionStructures.add(cbrs);
            }

        }

        return checkBoxRecognitionStructures;

    }

    @Override public void postLoad() {
        CachedFontManager cachedFontManager = Main.getCachedFontManager();
        Font fontNameFont = null;
        try {
            fontNameFont = cachedFontManager.getFont(fontName);
        } catch (Exception ex) {
        }
        if (fontNameFont != null) {
            setFont(fontNameFont.deriveFont(fontSize));
        } else {
            Exception ex =
                new Exception("Font Not Found - " + fontName + ". Replacing with default font.");
            Font defaultFont = cachedFontManager.getDefaultFont();
            Main.applicationExceptionLog.error(String
                .format(Localizer.localize("GefBase", "CheckboxUnableToFindFontMessage"), fontName,
                    defaultFont.getFontName()), ex);
            setFont(defaultFont.deriveFont(fontSize));
        }
        damage();
    }

    public CheckboxMemento getUpdateMemento() {

        CheckboxMemento memento = null;

        if (UndoManager.getInstance().isGenerateMementos()) {
            UndoManager.getInstance().startChain();
            memento = new CheckboxMemento(this);
            UndoManager.getInstance().addMemento(memento);
        }

        memento.setOldCheckboxValues(getCheckboxValues());

        return memento;

    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public String[][] getCheckboxValues() {
        return checkboxValues;
    }

    public void setCheckboxValues(String[][] checkboxValues) {
        this.checkboxValues = checkboxValues;
    }

    @Override public EditorPanel getEditorPanel() {
        return new FigCheckboxPanel();
    }

    @Override public EditorMultiPanel getEditorMultiPanel() {
        return new FigCheckboxMultiPanel();
    }

    public void setWidthRoundness(int widthRoundness) {
        this.widthRoundness = widthRoundness;
    }

    public int getWidthRoundness() {
        return this.widthRoundness;
    }

    public int getHeightRoundness() {
        return heightRoundness;
    }

    public void setHeightRoundness(int heightRoundness) {
        this.heightRoundness = heightRoundness;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public float getFontSize() {
        return fontSize;
    }

    public int getFontDarkness() {
        return fontDarkness;
    }

    public void setFontDarkness(int fontDarkness) {
        this.fontDarkness = fontDarkness;
    }

    public String getAggregationRule() {
        return aggregationRule;
    }

    public void setAggregationRule(String aggregationRule) {
        this.aggregationRule = aggregationRule;
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) {
            FigCheckboxProperties fcp = new FigCheckboxProperties(this);
            fcp.setModal(true);
            fcp.setVisible(true);
            fcp.updatePreview();
            if (fcp.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {
                // TODO: don't know what though.
            }
            fcp.dispose();
            e.consume();
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

    public boolean isCombineColumnCharacters() {
        return combineColumnCharacters;
    }

    public void setCombineColumnCharacters(boolean combineColumnCharacters) {
        this.combineColumnCharacters = combineColumnCharacters;
    }

    public boolean isReconciliationKey() {
        return reconciliationKey;
    }

    public void setReconciliationKey(boolean reconciliationKey) {
        this.reconciliationKey = reconciliationKey;
    }

    public String getMarkFieldname() {
        return markFieldname;
    }

    public void setMarkFieldname(String markFieldname) {
        this.markFieldname = markFieldname;
    }

    public int getFieldnameOrderIndex() {
        return fieldnameOrderIndex;
    }

    public void setFieldnameOrderIndex(int fieldnameOrderIndex) {
        this.fieldnameOrderIndex = fieldnameOrderIndex;
    }

    public int getMarkFieldnameOrderIndex() {
        return markFieldnameOrderIndex;
    }

    public void setMarkFieldnameOrderIndex(int markFieldnameOrderIndex) {
        this.markFieldnameOrderIndex = markFieldnameOrderIndex;
    }

    public String getColumnReadDirection() {
        if (this.readDirection == READ_STRING_TOP_TO_BOTTOM) {
            return "TB";
        } else {
            return "LR";
        }
    }

    public void setColumnReadDirection(String direction) {
        if (direction.equalsIgnoreCase("TB")) {
            setReadDirection(READ_STRING_TOP_TO_BOTTOM);
        } else {
            setReadDirection(READ_STRING_LEFT_TO_RIGHT);
        }
    }

}

package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebstrada.formreturn.manager.gef.font.CachedFontManager;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.persistence.ExportAttributes;
import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.gef.undo.memento.BarcodeReaderMemento;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.reader.BarcodeReaderTypes;
import com.ebstrada.formreturn.manager.logic.recognition.structure.BarcodeRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FormRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.SegmentRecognitionStructure;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.dialog.FigBarcodeReaderProperties;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.FigBarcodeReaderPanel;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("barcodeReader") public class FigBarcodeReader extends Fig
    implements NoObfuscation, MouseListener, RecognitionStructureFig {

    private static final long serialVersionUID = 1L;

    private transient static Log LOG = LogFactory.getLog(FigBarcodeReader.class);

    private transient Font font;

    private boolean reconciliationKey = false;

    private String fieldname = Localizer.localize("UI", "BarcodeFieldnamePrefix");

    private int fieldnameOrderIndex = 0;

    private boolean showText = true;

    private boolean showCorners = true;

    private int cornerDivisor = 4;

    private String barcodeAreaText = Localizer.localize("UI", "BarcodeReaderArea");

    private int barcodeType = BarcodeReaderTypes.AUTO_DETECT;

    public FigBarcodeReader(int x, int y, int w, int h) {
        super(x, y, w, h);
        setDefaultFont();
        setDefaultFieldname();
    }

    @Override public EditorPanel getEditorPanel() {
        return new FigBarcodeReaderPanel();
    }

    public void setFont(Font font) {
        this.font = font;
    }

    private void setDefaultFont() {
        CachedFontManager cachedFontManager = Main.getCachedFontManager();
        Font defaultFont = cachedFontManager.getDefaultFont();
        setFont(defaultFont.deriveFont(10.0f));
    }

    public void setDefaultFieldname() {
        JGraph graph = getGraph();
        DocumentAttributes documentAttributes = graph.getDocumentAttributes();
        String defaultCDFN = documentAttributes.getDefaultBarcodeCapturedDataFieldname();
        int defaultCDFNIncrementor = documentAttributes.getDefaultCDFNIncrementor();
        setDefaultFieldname(defaultCDFN, defaultCDFNIncrementor, false);
        setNextOrderIndex();
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
                        break;
                    }

                }


            }
        }

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
                    }
                }

                setFieldnameOrderIndex(maximumOrderIndex + 1);

            }
        }

    }

    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() > 1) {
            e.consume();
            FigBarcodeReaderProperties fbrp = new FigBarcodeReaderProperties(this);
            fbrp.setTitle(Localizer.localize("UI", "BarcodeReaderAreaPropertiesDialogTitle"));
            fbrp.setModal(true);
            fbrp.setVisible(true);
            fbrp.dispose();
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

    public boolean isReconciliationKey() {
        return reconciliationKey;
    }

    public void setReconciliationKey(boolean reconciliationKey) {
        this.reconciliationKey = reconciliationKey;
    }

    public int getFieldnameOrderIndex() {
        return fieldnameOrderIndex;
    }

    public void setFieldnameOrderIndex(int fieldnameOrderIndex) {
        this.fieldnameOrderIndex = fieldnameOrderIndex;
    }

    public BarcodeReaderMemento getUpdateMemento() {

        BarcodeReaderMemento memento = null;

        if (UndoManager.getInstance().isGenerateMementos()) {
            UndoManager.getInstance().startChain();
            memento = new BarcodeReaderMemento(this);
            UndoManager.getInstance().addMemento(memento);
        }

        return memento;

    }

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    @Override public Object clone() {
        Object obj = super.clone();

        /*
         * get a unique fieldname on paste
         */
        if (obj instanceof FigBarcodeReader) {
            ((FigBarcodeReader) obj).setDefaultFieldname();
        }

        return obj;
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

        ((Graphics) g).setColor(Color.black);

        if (isShowCorners()) {

            int lineLength = 2;
            if (_w < _h) {
                lineLength = _w / cornerDivisor;
            } else {
                lineLength = _h / cornerDivisor;
            }

            // top left going down
            int x1 = getX();
            int y1 = getY();
            int x2 = getX();
            int y2 = getY() + lineLength;

            ((Graphics) g).drawLine(x1, y1, x2, y2);

            // top left going right
            x1 = getX();
            y1 = getY();
            x2 = getX() + lineLength;
            y2 = getY();

            ((Graphics) g).drawLine(x1, y1, x2, y2);

            // top right going left
            x1 = getX() + _w;
            y1 = getY();
            x2 = getX() + _w - lineLength;
            y2 = getY();

            ((Graphics) g).drawLine(x1, y1, x2, y2);

            // top right going down
            x1 = getX() + _w;
            y1 = getY();
            x2 = getX() + _w;
            y2 = getY() + lineLength;

            ((Graphics) g).drawLine(x1, y1, x2, y2);

            // bottom left going up
            x1 = getX();
            y1 = getY() + _h;
            x2 = getX();
            y2 = getY() + _h - lineLength;

            ((Graphics) g).drawLine(x1, y1, x2, y2);

            // bottom left going right
            x1 = getX();
            y1 = getY() + _h;
            x2 = getX() + lineLength;
            y2 = getY() + _h;

            ((Graphics) g).drawLine(x1, y1, x2, y2);

            // bottom right going left
            x1 = getX() + _w;
            y1 = getY() + _h;
            x2 = getX() + _w - lineLength;
            y2 = getY() + _h;

            ((Graphics) g).drawLine(x1, y1, x2, y2);

            // bottom right going up
            x1 = getX() + _w;
            y1 = getY() + _h;
            x2 = getX() + _w;
            y2 = getY() + _h - lineLength;

            ((Graphics) g).drawLine(x1, y1, x2, y2);

        }

        if (isShowText()) {

            String character = barcodeAreaText;
            int characterWidth = fontMetrics.stringWidth(character);
            int characterHeight = fontMetrics.getHeight();

            int characterX = getX() + (_w / 2) - (characterWidth / 2);
            int characterY = getY() + (_h / 2) + (characterHeight / 2);

            if (getWidth() > characterWidth && getHeight() > characterHeight) {

                ((Graphics) g).drawString(character, characterX, characterY);

            }

        }

    }

    public void addRecognitionStructure(SegmentRecognitionStructure segmentRecognitionStructure) {

        BarcodeRecognitionStructure fragmentRecognitionStructure =
            new BarcodeRecognitionStructure();
        fragmentRecognitionStructure.setBarcodeType(barcodeType);
        fragmentRecognitionStructure.setX(_x);
        fragmentRecognitionStructure.setY(_y);
        fragmentRecognitionStructure.setWidth(_w);
        fragmentRecognitionStructure.setHeight(_h);
        fragmentRecognitionStructure.setOrderIndex(getFieldnameOrderIndex());
        fragmentRecognitionStructure.calculateX(segmentRecognitionStructure.getWidth());
        fragmentRecognitionStructure.calculateY(segmentRecognitionStructure.getHeight());
        fragmentRecognitionStructure.setFieldName(fieldname);
        fragmentRecognitionStructure.setReconciliationKey(reconciliationKey);

        segmentRecognitionStructure.addFragment(fieldname, fragmentRecognitionStructure);

    }

    public int getCornerDivisor() {
        return cornerDivisor;
    }

    public void setCornerDivisor(int cornerDivisor) {
        this.cornerDivisor = cornerDivisor;
    }

    public String getBarcodeAreaText() {
        return barcodeAreaText;
    }

    public void setBarcodeAreaText(String barcodeAreaText) {
        this.barcodeAreaText = barcodeAreaText;
    }

    public boolean isShowCorners() {
        return showCorners;
    }

    public void setShowCorners(boolean showCorners) {
        this.showCorners = showCorners;
    }

    public int getBarcodeType() {
        return barcodeType;
    }

    public void setBarcodeType(int barcodeType) {
        this.barcodeType = barcodeType;
    }

}

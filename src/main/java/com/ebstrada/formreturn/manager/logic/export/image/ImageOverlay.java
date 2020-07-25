package com.ebstrada.formreturn.manager.logic.export.image;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.export.Column;
import com.ebstrada.formreturn.manager.logic.export.ExportMap;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.Grading;
import com.ebstrada.formreturn.manager.persistence.jpa.ProcessedImage;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.persistence.jpa.Record;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.image.ImageUtil;

public class ImageOverlay {

    private FormPage formPage;
    private PDPageContentStream contentStream;
    private PDDocument doc;
    private ImageExportPage imageExportPage;
    private float y = 0f;
    private ImageExportPreferences imageExportAttributes;

    private final float cellMargin = 5f;
    private float rowHeight = 20f;

    public ImageOverlay(FormPage formPage, PDDocument doc, PDRectangle pageRectangle,
        ImageExportPreferences imageExportAttributes) throws IOException {
        this.formPage = formPage;
        this.doc = doc;
        this.imageExportAttributes = imageExportAttributes;
        PDPage page = new PDPage(pageRectangle);
        doc.addPage(page);
        this.contentStream = new PDPageContentStream(doc, page);
    }

    public float getLeftMargin() {
        return this.imageExportAttributes.getSizeAttributes().getLeftMargin();
    }

    public float getRightMargin() {
        return this.imageExportAttributes.getSizeAttributes().getRightMargin();
    }

    public float getTopMargin() {
        return this.imageExportAttributes.getSizeAttributes().getTopMargin();
    }

    public float getBottomMargin() {
        return this.imageExportAttributes.getSizeAttributes().getBottomMargin();
    }

    private String format(double value) {
        NumberFormat format = DecimalFormat.getInstance();
        format.setRoundingMode(RoundingMode.FLOOR);
        format.setMinimumFractionDigits(0);
        format.setMaximumFractionDigits(8);
        return format.format(value);
    }

    public void drawTable(String[][] content) throws IOException {

        final int cols = content[0].length;

        final float tableWidth =
            this.imageExportPage.getPageRectangle().getWidth() - (getLeftMargin()
                + getRightMargin());
        final float colWidth = tableWidth / (float) cols;

        this.contentStream
            .setFont(PDType1Font.HELVETICA, this.imageExportAttributes.getColumnFontSize());

        float textx = getLeftMargin() + cellMargin;
        this.y -= 15;
        for (int i = 0; i < content.length; i++) {
            for (int j = 0; j < content[i].length; j++) {
                String text = content[i][j];
                this.contentStream.beginText();
                this.contentStream.moveTextPositionByAmount(textx, this.y);
                if (text == null) {
                    text = "";
                }
                this.contentStream.drawString(text);
                this.contentStream.endText();
                textx += colWidth;
            }
            advanceRow();
            textx = getLeftMargin() + cellMargin;
        }
    }

    public void drawImage(ProcessedImage processedImage, PDRectangle pageRectangle)
        throws Exception {

        this.rowHeight = 20f;

        byte[] data = processedImage.getProcessedImageData();
        BufferedImage img = ImageUtil.readImage(data, 1);
        img.flush();
        imageExportPage =
            new ImageExportPage(img, pageRectangle, this.imageExportAttributes.getSizeAttributes());
        if (this.imageExportAttributes.isImageRotated()) {
            imageExportPage.rotate();
        }
        PDImageXObject ximage =
            LosslessFactory.createFromImage(this.doc, imageExportPage.getImage());
        this.y = imageExportPage.getLowerLeftY() - getTopMargin();
        if (this.y < 0) {
            this.y =
                (this.imageExportPage.getPageRectangle().getHeight() - imageExportPage.getHeight())
                    - getTopMargin();
        }
        this.contentStream
            .drawImage(ximage, imageExportPage.getLowerLeftX(), this.y, imageExportPage.getWidth(),
                imageExportPage.getHeight());

        this.y = (imageExportPage.getPageRectangle().getHeight() - imageExportPage.getHeight())
            - getTopMargin();

    }

    public void drawHeader() throws IOException {

        if ((this.y - 100f) <= 0) {
            newPage();
            return;
        }

        this.rowHeight = 20f;

        Publication publication = formPage.getFormId().getPublicationId();
        String publicationName = publication.getPublicationName();
        long publicationId = publication.getPublicationId();

        this.contentStream.drawLine(getLeftMargin(), y,
            this.imageExportPage.getPageRectangle().getWidth() - getRightMargin(), this.y);

        float textx = getLeftMargin() + cellMargin;
        this.y -= 15;

        this.contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

        this.contentStream.beginText();
        this.contentStream.moveTextPositionByAmount(textx, this.y);
        this.contentStream.drawString(publicationName + " (" + String
            .format(Localizer.localize("Util", "ExportPageNumberMessage"),
                formPage.getFormPageNumber()) + ")");
        this.contentStream.endText();

        this.y -= rowHeight;

        this.contentStream.setFont(PDType1Font.HELVETICA, 7);

        this.contentStream.beginText();
        this.contentStream.moveTextPositionByAmount(textx, this.y);
        String identifierMsg = Localizer.localize("Util", "ExportIdentifierMessage");
        this.contentStream.drawString(String
            .format(identifierMsg, publicationId, formPage.getFormId().getFormId(),
                formPage.getFormPageId()));
        this.contentStream.endText();

        this.y -= rowHeight;

        this.contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);

        String str = "";

        if (this.imageExportAttributes.getOverlay().contains(Overlay.GRADES)) {
            str += getGrading();
        }

        if (this.imageExportAttributes.getOverlay().contains(Overlay.FORM_SCORE)) {
            str += Localizer.localize("Util", "FormScoreExportMessage") + ": " + format(
                formPage.getFormId().getAggregateMark());
        }

        if (this.imageExportAttributes.getOverlay().contains(Overlay.PAGE_SCORE)) {
            str += "   (" + Localizer.localize("Util", "PageScoreExportMessage") + ": " + format(
                formPage.getAggregateMark()) + ")";
        }

        if (str.length() > 0) {

            this.contentStream.beginText();
            this.contentStream.moveTextPositionByAmount(textx, this.y);
            this.contentStream.drawString(str);
            this.contentStream.endText();

            this.y -= 10f;

        }

        this.contentStream.drawLine(getLeftMargin(), y,
            this.imageExportPage.getPageRectangle().getWidth() - getRightMargin(), this.y);

    }

    private void advanceRow() throws IOException {
        if ((this.y - (rowHeight + getBottomMargin())) <= 0) {
            newPage();
        } else {
            this.y -= rowHeight;
        }
    }

    private void newPage() throws IOException {
        this.contentStream.close();
        PDPage page = new PDPage(imageExportPage.getPageRectangle());
        doc.addPage(page);
        this.contentStream = new PDPageContentStream(doc, page);
        this.y = imageExportPage.getPageRectangle().getHeight() - getTopMargin();
        drawHeader();
        this.y -= 15f;
        this.rowHeight = this.imageExportAttributes.getColumnFontSize() + 2f;
        this.contentStream
            .setFont(PDType1Font.HELVETICA, this.imageExportAttributes.getColumnFontSize());
    }

    private String getGrading() {
        List<Grading> gradings = formPage.getFormId().getPublicationId().getGradingCollection();
        if (gradings == null || gradings.size() <= 0) {
            return "";
        }
        double aggregate = formPage.getFormId().getAggregateMark();
        Grading grading = gradings.get(gradings.size() - 1);
        double percentage = (aggregate / grading.getTotalPossibleScore()) * 100.0d;
        String grade = Misc.getGrading(aggregate, grading.getGradingRuleCollection(),
            grading.getTotalPossibleScore());
        return Localizer.localize("Util", "GradeExportMessage") + ": " + grade + " (" + format(
            percentage) + "%) - ";
    }

    public void drawSourceData(EntityManager entityManager) throws IOException {

        this.rowHeight = this.imageExportAttributes.getColumnFontSize() + 2f;

        Record record = formPage.getFormId().getRecordId();
        Map<String, String> sourceData = Misc.getSourceData(entityManager, record);

        int columnCount = this.imageExportAttributes.getSourceDataColumnCount();

        double columns = columnCount;
        double recordCount = sourceData.size();

        int rowCount = (int) Math.ceil(recordCount / columns);

        String[][] content = new String[rowCount][columnCount];

        int row = 0;
        int column = 0;
        for (String fieldname : sourceData.keySet()) {
            if (column > 0 && ((column % columnCount) == 0)) {
                column = 0;
                ++row;
            }
            String str = fieldname + ": " + sourceData.get(fieldname);
            content[row][column] = str;
            ++column;
        }

        drawTable(content);

    }

    public void drawCapturedData(EntityManager entityManager) throws IOException {

        this.rowHeight = this.imageExportAttributes.getColumnFontSize() + 2f;

        final int exportRowNumber = 0;

        ExportMap exportMap = new ExportMap();
        HashMap<String, Double> marks = new HashMap<String, Double>();
        exportMap.setSortType(ExportMap.SORT_BY_ORDER_INDEX);
        exportMap.setSize(exportRowNumber + 1);
        Misc.getCapturedData(formPage, exportMap, exportRowNumber, marks);
        List<Column> sortedColumns = exportMap.getSortedData();

        int columnCount = this.imageExportAttributes.getCapturedDataColumnCount();

        double columns = columnCount;
        double recordCount = sortedColumns.size();

        int rowCount = (int) Math.ceil(recordCount / columns);

        String[][] content = new String[rowCount][columnCount];

        int row = 0;
        int column = 0;
        for (Column col : sortedColumns) {
            if (column > 0 && ((column % columnCount) == 0)) {
                column = 0;
                ++row;
            }
            String str = col.getFieldname() + ": " + col.getColumnValue(exportRowNumber);

            if (this.imageExportAttributes.getOverlay().contains(Overlay.INDIVIDUAL_SCORES)) {
                if (marks.containsKey(col.getFieldname())) {
                    double mark = marks.get(col.getFieldname());
                    if ((mark > 0d) || (mark < 0d)) {
                        if (mark > 0) {
                            str += " (+" + format(mark) + ")";
                        } else {
                            str += " (" + format(mark) + ")";
                        }
                    } else {
                        str += " (0)";
                    }
                } else {
                    // ignore outputting scores for barcodes, etc.
                    // System.out.println(col.getFieldname());
                }
            }
            content[row][column] = str;
            ++column;
        }

        drawTable(content);

    }

    public void closeContentStream() throws IOException {
        this.contentStream.close();
    }

}

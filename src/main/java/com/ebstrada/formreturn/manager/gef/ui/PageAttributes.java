package com.ebstrada.formreturn.manager.gef.ui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.Serializable;

import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.ebstrada.formreturn.manager.util.RandomGUID;
import com.ebstrada.formreturn.manager.util.graph.GraphUtils;
import com.thoughtworks.xstream.annotations.XStreamAlias;

public class PageAttributes implements NoObfuscation {

    private static final long serialVersionUID = 1L;

    public static final int PORTRAIT = 1;

    public static final int LANDSCAPE = 2;

    public static final boolean RECOGNITON_BARCODES_OFF = false;

    public static final boolean RECOGNITON_BARCODES_ON = true;

    public static final int SEGMENT_BARCODE_HEIGHT = 28;

    @XStreamAlias("hasRecognitionBarcodes") private boolean recognitionBarcodes;

    private int orientation;

    private Dimension dimension;

    private int leftMargin = 0;

    private int rightMargin = 0;

    private int topMargin = 0;

    private int bottomMargin = 0;

    private int croppedWidth = 0;

    private int croppedHeight = 0;

    private String pageSize;

    private String GUID = (new RandomGUID()).toString();

    private int barcodeOneValue = 1;

    private int barcodeTwoValue = 2;

    private transient long formPageId;

    private transient long formId;

    private transient long publicationId;

    private transient String formPassword;

    private double recognitionBarcodesScale = 0.6;

    private transient BufferedImage backgroundImage;

    public PageAttributes() {
        setOrientation(PageAttributes.PORTRAIT);
        setMargin(0, 0, 0, 0);
    }

    public void setBarcodeOneValue(int newBarcodeOneValue) {
        barcodeOneValue = newBarcodeOneValue;
    }

    public int getBarcodeOneValue() {
        return barcodeOneValue;
    }

    public void setBarcodeTwoValue(int newBarcodeTwoValue) {
        barcodeTwoValue = newBarcodeTwoValue;
    }

    public int getBarcodeTwoValue() {
        return barcodeTwoValue;
    }

    public String getGUID() {
        if (GUID == null) {
            newGUID();
        }
        return GUID;
    }

    public void newGUID() {
        GUID = (new RandomGUID()).toString();
    }

    public void setDimension(Dimension newDimension) {
        dimension = newDimension;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setMargin(int left, int right, int top, int bottom) {
        leftMargin = left;
        rightMargin = right;
        topMargin = top;
        bottomMargin = bottom;
    }

    public void setLeftMargin(int newLeftMargin) {
        leftMargin = newLeftMargin;
    }

    public int getLeftMargin() {
        return leftMargin;
    }

    public void setRightMargin(int newRightMargin) {
        rightMargin = newRightMargin;
    }

    public int getRightMargin() {
        return rightMargin;
    }

    public void setTopMargin(int newTopMargin) {
        topMargin = newTopMargin;
    }

    public int getTopMargin() {
        return topMargin;
    }

    public void setBottomMargin(int newBottomMargin) {
        bottomMargin = newBottomMargin;
    }

    public int getBottomMargin() {
        return bottomMargin;
    }

    public void setCroppedWidth(int newCroppedWidth) {
        croppedWidth = newCroppedWidth;
    }

    public int getCroppedWidth() {
        return croppedWidth;
    }

    public int getFullWidth() {
        return croppedWidth + leftMargin + rightMargin;
    }

    public void setCroppedHeight(int newCroppedHeight) {
        croppedHeight = newCroppedHeight;
    }

    public int getCroppedHeight() {
        return croppedHeight;
    }

    public int getFullHeight() {
        return croppedHeight + topMargin + bottomMargin;
    }

    public void setPageSize(String newPageSize) {
        pageSize = newPageSize;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setOrientation(int newOrientation) {
        switch (newOrientation) {
            case PageAttributes.PORTRAIT:
                orientation = PageAttributes.PORTRAIT;
                break;
            case PageAttributes.LANDSCAPE:
                orientation = PageAttributes.LANDSCAPE;
                break;
            default:
                orientation = PageAttributes.PORTRAIT;
        }
    }

    public int getOrientation() {
        return orientation;
    }

    public static String getOrientationText(int newOrientation) {

        String orientationText;

        switch (newOrientation) {
            case PageAttributes.PORTRAIT:
                orientationText = "Portrait";
                break;
            case PageAttributes.LANDSCAPE:
                orientationText = "Landscape";
                break;
            default:
                orientationText = "Portrait";
        }

        return orientationText;

    }

    public boolean hasRecognitionBarcodes() {
        return recognitionBarcodes;
    }

    public void setRecognitionBarcodes(boolean recognitionBarcodes) {
        this.recognitionBarcodes = recognitionBarcodes;
    }

    public PageAttributes duplicate() {

        PageAttributes duplicatePageAttributes = new PageAttributes();

        duplicatePageAttributes.setDimension(this.getDimension());
        duplicatePageAttributes.setPageSize(this.getPageSize());
        duplicatePageAttributes.setLeftMargin(this.getLeftMargin());
        duplicatePageAttributes.setRightMargin(this.getRightMargin());
        duplicatePageAttributes.setTopMargin(this.getTopMargin());
        duplicatePageAttributes.setBottomMargin(this.getBottomMargin());
        duplicatePageAttributes.setOrientation(this.getOrientation());
        duplicatePageAttributes.setCroppedWidth(this.getCroppedWidth());
        duplicatePageAttributes.setCroppedHeight(this.getCroppedHeight());

        return duplicatePageAttributes;
    }

    public void setFormPageId(long formPageId) {
        this.formPageId = formPageId;
    }

    public void setFormId(long formId) {
        this.formId = formId;
    }

    public void setPublicationId(long publicationId) {
        this.publicationId = publicationId;
    }

    public long getFormPageId() {
        return formPageId;
    }

    public long getFormId() {
        return formId;
    }

    public long getPublicationId() {
        return publicationId;
    }

    public void setFormPassword(String formPassword) {
        this.formPassword = formPassword;
    }

    public String getFormPassword() {
        return formPassword;
    }

    public void setRecognitionBarcodesScale(double recognitionBarcodesScale) {
        this.recognitionBarcodesScale = recognitionBarcodesScale;
    }

    public double getRecognitionBarcodesScale() {
        return recognitionBarcodesScale;
    }

    public void setBackgroundImage(BufferedImage sourceImage) {
        backgroundImage = sourceImage;
    }

    public BufferedImage getBackgroundImage() {
        return backgroundImage;
    }

}

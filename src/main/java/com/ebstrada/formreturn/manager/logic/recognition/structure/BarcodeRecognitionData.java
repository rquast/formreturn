package com.ebstrada.formreturn.manager.logic.recognition.structure;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.google.zxing.BarcodeFormat;

public class BarcodeRecognitionData {

    public static final int FORMID_BARCODE = 1;
    public static final int SEGMENT_BARCODE = 2;
    public static final int CODE128 = 3;

    private int orientation;

    private int type;

    private BarcodeFormat barcodeFormat;

    private Rectangle2D barcodeBoundary;

    private double X1;
    private double Y1;
    private double X2;
    private double Y2;

    private String value;
    private BufferedImage barcodeImage;

    public int getOrientation() {
        return orientation;
    }

    public String getOrientationDescription() {
        String description = String
            .format(Localizer.localize("UI", "BarcodeRecognitionOrientationRotation"),
                orientation + "");
        return description;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public Rectangle2D getBarcodeBoundary() {
        return barcodeBoundary;
    }

    public void setBarcodeBoundary(Rectangle2D barcodeBoundary) {
        this.barcodeBoundary = barcodeBoundary;
    }

    public double getX1() {
        return X1;
    }

    public void setX1(double x1) {
        X1 = x1;
    }

    public double getY1() {
        return Y1;
    }

    public void setY1(double y1) {
        Y1 = y1;
    }

    public double getX2() {
        return X2;
    }

    public void setX2(double x2) {
        X2 = x2;
    }

    public double getY2() {
        return Y2;
    }

    public void setY2(double y2) {
        Y2 = y2;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BarcodeFormat getBarcodeFormat() {
        return barcodeFormat;
    }

    public String getBarcodeFormatDescription() {
        return barcodeFormat.toString();
    }

    public void setBarcodeFormat(BarcodeFormat barcodeFormat) {
        this.barcodeFormat = barcodeFormat;
    }

    public void setBarcodeImage(BufferedImage barcodeImage) {
        this.barcodeImage = barcodeImage;
    }

    public BufferedImage getBarcodeImage() {
        return barcodeImage;
    }

    public int getType() {
        return type;
    }

    public String getTypeDescription() {
        String description = "";
        switch (type) {
            case FORMID_BARCODE:
                description =
                    Localizer.localize("UI", "BarcodeRecognitionFormIDBarcodeDescription");
                break;
            case SEGMENT_BARCODE:
                description =
                    Localizer.localize("UI", "BarcodeRecognitionSegmentBarcodeDescription");
                break;
            case CODE128:
                description =
                    Localizer.localize("UI", "BarcodeRecognitionCode128BarcodeDescription");
                break;
        }
        return description;
    }

    public void setType(int type) {
        this.type = type;
    }

}

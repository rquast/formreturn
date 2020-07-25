package com.ebstrada.formreturn.manager.logic.recognition.structure;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import com.ebstrada.formreturn.manager.logic.recognition.reader.BarcodeImageReader;

public class FragmentRecognitionData {

    public static final int OMR_FRAGMENT = 1;
    public static final int OCR_FRAGMENT = 2;
    public static final int ICR_FRAGMENT = 3;
    public static final int IMAGE_ZONE_FRAGMENT = 4;
    public static final int BARCODE_FRAGMENT = 5;
    public static final int DAMAGED_OMR_FRAGMENT = 6;

    private int type;

    private Rectangle2D fragmentBoundary;

    private OMRMatrix omrMatrix;

    private BarcodeImageReader barcodeImageReader;

    public Rectangle2D getFragmentBoundary() {
        return fragmentBoundary;
    }

    public void setFragmentBoundary(Rectangle2D fragmentBoundary) {
        this.fragmentBoundary = fragmentBoundary;
    }

    public OMRMatrix getOmrMatrix() {
        return omrMatrix;
    }

    public void setOmrMatrix(OMRMatrix omrMatrix) {
        this.omrMatrix = omrMatrix;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public BufferedImage getFragmentImage() {
        return omrMatrix.getFragmentImage();
    }

    public BarcodeImageReader getBarcodeImageReader() {
        return barcodeImageReader;
    }

    public void setBarcodeImageReader(BarcodeImageReader barcodeImageReader) {
        this.barcodeImageReader = barcodeImageReader;
    }

}

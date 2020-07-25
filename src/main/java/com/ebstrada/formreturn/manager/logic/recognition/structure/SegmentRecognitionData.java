package com.ebstrada.formreturn.manager.logic.recognition.structure;

import java.awt.geom.Rectangle2D;
import java.util.Vector;

public class SegmentRecognitionData {

    private BarcodeRecognitionData barcodeOne;

    private BarcodeRecognitionData barcodeTwo;

    private Rectangle2D segmentBoundary;

    private Vector<FragmentRecognitionData> fragmentRecognitionData =
        new Vector<FragmentRecognitionData>();

    public BarcodeRecognitionData getBarcodeOne() {
        return barcodeOne;
    }

    public void setBarcodeOne(BarcodeRecognitionData barcodeOne) {
        this.barcodeOne = barcodeOne;
    }

    public BarcodeRecognitionData getBarcodeTwo() {
        return barcodeTwo;
    }

    public void setBarcodeTwo(BarcodeRecognitionData barcodeTwo) {
        this.barcodeTwo = barcodeTwo;
    }

    public Rectangle2D getSegmentBoundary() {
        return segmentBoundary;
    }

    public void setSegmentBoundary(Rectangle2D segmentBoundary) {
        this.segmentBoundary = segmentBoundary;
    }

    public Vector<FragmentRecognitionData> getFragmentRecognitionData() {
        return fragmentRecognitionData;
    }

    public void setFragmentRecognitionData(
        Vector<FragmentRecognitionData> fragmentRecognitionData) {
        this.fragmentRecognitionData = fragmentRecognitionData;
    }

}

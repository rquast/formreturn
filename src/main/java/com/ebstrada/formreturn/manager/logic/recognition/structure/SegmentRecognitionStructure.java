package com.ebstrada.formreturn.manager.logic.recognition.structure;

import java.util.HashMap;
import java.util.Map;

public class SegmentRecognitionStructure {

    private int x;
    private int y;
    private int width;
    private int height;

    private int barcodeOneValue = 0;
    private int barcodeTwoValue = 0;

    private Map<String, BarcodeRecognitionStructure> BarcodeRecognitionStructures =
        new HashMap<String, BarcodeRecognitionStructure>();
    private Map<String, OMRRecognitionStructure> OMRRecognitionStructures =
        new HashMap<String, OMRRecognitionStructure>();
    private Map<String, OCRRecognitionStructure> OCRRecognitionStructures =
        new HashMap<String, OCRRecognitionStructure>();
    private long pageNumber = 0;
    private String name;

    public SegmentRecognitionStructure() {
    }

    public void setX(int _x) {
        x = _x;
    }

    public void setY(int _y) {
        y = _y;
    }

    public void setWidth(int _width) {
        width = _width;
    }

    public void setHeight(int _height) {
        height = _height;
    }

    public int getX(int _x) {
        return x;
    }

    public int getY(int _y) {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Map<String, OMRRecognitionStructure> getOMRRecognitionStructures() {
        return OMRRecognitionStructures;
    }

    public Map<String, OCRRecognitionStructure> getOCRRecognitionStructures() {
        return OCRRecognitionStructures;
    }

    public void addFragment(String fieldname,
        FragmentRecognitionStructure fragmentRecognitionStructure) {
        if (fragmentRecognitionStructure instanceof OMRRecognitionStructure) {
            OMRRecognitionStructures
                .put(fieldname, (OMRRecognitionStructure) fragmentRecognitionStructure);
        } else if (fragmentRecognitionStructure instanceof OCRRecognitionStructure) {
            OCRRecognitionStructures
                .put(fieldname, (OCRRecognitionStructure) fragmentRecognitionStructure);
        } else if (fragmentRecognitionStructure instanceof BarcodeRecognitionStructure) {
            BarcodeRecognitionStructures
                .put(fieldname, (BarcodeRecognitionStructure) fragmentRecognitionStructure);
        }
    }

    public int getBarcodeOneValue() {
        return barcodeOneValue;
    }

    public void setBarcodeOneValue(int barcodeOneValue) {
        this.barcodeOneValue = barcodeOneValue;
    }

    public int getBarcodeTwoValue() {
        return barcodeTwoValue;
    }

    public void setBarcodeTwoValue(int barcodeTwoValue) {
        this.barcodeTwoValue = barcodeTwoValue;
    }

    public long getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(long pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Map<String, BarcodeRecognitionStructure> getBarcodeRecognitionStructures() {
        return BarcodeRecognitionStructures;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}

package com.ebstrada.formreturn.manager.logic.recognition.structure;

import java.util.TreeMap;
import java.util.Vector;

public class PageRecognitionData {

    private BarcodeRecognitionData formIDBarcode;

    private TreeMap<Integer, BarcodeRecognitionData> segmentBarcodes =
        new TreeMap<Integer, BarcodeRecognitionData>();

    private Vector<BarcodeRecognitionData> code128Barcodes = new Vector<BarcodeRecognitionData>();

    private Vector<SegmentRecognitionData> segmentRecognitionData =
        new Vector<SegmentRecognitionData>();

    public Vector<SegmentRecognitionData> getSegmentRecognitionData() {
        return segmentRecognitionData;
    }

    public void setSegmentRecognitionData(Vector<SegmentRecognitionData> segmentRecognitionData) {
        this.segmentRecognitionData = segmentRecognitionData;
    }

    public BarcodeRecognitionData getFormIDBarcode() {
        return formIDBarcode;
    }

    public void setFormIDBarcode(BarcodeRecognitionData formIDBarcode) {
        this.formIDBarcode = formIDBarcode;
    }

    public TreeMap<Integer, BarcodeRecognitionData> getSegmentBarcodes() {
        return segmentBarcodes;
    }

    public Vector<BarcodeRecognitionData> getCode128Barcodes() {
        return code128Barcodes;
    }

    public void setCode128Barcodes(Vector<BarcodeRecognitionData> code128Barcodes) {
        this.code128Barcodes = code128Barcodes;
    }

    public void setSegmentBarcodes(TreeMap<Integer, BarcodeRecognitionData> segmentBarcodes) {
        this.segmentBarcodes = segmentBarcodes;
    }

}

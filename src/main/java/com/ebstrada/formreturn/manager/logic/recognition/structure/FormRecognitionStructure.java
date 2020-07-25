package com.ebstrada.formreturn.manager.logic.recognition.structure;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class FormRecognitionStructure {

    private Map<Integer, SegmentRecognitionStructure> segmentRecognitionStructures;

    private int formPageID = 0;

    private Vector<String> fieldNames;

    public FormRecognitionStructure() {
        segmentRecognitionStructures = new HashMap<Integer, SegmentRecognitionStructure>();
    }

    public void addSegmentRecognitionStructure(
        SegmentRecognitionStructure segmentRecognitionStructure) {
        segmentRecognitionStructures
            .put(segmentRecognitionStructure.getBarcodeOneValue(), segmentRecognitionStructure);
    }

    public void addSegmentRecognitionStructure(int barcodeOneValue,
        SegmentRecognitionStructure segmentRecognitionStructure) {
        segmentRecognitionStructures
            .put(segmentRecognitionStructure.getBarcodeOneValue(), segmentRecognitionStructure);
    }

    public SegmentRecognitionStructure getSegmentRecognitionStructure(int barcodeOneValue) {
        return segmentRecognitionStructures.get(barcodeOneValue);
    }

    public Map<Integer, SegmentRecognitionStructure> getSegmentRecognitionStructures() {
        return segmentRecognitionStructures;
    }

    public int getFormPageID() {
        return formPageID;
    }

    public void setFormPageID(int formPageID) {
        this.formPageID = formPageID;
    }


    public void setFormFieldNames(Vector<String> fieldNames) {
        this.fieldNames = fieldNames;
    }

    public Vector<String> getFormFieldNames() {
        return fieldNames;
    }

}

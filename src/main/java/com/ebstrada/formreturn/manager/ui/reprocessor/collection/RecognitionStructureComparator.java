package com.ebstrada.formreturn.manager.ui.reprocessor.collection;

import com.ebstrada.formreturn.manager.logic.recognition.structure.FragmentRecognitionStructure;

public class RecognitionStructureComparator implements Comparable<RecognitionStructureComparator> {

    private String fieldname;

    private int order;

    private FragmentRecognitionStructure recognitionStructure;

    /*
     * Returns a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    public int compareTo(RecognitionStructureComparator otherColumn) {

        if (getOrder() < otherColumn.getOrder()) {
            return -1;
        } else if (getOrder() > otherColumn.getOrder()) {
            return 1;
        } else {
            return 0;
        }

    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public void addData(FragmentRecognitionStructure data) {
        recognitionStructure = data;
    }

    public FragmentRecognitionStructure getData() {
        return recognitionStructure;
    }

    public String getFieldname() {
        return fieldname;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}

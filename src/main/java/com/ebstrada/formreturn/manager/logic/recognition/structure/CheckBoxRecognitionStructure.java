package com.ebstrada.formreturn.manager.logic.recognition.structure;

public class CheckBoxRecognitionStructure {

    private short row;

    private short column;

    private String checkBoxValue;

    private long preRenderedPixelCount;

    private double fragmentXRatio;

    private double fragmentYRatio;

    private boolean checkBoxMarked;

    public short getRow() {
        return row;
    }

    public void setRow(short row) {
        this.row = row;
    }

    public short getColumn() {
        return column;
    }

    public void setColumn(short column) {
        this.column = column;
    }

    public String getCheckBoxValue() {
        return checkBoxValue;
    }

    public void setCheckBoxValue(String checkBoxValue) {
        this.checkBoxValue = checkBoxValue;
    }

    public long getPreRenderedPixelCount() {
        return preRenderedPixelCount;
    }

    public void setPreRenderedPixelCount(long preRenderedPixelCount) {
        this.preRenderedPixelCount = preRenderedPixelCount;
    }

    public double getFragmentXRatio() {
        return fragmentXRatio;
    }

    public void setFragmentXRatio(double fragmentXRatio) {
        this.fragmentXRatio = fragmentXRatio;
    }

    public double getFragmentYRatio() {
        return fragmentYRatio;
    }

    public void setFragmentYRatio(double fragmentYRatio) {
        this.fragmentYRatio = fragmentYRatio;
    }

    public void setCheckBoxMarked(boolean checkBoxMarked) {
        this.checkBoxMarked = checkBoxMarked;
    }

    public boolean isCheckBoxMarked() {
        return checkBoxMarked;
    }

}

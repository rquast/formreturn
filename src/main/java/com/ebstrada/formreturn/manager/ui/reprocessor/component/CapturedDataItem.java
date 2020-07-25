package com.ebstrada.formreturn.manager.ui.reprocessor.component;

public class CapturedDataItem {

    public static final int OMR_FIELD = 1;

    public static final int BARCODE_FIELD = 2;

    private int row;

    private int column;

    private String value;

    private int type = OMR_FIELD;

    private boolean marked;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public boolean isMarked() {
        return marked;
    }

}

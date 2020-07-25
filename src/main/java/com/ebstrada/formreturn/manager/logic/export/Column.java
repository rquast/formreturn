package com.ebstrada.formreturn.manager.logic.export;

import java.util.Vector;

import com.ebstrada.formreturn.manager.util.Misc;

public class Column implements Comparable<Column> {

    private String fieldname;
    private String identifier;
    private int offset;
    private int order;
    private int sortType = ExportMap.SORT_BY_ORDER_INDEX;
    private int size;

    private Vector<String> columnData = new Vector<String>();

    /*
     * Returns a negative integer, zero, or a positive integer as this object
     * is less than, equal to, or greater than the specified object.
     */
    public int compareTo(Column otherColumn) {

        switch (sortType) {
            case ExportMap.SORT_NATRUAL_ASCENDING:
                return Misc.compareString(getIdentifier(), otherColumn.getIdentifier());
            case ExportMap.SORT_NATURAL_DECENDING:
                return Misc.compareString(otherColumn.getIdentifier(), getIdentifier());
            case ExportMap.SORT_BY_ORDER_INDEX:
            default:
                if (getOffset() < otherColumn.getOffset()) {
                    return -1;
                } else if (getOffset() > otherColumn.getOffset()) {
                    return 1;
                } else if (getOrder() < otherColumn.getOrder()) {
                    return -1;
                } else if (getOrder() > otherColumn.getOrder()) {
                    return 1;
                } else {
                    return 0;
                }
        }

    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;

    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void addData(String data, int row) {
        if (data == null) {
            data = "";
        }
        columnData.set(row, data);
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public String getColumnValue(int row) {
        String data = columnData.get(row);
        if (data == null) {
            data = "";
        }
        return data;
    }

    public String getFieldname() {
        return fieldname;
    }

    public int getOrder() {
        return order;
    }

    public int getSortType() {
        return sortType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
        columnData.setSize(size);
    }

}


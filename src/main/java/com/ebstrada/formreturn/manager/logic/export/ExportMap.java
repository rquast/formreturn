package com.ebstrada.formreturn.manager.logic.export;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("serial") public class ExportMap extends java.util.HashMap<String, Column> {

    public static final int SORT_BY_ORDER_INDEX = 1;
    public static final int SORT_NATRUAL_ASCENDING = 2;
    public static final int SORT_NATURAL_DECENDING = 3;

    private int size = 0;

    private int sortType = SORT_BY_ORDER_INDEX;

    public void addData(String fieldname, String data, int row, int order) {
        addData(fieldname, data, row, order, fieldname, 0);
    }

    public void addData(String fieldname, String data, int row, int order, int offset) {
        addData(fieldname, data, row, order, fieldname, offset);
    }

    public void addData(String fieldname, String data, int row, int order, String identifier) {
        addData(fieldname, data, row, order, identifier, 0);
    }

    public void addData(String fieldname, String data, int row, int order, String identifier,
        int offset) {

        // look for the fieldname in this map
        Column column = get(identifier);
        if (column == null) {
            addColumn(fieldname, data, row, order, identifier, offset);
        } else {
            column.addData(data, row);
        }

    }

    private void addColumn(String fieldname, String data, int row, int order, String identifier,
        int offset) {
        Column column = new Column();
        column.setIdentifier(identifier);
        column.setFieldname(fieldname);
        column.setOrder(order);
        column.setOffset(offset);
        column.setSortType(getSortType());
        column.setSize(size);
        column.addData(data, row);
        put(identifier, column);
    }

    public List<Column> getSortedData() {
        List<Column> values = new ArrayList<Column>(values());
        Collections.sort(values);
        return values;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public void setSize(int size) {
        this.size = size;
    }

}

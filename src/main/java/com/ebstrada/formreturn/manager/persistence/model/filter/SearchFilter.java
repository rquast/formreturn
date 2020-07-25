package com.ebstrada.formreturn.manager.persistence.model.filter;

import java.util.ArrayList;

import com.ebstrada.formreturn.manager.persistence.model.AbstractDataModel;

public class SearchFilter {

    private String searchFieldName;

    private String nativeSearchFieldName;

    private int fieldType = AbstractDataModel.FIELD_TYPE_LONG;

    private String name;

    private boolean enabled;

    private int searchType = AbstractDataModel.SEARCH_LIKE;

    private String searchString;

    private long searchLong;

    private ArrayList<Long> searchLongArr;

    public ArrayList<Long> getSearchLongArr() {
        return searchLongArr;
    }

    public void setSearchLongArr(ArrayList<Long> searchLongArr) {
        this.searchLongArr = searchLongArr;
    }

    public String getSearchFieldName() {
        return searchFieldName;
    }

    public void setSearchFieldName(String searchFieldName) {
        this.searchFieldName = searchFieldName;
    }

    public int getSearchType() {
        return searchType;
    }

    public void setSearchType(int searchType) {
        this.searchType = searchType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getNativeSearchFieldName() {
        return nativeSearchFieldName;
    }

    public void setNativeSearchFieldName(String nativeSearchFieldName) {
        this.nativeSearchFieldName = nativeSearchFieldName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

    public void setSearchLong(long searchLong) {
        this.searchLong = searchLong;
    }

    public long getSearchLong() {
        return searchLong;
    }

}

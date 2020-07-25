package com.ebstrada.formreturn.manager.persistence.model;

import java.util.ArrayList;
import java.util.Vector;

import javax.swing.table.TableModel;

import org.apache.commons.lang.StringEscapeUtils;

import com.ebstrada.formreturn.manager.persistence.model.filter.OrderByFilter;
import com.ebstrada.formreturn.manager.persistence.model.filter.SearchFilter;
import com.ebstrada.formreturn.manager.util.Misc;

public abstract class AbstractDataModel {

    public static final int ORDER_ASCENDING = 0; // using SQL ASC

    public static final int ORDER_DESCENDING = 1; // using SQL DESC

    public static final int SEARCH_LIKE = 0; // using SQL LIKE %str%

    public static final int SEARCH_EQUALS = 1; // using SQL = 'value'

    public static final int SEARCH_PARENT = 2; // the parent search filter

    public static final int SEARCH_IN = 3; // using SQL WHERE IN

    public static final int FILTER_ACTIVE = 0;

    public static final int FILTER_INNACTIVE = 1;

    public static final int FIELD_TYPE_LONG = 0;

    public static final int FIELD_TYPE_STRING = 1;

    private long size = 0;

    private long offset = 0;

    private long limit = 1000;

    private Vector<OrderByFilter> orderByFilters;

    private boolean orderByActivated = true;

    private Vector<SearchFilter> searchFilters;

    private boolean searchActivated = false;

    private long[] selectedIds = new long[] {};

    private Vector<SearchFilter> parentSearchFilters;

    public abstract TableModel getTableModel();

    public long getSize() {
        return this.size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public void setOrderByFilters(Vector<OrderByFilter> orderByFilters) {
        this.orderByFilters = orderByFilters;
    }

    public void setSearchFilters(Vector<SearchFilter> searchFilters) {
        this.searchFilters = searchFilters;
    }

    public boolean isOrderByActivated() {
        return orderByActivated;
    }

    public void setOrderByActivated(boolean orderByActivated) {
        this.orderByActivated = orderByActivated;
    }

    public boolean isSearchActivated() {
        return searchActivated;
    }

    public void setSearchActivated(boolean searchActivated) {
        this.searchActivated = searchActivated;
    }

    public long getLimit() {
        return this.limit;
    }

    public long getOffset() {
        return this.offset;
    }

    public Vector<OrderByFilter> getOrderByFilters() {

        if (this.orderByFilters == null) {
            resetOrderByFilters();
        }

        return this.orderByFilters;

    }

    public Vector<SearchFilter> getSearchFilters() {

        if (this.searchFilters == null) {
            resetSearchFilters();
        }

        return this.searchFilters;

    }

    public void resetSearchFilters() {
        this.searchFilters = getDefaultSearchFilters();
    }

    public void resetOrderByFilters() {
        this.orderByFilters = getDefaultOrderByFilters();
    }

    public String getLimitSQL() {
        return Misc.getOffsetStr(getOffset(), getLimit());
    }

    public String getNativeSearchSQL() {
        return getSearchSQL(true);
    }

    public String getSearchSQL() {
        return getSearchSQL(false);
    }

    public String getSearchSQL(boolean isNativeSearch) {

        String sql = "";

        Vector<String> parts = new Vector<String>();

        Vector<SearchFilter> allSearchFilters = new Vector<SearchFilter>();
        allSearchFilters.addAll(getSearchFilters());
        allSearchFilters.addAll(getParentSearchFilters());

        for (SearchFilter searchFilter : allSearchFilters) {

            // TODO: fix this.. it is ugly...
            if (isSearchActivated() == false) {
                if (searchFilter.getSearchType() != SEARCH_PARENT)
                    if (searchFilter.getSearchType() != SEARCH_IN)
                        continue;
            }

            if (searchFilter.isEnabled() == false) {
                continue;
            }

            if (searchFilter.getFieldType() == FIELD_TYPE_STRING
                && searchFilter.getSearchString() == null) {
                continue;
            }

            if (searchFilter.getFieldType() == FIELD_TYPE_LONG && searchFilter.getSearchLong() <= 0
                && searchFilter.getSearchType() != SEARCH_IN) {
                continue;
            }

            String searchString = "";

            ArrayList<Long> searchIn = new ArrayList<Long>();

            if (searchFilter.getSearchType() == SEARCH_IN) {
                searchIn = searchFilter.getSearchLongArr();
            } else {
                if (searchFilter.getFieldType() == FIELD_TYPE_STRING) {
                    searchString =
                        StringEscapeUtils.escapeSql(searchFilter.getSearchString().trim());
                    if (searchString.length() <= 0) {
                        continue;
                    }
                } else {
                    searchString = searchFilter.getSearchLong() + "";
                    if (searchString.length() <= 0) {
                        continue;
                    }
                }
            }

            String sqlPart = "";

            String searchFieldName = isNativeSearch ?
                searchFilter.getNativeSearchFieldName() :
                searchFilter.getSearchFieldName();

            if (searchFilter.getSearchType() == SEARCH_LIKE) {
                if (searchFilter.getFieldType() == FIELD_TYPE_LONG) {
                    long val = Misc.parseLongString(searchString);
                    if (val <= 0 || val > Long.MAX_VALUE) {
                        continue;
                    }
                    sqlPart = searchFieldName + " = " + val;
                } else {
                    sqlPart = searchFieldName + " LIKE '%" + searchString + "%'";
                }
            } else if (searchFilter.getSearchType() == SEARCH_EQUALS) {
                if (searchFilter.getFieldType() == FIELD_TYPE_LONG) {
                    long val = Misc.parseLongString(searchString);
                    if (val <= 0 || val > Long.MAX_VALUE) {
                        continue;
                    }
                    sqlPart = searchFieldName + " = " + val;
                } else {
                    sqlPart = searchFieldName + " = '" + searchString + "'";
                }
            } else if (searchFilter.getSearchType() == SEARCH_PARENT) {
                sqlPart = searchFieldName + " = " + searchString;
            } else if (searchFilter.getSearchType() == SEARCH_IN) {
                if (searchFilter.getFieldType() == FIELD_TYPE_LONG) {
                    sqlPart = searchFieldName + " IN (" + Misc.implode(searchIn, ",") + ")";
                } else {
                    sqlPart = searchFieldName + " IN ('" + Misc.implode(searchIn, "','") + "')";
                }
            } else {
                continue;
            }

            parts.add(sqlPart);

        }

        if (parts.size() <= 0) {
            return "";
        }

        sql += Misc.implode(parts, " AND ");

        return sql;

    }

    public String getNativeOrderBySQL() {
        return getOrderBySQL(true);
    }

    public String getOrderBySQL() {
        return getOrderBySQL(false);
    }

    public String getOrderBySQL(boolean isNativeOrderBy) {
        String sql = "";
        Vector<String> parts = new Vector<String>();
        for (OrderByFilter orderByFilter : getOrderByFilters()) {
            if (orderByFilter.isEnabled() == false) {
                continue;
            }
            parts.add(orderByFilter.getSQL(isNativeOrderBy));
        }
        sql += Misc.implode(parts, ",");
        return sql;
    }

    public void setSelectedIds(long[] selectedIds) {
        this.selectedIds = selectedIds;
    }

    public long[] getSelectedIds() {
        return this.selectedIds;
    }

    public Vector<SearchFilter> getParentSearchFilters() {

        if (this.parentSearchFilters == null) {
            this.parentSearchFilters = new Vector<SearchFilter>();
            resetParentSearchFilters();
        }

        return this.parentSearchFilters;
    }

    public void setParentSearchFilters(Vector<SearchFilter> parentSearchFilters) {
        this.parentSearchFilters = parentSearchFilters;
    }

    public abstract void resetParentSearchFilters();

    public abstract Vector<OrderByFilter> getDefaultOrderByFilters();

    public abstract Vector<SearchFilter> getDefaultSearchFilters();

    public abstract long getDefaultLimit();

}

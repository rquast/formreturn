package com.ebstrada.formreturn.manager.persistence.model.filter;

import com.ebstrada.formreturn.manager.persistence.model.AbstractDataModel;
import com.ebstrada.formreturn.manager.util.Misc;


public class OrderByFilter {

    private String orderByFieldName;

    private String nativeOrderByFieldName;

    private int fieldType = AbstractDataModel.FIELD_TYPE_LONG;

    private String name;

    private boolean enabled;

    private int order = AbstractDataModel.ORDER_ASCENDING;

    private OrderByFilter nextOrderByFilter;

    public String getOrderByFieldName() {
        return orderByFieldName;
    }

    public OrderByFilter getNextOrderByFilter() {
        return nextOrderByFilter;
    }

    public void setNextOrderByFilter(OrderByFilter nextOrderByFilter) {
        this.nextOrderByFilter = nextOrderByFilter;
    }

    public void setOrderByFieldName(String orderByFieldName) {
        this.orderByFieldName = orderByFieldName;
    }

    public String getNativeOrderByFieldName() {
        return nativeOrderByFieldName;
    }

    public void setNativeOrderByFieldName(String nativeOrderByFieldName) {
        this.nativeOrderByFieldName = nativeOrderByFieldName;
    }

    public String getSQL(boolean isNativeOrderBy) {
        String orderFieldName =
            isNativeOrderBy ? getNativeOrderByFieldName() : getOrderByFieldName();

        if (getFieldType() == AbstractDataModel.FIELD_TYPE_STRING) {
            orderFieldName = "UPPER(" + orderFieldName + ")";
        }
        String sqlPart = orderFieldName + " " + (getOrder() == AbstractDataModel.ORDER_ASCENDING ?
            "ASC" :
            "DESC NULLS LAST");

        if (getNextOrderByFilter() != null) {
            String nextSql = getNextOrderByFilter().getSQL(isNativeOrderBy);
            if (nextSql.trim().length() > 0) {
                sqlPart += ", " + nextSql;
            }
        }

        return sqlPart;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFieldType() {
        return fieldType;
    }

    public void setFieldType(int fieldType) {
        this.fieldType = fieldType;
    }

}

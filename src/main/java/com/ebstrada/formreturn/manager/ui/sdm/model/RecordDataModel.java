package com.ebstrada.formreturn.manager.ui.sdm.model;

import java.util.Iterator;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.persistence.jpa.Record;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceText;
import com.ebstrada.formreturn.manager.persistence.model.AbstractDataModel;
import com.ebstrada.formreturn.manager.persistence.model.filter.OrderByFilter;
import com.ebstrada.formreturn.manager.persistence.model.filter.SearchFilter;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;

public class RecordDataModel extends AbstractDataModel {

    public static final int DEFAULT_LIMIT = 50;

    public RecordDataModel() {
        setLimit(DEFAULT_LIMIT);
    }

    public void resetParentSearchFilters() {

        Vector<SearchFilter> parentSearchFilters = getParentSearchFilters();

        // DATA_SET_ID
        SearchFilter idFilter = new SearchFilter();
        idFilter.setEnabled(true);
        idFilter.setSearchType(SEARCH_PARENT);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("PARENT");
        idFilter.setNativeSearchFieldName("RECORD.DATA_SET_ID");
        idFilter.setSearchFieldName("Record.dataSetId");
        idFilter.setSearchLong(-1);
        parentSearchFilters.add(idFilter);

    }

    public long getParentId() {
        long parentId = -1;
        for (SearchFilter parentSearchFilter : getParentSearchFilters()) {
            parentId = parentSearchFilter.getSearchLong();
        }
        return parentId;
    }

    public void setParentId(long parentId) {
        for (SearchFilter parentSearchFilter : getParentSearchFilters()) {
            parentSearchFilter.setSearchLong(parentId);
        }
    }

    public TableModel getTableModel() {

        DefaultTableModel dtm = new DefaultTableModel();

        dtm.addColumn("ID");

        if (getParentId() < 0) {
            dtm.addColumn(Localizer.localize("UI", "SourceDataModelNoRecordsFoundColumnName"));
            return dtm;
        }

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return dtm;
        }

        try {

            String sourceFieldSql =
                "SELECT SOURCE_FIELD.SOURCE_FIELD_ID, SOURCE_FIELD.SOURCE_FIELD_NAME FROM SOURCE_FIELD WHERE SOURCE_FIELD.DATA_SET_ID = "
                    + getParentId();
            Query sourceFieldQuery =
                entityManager.createNativeQuery(sourceFieldSql, SourceField.class);
            Object[] sourceFields = sourceFieldQuery.getResultList().toArray();
            if (sourceFields.length <= 0) {
                dtm.addColumn(Localizer.localize("UI", "SourceDataModelNoRecordsFoundColumnName"));
                return dtm;
            }

            Vector<Long> sourceFieldIds = new Vector<Long>();

            for (int i = 0; i < sourceFields.length; i++) {
                SourceField sourceField = (SourceField) sourceFields[i];
                sourceFieldIds.add(sourceField.getSourceFieldId());
                dtm.addColumn(sourceField.getSourceFieldName());
            }

            String countSql = "SELECT COUNT(RECORD.RECORD_ID) FROM RECORD";
            String countSearchSQL = getNativeSearchSQL();
            if (countSearchSQL.trim().length() > 0) {
                countSql += " WHERE " + countSearchSQL;
            }

            Query query = entityManager.createNativeQuery(countSql);

            Number countResult = (Number) query.getSingleResult();
            setSize(countResult.intValue());

            String sql = "SELECT RECORD.RECORD_ID FROM RECORD";

            // SEARCH FILTER
            String nativeSearchSQL = getNativeSearchSQL();
            if (nativeSearchSQL.trim().length() > 0) {
                sql += " WHERE " + nativeSearchSQL;
            }

            // ORDER BY
            if (this.isOrderByActivated()) {
                sql += " ORDER BY " + getNativeOrderBySQL();
            }

            // LIMIT
            sql += " " + getLimitSQL();

            Query recordQuery = entityManager.createNativeQuery(sql, Record.class);

            Iterator<Record> resultListIterator = recordQuery.getResultList().iterator();
            while (resultListIterator.hasNext()) {
                Record rf = resultListIterator.next();

                String[] rowData = new String[sourceFieldIds.size() + 1];
                rowData[0] = rf.getRecordId() + "";

                String sourceTextSQL = "SELECT st FROM SourceText st WHERE st.recordId = :recordId";

                Query sourceTextQuery = entityManager.createQuery(sourceTextSQL, SourceText.class);
                sourceTextQuery.setParameter("recordId", rf);

                Iterator<SourceText> stci = sourceTextQuery.getResultList().iterator();
                while (stci.hasNext()) {
                    SourceText st = stci.next();
                    int sfIndex = sourceFieldIds.indexOf(st.getSourceFieldId().getSourceFieldId());
                    rowData[(sfIndex + 1)] = st.getSourceTextString() + "";
                }
                dtm.addRow(rowData);
            }

        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return dtm;
        } finally {
            entityManager.close();
        }

        return dtm;

    }

    public Vector<OrderByFilter> getDefaultOrderByFilters() {

        Vector<OrderByFilter> filters = new Vector<OrderByFilter>();

        // RECORD_ID
        OrderByFilter idFilter = new OrderByFilter();
        idFilter.setEnabled(true);
        idFilter.setOrder(ORDER_ASCENDING);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("ID");
        idFilter.setNativeOrderByFieldName("RECORD.RECORD_ID");
        idFilter.setOrderByFieldName("Record.recordId");
        filters.add(idFilter);

        return filters;

    }

    public Vector<SearchFilter> getDefaultSearchFilters() {

        Vector<SearchFilter> filters = new Vector<SearchFilter>();

        // RECORD_ID
        SearchFilter idFilter = new SearchFilter();
        idFilter.setEnabled(true);
        idFilter.setSearchType(SEARCH_EQUALS);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("ID");
        idFilter.setNativeSearchFieldName("RECORD.RECORD_ID");
        idFilter.setSearchFieldName("Record.recordId");
        filters.add(idFilter);

        return filters;

    }

    public long getDefaultLimit() {
        return DEFAULT_LIMIT;
    }

}

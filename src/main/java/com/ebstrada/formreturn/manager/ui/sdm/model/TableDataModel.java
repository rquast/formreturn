package com.ebstrada.formreturn.manager.ui.sdm.model;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.persistence.model.AbstractDataModel;
import com.ebstrada.formreturn.manager.persistence.model.filter.OrderByFilter;
import com.ebstrada.formreturn.manager.persistence.model.filter.SearchFilter;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;

public class TableDataModel extends AbstractDataModel {

    public static final int DEFAULT_LIMIT = 50;

    public TableDataModel() {
        setLimit(DEFAULT_LIMIT);
        setSearchActivated(true);
    }

    public void resetParentSearchFilters() {
        // do nothing because there is no parent
    }

    public TableModel getTableModel() {
        return getTableModel(null);
    }

    public TableModel getTableModel(String regexFilter) {

        DefaultTableModel dtm = new DefaultTableModel() {

            private static final long serialVersionUID = 1L;

            Class[] columnTypes = new Class[] {String.class, String.class};
            boolean[] columnEditable = new boolean[] {false, false};

            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        };
        dtm.addColumn("ID");
        dtm.addColumn(Localizer.localize("UI", "SourceDataModelTableNameColumnName"));

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return dtm;
        }

        try {

            Query query = entityManager.createNamedQuery("DataSet.count");
            Number countResult = (Number) query.getSingleResult();
            setSize(countResult.intValue());

            String sql = "SELECT DATA_SET_ID, DATA_SET_NAME FROM DATA_SET";

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

            Query dataSetQuery = entityManager.createNativeQuery(sql, DataSet.class);

            List<DataSet> resultList = dataSetQuery.getResultList();
            Iterator<DataSet> resultListIterator = resultList.iterator();
            while (resultListIterator.hasNext()) {
                DataSet ds = resultListIterator.next();
                if (regexFilter != null && regexFilter.length() > 0 && !(Misc
                    .matchRegex(regexFilter, ds.getDataSetName()))) {
                    continue;
                }
                dtm.addRow(new String[] {ds.getDataSetId() + "", ds.getDataSetName()});
            }

        } catch (org.apache.openjpa.persistence.PersistenceException pe) {

            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(pe);

            return dtm;

        } finally {
            entityManager.close();
        }

        return dtm;

    }

    public Vector<OrderByFilter> getDefaultOrderByFilters() {

        Vector<OrderByFilter> filters = new Vector<OrderByFilter>();

        // DATA_SET_ID
        OrderByFilter idFilter = new OrderByFilter();
        idFilter.setEnabled(true);
        idFilter.setOrder(ORDER_DESCENDING);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("ID");
        idFilter.setNativeOrderByFieldName("DATA_SET.DATA_SET_ID");
        idFilter.setOrderByFieldName("DataSet.dataSetId");
        filters.add(idFilter);

        // DATA_SET_NAME
        OrderByFilter nameFilter = new OrderByFilter();
        nameFilter.setEnabled(false);
        nameFilter.setOrder(ORDER_ASCENDING);
        nameFilter.setFieldType(FIELD_TYPE_STRING);
        nameFilter.setName(Localizer.localize("UI", "TableNameText"));
        nameFilter.setNativeOrderByFieldName("DATA_SET.DATA_SET_NAME");
        nameFilter.setOrderByFieldName("DataSet.dataSetName");
        filters.add(nameFilter);

        return filters;

    }

    public Vector<SearchFilter> getDefaultSearchFilters() {

        Vector<SearchFilter> filters = new Vector<SearchFilter>();

        // DATA_SET_ID
        SearchFilter idFilter = new SearchFilter();
        idFilter.setEnabled(true);
        idFilter.setSearchType(SEARCH_EQUALS);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("ID");
        idFilter.setNativeSearchFieldName("DATA_SET.DATA_SET_ID");
        idFilter.setSearchFieldName("DataSet.dataSetId");
        filters.add(idFilter);

        // DATA_SET_NAME
        SearchFilter nameFilter = new SearchFilter();
        nameFilter.setEnabled(false);
        nameFilter.setSearchType(SEARCH_LIKE);
        nameFilter.setFieldType(FIELD_TYPE_STRING);
        nameFilter.setName(Localizer.localize("UI", "TableNameText"));
        nameFilter.setNativeSearchFieldName("DATA_SET.DATA_SET_NAME");
        nameFilter.setSearchFieldName("DataSet.dataSetName");
        filters.add(nameFilter);

        return filters;

    }

    public long getDefaultLimit() {
        return DEFAULT_LIMIT;
    }

}

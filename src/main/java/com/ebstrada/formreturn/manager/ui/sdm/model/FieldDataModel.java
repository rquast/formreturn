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
import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.persistence.model.AbstractDataModel;
import com.ebstrada.formreturn.manager.persistence.model.filter.OrderByFilter;
import com.ebstrada.formreturn.manager.persistence.model.filter.SearchFilter;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;

public class FieldDataModel extends AbstractDataModel {

    public static final int DEFAULT_LIMIT = 1000;

    public FieldDataModel() {
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
        idFilter.setNativeSearchFieldName("SOURCE_FIELD.DATA_SET_ID");
        idFilter.setSearchFieldName("sf.dataSetId");
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

        DefaultTableModel dtm = new DefaultTableModel() {

            private static final long serialVersionUID = 1L;

            Class[] columnTypes = new Class[] {String.class, String.class, String.class};
            boolean[] columnEditable = new boolean[] {false, false, false};

            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        };
        dtm.addColumn("ID");
        dtm.addColumn(Localizer.localize("UI", "SourceDataModelFieldNameColumnName"));
        dtm.addColumn(Localizer.localize("UI", "SourceDataModelExportOrderIndexColumnName"));

        List<SourceField> resultList = getSourceFields();
        if (resultList == null) {
            return dtm;
        }

        Iterator<SourceField> resultListIterator = resultList.iterator();
        while (resultListIterator.hasNext()) {
            SourceField sf = resultListIterator.next();
            dtm.addRow(new String[] {sf.getSourceFieldId() + "", sf.getSourceFieldName(),
                sf.getOrderIndex() + ""});
        }

        return dtm;

    }

    public List<SourceField> getSourceFields() {

        if (getParentId() < 0) {
            return null;
        }

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return null;
        }

        List<SourceField> resultList = null;

        try {
            String sourceFieldSql = "SELECT sf FROM SourceField sf WHERE sf.dataSetId = :dataSetId";
            Query sourceFieldQuery = entityManager.createQuery(sourceFieldSql, SourceField.class);
            sourceFieldQuery
                .setParameter("dataSetId", entityManager.find(DataSet.class, getParentId()));

            resultList = sourceFieldQuery.getResultList();
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return null;
        } finally {
            entityManager.close();
        }

        return resultList;

    }

    public int getMaximumOrderIndex() {

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null || getParentId() <= 0) {
            return 0;
        }

        int maximumOrderIndex = 0;

        try {

            Query query = entityManager.createNativeQuery(
                "SELECT ORDER_INDEX FROM SOURCE_FIELD WHERE " + getNativeSearchSQL(), Long.class);
            List<Long> resultList = query.getResultList();

            if (resultList.size() > 0) {
                for (Long objArr : resultList) {
                    maximumOrderIndex = objArr.intValue();
                }
            }

        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
            return 0;
        } finally {
            entityManager.close();
        }

        return (maximumOrderIndex + 1);

    }

    public Vector<OrderByFilter> getDefaultOrderByFilters() {

        return new Vector<OrderByFilter>();

    }

    public Vector<SearchFilter> getDefaultSearchFilters() {

        return new Vector<SearchFilter>();

    }

    public long getDefaultLimit() {
        return DEFAULT_LIMIT;
    }

}

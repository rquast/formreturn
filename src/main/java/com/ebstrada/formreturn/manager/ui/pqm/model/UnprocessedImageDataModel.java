package com.ebstrada.formreturn.manager.ui.pqm.model;

import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.jpa.IncomingImage;
import com.ebstrada.formreturn.manager.persistence.model.AbstractDataModel;
import com.ebstrada.formreturn.manager.persistence.model.filter.OrderByFilter;
import com.ebstrada.formreturn.manager.persistence.model.filter.SearchFilter;
import com.ebstrada.formreturn.manager.ui.Main;

public class UnprocessedImageDataModel extends AbstractDataModel {

    public static final int DEFAULT_LIMIT = 1000;

    public UnprocessedImageDataModel() {
        setLimit(DEFAULT_LIMIT);
        setSearchActivated(false);
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

    @Override public long getDefaultLimit() {
        return DEFAULT_LIMIT;
    }

    @Override public Vector<OrderByFilter> getDefaultOrderByFilters() {

        Vector<OrderByFilter> filters = new Vector<OrderByFilter>();

        // INCOMING_IMAGE_ID
        OrderByFilter idFilter = new OrderByFilter();
        idFilter.setEnabled(true);
        idFilter.setOrder(ORDER_DESCENDING);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("ID");
        idFilter.setNativeOrderByFieldName("INCOMING_IMAGE.INCOMING_IMAGE_ID");
        idFilter.setOrderByFieldName("IncomingImage.incomingImageId");
        filters.add(idFilter);

        // INCOMING_IMAGE_NAME
        OrderByFilter nameFilter = new OrderByFilter();
        nameFilter.setEnabled(false);
        nameFilter.setOrder(ORDER_ASCENDING);
        nameFilter.setFieldType(FIELD_TYPE_STRING);
        nameFilter.setName(Localizer.localize("UI", "ImageNameText"));
        nameFilter.setNativeOrderByFieldName("INCOMING_IMAGE.INCOMING_IMAGE_NAME");
        nameFilter.setOrderByFieldName("IncomingImage.incomingImageName");
        filters.add(nameFilter);

        return filters;

    }

    @Override public Vector<SearchFilter> getDefaultSearchFilters() {

        Vector<SearchFilter> filters = new Vector<SearchFilter>();

        // INCOMING_IMAGE_ID
        SearchFilter idFilter = new SearchFilter();
        idFilter.setEnabled(true);
        idFilter.setSearchType(SEARCH_EQUALS);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("ID");
        idFilter.setNativeSearchFieldName("INCOMING_IMAGE.INCOMING_IMAGE_ID");
        idFilter.setSearchFieldName("IncomingImage.incomingImageId");
        filters.add(idFilter);

        // INCOMING_IMAGE_NAME
        SearchFilter nameFilter = new SearchFilter();
        nameFilter.setEnabled(false);
        nameFilter.setSearchType(SEARCH_LIKE);
        nameFilter.setFieldType(FIELD_TYPE_STRING);
        nameFilter.setName(Localizer.localize("UI", "ImageNameText"));
        nameFilter.setNativeSearchFieldName("INCOMING_IMAGE.INCOMING_IMAGE_NAME");
        nameFilter.setSearchFieldName("IncomingImage.incomingImageName");
        filters.add(nameFilter);

        return filters;

    }

    @Override public TableModel getTableModel() {

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
        dtm.addColumn(Localizer.localize("UI", "ProcessingQueueUnprocessedImageNameColumnName"));
        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return dtm;
        }

        try {

            String countSql =
                "SELECT COUNT(INCOMING_IMAGE.INCOMING_IMAGE_ID) FROM INCOMING_IMAGE WHERE INCOMING_IMAGE.MATCH_STATUS = 0";
            String countSearchSQL = getNativeSearchSQL();
            if (countSearchSQL.trim().length() > 0) {
                countSql += " AND " + countSearchSQL;
            }
            Query query = entityManager.createNativeQuery(countSql);
            Number countResult = (Number) query.getSingleResult();
            setSize(countResult.intValue());

            String sql =
                "SELECT INCOMING_IMAGE.INCOMING_IMAGE_ID, INCOMING_IMAGE.INCOMING_IMAGE_NAME FROM INCOMING_IMAGE WHERE INCOMING_IMAGE.MATCH_STATUS = 0";

            // SEARCH FILTER
            String nativeSearchSQL = getNativeSearchSQL();
            if (nativeSearchSQL.trim().length() > 0) {
                sql += " AND " + nativeSearchSQL;
            }

            // ORDER BY
            if (this.isOrderByActivated()) {
                sql += " ORDER BY " + getNativeOrderBySQL();
            }

            // LIMIT
            sql += " " + getLimitSQL();

            Query incomingImageQuery = entityManager.createNativeQuery(sql, IncomingImage.class);

            List<IncomingImage> resultList = incomingImageQuery.getResultList();
            for (IncomingImage incomingImage : resultList) {
                dtm.addRow(new String[] {incomingImage.getIncomingImageId() + "",
                    incomingImage.getIncomingImageName()});
            }

        } catch (org.apache.openjpa.persistence.PersistenceException pe) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(pe);
            return dtm;
        } finally {
            entityManager.close();
        }

        return dtm;
    }

    @Override public void resetParentSearchFilters() {
        // no parent filters
    }

}

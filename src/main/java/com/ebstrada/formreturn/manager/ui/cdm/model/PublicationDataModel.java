package com.ebstrada.formreturn.manager.ui.cdm.model;

import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.persistence.model.AbstractDataModel;
import com.ebstrada.formreturn.manager.persistence.model.filter.OrderByFilter;
import com.ebstrada.formreturn.manager.persistence.model.filter.SearchFilter;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.preferences.persistence.PublicationPreferences;

public class PublicationDataModel extends AbstractDataModel {

    public static final int DEFAULT_LIMIT = 50;

    public PublicationDataModel() {
        setLimit(DEFAULT_LIMIT);
        setSearchActivated(true);
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

        // PUBLICATION_ID
        OrderByFilter idFilter = new OrderByFilter();
        idFilter.setEnabled(true);
        idFilter.setOrder(ORDER_DESCENDING);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("ID");
        idFilter.setNativeOrderByFieldName("PUBLICATION.PUBLICATION_ID");
        idFilter.setOrderByFieldName("Publication.publicationId");
        filters.add(idFilter);

        // PUBLICATION_NAME
        OrderByFilter nameFilter = new OrderByFilter();
        nameFilter.setEnabled(false);
        nameFilter.setOrder(ORDER_ASCENDING);
        nameFilter.setFieldType(FIELD_TYPE_STRING);
        nameFilter.setName(Localizer.localize("UI", "PublicationNameText"));
        nameFilter.setNativeOrderByFieldName("PUBLICATION.PUBLICATION_NAME");
        nameFilter.setOrderByFieldName("Publication.publicationName");
        filters.add(nameFilter);

        return filters;

    }

    @Override public Vector<SearchFilter> getDefaultSearchFilters() {

        Vector<SearchFilter> filters = new Vector<SearchFilter>();

        // PUBLICATION_ID
        SearchFilter idFilter = new SearchFilter();
        idFilter.setEnabled(false);
        idFilter.setSearchType(SEARCH_EQUALS);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("ID");
        idFilter.setNativeSearchFieldName("PUBLICATION.PUBLICATION_ID");
        idFilter.setSearchFieldName("Publication.publicationId");
        filters.add(idFilter);

        // PUBLICATION_NAME
        SearchFilter nameFilter = new SearchFilter();
        nameFilter.setEnabled(true);
        nameFilter.setSearchType(SEARCH_LIKE);
        nameFilter.setFieldType(FIELD_TYPE_STRING);
        nameFilter.setName(Localizer.localize("UI", "PublicationNameText"));
        nameFilter.setNativeSearchFieldName("PUBLICATION.PUBLICATION_NAME");
        nameFilter.setSearchFieldName("Publication.publicationName");
        filters.add(nameFilter);

        return filters;

    }

    @Override public TableModel getTableModel() {

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
        dtm.addColumn(Localizer.localize("UICDM", "PublicationNameColumnText"));
        dtm.addColumn(Localizer.localize("UICDM", "PublicationTypeColumnText"));

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return dtm;
        }

        try {

            String countSql = "SELECT COUNT(PUBLICATION.PUBLICATION_ID) FROM PUBLICATION";
            String countSearchSQL = getNativeSearchSQL();
            if (countSearchSQL.trim().length() > 0) {
                countSql += " WHERE " + countSearchSQL;
            }

            Query query = entityManager.createNativeQuery(countSql);

            Number countResult = (Number) query.getSingleResult();
            setSize(countResult.intValue());

            String sql =
                "SELECT PUBLICATION.PUBLICATION_ID, PUBLICATION.PUBLICATION_NAME, PUBLICATION.PUBLICATION_TYPE, PUBLICATION.DATA_SET_ID FROM PUBLICATION";

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

            Query publicationQuery = entityManager.createNativeQuery(sql, Publication.class);

            List<Publication> resultList = publicationQuery.getResultList();

            for (Publication publication : resultList) {
                dtm.addRow(new String[] {publication.getPublicationId() + "",
                    publication.getPublicationName(),
                    getPublicationTypeName(publication.getPublicationType())});
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
        // publication has no parent in this view
    }

    public String getPublicationTypeName(int publicationType) {

        switch (publicationType) {
            case PublicationPreferences.FORM_ID_RECONCILE_WITH_SOURCE_DATA_RECORD:
                return Localizer.localize("Util", "PublicationType0");
            case PublicationPreferences.RECONCILE_KEY_WITH_SOURCE_DATA_RECORD_NO_CREATE:
                return Localizer.localize("Util", "PublicationType1");
            case PublicationPreferences.RECONCILE_KEY_WITH_SOURCE_DATA_RECORD_CREATE_NEW:
                return Localizer.localize("Util", "PublicationType2");
            default:
                return "";
        }

    }

}

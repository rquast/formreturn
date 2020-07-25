package com.ebstrada.formreturn.manager.ui.cdm.model;

import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.jpa.Grading;
import com.ebstrada.formreturn.manager.persistence.jpa.GradingRule;
import com.ebstrada.formreturn.manager.persistence.jpa.Publication;
import com.ebstrada.formreturn.manager.persistence.model.AbstractDataModel;
import com.ebstrada.formreturn.manager.persistence.model.filter.OrderByFilter;
import com.ebstrada.formreturn.manager.persistence.model.filter.SearchFilter;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.database.Pivot;

public class FormDataModel extends AbstractDataModel {

    public static final int DEFAULT_LIMIT = 1000;

    public FormDataModel() {
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

        // FORM_ID
        OrderByFilter idFilter = new OrderByFilter();
        idFilter.setEnabled(true);
        idFilter.setOrder(ORDER_DESCENDING);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("ID");
        idFilter.setNativeOrderByFieldName("FORM.FORM_ID");
        idFilter.setOrderByFieldName("Form.formId");
        filters.add(idFilter);

        // FORM_PASSWORD
        OrderByFilter passwordFilter = new OrderByFilter();
        passwordFilter.setEnabled(false);
        passwordFilter.setOrder(ORDER_ASCENDING);
        passwordFilter.setFieldType(FIELD_TYPE_STRING);
        passwordFilter.setName(Localizer.localize("UI", "FormPasswordText"));
        passwordFilter.setNativeOrderByFieldName("FORM.FORM_PASSWORD");
        passwordFilter.setOrderByFieldName("Form.formPassword");
        filters.add(passwordFilter);

        // ERROR_COUNT
        OrderByFilter errorCountFilter = new OrderByFilter();
        errorCountFilter.setEnabled(false);
        errorCountFilter.setOrder(ORDER_DESCENDING);
        errorCountFilter.setFieldType(FIELD_TYPE_LONG);
        errorCountFilter.setName(Localizer.localize("UI", "ErrorCountText"));
        errorCountFilter.setNativeOrderByFieldName("FORM.ERROR_COUNT");
        errorCountFilter.setOrderByFieldName("Form.errorCount");
        filters.add(errorCountFilter);

        // AGGREGATE_MARK
        OrderByFilter aggregateMarkFilter = new OrderByFilter();
        aggregateMarkFilter.setEnabled(false);
        aggregateMarkFilter.setOrder(ORDER_DESCENDING);
        aggregateMarkFilter.setFieldType(FIELD_TYPE_LONG);
        aggregateMarkFilter.setName(Localizer.localize("UI", "FormScoreText"));
        aggregateMarkFilter.setNativeOrderByFieldName("FORM.AGGREGATE_MARK");
        aggregateMarkFilter.setOrderByFieldName("Form.aggregateMark");
        filters.add(aggregateMarkFilter);

        return filters;

    }

    @Override public Vector<SearchFilter> getDefaultSearchFilters() {

        Vector<SearchFilter> filters = new Vector<SearchFilter>();

        // FORM_ID
        SearchFilter idFilter = new SearchFilter();
        idFilter.setEnabled(false);
        idFilter.setSearchType(SEARCH_EQUALS);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("ID");
        idFilter.setNativeSearchFieldName("FORM.FORM_ID");
        idFilter.setSearchFieldName("Form.formId");
        filters.add(idFilter);

        // FORM_PASSWORD
        SearchFilter passwordFilter = new SearchFilter();
        passwordFilter.setEnabled(true);
        passwordFilter.setSearchType(SEARCH_LIKE);
        passwordFilter.setFieldType(FIELD_TYPE_STRING);
        passwordFilter.setName(Localizer.localize("UI", "FormPasswordText"));
        passwordFilter.setNativeSearchFieldName("FORM.FORM_PASSWORD");
        passwordFilter.setSearchFieldName("Form.formPassword");
        filters.add(passwordFilter);

        return filters;

    }

    @Override public TableModel getTableModel() {

        DefaultTableModel dtm = new DefaultTableModel();
        dtm.addColumn("ID");
        dtm.addColumn(Localizer.localize("UICDM", "FormPasswordColumnText"));
        dtm.addColumn(Localizer.localize("UICDM", "FormScoreColumnText"));
        dtm.addColumn(Localizer.localize("UICDM", "ErrorCountColumnText"));
        dtm.addColumn(Localizer.localize("UICDM", "ProcessedPagesColumnText"));

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return dtm;
        }

        try {

            String sourceDataSql = "";
            String sourceDataSqlPrefix = "";

            Grading grading = null;

            if (getParentId() > 0) {

                sourceDataSqlPrefix = ", SOURCE_DATA.*";

                String publicationSql =
                    "SELECT PUBLICATION.DATA_SET_ID FROM PUBLICATION WHERE PUBLICATION.PUBLICATION_ID = "
                        + getParentId();
                Query qry = entityManager.createNativeQuery(publicationSql);
                long dataSetId = (Long) qry.getSingleResult();

                Pivot pivot = new Pivot(entityManager, dataSetId, 0);
                sourceDataSql = "LEFT JOIN (" + pivot.getQueryString()
                    + ") AS SOURCE_DATA ON SOURCE_DATA.RECORD_ID = FORM.RECORD_ID ";

                for (String columnName : pivot.getSourceFieldMap().values()) {
                    dtm.addColumn(columnName);
                }

                String gradingSql =
                    "SELECT gr FROM Grading gr WHERE gr.publicationId = :publicationId";
                Query gradingQry = entityManager.createQuery(gradingSql, Grading.class);
                gradingQry.setParameter("publicationId",
                    entityManager.find(Publication.class, getParentId()));
                List<Grading> results = (List<Grading>) gradingQry.getResultList();
                if (results.size() > 0) {
                    grading = results.iterator().next();
                }

            }

            String countSql =
                "SELECT COUNT(FORM.FORM_ID) FROM FORM WHERE FORM.RECORD_ID IS NOT NULL";
            String countSearchSQL = getNativeSearchSQL();
            if (countSearchSQL.trim().length() > 0) {
                countSql += " AND " + countSearchSQL;
            }

            Query query = entityManager.createNativeQuery(countSql);

            Number countResult = (Number) query.getSingleResult();
            setSize(countResult.intValue());

            String processedPageCountSql =
                ", (SELECT COUNT(FORM_PAGE.FORM_PAGE_ID) FROM FORM_PAGE WHERE FORM_PAGE.FORM_ID = FORM.FORM_ID AND FORM_PAGE.PROCESSED_TIME IS NOT NULL) AS PROCESSED_COUNT ";

            String sql =
                "SELECT FORM.FORM_ID, FORM.FORM_PASSWORD, FORM.AGGREGATE_MARK, FORM.ERROR_COUNT "
                    + processedPageCountSql + sourceDataSqlPrefix + "FROM FORM " + sourceDataSql
                    + "WHERE FORM.RECORD_ID IS NOT NULL";

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

            Query finalQry = entityManager.createNativeQuery(sql);

            List<Object[]> results = finalQry.getResultList();
            for (Object[] obj : results) {
                dtm.addRow(obj);
            }

            if (grading != null) {

                List<GradingRule> gradingRules = grading.getGradingRuleCollection();

                dtm.addColumn("Percentage Correct");
                dtm.addColumn("Grading");
                int scoreColumnIndex =
                    dtm.findColumn(Localizer.localize("UICDM", "FormScoreColumnText"));
                int percentageColumnIndex = dtm.findColumn("Percentage Correct");
                int gradingColumnIndex = dtm.findColumn("Grading");
                for (int row = 0; row < dtm.getRowCount(); row++) {
                    Double value = (Double) dtm
                        .getValueAt(row, scoreColumnIndex); // 2 is the index of the score column
                    double percentage = (value / grading.getTotalPossibleScore()) * 100.0d;
                    dtm.setValueAt(percentage + "%", row, percentageColumnIndex);
                    dtm.setValueAt(
                        Misc.getGrading(value, gradingRules, grading.getTotalPossibleScore()), row,
                        gradingColumnIndex);
                }
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

        Vector<SearchFilter> parentSearchFilters = getParentSearchFilters();
        parentSearchFilters.removeAllElements();

        // PUBLICATION_ID
        SearchFilter idFilter = new SearchFilter();
        idFilter.setEnabled(true);
        idFilter.setSearchType(SEARCH_PARENT);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("PARENT");
        idFilter.setNativeSearchFieldName("FORM.PUBLICATION_ID");
        idFilter.setSearchFieldName("form.publicationId");
        idFilter.setSearchLong(-1);
        parentSearchFilters.add(idFilter);

    }

}

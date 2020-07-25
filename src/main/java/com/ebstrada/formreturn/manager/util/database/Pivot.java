package com.ebstrada.formreturn.manager.util.database;

import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.commons.lang.StringEscapeUtils;

import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class Pivot {

    private EntityManager entityManager;

    private long dataSetId;

    private long recordId;

    private HashMap<Long, String> sourceFieldMap;

    public Pivot(EntityManager entityManager, long dataSetId, long recordId) {
        this.entityManager = entityManager;
        this.dataSetId = dataSetId;
        this.recordId = recordId;
    }

    public String getQueryString() {
        this.sourceFieldMap = this.getSourceFields();
        String sql = getDataTableQuery(sourceFieldMap);
        return sql;
    }

    public HashMap<Long, String> getSourceFieldMap() {
        return sourceFieldMap;
    }

    @SuppressWarnings("unchecked") public HashMap<Long, String> getSourceFields() {

        String sourceFieldSql =
            "SELECT SOURCE_FIELD.SOURCE_FIELD_ID, SOURCE_FIELD.SOURCE_FIELD_NAME FROM SOURCE_FIELD WHERE SOURCE_FIELD.DATA_SET_ID"
                + getDataSetId() + getHiddenFieldNames() + " ORDER BY SOURCE_FIELD.ORDER_INDEX";
        Query sourceFieldQuery = entityManager.createNativeQuery(sourceFieldSql, SourceField.class);

        HashMap<Long, String> sourceFieldMap = new HashMap<Long, String>();
        for (SourceField sf : (List<SourceField>) sourceFieldQuery.getResultList()) {
            sourceFieldMap.put(sf.getSourceFieldId(), sf.getSourceFieldName());
        }

        return sourceFieldMap;

    }

    public String getHiddenFieldNames() {
        List<String> hiddenFields = PreferencesManager.getHiddenFields();
        if (hiddenFields == null || hiddenFields.size() <= 0) {
            return "";
        }

        String sql = " ";

        sql += "AND SOURCE_FIELD.SOURCE_FIELD_NAME NOT IN ('" + Misc.implode(hiddenFields, "','")
            + "')";

        return sql;
    }

    public String getDataTableQuery(HashMap<Long, String> output) {

        String sql = "SELECT ";

        for (Long sourceFieldId : this.sourceFieldMap.keySet()) {
            sql += "MAX(CASE WHEN " + sourceFieldId + " = " + "SOURCE_TEXT.SOURCE_FIELD_ID ";
            sql += "THEN SOURCE_TEXT.SOURCE_TEXT_STRING ";
            sql +=
                "END) AS \"" + StringEscapeUtils.escapeSql(this.sourceFieldMap.get(sourceFieldId))
                    + "\", ";
        }

        sql += "RECORD_ID FROM SOURCE_TEXT WHERE SOURCE_TEXT.RECORD_ID" + getRecordId();
        sql += " GROUP BY RECORD_ID";

        return sql;
    }



    private String getDataSetId() {
        if (this.dataSetId <= 0) {
            return " IS NOT NULL";
        } else {
            return " = " + this.dataSetId;
        }
    }

    private String getRecordId() {
        if (this.recordId <= 0) {
            return " IS NOT NULL";
        } else {
            return " = " + this.recordId;
        }
    }

}

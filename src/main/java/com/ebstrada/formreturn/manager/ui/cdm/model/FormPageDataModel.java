package com.ebstrada.formreturn.manager.ui.cdm.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.export.Column;
import com.ebstrada.formreturn.manager.logic.export.ExportMap;
import com.ebstrada.formreturn.manager.logic.recognition.reader.FormReaderException;
import com.ebstrada.formreturn.manager.persistence.jpa.CheckBox;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentBarcode;
import com.ebstrada.formreturn.manager.persistence.jpa.FragmentOmr;
import com.ebstrada.formreturn.manager.persistence.jpa.ProcessedImage;
import com.ebstrada.formreturn.manager.persistence.jpa.Segment;
import com.ebstrada.formreturn.manager.persistence.model.AbstractDataModel;
import com.ebstrada.formreturn.manager.persistence.model.filter.OrderByFilter;
import com.ebstrada.formreturn.manager.persistence.model.filter.SearchFilter;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;

public class FormPageDataModel extends AbstractDataModel {

    public static final int DEFAULT_LIMIT = 1000;

    public FormPageDataModel() {
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

        // FORM_PAGE_ID
        OrderByFilter idFilter = new OrderByFilter();
        idFilter.setEnabled(false);
        idFilter.setOrder(ORDER_DESCENDING);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("ID");
        idFilter.setNativeOrderByFieldName("FORM_PAGE.FORM_PAGE_ID");
        idFilter.setOrderByFieldName("FormPage.formId");
        filters.add(idFilter);

        // FORM_PAGE_NUMBER
        OrderByFilter numberFilter = new OrderByFilter();
        numberFilter.setEnabled(false);
        numberFilter.setOrder(ORDER_ASCENDING);
        numberFilter.setFieldType(FIELD_TYPE_LONG);
        numberFilter.setName(Localizer.localize("UI", "PageNumberText"));
        numberFilter.setNativeOrderByFieldName("FORM_PAGE.FORM_PAGE_NUMBER");
        numberFilter.setOrderByFieldName("FormPage.formPageNumber");
        filters.add(numberFilter);

        // FORM_AND_PAGE_NUMBER
        OrderByFilter firstFilter = new OrderByFilter();
        firstFilter.setEnabled(true);
        firstFilter.setOrder(ORDER_ASCENDING);
        firstFilter.setFieldType(FIELD_TYPE_LONG);
        firstFilter.setName(Localizer.localize("UI", "FormPageNumberText"));
        firstFilter.setNativeOrderByFieldName("FORM_PAGE.FORM_ID");
        firstFilter.setOrderByFieldName("FormPage.formId");
        OrderByFilter secondFilter = new OrderByFilter();
        secondFilter.setEnabled(true);
        secondFilter.setOrder(ORDER_ASCENDING);
        secondFilter.setFieldType(FIELD_TYPE_LONG);
        secondFilter.setName("Page Number");
        secondFilter.setNativeOrderByFieldName("FORM_PAGE.FORM_PAGE_NUMBER");
        secondFilter.setOrderByFieldName("FormPage.formPageNumber");
        firstFilter.setNextOrderByFilter(secondFilter);
        filters.add(firstFilter);

        // PROCESSED_TIME
        OrderByFilter processedTimeFilter = new OrderByFilter();
        processedTimeFilter.setEnabled(false);
        processedTimeFilter.setOrder(ORDER_DESCENDING);
        processedTimeFilter.setFieldType(FIELD_TYPE_LONG);
        processedTimeFilter.setName(Localizer.localize("UI", "ProcessedTimeText"));
        processedTimeFilter.setNativeOrderByFieldName("FORM_PAGE.PROCESSED_TIME");
        processedTimeFilter.setOrderByFieldName("FormPage.processedTime");
        filters.add(processedTimeFilter);

        // ERROR_COUNT
        OrderByFilter errorCountFilter = new OrderByFilter();
        errorCountFilter.setEnabled(false);
        errorCountFilter.setOrder(ORDER_DESCENDING);
        errorCountFilter.setFieldType(FIELD_TYPE_LONG);
        errorCountFilter.setName(Localizer.localize("UI", "ErrorCountText"));
        errorCountFilter.setNativeOrderByFieldName("FORM_PAGE.ERROR_COUNT");
        errorCountFilter.setOrderByFieldName("FormPage.errorCount");
        filters.add(errorCountFilter);

        return filters;

    }

    @Override public Vector<SearchFilter> getDefaultSearchFilters() {

        Vector<SearchFilter> filters = new Vector<SearchFilter>();

        // FORM_PAGE_ID
        SearchFilter idFilter = new SearchFilter();
        idFilter.setEnabled(true);
        idFilter.setSearchType(SEARCH_EQUALS);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("ID");
        idFilter.setNativeSearchFieldName("FORM_PAGE.FORM_PAGE_ID");
        idFilter.setSearchFieldName("FormPage.formId");
        filters.add(idFilter);

        // FORM_ID
        SearchFilter formIdFilter = new SearchFilter();
        formIdFilter.setEnabled(false);
        formIdFilter.setSearchType(SEARCH_EQUALS);
        formIdFilter.setFieldType(FIELD_TYPE_LONG);
        formIdFilter.setName(Localizer.localize("UI", "FormIDText"));
        formIdFilter.setNativeSearchFieldName("FORM_PAGE.FORM_ID");
        formIdFilter.setSearchFieldName("FormPage.formId");
        filters.add(formIdFilter);

        // PUBLICATION_ID
        SearchFilter publicationIdFilter = new SearchFilter();
        publicationIdFilter.setEnabled(false);
        publicationIdFilter.setSearchType(SEARCH_EQUALS);
        publicationIdFilter.setFieldType(FIELD_TYPE_LONG);
        publicationIdFilter.setName(Localizer.localize("UI", "PublicationIDText"));
        publicationIdFilter.setNativeSearchFieldName("FORM.PUBLICATION_ID");
        publicationIdFilter.setSearchFieldName("Form.publicationId");
        filters.add(publicationIdFilter);

        return filters;

    }

    @Override public TableModel getTableModel() {

        DefaultTableModel dtm = new DefaultTableModel() {

            private static final long serialVersionUID = 1L;

            Class[] columnTypes =
                new Class[] {String.class, String.class, String.class, String.class, String.class,
                    String.class, String.class};
            boolean[] columnEditable =
                new boolean[] {false, false, false, false, false, false, false};

            @Override public Class<?> getColumnClass(int columnIndex) {
                return columnTypes[columnIndex];
            }

            @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
                return columnEditable[columnIndex];
            }
        };
        dtm.addColumn("ID");
        dtm.addColumn(Localizer.localize("UICDM", "PublicationIDColumnText"));
        dtm.addColumn(Localizer.localize("UICDM", "FormIDColumnText"));
        dtm.addColumn(Localizer.localize("UICDM", "PageNumberColumnText"));
        dtm.addColumn(Localizer.localize("UICDM", "PageScoreColumnText"));
        dtm.addColumn(Localizer.localize("UICDM", "ErrorCountColumnText"));
        dtm.addColumn(Localizer.localize("UICDM", "ProcessedTimeColumnText"));

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return dtm;
        }

        try {

            String countSql =
                "SELECT COUNT(FORM_PAGE.FORM_PAGE_ID) FROM FORM_PAGE LEFT JOIN FORM ON FORM_PAGE.FORM_ID = FORM.FORM_ID WHERE FORM.RECORD_ID IS NOT NULL";
            String countSearchSQL = getNativeSearchSQL();
            if (countSearchSQL.trim().length() > 0) {
                countSql += " AND " + countSearchSQL;
            }
            Query query = entityManager.createNativeQuery(countSql);
            Number countResult = (Number) query.getSingleResult();
            setSize(countResult.intValue());


            String sql =
                "SELECT FORM.PUBLICATION_ID, FORM_PAGE.FORM_PAGE_ID, FORM_PAGE.FORM_ID, FORM_PAGE.FORM_PAGE_NUMBER, FORM_PAGE.AGGREGATE_MARK, FORM_PAGE.ERROR_COUNT, FORM_PAGE.PROCESSED_TIME FROM FORM_PAGE LEFT JOIN FORM ON FORM_PAGE.FORM_ID = FORM.FORM_ID WHERE FORM.RECORD_ID IS NOT NULL";

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

            Query formPageQuery = entityManager.createNativeQuery(sql);
            List<Object[]> resultList = formPageQuery.getResultList();

            for (Object[] obj : resultList) {

                long publicationId = (Long) obj[0];
                long formPageId = (Long) obj[1];
                long formId = (Long) obj[2];
                long formPageNumber = (Long) obj[3];
                double aggregateMark = obj[4] == null ? 0 : (Double) obj[4];
                long errorCount = obj[5] == null ? 0 : (Long) obj[5];
                Timestamp processedTimeStamp = (Timestamp) obj[6];


                String processedTime = Localizer.localize("UICDM", "NotYetProcessedMessage");
                long processedTimestamp = 0;
                if (processedTimeStamp != null) {
                    processedTimestamp = processedTimeStamp.getTime();
                    if (processedTimestamp > 0) {
                        processedTime = new SimpleDateFormat().format(processedTimeStamp.getTime());
                    }
                }

                dtm.addRow(new String[] {Long.toString(formPageId), Long.toString(publicationId),
                    Long.toString(formId), Long.toString(formPageNumber),
                    Double.toString(aggregateMark), Long.toString(errorCount), processedTime});
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

        // FORM_ID
        SearchFilter idFilter = new SearchFilter();
        idFilter.setEnabled(true);
        idFilter.setSearchType(SEARCH_PARENT);
        idFilter.setFieldType(FIELD_TYPE_LONG);
        idFilter.setName("PARENT");
        idFilter.setNativeSearchFieldName("FORM_PAGE.FORM_ID");
        idFilter.setSearchFieldName("formPage.formId");
        idFilter.setSearchLong(-1);
        parentSearchFilters.add(idFilter);

    }

    public TableModel getCapturedDataModel(JTextField pageScoreTextField,
        JTextField formScoreTextField) {
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

        dtm.addColumn(Localizer.localize("UICDM", "OrderIndexColumnText"));
        dtm.addColumn(Localizer.localize("UICDM", "FieldNameColumnText"));
        dtm.addColumn(Localizer.localize("UICDM", "CapturedValueColumnText"));

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null || getSelectedIds().length <= 0) {
            return dtm;
        }

        try {

            ExportMap export = new ExportMap();
            export.setSize(1);

            FormPage formPage = entityManager.find(FormPage.class, getSelectedIds()[0]);

            formScoreTextField.setText(formPage.getFormId().getAggregateMark() + "");
            pageScoreTextField.setText(formPage.getAggregateMark() + "");

            Iterator<Segment> si = formPage.getSegmentCollection().iterator();

            while (si.hasNext()) {
                Segment segment = si.next();

                List<FragmentBarcode> fbc = segment.getFragmentBarcodeCollection();
                if (fbc != null && fbc.size() > 0) {
                    for (FragmentBarcode fragmentBarcode : fbc) {
                        if (fragmentBarcode.getBarcodeValue() != null) {
                            export.addData(fragmentBarcode.getCapturedDataFieldName(),
                                fragmentBarcode.getBarcodeValue(), 0,
                                (int) fragmentBarcode.getOrderIndex());
                        }
                    }
                }

                Iterator<FragmentOmr> fomri = segment.getFragmentOmrCollection().iterator();
                while (fomri.hasNext()) {
                    FragmentOmr fragmentOmr = fomri.next();

                    String cdfnField = fragmentOmr.getCapturedDataFieldName();

                    List<CheckBox> cbc = fragmentOmr.getCheckBoxCollection();

                    if (fragmentOmr.getCapturedString() != null) {

                        if (fragmentOmr.getInvalidated() > 0) {

                            FormReaderException fre =
                                new FormReaderException((int) fragmentOmr.getErrorType());
                            fre.setMissingCheckboxFieldName(cdfnField);
                            String capturedValueError = String
                                .format(Localizer.localize("UICDM", "CapturedFieldErrorText"),
                                    fre.getErrorTitle());
                            export.addData(cdfnField, capturedValueError, 0,
                                (int) fragmentOmr.getOrderIndex());
                            export.addData(fragmentOmr.getMarkColumnName(), 0 + "", 0,
                                (int) fragmentOmr.getMarkOrderIndex());

                        } else {

                            export.addData(cdfnField, fragmentOmr.getCapturedString(), 0,
                                (int) fragmentOmr.getOrderIndex());
                            export.addData(fragmentOmr.getMarkColumnName(),
                                fragmentOmr.getMark() + "", 0,
                                (int) fragmentOmr.getMarkOrderIndex());

                        }

                    } else if (fragmentOmr.getCapturedData() != null) {

                        if (fragmentOmr.getInvalidated() > 0) {

                            FormReaderException fre =
                                new FormReaderException((int) fragmentOmr.getErrorType());
                            fre.setMissingCheckboxFieldName(cdfnField);
                            String capturedValueError = String
                                .format(Localizer.localize("UICDM", "CapturedFieldErrorText"),
                                    fre.getErrorTitle());
                            export.addData(cdfnField, capturedValueError, 0,
                                (int) fragmentOmr.getOrderIndex());
                            export.addData(fragmentOmr.getMarkColumnName(), 0 + "", 0,
                                (int) fragmentOmr.getMarkOrderIndex());

                        } else {

                            export.addData(cdfnField,
                                Misc.implode(fragmentOmr.getCapturedData(), ","), 0,
                                (int) fragmentOmr.getOrderIndex());
                            export.addData(fragmentOmr.getMarkColumnName(),
                                fragmentOmr.getMark() + "", 0,
                                (int) fragmentOmr.getMarkOrderIndex());

                        }

                    } else if (cbc.size() > 0) {

                        if (fragmentOmr.getInvalidated() > 0) {

                            FormReaderException fre =
                                new FormReaderException((int) fragmentOmr.getErrorType());
                            fre.setMissingCheckboxFieldName(cdfnField);
                            String capturedValueError = String
                                .format(Localizer.localize("UICDM", "CapturedFieldErrorText"),
                                    fre.getErrorTitle());
                            export.addData(cdfnField, capturedValueError, 0,
                                (int) fragmentOmr.getOrderIndex());
                            export.addData(fragmentOmr.getMarkColumnName(), 0 + "", 0,
                                (int) fragmentOmr.getMarkOrderIndex());

                        } else {

                            Vector<String> capturedValues = new Vector<String>();
                            for (CheckBox cb : cbc) {
                                if (cb.getCheckBoxMarked() > 0) {
                                    capturedValues.add(cb.getCheckBoxValue());
                                }
                            }
                            String[] capturedValuesArr = new String[capturedValues.size()];
                            for (int i = 0; i < capturedValuesArr.length; ++i) {
                                capturedValuesArr[i] = capturedValues.get(i);
                            }
                            export.addData(cdfnField, Misc.implode(capturedValuesArr, ","), 0,
                                (int) fragmentOmr.getOrderIndex());
                            export.addData(fragmentOmr.getMarkColumnName(),
                                fragmentOmr.getMark() + "", 0,
                                (int) fragmentOmr.getMarkOrderIndex());

                        }
                    }
                }
            }

            List<Column> sortedData = export.getSortedData();
            for (Column column : sortedData) {
                int orderIndex = column.getOrder();
                String fieldname = column.getFieldname();
                String data = column.getColumnValue(0);
                if (data == null) {
                    data = "";
                }
                dtm.addRow(new String[] {orderIndex + "", fieldname, data});
            }

        } catch (org.apache.openjpa.persistence.PersistenceException pe) {

            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(pe);

            return dtm;

        } finally {
            entityManager.close();
        }

        return dtm;
    }

    public byte[] getImage() {

        if (getSelectedIds() == null || getSelectedIds().length <= 0) {
            return null;
        }

        EntityManager entityManager = Main.getInstance().getJPAConfiguration().getEntityManager();

        if (entityManager == null) {
            return null;
        }

        FormPage formPage = entityManager.find(FormPage.class, getSelectedIds()[0]);

        List<ProcessedImage> pic = formPage.getProcessedImageCollection();
        if (pic == null || pic.size() <= 0) {
            return null;
        }

        Iterator<ProcessedImage> fpi = pic.iterator();

        if (fpi.hasNext()) {
            ProcessedImage pi = fpi.next();
            return pi.getProcessedImageData();
        } else {
            return null;
        }

    }


}

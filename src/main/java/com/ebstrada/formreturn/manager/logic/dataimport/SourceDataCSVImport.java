package com.ebstrada.formreturn.manager.logic.dataimport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import au.com.bytecode.opencsv.CSVReader;

import com.ebstrada.formreturn.manager.persistence.jpa.DataSet;
import com.ebstrada.formreturn.manager.persistence.jpa.Record;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceText;
import com.ebstrada.formreturn.manager.util.UnicodeReader;

public class SourceDataCSVImport {

    public void process(File unprocessedFile, String name, EntityManager entityManager,
        DataSet dataSet) throws IOException {

        CSVReader reader = null;
        FileInputStream fis = null;
        UnicodeReader ucr = null;

        try {
            String separator = ",";
            String quotechar = "'";

            fis = new FileInputStream(unprocessedFile);
            ucr = new UnicodeReader(fis, "UTF-8");

            if (quotechar.length() > 0) {
                reader = new CSVReader(ucr, separator.charAt(0), quotechar.charAt(0));
            } else {
                reader = new CSVReader(ucr, separator.charAt(0));
            }

            String[] nextLine;
            String[] columnNames;

            columnNames = reader.readNext();
            if (columnNames == null) {
                return;
            }

            int rowNumber = 1;
            while ((nextLine = reader.readNext()) != null) {
                createNewRecord(dataSet, columnNames, nextLine, entityManager);
                rowNumber++;
            }

        } catch (FileNotFoundException ex) {
            throw ex;
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
            if (ucr != null) {
                try {
                    ucr.close();
                } catch (IOException e) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }

    }

    public void createNewRecord(DataSet dataSet, String[] columnNames, String[] nextLine,
        EntityManager entityManager) {

        Record record = createNewRecord(dataSet, entityManager);

        for (int i = 0; i < columnNames.length; i++) {

            Query sourceFieldQuery =
                entityManager.createNamedQuery("SourceField.findBySourceFieldName");
            sourceFieldQuery.setParameter("sourceFieldName", columnNames[i].trim());
            sourceFieldQuery.setParameter("dataSetId", dataSet);

            List resultList = sourceFieldQuery.getResultList();
            SourceField sourceField;

            if (resultList.size() <= 0) {
                sourceField = new SourceField();
                sourceField.setDataSetId(dataSet);
                sourceField.setSourceFieldName(columnNames[i].trim());
                sourceField.setOrderIndex(i + 1);
                // TODO: this needs to be set when defining the field type
                sourceField.setSourceFieldType("STRING");
                entityManager.persist(sourceField);
                entityManager.flush();
            } else {
                Object[] sourceFields = resultList.toArray();
                sourceField = (SourceField) sourceFields[0];
            }
            SourceText sourceText = new SourceText();
            sourceText.setSourceFieldId(sourceField);
            try {
                sourceText.setSourceTextString(nextLine[i]);
            } catch (Exception ex) {
                sourceText.setSourceTextString("");
            }
            sourceText.setRecordId(record);
            entityManager.persist(sourceText);
            entityManager.flush();
        }

    }

    public Record createNewRecord(DataSet dataSet, EntityManager entityManager) {
        Record newRecord = new Record();
        newRecord.setRecordCreated(new Timestamp(System.currentTimeMillis()));
        newRecord.setRecordModified(new Timestamp(System.currentTimeMillis()));
        newRecord.setDataSetId(dataSet);
        entityManager.persist(newRecord);
        entityManager.flush();
        return newRecord;
    }

}

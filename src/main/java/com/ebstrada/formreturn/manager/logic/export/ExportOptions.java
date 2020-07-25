package com.ebstrada.formreturn.manager.logic.export;

import java.util.ArrayList;

import com.ebstrada.formreturn.manager.logic.export.filter.Filter;
import com.ebstrada.formreturn.manager.logic.export.image.ImageExportPreferences;
import com.ebstrada.formreturn.manager.logic.export.stats.StatisticMap;
import com.ebstrada.formreturn.manager.logic.export.xml.XMLExportPreferences;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.ebstrada.formreturn.manager.util.preferences.persistence.CSVExportPreferences;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("exportOptions") public class ExportOptions implements NoObfuscation {

    public static final int EXPORT_CSV = 0;
    public static final int EXPORT_CSV_WITH_STATS = 6;
    public static final int EXPORT_JAR_FROM_DATABASE = 4;
    public static final int EXPORT_JAR_FROM_FILE = 5;
    public static final int EXPORT_XML = 1;
    public static final int EXPORT_XSLFO_FROM_DATABASE = 2;
    public static final int EXPORT_XSLFO_FROM_FILE = 3;
    public static final int IMAGE_EXPORT = 7;

    private ArrayList<Long> publicationIds;

    private int exportType = ExportOptions.EXPORT_CSV;

    private ArrayList<Filter> filters;

    private CSVExportPreferences csvExportPreferences;
    private ImageExportPreferences imageExportPreferences;
    private XMLExportPreferences xmlExportPreferences;

    private String csvFile;
    private String csvStatsFile;
    private String pdfFile;
    private String xmlFile;
    private String xslFile;

    public CSVExportPreferences getCsvExportPreferences() {
        return csvExportPreferences;
    }

    public String getCsvFile() {
        return csvFile;
    }

    public String getCsvStatsFile() {
        return csvStatsFile;
    }

    public int getExportType() {
        return exportType;
    }

    public ArrayList<Filter> getFilters() {
        return filters;
    }

    public ImageExportPreferences getImageExportPreferences() {
        return imageExportPreferences;
    }

    public String getPdfFile() {
        return pdfFile;
    }

    public ArrayList<Long> getPublicationIds() {
        return publicationIds;
    }

    public XMLExportPreferences getXmlExportPreferences() {
        return this.xmlExportPreferences;
    }

    public String getXmlFile() {
        return xmlFile;
    }

    public String getXslFile() {
        return xslFile;
    }

    public void setCsvExportPreferences(CSVExportPreferences csvExportPreferences) {
        this.csvExportPreferences = csvExportPreferences;
    }

    public void setCsvFile(String csvFile) {
        this.csvFile = csvFile;
    }

    public void setCsvStatsFile(String csvStatsFile) {
        this.csvStatsFile = csvStatsFile;
    }

    public void setExportType(int exportType) {
        this.exportType = exportType;
    }

    public void setFilters(ArrayList<Filter> filters) {
        this.filters = filters;
    }

    public void setImageExportPreferences(ImageExportPreferences imageExportPreferences) {
        this.imageExportPreferences = imageExportPreferences;
    }

    public void setPdfFile(String pdfFile) {
        this.pdfFile = pdfFile;
    }

    public void setPublicationIds(ArrayList<Long> publicationIds) {
        this.publicationIds = publicationIds;
    }

    public void setXmlExportPerferences(XMLExportPreferences xmlExportPreferences) {
        this.xmlExportPreferences = xmlExportPreferences;
    }

    public void setXmlFile(String xmlFile) {
        this.xmlFile = xmlFile;
    }

    public void setXslFile(String xslFile) {
        this.xslFile = xslFile;
    }

}

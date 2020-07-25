package com.ebstrada.formreturn.manager.logic.export.xml;

import com.ebstrada.formreturn.manager.ui.editor.persistence.XSLTemplate;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("xmlExportPreferences") public class XMLExportPreferences implements NoObfuscation {

    private boolean includeIndividualScores = true;

    private boolean includeIndividualResponses = true;

    private boolean includeStatistics = true;

    private boolean includeXMLHeader = true;

    private boolean indentXMLContent = true;

    private boolean timestampFilenamePrefix = false;

    private XSLTemplate selectedXSLTemplate;

    public boolean isTimestampFilenamePrefix() {
        return timestampFilenamePrefix;
    }

    public void setTimestampFilenamePrefix(boolean timestampFilenamePrefix) {
        this.timestampFilenamePrefix = timestampFilenamePrefix;
    }

    public boolean isIncludeIndividualScores() {
        return includeIndividualScores;
    }

    public void setIncludeIndividualScores(boolean includeIndividualScores) {
        this.includeIndividualScores = includeIndividualScores;
    }

    public boolean isIncludeIndividualResponses() {
        return includeIndividualResponses;
    }

    public void setIncludeIndividualResponses(boolean includeIndividualResponses) {
        this.includeIndividualResponses = includeIndividualResponses;
    }

    public boolean isIncludeStatistics() {
        return includeStatistics;
    }

    public void setIncludeStatistics(boolean includeStatistics) {
        this.includeStatistics = includeStatistics;
    }

    public boolean isIncludeXMLHeader() {
        return includeXMLHeader;
    }

    public void setIncludeXMLHeader(boolean includeXMLHeader) {
        this.includeXMLHeader = includeXMLHeader;
    }

    public boolean isIndentXMLContent() {
        return indentXMLContent;
    }

    public void setIndentXMLContent(boolean indentXMLContent) {
        this.indentXMLContent = indentXMLContent;
    }

    public XSLTemplate getSelectedXSLTemplate() {
        return selectedXSLTemplate;
    }

    public void setSelectedXSLTemplate(XSLTemplate selectedXSLTemplate) {
        this.selectedXSLTemplate = selectedXSLTemplate;
    }

}

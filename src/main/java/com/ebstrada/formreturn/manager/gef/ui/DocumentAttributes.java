package com.ebstrada.formreturn.manager.gef.ui;

import java.io.IOException;
import java.util.ArrayList;

import com.ebstrada.formreturn.manager.logic.recognition.structure.PublicationRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.ui.editor.persistence.FieldnameDuplicatePresets;
import com.ebstrada.formreturn.manager.ui.editor.persistence.JARPlugin;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkingProperties;
import com.ebstrada.formreturn.manager.ui.editor.persistence.Plugins;
import com.ebstrada.formreturn.manager.ui.editor.persistence.XSLTemplate;
import com.ebstrada.formreturn.manager.ui.editor.persistence.Templates;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.ebstrada.formreturn.manager.util.RandomGUID;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class DocumentAttributes implements NoObfuscation {

    public static final int FORM = 1;

    public static final int SEGMENT = 2;

    public static final int REPROCESSOR = 3;

    private String name;

    private String description;

    private String comments;

    private String author;

    private String company;

    private String copyright;

    private String GUID = (new RandomGUID()).toString();

    private int documentType;

    private PublicationRecognitionStructure publicationRecognitionStructure;

    private FieldnameDuplicatePresets fieldnameDuplicatePresets;

    private MarkingProperties markingProperties;

    private Templates xslTemplates;

    private Plugins jarPlugins;

    private String sourceDataTableFilterRegex;

    public DocumentAttributes() {
        name = "null";
        documentType = DocumentAttributes.FORM;
    }

    public void setCompany(String newCompany) {
        company = newCompany;
    }

    public String getCompany() {
        return company;
    }

    public void setCopyright(String newCopyright) {
        copyright = newCopyright;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setDescription(String newDescription) {
        description = newDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setComments(String newComments) {
        comments = newComments;
    }

    public String getComments() {
        return comments;
    }

    public void setAuthor(String newAuthor) {
        author = newAuthor;
    }

    public String getAuthor() {
        return author;
    }

    public String getSourceDataTableFilterRegex() {
        if (this.sourceDataTableFilterRegex == null) {
            return "";
        }
        return sourceDataTableFilterRegex;
    }

    public void setSourceDataTableFilterRegex(String sourceDataTableFilterRegex) {
        this.sourceDataTableFilterRegex = sourceDataTableFilterRegex;
    }

    public void setDocumentType(int newDocumentType) {
        switch (newDocumentType) {
            case DocumentAttributes.FORM:
                documentType = DocumentAttributes.FORM;
                break;
            case DocumentAttributes.SEGMENT:
                documentType = DocumentAttributes.SEGMENT;
                break;
            default:
                documentType = DocumentAttributes.FORM;
        }
    }

    public int getDocumentType() {
        return documentType;
    }

    public void setName(String newName) {
        name = newName;
    }

    public String getName() {
        return name;
    }

    public String getGUID() {
        if (GUID == null) {
            newGUID();
        }
        return GUID;
    }

    public void newGUID() {
        GUID = (new RandomGUID()).toString();
    }

    public String getDefaultCapturedDataFieldname() {
        FieldnameDuplicatePresets fdp = getFieldnameDuplicatePresets();
        return fdp.getFieldname();
    }

    public String getDefaultBarcodeCapturedDataFieldname() {
        FieldnameDuplicatePresets fdp = getFieldnameDuplicatePresets();
        return fdp.getBarcodeFieldname();
    }

    public void setDefaultCapturedDataFieldname(String defaultCapturedDataFieldname) {
        FieldnameDuplicatePresets fdp = getFieldnameDuplicatePresets();
        fdp.setFieldname(defaultCapturedDataFieldname);
    }

    public int getDefaultCDFNIncrementor() {
        FieldnameDuplicatePresets fdp = getFieldnameDuplicatePresets();
        return fdp.getCounterStart();
    }

    public void setDefaultCDFNIncrementor(int defaultCDFNIncrementor) {
        FieldnameDuplicatePresets fdp = getFieldnameDuplicatePresets();
        fdp.setCounterStart(defaultCDFNIncrementor);
    }

    public MarkingProperties getMarkingProperties() {
        if (this.markingProperties == null) {
            this.markingProperties = new MarkingProperties();
        }
        return this.markingProperties;
    }

    public Templates getXSLTemplates() {
        if (this.xslTemplates == null) {
            this.xslTemplates = new Templates();
        }
        return this.xslTemplates;
    }

    public void setMarkingProperties(MarkingProperties markingProperties) {
        this.markingProperties = markingProperties;
    }

    public PublicationRecognitionStructure getPublicationRecognitionStructure() {

        if (publicationRecognitionStructure == null) {

            // clone the preferences manager recognition structure
            PublicationRecognitionStructure prs =
                PreferencesManager.getPublicationRecognitionStructure();

            publicationRecognitionStructure = new PublicationRecognitionStructure();
            publicationRecognitionStructure.setDeskewThreshold(prs.getDeskewThreshold());
            publicationRecognitionStructure.setLuminanceCutOff(prs.getLuminanceCutOff());
            publicationRecognitionStructure.setMarkThreshold(prs.getMarkThreshold());
            publicationRecognitionStructure.setFragmentPadding(prs.getFragmentPadding());
            publicationRecognitionStructure.setPerformDeskew(prs.isPerformDeskew());

        }

        return publicationRecognitionStructure;
    }

    public void setPublicationRecognitionStructure(
        PublicationRecognitionStructure publicationRecognitionStructure) {
        this.publicationRecognitionStructure = publicationRecognitionStructure;
    }

    public FieldnameDuplicatePresets getFieldnameDuplicatePresets() {

        if (fieldnameDuplicatePresets == null) {

            // make a clone
            FieldnameDuplicatePresets fdp = PreferencesManager.getFieldnameDupliatePresets();
            fieldnameDuplicatePresets = new FieldnameDuplicatePresets();
            fieldnameDuplicatePresets.setFieldname(fdp.getFieldname());
            fieldnameDuplicatePresets.setCounterStart(fdp.getCounterStart());
            fieldnameDuplicatePresets.setHorizontalDuplicates(fdp.getHorizontalDuplicates());
            fieldnameDuplicatePresets.setHorizontalSpacing(fdp.getHorizontalSpacing());
            fieldnameDuplicatePresets.setVerticalDuplicates(fdp.getVerticalDuplicates());
            fieldnameDuplicatePresets.setVerticalSpacing(fdp.getVerticalSpacing());
            fieldnameDuplicatePresets.setNamingDirection(fdp.getNamingDirection());

        }

        return fieldnameDuplicatePresets;
    }

    public void setFieldnameDuplicatePresets(FieldnameDuplicatePresets fieldnameDuplicatePresets) {
        this.fieldnameDuplicatePresets = fieldnameDuplicatePresets;
    }

    public void checkCopyXSLTemplates(Document document, String workingDirName,
        Templates newXSLTemplates) throws IOException {
        ArrayList<String> guids = new ArrayList<String>();
        for (XSLTemplate xslTemplate : newXSLTemplates.getXSLTemplates()) {
            guids.add(xslTemplate.getTemplateGUID());
        }
        document.removeMissingXSLFiles(guids, workingDirName);
        document.copyXSLFiles(workingDirName, newXSLTemplates.getXSLTemplates());
    }

    public void setXSLTemplates(Document document, String workingDirName, Templates newXSLTemplates)
        throws Exception {
        try {
            checkCopyXSLTemplates(document, workingDirName, newXSLTemplates);
            this.xslTemplates = newXSLTemplates;
        } catch (Exception ex) {
            throw ex;
        }
    }

    public void setJARPlugins(Document document, String workingDirName, Plugins newJARPlugins)
        throws Exception {
        try {
            checkCopyJARPlugins(document, workingDirName, newJARPlugins);
            this.jarPlugins = newJARPlugins;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void checkCopyJARPlugins(Document document, String workingDirName,
        Plugins newJARPlugins) throws IOException {
        ArrayList<String> guids = new ArrayList<String>();
        for (JARPlugin jarPlugin : newJARPlugins.getJARPlugins()) {
            guids.add(jarPlugin.getPluginGUID());
        }
        document.removeMissingJARFiles(guids, workingDirName);
        document.copyJARFiles(workingDirName, newJARPlugins.getJARPlugins());
    }

    public Plugins getJARPlugins() {
        if (this.jarPlugins == null) {
            this.jarPlugins = new Plugins();
        }
        return this.jarPlugins;
    }
}

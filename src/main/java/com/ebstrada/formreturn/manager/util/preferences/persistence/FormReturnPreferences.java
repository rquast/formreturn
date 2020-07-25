package com.ebstrada.formreturn.manager.util.preferences.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.export.filter.Filter;
import com.ebstrada.formreturn.manager.logic.export.image.ImageExportPreferences;
import com.ebstrada.formreturn.manager.logic.export.xml.XMLExportPreferences;
import com.ebstrada.formreturn.manager.logic.recognition.structure.PublicationRecognitionStructure;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.persistence.FieldnameDuplicatePresets;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkAreaPresetStyle;
import com.ebstrada.formreturn.manager.ui.sdm.persistence.JDBCProfile;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.ebstrada.formreturn.manager.util.graph.SizeAttributes;
import com.ebstrada.formreturn.manager.util.graph.SizePresets;
import com.swingsane.preferences.ISwingSanePreferences;
import com.swingsane.preferences.SwingSanePreferencesImpl;
import com.swingsane.preferences.model.ApplicationPreferences;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("preferences") public class FormReturnPreferences implements NoObfuscation {

    @XStreamAlias("version") private String version = Main.VERSION;

    @XStreamAlias("recentFiles") private List<String> recentFiles = new ArrayList<String>();

    @XStreamAlias("fontPaths") private List<String> fontPaths = new ArrayList<String>();

    @XStreamAlias("jdbcProfiles") private List<JDBCProfile> jdbcProfiles =
        new ArrayList<JDBCProfile>();

    @XStreamAlias("applicationState") private ApplicationStatePreferences applicationState =
        new ApplicationStatePreferences();

    @XStreamAlias("publicationPreferences") private PublicationPreferences publicationPreferences =
        new PublicationPreferences();

    @XStreamAlias("clientDatabase") private ClientDatabasePreferences clientDatabase =
        new ClientDatabasePreferences();

    @XStreamAlias("softwareUpdatePreferences") private SoftwareUpdatePreferences
        softwareUpdatePreferences = new SoftwareUpdatePreferences();

    @XStreamAlias("markAreaPresetStyles") private List<MarkAreaPresetStyle> markAreaPresetStyles =
        new ArrayList<MarkAreaPresetStyle>();

    private Map<String, Boolean> flags = new HashMap<String, Boolean>();

    private Map<String, String> stringValues = new HashMap<String, String>();

    @XStreamAlias("defaultSegmentBarcodeSize") private double defaultSegmentBarcodeSize = 0.6;

    @XStreamAlias("defaultSizeAttributes") private Map<String, SizeAttributes>
        defaultSizeAttributes = new HashMap<String, SizeAttributes>();

    @XStreamAlias("formSizeAttributes") private List<SizeAttributes[]> formSizeAttributes =
        new ArrayList<SizeAttributes[]>();

    @XStreamAlias("segmentSizeAttributes") private List<SizeAttributes[]> segmentSizeAttributes =
        new ArrayList<SizeAttributes[]>();

    @XStreamAlias("hiddenFields") private List<String> hiddenFields = new ArrayList<String>();

    private PublicationRecognitionStructure publicationRecognitionStructure;

    private FieldnameDuplicatePresets fieldnameDuplicatePresets;

    private CSVExportPreferences exportPreferences;

    private ScannerPreferences scannerPreferences;

    @XStreamAlias("useCJKFont") private Boolean useCJKFont = true;

    private ImageExportPreferences imageExportPreferences;

    private XMLExportPreferences xmlExportPreferences;

    private ArrayList<Filter> exportFilterPreferences;

    private ApplicationPreferences swingSaneApplicationPreferences;

    public SizeAttributes getFormSizeAttribute(String attributeName, int orientation) {

        List<SizeAttributes[]> formSizeAttributes = getFormSizeAttributes();

        for (SizeAttributes[] formSizeAttributePair : formSizeAttributes) {
            SizeAttributes portraitFormSizeAttribute =
                formSizeAttributePair[(SizeAttributes.PORTRAIT - 1)];
            if (attributeName.trim().length() == 0 || portraitFormSizeAttribute.getName().trim().equalsIgnoreCase(attributeName.trim())) {
                return formSizeAttributePair[(orientation - 1)];
            }
        }

        // return the default if can't find
        SizeAttributes defaultAttributes = defaultSizeAttributes.get("formSizeAttributes");

        return defaultAttributes;

    }

    public SizeAttributes getSegmentSizeAttribute(String attributeName, int orientation) {

        List<SizeAttributes[]> segmentSizeAttributes = getSegmentSizeAttributes();

        for (SizeAttributes[] segmentSizeAttributePair : segmentSizeAttributes) {
            SizeAttributes portraitSegmentSizeAttribute =
                segmentSizeAttributePair[(SizeAttributes.PORTRAIT - 1)];
            if (attributeName.trim().length() == 0 || portraitSegmentSizeAttribute.getName().trim()
                .equalsIgnoreCase(attributeName.trim())) {
                return segmentSizeAttributePair[(orientation - 1)];
            }
        }

        // return the default if can't find
        SizeAttributes defaultAttributes = defaultSizeAttributes.get("segmentSizeAttributes");

        return defaultAttributes;

    }

    public void addRecentFile(String recentFile) {

        if (recentFiles == null) {
            recentFiles = new ArrayList<String>();
        }

        if (recentFiles.contains(recentFile)) {
            return;
        }
        recentFiles.add(recentFile);
        if (recentFiles.size() > 10) {
            recentFiles.remove(0);
        }
    }

    public List<String> getRecentFiles() {

        if (recentFiles == null) {
            recentFiles = new ArrayList<String>();
        }

        return recentFiles;
    }

    public List<String> getHiddenFields() {
        if (this.hiddenFields == null) {
            this.hiddenFields = new ArrayList<String>();
        }
        return hiddenFields;
    }

    public void setHiddenFields(List<String> hiddenFields) {
        this.hiddenFields = hiddenFields;
    }

    public void addMarkAreaPresetStyle(MarkAreaPresetStyle markAreaPresetStyle) {
        if (markAreaPresetStyles == null) {
            markAreaPresetStyles = new ArrayList<MarkAreaPresetStyle>();
        }
        if (markAreaPresetStyles.contains(markAreaPresetStyle)) {
            return;
        }
        markAreaPresetStyles.add(markAreaPresetStyle);
    }

    public void removeMarkAreaPresetStyle(MarkAreaPresetStyle markAreaPresetStyle) {
        if (markAreaPresetStyles == null) {
            markAreaPresetStyles = new ArrayList<MarkAreaPresetStyle>();
        }
        if (!(markAreaPresetStyles.contains(markAreaPresetStyle))) {
            return;
        }
        markAreaPresetStyles.remove(markAreaPresetStyle);
    }

    public List<MarkAreaPresetStyle> getMarkAreaPresetStyles() {
        if (markAreaPresetStyles == null) {
            markAreaPresetStyles = new ArrayList<MarkAreaPresetStyle>();
        }
        return markAreaPresetStyles;
    }

    public void addJDBCProfile(JDBCProfile jdbcProfile) {
        if (jdbcProfiles == null) {
            jdbcProfiles = new ArrayList<JDBCProfile>();
        }
        if (jdbcProfiles.contains(jdbcProfile)) {
            return;
        }
        jdbcProfiles.add(jdbcProfile);
    }

    public void removeJDBCProfile(JDBCProfile jdbcProfile) {
        if (jdbcProfiles == null) {
            jdbcProfiles = new ArrayList<JDBCProfile>();
        }
        if (!(jdbcProfiles.contains(jdbcProfile))) {
            return;
        }
        jdbcProfiles.remove(jdbcProfile);
    }

    public List<JDBCProfile> getJDBCProfiles() {
        if (jdbcProfiles == null) {
            jdbcProfiles = new ArrayList<JDBCProfile>();
        }
        return jdbcProfiles;
    }

    public void addFontPath(String fontPath) {

        if (fontPaths == null) {
            fontPaths = new ArrayList<String>();
        }

        if (fontPaths.contains(fontPath)) {
            return;
        }
        fontPaths.add(fontPath);
    }

    public void removeFontPath(String fontPath) {

        if (fontPaths == null) {
            fontPaths = new ArrayList<String>();
        }

        if (!(fontPaths.contains(fontPath))) {
            return;
        }
        fontPaths.remove(fontPath);
    }

    public List<String> getFontPaths() {

        if (fontPaths == null) {
            fontPaths = new ArrayList<String>();
        }

        return fontPaths;
    }

    public void setFontPaths(ArrayList<String> fontPaths) {
        this.fontPaths = fontPaths;
    }

    public boolean getFlag(String flagName) {

        if (flags == null) {
            flags = new HashMap<String, Boolean>();
        }

        Boolean value = flags.get(flagName);
        if (value == null) {
            return false;
        }

        return flags.get(flagName);
    }

    public void setFlag(String flagName, boolean value) {

        if (flags == null) {
            flags = new HashMap<String, Boolean>();
        }

        flags.put(flagName, new Boolean(value));
    }

    public String getStringValue(String flagName) {

        if (stringValues == null) {
            stringValues = new HashMap<String, String>();
        }

        return stringValues.get(flagName);
    }

    public void setStringValue(String flagName, String value) {

        if (stringValues == null) {
            stringValues = new HashMap<String, String>();
        }

        stringValues.put(flagName, value);
    }

    public void removeAllRecentFiles() {
        recentFiles = new ArrayList<String>();
    }

    public ClientDatabasePreferences getClientDatabase() {
        if (clientDatabase == null) {
            clientDatabase = new ClientDatabasePreferences();
        }
        return clientDatabase;
    }

    public void setClientDatabase(ClientDatabasePreferences clientDatabase) {
        this.clientDatabase = clientDatabase;
    }

    public SoftwareUpdatePreferences getSoftwareUpdatePreferences() {
        if (softwareUpdatePreferences == null) {
            softwareUpdatePreferences = new SoftwareUpdatePreferences();
        }
        return softwareUpdatePreferences;
    }

    public void setSoftwareUpdatePreferences(SoftwareUpdatePreferences softwareUpdatePreferences) {
        this.softwareUpdatePreferences = softwareUpdatePreferences;
    }

    public PublicationRecognitionStructure getPublicationRecognitionStructure() {

        if (publicationRecognitionStructure == null) {
            publicationRecognitionStructure = new PublicationRecognitionStructure();
            publicationRecognitionStructure.setDeskewThreshold(1.05);
            publicationRecognitionStructure.setLuminanceCutOff(200);
            publicationRecognitionStructure.setMarkThreshold(40);
            publicationRecognitionStructure.setFragmentPadding(1);
            publicationRecognitionStructure.setPerformDeskew(true);
        }

        return publicationRecognitionStructure;

    }

    public void setPublicationRecognitionStructure(
        PublicationRecognitionStructure publicationRecognitionStructure) {
        this.publicationRecognitionStructure = publicationRecognitionStructure;
    }

    public FieldnameDuplicatePresets getFieldnameDupliatePresets() {

        if (fieldnameDuplicatePresets == null) {
            fieldnameDuplicatePresets = new FieldnameDuplicatePresets();
            fieldnameDuplicatePresets.setCounterStart(1);
            fieldnameDuplicatePresets
                .setFieldname(Localizer.localize("Util", "DefaultFieldnamePrefix"));
            fieldnameDuplicatePresets.setHorizontalDuplicates(1);
            fieldnameDuplicatePresets.setVerticalDuplicates(1);
            fieldnameDuplicatePresets.setHorizontalSpacing(20);
            fieldnameDuplicatePresets.setVerticalSpacing(20);
            fieldnameDuplicatePresets.setNamingDirection(
                FieldnameDuplicatePresets.DIRECTION_TOP_TO_BOTTOM_LEFT_TO_RIGHT);
        }

        return fieldnameDuplicatePresets;
    }

    public void setFieldnameDuplicatePresets(FieldnameDuplicatePresets fieldnameDuplicatePresets) {
        this.fieldnameDuplicatePresets = fieldnameDuplicatePresets;
    }

    public List<SizeAttributes[]> getFormSizeAttributes() {

        if (formSizeAttributes == null || formSizeAttributes.size() <= 0) {
            List<SizeAttributes[]> presetFormSizeAttributes = SizePresets.getFormSizeAttributes();

            formSizeAttributes = new ArrayList<SizeAttributes[]>();

            // clone this...
            for (SizeAttributes[] presetFormSizeAttributePair : presetFormSizeAttributes) {
                SizeAttributes[] cloneSizeAttributes = new SizeAttributes[2];

                SizeAttributes portraitSizeAttributes = new SizeAttributes();
                portraitSizeAttributes.setOrientation(SizeAttributes.PORTRAIT);
                portraitSizeAttributes.setBottomMargin(
                    presetFormSizeAttributePair[((SizeAttributes.PORTRAIT - 1))].getBottomMargin());
                portraitSizeAttributes.setTopMargin(
                    presetFormSizeAttributePair[((SizeAttributes.PORTRAIT - 1))].getTopMargin());
                portraitSizeAttributes.setLeftMargin(
                    presetFormSizeAttributePair[((SizeAttributes.PORTRAIT - 1))].getLeftMargin());
                portraitSizeAttributes.setRightMargin(
                    presetFormSizeAttributePair[((SizeAttributes.PORTRAIT - 1))].getRightMargin());
                portraitSizeAttributes.setWidth(
                    presetFormSizeAttributePair[((SizeAttributes.PORTRAIT - 1))].getWidth());
                portraitSizeAttributes.setHeight(
                    presetFormSizeAttributePair[((SizeAttributes.PORTRAIT - 1))].getHeight());
                portraitSizeAttributes.setName(
                    presetFormSizeAttributePair[((SizeAttributes.PORTRAIT - 1))].getName());

                SizeAttributes landscapeSizeAttributes = new SizeAttributes();
                landscapeSizeAttributes.setOrientation(SizeAttributes.LANDSCAPE);
                landscapeSizeAttributes.setBottomMargin(
                    presetFormSizeAttributePair[((SizeAttributes.LANDSCAPE - 1))]
                        .getBottomMargin());
                landscapeSizeAttributes.setTopMargin(
                    presetFormSizeAttributePair[((SizeAttributes.LANDSCAPE - 1))].getTopMargin());
                landscapeSizeAttributes.setLeftMargin(
                    presetFormSizeAttributePair[((SizeAttributes.LANDSCAPE - 1))].getLeftMargin());
                landscapeSizeAttributes.setRightMargin(
                    presetFormSizeAttributePair[((SizeAttributes.LANDSCAPE - 1))].getRightMargin());
                landscapeSizeAttributes.setWidth(
                    presetFormSizeAttributePair[((SizeAttributes.LANDSCAPE - 1))].getWidth());
                landscapeSizeAttributes.setHeight(
                    presetFormSizeAttributePair[((SizeAttributes.LANDSCAPE - 1))].getHeight());
                landscapeSizeAttributes.setName(
                    presetFormSizeAttributePair[((SizeAttributes.LANDSCAPE - 1))].getName());


                cloneSizeAttributes[(SizeAttributes.PORTRAIT - 1)] = portraitSizeAttributes;
                cloneSizeAttributes[(SizeAttributes.LANDSCAPE - 1)] = landscapeSizeAttributes;

                formSizeAttributes.add(cloneSizeAttributes);
            }

        }

        return formSizeAttributes;

    }

    public void setFormSizeAttributes(List<SizeAttributes[]> formSizeAttributes) {
        this.formSizeAttributes = formSizeAttributes;
    }

    public List<String> getFormSizeNames() {
        ArrayList<String> formSizeNames = new ArrayList<String>();
        if (formSizeAttributes == null || formSizeAttributes.size() <= 0) {
            getFormSizeAttributes();
        }

        for (SizeAttributes[] formSizeAttributePair : formSizeAttributes) {
            SizeAttributes portraitFormSizeAttribute =
                formSizeAttributePair[(SizeAttributes.PORTRAIT - 1)];
            formSizeNames.add(portraitFormSizeAttribute.getName());
        }

        formSizeNames.add("Custom");

        return formSizeNames;
    }

    public List<String> getSegmentSizeNames() {
        ArrayList<String> segmentSizeNames = new ArrayList<String>();
        if (segmentSizeAttributes == null || segmentSizeAttributes.size() <= 0) {
            getSegmentSizeAttributes();
        }

        for (SizeAttributes[] segmentSizeAttributePair : segmentSizeAttributes) {
            SizeAttributes portraitFormSizeAttribute =
                segmentSizeAttributePair[(SizeAttributes.PORTRAIT - 1)];
            segmentSizeNames.add(portraitFormSizeAttribute.getName());
        }

        segmentSizeNames.add("Custom");

        return segmentSizeNames;
    }

    public void resetSegmentSizeAttributes() {
        segmentSizeAttributes = null;
        getSegmentSizeAttributes();
    }

    public void resetFormSizeAttributes() {
        formSizeAttributes = null;
        getFormSizeAttributes();
    }

    public List<SizeAttributes[]> getSegmentSizeAttributes() {

        if (segmentSizeAttributes == null || segmentSizeAttributes.size() <= 0) {
            List<SizeAttributes[]> presetSegmentSizeAttributes =
                SizePresets.getSegmentSizeAttributes();

            segmentSizeAttributes = new ArrayList<SizeAttributes[]>();

            // clone this...
            for (SizeAttributes[] presetSegmentSizeAttributePair : presetSegmentSizeAttributes) {
                SizeAttributes[] cloneSizeAttributes = new SizeAttributes[2];

                SizeAttributes portraitSizeAttributes = new SizeAttributes();
                portraitSizeAttributes.setOrientation(SizeAttributes.PORTRAIT);
                portraitSizeAttributes.setBottomMargin(
                    presetSegmentSizeAttributePair[(SizeAttributes.PORTRAIT - 1)]
                        .getBottomMargin());
                portraitSizeAttributes.setTopMargin(
                    presetSegmentSizeAttributePair[(SizeAttributes.PORTRAIT - 1)].getTopMargin());
                portraitSizeAttributes.setLeftMargin(
                    presetSegmentSizeAttributePair[(SizeAttributes.PORTRAIT - 1)].getLeftMargin());
                portraitSizeAttributes.setRightMargin(
                    presetSegmentSizeAttributePair[(SizeAttributes.PORTRAIT - 1)].getRightMargin());
                portraitSizeAttributes.setWidth(
                    presetSegmentSizeAttributePair[(SizeAttributes.PORTRAIT - 1)].getWidth());
                portraitSizeAttributes.setHeight(
                    presetSegmentSizeAttributePair[(SizeAttributes.PORTRAIT - 1)].getHeight());
                portraitSizeAttributes.setName(
                    presetSegmentSizeAttributePair[(SizeAttributes.PORTRAIT - 1)].getName());

                SizeAttributes landscapeSizeAttributes = new SizeAttributes();
                landscapeSizeAttributes.setOrientation(SizeAttributes.LANDSCAPE);
                landscapeSizeAttributes.setBottomMargin(
                    presetSegmentSizeAttributePair[(SizeAttributes.LANDSCAPE - 1)]
                        .getBottomMargin());
                landscapeSizeAttributes.setTopMargin(
                    presetSegmentSizeAttributePair[(SizeAttributes.LANDSCAPE - 1)].getTopMargin());
                landscapeSizeAttributes.setLeftMargin(
                    presetSegmentSizeAttributePair[(SizeAttributes.LANDSCAPE - 1)].getLeftMargin());
                landscapeSizeAttributes.setRightMargin(
                    presetSegmentSizeAttributePair[(SizeAttributes.LANDSCAPE - 1)]
                        .getRightMargin());
                landscapeSizeAttributes.setWidth(
                    presetSegmentSizeAttributePair[(SizeAttributes.LANDSCAPE - 1)].getWidth());
                landscapeSizeAttributes.setHeight(
                    presetSegmentSizeAttributePair[(SizeAttributes.LANDSCAPE - 1)].getHeight());
                landscapeSizeAttributes.setName(
                    presetSegmentSizeAttributePair[(SizeAttributes.LANDSCAPE - 1)].getName());

                cloneSizeAttributes[(SizeAttributes.PORTRAIT - 1)] = portraitSizeAttributes;
                cloneSizeAttributes[(SizeAttributes.LANDSCAPE - 1)] = landscapeSizeAttributes;

                segmentSizeAttributes.add(cloneSizeAttributes);
            }

        }

        return segmentSizeAttributes;
    }

    public void setSegmentSizeAttributes(List<SizeAttributes[]> segmentSizeAttributes) {
        this.segmentSizeAttributes = segmentSizeAttributes;
    }

    public double getDefaultSegmentBarcodeSize() {

        if (defaultSegmentBarcodeSize <= 0.1) {
            defaultSegmentBarcodeSize = 0.6;
        }

        return defaultSegmentBarcodeSize;
    }

    public void setDefaultSegmentBarcodeSize(double defaultSegmentBarcodeSize) {
        this.defaultSegmentBarcodeSize = defaultSegmentBarcodeSize;
    }

    public Map<String, SizeAttributes> getDefaultSizeAttributes() {

        if (defaultSizeAttributes == null) {
            defaultSizeAttributes = new HashMap<String, SizeAttributes>();
        }

        return defaultSizeAttributes;
    }

    public void setDefaultSizeAttributes(Map<String, SizeAttributes> defaultSizeAttributes) {
        this.defaultSizeAttributes = defaultSizeAttributes;
    }

    public ApplicationStatePreferences getApplicationState() {

        if (applicationState == null) {
            applicationState = new ApplicationStatePreferences();
        }

        return applicationState;
    }

    public void setApplicationState(ApplicationStatePreferences applicationState) {
        this.applicationState = applicationState;
    }

    public PublicationPreferences getPublicationPreferences() {

        if (publicationPreferences == null) {
            publicationPreferences = new PublicationPreferences();
        }

        return publicationPreferences;
    }

    public void setPublicationPreferences(PublicationPreferences publicationPreferences) {
        this.publicationPreferences = publicationPreferences;
    }

    public CSVExportPreferences getCSVExportPreferences() {
        if (exportPreferences == null) {
            resetCSVExportPreferences();
        }

        return exportPreferences;
    }

    public void setExportPreferences(CSVExportPreferences exportPreferences) {
        this.exportPreferences = exportPreferences;
    }

    public ScannerPreferences getScannerPreferences() {

        if (scannerPreferences == null) {
            scannerPreferences = new ScannerPreferences();
        }

        return scannerPreferences;
    }

    public void setScannerPreferences(ScannerPreferences scannerPreferences) {
        this.scannerPreferences = scannerPreferences;
    }

    public void setUseCJKFont(Boolean useCJKFont) {
        this.useCJKFont = useCJKFont;
    }

    public boolean getUseCJKFont() {
        return this.useCJKFont;
    }

    public ImageExportPreferences getImageExportPreferences() {
        if (imageExportPreferences == null) {
            resetImageExportPreferences();
        }

        return imageExportPreferences;
    }

    public XMLExportPreferences getXMLExportPreferences() {
        if (xmlExportPreferences == null) {
            resetXMLExportPreferences();
        }

        return xmlExportPreferences;
    }

    public ArrayList<Filter> getExportFilterPreferences() {
        if (exportFilterPreferences == null) {
            exportFilterPreferences = new ArrayList<Filter>();
        }

        return exportFilterPreferences;
    }

    public void resetCSVExportPreferences() {
        exportPreferences = new CSVExportPreferences();
    }

    public void resetImageExportPreferences() {
        imageExportPreferences = new ImageExportPreferences();
    }

    public void resetXMLExportPreferences() {
        xmlExportPreferences = new XMLExportPreferences();
    }

    public void setDefaultCSVExportPreferences(CSVExportPreferences csvExportPreferences) {
        this.exportPreferences = csvExportPreferences;
    }

    public void setDefaultImageExportPreferences(ImageExportPreferences imageExportPreferences) {
        this.imageExportPreferences = imageExportPreferences;
    }

    public void setDefaultXMLExportPreferences(XMLExportPreferences xmlExportPreferences) {
        this.xmlExportPreferences = xmlExportPreferences;
    }

    public ApplicationPreferences getSwingSaneApplicationPreferences() {
        if (swingSaneApplicationPreferences == null) {
            swingSaneApplicationPreferences = new ApplicationPreferences();
        }
        return swingSaneApplicationPreferences;
    }

}

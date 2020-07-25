package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.structure.PublicationRecognitionStructure;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.editor.persistence.FieldnameDuplicatePresets;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkingRule;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkingProperties;
import com.ebstrada.formreturn.manager.ui.editor.persistence.Plugins;
import com.ebstrada.formreturn.manager.ui.editor.persistence.XSLTemplate;
import com.ebstrada.formreturn.manager.ui.editor.persistence.Templates;
import com.ebstrada.formreturn.manager.ui.frame.EditorFrame;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class FormPropertiesDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private EditorFrame selectedFrame;

    private MarkingProperties markingProperties;

    private Templates xslTemplates;

    private Plugins jarPlugins;

    public FormPropertiesDialog(Frame owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(okButton);
        localizeHeadings();
    }

    public FormPropertiesDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(okButton);
        localizeHeadings();
    }

    public void localizeHeadings() {
        documentPropertiesTabbedPane
            .setTitleAt(0, Localizer.localize("UI", "FormPropertiesDocumentDetailsTabTitle"));
        documentPropertiesTabbedPane
            .setTitleAt(1, Localizer.localize("UI", "FormPropertiesGeneralSettingsTabTitle"));
        documentPropertiesTabbedPane
            .setTitleAt(2, Localizer.localize("UI", "FormPropertiesMarkingTabTitle"));
        documentPropertiesTabbedPane
            .setTitleAt(3, Localizer.localize("UI", "FormPropertiesXSLFOReportTabTitle"));
    }

    private void okButtonActionPerformed(ActionEvent e) {
        DocumentAttributes documentAttributes = selectedFrame.getDocumentAttributes();
        documentAttributes.setName(documentNameTextField.getText());
        documentAttributes.setDescription(descriptionTextArea.getText());
        documentAttributes.setComments(commentsTextField.getText().trim());
        documentAttributes.setCompany(companyTextField.getText().trim());
        documentAttributes.setAuthor(authorTextField.getText().trim());
        documentAttributes.setCopyright(copyrightTextField.getText().trim());
        documentAttributes
            .setSourceDataTableFilterRegex(sourceDataTableFilterTextField.getText().trim());

        // set recognition settings
        PublicationRecognitionStructure prs =
            documentAttributes.getPublicationRecognitionStructure();
        prs.setLuminanceCutOff((Integer) luminanceThresholdSpinner.getValue());
        prs.setMarkThreshold((Integer) markThresholdSpinner.getValue());
        prs.setFragmentPadding((Integer) fragmentPaddingSpinner.getValue());
        prs.setDeskewThreshold((Double) deskewThresholdSpinner.getValue());
        prs.setPerformDeskew(performDeskewCheckBox.isSelected());

        // restore the fieldname duplicate presets
        FieldnameDuplicatePresets fdp = documentAttributes.getFieldnameDuplicatePresets();
        fdp.setHorizontalDuplicates((Integer) horizontalDuplicatesSpinner.getValue());
        fdp.setVerticalDuplicates((Integer) verticalDuplicatesSpinner.getValue());
        fdp.setHorizontalSpacing((Integer) horizontalSpacingSpinner.getValue());
        fdp.setVerticalSpacing((Integer) verticalSpacingSpinner.getValue());
        if (tblrButton.isSelected()) {
            fdp.setNamingDirection(FieldnameDuplicatePresets.DIRECTION_TOP_TO_BOTTOM_LEFT_TO_RIGHT);
        } else {
            fdp.setNamingDirection(FieldnameDuplicatePresets.DIRECTION_LEFT_TO_RIGHT_TOP_TO_BOTTOM);
        }

        // set the mark aggregation and grading settings
        try {
            markingProperties
                .setTotalPossibleScore(Misc.parseDoubleString(this.totalScoreTextField.getText()));
        } catch (Exception ex) {
            Misc.showErrorMsg(this, "Invalid total possible score.");
            return;
        }
        documentAttributes.setMarkingProperties(this.markingProperties);

        // set the new xsl templates
        try {
            documentAttributes.setXSLTemplates(this.selectedFrame.getGraph().getDocument(),
                this.selectedFrame.getGraph().getDocumentPackage().getWorkingDirName(),
                this.xslTemplates);
        } catch (Exception e1) {
            Misc.showExceptionMsg(this, e1);
            return;
        }

        // set the new jar plugins
        try {
            documentAttributes.setJARPlugins(this.selectedFrame.getGraph().getDocument(),
                this.selectedFrame.getGraph().getDocumentPackage().getWorkingDirName(),
                this.jarPlugins);
        } catch (Exception e1) {
            Misc.showExceptionMsg(this, e1);
            return;
        }

        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void resetRecognitionSettingsToDefaultsButtonActionPerformed(ActionEvent e) {

        String message =
            Localizer.localize("UI", "DocumentPropertiesConfirmRestoreSettingsMessage");
        int n = JOptionPane.showConfirmDialog(null, message,
            Localizer.localize("UI", "DocumentPropertiesConfirmRestoreSettingsTitle"),
            JOptionPane.YES_NO_OPTION);

        if (n != 0) {
            return;
        }

        PublicationRecognitionStructure prs =
            PreferencesManager.getPublicationRecognitionStructure();
        luminanceThresholdSpinner.setValue(new Integer(prs.getLuminanceCutOff()));
        markThresholdSpinner.setValue(new Integer(prs.getMarkThreshold()));
        fragmentPaddingSpinner.setValue(new Integer(prs.getFragmentPadding()));
        deskewThresholdSpinner.setValue(new Double(prs.getDeskewThreshold()));
        performDeskewCheckBox.setSelected(prs.isPerformDeskew());

    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                okButton.requestFocusInWindow();
            }
        });
    }

    private void addGradingRuleButtonActionPerformed(ActionEvent e) {
        final Dialog thisDialog = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                MarkingRule gradingRule = new MarkingRule();

                GradingRuleDialog grd = new GradingRuleDialog(thisDialog, gradingRule);
                grd.setModal(true);
                grd.setVisible(true);
                if (grd.getDialogResult() != JOptionPane.CANCEL_OPTION) {

                    String grade = grd.getGrade();
                    int qualifier = grd.getQualifier();
                    double threshold = grd.getThreshold();
                    int thresholdType = grd.getThresholdType();

                    gradingRule.setGrade(grade);
                    gradingRule.setQualifier(qualifier);
                    gradingRule.setThreshold(threshold);
                    gradingRule.setThresholdType(thresholdType);

                    markingProperties.getGradingRules().add(gradingRule);

                    refreshGradingRuleList();

                }
            }
        });
    }

    private void refreshGradingRuleList() {
        this.gradingList.setModel(markingProperties.getGradingRulesListModel());
        this.gradingList.validate();
    }

    private void refreshXSLTemplatesList() {
        this.xslFileList.setModel(this.xslTemplates.getXSLTemplatesListModel());
        this.xslFileList.validate();
    }

    private void removeGradingRuleButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MarkingRule gradingRule = (MarkingRule) gradingList.getSelectedValue();

                if (gradingRule == null) {
                    return;
                }

                ArrayList<MarkingRule> gradingRules = markingProperties.getGradingRules();

                if (gradingRules.contains(gradingRule)) {
                    gradingRules.remove(gradingRule);
                }

                refreshGradingRuleList();
            }
        });
    }

    private void editGradingRuleButtonActionPerformed(ActionEvent e) {
        final Dialog thisDialog = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                MarkingRule gradingRule = (MarkingRule) gradingList.getSelectedValue();

                if (gradingRule == null) {
                    return;
                }

                GradingRuleDialog grd = new GradingRuleDialog(thisDialog, gradingRule);
                grd.setModal(true);
                grd.setVisible(true);
                if (grd.getDialogResult() != JOptionPane.CANCEL_OPTION) {

                    String grade = grd.getGrade();
                    int qualifier = grd.getQualifier();
                    double threshold = grd.getThreshold();
                    int thresholdType = grd.getThresholdType();

                    gradingRule.setGrade(grade);
                    gradingRule.setQualifier(qualifier);
                    gradingRule.setThreshold(threshold);
                    gradingRule.setThresholdType(thresholdType);

                    ArrayList<MarkingRule> gradingRules = markingProperties.getGradingRules();

                    if (!(gradingRules.contains(gradingRule))) {
                        gradingRules.add(gradingRule);
                    }

                    refreshGradingRuleList();

                }
            }
        });
    }

    private void gradingRuleUpButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MarkingRule gradingRule = (MarkingRule) gradingList.getSelectedValue();

                if (gradingRule == null) {
                    return;
                }

                ArrayList<MarkingRule> gradingRules = markingProperties.getGradingRules();

                if (gradingRules.contains(gradingRule)) {
                    int selectedIndex = gradingRules.indexOf(gradingRule);
                    if (selectedIndex > 0) {
                        Collections.swap(gradingRules, selectedIndex, selectedIndex - 1);
                    }
                }

                refreshGradingRuleList();
            }
        });
    }

    private void gradingRuleDownButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MarkingRule gradingRule = (MarkingRule) gradingList.getSelectedValue();

                if (gradingRule == null) {
                    return;
                }

                ArrayList<MarkingRule> gradingRules = markingProperties.getGradingRules();

                if (gradingRules.contains(gradingRule)) {
                    int selectedIndex = gradingRules.indexOf(gradingRule);
                    if (selectedIndex < (gradingRules.size() - 1)) {
                        Collections.swap(gradingRules, selectedIndex, selectedIndex + 1);
                    }
                }

                refreshGradingRuleList();
            }
        });
    }

    private void xslFileUpButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                XSLTemplate xslTemplate = (XSLTemplate) xslFileList.getSelectedValue();

                if (xslTemplate == null) {
                    return;
                }

                ArrayList<XSLTemplate> templates = xslTemplates.getXSLTemplates();

                if (templates.contains(xslTemplate)) {
                    int selectedIndex = templates.indexOf(xslTemplate);
                    if (selectedIndex > 0) {
                        Collections.swap(templates, selectedIndex, selectedIndex - 1);
                    }
                }

                refreshXSLTemplatesList();
            }
        });
    }

    private void xslFileDownButtonActionPerformed(ActionEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                XSLTemplate xslTemplate = (XSLTemplate) xslFileList.getSelectedValue();

                if (xslTemplate == null) {
                    return;
                }

                ArrayList<XSLTemplate> templates = xslTemplates.getXSLTemplates();

                if (templates.contains(xslTemplate)) {
                    int selectedIndex = templates.indexOf(xslTemplate);
                    if (selectedIndex < (templates.size() - 1)) {
                        Collections.swap(templates, selectedIndex, selectedIndex + 1);
                    }
                }

                refreshXSLTemplatesList();
            }
        });
    }

    private void addXSLFileButtonActionPerformed(ActionEvent e) {
        final Dialog thisDialog = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                XSLTemplate xslTemplate = new XSLTemplate();

                XSLTemplateDialog xtd = new XSLTemplateDialog(thisDialog, xslTemplate);
                xtd.setModal(true);
                xtd.setVisible(true);
                if (xtd.getDialogResult() == JOptionPane.OK_OPTION) {

                    try {
                        xslTemplate.setFileName(xtd.getFileName());
                        xslTemplate.setFile(xtd.getFile());
                        xslTemplate.setDescription(xtd.getTemplateDescription());
                        xslTemplate.setGUID(xtd.getTemplateGUID());

                        for (XSLTemplate t : xslTemplates.getXSLTemplates()) {
                            if (xslTemplate.getTemplateGUID().equals(t.getTemplateGUID())) {
                                throw new Exception(
                                    "Template already in list of templates. (" + t.getFileName()
                                        + ")");
                            }
                        }

                        xslTemplates.getXSLTemplates().add(xslTemplate);
                        refreshXSLTemplatesList();

                    } catch (Exception ex) {
                        Misc.showExceptionMsg(thisDialog, ex);
                    }

                }
            }
        });
    }

    private void removeXSLFileButtonActionPerformed(ActionEvent e) {
        final Dialog thisDialog = this;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                XSLTemplate xslTemplate = (XSLTemplate) xslFileList.getSelectedValue();

                if (xslTemplate == null) {
                    return;
                }

                Object[] options =
                    {Localizer.localize("UI", "Yes"), Localizer.localize("UI", "No")};

                String msg =
                    "Are you sure you want to remove \"" + xslTemplate.getFileName() + "\"?";

                int result = JOptionPane
                    .showOptionDialog(thisDialog, msg, Localizer.localize("Util", "WarningTitle"),
                        JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options,
                        options[0]);

                if (result != 0) {
                    return;
                }

                xslTemplates.getXSLTemplates().remove(xslTemplate);
                refreshXSLTemplatesList();

            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        documentPropertiesTabbedPane = new JTabbedPane();
        documentDetailsPanel = new JPanel();
        documentTitlePanel = new JPanel();
        documentNameTextField = new JTextField();
        descriptionPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        descriptionTextArea = new JTextArea();
        otherInformationPanel = new JPanel();
        commentsLabel = new JLabel();
        commentsTextField = new JTextField();
        companyLabel = new JLabel();
        companyTextField = new JTextField();
        authorLabel = new JLabel();
        authorTextField = new JTextField();
        copyrightLabel = new JLabel();
        copyrightTextField = new JTextField();
        generalSettingsPanel = new JPanel();
        panel1 = new JPanel();
        recognitionSettingsPanel = new JPanel();
        panel8 = new JPanel();
        luminanceLabel = new JLabel();
        luminanceThresholdSpinner = new JSpinner();
        markThresholdLabel = new JLabel();
        markThresholdSpinner = new JSpinner();
        fragmentPaddingLabel = new JLabel();
        fragmentPaddingSpinner = new JSpinner();
        deskewThresholdLabel = new JLabel();
        deskewThresholdSpinner = new JSpinner();
        panel9 = new JPanel();
        performDeskewCheckBox = new JCheckBox();
        duplicationPanel = new JPanel();
        panel13 = new JPanel();
        horizontalDuplicatesLabel = new JLabel();
        horizontalDuplicatesSpinner = new JSpinner();
        verticalDuplicatesLabel = new JLabel();
        verticalDuplicatesSpinner = new JSpinner();
        horizontalSpacingLabel = new JLabel();
        horizontalSpacingSpinner = new JSpinner();
        verticalSpacingLabel = new JLabel();
        verticalSpacingSpinner = new JSpinner();
        panel6 = new JPanel();
        namingDirectionLabel = new JLabel();
        panel12 = new JPanel();
        tblrButton = new JRadioButton();
        lrtbButton = new JRadioButton();
        sourceDataTableFilterPanel = new JPanel();
        sourceDataFilterLabel = new JLabel();
        sourceDataTableFilterTextField = new JTextField();
        helpLabel = new JHelpLabel();
        markAggregationGradingPanel = new JPanel();
        gradingCalculationsPanel = new JPanel();
        gradingDescriptionPanel = new JPanel();
        gradingDescriptionLabel = new JLabel();
        gradingDescriptionHelpLabel = new JHelpLabel();
        totalPossibleScorePanel = new JPanel();
        totalPossibleFormScoreLabel = new JLabel();
        totalScoreTextField = new JTextField();
        gradingRulesLabel = new JLabel();
        gradingRulesPanel = new JPanel();
        gradingListScrollPane = new JScrollPane();
        gradingList = new JList();
        gradingRulesButtonPanel = new JPanel();
        gradingRuleUpButton = new JButton();
        gradingRuleDownButton = new JButton();
        addGradingRuleButton = new JButton();
        editGradingRuleButton = new JButton();
        removeGradingRuleButton = new JButton();
        xslFoReportsPanel = new JPanel();
        embeddedReportTemplatesPanel = new JPanel();
        xslReportDescriptionPanel = new JPanel();
        xslReportDescriptionLabel = new JLabel();
        xslReportDescriptionHelpLabel = new JHelpLabel();
        xslFileListScrollPane = new JScrollPane();
        xslFileList = new JList();
        xslFileListButtonPanel = new JPanel();
        xslFileUpButton = new JButton();
        xslFileDownButton = new JButton();
        addXSLFileButton = new JButton();
        removeXSLFileButton = new JButton();
        buttonBar = new JPanel();
        resetRecognitionSettingsToDefaultsButton = new JButton();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)contentPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== documentPropertiesTabbedPane ========
                {
                    documentPropertiesTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));

                    //======== documentDetailsPanel ========
                    {
                        documentDetailsPanel.setOpaque(false);
                        documentDetailsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                        documentDetailsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)documentDetailsPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)documentDetailsPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)documentDetailsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)documentDetailsPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

                        //======== documentTitlePanel ========
                        {
                            documentTitlePanel.setOpaque(false);
                            documentTitlePanel.setFont(UIManager.getFont("TitledBorder.font"));
                            documentTitlePanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)documentTitlePanel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)documentTitlePanel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)documentTitlePanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)documentTitlePanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};
                            documentTitlePanel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "DocumentTitleBorderTitle")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //---- documentNameTextField ----
                            documentNameTextField.setFont(UIManager.getFont("TextField.font"));
                            documentTitlePanel.add(documentNameTextField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        documentDetailsPanel.add(documentTitlePanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== descriptionPanel ========
                        {
                            descriptionPanel.setOpaque(false);
                            descriptionPanel.setFont(UIManager.getFont("TitledBorder.font"));
                            descriptionPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)descriptionPanel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)descriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)descriptionPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)descriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                            descriptionPanel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "DescriptionBorderTitle")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //======== scrollPane1 ========
                            {
                                scrollPane1.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

                                //---- descriptionTextArea ----
                                descriptionTextArea.setBorder(null);
                                descriptionTextArea.setBackground(Color.white);
                                descriptionTextArea.setFont(UIManager.getFont("TextArea.font"));
                                scrollPane1.setViewportView(descriptionTextArea);
                            }
                            descriptionPanel.add(scrollPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        documentDetailsPanel.add(descriptionPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== otherInformationPanel ========
                        {
                            otherInformationPanel.setOpaque(false);
                            otherInformationPanel.setFont(UIManager.getFont("TitledBorder.font"));
                            otherInformationPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)otherInformationPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                            ((GridBagLayout)otherInformationPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                            ((GridBagLayout)otherInformationPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)otherInformationPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
                            otherInformationPanel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "OtherInformationBorderTitle")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //---- commentsLabel ----
                            commentsLabel.setFont(UIManager.getFont("Label.font"));
                            commentsLabel.setText(Localizer.localize("UI", "DocumentPropertiesCommentsLabel"));
                            otherInformationPanel.add(commentsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                                new Insets(0, 0, 5, 5), 0, 0));

                            //---- commentsTextField ----
                            commentsTextField.setFont(UIManager.getFont("TextField.font"));
                            otherInformationPanel.add(commentsTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //---- companyLabel ----
                            companyLabel.setFont(UIManager.getFont("Label.font"));
                            companyLabel.setText(Localizer.localize("UI", "DocumentPropertiesCompanyLabel"));
                            otherInformationPanel.add(companyLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                                new Insets(0, 0, 5, 5), 0, 0));

                            //---- companyTextField ----
                            companyTextField.setFont(UIManager.getFont("TextField.font"));
                            otherInformationPanel.add(companyTextField, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //---- authorLabel ----
                            authorLabel.setFont(UIManager.getFont("Label.font"));
                            authorLabel.setText(Localizer.localize("UI", "DocumentPropertiesAuthorLabel"));
                            otherInformationPanel.add(authorLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                                new Insets(0, 0, 5, 5), 0, 0));

                            //---- authorTextField ----
                            authorTextField.setFont(UIManager.getFont("TextField.font"));
                            otherInformationPanel.add(authorTextField, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //---- copyrightLabel ----
                            copyrightLabel.setFont(UIManager.getFont("Label.font"));
                            copyrightLabel.setText(Localizer.localize("UI", "DocumentPropertiesCopyrightLabel"));
                            otherInformationPanel.add(copyrightLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                                GridBagConstraints.EAST, GridBagConstraints.VERTICAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- copyrightTextField ----
                            copyrightTextField.setFont(UIManager.getFont("TextField.font"));
                            otherInformationPanel.add(copyrightTextField, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        documentDetailsPanel.add(otherInformationPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    documentPropertiesTabbedPane.addTab("Document Details", documentDetailsPanel);

                    //======== generalSettingsPanel ========
                    {
                        generalSettingsPanel.setOpaque(false);
                        generalSettingsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                        generalSettingsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)generalSettingsPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)generalSettingsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                        ((GridBagLayout)generalSettingsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)generalSettingsPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

                        //======== panel1 ========
                        {
                            panel1.setOpaque(false);
                            panel1.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0};
                            ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                            //======== recognitionSettingsPanel ========
                            {
                                recognitionSettingsPanel.setOpaque(false);
                                recognitionSettingsPanel.setFont(UIManager.getFont("TitledBorder.font"));
                                recognitionSettingsPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)recognitionSettingsPanel.getLayout()).columnWidths = new int[] {300, 0};
                                ((GridBagLayout)recognitionSettingsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                                ((GridBagLayout)recognitionSettingsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                                ((GridBagLayout)recognitionSettingsPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};
                                recognitionSettingsPanel.setBorder(new CompoundBorder(
                                    new TitledBorder(Localizer.localize("UI", "RecognitionSettingsBorderTitle")),
                                    new EmptyBorder(5, 5, 5, 5)));

                                //======== panel8 ========
                                {
                                    panel8.setOpaque(false);
                                    panel8.setLayout(new GridBagLayout());
                                    ((GridBagLayout)panel8.getLayout()).columnWidths = new int[] {0, 90, 0};
                                    ((GridBagLayout)panel8.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                                    ((GridBagLayout)panel8.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                                    ((GridBagLayout)panel8.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0E-4};

                                    //---- luminanceLabel ----
                                    luminanceLabel.setFont(UIManager.getFont("Label.font"));
                                    luminanceLabel.setText(Localizer.localize("UI", "DocumentPropertiesLuminanceLabel"));
                                    panel8.add(luminanceLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                                        new Insets(0, 0, 5, 5), 0, 0));

                                    //---- luminanceThresholdSpinner ----
                                    luminanceThresholdSpinner.setModel(new SpinnerNumberModel(200, 0, 255, 1));
                                    luminanceThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                                    panel8.add(luminanceThresholdSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                        new Insets(0, 0, 5, 0), 0, 0));

                                    //---- markThresholdLabel ----
                                    markThresholdLabel.setFont(UIManager.getFont("Label.font"));
                                    markThresholdLabel.setText(Localizer.localize("UI", "DocumentPropertiesMarkThresholdLabel"));
                                    panel8.add(markThresholdLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                                        new Insets(0, 0, 5, 5), 0, 0));

                                    //---- markThresholdSpinner ----
                                    markThresholdSpinner.setModel(new SpinnerNumberModel(40, 0, 1000, 1));
                                    markThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                                    panel8.add(markThresholdSpinner, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                        new Insets(0, 0, 5, 0), 0, 0));

                                    //---- fragmentPaddingLabel ----
                                    fragmentPaddingLabel.setFont(UIManager.getFont("Label.font"));
                                    fragmentPaddingLabel.setText(Localizer.localize("UI", "DocumentPropertiesFragmentPaddingLabel"));
                                    panel8.add(fragmentPaddingLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                                        new Insets(0, 0, 5, 5), 0, 0));

                                    //---- fragmentPaddingSpinner ----
                                    fragmentPaddingSpinner.setModel(new SpinnerNumberModel(1, 0, 200, 1));
                                    fragmentPaddingSpinner.setFont(UIManager.getFont("Spinner.font"));
                                    panel8.add(fragmentPaddingSpinner, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                        new Insets(0, 0, 5, 0), 0, 0));

                                    //---- deskewThresholdLabel ----
                                    deskewThresholdLabel.setFont(UIManager.getFont("Label.font"));
                                    deskewThresholdLabel.setText(Localizer.localize("UI", "DocumentPropertiesDeskewThresholdLabel"));
                                    panel8.add(deskewThresholdLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                                        new Insets(0, 0, 0, 5), 0, 0));

                                    //---- deskewThresholdSpinner ----
                                    deskewThresholdSpinner.setModel(new SpinnerNumberModel(1.05, 0.0, 90.0, 0.01));
                                    deskewThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                                    panel8.add(deskewThresholdSpinner, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                        new Insets(0, 0, 0, 0), 0, 0));
                                }
                                recognitionSettingsPanel.add(panel8, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 5, 0), 0, 0));

                                //======== panel9 ========
                                {
                                    panel9.setOpaque(false);
                                    panel9.setLayout(new GridBagLayout());
                                    ((GridBagLayout)panel9.getLayout()).columnWidths = new int[] {0, 0};
                                    ((GridBagLayout)panel9.getLayout()).rowHeights = new int[] {0, 0};
                                    ((GridBagLayout)panel9.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                                    ((GridBagLayout)panel9.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                                    //---- performDeskewCheckBox ----
                                    performDeskewCheckBox.setSelected(true);
                                    performDeskewCheckBox.setFocusPainted(false);
                                    performDeskewCheckBox.setOpaque(false);
                                    performDeskewCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                                    performDeskewCheckBox.setHorizontalAlignment(SwingConstants.CENTER);
                                    performDeskewCheckBox.setText(Localizer.localize("UI", "DocumentPropertiesPerformDeskewCheckBoxText"));
                                    panel9.add(performDeskewCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                        new Insets(0, 0, 0, 0), 0, 0));
                                }
                                recognitionSettingsPanel.add(panel9, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                            }
                            panel1.add(recognitionSettingsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //======== duplicationPanel ========
                            {
                                duplicationPanel.setOpaque(false);
                                duplicationPanel.setFont(UIManager.getFont("TitledBorder.font"));
                                duplicationPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)duplicationPanel.getLayout()).columnWidths = new int[] {340, 0};
                                ((GridBagLayout)duplicationPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                                ((GridBagLayout)duplicationPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                                ((GridBagLayout)duplicationPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};
                                duplicationPanel.setBorder(new CompoundBorder(
                                    new TitledBorder(Localizer.localize("UI", "DocumentDefaultsBorderTitle")),
                                    new EmptyBorder(5, 5, 5, 5)));

                                //======== panel13 ========
                                {
                                    panel13.setOpaque(false);
                                    panel13.setBorder(null);
                                    panel13.setLayout(new GridBagLayout());
                                    ((GridBagLayout)panel13.getLayout()).columnWidths = new int[] {0, 0, 0};
                                    ((GridBagLayout)panel13.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                                    ((GridBagLayout)panel13.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                                    ((GridBagLayout)panel13.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0E-4};

                                    //---- horizontalDuplicatesLabel ----
                                    horizontalDuplicatesLabel.setFont(UIManager.getFont("Label.font"));
                                    horizontalDuplicatesLabel.setText(Localizer.localize("UI", "DocumentPropertiesHorizontalDuplicatesLabel"));
                                    panel13.add(horizontalDuplicatesLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                                        new Insets(0, 0, 5, 5), 0, 0));

                                    //---- horizontalDuplicatesSpinner ----
                                    horizontalDuplicatesSpinner.setModel(new SpinnerNumberModel(1, 0, 1000, 1));
                                    horizontalDuplicatesSpinner.setFont(UIManager.getFont("Spinner.font"));
                                    horizontalDuplicatesSpinner.setPreferredSize(null);
                                    horizontalDuplicatesSpinner.setMaximumSize(null);
                                    panel13.add(horizontalDuplicatesSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                        new Insets(0, 0, 5, 0), 0, 0));

                                    //---- verticalDuplicatesLabel ----
                                    verticalDuplicatesLabel.setFont(UIManager.getFont("Label.font"));
                                    verticalDuplicatesLabel.setText(Localizer.localize("UI", "DocumentPropertiesVerticalDuplicatesLabel"));
                                    panel13.add(verticalDuplicatesLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                                        new Insets(0, 0, 5, 5), 0, 0));

                                    //---- verticalDuplicatesSpinner ----
                                    verticalDuplicatesSpinner.setModel(new SpinnerNumberModel(1, 0, 1000, 1));
                                    verticalDuplicatesSpinner.setFont(UIManager.getFont("Spinner.font"));
                                    verticalDuplicatesSpinner.setPreferredSize(null);
                                    verticalDuplicatesSpinner.setMinimumSize(null);
                                    verticalDuplicatesSpinner.setMaximumSize(null);
                                    panel13.add(verticalDuplicatesSpinner, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                        new Insets(0, 0, 5, 0), 0, 0));

                                    //---- horizontalSpacingLabel ----
                                    horizontalSpacingLabel.setFont(UIManager.getFont("Label.font"));
                                    horizontalSpacingLabel.setText(Localizer.localize("UI", "DocumentPropertiesHorizontalSpacingLabel"));
                                    panel13.add(horizontalSpacingLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                                        new Insets(0, 0, 5, 5), 0, 0));

                                    //---- horizontalSpacingSpinner ----
                                    horizontalSpacingSpinner.setModel(new SpinnerNumberModel(20, 0, 5000, 1));
                                    horizontalSpacingSpinner.setFont(UIManager.getFont("Spinner.font"));
                                    horizontalSpacingSpinner.setPreferredSize(null);
                                    horizontalSpacingSpinner.setMinimumSize(null);
                                    horizontalSpacingSpinner.setMaximumSize(null);
                                    panel13.add(horizontalSpacingSpinner, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                        new Insets(0, 0, 5, 0), 0, 0));

                                    //---- verticalSpacingLabel ----
                                    verticalSpacingLabel.setFont(UIManager.getFont("Label.font"));
                                    verticalSpacingLabel.setText(Localizer.localize("UI", "DocumentPropertiesVerticalSpacingLabel"));
                                    panel13.add(verticalSpacingLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                                        new Insets(0, 0, 0, 5), 0, 0));

                                    //---- verticalSpacingSpinner ----
                                    verticalSpacingSpinner.setModel(new SpinnerNumberModel(20, 0, 5000, 1));
                                    verticalSpacingSpinner.setFont(UIManager.getFont("Spinner.font"));
                                    verticalSpacingSpinner.setPreferredSize(null);
                                    verticalSpacingSpinner.setMinimumSize(null);
                                    verticalSpacingSpinner.setMaximumSize(null);
                                    panel13.add(verticalSpacingSpinner, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                        new Insets(0, 0, 0, 0), 0, 0));
                                }
                                duplicationPanel.add(panel13, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 5, 0), 0, 0));

                                //======== panel6 ========
                                {
                                    panel6.setOpaque(false);
                                    panel6.setBorder(null);
                                    panel6.setLayout(new GridBagLayout());
                                    ((GridBagLayout)panel6.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                                    ((GridBagLayout)panel6.getLayout()).rowHeights = new int[] {0, 0};
                                    ((GridBagLayout)panel6.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                                    ((GridBagLayout)panel6.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                                    //---- namingDirectionLabel ----
                                    namingDirectionLabel.setFont(UIManager.getFont("Label.font"));
                                    namingDirectionLabel.setText(Localizer.localize("UI", "DocumentPropertiesNamingDirectionLabel"));
                                    panel6.add(namingDirectionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                                        new Insets(0, 0, 0, 5), 0, 0));

                                    //======== panel12 ========
                                    {
                                        panel12.setOpaque(false);
                                        panel12.setLayout(new GridBagLayout());
                                        ((GridBagLayout)panel12.getLayout()).columnWidths = new int[] {0, 0};
                                        ((GridBagLayout)panel12.getLayout()).rowHeights = new int[] {0, 0, 0};
                                        ((GridBagLayout)panel12.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                                        ((GridBagLayout)panel12.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                                        //---- tblrButton ----
                                        tblrButton.setSelected(true);
                                        tblrButton.setFont(UIManager.getFont("RadioButton.font"));
                                        tblrButton.setOpaque(false);
                                        tblrButton.setText(Localizer.localize("UI", "DocumentPropertiesTBLRRadioButtonText"));
                                        panel12.add(tblrButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                            new Insets(0, 0, 5, 0), 0, 0));

                                        //---- lrtbButton ----
                                        lrtbButton.setFont(UIManager.getFont("RadioButton.font"));
                                        lrtbButton.setOpaque(false);
                                        lrtbButton.setText(Localizer.localize("UI", "DocumentPropertiesLRTBRadioButtonText"));
                                        panel12.add(lrtbButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                            new Insets(0, 0, 0, 0), 0, 0));
                                    }
                                    panel6.add(panel12, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                        new Insets(0, 0, 0, 5), 0, 0));
                                }
                                duplicationPanel.add(panel6, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                            }
                            panel1.add(duplicationPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        generalSettingsPanel.add(panel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== sourceDataTableFilterPanel ========
                        {
                            sourceDataTableFilterPanel.setOpaque(false);
                            sourceDataTableFilterPanel.setFont(UIManager.getFont("TitledBorder.font"));
                            sourceDataTableFilterPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)sourceDataTableFilterPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                            ((GridBagLayout)sourceDataTableFilterPanel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)sourceDataTableFilterPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
                            ((GridBagLayout)sourceDataTableFilterPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                            sourceDataTableFilterPanel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "SourceDataTableFilterPanelBorderTitle")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //---- sourceDataFilterLabel ----
                            sourceDataFilterLabel.setText("Only show tables matching this regular expression:");
                            sourceDataFilterLabel.setFont(UIManager.getFont("Label.font"));
                            sourceDataFilterLabel.setText(Localizer.localize("UI", "SourceDataTableFilterLabel"));
                            sourceDataTableFilterPanel.add(sourceDataFilterLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- sourceDataTableFilterTextField ----
                            sourceDataTableFilterTextField.setFont(UIManager.getFont("TextField.font"));
                            sourceDataTableFilterPanel.add(sourceDataTableFilterTextField, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- helpLabel ----
                            helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                            helpLabel.setHelpGUID("source-data-table-name-regex-filter");
                            helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                            sourceDataTableFilterPanel.add(helpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        generalSettingsPanel.add(sourceDataTableFilterPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    documentPropertiesTabbedPane.addTab("General Settings", generalSettingsPanel);

                    //======== markAggregationGradingPanel ========
                    {
                        markAggregationGradingPanel.setOpaque(false);
                        markAggregationGradingPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                        markAggregationGradingPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)markAggregationGradingPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)markAggregationGradingPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)markAggregationGradingPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)markAggregationGradingPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //======== gradingCalculationsPanel ========
                        {
                            gradingCalculationsPanel.setOpaque(false);
                            gradingCalculationsPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)gradingCalculationsPanel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)gradingCalculationsPanel.getLayout()).rowHeights = new int[] {35, 0, 0, 0, 0};
                            ((GridBagLayout)gradingCalculationsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)gradingCalculationsPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, 1.0E-4};

                            //======== gradingDescriptionPanel ========
                            {
                                gradingDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                                gradingDescriptionPanel.setOpaque(false);
                                gradingDescriptionPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)gradingDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                                ((GridBagLayout)gradingDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout)gradingDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                                ((GridBagLayout)gradingDescriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                                //---- gradingDescriptionLabel ----
                                gradingDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                gradingDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                                gradingDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "GradingDescriptionLabel") + "</strong></body></html>");
                                gradingDescriptionPanel.add(gradingDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- gradingDescriptionHelpLabel ----
                                gradingDescriptionHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                                gradingDescriptionHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                gradingDescriptionHelpLabel.setFont(UIManager.getFont("Label.font"));
                                gradingDescriptionHelpLabel.setHelpGUID("publication-grading-rules");
                                gradingDescriptionHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                                gradingDescriptionPanel.add(gradingDescriptionHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));
                            }
                            gradingCalculationsPanel.add(gradingDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //======== totalPossibleScorePanel ========
                            {
                                totalPossibleScorePanel.setOpaque(false);
                                totalPossibleScorePanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)totalPossibleScorePanel.getLayout()).columnWidths = new int[] {0, 0, 100, 0};
                                ((GridBagLayout)totalPossibleScorePanel.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout)totalPossibleScorePanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0E-4};
                                ((GridBagLayout)totalPossibleScorePanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                                //---- totalPossibleFormScoreLabel ----
                                totalPossibleFormScoreLabel.setFont(UIManager.getFont("Label.font"));
                                totalPossibleFormScoreLabel.setText(Localizer.localize("UI", "TotalPossibleFormScoreText"));
                                totalPossibleScorePanel.add(totalPossibleFormScoreLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- totalScoreTextField ----
                                totalScoreTextField.setFont(UIManager.getFont("TextField.font"));
                                totalPossibleScorePanel.add(totalScoreTextField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                            }
                            gradingCalculationsPanel.add(totalPossibleScorePanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //---- gradingRulesLabel ----
                            gradingRulesLabel.setBorder(new CompoundBorder(
                                new MatteBorder(1, 0, 0, 0, Color.lightGray),
                                new EmptyBorder(5, 0, 0, 0)));
                            gradingRulesLabel.setFont(UIManager.getFont("Label.font"));
                            gradingRulesLabel.setText(Localizer.localize("UI", "GradingRulesLabelText"));
                            gradingCalculationsPanel.add(gradingRulesLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //======== gradingRulesPanel ========
                            {
                                gradingRulesPanel.setOpaque(false);
                                gradingRulesPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)gradingRulesPanel.getLayout()).columnWidths = new int[] {0, 0};
                                ((GridBagLayout)gradingRulesPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                                ((GridBagLayout)gradingRulesPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                                ((GridBagLayout)gradingRulesPanel.getLayout()).rowWeights = new double[] {1.0, 0.0, 1.0E-4};

                                //======== gradingListScrollPane ========
                                {

                                    //---- gradingList ----
                                    gradingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                    gradingList.setFont(UIManager.getFont("List.font"));
                                    gradingListScrollPane.setViewportView(gradingList);
                                }
                                gradingRulesPanel.add(gradingListScrollPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 5, 0), 0, 0));

                                //======== gradingRulesButtonPanel ========
                                {
                                    gradingRulesButtonPanel.setOpaque(false);
                                    gradingRulesButtonPanel.setLayout(new GridBagLayout());
                                    ((GridBagLayout)gradingRulesButtonPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0};
                                    ((GridBagLayout)gradingRulesButtonPanel.getLayout()).rowHeights = new int[] {0, 0};
                                    ((GridBagLayout)gradingRulesButtonPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                                    ((GridBagLayout)gradingRulesButtonPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                                    //---- gradingRuleUpButton ----
                                    gradingRuleUpButton.setFont(UIManager.getFont("Button.font"));
                                    gradingRuleUpButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_up.png")));
                                    gradingRuleUpButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            gradingRuleUpButtonActionPerformed(e);
                                        }
                                    });
                                    gradingRuleUpButton.setText(Localizer.localize("UI", "UpText"));
                                    gradingRulesButtonPanel.add(gradingRuleUpButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 5), 0, 0));

                                    //---- gradingRuleDownButton ----
                                    gradingRuleDownButton.setFont(UIManager.getFont("Button.font"));
                                    gradingRuleDownButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_down.png")));
                                    gradingRuleDownButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            gradingRuleDownButtonActionPerformed(e);
                                        }
                                    });
                                    gradingRuleDownButton.setText(Localizer.localize("UI", "DownText"));
                                    gradingRulesButtonPanel.add(gradingRuleDownButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 5), 0, 0));

                                    //---- addGradingRuleButton ----
                                    addGradingRuleButton.setFont(UIManager.getFont("Button.font"));
                                    addGradingRuleButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/add.png")));
                                    addGradingRuleButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            addGradingRuleButtonActionPerformed(e);
                                        }
                                    });
                                    addGradingRuleButton.setText(Localizer.localize("UI", "AddGradingRuleButtonText"));
                                    gradingRulesButtonPanel.add(addGradingRuleButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 5), 0, 0));

                                    //---- editGradingRuleButton ----
                                    editGradingRuleButton.setFont(UIManager.getFont("Button.font"));
                                    editGradingRuleButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/pencil.png")));
                                    editGradingRuleButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            editGradingRuleButtonActionPerformed(e);
                                        }
                                    });
                                    editGradingRuleButton.setText(Localizer.localize("UI", "EditGradingRuleButtonText"));
                                    gradingRulesButtonPanel.add(editGradingRuleButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 5), 0, 0));

                                    //---- removeGradingRuleButton ----
                                    removeGradingRuleButton.setFont(UIManager.getFont("Button.font"));
                                    removeGradingRuleButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/delete.png")));
                                    removeGradingRuleButton.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            removeGradingRuleButtonActionPerformed(e);
                                        }
                                    });
                                    removeGradingRuleButton.setText(Localizer.localize("UI", "RemoveGradingRuleButtonText"));
                                    gradingRulesButtonPanel.add(removeGradingRuleButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 0), 0, 0));
                                }
                                gradingRulesPanel.add(gradingRulesButtonPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                            }
                            gradingCalculationsPanel.add(gradingRulesPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        markAggregationGradingPanel.add(gradingCalculationsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    documentPropertiesTabbedPane.addTab("Automatic Grading", markAggregationGradingPanel);

                    //======== xslFoReportsPanel ========
                    {
                        xslFoReportsPanel.setOpaque(false);
                        xslFoReportsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                        xslFoReportsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)xslFoReportsPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)xslFoReportsPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)xslFoReportsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)xslFoReportsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //======== embeddedReportTemplatesPanel ========
                        {
                            embeddedReportTemplatesPanel.setOpaque(false);
                            embeddedReportTemplatesPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)embeddedReportTemplatesPanel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)embeddedReportTemplatesPanel.getLayout()).rowHeights = new int[] {35, 0, 0, 0};
                            ((GridBagLayout)embeddedReportTemplatesPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)embeddedReportTemplatesPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

                            //======== xslReportDescriptionPanel ========
                            {
                                xslReportDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                                xslReportDescriptionPanel.setOpaque(false);
                                xslReportDescriptionPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)xslReportDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                                ((GridBagLayout)xslReportDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout)xslReportDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                                ((GridBagLayout)xslReportDescriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                                //---- xslReportDescriptionLabel ----
                                xslReportDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                                xslReportDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                                xslReportDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UICDM", "XSLReportDescriptionLabel") + "</strong></body></html>");
                                xslReportDescriptionPanel.add(xslReportDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- xslReportDescriptionHelpLabel ----
                                xslReportDescriptionHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                                xslReportDescriptionHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                xslReportDescriptionHelpLabel.setFont(UIManager.getFont("Label.font"));
                                xslReportDescriptionHelpLabel.setHelpGUID("publication-xsl-fo-report-template");
                                xslReportDescriptionHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                                xslReportDescriptionPanel.add(xslReportDescriptionHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));
                            }
                            embeddedReportTemplatesPanel.add(xslReportDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //======== xslFileListScrollPane ========
                            {

                                //---- xslFileList ----
                                xslFileList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                                xslFileList.setFont(UIManager.getFont("List.font"));
                                xslFileListScrollPane.setViewportView(xslFileList);
                            }
                            embeddedReportTemplatesPanel.add(xslFileListScrollPane, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //======== xslFileListButtonPanel ========
                            {
                                xslFileListButtonPanel.setOpaque(false);
                                xslFileListButtonPanel.setLayout(new GridBagLayout());
                                ((GridBagLayout)xslFileListButtonPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0};
                                ((GridBagLayout)xslFileListButtonPanel.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout)xslFileListButtonPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                                ((GridBagLayout)xslFileListButtonPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                                //---- xslFileUpButton ----
                                xslFileUpButton.setFont(UIManager.getFont("Button.font"));
                                xslFileUpButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_up.png")));
                                xslFileUpButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        xslFileUpButtonActionPerformed(e);
                                    }
                                });
                                xslFileUpButton.setText(Localizer.localize("UI", "UpText"));
                                xslFileListButtonPanel.add(xslFileUpButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- xslFileDownButton ----
                                xslFileDownButton.setFont(UIManager.getFont("Button.font"));
                                xslFileDownButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_down.png")));
                                xslFileDownButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        xslFileDownButtonActionPerformed(e);
                                    }
                                });
                                xslFileDownButton.setText(Localizer.localize("UI", "DownText"));
                                xslFileListButtonPanel.add(xslFileDownButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- addXSLFileButton ----
                                addXSLFileButton.setFont(UIManager.getFont("Button.font"));
                                addXSLFileButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/add.png")));
                                addXSLFileButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        addXSLFileButtonActionPerformed(e);
                                    }
                                });
                                addXSLFileButton.setText(Localizer.localize("UI", "AddXSLFileButtonText"));
                                xslFileListButtonPanel.add(addXSLFileButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- removeXSLFileButton ----
                                removeXSLFileButton.setFont(UIManager.getFont("Button.font"));
                                removeXSLFileButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/delete.png")));
                                removeXSLFileButton.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent e) {
                                        removeXSLFileButtonActionPerformed(e);
                                    }
                                });
                                removeXSLFileButton.setText(Localizer.localize("UI", "RemoveXSLFileButtonText"));
                                xslFileListButtonPanel.add(removeXSLFileButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                            }
                            embeddedReportTemplatesPanel.add(xslFileListButtonPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        xslFoReportsPanel.add(embeddedReportTemplatesPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    documentPropertiesTabbedPane.addTab("XSL-FO Report Templates", xslFoReportsPanel);
                }
                contentPanel.add(documentPropertiesTabbedPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 0.0};

                //---- resetRecognitionSettingsToDefaultsButton ----
                resetRecognitionSettingsToDefaultsButton.setFont(UIManager.getFont("Button.font"));
                resetRecognitionSettingsToDefaultsButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                resetRecognitionSettingsToDefaultsButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        resetRecognitionSettingsToDefaultsButtonActionPerformed(e);
                    }
                });
                resetRecognitionSettingsToDefaultsButton.setText(Localizer.localize("UI", "ResetRecognitionSettingsToDefaultsButtonText"));
                buttonBar.add(resetRecognitionSettingsToDefaultsButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- okButton ----
                okButton.setFont(UIManager.getFont("Button.font"));
                okButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/accept.png")));
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                okButton.setText(Localizer.localize("UI", "OKButtonText"));
                buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(990, 510);
        setLocationRelativeTo(getOwner());

        //---- namingDirectionButtonGroup ----
        ButtonGroup namingDirectionButtonGroup = new ButtonGroup();
        namingDirectionButtonGroup.add(tblrButton);
        namingDirectionButtonGroup.add(lrtbButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JTabbedPane documentPropertiesTabbedPane;
    private JPanel documentDetailsPanel;
    private JPanel documentTitlePanel;
    private JTextField documentNameTextField;
    private JPanel descriptionPanel;
    private JScrollPane scrollPane1;
    private JTextArea descriptionTextArea;
    private JPanel otherInformationPanel;
    private JLabel commentsLabel;
    private JTextField commentsTextField;
    private JLabel companyLabel;
    private JTextField companyTextField;
    private JLabel authorLabel;
    private JTextField authorTextField;
    private JLabel copyrightLabel;
    private JTextField copyrightTextField;
    private JPanel generalSettingsPanel;
    private JPanel panel1;
    private JPanel recognitionSettingsPanel;
    private JPanel panel8;
    private JLabel luminanceLabel;
    private JSpinner luminanceThresholdSpinner;
    private JLabel markThresholdLabel;
    private JSpinner markThresholdSpinner;
    private JLabel fragmentPaddingLabel;
    private JSpinner fragmentPaddingSpinner;
    private JLabel deskewThresholdLabel;
    private JSpinner deskewThresholdSpinner;
    private JPanel panel9;
    private JCheckBox performDeskewCheckBox;
    private JPanel duplicationPanel;
    private JPanel panel13;
    private JLabel horizontalDuplicatesLabel;
    private JSpinner horizontalDuplicatesSpinner;
    private JLabel verticalDuplicatesLabel;
    private JSpinner verticalDuplicatesSpinner;
    private JLabel horizontalSpacingLabel;
    private JSpinner horizontalSpacingSpinner;
    private JLabel verticalSpacingLabel;
    private JSpinner verticalSpacingSpinner;
    private JPanel panel6;
    private JLabel namingDirectionLabel;
    private JPanel panel12;
    private JRadioButton tblrButton;
    private JRadioButton lrtbButton;
    private JPanel sourceDataTableFilterPanel;
    private JLabel sourceDataFilterLabel;
    private JTextField sourceDataTableFilterTextField;
    private JHelpLabel helpLabel;
    private JPanel markAggregationGradingPanel;
    private JPanel gradingCalculationsPanel;
    private JPanel gradingDescriptionPanel;
    private JLabel gradingDescriptionLabel;
    private JHelpLabel gradingDescriptionHelpLabel;
    private JPanel totalPossibleScorePanel;
    private JLabel totalPossibleFormScoreLabel;
    private JTextField totalScoreTextField;
    private JLabel gradingRulesLabel;
    private JPanel gradingRulesPanel;
    private JScrollPane gradingListScrollPane;
    private JList gradingList;
    private JPanel gradingRulesButtonPanel;
    private JButton gradingRuleUpButton;
    private JButton gradingRuleDownButton;
    private JButton addGradingRuleButton;
    private JButton editGradingRuleButton;
    private JButton removeGradingRuleButton;
    private JPanel xslFoReportsPanel;
    private JPanel embeddedReportTemplatesPanel;
    private JPanel xslReportDescriptionPanel;
    private JLabel xslReportDescriptionLabel;
    private JHelpLabel xslReportDescriptionHelpLabel;
    private JScrollPane xslFileListScrollPane;
    private JList xslFileList;
    private JPanel xslFileListButtonPanel;
    private JButton xslFileUpButton;
    private JButton xslFileDownButton;
    private JButton addXSLFileButton;
    private JButton removeXSLFileButton;
    private JPanel buttonBar;
    private JButton resetRecognitionSettingsToDefaultsButton;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public void setSelectedFrame(EditorFrame selectedFrame) {
        this.selectedFrame = selectedFrame;
        DocumentAttributes documentAttributes = selectedFrame.getDocumentAttributes();
        documentNameTextField.setText(documentAttributes.getName());

        descriptionTextArea.setText(documentAttributes.getDescription());
        commentsTextField.setText(documentAttributes.getComments());
        companyTextField.setText(documentAttributes.getCompany());
        authorTextField.setText(documentAttributes.getAuthor());
        copyrightTextField.setText(documentAttributes.getCopyright());

        sourceDataTableFilterTextField.setText(documentAttributes.getSourceDataTableFilterRegex());

        // restore recognition settings
        PublicationRecognitionStructure prs =
            documentAttributes.getPublicationRecognitionStructure();
        luminanceThresholdSpinner.setValue(new Integer(prs.getLuminanceCutOff()));
        markThresholdSpinner.setValue(new Integer(prs.getMarkThreshold()));
        fragmentPaddingSpinner.setValue(new Integer(prs.getFragmentPadding()));
        deskewThresholdSpinner.setValue(new Double(prs.getDeskewThreshold()));
        performDeskewCheckBox.setSelected(prs.isPerformDeskew());

        // restore the fieldname duplicate presets
        FieldnameDuplicatePresets fdp = documentAttributes.getFieldnameDuplicatePresets();
        horizontalDuplicatesSpinner.setValue(fdp.getHorizontalDuplicates());
        verticalDuplicatesSpinner.setValue(fdp.getVerticalDuplicates());
        horizontalSpacingSpinner.setValue(fdp.getHorizontalSpacing());
        verticalSpacingSpinner.setValue(fdp.getVerticalSpacing());

        if (fdp.getNamingDirection()
            == FieldnameDuplicatePresets.DIRECTION_TOP_TO_BOTTOM_LEFT_TO_RIGHT) {
            lrtbButton.setSelected(false);
            tblrButton.setSelected(true);
        }

        if (fdp.getNamingDirection()
            == FieldnameDuplicatePresets.DIRECTION_LEFT_TO_RIGHT_TOP_TO_BOTTOM) {
            tblrButton.setSelected(false);
            lrtbButton.setSelected(true);
        }

        // restore the mark aggregation and grading settings
        this.markingProperties = documentAttributes.getMarkingProperties().clone();

        // restore the xsl templates
        this.xslTemplates = documentAttributes.getXSLTemplates().clone();

        // restore the jar plugins
        this.jarPlugins = documentAttributes.getJARPlugins().clone();

        this.refreshGradingRuleList();

        this.refreshXSLTemplatesList();

        this.totalScoreTextField.setText(markingProperties.getTotalPossibleScore() + "");

    }

    public JPanel getSelectedFrame() {
        return selectedFrame;
    }
}

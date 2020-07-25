package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.structure.PublicationRecognitionStructure;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.editor.persistence.FieldnameDuplicatePresets;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkingProperties;
import com.ebstrada.formreturn.manager.ui.frame.EditorFrame;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class SegmentPropertiesDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private EditorFrame selectedFrame;

    private MarkingProperties markingProperties;

    private PageAttributes pageAttributes;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    public SegmentPropertiesDialog(Frame owner, PageAttributes pageAttributes) {
        super(owner);
        this.pageAttributes = pageAttributes;
        initComponents();
        getRootPane().setDefaultButton(okButton);
        localizeHeadings();
    }

    public SegmentPropertiesDialog(Dialog owner, PageAttributes pageAttributes) {
        super(owner);
        this.pageAttributes = pageAttributes;
        initComponents();
        getRootPane().setDefaultButton(okButton);
        localizeHeadings();
    }

    public void localizeHeadings() {
        segmentPropertiesTabbedPane
            .setTitleAt(0, Localizer.localize("UI", "SegmentPropertiesSegmentDetailsTabTitle"));
        segmentPropertiesTabbedPane
            .setTitleAt(1, Localizer.localize("UI", "SegmentPropertiesGeneralSettingsTabTitle"));
        segmentPropertiesTabbedPane.setTitleAt(2,
            Localizer.localize("UI", "SegmentPropertiesDuplicationDefaultsTabTitle"));
    }

    private void okButtonActionPerformed(ActionEvent e) {
        if (Misc.validateFieldname(defaultFieldnamePrefixTextField.getText().trim()) == false) {
            String msg = Localizer.localize("UI", "DocumentPropertiesInvalidFieldNameMessage");
            Misc.showErrorMsg(Main.getInstance(), msg);
            return;
        }

        DocumentAttributes documentAttributes = selectedFrame.getDocumentAttributes();
        documentAttributes.setName(documentNameTextField.getText());
        documentAttributes
            .setDefaultCapturedDataFieldname(defaultFieldnamePrefixTextField.getText().trim());
        documentAttributes.setDefaultCDFNIncrementor((Integer) fieldnameCounterSpinner.getValue());
        documentAttributes.setDescription(descriptionTextArea.getText());
        documentAttributes.setComments(commentsTextField.getText().trim());
        documentAttributes.setCompany(companyTextField.getText().trim());
        documentAttributes.setAuthor(authorTextField.getText().trim());
        documentAttributes.setCopyright(copyrightTextField.getText().trim());

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
        fdp.setFieldname(defaultFieldnamePrefixTextField.getText().trim());
        fdp.setCounterStart((Integer) fieldnameCounterSpinner.getValue());
        fdp.setHorizontalDuplicates((Integer) horizontalDuplicatesSpinner.getValue());
        fdp.setVerticalDuplicates((Integer) verticalDuplicatesSpinner.getValue());
        fdp.setHorizontalSpacing((Integer) horizontalSpacingSpinner.getValue());
        fdp.setVerticalSpacing((Integer) verticalSpacingSpinner.getValue());
        if (tblrButton.isSelected()) {
            fdp.setNamingDirection(FieldnameDuplicatePresets.DIRECTION_TOP_TO_BOTTOM_LEFT_TO_RIGHT);
        } else {
            fdp.setNamingDirection(FieldnameDuplicatePresets.DIRECTION_LEFT_TO_RIGHT_TOP_TO_BOTTOM);
        }

        // set the grading settings
        documentAttributes.setMarkingProperties(this.markingProperties);

        // set the page attributes
        pageAttributes.setRecognitionBarcodes(this.opticalRecognitionCheckBox.isSelected());
        pageAttributes
            .setRecognitionBarcodesScale((Double) this.segmentBarcodeScaleSpinner.getValue());

        this.dialogResult = JOptionPane.OK_OPTION;

        dispose();
    }

    public int getDialogResult() {
        return dialogResult;
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

        // restore the fieldname duplicate presets
        FieldnameDuplicatePresets fdp = PreferencesManager.getFieldnameDupliatePresets();
        defaultFieldnamePrefixTextField.setText(fdp.getFieldname());
        fieldnameCounterSpinner.setValue(fdp.getCounterStart());

    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                okButton.requestFocusInWindow();
            }
        });
    }

    private void markAggregationEnabledCheckBoxActionPerformed(ActionEvent e) {

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        segmentPropertiesTabbedPane = new JTabbedPane();
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
        recognitionSettingsPanel = new JPanel();
        panel8 = new JPanel();
        luminanceLabel = new JLabel();
        luminanceThresholdSpinner = new JSpinner();
        markThresholdLabel = new JLabel();
        markThresholdSpinner = new JSpinner();
        deskewThresholdLabel = new JLabel();
        deskewThresholdSpinner = new JSpinner();
        fragmentPaddingLabel = new JLabel();
        fragmentPaddingSpinner = new JSpinner();
        panel9 = new JPanel();
        performDeskewCheckBox = new JCheckBox();
        aggregationSettingsPanel = new JPanel();
        markAggregationEnabledCheckBox = new JCheckBox();
        segmentBarcodeSettingsPanel = new JPanel();
        opticalRecognitionCheckBox = new JCheckBox();
        scaleLabel = new JLabel();
        segmentBarcodeScaleSpinner = new JSpinner();
        duplicationDefaultsPanel = new JPanel();
        duplicationPanel = new JPanel();
        panel4 = new JPanel();
        fieldnamePrefixLabel = new JLabel();
        defaultFieldnamePrefixTextField = new JTextField();
        counterStartsAtLabel = new JLabel();
        fieldnameCounterSpinner = new JSpinner();
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
        buttonBar = new JPanel();
        helpLabel = new JHelpLabel();
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
        setTitle(Localizer.localize("UI", "SegmentPropertiesDialogTitle"));

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

                //======== segmentPropertiesTabbedPane ========
                {
                    segmentPropertiesTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));

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
                    segmentPropertiesTabbedPane.addTab("Segment Details", documentDetailsPanel);

                    //======== generalSettingsPanel ========
                    {
                        generalSettingsPanel.setOpaque(false);
                        generalSettingsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                        generalSettingsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)generalSettingsPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)generalSettingsPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                        ((GridBagLayout)generalSettingsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)generalSettingsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};

                        //======== recognitionSettingsPanel ========
                        {
                            recognitionSettingsPanel.setOpaque(false);
                            recognitionSettingsPanel.setFont(UIManager.getFont("TitledBorder.font"));
                            recognitionSettingsPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)recognitionSettingsPanel.getLayout()).columnWidths = new int[] {0, 0};
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
                                ((GridBagLayout)panel8.getLayout()).columnWidths = new int[] {0, 95, 25, 0, 90, 0};
                                ((GridBagLayout)panel8.getLayout()).rowHeights = new int[] {0, 0, 0};
                                ((GridBagLayout)panel8.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
                                ((GridBagLayout)panel8.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

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
                                    new Insets(0, 0, 5, 5), 0, 0));

                                //---- markThresholdLabel ----
                                markThresholdLabel.setFont(UIManager.getFont("Label.font"));
                                markThresholdLabel.setText(Localizer.localize("UI", "DocumentPropertiesMarkThresholdLabel"));
                                panel8.add(markThresholdLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                                    new Insets(0, 0, 5, 5), 0, 0));

                                //---- markThresholdSpinner ----
                                markThresholdSpinner.setModel(new SpinnerNumberModel(40, 0, 1000, 1));
                                markThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                                panel8.add(markThresholdSpinner, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 5, 0), 0, 0));

                                //---- deskewThresholdLabel ----
                                deskewThresholdLabel.setFont(UIManager.getFont("Label.font"));
                                deskewThresholdLabel.setText(Localizer.localize("UI", "DocumentPropertiesDeskewThresholdLabel"));
                                panel8.add(deskewThresholdLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- deskewThresholdSpinner ----
                                deskewThresholdSpinner.setModel(new SpinnerNumberModel(1.05, 0.0, 90.0, 0.01));
                                deskewThresholdSpinner.setFont(UIManager.getFont("Spinner.font"));
                                panel8.add(deskewThresholdSpinner, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- fragmentPaddingLabel ----
                                fragmentPaddingLabel.setFont(UIManager.getFont("Label.font"));
                                fragmentPaddingLabel.setText(Localizer.localize("UI", "DocumentPropertiesFragmentPaddingLabel"));
                                panel8.add(fragmentPaddingLabel, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- fragmentPaddingSpinner ----
                                fragmentPaddingSpinner.setModel(new SpinnerNumberModel(1, 0, 200, 1));
                                fragmentPaddingSpinner.setFont(UIManager.getFont("Spinner.font"));
                                panel8.add(fragmentPaddingSpinner, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
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
                                ((GridBagLayout)panel9.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

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
                        generalSettingsPanel.add(recognitionSettingsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== aggregationSettingsPanel ========
                        {
                            aggregationSettingsPanel.setOpaque(false);
                            aggregationSettingsPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)aggregationSettingsPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
                            ((GridBagLayout)aggregationSettingsPanel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)aggregationSettingsPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                            ((GridBagLayout)aggregationSettingsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                            aggregationSettingsPanel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "AggregationSettingsBorderTitle")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //---- markAggregationEnabledCheckBox ----
                            markAggregationEnabledCheckBox.setOpaque(false);
                            markAggregationEnabledCheckBox.setSelected(true);
                            markAggregationEnabledCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            markAggregationEnabledCheckBox.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    markAggregationEnabledCheckBoxActionPerformed(e);
                                }
                            });
                            markAggregationEnabledCheckBox.setText(Localizer.localize("UI", "EnableMarkAggregationCheckBoxText"));
                            aggregationSettingsPanel.add(markAggregationEnabledCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 5), 0, 0));
                        }
                        generalSettingsPanel.add(aggregationSettingsPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 5, 0), 0, 0));

                        //======== segmentBarcodeSettingsPanel ========
                        {
                            segmentBarcodeSettingsPanel.setOpaque(false);
                            segmentBarcodeSettingsPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)segmentBarcodeSettingsPanel.getLayout()).columnWidths = new int[] {0, 15, 0, 80, 0};
                            ((GridBagLayout)segmentBarcodeSettingsPanel.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)segmentBarcodeSettingsPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 1.0E-4};
                            ((GridBagLayout)segmentBarcodeSettingsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                            segmentBarcodeSettingsPanel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "SegmentBarcodeSettingsBorderTitle")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //---- opticalRecognitionCheckBox ----
                            opticalRecognitionCheckBox.setSelected(true);
                            opticalRecognitionCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            opticalRecognitionCheckBox.setBackground(null);
                            opticalRecognitionCheckBox.setOpaque(false);
                            opticalRecognitionCheckBox.setText(Localizer.localize("UI", "SegmentPanelSegmentBarcodesCheckBox"));
                            segmentBarcodeSettingsPanel.add(opticalRecognitionCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- scaleLabel ----
                            scaleLabel.setFont(UIManager.getFont("Label.font"));
                            scaleLabel.setText(Localizer.localize("UI", "SegmentPanelScaleLabel"));
                            segmentBarcodeSettingsPanel.add(scaleLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 5), 0, 0));

                            //---- segmentBarcodeScaleSpinner ----
                            segmentBarcodeScaleSpinner.setModel(new SpinnerNumberModel(0.6, 0.1, 5.0, 0.1));
                            segmentBarcodeScaleSpinner.setFont(UIManager.getFont("Spinner.font"));
                            segmentBarcodeSettingsPanel.add(segmentBarcodeScaleSpinner, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        generalSettingsPanel.add(segmentBarcodeSettingsPanel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    segmentPropertiesTabbedPane.addTab("General Settings", generalSettingsPanel);

                    //======== duplicationDefaultsPanel ========
                    {
                        duplicationDefaultsPanel.setOpaque(false);
                        duplicationDefaultsPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                        duplicationDefaultsPanel.setLayout(new GridBagLayout());
                        ((GridBagLayout)duplicationDefaultsPanel.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)duplicationDefaultsPanel.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)duplicationDefaultsPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)duplicationDefaultsPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //======== duplicationPanel ========
                        {
                            duplicationPanel.setOpaque(false);
                            duplicationPanel.setFont(UIManager.getFont("TitledBorder.font"));
                            duplicationPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout)duplicationPanel.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)duplicationPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
                            ((GridBagLayout)duplicationPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)duplicationPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};
                            duplicationPanel.setBorder(new CompoundBorder(
                                new TitledBorder(Localizer.localize("UI", "DocumentDefaultsBorderTitle")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //======== panel4 ========
                            {
                                panel4.setOpaque(false);
                                panel4.setBorder(null);
                                panel4.setLayout(new GridBagLayout());
                                ((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                                ((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0, 0};
                                ((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                                ((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

                                //---- fieldnamePrefixLabel ----
                                fieldnamePrefixLabel.setFont(UIManager.getFont("Label.font"));
                                fieldnamePrefixLabel.setText(Localizer.localize("UI", "DocumentPropertiesFieldnamePrefixLabel"));
                                panel4.add(fieldnamePrefixLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                                    new Insets(0, 0, 5, 5), 0, 0));

                                //---- defaultFieldnamePrefixTextField ----
                                defaultFieldnamePrefixTextField.setFont(UIManager.getFont("TextField.font"));
                                defaultFieldnamePrefixTextField.setText("fieldname");
                                panel4.add(defaultFieldnamePrefixTextField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 5, 5), 0, 0));

                                //---- counterStartsAtLabel ----
                                counterStartsAtLabel.setFont(UIManager.getFont("Label.font"));
                                counterStartsAtLabel.setText(Localizer.localize("UI", "DocumentPropertiesCounterStartsAtLabel"));
                                panel4.add(counterStartsAtLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- fieldnameCounterSpinner ----
                                fieldnameCounterSpinner.setModel(new SpinnerNumberModel(1, 0, 9999999, 1));
                                fieldnameCounterSpinner.setFont(UIManager.getFont("Spinner.font"));
                                panel4.add(fieldnameCounterSpinner, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));
                            }
                            duplicationPanel.add(panel4, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //======== panel13 ========
                            {
                                panel13.setOpaque(false);
                                panel13.setBorder(null);
                                panel13.setLayout(new GridBagLayout());
                                ((GridBagLayout)panel13.getLayout()).columnWidths = new int[] {0, 0, 25, 0, 0, 0};
                                ((GridBagLayout)panel13.getLayout()).rowHeights = new int[] {0, 0, 0};
                                ((GridBagLayout)panel13.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
                                ((GridBagLayout)panel13.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

                                //---- horizontalDuplicatesLabel ----
                                horizontalDuplicatesLabel.setFont(UIManager.getFont("Label.font"));
                                horizontalDuplicatesLabel.setText(Localizer.localize("UI", "DocumentPropertiesHorizontalDuplicatesLabel"));
                                panel13.add(horizontalDuplicatesLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                                    new Insets(0, 0, 5, 5), 0, 0));

                                //---- horizontalDuplicatesSpinner ----
                                horizontalDuplicatesSpinner.setModel(new SpinnerNumberModel(1, 0, 1000, 1));
                                horizontalDuplicatesSpinner.setFont(UIManager.getFont("Spinner.font"));
                                panel13.add(horizontalDuplicatesSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 5, 5), 0, 0));

                                //---- verticalDuplicatesLabel ----
                                verticalDuplicatesLabel.setFont(UIManager.getFont("Label.font"));
                                verticalDuplicatesLabel.setText(Localizer.localize("UI", "DocumentPropertiesVerticalDuplicatesLabel"));
                                panel13.add(verticalDuplicatesLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                                    new Insets(0, 0, 5, 5), 0, 0));

                                //---- verticalDuplicatesSpinner ----
                                verticalDuplicatesSpinner.setModel(new SpinnerNumberModel(1, 0, 1000, 1));
                                verticalDuplicatesSpinner.setFont(UIManager.getFont("Spinner.font"));
                                panel13.add(verticalDuplicatesSpinner, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 5, 0), 0, 0));

                                //---- horizontalSpacingLabel ----
                                horizontalSpacingLabel.setFont(UIManager.getFont("Label.font"));
                                horizontalSpacingLabel.setText(Localizer.localize("UI", "DocumentPropertiesHorizontalSpacingLabel"));
                                panel13.add(horizontalSpacingLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- horizontalSpacingSpinner ----
                                horizontalSpacingSpinner.setModel(new SpinnerNumberModel(20, 0, 5000, 1));
                                horizontalSpacingSpinner.setFont(UIManager.getFont("Spinner.font"));
                                panel13.add(horizontalSpacingSpinner, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- verticalSpacingLabel ----
                                verticalSpacingLabel.setFont(UIManager.getFont("Label.font"));
                                verticalSpacingLabel.setText(Localizer.localize("UI", "DocumentPropertiesVerticalSpacingLabel"));
                                panel13.add(verticalSpacingLabel, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.EAST, GridBagConstraints.NONE,
                                    new Insets(0, 0, 0, 5), 0, 0));

                                //---- verticalSpacingSpinner ----
                                verticalSpacingSpinner.setModel(new SpinnerNumberModel(20, 0, 5000, 1));
                                verticalSpacingSpinner.setFont(UIManager.getFont("Spinner.font"));
                                panel13.add(verticalSpacingSpinner, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 0), 0, 0));
                            }
                            duplicationPanel.add(panel13, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 5, 0), 0, 0));

                            //======== panel6 ========
                            {
                                panel6.setOpaque(false);
                                panel6.setBorder(null);
                                panel6.setLayout(new GridBagLayout());
                                ((GridBagLayout)panel6.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                                ((GridBagLayout)panel6.getLayout()).rowHeights = new int[] {0, 0};
                                ((GridBagLayout)panel6.getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0, 1.0E-4};
                                ((GridBagLayout)panel6.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

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
                                    ((GridBagLayout)panel12.getLayout()).columnWidths = new int[] {0, 0, 0};
                                    ((GridBagLayout)panel12.getLayout()).rowHeights = new int[] {0, 0, 0};
                                    ((GridBagLayout)panel12.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                                    ((GridBagLayout)panel12.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                                    //---- tblrButton ----
                                    tblrButton.setSelected(true);
                                    tblrButton.setFont(UIManager.getFont("RadioButton.font"));
                                    tblrButton.setOpaque(false);
                                    tblrButton.setText(Localizer.localize("UI", "DocumentPropertiesTBLRRadioButtonText"));
                                    panel12.add(tblrButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 5, 5), 0, 0));

                                    //---- lrtbButton ----
                                    lrtbButton.setFont(UIManager.getFont("RadioButton.font"));
                                    lrtbButton.setOpaque(false);
                                    lrtbButton.setText(Localizer.localize("UI", "DocumentPropertiesLRTBRadioButtonText"));
                                    panel12.add(lrtbButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 5), 0, 0));
                                }
                                panel6.add(panel12, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 0), 0, 0));
                            }
                            duplicationPanel.add(panel6, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        duplicationDefaultsPanel.add(duplicationPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    segmentPropertiesTabbedPane.addTab("Duplication Defaults", duplicationDefaultsPanel);
                }
                contentPanel.add(segmentPropertiesTabbedPane, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 0, 0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0};

                //---- helpLabel ----
                helpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                helpLabel.setHelpGUID("segment-properties");
                helpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                buttonBar.add(helpLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

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
                buttonBar.add(resetRecognitionSettingsToDefaultsButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
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
                buttonBar.add(okButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
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
                buttonBar.add(cancelButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(665, 520);
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
    private JTabbedPane segmentPropertiesTabbedPane;
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
    private JPanel recognitionSettingsPanel;
    private JPanel panel8;
    private JLabel luminanceLabel;
    private JSpinner luminanceThresholdSpinner;
    private JLabel markThresholdLabel;
    private JSpinner markThresholdSpinner;
    private JLabel deskewThresholdLabel;
    private JSpinner deskewThresholdSpinner;
    private JLabel fragmentPaddingLabel;
    private JSpinner fragmentPaddingSpinner;
    private JPanel panel9;
    private JCheckBox performDeskewCheckBox;
    private JPanel aggregationSettingsPanel;
    private JCheckBox markAggregationEnabledCheckBox;
    private JPanel segmentBarcodeSettingsPanel;
    private JCheckBox opticalRecognitionCheckBox;
    private JLabel scaleLabel;
    private JSpinner segmentBarcodeScaleSpinner;
    private JPanel duplicationDefaultsPanel;
    private JPanel duplicationPanel;
    private JPanel panel4;
    private JLabel fieldnamePrefixLabel;
    private JTextField defaultFieldnamePrefixTextField;
    private JLabel counterStartsAtLabel;
    private JSpinner fieldnameCounterSpinner;
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
    private JPanel buttonBar;
    private JHelpLabel helpLabel;
    private JButton resetRecognitionSettingsToDefaultsButton;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public void setSelectedFrame(EditorFrame selectedFrame) {
        this.selectedFrame = selectedFrame;
        DocumentAttributes documentAttributes = selectedFrame.getDocumentAttributes();
        documentNameTextField.setText(documentAttributes.getName());
        defaultFieldnamePrefixTextField
            .setText(documentAttributes.getDefaultCapturedDataFieldname());
        fieldnameCounterSpinner
            .setValue(new Integer(documentAttributes.getDefaultCDFNIncrementor()));

        descriptionTextArea.setText(documentAttributes.getDescription());
        commentsTextField.setText(documentAttributes.getComments());
        companyTextField.setText(documentAttributes.getCompany());
        authorTextField.setText(documentAttributes.getAuthor());
        copyrightTextField.setText(documentAttributes.getCopyright());

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
        defaultFieldnamePrefixTextField.setText(fdp.getFieldname());
        fieldnameCounterSpinner.setValue(fdp.getCounterStart());
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

        // TODO: restore the segment barcode settings...
        opticalRecognitionCheckBox.setSelected(pageAttributes.hasRecognitionBarcodes());
        segmentBarcodeScaleSpinner.setValue(pageAttributes.getRecognitionBarcodesScale());


    }

    public JPanel getSelectedFrame() {
        return selectedFrame;
    }
}

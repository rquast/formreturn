package com.ebstrada.formreturn.manager.util.preferences.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class CustomFormDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;

    private String originalCustomFormName;

    public CustomFormDialog(Frame owner) {
        super(owner);
        initComponents();

        orientationTabbedPane
            .setTitleAt(0, Localizer.localize("Util", "CustomFormPortraitOrientationTabTitle"));
        orientationTabbedPane
            .setTitleAt(1, Localizer.localize("Util", "CustomFormLandscapeOrientationTabTitle"));

    }

    private void saveButtonActionPerformed(ActionEvent e) {

        String newFormSizeName = customFormNameTextField.getText().trim();

        // 1. validate name
        if (newFormSizeName.length() <= 0) {
            Misc.showErrorMsg(this, Localizer.localize("Util", "CustomFormInvalidFormNameMessage"),
                Localizer.localize("Util", "CustomFormInvalidFormNameTitle"));
            return;
        }

        // 2. validate that there is no other name like it
        List<String> ssnames = PreferencesManager.getFormSizeNames();
        for (String formSizeName : ssnames) {
            if (formSizeName.equalsIgnoreCase(newFormSizeName)) {
                // check the rename condition
                if (originalCustomFormName != null && originalCustomFormName.length() > 0) {
                    if (originalCustomFormName.equals(newFormSizeName)) {
                        continue;
                    }
                }
                // not a rename, warn that there's a duplicate name
                Misc.showErrorMsg(this,
                    Localizer.localize("Util", "CustomFormDuplicateFormNameMessage"),
                    Localizer.localize("Util", "CustomFormDuplicateFormNameTitle"));
                return;
            }
        }

        // 3. validate dimensions
        if (getPortraitWidth() < 50 || getPortraitHeight() < 40 || getLandscapeWidth() < 40
            || getLandscapeHeight() < 50) {
            Misc.showErrorMsg(this,
                Localizer.localize("Util", "CustomFormInvalidPageDimensionsMessage"),
                Localizer.localize("Util", "CustomFormInvalidPageDimensionsTitle"));
            return;
        }

        this.dialogResult = javax.swing.JOptionPane.OK_OPTION;
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    public void setCustomFormName(String customFormName) {
        originalCustomFormName = customFormName;
        customFormNameTextField.setText(customFormName);
    }

    public String getCustomFormName() {
        return customFormNameTextField.getText().trim();
    }

    public void setPortraitWidth(int width) {
        portraitFormWidth.setValue(width);
    }

    public int getPortraitWidth() {
        return (Integer) portraitFormWidth.getValue();
    }

    public void setPortraitHeight(int height) {
        portraitFormHeight.setValue(height);
    }

    public int getPortraitHeight() {
        return (Integer) portraitFormHeight.getValue();
    }

    public void setLandscapeWidth(int width) {
        landscapeFormWidth.setValue(width);
    }

    public int getLandscapeWidth() {
        return (Integer) landscapeFormWidth.getValue();
    }

    public void setLandscapeHeight(int height) {
        landscapeFormHeight.setValue(height);
    }

    public int getLandscapeHeight() {
        return (Integer) landscapeFormHeight.getValue();
    }

    public void setLandscapeTopMargin(int height) {
        landscapeTopMargin.setValue(height);
    }

    public int getLandscapeTopMargin() {
        return (Integer) landscapeTopMargin.getValue();
    }

    public void setLandscapeBottomMargin(int height) {
        landscapeBottomMargin.setValue(height);
    }

    public int getLandscapeBottomMargin() {
        return (Integer) landscapeBottomMargin.getValue();
    }

    public void setLandscapeLeftMargin(int height) {
        landscapeLeftMargin.setValue(height);
    }

    public int getLandscapeLeftMargin() {
        return (Integer) landscapeLeftMargin.getValue();
    }

    public void setLandscapeRightMargin(int height) {
        landscapeRightMargin.setValue(height);
    }

    public int getLandscapeRightMargin() {
        return (Integer) landscapeRightMargin.getValue();
    }

    public void setPortraitTopMargin(int height) {
        portraitTopMargin.setValue(height);
    }

    public int getPortraitTopMargin() {
        return (Integer) portraitTopMargin.getValue();
    }

    public void setPortraitBottomMargin(int height) {
        portraitBottomMargin.setValue(height);
    }

    public int getPortraitBottomMargin() {
        return (Integer) portraitBottomMargin.getValue();
    }

    public void setPortraitLeftMargin(int height) {
        portraitLeftMargin.setValue(height);
    }

    public int getPortraitLeftMargin() {
        return (Integer) portraitLeftMargin.getValue();
    }

    public void setPortraitRightMargin(int height) {
        portraitRightMargin.setValue(height);
    }

    public int getPortraitRightMargin() {
        return (Integer) portraitRightMargin.getValue();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        customFormSizeNamePanel = new JPanel();
        customFormNameTextField = new JTextField();
        orientationTabbedPane = new JTabbedPane();
        portraitPageSizePanel = new JPanel();
        portraitWidthLabel = new JLabel();
        portraitFormWidth = new JSpinner();
        portraitHeightLabel = new JLabel();
        portraitFormHeight = new JSpinner();
        portraitLeftMarginLabel = new JLabel();
        portraitLeftMargin = new JSpinner();
        portraitRightMarginLabel = new JLabel();
        portraitRightMargin = new JSpinner();
        portraitTopMarginLabel = new JLabel();
        portraitTopMargin = new JSpinner();
        portraitBottomMarginLabel = new JLabel();
        portraitBottomMargin = new JSpinner();
        landscapePageSizePanel = new JPanel();
        landscapeWidthLabel = new JLabel();
        landscapeFormWidth = new JSpinner();
        landscapeHeightLabel = new JLabel();
        landscapeFormHeight = new JSpinner();
        landscapeLeftMarginLabel = new JLabel();
        landscapeLeftMargin = new JSpinner();
        landscapeRightMarginLabel = new JLabel();
        landscapeRightMargin = new JSpinner();
        landscapeTopMarginLabel = new JLabel();
        landscapeTopMargin = new JSpinner();
        landscapeBottomMarginLabel = new JLabel();
        landscapeBottomMargin = new JSpinner();
        buttonBar = new JPanel();
        saveButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {0.0, 1.0, 1.0E-4};

                //======== customFormSizeNamePanel ========
                {
                    customFormSizeNamePanel.setOpaque(false);
                    customFormSizeNamePanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) customFormSizeNamePanel.getLayout()).columnWidths =
                        new int[] {0, 0};
                    ((GridBagLayout) customFormSizeNamePanel.getLayout()).rowHeights =
                        new int[] {0, 0};
                    ((GridBagLayout) customFormSizeNamePanel.getLayout()).columnWeights =
                        new double[] {1.0, 1.0E-4};
                    ((GridBagLayout) customFormSizeNamePanel.getLayout()).rowWeights =
                        new double[] {0.0, 1.0E-4};
                    customFormSizeNamePanel.setBorder(new CompoundBorder(new TitledBorder(
                        Localizer.localize("Util", "CustomFormSizeNameBorderTitle")),
                        new EmptyBorder(2, 2, 2, 2)));

                    //---- customFormNameTextField ----
                    customFormNameTextField.setFont(UIManager.getFont("TextField.font"));
                    customFormSizeNamePanel.add(customFormNameTextField,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(customFormSizeNamePanel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //======== orientationTabbedPane ========
                {
                    orientationTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));

                    //======== portraitPageSizePanel ========
                    {
                        portraitPageSizePanel.setOpaque(false);
                        portraitPageSizePanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) portraitPageSizePanel.getLayout()).columnWidths =
                            new int[] {0, 0, 15, 0, 0, 0};
                        ((GridBagLayout) portraitPageSizePanel.getLayout()).rowHeights =
                            new int[] {0, 0, 0, 0};
                        ((GridBagLayout) portraitPageSizePanel.getLayout()).columnWeights =
                            new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout) portraitPageSizePanel.getLayout()).rowWeights =
                            new double[] {1.0, 1.0, 1.0, 1.0E-4};
                        portraitPageSizePanel.setBorder(new CompoundBorder(new TitledBorder(
                            Localizer.localize("Util", "CustomFormPortraitPageSizeBorderTitle")),
                            new EmptyBorder(2, 2, 2, 2)));

                        //---- portraitWidthLabel ----
                        portraitWidthLabel.setFont(UIManager.getFont("Label.font"));
                        portraitWidthLabel
                            .setText(Localizer.localize("Util", "CustomFormPortraitWidthLabel"));
                        portraitPageSizePanel.add(portraitWidthLabel,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- portraitFormWidth ----
                        portraitFormWidth.setModel(new SpinnerNumberModel(0, 0, 5990, 1));
                        portraitFormWidth.setFont(UIManager.getFont("Spinner.font"));
                        portraitPageSizePanel.add(portraitFormWidth,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

                        //---- portraitHeightLabel ----
                        portraitHeightLabel.setFont(UIManager.getFont("Label.font"));
                        portraitHeightLabel
                            .setText(Localizer.localize("Util", "CustomFormPortraitHeightLabel"));
                        portraitPageSizePanel.add(portraitHeightLabel,
                            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- portraitFormHeight ----
                        portraitFormHeight.setModel(new SpinnerNumberModel(0, 0, 5990, 1));
                        portraitFormHeight.setFont(UIManager.getFont("Spinner.font"));
                        portraitPageSizePanel.add(portraitFormHeight,
                            new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                        //---- portraitLeftMarginLabel ----
                        portraitLeftMarginLabel.setFont(UIManager.getFont("Label.font"));
                        portraitLeftMarginLabel.setText(
                            Localizer.localize("Util", "CustomFormPortraitLeftMarginLabel"));
                        portraitPageSizePanel.add(portraitLeftMarginLabel,
                            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- portraitLeftMargin ----
                        portraitLeftMargin.setFont(UIManager.getFont("Spinner.font"));
                        portraitPageSizePanel.add(portraitLeftMargin,
                            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

                        //---- portraitRightMarginLabel ----
                        portraitRightMarginLabel.setFont(UIManager.getFont("Label.font"));
                        portraitRightMarginLabel.setText(
                            Localizer.localize("Util", "CustomFormPortraitRightMarginLabel"));
                        portraitPageSizePanel.add(portraitRightMarginLabel,
                            new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- portraitRightMargin ----
                        portraitRightMargin.setFont(UIManager.getFont("Spinner.font"));
                        portraitPageSizePanel.add(portraitRightMargin,
                            new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                        //---- portraitTopMarginLabel ----
                        portraitTopMarginLabel.setFont(UIManager.getFont("Label.font"));
                        portraitTopMarginLabel.setText(
                            Localizer.localize("Util", "CustomFormPortraitTopMarginLabel"));
                        portraitPageSizePanel.add(portraitTopMarginLabel,
                            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                        //---- portraitTopMargin ----
                        portraitTopMargin.setFont(UIManager.getFont("Spinner.font"));
                        portraitPageSizePanel.add(portraitTopMargin,
                            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                        //---- portraitBottomMarginLabel ----
                        portraitBottomMarginLabel.setFont(UIManager.getFont("Label.font"));
                        portraitBottomMarginLabel.setText(
                            Localizer.localize("Util", "CustomFormPortraitBottomMarginLabel"));
                        portraitPageSizePanel.add(portraitBottomMarginLabel,
                            new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                        //---- portraitBottomMargin ----
                        portraitBottomMargin.setFont(UIManager.getFont("Spinner.font"));
                        portraitPageSizePanel.add(portraitBottomMargin,
                            new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    orientationTabbedPane.addTab("Portrait Size", portraitPageSizePanel);


                    //======== landscapePageSizePanel ========
                    {
                        landscapePageSizePanel.setOpaque(false);
                        landscapePageSizePanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) landscapePageSizePanel.getLayout()).columnWidths =
                            new int[] {0, 0, 15, 0, 0, 0};
                        ((GridBagLayout) landscapePageSizePanel.getLayout()).rowHeights =
                            new int[] {0, 0, 0, 0};
                        ((GridBagLayout) landscapePageSizePanel.getLayout()).columnWeights =
                            new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout) landscapePageSizePanel.getLayout()).rowWeights =
                            new double[] {1.0, 1.0, 1.0, 1.0E-4};
                        landscapePageSizePanel.setBorder(new CompoundBorder(new TitledBorder(
                            Localizer.localize("Util", "CustomFormLandscapePageSizeBorderTitle")),
                            new EmptyBorder(2, 2, 2, 2)));

                        //---- landscapeWidthLabel ----
                        landscapeWidthLabel.setFont(UIManager.getFont("Label.font"));
                        landscapeWidthLabel
                            .setText(Localizer.localize("Util", "CustomFormLandscapeWidthLabel"));
                        landscapePageSizePanel.add(landscapeWidthLabel,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- landscapeFormWidth ----
                        landscapeFormWidth.setModel(new SpinnerNumberModel(0, 0, 5990, 1));
                        landscapeFormWidth.setFont(UIManager.getFont("Spinner.font"));
                        landscapePageSizePanel.add(landscapeFormWidth,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

                        //---- landscapeHeightLabel ----
                        landscapeHeightLabel.setFont(UIManager.getFont("Label.font"));
                        landscapeHeightLabel
                            .setText(Localizer.localize("Util", "CustomFormLandscapeHeightLabel"));
                        landscapePageSizePanel.add(landscapeHeightLabel,
                            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- landscapeFormHeight ----
                        landscapeFormHeight.setModel(new SpinnerNumberModel(0, 0, 5990, 1));
                        landscapeFormHeight.setFont(UIManager.getFont("Spinner.font"));
                        landscapePageSizePanel.add(landscapeFormHeight,
                            new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                        //---- landscapeLeftMarginLabel ----
                        landscapeLeftMarginLabel.setFont(UIManager.getFont("Label.font"));
                        landscapeLeftMarginLabel.setText(
                            Localizer.localize("Util", "CustomFormLandscapeLeftMarginLabel"));
                        landscapePageSizePanel.add(landscapeLeftMarginLabel,
                            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- landscapeLeftMargin ----
                        landscapeLeftMargin.setFont(UIManager.getFont("Spinner.font"));
                        landscapePageSizePanel.add(landscapeLeftMargin,
                            new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

                        //---- landscapeRightMarginLabel ----
                        landscapeRightMarginLabel.setFont(UIManager.getFont("Label.font"));
                        landscapeRightMarginLabel.setText(
                            Localizer.localize("Util", "CustomFormLandscapeRightMarginLabel"));
                        landscapePageSizePanel.add(landscapeRightMarginLabel,
                            new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

                        //---- landscapeRightMargin ----
                        landscapeRightMargin.setFont(UIManager.getFont("Spinner.font"));
                        landscapePageSizePanel.add(landscapeRightMargin,
                            new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                        //---- landscapeTopMarginLabel ----
                        landscapeTopMarginLabel.setFont(UIManager.getFont("Label.font"));
                        landscapeTopMarginLabel.setText(
                            Localizer.localize("Util", "CustomFormLandscapeTopMarginLabel"));
                        landscapePageSizePanel.add(landscapeTopMarginLabel,
                            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                        //---- landscapeTopMargin ----
                        landscapeTopMargin.setFont(UIManager.getFont("Spinner.font"));
                        landscapePageSizePanel.add(landscapeTopMargin,
                            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                        //---- landscapeBottomMarginLabel ----
                        landscapeBottomMarginLabel.setFont(UIManager.getFont("Label.font"));
                        landscapeBottomMarginLabel.setText(
                            Localizer.localize("Util", "CustomFormLandscapeBottomMarginLabel"));
                        landscapePageSizePanel.add(landscapeBottomMarginLabel,
                            new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                        //---- landscapeBottomMargin ----
                        landscapeBottomMargin.setFont(UIManager.getFont("Spinner.font"));
                        landscapePageSizePanel.add(landscapeBottomMargin,
                            new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    orientationTabbedPane.addTab("Landscape Size", landscapePageSizePanel);

                }
                contentPanel.add(orientationTabbedPane,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 0.0};

                //---- saveButton ----
                saveButton.setFont(UIManager.getFont("Button.font"));
                saveButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        saveButtonActionPerformed(e);
                    }
                });
                saveButton.setText(Localizer.localize("Util", "SaveButtonText"));
                buttonBar.add(saveButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("Util", "CancelButtonText"));
                buttonBar.add(cancelButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(515, 350);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel customFormSizeNamePanel;
    private JTextField customFormNameTextField;
    private JTabbedPane orientationTabbedPane;
    private JPanel portraitPageSizePanel;
    private JLabel portraitWidthLabel;
    private JSpinner portraitFormWidth;
    private JLabel portraitHeightLabel;
    private JSpinner portraitFormHeight;
    private JLabel portraitLeftMarginLabel;
    private JSpinner portraitLeftMargin;
    private JLabel portraitRightMarginLabel;
    private JSpinner portraitRightMargin;
    private JLabel portraitTopMarginLabel;
    private JSpinner portraitTopMargin;
    private JLabel portraitBottomMarginLabel;
    private JSpinner portraitBottomMargin;
    private JPanel landscapePageSizePanel;
    private JLabel landscapeWidthLabel;
    private JSpinner landscapeFormWidth;
    private JLabel landscapeHeightLabel;
    private JSpinner landscapeFormHeight;
    private JLabel landscapeLeftMarginLabel;
    private JSpinner landscapeLeftMargin;
    private JLabel landscapeRightMarginLabel;
    private JSpinner landscapeRightMargin;
    private JLabel landscapeTopMarginLabel;
    private JSpinner landscapeTopMargin;
    private JLabel landscapeBottomMarginLabel;
    private JSpinner landscapeBottomMargin;
    private JPanel buttonBar;
    private JButton saveButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }
}

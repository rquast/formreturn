package com.ebstrada.formreturn.manager.util.preferences.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class CustomSegmentDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;

    private String originalCustomSegmentName = "";

    public CustomSegmentDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    private void saveButtonActionPerformed(ActionEvent e) {

        String newSegmentSizeName = customSegmentNameTextField.getText().trim();

        // 1. validate name
        if (newSegmentSizeName.length() <= 0) {
            Misc.showErrorMsg(this,
                Localizer.localize("Util", "CustomSegmentInvalidSegmentNameMessage"),
                Localizer.localize("Util", "CustomSegmentInvalidSegmentNameTitle"));
            return;
        }

        // 2. validate that there is no other name like it
        List<String> ssnames = PreferencesManager.getSegmentSizeNames();
        for (String segmentSizeName : ssnames) {
            if (segmentSizeName.equalsIgnoreCase(newSegmentSizeName)) {
                // check the rename condition
                if (originalCustomSegmentName != null && originalCustomSegmentName.length() > 0) {
                    if (originalCustomSegmentName.equals(newSegmentSizeName)) {
                        continue;
                    }
                }
                // not a rename, warn that there's a duplicate name
                Misc.showErrorMsg(this,
                    Localizer.localize("Util", "CustomSegmentDuplicateSegmentNameMessage"),
                    Localizer.localize("Util", "CustomSegmentDuplicateSegmentNameTitle"));
                return;
            }
        }

        // 3. validate dimensions
        if (getWidth() < 50 || getHeight() < 40) {
            Misc.showErrorMsg(this,
                Localizer.localize("Util", "CustomSegmentInvalidPageDimensionsMessage"),
                Localizer.localize("Util", "CustomSegmentInvalidPageDimensionsTitle"));
            return;
        }

        this.dialogResult = javax.swing.JOptionPane.OK_OPTION;
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    public void setCustomSegmentName(String customSegmentName) {
        originalCustomSegmentName = customSegmentName;
        customSegmentNameTextField.setText(customSegmentName);
    }

    public String getCustomSegmentName() {
        return customSegmentNameTextField.getText().trim();
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

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        customSegmentSizeNamePanel = new JPanel();
        customSegmentNameTextField = new JTextField();
        orientationTabbedPane = new JTabbedPane();
        portraitPageSizePanel = new JPanel();
        portraitWidthLabel = new JLabel();
        portraitFormWidth = new JSpinner();
        portraitHeightLabel = new JLabel();
        portraitFormHeight = new JSpinner();
        landscapePageSizePanel = new JPanel();
        landscapeWidthLabel = new JLabel();
        landscapeFormWidth = new JSpinner();
        landscapeHeightLabel = new JLabel();
        landscapeFormHeight = new JSpinner();
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

                //======== customSegmentSizeNamePanel ========
                {
                    customSegmentSizeNamePanel.setOpaque(false);
                    customSegmentSizeNamePanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) customSegmentSizeNamePanel.getLayout()).columnWidths =
                        new int[] {0, 0};
                    ((GridBagLayout) customSegmentSizeNamePanel.getLayout()).rowHeights =
                        new int[] {0, 0};
                    ((GridBagLayout) customSegmentSizeNamePanel.getLayout()).columnWeights =
                        new double[] {1.0, 1.0E-4};
                    ((GridBagLayout) customSegmentSizeNamePanel.getLayout()).rowWeights =
                        new double[] {0.0, 1.0E-4};
                    customSegmentSizeNamePanel.setBorder(new CompoundBorder(new TitledBorder(
                        Localizer.localize("Util", "CustomSegmentSizeNameBorderTitle")),
                        new EmptyBorder(2, 2, 2, 2)));

                    //---- customSegmentNameTextField ----
                    customSegmentNameTextField.setFont(UIManager.getFont("TextField.font"));
                    customSegmentSizeNamePanel.add(customSegmentNameTextField,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(customSegmentSizeNamePanel,
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
                            new int[] {0, 0};
                        ((GridBagLayout) portraitPageSizePanel.getLayout()).columnWeights =
                            new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout) portraitPageSizePanel.getLayout()).rowWeights =
                            new double[] {1.0, 1.0E-4};
                        portraitPageSizePanel.setBorder(new CompoundBorder(new TitledBorder(
                            Localizer.localize("Util", "CustomFormPortraitPageSizeBorderTitle")),
                            new EmptyBorder(2, 2, 2, 2)));

                        //---- portraitWidthLabel ----
                        portraitWidthLabel.setFont(UIManager.getFont("Label.font"));
                        portraitWidthLabel
                            .setText(Localizer.localize("Util", "CustomFormPortraitWidthLabel"));
                        portraitPageSizePanel.add(portraitWidthLabel,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                        //---- portraitFormWidth ----
                        portraitFormWidth.setModel(new SpinnerNumberModel(0, 0, 5990, 1));
                        portraitFormWidth.setFont(UIManager.getFont("Spinner.font"));
                        portraitPageSizePanel.add(portraitFormWidth,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                        //---- portraitHeightLabel ----
                        portraitHeightLabel.setFont(UIManager.getFont("Label.font"));
                        portraitHeightLabel
                            .setText(Localizer.localize("Util", "CustomFormPortraitHeightLabel"));
                        portraitPageSizePanel.add(portraitHeightLabel,
                            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                        //---- portraitFormHeight ----
                        portraitFormHeight.setModel(new SpinnerNumberModel(0, 0, 5990, 1));
                        portraitFormHeight.setFont(UIManager.getFont("Spinner.font"));
                        portraitPageSizePanel.add(portraitFormHeight,
                            new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
                            new int[] {0, 0};
                        ((GridBagLayout) landscapePageSizePanel.getLayout()).columnWeights =
                            new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout) landscapePageSizePanel.getLayout()).rowWeights =
                            new double[] {1.0, 1.0E-4};
                        landscapePageSizePanel.setBorder(new CompoundBorder(new TitledBorder(
                            Localizer.localize("Util", "CustomFormLandscapePageSizeBorderTitle")),
                            new EmptyBorder(2, 2, 2, 2)));

                        //---- landscapeWidthLabel ----
                        landscapeWidthLabel.setFont(UIManager.getFont("Label.font"));
                        landscapeWidthLabel
                            .setText(Localizer.localize("Util", "CustomFormLandscapeWidthLabel"));
                        landscapePageSizePanel.add(landscapeWidthLabel,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                        //---- landscapeFormWidth ----
                        landscapeFormWidth.setModel(new SpinnerNumberModel(0, 0, 5990, 1));
                        landscapeFormWidth.setFont(UIManager.getFont("Spinner.font"));
                        landscapePageSizePanel.add(landscapeFormWidth,
                            new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                        //---- landscapeHeightLabel ----
                        landscapeHeightLabel.setFont(UIManager.getFont("Label.font"));
                        landscapeHeightLabel
                            .setText(Localizer.localize("Util", "CustomFormLandscapeHeightLabel"));
                        landscapePageSizePanel.add(landscapeHeightLabel,
                            new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                                GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                        //---- landscapeFormHeight ----
                        landscapeFormHeight.setModel(new SpinnerNumberModel(0, 0, 5990, 1));
                        landscapeFormHeight.setFont(UIManager.getFont("Spinner.font"));
                        landscapePageSizePanel.add(landscapeFormHeight,
                            new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
    private JPanel customSegmentSizeNamePanel;
    private JTextField customSegmentNameTextField;
    private JTabbedPane orientationTabbedPane;
    private JPanel portraitPageSizePanel;
    private JLabel portraitWidthLabel;
    private JSpinner portraitFormWidth;
    private JLabel portraitHeightLabel;
    private JSpinner portraitFormHeight;
    private JPanel landscapePageSizePanel;
    private JLabel landscapeWidthLabel;
    private JSpinner landscapeFormWidth;
    private JLabel landscapeHeightLabel;
    private JSpinner landscapeFormHeight;
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

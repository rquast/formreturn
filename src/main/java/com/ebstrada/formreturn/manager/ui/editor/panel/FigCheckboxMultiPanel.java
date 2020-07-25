package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.util.List;
import java.util.Vector;

import javax.swing.*;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;

import org.jdesktop.swingx.*;

import com.ebstrada.formreturn.manager.gef.presentation.FigCheckbox;
import com.ebstrada.formreturn.manager.gef.presentation.FigText;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.dialog.RestoreMarkAreaPresetStyleDialog;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkAreaPresetStyle;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class FigCheckboxMultiPanel extends EditorMultiPanel {

    private static final long serialVersionUID = 1L;

    private Vector selectedElements;

    private List<MarkAreaPresetStyle> markAreaPresetStyles;

    public FigCheckboxMultiPanel() {
        initComponents();
    }

    private void updatePresetStyles() {
        markAreaPresetStyles = PreferencesManager.getMarkAreaPresetStyles();

        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        for (MarkAreaPresetStyle markAreaPresetStyle : markAreaPresetStyles) {
            dcbm.addElement(markAreaPresetStyle.getName());
        }
        presetStyleComboBox.setModel(dcbm);
    }

    private void updateAggregationRule() {
        if (selectedElements == null) {
            return;
        }
        try {
            FigCheckbox firstFig = (FigCheckbox) selectedElements.get(0);
            String aggregationRule = firstFig.getAggregationRule();
            for (Object o : selectedElements) {
                FigCheckbox fig = (FigCheckbox) o;
                if (!(fig.getAggregationRule().equals(aggregationRule))) {
                    throw new Exception(Localizer.localize("UI", "AggregationRuleDiffersMessage"));
                }
                aggregationRule = fig.getAggregationRule();
            }
            this.aggregationRuleTextField.setText(aggregationRule);
        } catch (Exception ex) {
            this.aggregationRuleTextField.setText(Localizer.localize("UI", "DiffersText"));
        }
    }

    @Override public void updatePanel() {
        updatePresetStyles();
        updateAggregationRule();
    }

    @Override public void removeListeners() {
    }

    @Override public void setSelectedElements(Vector selectedFig) {
        selectedElements = selectedFig;
    }

    private void applyStyleButtonActionPerformed(ActionEvent e) {

        if (markAreaPresetStyles == null || markAreaPresetStyles.size() <= 0) {
            return;
        }

        if (selectedElements == null || selectedElements.size() <= 0) {
            return;
        }

        MarkAreaPresetStyle selectedStyle =
            markAreaPresetStyles.get(presetStyleComboBox.getSelectedIndex());

        RestoreMarkAreaPresetStyleDialog rmapsd =
            new RestoreMarkAreaPresetStyleDialog(Main.getInstance());
        rmapsd.setTitle(Localizer.localize("UI", "MarkAreasRestorePresetStyleTitle"));
        rmapsd.setRestoreMessage(String
            .format(Localizer.localize("UI", "MarkAreasRestorePresetStyleMessage"),
                selectedStyle.getName()));
        rmapsd.setModal(true);
        rmapsd.setVisible(true);

        if (rmapsd.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {

            for (Object o : selectedElements) {

                if (o instanceof FigCheckbox) {

                    FigCheckbox fig = (FigCheckbox) o;

                    if (rmapsd.isRestoreBoxDesign()) {
                        fig.setBoxWidth(selectedStyle.getBoxWidth());
                        fig.setBoxHeight(selectedStyle.getBoxHeight());
                        fig.setWidthRoundness(selectedStyle.getWidthRoundness());
                        fig.setHeightRoundness(selectedStyle.getHeightRoundness());
                        fig.setBoxWeight(selectedStyle.getBoxWeight());
                        fig.setFontSize(selectedStyle.getFontSize());
                        fig.setFontDarkness(selectedStyle.getFontDarkness());
                        fig.setShowText(selectedStyle.isShowText());
                        fig.setCombineColumnCharacters(selectedStyle.isCombineColumnCharacters());
                        fig.setReconciliationKey(selectedStyle.isReconciliationKey());
                    }

                    if (rmapsd.isRestoreSpacing()) {
                        fig.setHorizontalSpace(selectedStyle.getHorizontalSpace());
                        fig.setVerticalSpace(selectedStyle.getVerticalSpace());
                    }

                    if (rmapsd.isRestoreAggregationRule()) {
                        fig.setAggregationRule(selectedStyle.getAggregationRule());
                    }

                    if (rmapsd.isRestoreMarkAreaValues()) {
                        fig.setCheckboxValues(selectedStyle.getCheckboxValues());
                    }

                    fig.damage();

                }
            }

        } else {
            rmapsd.dispose();
            return;
        }

        rmapsd.dispose();

        updatePanel();

    }

    private void aggregationRuleApplyButtonActionPerformed(ActionEvent e) {

        if (this.aggregationRuleTextField.getText().trim()
            .equals(Localizer.localize("UI", "DiffersText"))) {
            return;
        }

        for (Object o : selectedElements) {
            if (o instanceof FigCheckbox) {
                FigCheckbox fig = (FigCheckbox) o;
                fig.setAggregationRule(this.aggregationRuleTextField.getText().trim());
                fig.damage();
            }
        }

        updatePanel();

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        presetStyleLabel = new JLabel();
        presetStyleComboBox = new JComboBox();
        applyStyleButton = new JButton();
        aggregationRuleLabel = new JLabel();
        aggregationRuleTextField = new JTextField();
        aggregationRuleApplyButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        this.setTitle(Localizer.localize("UI", "MarkAreasPanelTitle"));

        //======== panel1 ========
        {
            panel1.setBorder(null);
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};

            //---- presetStyleLabel ----
            presetStyleLabel.setFont(UIManager.getFont("Label.font"));
            presetStyleLabel.setText(Localizer.localize("UI", "MarkAreasPresetStyleLabel"));
            panel1.add(presetStyleLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- presetStyleComboBox ----
            presetStyleComboBox.setFont(UIManager.getFont("ComboBox.font"));
            panel1.add(presetStyleComboBox,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- applyStyleButton ----
            applyStyleButton.setFont(UIManager.getFont("Button.font"));
            applyStyleButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    applyStyleButtonActionPerformed(e);
                }
            });
            applyStyleButton.setText(Localizer.localize("UI", "ApplyStyleButtonText"));
            panel1.add(applyStyleButton,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- aggregationRuleLabel ----
            aggregationRuleLabel.setFont(UIManager.getFont("Label.font"));
            aggregationRuleLabel.setText(Localizer.localize("UI", "MarkAreasAggregationRuleLabel"));
            panel1.add(aggregationRuleLabel,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- aggregationRuleTextField ----
            aggregationRuleTextField.setFont(UIManager.getFont("TextField.font"));
            panel1.add(aggregationRuleTextField,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- aggregationRuleApplyButton ----
            aggregationRuleApplyButton.setFont(UIManager.getFont("Button.font"));
            aggregationRuleApplyButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    aggregationRuleApplyButtonActionPerformed(e);
                }
            });
            aggregationRuleApplyButton.setText(Localizer.localize("UI", "ApplyRuleButtonText"));
            panel1.add(aggregationRuleApplyButton,
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel presetStyleLabel;
    private JComboBox presetStyleComboBox;
    private JButton applyStyleButton;
    private JLabel aggregationRuleLabel;
    private JTextField aggregationRuleTextField;
    private JButton aggregationRuleApplyButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables

}

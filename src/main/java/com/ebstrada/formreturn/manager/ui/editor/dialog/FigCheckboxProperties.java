package com.ebstrada.formreturn.manager.ui.editor.dialog;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import javax.swing.*;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.text.Document;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigCheckbox;
import com.ebstrada.formreturn.manager.gef.undo.memento.CheckboxMemento;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.*;
import com.ebstrada.formreturn.manager.ui.editor.component.*;
import com.ebstrada.formreturn.manager.ui.editor.persistence.MarkAreaPresetStyle;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;

public class FigCheckboxProperties extends JDialog {

    private static final long serialVersionUID = 1L;

    private FigCheckbox figCheckbox;

    private int dialogResult = javax.swing.JOptionPane.CANCEL_OPTION;

    private List<MarkAreaPresetStyle> markAreaPresetStyles;

    public FigCheckboxProperties(FigCheckbox fcb) {
        super(Main.getInstance(), true);
        figCheckbox = fcb;
        initComponents();
        updateValues();
        updatePresetStyles();
        updatePreview();

        EditorTextField textField = new EditorTextField();
        textField.addCaretListener(new MarkValuesCaretListener());
        textField.setBorder(null);
        checkboxValuesTable.setDefaultEditor(Object.class, new DefaultCellEditor(textField));
        getRootPane().setDefaultButton(okButton);
    }

    public class EditorTextField extends JTextField {

        private static final long serialVersionUID = 1L;

        private boolean appendFirstKey;
        private boolean firstHandled;

        @Override public void addNotify() {
            super.addNotify();
            selectAll();
            firstHandled = false;
        }

        public void setAppendFirstKey(boolean appendFirst) {
            this.appendFirstKey = appendFirst;
        }

        @Override protected boolean processKeyBinding(KeyStroke ks, KeyEvent e, int condition,
            boolean pressed) {
            checkSelection(e);
            return super.processKeyBinding(ks, e, condition, pressed);
        }

        private void checkSelection(KeyEvent e) {
            if (!appendFirstKey || firstHandled)
                return;
            firstHandled = true;
            if ((e == null) || (e.getSource() != this)) {
                clearSelection();
            }
        }

        private void clearSelection() {
            Document doc = getDocument();
            select(doc.getLength(), doc.getLength());
        }

    }


    private class MarkValuesCaretListener implements CaretListener {
        public void caretUpdate(CaretEvent e) {
            JTextField source = (JTextField) e.getSource();
            int column = checkboxValuesTable.getSelectedColumn();
            int row = checkboxValuesTable.getSelectedRow();
            String newText = source.getText();
            String oldText = (String) checkboxValuesTable.getValueAt(row, column);
            if (oldText == null || !(oldText.equals(newText))) {
                checkboxValuesTable.setValueAt(newText, row, column);
                updatePreview();
            }
        }
    }

    public boolean existsDuplicateFieldname(String newFieldName) {

        if (figCheckbox != null) {
            List layerContents = figCheckbox.getGraph().getEditor().getLayerManager().getContents();
            for (int i = 0; i < layerContents.size(); i++) {
                Fig fig = (Fig) layerContents.get(i);
                if (fig instanceof FigCheckbox) {
                    if (fig == figCheckbox) {
                        continue;
                    }
                    if (((FigCheckbox) fig).getFieldname().equalsIgnoreCase(newFieldName.trim())) {
                        return true;
                    }
                }
            }
        }

        return false;

    }

    private void updatePresetStyles() {
        markAreaPresetStyles = PreferencesManager.getMarkAreaPresetStyles();

        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        for (MarkAreaPresetStyle markAreaPresetStyle : markAreaPresetStyles) {
            dcbm.addElement(markAreaPresetStyle.getName());
        }
        markAreaPresetStylesComboBox.setModel(dcbm);
    }

    private boolean validateSettings() {

        if (getFieldname().trim().equals(getMarkFieldname().trim())) {
            String msg =
                Localizer.localize("UI", "CheckboxPropertiesFieldnameAlreadyExistsMessage");
            Misc.showErrorMsg(Main.getInstance(), msg);
            return false;
        }

        if (existsDuplicateFieldname(getFieldname())) {
            String msg =
                Localizer.localize("UI", "CheckboxPropertiesFieldnameAlreadyExistsMessage");
            Misc.showErrorMsg(Main.getInstance(), msg);
            return false;
        }

        if (Misc.validateFieldname(getFieldname()) == false) {
            String msg =
                Localizer.localize("UI", "CheckboxPropertiesFieldnameInvalidLengthMessage");
            Misc.showErrorMsg(Main.getInstance(), msg);
            return false;
        }

        return true;
    }

    private void defaultsButtonActionPerformed(ActionEvent e) {

        RestoreMarkAreaPresetStyleDialog rmapsd = new RestoreMarkAreaPresetStyleDialog(this);
        rmapsd.setTitle(Localizer.localize("UI", "CheckboxPropertiesRestoreDefaultStyleTitle"));
        rmapsd.setRestoreMessage(
            Localizer.localize("UI", "CheckboxPropertiesRestoreDefaultStyleMessage"));
        rmapsd.setModal(true);
        rmapsd.setVisible(true);

        if (rmapsd.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {

            if (rmapsd.isRestoreBoxDesign()) {
                setBoxWidth(16);
                setBoxHeight(9);
                setWidthRoundness(65);
                setHeightRoundness(65);
                setBoxWeight(1.0f);
                setCheckboxFontSize(6.0f);
                setFontDarkness(100);
                setShowText(true);
                setCombineColumnCharacters(false);
                setCombinedColumnReadDirection("LR");
                setReconciliationKey(false);
            }

            if (rmapsd.isRestoreSpacing()) {
                setHorizontalSpace(12);
                setVerticalSpace(10);
            }

            if (rmapsd.isRestoreAggregationRule()) {
                setAggregationRule("A?+1");
            }

            if (rmapsd.isRestoreMarkAreaValues()) {
                TableModel fctm = getFigCheckboxTableModel();
                columnSpinner.setValue(new Integer(fctm.getColumnCount()));
                rowSpinner.setValue(new Integer(fctm.getRowCount()));
                checkboxValuesTable.setModel(getFigCheckboxTableModel());
            }

            updatePreview();

        } else {
            rmapsd.dispose();
            return;
        }

        rmapsd.dispose();

    }

    private void boxHeightSpinnerStateChanged(ChangeEvent e) {
        updatePreview();
    }

    private void boxWeightStateChanged(ChangeEvent e) {
        updatePreview();
    }

    private void verticalSpaceStateChanged(ChangeEvent e) {
        updatePreview();
    }

    private void horizontalSpaceStateChanged(ChangeEvent e) {
        updatePreview();
    }

    private void widthRoundnessSpinnerStateChanged(ChangeEvent e) {
        updatePreview();
    }

    private void fontSizeSpinnerStateChanged(ChangeEvent e) {
        updatePreview();
    }

    private void fontDarknessSpinnerStateChanged(ChangeEvent e) {
        updatePreview();
    }

    private void boxWidthSpinnerStateChanged(ChangeEvent e) {
        updatePreview();
    }

    private void validateAggregationRuleButtonActionPerformed(ActionEvent e) {
        validateAggregationRule();
    }

    private void validateAggregationRule() {

        ValidateAggregationRuleDialog vard = new ValidateAggregationRuleDialog(Main.getInstance());
        vard.setAggregationRule(getAggregationRule());
        vard.setModal(true);
        vard.setVisible(true);
        if (vard.getDialogResult() == JOptionPane.OK_OPTION) {
            aggregationRuleTextField.setText(vard.getAggregationRule());
        }

    }

    private void restorePresetStyleButtonActionPerformed(ActionEvent e) {
        markAreaPresetStyles = PreferencesManager.getMarkAreaPresetStyles();

        String selectedStyleName = (String) markAreaPresetStylesComboBox.getSelectedItem();

        if (selectedStyleName == null || selectedStyleName == "") {
            return;
        }
        MarkAreaPresetStyle selectedStyle = null;

        for (MarkAreaPresetStyle markAreaPresetStyle : markAreaPresetStyles) {
            if (markAreaPresetStyle.getName().trim().equalsIgnoreCase(selectedStyleName)) {
                selectedStyle = markAreaPresetStyle;
                break;
            }
        }

        if (selectedStyle != null) {

            RestoreMarkAreaPresetStyleDialog rmapsd = new RestoreMarkAreaPresetStyleDialog(this);
            rmapsd.setTitle(Localizer.localize("UI", "CheckboxPropertiesRestorePresetStyleTitle"));
            rmapsd.setRestoreMessage(String
                .format(Localizer.localize("UI", "CheckboxPropertiesRestorePresetStyleMessage"),
                    selectedStyleName));
            rmapsd.setModal(true);
            rmapsd.setVisible(true);

            if (rmapsd.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {

                if (rmapsd.isRestoreBoxDesign()) {
                    setBoxWidth(selectedStyle.getBoxWidth());
                    setBoxHeight(selectedStyle.getBoxHeight());
                    setWidthRoundness(selectedStyle.getWidthRoundness());
                    setHeightRoundness(selectedStyle.getHeightRoundness());
                    setBoxWeight(selectedStyle.getBoxWeight());
                    setCheckboxFontSize(selectedStyle.getFontSize());
                    setFontDarkness(selectedStyle.getFontDarkness());
                    setShowText(selectedStyle.isShowText());
                    setCombineColumnCharacters(selectedStyle.isCombineColumnCharacters());
                    setCombinedColumnReadDirection(selectedStyle.getCombinedColumnReadDirection());
                    setReconciliationKey(selectedStyle.isReconciliationKey());
                }

                if (rmapsd.isRestoreSpacing()) {
                    setHorizontalSpace(selectedStyle.getHorizontalSpace());
                    setVerticalSpace(selectedStyle.getVerticalSpace());
                }

                if (rmapsd.isRestoreAggregationRule()) {
                    setAggregationRule(selectedStyle.getAggregationRule());
                }

                if (rmapsd.isRestoreMarkAreaValues()) {
                    columnSpinner.setValue(new Integer(selectedStyle.getColumnCount()));
                    rowSpinner.setValue(new Integer(selectedStyle.getRowCount()));
                    String[] columnHeadings = new String[selectedStyle.getColumnCount()];
                    for (int i = 1; i <= selectedStyle.getColumnCount(); i++) {
                        columnHeadings[(i - 1)] = i + "";
                    }
                    DefaultTableModel dtm =
                        new DefaultTableModel(selectedStyle.getCheckboxValues(), columnHeadings);
                    checkboxValuesTable.setModel(dtm);
                }

                updatePreview();

            } else {
                rmapsd.dispose();
                return;
            }

            rmapsd.dispose();

        }
    }

    private void setCombinedColumnReadDirection(String combinedColumnReadDirection) {
        if (combinedColumnReadDirection != null && combinedColumnReadDirection
            .equalsIgnoreCase("TB")) {
            combinedColumnReadDirectionComboBox.setSelectedIndex(1);
        } else {
            combinedColumnReadDirectionComboBox.setSelectedIndex(0);
        }
    }

    private void removePresetStyleButtonActionPerformed(ActionEvent e) {
        markAreaPresetStyles = PreferencesManager.getMarkAreaPresetStyles();

        String selectedStyleName = (String) markAreaPresetStylesComboBox.getSelectedItem();

        if (selectedStyleName == null || selectedStyleName == "") {
            return;
        }

        MarkAreaPresetStyle removeStyle = null;

        for (MarkAreaPresetStyle markAreaPresetStyle : markAreaPresetStyles) {
            if (markAreaPresetStyle.getName().trim().equalsIgnoreCase(selectedStyleName)) {
                removeStyle = markAreaPresetStyle;
            }
        }
        if (removeStyle != null) {
            Object[] options = {Localizer.localize("UI", "Yes"), Localizer.localize("UI", "No")};
            String msg = String
                .format(Localizer.localize("UI", "CheckboxPropertiesRemovePresetStyleMessage"),
                    selectedStyleName);
            int result = JOptionPane.showOptionDialog(this, msg,
                Localizer.localize("UI", "CheckboxPropertiesRemovePresetStyleTitle"),
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
            if (result == 0) {
                PreferencesManager.removeMarkAreaPresetStyle(removeStyle);
                try {
                    PreferencesManager.savePreferences(Main.getXstream());
                } catch (IOException e1) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                }
            }
        }
        updatePresetStyles();
    }

    private void saveAsPresetStyleButtonActionPerformed(ActionEvent e) {

        if (validateSettings() == false) {
            return;
        }

        AddNewMarkAreaPresetStyleDialog mapsd =
            new AddNewMarkAreaPresetStyleDialog(Main.getInstance());
        mapsd.setModal(true);
        mapsd.setVisible(true);

        if (mapsd.getDialogResult() == javax.swing.JOptionPane.OK_OPTION) {

            markAreaPresetStyles = PreferencesManager.getMarkAreaPresetStyles();

            for (MarkAreaPresetStyle markAreaPresetStyle : markAreaPresetStyles) {
                if (markAreaPresetStyle.getName().trim().equalsIgnoreCase(mapsd.getStyleName())) {
                    String msg =
                        Localizer.localize("UI", "CheckboxPropertiesStyleAlreadyExistsMessage");
                    Misc.showErrorMsg(Main.getInstance(), msg);
                    return;
                }
            }

            // create new preset style:

            MarkAreaPresetStyle maps = new MarkAreaPresetStyle();
            maps.setName(mapsd.getStyleName());
            maps.setAggregationRule(getAggregationRule());
            maps.setBoxHeight(getBoxHeight());
            maps.setBoxWeight(getBoxWeight());
            maps.setBoxWidth(getBoxWidth());
            maps.setRowCount((Integer) rowSpinner.getValue());
            maps.setColumnCount((Integer) columnSpinner.getValue());
            maps.setCheckboxValues(getCheckboxValues());
            maps.setFontDarkness(getFontDarkness());
            maps.setFontSize(getCheckboxFontSize());
            maps.setHeightRoundness(getHeightRoundness());
            maps.setHorizontalSpace(getHorizontalSpace());
            maps.setVerticalSpace(getVerticalSpace());
            maps.setWidthRoundness(getWidthRoundness());
            maps.setShowText(isShowText());
            maps.setCombineColumnCharacters(isCombineColumnCharacters());
            maps.setCombinedColumnReadDirection(getCombinedColumnReadDirection());
            maps.setReconciliationKey(isReconciliationKey());

            PreferencesManager.addMarkAreaPresetStyle(maps);
            try {
                PreferencesManager.savePreferences(Main.getXstream());
            } catch (IOException e1) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            }

            updatePresetStyles();

        }

        mapsd.dispose();

    }

    private String getCombinedColumnReadDirection() {
        if (this.combinedColumnReadDirectionComboBox.getSelectedIndex() == 1) {
            return "TB";
        } else {
            return "LR";
        }
    }

    private boolean isCombineColumnCharacters() {
        return combineColumnCharactersCheckBox.isSelected();
    }

    private void setCombineColumnCharacters(boolean isCombineColumnCharacters) {
        combineColumnCharactersCheckBox.setSelected(isCombineColumnCharacters);
    }

    private boolean isReconciliationKey() {
        return reconciliationKeyCheckBox.isSelected();
    }

    private void setReconciliationKey(boolean isReconciliationKey) {
        reconciliationKeyCheckBox.setSelected(isReconciliationKey);
    }

    private String getFieldname() {
        return fieldNameTextField.getText().trim();
    }

    private void showTextCheckBoxItemStateChanged(ItemEvent e) {
        updatePreview();
    }

    private void reconciliationKeyCheckBoxActionPerformed(ActionEvent e) {
        if (this.reconciliationKeyCheckBox.isSelected()) {
            combineColumnCharactersCheckBox.setSelected(true);
        } else {
            combineColumnCharactersCheckBox.setSelected(false);
        }
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                okButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        dialogPane = new JPanel();
        markAreaPropertiesDescriptionPanel = new JPanel();
        markAreaPropertiesDescriptionLabel = new JLabel();
        markAreaPropertiesDescriptionHelpLabel = new JHelpLabel();
        panel12 = new JPanel();
        dataExportSettingsPanel = new JPanel();
        cdfnLabel = new JLabel();
        fieldNameTextField = new JTextField();
        label16 = new JLabel();
        cdfnOrderLabel = new JLabel();
        cdfnOrderIndexSpinner = new JSpinner();
        scoreFieldNameLabel = new JLabel();
        markFieldNameTextField = new JTextField();
        label17 = new JLabel();
        scoreFieldNameOrderLabel = new JLabel();
        scoreFieldOrderIndexSpinner = new JSpinner();
        panel4 = new JPanel();
        markAreaSettingsPanel = new JPanel();
        panel15 = new JPanel();
        OMRIDMarkAreaLabel = new JLabel();
        panel11 = new JPanel();
        reconciliationKeyCheckBox = new JCheckBox();
        combineColumnLabel = new JLabel();
        panel10 = new JPanel();
        combineColumnCharactersCheckBox = new JCheckBox();
        panel1 = new JPanel();
        combinedColumnReadDirectionLabel = new JLabel();
        combinedColumnReadDirectionComboBox = new JComboBox();
        panel2 = new JPanel();
        rowsLabel = new JLabel();
        rowSpinner = new JSpinner();
        columnsLabel = new JLabel();
        columnSpinner = new JSpinner();
        verticalSpaceLabel = new JLabel();
        verticalSpace = new JSpinner();
        horizontalSpaceLabel = new JLabel();
        horizontalSpace = new JSpinner();
        boxWidthLabel = new JLabel();
        boxWidthSpinner = new JSpinner();
        boxHeightLabel = new JLabel();
        boxHeightSpinner = new JSpinner();
        boxWeightLabel = new JLabel();
        boxWeight = new JSpinner();
        boxRoundnessLabel = new JLabel();
        widthRoundnessSpinner = new JSpinner();
        fontSizeLabel = new JLabel();
        fontSizeSpinner = new JSpinner();
        fontBrightnessLabel = new JLabel();
        fontDarknessSpinner = new JSpinner();
        panel17 = new JPanel();
        visibleCheckboxTextLabel = new JLabel();
        showTextCheckBox = new JCheckBox();
        markValueEditorPanel = new JPanel();
        scrollPane2 = new JScrollPane();
        panel7 = new JPanel();
        previewLabel = new CheckBoxPreviewLabel();
        panel6 = new JPanel();
        scrollPane1 = new JScrollPane();
        checkboxValuesTable = new JTable();
        markValueEditorInstructionsLabel = new JLabel();
        panel9 = new JPanel();
        aggregationRuleLabel = new JLabel();
        aggregationRuleTextField = new JTextField();
        validateAggregationRuleButton = new JButton();
        markAggregationHelpLabel = new JHelpLabel();
        aggregationRuleDescriptionLabel = new JLabel();
        presetStylesPanel = new JPanel();
        markAreaPresetStylesComboBox = new JComboBox();
        restorePresetStyleButton = new JButton();
        removePresetStyleButton = new JButton();
        saveAsPresetStyleButton = new JButton();
        buttonBar = new JPanel();
        defaultsButton = new JButton();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "CheckboxPropertiesDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

            dialogPane.setLayout(new GridBagLayout());
            ((GridBagLayout)dialogPane.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout)dialogPane.getLayout()).rowHeights = new int[] {35, 0, 0, 0, 0, 0};
            ((GridBagLayout)dialogPane.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout)dialogPane.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4};

            //======== markAreaPropertiesDescriptionPanel ========
            {
                markAreaPropertiesDescriptionPanel.setBorder(new MatteBorder(0, 0, 3, 0, Color.gray));
                markAreaPropertiesDescriptionPanel.setOpaque(false);
                markAreaPropertiesDescriptionPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)markAreaPropertiesDescriptionPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                ((GridBagLayout)markAreaPropertiesDescriptionPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)markAreaPropertiesDescriptionPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout)markAreaPropertiesDescriptionPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //---- markAreaPropertiesDescriptionLabel ----
                markAreaPropertiesDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                markAreaPropertiesDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                markAreaPropertiesDescriptionLabel.setText("<html><body><strong>" + Localizer.localize("UI", "MarkAreaPropertiesDescriptionLabel") + "</strong></body></html>");
                markAreaPropertiesDescriptionPanel.add(markAreaPropertiesDescriptionLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- markAreaPropertiesDescriptionHelpLabel ----
                markAreaPropertiesDescriptionHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                markAreaPropertiesDescriptionHelpLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                markAreaPropertiesDescriptionHelpLabel.setFont(UIManager.getFont("Label.font"));
                markAreaPropertiesDescriptionHelpLabel.setHelpGUID("mark-area-properties");
                markAreaPropertiesDescriptionHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                markAreaPropertiesDescriptionPanel.add(markAreaPropertiesDescriptionHelpLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 5), 0, 0));
            }
            dialogPane.add(markAreaPropertiesDescriptionPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

            //======== panel12 ========
            {
                panel12.setOpaque(false);
                panel12.setLayout(new GridBagLayout());
                ((GridBagLayout)panel12.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout)panel12.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)panel12.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                ((GridBagLayout)panel12.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //======== dataExportSettingsPanel ========
                {
                    dataExportSettingsPanel.setOpaque(false);
                    dataExportSettingsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)dataExportSettingsPanel.getLayout()).columnWidths = new int[] {10, 0, 0, 15, 0, 0, 5, 0};
                    ((GridBagLayout)dataExportSettingsPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                    ((GridBagLayout)dataExportSettingsPanel.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout)dataExportSettingsPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};
                    dataExportSettingsPanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UI", "CheckboxPropertiesDataExportSettingsPanelTitle")),
                        new EmptyBorder(2, 2, 2, 2)));

                    //---- cdfnLabel ----
                    cdfnLabel.setFont(UIManager.getFont("Label.font"));
                    cdfnLabel.setText(Localizer.localize("UI", "CheckboxPropertiesCDFNLabel"));
                    dataExportSettingsPanel.add(cdfnLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- fieldNameTextField ----
                    fieldNameTextField.setFont(UIManager.getFont("TextField.font"));
                    dataExportSettingsPanel.add(fieldNameTextField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- label16 ----
                    label16.setText("-");
                    label16.setFont(UIManager.getFont("Label.font"));
                    dataExportSettingsPanel.add(label16, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- cdfnOrderLabel ----
                    cdfnOrderLabel.setFont(UIManager.getFont("Label.font"));
                    cdfnOrderLabel.setText(Localizer.localize("UI", "CheckboxPropertiesCDFNOrderLabel"));
                    dataExportSettingsPanel.add(cdfnOrderLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- cdfnOrderIndexSpinner ----
                    cdfnOrderIndexSpinner.setModel(new SpinnerNumberModel(0, 0, 100000, 1));
                    cdfnOrderIndexSpinner.setFont(UIManager.getFont("Spinner.font"));
                    dataExportSettingsPanel.add(cdfnOrderIndexSpinner, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 5, 5), 0, 0));

                    //---- scoreFieldNameLabel ----
                    scoreFieldNameLabel.setFont(UIManager.getFont("Label.font"));
                    scoreFieldNameLabel.setText(Localizer.localize("UI", "CheckboxPropertiesScoreFieldNameLabel"));
                    dataExportSettingsPanel.add(scoreFieldNameLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.EAST, GridBagConstraints.NONE,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- markFieldNameTextField ----
                    markFieldNameTextField.setFont(UIManager.getFont("TextField.font"));
                    dataExportSettingsPanel.add(markFieldNameTextField, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- label17 ----
                    label17.setText("-");
                    label17.setFont(UIManager.getFont("Label.font"));
                    dataExportSettingsPanel.add(label17, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- scoreFieldNameOrderLabel ----
                    scoreFieldNameOrderLabel.setFont(UIManager.getFont("Label.font"));
                    scoreFieldNameOrderLabel.setText(Localizer.localize("UI", "CheckboxPropertiesScoreFieldNameOrderLabel"));
                    dataExportSettingsPanel.add(scoreFieldNameOrderLabel, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));

                    //---- scoreFieldOrderIndexSpinner ----
                    scoreFieldOrderIndexSpinner.setModel(new SpinnerNumberModel(0, 0, 100000, 1));
                    scoreFieldOrderIndexSpinner.setFont(UIManager.getFont("Spinner.font"));
                    dataExportSettingsPanel.add(scoreFieldOrderIndexSpinner, new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 5), 0, 0));
                }
                panel12.add(dataExportSettingsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(panel12, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

            //======== panel4 ========
            {
                panel4.setOpaque(false);
                panel4.setLayout(new GridBagLayout());
                ((GridBagLayout)panel4.getLayout()).columnWidths = new int[] {0, 250, 0};
                ((GridBagLayout)panel4.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)panel4.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
                ((GridBagLayout)panel4.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== markAreaSettingsPanel ========
                {
                    markAreaSettingsPanel.setOpaque(false);
                    markAreaSettingsPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)markAreaSettingsPanel.getLayout()).columnWidths = new int[] {7, 0, 5, 0};
                    ((GridBagLayout)markAreaSettingsPanel.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0};
                    ((GridBagLayout)markAreaSettingsPanel.getLayout()).columnWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};
                    ((GridBagLayout)markAreaSettingsPanel.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 0.0, 1.0E-4};
                    markAreaSettingsPanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UI", "CheckboxPropertiesMarkAreaSettingsPanelTitle")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //======== panel15 ========
                    {
                        panel15.setOpaque(false);
                        panel15.setBorder(null);
                        panel15.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel15.getLayout()).columnWidths = new int[] {0, 0, 0, 10, 0};
                        ((GridBagLayout)panel15.getLayout()).rowHeights = new int[] {30, 30, 0};
                        ((GridBagLayout)panel15.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)panel15.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

                        //---- OMRIDMarkAreaLabel ----
                        OMRIDMarkAreaLabel.setFont(UIManager.getFont("Label.font"));
                        OMRIDMarkAreaLabel.setText(Localizer.localize("UI", "CheckboxPropertiesOMRIDMarkAreaLabel"));
                        panel15.add(OMRIDMarkAreaLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //======== panel11 ========
                        {
                            panel11.setOpaque(false);
                            panel11.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel11.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)panel11.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel11.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                            ((GridBagLayout)panel11.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- reconciliationKeyCheckBox ----
                            reconciliationKeyCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            reconciliationKeyCheckBox.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    reconciliationKeyCheckBoxActionPerformed(e);
                                }
                            });
                            panel11.add(reconciliationKeyCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        panel15.add(panel11, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- combineColumnLabel ----
                        combineColumnLabel.setFont(UIManager.getFont("Label.font"));
                        combineColumnLabel.setText(Localizer.localize("UI", "CheckboxPropertiesCombineColumnLabel"));
                        panel15.add(combineColumnLabel, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //======== panel10 ========
                        {
                            panel10.setOpaque(false);
                            panel10.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel10.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)panel10.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel10.getLayout()).columnWeights = new double[] {0.0, 1.0E-4};
                            ((GridBagLayout)panel10.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                            //---- combineColumnCharactersCheckBox ----
                            combineColumnCharactersCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                            panel10.add(combineColumnCharactersCheckBox, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        panel15.add(panel10, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 2), 0, 0));
                    }
                    markAreaSettingsPanel.add(panel15, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 2, 2), 0, 0));

                    //======== panel1 ========
                    {
                        panel1.setOpaque(false);
                        panel1.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {0, 0, 0};
                        ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {30, 0};
                        ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {1.0, 1.0, 1.0E-4};
                        ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- combinedColumnReadDirectionLabel ----
                        combinedColumnReadDirectionLabel.setFont(UIManager.getFont("Label.font"));
                        combinedColumnReadDirectionLabel.setText(Localizer.localize("UI", "CheckboxPropertiesCombinedColumnReadDirectionLabel"));
                        panel1.add(combinedColumnReadDirectionLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- combinedColumnReadDirectionComboBox ----
                        combinedColumnReadDirectionComboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxx");
                        combinedColumnReadDirectionComboBox.setFont(UIManager.getFont("ComboBox.font"));
                        panel1.add(combinedColumnReadDirectionComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.WEST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    markAreaSettingsPanel.add(panel1, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 2, 2), 0, 0));

                    //======== panel2 ========
                    {
                        panel2.setBorder(new CompoundBorder(
                            new MatteBorder(1, 0, 0, 0, Color.lightGray),
                            new EmptyBorder(5, 0, 0, 0)));
                        panel2.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0, 12, 0, 0, 0};
                        ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
                        ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};

                        //---- rowsLabel ----
                        rowsLabel.setFont(UIManager.getFont("Label.font"));
                        rowsLabel.setText(Localizer.localize("UI", "CheckboxPropertiesRowsLabel"));
                        panel2.add(rowsLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- rowSpinner ----
                        rowSpinner.setModel(new SpinnerNumberModel(1, 1, 200, 1));
                        rowSpinner.setFont(UIManager.getFont("Spinner.font"));
                        rowSpinner.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                rowSpinnerStateChanged(e);
                            }
                        });
                        panel2.add(rowSpinner, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- columnsLabel ----
                        columnsLabel.setFont(UIManager.getFont("Label.font"));
                        columnsLabel.setText(Localizer.localize("UI", "CheckboxPropertiesColumnsLabel"));
                        panel2.add(columnsLabel, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- columnSpinner ----
                        columnSpinner.setModel(new SpinnerNumberModel(3, 1, 200, 1));
                        columnSpinner.setFont(UIManager.getFont("Spinner.font"));
                        columnSpinner.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                columnSpinnerStateChanged(e);
                            }
                        });
                        panel2.add(columnSpinner, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));

                        //---- verticalSpaceLabel ----
                        verticalSpaceLabel.setFont(UIManager.getFont("Label.font"));
                        verticalSpaceLabel.setText(Localizer.localize("UI", "CheckboxPropertiesVerticalSpaceLabel"));
                        panel2.add(verticalSpaceLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- verticalSpace ----
                        verticalSpace.setModel(new SpinnerNumberModel(10, 2, 500, 1));
                        verticalSpace.setFont(UIManager.getFont("Spinner.font"));
                        verticalSpace.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                verticalSpaceStateChanged(e);
                            }
                        });
                        panel2.add(verticalSpace, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- horizontalSpaceLabel ----
                        horizontalSpaceLabel.setFont(UIManager.getFont("Label.font"));
                        horizontalSpaceLabel.setText(Localizer.localize("UI", "CheckboxPropertiesHorizontalSpaceLabel"));
                        panel2.add(horizontalSpaceLabel, new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- horizontalSpace ----
                        horizontalSpace.setModel(new SpinnerNumberModel(12, 2, 500, 1));
                        horizontalSpace.setFont(UIManager.getFont("Spinner.font"));
                        horizontalSpace.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                horizontalSpaceStateChanged(e);
                            }
                        });
                        panel2.add(horizontalSpace, new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));

                        //---- boxWidthLabel ----
                        boxWidthLabel.setFont(UIManager.getFont("Label.font"));
                        boxWidthLabel.setText(Localizer.localize("UI", "CheckboxPropertiesBoxWidthLabel"));
                        panel2.add(boxWidthLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- boxWidthSpinner ----
                        boxWidthSpinner.setModel(new SpinnerNumberModel(16, 3, 300, 1));
                        boxWidthSpinner.setFont(UIManager.getFont("Spinner.font"));
                        boxWidthSpinner.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                boxWidthSpinnerStateChanged(e);
                            }
                        });
                        panel2.add(boxWidthSpinner, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- boxHeightLabel ----
                        boxHeightLabel.setFont(UIManager.getFont("Label.font"));
                        boxHeightLabel.setText(Localizer.localize("UI", "CheckboxPropertiesBoxHeightLabel"));
                        panel2.add(boxHeightLabel, new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- boxHeightSpinner ----
                        boxHeightSpinner.setModel(new SpinnerNumberModel(9, 3, 300, 1));
                        boxHeightSpinner.setFont(UIManager.getFont("Spinner.font"));
                        boxHeightSpinner.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                boxHeightSpinnerStateChanged(e);
                            }
                        });
                        panel2.add(boxHeightSpinner, new GridBagConstraints(4, 2, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));

                        //---- boxWeightLabel ----
                        boxWeightLabel.setFont(UIManager.getFont("Label.font"));
                        boxWeightLabel.setText(Localizer.localize("UI", "CheckboxPropertiesBoxWeightLabel"));
                        panel2.add(boxWeightLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- boxWeight ----
                        boxWeight.setModel(new SpinnerNumberModel(1.0, 0.5, 10.0, 0.10000000149011612));
                        boxWeight.setFont(UIManager.getFont("Spinner.font"));
                        boxWeight.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                boxWeightStateChanged(e);
                            }
                        });
                        panel2.add(boxWeight, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- boxRoundnessLabel ----
                        boxRoundnessLabel.setFont(UIManager.getFont("Label.font"));
                        boxRoundnessLabel.setText(Localizer.localize("UI", "CheckboxPropertiesBoxRoundnessLabel"));
                        panel2.add(boxRoundnessLabel, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- widthRoundnessSpinner ----
                        widthRoundnessSpinner.setModel(new SpinnerNumberModel(0, 0, 100, 1));
                        widthRoundnessSpinner.setFont(UIManager.getFont("Spinner.font"));
                        widthRoundnessSpinner.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                widthRoundnessSpinnerStateChanged(e);
                            }
                        });
                        panel2.add(widthRoundnessSpinner, new GridBagConstraints(4, 3, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));

                        //---- fontSizeLabel ----
                        fontSizeLabel.setFont(UIManager.getFont("Label.font"));
                        fontSizeLabel.setText(Localizer.localize("UI", "CheckboxPropertiesFontSizeLabel"));
                        panel2.add(fontSizeLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- fontSizeSpinner ----
                        fontSizeSpinner.setModel(new SpinnerNumberModel(6, 0, 100, 1));
                        fontSizeSpinner.setFont(UIManager.getFont("Spinner.font"));
                        fontSizeSpinner.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                fontSizeSpinnerStateChanged(e);
                            }
                        });
                        panel2.add(fontSizeSpinner, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- fontBrightnessLabel ----
                        fontBrightnessLabel.setFont(UIManager.getFont("Label.font"));
                        fontBrightnessLabel.setText(Localizer.localize("UI", "CheckboxPropertiesFontBrightnessLabel"));
                        panel2.add(fontBrightnessLabel, new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.EAST, GridBagConstraints.NONE,
                            new Insets(0, 0, 0, 2), 0, 0));

                        //---- fontDarknessSpinner ----
                        fontDarknessSpinner.setModel(new SpinnerNumberModel(100, 0, 255, 1));
                        fontDarknessSpinner.setFont(UIManager.getFont("Spinner.font"));
                        fontDarknessSpinner.addChangeListener(new ChangeListener() {
                            @Override
                            public void stateChanged(ChangeEvent e) {
                                fontDarknessSpinnerStateChanged(e);
                            }
                        });
                        panel2.add(fontDarknessSpinner, new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    markAreaSettingsPanel.add(panel2, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 2, 2), 0, 0));

                    //======== panel17 ========
                    {
                        panel17.setOpaque(false);
                        panel17.setBorder(new EmptyBorder(0, 0, 2, 0));
                        panel17.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel17.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
                        ((GridBagLayout)panel17.getLayout()).rowHeights = new int[] {30, 0};
                        ((GridBagLayout)panel17.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
                        ((GridBagLayout)panel17.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- visibleCheckboxTextLabel ----
                        visibleCheckboxTextLabel.setFont(UIManager.getFont("Label.font"));
                        visibleCheckboxTextLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                        visibleCheckboxTextLabel.setText(Localizer.localize("UI", "CheckboxPropertiesVisibleCheckboxTextLabel"));
                        panel17.add(visibleCheckboxTextLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- showTextCheckBox ----
                        showTextCheckBox.setFont(UIManager.getFont("CheckBox.font"));
                        showTextCheckBox.addItemListener(new ItemListener() {
                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                showTextCheckBoxItemStateChanged(e);
                            }
                        });
                        panel17.add(showTextCheckBox, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    markAreaSettingsPanel.add(panel17, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                        new Insets(0, 0, 0, 2), 0, 0));
                }
                panel4.add(markAreaSettingsPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //======== markValueEditorPanel ========
                {
                    markValueEditorPanel.setOpaque(false);
                    markValueEditorPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout)markValueEditorPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout)markValueEditorPanel.getLayout()).rowHeights = new int[] {85, 0, 0, 0, 0, 0};
                    ((GridBagLayout)markValueEditorPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                    ((GridBagLayout)markValueEditorPanel.getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
                    markValueEditorPanel.setBorder(new CompoundBorder(
                        new TitledBorder(Localizer.localize("UI", "CheckboxPropertiesMarkValueEditorPanelTitle")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //======== scrollPane2 ========
                    {
                        scrollPane2.setBorder(null);

                        //======== panel7 ========
                        {
                            panel7.setBackground(Color.white);
                            panel7.setBorder(new CompoundBorder(
                                new MatteBorder(1, 1, 1, 1, Color.lightGray),
                                new EmptyBorder(5, 5, 5, 5)));
                            panel7.setLayout(new GridBagLayout());
                            ((GridBagLayout)panel7.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout)panel7.getLayout()).rowHeights = new int[] {0, 0};
                            ((GridBagLayout)panel7.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                            ((GridBagLayout)panel7.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                            //---- previewLabel ----
                            previewLabel.setText(" ");
                            panel7.add(previewLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        scrollPane2.setViewportView(panel7);
                    }
                    markValueEditorPanel.add(scrollPane2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== panel6 ========
                    {
                        panel6.setOpaque(false);
                        panel6.setBorder(null);
                        panel6.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel6.getLayout()).columnWidths = new int[] {0, 0};
                        ((GridBagLayout)panel6.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel6.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
                        ((GridBagLayout)panel6.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                        //======== scrollPane1 ========
                        {

                            //---- checkboxValuesTable ----
                            checkboxValuesTable.setCellSelectionEnabled(true);
                            checkboxValuesTable.setGridColor(Color.lightGray);
                            checkboxValuesTable.setFont(UIManager.getFont("Table.font"));
                            checkboxValuesTable.getTableHeader().setFont(UIManager.getFont("TableHeader.font"));
                            scrollPane1.setViewportView(checkboxValuesTable);
                        }
                        panel6.add(scrollPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                            new Insets(0, 0, 0, 0), 0, 0));
                    }
                    markValueEditorPanel.add(panel6, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- markValueEditorInstructionsLabel ----
                    markValueEditorInstructionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    markValueEditorInstructionsLabel.setFont(UIManager.getFont("Label.font"));
                    markValueEditorInstructionsLabel.setText(Localizer.localize("UI", "CheckboxPropertiesMarkValueEditorInstructionsLabel"));
                    markValueEditorPanel.add(markValueEditorInstructionsLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //======== panel9 ========
                    {
                        panel9.setOpaque(false);
                        panel9.setBorder(new CompoundBorder(
                            new MatteBorder(1, 0, 0, 0, Color.lightGray),
                            new EmptyBorder(5, 0, 0, 0)));
                        panel9.setLayout(new GridBagLayout());
                        ((GridBagLayout)panel9.getLayout()).columnWidths = new int[] {11, 0, 0, 0, 0, 6, 0};
                        ((GridBagLayout)panel9.getLayout()).rowHeights = new int[] {0, 0};
                        ((GridBagLayout)panel9.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
                        ((GridBagLayout)panel9.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                        //---- aggregationRuleLabel ----
                        aggregationRuleLabel.setFont(UIManager.getFont("Label.font"));
                        aggregationRuleLabel.setText(Localizer.localize("UI", "CheckboxPropertiesAggregationRuleLabel"));
                        panel9.add(aggregationRuleLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- aggregationRuleTextField ----
                        aggregationRuleTextField.setFont(UIManager.getFont("TextField.font"));
                        panel9.add(aggregationRuleTextField, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- validateAggregationRuleButton ----
                        validateAggregationRuleButton.setFont(UIManager.getFont("Button.font"));
                        validateAggregationRuleButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                validateAggregationRuleButtonActionPerformed(e);
                            }
                        });
                        validateAggregationRuleButton.setText(Localizer.localize("UI", "ValidateAggregationRuleButtonText"));
                        panel9.add(validateAggregationRuleButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));

                        //---- markAggregationHelpLabel ----
                        markAggregationHelpLabel.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/help.png")));
                        markAggregationHelpLabel.setHelpGUID("mark-area-aggregation-rule");
                        markAggregationHelpLabel.setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
                        panel9.add(markAggregationHelpLabel, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                            GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                            new Insets(0, 0, 0, 5), 0, 0));
                    }
                    markValueEditorPanel.add(panel9, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 5, 0), 0, 0));

                    //---- aggregationRuleDescriptionLabel ----
                    aggregationRuleDescriptionLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
                    aggregationRuleDescriptionLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    aggregationRuleDescriptionLabel.setText(Localizer.localize("UI", "CheckboxPropertiesAggregationRuleDescriptionLabel"));
                    markValueEditorPanel.add(aggregationRuleDescriptionLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                        new Insets(0, 0, 0, 0), 0, 0));
                }
                panel4.add(markValueEditorPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(panel4, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

            //======== presetStylesPanel ========
            {
                presetStylesPanel.setLayout(new GridBagLayout());
                ((GridBagLayout)presetStylesPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0};
                ((GridBagLayout)presetStylesPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)presetStylesPanel.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout)presetStylesPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                presetStylesPanel.setBorder(new CompoundBorder(
                    new TitledBorder(Localizer.localize("UI", "CheckboxPropertiesPresetStylesPanelTitle")),
                    new EmptyBorder(2, 2, 2, 2)));

                //---- markAreaPresetStylesComboBox ----
                markAreaPresetStylesComboBox.setFont(UIManager.getFont("ComboBox.font"));
                markAreaPresetStylesComboBox.setMaximumSize(new Dimension(140, 32767));
                markAreaPresetStylesComboBox.setFocusable(false);
                markAreaPresetStylesComboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxx");
                presetStylesPanel.add(markAreaPresetStylesComboBox, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- restorePresetStyleButton ----
                restorePresetStyleButton.setFont(UIManager.getFont("Button.font"));
                restorePresetStyleButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        restorePresetStyleButtonActionPerformed(e);
                    }
                });
                restorePresetStyleButton.setText(Localizer.localize("UI", "RestoreButtonText"));
                presetStylesPanel.add(restorePresetStyleButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- removePresetStyleButton ----
                removePresetStyleButton.setFont(UIManager.getFont("Button.font"));
                removePresetStyleButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        removePresetStyleButtonActionPerformed(e);
                    }
                });
                removePresetStyleButton.setText(Localizer.localize("UI", "RemoveButtonText"));
                presetStylesPanel.add(removePresetStyleButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- saveAsPresetStyleButton ----
                saveAsPresetStyleButton.setFont(UIManager.getFont("Button.font"));
                saveAsPresetStyleButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveAsPresetStyleButtonActionPerformed(e);
                    }
                });
                saveAsPresetStyleButton.setText(Localizer.localize("UI", "CheckboxPropertiesSaveAsPresetStyleButtonText"));
                presetStylesPanel.add(saveAsPresetStyleButton, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 5), 0, 0));
            }
            dialogPane.add(presetStylesPanel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 5, 0), 0, 0));

            //======== buttonBar ========
            {
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {75, 5, 0, 0, 6, 75, 0};
                ((GridBagLayout)buttonBar.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
                ((GridBagLayout)buttonBar.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- defaultsButton ----
                defaultsButton.setFont(UIManager.getFont("Button.font"));
                defaultsButton.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                defaultsButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        defaultsButtonActionPerformed(e);
                    }
                });
                defaultsButton.setText(Localizer.localize("UI", "ResetToDefaultsButtonText"));
                buttonBar.add(defaultsButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));

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
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));

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
                buttonBar.add(cancelButton, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(950, 600);
        setLocationRelativeTo(null);
        // //GEN-END:initComponents
    }

    public void updatePreview() {
        previewLabel.setBoxWidth(getBoxWidth());
        previewLabel.setBoxHeight(getBoxHeight());
        previewLabel.setBoxWeight(getBoxWeight());
        previewLabel.setHorizontalSpace(getHorizontalSpace());
        previewLabel.setVerticalSpace(getVerticalSpace());
        previewLabel.setRowCount(getRowCount());
        previewLabel.setColumnCount(getColumnCount());
        previewLabel.setCheckboxValues(getCheckboxValues());
        previewLabel.setWidthRoundness(getWidthRoundness());
        previewLabel.setFontDarkness(getFontDarkness());
        previewLabel.setHeightRoundness(getHeightRoundness());
        previewLabel.setFont(getCheckboxFont());
        previewLabel.setFontSize(getCheckboxFontSize());
        previewLabel.setShowText(isShowText());
        previewLabel.updatePreview();
    }

    private float getCheckboxFontSize() {
        return ((Integer) fontSizeSpinner.getValue()).floatValue();
    }

    private Font getCheckboxFont() {
        return Main.getCachedFontManager().getDefaultFont();
    }

    private int getHeightRoundness() {
        // return ((Integer) heightRoundnessSpinner.getValue()).intValue();
        return ((Integer) widthRoundnessSpinner.getValue()).intValue();
    }

    private int getWidthRoundness() {
        return ((Integer) widthRoundnessSpinner.getValue()).intValue();
    }

    private void setWidthRoundness(int widthRoundness) {
        widthRoundnessSpinner.setValue(new Integer(widthRoundness));
    }

    private void setHeightRoundness(int heightRoundness) {
        // heightRoundnessSpinner.setValue(new Integer(heightRoundness));
    }

    private int getColumnCount() {
        return ((Integer) columnSpinner.getValue()).intValue();
    }

    private int getRowCount() {
        return ((Integer) rowSpinner.getValue()).intValue();
    }

    public Stroke getStroke() {
        BasicStroke stroke = null;
        stroke = new BasicStroke(getBoxWeight(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        return stroke;
    }

    public String getMarkFieldname() {
        return markFieldNameTextField.getText();
    }

    public void setMarkFieldname(String markFieldname) {
        markFieldNameTextField.setText(markFieldname);
    }

    public int getMarkFieldnameOrderIndex() {
        return (Integer) scoreFieldOrderIndexSpinner.getValue();
    }

    public void setMarkFieldnameOrderIndex(int markFieldnameOrderIndex) {
        scoreFieldOrderIndexSpinner.setValue(markFieldnameOrderIndex);
    }

    public int getFieldnameOrderIndex() {
        return (Integer) this.cdfnOrderIndexSpinner.getValue();
    }

    public void setFieldnameOrderIndex(int fieldnameOrderIndex) {
        cdfnOrderIndexSpinner.setValue(fieldnameOrderIndex);
    }

    private void okButtonActionPerformed(ActionEvent e) {

        if (validateSettings() == false) {
            return;
        }

        CheckboxMemento memento = figCheckbox.getUpdateMemento();
        memento.setOldCheckboxValues(figCheckbox.getCheckboxValues());
        memento.setOldBoxWidth(figCheckbox.getBoxWidth());
        memento.setOldBoxHeight(figCheckbox.getBoxHeight());
        memento.setOldWidthRoundness(figCheckbox.getWidthRoundness());
        memento.setOldHeightRoundness(figCheckbox.getHeightRoundness());
        memento.setOldFontDarkness(figCheckbox.getFontDarkness());
        memento.setOldBoxWeight(figCheckbox.getBoxWeight());
        memento.setOldCheckboxFontSize(figCheckbox.getFontSize());
        memento.setOldHorizontalSpace(figCheckbox.getHorizontalSpace());
        memento.setOldVerticalSpace(figCheckbox.getVerticalSpace());
        memento.setOldFieldname(figCheckbox.getFieldname());
        memento.setOldAggregationRule(figCheckbox.getAggregationRule());
        memento.setOldShowText(figCheckbox.isShowText());
        memento.setOldCombineColumnCharacters(figCheckbox.isCombineColumnCharacters());
        memento.setOldCombinedColumnReadDirection(figCheckbox.getColumnReadDirection());
        memento.setOldReconciliationKey(figCheckbox.isReconciliationKey());
        memento.setOldMarkFieldname(figCheckbox.getMarkFieldname());
        memento.setOldMarkFieldnameOrderIndex(figCheckbox.getMarkFieldnameOrderIndex());
        memento.setOldFieldnameOrderIndex(figCheckbox.getFieldnameOrderIndex());

        memento.setNewCheckboxValues(getCheckboxValues());
        memento.setNewBoxWidth(getBoxWidth());
        memento.setNewBoxHeight(getBoxHeight());
        memento.setNewWidthRoundness(getWidthRoundness());
        memento.setNewHeightRoundness(getHeightRoundness());
        memento.setNewFontDarkness(getFontDarkness());
        memento.setNewBoxWeight(getBoxWeight());
        memento.setNewFontSize(getCheckboxFontSize());
        memento.setNewHorizontalSpace(getHorizontalSpace());
        memento.setNewVerticalSpace(getVerticalSpace());
        memento.setNewFieldname(fieldNameTextField.getText());
        memento.setNewAggregationRule(getAggregationRule());
        memento.setNewShowText(isShowText());
        memento.setNewCombineColumnCharacters(isCombineColumnCharacters());
        memento.setNewCombinedColumnReadDirection(getCombinedColumnReadDirection());
        memento.setNewReconciliationKey(isReconciliationKey());
        memento.setNewMarkFieldname(getMarkFieldname());
        memento.setNewMarkFieldnameOrderIndex(getMarkFieldnameOrderIndex());
        memento.setNewFieldnameOrderIndex(getFieldnameOrderIndex());

        setDialogResult(JOptionPane.OK_OPTION);
        // update to new table model.
        figCheckbox.setCheckboxValues(getCheckboxValues());
        // update properties of figCheckbox
        figCheckbox.setBoxWidth(getBoxWidth());
        figCheckbox.setBoxHeight(getBoxHeight());
        figCheckbox.setWidthRoundness(getWidthRoundness());
        figCheckbox.setHeightRoundness(getHeightRoundness());
        figCheckbox.setFontDarkness(getFontDarkness());
        figCheckbox.setBoxWeight(getBoxWeight());
        figCheckbox.setFontSize(getCheckboxFontSize());
        figCheckbox.setHorizontalSpace(getHorizontalSpace());
        figCheckbox.setVerticalSpace(getVerticalSpace());
        figCheckbox.setFieldname(fieldNameTextField.getText());
        figCheckbox.setAggregationRule(getAggregationRule());
        figCheckbox.setShowText(isShowText());
        figCheckbox.setCombineColumnCharacters(isCombineColumnCharacters());
        figCheckbox.setColumnReadDirection(getCombinedColumnReadDirection());
        figCheckbox.setReconciliationKey(isReconciliationKey());
        figCheckbox.setMarkFieldname(getMarkFieldname());
        figCheckbox.setMarkFieldnameOrderIndex(getMarkFieldnameOrderIndex());
        figCheckbox.setFieldnameOrderIndex(getFieldnameOrderIndex());

        dispose();

        figCheckbox.damage();

    }

    private void setShowText(boolean showText) {
        showTextCheckBox.setSelected(showText);
    }

    private boolean isShowText() {
        return showTextCheckBox.isSelected();
    }

    private String getAggregationRule() {
        return aggregationRuleTextField.getText().trim();
    }

    private void setAggregationRule(String aggregationRule) {
        aggregationRuleTextField.setText(aggregationRule);
    }

    private String[][] getCheckboxValues() {

        TableModel tableModel = checkboxValuesTable.getModel();
        String[][] checkboxValues =
            new String[tableModel.getRowCount()][tableModel.getColumnCount()];

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                if (tableModel.getValueAt(i, j) == null
                    || (String) tableModel.getValueAt(i, j) == "") {
                    checkboxValues[i][j] = " ";
                } else {
                    checkboxValues[i][j] = (String) tableModel.getValueAt(i, j);
                }
            }
        }

        return checkboxValues;

    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.CANCEL_OPTION);
        dispose();
    }

    private void updateValues() {

        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();
        dcbm.addElement(
            Localizer.localize("UI", "CheckboxPropertiesCombinedColumnReadDirectionLR"));
        dcbm.addElement(
            Localizer.localize("UI", "CheckboxPropertiesCombinedColumnReadDirectionTB"));
        combinedColumnReadDirectionComboBox.setModel(dcbm);

        TableModel tm = getFigCheckboxTableModel();
        rowSpinner.setModel(new SpinnerNumberModel(tm.getRowCount(), 1, 200, 1));
        columnSpinner.setModel(new SpinnerNumberModel(tm.getColumnCount(), 1, 200, 1));
        checkboxValuesTable.setModel(tm);

        setBoxWidth(figCheckbox.getBoxWidth());
        setBoxHeight(figCheckbox.getBoxHeight());
        setWidthRoundness(figCheckbox.getWidthRoundness());
        setHeightRoundness(figCheckbox.getHeightRoundness());
        setBoxWeight(figCheckbox.getBoxWeight());
        setCheckboxFontSize(figCheckbox.getFontSize());
        setHorizontalSpace(figCheckbox.getHorizontalSpace());
        setFontDarkness(figCheckbox.getFontDarkness());
        setVerticalSpace(figCheckbox.getVerticalSpace());
        setAggregationRule(figCheckbox.getAggregationRule());
        setShowText(figCheckbox.isShowText());
        setCombineColumnCharacters(figCheckbox.isCombineColumnCharacters());
        setCombinedColumnReadDirection(figCheckbox.getColumnReadDirection());
        setReconciliationKey(figCheckbox.isReconciliationKey());
        setMarkFieldname(figCheckbox.getMarkFieldname());
        setMarkFieldnameOrderIndex(figCheckbox.getMarkFieldnameOrderIndex());
        setFieldnameOrderIndex(figCheckbox.getFieldnameOrderIndex());
        fieldNameTextField.setText(figCheckbox.getFieldname());

    }


    private void setCheckboxFontSize(float fontSize) {
        fontSizeSpinner.setValue(new Integer(((Float) fontSize).intValue()));
    }

    public TableModel getFigCheckboxTableModel() {

        String[][] checkboxValues = figCheckbox.getCheckboxValues();

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.setColumnCount(figCheckbox.getColumnCount());
        tableModel.setRowCount(figCheckbox.getRowCount());

        Vector<String> columnIdentifiers = new Vector<String>();
        for (int i = 1; i <= tableModel.getColumnCount(); i++) {
            columnIdentifiers.add(i + "");
        }
        tableModel.setColumnIdentifiers(columnIdentifiers);

        for (int i = 0; i < tableModel.getRowCount(); i++) {
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                tableModel.setValueAt(checkboxValues[i][j], i, j);
            }
        }

        return tableModel;

    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    public int getFontDarkness() {
        return ((Integer) fontDarknessSpinner.getValue()).intValue();
    }

    public void setFontDarkness(int fontDarkness) {
        fontDarknessSpinner.setValue(new Integer(fontDarkness));
    }

    public int getBoxWidth() {
        return ((Integer) boxWidthSpinner.getValue()).intValue();
    }

    public void setBoxWidth(int boxWidth) {
        boxWidthSpinner.setValue(new Integer(boxWidth));
    }

    public int getBoxHeight() {
        return ((Integer) boxHeightSpinner.getValue()).intValue();
    }

    private void setBoxHeight(int boxHeight) {
        boxHeightSpinner.setValue(new Integer(boxHeight));
    }

    public float getBoxWeight() {
        return ((Double) boxWeight.getValue()).floatValue();
    }

    public void setBoxWeight(float newBoxWeight) {
        boxWeight.setValue(new Double(newBoxWeight));
    }

    public int getHorizontalSpace() {
        return ((Integer) horizontalSpace.getValue()).intValue();
    }

    public void setHorizontalSpace(int newHorizontalSpace) {
        setHorizontalSpace(new Integer(newHorizontalSpace));
    }

    public void setHorizontalSpace(Integer newHorizontalSpace) {
        horizontalSpace.setValue(newHorizontalSpace);
    }

    public int getVerticalSpace() {
        return ((Integer) verticalSpace.getValue()).intValue();
    }

    public void setVerticalSpace(int newVerticalSpace) {
        setVerticalSpace(new Integer(newVerticalSpace));
    }

    public void setVerticalSpace(Integer newVerticalSpace) {
        verticalSpace.setValue(newVerticalSpace);
    }

    private void rowSpinnerStateChanged(ChangeEvent e) {
        DefaultTableModel dtm = (DefaultTableModel) checkboxValuesTable.getModel();
        int currentRowCount = checkboxValuesTable.getRowCount();
        int newRowCount = ((Integer) rowSpinner.getValue()).intValue();

        if (currentRowCount < newRowCount) {

            // add rows
            for (int i = currentRowCount; i < newRowCount; i++) {
                dtm.addRow(new Vector<String>());
            }

        } else if (currentRowCount > newRowCount) {

            // remove rows
            for (int i = currentRowCount; i > newRowCount; i--) {
                dtm.removeRow(i - 1);
            }

        }

        updatePreview();

    }

    private void columnSpinnerStateChanged(ChangeEvent e) {
        DefaultTableModel dtm = (DefaultTableModel) checkboxValuesTable.getModel();
        DefaultTableColumnModel dtcm =
            (DefaultTableColumnModel) checkboxValuesTable.getColumnModel();

        int currentColumnCount = checkboxValuesTable.getColumnCount();
        int newColumnCount = ((Integer) columnSpinner.getValue()).intValue();

        if (currentColumnCount < newColumnCount) {

            // add Column
            for (int i = currentColumnCount; i < newColumnCount; i++) {
                dtm.addColumn("" + (checkboxValuesTable.getColumnCount() + 1));
            }

        } else if (currentColumnCount > newColumnCount) {

            // remove Column
            for (int i = currentColumnCount; i > newColumnCount; i--) {
                for (int j = 0; j < checkboxValuesTable.getRowCount(); j++) {
                    dtm.setValueAt(null, j, (checkboxValuesTable.getColumnCount() - 1));
                }

                TableColumn lastColumn = dtcm.getColumn(checkboxValuesTable.getColumnCount() - 1);
                checkboxValuesTable.removeColumn(lastColumn);

                Integer[] colIds = new Integer[checkboxValuesTable.getColumnCount()];

                for (int j = 0; j < checkboxValuesTable.getColumnCount(); j++) {
                    colIds[j] = new Integer(j + 1);
                }

                dtm.setColumnIdentifiers(colIds);

            }
        }

        updatePreview();

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel dialogPane;
    private JPanel markAreaPropertiesDescriptionPanel;
    private JLabel markAreaPropertiesDescriptionLabel;
    private JHelpLabel markAreaPropertiesDescriptionHelpLabel;
    private JPanel panel12;
    private JPanel dataExportSettingsPanel;
    private JLabel cdfnLabel;
    private JTextField fieldNameTextField;
    private JLabel label16;
    private JLabel cdfnOrderLabel;
    private JSpinner cdfnOrderIndexSpinner;
    private JLabel scoreFieldNameLabel;
    private JTextField markFieldNameTextField;
    private JLabel label17;
    private JLabel scoreFieldNameOrderLabel;
    private JSpinner scoreFieldOrderIndexSpinner;
    private JPanel panel4;
    private JPanel markAreaSettingsPanel;
    private JPanel panel15;
    private JLabel OMRIDMarkAreaLabel;
    private JPanel panel11;
    private JCheckBox reconciliationKeyCheckBox;
    private JLabel combineColumnLabel;
    private JPanel panel10;
    private JCheckBox combineColumnCharactersCheckBox;
    private JPanel panel1;
    private JLabel combinedColumnReadDirectionLabel;
    private JComboBox combinedColumnReadDirectionComboBox;
    private JPanel panel2;
    private JLabel rowsLabel;
    private JSpinner rowSpinner;
    private JLabel columnsLabel;
    private JSpinner columnSpinner;
    private JLabel verticalSpaceLabel;
    private JSpinner verticalSpace;
    private JLabel horizontalSpaceLabel;
    private JSpinner horizontalSpace;
    private JLabel boxWidthLabel;
    private JSpinner boxWidthSpinner;
    private JLabel boxHeightLabel;
    private JSpinner boxHeightSpinner;
    private JLabel boxWeightLabel;
    private JSpinner boxWeight;
    private JLabel boxRoundnessLabel;
    private JSpinner widthRoundnessSpinner;
    private JLabel fontSizeLabel;
    private JSpinner fontSizeSpinner;
    private JLabel fontBrightnessLabel;
    private JSpinner fontDarknessSpinner;
    private JPanel panel17;
    private JLabel visibleCheckboxTextLabel;
    private JCheckBox showTextCheckBox;
    private JPanel markValueEditorPanel;
    private JScrollPane scrollPane2;
    private JPanel panel7;
    private CheckBoxPreviewLabel previewLabel;
    private JPanel panel6;
    private JScrollPane scrollPane1;
    private JTable checkboxValuesTable;
    private JLabel markValueEditorInstructionsLabel;
    private JPanel panel9;
    private JLabel aggregationRuleLabel;
    private JTextField aggregationRuleTextField;
    private JButton validateAggregationRuleButton;
    private JHelpLabel markAggregationHelpLabel;
    private JLabel aggregationRuleDescriptionLabel;
    private JPanel presetStylesPanel;
    private JComboBox markAreaPresetStylesComboBox;
    private JButton restorePresetStyleButton;
    private JButton removePresetStyleButton;
    private JButton saveAsPresetStyleButton;
    private JPanel buttonBar;
    private JButton defaultsButton;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables

}

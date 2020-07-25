package com.ebstrada.formreturn.manager.util.preferences.panel;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;
import com.ebstrada.formreturn.manager.util.graph.GraphUtils;
import com.ebstrada.formreturn.manager.util.graph.SizeAttributes;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.dialog.CustomFormDialog;
import com.ebstrada.formreturn.manager.util.preferences.dialog.CustomSegmentDialog;

public class EditorPreferencesPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private Frame preferencesDialog;

    public EditorPreferencesPanel(JFrame preferencesDialog) {
        this.preferencesDialog = preferencesDialog;
        initComponents();
        restoreSettings();
    }

    private void restoreDefaultSegmentPageSizeList() {
        List<String> segmentSizeNames = PreferencesManager.getSegmentSizeNames();

        DefaultComboBoxModel sdcbm = new DefaultComboBoxModel();
        for (String segmentSizeName : segmentSizeNames) {
            sdcbm.addElement(segmentSizeName);
        }

        defaultSegmentPageSizeComboBox.setModel(sdcbm);

        SizeAttributes defaultSegmentSizeAttributes =
            PreferencesManager.getDefaultSegmentSizeAttributes();
        if (defaultSegmentSizeAttributes != null) {
            String defaultSegmentSizeName = defaultSegmentSizeAttributes.getName();
            for (int i = 0; i < defaultSegmentPageSizeComboBox.getItemCount(); i++) {
                if (defaultSegmentPageSizeComboBox.getItemAt(i).equals(defaultSegmentSizeName)) {
                    defaultSegmentPageSizeComboBox.setSelectedIndex(i);
                }
            }
        }

    }

    private void restoreDefaultFormPageSizeList() {

        DefaultComboBoxModel fdcbm = new DefaultComboBoxModel();
        List<String> formSizeNames = PreferencesManager.getFormSizeNames();
        for (String formSizeName : formSizeNames) {
            fdcbm.addElement(formSizeName);
        }
        defaultFormPageSizeComboBox.setModel(fdcbm);

        SizeAttributes defaultFormSizeAttributes =
            PreferencesManager.getDefaultFormSizeAttributes();

        if (defaultFormSizeAttributes != null) {
            String defaultFormSizeName = defaultFormSizeAttributes.getName();
            for (int i = 0; i < defaultFormPageSizeComboBox.getItemCount(); i++) {
                if (defaultFormPageSizeComboBox.getItemAt(i).equals(defaultFormSizeName)) {
                    defaultFormPageSizeComboBox.setSelectedIndex(i);
                    if (defaultFormSizeAttributes.getOrientation() == SizeAttributes.PORTRAIT) {
                        defaultFormOrientationComboBox.setSelectedItem("Portrait");
                    } else {
                        defaultFormOrientationComboBox.setSelectedItem("Landscape");
                    }
                }
            }
        }

    }

    private void restoreSettings() {
        segmentBarcodeScaleSpinner.setValue(PreferencesManager.getDefaultSegmentBarcodeSize());
        restoreDefaultSegmentPageSizeList();
        restoreDefaultFormPageSizeList();
        rebuildSegmentList();
        rebuildFormList();
    }

    private void rebuildSegmentList() {
        List<String> segmentSizeNames = PreferencesManager.getSegmentSizeNames();
        DefaultListModel dlm = new DefaultListModel();
        for (String segmentSizeName : segmentSizeNames) {
            if (segmentSizeName.equalsIgnoreCase("Custom")) {
                continue;
            }
            dlm.addElement(segmentSizeName);
        }
        segmentSizesList.setModel(dlm);
    }

    private void rebuildFormList() {
        List<String> formSizeNames = PreferencesManager.getFormSizeNames();

        DefaultListModel dlm = new DefaultListModel();
        for (String formSizeName : formSizeNames) {
            if (formSizeName.equalsIgnoreCase("Custom")) {
                continue;
            }
            dlm.addElement(formSizeName);
        }

        formSizesList.setModel(dlm);
    }

    private void saveDefaultSizesButtonActionPerformed(ActionEvent e) {

        if (defaultSegmentPageSizeComboBox.getSelectedIndex() != -1) {
            String size = (String) defaultSegmentPageSizeComboBox.getSelectedItem();
            SizeAttributes sizeAttributes = GraphUtils
                .getDefaultSizeAttributes(SizeAttributes.SEGMENT, SizeAttributes.PORTRAIT, size);
            PreferencesManager.setDefaultSegmentSizeAttributes(sizeAttributes);
        }

        if (defaultFormPageSizeComboBox.getSelectedIndex() != -1) {
            String size = (String) defaultFormPageSizeComboBox.getSelectedItem();
            int orientation = SizeAttributes.PORTRAIT;
            if (defaultFormOrientationComboBox.getSelectedItem().equals("Portrait")) {
                orientation = SizeAttributes.PORTRAIT;
            } else {
                orientation = SizeAttributes.LANDSCAPE;
            }
            SizeAttributes sizeAttributes =
                GraphUtils.getDefaultSizeAttributes(SizeAttributes.FORM, orientation, size);
            PreferencesManager.setDefaultFormSizeAttributes(sizeAttributes);
        }

        PreferencesManager
            .setDefaultSegmentBarcodeSize((Double) segmentBarcodeScaleSpinner.getValue());
        try {
            PreferencesManager.savePreferences(Main.getXstream());
        } catch (IOException e1) {
            Misc.showErrorMsg(this, Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            return;
        }

        Misc.showSuccessMsg(this,
            Localizer.localize("Util", "EditorPreferencesSavedSuccessfullyMessage"));
    }

    private void restoreDefaultSizesButtonActionPerformed(ActionEvent e) {
        SizeAttributes sa =
            GraphUtils.getDefaultSizeAttributes(SizeAttributes.FORM, SizeAttributes.PORTRAIT);
        defaultFormPageSizeComboBox.setSelectedItem(sa.getName());
        defaultSegmentPageSizeComboBox.setSelectedIndex(0);
        defaultFormOrientationComboBox.setSelectedItem("Portrait");
        segmentBarcodeScaleSpinner.setValue(new Double(0.6));
    }

    private void addNewSegmentSizeButtonActionPerformed(ActionEvent e) {

        CustomSegmentDialog csd = new CustomSegmentDialog(preferencesDialog);
        csd.setTitle(
            Localizer.localize("Util", "EditorPreferencesNewCustomSegmentSizeDialogTitle"));
        csd.setModal(true);
        csd.setVisible(true);

        if (csd.getDialogResult() == JOptionPane.OK_OPTION) {

            SizeAttributes[] sizeAttributesPair = new SizeAttributes[2];

            // because this is a segment, do the landscape one as well automatically
            SizeAttributes sa = new SizeAttributes();
            sa.setOrientation(SizeAttributes.PORTRAIT);
            sa.setHeight(csd.getPortraitHeight());
            sa.setWidth(csd.getPortraitWidth());
            sa.setBottomMargin(0);
            sa.setTopMargin(0);
            sa.setRightMargin(0);
            sa.setLeftMargin(0);
            sa.setName(csd.getCustomSegmentName());

            SizeAttributes lsa = new SizeAttributes();
            lsa.setOrientation(SizeAttributes.LANDSCAPE);
            lsa.setHeight(csd.getLandscapeWidth());
            lsa.setWidth(csd.getLandscapeHeight());
            lsa.setBottomMargin(0);
            lsa.setTopMargin(0);
            lsa.setRightMargin(0);
            lsa.setLeftMargin(0);
            lsa.setName(csd.getCustomSegmentName());

            sizeAttributesPair[(SizeAttributes.PORTRAIT - 1)] = sa;
            sizeAttributesPair[(SizeAttributes.LANDSCAPE - 1)] = lsa;

            List<SizeAttributes[]> ssa = PreferencesManager.getSegmentSizeAttributes();
            ssa.add(sizeAttributesPair);

            try {
                PreferencesManager.savePreferences(Main.getXstream());
            } catch (IOException e1) {
                Misc.showErrorMsg(this, Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                    Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                return;
            }

            Misc.showSuccessMsg(this,
                Localizer.localize("Util", "EditorPreferencesSavedSuccessfullyMessage"));

            rebuildSegmentList();
            restoreDefaultSegmentPageSizeList();

        }

        csd.dispose();

    }

    private void editSegmentSizeButtonActionPerformed(ActionEvent e) {

        // pick the segment from the list by the id.
        int selectedIndex = segmentSizesList.getSelectedIndex();
        if (selectedIndex != -1) {

            List<SizeAttributes[]> ssa = PreferencesManager.getSegmentSizeAttributes();
            SizeAttributes[] saPair = ssa.get(selectedIndex);
            SizeAttributes sa = saPair[(SizeAttributes.PORTRAIT - 1)];
            SizeAttributes lsa = saPair[(SizeAttributes.LANDSCAPE - 1)];

            CustomSegmentDialog csd = new CustomSegmentDialog(preferencesDialog);
            csd.setTitle(
                Localizer.localize("Util", "EditorPreferencesEditCustomSegmentSizeDialogTitle"));

            csd.setPortraitWidth(sa.getWidth());
            csd.setPortraitHeight(sa.getHeight());


            csd.setLandscapeWidth(lsa.getWidth());
            csd.setLandscapeHeight(lsa.getHeight());

            csd.setCustomSegmentName(sa.getName());

            csd.setModal(true);
            csd.setVisible(true);

            if (csd.getDialogResult() == JOptionPane.OK_OPTION) {

                sa.setName(csd.getCustomSegmentName());
                sa.setWidth(csd.getPortraitWidth());
                sa.setHeight(csd.getPortraitHeight());

                lsa.setName(csd.getCustomSegmentName());
                lsa.setHeight(csd.getLandscapeHeight());
                lsa.setWidth(csd.getLandscapeWidth());

                try {
                    PreferencesManager.savePreferences(Main.getXstream());
                } catch (IOException e1) {
                    Misc.showErrorMsg(this,
                        Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                        Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                    return;
                }

                Misc.showSuccessMsg(this,
                    Localizer.localize("Util", "EditorPreferencesSavedSuccessfullyMessage"));

                rebuildSegmentList();
                restoreDefaultSegmentPageSizeList();

            }

            csd.dispose();

        }

    }

    private void removeSegmentSizeButtonActionPerformed(ActionEvent e) {

        int selectedIndex = segmentSizesList.getSelectedIndex();
        if (selectedIndex != -1) {

            String message = String
                .format(Localizer.localize("Util", "EditorPreferencesConfirmRemoveMessage"),
                    segmentSizesList.getSelectedValue() + "");
            int n = JOptionPane.showConfirmDialog(null, message,
                Localizer.localize("Util", "EditorPreferencesConfirmRemoveSegmentTitle"),
                JOptionPane.YES_NO_OPTION);

            if (n != 0) {
                return;
            }

            List<SizeAttributes[]> ssa = PreferencesManager.getSegmentSizeAttributes();
            ssa.remove(selectedIndex);

            try {
                PreferencesManager.savePreferences(Main.getXstream());
            } catch (IOException e1) {
                Misc.showErrorMsg(this, Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                    Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                return;
            }

            rebuildSegmentList();
            restoreDefaultSegmentPageSizeList();

        }

    }

    private void restoreDefaultSegmentSizesButtonActionPerformed(ActionEvent e) {

        String message =
            Localizer.localize("Util", "EditorPreferencesConfirmRestoreDefaultSegmentSizesMessage");
        int n = JOptionPane.showConfirmDialog(null, message,
            Localizer.localize("Util", "EditorPreferencesConfirmRestoreDefaultSegmentSizesTitle"),
            JOptionPane.YES_NO_OPTION);

        if (n != 0) {
            return;
        }

        PreferencesManager.resetSegmentSizeAttributes();

        try {
            PreferencesManager.savePreferences(Main.getXstream());
        } catch (IOException e1) {
            Misc.showErrorMsg(this, Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            return;
        }

        Misc.showSuccessMsg(this, Localizer
            .localize("Util", "EditorPreferencesRestoreDefaultSegmentSizesSuccessMessage"));

        rebuildSegmentList();
        restoreDefaultSegmentPageSizeList();

    }

    private void addNewFormSizeButtonActionPerformed(ActionEvent e) {
        CustomFormDialog cfd = new CustomFormDialog(preferencesDialog);
        cfd.setTitle(Localizer.localize("Util", "EditorPreferencesNewCustomFormSizeDialogTitle"));
        cfd.setModal(true);
        cfd.setVisible(true);

        if (cfd.getDialogResult() == JOptionPane.OK_OPTION) {

            SizeAttributes[] sizeAttributesPair = new SizeAttributes[2];

            SizeAttributes sa = new SizeAttributes();
            sa.setOrientation(SizeAttributes.PORTRAIT);
            sa.setHeight(cfd.getPortraitHeight());
            sa.setWidth(cfd.getPortraitWidth());
            sa.setBottomMargin(cfd.getPortraitBottomMargin());
            sa.setTopMargin(cfd.getPortraitTopMargin());
            sa.setRightMargin(cfd.getPortraitRightMargin());
            sa.setLeftMargin(cfd.getPortraitLeftMargin());
            sa.setName(cfd.getCustomFormName());

            SizeAttributes lsa = new SizeAttributes();
            lsa.setOrientation(SizeAttributes.LANDSCAPE);
            lsa.setHeight(cfd.getLandscapeHeight());
            lsa.setWidth(cfd.getLandscapeWidth());
            lsa.setBottomMargin(cfd.getLandscapeBottomMargin());
            lsa.setTopMargin(cfd.getLandscapeTopMargin());
            lsa.setRightMargin(cfd.getLandscapeRightMargin());
            lsa.setLeftMargin(cfd.getLandscapeLeftMargin());
            lsa.setName(cfd.getCustomFormName());

            sizeAttributesPair[(SizeAttributes.PORTRAIT - 1)] = sa;
            sizeAttributesPair[(SizeAttributes.LANDSCAPE - 1)] = lsa;

            List<SizeAttributes[]> ssa = PreferencesManager.getFormSizeAttributes();
            ssa.add(sizeAttributesPair);

            try {
                PreferencesManager.savePreferences(Main.getXstream());
            } catch (IOException e1) {
                Misc.showErrorMsg(this, Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                    Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                return;
            }

            Misc.showSuccessMsg(this,
                Localizer.localize("Util", "EditorPreferencesSavedSuccessfullyMessage"));

            rebuildFormList();
            restoreDefaultFormPageSizeList();

        }

        cfd.dispose();
    }

    private void editFormSizeButtonActionPerformed(ActionEvent e) {
        // pick the form from the list by the id.
        int selectedIndex = formSizesList.getSelectedIndex();
        if (selectedIndex != -1) {

            List<SizeAttributes[]> ssa = PreferencesManager.getFormSizeAttributes();
            SizeAttributes[] saPair = ssa.get(selectedIndex);
            SizeAttributes sa = saPair[(SizeAttributes.PORTRAIT - 1)];
            SizeAttributes lsa = saPair[(SizeAttributes.LANDSCAPE - 1)];

            CustomFormDialog cfd = new CustomFormDialog(preferencesDialog);
            cfd.setTitle(
                Localizer.localize("Util", "EditorPreferencesEditCustomFormSizeDialogTitle"));

            cfd.setPortraitWidth(sa.getWidth());
            cfd.setPortraitHeight(sa.getHeight());
            cfd.setCustomFormName(sa.getName());
            cfd.setPortraitBottomMargin(sa.getBottomMargin());
            cfd.setPortraitTopMargin(sa.getTopMargin());
            cfd.setPortraitLeftMargin(sa.getLeftMargin());
            cfd.setPortraitRightMargin(sa.getRightMargin());

            cfd.setLandscapeWidth(lsa.getWidth());
            cfd.setLandscapeHeight(lsa.getHeight());
            cfd.setLandscapeBottomMargin(lsa.getBottomMargin());
            cfd.setLandscapeTopMargin(lsa.getTopMargin());
            cfd.setLandscapeLeftMargin(lsa.getLeftMargin());
            cfd.setLandscapeRightMargin(lsa.getRightMargin());

            cfd.setModal(true);
            cfd.setVisible(true);

            if (cfd.getDialogResult() == JOptionPane.OK_OPTION) {

                sa.setName(cfd.getCustomFormName());
                sa.setWidth(cfd.getPortraitWidth());
                sa.setHeight(cfd.getPortraitHeight());
                sa.setBottomMargin(cfd.getPortraitBottomMargin());
                sa.setTopMargin(cfd.getPortraitTopMargin());
                sa.setLeftMargin(cfd.getPortraitLeftMargin());
                sa.setRightMargin(cfd.getPortraitRightMargin());

                lsa.setName(cfd.getCustomFormName());
                lsa.setWidth(cfd.getLandscapeHeight());
                lsa.setHeight(cfd.getLandscapeWidth());
                lsa.setBottomMargin(cfd.getLandscapeBottomMargin());
                lsa.setTopMargin(cfd.getLandscapeTopMargin());
                lsa.setLeftMargin(cfd.getLandscapeLeftMargin());
                lsa.setRightMargin(cfd.getLandscapeRightMargin());

                try {
                    PreferencesManager.savePreferences(Main.getXstream());
                } catch (IOException e1) {
                    Misc.showErrorMsg(this,
                        Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                        Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                    return;
                }

                Misc.showSuccessMsg(this,
                    Localizer.localize("Util", "EditorPreferencesSavedSuccessfullyMessage"));

                rebuildFormList();
                restoreDefaultFormPageSizeList();

            }

            cfd.dispose();

        }
    }

    private void removeFormSizeButtonActionPerformed(ActionEvent e) {
        int selectedIndex = formSizesList.getSelectedIndex();
        if (selectedIndex != -1) {

            String message = String
                .format(Localizer.localize("Util", "EditorPreferencesConfirmRemoveMessage"),
                    formSizesList.getSelectedValue() + "");
            int n = JOptionPane.showConfirmDialog(null, message,
                Localizer.localize("Util", "EditorPreferencesConfirmRemoveFormTitle"),
                JOptionPane.YES_NO_OPTION);

            if (n != 0) {
                return;
            }

            List<SizeAttributes[]> ssa = PreferencesManager.getFormSizeAttributes();
            ssa.remove(selectedIndex);

            try {
                PreferencesManager.savePreferences(Main.getXstream());
            } catch (IOException e1) {
                Misc.showErrorMsg(this, Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                    Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
                return;
            }

            rebuildFormList();
            restoreDefaultFormPageSizeList();

        }
    }

    private void restoreDefaultFormSizesButtonActionPerformed(ActionEvent e) {
        String message =
            Localizer.localize("Util", "EditorPreferencesConfirmRestoreDefaultFormSizesMessage");
        int n = JOptionPane.showConfirmDialog(null, message,
            Localizer.localize("Util", "EditorPreferencesConfirmRestoreDefaultFormSizesTitle"),
            JOptionPane.YES_NO_OPTION);

        if (n != 0) {
            return;
        }

        PreferencesManager.resetFormSizeAttributes();

        try {
            PreferencesManager.savePreferences(Main.getXstream());
        } catch (IOException e1) {
            Misc.showErrorMsg(this, Localizer.localize("Util", "ErrorSavingPreferencesMessage"),
                Localizer.localize("Util", "ErrorSavingPreferencesTitle"));
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
            return;
        }

        Misc.showSuccessMsg(this,
            Localizer.localize("Util", "EditorPreferencesRestoreDefaultFormSizesSuccessMessage"));

        rebuildFormList();
        restoreDefaultFormPageSizeList();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        newDocumentDefaultsHeadingLabel = new JLabel();
        panel5 = new JPanel();
        panel3 = new JPanel();
        segmentPageSizeLabel = new JLabel();
        defaultSegmentPageSizeComboBox = new JComboBox();
        segmentBarcodeSizeLabel = new JLabel();
        segmentBarcodeScaleSpinner = new JSpinner();
        formPageSizeLabel = new JLabel();
        defaultFormPageSizeComboBox = new JComboBox();
        formOrientationLabel = new JLabel();
        defaultFormOrientationComboBox = new JComboBox();
        panel10 = new JPanel();
        panel11 = new JPanel();
        restoreDefaultSizesButton = new JButton();
        saveDefaultSizesButton = new JButton();
        panel2 = new JPanel();
        customizedSegmentSizesHeadingLabel = new JLabel();
        panel6 = new JPanel();
        scrollPane1 = new JScrollPane();
        segmentSizesList = new JList();
        panel4 = new JPanel();
        restoreDefaultSegmentSizesButton = new JButton();
        addNewSegmentSizeButton = new JButton();
        editSegmentSizeButton = new JButton();
        removeSegmentSizeButton = new JButton();
        panel7 = new JPanel();
        customizedFormSizesHeadingLabel = new JLabel();
        panel8 = new JPanel();
        scrollPane2 = new JScrollPane();
        formSizesList = new JList();
        panel9 = new JPanel();
        restoreDefaultFormSizesButton = new JButton();
        addNewFormSizeButton = new JButton();
        editFormSizeButton = new JButton();
        removeFormSizeButton = new JButton();

        //======== this ========
        setBorder(new EmptyBorder(10, 15, 10, 15));
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {35, 0, 35, 35, 0, 35, 35, 0, 30, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights =
            new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 0.0, 1.0E-4};

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- newDocumentDefaultsHeadingLabel ----
            newDocumentDefaultsHeadingLabel.setFont(UIManager.getFont("Label.font"));
            newDocumentDefaultsHeadingLabel.setText(
                Localizer.localize("Util", "EditorPreferencesNewDocumentDefaultsHeadingLabel"));
            panel1.add(newDocumentDefaultsHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel5 ========
            {
                panel5.setOpaque(false);
                panel5.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                panel5.setLayout(new BorderLayout());
            }
            panel1.add(panel5,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel3 ========
        {
            panel3.setOpaque(false);
            panel3.setLayout(new GridBagLayout());
            ((GridBagLayout) panel3.getLayout()).columnWidths =
                new int[] {105, 145, 15, 105, 140, 0};
            ((GridBagLayout) panel3.getLayout()).rowHeights = new int[] {35, 30, 0};
            ((GridBagLayout) panel3.getLayout()).columnWeights =
                new double[] {0.0, 1.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel3.getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0E-4};

            //---- segmentPageSizeLabel ----
            segmentPageSizeLabel.setFont(UIManager.getFont("Label.font"));
            segmentPageSizeLabel
                .setText(Localizer.localize("Util", "EditorPreferencesSegmentPageSizeLabel"));
            panel3.add(segmentPageSizeLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

            //---- defaultSegmentPageSizeComboBox ----
            defaultSegmentPageSizeComboBox.setFont(UIManager.getFont("ComboBox.font"));
            defaultSegmentPageSizeComboBox.setMaximumSize(new Dimension(140, 32767));
            defaultSegmentPageSizeComboBox.setPrototypeDisplayValue("xxxxxxxxxxxx");
            panel3.add(defaultSegmentPageSizeComboBox,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

            //---- segmentBarcodeSizeLabel ----
            segmentBarcodeSizeLabel.setFont(UIManager.getFont("Label.font"));
            segmentBarcodeSizeLabel
                .setText(Localizer.localize("Util", "EditorPreferencesSegmentBarcodeSizeLabel"));
            panel3.add(segmentBarcodeSizeLabel,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 5, 5), 0, 0));

            //---- segmentBarcodeScaleSpinner ----
            segmentBarcodeScaleSpinner.setModel(new SpinnerNumberModel(0.6, 0.1, 5.0, 0.1));
            segmentBarcodeScaleSpinner.setFont(UIManager.getFont("Spinner.font"));
            panel3.add(segmentBarcodeScaleSpinner,
                new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- formPageSizeLabel ----
            formPageSizeLabel.setFont(UIManager.getFont("Label.font"));
            formPageSizeLabel
                .setText(Localizer.localize("Util", "EditorPreferencesFormPageSizeLabel"));
            panel3.add(formPageSizeLabel,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

            //---- defaultFormPageSizeComboBox ----
            defaultFormPageSizeComboBox.setFont(UIManager.getFont("ComboBox.font"));
            defaultFormPageSizeComboBox.setMaximumSize(new Dimension(140, 32767));
            defaultFormPageSizeComboBox.setPrototypeDisplayValue("xxxxxxxxxxxx");
            panel3.add(defaultFormPageSizeComboBox,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- formOrientationLabel ----
            formOrientationLabel.setFont(UIManager.getFont("Label.font"));
            formOrientationLabel
                .setText(Localizer.localize("Util", "EditorPreferencesFormOrientationLabel"));
            panel3.add(formOrientationLabel,
                new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

            //---- defaultFormOrientationComboBox ----
            defaultFormOrientationComboBox
                .setModel(new DefaultComboBoxModel(new String[] {"Portrait", "Landscape"}));
            defaultFormOrientationComboBox.setFont(UIManager.getFont("ComboBox.font"));
            panel3.add(defaultFormOrientationComboBox,
                new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel10 ========
        {
            panel10.setOpaque(false);
            panel10.setLayout(new GridBagLayout());
            ((GridBagLayout) panel10.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel10.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) panel10.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel10.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //======== panel11 ========
            {
                panel11.setOpaque(false);
                panel11.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Color.lightGray),
                    new EmptyBorder(5, 0, 0, 0)));
                panel11.setLayout(new GridBagLayout());
                ((GridBagLayout) panel11.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
                ((GridBagLayout) panel11.getLayout()).rowHeights = new int[] {30, 0};
                ((GridBagLayout) panel11.getLayout()).columnWeights =
                    new double[] {0.0, 1.0, 0.0, 1.0E-4};
                ((GridBagLayout) panel11.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

                //---- restoreDefaultSizesButton ----
                restoreDefaultSizesButton.setFocusPainted(false);
                restoreDefaultSizesButton.setFont(UIManager.getFont("Button.font"));
                restoreDefaultSizesButton.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
                restoreDefaultSizesButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        restoreDefaultSizesButtonActionPerformed(e);
                    }
                });
                restoreDefaultSizesButton.setText(
                    Localizer.localize("Util", "EditorPreferencesRestoreDefaultSizesButtonText"));
                panel11.add(restoreDefaultSizesButton,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- saveDefaultSizesButton ----
                saveDefaultSizesButton.setFocusPainted(false);
                saveDefaultSizesButton.setFont(UIManager.getFont("Button.font"));
                saveDefaultSizesButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                saveDefaultSizesButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        saveDefaultSizesButtonActionPerformed(e);
                    }
                });
                saveDefaultSizesButton.setText(Localizer
                    .localize("Util", "EditorPreferencesSaveDefaultSizeSettingsButtonText"));
                panel11.add(saveDefaultSizesButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
            }
            panel10.add(panel11,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel10, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel2 ========
        {
            panel2.setOpaque(false);
            panel2.setLayout(new GridBagLayout());
            ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) panel2.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- customizedSegmentSizesHeadingLabel ----
            customizedSegmentSizesHeadingLabel.setFont(UIManager.getFont("Label.font"));
            customizedSegmentSizesHeadingLabel.setText(
                Localizer.localize("Util", "EditorPreferencesCustomizedSegmentSizesHeadingLabel"));
            panel2.add(customizedSegmentSizesHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel6 ========
            {
                panel6.setOpaque(false);
                panel6.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                panel6.setLayout(new BorderLayout());
            }
            panel2.add(panel6,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel2, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

        //======== scrollPane1 ========
        {

            //---- segmentSizesList ----
            segmentSizesList.setFont(UIManager.getFont("List.font"));
            scrollPane1.setViewportView(segmentSizesList);
        }
        add(scrollPane1, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel4 ========
        {
            panel4.setOpaque(false);
            panel4.setLayout(new GridBagLayout());
            ((GridBagLayout) panel4.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel4.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel4.getLayout()).columnWeights =
                new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel4.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- restoreDefaultSegmentSizesButton ----
            restoreDefaultSegmentSizesButton.setFocusPainted(false);
            restoreDefaultSegmentSizesButton.setFont(UIManager.getFont("Button.font"));
            restoreDefaultSegmentSizesButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
            restoreDefaultSegmentSizesButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    restoreDefaultSegmentSizesButtonActionPerformed(e);
                }
            });
            restoreDefaultSegmentSizesButton.setText(Localizer
                .localize("Util", "EditorPreferencesRestoreDefaultSegmentSizesButtonText"));
            panel4.add(restoreDefaultSegmentSizesButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- addNewSegmentSizeButton ----
            addNewSegmentSizeButton.setFocusPainted(false);
            addNewSegmentSizeButton.setFont(UIManager.getFont("Button.font"));
            addNewSegmentSizeButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/add.png")));
            addNewSegmentSizeButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    addNewSegmentSizeButtonActionPerformed(e);
                }
            });
            addNewSegmentSizeButton.setText(
                Localizer.localize("Util", "EditorPreferencesAddNewSegmentSizeButtonText"));
            panel4.add(addNewSegmentSizeButton,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- editSegmentSizeButton ----
            editSegmentSizeButton.setFocusPainted(false);
            editSegmentSizeButton.setFont(UIManager.getFont("Button.font"));
            editSegmentSizeButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/pencil.png")));
            editSegmentSizeButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    editSegmentSizeButtonActionPerformed(e);
                }
            });
            editSegmentSizeButton
                .setText(Localizer.localize("Util", "EditorPreferencesEditSegmentSizeButtonText"));
            panel4.add(editSegmentSizeButton,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- removeSegmentSizeButton ----
            removeSegmentSizeButton.setFocusPainted(false);
            removeSegmentSizeButton.setFont(UIManager.getFont("Button.font"));
            removeSegmentSizeButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/delete.png")));
            removeSegmentSizeButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    removeSegmentSizeButtonActionPerformed(e);
                }
            });
            removeSegmentSizeButton.setText(
                Localizer.localize("Util", "EditorPreferencesRemoveSegmentSizeButtonText"));
            panel4.add(removeSegmentSizeButton,
                new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel4, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel7 ========
        {
            panel7.setOpaque(false);
            panel7.setLayout(new GridBagLayout());
            ((GridBagLayout) panel7.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) panel7.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel7.getLayout()).columnWeights = new double[] {0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel7.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- customizedFormSizesHeadingLabel ----
            customizedFormSizesHeadingLabel.setFont(UIManager.getFont("Label.font"));
            customizedFormSizesHeadingLabel.setText(
                Localizer.localize("Util", "EditorPreferencesCustomizedFormSizesHeadingLabel"));
            panel7.add(customizedFormSizesHeadingLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //======== panel8 ========
            {
                panel8.setOpaque(false);
                panel8.setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                panel8.setLayout(new BorderLayout());
            }
            panel7.add(panel8,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel7, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== scrollPane2 ========
        {

            //---- formSizesList ----
            formSizesList.setFont(UIManager.getFont("List.font"));
            scrollPane2.setViewportView(formSizesList);
        }
        add(scrollPane2, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

        //======== panel9 ========
        {
            panel9.setOpaque(false);
            panel9.setLayout(new GridBagLayout());
            ((GridBagLayout) panel9.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel9.getLayout()).rowHeights = new int[] {30, 0};
            ((GridBagLayout) panel9.getLayout()).columnWeights =
                new double[] {0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel9.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- restoreDefaultFormSizesButton ----
            restoreDefaultFormSizesButton.setFocusPainted(false);
            restoreDefaultFormSizesButton.setFont(UIManager.getFont("Button.font"));
            restoreDefaultFormSizesButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/arrow_rotate_anticlockwise.png")));
            restoreDefaultFormSizesButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    restoreDefaultFormSizesButtonActionPerformed(e);
                }
            });
            restoreDefaultFormSizesButton.setText(
                Localizer.localize("Util", "EditorPreferencesRestoreDefaultFormSizesButtonText"));
            panel9.add(restoreDefaultFormSizesButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- addNewFormSizeButton ----
            addNewFormSizeButton.setFocusPainted(false);
            addNewFormSizeButton.setFont(UIManager.getFont("Button.font"));
            addNewFormSizeButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/add.png")));
            addNewFormSizeButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    addNewFormSizeButtonActionPerformed(e);
                }
            });
            addNewFormSizeButton
                .setText(Localizer.localize("Util", "EditorPreferencesAddNewFormSizeButtonText"));
            panel9.add(addNewFormSizeButton,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- editFormSizeButton ----
            editFormSizeButton.setFocusPainted(false);
            editFormSizeButton.setFont(UIManager.getFont("Button.font"));
            editFormSizeButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/pencil.png")));
            editFormSizeButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    editFormSizeButtonActionPerformed(e);
                }
            });
            editFormSizeButton
                .setText(Localizer.localize("Util", "EditorPreferencesEditFormSizeButtonText"));
            panel9.add(editFormSizeButton,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- removeFormSizeButton ----
            removeFormSizeButton.setFocusPainted(false);
            removeFormSizeButton.setFont(UIManager.getFont("Button.font"));
            removeFormSizeButton.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/delete.png")));
            removeFormSizeButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    removeFormSizeButtonActionPerformed(e);
                }
            });
            removeFormSizeButton
                .setText(Localizer.localize("Util", "EditorPreferencesRemoveFormSizeButtonText"));
            panel9.add(removeFormSizeButton,
                new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel9, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel newDocumentDefaultsHeadingLabel;
    private JPanel panel5;
    private JPanel panel3;
    private JLabel segmentPageSizeLabel;
    private JComboBox defaultSegmentPageSizeComboBox;
    private JLabel segmentBarcodeSizeLabel;
    private JSpinner segmentBarcodeScaleSpinner;
    private JLabel formPageSizeLabel;
    private JComboBox defaultFormPageSizeComboBox;
    private JLabel formOrientationLabel;
    private JComboBox defaultFormOrientationComboBox;
    private JPanel panel10;
    private JPanel panel11;
    private JButton restoreDefaultSizesButton;
    private JButton saveDefaultSizesButton;
    private JPanel panel2;
    private JLabel customizedSegmentSizesHeadingLabel;
    private JPanel panel6;
    private JScrollPane scrollPane1;
    private JList segmentSizesList;
    private JPanel panel4;
    private JButton restoreDefaultSegmentSizesButton;
    private JButton addNewSegmentSizeButton;
    private JButton editSegmentSizeButton;
    private JButton removeSegmentSizeButton;
    private JPanel panel7;
    private JLabel customizedFormSizesHeadingLabel;
    private JPanel panel8;
    private JScrollPane scrollPane2;
    private JList formSizesList;
    private JPanel panel9;
    private JButton restoreDefaultFormSizesButton;
    private JButton addNewFormSizeButton;
    private JButton editFormSizeButton;
    private JButton removeFormSizeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

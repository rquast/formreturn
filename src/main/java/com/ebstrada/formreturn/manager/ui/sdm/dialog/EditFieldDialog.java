package com.ebstrada.formreturn.manager.ui.sdm.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.jpa.SourceFieldController;
import com.ebstrada.formreturn.manager.persistence.jpa.SourceField;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.sdm.SourceDataManagerFrame;
import com.ebstrada.formreturn.manager.ui.sdm.component.*;
import com.ebstrada.formreturn.manager.util.Misc;

public class EditFieldDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private SourceDataManagerFrame sourceDataManagerFrame;
    private long sourceFieldId;

    public EditFieldDialog(Frame owner, SourceDataManagerFrame sourceDataManagerFrame) {
        super(owner);
        initComponents();
        this.sourceDataManagerFrame = sourceDataManagerFrame;
        this.sourceFieldId = sourceDataManagerFrame.getSelectedSourceFieldIds()[0];
        restoreName();
        restoreHiddenFieldsList();
        getRootPane().setDefaultButton(saveButton);
    }

    public void restoreHiddenFieldsList() {
        this.hiddenFieldsPanel.restoreHiddenFieldsList();
    }

    private void restoreName() {
        SourceFieldController sfc = new SourceFieldController();
        SourceField sf = sfc.getSourceFieldById(sourceFieldId);
        if (sf != null) {
            sourceFieldNameTextField.setText(sf.getSourceFieldName());
        }
        sourceFieldOrderIndexSpinner.setValue((int) sf.getOrderIndex());
    }

    public EditFieldDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(saveButton);
    }

    private void saveButtonActionPerformed(ActionEvent e) {
        String newFieldName = sourceFieldNameTextField.getText().trim();
        if (Misc.validateFieldname(newFieldName)) {

            List<SourceField> sourceFields =
                sourceDataManagerFrame.getFieldDataModel().getSourceFields();
            if (sourceFields == null) {
                return;
            }
            Iterator<SourceField> sfi = sourceFields.iterator();

            boolean containsSameFieldName = false;

            while (sfi.hasNext()) {
                SourceField sf = (SourceField) sfi.next();
                if (sf.getSourceFieldName().equals(newFieldName)
                    && sf.getSourceFieldId() != sourceFieldId) {
                    containsSameFieldName = true;
                }
            }

            if (containsSameFieldName) {
                String message =
                    Localizer.localize("UI", "EditFieldDuplicateFieldNameMessage") + "\n";
                String caption = Localizer.localize("UI", "EditFieldDuplicateFieldNameTitle");
                javax.swing.JOptionPane.showConfirmDialog(this, message, caption,
                    javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.ERROR_MESSAGE);
            } else {

                EntityManager entityManager =
                    Main.getInstance().getJPAConfiguration().getEntityManager();

                if (entityManager == null) {
                    return;
                }

                try {
                    SourceField sourceField = entityManager.find(SourceField.class, sourceFieldId);
                    sourceField.setSourceFieldName(newFieldName);
                    int orderIndex = (Integer) sourceFieldOrderIndexSpinner.getValue();
                    sourceField.setOrderIndex(orderIndex);
                    entityManager.getTransaction().begin();
                    entityManager.flush();
                    entityManager.persist(sourceField);
                    entityManager.getTransaction().commit();
                } catch (Exception ex) {
                    if (entityManager.getTransaction().isActive()) {
                        try {
                            entityManager.getTransaction().rollback();
                        } catch (Exception rbex) {
                            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(rbex);
                        }
                    }
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                } finally {
                    entityManager.close();
                }

                sourceDataManagerFrame.restoreFields();
                dispose();

            }

        } else {
            String message = Localizer.localize("UI", "EditFieldInvalidFieldNameMessage1") + "\n";
            message += Localizer.localize("UI", "EditFieldInvalidFieldNameMessage2");
            String caption = Localizer.localize("UI", "EditFieldInvalidFieldNameTitle");
            javax.swing.JOptionPane
                .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.ERROR_MESSAGE);

        }
    }

    private void closeButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                sourceFieldNameTextField.requestFocusInWindow();
            }
        });
    }

    private void tabbedPaneStateChanged(ChangeEvent e) {
        if (tabbedPane.getSelectedIndex() == 1) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    hiddenFieldsPanel.setFieldName(sourceFieldNameTextField.getText());
                }
            });
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        tabbedPane = new JTabbedPane();
        fieldNamePanel = new JPanel();
        sourceDataFieldNamePanel = new JPanel();
        fieldNameLabel = new JLabel();
        sourceFieldNameTextField = new JTextField();
        fieldNameOrderIndexPanel = new JPanel();
        orderIndexLabel = new JLabel();
        sourceFieldOrderIndexSpinner = new JSpinner();
        fieldNameButtonPanel = new JPanel();
        saveButton = new JButton();
        hiddenFieldsPanel = new HiddenFieldsPanel();
        buttonBar = new JPanel();
        closeButton = new JButton();

        //======== this ========
        setModal(true);
        setResizable(false);
        setAlwaysOnTop(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "EditFieldDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {200, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== tabbedPane ========
                {
                    tabbedPane.setFont(UIManager.getFont("TabbedPane.font"));
                    tabbedPane.addChangeListener(new ChangeListener() {
                        @Override public void stateChanged(ChangeEvent e) {
                            tabbedPaneStateChanged(e);
                        }
                    });

                    //======== fieldNamePanel ========
                    {
                        fieldNamePanel.setOpaque(false);
                        fieldNamePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
                        fieldNamePanel.setLayout(new GridBagLayout());
                        ((GridBagLayout) fieldNamePanel.getLayout()).columnWidths =
                            new int[] {0, 0};
                        ((GridBagLayout) fieldNamePanel.getLayout()).rowHeights =
                            new int[] {0, 0, 0, 0};
                        ((GridBagLayout) fieldNamePanel.getLayout()).columnWeights =
                            new double[] {1.0, 1.0E-4};
                        ((GridBagLayout) fieldNamePanel.getLayout()).rowWeights =
                            new double[] {1.0, 1.0, 0.0, 1.0E-4};

                        //======== sourceDataFieldNamePanel ========
                        {
                            sourceDataFieldNamePanel.setBorder(
                                new CompoundBorder(new TitledBorder("Source Data Field Name"),
                                    new EmptyBorder(5, 5, 5, 5)));
                            sourceDataFieldNamePanel.setOpaque(false);
                            sourceDataFieldNamePanel.setLayout(new GridBagLayout());
                            ((GridBagLayout) sourceDataFieldNamePanel.getLayout()).columnWidths =
                                new int[] {0, 0, 0};
                            ((GridBagLayout) sourceDataFieldNamePanel.getLayout()).rowHeights =
                                new int[] {0, 0};
                            ((GridBagLayout) sourceDataFieldNamePanel.getLayout()).columnWeights =
                                new double[] {0.0, 1.0, 1.0E-4};
                            ((GridBagLayout) sourceDataFieldNamePanel.getLayout()).rowWeights =
                                new double[] {1.0, 1.0E-4};
                            sourceDataFieldNamePanel.setBorder(new CompoundBorder(new TitledBorder(
                                Localizer.localize("UI", "SourceDataFieldNameBorderTitle")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //---- fieldNameLabel ----
                            fieldNameLabel.setFont(UIManager.getFont("Label.font"));
                            fieldNameLabel
                                .setText(Localizer.localize("UI", "EditFieldFieldNameLabel"));
                            sourceDataFieldNamePanel.add(fieldNameLabel,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- sourceFieldNameTextField ----
                            sourceFieldNameTextField.setFont(UIManager.getFont("TextField.font"));
                            sourceDataFieldNamePanel.add(sourceFieldNameTextField,
                                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        fieldNamePanel.add(sourceDataFieldNamePanel,
                            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                        //======== fieldNameOrderIndexPanel ========
                        {
                            fieldNameOrderIndexPanel.setBorder(
                                new CompoundBorder(new TitledBorder("Field Name Order Index"),
                                    new EmptyBorder(5, 5, 5, 5)));
                            fieldNameOrderIndexPanel.setOpaque(false);
                            fieldNameOrderIndexPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout) fieldNameOrderIndexPanel.getLayout()).columnWidths =
                                new int[] {0, 0, 0};
                            ((GridBagLayout) fieldNameOrderIndexPanel.getLayout()).rowHeights =
                                new int[] {0, 0};
                            ((GridBagLayout) fieldNameOrderIndexPanel.getLayout()).columnWeights =
                                new double[] {0.0, 0.0, 1.0E-4};
                            ((GridBagLayout) fieldNameOrderIndexPanel.getLayout()).rowWeights =
                                new double[] {1.0, 1.0E-4};
                            fieldNameOrderIndexPanel.setBorder(new CompoundBorder(new TitledBorder(
                                Localizer.localize("UI", "FieldNameOrderIndexBorderTitle")),
                                new EmptyBorder(5, 5, 5, 5)));

                            //---- orderIndexLabel ----
                            orderIndexLabel.setFont(UIManager.getFont("Label.font"));
                            orderIndexLabel
                                .setText(Localizer.localize("UI", "EditFieldOrderIndexLabel"));
                            fieldNameOrderIndexPanel.add(orderIndexLabel,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 5), 0, 0));

                            //---- sourceFieldOrderIndexSpinner ----
                            sourceFieldOrderIndexSpinner
                                .setModel(new SpinnerNumberModel(0, -100000, 100000, 1));
                            sourceFieldOrderIndexSpinner.setFont(UIManager.getFont("Spinner.font"));
                            fieldNameOrderIndexPanel.add(sourceFieldOrderIndexSpinner,
                                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        fieldNamePanel.add(fieldNameOrderIndexPanel,
                            new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                        //======== fieldNameButtonPanel ========
                        {
                            fieldNameButtonPanel.setOpaque(false);
                            fieldNameButtonPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout) fieldNameButtonPanel.getLayout()).columnWidths =
                                new int[] {0, 80, 0};
                            ((GridBagLayout) fieldNameButtonPanel.getLayout()).rowHeights =
                                new int[] {0, 0};
                            ((GridBagLayout) fieldNameButtonPanel.getLayout()).columnWeights =
                                new double[] {1.0, 0.0, 1.0E-4};
                            ((GridBagLayout) fieldNameButtonPanel.getLayout()).rowWeights =
                                new double[] {0.0, 1.0E-4};

                            //---- saveButton ----
                            saveButton.setFont(UIManager.getFont("Button.font"));
                            saveButton.setIcon(new ImageIcon(getClass().getResource(
                                "/com/ebstrada/formreturn/manager/ui/icons/disk.png")));
                            saveButton.addActionListener(new ActionListener() {
                                @Override public void actionPerformed(ActionEvent e) {
                                    saveButtonActionPerformed(e);
                                }
                            });
                            saveButton.setText(Localizer.localize("UI", "SaveButtonText"));
                            fieldNameButtonPanel.add(saveButton,
                                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        fieldNamePanel.add(fieldNameButtonPanel,
                            new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                    }
                    tabbedPane.addTab("Field Name", fieldNamePanel);

                    //---- hiddenFieldsPanel ----
                    hiddenFieldsPanel.setOpaque(false);
                    tabbedPane.addTab("Hidden Fields", hiddenFieldsPanel);
                }
                contentPanel.add(tabbedPane,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

                //---- closeButton ----
                closeButton.setFont(UIManager.getFont("Button.font"));
                closeButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                closeButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        closeButtonActionPerformed(e);
                    }
                });
                closeButton.setText(Localizer.localize("UI", "CloseButtonText"));
                buttonBar.add(closeButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(525, 390);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JTabbedPane tabbedPane;
    private JPanel fieldNamePanel;
    private JPanel sourceDataFieldNamePanel;
    private JLabel fieldNameLabel;
    private JTextField sourceFieldNameTextField;
    private JPanel fieldNameOrderIndexPanel;
    private JLabel orderIndexLabel;
    private JSpinner sourceFieldOrderIndexSpinner;
    private JPanel fieldNameButtonPanel;
    private JButton saveButton;
    private HiddenFieldsPanel hiddenFieldsPanel;
    private JPanel buttonBar;
    private JButton closeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

package com.ebstrada.formreturn.manager.ui.reprocessor.dialog;

import java.awt.*;
import java.awt.event.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.jpa.FormPage;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.tree.RecordTree;
import com.ebstrada.formreturn.manager.ui.component.tree.node.FormPageNode;
import com.ebstrada.formreturn.manager.ui.component.tree.node.RecordNode;

import com.ebstrada.formreturn.manager.ui.reprocessor.frame.ReprocessorFrame;
import com.ebstrada.formreturn.manager.util.Misc;

public class FormPageSelectionDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private int dialogResult = JOptionPane.CANCEL_OPTION;

    private ReprocessorFrame reprocessorFrame;

    private long selectedFormPageId = -1;

    public static final int FORM_PAGE_ID_SEARCH_TYPE = 0;

    public static final int FORM_ID_SEARCH_TYPE = 1;

    public static final int PUBLICATION_ID_SEARCH_TYPE = 2;

    public FormPageSelectionDialog(Frame owner, ReprocessorFrame reprocessorFrame) {
        super(owner);
        this.reprocessorFrame = reprocessorFrame;
        initComponents();
        getRootPane().setDefaultButton(searchButton);
    }

    public FormPageSelectionDialog(Dialog owner) {
        super(owner);
        initComponents();
        getRootPane().setDefaultButton(searchButton);
    }

    public long getSelectedFormPageIdFromTree() {
        RecordNode recordNode = recordTree.getSelectedRecordNode();
        if (recordNode == null || !(recordNode instanceof FormPageNode)) {
            return -1;
        }
        return ((FormPageNode) recordNode).getRecordId();
    }

    public long getSelectedFormPageId() {
        return selectedFormPageId;
    }

    private void okButtonActionPerformed(ActionEvent e) {
        selectedFormPageId = getSelectedFormPageIdFromTree();
        setDialogResult(JOptionPane.OK_OPTION);
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        setDialogResult(JOptionPane.CANCEL_OPTION);
        dispose();
    }

    public int getDialogResult() {
        return dialogResult;
    }

    public void setDialogResult(int dialogResult) {
        this.dialogResult = dialogResult;
    }

    public ReprocessorFrame getReprocessorFrame() {
        return reprocessorFrame;
    }

    public void setReprocessorFrame(ReprocessorFrame reprocessorFrame) {
        this.reprocessorFrame = reprocessorFrame;
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                searchTextField.requestFocusInWindow();
            }
        });
    }

    private void searchFormPageId() {

        long formPageId = 0;
        long formPassword = 0;

        String barcodeValue = searchTextField.getText().trim();

        boolean isFormIDBarcode = false;

        Pattern p = Pattern.compile("\\d+-\\d+");
        try {
            Matcher m = p.matcher(barcodeValue);
            m.lookingAt();
            if (m.group().equals(barcodeValue)) {
                isFormIDBarcode = true;
            }
        } catch (Exception ex) {
        }

        if (isFormIDBarcode) {
            // form page id
            String[] parts = barcodeValue.split("-");
            if (parts.length == 2) {
                formPageId = Misc.parseLongString(parts[0].trim());
                formPassword = Misc.parseLongString(parts[1].trim());
            }
        } else {
            formPageId = Misc.parseLongString(barcodeValue);
        }

        if (formPageId > 0) {

            EntityManager entityManager =
                Main.getInstance().getJPAConfiguration().getEntityManager();

            if (entityManager == null) {
                return;
            }

            try {

                FormPage formPage = entityManager.find(FormPage.class, formPageId);

                if (formPage == null) {
                    Misc.showErrorMsg(Main.getInstance(), String.format(
                        Localizer.localize("UI", "ReprocessorFrameFormPageIDNotFoundMessage"),
                        formPageId + ""));
                    return;
                }

                if (formPassword > 0) {

                    if (!(formPage.getFormId().getFormPassword().equals(formPassword + ""))) {
                        Misc.showErrorMsg(Main.getInstance(), Localizer
                            .localize("UI", "ReprocessorFrameFormPagePasswordsDoNotMatchMessage"));
                    }

                }

                // when we have the form id, show a dialog saying that you'd like to choose that record. Show some details about the record.
                String message = "";
                if (formPage.getProcessedTime() != null) {
                    message = String.format(Localizer.localize("UI",
                        "ReprocessorFrameFormPageAlreadyProcessedConfirmReplaceMessage"),
                        formPageId + "");
                } else {
                    message = String.format(
                        Localizer.localize("UI", "ReprocessorFrameFormPageConfirmSelectionMessage"),
                        formPageId + "");
                }

                // show confirm to use this record or not..
                boolean confirmed = Misc.showConfirmDialog(Main.getInstance(),
                    Localizer.localize("UI", "ReprocessorFrameFormPageConfirmSelectionTitle"),
                    message, Localizer.localize("UI", "Yes"), Localizer.localize("UI", "No"));

                if (confirmed) {

                    // if confirm... close the dialog and use!
                    selectedFormPageId = formPageId;
                    setDialogResult(JOptionPane.OK_OPTION);
                    dispose();

                } // else do nothing

            } catch (Exception ex) {
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                return;
            } finally {
                if (entityManager.isOpen()) {
                    entityManager.close();
                }
            }

        }
    }

    public void searchFormId() {
        // TODO: search by the form id

        // filter forms displayed in the tree

    }

    public void searchPublicationId() {
        // TODO: search by the publication id

        // filter publications displayed in the tree

    }

    private void searchButtonActionPerformed(ActionEvent e) {

        // TODO: offer filtering options and a dropdown box for the search.
        // int selectedIndex = searchTypeComboBox.getSelectedIndex();
        int selectedIndex = FORM_PAGE_ID_SEARCH_TYPE;
        if (selectedIndex == -1) {
            return;
        }

        switch (selectedIndex) {
            case FORM_PAGE_ID_SEARCH_TYPE:
                searchFormPageId();
                break;
            case FORM_ID_SEARCH_TYPE:
                searchFormId();
                break;
            case PUBLICATION_ID_SEARCH_TYPE:
                searchPublicationId();
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        quickSearchPanel = new JPanel();
        formPageIDLabel = new JLabel();
        searchTextField = new JTextField();
        searchButton = new JButton();
        formPagesPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        recordTree = new RecordTree();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setModal(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
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
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights =
                    new double[] {0.0, 1.0, 1.0E-4};

                //======== quickSearchPanel ========
                {
                    quickSearchPanel.setOpaque(false);
                    quickSearchPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) quickSearchPanel.getLayout()).columnWidths =
                        new int[] {0, 0, 0, 0};
                    ((GridBagLayout) quickSearchPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) quickSearchPanel.getLayout()).columnWeights =
                        new double[] {0.0, 1.0, 0.0, 1.0E-4};
                    ((GridBagLayout) quickSearchPanel.getLayout()).rowWeights =
                        new double[] {0.0, 1.0E-4};
                    quickSearchPanel.setBorder(new CompoundBorder(new TitledBorder(
                        Localizer.localize("UI", "ReprocessorFrameQuickSearchPanelTitle")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //---- formPageIDLabel ----
                    formPageIDLabel.setFont(UIManager.getFont("Label.font"));
                    formPageIDLabel
                        .setText(Localizer.localize("UI", "ReprocessorFrameFormPageIDLabel"));
                    quickSearchPanel.add(formPageIDLabel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                    //---- searchTextField ----
                    searchTextField.setFont(UIManager.getFont("TextField.font"));
                    quickSearchPanel.add(searchTextField,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                    //---- searchButton ----
                    searchButton.setFont(UIManager.getFont("Button.font"));
                    searchButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            searchButtonActionPerformed(e);
                        }
                    });
                    searchButton
                        .setText(Localizer.localize("UI", "ReprocessorFrameSearchButtonText"));
                    quickSearchPanel.add(searchButton,
                        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(quickSearchPanel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //======== formPagesPanel ========
                {
                    formPagesPanel.setOpaque(false);
                    formPagesPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) formPagesPanel.getLayout()).columnWidths = new int[] {0, 0};
                    ((GridBagLayout) formPagesPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) formPagesPanel.getLayout()).columnWeights =
                        new double[] {1.0, 1.0E-4};
                    ((GridBagLayout) formPagesPanel.getLayout()).rowWeights =
                        new double[] {1.0, 1.0E-4};
                    formPagesPanel.setBorder(new CompoundBorder(new TitledBorder(
                        Localizer.localize("UI", "ReprocessorFrameFormPagesPanelTitle")),
                        new EmptyBorder(5, 5, 5, 5)));

                    //======== scrollPane1 ========
                    {

                        //---- recordTree ----
                        recordTree.setFont(UIManager.getFont("Tree.font"));
                        scrollPane1.setViewportView(recordTree);
                    }
                    formPagesPanel.add(scrollPane1,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(formPagesPanel,
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

                //---- okButton ----
                okButton.setFont(UIManager.getFont("Button.font"));
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                okButton.setText(Localizer.localize("UI", "OKButtonText"));
                buttonBar.add(okButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        cancelButtonActionPerformed(e);
                    }
                });
                cancelButton.setText(Localizer.localize("UI", "CancelButtonText"));
                buttonBar.add(cancelButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(710, 455);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel quickSearchPanel;
    private JLabel formPageIDLabel;
    private JTextField searchTextField;
    private JButton searchButton;
    private JPanel formPagesPanel;
    private JScrollPane scrollPane1;
    private RecordTree recordTree;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}

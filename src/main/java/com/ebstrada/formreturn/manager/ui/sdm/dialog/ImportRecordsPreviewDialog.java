package com.ebstrada.formreturn.manager.ui.sdm.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class ImportRecordsPreviewDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public ImportRecordsPreviewDialog(Frame owner, DefaultTableModel dtm) {
        super(owner);
        initComponents();
        previewRecordsTable.setModel(dtm);
        previewRecordsTable.getTableHeader().setReorderingAllowed(false);
        getRootPane().setDefaultButton(closeButton);
    }

    public ImportRecordsPreviewDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    private void closeButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                closeButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        scrollPane1 = new JScrollPane();
        previewRecordsTable = new JTable();
        previewLimitNoticeLabel = new JLabel();
        buttonBar = new JPanel();
        closeButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "ImportRecordsPreviewDialogTitle"));

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
                    new double[] {1.0, 0.0, 1.0E-4};

                //======== scrollPane1 ========
                {

                    //---- previewRecordsTable ----
                    previewRecordsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    previewRecordsTable.setRequestFocusEnabled(false);
                    previewRecordsTable.setFont(UIManager.getFont("Table.font"));
                    previewRecordsTable.getTableHeader()
                        .setFont(UIManager.getFont("TableHeader.font"));
                    scrollPane1.setViewportView(previewRecordsTable);
                }
                contentPanel.add(scrollPane1,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

                //---- previewLimitNoticeLabel ----
                previewLimitNoticeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
                previewLimitNoticeLabel.setFont(UIManager.getFont("Label.font"));
                previewLimitNoticeLabel
                    .setText(Localizer.localize("UI", "ImportRecordsPreviewLimitNoticeLabel"));
                contentPanel.add(previewLimitNoticeLabel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
        setSize(785, 485);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JScrollPane scrollPane1;
    private JTable previewRecordsTable;
    private JLabel previewLimitNoticeLabel;
    private JPanel buttonBar;
    private JButton closeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

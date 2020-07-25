package com.ebstrada.formreturn.manager.ui.cdm.dialog;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

@SuppressWarnings("serial") public class MutliFormPageImageUploadDialog extends JDialog {

    private int dialogResult = JOptionPane.CANCEL_OPTION;
    private int pageCount = 1;

    public MutliFormPageImageUploadDialog(Frame owner, int pageCount) {
        super(owner);
        initComponents();
        this.pageCount = pageCount;
        setModels();
        getRootPane().setDefaultButton(okButton);
    }

    public MutliFormPageImageUploadDialog(Dialog owner, int pageCount) {
        super(owner);
        initComponents();
        this.pageCount = pageCount;
        setModels();
        getRootPane().setDefaultButton(okButton);
    }

    private void setModels() {
        this.startPageSpinner.setModel(new SpinnerNumberModel(1, 0, pageCount, 1));
    }

    private void okButtonActionPerformed(ActionEvent e) {
        this.dialogResult = JOptionPane.OK_OPTION;
        dispose();
    }

    private void cancelButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                okButton.requestFocusInWindow();
            }
        });
    }

    public int getStartPage() {
        return (Integer) this.startPageSpinner.getValue();
    }

    public int getDialogResult() {
        return dialogResult;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        startPagePanel = new JPanel();
        startPageLabel = new JLabel();
        startPageSpinner = new JSpinner();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "MultiFormPageImageUploadDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== startPagePanel ========
                {
                    startPagePanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) startPagePanel.getLayout()).columnWidths =
                        new int[] {0, 90, 0};
                    ((GridBagLayout) startPagePanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) startPagePanel.getLayout()).columnWeights =
                        new double[] {0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) startPagePanel.getLayout()).rowWeights =
                        new double[] {1.0, 1.0E-4};

                    //---- startPageLabel ----
                    startPageLabel.setText("Start From Page:");
                    startPageLabel.setFont(UIManager.getFont("Label.font"));
                    startPageLabel.setText(Localizer.localize("UI", "StartPageLabelText"));
                    startPagePanel.add(startPageLabel,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                    //---- startPageSpinner ----
                    startPageSpinner.setFont(UIManager.getFont("Spinner.font"));
                    startPageSpinner.setModel(new SpinnerNumberModel(1, 0, 999, 1));
                    startPagePanel.add(startPageSpinner,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
                }
                contentPanel.add(startPagePanel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
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
                okButton.setText("OK");
                okButton.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/accept.png")));
                okButton.setFont(UIManager.getFont("Button.font"));
                okButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        okButtonActionPerformed(e);
                    }
                });
                okButton.setText(Localizer.localize("UI", "OKButtonText"));
                buttonBar.add(okButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText("Cancel");
                cancelButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
                cancelButton.setFont(UIManager.getFont("Button.font"));
                cancelButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
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
        setSize(370, 120);
        setLocationRelativeTo(getOwner());
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel startPagePanel;
    private JLabel startPageLabel;
    private JSpinner startPageSpinner;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}

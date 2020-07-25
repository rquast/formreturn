package com.ebstrada.formreturn.server.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

@SuppressWarnings("serial") public class NewTaskSelectionDialog extends JDialog {

    public static final int NONE_SELECTED = 0;

    public static final int FOLDER_MONITOR = 1;

    public static final int VACUUM = 2;

    public static final int CUSTOM = 3;

    public static final int EXPORT = 4;

    private int selection = NONE_SELECTED;

    public NewTaskSelectionDialog(Frame owner) {
        super(owner);
        getRootPane().setDefaultButton(folderMonitorTaskButton);
        initComponents();
    }

    public NewTaskSelectionDialog(Dialog owner) {
        super(owner);
        getRootPane().setDefaultButton(folderMonitorTaskButton);
        initComponents();
    }

    private void folderMonitorTaskButtonActionPerformed(ActionEvent e) {
        this.selection = FOLDER_MONITOR;
        dispose();
    }

    private void vacuumTaskButtonActionPerformed(ActionEvent e) {
        this.selection = VACUUM;
        dispose();
    }

    private void pluginTaskButtonActionPerformed(ActionEvent e) {
        this.selection = CUSTOM;
        dispose();
    }

    private void exportTaskButtonActionPerformed(ActionEvent e) {
        this.selection = EXPORT;
        dispose();
    }

    public int getSelection() {
        return this.selection;
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                exportTaskButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        taskSelectionPanel = new JPanel();
        newTaskInformationLabel = new JLabel();
        taskSelectionSubPanel = new JPanel();
        exportTaskButton = new JButton();
        exportTaskLabel = new JLabel();
        folderMonitorTaskButton = new JButton();
        folderMonitorTaskDescriptionLabel = new JLabel();
        vacuumTaskButton = new JButton();
        vacuumTaskDescriptionLabel = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== taskSelectionPanel ========
        {
            taskSelectionPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
            taskSelectionPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) taskSelectionPanel.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) taskSelectionPanel.getLayout()).rowHeights = new int[] {35, 0, 0};
            ((GridBagLayout) taskSelectionPanel.getLayout()).columnWeights =
                new double[] {1.0, 1.0E-4};
            ((GridBagLayout) taskSelectionPanel.getLayout()).rowWeights =
                new double[] {0.0, 1.0, 1.0E-4};

            //---- newTaskInformationLabel ----
            newTaskInformationLabel.setHorizontalAlignment(SwingConstants.LEFT);
            newTaskInformationLabel.setFont(UIManager.getFont("Label.font"));
            newTaskInformationLabel.setText(
                "<html><strong>" + Localizer.localize("Server", "newTaskInformationLabelText")
                    + ":</strong></html>");
            taskSelectionPanel.add(newTaskInformationLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //======== taskSelectionSubPanel ========
            {
                taskSelectionSubPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) taskSelectionSubPanel.getLayout()).columnWidths =
                    new int[] {0, 15, 0, 0};
                ((GridBagLayout) taskSelectionSubPanel.getLayout()).rowHeights =
                    new int[] {0, 0, 0, 0};
                ((GridBagLayout) taskSelectionSubPanel.getLayout()).columnWeights =
                    new double[] {0.0, 0.0, 1.0, 1.0E-4};
                ((GridBagLayout) taskSelectionSubPanel.getLayout()).rowWeights =
                    new double[] {1.0, 1.0, 1.0, 1.0E-4};

                //---- exportTaskButton ----
                exportTaskButton.setFont(UIManager.getFont("Button.font"));
                exportTaskButton.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/sdm/table_go.png")));
                exportTaskButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        exportTaskButtonActionPerformed(e);
                    }
                });
                exportTaskButton.setText(Localizer.localize("Server", "ExportTaskButtonText"));
                taskSelectionSubPanel.add(exportTaskButton,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

                //---- exportTaskLabel ----
                exportTaskLabel.setText(
                    "<html><p>An export task will export captured data or images from the FormReturn database.</p></html>");
                exportTaskLabel.setFont(UIManager.getFont("Label.font"));
                exportTaskLabel.setText(
                    "<html>" + Localizer.localize("Server", "ExportTaskLabelText") + "</html>");
                taskSelectionSubPanel.add(exportTaskLabel,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //---- folderMonitorTaskButton ----
                folderMonitorTaskButton.setText("Folder Monitor Task");
                folderMonitorTaskButton.setFont(UIManager.getFont("Button.font"));
                folderMonitorTaskButton.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/folder_magnify.png")));
                folderMonitorTaskButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        folderMonitorTaskButtonActionPerformed(e);
                    }
                });
                folderMonitorTaskButton
                    .setText(Localizer.localize("Server", "folderMonitorTaskButtonText"));
                taskSelectionSubPanel.add(folderMonitorTaskButton,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 5), 0, 0));

                //---- folderMonitorTaskDescriptionLabel ----
                folderMonitorTaskDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                folderMonitorTaskDescriptionLabel.setText(
                    "<html>" + Localizer.localize("Server", "folderMonitorTaskDescriptionLabelText")
                        + "</html>");
                taskSelectionSubPanel.add(folderMonitorTaskDescriptionLabel,
                    new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

                //---- vacuumTaskButton ----
                vacuumTaskButton.setText("Database Vacuum Task");
                vacuumTaskButton.setFont(UIManager.getFont("Button.font"));
                vacuumTaskButton.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/sdm/database_refresh.png")));
                vacuumTaskButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        vacuumTaskButtonActionPerformed(e);
                    }
                });
                vacuumTaskButton.setText(Localizer.localize("Server", "vacuumTaskButtonText"));
                taskSelectionSubPanel.add(vacuumTaskButton,
                    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                //---- vacuumTaskDescriptionLabel ----
                vacuumTaskDescriptionLabel.setFont(UIManager.getFont("Label.font"));
                vacuumTaskDescriptionLabel.setText(
                    "<html>" + Localizer.localize("Server", "vacuumTaskDescriptionLabelText")
                        + "</html>");
                taskSelectionSubPanel.add(vacuumTaskDescriptionLabel,
                    new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            }
            taskSelectionPanel.add(taskSelectionSubPanel,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(taskSelectionPanel, BorderLayout.CENTER);
        setSize(870, 305);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel taskSelectionPanel;
    private JLabel newTaskInformationLabel;
    private JPanel taskSelectionSubPanel;
    private JButton exportTaskButton;
    private JLabel exportTaskLabel;
    private JButton folderMonitorTaskButton;
    private JLabel folderMonitorTaskDescriptionLabel;
    private JButton vacuumTaskButton;
    private JLabel vacuumTaskDescriptionLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}

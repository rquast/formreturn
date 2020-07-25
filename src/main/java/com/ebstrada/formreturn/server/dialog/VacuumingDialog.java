package com.ebstrada.formreturn.server.dialog;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import org.jdesktop.swingx.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class VacuumingDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private boolean isShuttingDown = true;

    public VacuumingDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    public VacuumingDialog(Frame owner, boolean isShuttingDown) {
        super(owner);
        this.isShuttingDown = isShuttingDown;
        initComponents();
    }

    public boolean isShuttingDown() {
        return isShuttingDown;
    }

    public void setShuttingDown(boolean isShuttingDown) {
        this.isShuttingDown = isShuttingDown;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        serverShutdownInProgressLabel = new JLabel();
        vacuumingDatabaseLabel = new JLabel();
        busyLabel = new JXBusyLabel();

        //======== this ========
        setUndecorated(true);
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setBorder(new MatteBorder(1, 1, 1, 1, Color.black));
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 5, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};

            //---- serverShutdownInProgressLabel ----
            serverShutdownInProgressLabel.setFont(UIManager.getFont("Label.font"));
            if (isShuttingDown) {
                serverShutdownInProgressLabel
                    .setText(Localizer.localize("Server", "VacuumShutdownInProgressLabel"));
            }
            panel1.add(serverShutdownInProgressLabel,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

            //---- vacuumingDatabaseLabel ----
            vacuumingDatabaseLabel.setFont(UIManager.getFont("Label.font"));
            vacuumingDatabaseLabel
                .setText(Localizer.localize("Server", "VacuumVacuumingDatabaseLabel"));
            panel1.add(vacuumingDatabaseLabel,
                new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

            //---- busyLabel ----
            busyLabel.setBusy(true);
            busyLabel.setFont(UIManager.getFont("Label.font"));
            panel1.add(busyLabel,
                new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        setSize(305, 155);
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel serverShutdownInProgressLabel;
    private JLabel vacuumingDatabaseLabel;
    private JXBusyLabel busyLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

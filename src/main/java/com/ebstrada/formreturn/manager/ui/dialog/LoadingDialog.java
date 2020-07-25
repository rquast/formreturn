package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import org.jdesktop.swingx.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class LoadingDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public LoadingDialog(Frame owner) {
        super(owner);
        initComponents();
    }

    public LoadingDialog(Dialog owner) {
        super(owner);
        initComponents();
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel2 = new JPanel();
        messageLabel = new JLabel();
        busyLabel = new JXBusyLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        setBackground(null);
        setAlwaysOnTop(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel2 ========
        {
            panel2.setBackground(Color.white);
            panel2.setBorder(new MatteBorder(1, 1, 1, 1, Color.black));
            panel2.setLayout(new GridBagLayout());
            ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {60, 0};
            ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {0, 0, 60, 0, 0};
            ((GridBagLayout) panel2.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel2.getLayout()).rowWeights =
                new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};

            //---- messageLabel ----
            messageLabel.setFont(UIManager.getFont("Label.font"));
            messageLabel.setText(Localizer.localize("UI", "LoadingMessage"));
            panel2.add(messageLabel,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 0), 0, 0));

            //---- busyLabel ----
            busyLabel.setBusy(true);
            panel2.add(busyLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel2, BorderLayout.CENTER);
        setSize(265, 90);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel2;
    private JLabel messageLabel;
    private JXBusyLabel busyLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

package com.ebstrada.formreturn.server.dialog;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;

import org.jdesktop.swingx.*;

import com.ebstrada.formreturn.api.messaging.MessageNotification;
import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class BackupStatusDialog extends JDialog implements MessageNotification {

    private static final long serialVersionUID = 1L;

    private boolean interrupted = false;

    private Exception exception;

    public BackupStatusDialog(Component owner) {
        super((Frame) owner);
        initComponents();
        addAbortListener();
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public void addAbortListener() {

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setInterrupted(true);
            }
        };

        KeyStroke keystroke = Localizer.getShortcut("UI", "AbortCommandShortcut");
        getRootPane().registerKeyboardAction(actionListener, keystroke, JComponent.WHEN_FOCUSED);

        abortMessage.setText(Localizer.localize("UI", "AbortCommandMessage"));
        messageLabel.setText(Localizer.localize("UI", "PleaseWaitLabel"));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        messageLabel = new JLabel();
        busyLabel = new JXBusyLabel();
        abortMessage = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        setBackground(null);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== panel1 ========
        {
            panel1.setBackground(Color.white);
            panel1.setBorder(new MatteBorder(1, 1, 1, 1, Color.black));
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0, 1.0E-4};

            //---- messageLabel ----
            messageLabel.setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
            messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            messageLabel.setText("."); //$NON-NLS-1$
            panel1.add(messageLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

            //---- busyLabel ----
            busyLabel.setBusy(true);
            busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel1.add(busyLabel,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

            //---- abortMessage ----
            abortMessage.setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
            abortMessage.setHorizontalAlignment(SwingConstants.CENTER);
            abortMessage.setText("."); //$NON-NLS-1$
            panel1.add(abortMessage,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1, BorderLayout.CENTER);
        setSize(265, 135);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel messageLabel;
    private JXBusyLabel busyLabel;
    private JLabel abortMessage;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public boolean isInterrupted() {
        return interrupted;
    }

    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    @Override public Exception getException() {
        return this.exception;
    }

    @Override public void setException(Exception exception) {
        this.exception = exception;
    }

}

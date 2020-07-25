package com.ebstrada.formreturn.scanner.client;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;

@SuppressWarnings("serial") public class ScannerClientFrame extends JFrame {

    private ScannerPanel scannerPanel;

    public ScannerClientFrame(ScannerPanel scannerPanel) throws Exception {
        initComponents();
        this.scannerPanel = scannerPanel;
        Container contentPane = getContentPane();
        contentPane.add((Component) scannerPanel, BorderLayout.CENTER);
        scannerPanel.init();
    }

    private void thisWindowClosing(WindowEvent e) {
        if (scannerPanel.close() == false) {
            return;
        } else {
            this.dispose();
            Main.getInstance().getScanClientLauncher().clearScanClientFrame();
        }
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                scannerPanel.focusGained();
            }
        });
    }

    private void closeButtonActionPerformed(ActionEvent e) {
        if (scannerPanel.close() == false) {
            return;
        } else {
            this.dispose();
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        buttonPanel = new JPanel();
        closeButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/frscanner_32x32.png"))
            .getImage());
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== buttonPanel ========
        {
            buttonPanel.setBorder(new EmptyBorder(5, 12, 12, 12));
            buttonPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) buttonPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) buttonPanel.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) buttonPanel.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 1.0E-4};
            ((GridBagLayout) buttonPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- closeButton ----
            closeButton.setFont(UIManager.getFont("Button.font"));
            closeButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    closeButtonActionPerformed(e);
                }
            });
            closeButton.setText(Localizer.localize("UI", "CloseButtonText"));
            buttonPanel.add(closeButton,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        setSize(670, 550);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel buttonPanel;
    private JButton closeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

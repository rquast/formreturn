package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.net.URL;
import java.util.Locale;

import javax.swing.*;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.border.*;

import org.jdesktop.swingx.*;

public class SplashDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    public SplashDialog(Frame owner) {
        super(owner);
        initComponents();
        setLocalDialogImage();
        pack();
    }

    private void setLocalDialogImage() {

        String country = Locale.getDefault().getCountry().toUpperCase();
        String language = Locale.getDefault().getLanguage().toLowerCase();

        try {
            URL imageResource = getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/images/splashscreen_" + language + "_"
                    + country + ".png");
            ImageIcon imgIcon = new ImageIcon(imageResource);
            jLabel1.setIcon(imgIcon);
        } catch (Exception ex) {
            try {
                URL imageResource = getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/images/splashscreen_" + language + ".png");
                ImageIcon imgIcon = new ImageIcon(imageResource);
                jLabel1.setIcon(imgIcon);
            } catch (Exception ex2) {
                URL imageResource = getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/images/splashscreen.png");
                ImageIcon imgIcon = new ImageIcon(imageResource);
                jLabel1.setIcon(imgIcon);
            }
        }

    }

    public void updateLoadingStatus(final String label) {
        statusLabel.setText(label);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Roland Quast
        jPanel1 = new JPanel();
        jLabel1 = new JLabel();
        panel2 = new JPanel();
        panel1 = new JPanel();
        statusLabel = new JLabel();
        busyLabel = new JXBusyLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);
        setBackground(null);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== jPanel1 ========
        {
            jPanel1.setBorder(new LineBorder(Color.lightGray));
            jPanel1.setOpaque(false);

            jPanel1.setLayout(new BorderLayout());

            //---- jLabel1 ----
            jLabel1.setIcon(new ImageIcon(getClass().getResource("/com/ebstrada/formreturn/manager/ui/images/splashscreen.png")));
            jLabel1.setIconTextGap(0);
            jLabel1.setDoubleBuffered(true);
            jLabel1.setOpaque(true);
            jPanel1.add(jLabel1, BorderLayout.NORTH);

            //======== panel2 ========
            {
                panel2.setBorder(new EmptyBorder(0, 0, 10, 0));
                panel2.setLayout(new GridBagLayout());
                ((GridBagLayout)panel2.getLayout()).columnWidths = new int[] {0, 0, 60, 0};
                ((GridBagLayout)panel2.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout)panel2.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 1.0E-4};
                ((GridBagLayout)panel2.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //======== panel1 ========
                {
                    panel1.setBorder(null);
                    panel1.setBackground(null);
                    panel1.setLayout(new GridBagLayout());
                    ((GridBagLayout)panel1.getLayout()).columnWidths = new int[] {20, 10, 0, 0};
                    ((GridBagLayout)panel1.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout)panel1.getLayout()).columnWeights = new double[] {0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout)panel1.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};
                }
                panel2.add(panel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- statusLabel ----
                statusLabel.setHorizontalAlignment(SwingConstants.TRAILING);
                statusLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
                panel2.add(statusLabel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                    new Insets(0, 0, 0, 0), 0, 0));

                //---- busyLabel ----
                busyLabel.setBusy(true);
                panel2.add(busyLabel, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            jPanel1.add(panel2, BorderLayout.SOUTH);
        }
        contentPane.add(jPanel1, BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(null);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Roland Quast
    private JPanel jPanel1;
    private JLabel jLabel1;
    private JPanel panel2;
    private JPanel panel1;
    private JLabel statusLabel;
    private JXBusyLabel busyLabel;
    // JFormDesigner - End of variables declaration //GEN-END:variables
}

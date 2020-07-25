package com.ebstrada.formreturn.installer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

public class WelcomePanel extends JPanel {

  private static final long serialVersionUID = 1L;

  public WelcomePanel() {
    super();
    initComponents();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY
    // //GEN-BEGIN:initComponents
    label1 = new JLabel();
    panel1 = new JPanel();
    applicationNameLabel = new JLabel() {
      @Override
      public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g2);
        g2.dispose();
      }
    };
    label3 = new JLabel() {
      @Override
      public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g2);
        g2.dispose();
      }
    };
    textPane1 = new JTextPane();

    // ======== this ========
    setBorder(new EmptyBorder(0, 10, 0, 0));
    setLayout(new BorderLayout());

    // ---- label1 ----
    label1.setIcon(new ImageIcon(getClass().getResource(
        "/com/ebstrada/formreturn/installer/images/frmanager_package.png"))); //$NON-NLS-1$
    label1.setPreferredSize(new Dimension(256, 256));
    label1.setMinimumSize(new Dimension(256, 256));
    add(label1, BorderLayout.WEST);

    // ======== panel1 ========
    {
      panel1.setBorder(new EmptyBorder(20, 5, 0, 0));
      panel1.setLayout(new GridBagLayout());
      ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] { 0, 0 };
      ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] { 0, 0, 0, 0 };
      ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] { 1.0, 1.0E-4 };
      ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] { 0.0, 0.0, 0.0, 1.0E-4 };

      // ---- applicationNameLabel ----
      applicationNameLabel.setText("."); //$NON-NLS-1$
      applicationNameLabel.setFont(new Font("Lucida Grande", Font.BOLD, 20)); //$NON-NLS-1$
      applicationNameLabel.setText(Main.APPLICATION_NAME);
      panel1.add(applicationNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

      // ---- label3 ----
      label3.setText("Installation Wizard"); //$NON-NLS-1$
      label3.setFont(new Font("Lucida Grande", Font.BOLD, 13)); //$NON-NLS-1$
      panel1.add(label3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

      // ---- textPane1 ----
      textPane1.setMinimumSize(new Dimension(0, 30));
      textPane1.setPreferredSize(new Dimension(0, 220));
      textPane1
          .setText("This will install FormReturn on your computer.  It is recommended that you close all other applications before continuing.  Click Next to continue, or Cancel to exit Setup."); //$NON-NLS-1$
      textPane1.setBorder(null);
      textPane1.setBackground(null);
      textPane1.setEditable(false);
      textPane1.setDragEnabled(false);
      panel1.add(textPane1, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }
    add(panel1, BorderLayout.CENTER);
    // //GEN-END:initComponents
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY
  // //GEN-BEGIN:variables
  private JLabel label1;
  private JPanel panel1;
  private JLabel applicationNameLabel;
  private JLabel label3;
  private JTextPane textPane1;
  // JFormDesigner - End of variables declaration //GEN-END:variables
}

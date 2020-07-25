package com.ebstrada.formreturn.installer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class InstallerPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  public InstallerPanel() {
    super();
    initComponents();
    if (!(Main.WINDOWS)) {
      panel2.remove(shortcutCheckBox);
    }
  }

  public void setProgressText(String s) {
    progressDescription.setText(s);
  }

  public boolean getLaunchCheckBoxEnabled() {
    System.out.println(launchCheckBox.isSelected());
    return launchCheckBox.isSelected();
  }

  public JCheckBox getLaunchCheckBox() {
    return launchCheckBox;
  }

  public JCheckBox getShortcutCheckBox() {
    return shortcutCheckBox;
  }

  public void setProgressStatus(String s) {
    progressStatus.setText(s);
  }

  public void setProgressValue(int i) {
    progressInstalled.setValue(i);
  }

  public void setLaunchCheckBoxEnabled(boolean flag) {
    launchCheckBox.setEnabled(flag);
  }

  public void setShortcutCheckBoxEnabled(boolean flag) {
    shortcutCheckBox.setEnabled(flag);
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY
    // //GEN-BEGIN:initComponents
    panel1 = new JPanel();
    label1 = new JLabel() {
      /**
	     * 
	     */
      private static final long serialVersionUID = 1L;

      @Override
      public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        super.paint(g2);
        g2.dispose();
      }
    };
    label2 = new JLabel();
    panel2 = new JPanel();
    progressStatus = new JLabel();
    progressInstalled = new JProgressBar();
    progressDescription = new JLabel();
    vSpacer1 = new JPanel(null);
    launchCheckBox = new JCheckBox();
    shortcutCheckBox = new JCheckBox();

    // ======== this ========
    setLayout(new BorderLayout());

    // ======== panel1 ========
    {
      panel1.setBorder(new CompoundBorder(new MatteBorder(0, 0, 1, 0, Color.darkGray),
          new EmptyBorder(5, 10, 5, 10)));
      panel1.setBackground(Color.white);
      panel1.setLayout(new GridBagLayout());
      ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] { 0, 0, 0 };
      ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] { 0, 0 };
      ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] { 1.0, 0.0, 1.0E-4 };
      ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] { 0.0, 1.0E-4 };

      // ---- label1 ----
      label1.setText("Installation");
      label1.setFont(new Font("Lucida Grande", Font.BOLD, 13));
      panel1.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

      // ---- label2 ----
      label2.setIcon(new ImageIcon(getClass().getResource(
          "/com/ebstrada/formreturn/installer/images/frmanager_package_small.png")));
      panel1.add(label2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }
    add(panel1, BorderLayout.NORTH);

    // ======== panel2 ========
    {
      panel2.setBorder(new EmptyBorder(10, 30, 10, 30));
      panel2.setLayout(new GridBagLayout());
      ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] { 0, 0 };
      ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0 };
      ((GridBagLayout) panel2.getLayout()).columnWeights = new double[] { 1.0, 1.0E-4 };
      ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
          0.0, 1.0E-4 };

      // ---- progressStatus ----
      progressStatus.setText("Installing ...");
      panel2.add(progressStatus, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
      panel2.add(progressInstalled, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

      // ---- progressDescription ----
      progressDescription.setText("filename");
      panel2.add(progressDescription, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));
      panel2.add(vSpacer1, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

      // ---- launchCheckBox ----
      launchCheckBox.setText("Launch the application when finished");
      launchCheckBox.setEnabled(false);
      launchCheckBox.setSelected(true);
      panel2.add(launchCheckBox, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

      // ---- shortcutCheckBox ----
      shortcutCheckBox.setText("Create shortcut on the desktop");
      shortcutCheckBox.setSelected(true);
      shortcutCheckBox.setEnabled(false);
      panel2.add(shortcutCheckBox, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }
    add(panel2, BorderLayout.CENTER);
    // JFormDesigner - End of component initialization
    // //GEN-END:initComponents
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY
  // //GEN-BEGIN:variables
  private JPanel panel1;

  private JLabel label1;

  private JLabel label2;

  private JPanel panel2;

  private JLabel progressStatus;

  private JProgressBar progressInstalled;

  private JLabel progressDescription;

  private JPanel vSpacer1;

  private JCheckBox launchCheckBox;

  private JCheckBox shortcutCheckBox;
  // JFormDesigner - End of variables declaration //GEN-END:variables
}

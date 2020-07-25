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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class SetupOptionsPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  public SetupOptionsPanel() {
    super();
    initComponents();
  }

  public void setDefaultInstallationFolder() {
    if (Main.WINDOWS) {
      setDestinationFolder("C:\\Program Files\\FormReturn");
    } else if (Main.MAC_OS_X) {
      setDestinationFolder("/Applications/FormReturn");
    } else {
      // linux or unix?
      setDestinationFolder(System.getProperty("user.home") + "/FormReturn");
    }
  }

  public String getJarFileName() {
    Main mainInstance = Main.getInstance();
    return mainInstance.jarFileName;
  }

  public File getOutputDir() {
    Main mainInstance = Main.getInstance();
    return mainInstance.outputDir;
  }

  public String getDestinationFolder() {
    return destinationFolderTextField.getText();
  }

  public void setDestinationFolder(String folderName) {
    destinationFolderTextField.setText(folderName);
  }

  public boolean checkOutputDir() {
    Main mainInstance = Main.getInstance();

    if (mainInstance.outputDir == null) {
      // create dir
      mainInstance.outputDir = new File(getDestinationFolder());
      try {
        try {
          if (!(mainInstance.outputDir.exists())) {
            if (!mainInstance.outputDir.mkdirs()) {
              warningLabel.setText("Unable to create folder, please choose another.");
              return false;
            }
          }
        } catch (Exception ex) {
          warningLabel.setText("Unable to create folder, please choose another.");
          return false;
        }
      } catch (Exception ex) {
        System.out.println(ex.getMessage());
        ex.printStackTrace();
      }
    } else {
      if (!(mainInstance.outputDir.canWrite())) {
        mainInstance.outputDir = new File(getDestinationFolder());
        if (!(mainInstance.outputDir.canWrite())) {
          warningLabel.setText("Unable to create folder, please choose another.");
          return false;
        } else {
          warningLabel.setText("");
        }
      }
    }

    return true;
  }

  private void browseFolderButtonActionPerformed(ActionEvent e) {

    Main mainInstance = Main.getInstance();

    File currentArchive = new File(mainInstance.jarFileName);

    JFileChooser fc = new JFileChooser();
    fc.setCurrentDirectory(new File(System.getProperty("user.home")));
    fc.setDialogType(JFileChooser.OPEN_DIALOG);
    fc.setDialogTitle("Select destination directory for extracting " + currentArchive.getName());
    fc.setMultiSelectionEnabled(false);
    fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

    // only when user select valid dir, it can return approve_option
    if (fc.showDialog(this, "Select") != JFileChooser.APPROVE_OPTION) {
      return;
    }

    mainInstance.outputDir = fc.getSelectedFile();
    setDestinationFolder(fc.getSelectedFile().getPath());

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
    label3 = new JLabel();
    panel3 = new JPanel();
    destinationFolderTextField = new JTextField();
    browseFolderButton = new JButton();
    warningLabel = new JLabel();
    label6 = new JLabel();
    label7 = new JLabel();

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
      label1.setText("Select Application Folder");
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
      ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
      ((GridBagLayout) panel2.getLayout()).columnWeights = new double[] { 1.0, 1.0E-4 };
      ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0,
          1.0E-4 };

      // ---- label3 ----
      label3.setText("Please choose the folder for installation.");
      panel2.add(label3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

      // ======== panel3 ========
      {
        panel3.setLayout(new GridBagLayout());
        ((GridBagLayout) panel3.getLayout()).columnWidths = new int[] { 0, 0, 0 };
        ((GridBagLayout) panel3.getLayout()).rowHeights = new int[] { 0, 0 };
        ((GridBagLayout) panel3.getLayout()).columnWeights = new double[] { 1.0, 0.0, 1.0E-4 };
        ((GridBagLayout) panel3.getLayout()).rowWeights = new double[] { 0.0, 1.0E-4 };
        panel3
            .add(destinationFolderTextField, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5),
                0, 0));

        // ---- browseFolderButton ----
        browseFolderButton.setText("Browse ...");
        browseFolderButton.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
            browseFolderButtonActionPerformed(e);
          }
        });
        panel3
            .add(browseFolderButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0),
                0, 0));
      }
      panel2.add(panel3, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

      // ---- warningLabel ----
      warningLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
      warningLabel.setForeground(Color.red);
      panel2.add(warningLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

      // ---- label6 ----
      label6.setText("NOTE: If the specified folder(s) do not not exist they will be ");
      label6.setFont(new Font("Lucida Grande", Font.BOLD, 13));
      panel2.add(label6, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

      // ---- label7 ----
      label7.setText("automatically created.");
      label7.setFont(new Font("Lucida Grande", Font.BOLD, 13));
      panel2.add(label7, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
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

  private JLabel label3;

  private JPanel panel3;

  private JTextField destinationFolderTextField;

  private JButton browseFolderButton;

  public JLabel warningLabel;

  private JLabel label6;

  private JLabel label7;
  // JFormDesigner - End of variables declaration //GEN-END:variables
}

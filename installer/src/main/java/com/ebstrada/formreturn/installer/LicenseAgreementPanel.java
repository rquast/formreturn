package com.ebstrada.formreturn.installer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentListener;

import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

public class LicenseAgreementPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  public LicenseAgreementPanel() {
    super();
    initComponents();
    textArea1.setCaretPosition(0);
  }

  public void addScrollBarAdjustmentListener(AdjustmentListener l) {
    JScrollBar scrollBar = scrollPane1.getVerticalScrollBar();
    scrollBar.addAdjustmentListener(l);
  }

  public void addCheckBoxActionListener(ActionListener l) {
    agreeCheckBox.addActionListener(l);
  }

  public void removeScrollDownBlock() {
    agreeCheckBox.setEnabled(true);
    agreeCheckBox.updateUI();
  }

  public boolean isCheckBoxSelected() {
    return agreeCheckBox.isSelected();
  }

  private void initComponents() {
    // JFormDesigner - Component initialization - DO NOT MODIFY
    // //GEN-BEGIN:initComponents
    panel1 = new JPanel();
    label1 = new JLabel() {
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
    scrollPane1 = new JScrollPane();
    textArea1 = new JTextArea();
    label5 = new JLabel();
    label6 = new JLabel();
    agreeCheckBox = new JCheckBox();

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
      label1.setText("License Agreement"); //$NON-NLS-1$
      label1.setFont(new Font("Lucida Grande", Font.BOLD, 13)); //$NON-NLS-1$
      panel1.add(label1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

      // ---- label2 ----
      label2.setIcon(new ImageIcon(getClass().getResource(
          "/com/ebstrada/formreturn/installer/images/frmanager_package_small.png"))); //$NON-NLS-1$
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
      ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 0.0,
          1.0E-4 };

      // ---- label3 ----
      label3.setText("Please read the following License Agreement carefully."); //$NON-NLS-1$
      panel2.add(label3, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

      // ======== scrollPane1 ========
      {
        scrollPane1.setPreferredSize(new Dimension(244, 200));
        scrollPane1.setMinimumSize(new Dimension(244, 200));
        scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // ---- textArea1 ----
        textArea1
            .setText(
                "Copyright 2020 EB Strada Holdings Pty Ltd and Contributors\n" +
                "\n" +
                "Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the \"Software\"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:\n" +
                "\n" +
                "The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.\n" +
                "\n" +
                "THE SOFTWARE IS PROVIDED \"AS IS\", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE."
            );
        textArea1.setWrapStyleWord(true);
        textArea1.setEditable(false);
        textArea1.setColumns(20);
        textArea1.setLineWrap(true);
        scrollPane1.setViewportView(textArea1);
      }
      panel2.add(scrollPane1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

      // ---- label5 ----
      label5.setText("NOTE: To continue you must accept the agreement, scroll to the bottom"); //$NON-NLS-1$
      label5.setFont(new Font("Lucida Grande", Font.BOLD, 13)); //$NON-NLS-1$
      label5.setHorizontalAlignment(SwingConstants.CENTER);
      panel2.add(label5, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

      // ---- label6 ----
      label6.setText("of the agreement, then click \"I accept the agreement\" to continue."); //$NON-NLS-1$
      label6.setFont(new Font("Lucida Grande", Font.BOLD, 13)); //$NON-NLS-1$
      label6.setHorizontalAlignment(SwingConstants.CENTER);
      panel2.add(label6, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
          GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

      // ---- agreeCheckBox ----
      agreeCheckBox.setText("I accept the agreement"); //$NON-NLS-1$
      agreeCheckBox.setHorizontalAlignment(SwingConstants.RIGHT);
      agreeCheckBox.setEnabled(false);
      agreeCheckBox.setFocusPainted(false);
      panel2.add(agreeCheckBox, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
          GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }
    add(panel2, BorderLayout.CENTER);
    // //GEN-END:initComponents
  }

  // JFormDesigner - Variables declaration - DO NOT MODIFY
  // //GEN-BEGIN:variables
  private JPanel panel1;
  private JLabel label1;
  private JLabel label2;
  private JPanel panel2;
  private JLabel label3;
  private JScrollPane scrollPane1;
  private JTextArea textArea1;
  private JLabel label5;
  private JLabel label6;
  private JCheckBox agreeCheckBox;
  // JFormDesigner - End of variables declaration //GEN-END:variables
}

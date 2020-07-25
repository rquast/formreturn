package com.ebstrada.formreturn.manager.ui.panel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.GradientHeaderUI;


public class StandardDesktopToolbarPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public StandardDesktopToolbarPanel() {
        initComponents();
        scanFormsButton.setVisible(true);
        setUI(new GradientHeaderUI());
        setBorder(new MatteBorder(0, 0, 1, 0, Color.gray));
        if (!Main.LINUX) {
            scanFormsButton.setToolTipText(Localizer.localize("UI", "ScannerClickToolTip"));
        }
    }

    public void openFileButtonActionPerformed(ActionEvent e) {
        Main.getInstance().openItemActionPerformed(e);
    }

    public void newSegmentButtonActionPerformed(ActionEvent e) {
        Main.getInstance().createNewSegment(e);
    }

    public void newFormButtonActionPerformed(ActionEvent e) {
        Main.getInstance().createNewForm(e);
    }

    public void sourceDataButtonActionPerformed(ActionEvent e) {
        Main.getInstance().sourceDataManagerActionPerformed(e);
    }

    public void capturedDataButtonActionPerformed(ActionEvent e) {
        Main.getInstance().capturedDataManagerItemActionPerformed(e);
    }

    public void processingQueueButtonActionPerformed(ActionEvent e) {
        Main.getInstance().processingQueueManagerActionPerformed(e);
    }

    public void preferencesButtonActionPerformed(ActionEvent e) {
        Main.getInstance().preferencesItemActionPerformed(e);
    }

    private void scanFormsButtonActionPerformed(ActionEvent e) {
        Main.getInstance().scanFormsItemActionPerformed(e);
    }

    private void openFileButtonMouseEntered(MouseEvent e) {
        this.openFileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void openFileButtonMouseExited(MouseEvent e) {
        this.openFileButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void newSegmentButtonMouseEntered(MouseEvent e) {
        this.newSegmentButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void newSegmentButtonMouseExited(MouseEvent e) {
        this.newSegmentButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void newFormButtonMouseEntered(MouseEvent e) {
        this.newFormButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void newFormButtonMouseExited(MouseEvent e) {
        this.newFormButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void sourceDataButtonMouseEntered(MouseEvent e) {
        this.sourceDataButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void sourceDataButtonMouseExited(MouseEvent e) {
        this.sourceDataButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void processingQueueButtonMouseEntered(MouseEvent e) {
        this.processingQueueButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void processingQueueButtonMouseExited(MouseEvent e) {
        this.processingQueueButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void capturedDataButtonMouseEntered(MouseEvent e) {
        this.capturedDataButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void capturedDataButtonMouseExited(MouseEvent e) {
        this.capturedDataButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void preferencesButtonMouseEntered(MouseEvent e) {
        this.preferencesButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void preferencesButtonMouseExited(MouseEvent e) {
        this.preferencesButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void scanFormsButtonMouseEntered(MouseEvent e) {
        this.scanFormsButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void scanFormsButtonMouseExited(MouseEvent e) {
        this.scanFormsButton.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        leftPanel = new JPanel();
        openFileButton = new JButton();
        newSegmentButton = new JButton();
        newFormButton = new JButton();
        sourceDataButton = new JButton();
        processingQueueButton = new JButton();
        capturedDataButton = new JButton();
        preferencesButton = new JButton();
        scanFormsButton = new JButton();

        //======== this ========
        setPreferredSize(new Dimension(1024, 70));
        setMinimumSize(new Dimension(1024, 70));
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        //======== leftPanel ========
        {
            leftPanel.setOpaque(false);
            leftPanel.setBorder(new EmptyBorder(6, 0, 0, 0));
            leftPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) leftPanel.getLayout()).columnWidths =
                new int[] {20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 0};
            ((GridBagLayout) leftPanel.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) leftPanel.getLayout()).columnWeights =
                new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) leftPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            //---- openFileButton ----
            openFileButton.setBorder(new EmptyBorder(6, 8, 4, 8));
            openFileButton.setFont(UIManager.getFont("Button.font"));
            openFileButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/desktop/open_button.png")));
            openFileButton.setContentAreaFilled(false);
            openFileButton.setFocusable(false);
            openFileButton.setFocusPainted(false);
            openFileButton.setRolloverEnabled(false);
            openFileButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    openFileButtonActionPerformed(e);
                }
            });
            openFileButton.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    openFileButtonMouseEntered(e);
                }

                @Override public void mouseExited(MouseEvent e) {
                    openFileButtonMouseExited(e);
                }
            });
            openFileButton.setHorizontalTextPosition(SwingConstants.CENTER);
            openFileButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            openFileButton.setText(Localizer.localize("UI", "DesktopToolbarOpenFileButtonText"));
            leftPanel.add(openFileButton,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));

            //---- newSegmentButton ----
            newSegmentButton.setBorder(new EmptyBorder(6, 8, 4, 8));
            newSegmentButton.setFont(UIManager.getFont("Button.font"));
            newSegmentButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/desktop/segment_button.png")));
            newSegmentButton.setContentAreaFilled(false);
            newSegmentButton.setFocusable(false);
            newSegmentButton.setFocusPainted(false);
            newSegmentButton.setRolloverEnabled(false);
            newSegmentButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    newSegmentButtonActionPerformed(e);
                }
            });
            newSegmentButton.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    newSegmentButtonMouseEntered(e);
                }

                @Override public void mouseExited(MouseEvent e) {
                    newSegmentButtonMouseExited(e);
                }
            });
            newSegmentButton.setHorizontalTextPosition(SwingConstants.CENTER);
            newSegmentButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            newSegmentButton
                .setText(Localizer.localize("UI", "DesktopToolbarNewSegmentButtonText"));
            leftPanel.add(newSegmentButton,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));

            //---- newFormButton ----
            newFormButton.setBorder(new EmptyBorder(6, 8, 4, 8));
            newFormButton.setFont(UIManager.getFont("Button.font"));
            newFormButton.setIcon(new ImageIcon(getClass()
                .getResource("/com/ebstrada/formreturn/manager/ui/icons/desktop/form_button.png")));
            newFormButton.setContentAreaFilled(false);
            newFormButton.setFocusable(false);
            newFormButton.setFocusPainted(false);
            newFormButton.setRolloverEnabled(false);
            newFormButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    newFormButtonActionPerformed(e);
                }
            });
            newFormButton.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    newFormButtonMouseEntered(e);
                }

                @Override public void mouseExited(MouseEvent e) {
                    newFormButtonMouseExited(e);
                }
            });
            newFormButton.setHorizontalTextPosition(SwingConstants.CENTER);
            newFormButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            newFormButton.setText(Localizer.localize("UI", "DesktopToolbarNewFormButtonText"));
            leftPanel.add(newFormButton,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));

            //---- sourceDataButton ----
            sourceDataButton.setBorder(new EmptyBorder(6, 8, 4, 8));
            sourceDataButton.setFont(UIManager.getFont("Button.font"));
            sourceDataButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/desktop/source_data_button.png")));
            sourceDataButton.setContentAreaFilled(false);
            sourceDataButton.setFocusable(false);
            sourceDataButton.setFocusPainted(false);
            sourceDataButton.setRolloverEnabled(false);
            sourceDataButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    sourceDataButtonActionPerformed(e);
                }
            });
            sourceDataButton.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    sourceDataButtonMouseEntered(e);
                }

                @Override public void mouseExited(MouseEvent e) {
                    sourceDataButtonMouseExited(e);
                }
            });
            sourceDataButton.setHorizontalTextPosition(SwingConstants.CENTER);
            sourceDataButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            sourceDataButton
                .setText(Localizer.localize("UI", "DesktopToolbarSourceDataButtonText"));
            leftPanel.add(sourceDataButton,
                new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));

            //---- processingQueueButton ----
            processingQueueButton.setBorder(new EmptyBorder(6, 8, 4, 8));
            processingQueueButton.setFont(UIManager.getFont("Button.font"));
            processingQueueButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/desktop/processing_queue.png")));
            processingQueueButton.setContentAreaFilled(false);
            processingQueueButton.setFocusable(false);
            processingQueueButton.setFocusPainted(false);
            processingQueueButton.setRolloverEnabled(false);
            processingQueueButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    processingQueueButtonActionPerformed(e);
                }
            });
            processingQueueButton.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    processingQueueButtonMouseEntered(e);
                }

                @Override public void mouseExited(MouseEvent e) {
                    processingQueueButtonMouseExited(e);
                }
            });
            processingQueueButton.setHorizontalTextPosition(SwingConstants.CENTER);
            processingQueueButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            processingQueueButton
                .setText(Localizer.localize("UI", "DesktopToolbarProcessingQueueButtonText"));
            leftPanel.add(processingQueueButton,
                new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));

            //---- capturedDataButton ----
            capturedDataButton.setBorder(new EmptyBorder(6, 8, 4, 8));
            capturedDataButton.setFont(UIManager.getFont("Button.font"));
            capturedDataButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/desktop/captured_data_button.png")));
            capturedDataButton.setContentAreaFilled(false);
            capturedDataButton.setFocusable(false);
            capturedDataButton.setFocusPainted(false);
            capturedDataButton.setRolloverEnabled(false);
            capturedDataButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    capturedDataButtonActionPerformed(e);
                }
            });
            capturedDataButton.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    capturedDataButtonMouseEntered(e);
                }

                @Override public void mouseExited(MouseEvent e) {
                    capturedDataButtonMouseExited(e);
                }
            });
            capturedDataButton.setHorizontalTextPosition(SwingConstants.CENTER);
            capturedDataButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            capturedDataButton
                .setText(Localizer.localize("UI", "DesktopToolbarCapturedDataButtonText"));
            leftPanel.add(capturedDataButton,
                new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));

            //---- preferencesButton ----
            preferencesButton.setBorder(new EmptyBorder(6, 8, 4, 8));
            preferencesButton.setFont(UIManager.getFont("Button.font"));
            preferencesButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/desktop/preferences_button.png")));
            preferencesButton.setContentAreaFilled(false);
            preferencesButton.setFocusable(false);
            preferencesButton.setFocusPainted(false);
            preferencesButton.setRolloverEnabled(false);
            preferencesButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    preferencesButtonActionPerformed(e);
                }
            });
            preferencesButton.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    preferencesButtonMouseEntered(e);
                }

                @Override public void mouseExited(MouseEvent e) {
                    preferencesButtonMouseExited(e);
                }
            });
            preferencesButton.setHorizontalTextPosition(SwingConstants.CENTER);
            preferencesButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            preferencesButton
                .setText(Localizer.localize("UI", "DesktopToolbarPreferencesButtonText"));
            leftPanel.add(preferencesButton,
                new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));

            //---- scanFormsButton ----
            scanFormsButton.setBorder(new EmptyBorder(6, 8, 4, 8));
            scanFormsButton.setFont(UIManager.getFont("Button.font"));
            scanFormsButton.setIcon(new ImageIcon(getClass().getResource(
                "/com/ebstrada/formreturn/manager/ui/icons/desktop/scan_forms_button.png")));
            scanFormsButton.setContentAreaFilled(false);
            scanFormsButton.setFocusable(false);
            scanFormsButton.setFocusPainted(false);
            scanFormsButton.setRolloverEnabled(false);
            scanFormsButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    scanFormsButtonActionPerformed(e);
                }
            });
            scanFormsButton.addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) {
                    scanFormsButtonMouseEntered(e);
                }

                @Override public void mouseExited(MouseEvent e) {
                    scanFormsButtonMouseExited(e);
                }
            });
            scanFormsButton.setHorizontalTextPosition(SwingConstants.CENTER);
            scanFormsButton.setVerticalTextPosition(SwingConstants.BOTTOM);
            scanFormsButton.setText(Localizer.localize("UI", "DesktopToolbarScanFormsButtonText"));
            leftPanel.add(scanFormsButton,
                new GridBagConstraints(9, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 10), 0, 0));
        }
        add(leftPanel);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel leftPanel;
    private JButton openFileButton;
    private JButton newSegmentButton;
    private JButton newFormButton;
    private JButton sourceDataButton;
    private JButton processingQueueButton;
    private JButton capturedDataButton;
    private JButton preferencesButton;
    private JButton scanFormsButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

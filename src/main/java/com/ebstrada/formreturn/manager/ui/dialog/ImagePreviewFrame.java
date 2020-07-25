package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.component.ImagePreviewPanel;
import com.ebstrada.formreturn.manager.ui.component.ZoomSettings;

public class ImagePreviewFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private ImagePreviewPanel imagePreviewPanel;

    private ZoomSettings zoomSettings = new ZoomSettings();

    public ImagePreviewFrame(Frame owner, byte[] imageData, boolean detectionEnabled) {
        initComponents();
        imagePreviewPanel = new ImagePreviewPanel(imageData, detectionEnabled, zoomSettings);
        contentPanel.add(imagePreviewPanel, BorderLayout.CENTER);
        getRootPane().setDefaultButton(closeButton);
    }

    public ImagePreviewFrame(Frame owner, ImagePreviewPanel imagePreviewPanel) {
        initComponents();
        contentPanel.add(imagePreviewPanel, BorderLayout.CENTER);
        getRootPane().setDefaultButton(closeButton);
    }

    public ImagePreviewFrame(Dialog owner) {
        initComponents();
    }

    private void closeButtonActionPerformed(ActionEvent e) {
        dispose();
    }

    private void thisWindowGainedFocus(WindowEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                closeButton.requestFocusInWindow();
            }
        });
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        buttonBar = new JPanel();
        closeButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(getClass()
            .getResource("/com/ebstrada/formreturn/manager/ui/icons/frmanager_256x256.png"))
            .getImage());
        setName("imagePreviewFrame");
        setLocationByPlatform(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        setTitle(Localizer.localize("UI", "ImagePreviewDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BorderLayout());
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

                //---- closeButton ----
                closeButton.setFont(UIManager.getFont("Button.font"));
                closeButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        closeButtonActionPerformed(e);
                    }
                });
                closeButton.setText(Localizer.localize("UI", "CloseButtonText"));
                buttonBar.add(closeButton,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(800, 565);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel buttonBar;
    private JButton closeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

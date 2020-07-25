package com.ebstrada.formreturn.manager.ui.dialog;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.structure.OMRMatrix;
import com.ebstrada.formreturn.manager.ui.component.BlobExtractionDebugPanel;

public class BlobExtractionDebugDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private BlobExtractionDebugPanel binarizedImage;
    private BlobExtractionDebugPanel regionLabelFirstParse;
    private BlobExtractionDebugPanel bubbleDetection;

    private OMRMatrix omrMatrix;

    public BlobExtractionDebugDialog(Frame owner, OMRMatrix omrMatrix) {
        super(owner);
        initComponents();

        this.omrMatrix = omrMatrix;

        binarizedImage = new BlobExtractionDebugPanel(omrMatrix.getBlobExtraction(), null,
            BlobExtractionDebugPanel.BINARIZED_IMAGE);
        binarizedImagePanel.add(binarizedImage, BorderLayout.CENTER);

        regionLabelFirstParse = new BlobExtractionDebugPanel(omrMatrix.getBlobExtraction(), null,
            BlobExtractionDebugPanel.LABEL_FIRST_PARSE);
        regionLabelFirstParsePanel.add(regionLabelFirstParse, BorderLayout.CENTER);

        bubbleDetection = new BlobExtractionDebugPanel(omrMatrix.getBlobExtraction(), null,
            BlobExtractionDebugPanel.BUBBLE_DETECTION);
        bubbleDetectionPanel.add(bubbleDetection, BorderLayout.CENTER);

        setTabTitles();

        getRootPane().setDefaultButton(closeButton);

    }

    private void setTabTitles() {
        blobExtractionTabbedPane
            .setTitleAt(0, Localizer.localize("UI", "BlobExtractionDebugBinarizedImageTabTitle"));
        blobExtractionTabbedPane
            .setTitleAt(1, Localizer.localize("UI", "BlobExtractionDebugFirstParseTabTitle"));
        blobExtractionTabbedPane
            .setTitleAt(2, Localizer.localize("UI", "BlobExtractionDebugBubbleDetectionTabTitle"));
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
        blobExtractionTabbedPane = new JTabbedPane();
        binarizedImagePanel = new JPanel();
        regionLabelFirstParsePanel = new JPanel();
        bubbleDetectionPanel = new JPanel();
        buttonBar = new JPanel();
        closeButton = new JButton();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        addWindowFocusListener(new WindowAdapter() {
            @Override public void windowGainedFocus(WindowEvent e) {
                thisWindowGainedFocus(e);
            }
        });
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("UI", "BlobExtractionDebugDialogTitle"));

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== blobExtractionTabbedPane ========
            {
                blobExtractionTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));

                //======== binarizedImagePanel ========
                {
                    binarizedImagePanel.setOpaque(false);
                    binarizedImagePanel.setLayout(new BorderLayout());
                }
                blobExtractionTabbedPane.addTab("Binarized Image", binarizedImagePanel);

                //======== regionLabelFirstParsePanel ========
                {
                    regionLabelFirstParsePanel.setOpaque(false);
                    regionLabelFirstParsePanel.setLayout(new BorderLayout());
                }
                blobExtractionTabbedPane.addTab("Label First Parse", regionLabelFirstParsePanel);

                //======== bubbleDetectionPanel ========
                {
                    bubbleDetectionPanel.setOpaque(false);
                    bubbleDetectionPanel.setLayout(new BorderLayout());
                }
                blobExtractionTabbedPane.addTab("Bubble Detection", bubbleDetectionPanel);
            }
            dialogPane.add(blobExtractionTabbedPane, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {0, 80};
                ((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

                //---- closeButton ----
                closeButton.setFont(UIManager.getFont("Button.font"));
                closeButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
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
        setSize(820, 565);
        setLocationRelativeTo(null);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JPanel dialogPane;
    private JTabbedPane blobExtractionTabbedPane;
    private JPanel binarizedImagePanel;
    private JPanel regionLabelFirstParsePanel;
    private JPanel bubbleDetectionPanel;
    private JPanel buttonBar;
    private JButton closeButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

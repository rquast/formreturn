package com.ebstrada.formreturn.manager.ui.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.component.JStatusBar;

public class GraphRenderPanel extends JPanel implements Runnable {

    private static final long serialVersionUID = 1L;

    RecognitionPreviewPanel recognitionPreviewPanel;

    public GraphRenderPanel() {
        super();
    }

    public RecognitionPreviewPanel getRecognitionPreviewPanel() {
        return recognitionPreviewPanel;
    }

    public void updateView(final RecognitionPreviewPanel recognitionPreviewPanel,
        Dimension panelSize) {

        if (recognitionPreviewPanel != null) {
            removeAll();
        }

        this.recognitionPreviewPanel = recognitionPreviewPanel;
        recognitionPreviewPanel.setPreferredSize(panelSize);
        this.add(recognitionPreviewPanel, BorderLayout.CENTER);
        recognitionPreviewPanel.setFinishedLoading(true);
        recognitionPreviewPanel.updatePanels();
        JStatusBar.getInstance().setProgressValue(100);
        JStatusBar.getInstance()
            .setProgressLabelText(Localizer.localize("UI", "DoneStatusMessage"));
        Thread t = new Thread(this);
        t.start();

    }

    public void run() {
        try {
            Thread.sleep(500);
            JStatusBar.getInstance().setProgressValue(0);
        } catch (InterruptedException x) {
            Thread.currentThread().interrupt();
        }
    }

    public void showMessages() {
        recognitionPreviewPanel.showErrorMessages();
    }

}

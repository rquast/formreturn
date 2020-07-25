package com.ebstrada.formreturn.manager.ui.editor.panel;

import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTaskPaneContainer;

import com.ebstrada.formreturn.manager.ui.editor.RecognitionPreviewPanel;

public class RecognitionPanelController {

    public JXTaskPaneContainer propertyBoxPanel;
    private PreFlightCheckPanel preFlightCheckPanel;
    private RecognitionSettingsPanel recognitionSettingsPanel;

    public RecognitionPanelController(JXTaskPaneContainer _propertiesPanel,
        RecognitionPreviewPanel recognitionPreviewPanel) {
        propertyBoxPanel = _propertiesPanel;
        preFlightCheckPanel = new PreFlightCheckPanel(recognitionPreviewPanel);
        recognitionSettingsPanel = new RecognitionSettingsPanel(recognitionPreviewPanel);
    }

    public void setSelectedCheckBoxes(boolean formDetectBarcode, boolean formDetectSegment,
        boolean formDetectfragment, boolean formDetectMarks) {
        preFlightCheckPanel
            .setSelectedCheckBoxes(formDetectBarcode, formDetectSegment, formDetectfragment,
                formDetectMarks);
    }

    public void updateAllPanels() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                destroyPanels();
                propertyBoxPanel.add(preFlightCheckPanel);
                propertyBoxPanel.add(recognitionSettingsPanel);
            }
        });
    }

    public void destroyPanels() {
        propertyBoxPanel.removeAll();
        propertyBoxPanel.updateUI();
    }

}

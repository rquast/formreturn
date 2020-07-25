package com.ebstrada.formreturn.manager.ui.panel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import org.jdesktop.swingx.JXTaskPaneContainer;

import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegmentArea;
import com.ebstrada.formreturn.manager.gef.presentation.FigText;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.cdm.panel.CDMPanel;
import com.ebstrada.formreturn.manager.ui.editor.frame.FormFrame;
import com.ebstrada.formreturn.manager.ui.editor.frame.SegmentFrame;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorMultiPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.FigPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.FormPropertiesPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.PagePropertiesPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.PublicationsPropertiesPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.SegmentPropertiesPanel;
import com.ebstrada.formreturn.manager.ui.pqm.panel.PQMPanel;
import com.ebstrada.formreturn.manager.ui.reprocessor.panel.ReprocessorPanel;
import com.ebstrada.formreturn.manager.ui.sdm.panel.SDMPanel;
import com.ebstrada.formreturn.manager.ui.sdm.panel.TablePropertiesPanel;

public class PropertiesPanelController {

    public Stack<Fig> figStack = new Stack<Fig>();

    public Stack<Vector> multiFigStack = new Stack<Vector>();

    public Stack<PropertyChangeListener> propertyChangeListenerStack =
        new Stack<PropertyChangeListener>();

    public Stack<EditorPanel> editorPanelStack = new Stack<EditorPanel>();

    public Stack<EditorMultiPanel> editorMultiPanelStack = new Stack<EditorMultiPanel>();

    public Stack<SDMPanel> sourceDataManagerPanelStack = new Stack<SDMPanel>();

    public Stack<CDMPanel> capturedDataManagerPanelStack = new Stack<CDMPanel>();

    public Stack<ReprocessorPanel> reprocessorPanelStack = new Stack<ReprocessorPanel>();

    public JXTaskPaneContainer propertyBoxPanel;

    public PagePropertiesPanel pagePropertiesPanel;

    private SegmentPropertiesPanel segmentPropertiesPanel;

    private Stack<PQMPanel> processingQueueManagerPanelStack = new Stack<PQMPanel>();

    private FormPropertiesPanel formPropertiesPanel;

    public PropertiesPanelController(JXTaskPaneContainer _propertiesPanel) {
        propertyBoxPanel = _propertiesPanel;
    }

    public void updateAllPanels() {

        Iterator<EditorPanel> editorPanelStackIterator = editorPanelStack.iterator();
        while (editorPanelStackIterator.hasNext()) {
            EditorPanel editorPanel = (EditorPanel) editorPanelStackIterator.next();
            editorPanel.updatePanel();
        }

        for (EditorMultiPanel editorMultiPanel : editorMultiPanelStack) {
            editorMultiPanel.updatePanel();
        }

        Iterator<SDMPanel> sourceDataManagerPanelStackIterator =
            sourceDataManagerPanelStack.iterator();
        while (sourceDataManagerPanelStackIterator.hasNext()) {
            SDMPanel sourceDataManagerPanel = (SDMPanel) sourceDataManagerPanelStackIterator.next();
            sourceDataManagerPanel.updatePanel();
        }

        Iterator<CDMPanel> capturedDataManagerPanelStackIterator =
            capturedDataManagerPanelStack.iterator();
        while (capturedDataManagerPanelStackIterator.hasNext()) {
            CDMPanel capturedDataManagerPanel =
                (CDMPanel) capturedDataManagerPanelStackIterator.next();
            capturedDataManagerPanel.updatePanel();
        }

        Iterator<ReprocessorPanel> reprocessorPanelStackIterator = reprocessorPanelStack.iterator();
        while (reprocessorPanelStackIterator.hasNext()) {
            ReprocessorPanel reprocessorPanel =
                (ReprocessorPanel) reprocessorPanelStackIterator.next();
            reprocessorPanel.updatePanel();
        }

    }

    public void createMultiPanel(EditorMultiPanel _editorMultiPanel, Vector selectedFigs) {
        if (_editorMultiPanel == null) {
            return;
        }
        _editorMultiPanel.setSelectedElements(selectedFigs);
        editorMultiPanelStack.push(_editorMultiPanel);
        _editorMultiPanel.updatePanel();

        propertyBoxPanel.add(_editorMultiPanel);
        propertyBoxPanel.updateUI();
    }

    public void createPanel(EditorPanel _editorPanel, Fig fig) {
        _editorPanel.setSelectedElement(fig);
        editorPanelStack.push(_editorPanel);
        _editorPanel.updatePanel();

        propertyBoxPanel.add(_editorPanel);
        propertyBoxPanel.updateUI();
    }

    public void createPanel(SDMPanel _sourceDataManagerPanel) {
        sourceDataManagerPanelStack.push(_sourceDataManagerPanel);
        _sourceDataManagerPanel.updatePanel();

        propertyBoxPanel.add(_sourceDataManagerPanel);
        propertyBoxPanel.updateUI();
    }

    public void createPanel(CDMPanel _capturedDataManagerPanel) {
        capturedDataManagerPanelStack.push(_capturedDataManagerPanel);
        _capturedDataManagerPanel.updatePanel();

        propertyBoxPanel.add(_capturedDataManagerPanel);
        propertyBoxPanel.updateUI();
    }

    public void createPanel(PQMPanel _processingQueueManagerPanel) {
        processingQueueManagerPanelStack.push(_processingQueueManagerPanel);
        _processingQueueManagerPanel.updatePanel();

        propertyBoxPanel.add(_processingQueueManagerPanel);
        propertyBoxPanel.updateUI();
    }

    public void createPanel(ReprocessorPanel _reprocessorPanel) {
        reprocessorPanelStack.push(_reprocessorPanel);
        _reprocessorPanel.updatePanel();

        propertyBoxPanel.add(_reprocessorPanel);
        propertyBoxPanel.updateUI();
    }

    public void destroyPanels() {

        formPropertiesPanel = null;
        pagePropertiesPanel = null;
        segmentPropertiesPanel = null;

        propertyBoxPanel.removeAll();
        propertyBoxPanel.updateUI();

        while (!(figStack.empty())) {

            Fig fig = figStack.pop();
            PropertyChangeListener pcl = propertyChangeListenerStack.pop();

            fig.removePropertyChangeListener(pcl);

        }

        while (!(editorPanelStack.empty())) {
            EditorPanel _editorPanel = editorPanelStack.pop();
            _editorPanel.removeListeners();
        }

        if (!(editorMultiPanelStack.empty())) {
            editorMultiPanelStack = new Stack<EditorMultiPanel>();
            multiFigStack = new Stack<Vector>();
        }

        while (!(sourceDataManagerPanelStack.empty())) {
            SDMPanel _sourceDataManagerPanel = sourceDataManagerPanelStack.pop();
            _sourceDataManagerPanel.removeListeners();
        }

        while (!(capturedDataManagerPanelStack.empty())) {
            CDMPanel _capturedDataManagerPanel = capturedDataManagerPanelStack.pop();
            _capturedDataManagerPanel.removeListeners();
        }

        while (!(reprocessorPanelStack.empty())) {
            ReprocessorPanel _reprocessorPanel = reprocessorPanelStack.pop();
            _reprocessorPanel.removeListeners();
        }

    }

    public void showPublishPanel(FormFrame formFrame) {
        propertyBoxPanel.add(
            new com.ebstrada.formreturn.manager.ui.editor.panel.TablePropertiesPanel(formFrame));
        propertyBoxPanel.add(new PublicationsPropertiesPanel(formFrame));
    }

    public void showPagePanel(JGraph _graph, FormFrame formFrame) {
        pagePropertiesPanel = new PagePropertiesPanel(_graph, formFrame);
        propertyBoxPanel.add(pagePropertiesPanel);
    }

    public void showSegmentPanel(JGraph _graph, SegmentFrame segmentFrame) {
        segmentPropertiesPanel = new SegmentPropertiesPanel(_graph, segmentFrame);
        propertyBoxPanel.add(segmentPropertiesPanel);
    }

    public void initFigs(Vector selectedFigs) {

        if (!(multiFigStack.contains(selectedFigs))) {

            multiFigStack.push(selectedFigs);

            Fig fig = null;
            if (selectedFigs.size() > 0) {
                fig = (Fig) selectedFigs.get(0);
            }

            if (fig != null) {
                createMultiPanel(fig.getEditorMultiPanel(), selectedFigs);
            }

        }

    }

    public void initFig(Fig fig) {

        // if fig is not in the figStack...
        if (!(figStack.contains(fig))) {

            PropertyChangeListener pcl = new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent pce) {
                    updateAllPanels();
                }

            };

            // add fig and pcl to stack
            figStack.push(fig);
            fig.addPropertyChangeListener(pcl);
            propertyChangeListenerStack.push(pcl);

            // Draw Base Fig Panel
            FigPanel figPanel = new FigPanel();
            if (fig instanceof FigText) {
                figPanel.setCollapsed(true);
            }
            createPanel(figPanel, fig);

            // Draw extended panel
            createPanel(fig.getEditorPanel(), fig);

        }

    }

    public PagePropertiesPanel getPagePropertiesPanel() {
        return pagePropertiesPanel;
    }

    public SegmentPropertiesPanel getSegmentPropertiesPanel() {
        return segmentPropertiesPanel;
    }

    public void showFormPropertiesPanel(JGraph _graph, FormFrame formFrame) {
        formPropertiesPanel = new FormPropertiesPanel(formFrame);
        propertyBoxPanel.add(formPropertiesPanel);
    }

}

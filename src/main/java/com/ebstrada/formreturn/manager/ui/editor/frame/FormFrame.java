package com.ebstrada.formreturn.manager.ui.editor.frame;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicComboPopup;

import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.icon.EmptyIcon;
import org.jdesktop.swingx.painter.BusyPainter;

import com.ebstrada.formreturn.manager.gef.base.Editor;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.base.Layer;
import com.ebstrada.formreturn.manager.gef.base.ZoomAction;
import com.ebstrada.formreturn.manager.gef.event.GraphSelectionEvent;
import com.ebstrada.formreturn.manager.gef.event.GraphSelectionListener;
import com.ebstrada.formreturn.manager.gef.graph.GraphModel;
import com.ebstrada.formreturn.manager.gef.graph.presentation.DefaultGraphModel;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.ui.AlignmentPalette;
import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.ui.FormPalette;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.structure.PublicationRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.persistence.xstream.Page;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.GradientHeaderUI;
import com.ebstrada.formreturn.manager.ui.component.JStatusBar;
import com.ebstrada.formreturn.manager.ui.dialog.InsertNewPageDialog;
import com.ebstrada.formreturn.manager.ui.editor.GraphRenderPanel;
import com.ebstrada.formreturn.manager.ui.editor.PublishFormPanel;
import com.ebstrada.formreturn.manager.ui.editor.RecognitionPreviewPanel;
import com.ebstrada.formreturn.manager.ui.frame.EditorFrame;
import com.ebstrada.formreturn.manager.ui.panel.PropertiesPanelController;

public class FormFrame extends EditorFrame {

    private static final long serialVersionUID = 1L;

    private AlignmentPalette _alignmentToolBar;

    private ZoomAction zoomAction;

    private float realZoom = 1.0f;

    private BasicComboPopup pageSelectionPopup;

    private JComboBox pageSelectionList;

    private PublishFormPanel pfp;

    private boolean finishedLoading = false;

    public FormFrame(JGraph graph) {

        super();

        _graph = graph;

        initComponents();

        formFrameTabbedPane.setTitleAt(0, Localizer.localize("UI", "FormFrameFormEditorTabTitle"));
        formFrameTabbedPane
            .setTitleAt(1, Localizer.localize("UI", "FormFrameRecognitionPreviewTabTitle"));
        formFrameTabbedPane.setTitleAt(2, Localizer.localize("UI", "FormFramePublishFormTabTitle"));

        pfp = new PublishFormPanel(this);
        publishFormPanel.add(pfp);

        _graph.setTopMarginPanel(topMarginPanel);
        _graph.setBottomMarginPanel(bottomMarginPanel);
        _graph.setLeftMarginPanel(leftMarginPanel);
        _graph.setRightMarginPanel(rightMarginPanel);

        toolbarScrollpane.getViewport().setBackground(null);
        toolbarScrollpane.getViewport().setOpaque(false);
        toolbarScrollpane.getViewport().setBorder(null);
        toolbarScrollpane.setViewportBorder(null);

        _alignmentToolBar = new AlignmentPalette();
        _alignmentToolBar.setBorder(null);
        alignmentToolbarContainerPanel.add(_alignmentToolBar);

        GraphModel graphModel = null;

        if (_graph.getGraphModel() == null) {
            graphModel = new DefaultGraphModel();
            _graph.setGraphModel(graphModel);
        }
        zoomAction = new ZoomAction();

        restorePageAttributes();

        GraphSelectionListener gsl = new GraphSelectionListener() {

            public void selectionChanged(GraphSelectionEvent gse) {
                getPropertiesPanelController().destroyPanels();
                Vector sels = _graph.selectedFigs();
                updatePropertyBox(sels);
                unpressAllButtons();
            }

        };

        _graph.addGraphSelectionListener(gsl);

        // set the antialiasing on for the editor
        Editor ce = _graph.getEditor();
        ce.setAntiAlias(true);

        Vector sels = _graph.selectedFigs();
        updatePropertyBox(sels);
        unpressAllButtons();

        updatePageNumbers();

        printDesignViewScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        printDesignViewScrollPane.getVerticalScrollBar().setBlockIncrement(90);
        printDesignViewScrollPane.getHorizontalScrollBar().setUnitIncrement(30);
        printDesignViewScrollPane.getHorizontalScrollBar().setBlockIncrement(90);

    }

    public PropertiesPanelController getPropertiesPanelController() {
        return Main.getInstance().getPropertiesPanelController();
    }

    @Override public void updatePropertyBox(Vector sels) {

        if (!isFinishedLoading()) {
            return;
        }

        Iterator i = sels.iterator();

        // only change properties panel on single selections
        if (sels.size() == 1) {

            Object o = i.next();

            if (o instanceof Fig) {

                // clear the properties panel
                getPropertiesPanelController().destroyPanels();

                getPropertiesPanelController().showFormPropertiesPanel(_graph, this);

                Fig selectedFig = (Fig) o;

                getPropertiesPanelController().initFig(selectedFig);

            }

            // else clear properties panel and reset all selected object
            // listeners
        } else if (sels.size() > 1) {

            // itterate through all of the figs and check if they are all the same
            Iterator selectedFigsIterator = sels.iterator();
            Class clazz = selectedFigsIterator.next().getClass();

            boolean classIsDifferent = false;
            while (selectedFigsIterator.hasNext()) {
                Class clazz2 = selectedFigsIterator.next().getClass();
                if (clazz != clazz2) {
                    classIsDifferent = true;
                    break;
                }
            }

            if (!classIsDifferent) {
                getPropertiesPanelController().destroyPanels();
                getPropertiesPanelController().showFormPropertiesPanel(_graph, this);
                getPropertiesPanelController().initFigs(sels);
            } else {
                getPropertiesPanelController().destroyPanels();
                getPropertiesPanelController().showFormPropertiesPanel(_graph, this);
                getPropertiesPanelController().showPagePanel(_graph, this);
            }

            // else clear properties panel and reset all selected object
            // listeners
        } else {

            getPropertiesPanelController().destroyPanels();
            getPropertiesPanelController().showFormPropertiesPanel(_graph, this);
            getPropertiesPanelController().showPagePanel(_graph, this);

        }

    }

    public void setPageAttributes(PageAttributes pageAttributes) {
        _graph.setPageAttributes(pageAttributes);
    }

    public void restorePageAttributes(PageAttributes pageAttributes) {
        _graph.setPageAttributes(pageAttributes);
        restorePageAttributes();
    }

    public void restoreDocumentAttributes(DocumentAttributes documentAttributes) {
        _graph.setDocumentAttributes(documentAttributes);
    }

    public void restorePageAttributes() {

        int pageWidth = (new Double(getPageAttributes().getDimension().getWidth())).intValue();
        int pageHeight = (new Double(getPageAttributes().getDimension().getHeight())).intValue();
        int leftMargin = getPageAttributes().getLeftMargin();
        int rightMargin = getPageAttributes().getRightMargin();
        int topMargin = getPageAttributes().getTopMargin();
        int bottomMargin = getPageAttributes().getBottomMargin();

        int croppedHeight = pageHeight - (topMargin + bottomMargin);
        getPageAttributes().setCroppedHeight(croppedHeight);
        int croppedWidth = pageWidth - (leftMargin + rightMargin);
        getPageAttributes().setCroppedWidth(croppedWidth);

        if (getGraph().getDocumentPackage().getPackageFile() != null) {
            setTitle(getGraph().getDocumentPackage().getPackageFile().getName());
        } else {
            setTitle(getDocumentAttributes().getName());
        }

        _graph.setMinimumSize(new Dimension(croppedWidth, croppedHeight));
        _graph.setDrawingSize(new Dimension(croppedWidth, croppedHeight));
        _graph.setPreferredSize(new Dimension(croppedWidth, croppedHeight));

        _graph.updateUI();

        resizeMargins();

        restoreScale();

    }

    public void restoreScale() {

        Editor ed = Globals.curEditor();
        if (ed == null) {
            return;
        }

        if (_graph.getEditor().getScale() > 0.0) {
            ed.setScale(_graph.getEditor().getScale());
        } else {
            ed.setScale(1.0);
        }
        ed.damageAll();

    }

    public void resizeMargins() {
        float leftMargin = realZoom * getPageAttributes().getLeftMargin();
        float rightMargin = realZoom * getPageAttributes().getRightMargin();
        float topMargin = realZoom * getPageAttributes().getTopMargin();
        float bottomMargin = realZoom * getPageAttributes().getBottomMargin();
        _graph.getTopMarginPanel().setPreferredSize(new Dimension(1, (int) topMargin));
        _graph.getBottomMarginPanel().setPreferredSize(new Dimension(1, (int) bottomMargin));
        _graph.getLeftMarginPanel().setPreferredSize(new Dimension((int) leftMargin, 1));
        _graph.getRightMarginPanel().setPreferredSize(new Dimension((int) rightMargin, 1));
    }


    public Dimension getGraphDimension() {
        return _graph.getMinimumSize();
    }

    @Override public boolean closeEditorFrame() {
        Editor ce = _graph.getEditor();

        if (ce.hasEditorStateChanged() != false) {

            Main.getInstance().activateEditorFrame(this);

            String filename = getTitle();
            if (getGraph().getDocumentPackage().getPackageFile() != null) {
                filename = getGraph().getDocumentPackage().getPackageFile().getName();
            }

            String message = String
                .format(Localizer.localize("UI", "FormFrameFileModifiedSaveConfirmationMessage"),
                    filename);
            String caption = Localizer.localize("UI", "FormFrameFileModifiedSaveConfirmationTitle");

            int ret = javax.swing.JOptionPane
                .showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.YES_NO_CANCEL_OPTION);

            if (ret == javax.swing.JOptionPane.YES_OPTION) {
                Main mainInstance = Main.getInstance();
                if (!(mainInstance.save(this, false))) {
                    return false;
                }
            } else if (ret == javax.swing.JOptionPane.CANCEL_OPTION) {
                return false;
            }

        }

        try {
            getGraph().getDocumentPackage().close();
        } catch (Exception e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

        getPropertiesPanelController().destroyPanels();

        // do this or there will be a memory leak because of the property change listener on figs.
        removeAllFigs();
        Globals.curEditor(null);
        Globals.mode(null);
        System.gc();

        return true;

    }

    public void removeAllFigs() {
        Enumeration<Fig> figs = _graph.getEditor().figs();
        while (figs.hasMoreElements()) {
            _graph.getEditor().removePropertyChangeListener(figs.nextElement());
        }
        _graph.getEditor().getSelectionManager().deselectAll();
        _graph.getEditor().getLayerManager().removeAll();
        _graph.getEditor().damageAll();
    }

    private void editorZoomComboBoxActionPerformed(ActionEvent e) {
        String unparsedString = (String) editorZoomComboBox.getSelectedObjects()[0];
        String zoomString = "";
        for (int i = 0; i < unparsedString.length(); i++) {
            if (unparsedString.charAt(i) >= 48 && unparsedString.charAt(i) <= 57) {
                zoomString += unparsedString.charAt(i);
            }
        }
        float newZoom = Float.parseFloat(zoomString) / 100.0f;

        if (newZoom >= 0.1 && newZoom <= 10) {
            realZoom = newZoom;
            zoomAction = new ZoomAction(realZoom);
            zoomAction.actionPerformed(e);
        } else {
            editorZoomComboBox.setSelectedItem(((int) (realZoom * 100)) + "%");
        }

        resizeMargins();
        repaint();
    }

    private void topMarginPanelMouseClicked(MouseEvent e) {
        if (getPropertiesPanelController() != null) {
            getPropertiesPanelController().getPagePropertiesPanel().focusTopMarginSpinner();
        }
    }

    private void leftMarginPanelMouseClicked(MouseEvent e) {
        if (getPropertiesPanelController() != null) {
            getPropertiesPanelController().getPagePropertiesPanel().focusLeftMarginSpinner();
        }
    }

    private void rightMarginPanelMouseClicked(MouseEvent e) {
        if (getPropertiesPanelController() != null) {
            getPropertiesPanelController().getPagePropertiesPanel().focusRightMarginSpinner();
        }
    }

    private void bottomMarginPanelMouseClicked(MouseEvent e) {
        if (getPropertiesPanelController() != null) {
            getPropertiesPanelController().getPagePropertiesPanel().focusBottomMarginSpinner();
        }
    }

    private void previousPageButtonActionPerformed(ActionEvent e) {

        // 1. Save the current page back to the document container.
        Page currentPageContainer =
            getGraph().getDocument().getPageByPageNumber(getGraph().getCurrentPageNumber());
        currentPageContainer.setFigs(getGraph().getEditor().getLayerManager().getContents());

        // 2. Get the previous page number (if there is one)
        int currentPageNumber = getGraph().getCurrentPageNumber();
        int nextPageNumber = currentPageNumber - 1;
        if (nextPageNumber > 0) {

            // 3. Clear the graph
            getGraph().getEditor().getSelectionManager().deselectAll();
            getGraph().getEditor().getLayerManager().getActiveLayer().removeAll();
            getGraph().getEditor().damageAll();

            // 4. Get the new page container and set the new page number
            Page nextPageContainer = getGraph().getDocument().getPageByPageNumber(nextPageNumber);
            getGraph().setCurrentPageNumber(nextPageNumber);

            // 5. Load the figs from this page container to the active layer
            List figs = nextPageContainer.getFigs();
            if (figs != null) {
                Layer lay = getGraph().getEditor().getLayerManager().getActiveLayer();
                for (Iterator it = figs.iterator(); it.hasNext(); ) {
                    Fig fig = (Fig) it.next();
                    lay.add(fig);
                }
            }
            getGraph().getEditor().postLoad();

            // 6. Update the page numbers
            updatePageNumbers();

            // 7. Update graph boundaries
            getGraph().updateGraphBoundaries();
            if (getPropertiesPanelController() != null) {
                getPropertiesPanelController().getPagePropertiesPanel().restorePageAttributes();
            }
            getGraph().getEditor().setScale(realZoom);
            getGraph().getEditor().damageAll();
            resizeMargins();

        }

    }

    private void pageNumberLabelMouseClicked(MouseEvent e) {

        int numberOfPages = getGraph().getDocument().getNumberOfPages();
        String[] pageNumberStrings = new String[numberOfPages];
        for (int i = 0; i < numberOfPages; i++) {
            pageNumberStrings[i] = "" + (i + 1);
        }
        pageSelectionList = new JComboBox(pageNumberStrings);
        pageSelectionList.setSelectedItem("" + getGraph().getCurrentPageNumber());
        pageSelectionList.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                // 1. Save the current page back to the document container.
                Page currentPageContainer =
                    getGraph().getDocument().getPageByPageNumber(getGraph().getCurrentPageNumber());
                currentPageContainer
                    .setFigs(getGraph().getEditor().getLayerManager().getContents());

                int nextPageNumber = Integer.parseInt((String) pageSelectionList.getSelectedItem());
                pageSelectionPopup.hide();

                // 2. Clear the graph
                getGraph().getEditor().getSelectionManager().deselectAll();
                getGraph().getEditor().getLayerManager().getActiveLayer().removeAll();
                getGraph().getEditor().damageAll();

                // 3. Get the new page container and set the new page number
                Page nextPageContainer =
                    getGraph().getDocument().getPageByPageNumber(nextPageNumber);
                getGraph().setCurrentPageNumber(nextPageNumber);

                // 4. Load the figs from this page container to the active layer
                List figs = nextPageContainer.getFigs();
                if (figs != null) {
                    Layer lay = getGraph().getEditor().getLayerManager().getActiveLayer();
                    for (Iterator it = figs.iterator(); it.hasNext(); ) {
                        Fig fig = (Fig) it.next();
                        lay.add(fig);
                    }
                }
                getGraph().getEditor().postLoad();

                // 5. Update the page numbers
                updatePageNumbers();

                // 6. Update graph boundaries
                getGraph().updateGraphBoundaries();
                if (getPropertiesPanelController() != null) {
                    getPropertiesPanelController().getPagePropertiesPanel().restorePageAttributes();
                }
                getGraph().getEditor().setScale(realZoom);
                getGraph().getEditor().damageAll();
                resizeMargins();

            }
        });
        pageSelectionPopup = new BasicComboPopup(pageSelectionList);
        pageSelectionPopup.setPopupSize(50, 100);
        pageSelectionPopup.show(pageNumberLabel, e.getX(), e.getY());
    }

    private void nextPageButtonActionPerformed(ActionEvent e) {

        // 1. Save the current page back to the document container.
        Page currentPageContainer =
            getGraph().getDocument().getPageByPageNumber(getGraph().getCurrentPageNumber());
        currentPageContainer.setFigs(getGraph().getEditor().getLayerManager().getContents());

        // 2. Get the next page number (if there is one)
        int currentPageNumber = getGraph().getCurrentPageNumber();
        int totalNumberOfPages = getGraph().getDocument().getNumberOfPages();
        int nextPageNumber = currentPageNumber + 1;
        if (nextPageNumber <= totalNumberOfPages) {

            // 3. Clear the graph
            getGraph().getEditor().getSelectionManager().deselectAll();
            getGraph().getEditor().getLayerManager().getActiveLayer().removeAll();
            getGraph().getEditor().damageAll();

            // 4. Get the new page container and set the new page number
            Page nextPageContainer = getGraph().getDocument().getPageByPageNumber(nextPageNumber);
            getGraph().setCurrentPageNumber(nextPageNumber);

            // 5. Load the figs from this page container to the active layer
            List figs = nextPageContainer.getFigs();
            if (figs != null) {
                Layer lay = getGraph().getEditor().getLayerManager().getActiveLayer();
                for (Iterator it = figs.iterator(); it.hasNext(); ) {
                    Fig fig = (Fig) it.next();
                    lay.add(fig);
                }
            }
            getGraph().getEditor().postLoad();

            // 6. Update the page numbers
            updatePageNumbers();

            // 7. Update graph boundaries
            getGraph().updateGraphBoundaries();
            if (getPropertiesPanelController() != null) {
                getPropertiesPanelController().getPagePropertiesPanel().restorePageAttributes();
            }
            getGraph().getEditor().setScale(realZoom);
            getGraph().getEditor().damageAll();
            resizeMargins();

        }

    }

    public void addPageBeforeCurrentPage() {

        int currentPageNumber = getGraph().getCurrentPageNumber();

        // 1. Save figs back to the document container of the current page.
        Page currentPageContainer = getGraph().getDocument().getPageByPageNumber(currentPageNumber);
        currentPageContainer.setFigs(getGraph().getEditor().getLayerManager().getContents());

        // 2. Create a new page container, cloning the page attributes.
        PageAttributes duplicatedPageAttributes = getPageAttributes().duplicate();
        getGraph().insertPage(getGraph().getDocument(), duplicatedPageAttributes,
            (currentPageNumber - 1));

        // 3. Clear the graph
        getGraph().getEditor().getSelectionManager().deselectAll();
        getGraph().getEditor().getLayerManager().getActiveLayer().removeAll();
        getGraph().getEditor().damageAll();

        // 4. Update the page numbering
        updatePageNumbers();

        // 5. Update graph boundaries
        getGraph().updateGraphBoundaries();
        if (getPropertiesPanelController() != null) {
            getPropertiesPanelController().getPagePropertiesPanel().restorePageAttributes();
        }
        getGraph().getEditor().setScale(realZoom);
        getGraph().getEditor().damageAll();
        resizeMargins();

    }

    public void addPageAfterCurrentPage() {

        int currentPageNumber = getGraph().getCurrentPageNumber();

        // 1. Save figs back to the document container of the current page.
        Page currentPageContainer = getGraph().getDocument().getPageByPageNumber(currentPageNumber);
        currentPageContainer.setFigs(getGraph().getEditor().getLayerManager().getContents());

        // 2. Create a new page container, cloning the page attributes.
        PageAttributes duplicatedPageAttributes = getPageAttributes().duplicate();
        int newPageNumber = currentPageNumber + 1;

        getGraph()
            .insertPage(getGraph().getDocument(), duplicatedPageAttributes, currentPageNumber);

        getGraph().setCurrentPageNumber(newPageNumber);

        // 3. Clear the graph
        getGraph().getEditor().getSelectionManager().deselectAll();
        getGraph().getEditor().getLayerManager().getActiveLayer().removeAll();
        getGraph().getEditor().damageAll();

        // 4. Update the page numbering
        updatePageNumbers();

        // 5. Update graph boundaries
        getGraph().updateGraphBoundaries();
        if (getPropertiesPanelController() != null) {
            getPropertiesPanelController().getPagePropertiesPanel().restorePageAttributes();
        }
        getGraph().getEditor().setScale(realZoom);
        getGraph().getEditor().damageAll();
        resizeMargins();
    }

    public void addPageAtEndOfDocument() {
        // 1. Save figs back to the document container of the current page.
        Page currentPageContainer =
            getGraph().getDocument().getPageByPageNumber(getGraph().getCurrentPageNumber());
        currentPageContainer.setFigs(getGraph().getEditor().getLayerManager().getContents());

        // 2. Create a new page container, cloning the page attributes.
        PageAttributes duplicatedPageAttributes = getPageAttributes().duplicate();
        int newPageNumber = getGraph().getDocument().getNumberOfPages() + 1;

        getGraph().createPage(getGraph().getDocument(), duplicatedPageAttributes);
        getGraph().setCurrentPageNumber(newPageNumber);

        // 3. Clear the graph
        getGraph().getEditor().getSelectionManager().deselectAll();
        getGraph().getEditor().getLayerManager().getActiveLayer().removeAll();
        getGraph().getEditor().damageAll();

        // 4. Update the page numbering
        updatePageNumbers();

        // 5. Update graph boundaries
        getGraph().updateGraphBoundaries();
        if (getPropertiesPanelController() != null) {
            getPropertiesPanelController().getPagePropertiesPanel().restorePageAttributes();
        }
        getGraph().getEditor().setScale(realZoom);
        getGraph().getEditor().damageAll();
        resizeMargins();
    }

    private void addPageButtonActionPerformed(ActionEvent e) {

        InsertNewPageDialog inpd = new InsertNewPageDialog(Main.getInstance(), this);
        inpd.setVisible(true);

    }

    public void updatePageNumbers() {
        int currentPageNumber = getGraph().getCurrentPageNumber();
        int totalPages = getGraph().getDocument().getNumberOfPages();
        pageNumberLabel.setText(String
            .format(Localizer.localize("UI", "FormFramePageNumberLabel"), currentPageNumber + "",
                totalPages + ""));
    }

    private void removePageButtonActionPerformed(ActionEvent e) {

        // do not remove page 1 if there are no other pages.
        if (getGraph().getDocument().getNumberOfPages() <= 1) {
            return;
        }

        int currentPageNumber = getGraph().getCurrentPageNumber();

        // 1. Show a confirmation message
        String message = String
            .format(Localizer.localize("UI", "FormFrameConfirmRemovePageMessage"),
                currentPageNumber + "");
        String caption = Localizer.localize("UI", "FormFrameConfirmRemovePageTitle");
        int ret = javax.swing.JOptionPane
            .showConfirmDialog(this, message, caption, javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE);

        // 2. If confirmation message true
        if (ret == javax.swing.JOptionPane.YES_OPTION) {

            // 2.1 Remove page container from the document container, clear the graph too
            Document documentContainer = getGraph().getDocument();
            documentContainer.removePageByPageNumber(currentPageNumber);
            getGraph().getEditor().getSelectionManager().deselectAll();
            getGraph().getEditor().getLayerManager().getActiveLayer().removeAll();
            getGraph().getEditor().damageAll();

            int newPageNumber = 1;

            // 2.2 Figure out the next page number
            if (getGraph().getDocument().getNumberOfPages() > currentPageNumber) {
                newPageNumber = currentPageNumber;
            } else {
                newPageNumber = getGraph().getDocument().getNumberOfPages();
            }

            // 2.3 Set the new current page number then load the new page
            Page nextPageContainer = getGraph().getDocument().getPageByPageNumber(newPageNumber);
            getGraph().setCurrentPageNumber(newPageNumber);
            List figs = nextPageContainer.getFigs();
            if (figs != null) {
                Layer lay = getGraph().getEditor().getLayerManager().getActiveLayer();
                for (Iterator it = figs.iterator(); it.hasNext(); ) {
                    Fig fig = (Fig) it.next();
                    lay.add(fig);
                }
            }
            getGraph().getEditor().postLoad();

            // 2.4 Update the page numbering
            updatePageNumbers();

            // 2.5 Update graph boundaries
            getGraph().updateGraphBoundaries();
            if (getPropertiesPanelController() != null) {
                getPropertiesPanelController().getPagePropertiesPanel().restorePageAttributes();
            }
            getGraph().getEditor().setScale(realZoom);
            getGraph().getEditor().damageAll();
            resizeMargins();

        }

    }

    private void zoomInLabelMouseClicked(MouseEvent e) {
        int selectedIndex = editorZoomComboBox.getSelectedIndex();

        if (selectedIndex < (editorZoomComboBox.getItemCount() - 1)) {
            editorZoomComboBox.setSelectedIndex(selectedIndex + 1);
        }
    }

    private void zoomOutLabelMouseClicked(MouseEvent e) {
        int selectedIndex = editorZoomComboBox.getSelectedIndex();

        if (selectedIndex > 0) {
            editorZoomComboBox.setSelectedIndex(selectedIndex - 1);
        }
    }

    public void initComponents() {

        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        formFrameTabbedPane = new JTabbedPane();
        formEditorPanel = new JPanel();
        editorPanel = new JPanel();
        printDesignViewScrollPane = new JScrollPane();
        panel4 = new JPanel();
        pagePanel = new JPanel();
        topMarginPanel = new JPanel();
        centerPanel = new JPanel();
        leftMarginPanel = new JPanel();
        _graph = _graph;
        rightMarginPanel = new JPanel();
        bottomMarginPanel = new JPanel();
        toolbarScrollpane = new JScrollPane();
        toolbarContainerPanel = new JPanel();
        drawingToolsHeaderPanel = new JPanel();
        drawingToolsLabel = new JLabel();
        formPalette = new FormPalette();
        panel2 = new JPanel();
        editorZoomComboBox = new JComboBox();
        zoomInLabel = new JLabel();
        zoomOutLabel = new JLabel();
        alignmentToolbarContainerPanel = new JPanel();
        previousPageButton = new JButton();
        pageNumberLabel = new JLabel();
        nextPageButton = new JButton();
        addPageButton = new JButton();
        removePageButton = new JButton();
        graphRenderPanel = new GraphRenderPanel();
        publishFormPanel = new JPanel();

        //======== this ========
        setVisible(true);
        setBorder(null);
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

        //======== formFrameTabbedPane ========
        {
            formFrameTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));
            formFrameTabbedPane.addChangeListener(new ChangeListener() {
                @Override public void stateChanged(ChangeEvent e) {
                    formFrameTabbedPaneStateChanged(e);
                }
            });

            //======== formEditorPanel ========
            {
                formEditorPanel.setBorder(null);
                formEditorPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) formEditorPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) formEditorPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) formEditorPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) formEditorPanel.getLayout()).rowWeights =
                    new double[] {1.0, 0.0, 1.0E-4};

                //======== editorPanel ========
                {
                    editorPanel.setOpaque(false);
                    editorPanel.setLayout(new BorderLayout());

                    //======== printDesignViewScrollPane ========
                    {
                        printDesignViewScrollPane
                            .setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

                        //======== panel4 ========
                        {
                            panel4.setBackground(Color.darkGray);
                            panel4.setBorder(null);
                            panel4.setLayout(new GridBagLayout());
                            ((GridBagLayout) panel4.getLayout()).columnWidths = new int[] {0, 0};
                            ((GridBagLayout) panel4.getLayout()).rowHeights =
                                new int[] {0, 0, 0, 0};
                            ((GridBagLayout) panel4.getLayout()).columnWeights =
                                new double[] {1.0, 1.0E-4};
                            ((GridBagLayout) panel4.getLayout()).rowWeights =
                                new double[] {1.0, 0.0, 1.0, 1.0E-4};

                            //======== pagePanel ========
                            {
                                pagePanel.setLayout(new GridBagLayout());
                                ((GridBagLayout) pagePanel.getLayout()).columnWidths =
                                    new int[] {0, 0};
                                ((GridBagLayout) pagePanel.getLayout()).rowHeights =
                                    new int[] {0, 0, 0, 0};
                                ((GridBagLayout) pagePanel.getLayout()).columnWeights =
                                    new double[] {0.0, 1.0E-4};
                                ((GridBagLayout) pagePanel.getLayout()).rowWeights =
                                    new double[] {0.0, 0.0, 0.0, 1.0E-4};

                                //======== topMarginPanel ========
                                {
                                    topMarginPanel.setBackground(Color.white);
                                    topMarginPanel
                                        .setBorder(new MatteBorder(0, 0, 1, 0, Color.lightGray));
                                    topMarginPanel.setPreferredSize(new Dimension(1, 20));
                                    topMarginPanel.addMouseListener(new MouseAdapter() {
                                        @Override public void mouseClicked(MouseEvent e) {
                                            topMarginPanelMouseClicked(e);
                                        }
                                    });
                                    topMarginPanel.setLayout(new BorderLayout());
                                }
                                pagePanel.add(topMarginPanel,
                                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 0), 0, 0));

                                //======== centerPanel ========
                                {
                                    centerPanel.setBackground(Color.white);
                                    centerPanel.setBorder(null);
                                    centerPanel.setLayout(new GridBagLayout());
                                    ((GridBagLayout) centerPanel.getLayout()).columnWidths =
                                        new int[] {0, 0, 0, 0};
                                    ((GridBagLayout) centerPanel.getLayout()).rowHeights =
                                        new int[] {0, 0};
                                    ((GridBagLayout) centerPanel.getLayout()).columnWeights =
                                        new double[] {0.0, 1.0, 0.0, 1.0E-4};
                                    ((GridBagLayout) centerPanel.getLayout()).rowWeights =
                                        new double[] {0.0, 1.0E-4};

                                    //======== leftMarginPanel ========
                                    {
                                        leftMarginPanel.setBackground(Color.white);
                                        leftMarginPanel.setBorder(
                                            new MatteBorder(0, 0, 0, 1, Color.lightGray));
                                        leftMarginPanel.setPreferredSize(new Dimension(30, 1));
                                        leftMarginPanel.addMouseListener(new MouseAdapter() {
                                            @Override public void mouseClicked(MouseEvent e) {
                                                leftMarginPanelMouseClicked(e);
                                            }
                                        });
                                        leftMarginPanel.setLayout(new BorderLayout());
                                    }
                                    centerPanel.add(leftMarginPanel,
                                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                            new Insets(0, 0, 0, 0), 0, 0));

                                    //---- _graph ----
                                    _graph.setToolTipText("");
                                    _graph.setBorder(null);
                                    _graph.setPreferredSize(new Dimension(300, 300));
                                    centerPanel.add(_graph,
                                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                            new Insets(0, 0, 0, 0), 0, 0));

                                    //======== rightMarginPanel ========
                                    {
                                        rightMarginPanel.setBackground(Color.white);
                                        rightMarginPanel.setBorder(
                                            new MatteBorder(0, 1, 0, 0, Color.lightGray));
                                        rightMarginPanel.setPreferredSize(new Dimension(30, 1));
                                        rightMarginPanel.addMouseListener(new MouseAdapter() {
                                            @Override public void mouseClicked(MouseEvent e) {
                                                rightMarginPanelMouseClicked(e);
                                            }
                                        });
                                        rightMarginPanel.setLayout(new BorderLayout());
                                    }
                                    centerPanel.add(rightMarginPanel,
                                        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                                            GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                            new Insets(0, 0, 0, 0), 0, 0));
                                }
                                pagePanel.add(centerPanel,
                                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 0), 0, 0));

                                //======== bottomMarginPanel ========
                                {
                                    bottomMarginPanel.setBackground(Color.white);
                                    bottomMarginPanel
                                        .setBorder(new MatteBorder(1, 0, 0, 0, Color.lightGray));
                                    bottomMarginPanel.setPreferredSize(new Dimension(1, 20));
                                    bottomMarginPanel.addMouseListener(new MouseAdapter() {
                                        @Override public void mouseClicked(MouseEvent e) {
                                            bottomMarginPanelMouseClicked(e);
                                        }
                                    });
                                    bottomMarginPanel.setLayout(new BorderLayout());
                                }
                                pagePanel.add(bottomMarginPanel,
                                    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
                                        GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                        new Insets(0, 0, 0, 0), 0, 0));
                            }
                            panel4.add(pagePanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.VERTICAL,
                                new Insets(0, 0, 5, 0), 0, 0));
                        }
                        printDesignViewScrollPane.setViewportView(panel4);
                    }
                    editorPanel.add(printDesignViewScrollPane, BorderLayout.CENTER);

                    //======== toolbarScrollpane ========
                    {
                        toolbarScrollpane.setBorder(null);
                        toolbarScrollpane.setOpaque(false);
                        toolbarScrollpane.setHorizontalScrollBarPolicy(
                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

                        //======== toolbarContainerPanel ========
                        {
                            toolbarContainerPanel.setOpaque(false);
                            toolbarContainerPanel.setBorder(
                                new CompoundBorder(new MatteBorder(0, 1, 0, 0, Color.gray),
                                    new EmptyBorder(0, 2, 0, 0)));
                            toolbarContainerPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout) toolbarContainerPanel.getLayout()).columnWidths =
                                new int[] {0, 0};
                            ((GridBagLayout) toolbarContainerPanel.getLayout()).rowHeights =
                                new int[] {0, 0, 0, 0};
                            ((GridBagLayout) toolbarContainerPanel.getLayout()).columnWeights =
                                new double[] {1.0, 1.0E-4};
                            ((GridBagLayout) toolbarContainerPanel.getLayout()).rowWeights =
                                new double[] {0.0, 0.0, 1.0, 1.0E-4};

                            //======== drawingToolsHeaderPanel ========
                            {
                                drawingToolsHeaderPanel
                                    .setBorder(new MatteBorder(1, 1, 1, 1, Color.gray));
                                drawingToolsHeaderPanel.setLayout(new BorderLayout());
                                drawingToolsHeaderPanel.setUI(new GradientHeaderUI());

                                //---- drawingToolsLabel ----
                                drawingToolsLabel.setIcon(new ImageIcon(getClass().getResource(
                                    "/com/ebstrada/formreturn/manager/ui/icons/paintbrush.png")));
                                drawingToolsLabel.setMinimumSize(new Dimension(85, 26));
                                drawingToolsLabel.setPreferredSize(new Dimension(85, 26));
                                drawingToolsLabel.setFont(UIManager.getFont("Label.font"));
                                drawingToolsLabel.setIconTextGap(8);
                                drawingToolsLabel.setBorder(new EmptyBorder(0, 8, 0, 0));
                                drawingToolsLabel.setBackground(null);
                                drawingToolsLabel
                                    .setText(Localizer.localize("UI", "DrawingToolsLabel"));
                                drawingToolsHeaderPanel.add(drawingToolsLabel, BorderLayout.CENTER);
                            }
                            toolbarContainerPanel.add(drawingToolsHeaderPanel,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));

                            //---- formPalette ----
                            formPalette.setBorder(new EmptyBorder(3, 0, 0, 0));
                            toolbarContainerPanel.add(formPalette,
                                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        toolbarScrollpane.setViewportView(toolbarContainerPanel);
                    }
                    editorPanel.add(toolbarScrollpane, BorderLayout.EAST);
                }
                formEditorPanel.add(editorPanel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

                //======== panel2 ========
                {
                    panel2.setOpaque(false);
                    panel2.setBorder(new EmptyBorder(2, 3, 2, 3));
                    panel2.setLayout(new GridBagLayout());
                    ((GridBagLayout) panel2.getLayout()).columnWidths =
                        new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) panel2.getLayout()).columnWeights =
                        new double[] {0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};
                    ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                    //---- editorZoomComboBox ----
                    editorZoomComboBox.setModel(new DefaultComboBoxModel(
                        new String[] {"10%", "25%", "50%", "75%", "100%", "125%", "150%", "200%",
                            "250%", "350%", "500%", "700%", "1000%"}));
                    editorZoomComboBox.setSelectedIndex(4);
                    editorZoomComboBox.setFont(UIManager.getFont("ComboBox.font"));
                    editorZoomComboBox.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            editorZoomComboBoxActionPerformed(e);
                        }
                    });
                    panel2.add(editorZoomComboBox,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                            GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                    //---- zoomInLabel ----
                    zoomInLabel.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_in.png")));
                    zoomInLabel.setFont(UIManager.getFont("Label.font"));
                    zoomInLabel.addMouseListener(new MouseAdapter() {
                        @Override public void mouseClicked(MouseEvent e) {
                            zoomInLabelMouseClicked(e);
                        }
                    });
                    zoomInLabel.setToolTipText(Localizer.localize("UI", "ZoomInToolTip"));
                    panel2.add(zoomInLabel,
                        new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- zoomOutLabel ----
                    zoomOutLabel.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_out.png")));
                    zoomOutLabel.setFont(UIManager.getFont("Label.font"));
                    zoomOutLabel.addMouseListener(new MouseAdapter() {
                        @Override public void mouseClicked(MouseEvent e) {
                            zoomOutLabelMouseClicked(e);
                        }
                    });
                    zoomOutLabel.setToolTipText(Localizer.localize("UI", "ZoomOutToolTip"));
                    panel2.add(zoomOutLabel,
                        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //======== alignmentToolbarContainerPanel ========
                    {
                        alignmentToolbarContainerPanel.setOpaque(false);
                        alignmentToolbarContainerPanel.setLayout(
                            new BoxLayout(alignmentToolbarContainerPanel, BoxLayout.X_AXIS));
                    }
                    panel2.add(alignmentToolbarContainerPanel,
                        new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- previousPageButton ----
                    previousPageButton.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/editor/page_previous.png")));
                    previousPageButton.setBorderPainted(false);
                    previousPageButton.setFocusPainted(false);
                    previousPageButton.setMargin(new Insets(0, 0, 0, 0));
                    previousPageButton.setContentAreaFilled(false);
                    previousPageButton.setFont(UIManager.getFont("Label.font"));
                    previousPageButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            previousPageButtonActionPerformed(e);
                        }
                    });
                    previousPageButton.setToolTipText(
                        Localizer.localize("UI", "FormFramePreviewPageToolTipText"));
                    panel2.add(previousPageButton,
                        new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- pageNumberLabel ----
                    pageNumberLabel.setFont(UIManager.getFont("Label.font"));
                    pageNumberLabel.addMouseListener(new MouseAdapter() {
                        @Override public void mouseClicked(MouseEvent e) {
                            pageNumberLabelMouseClicked(e);
                        }
                    });
                    pageNumberLabel.setText(Localizer.localize("UI", "FormFrameDefaultPageNumber"));
                    pageNumberLabel.setToolTipText(
                        Localizer.localize("UI", "FormFrameShowAllPageNumbersToolTipText"));
                    panel2.add(pageNumberLabel,
                        new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- nextPageButton ----
                    nextPageButton.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/editor/page_next.png")));
                    nextPageButton.setBorderPainted(false);
                    nextPageButton.setFocusPainted(false);
                    nextPageButton.setMargin(new Insets(0, 0, 0, 0));
                    nextPageButton.setContentAreaFilled(false);
                    nextPageButton.setFont(UIManager.getFont("Label.font"));
                    nextPageButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            nextPageButtonActionPerformed(e);
                        }
                    });
                    nextPageButton
                        .setToolTipText(Localizer.localize("UI", "FormFrameNextPageToolTipText"));
                    panel2.add(nextPageButton,
                        new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- addPageButton ----
                    addPageButton.setFont(UIManager.getFont("Label.font"));
                    addPageButton.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/editor/page_add.png")));
                    addPageButton.setBorderPainted(false);
                    addPageButton.setFocusPainted(false);
                    addPageButton.setMargin(new Insets(0, 0, 0, 0));
                    addPageButton.setContentAreaFilled(false);
                    addPageButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            addPageButtonActionPerformed(e);
                        }
                    });
                    addPageButton
                        .setToolTipText(Localizer.localize("UI", "FormFrameAddPageToolTipText"));
                    panel2.add(addPageButton,
                        new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- removePageButton ----
                    removePageButton.setFont(UIManager.getFont("Label.font"));
                    removePageButton.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/editor/page_delete.png")));
                    removePageButton.setBorderPainted(false);
                    removePageButton.setFocusPainted(false);
                    removePageButton.setMargin(new Insets(0, 0, 0, 0));
                    removePageButton.setContentAreaFilled(false);
                    removePageButton.addActionListener(new ActionListener() {
                        @Override public void actionPerformed(ActionEvent e) {
                            removePageButtonActionPerformed(e);
                        }
                    });
                    removePageButton
                        .setToolTipText(Localizer.localize("UI", "FormFrameRemovePageToolTipText"));
                    panel2.add(removePageButton,
                        new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
                }
                formEditorPanel.add(panel2,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            formFrameTabbedPane.addTab("Form Editor", new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/one.png")),
                formEditorPanel);

            //---- graphRenderPanel ----
            graphRenderPanel.setOpaque(false);
            graphRenderPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            formFrameTabbedPane.addTab("Recognition Preview", new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/two.png")),
                graphRenderPanel);

            //======== publishFormPanel ========
            {
                publishFormPanel.setOpaque(false);
                publishFormPanel.setLayout(new BorderLayout());
            }
            formFrameTabbedPane.addTab("Publish Form", new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/three.png")),
                publishFormPanel);
        }
        add(formFrameTabbedPane,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // //GEN-END:initComponents
    }

    @Override public Editor getEditor() {
        return _graph.getEditor();
    }

    @Override public void unpressAllButtons() {
        if (formPalette != null) {
            formPalette.unpressAllButtons();
        }
    }

    @Override public void setActiveEditor() {
        if (_graph != null) {
            Globals.curEditor(_graph.getEditor());
        }
    }

    @Override public DocumentAttributes getDocumentAttributes() {
        return getGraph().getDocument().getDocumentAttributes();
    }

    @Override public PageAttributes getPageAttributes() {
        return _graph.getPageAttributes();
    }

    public void lockTabs() {

        if (formFrameTabbedPane == null) {
            return;
        }

        if (formFrameTabbedPane.getTabCount() != 3) {
            return;
        }

        formFrameTabbedPane.setEnabledAt(0, false);
        formFrameTabbedPane.setEnabledAt(1, false);
        formFrameTabbedPane.setEnabledAt(2, false);
    }

    public void unlockTabs() {

        if (formFrameTabbedPane == null) {
            return;
        }

        if (formFrameTabbedPane.getTabCount() != 3) {
            return;
        }

        formFrameTabbedPane.setEnabledAt(0, true);
        formFrameTabbedPane.setEnabledAt(1, true);
        formFrameTabbedPane.setEnabledAt(2, true);
    }

    public void updateProperties() {

        if (formFrameTabbedPane.getSelectedIndex() == 0) {
            if (getPropertiesPanelController() != null) {
                getPropertiesPanelController().destroyPanels();
                Vector sels = _graph.selectedFigs();
                updatePropertyBox(sels);
                restoreScale();
            }
        }
        if (formFrameTabbedPane.getSelectedIndex() == 1) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (getPropertiesPanelController() != null) {
                        getPropertiesPanelController().destroyPanels();
                        if (graphRenderPanel.getRecognitionPreviewPanel() != null) {
                            if (graphRenderPanel.getRecognitionPreviewPanel()
                                .getRecognitionPanelController() != null) {
                                graphRenderPanel.getRecognitionPreviewPanel()
                                    .getRecognitionPanelController().updateAllPanels();
                            }
                        }
                    }
                }
            });
        }
        if (formFrameTabbedPane.getSelectedIndex() == 2) {
            if (getPropertiesPanelController() != null) {
                getPropertiesPanelController().destroyPanels();
                getPropertiesPanelController().showPublishPanel(this);
            }
        }
    }

    public void addFigsToActiveLayer() {
        List figs = getGraph().getEditor().getLayerManager().getContents();
        if (figs != null) {
            Layer lay = getGraph().getEditor().getLayerManager().getActiveLayer();
            for (Iterator it = figs.iterator(); it.hasNext(); ) {
                Fig fig = (Fig) it.next();
                fig.setLayer(lay);
            }
        }
        getGraph().getEditor().postLoad();
    }

    private void formFrameTabbedPaneStateChanged(ChangeEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                formFrameTabbedPaneStateChanged();
            }
        });
    }

    private void formFrameTabbedPaneStateChanged() {

        lockTabs();
        Main.getInstance().blockInput();

        if (formFrameTabbedPane.getSelectedIndex() == 0) {
            Globals.curEditor(getGraph().getEditor());
            updateProperties();
            unlockTabs();
            Main.getInstance().unblockInput();
        }

        if (formFrameTabbedPane.getSelectedIndex() == 1) {

            getPageAttributes().setFormPageId(12345);
            getPageAttributes().setFormPassword("67890");

            boolean hadToResizeGraphToFitFigs = _graph.updateGraphBoundaries();

            if (hadToResizeGraphToFitFigs) {
                getPageAttributes().setCroppedWidth((int) _graph.getPreferredSize().getWidth());
                getPageAttributes().setCroppedHeight((int) _graph.getPreferredSize().getHeight());
                getPageAttributes().setDimension(new Dimension(
                    getPageAttributes().getCroppedWidth() + getPageAttributes().getLeftMargin()
                        + getPageAttributes().getRightMargin(),
                    getPageAttributes().getCroppedHeight() + getPageAttributes().getTopMargin()
                        + getPageAttributes().getBottomMargin()));
                if (getPropertiesPanelController() != null) {
                    getPropertiesPanelController().getPagePropertiesPanel().restorePageAttributes();
                }
                String message = Localizer.localize("UI", "FormFrameGraphResizedWarningMessage");
                String caption = Localizer.localize("UI", "WarningTitle");
                javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            }

            getPropertiesPanelController().destroyPanels();
            rebuildPreview();

        }

        if (formFrameTabbedPane.getSelectedIndex() == 2) {

            boolean hadToResizeGraphToFitFigs = _graph.updateGraphBoundaries();

            if (hadToResizeGraphToFitFigs) {
                getPageAttributes().setCroppedWidth((int) _graph.getPreferredSize().getWidth());
                getPageAttributes().setCroppedHeight((int) _graph.getPreferredSize().getHeight());
                getPageAttributes().setDimension(new Dimension(
                    getPageAttributes().getCroppedWidth() + getPageAttributes().getLeftMargin()
                        + getPageAttributes().getRightMargin(),
                    getPageAttributes().getCroppedHeight() + getPageAttributes().getTopMargin()
                        + getPageAttributes().getBottomMargin()));
                if (getPropertiesPanelController() != null) {
                    getPropertiesPanelController().getPagePropertiesPanel().restorePageAttributes();
                }
                String message = Localizer.localize("UI", "FormFrameGraphResizedWarningMessage");
                String caption = Localizer.localize("UI", "WarningTitle");
                javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
                    javax.swing.JOptionPane.DEFAULT_OPTION,
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            }

            pfp.restorePublicationRecognitionStructure();

            updateProperties();
            unlockTabs();
            Main.getInstance().unblockInput();

        }

    }


    public PublicationRecognitionStructure getPublicationRecognitionStructure() {

        return getDocumentAttributes().getPublicationRecognitionStructure();

    }

    public Document getDocument() {

        // set the current page data into the document
        Page currentPageContainer =
            getGraph().getDocument().getPageByPageNumber(getGraph().getCurrentPageNumber());
        currentPageContainer.setFigs(getGraph().getEditor().getLayerManager().getContents());

        return getGraph().getDocument();

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JTabbedPane formFrameTabbedPane;
    private JPanel formEditorPanel;
    private JPanel editorPanel;
    private JScrollPane printDesignViewScrollPane;
    private JPanel panel4;
    private JPanel pagePanel;
    private JPanel topMarginPanel;
    private JPanel centerPanel;
    private JPanel leftMarginPanel;
    private JGraph _graph;
    private JPanel rightMarginPanel;
    private JPanel bottomMarginPanel;
    private JScrollPane toolbarScrollpane;
    private JPanel toolbarContainerPanel;
    private JPanel drawingToolsHeaderPanel;
    private JLabel drawingToolsLabel;
    private FormPalette formPalette;
    private JPanel panel2;
    private JComboBox editorZoomComboBox;
    private JLabel zoomInLabel;
    private JLabel zoomOutLabel;
    private JPanel alignmentToolbarContainerPanel;
    private JButton previousPageButton;
    private JLabel pageNumberLabel;
    private JButton nextPageButton;
    private JButton addPageButton;
    private JButton removePageButton;
    private GraphRenderPanel graphRenderPanel;
    private JPanel publishFormPanel;
    // JFormDesigner - End of variables declaration //GEN-END:variables


    public boolean isFigFullyVisible(Fig fig) {

        int figRightBoundary =
            (int) ((fig.getX() + fig.getWidth() + getPageAttributes().getLeftMargin()) * realZoom);
        int figBottomBoundary =
            (int) ((fig.getY() + fig.getHeight() + getPageAttributes().getTopMargin()) * realZoom);

        Rectangle visibleRectangle = printDesignViewScrollPane.getViewport().getViewRect();

        int visibleHeight = (int) (visibleRectangle.getY() + visibleRectangle.getHeight());
        int visibleWidth = (int) (visibleRectangle.getX() + visibleRectangle.getWidth());

        boolean isFullyVisible = true;

        if (figRightBoundary > visibleWidth) {
            isFullyVisible = false;
        }

        if (figBottomBoundary > visibleHeight) {
            isFullyVisible = false;
        }

        if (visibleRectangle.getY() > ((fig.getY() + getPageAttributes().getTopMargin())
            * realZoom)) {
            isFullyVisible = false;
        }

        if (visibleRectangle.getX() > ((fig.getX() + getPageAttributes().getLeftMargin())
            * realZoom)) {
            isFullyVisible = false;
        }

        return isFullyVisible;
    }

    @Override public JGraph getGraph() {
        return _graph;
    }

    public PublishFormPanel getPfp() {
        return pfp;
    }

    public void setPfp(PublishFormPanel pfp) {
        this.pfp = pfp;
    }

    @Override public void rebuildPreview() {

        SwingWorker worker = new SwingWorker<RecognitionPreviewPanel, Void>() {

            @Override public RecognitionPreviewPanel doInBackground() {

                JStatusBar.getInstance().setProgressLabelText(
                    Localizer.localize("UI", "FormFrameCreatingPreviewMessage"));

                JStatusBar.getInstance().setProgressValue(20);

                RecognitionPreviewPanel recognitionPreviewPanel =
                    new RecognitionPreviewPanel(_graph, getPageAttributes(),
                        getPublicationRecognitionStructure());
                JStatusBar.getInstance().setProgressValue(70);

                return recognitionPreviewPanel;

            }

            public void done() {
                RecognitionPreviewPanel recognitionPreviewPanel;
                try {
                    recognitionPreviewPanel = get();
                    graphRenderPanel.updateView(recognitionPreviewPanel,
                        new Dimension(formEditorPanel.getWidth(), formEditorPanel.getHeight() - 5));
                } catch (InterruptedException e) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                } catch (ExecutionException e) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
                }

                unlockTabs();
                Main.getInstance().unblockInput();
                graphRenderPanel.showMessages();
            }

        };
        worker.execute();
        graphRenderPanel.removeAll();

        JXBusyLabel label = new JXBusyLabel(new Dimension(100, 100));
        BusyPainter painter =
            new BusyPainter(new RoundRectangle2D.Float(0, 0, 21.0f, 4.2f, 10.0f, 10.0f),
                new Ellipse2D.Float(15.0f, 15.0f, 70.0f, 70.0f));
        painter.setTrailLength(7);
        painter.setPoints(20);
        painter.setFrame(9);
        label.setPreferredSize(new Dimension(100, 100));
        label.setIcon(new EmptyIcon(100, 100));
        label.setBusyPainter(painter);
        graphRenderPanel.setLayout(new BorderLayout());
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        graphRenderPanel.add(label, BorderLayout.CENTER);
        label.setBusy(true);

    }

    public boolean isFinishedLoading() {
        return finishedLoading;
    }

    public void setFinishedLoading(boolean finishedLoading) {
        this.finishedLoading = finishedLoading;
    }


    public void refreshDatabaseTables() {
        if (pfp != null) {
            pfp.refresh();
        }
    }

}

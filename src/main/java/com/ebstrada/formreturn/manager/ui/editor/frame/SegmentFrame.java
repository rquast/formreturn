package com.ebstrada.formreturn.manager.ui.editor.frame;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import javax.swing.*;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import com.ebstrada.formreturn.manager.gef.presentation.FigBarcode;
import com.ebstrada.formreturn.manager.gef.ui.AlignmentPalette;
import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.ui.SegmentPalette;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FormRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.PublicationRecognitionStructure;
import com.ebstrada.formreturn.manager.logic.recognition.structure.SegmentRecognitionStructure;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.GradientHeaderUI;
import com.ebstrada.formreturn.manager.ui.component.JStatusBar;
import com.ebstrada.formreturn.manager.ui.editor.GraphRenderPanel;
import com.ebstrada.formreturn.manager.ui.editor.RecognitionPreviewPanel;
import com.ebstrada.formreturn.manager.ui.frame.EditorFrame;
import com.ebstrada.formreturn.manager.ui.panel.PropertiesPanelController;

public class SegmentFrame extends EditorFrame {

    private static final long serialVersionUID = 1L;

    int headerHeight = 0;

    int footerHeight = 0;

    private FigBarcode topRightBarcode;

    private FigBarcode bottomLeftBarcode;

    private ZoomAction zoomAction;

    private float realZoom = 1.0f;

    private boolean finishedLoading = false;

    public SegmentFrame(JGraph graph) {

        super();

        _graph = graph;

        initComponents();

        segmentFrameTabbedPane
            .setTitleAt(0, Localizer.localize("UI", "SegmentFrameSegmentEditorTabTitle"));
        segmentFrameTabbedPane
            .setTitleAt(1, Localizer.localize("UI", "SegmentFrameRecognitionPreviewTabTitle"));

        // setup toolbar
        toolbarScrollPane.getViewport().setBackground(null);
        toolbarScrollPane.getViewport().setOpaque(false);
        toolbarScrollPane.getViewport().setBorder(null);
        toolbarScrollPane.setViewportBorder(null);

        GraphModel graphModel = null;

        if (_graph.getGraphModel() == null) {
            graphModel = new DefaultGraphModel();
            _graph.setGraphModel(graphModel);
        }

        restorePageAttributes();

        GraphSelectionListener gsl = new GraphSelectionListener() {

            public void selectionChanged(GraphSelectionEvent gse) {
                getPropertiesPanelController().destroyPanels();
                Vector sels = gse.getSelections();
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

        printDesignViewScrollPane.getVerticalScrollBar().setUnitIncrement(30);
        printDesignViewScrollPane.getVerticalScrollBar().setBlockIncrement(90);
        printDesignViewScrollPane.getHorizontalScrollBar().setUnitIncrement(30);
        printDesignViewScrollPane.getHorizontalScrollBar().setBlockIncrement(90);

        // reset the undo manager
        UndoManager undoInstance = UndoManager.getInstance();
        if (undoInstance != null) {
            undoInstance.reset();
            undoInstance.fireAllEvents();
        }

    }

    public PublicationRecognitionStructure getPublicationRecognitionStructure() {

        return getDocumentAttributes().getPublicationRecognitionStructure();

    }

    public PropertiesPanelController getPropertiesPanelController() {
        return Main.getInstance().getPropertiesPanelController();
    }

    public void removeRecognitionFigs() {
        Editor ce = _graph.getEditor();
        if (topRightBarcode != null) {
            ce.removeNoEventChange(getTopRightBarcode());
            topRightBarcode = null;
        }
        if (bottomLeftBarcode != null) {
            ce.removeNoEventChange(getBottomLeftBarcode());
            bottomLeftBarcode = null;
        }

        ArrayList<FigBarcode> removeBarcodes = new ArrayList<FigBarcode>();
        Layer lay = _graph.getEditor().getLayerManager().getActiveLayer();
        List figs = lay.getContents();
        if (figs != null) {
            Iterator fit = figs.iterator();
            while (fit.hasNext()) {
                Fig fig = (Fig) fit.next();
                if (fig instanceof FigBarcode) {
                    FigBarcode figBarcode = (FigBarcode) fig;
                    if (figBarcode.getLocked()) {
                        removeBarcodes.add(figBarcode);
                    }
                }
            }
        }
        for (FigBarcode figBarcode : removeBarcodes) {
            _graph.getEditor().remove(figBarcode);
        }

        _graph.getEditor().damageAll();

    }

    public void setRecognitionFigs() {

        Editor ce = _graph.getEditor();

        if (getPageAttributes().hasRecognitionBarcodes()) {
            ce.addNoEventChange(getTopRightBarcode());
            ce.addNoEventChange(getBottomLeftBarcode());
        } else {
            if (topRightBarcode != null) {
                ce.removeNoEventChange(getTopRightBarcode());
            }
            if (bottomLeftBarcode != null) {
                ce.removeNoEventChange(getBottomLeftBarcode());
            }
        }

        if (topRightBarcode != null) {
            topRightBarcode.updateSegmentBarcodes();
        }

        if (bottomLeftBarcode != null) {
            bottomLeftBarcode.updateSegmentBarcodes();
        }


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

                Fig selectedFig = (Fig) o;

                getPropertiesPanelController().initFig(selectedFig);

            }

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
                getPropertiesPanelController().initFigs(sels);
            } else {
                getPropertiesPanelController().destroyPanels();
                getPropertiesPanelController().showSegmentPanel(_graph, this);
            }

            // else clear properties panel and reset all selected object
            // listeners
        } else {

            getPropertiesPanelController().destroyPanels();
            getPropertiesPanelController().showSegmentPanel(_graph, this);

        }

    }

    public void setPageAttributes(PageAttributes pageAttributes) {
        _graph.setPageAttributes(pageAttributes);
    }

    public void restorePageAttributes(PageAttributes pageAttributes) {
        setPageAttributes(pageAttributes);
        restorePageAttributes();
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

        _graph.setMinimumSize(new Dimension(getPageAttributes().getCroppedWidth(),
            getPageAttributes().getCroppedHeight()));
        _graph.setDrawingSize(new Dimension(getPageAttributes().getCroppedWidth(),
            getPageAttributes().getCroppedHeight()));
        _graph.setPreferredSize(new Dimension((int) (getPageAttributes().getCroppedWidth()),
            (int) (getPageAttributes().getCroppedHeight())));
        _graph.updateUI();

        Editor ed = Globals.curEditor();
        if (ed == null) {
            return;
        }

        if (_graph.getEditor().getScale() > 0.0) {
            ed.setScale(_graph.getEditor().getScale());
        } else {
            ed.setScale(1.0);
        }

        removeRecognitionFigs();
        setRecognitionFigs();

        ed.damageAll();

        restoreScale();

    }

    public Dimension getGraphDimension() {
        return _graph.getMinimumSize();
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

    @Override public boolean closeEditorFrame() {
        Editor ce = _graph.getEditor();

        if (ce.hasEditorStateChanged() != false) {

            Main.getInstance().activateEditorFrame(this);

            String filename = getTitle();
            if (getGraph().getDocumentPackage().getPackageFile() != null) {
                filename = getGraph().getDocumentPackage().getPackageFile().getName();
            }

            String message = String
                .format(Localizer.localize("UI", "SegmentFrameFileModifiedSaveConfirmationMessage"),
                    filename);
            String caption =
                Localizer.localize("UI", "SegmentFrameFileModifiedSaveConfirmationTitle");

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

        repaint();
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
        segmentFrameTabbedPane = new JTabbedPane();
        segmentEditorPanel = new JPanel();
        editorDrawingPanel = new JPanel();
        toolbarScrollPane = new JScrollPane();
        toolbarPanel = new JPanel();
        drawingToolsHeaderPanel = new JPanel();
        drawingToolsLabel = new JLabel();
        segmentPalette = new SegmentPalette();
        printDesignViewScrollPane = new JScrollPane();
        backgroundPanel = new JPanel();
        _graph = _graph;
        editorLowerPanel = new JPanel();
        editorZoomComboBox = new JComboBox();
        zoomInLabel = new JLabel();
        zoomOutLabel = new JLabel();
        alignmentToolbarContainerPanel = new AlignmentPalette();
        graphRenderPanel = new GraphRenderPanel();

        //======== this ========
        setVisible(true);
        setBorder(null);
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

        //======== segmentFrameTabbedPane ========
        {
            segmentFrameTabbedPane.setFont(UIManager.getFont("TabbedPane.font"));
            segmentFrameTabbedPane.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent e) {
                    segmentFrameTabbedPaneStateChanged(e);
                }
            });

            //======== segmentEditorPanel ========
            {
                segmentEditorPanel.setBorder(null);
                segmentEditorPanel.setFont(UIManager.getFont("Panel.font"));
                segmentEditorPanel.setMinimumSize(new Dimension(169, 34));
                segmentEditorPanel.setLayout(new GridBagLayout());
                ((GridBagLayout) segmentEditorPanel.getLayout()).columnWidths = new int[] {0, 0};
                ((GridBagLayout) segmentEditorPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
                ((GridBagLayout) segmentEditorPanel.getLayout()).columnWeights =
                    new double[] {1.0, 1.0E-4};
                ((GridBagLayout) segmentEditorPanel.getLayout()).rowWeights =
                    new double[] {1.0, 0.0, 1.0E-4};

                //======== editorDrawingPanel ========
                {
                    editorDrawingPanel.setLayout(new BorderLayout());

                    //======== toolbarScrollPane ========
                    {
                        toolbarScrollPane.setHorizontalScrollBarPolicy(
                            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        toolbarScrollPane.setBorder(null);

                        //======== toolbarPanel ========
                        {
                            toolbarPanel.setBorder(
                                new CompoundBorder(new MatteBorder(0, 1, 0, 0, Color.gray),
                                    new EmptyBorder(0, 2, 0, 0)));
                            toolbarPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout) toolbarPanel.getLayout()).columnWidths =
                                new int[] {0, 0};
                            ((GridBagLayout) toolbarPanel.getLayout()).rowHeights =
                                new int[] {27, 0, 0, 0};
                            ((GridBagLayout) toolbarPanel.getLayout()).columnWeights =
                                new double[] {1.0, 1.0E-4};
                            ((GridBagLayout) toolbarPanel.getLayout()).rowWeights =
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
                            toolbarPanel.add(drawingToolsHeaderPanel,
                                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));

                            //---- segmentPalette ----
                            segmentPalette.setBorder(new EmptyBorder(3, 0, 0, 0));
                            segmentPalette.unpressAllButtons();
                            toolbarPanel.add(segmentPalette,
                                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                    new Insets(0, 0, 0, 0), 0, 0));
                        }
                        toolbarScrollPane.setViewportView(toolbarPanel);
                    }
                    editorDrawingPanel.add(toolbarScrollPane, BorderLayout.EAST);

                    //======== printDesignViewScrollPane ========
                    {
                        printDesignViewScrollPane
                            .setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));

                        //======== backgroundPanel ========
                        {
                            backgroundPanel.setBackground(Color.darkGray);
                            backgroundPanel.setBorder(null);
                            backgroundPanel.setLayout(new GridBagLayout());
                            ((GridBagLayout) backgroundPanel.getLayout()).columnWidths =
                                new int[] {0, 0};
                            ((GridBagLayout) backgroundPanel.getLayout()).rowHeights =
                                new int[] {0, 0};
                            ((GridBagLayout) backgroundPanel.getLayout()).columnWeights =
                                new double[] {1.0, 1.0E-4};
                            ((GridBagLayout) backgroundPanel.getLayout()).rowWeights =
                                new double[] {1.0, 1.0E-4};

                            //---- _graph ----
                            _graph.setToolTipText("");
                            _graph.setBorder(null);
                            _graph.setPreferredSize(new Dimension(300, 300));
                            backgroundPanel.add(_graph, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
                                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                                new Insets(0, 0, 0, 0), 0, 0));
                        }
                        printDesignViewScrollPane.setViewportView(backgroundPanel);
                    }
                    editorDrawingPanel.add(printDesignViewScrollPane, BorderLayout.CENTER);
                }
                segmentEditorPanel.add(editorDrawingPanel,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

                //======== editorLowerPanel ========
                {
                    editorLowerPanel.setOpaque(false);
                    editorLowerPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
                    editorLowerPanel.setLayout(new GridBagLayout());
                    ((GridBagLayout) editorLowerPanel.getLayout()).columnWidths =
                        new int[] {0, 0, 0, 0, 0, 0};
                    ((GridBagLayout) editorLowerPanel.getLayout()).rowHeights = new int[] {0, 0};
                    ((GridBagLayout) editorLowerPanel.getLayout()).columnWeights =
                        new double[] {0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
                    ((GridBagLayout) editorLowerPanel.getLayout()).rowWeights =
                        new double[] {1.0, 1.0E-4};

                    //---- editorZoomComboBox ----
                    editorZoomComboBox.setModel(new DefaultComboBoxModel(
                        new String[] {"10%", "25%", "50%", "75%", "100%", "125%", "150%", "200%",
                            "250%", "350%", "500%", "700%", "1000%"}));
                    editorZoomComboBox.setSelectedIndex(4);
                    editorZoomComboBox.setFont(UIManager.getFont("ComboBox.font"));
                    editorZoomComboBox.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            editorZoomComboBoxActionPerformed(e);
                        }
                    });
                    editorLowerPanel.add(editorZoomComboBox,
                        new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                            GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));

                    //---- zoomInLabel ----
                    zoomInLabel.setIcon(new ImageIcon(getClass().getResource(
                        "/com/ebstrada/formreturn/manager/ui/icons/preview/zoom_in.png")));
                    zoomInLabel.addMouseListener(new MouseAdapter() {
                        @Override public void mouseClicked(MouseEvent e) {
                            zoomInLabelMouseClicked(e);
                        }
                    });
                    zoomInLabel.setToolTipText(Localizer.localize("UI", "ZoomInToolTip"));
                    editorLowerPanel.add(zoomInLabel,
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
                    editorLowerPanel.add(zoomOutLabel,
                        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                            GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 5), 0, 0));

                    //---- alignmentToolbarContainerPanel ----
                    alignmentToolbarContainerPanel.setOpaque(false);
                    editorLowerPanel.add(alignmentToolbarContainerPanel,
                        new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                            GridBagConstraints.NONE, new Insets(0, 0, 0, 5), 0, 0));
                }
                segmentEditorPanel.add(editorLowerPanel,
                    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            segmentFrameTabbedPane.addTab("Segment Editor", new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/one.png")),
                segmentEditorPanel);


            //---- graphRenderPanel ----
            graphRenderPanel.setOpaque(false);
            graphRenderPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            graphRenderPanel.setFont(UIManager.getFont("Panel.font"));
            segmentFrameTabbedPane.addTab("Recognition Preview", new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/two.png")),
                graphRenderPanel);

        }
        add(segmentFrameTabbedPane,
            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // //GEN-END:initComponents
    }

    @Override public Editor getEditor() {
        return _graph.getEditor();
    }

    @Override public void unpressAllButtons() {
        if (segmentPalette != null) {
            segmentPalette.unpressAllButtons();
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

        if (segmentFrameTabbedPane == null) {
            return;
        }

        if (segmentFrameTabbedPane.getTabCount() != 2) {
            return;
        }

        segmentFrameTabbedPane.setEnabledAt(0, false);
        segmentFrameTabbedPane.setEnabledAt(1, false);

    }

    public void unlockTabs() {

        if (segmentFrameTabbedPane == null) {
            return;
        }

        if (segmentFrameTabbedPane.getTabCount() != 2) {
            return;
        }

        segmentFrameTabbedPane.setEnabledAt(0, true);
        segmentFrameTabbedPane.setEnabledAt(1, true);

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

    public void updateProperties() {

        if (segmentFrameTabbedPane.getSelectedIndex() == 0) {
            if (getPropertiesPanelController() != null) {
                getPropertiesPanelController().destroyPanels();
                Vector sels = _graph.selectedFigs();
                updatePropertyBox(sels);
                restoreScale();
            }
        }
        if (segmentFrameTabbedPane.getSelectedIndex() == 1) {
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

    }

    private void segmentFrameTabbedPaneStateChanged(ChangeEvent e) {

        lockTabs();
        Main.getInstance().blockInput();

        if (segmentFrameTabbedPane.getSelectedIndex() == 0) {
            if (getPropertiesPanelController() != null) {
                getPropertiesPanelController().destroyPanels();
                Vector sels = _graph.selectedFigs();
                updatePropertyBox(sels);
                restoreScale();
            }
            unlockTabs();
            Main.getInstance().unblockInput();
        }

        if (segmentFrameTabbedPane.getSelectedIndex() == 1) {

            boolean hadToResizeGraphToFitFigs = _graph.updateGraphBoundaries();
	    
	    /*
	    if ( hadToResizeGraphToFitFigs ) {
		    getPageAttributes().setCroppedWidth((int) _graph.getPreferredSize().getWidth());
		    getPageAttributes().setCroppedHeight((int) _graph.getPreferredSize().getHeight());
		    getPageAttributes().setDimension(new Dimension(getPageAttributes().getCroppedWidth() + getPageAttributes().getLeftMargin() + getPageAttributes().getRightMargin(),
			    getPageAttributes().getCroppedHeight() + getPageAttributes().getTopMargin() + getPageAttributes().getBottomMargin()));
		    if ( getPropertiesPanelController() != null ) {
			getPropertiesPanelController().getPagePropertiesPanel().restorePageAttributes();
		    }		
		    String message = Localizer.localize("UI", "SegmentFrameGraphResizedWarningMessage");
		    String caption = Localizer.localize("UI", "WarningTitle");
		    javax.swing.JOptionPane.showConfirmDialog(Main.getInstance(), message, caption,
			    javax.swing.JOptionPane.DEFAULT_OPTION,
			    javax.swing.JOptionPane.WARNING_MESSAGE);
	    }
	    */

            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    getPropertiesPanelController().destroyPanels();
                }
            });
            rebuildPreview();

        }

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JTabbedPane segmentFrameTabbedPane;
    private JPanel segmentEditorPanel;
    private JPanel editorDrawingPanel;
    private JScrollPane toolbarScrollPane;
    private JPanel toolbarPanel;
    private JPanel drawingToolsHeaderPanel;
    private JLabel drawingToolsLabel;
    private SegmentPalette segmentPalette;
    private JScrollPane printDesignViewScrollPane;
    private JPanel backgroundPanel;
    private JGraph _graph;
    private JPanel editorLowerPanel;
    private JComboBox editorZoomComboBox;
    private JLabel zoomInLabel;
    private JLabel zoomOutLabel;
    private AlignmentPalette alignmentToolbarContainerPanel;
    private GraphRenderPanel graphRenderPanel;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public FigBarcode getTopRightBarcode() {

        if (topRightBarcode == null) {
            try {
                topRightBarcode = new FigBarcode(0, 0, "CODE128", "01",
                    getPageAttributes().getRecognitionBarcodesScale(), false, false);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            }
        }

        topRightBarcode.setRecognitionMarkerType(FigBarcode.MARKER_TOP_RIGHT);
        topRightBarcode.setLocked(true);

        return topRightBarcode;
    }

    public void setTopRightBarcode(FigBarcode topRightBarcode) {
        this.topRightBarcode = topRightBarcode;
    }

    public FigBarcode getBottomLeftBarcode() {

        if (bottomLeftBarcode == null) {
            try {
                bottomLeftBarcode = new FigBarcode(0, 0, "CODE128", "02",
                    getPageAttributes().getRecognitionBarcodesScale(), false, false);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
            }

        }

        bottomLeftBarcode.setRecognitionMarkerType(FigBarcode.MARKER_BOTTOM_LEFT);
        bottomLeftBarcode.setLocked(true);

        return bottomLeftBarcode;
    }

    public void setBottomLeftBarcode(FigBarcode bottomLeftBarcode) {
        this.bottomLeftBarcode = bottomLeftBarcode;
    }


    public boolean isFigFullyVisible(Fig fig) {

        int figRightBoundary = (int) ((fig.getX() + fig.getWidth()) * realZoom);
        int figBottomBoundary = (int) ((fig.getY() + fig.getHeight()) * realZoom);

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

        if (visibleRectangle.getY() > (fig.getY() * realZoom)) {
            isFullyVisible = false;
        }

        if (visibleRectangle.getX() > (fig.getX() * realZoom)) {
            isFullyVisible = false;
        }

        return isFullyVisible;
    }

    @Override public JGraph getGraph() {
        return _graph;
    }

    @Override public void rebuildPreview() {
        SwingWorker worker = new SwingWorker<RecognitionPreviewPanel, Void>() {

            @Override public RecognitionPreviewPanel doInBackground() {

                JStatusBar.getInstance().setProgressLabelText(
                    Localizer.localize("UI", "SegmentFrameCreatingPreviewMessage"));

                int barcodeOneValue = 1;

                // FIX THIS DETECTION!!!!!
                FormRecognitionStructure frs = new FormRecognitionStructure();
                SegmentRecognitionStructure segmentRecognitionStructure =
                    new SegmentRecognitionStructure();
                segmentRecognitionStructure.setBarcodeOneValue(barcodeOneValue);
                segmentRecognitionStructure.setBarcodeTwoValue(barcodeOneValue + 1);
                segmentRecognitionStructure.setWidth(getPageAttributes().getCroppedWidth());
                segmentRecognitionStructure.setHeight(getPageAttributes().getCroppedHeight());
                frs.addSegmentRecognitionStructure(barcodeOneValue, segmentRecognitionStructure);
                _graph.setFormRecognitionStructure(frs);

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
                        new Dimension(segmentEditorPanel.getWidth(),
                            segmentEditorPanel.getHeight() - 5));
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

}

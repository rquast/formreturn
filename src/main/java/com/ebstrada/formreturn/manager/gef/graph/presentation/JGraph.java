package com.ebstrada.formreturn.manager.gef.graph.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.ToolTipManager;

import com.ebstrada.formreturn.manager.gef.base.DeleteFromModelAction;
import com.ebstrada.formreturn.manager.gef.base.Editor;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.base.Layer;
import com.ebstrada.formreturn.manager.gef.base.LayerDiagram;
import com.ebstrada.formreturn.manager.gef.base.LayerGrid;
import com.ebstrada.formreturn.manager.gef.base.NudgeAction;
import com.ebstrada.formreturn.manager.gef.base.SelectNearAction;
import com.ebstrada.formreturn.manager.gef.base.SelectNextAction;
import com.ebstrada.formreturn.manager.gef.base.ZoomAction;
import com.ebstrada.formreturn.manager.gef.event.GraphSelectionListener;
import com.ebstrada.formreturn.manager.gef.event.ModeChangeListener;
import com.ebstrada.formreturn.manager.gef.font.CachedFontManager;
import com.ebstrada.formreturn.manager.gef.graph.GraphModel;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigBarcode;
import com.ebstrada.formreturn.manager.gef.presentation.FigTextEditor;
import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.logic.recognition.structure.FormRecognitionStructure;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.persistence.xstream.DocumentPackage;
import com.ebstrada.formreturn.manager.persistence.xstream.Page;
import com.ebstrada.formreturn.manager.ui.Main;

public class JGraph extends JPanel implements Cloneable, AdjustmentListener {

    private static final long serialVersionUID = 1L;

    private int currentPageNumber = 1;

    private Editor editor;

    private JGraphInternalPane drawingPane;

    // private JScrollPane scrollPane;

    private Dimension defaultSize = new Dimension(6000, 6000);

    private ZoomAction zoomOut = new ZoomAction(0.9);
    private ZoomAction zoomIn = new ZoomAction(1.1);

    private List<String> segmentAreaBarcodes = new ArrayList<String>();

    private JPanel propertiesPanel;
    private JPanel topMarginPanel;
    private JPanel bottomMarginPanel;
    private JPanel leftMarginPanel;
    private JPanel rightMarginPanel;

    private DocumentPackage documentPackage;

    private FormRecognitionStructure formRecognitionStructure;

    private List<String> formFieldNames = new ArrayList<String>();

    private Map<String, String> recordMap;

    public JGraph() {
        this(new DefaultGraphModel());
    }

    public JGraph(GraphModel gm) {
        this(new Editor(gm, null));
    }

    public JGraph(Editor ed) {
        super(false); // not double buffered. I do my own flicker-free
        // redraw.
        editor = ed;
        editor.setGraph(this);
        Globals.curEditor(editor);

        try {
            documentPackage = new DocumentPackage();
        } catch (Exception e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }

        drawingPane = new JGraphInternalPane(editor);
        setDrawingSize(getDefaultSize());

        // scrollPane = new JScrollPane(drawingPane);

	/*
	scrollPane.setBorder(null);
	scrollPane.getHorizontalScrollBar().setUnitIncrement(25);
	scrollPane.getVerticalScrollBar().setUnitIncrement(25);
	 */

        editor.setJComponent(drawingPane);
        setLayout(new BorderLayout());
        add(drawingPane, BorderLayout.CENTER);
        // add(scrollPane, BorderLayout.CENTER);
        addMouseListener(editor);
        addMouseMotionListener(editor);
        addKeyListener(editor);
        // scrollPane.getHorizontalScrollBar().addAdjustmentListener(this);
        // scrollPane.getVerticalScrollBar().addAdjustmentListener(this);

        initKeys();

        validate();

        Collection<?> layerManagerContent = ed.getLayerManager().getContents();
        if (layerManagerContent != null) {
            updateDrawingSizeToIncludeAllFigs(Collections.enumeration(layerManagerContent));
        }

        // int mask = InputEvent.ALT_MASK | InputEvent.CTRL_MASK;
        // establishAlternateMouseWheelListener(null, mask);

        propertiesPanel = new JPanel();
        propertiesPanel.setBorder(null);
        propertiesPanel.setLayout(new BoxLayout(propertiesPanel, BoxLayout.Y_AXIS));

    }

    public void postSave() {
        Editor ce = getEditor();
        ce.resetEditorStateChangedFlag();
    }

    public DocumentAttributes getDocumentAttributes() {

        if (getDocument() == null) {
            return null;
        }

        return getDocument().getDocumentAttributes();
    }

    public PageAttributes getPageAttributes() {

        if (getDocument() == null) {
            return null;
        }

        if (getDocument().getPageByPageNumber(getCurrentPageNumber()) == null) {
            return null;
        }

        return getDocument().getPageByPageNumber(getCurrentPageNumber()).getPageAttributes();
    }

    public void setPageAttributes(PageAttributes pageAttributes) {
        if (pageAttributes instanceof PageAttributes) {
            getDocument().getPageByPageNumber(getCurrentPageNumber())
                .setPageAttributes(pageAttributes);
        }
    }

    public void updateUI() {
        super.updateUI();
        if (editor != null) {
            Dimension dim = getPreferredSize();
            editor.setNaturalGraphWidth((int) dim.getWidth());
            editor.setNaturalGraphHeight((int) dim.getHeight());
        }
    }

    public void paintComponent(Graphics g) {
        drawingPane.paintComponent(g);
    }

    public JPanel getPropertiesPanel() {
        return propertiesPanel;
    }

    @Override public void addMouseListener(MouseListener listener) {
        drawingPane.addMouseListener(listener);
    }

    @Override public void addMouseMotionListener(MouseMotionListener listener) {
        drawingPane.addMouseMotionListener(listener);
    }

    @Override public void addKeyListener(KeyListener listener) {
        drawingPane.addKeyListener(listener);
    }

    /**
     * Make a copy of this JGraph so that it can be shown in another window.
     */
    @Override public Object clone() {
        JGraph newJGraph = new JGraph((Editor) editor.clone());
        return newJGraph;
    }

    /* Set up some standard keystrokes and the Cmds that they invoke. */
    public void initKeys() {
        int shift = InputEvent.SHIFT_MASK;
        int alt = InputEvent.ALT_MASK;
        int meta = InputEvent.META_MASK;

        bindKey(new SelectNextAction("Select Next", true), KeyEvent.VK_TAB, 0);
        bindKey(new SelectNextAction("Select Previous", false), KeyEvent.VK_TAB, shift);

        bindKey(new NudgeAction(NudgeAction.LEFT), KeyEvent.VK_LEFT, 0);
        bindKey(new NudgeAction(NudgeAction.RIGHT), KeyEvent.VK_RIGHT, 0);
        bindKey(new NudgeAction(NudgeAction.UP), KeyEvent.VK_UP, 0);
        bindKey(new NudgeAction(NudgeAction.DOWN), KeyEvent.VK_DOWN, 0);

        bindKey(new NudgeAction(NudgeAction.LEFT, 8), KeyEvent.VK_LEFT, shift);
        bindKey(new NudgeAction(NudgeAction.RIGHT, 8), KeyEvent.VK_RIGHT, shift);
        bindKey(new NudgeAction(NudgeAction.UP, 8), KeyEvent.VK_UP, shift);
        bindKey(new NudgeAction(NudgeAction.DOWN, 8), KeyEvent.VK_DOWN, shift);

        bindKey(new NudgeAction(NudgeAction.LEFT, 18), KeyEvent.VK_LEFT, alt);
        bindKey(new NudgeAction(NudgeAction.RIGHT, 18), KeyEvent.VK_RIGHT, alt);
        bindKey(new NudgeAction(NudgeAction.UP, 18), KeyEvent.VK_UP, alt);
        bindKey(new NudgeAction(NudgeAction.DOWN, 18), KeyEvent.VK_DOWN, alt);

        bindKey(new SelectNearAction(SelectNearAction.LEFT), KeyEvent.VK_LEFT, meta);
        bindKey(new SelectNearAction(SelectNearAction.RIGHT), KeyEvent.VK_RIGHT, meta);
        bindKey(new SelectNearAction(SelectNearAction.UP), KeyEvent.VK_UP, meta);
        bindKey(new SelectNearAction(SelectNearAction.DOWN), KeyEvent.VK_DOWN, meta);

        bindKey(new DeleteFromModelAction("Delete"), KeyEvent.VK_DELETE, 0);


    }

    /**
     * Utility function to bind a keystroke to a Swing Action. Note that GEF
     * Cmds are subclasses of Swing's Actions.
     */
    public void bindKey(ActionListener action, int keyCode, int modifiers) {
        drawingPane.registerKeyboardAction(action, KeyStroke.getKeyStroke(keyCode, modifiers),
            WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    /**
     * Get the Editor that is being displayed
     */
    public Editor getEditor() {
        return editor;
    }

    /**
     * Enlarges the JGraphInternalPane dimensions as necessary to insure
     * that all the contained Figs are visible.
     */
    protected boolean updateDrawingSizeToIncludeAllFigs(Enumeration iter) {

        boolean sizeUpdated = false;

        if (iter == null) {
            return sizeUpdated;
        }

        Dimension drawingSize = new Dimension(defaultSize.width, defaultSize.height);

        if (getPageAttributes() != null) {
            drawingSize = new Dimension(getPageAttributes().getCroppedWidth(),
                getPageAttributes().getCroppedHeight());
        }
        while (iter.hasMoreElements()) {
            Fig fig = (Fig) iter.nextElement();

            if (fig instanceof FigBarcode) {
                if (((FigBarcode) fig).getRecognitionMarkerType() != FigBarcode.NOT_MARKER) {
                    continue;
                }
            }

            Rectangle rect = fig.getBounds();
            Point point = rect.getLocation();
            Dimension dim = rect.getSize();
            if ((point.x + dim.width) > drawingSize.width) {
                drawingSize.setSize(point.x + dim.width, drawingSize.height);
                sizeUpdated = true;
            }
            if ((point.y + dim.height) > drawingSize.height) {
                drawingSize.setSize(drawingSize.width, point.y + dim.height);
                sizeUpdated = true;
            }
        }
        setDrawingSize(drawingSize.width, drawingSize.height);
        setMinimumSize(drawingSize);
        setPreferredSize(drawingSize);
        updateUI();
        return sizeUpdated;
    }

    public void setDrawingSize(int width, int height) {
        setDrawingSize(new Dimension(width, height));
    }

    public void setDrawingSize(Dimension dim) {
        editor.drawingSizeChanged(dim);
    }

    /**
     * Set the GraphModel the Editor is using.
     */
    public void setGraphModel(GraphModel gm) {
        editor.setGraphModel(gm);
    }

    /**
     * Get the GraphModel the Editor is using.
     */
    public GraphModel getGraphModel() {
        return editor.getGraphModel();
    }

    /**
     * When the JGraph is hidden, hide its internal pane
     */
    @Override public void setVisible(boolean visible) {
        super.setVisible(visible);
        drawingPane.setVisible(visible);
        // if(editor.getActiveTextEditor() != null)
        // {
        // FigTextEditor.remove();
        // }
    }

    /**
     * Tell Swing/AWT that JGraph handles tab-order itself.
     */
    public boolean isFocusCycleRoot() {
        return true;
    }

    /**
     * Tell Swing/AWT that JGraph can be tabbed into.
     */
    @Override public boolean isFocusable() {
        return true;
    }

    // //////////////////////////////////////////////////////////////
    // events

    /**
     * Add listener to the objects to notify whenever the Editor changes its
     * current selection.
     */
    public void addGraphSelectionListener(GraphSelectionListener listener) {
        getEditor().addGraphSelectionListener(listener);
    }

    public void removeGraphSelectionListener(GraphSelectionListener listener) {
        getEditor().removeGraphSelectionListener(listener);
    }

    public void addModeChangeListener(ModeChangeListener listener) {
        getEditor().addModeChangeListener(listener);
    }

    public void removeModeChangeListener(ModeChangeListener listener) {
        getEditor().removeModeChangeListener(listener);
    }

    // //////////////////////////////////////////////////////////////
    // Editor facade

    /**
     * The JGraph is painted by simply painting its Editor.
     */
    // public void paint(Graphics g) { _editor.paint(getGraphics()); }
    // //////////////////////////////////////////////////////////////
    // selection methods

    /**
     * Add the given item to this Editor's selections.
     */
    public void select(Fig f) {
        if (f == null) {
            deselectAll();
        } else {
            editor.getSelectionManager().select(f);
        }
    }

    /**
     * Find the Fig that owns the given item and select it.
     */
    public void selectByOwner(Object owner) {
        Layer lay = editor.getLayerManager().getActiveLayer();
        if (lay instanceof LayerDiagram) {
            select(((LayerDiagram) lay).presentationFor(owner));
        }
    }

    /**
     * Find Fig that owns the given item, or the item if it is a Fig, and
     * select it.
     */
    public void selectByOwnerOrFig(Object owner) {
        if (owner instanceof Fig) {
            select((Fig) owner);
        } else {
            selectByOwner(owner);
        }
    }

    /**
     * Add the Fig that owns the given item to this Editor's selections.
     */
    public void selectByOwnerOrNoChange(Object owner) {
        Layer lay = editor.getLayerManager().getActiveLayer();
        if (lay instanceof LayerDiagram) {
            Fig f = ((LayerDiagram) lay).presentationFor(owner);
            if (f != null) {
                select(f);
            }
        }
    }

    /**
     * Remove the given item from this editors selections.
     */
    public void deselect(Fig f) {
        editor.getSelectionManager().deselect(f);
    }

    /**
     * Select the given item if it was not already selected, and
     * vis-a-versa.
     */
    public void toggleItem(Fig f) {
        editor.getSelectionManager().toggle(f);
    }

    /**
     * Deslect everything that is currently selected.
     */
    public void deselectAll() {
        editor.getSelectionManager().deselectAll();
    }

    /**
     * Select a collection of Figs.
     */
    public void select(Vector items) {
        editor.getSelectionManager().select(items);
    }

    /**
     * Toggle the selection of a collection of Figs.
     */
    public void toggleItems(Vector items) {
        editor.getSelectionManager().toggle(items);
    }

    /**
     * reply a Vector of all selected Figs. Used in many Cmds.
     */
    public Vector selectedFigs() {
        return editor.getSelectionManager().getFigs();
    }

    public void setDefaultSize(int width, int height) {
        defaultSize = new Dimension(width, height);
    }

    public void setDefaultSize(Dimension dim) {
        defaultSize = dim;
    }

    public Dimension getDefaultSize() {
        return defaultSize;
    }

    /** Get the position of the editor's scrollpane. */
    // public Point getViewPosition() {
    //return scrollPane.getViewport().getViewPosition();
    // }

    /** Set the position of the editor's scrollpane. */
    // public void setViewPosition(Point p) {
    // if (p != null) {
    // scrollPane.getViewport().setViewPosition(p);
    // }
    // }

    /**
     * Establishes alternate MouseWheelListener object that's only active
     * when the alt/shift/ctrl keys are held down.
     *
     * @param listener MouseWheelListener that will receive MouseWheelEvents
     *                 generated by this JGraph.
     * @param mask     logical OR of key modifier values as defined by
     *                 java.awt.event.KeyEvent constants. This has been
     *                 tested with ALT_MASK, SHIFT_MASK, and CTRL_MASK.
     */
    public void establishAlternateMouseWheelListener(MouseWheelListener listener, int mask) {

        WheelKeyListenerToggleAction keyListener =
            new WheelKeyListenerToggleAction(this.drawingPane, listener, mask);
        this.drawingPane.addKeyListener(keyListener);
    }

    public void adjustmentValueChanged(AdjustmentEvent e) {
        FigTextEditor.getInstance().endEditing();
        editor.damageAll();
    }

    /**
     * Zooms diagram in and out when mousewheel is rolled while holding down
     * ctrl and/or alt key. Alt, because alt + mouse motion pans the diagram &
     * zooming while panning makes more sense than scrolling while panning.
     * Ctrl, because Ctrl/+ and Ctrl/- are used to zoom using the keyboard.
     */
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.isAltDown() || e.isControlDown()) {

            if (e.getWheelRotation() < 0) {
                this.zoomOut.actionPerformed(null);
            } else if (e.getWheelRotation() > 0) {
                this.zoomIn.actionPerformed(null);
            }

            e.consume();
        }
    }

    public JPanel getTopMarginPanel() {
        return topMarginPanel;
    }

    public void setTopMarginPanel(JPanel topMarginPanel) {
        this.topMarginPanel = topMarginPanel;
    }

    public JPanel getBottomMarginPanel() {
        return bottomMarginPanel;
    }

    public void setBottomMarginPanel(JPanel bottomMarginPanel) {
        this.bottomMarginPanel = bottomMarginPanel;
    }

    public JPanel getLeftMarginPanel() {
        return leftMarginPanel;
    }

    public void setLeftMarginPanel(JPanel leftMarginPanel) {
        this.leftMarginPanel = leftMarginPanel;
    }

    public JPanel getRightMarginPanel() {
        return rightMarginPanel;
    }

    public void setRightMarginPanel(JPanel rightMarginPanel) {
        this.rightMarginPanel = rightMarginPanel;
    }

    public boolean updateGraphBoundaries() {

        boolean drawingSizeModified = false;

        Collection layerManagerContent = editor.getLayerManager().getContents();
        if (layerManagerContent != null && getPageAttributes() != null) {
            drawingSizeModified =
                updateDrawingSizeToIncludeAllFigs(Collections.enumeration(layerManagerContent));
        }

        return drawingSizeModified;

    }

    public FormRecognitionStructure getFormRecognitionStructure() {
        if (formRecognitionStructure == null) {
            formRecognitionStructure = new FormRecognitionStructure();
        }
        return formRecognitionStructure;
    }

    public void setFormRecognitionStructure(FormRecognitionStructure formRecognitionStructure) {
        this.formRecognitionStructure = formRecognitionStructure;
    }

    public void setDocument(Document document) {
        if (documentPackage == null) {
            return;
        }
        documentPackage.setDocument(document);
    }

    public Document getDocument() {
        if (documentPackage == null) {
            return null;
        }
        return documentPackage.getDocument();
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    public void setCurrentPageNumber(int currentPageNumber) {
        this.currentPageNumber = currentPageNumber;
    }

    public void createDocument(PageAttributes newPageAttributes) {
        Document newDocument = new Document();
        createPage(newDocument, newPageAttributes);
        setDocument(newDocument);
    }

    public void createPage(Document newDocument, PageAttributes newPageAttributes) {
        Page newPage = new Page();
        newPage.setPageAttributes(newPageAttributes);
        newDocument.addPage(newPage);
    }

    public void insertPage(Document newDocument, PageAttributes newPageAttributes,
        int insertAtIndex) {
        Page newPage = new Page();
        newPage.setPageAttributes(newPageAttributes);
        newDocument.addPageAtIndex(newPage, insertAtIndex);
    }

    public void setDocumentAttributes(DocumentAttributes documentAttributes) {
        if (documentAttributes instanceof DocumentAttributes) {
            getDocument().setDocumentAttributes(documentAttributes);
        }
    }

    public DocumentPackage getDocumentPackage() {
        return documentPackage;
    }

    public List<String> getSegmentAreaBarcodes() {
        return segmentAreaBarcodes;
    }

    public void setSegmentAreaBarcodes(List<String> segmentAreaBarcodes) {
        this.segmentAreaBarcodes = segmentAreaBarcodes;
    }

    public List<String> getFormFieldNames() {
        return formFieldNames;
    }

    public void setFormFieldNames(Vector<String> formFieldNames) {
        this.formFieldNames = formFieldNames;
    }

    public Map<String, String> getRecordMap() {
        if (recordMap == null) {
            recordMap = new HashMap<String, String>();
        }
        return recordMap;
    }

    public void setRecordMap(Map<String, String> recordMap) {
        this.recordMap = recordMap;
    }

}


class JGraphInternalPane extends JPanel {

    private Editor _editor;

    private boolean registeredWithTooltip;

    public JGraphInternalPane(Editor e) {
        _editor = e;
        setLayout(null);
        setDoubleBuffered(false);
    }

    @Override public void paintComponent(Graphics g) {
        _editor.paint(g);
    }

    @Override public Graphics getGraphics() {
        Graphics res = super.getGraphics();
        if (res == null) {
            return res;
        }
        Component parent = getParent();

        if (parent instanceof JViewport) {
            JViewport view = (JViewport) parent;
            Rectangle bounds = view.getBounds();
            Point pos = view.getViewPosition();
            res.clipRect(bounds.x + pos.x - 1, bounds.y + pos.y - 1, bounds.width + 1,
                bounds.height + 1);
        }
        return res;
    }

    @Override public Point getToolTipLocation(MouseEvent event) {
        event = Globals.curEditor().retranslateMouseEvent(event);
        return (super.getToolTipLocation(event));
    }

    @Override public void setToolTipText(String text) {
        if ("".equals(text)) {
            text = null;
        }
        putClientProperty(TOOL_TIP_TEXT_KEY, text);
        ToolTipManager toolTipManager = ToolTipManager.sharedInstance();
        // if (text != null) {
        if (!registeredWithTooltip) {
            toolTipManager.registerComponent(this);
            registeredWithTooltip = true;
        }
    }

    @Override protected void processMouseEvent(MouseEvent e) {
        if (e.getID() == MouseEvent.MOUSE_PRESSED) {
            requestFocus();
        }

        super.processMouseEvent(e);
    }


    /**
     * Tell Swing/AWT that JGraph handles tab-order itself.
     */
    public boolean isFocusCycleRoot() {
        return true;
    }

    /**
     * Tell Swing/AWT that JGraph can be tabbed into.
     */
    @Override public boolean isFocusable() {
        return true;
    }

    static final long serialVersionUID = -5067026168452437942L;

}


class WheelKeyListenerToggleAction implements KeyListener {

    private int mask;
    private int down;

    private MouseWheelListener listener;
    private JPanel panel;

    /**
     * Creates KeyListener that adds and removes MouseWheelListener from
     * indicated JPanel so that it's only active when the modifier keys
     * (indicated by modifiersMask) are held down. Otherwise, the scrollbars
     * automatically managed by the JScrollPanel would never see the wheel
     * events.
     *
     * @param panel         JPanel object that will be listening for
     *                      MouseWheelEvents on demand.
     * @param listener      MouseWheelListener that listens for MouseWheelEvents
     * @param modifiersMask the logical OR of the AWT modifier keys values defined
     *                      as constants by the KeyEvent class. This has been
     *                      tested with ALT_MASK, CTRL_MASK, and SHIFT_MASK.
     */
    public WheelKeyListenerToggleAction(JPanel panel, MouseWheelListener listener,
        int modifiersMask) {
        this.panel = panel;
        this.listener = listener;
        this.mask = modifiersMask;
    }

    public synchronized void keyPressed(KeyEvent e) {
        if ((e.getModifiers() | mask) != mask) {
            return;
        }

        if (down == 0) {
            // panel.addMouseWheelListener(listener);
        }
        down |= e.getModifiers();
    }

    public synchronized void keyReleased(KeyEvent e) {
        if ((e.getModifiers() & mask) == 0) {
            // panel.removeMouseWheelListener(listener);
        }
        down = e.getModifiers();
    }

    public void keyTyped(KeyEvent e) {
    }
}

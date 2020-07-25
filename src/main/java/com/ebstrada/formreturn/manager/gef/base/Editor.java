package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import com.ebstrada.formreturn.manager.gef.event.GraphSelectionListener;
import com.ebstrada.formreturn.manager.gef.event.ModeChangeListener;
import com.ebstrada.formreturn.manager.gef.graph.GraphModel;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigTextEditor;
import com.ebstrada.formreturn.manager.gef.presentation.FigTextLayoutEditorComponent;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.ui.reprocessor.frame.ReprocessorFrame;
import com.ebstrada.formreturn.manager.ui.tab.DesktopTabbedPane;

/**
 * This class provides an editor for manipulating graphical documents. The
 * editor is the central class of the graph editing framework, but it does not
 * contain very much code. It can be this small because all the net-level
 * models, graphical objects, layers, editor modes, editor commands, and
 * supporting dialogs and frames are implemented in their own classes.
 * <p>
 * <p>
 * An Editor's LayerManager has a stack of Layer's. Normally Layers contain
 * Figs. Some Figs are linked to NetPrimitives. When Figs are selected the
 * SelectionManager holds a Selection object. The behavior of the Editor is
 * determined by its current Mode. The Editor's ModeManager keeps track of all
 * the active Modes. Modes interpert user input events and decide how to change
 * the state of the diagram. The Editor acts as a shell for executing Commands
 * that modify the document or the Editor itself.
 * <p>
 * <p>
 * When Figs change visible state (e.g., color, size, or postition) they tell
 * their Layer that they are damageAll and need to be repainted. The Layer tells
 * all Editors that are editing that
 * <p>
 * A major goal of GEF is to make it easy to extend the framework for
 * application to a specific domain. It is very important that new functionality
 * can be added without modifying what is already there. The fairly small size
 * of the Editor is a good indicator that it is not a bottleneck for enhancing
 * the framework.
 * <p>
 *
 * @see Layer
 * @see Fig
 * @see com.ebstrada.formreturn.manager.gef.graph.presentation.NetPrimitive
 * @see Selection
 * @see Mode
 * @see Cmd
 */

public class Editor implements Serializable, MouseListener, MouseMotionListener, KeyListener {
    // //////////////////////////////////////////////////////////////
    // constants

    /**
     *
     */
    private static final long serialVersionUID = 2324579872610012639L;

    /**
     * Clicking exactly on a small shape is hard for users to do. GRIP_MARGIN
     * gives them a chance to have the mouse outside a Fig by a few pixels and
     * still hit it.
     */
    public static final double GRIP_SIZE = 10d;

    // //////////////////////////////////////////////////////////////
    // instance variables

    /**
     * The user interface mode that the Editor is currently in. Generally Modes
     * that the user has to think about are a bad idea. But even in a very easy
     * to use editor there are plenty of "spring-loaded" modes that change the
     * way the system interprets input. For example, when placing a new node,
     * the editor is in ModePlace, and when dragging a handle of an object the
     * editor is in ModeModify. In each case moving or dragging the mouse has a
     * different effect.
     *
     * @see ModeModify
     * @see ModeSelect
     * @see ModePlace
     */
    protected ModeManager _modeManager = new ModeManager(this);

    /**
     * This points to the document object that the user is working on. At this
     * point the framework does not have a very strong concept of document and
     * there is no class Document. For now the meaning of this pointer is in the
     * hands of the person applying this framework to an application.
     */
    protected Object _document;

    /**
     * All the selection objects for what the user currently has selected.
     */
    protected SelectionManager _selectionManager = new SelectionManager(this);

    /**
     * The LayerManager for this Editor.
     */
    protected LayerManager _layerManager = new LayerManager(this);

    /**
     * The grid to snap points to.
     */
    protected Guide _guide = new GuideGrid(16);

    /**
     * The Fig that the mouse is in.
     */
    private Fig _curFig = null;

    /**
     * The Selection object that the mouse is in.
     */
    private Selection _curSel = null;

    /**
     * The scale at which to draw the diagram
     */
    private double _scale = 1.0;

    /*
     * The undo manager for this editor
     */
    private transient UndoManager undoManager = new UndoManager();

    /**
     * Should elements in this editor be selectable?
     */
    protected boolean _canSelectElements = true;

    /**
     * Has the editor state changed?
     */
    private boolean _hasEditorStateChanged = false;

    /**
     * The swing panel that the Editor draws to.
     */
    private transient JComponent _jComponent;

    /**
     * The graph panel
     */
    private transient JGraph _graph;
    private transient int _naturalGraphWidth;
    private transient int _naturalGraphHeight;

    /**
     * The width of the swing panel before scaling.
     */
    private transient int _naturalComponentWidth;

    /**
     * The height of the swing panel before scaling.
     */
    private transient int _naturalComponentHeight;

    /**
     * The ancestor of _jComponent that has a peer that can create an image.
     */
    private transient Component _peer_component = null;

    private RenderingHints _renderingHints = new RenderingHints(null);

    // private static Log LOG = LogFactory.getLog(Editor.class);

    private FigTextEditor _activeTextEditor = null;

    private HashMap<Fig, PropertyChangeListener> _pcListeners =
        new HashMap<Fig, PropertyChangeListener>();

    private Layer editorGrid;

    private boolean gridHidden = false;

    private boolean snapToGrid = true;

    private Point lastMousePosition = new Point(0, 0);

    // //////////////////////////////////////////////////////////////
    // constructors and related functions

    /**
     * Construct a new Editor to edit the given NetList
     */
    public Editor(GraphModel gm, JComponent jComponent) {
        this(gm, jComponent, null);
    }

    public Editor(GraphModel gm) {
        this(gm, null, null);
    }

    public Editor() {
        this(null, null, null);
    }

    public Editor(GraphModel gm, JComponent jComponent, Layer lay) {

        /*
         * Set the instance of the undo manager of this editor first
         */
        setAsActiveUndoManager();

        _jComponent = jComponent;
        defineLayers(gm, lay);

        pushMode(new ModeSelect(this));
        pushMode(new ModePopup(this));
        pushMode(new ModeDragScroll(this));
        Globals.curEditor(this);

        _renderingHints
            .put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        _renderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);

        setGridHidden(true);
        setSnapToGrid(false);

    }

    public void setAntiAlias(boolean b) {
        if (b == true) {
            _renderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        } else {
            _renderingHints
                .put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        }
    }

    protected void defineLayers(GraphModel gm, Layer lay) {
        _layerManager.addLayer(new LayerGrid());
        // _layerManager.addLayer(new LayerPageBreaks());
        // the following line is an example of another "grid"
        // _layerManager.addLayer(new LayerPolar());
        if (lay != null) {
            _layerManager.addLayer(lay);
        } else if (gm == null) {
            _layerManager.addLayer(new LayerDiagram("Example"));
        } else {
            _layerManager.addLayer(new LayerPerspective("canvas", gm));
        }
    }

    /**
     * Called before the Editor is saved to a file.
     */
    public void preSave() {
        _layerManager.preSave();
    }

    /**
     * Called after the Editor is saved to a file.
     */
    public void postSave() {
        _layerManager.postSave();
    }

    /**
     * Called after the Editor is loaded from a file.
     */
    public void postLoad() {
        _layerManager.postLoad();
    }

    /**
     * Return true if the Grid layer is currently hidden.
     */
    public boolean getGridHidden() {
        return gridHidden;
    }

    /**
     * Set the hidden state of the Grid layer.
     */
    public void setGridHidden(boolean b) {
        if (editorGrid == null) {
            editorGrid = getLayerManager().findLayerNamed("Grid");
        }
        if (editorGrid != null) {
            if (b == true) {
                Layer newGrid = new LayerGrid(Color.white, Color.white, 8, true);
                getLayerManager().replaceLayer(editorGrid, newGrid);
            } else {
                getLayerManager()
                    .replaceLayer(getLayerManager().findLayerNamed("Grid"), editorGrid);
            }
            gridHidden = b;
        }
    }

    /**
     * Clone the receiving editor. Called from ActionSpawn. Subclasses of Editor
     * should override this method. TODO shouldn't this just call super.clone()
     * instead of using reflection? Bob 29 Jan 2004
     */
    @Override public Object clone() {
        try {
            Editor ed = this.getClass().newInstance();
            ed.getLayerManager().addLayer(_layerManager.getActiveLayer());
            // needs-more-work: does not duplicate layer stack!
            ed.document(document());
            return ed;
        } catch (java.lang.IllegalAccessException ignore) {
            // LOG.error("IllegalAccessException in spawn");
        } catch (java.lang.InstantiationException ignore) {
            // LOG.error("InstantiationException in spawn");
        }
        return null;
    }

    // //////////////////////////////////////////////////////////////
    // / methods related to editor state: graphical attributes, modes, view

    public ModeManager getModeManager() {
        return _modeManager;
    }

    /**
     * Pushes a new mode to the mode manager
     */
    public void pushMode(FigModifyingMode mode) {
        _modeManager.push(mode);
        mode.setEditor(this);
        // Globals.showStatus(mode.instructions());
    }

    /**
     * Set this Editor's current Mode to the next global Mode.
     */
    public void finishMode() {
        _modeManager.pop();
        pushMode((FigModifyingMode) Globals.mode());
        Globals.clearStatus();
    }

    /**
     * Return the LayerComposite that holds the diagram being edited.
     */
    public LayerManager getLayerManager() {
        return _layerManager;
    }

    public double getScale() {
        return _scale;
    }

    /**
     * Set this Editor's drawing scale. A value of 1.0 draws at 1 to 1. A value
     * greater than 1 draws larger, less than 1 draws smaller. Conceptually the
     * scale is an attribute of JGraph, but the editor needs to know it to paint
     * accordingly.
     */
    public void setScale(double scale) {
        _scale = scale;
        _hitRect = new Rectangle(0, 0, getGripSize(), getGripSize());
        _layerManager.setScale(_scale);
        _jComponent.setPreferredSize(new Dimension((int) (_naturalComponentWidth * _scale),
            (int) (_naturalComponentHeight * _scale)));
        _graph.setPreferredSize(new Dimension((int) (_naturalGraphWidth * _scale),
            (int) (_naturalGraphHeight * _scale)));
        damageAll();
    }

    /**
     * Returns this Editor's current value for the selection flag.
     *
     * @return The current value of the selection flag.
     */
    public boolean canSelectElements() {
        return _canSelectElements;
    }

    /**
     * Set's the selection flag for the Editor. If the flag is set to true
     * (default), elements in this Editor are selectable. Otherwise, elements
     * are not selectable, neither by keyboard nor by mouse activity.
     *
     * @param selectable New value for the flag.
     */
    public void setElementsSelectable(boolean selectable) {
        _canSelectElements = selectable;
    }

    public JGraph getGraph() {
        return _graph;
    }

    /**
     * Return the net under the diagram being edited.
     */
    public GraphModel getGraphModel() {
        Layer active = _layerManager.getActiveLayer();
        if (active instanceof LayerPerspective) {
            return ((LayerPerspective) active).getGraphModel();
        }
        return null;
    }

    public void setGraphModel(GraphModel gm) {
        Layer active = _layerManager.getActiveLayer();
        if (active instanceof LayerPerspective) {
            ((LayerPerspective) active).setGraphModel(gm);
        }
    }

    /**
     * Scroll the JGraph so that the given point is visible. This is used when
     * the user wants to drag an object a long distance.
     */
    public void scrollToShow(final int x, final int y) {

        // Segment ViewPort

        final Component segmentViewPort = getJComponent().getParent().getParent();
        if (segmentViewPort != null && segmentViewPort.getParent() instanceof JViewport) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ((JComponent) segmentViewPort).scrollRectToVisible(
                        new Rectangle((int) (((double) x * _scale) - 10),
                            (int) (((double) y * _scale) - 10), 20, 20));
                }
            });
        }

        // FormFrame ViewPort is one more level up

        final Component formFrameViewPort =
            getJComponent().getParent().getParent().getParent().getParent();
        if (formFrameViewPort != null && formFrameViewPort.getParent() instanceof JViewport) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    ((JComponent) formFrameViewPort).scrollRectToVisible(
                        new Rectangle((int) (((double) x * _scale) - 10),
                            (int) (((double) y * _scale) - 10), 20, 20));
                }
            });
        }

    }

    // //////////////////////////////////////////////////////////////
    // methods related to adding, removing, and accessing Figs
    // shown in the editor

    /**
     * Returns a collection of all Figs in the layer currently being edited.
     */
    public Enumeration figs() {
        return _layerManager.elements();
    }

    /**
     * Add a Fig to the diagram being edited.
     */
    public void addNoEventChange(Fig f) {
        getLayerManager().add(f);
    }

    public void add(Fig f) {
        addPropertyChangeListener(f);
        getLayerManager().add(f);
    }

    public void add(Fig f, Layer l) {
        addPropertyChangeListener(f);
        l.add(f);
    }

    public void removeNoEventChange(Fig f) {
        getLayerManager().remove(f);
    }

    /**
     * Remove a Fig from the diagram being edited.
     */
    public void remove(Fig f) {
        removePropertyChangeListener(f);
        getLayerManager().remove(f);
    }

    public boolean hasEditorStateChanged() {
        return _hasEditorStateChanged;
    }

    public void resetEditorStateChangedFlag() {
        _hasEditorStateChanged = false;
    }

    public void addPropertyChangeListener(Fig fig) {
        PropertyChangeListener pcl = _pcListeners.get(fig);
        if (pcl == null) {
            pcl = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent pce) {
                    if (_hasEditorStateChanged == false) {
                        updateEditorTitle();
                    }
                    _hasEditorStateChanged = true;
                    Fig fig = (Fig) pce.getSource();
                    checkFigBounds(fig);
                }
            };
            fig.addPropertyChangeListener(pcl);
            _pcListeners.put(fig, pcl);

        }
    }

    public void updateEditorTitle() {

        DesktopTabbedPane desktopTabbedPane = getDesktopTabbedPane();

        if (desktopTabbedPane == null) {
            return;
        }

        int selectedIndex = desktopTabbedPane.getSelectedIndex();

        String currentTitle = desktopTabbedPane.getTitleAt(selectedIndex);

        if (currentTitle.trim().endsWith("*")) {
            return;
        }

        desktopTabbedPane.setTitleAt(selectedIndex, currentTitle + "*");

    }

    public DesktopTabbedPane getDesktopTabbedPane() {

        if (getGraph() == null) {
            return null;
        }

        Component parent = getGraph().getParent();

        while (parent != null) {

            if (parent instanceof DesktopTabbedPane) {
                return (DesktopTabbedPane) parent;
            }
            parent = parent.getParent();
        }

        return null;

    }

    /*
     * This code is run when a fig's property change listener is fired.
     */
    public void checkFigBounds(Fig fig) {

        int x = fig._x;
        int y = fig._y;
        int width = fig.getWidth();
        int height = fig.getHeight();

        int x2 = x + width;
        int y2 = y + height;

        PageAttributes pageAttributes = getPageAttributes();
        int pageWidth = pageAttributes.getCroppedWidth();
        int pageHeight = pageAttributes.getCroppedHeight();

        if (height > pageHeight) {
            height = pageHeight;
            fig.setHeight(height);
        }

        if (width > pageWidth) {
            width = pageWidth;
            fig.setWidth(width);
        }

        // PAGE WIDTH

        // LESS THAN 0
        if (x < 0) {
            fig._x = 0;
        }

        // GREATER THAN WIDTH
        if (x2 > pageWidth) {
            fig._x = pageWidth - width;
        }

        // PAGE HEIGHT

        // LESS THAN 0
        if (y < 0) {
            fig._y = 0;
        }

        // GREATER THAN HEIGHT
        if (y2 > pageHeight) {
            fig._y = pageHeight - height;
        }

        fig.damage();

    }

    public PageAttributes getPageAttributes() {
        return getGraph().getPageAttributes();
    }

    public void removePropertyChangeListener(Fig fig) {
        PropertyChangeListener pcl = _pcListeners.get(fig);
        if (pcl != null) {
            fig.removePropertyChangeListener(pcl);
            _pcListeners.remove(fig);
        }
    }

    /**
     * Temp var used to implement hit() without doing memory allocation.
     */
    protected Rectangle _hitRect = new Rectangle(0, 0, getGripSize(), getGripSize());

    private ReprocessorFrame reprocessorFrame;

    /**
     * Reply the top Fig in the current layer that contains the given point.
     * This is used in determining what the user clicked on, among other uses.
     */
    public final Fig hit(Point p) {
        _hitRect.setLocation(p.x - getGripSize() / 2, p.y - getGripSize() / 2);
        return hit(_hitRect);
    }

    protected int getGripSize() {
        return (int) Math.ceil(GRIP_SIZE / _scale);
    }

    public final Fig hit(int x, int y) {
        _hitRect.setLocation(x - getGripSize() / 2, y - getGripSize() / 2);
        return hit(_hitRect);
    }

    public final Fig hit(int x, int y, int w, int h) {
        return hit(new Rectangle(x, y, w, h));
    }

    /**
     * Reply the top Fig in the current layer that contains the given rectangle.
     * This is called by all other hit methods.
     */
    public Fig hit(Rectangle r) {
        Fig f = getLayerManager().hit(r);
        return f;
    }

    /**
     * Find the Fig under the mouse, and the node it represents, if any
     */
    protected void setUnderMouse(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        Fig f = hit(x, y);
        if (f != _curFig) {
            if (_curFig instanceof MouseListener) {
                ((MouseListener) _curFig).mouseExited(me);
            }
            if (f instanceof MouseListener) {
                ((MouseListener) f).mouseEntered(me);
            }
        }
        _curFig = f;

        if (_canSelectElements) {
            Selection sel = _selectionManager.findSelectionAt(x, y);
            if (sel != _curSel) {
                if (_curSel != null) {
                    _curSel.mouseExited(me);
                }
                if (sel != null) {
                    sel.mouseEntered(me);
                }
            }
            _curSel = sel;
        }
    }

    // //////////////////////////////////////////////////////////////
    // document related methods

    /**
     * Get and set document being edited. There are no deep semantics here yet,
     * a "document" is up to you to define.
     */
    public Object document() {
        return _document;
    }

    public void document(Object d) {
        _document = d;
    }

    // //////////////////////////////////////////////////////////////
    // Guide and layout related commands

    /**
     * Modify the given point to be on the guideline (In this case, a gridline).
     */
    public void snap(Point p) {
        if (_guide != null && snapToGrid) {
            _guide.snap(p);
        }
    }

    public Guide getGuide() {
        return _guide;
    }

    public void setGuide(Guide g) {
        _guide = g;
    }

    // //////////////////////////////////////////////////////////////
    // recording damage to the display for later repair

    /**
     * Calling any one of the following damageAll() methods adds a damageAll
     * region (rectangle) that will be redrawn asap.
     */
    public void damaged(Rectangle r) {
        damaged(r.x, r.y, r.width, r.height);
    }

    /**
     * Calling any one of the following damageAll() methods adds a damageAll
     * region (rectangle) that will be redrawn asap. The given bounds must
     * already be scaled accordingly.
     */
    public void damaged(int x, int y, int width, int height) {
        getJComponent().repaint(0, x, y, width, height);
    }

    /**
     * This method will take the current scale into account
     *
     * @param sel
     */
    public void damaged(Selection sel) {
        Rectangle bounds = sel.getBounds();
        scaleRect(bounds);

        damaged(bounds);
    }

    public void damaged(Fig f) {
        // - if (_redrawer == null) _redrawer = new RedrawManager(this);
        // the line above should not be needed, but without it I get
        // NullPointerExceptions...
        // - if (f != null) _redrawer.add(f);
        (getJComponent()).repaint();
    }

    public void scaleRect(Rectangle bounds) {
        bounds.x = (int) Math.floor(bounds.x * _scale);
        bounds.y = (int) Math.floor(bounds.y * _scale);
        bounds.width = (int) Math.floor(bounds.width * _scale) + 1;
        bounds.height = (int) Math.floor(bounds.height * _scale) + 1;
    }

    /**
     * Mark the entire visible area of this Editor as damageAll. Currently
     * called when a LayerGrid is adjusted. This will be useful for
     * ActionRefresh if I get around to it. Also some Actions may perfer to do
     * this instead of keeping track of all modified objects, but only in cases
     * where most of the visible area is expected to change anyway.
     */
    public void damageAll() {
        Rectangle r = _jComponent.getVisibleRect();
        _jComponent.revalidate();
        _jComponent.repaint(r.x, r.y, r.width, r.height);
    }

    // //////////////////////////////////////////////////////////////
    // display methods

    /**
     * Paints the graphs nodes by calling paint() on layers, selections, and
     * mode.
     */
    public void paint(Graphics g) {
	
	/*
	if ( !(SwingUtilities.isEventDispatchThread()) ) {
	    System.out.println("I AM NOT THE EVENT DISPATCHING THREAD! I AM: " + Thread.currentThread().getName());
	}
	*/

        if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHints(_renderingHints);
            g2.scale(_scale, _scale);
        }
        getLayerManager().paint(g);
        // getLayerManager().getActiveLayer().paint(g);
        if (_canSelectElements) {
            _selectionManager.paint(g);
            _modeManager.paint(g);
        }
    }

    /**
     * Reply the current SelectionManager of this Editor.
     */
    public SelectionManager getSelectionManager() {
        return _selectionManager;
    }

    public Fig getCurrentFig() {
        return _curFig;
    }

    // //////////////////////////////////////////////////////////////
    // Frame and panel related methods

    public JComponent getJComponent() {
        return _jComponent;
    }

    public void setJComponent(JComponent c) {
        _jComponent = c;
        _peer_component = null;
    }

    public void setCursor(Cursor c) {
        if (getJComponent() != null) {
            getJComponent().setCursor(c);
            java.awt.Toolkit.getDefaultToolkit().sync();
        }
    }

    /**
     * Find the AWT Frame that this Editor is being displayed in. This is needed
     * to open a dialog box.
     */
    public Frame findFrame() {
        Component c = _jComponent;
        while (c != null && !(c instanceof Frame)) {
            c = c.getParent();
        }
        return (Frame) c;
    }

    /**
     * Create an Image (an off-screen bit-map) to be used to reduce flicker in
     * redrawing.
     * <p>
     * <p>
     * The image is also useable for other purposes, e.g. to put a bitmap of a
     * diagram on the system clipboard.
     */
    public Image createImage(int w, int h) {
        if (_jComponent == null) {
            return null;
        }
        if (_peer_component == null) {
            _peer_component = _jComponent;
            while (_peer_component instanceof JComponent) {
                _peer_component = _peer_component.getParent();
            }
        }
        // try { if (_jComponent.getPeer() == null) _jComponent.addNotify(); }
        // catch (java.lang.NullPointerException ignore) { }
        // This catch works around a bug:
        // Sometimes there is an exception in the AWT peer classes,
        // but the next line should still work, despite the exception
        return _peer_component.createImage(w, h);
    }

    /**
     * Get the backgrund color of the Editor. Often, none of the background will
     * be visible because LayerGrid covers the entire drawing area.
     */
    public Color getBackground() {
        if (_jComponent == null) {
            return Color.white;
        }
        return _jComponent.getBackground();
    }

    public void setActiveTextEditor(FigTextEditor fte) {
        FigTextEditor oldTextEditor = _activeTextEditor;
        _activeTextEditor = fte;
        if (oldTextEditor != null) {
            oldTextEditor.endEditing();
        }
    }

    public FigTextEditor getActiveTextEditor() {
        if (_activeTextEditor != null) {
            return FigTextLayoutEditorComponent.getActiveTextEditor();
        } else {
            return null;
        }
    }

    /**
     * This method is called when the Editor is notified that the drawing
     * panel's natural size has changed, typically because a new diagram has
     * been set.
     */
    public void drawingSizeChanged(Dimension dim) {
        _naturalComponentWidth = dim.width;
        _naturalComponentHeight = dim.height;
        if (_jComponent != null) {
            _jComponent.setPreferredSize(new Dimension((int) (_naturalComponentWidth * _scale),
                (int) (_naturalComponentHeight * _scale)));
            _jComponent.revalidate();
        }
    }

    // //////////////////////////////////////////////////////////////
    // event handlers

    /**
     * Remember to notify listener whenever the selection changes.
     */
    public void addGraphSelectionListener(GraphSelectionListener listener) {
        _selectionManager.addGraphSelectionListener(listener);
    }

    /**
     * Stop notifing listener of selection changes.
     */
    public void removeGraphSelectionListener(GraphSelectionListener listener) {
        _selectionManager.removeGraphSelectionListener(listener);
    }

    /**
     * Remember to notify listener whenever the mode changes.
     */
    public void addModeChangeListener(ModeChangeListener listener) {
        _modeManager.addModeChangeListener(listener);
    }

    /**
     * Stop notifing listener of mode changes.
     */
    public void removeModeChangeListener(ModeChangeListener listener) {
        _modeManager.removeModeChangeListener(listener);
    }

    public Point getLastMousePosition() {
        return lastMousePosition;
    }

    /**
     * Scales the mouse coordinates (which match the drawing scale) back to the
     * model scale.
     */
    protected MouseEvent translateMouseEvent(MouseEvent me) {
        double xp = me.getX();
        double yp = me.getY();

        this.lastMousePosition = me.getPoint();

        me.translatePoint((int) Math.round((xp / _scale) - me.getX()),
            (int) Math.round((yp / _scale) - me.getY()));
        return me;
    }

    /**
     * Scales the mouse coordinates (which match the model scale) back to the
     * drawing scale.
     */
    public MouseEvent retranslateMouseEvent(MouseEvent me) {
        double xp = me.getX();
        double yp = me.getY();
        int dx = (int) (xp * _scale - xp);
        int dy = (int) (yp * _scale - yp);
        me.translatePoint(dx, dy);
        return me;
    }

    /**
     * Invoked after the mouse has been pressed and released. All events are
     * passed on the SelectionManager and then ModeManager.
     */
    public void mouseClicked(MouseEvent me) {
        translateMouseEvent(me);
        Globals.curEditor(this);

        setUnderMouse(me);
        if (_curFig instanceof MouseListener) {
            ((MouseListener) _curFig).mouseClicked(me);
        }
        if (_canSelectElements) {
            _selectionManager.mouseClicked(me);
            _modeManager.mouseClicked(me);
        }
    }

    /**
     * Invoked when a mouse button has been pressed.
     */
    public void mousePressed(MouseEvent me) {
        if (me.isConsumed()) {
            // if (LOG.isDebugEnabled()) {
            // LOG
            //	.debug("MousePressed detected but rejected as already consumed");
            // }
            return;
        }
        translateMouseEvent(me);
        // FigTextEditor.remove();

        Globals.curEditor(this);
        setUnderMouse(me);
        if (_curFig instanceof MouseListener) {
            ((MouseListener) _curFig).mousePressed(me);
        }
        if (_canSelectElements) {
            _selectionManager.mousePressed(me);
            _modeManager.mousePressed(me);
        }
    }

    /**
     * Invoked when a mouse button has been released.
     */
    public void mouseReleased(MouseEvent me) {
        translateMouseEvent(me);
        Globals.curEditor(this);

        if (_curFig instanceof MouseListener) {
            ((MouseListener) _curFig).mouseReleased(me);
        }
        if (_canSelectElements) {
            _selectionManager.mouseReleased(me);
            _modeManager.mouseReleased(me);
        }
    }

    /**
     * Invoked when the mouse enters the Editor.
     */
    public void mouseEntered(MouseEvent me) {
        translateMouseEvent(me);
        Globals.curEditor(this);
        pushMode((FigModifyingMode) Globals.mode());
        setUnderMouse(me);
        if (_canSelectElements) {
            _modeManager.mouseEntered(me);
        }
    }

    /**
     * Invoked when the mouse exits the Editor.
     */
    public void mouseExited(MouseEvent me) {
        translateMouseEvent(me);
        setUnderMouse(me);
        if (_curFig instanceof MouseListener) {
            ((MouseListener) _curFig).mouseExited(me);
        }
    }

    /**
     * Invoked when a mouse button is pressed in the Editor and then dragged.
     * Mouse drag events will continue to be delivered to the Editor where the
     * first originated until the mouse button is released (regardless of
     * whether the mouse position is within the bounds of the Editor). BTW, this
     * makes drag and drop editing almost impossible.
     */
    public void mouseDragged(MouseEvent me) {
        translateMouseEvent(me);
        Globals.curEditor(this);
        setUnderMouse(me);
        if (_canSelectElements) {
            _selectionManager.mouseDragged(me);
            _modeManager.mouseDragged(me);
        }
    }

    /**
     * Invoked when the mouse button has been moved (with no buttons no down).
     */
    public void mouseMoved(MouseEvent me) {
        translateMouseEvent(me);
        Globals.curEditor(this);
        // setUnderMouse(me);

        if (_canSelectElements) {
            _selectionManager.mouseMoved(me);
            _modeManager.mouseMoved(me);
        }
    }

    /**
     * Invoked when a key has been pressed and released. The KeyEvent has its
     * keyChar ivar set to something, keyCode ivar is junk.
     */
    public void keyTyped(KeyEvent ke) {
        Globals.curEditor(this);
        if (_canSelectElements) {
            _selectionManager.keyTyped(ke);
            _modeManager.keyTyped(ke);
        }
    }

    /**
     * Invoked when a key has been pressed. The KeyEvent has its keyCode ivar
     * set to something, keyChar ivar is junk.
     */
    public void keyPressed(KeyEvent ke) {
        Globals.curEditor(this);
        if (_canSelectElements) {
            _selectionManager.keyPressed(ke);
            _modeManager.keyPressed(ke);
        }
    }

    /**
     * Invoked when a key has been released.
     */
    public void keyReleased(KeyEvent ke) {
        Globals.curEditor(this);
        if (_canSelectElements) {
            _selectionManager.keyReleased(ke);
            _modeManager.keyReleased(ke);
        }
    }

    // //////////////////////////////////////////////////////////////
    // notifications and updates

    /**
     * The given Fig was removed from the diagram this Editor is showing. Now
     * update the display.
     */
    public void removed(Fig f) {
        _selectionManager.deselect(f);
        remove(f);
    }

    /**
     * Gets the selection object the mouse is in
     *
     * @return the selection object or null
     */
    public Selection getCurrentSelection() {
        return _curSel;
    }

    public int getNaturalGraphWidth() {
        return _naturalGraphWidth;
    }

    public void setNaturalGraphWidth(int graphWidth) {
        _naturalGraphWidth = graphWidth;
    }

    public int getNaturalGraphHeight() {
        return _naturalGraphHeight;
    }

    public void setNaturalGraphHeight(int graphHeight) {
        _naturalGraphHeight = graphHeight;
    }

    public void setGraph(JGraph graph) {
        _graph = graph;
    }

    public void setAsActiveUndoManager() {
        UndoManager.setInstance(undoManager);
        undoManager.fireAllEvents();
    }

    public UndoManager getUndoManager() {
        return undoManager;
    }

    public void setSnapToGrid(boolean b) {
        this.snapToGrid = b;
    }

    public boolean isSnapToGrid() {
        return snapToGrid;
    }

    public void setReprocessorFrame(ReprocessorFrame reprocessorFrame) {
        this.reprocessorFrame = reprocessorFrame;
    }

    public ReprocessorFrame getReprocessorFrame() {
        return this.reprocessorFrame;
    }

}

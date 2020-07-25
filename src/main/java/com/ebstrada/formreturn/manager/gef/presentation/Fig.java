package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.UIManager;

import com.ebstrada.formreturn.manager.gef.base.AlignAction;
import com.ebstrada.formreturn.manager.gef.base.CopyAction;
import com.ebstrada.formreturn.manager.gef.base.CutAction;
import com.ebstrada.formreturn.manager.gef.base.DeleteFromModelAction;
import com.ebstrada.formreturn.manager.gef.base.DuplicateAction;
import com.ebstrada.formreturn.manager.gef.base.Editor;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.base.Layer;
import com.ebstrada.formreturn.manager.gef.base.LayerDiagram;
import com.ebstrada.formreturn.manager.gef.base.PasteAction;
import com.ebstrada.formreturn.manager.gef.base.ReorderAction;
import com.ebstrada.formreturn.manager.gef.base.Selection;
import com.ebstrada.formreturn.manager.gef.di.GraphicElement;
import com.ebstrada.formreturn.manager.gef.graph.MutableGraphSupport;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.plot2d.Java2d;
import com.ebstrada.formreturn.manager.gef.plot2d.Plotter;
import com.ebstrada.formreturn.manager.gef.properties.PropCategoryManager;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;
import com.ebstrada.formreturn.manager.gef.ui.PopupGenerator;
import com.ebstrada.formreturn.manager.gef.undo.Memento;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.gef.undo.memento.FigAddMemento;
import com.ebstrada.formreturn.manager.gef.undo.memento.FigRemoveMemento;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorMultiPanel;
import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("fig") public abstract class Fig
    implements GraphicElement, Cloneable, java.io.Serializable, PropertyChangeListener,
    PopupGenerator, NoObfuscation {

    protected static Plotter plotter = new Java2d();

    /**
     * The smallest size that the user can drag this Fig.
     */
    @XStreamOmitField public final int MIN_SIZE = 4;

    /**
     * The size of the dashes drawn when the Fig is dashed.
     */
    @XStreamOmitField public static final String[] DASHED_CHOICES =
        {"Solid", "Dashed", "Dotted", "Double"};
    @XStreamOmitField private static final float[][] DASH_ARRAYS =
        {null, {5.0f, 5.0f}, {3.0f, 10.0f}, {3.0f, 6.0f, 10.0f, 6.0f}}; // opaque,
    // transparent,
    // [opaque,
    // transparent]
    @XStreamOmitField private static final int[] DASH_PERIOD = {0, 10, 13, 25,}; // the
    // sum
    // of
    // each
    // subarray

    private BasicStroke stroke;

    /**
     * Indicates whether this fig can be moved
     */
    @XStreamAlias("isMovable") boolean movable = true;

    /**
     * Indicates whether this fig can be resized
     */
    @XStreamAlias("isResizable") boolean resizable = true;

    /**
     * The Layer that this Fig is in. Each Fig can be in exactly one Layer,
     * but there can be multiple Editors on a given Layer.
     */
    private transient Layer _layer = null;

    private transient int lastLinearPosition = 0;
    private transient double copyY = 0;

    /**
     * True if this object is locked and cannot be moved by the user.
     */
    @XStreamAlias("isLocked") private boolean _locked = false;

    /**
     * Owners are underlying objects that "own" the graphical Fig's that
     * represent them. For example, a FigNode and FigEdge keep a pointer to
     * the net-level object that they represent. Also, any Fig can have
     * NetPort as an owner.
     *
     * @see FigNode#setOwner
     * @see FigNode#bindPort
     */
    private transient Object _owner;

    /**
     * X coordinate of the Fig's bounding box. It is the responsibility of
     * subclasses to make sure this value is ALWAYS up-to-date.
     */
    @XStreamAlias("x") public int _x;

    /**
     * Y coordinate of the Fig's bounding box. It is the responsibility of
     * subclasses to make sure this value is ALWAYS up-to-date.
     */
    @XStreamAlias("y") public int _y;

    /**
     * Width of the Fig's bounding box. It is the responsibility of
     * subclasses to make sure this value is ALWAYS up-to-date.
     */
    @XStreamAlias("width") protected int _w;

    /**
     * Height of the Fig's bounding box. It is the responsibility of
     * subclasses to make sure this value is ALWAYS up-to-date.
     */
    @XStreamAlias("height") protected int _h;

    /**
     * Name of the resource being basis to this figs localization.
     */
    @XStreamAlias("resource") private String _resource = "";

    /**
     * Outline color of fig object.
     */
    @XStreamAlias("foregroundColor") Color _lineColor = Color.black;

    /**
     * Fill color of fig object.
     */
    @XStreamAlias("backgroundColor") Color _fillColor = Color.white;

    /**
     * Thickness of line around object, for now limited to 0 or 1.
     */
    @XStreamAlias("lineWidth") float _lineWidth = 1;

    @XStreamAlias("dashLengths") protected float[] _dashes = null;

    @XStreamAlias("dashStyle") protected int _dashStyle = 0;

    @XStreamAlias("dashPeriod") protected int _dashPeriod = 0;

    /**
     * True if the object should fill in its area.
     */
    @XStreamAlias("isFilled") protected boolean _filled = true;

    /**
     * The parent Fig of which this Fig is a child
     */
    @XStreamAlias("groupFig") private Fig group = null;

    @XStreamAlias("context") protected String _context = "";

    /**
     * True if the Fig is visible
     */
    @XStreamAlias("isVisible") private boolean visible = true;

    @XStreamAlias("isAllowedToSave") protected boolean _allowsSaving = true;

    /**
     * This flag is set at the start of the removal process. It is later
     * used for testing to confirm that all removed figs have actually gone
     * from all layers.
     */
    public transient boolean removeStarted;

    // //////////////////////////////////////////////////////////////
    // static initializer
    static {
        // needs-more-work: get rect editor to work
        // PropCategoryManager.categorizeProperty("Geometry", "bounds");
        PropCategoryManager.categorizeProperty("Geometry", "x");
        PropCategoryManager.categorizeProperty("Geometry", "y");
        PropCategoryManager.categorizeProperty("Geometry", "width");
        PropCategoryManager.categorizeProperty("Geometry", "height");
        PropCategoryManager.categorizeProperty("Geometry", "filled");
        PropCategoryManager.categorizeProperty("Geometry", "locked");
        PropCategoryManager.categorizeProperty("Style", "lineWidth");
        PropCategoryManager.categorizeProperty("Style", "fillColor");
        PropCategoryManager.categorizeProperty("Style", "lineColor");
        PropCategoryManager.categorizeProperty("Style", "filled");
    }

    // //////////////////////////////////////////////////////////////
    // geometric manipulations

    /**
     * Margin between this Fig and automatically routed arcs.
     */
    @XStreamOmitField public final int BORDER = 8;

    private transient int xOffset;

    private transient int yOffset;

    private transient int leftMarginOffset;

    private transient int topMarginOffset;

    /**
     * Most subclasses will not use this constructor, it is only useful for
     * subclasses that redefine most of the infrastructure provided by class
     * Fig.
     */
    public Fig() {
    }

    /**
     * Construct a new Fig with the given bounds.
     */
    public Fig(int x, int y, int w, int h) {
        this(x, y, w, h, Color.black, Color.white, null);
    }

    /**
     * Construct a new Fig with the given bounds and colors.
     */
    public Fig(int x, int y, int w, int h, Color lineColor, Color fillColor) {
        this(x, y, w, h, lineColor, fillColor, null);
    }

    // //////////////////////////////////////////////////////////////
    // constuctors

    /**
     * Construct a new Fig with the given bounds, colors, and owner.
     */
    public Fig(int x, int y, int w, int h, Color lineColor, Color fillColor, Object own) {
        this();
        _x = x;
        _y = y;
        _w = w;
        _h = h;
        if (lineColor != null) {
            _lineColor = lineColor;
        } else {
            _lineWidth = 0;
        }

        if (fillColor != null) {
            _fillColor = fillColor;
        } else {
            _filled = false;
        }
    }

    abstract public EditorPanel getEditorPanel();

    /**
     * Add a point to this fig. sub classes should implement. TODO: Why
     * isn't this extended by FigEdgePoly?
     */
    public void addPoint(int x, int y) {
    }

    /**
     * The specified PropertyChangeListeners <b>propertyChange</b> method
     * will be called each time the value of any bound property is changed.
     * Note: the JavaBeans specification does not require
     * PropertyChangeListeners to run in any particular order.
     * <p>
     * <p>
     * Since most Fig's will never have any listeners, and I want Figs to be
     * fairly light-weight objects, listeners are kept in a global
     * Hashtable, keyed by Fig. NOTE: It is important that all listeners
     * eventually remove themselves, otherwise this will prevent garbage
     * collection.
     */
    final public void addPropertyChangeListener(PropertyChangeListener l) {
        Globals.addPropertyChangeListener(this, l);
    }

    /**
     * Remove this PropertyChangeListener from the JavaBeans internal list.
     * If the PropertyChangeListener isn't on the list, silently do nothing.
     */
    final public void removePropertyChangeListener(PropertyChangeListener l) {
        Globals.removePropertyChangeListener(this, l);
    }

    /**
     * Align this Fig with the given rectangle. Some subclasses may need to
     * know the editor that initiated this action.
     *
     * @param r         the rectangle to align to.
     * @param direction
     * @param ed        the editor that initiated this action.
     */
    final public void align(Rectangle r, int direction, Editor ed) {
        Rectangle bbox = getBounds();
        int dx = 0;
        int dy = 0;
        switch (direction) {

            case AlignAction.ALIGN_TOPS:
                dy = r.y - bbox.y;
                break;

            case AlignAction.ALIGN_BOTTOMS:
                dy = r.y + r.height - (bbox.y + bbox.height);
                break;

            case AlignAction.ALIGN_LEFTS:
                dx = r.x - bbox.x;
                break;

            case AlignAction.ALIGN_RIGHTS:
                dx = r.x + r.width - (bbox.x + bbox.width);
                break;

            case AlignAction.ALIGN_CENTERS:
                dx = r.x + r.width / 2 - (bbox.x + bbox.width / 2);
                dy = r.y + r.height / 2 - (bbox.y + bbox.height / 2);
                break;

            case AlignAction.ALIGN_H_CENTERS:
                dx = r.x + r.width / 2 - (bbox.x + bbox.width / 2);
                break;

            case AlignAction.ALIGN_V_CENTERS:
                dy = r.y + r.height / 2 - (bbox.y + bbox.height / 2);
                break;

            case AlignAction.ALIGN_TO_GRID:
                Point loc = getLocation();
                Point snapPt = new Point(loc.x, loc.y);
                ed.snap(snapPt);
                dx = snapPt.x - loc.x;
                dy = snapPt.y - loc.y;
                break;
        }

        translate(dx, dy);
    }

    /**
     * Update the bounds of this Fig. By default it is assumed that the
     * bounds have already been updated, so this does nothing.
     *
     * @see FigText#calcBounds
     */
    public void calcBounds() {
    }

    /**
     * Return the center of the given Fig. By default the center is the
     * center of its bounding box. Subclasses may want to define something
     * else.
     */
    // USED BY PGML.tee
    public Point getCenter() {
        Rectangle bbox = getBounds();
        return new Point(bbox.x + bbox.width / 2, bbox.y + bbox.height / 2);
    }

    public PageAttributes getPageAttributes() {
        return null;
    }

    public void setPageAttributes(PageAttributes pageAttributes) {
    }

    public void cleanUp() {
    }

    @Override public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Can the fig can be copied and pasted
     */
    public boolean isCopyable() {
        return true;
    }

    /**
     * Can the fig can be cut and pasted
     */
    public boolean isCutable() {
        return true;
    }

    /**
     * Reply true if the given point is inside the given Fig. By default
     * reply true if the point is in my bounding box. Subclasses like
     * FigCircle and FigEdge do more specific checks.
     */
    public boolean contains(int x, int y) {
        return (_x <= x) && (x <= _x + _w) && (_y <= y) && (y <= _y + _h);
    }

    /**
     * Reply true if the given point is inside this Fig by calling
     * contains(int x, int y).
     */
    final public boolean contains(Point p) {
        return contains(p.x, p.y);
    }

    /**
     * Reply true if the all four corners of the given rectangle are inside
     * this Fig, as determined by contains(int x, int y).
     */
    final public boolean contains(Rectangle r) {
        return countCornersContained(r.x, r.y, r.width, r.height) == 4;
    }

    /**
     * Reply the number of corners of the given rectangle that are inside
     * this Fig, as determined by contains(int x, int y).
     */
    protected int countCornersContained(int x, int y, int w, int h) {
        int cornersHit = 0;
        if (contains(x, y)) {
            cornersHit++;
        }

        if (contains(x + w, y)) {
            cornersHit++;
        }

        if (contains(x, y + h)) {
            cornersHit++;
        }

        if (contains(x + w, y + h)) {
            cornersHit++;
        }

        return cornersHit;
    }

    /**
     * Resize the object for drag on creation. It bypasses the things done
     * in resize so that the position of the object can be kept as the
     * anchor point. Needs-More-Work: do I really need this function?
     *
     * @see FigLine#createDrag
     */
    public void createDrag(int anchorX, int anchorY, int x, int y, int snapX, int snapY,
        boolean released) {
        int newX = Math.min(anchorX, snapX);
        int newY = Math.min(anchorY, snapY);
        int newW = Math.max(anchorX, snapX) - newX;
        int newH = Math.max(anchorY, snapY) - newY;

        boolean isNewFig = true;
        setBounds(newX, newY, newW, newH, isNewFig, released);
    }



    /**
     * This is called after an Cmd modifies a Fig and the Fig needs to be
     * redrawn in its new position.
     */
    public void endTrans() {
        damage();
    }

    /**
     * This Fig has changed in some way, tell its Layer to record my
     * bounding box as a damageAll region so that I will eventualy be
     * redrawn.
     */
    public void damage() {
        Layer lay = getLayer();
        Fig group = getGroup();
        while (lay == null && group != null) {
            lay = group.getLayer();
            group = group.getGroup();
        }
        if (lay != null) {
            lay.damageAll();
        }
    }

    /**
     * Get the rectangle on whose corners the dragging handles are to be
     * drawn. Should be overwritten by Figures with Bounds larger than the
     * HandleBox. Normally these should be identical.
     */
    public Rectangle getHandleBox() {
        return getBounds();
    }

    /**
     * Set the HandleBox. Normally this should not be used. It is intended
     * for figures where the Handlebox is different from the Bounds. Overide
     * this method if HandleBox and bounds differ
     */
    public void setHandleBox(int x, int y, int w, int h) {
        setBounds(x, y, w, h);
    }

    // //////////////////////////////////////////////////////////////
    // Editor API

    /**
     * Remove this Fig from the Layer it belongs to.
     */
    public void removeFromDiagram() {

        if (UndoManager.getInstance().isGenerateMementos()) {
            UndoManager.getInstance().addMemento(new FigRemoveMemento(this));
        }

        removeStarted = true;
        visible = false;

        if (_layer != null) {
            Layer oldLayer = _layer;
            _layer.remove(this);
            oldLayer.deleted(this);
        }

        // ak: remove this figure from the enclosed figures of the
        // encloser
        setEnclosingFig(null);
    }

    /**
     * Delete whatever application object this Fig is representing, the Fig
     * itself should automatically be deleted as a side-effect. Simple Figs
     * have no underlying model, so they are just deleted. Figs that
     * graphically present some part of an underlying model should NOT
     * delete themselves, instead they should ask the model to dispose, and
     * IF it does then the figs will be notified.
     */
    public void deleteFromModel() {
        removeStarted = true;
        removeFromDiagram();
    }

    final public void firePropChange(String propName, int oldV, int newV) {
        firePropChange(propName, new Integer(oldV), new Integer(newV));
    }

    /**
     * Creates a PropertyChangeEvent and calls all registered listeners
     * propertyChanged() method.
     */
    final public void firePropChange(String propName, Object oldV, Object newV) {
        Globals.firePropChange(this, propName, oldV, newV);
        if (group != null) {
            PropertyChangeEvent pce = new PropertyChangeEvent(this, propName, oldV, newV);
            group.propertyChange(pce);
        }
    }

    final public void firePropChange(String propName, boolean oldV, boolean newV) {
        firePropChange(propName, new Boolean(oldV), new Boolean(newV));
    }

    /**
     * Return a Rectangle that completely encloses this Fig. Subclasses may
     * override getBounds(Rectangle).
     */
    // USED BY PGML.tee
    public Rectangle getBounds() {
        return getBounds(null);
    }

    /**
     * Stores the Rectangle that completely encloses this Fig into "return
     * value" <b>r</b> and return <b>r</b>. If r is <code>null</code> a
     * new <code>Rectangle</code> is allocated. This version of
     * <code>getBounds</code> is useful if the caller wants to avoid
     * allocating a new <code>Rectangle</code> object on the heap.
     *
     * @param r the return value, modified to the components bounds
     * @return r
     */
    public Rectangle getBounds(Rectangle r) {
        if (r == null) {
            return new Rectangle(_x, _y, _w, _h);
        }
        r.setBounds(_x, _y, _w, _h);
        return r;
    }

    /**
     * Get the dashed attribute *
     */
    public boolean getDashed() {
        return (_dashes != null);
    }

    final public String getDashedString() {
        return DASHED_CHOICES[_dashStyle];
    }

    public Vector getEnclosedFigs() {
        return null;
    }

    /**
     * USED BY PGML.tee
     */
    public Fig getEnclosingFig() {
        return null;
    }

    /**
     * Does this Fig support the concept of "fill color" in principle
     *
     * @return true if the Fig can be filled
     */
    public boolean hasFillColor() {
        return true;
    }

    public Color getFillColor() {
        return _fillColor;
    }

    public boolean getFilled() {
        return _filled;
    }

    /**
     * Does this Fig support the concept of "line color" in principle
     *
     * @return true if the Fig can have a line color
     */
    public boolean hasLineColor() {
        return true;
    }

    public Color getLineColor() {
        return _lineColor;
    }

    public float getLineWidth() {
        return _lineWidth;
    }

    /**
     * TODO: Should be abstract. Is this needed on Fig or FigEdge
     */
    public Point getFirstPoint() {
        return new Point();
    }

    /**
     * Overrule this if you want to connect to a limited number of points,
     * and to points only.
     * <p>
     * Instead, if you want to connect to any point on one or more
     * line-segments, then you should overrule getClosestPoint().
     *
     * @return the list of gravity points.
     */
    public List getGravityPoints() {
        return null;
    }

    final public Fig getGroup() {
        return group;
    }

    public JGraph getGraph() {

        Layer layer = getLayer();

        if (layer == null) {
            return Globals.curEditor().getGraph();
        }

        List<Editor> editors = layer.getEditors();

        if (editors == null || editors.size() <= 0) {
            return Globals.curEditor().getGraph();
        }

        Editor editor = editors.iterator().next();

        if (editor == null) {
            return Globals.curEditor().getGraph();
        }

        return editor.getGraph();

    }

    /**
     * TODO must determine the purpose of this.
     *
     * @return the context of the Fig.
     */
    // USED BY PGML.tee
    final public String getContext() {
        return _context;
    }

    /*
     * USED BY PGML.tee
     */
    public String getId() {
        if (getGroup() != null) {
            String gID = getGroup().getId();
            if (getGroup() instanceof FigGroup) {
                return gID + "." + (((FigGroup) getGroup()).getFigs()).indexOf(this);
            } else {
                return gID + ".0";
            }
        }

        Layer layer = getLayer();
        if (layer == null) {
            return "LAYER_NULL";
        }

        List c = (List) layer.getContents();
        int index = c.indexOf(this);
        return "Fig" + index;
    }

    /**
     * TODO: Should be abstract. Is this needed on Fig or FigEdge
     */
    public Point getLastPoint() {
        return new Point();
    }

    final public Layer getLayer() {
        return _layer;
    }

    /**
     * Returns a point that is the upper left corner of the Fig's bounding
     * box. Implementation creates a new Point instance, consider getX() and
     * getY() for performance.
     */
    final public Point getLocation() {
        return new Point(_x, _y);
    }

    final public boolean getLocked() {
        return _locked;
    }

    /**
     * Returns the minimum size of the Fig. This is the smallest size that
     * the user can make this Fig by dragging. You can ignore this and make
     * Figs smaller programmitically if you must. TODO: return a single
     * instance of an immutable Dimension
     */
    public Dimension getMinimumSize() {
        return new Dimension(MIN_SIZE, MIN_SIZE);
    }

    public int getNumPoints() {
        return 0;
    }

    /**
     * Return the model element that this Fig represents. USED BY PGML.tee
     */
    public Object getOwner() {
        return _owner;
    }

    /**
     * Return the length of the path around this Fig. By default, returns
     * the perimeter of the Fig's bounding box. Subclasses like FigPoly have
     * more specific logic.
     */
    public int getPerimeterLength() {
        return _w + _w + _h + _h;
    }

    public Point[] getPoints() {
        return new Point[0];
    }

    public Point getPoint(int i) {
        return null;
    }

    public Vector getPopUpActions(MouseEvent me) {
        Vector popUpActions = new Vector();
        JMenu orderMenu = new JMenu(Localizer.localize("GefBase", "Ordering"));
        orderMenu.setFont(UIManager.getFont("Menu.font"));

        Icon toFrontIcon = new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/gef/Images/ToFront.png"));
        Icon toBackIcon = new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/gef/Images/ToBack.png"));
        Icon forwardIcon = new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/gef/Images/Forward.png"));
        Icon backwardIcon = new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/gef/Images/Backward.png"));

        JMenuItem menuItem = new JMenuItem(
            new ReorderAction(Localizer.localize("GefBase", "Forward"), forwardIcon,
                ReorderAction.BRING_FORWARD));
        menuItem.setFont(UIManager.getFont("MenuItem.font"));
        orderMenu.add(menuItem);
        menuItem = new JMenuItem(
            new ReorderAction(Localizer.localize("GefBase", "Backward"), backwardIcon,
                ReorderAction.SEND_BACKWARD));
        menuItem.setFont(UIManager.getFont("MenuItem.font"));
        orderMenu.add(menuItem);
        menuItem = new JMenuItem(
            new ReorderAction(Localizer.localize("GefBase", "ToFront"), toFrontIcon,
                ReorderAction.BRING_TO_FRONT));
        menuItem.setFont(UIManager.getFont("MenuItem.font"));
        orderMenu.add(menuItem);
        menuItem = new JMenuItem(
            new ReorderAction(Localizer.localize("GefBase", "ToBack"), toBackIcon,
                ReorderAction.SEND_TO_BACK));
        menuItem.setFont(UIManager.getFont("MenuItem.font"));
        orderMenu.add(menuItem);
        popUpActions.addElement(orderMenu);

        JMenu editMenu = new JMenu(Localizer.localize("GefBase", "Edit"));
        editMenu.setFont(UIManager.getFont("Menu.font"));
        // -- delete
        JMenuItem deleteItem = new JMenuItem();
        deleteItem = editMenu.add(new DeleteFromModelAction("DeleteFromDiagram"));
        deleteItem.setFont(UIManager.getFont("MenuItem.font"));
        deleteItem.setText(Localizer.localize("GefBase", "Delete"));

        deleteItem.setIcon(new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/cross.png")));
        editMenu.add(deleteItem);

        // -- cut
        JMenuItem cutItem = new JMenuItem();
        cutItem = editMenu.add(new CutAction("Cut"));
        cutItem.setFont(UIManager.getFont("MenuItem.font"));
        cutItem.setText(Localizer.localize("GefBase", "Cut"));

        cutItem.setIcon(new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/mainmenu/cut.png")));
        editMenu.add(cutItem);

        // -- copy
        JMenuItem copyItem = new JMenuItem();
        copyItem = editMenu.add(new CopyAction());
        copyItem.setFont(UIManager.getFont("MenuItem.font"));
        copyItem.setText(Localizer.localize("GefBase", "Copy"));

        copyItem.setIcon(new ImageIcon(getClass().getResource(
            "/com/ebstrada/formreturn/manager/ui/icons/mainmenu/page_white_copy.png")));
        editMenu.add(copyItem);

        // -- paste
        JMenuItem pasteItem = new JMenuItem();
        pasteItem = editMenu.add(new PasteAction("Paste"));
        pasteItem.setFont(UIManager.getFont("MenuItem.font"));
        pasteItem.setText(Localizer.localize("GefBase", "Paste"));

        pasteItem.setIcon(new ImageIcon(getClass()
            .getResource("/com/ebstrada/formreturn/manager/ui/icons/mainmenu/paste_plain.png")));
        editMenu.add(pasteItem);

        editMenu.addSeparator();

        // -- duplicate
        JMenuItem duplicateItem = new JMenuItem();
        duplicateItem = editMenu.add(new DuplicateAction("Duplicate"));
        duplicateItem.setFont(UIManager.getFont("MenuItem.font"));
        duplicateItem.setText(Localizer.localize("GefBase", "Duplicate"));
        duplicateItem.setIcon(new ImageIcon(getClass()
            .getResource("/com/ebstrada/formreturn/manager/ui/icons/mainmenu/duplicate.png")));

        editMenu.add(duplicateItem);

        popUpActions.addElement(editMenu);

        return popUpActions;
    }

    /**
     * Returns the prefered size of the Fig. This will be useful for
     * automated layout. By default just uses the current size. Subclasses
     * must override to return something useful.
     */
    final public Dimension getPreferredSize() {
        return new Dimension(_w, _h);
    }

    /**
     * Returns the size of the Fig.
     */
    public Dimension getSize() {
        return new Dimension(_w, _h);
    }

    public String getTipString(MouseEvent me) {
        if (_owner == null) {
            return toString();
        }
        return _owner.toString();
    }

    final public Rectangle getTrapRect() {
        return getBounds();
    }

    public boolean getUseTrapRect() {
        return false;
    }

    /**
     * Get the current width of the Fig.
     */
    // USED BY PGML.tee
    final public int getWidth() {
        return _w;
    }

    /**
     * Get the current height of the Fig.
     */
    // USED BY PGML.tee
    final public int getHeight() {
        return _h;
    }

    /**
     * Get the x position of the Fig.
     */
    // USED BY PGML.tee
    final public int getX() {
        return _x + getXOffset() + getLeftMarginOffset();
    }

    /**
     * Get the y position of the Fig.
     */
    // USED BY PGML.tee
    final public int getY() {
        return _y + getYOffset() + getTopMarginOffset();
    }

    public int getXOffset() {
        return xOffset;
    }

    public void setXOffset(int xOffset) {
        this.xOffset = xOffset;
    }

    public int getYOffset() {
        return yOffset;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public int getLeftMarginOffset() {
        return leftMarginOffset;
    }

    public void setLeftMarginOffset(int leftMarginOffset) {
        this.leftMarginOffset = leftMarginOffset;
    }

    public int getTopMarginOffset() {
        return topMarginOffset;
    }

    public void setTopMarginOffset(int topMarginOffset) {
        this.topMarginOffset = topMarginOffset;
    }

    public int[] getXs() {
        return new int[0];
    }

    public int[] getYs() {
        return new int[0];
    }

    /**
     * Determine if the given rectangle contains some pixels of the Fig.
     * This is used to determine if the user is trying to select this Fig.
     * Rather than ask if the mouse point is in the Fig, I use a small
     * rectangle around the mouse point so that small objects and lines are
     * easier to select. If the fig is invisible this method always returns
     * false.
     *
     * @param r the rectangular hit area
     * @return true if the hit rectangle strikes this fig
     */
    public boolean hit(Rectangle r) {
        if (!isVisible() || !isSelectable()) {
            return false;
        }
        int cornersHit = countCornersContained(r.x, r.y, r.width, r.height);
        if (_filled) {
            return cornersHit > 0;
        } else {
            return cornersHit > 0 && cornersHit < 4;
        }
    }

    public void insertPoint(int i, int x, int y) {
    }

    /**
     * Reply true if the object intersects the given rectangle. Used for
     * selective redrawing and by ModeSelect to select all Figs that are
     * partly within the selection rectangle.
     * <p>
     * Note: comparisons are strict (e.g. '<' instead of '<='), so that
     * figs with zero height or width are handled correctly.
     */
    public boolean intersects(Rectangle r) {
        return !((r.x + r.width < _x) || (r.y + r.height < _y) || (r.x > _x + _w) || (r.y
            > _y + _h));
    }

    /**
     * Reply true if the object's perimeter intersects the given rectangle.
     * Used for selective redrawing and by ModeSelect to select all Figs
     * that are partly within the selection rectangle.
     * <p>
     * Note: comparisons are strict (e.g. '<' instead of '<='), so that
     * figs with zero height or width are handled correctly.
     */
    public boolean intersectsPerimeter(Rectangle r) {
        return (r.intersectsLine(_x, _y, _x, _y + _h) && r
            .intersectsLine(_x, _y + _h, _x + _w, _y + _h) && r
            .intersectsLine(_x + _w, _y + _h, _x + _w, _y) && r
            .intersectsLine(_x + _w, _y, _x, _y));
    }

    /**
     * Returns true if this Fig can be resized by the user.
     */
    final public boolean isLowerRightResizable() {
        return false;
    }

    /**
     * Returns true if this Fig can be moved around by the user.
     */
    final public boolean isMovable() {
        return movable;
    }

    /**
     * Returns true if this Fig can be reshaped by the user.
     */
    public boolean isReshapable() {
        return false;
    }

    /**
     * Determine if this Fig can be resized
     *
     * @return true if this Fig can be resized by the user.
     */
    public boolean isResizable() {
        return resizable;
    }

    /**
     * Determine if this Fig can be selected
     *
     * @return true if this Fig can be selected by the user.
     */
    public boolean isSelectable() {
        return true;
    }

    /**
     * Returns true if this Fig can be rotated by the user.
     */
    public boolean isRotatable() {
        return false;
    }

    /**
     * SelectionManager calls this to attempt to create a custom Selection
     * object when selecting a Fig. Override this only if you have
     * specialist requirements For a selected Fig. SelectionManger uses its
     * own rules if this method returns null.
     *
     * @return a specialist Selection class or null to delegate creation to
     * the Selection Manager.
     */
    public Selection makeSelection() {
        return null;
    }

    /**
     * @param g
     * @depreacted use paint(Object) and make this final
     */
    public void paint(Graphics g) {
        paint((Object) g);
    }

    /**
     * Method to paint this Fig. By default it paints an "empty" space,
     * subclasses should override this method.
     */
    abstract public void paint(Object g);

    abstract public void paint(Object g, boolean includeMargins);

    /**
     * Return a point at the given distance along the path around this Fig.
     * By default, uses perimeter of the Fig's bounding box. Subclasses like
     * FigPoly have more specific logic.
     */
    final public Point pointAlongPerimeter(int dist) {
        Point res = new Point();
        stuffPointAlongPerimeter(dist, res);
        return res;
    }

    public void postLoad() {
    }

    public void postSave() {
    }

    public void preSave() {
    }

    /**
     * Draw the Fig on a PrintGraphics. This just calls paint.
     */
    final public void print(Graphics g) {
        paint(g);
    }

    /**
     * By default just pass it up to enclosing groups. Subclasses of FigNode
     * may want to override this method.
     */
    public void propertyChange(PropertyChangeEvent pce) {
        if (group != null) {
            group.propertyChange(pce);
        }
    }

    /**
     * Force recalculating of bounds and redraw of fig.
     */
    final public void redraw() {
        Rectangle rect = getBounds();
        setBounds(rect.x, rect.y, rect.width, rect.height);
        damage();
    }

    // TODO: Make sure this is extended in FigEdgePoly and FigPoly
    public void removePoint(int i) {
    }

    /**
     * Change the back-to-front ordering of a Fig in LayerDiagram. Should
     * the Fig have any say in it?
     *
     * @see LayerDiagram#reorder
     * @see CmdReorder
     */
    final public void reorder(int func, Layer lay) {
        lay.reorder(this, func);
    }

    /**
     * Reply a rectangle that arcs should not route through. Basically this
     * is the bounding box plus some margin around all egdes.
     */
    final public Rectangle routingRect() {
        return new Rectangle(_x - BORDER, _y - BORDER, _w + BORDER * 2, _h + BORDER * 2);
    }

    private void setBounds(int newX, int newY, int newW, int newH, boolean isNewFig,
        boolean released) {

        if (released) {
            MutableGraphSupport.enableSaveAction();
            if (UndoManager.getInstance().isGenerateMementos()) {
                UndoManager.getInstance().startChain();
                UndoManager.getInstance().addMemento(new FigAddMemento(this));
            }

        }
        setBoundsImpl(newX, newY, newW, newH);

    }

    public void removeFromDiagramNoMemento() {
        removeStarted = true;
        visible = false;

        if (_layer != null) {
            Layer oldLayer = _layer;
            _layer.remove(this);
            oldLayer.deleted(this);
        }

        // ak: remove this figure from the enclosed figures of the
        // encloser
        setEnclosingFig(null);
    }

    /**
     * Set the bounds of this Fig. Fires PropertyChangeEvent "bounds". This
     * method can be undone by performing UndoAction.
     */
    final public void setBounds(final int newX, final int newY, final int newWidth,
        final int newHeight) {

        if (group == null && (newX != _x || newY != _y || newWidth != _w || newHeight != _h)) {
            MutableGraphSupport.enableSaveAction();
            if (UndoManager.getInstance().isGenerateMementos()) {

                Memento memento = new Memento() {
                    int oldX = _x;
                    int oldY = _y;
                    int oldWidth = _w;
                    int oldHeight = _h;

                    public void undo() {
                        setBoundsImpl(oldX, oldY, oldWidth, oldHeight);
                        damage();
                        firePropChange("undo", null, null);
                    }

                    public void redo() {
                        setBoundsImpl(newX, newY, newWidth, newHeight);
                        damage();
                        firePropChange("redo", null, null);
                    }

                    public void dispose() {
                    }

                    public String toString() {
                        return (isStartChain() ? "*" : " ") + "BoundsMemento " + oldX + ", " + oldY
                            + ", " + oldWidth + ", " + oldHeight;
                    }
                };
                UndoManager.getInstance().addMemento(memento);
            }
        }
        setBoundsImpl(newX, newY, newWidth, newHeight);
    }

    /**
     * Set the bounds of this Fig. Fires PropertyChangeEvent "bounds".
     */
    protected void setBoundsImpl(int x, int y, int w, int h) {
        Rectangle oldBounds = getBounds();
        if (w == 0) {
            w = 10;
        }
        if (h == 0) {
            h = 10;
        }
        _x = x;
        _y = y;
        _w = w;
        _h = h;
        firePropChange("bounds", oldBounds, getBounds());
    }

    /**
     * Change my bounding box to the given Rectangle. Just calls
     * setBounds(x, y, w, h).
     */
    public final void setBounds(Rectangle r) {
        setBounds(r.x, r.y, r.width, r.height);
    }

    public final void setCenter(Point p) {
        int newX = p.x - (_w / 2);
        int newY = p.y - (_h / 2);
        setLocation(newX, newY);
    }

    /**
     * USED BY PGML.tee
     */
    public void setEnclosingFig(Fig f) {
        if (f != null && f != getEnclosingFig() && _layer != null) {
            _layer.bringInFrontOf(this, f);
            damage();
        }
    }

    /**
     * Sets the enclosing FigGroup of this Fig. The enclosing group is
     * always notified of property changes, without need to add a listener.
     */
    final public void setGroup(Fig f) {
        group = f;
    }

    final public void setContext(String context) {
        _context = context;
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * Sets the Layer that this Fig belongs to. Fires PropertyChangeEvent
     * "layer".
     */
    public void setLayer(Layer lay) {
        firePropChange("layer", _layer, lay);
        _layer = lay;
    }

    /**
     * Sets the color that will be used if the Fig is filled. If col is
     * null, turns off filling. Fires PropertyChangeEvent "fillColor", or
     * "filled".
     */
    public void setFillColor(Color col) {

        Color oldFillColor = _fillColor;

        if (col == null) {
            if (_fillColor == null) {
                return;
            }
        } else {
            if (col.equals(_fillColor)) {
                return;
            }
        }

        if (col != null) {
            _fillColor = col;
        } else {
            firePropChange("filled", _filled, false);
            _filled = false;
        }

        firePropChange("fillColor", oldFillColor, col);

        MutableGraphSupport.enableSaveAction();
    }

    /**
     * Sets a flag to either fill the Fig with its fillColor or not. Fires
     * PropertyChangeEvent "filled".
     */
    public void setFilled(boolean f) {
        firePropChange("filled", _filled, f);
        _filled = f;
    }

    /**
     * Sets the color to be used if the lineWidth is > 0. If col is null,
     * sets the lineWidth to 0. Fires PropertyChangeEvent "lineColor", or
     * "lineWidth".
     */
    public void setLineColor(Color col) {

        Color oldLineColor = _lineColor;

        if (col == null) {
            if (_lineColor == null) {
                return;
            }
        } else {
            if (col.equals(_lineColor)) {
                return;
            }
        }
        if (col != null) {
            _lineColor = col;
        } else {
            firePropChange("lineWidth", _lineWidth, 0);
            _lineWidth = 0;
        }

        firePropChange("lineColor", oldLineColor, col);

        MutableGraphSupport.enableSaveAction();

    }

    /**
     * Set the line width. Zero means lines are not drawn. One draws them
     * one pixel wide. Larger widths are in experimental support stadium
     * (hendrik@freiheit.com, 2003-02-05). Fires PropertyChangeEvent
     * "lineWidth".
     *
     * @param newWidthValue The new lineWidth value
     */
    public void setLineWidth(float newWidthValue) {
        float newLW = Math.max(0, newWidthValue);
        firePropChange("lineWidth", _lineWidth, newLW);
        _lineWidth = newLW;
    }

    /**
     * Set line to be dashed or not *
     */
    public void setDashed(boolean now_dashed) {
        if (now_dashed) {
            _dashes = DASH_ARRAYS[1];
            _dashPeriod = DASH_PERIOD[1];
            _dashStyle = 1;
        } else {
            _dashes = null;
            _dashStyle = 0;
        }
    }

    public void setDashedString(String dashString) {
        if (dashString.equalsIgnoreCase("Solid")) {
            setDashed(false);
        } else if (dashString.equalsIgnoreCase("Dashed")) {
            _dashes = DASH_ARRAYS[1];
            _dashPeriod = DASH_PERIOD[1];
            _dashStyle = 1;
        } else if (dashString.equalsIgnoreCase("Dotted")) {
            _dashes = DASH_ARRAYS[2];
            _dashPeriod = DASH_PERIOD[2];
            _dashStyle = 2;
        } else if (dashString.equalsIgnoreCase("Double")) {
            _dashes = DASH_ARRAYS[3];
            _dashPeriod = DASH_PERIOD[3];
            _dashStyle = 3;
        }
    }

    /**
     * Move the Fig to the given position. By default translates the Fig so
     * that the upper left corner of its bounding box is at the location.
     * Fires property "bounds".
     */
    final public void setLocation(int x, int y) {
        translate(x - _x, y - _y);
    }

    /**
     * Move the Fig to the given position.
     */
    public final void setLocation(Point p) {
        setLocation(p.x, p.y);
    }

    /**
     * Sets whether this Fig is locked or not. Most Cmds check to see if
     * Figs are locked and will not request modifications to locked Figs.
     * Fires PropertyChangeEvent "locked".
     */
    final public void setLocked(boolean b) {
        firePropChange("locked", _locked, b);
        _locked = b;
    }

    public void setNumPoints(int npoints) {
    }

    /**
     * Sets the owner object of this Fig. Fires PropertyChangeEvent "owner"
     */
    public void setOwner(Object own) {
        firePropChange("owner", _owner, own);
        _owner = own;
    }

    /**
     * Get and set the points along a path for Figs that are path-like.
     */
    public void setPoints(Point[] ps) {
    }

    public void setPoint(int i, int x, int y) {
    }

    final public void setPoint(int i, Point p) {
        setPoint(i, p.x, p.y);
    }

    public void setPoint(Handle h, int x, int y) {
        setPoint(h.index, x, y);
    }

    final public void setPoint(Handle h, Point p) {
        setPoint(h, p.x, p.y);
    }

    /**
     * Sets the size of the Fig. Fires property "bounds".
     */
    final public void setSize(int w, int h) {
        setBounds(_x, _y, w, h);
    }

    /**
     * Sets the size of the Fig. Fires property "bounds".
     */
    final public void setSize(Dimension d) {
        setSize(d.width, d.height);
    }

    /**
     * Set the width of the Fig.
     * <p>
     * Use this method only if the width property is the only bounds
     * property of the Fig you wish to amend. If you intend to also change
     * the height use setSize(int width, int height). If you also intend to
     * amend the location use setBounds(int x, int y, int width, int
     * height). Calling a single method will be far more efficient in
     * changing bounds.
     *
     * @param width The new width.
     */
    public void setWidth(int w) {
        setBounds(_x, _y, w, _h);
    }

    /**
     * Set the height of the Fig.
     * <p>
     * Use this method only if the height property is the only bounds
     * property of the Fig you wish to amend. If you intend to also change
     * the width use setSize(int width, int height). If you also intend to
     * amend the location use setBounds(int x, int y, int width, int
     * height). Calling a single method will be far more efficient in
     * changing bounds.
     *
     * @param height The new height.
     */
    public void setHeight(int h) {
        setBounds(_x, _y, _w, h);
    }

    /**
     * Set the X co-ordinate of the Fig.
     * <p>
     * Use this method only if the X property is the only bounds property of
     * the Fig you wish to amend. If you intend to also change the Y co
     * ordinate use setLocation(int x, int y). If you also intend to amend
     * the width and/or height use setBounds(int x, int y, int width, int
     * height). Calling a single method will be far more efficient in
     * changing bounds.
     *
     * @param x The new x co-ordinate
     */
    final public void setX(int x) {
        setBounds(x, _y, _w, _h);
    }

    public void setXs(int[] xs) {
    }

    /**
     * Set the Y co-ordinate of the Fig.
     * <p>
     * Use this method only if the Y property is the only bounds property of
     * the Fig you wish to amend. If you intend to also change the X co
     * ordinate use setLocation(int x, int y). If you also intend to amend
     * the width and/or height use setBounds(int x, int y, int width, int
     * height). Calling a single method will be far more efficient in
     * changing bounds.
     *
     * @param y The new y co-ordinate
     */
    final public void setY(int y) {
        setBounds(_x, y, _w, _h);
    }

    public void setYs(int[] ys) {
    }

    public void stuffPointAlongPerimeter(int dist, Point res) {
        if (dist < _w && dist >= 0) {
            res.x = _x + (dist);
            res.y = _y;
        } else if (dist < _w + _h) {
            res.x = _x + _w;
            res.y = _y + (dist - _w);
        } else if (dist < _w + _h + _w) {
            res.x = _x + _w - (dist - _w - _h);
            res.y = _y + _h;
        } else if (dist < _w + _h + _w + _h) {
            res.x = _x;
            res.y = _y + (_w + _h + _w + _h - dist);
        } else {
            res.x = _x;
            res.y = _y;
        }
    }

    public int getLastLinearPosition() {
        return lastLinearPosition;
    }

    public void setLastLinearPosition(int lastLinearPosition) {
        this.lastLinearPosition = lastLinearPosition;
    }

    public double getCopyY() {
        return copyY;
    }

    public void setCopyY(double copyY) {
        this.copyY = copyY;
    }

    /**
     * Change the position of the object from where it is to where it is
     * plus dx and dy. Often called when an object is dragged. This could be
     * very useful if local-coordinate systems are used because deltas need
     * less transforming... maybe. Fires property "bounds". TODO: make final
     * and subclasses should extend translateImpl The method is undoable by
     * performing the UndoAction.
     *
     * @param dx the x offset
     * @param dy the y offset
     */
    public void translate(final int dx, final int dy) {
        if (dx == 0 && dy == 0) {
            return;
        }
        if (group == null) {

            class TranslateMemento extends Memento {

                int oldX;
                int oldY;
                int oldWidth;
                int oldHeight;

                TranslateMemento(int currentX, int currentY, int currentWidth, int currentHeight) {
                    oldX = currentX;
                    oldY = currentY;
                    oldWidth = currentWidth;
                    oldHeight = currentHeight;
                }

                public void undo() {
                    setBoundsImpl(oldX, oldY, oldWidth, oldHeight);
                    damage();
                }

                public void redo() {
                    translateImpl(dx, dy);
                    damage();
                }

                public String toString() {
                    return (isStartChain() ? "*" : " ") + "TranslateMemento " + oldX + ", " + oldY;
                }
            }
            if (UndoManager.getInstance().isGenerateMementos()) {
                UndoManager.getInstance().addMemento(new TranslateMemento(_x, _y, _w, _h));
            }
        }
        MutableGraphSupport.enableSaveAction();
        translateImpl(dx, dy);
    }

    /**
     * Change the position of the object from were it is to were it is plus
     * dx and dy. Often called when an object is dragged. This could be very
     * useful if local-coordinate systems are used because deltas need less
     * transforming... maybe. Fires property "bounds".
     */
    protected void translateImpl(int dx, int dy) {
        Rectangle oldBounds = getBounds();
        _x += dx;
        _y += dy;
        firePropChange("bounds", oldBounds, getBounds());
    }

    /**
     * Reply true if the entire Fig is contained within the given Rectangle.
     * This can be used by ModeSelect to select Figs that are totally within
     * the selection rectangle.
     */
    final public boolean within(Rectangle r) {
        return r.contains(_x, _y) && r.contains(_x + _w, _y + _h);
    }

    /**
     * Returns true if the fig is visible
     */
    final public boolean isVisible() {
        return visible;
    }

    /**
     * Set the visible status of the fig
     */
    public void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }
        MutableGraphSupport.enableSaveAction();
        this.visible = visible;
    }

    /**
     * Set whether this Fig can be resized
     *
     * @param resizable true to make this Fig resizable
     */
    public void setResizable(boolean resizable) {
        this.resizable = resizable;
    }

    /**
     * Set whether this Fig can be moved
     *
     * @param movable true to make this Fig movable
     */
    public void setMovable(boolean movable) {
        this.movable = movable;
    }

    final public boolean isRemoveStarted() {
        return removeStarted;
    }

    public BasicStroke getStroke() {

        BasicStroke stroke = null;

        stroke = new BasicStroke(getLineWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);

        switch (_dashStyle) {

            case 3: { // DOUBLE
                stroke = new BasicStroke(getLineWidth() / 3, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_MITER);
                break;
            }

            case 2: { // DOTTED (fix this one)
                stroke =
                    new BasicStroke(getLineWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10f, new float[] {getLineWidth(), getLineWidth()}, 0f);
                break;
            }

            case 1: { // DASHED
                stroke =
                    new BasicStroke(getLineWidth(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10f, new float[] {5 * getLineWidth(), 3 * getLineWidth()}, 0f);
                break;
            }

        }

        return stroke;
    }

    public EditorMultiPanel getEditorMultiPanel() {
        return null;
    }

    public void setOffset(int x, int y) {
        this.xOffset = x;
        this.yOffset = y;
    }

    public void setMarginOffset(int x, int y) {
        this.leftMarginOffset = x;
        this.topMarginOffset = y;
    }

    public void setRemoveStarted(boolean removeStarted) {
        this.removeStarted = removeStarted;
    }

}

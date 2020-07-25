package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Rectangle;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebstrada.formreturn.manager.gef.graph.GraphController;
import com.ebstrada.formreturn.manager.gef.graph.GraphEvent;
import com.ebstrada.formreturn.manager.gef.graph.GraphListener;
import com.ebstrada.formreturn.manager.gef.graph.GraphModel;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;

/**
 * A Layer like found in many drawing applications. It contains a collection of
 * Figs, ordered from back to front. Each LayerPerspective contains part of the
 * overall picture that the user is drawing. LayerPerspective is different from
 * LayerDiagram in that it assumes that you are drawing a connected graph that
 * is represented in a GraphModel and controlled by a GraphController.
 */

public class LayerPerspective extends LayerDiagram implements GraphListener {

    private static final long serialVersionUID = -3219953846728127850L;

    /**
     * The space between node FigNodes that are automatically places.
     */
    public static final int GAP = 16;

    /**
     * The underlying connected graph to be visualized.
     */
    private GraphModel _gm;

    private GraphController _controller;

    /**
     * Classes of NetNodes and NetEdges that are to be visualized in this
     * perspective.
     */
    protected Vector _allowedNetClasses = new Vector();

    /**
     * Rectangles of where to place nodes that are automatically added.
     */
    protected Hashtable _nodeTypeRegions = new Hashtable();

    private static Log LOG = LogFactory.getLog(LayerPerspective.class);

    // //////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a new LayerPerspective with the given name, and add it to the
     * menu of layers. Needs-More-Work: I have not implemented a menu of layers
     * yet. I don't know if that is really the right user interface
     */
    public LayerPerspective(String name, GraphModel gm) {
        super(name);
        _gm = gm;
        _controller = null;
        _gm.addGraphEventListener(this);
    }

    public LayerPerspective(String name, GraphModel gm, GraphController controller) {
        super(name);
        _gm = gm;
        _controller = controller;
        _gm.addGraphEventListener(this);
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * Reply the GraphModel of the underlying connected graph.
     */
    public GraphModel getGraphModel() {
        return _gm;
    }

    public void setGraphModel(GraphModel gm) {
        _gm.removeGraphEventListener(this);
        _gm = gm;
        _gm.addGraphEventListener(this);
    }

    /**
     * Reply the GraphController of the underlying connected graph.
     */
    public GraphController getGraphController() {
        return _controller;
    }

    public void setGraphController(GraphController controller) {
        _controller = controller;
    }

    /**
     * Add a node class of NetNodes or NetEdges to what will be shown in this
     * perspective.
     */
    public void allowNetClass(Class c) {
        _allowedNetClasses.addElement(c);
    }

    // //////////////////////////////////////////////////////////////
    // Layer API

    /**
     * Add a Fig to the contents of this Layer. Items are added on top of all
     * other items. If a node is explicitly added then accept it regardless of
     * the predicate, and add it to the net.
     */
    // public void add(Fig f) { super.add(f); }

    /**
     * Remove the given Fig from this layer.
     */
    // public void remove(Fig f) { super.remove(f); }
    // //////////////////////////////////////////////////////////////
    // node placement
    public void addNodeTypeRegion(Class nodeClass, Rectangle region) {
        _nodeTypeRegions.put(nodeClass, region);
    }

    public void putInPosition(Fig f) {
        Class nodeClass = f.getOwner().getClass();
        Rectangle placementRegion = (Rectangle) _nodeTypeRegions.get(nodeClass);
        if (placementRegion != null) {
            f.setLocation(placementRegion.x, placementRegion.y);
            bumpOffOtherNodesIn(f, placementRegion, false, true);
        }
    }

    public void bumpOffOtherNodesIn(Fig newFig, Rectangle bounds, boolean stagger,
        boolean vertical) {
        Rectangle bbox = newFig.getBounds();
        int origX = bbox.x, origY = bbox.y;
        int col = 0, row = 0, i = 1;
        while (bounds.intersects(bbox)) {
            Enumeration overlappers = nodesIn(bbox);
            if (!overlappers.hasMoreElements()) {
                return;
            }
            int unitOffset = ((i + 1) / 2) * ((i % 2 == 0) ? -1 : 1);
            if (vertical) {
                bbox.y = origY + unitOffset * (bbox.height + GAP);
            } else {
                bbox.x = origX + unitOffset * (bbox.width + GAP);
            }
            newFig.setLocation(bbox.x, bbox.y);
            if (!(bounds.intersects(bbox))) {
                int x = bounds.x;
                int y = bounds.y;
                if (vertical) {
                    col++;
                    x = bbox.x + bbox.width + GAP;
                    if (stagger) {
                        y += (col % 2) * (bbox.height + GAP) / 2;
                    }
                } else {
                    row++;
                    y = bbox.y + bbox.height + GAP;
                    if (stagger) {
                        x += (row % 2) * (bbox.width + GAP) / 2;
                    }
                }
                newFig.setLocation(x, y);
                bbox.setLocation(x, y);
            }
            i++;
        }
    }

    // //////////////////////////////////////////////////////////////
    // nofitications and updates

    public void graphChanged(GraphEvent ge) {
        // needs-more-work
    }

    /**
     * Test to determine if a given NetNode should have a FigNode in this layer.
     * Normally checks NetNode class against a list of allowable classes. For
     * more sophisticated rules, override this method. <A
     * HREF="../features.html#multiple_perspectives">
     * <TT>FEATURE: multiple_perspectives</TT></A>
     */
    public boolean shouldShow(Object obj) {
        if (_allowedNetClasses.size() > 0 && !_allowedNetClasses.contains(obj.getClass())) {
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.gef.graph.GraphListener#edgeAdded(com.ebstrada.formreturn.manager.gef.graph.GraphEvent)
     */
    public void edgeAdded(GraphEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.gef.graph.GraphListener#edgeRemoved(com.ebstrada.formreturn.manager.gef.graph.GraphEvent)
     */
    public void edgeRemoved(GraphEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.gef.graph.GraphListener#nodeAdded(com.ebstrada.formreturn.manager.gef.graph.GraphEvent)
     */
    public void nodeAdded(GraphEvent e) {
        // TODO Auto-generated method stub

    }

    /* (non-Javadoc)
     * @see com.ebstrada.formreturn.manager.gef.graph.GraphListener#nodeRemoved(com.ebstrada.formreturn.manager.gef.graph.GraphEvent)
     */
    public void nodeRemoved(GraphEvent e) {
        // TODO Auto-generated method stub

    }

    /**
     * Test to determine if a given NetEdge should have an FigEdge in this
     * layer. Normally checks NetNode class against a list of allowable classes.
     * For more sophisticated rules, override this method. <A
     * HREF="../features.html#multiple_perspectives">
     * <TT>FEATURE: multiple_perspectives</TT></A>
     */
    // public boolean shouldShow(NetEdge a) {
    // if (_allowedNetClasses.size() > 0 &&
    // !_allowedNetClasses.contains(a.getClass()))
    // return false;
    // if (getPortFig(a.getSourcePort()) == null ||
    // getPortFig(a.getDestPort()) == null)
    // return false;
    // return true;
    // }
} /* end class LayerPerspective */

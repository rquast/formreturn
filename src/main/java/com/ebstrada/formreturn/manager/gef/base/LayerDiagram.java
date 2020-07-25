package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigPainter;
import com.ebstrada.formreturn.manager.gef.presentation.FigText;

/**
 * A Layer like found in many drawing applications. It contains a collection of
 * Fig's, ordered from back to front. Each LayerDiagram contains part of the
 * overall picture that the user is drawing. Needs-More-Work: eventually add a
 * "Layers" menu to the Editor. <A HREF="../features.html#graph_visualization">
 * <TT>FEATURE: graph_visualization</TT></A>
 */
public class LayerDiagram extends Layer {

    private static final long serialVersionUID = 6193765162314431069L;

    /**
     * The Fig's that are contained in this layer.
     */
    private List<Fig> _contents = new ArrayList<Fig>();

    /**
     * A counter so that layers have default names like 'One', 'Two', ...
     */
    protected static int _nextLayerNumbered = 1;

    private static Log LOG = LogFactory.getLog(LayerDiagram.class);

    // //////////////////////////////////////////////////////////////
    // constuctors and related methods

    /**
     * Construct a new LayerDiagram with a default name and do not put it on the
     * Layer's menu.
     */
    public LayerDiagram() {
        this("Layer" + numberWordFor(_nextLayerNumbered++));
    }

    /**
     * Construct a new LayerDiagram with the given name, and add it to the menu
     * of layers. Needs-More-Work: I have not implemented a menu of layers yet.
     * I don't know if that is really the right user interface.
     */
    public LayerDiagram(String name) {
        super(name);
        _onMenu = true;
    }

    @Override public Enumeration elements() {
        return Collections.enumeration(_contents);
    }

    /**
     * A utility function to give the spelled-out word for numbers.
     */
    protected static String numberWordFor(int n) {
        switch (n) {

            case 1:
                return "One";

            case 2:
                return "Two";

            case 3:
                return "Three";

            case 4:
                return "Four";

            case 5:
                return "Five";

            case 6:
                return "Six";

            case 7:
                return "Seven";

            case 8:
                return "Eight";

            case 9:
                return "Nine";

            default:
                return "Layer " + n;
        }
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * Add a Fig to the contents of this layer. Items are added on top of all
     * other items.
     *
     * @param f the fig to add
     * @throws IllegalArgumentException if the fig is null
     */
    @Override public void add(Fig f) {
        if (f == null) {
            throw new IllegalArgumentException("Attempted to add a null fig to a LayerDiagram");
        }

        _contents.remove(f); // act like a set
        _contents.add(f);
        f.setLayer(this);
        f.endTrans();
    }

    /**
     * Add a Fig to the contents of this layer. Items are added on top of all
     * other items.
     *
     * @param f the fig to insert
     * @throws IllegalArgumentException if the fig is null
     */
    public void insertAt(Fig f, int index) {
        if (f == null) {
            throw new IllegalArgumentException("Attempted to insert a null fig to a LayerDiagram");
        }

        _contents.remove(f); // act like a set
        _contents.add(index, f);
        f.setLayer(this);
        f.endTrans();
    }

    /**
     * Add a Fig to the contents of this layer. Items are added on top of all
     * other items.
     *
     * @param f the fig to insert
     * @throws IllegalArgumentException if the fig is null
     */
    public int indexOf(Fig f) {
        if (f == null) {
            throw new IllegalArgumentException(
                "Attempted to find the index of a null fig in a LayerDiagram");
        }

        return _contents.indexOf(f);
    }

    /**
     * Remove the given Fig from this layer.
     */
    @Override public void remove(Fig f) {
        _contents.remove(f);
        f.endTrans();

        // this was removed because if you want to use undo,
        // there is no way for the fig which was deleted from
        // the layer to know which layer it was deleted from.
        // f.setLayer(null);
    }

    /**
     * Test if the given Fig is in this layer.
     *
     * @param f
     * @return
     */
    public boolean contains(Fig f) {
        return _contents.contains(f);
    }

    /**
     * Reply the contents of this layer. Do I really want to do this?
     */
    @Override public List getContents() {
        return Collections.unmodifiableList(_contents);
    }

    /**
     * Reply the 'top' Fig under the given (mouse) coordinates. Needs-More-Work:
     * For now, just do a linear search. Later, optimize this routine using Quad
     * Trees (or other) techniques.
     */
    @Override public Fig hit(Rectangle r) {

        /* search backward so that highest item is found first */
        for (int i = _contents.size() - 1; i >= 0; i--) {
            Fig f = (Fig) _contents.get(i);
            if (f.hit(r)) {
                return f;
            }
        }

        return null;
    }

    /**
     * Delete all Fig's from this layer.
     */
    @Override public void removeAll() {
        for (int i = _contents.size() - 1; i >= 0; i--) {
            Fig f = (Fig) _contents.get(i);
            f.setLayer(null);
        }

        _contents.clear();
        // notify?
    }

    /**
     * Find the Fig that visualise the given model element in this layer, or
     * null if there is none.
     */
    @Override public Fig presentationFor(Object obj) {
        int figCount = _contents.size();
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig fig = (Fig) _contents.get(figIndex);
            if (fig.getOwner() == obj) {
                return fig;
            }
        }

        return null;
    }

    /**
     * Find the all Figs that visualise the given model element in this layer,
     * or null if there is none.
     */
    public List presentationsFor(Object obj) {
        ArrayList presentations = new ArrayList();
        int figCount = _contents.size();
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig fig = (Fig) _contents.get(figIndex);
            if (fig.getOwner() == obj) {
                presentations.add(fig);
            }
        }

        return presentations;
    }

    public int presentationCountFor(Object obj) {
        int count = 0;
        int figCount = _contents.size();
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig fig = (Fig) _contents.get(figIndex);
            if (fig.getOwner() == obj) {
                count++;
            }
        }

        return count;
    }

    // //////////////////////////////////////////////////////////////
    // painting methods

    /**
     * Paint all the Fig's that belong to this layer.
     */
    @Override public void paintContents(Graphics g) { // kept for backwards compatibility
        paintContents(g, null);
    }

    /**
     * Paint all the Fig's that belong to this layer using a given FigPainter.
     * If painter is null, the Fig's are painted directly.
     */
    @Override public void paintContents(Graphics g, FigPainter painter) {
        Rectangle clipBounds = g.getClipBounds();
        Iterator figsIter;
        synchronized (_contents) {
            figsIter = (new ArrayList(_contents)).iterator();
        }
        while (figsIter.hasNext()) {
            Fig fig = (Fig) figsIter.next();
            if (clipBounds == null || fig.intersects(clipBounds)) {
                if (painter == null) {
                    if (fig instanceof FigText) {
                        ((FigText) fig).setRenderableText(null);
                    }
                    fig.paint(g);
                } else {
                    painter.paint(g, fig);
                }
            }
        }
    }

    // //////////////////////////////////////////////////////////////
    // ordering of Figs

    /**
     * Reorder the given Fig in this layer.
     */
    @Override public void sendToBack(Fig f) {
        _contents.remove(f);
        _contents.add(0, f);
    }

    /**
     * Reorder the given Fig in this layer.
     */
    @Override public void bringToFront(Fig f) {
        _contents.remove(f);
        _contents.add(f);
    }

    /**
     * Reorder the given Fig in this layer. Needs-more-work: Should come
     * backward/forward until they change positions with an object they overlap.
     * Maybe...
     */
    @Override public void sendBackward(Fig f) {
        int i = _contents.indexOf(f);
        if (i == -1 || i == 0) {
            return;
        }

        Fig prevFig = _contents.get(i - 1);
        _contents.set(i, prevFig);
        _contents.set(i - 1, f);
    }

    /**
     * Reorder the given Fig in this layer.
     */
    @Override public void bringForward(Fig f) {
        int i = _contents.indexOf(f);
        if (i == -1 || i == _contents.size() - 1) {
            return;
        }

        Fig nextFig = _contents.get(i + 1);
        _contents.set(i, nextFig);
        _contents.set(i + 1, f);
    }

    /**
     * Reorder the given Fig in this layer.
     */
    @Override public void bringInFrontOf(Fig f1, Fig f2) {
        int i1 = _contents.indexOf(f1);
        int i2 = _contents.indexOf(f2);
        if (i1 == -1) {
            return;
        }

        if (i2 == -1) {
            return;
        }

        if (i1 >= i2) {
            return;
        }

        _contents.remove(f1);
        _contents.add(i2, f1);
        // Object frontFig = _contents.elementAt(i1);
        // Object backFig = _contents.elementAt(i2);
        // _contents.setElementAt(frontFig, i2);
        // _contents.setElementAt(backFig, i1);
    }

    /**
     * Reorder the given Fig in this layer.
     */
    @Override public void reorder(Fig f, int function) {
        switch (function) {

            case ReorderAction.SEND_TO_BACK:
                sendToBack(f);
                break;

            case ReorderAction.BRING_TO_FRONT:
                bringToFront(f);
                break;

            case ReorderAction.SEND_BACKWARD:
                sendBackward(f);
                break;

            case ReorderAction.BRING_FORWARD:
                bringForward(f);
                break;
        }
    }

    @Override public void preSave() {
        validate();
        for (int i = 0; i < _contents.size(); i++) {
            Fig f = (Fig) _contents.get(i);
            f.preSave();
        }
    }

    /**
     * Scan the contents of the layer before a save takes place to validate its
     * state is legal.
     */
    private void validate() {
        for (int i = _contents.size() - 1; i >= 0; --i) {
            Fig f = (Fig) _contents.get(i);
            if (f.isRemoveStarted()) {
                // TODO: Once JRE1.4 is minimum support we should use assertions
                LOG.error("A fig has been found that should have been removed " + f.toString());
                _contents.remove(i);
            } else if (f.getLayer() != this) {
                // TODO: Once JRE1.4 is minimum support we should use assertions
                LOG.error("A fig has been found that doesn't refer back to the correct layer " + f
                    .toString() + " - " + f.getLayer());
                f.setLayer(this);
            }
        }
    }

    @Override public void postSave() {
        for (int i = 0; i < _contents.size(); i++) {
            ((Fig) _contents.get(i)).postSave();
        }
    }

    @Override public void postLoad() {
        for (int i = 0; i < _contents.size(); i++) {
            ((Fig) _contents.get(i)).postLoad();
        }
    }
}

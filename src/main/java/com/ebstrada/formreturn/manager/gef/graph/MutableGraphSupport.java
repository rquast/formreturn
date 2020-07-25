package com.ebstrada.formreturn.manager.gef.graph;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.Action;

import com.ebstrada.formreturn.manager.gef.di.GraphElement;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;

/**
 * An abstract class that makes it easier to implement your own version of
 * MutableGraphModel. This class basically includes the code for event
 * notifications, so that you don't have to write that. It also provides a few
 * utility methods.
 */

public abstract class MutableGraphSupport implements MutableGraphModel, java.io.Serializable {

    private Vector _graphListeners;

    private static Action saveAction;

    public MutableGraphSupport() {
    }

    public List getGraphListeners() {
        return _graphListeners;
    }

    // //////////////////////////////////////////////////////////////
    // MutableGraphModel implementation



    // //////////////////////////////////////////////////////////////
    // listener registration

    public void addGraphEventListener(GraphListener listener) {
        if (_graphListeners == null) {
            _graphListeners = new Vector();
        }
        _graphListeners.addElement(listener);
    }

    public void removeGraphEventListener(GraphListener listener) {
        if (_graphListeners == null) {
            return;
        }
        _graphListeners.removeElement(listener);
    }

    // //////////////////////////////////////////////////////////////
    // event notifications

    public void fireNodeAdded(Object node) {
        if (saveAction != null && !saveAction.isEnabled()) {
            saveAction.setEnabled(true);
        }
        if (_graphListeners == null) {
            return;
        }
        GraphEvent ge = new GraphEvent(this, node);
        Enumeration listeners = _graphListeners.elements();
        while (listeners.hasMoreElements()) {
            GraphListener listen = (GraphListener) listeners.nextElement();
            listen.nodeAdded(ge);
        }
    }

    public void fireNodeRemoved(Object node) {
        if (saveAction != null && !saveAction.isEnabled()) {
            saveAction.setEnabled(true);
        }
        if (_graphListeners == null) {
            return;
        }
        GraphEvent ge = new GraphEvent(this, node);
        Enumeration listeners = _graphListeners.elements();
        while (listeners.hasMoreElements()) {
            GraphListener listen = (GraphListener) listeners.nextElement();
            listen.nodeRemoved(ge);
        }
    }


    public void fireGraphChanged() {
        if (saveAction != null && !saveAction.isEnabled()) {
            saveAction.setEnabled(true);
        }
        if (_graphListeners == null) {
            return;
        }
        GraphEvent ge = new GraphEvent(this, null);
        Enumeration listeners = _graphListeners.elements();
        while (listeners.hasMoreElements()) {
            GraphListener listen = (GraphListener) listeners.nextElement();
            listen.graphChanged(ge);
        }
    }

    public static void setSaveAction(Action action) {
        saveAction = action;
    }

    public static void enableSaveAction() {
        if (saveAction != null) {
            saveAction.setEnabled(true);
        }
    }

    public void removeNode(Object node) {
        fireNodeRemoved(node);
    }

    /**
     * Add the given node to the graph, if valid.
     */
    public void addNode(Object node) {
        fireNodeAdded(node);
    }

    /**
     * Remove the given edge from the graph.
     */
    public void removeFig(Fig fig) {
        if (fig instanceof GraphElement) {
            throw new IllegalArgumentException(
                "Use removeEdge or removeNode to remove a complex Fig");
        }
        fig.removeFromDiagram();
        fireGraphChanged();
    }

    /**
     * Remove all the nodes from the graph.
     */
    public void removeAllNodes() {
        fireGraphChanged();
    }

    /**
     * Remove all the edges from the graph.
     */
    public void removeAllEdges() {
        fireGraphChanged();
    }

    /**
     * Remove all nodes and edges to reset the graph.
     */
    public void removeAll() {
        fireGraphChanged();
    }

    /**
     * Returns true if handle can be enclosed into encloser.
     */
    public boolean isEnclosable(Object handle, Object encloser) {
        return true;
    }

}

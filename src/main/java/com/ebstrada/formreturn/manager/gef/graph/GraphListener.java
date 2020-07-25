package com.ebstrada.formreturn.manager.gef.graph;

import java.util.EventListener;

/**
 * This defines a set of event notifications that objects can register for if
 * they are interested in changes to the connected graph. For example,
 * LayerPerspective implements this interface to update the Figs it contains
 * whenever a node or edge is added or removed from the GraphModel.
 *
 * @see com.ebstrada.formreturn.manager.gef.base.LayerPerspective
 */

public interface GraphListener extends EventListener {
    void nodeAdded(GraphEvent e);

    void edgeAdded(GraphEvent e);

    void nodeRemoved(GraphEvent e);

    void edgeRemoved(GraphEvent e);

    void graphChanged(GraphEvent e);
}

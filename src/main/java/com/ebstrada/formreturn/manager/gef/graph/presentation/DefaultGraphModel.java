package com.ebstrada.formreturn.manager.gef.graph.presentation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ebstrada.formreturn.manager.gef.graph.MutableGraphSupport;

/**
 * This interface provides a facade to a net-level representation. Similiar in
 * concept to the Swing class TreeModel. This implementation of GraphModel uses
 * the GEF classes NetList, NetNode, NetPort, and NetEdge. If you implement your
 * own GraphModel, you can use your own application-specific classes.
 *
 * @see NetList
 * @see NetNode
 * @see NetPort
 * @see NetEdge
 * @see AdjacencyListGraphModel
 */

public class DefaultGraphModel extends MutableGraphSupport implements java.io.Serializable {

    private static final long serialVersionUID = 8098329898758384131L;

    private static Log LOG = LogFactory.getLog(DefaultGraphModel.class);

    // //////////////////////////////////////////////////////////////
    // constructors

    public DefaultGraphModel() {
    }

    /**
     * Remove all nodes and edges to reset the graph.
     */
    @Override public void removeAll() {
        super.removeAll();
    }
}

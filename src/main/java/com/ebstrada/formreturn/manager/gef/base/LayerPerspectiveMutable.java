package com.ebstrada.formreturn.manager.gef.base;

import com.ebstrada.formreturn.manager.gef.graph.GraphModel;
import com.ebstrada.formreturn.manager.gef.graph.MutableGraphModel;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;

/**
 * A LayerPerspective with an underlying MutableGraphModel.
 * As figures are added and removed the underlying MutableGraphModel is
 * updated.
 */

public class LayerPerspectiveMutable extends LayerPerspective {

    private static final long serialVersionUID = 4692683431762315041L;

    /**
     * The underlying connected graph to be visualized.
     */
    private MutableGraphModel mutableGraphModel;

    // //////////////////////////////////////////////////////////////
    // constructors

    public LayerPerspectiveMutable(String name, MutableGraphModel mgm) {
        super(name, mgm);
        mutableGraphModel = mgm;
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    @Override public GraphModel getGraphModel() {
        return getMutableGraphModel();
    }

    @Override public void setGraphModel(GraphModel gm) {
        setMutableGraphModel((MutableGraphModel) gm);
    }

    public MutableGraphModel getMutableGraphModel() {
        return mutableGraphModel;
    }

    public void setMutableGraphModel(MutableGraphModel mgm) {
        super.setGraphModel(mgm);
        mutableGraphModel = mgm;
    }

    // //////////////////////////////////////////////////////////////
    // Layer API

    @Override public void add(Fig fig) {
        Object owner = fig.getOwner();
        super.add(fig);
        // if ( owner != null && _mgm.canAddNode(owner))
        // _mgm.addNode(owner);
        // FigEdges are added by the underlying MutableGraphModel.
    }

    @Override public void remove(Fig f) {
        super.remove(f);
        Object owner = f.getOwner();
    }
} /* end class LayerPerspectiveMutable */

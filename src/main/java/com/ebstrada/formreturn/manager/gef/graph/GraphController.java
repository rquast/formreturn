package com.ebstrada.formreturn.manager.gef.graph;

import java.util.List;

/**
 * This interface is the basis for each class that handles the control of pairs
 * of data and representational objects.
 */
public interface GraphController extends java.io.Serializable {
    /**
     * Add a new presentation to the list of known presentations. Each
     * presentation consists of a data object (referrer) and an object for its
     * graphical representation.
     */
    public boolean addPresentation(Object representation, Object referrer);

    /**
     * Remove a presentation from the list of known presentations.
     */
    public boolean removePresentation(Object element);

    /**
     * Get the graphical representation of the given object.
     */
    public Object presentationFor(Object data);

    /**
     * Get the related data object for the given object.
     */
    public Object referrerFor(Object presentation);

    /**
     * Tests, if the given object is a node known by the controller.
     */
    public boolean containsNode(Object node);

    /**
     * Tests, if the given object is an edge known by the controller.
     */
    public boolean containsEdge(Object edge);

    public List getNodes();

    public List getEdges();

    /**
     * Counts the number of presentations known to the GraphController for the
     * given referrer.
     */
    public int countPresentationsFor(Object referrer);
}

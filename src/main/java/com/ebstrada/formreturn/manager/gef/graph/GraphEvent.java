package com.ebstrada.formreturn.manager.gef.graph;

import java.util.EventObject;

/**
 * A notification that a graph has changed. The source is the object that
 * implements GraphModel. The argument is the specific node or edge that was
 * involved when a node or edge is added or removed. The argument is null if the
 * entire graph changed.
 */

public class GraphEvent extends EventObject implements java.io.Serializable {
    private static final long serialVersionUID = -7003555460670540322L;
    /**
     * The specific node, port, or arc that was modified.
     */
    private Object _arg;

    public GraphEvent(Object src) {
        this(src, null);
    }

    public GraphEvent(Object src, Object arg) {
        super(src);
        _arg = arg;
    }

    public Object getArg() {
        return _arg;
    }
}

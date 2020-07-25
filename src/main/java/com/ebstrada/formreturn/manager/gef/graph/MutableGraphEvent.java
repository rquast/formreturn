package com.ebstrada.formreturn.manager.gef.graph;

import java.util.EventObject;

/**
 * A notification that a graph has changed. The source is the object that
 * implements MutableGraphModel. The argument is the specific node or edge that
 * was involved when a node or edge is added or removed. The argument is null if
 * the entire graph changed.
 */

public class MutableGraphEvent extends EventObject {
    private static final long serialVersionUID = -9143159532643088192L;
    /**
     * The specific node, port, or arc that was modified.
     */
    private Object _arg;

    public MutableGraphEvent(Object src) {
        this(src, null);
    }

    public MutableGraphEvent(Object src, Object arg) {
        super(src);
        _arg = arg;
    }

    public Object getArg() {
        return _arg;
    }
}

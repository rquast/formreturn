package com.ebstrada.formreturn.manager.gef.event;

import java.util.EventObject;
import java.util.Vector;

/**
 * An event object that contains information about the current selection(s) in
 * an Editor. These events are sent to registered GraphSelectionListeners
 * whenever the Editor's selection changes.
 *
 * @see GraphSelectionListener
 * @see com.ebstrada.formreturn.manager.gef.base.Editor
 * @see com.ebstrada.formreturn.manager.gef.base.SelectionManager
 */
public class GraphSelectionEvent extends EventObject {

    private static final long serialVersionUID = 7055361155230503398L;
    private Vector _selections;

    // //////////////////////////////////////////////////////////////
    // constructor
    public GraphSelectionEvent(Object src, Vector selections) {
        super(src);
        _selections = selections;
    }

    // //////////////////////////////////////////////////////////////
    // accessors
    public Vector getSelections() {
        return _selections;
    }

} /* end class GraphSelectionEvent */

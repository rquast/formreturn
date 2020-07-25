package com.ebstrada.formreturn.manager.gef.event;

import java.util.EventObject;
import java.util.Vector;

/**
 * An event object that contains information about the current selection(s) in
 * an Editor. These events are sent to registered GraphSelectionListeners
 * whenever the Editor's selection changes.
 *
 * @see ModeChangeListener
 * @see com.ebstrada.formreturn.manager.gef.base.Editor
 * @see com.ebstrada.formreturn.manager.gef.base.SelectionManager
 */
public class ModeChangeEvent extends EventObject {

    private static final long serialVersionUID = -6352595981387658622L;
    private Vector _modes;

    // //////////////////////////////////////////////////////////////
    // constructor
    public ModeChangeEvent(Object src, Vector modes) {
        super(src);
        _modes = modes;
    }

    // //////////////////////////////////////////////////////////////
    // accessors
    public Vector getModes() {
        return _modes;
    }

} /* end class ModeChangeEvent */

package com.ebstrada.formreturn.manager.gef.event;

import java.util.EventListener;

/**
 * An interface that must be implemented by any object that wants to be notified
 * when an Editor changes its selection(s).
 *
 * @see com.ebstrada.formreturn.manager.gef.base.Editor
 * @see com.ebstrada.formreturn.manager.gef.base.SelectionManager
 */

public interface ModeChangeListener extends EventListener {

    void modeChange(ModeChangeEvent mce);
    // ? void modeAdded(modeChangeEvent mce);
    // ? void modeRemoved(modeChangeEvent mce);

} /* end class ModeChangeListener */

package com.ebstrada.formreturn.manager.gef.util;

import java.beans.VetoableChangeListener;
import java.util.Vector;

public interface VetoableChangeEventSource {
    void addVetoableChangeListener(VetoableChangeListener l);

    void removeVetoableChangeListener(VetoableChangeListener l);

    Vector getVetoableChangeListeners();
}

package com.ebstrada.formreturn.manager.ui.sdm.panel;

import org.jdesktop.swingx.JXTaskPane;

public abstract class SDMPanel extends JXTaskPane {

    private static final long serialVersionUID = 1L;

    public abstract void updatePanel();

    public abstract void removeListeners();

}
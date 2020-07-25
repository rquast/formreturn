package com.ebstrada.formreturn.manager.ui.cdm.panel;

import org.jdesktop.swingx.JXTaskPane;

public abstract class CDMPanel extends JXTaskPane {

    private static final long serialVersionUID = 1L;

    public abstract void updatePanel();

    public abstract void removeListeners();

}

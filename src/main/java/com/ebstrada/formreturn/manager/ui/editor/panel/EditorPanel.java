package com.ebstrada.formreturn.manager.ui.editor.panel;

import org.jdesktop.swingx.JXTaskPane;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;

public abstract class EditorPanel extends JXTaskPane {

    private static final long serialVersionUID = 1L;

    public abstract void setSelectedElement(Fig selectedFig);

    public abstract void updatePanel();

    public abstract void removeListeners();

}
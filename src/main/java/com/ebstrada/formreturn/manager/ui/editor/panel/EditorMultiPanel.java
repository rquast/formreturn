package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.util.ArrayList;
import java.util.Vector;

import org.jdesktop.swingx.JXTaskPane;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;

public abstract class EditorMultiPanel extends JXTaskPane {

    private static final long serialVersionUID = 1L;

    public abstract void setSelectedElements(Vector selectedFigs);

    public abstract void updatePanel();

    public abstract void removeListeners();

}

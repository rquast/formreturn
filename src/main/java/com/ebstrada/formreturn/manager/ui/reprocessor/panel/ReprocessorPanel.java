package com.ebstrada.formreturn.manager.ui.reprocessor.panel;

import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;

public abstract class ReprocessorPanel extends EditorPanel {

    private static final long serialVersionUID = 1L;

    public abstract void updatePanel();

    public abstract void removeListeners();
}

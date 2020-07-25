package com.ebstrada.formreturn.manager.ui.frame;

import java.util.Vector;

import javax.swing.JPanel;

import com.ebstrada.formreturn.manager.gef.base.Editor;
import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.ui.DocumentAttributes;
import com.ebstrada.formreturn.manager.gef.ui.PageAttributes;

public abstract class EditorFrame extends JPanel {

    private static final long serialVersionUID = 1L;

    public abstract JGraph getGraph();

    public abstract void setActiveEditor();

    public abstract void unpressAllButtons();

    public abstract Editor getEditor();

    public abstract boolean closeEditorFrame();

    public abstract DocumentAttributes getDocumentAttributes();

    public abstract PageAttributes getPageAttributes();

    public abstract void updatePropertyBox(Vector<?> sels);

    public abstract boolean isFigFullyVisible(Fig fig);

    public abstract void setFinishedLoading(boolean finishedLoading);

    public void setTitle(String title) {
        this.setName(title);
    }

    public String getTitle() {
        return this.getName();
    }

    public abstract void rebuildPreview();

}

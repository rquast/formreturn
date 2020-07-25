package com.ebstrada.formreturn.manager.gef.undo.memento;

import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.base.Layer;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.undo.Memento;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;

public class FigAddMemento extends Memento {

    private Layer lay;
    private Fig fig;
    private Fig encFig;
    private boolean vis;

    public FigAddMemento(Fig f) {
        fig = f;
        lay = fig.getLayer();
        encFig = f.getEnclosingFig();
        vis = fig.isVisible();
    }

    @Override public void redo() {
        UndoManager.getInstance().addMementoLock(this);
        fig.setEnclosingFig(encFig);
        if (lay != null) {
            Globals.curEditor().addPropertyChangeListener(fig);
            lay.add(fig);
        } else {
            if (fig.getLayer() != null) {
                lay = fig.getLayer();
                Globals.curEditor().addPropertyChangeListener(fig);
                lay.add(fig);
            }
        }
        fig.setRemoveStarted(false);
        fig.setVisible(true);
        fig.setMovable(true);
        fig.damage();
        fig.firePropChange("redo", null, null);

        UndoManager.getInstance().removeMementoLock(this);
    }

    @Override public void undo() {
        UndoManager.getInstance().addMementoLock(this);
        Globals.curEditor().removePropertyChangeListener(fig);
        fig.removeFromDiagramNoMemento();
        UndoManager.getInstance().removeMementoLock(this);
    }

}

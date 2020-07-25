package com.ebstrada.formreturn.manager.ui.reprocessor.component;

import com.ebstrada.formreturn.manager.gef.graph.presentation.JGraph;
import com.ebstrada.formreturn.manager.ui.reprocessor.frame.ReprocessorFrame;

public class ReprocessorGraph extends JGraph {

    private static final long serialVersionUID = 1L;

    private ReprocessorFrame reprocessorFrame;

    public ReprocessorFrame getReprocessorFrame() {
        return reprocessorFrame;
    }

    public void setReprocessorFrame(ReprocessorFrame reprocessorFrame) {
        this.reprocessorFrame = reprocessorFrame;
    }

}

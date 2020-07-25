package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.MouseEvent;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegment;
import com.ebstrada.formreturn.manager.ui.frame.EditorFrame;

public class ModeCreateFigSegment extends ModeCreate {

    private static final long serialVersionUID = 1L;

    @Override public String instructions() {
        return "Drag to define a segment";
    }

    @Override public Fig createNewItem(MouseEvent me, int snapX, int snapY) {

        java.awt.Component component = (java.awt.Component) me.getSource();
        EditorFrame desktopFrame = null;

        while (true) {
            if (component instanceof EditorFrame) {
                desktopFrame = (EditorFrame) component;
                break;
            }
            component = component.getParent();
        }

        return new FigSegment(snapX, snapY, 0, 0, desktopFrame.getPageAttributes().getGUID());
    }
}

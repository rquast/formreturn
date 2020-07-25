package com.ebstrada.formreturn.manager.gef.base;

import java.awt.event.MouseEvent;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigBarcodeReader;
import com.ebstrada.formreturn.manager.ui.frame.EditorFrame;

public class ModeCreateFigBarcodeReader extends ModeCreate {

    private static final long serialVersionUID = 1L;

    @Override public String instructions() {
        return "Drag to define a barcode reader area";
    }

    @Override public Fig createNewItem(MouseEvent me, int snapX, int snapY) {
        return new FigBarcodeReader(snapX, snapY, 0, 0);
    }
}

package com.ebstrada.formreturn.manager.gef.ui;

import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JToggleButton;

import com.ebstrada.formreturn.manager.gef.base.ModeCreateFigSegmentArea;
import com.ebstrada.formreturn.manager.gef.base.ModeSelect;
import com.ebstrada.formreturn.manager.gef.base.SetModeAction;
import com.ebstrada.formreturn.manager.gef.presentation.FigSegmentArea;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.reprocessor.frame.ReprocessorFrame;

public class ReprocessPalette extends Palette {

    private static final long serialVersionUID = 1L;

    ButtonGroup group;

    public ReprocessPalette() {
        super(Palette.HORIZONTAL);
        defineButtons();
    }

    public JToggleButton addToggle(Action a, String toolTip) {
        JToggleButton button = super.addToggle(a);
        button.setToolTipText(Localizer.localize("GefBase", toolTip));
        button.setText(Localizer.localize("GefBase", (String) a.getValue(Action.NAME)));
        return button;
    }

    @Override public void defineButtons() {

        group = new ButtonGroup();

        group.add(addToggle(new SetModeAction(ModeSelect.class, "Select"), "SelectTip"));
        addSeparator();
        group.add(addToggle(new SetModeAction(ModeCreateFigSegmentArea.class, "SegmentStencil"),
            "SegmentStencilTip"));

    }

}

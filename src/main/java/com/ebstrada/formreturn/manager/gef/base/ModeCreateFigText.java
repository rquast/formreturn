package com.ebstrada.formreturn.manager.gef.base;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigText;

/**
 * A Mode to interpert user input while creating a FigText. All of the actual
 * event handling is inherited from ModeCreate. This class just implements the
 * differences needed to make it specific to text.
 */

public class ModeCreateFigText extends ModeCreate {

    private static final long serialVersionUID = 3394093467647491098L;

    public ModeCreateFigText() {
        super();
        _defaultWidth = 15;
        _defaultHeight = 15;
    }

    @Override public String instructions() {
        return "Drag to define a text rectangle, then type";
    }

    /**
     * Create a new FigText instance based on the given mouse down event and the
     * state of the parent Editor.
     */
    @Override public Fig createNewItem(MouseEvent e, int snapX, int snapY) {
        return new FigText(snapX, snapY, 0, 0);
    }

    @Override public void paint(Graphics g) {
        if (null != _newItem) {

            Color OriginalColor = g.getColor();
            g.setColor(Globals.getPrefs().handleColorFor(_newItem));

            Stroke OriginalStroke = ((Graphics2D) g).getStroke();
            Stroke thindashed =
                new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f,
                    new float[] {8.0f, 3.0f, 2.0f, 3.0f}, 0.0f);
            ((Graphics2D) g).setStroke(thindashed);
            ((Graphics2D) g)
                .drawRect(_newItem.getX() - 2, _newItem.getY() - 2, _newItem.getWidth() + 4,
                    _newItem.getHeight() + 4);
            ((Graphics2D) g).setStroke(OriginalStroke);

            g.setColor(OriginalColor);

        }
    }

} /* end class ModeCreateFigText */

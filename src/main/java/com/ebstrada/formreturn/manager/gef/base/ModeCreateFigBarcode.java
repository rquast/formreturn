package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;

import com.ebstrada.formreturn.manager.gef.di.GraphElement;
import com.ebstrada.formreturn.manager.gef.graph.MutableGraphSupport;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigBarcode;

public class ModeCreateFigBarcode extends ModeCreate {

    private static final long serialVersionUID = -3062009802693268691L;

    /**
     * The image to be placed.
     */
    protected Image _image;

    public ModeCreateFigBarcode() {
        setDefaultWidth(0);
        setDefaultHeight(0);
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * get and set the image to be used for the new FigBarcode.
     */
    public Image image() {
        return _image;
    }

    public void image(Image i) {
        _image = i;
    }

    // //////////////////////////////////////////////////////////////
    // Mode API

    @Override public String instructions() {
        return "Click to place a barcode";
    }

    // //////////////////////////////////////////////////////////////
    // ModeCreate API

    /**
     * Create a new FigBarcode instance based on the given mouse down event and
     * the state of the parent Editor.
     */
    @Override public Fig createNewItem(MouseEvent me, int snapX, int snapY) {
        try {
            return new FigBarcode(snapX, snapY, "FORM ID", "12345-67890", 0.8, true, true);
        } catch (Exception e) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e);
        }
        return null;
    }

    // //////////////////////////////////////////////////////////////
    // event handlers

    @Override public void mousePressed(MouseEvent me) {
        createFig(me);
        if (!(_newItem instanceof GraphElement) && editor
            .getGraphModel() instanceof MutableGraphSupport) {
            ((MutableGraphSupport) (editor.getGraphModel())).fireGraphChanged();
        }
        int x = me.getX(), y = me.getY();

        start();
        anchorX = x;
        anchorY = y;
        Point snapPt = new Point(x, y);
        editor.snap(snapPt);
        if (null == _newItem) {
            _newItem = createNewItem(me, snapPt.x, snapPt.y);
        }
        me.consume();
    }

    public void mouseDragged(MouseEvent me) {
        me.consume();
    }

} /* end class ModeCreateFigBarcode */

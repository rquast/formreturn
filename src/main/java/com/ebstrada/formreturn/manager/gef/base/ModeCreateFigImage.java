package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigImage;

/**
 * A Mode to interpert user input while creating a FigImage. All of the actual
 * event handling is inherited from ModeCreate. This class just implements the
 * differences needed to make it specific to images.
 */

public class ModeCreateFigImage extends ModeCreate {

    private static final long serialVersionUID = -3062009802693268691L;

    /**
     * The image to be placed.
     */
    protected Image _image;

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * get and set the image to be used for the new FigImage.
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
        return "Click to place an image";
    }

    // //////////////////////////////////////////////////////////////
    // ModeCreate API

    /**
     * Create a new FigImage instance based on the given mouse down event and
     * the state of the parent Editor.
     */
    @Override public Fig createNewItem(MouseEvent me, int snapX, int snapY) {
        _image = (new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/gef/Images/Image.png")))
            .getImage();

        return new FigImage(snapX, snapY, _image);
    }

    // //////////////////////////////////////////////////////////////
    // event handlers

    /**
     * When the mouse enters an Editor, create the FigImage and place it at the
     * mouse position.
     */
    @Override public void mouseEntered(MouseEvent me) {
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

    /**
     * When the mouse exits the editor, clean up the display a little.
     */
    @Override public void mouseExited(MouseEvent me) {
        editor.damageAll();
        me.consume();
    }

    /**
     * On mouse down, do nothing.
     */
    @Override public void mousePressed(MouseEvent me) {
        me.consume();
    }

    /**
     * Whem the user drags or moves the mouse, move the FigImage to the current
     * mouse position.
     */
    @Override public void mouseMoved(MouseEvent me) {
        int x = me.getX(), y = me.getY();
        if (_newItem == null) {
            me.consume();
            return;
        }
        editor.damageAll();
        Point snapPt = new Point(x, y);
        editor.snap(snapPt);
        _newItem.setLocation(snapPt.x, snapPt.y);
        editor.damageAll(); /* needed? */
        me.consume();
    }

    /**
     * Exactly the same as mouseMove.
     */
    @Override public void mouseDragged(MouseEvent me) {
        mouseMoved(me);
    }
} /* end class ModeCreateFigImage */

package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Cursor;

/**
 * This class stores the index of the handle that the user is dragging. I
 * originally used a simple int, but some dragHandle() methods need to change
 * the index because new handles can be added during a drag.
 */

public class Handle {

    /**
     * The handle in the northwest corner of a FigNode
     */
    public static final int NORTHWEST = 0;
    /**
     * The handle in the north edge of a FigNode
     */
    public static final int NORTH = 1;
    /**
     * The handle in the northeast corner of a FigNode
     */
    public static final int NORTHEAST = 2;
    /**
     * The handle in the west edge of a FigNode
     */
    public static final int WEST = 3;
    /**
     * The handle in the east edge of a FigNode
     */
    public static final int EAST = 4;
    /**
     * The handle in the southwest corner of a FigNode
     */
    public static final int SOUTHWEST = 5;
    /**
     * The handle in the south edge of a FigNode
     */
    public static final int SOUTH = 6;
    /**
     * The handle in the southeast corner of a FigNode
     */
    public static final int SOUTHEAST = 7;

    // //////////////////////////////////////////////////////////////
    // instance variables

    /**
     * Index of the handle on some Fig that was clicked on.
     */
    public int index;

    /**
     * Instructions to be shown when the user's mouse is hovering over or is
     * dragging this handle
     */
    public String instructions = " ";

    /**
     * Mouse cursor Cursor while hovering or dragging
     */
    public Cursor cursor = null;

    // //////////////////////////////////////////////////////////////
    // constructors

    /**
     * Make a new Handle with the given handle index.
     */
    public Handle(int ind) {
        index = ind;
    }

} /* end class Handle */

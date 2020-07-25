package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Point;

/**
 * Constrains interactions to certain coordinates. For example GuideGrid makes
 * objects snap to a grid. Other subclasses might implement other snapping
 * rules, for example, a polar grid or gravity (objects cling to other objects
 * when they get close).
 *
 * @see GuideGrid
 */

public abstract class Guide implements java.io.Serializable {

    // //////////////////////////////////////////////////////////////
    // geometric constraints

    /**
     * Return a NEW Point that is close to p and on the guideline (e.g.,
     * gridline).
     */
    public final Point snapTo(Point p) {
        Point res = new Point(p.x, p.y);
        snap(res);
        return res;
    }

    /**
     * Modify the given point to satisfy guide conditions (e.g. be on a
     * gridline).
     */
    public abstract void snap(Point p);

    // //////////////////////////////////////////////////////////////
    // user interface

    /**
     * Set the parameters for this guide. E.g., toggle the size of a grid.
     */
    public void adjust() {
    }

    ;
} /* end class Guide */

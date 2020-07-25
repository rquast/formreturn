package com.ebstrada.formreturn.manager.gef.util;

import java.awt.Rectangle;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;

/**
 * Predicate that returns true for FigNode's that intersect the rectangle given
 * in the constructor. Useful for making a EnumerationPredicate that finds Figs
 * intersecting a given Rectangle.
 */
public class PredFigNodeInRect implements Predicate {

    // //////////////////////////////////////////////////////////////
    // instance variables

    Rectangle _r;

    // //////////////////////////////////////////////////////////////
    // constructor

    public PredFigNodeInRect(Rectangle r) {
        _r = r;
    }

    // //////////////////////////////////////////////////////////////
    // Predicate API

    public boolean predicate(Object o) {
        return ((Fig) o).intersects(_r);
    }

}

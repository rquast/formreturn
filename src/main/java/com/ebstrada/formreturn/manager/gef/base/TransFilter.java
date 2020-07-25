package com.ebstrada.formreturn.manager.gef.base;

import java.awt.image.RGBImageFilter;

/**
 * RGBImageFilter that turns on transparency for pixels of a specified color.
 */

class TransFilter extends RGBImageFilter {
    int _transBG;

    public TransFilter(int bg) {
        _transBG = bg;
        canFilterIndexColorModel = true;
    }

    @Override public int filterRGB(int x, int y, int rgb) {
        // background color w/any alpha level? make it transparent
        if ((rgb & 0x00ffffff) == _transBG) {
            return _transBG;
        }
        return 0xff000000 | rgb; // make it 100% opaque
    }
} /* end class TransFilter */

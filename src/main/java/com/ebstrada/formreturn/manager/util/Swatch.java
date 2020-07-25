package com.ebstrada.formreturn.manager.util;

import java.awt.*;
import java.util.*;

import javax.swing.*;

public class Swatch implements Icon {
    protected static Hashtable _swatches = new Hashtable();

    Color _color = Color.black;

    public static Swatch forColor(Color c) {
        Swatch s = (Swatch) _swatches.get(c);
        if (s == null) {
            s = new Swatch(c);
            _swatches.put(c, s);
        }
        return s;
    }

    public Swatch(Color c) {
        _color = c;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
        int w = getIconWidth(), h = getIconHeight();
        g.setColor(_color);
        g.fillRect(x, y, getIconWidth(), getIconHeight());
    }

    public int getIconWidth() {
        return 25;
    }

    public int getIconHeight() {
        return 8;
    }

}

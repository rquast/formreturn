package com.ebstrada.formreturn.manager.gef.presentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ebstrada.formreturn.manager.ui.editor.panel.EditorPanel;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * A FigGroup is a collection of Figs to all be treated as a single item
 *
 * @author Jason Robbins
 */

@XStreamAlias("group") public class FigGroup extends Fig implements NoObfuscation {

    // //////////////////////////////////////////////////////////////
    // instance variables

    /**
     *
     */
    private static final long serialVersionUID = -6310938507588533546L;

    /**
     * The Fig's contained in this FigGroup
     */
    private ArrayList figs;

    private int _extraFrameSpace = 0;

    /**
     * Color of the actual text characters.
     */
    private Color textColor = Color.black;

    /**
     * Color to be drawn behind the actual text characters. Note that this will
     * be a smaller area than the bounding box which is filled with FillColor.
     */
    private Color textFillColor = Color.white;

    /**
     * True if the area behind individual characters is to be filled with
     * TextColor.
     */
    private boolean textFilled = false;

    /**
     * Normally the bounds of the FigGroup is calculated whenever a Fig is
     * added. Setting this flag to false allows multiple adds without the
     * overhead of the calculation each time.
     */
    private boolean suppressCalcBounds;

    // //////////////////////////////////////////////////////////////
    // constructors

    /**
     * Construct a new FigGroup that holds no Figs.
     */
    public FigGroup() {
        super();
        figs = new ArrayList();
    }

    /**
     * Construct a new FigGroup that holds the given Figs.
     */
    public FigGroup(List figs) {
        super();
        this.figs = new ArrayList(figs);
        calcBounds();
    }

    /**
     * Add a Fig to the group. Takes no action if already part of the group.
     * Removes from any other group it may have been in already. New Figs are
     * added on the top.
     *
     * @param fig the Fig to add to this group
     */
    public void addFig(Fig fig) {
        Fig group = fig.getGroup();
        if (group != this) {
            if (group != null) {
                ((FigGroup) group).removeFig(fig);
            }
            this.figs.add(fig);
            fig.setGroup(this);
            calcBounds();
        }
    }

    /**
     * Add a collection of figs to the group.
     *
     * @param figs Collection of figs to be added.
     */
    public void addFigs(Collection figs) {
        Iterator figIter = figs.iterator();
        while (figIter.hasNext()) {
            addFig((Fig) figIter.next());
        }
        calcBounds();
    }

    /**
     * Sets a new collection of figs. The old collection is removed.
     *
     * @param figs Collection of figs to be set.
     */
    public void setFigs(Collection figs) {
        this.figs.clear();
        addFigs(figs);
    }

    /**
     * Accumulate a bounding box for all the Figs in the group. This method is
     * called by many parts of the framework and may cause some performance
     * problems. It is possible to suppress this mthod by calling
     * suppressCalcBounds(true). Be sure to call calcBounds as soon as this
     * suppression is turned off again.
     */
    @Override public void calcBounds() {
        if (suppressCalcBounds) {
            return;
        }
        Rectangle boundingBox = null;

        int figCount = this.figs.size();

        Fig f;
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            f = (Fig) this.figs.get(figIndex);
            if (f.isVisible()) {
                if (boundingBox == null) {
                    boundingBox = f.getBounds();
                } else {
                    boundingBox.add(getSubFigBounds(f));
                }
            }
        }

        if (boundingBox == null) {
            boundingBox = new Rectangle(0, 0, 0, 0);
        }

        _x = boundingBox.x;
        _y = boundingBox.y;
        _w = boundingBox.width;
        _h = boundingBox.height + _extraFrameSpace;
    }

    /**
     * Returns the bounds of the given subfig. This method can be overwritten in
     * order to use different strategies on determining the overall bounds of
     * the FigGroup.
     *
     * @param subFig Subfig of this group to calculate the bounds for.
     * @return Rectangle representing the bounds of the subfig.
     */
    protected Rectangle getSubFigBounds(Fig subFig) {
        return subFig.getBounds();
    }

    @Override public Object clone() {
        FigGroup figClone = (FigGroup) super.clone();
        int figCount = this.figs.size();
        ArrayList figsClone = new ArrayList(figCount);
        for (int i = 0; i < figCount; ++i) {
            Fig tempFig = (Fig) this.figs.get(i);
            Fig tempFigClone = (Fig) tempFig.clone();
            figsClone.add(tempFigClone);
            tempFigClone.setGroup(figClone);
        }

        figClone.figs = figsClone;
        return figClone;
    }

    /**
     * Returns true if any Fig in the group contains the given point.
     */
    @Override public boolean contains(int x, int y) {
        return hitFig(new Rectangle(x, y, 0, 0)) != null;
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * Reply an Iterator of the Figs contained in this FigGroup.
     */
    public Iterator iterator() {
        return this.figs.iterator();
    }

    /**
     * Get the fig within this group with the given index position
     *
     * @param i position of fig within this group
     */
    public Fig getFigAt(int i) {
        return (Fig) figs.get(i);
    }

    /**
     * Get the number of child Figs contained in this group
     */
    public int getFigCount() {
        return figs.size();
    }

    /**
     * Return the position of a Fig inside this group.
     */
    public int getFigPosn(Fig f) {
        return figs.indexOf(f);
    }

    /**
     * Get the child figs that make up this group.
     *
     * @return the figs of this group USED BY PGML.tee
     */
    public List getFigs() {
        return Collections.unmodifiableList(this.figs);
    }

    public Font getFont() {
        int size = this.figs.size();
        for (int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if (ft instanceof FigText) {
                return ((FigText) ft).getFont();
            }
        }
        return null;
    }

    public String getFontFamily() {
        int size = this.figs.size();
        for (int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if (ft instanceof FigText) {
                return ((FigText) ft).getFontFamily();
            }
        }
        return "Serif";
    }

    public float getFontSize() {
        int size = this.figs.size();
        for (int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if (ft instanceof FigText) {
                return ((FigText) ft).getFontSize();
            }
        }
        return 10;
    }

    /**
     * Returns the extra space that is added the frame surrounding the elements
     *
     * @return num of pixel used for extra spacing.
     */
    public int getExtraFrameSpace() {
        return _extraFrameSpace;
    }

    /**
     * Returns true if any Fig in the group hits the given rect.
     */
    @Override public boolean hit(Rectangle r) {
        return hitFig(r) != null;
    }

    // //////////////////////////////////////////////////////////////
    // Fig API

    /**
     * Retrieve the top-most Fig containing the given point, or null.
     * Needs-More-Work: just do a linear search. Later, optimize this routine
     * using Quad Trees (or other) techniques. Always returns false if this
     * FigGroup is invisible.
     */
    public Fig hitFig(Rectangle r) {
        if (!isVisible()) {
            return null;
        }
        Fig res = null;
        int figCount = this.figs.size();
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig) this.figs.get(figIndex);
            if (f.hit(r)) {
                res = f;
            }
        }

        return res;
    }

    /**
     * Groups are resizable by default (see super class), but not reshapable,
     * and not rotatable (for now).
     */
    @Override public boolean isReshapable() {
        return false;
    }

    /**
     * Groups are resizable by default (see super class), but not reshapable,
     * and not rotatable (for now).
     */
    @Override public boolean isRotatable() {
        return false;
    }

    // //////////////////////////////////////////////////////////////
    // display methods

    @Override public void paint(Object g) {
        paint(g, false);
    }

    @Override public void paint(Object g, boolean includeMargins) {

        if (includeMargins == false) {
            setMarginOffset(0, 0);
        }
        if (isVisible()) {
            int figCount = this.figs.size();
            for (int figIndex = 0; figIndex < figCount; ++figIndex) {
                Fig f = (Fig) this.figs.get(figIndex);
                if (f.isVisible()) {
                    f.paint(g);
                }
            }
        }
    }

    /**
     * Delete all Fig's from the group. Fires PropertyChange with "bounds".
     */
    public void removeAll() {
        Rectangle oldBounds = getBounds();
        int figCount = this.figs.size();
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig) this.figs.get(figIndex);
            f.setGroup(null);
        }
        this.figs.clear();
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }

    /**
     * Remove a Fig from the group. Fires PropertyChange with "bounds".
     */
    public void removeFig(Fig f) {
        if (!this.figs.contains(f)) {
            return;
        }
        Rectangle oldBounds = getBounds();
        this.figs.remove(f);
        f.setGroup(null);
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }

    /**
     * Returns a list of the displayable Figs enclosed. e.g. returns the list of
     * enclosed Figs, without the Compartments that should not be displayed.
     */
    public Collection getDisplayedFigs(Collection c) {
        if (c == null) {
            c = new ArrayList();
        }

        int figCount = this.figs.size();
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig) this.figs.get(figIndex);
            if (f.isVisible()) {
                c.add(f);
            }
        }

        return c;
    }

    /**
     * Set the bounding box to the given rect. Figs in the group are scaled to
     * fit. Fires PropertyChange with "bounds"
     *
     * @param x new X co ordinate for fig
     * @param y new Y co ordinate for fig
     * @param w new width for fig
     * @param h new height for fig
     */
    @Override protected void setBoundsImpl(int x, int y, int w, int h) {
        Rectangle oldBounds = getBounds();
        int figCount = this.figs.size();
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig) this.figs.get(figIndex);
            if (f.isVisible()) {
                int newW = (_w == 0) ? 0 : (f.getWidth() * w) / _w;
                int newH = (_h == 0) ? 0 : (f.getHeight() * h) / _h;
                int newX = (_w == 0) ? x : x + ((f.getX() - _x) * w) / _w;
                int newY = (_h == 0) ? y : y + ((f.getY() - _y) * h) / _h;
                f.setBoundsImpl(newX, newY, newW, newH);
            }
        }
        calcBounds(); // _x = x; _y = y; _w = w; _h = h;
        firePropChange("bounds", oldBounds, getBounds());
    }

    /**
     * Set the Figs in this group. Fires PropertyChange with "bounds".
     */
    public void setFigs(List figs) {
        Rectangle oldBounds = getBounds();
        this.figs = new ArrayList(figs);
        calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }

    @Override public void setFillColor(Color col) {
        int size = this.figs.size();
        for (int i = 0; i < size; i++) {
            ((Fig) this.figs.get(i)).setFillColor(col);
        }
        super.setFillColor(col);
    }

    @Override public void setFilled(boolean f) {
        int size = this.figs.size();
        for (int i = 0; i < size; i++) {
            ((Fig) this.figs.get(i)).setFilled(f);
        }
        super.setFilled(f);
    }

    public void setFont(Font f) {
        int size = this.figs.size();
        for (int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if (ft instanceof FigText) {
                ((FigText) ft).setFont(f);
            }
        }
    }

    public void setFontFamily(String s) {
        int size = this.figs.size();
        for (int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if (ft instanceof FigText) {
                ((FigText) ft).setFontFamily(s);
            }
        }
    }

    public void setFontSize(int s) {
        int size = this.figs.size();
        for (int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if (ft instanceof FigText) {
                ((FigText) ft).setFontSize(s);
            }
        }
    }

    // //////////////////////////////////////////////////////////////
    // Fig Accessors

    @Override public void setLineColor(Color col) {
        int size = this.figs.size();
        for (int i = 0; i < size; i++) {
            ((Fig) this.figs.get(i)).setLineColor(col);
        }
        super.setLineColor(col);
    }

    @Override public void setLineWidth(float w) {
        int size = this.figs.size();
        for (int i = 0; i < size; i++) {
            ((Fig) this.figs.get(i)).setLineWidth(w);
        }
        super.setLineWidth(w);
    }

    // //////////////////////////////////////////////////////////////
    // FigText Accessors

    public void setTextColor(Color c) {
        firePropChange("textColor", textColor, c);
        int size = this.figs.size();
        for (int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if (ft instanceof FigText) {
                ((FigText) ft).setTextColor(c);
            } else if (ft instanceof FigGroup) {
                ((FigGroup) ft).setTextColor(c);
            }
        }
        textColor = c;
    }

    public void setTextFillColor(Color c) {
        firePropChange("textFillColor", textFillColor, c);
        int size = this.figs.size();
        for (int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if (ft instanceof FigText) {
                ((FigText) ft).setTextFillColor(c);
            } else if (ft instanceof FigGroup) {
                ((FigGroup) ft).setTextFillColor(c);
            }
        }
        textFillColor = c;
    }

    public void setTextFilled(boolean b) {
        firePropChange("textFilled", textFilled, b);
        int size = this.figs.size();
        for (int i = 0; i < size; i++) {
            Object ft = this.figs.get(i);
            if (ft instanceof FigText) {
                ((FigText) ft).setTextFilled(b);
            } else if (ft instanceof FigGroup) {
                ((FigGroup) ft).setTextFilled(b);
            }
        }
        textFilled = b;
    }

    public Color getTextColor() {
        return textColor;
    }

    public Color getTextFillColor() {
        return textFillColor;
    }

    public boolean getTextFilled() {
        return textFilled;
    }

    /**
     * Sets the extra spacing for the frame around the elements
     *
     * @param extraSpace Num of pixels added as additional spacing
     */
    public void setExtraFrameSpace(int extraSpace) {
        _extraFrameSpace = extraSpace;
    }

    /**
     * Translate all the Fig in the list by the given offset.
     */
    @Override protected void translateImpl(int dx, int dy) {
        Rectangle oldBounds = getBounds();
        int figCount = this.figs.size();
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig f = (Fig) this.figs.get(figIndex);
            f.translate(dx, dy);
        }
        _x += dx;
        _y += dy; // no need to call calcBounds();
        firePropChange("bounds", oldBounds, getBounds());
    }

    /**
     * Let the group decide if it should be selected itself or the hitten
     * grouped fig instead.
     *
     * @param hitRect Rectangle surrounding the current mouse position.
     * @return The fig that should be selected.
     */
    public Fig deepSelect(Rectangle hitRect) {
        return this;
    }

    @Override public EditorPanel getEditorPanel() {
        return null;
        // TODO: return new FigGroupPanel();
    }
}

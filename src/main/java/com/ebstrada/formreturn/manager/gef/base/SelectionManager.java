// %1035450540230:org.tigris.gef.base%
// Copyright (c) 1996-99 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.
// File: SelectionManager.java
// Classes: SelectionManager
// Original Author: jrobbins@ics.uci.edu
// $Id: SelectionManager.java 1186 2008-12-09 20:18:13Z dthompson $
package com.ebstrada.formreturn.manager.gef.base;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.event.EventListenerList;

import com.ebstrada.formreturn.manager.gef.event.GraphSelectionEvent;
import com.ebstrada.formreturn.manager.gef.event.GraphSelectionListener;

import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.Handle;

import com.ebstrada.formreturn.manager.gef.undo.Memento;
import com.ebstrada.formreturn.manager.gef.undo.UndoManager;
import com.ebstrada.formreturn.manager.gef.util.VetoableChangeEventSource;

/**
 * This class handles Manager selections. It is basically a collection of
 * Selection instances. Most of its operations just dispatch the same operation
 * to each of the Selection instances in turn.
 * <p>
 * <p>
 * The SelectionManager is also responsible for sending out GraphSelectionEvents
 * to any GraphSelectionListeners that are registered.
 *
 * @see Selection
 */
public class SelectionManager
    implements Serializable, KeyListener, MouseListener, MouseMotionListener {

    private static final long serialVersionUID = 3232261288542010603L;

    /**
     * The collection of Selection instances
     */
    private List<Selection> selections = new ArrayList<Selection>();
    private Editor _editor;
    private EventListenerList _listeners = new EventListenerList();
    private DragMemento dragMemento;

    private Fig _dragTopMostFig;
    private Fig _dragLeftMostFig;

    /**
     * All of the nodes being dragged
     */
    private List _draggingNodes;
    /**
     * All the edges that have both ends attached to nodes that are being
     * dragged (they will also be dragged).
     */
    private List _draggingMovingEdges;
    /**
     * Edges that only have one end attached to an edge being dragged (they will
     * be reshaped)
     */
    private List _draggingNonMovingEdges;
    /**
     * Other Figs that are being dragged (ie primitives)
     */
    private List _draggingOthers;

    // //////////////////////////////////////////////////////////////
    // constructor
    public SelectionManager(Editor ed) {
        _editor = ed;
    }

    // //////////////////////////////////////////////////////////////
    // accessors

    /**
     * Add a new selection to the collection of selections
     */
    protected void addSelection(Selection s) {
        selections.add(s);
    }

    protected void addFig(Fig f) {
        if (f.isSelectable()) {
            selections.add(makeSelectionFor(f));
        }
    }

    /**
     * @deprecated in 0.13 use addFigs
     */
    @Deprecated protected void addAllFigs(Collection c) {
        Iterator it = c.iterator();
        while (it.hasNext()) {
            addFig((Fig) it.next());
        }
    }

    protected void addFigs(Collection<? extends Fig> figs) {
        for (Fig f : figs) {
            addFig(f);
        }
    }

    protected void removeAllElements() {
        selections.clear();
    }

    protected void removeSelection(Selection s) {
        if (s != null) {
            selections.remove(s);
        }
    }

    protected void removeFig(Fig f) {
        Selection s = findSelectionFor(f);
        if (s != null) {
            selections.remove(s);
        }
    }

    protected void allDamaged() {
        Rectangle bounds = this.getBounds();
        _editor.scaleRect(bounds);
        _editor.damaged(bounds);
    }

    public void select(Fig f) {
        if (UndoManager.getInstance().isGenerateMementos()) {
            UndoManager.getInstance().addMemento(new SelectionMemento());
        }
        allDamaged();
        removeAllElements();
        addFig(f);
        _editor.damageAll();
        fireSelectionChanged();
    }

    /**
     * Adds an additional fig to the current selection.
     *
     * @param fig Additional fig to select.
     */
    public void addToSelection(Fig fig) {
        if (UndoManager.getInstance().isGenerateMementos()) {
            UndoManager.getInstance().addMemento(new SelectionMemento());
        }
        addFig(fig);
        _editor.damageAll();
        fireSelectionChanged();
    }

    /**
     * Deselect the given Fig
     */
    public void deselect(Fig f) {
        if (UndoManager.getInstance().isGenerateMementos()) {
            UndoManager.getInstance().addMemento(new SelectionMemento());
        }
        if (containsFig(f)) {
            removeFig(f);
            _editor.damageAll();
            fireSelectionChanged();
        }
    }

    public void toggle(Fig f) {
        if (UndoManager.getInstance().isGenerateMementos()) {
            UndoManager.getInstance().addMemento(new SelectionMemento());
        }
        _editor.damageAll();
        if (containsFig(f)) {
            removeFig(f);
        } else {
            addFig(f);
        }

        _editor.damageAll();
        fireSelectionChanged();
    }

    public void deselectAll() {
        if (getSelections().size() > 0) {
            if (UndoManager.getInstance().isGenerateMementos()) {
                UndoManager.getInstance().addMemento(new SelectionMemento());
            }
            Rectangle damagedArea = this.getBounds(); // too much area
            removeAllElements();
            _editor.damaged(damagedArea);
            fireSelectionChanged();
        }
    }

    /**
     * @param items
     * @deprecated in 0.13 use selectFigs
     */
    @Deprecated public void select(Collection items) {
        if (UndoManager.getInstance().isGenerateMementos()) {
            UndoManager.getInstance().addMemento(new SelectionMemento());
        }
        allDamaged();
        removeAllElements();
        addAllFigs(items);
        allDamaged();
        fireSelectionChanged();
    }

    public void selectFigs(Collection<? extends Fig> items) {
        if (UndoManager.getInstance().isGenerateMementos()) {
            UndoManager.getInstance().addMemento(new SelectionMemento());
        }
        allDamaged();
        removeAllElements();
        addFigs(items);
        allDamaged();
        fireSelectionChanged();
    }

    public void toggle(Vector items) {
        if (UndoManager.getInstance().isGenerateMementos()) {
            UndoManager.getInstance().addMemento(new SelectionMemento());
        }
        allDamaged();
        Enumeration figs = ((Vector) items.clone()).elements();
        while (figs.hasMoreElements()) {
            Fig f = (Fig) figs.nextElement();
            if (containsFig(f)) {
                removeFig(f);
            } else {
                addFig(f);
            }
        }

        allDamaged();
        fireSelectionChanged();
    }

    public Selection findSelectionFor(Fig f) {
        // TODO: Why we cannot operate on the list itself?
        List<Selection> sels = new ArrayList<Selection>(getSelections());
        for (Selection sel : sels) {
            if (sel.contains(f)) {
                return sel;
            }
        }
        return null;
    }

    public Selection findSelectionAt(int x, int y) {
        // TODO: Why we cannot operate on the list itself?
        List<Selection> sels = new ArrayList<Selection>(getSelections());
        for (Selection sel : sels) {
            if (sel.contains(x, y)) {
                return sel;
            }
        }
        return null;
    }

    /**
     * Reply true if the given selection instance is part of my collection
     */
    public boolean contains(Selection s) {
        return selections.contains(s);
    }

    /**
     * Reply true if the given Fig is selected by any of my selection objects
     */
    public boolean containsFig(Fig f) {
        return findSelectionFor(f) != null;
    }

    /**
     * @return true if any Selection is locked
     */
    public boolean getLocked() {
        for (Selection sel : selections) {
            if (sel.getLocked()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Reply the number of selected Fig's. This assumes that this collection
     * holds only Selection instances and each of those holds one Fig
     */
    public int size() {
        return selections.size();
    }

    /**
     * @deprecated use getSelections()
     */
    @Deprecated public Vector selections() {
        return new Vector(selections);
    }

    public List<Selection> getSelections() {
        return Collections.unmodifiableList(new ArrayList<Selection>(selections));
    }

    /**
     * Reply the collection of all selected Fig's
     *
     * @deprecated in 0.13 use getSelectedFigs
     */
    public Vector<Fig> getFigs() {
        Vector<Fig> figs = new Vector<Fig>(selections.size());
        int selCount = selections.size();
        for (int i = 0; i < selCount; ++i) {
            figs.addElement(((Selection) selections.get(i)).getContent());
        }

        return figs;
    }

    /**
     * Reply the collection of all selected Fig's
     */
    public List<Fig> getSelectedFigs() {
        List<Fig> figs = new ArrayList<Fig>(selections.size());
        int selCount = selections.size();
        for (int i = 0; i < selCount; ++i) {
            figs.add(((Selection) selections.get(i)).getContent());
        }

        return figs;
    }

    /**
     * Get a collection of Figs that will be dragged as a result of dragging
     * this selection.
     */
    public List getDraggableFigs() {
        List figs = new ArrayList(getFigs());
        return figs;
    }

    /**
     * End a transaction that damages all selected Fig's
     */
    public void endTrans() {
        int selSize = selections.size();
        List affected = new ArrayList();
        for (int i = 0; i < selSize; ++i) {
            Selection s = (Selection) selections.get(i);
            addEnclosed(affected, s.getContent());
        }

        int size = affected.size();
        for (int i = 0; i < size; ++i) {
            Fig f = (Fig) affected.get(i);
            f.endTrans();
        }
    }

    /**
     * Paint all selection objects
     */
    public void paint(Graphics g) {
        for (Selection sel : selections) {
            sel.paint(g);
        }
    }

    /**
     * When the SelectionManager is damageAll, that implies that each Selection
     * should be damageAll.
     */
    public void damage() {
        for (Selection sel : selections) {
            sel.damage();
        }
    }

    /**
     * Reply true iff the given point is inside one of the selected Fig's
     */
    public boolean contains(int x, int y) {
        for (Selection sel : selections) {
            if (sel.contains(x, y)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Reply true iff the given point is inside one of the selected Fig's
     */
    public boolean hit(Rectangle r) {
        for (Selection sel : selections) {
            if (sel.hit(r)) {
                return true;
            }
        }

        return false;
    }

    public Rectangle getBounds() {
        int size = selections.size();
        if (size == 0) {
            return new Rectangle(0, 0, 0, 0);
        }

        Rectangle r = selections.get(0).getBounds();
        for (Selection sel : selections) {
            r.add(sel.getBounds());
        }

        return r;
    }

    public Rectangle getContentBounds() {
        Rectangle r = null;
        Iterator<Selection> it = selections.iterator();
        if (it.hasNext()) {
            r = it.next().getContentBounds();
        } else {
            return new Rectangle(0, 0, 0, 0);
        }

        while (it.hasNext()) {
            Selection sel = it.next();
            r.add(sel.getContentBounds());
        }

        return r;
    }

    /**
     * This method will return the upper-left coordinate point of the entire
     * selection by iterating through the figs
     *
     * @return Point - the point for that upper left corner
     */
    public Point getLocation() {
        int size = selections.size();
        if (size < 1) {
            return new Point(0, 0);
        }

        Selection sel = null;
        int lowestX = Integer.MAX_VALUE;
        int lowestY = Integer.MAX_VALUE;
        Point pt = null;
        for (int i = 0; i < size; i++) {
            sel = (Selection) selections.get(i);
            pt = sel.getLocation();
            if (pt.getX() < lowestX) {
                lowestX = (int) pt.getX();
            }

            if (pt.getY() < lowestY) {
                lowestY = (int) pt.getY();
            }
        }

        pt = null;
        sel = null;
        return new Point(lowestX, lowestY);
    }

    // /** Align the selected Fig's relative to each other */
    // /* needs-more-work: more of this logic should be in ActionAlign */
    // public void align(int dir) {
    // Editor ed = Globals.curEditor();
    // Rectangle bbox = getContentBounds();
    // Enumeration ss = _selections.elements();
    // while (ss.hasMoreElements())
    // ((Selection) ss.nextElement()).align(bbox, dir, ed);
    // }
    // public void align(Rectangle r, int dir, Editor ed) {
    // Enumeration ss = _selections.elements();
    // while(ss.hasMoreElements())
    // ((Selection)ss.nextElement()).align(r,dir,ed);
    // }

    /**
     * When Manager selections are sent to back, each of them is sent to back.
     */
    public void reorder(int func, Layer lay) {
        for (Selection sel : selections) {
            sel.reorder(func, lay);
        }
    }

    /**
     * When Manager selections are moved, each of them is moved
     */
    public void translate(int dx, int dy) {
        Vector affected = new Vector();
        Vector nonMovingEdges = new Vector();
        Vector movingEdges = new Vector();
        Vector nodes = new Vector();
        int selSize = selections.size();
        for (Selection sel : selections) {
            addEnclosed(affected, sel.getContent());
        }

        int size = affected.size();
        for (int i = 0; i < size; ++i) {
            Fig f = (Fig) affected.elementAt(i);
            int fx = f.getX();
            int fy = f.getY();
            dx = Math.max(-fx, dx);
            dy = Math.max(-fy, dy);
        }

        for (int i = 0; i < size; ++i) {
            Fig f = (Fig) affected.elementAt(i);
            f.translate(dx, dy); // lost selection.translate() !
        }

    }

    protected void addEnclosed(Collection affected, Fig f) {
        if (!affected.contains(f)) {
            affected.add(f);
            List enclosed = f.getEnclosedFigs();
            if (enclosed != null) {
                int size = enclosed.size();
                for (int i = 0; i < size; ++i) {
                    addEnclosed(affected, (Fig) enclosed.get(i));
                }
            }
        }
    }

    public void startDrag() {
        // While we're dragging we want to create DragMementos instead of
        // any other mementos that the framework would normally create for
        // us. So make sure generate mementos is turned off during drag.

        List draggingFigs = new ArrayList();
        _draggingNodes = new ArrayList();
        _draggingMovingEdges = new ArrayList();
        _draggingNonMovingEdges = new ArrayList();
        _draggingOthers = new ArrayList();

        int selectionCount = selections.size();
        for (int selectionIndex = 0; selectionIndex < selectionCount; ++selectionIndex) {
            Selection selection = (Selection) selections.get(selectionIndex);
            addEnclosed(draggingFigs, selection.getContent());
        }

        int figCount = draggingFigs.size();
        for (int figIndex = 0; figIndex < figCount; ++figIndex) {
            Fig fig = (Fig) draggingFigs.get(figIndex);
            _draggingOthers.add(fig);
        }

        List topLeftList = (_draggingNodes.size() > 0 ? _draggingNodes : _draggingOthers);
        int s = topLeftList.size();
        for (int i = 0; i < s; ++i) {
            Fig fig = (Fig) topLeftList.get(i);
            if (_dragLeftMostFig == null || fig.getX() < _dragLeftMostFig.getX()) {
                _dragLeftMostFig = fig;
            }

            if (_dragTopMostFig == null || fig.getY() < _dragTopMostFig.getY()) {
                _dragTopMostFig = fig;
            }
        }

        if (UndoManager.getInstance().isGenerateMementos()) {
            dragMemento = new DragMemento(_draggingNodes, _draggingOthers, _draggingMovingEdges,
                _draggingNonMovingEdges);
        }
        UndoManager.getInstance().addMementoLock(this);

    }

    public void drag(int dx, int dy) {
        if (_dragLeftMostFig == null || _dragTopMostFig == null) {
            return;
        }

        Rectangle dirtyRegion = _dragLeftMostFig.getBounds();
        Rectangle figBounds = _dragLeftMostFig.getBounds();
        dx = Math.max(-_dragLeftMostFig.getX(), dx);
        dy = Math.max(-_dragTopMostFig.getY(), dy);
        int nodeCount = _draggingNodes.size();


        int otherCount = _draggingOthers.size();
        for (int i = 0; i < otherCount; ++i) {
            Fig fig = (Fig) _draggingOthers.get(i);
            fig.getBounds(figBounds);
            dirtyRegion.add(figBounds.x, figBounds.y);
            dirtyRegion.add(figBounds.x + dx, figBounds.y + dy);
            dirtyRegion.add(figBounds.x + figBounds.width, figBounds.y + figBounds.height);
            dirtyRegion
                .add(figBounds.x + figBounds.width + dx, figBounds.y + figBounds.height + dy);
            fig.translate(dx, dy);
        }

        int extraDirt = 24;
        dirtyRegion.x -= extraDirt;
        dirtyRegion.y -= extraDirt;
        dirtyRegion.width += 2 * extraDirt;
        dirtyRegion.height += 2 * extraDirt;
        Layer layer = _dragLeftMostFig.getLayer();
        // try to get the layer of the owning fig (if there is one) in case
        // layer is null.
        if (layer == null) {
            if (_dragLeftMostFig.getOwner() instanceof Fig) {
                layer = ((Fig) _dragLeftMostFig.getOwner()).getLayer();
            }
        }

        if (layer != null) {
            List editors = layer.getEditors();
            int editorCount = editors.size();
            Rectangle dirtyRegionScaled = new Rectangle();
            for (int editorIndex = 0; editorIndex < editorCount; ++editorIndex) {
                Editor editor = (Editor) editors.get(editorIndex);
                double editorScale = editor.getScale();
                dirtyRegionScaled.x = (int) Math.floor(dirtyRegion.x * editorScale);
                dirtyRegionScaled.y = (int) Math.floor(dirtyRegion.y * editorScale);
                dirtyRegionScaled.width = (int) Math.floor(dirtyRegion.width * editorScale) + 1;
                dirtyRegionScaled.height = (int) Math.floor(dirtyRegion.height * editorScale) + 1;
                editor.damaged(dirtyRegionScaled);
            }
        } else {
            System.out.println("Selection manager: layer is null");
        }
    }

    public void stopDrag() {
        // Set the generate memento mode back to whatever it was before we
        // started dragging
        UndoManager.getInstance().removeMementoLock(this);
        ;

        if (dragMemento != null) {
            UndoManager.getInstance().addMemento(dragMemento);
        }
        dragMemento = null;

        cleanup();
    }

    private void cleanup() {
        _dragTopMostFig = null;
        _dragLeftMostFig = null;
        _draggingNodes = null;
        _draggingMovingEdges = null;
        _draggingNonMovingEdges = null;
        _draggingOthers = null;
    }

    // The top-left corner of the rectangle enclosing all figs that will move
    // when translated
    // (i.e. not including any non-moving edges).
    public Point getDragLocation() {
        return new Point(_dragLeftMostFig.getX(), _dragTopMostFig.getY());
    }

    /**
     * If only one thing is selected, then it is possible to mouse on one of its
     * handles, but if Manager things are selected, users can only drag the
     * objects around
     */

    /* needs-more-work: should take on more of this responsibility */
    public void hitHandle(Rectangle r, Handle h) {
        if (size() == 1) {
            selections.get(0).hitHandle(r, h);
        } else {
            h.index = -1;
        }
    }

    /**
     * If only one thing is selected, then it is possible to mouse on one of its
     * handles, but if Manager things are selected, users can only drag the
     * objects around
     */
    public void dragHandle(int mx, int my, int an_x, int an_y, Handle h) {
        if (size() != 1) {
            return;
        }

        Selection sel = selections.get(0);
        sel.dragHandle(mx, my, an_x, an_y, h);
    }

    public void cleanUp() {
        for (Selection sel : selections) {
            Fig f = sel.getContent();
            f.cleanUp();
        }
    }

    /**
     * When a multiple selection are deleted, each selection is deleted
     */
    public void removeFromGraph() {
        // TODO: Why are we not operating on the list itself?
        List<Selection> sels = new ArrayList<Selection>(selections);
        for (Selection sel : sels) {
            sel.delete();
        }
    }

    /**
     * When a multiple selection are deleted, each selection is deleted
     */
    public void dispose() {
        // TODO: Why we cannot operate on the list itself?
        List<Selection> sels = new ArrayList<Selection>(selections);
        for (Selection s : sels) {
            Fig f = s.getContent();
            Object o = f.getOwner();
            if (o instanceof VetoableChangeEventSource) {
                Vector v =
                    (Vector) ((VetoableChangeEventSource) o).getVetoableChangeListeners().clone();
                Enumeration vv = v.elements();
                vv = v.elements();
                Object firstElem = null;
                boolean firstIteration = true;
                while (vv.hasMoreElements()) {
                    Object elem = vv.nextElement();
                    if (elem instanceof Fig) {
                        if (firstIteration) {
                            firstElem = elem;
                            firstIteration = false;
                            continue;
                        }

                        ((Fig) elem).removeFromDiagram();
                    }
                }

                ((Fig) firstElem).deleteFromModel();
            }
        }
    }

    /**
     * When a multiple selection are deleted, each selection is deleted
     */
    public void deleteFromModel() {
        for (Selection sel : getSelections()) {
            Fig f = sel.getContent();
            f.deleteFromModel();
        }
    }

    // //////////////////////////////////////////////////////////////
    // input events

    /**
     * When an event is passed to a multiple selection, try to pass it off to
     * the first selection that will handle it.
     */
    public void keyTyped(KeyEvent ke) {
        // TODO: Why we cannot operate on the list itself?
        List<Selection> list = new ArrayList<Selection>(selections);
        Iterator<Selection> sels = list.iterator();
        while (sels.hasNext() && !ke.isConsumed()) {
            sels.next().keyTyped(ke);
        }
    }

    public void keyReleased(KeyEvent ke) {
        // TODO: Why we cannot operate on the list itself?
        List<Selection> list = new ArrayList<Selection>(selections);
        Iterator<Selection> sels = list.iterator();
        while (sels.hasNext() && !ke.isConsumed()) {
            sels.next().keyReleased(ke);
        }

    }

    public void keyPressed(KeyEvent ke) {
        // TODO: Why we cannot operate on the list itself?
        List<Selection> list = new ArrayList<Selection>(selections);
        Iterator<Selection> sels = list.iterator();
        while (sels.hasNext() && !ke.isConsumed()) {
            sels.next().keyPressed(ke);
        }
    }

    public void mouseMoved(MouseEvent me) {
        // TODO: Why we cannot operate on the list itself?
        List<Selection> list = new ArrayList<Selection>(selections);
        Iterator<Selection> sels = list.iterator();
        while (sels.hasNext() && !me.isConsumed()) {
            sels.next().mouseMoved(me);
        }
    }

    public void mouseDragged(MouseEvent me) {
        // TODO: Why we cannot operate on the list itself?
        List<Selection> list = new ArrayList<Selection>(selections);
        Iterator<Selection> sels = list.iterator();
        while (sels.hasNext() && !me.isConsumed()) {
            sels.next().mouseDragged(me);
        }
    }

    public void mouseClicked(MouseEvent me) {
        // TODO: Why we cannot operate on the list itself?
        List<Selection> list = new ArrayList<Selection>(selections);
        Iterator<Selection> sels = list.iterator();
        while (sels.hasNext() && !me.isConsumed()) {
            sels.next().mouseClicked(me);
        }
    }

    public void mousePressed(MouseEvent me) {
        // TODO: Why we cannot operate on the list itself?
        List<Selection> list = new ArrayList<Selection>(selections);
        Iterator<Selection> sels = list.iterator();
        while (sels.hasNext() && !me.isConsumed()) {
            sels.next().mousePressed(me);
        }
    }

    public void mouseReleased(MouseEvent me) {
        // TODO: Why we cannot operate on the list itself?
        List<Selection> list = new ArrayList<Selection>(selections);
        Iterator<Selection> sels = list.iterator();
        while (sels.hasNext() && !me.isConsumed()) {
            sels.next().mouseReleased(me);
        }
    }

    public void mouseExited(MouseEvent me) {
        // TODO: Why we cannot operate on the list itself?
        List<Selection> list = new ArrayList<Selection>(selections);
        Iterator<Selection> sels = list.iterator();
        while (sels.hasNext() && !me.isConsumed()) {
            sels.next().mouseExited(me);
        }
    }

    public void mouseEntered(MouseEvent me) {
        // TODO: Why we cannot operate on the list itself?
        List<Selection> list = new ArrayList<Selection>(selections);
        Iterator<Selection> sels = list.iterator();
        while (sels.hasNext() && !me.isConsumed()) {
            sels.next().mouseEntered(me);
        }
    }

    // //////////////////////////////////////////////////////////////
    // graph events
    public void addGraphSelectionListener(GraphSelectionListener listener) {
        _listeners.add(GraphSelectionListener.class, listener);
    }

    public void removeGraphSelectionListener(GraphSelectionListener listener) {
        _listeners.remove(GraphSelectionListener.class, listener);
    }

    protected void fireSelectionChanged() {
        cleanup(); // just to be paranoid
        Object[] listeners = _listeners.getListenerList();
        GraphSelectionEvent e = null;
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == GraphSelectionListener.class) {
                if (e == null) {
                    e = new GraphSelectionEvent(_editor, getFigs());
                }

                // needs-more-work: should copy vector, use JGraph as src?
                ((GraphSelectionListener) listeners[i + 1]).selectionChanged(e);
            }
        }

        updatePropertySheet();
    }

    // //////////////////////////////////////////////////////////////
    // property sheet methods
    public void updatePropertySheet() {
        // if (_selections.size() != 1) Globals.propertySheetSubject(null);
        // else {
        // Fig f = (Fig) getFigs().elementAt(0);
        // Globals.propertySheetSubject(f);
        // }
    }

    /**
     * Determines and returns the first common superclass of all selected items.
     */
    public Class findCommonSuperClass() {
        Iterator selectionIter = selections.iterator();
        Map superclasses = new HashMap();
        int maxCount = 0;
        Class maxClass = null;
        while (selectionIter.hasNext()) {
            Class figClass = ((Selection) selectionIter.next()).getContent().getClass();
            int count = 0;
            if (superclasses.containsKey(figClass.getName())) {
                count = ((Integer) superclasses.get(figClass.getName())).intValue();
                superclasses.put(figClass.getName(), new Integer(++count));
            } else {
                count = 1;
                superclasses.put(figClass.getName(), new Integer(count));
            }

            if (count > maxCount) {
                maxCount = count;
                maxClass = figClass;
            }

            Class superClass = figClass.getSuperclass();
            while (!(superClass == null || superClass.equals(Fig.class))) {
                if (superclasses.containsKey(superClass.getName())) {
                    count = ((Integer) superclasses.get(superClass.getName())).intValue();
                    superclasses.put(superClass.getName(), new Integer(++count));
                } else {
                    count = 1;
                    superclasses.put(superClass.getName(), new Integer(count));
                }

                if (count > maxCount) {
                    maxCount = count;
                    maxClass = superClass;
                }

                superClass = superClass.getSuperclass();
            }
        }

        if (maxCount == selections.size()) {
            return maxClass;
        } else {
            return Fig.class;
        }
    }

    /**
     * Searches for the first appearance of an object of the designated type in
     * the current selection.
     *
     * @param type Type of selection class to look for.
     * @return The first selected object being instance of the designated type.
     */
    public Object findFirstSelectionOfType(Class type) {
        Iterator selectionIter = selections.iterator();
        while (selectionIter.hasNext()) {
            Object selectionObj = ((Selection) selectionIter.next()).getContent();
            if (selectionObj instanceof Fig) {

                // I don't see the point of matching it to the type as
                // this method is only ever used once.

                // if (selectionObj.getClass().equals(type)) {
                return selectionObj;
            }
        }

        return null;
    }

    // //////////////////////////////////////////////////////////////
    // static methods
    // protected static Hashtable _SelectionRegistry = new Hashtable();
    // needs-more-work: cache a pool of selection objects?
    public static Selection makeSelectionFor(Fig f) {
        Selection customSelection = f.makeSelection();
        if (customSelection != null) {
            return customSelection;
        }

        // if (f.isRotatable()) return new SelectionRotate(f);
        if (f.isReshapable()) {
            return new SelectionReshape(f);
        } else if (f.isLowerRightResizable()) {
            return new SelectionLowerRight(f);
        } else if (f.isResizable()) {
            return new SelectionResize(f);
        } else if (f.isMovable()) {
            return new SelectionMove(f);
        } else {
            return new SelectionNoop(f);
        }
    }

    class DragMemento extends Memento {

        List draggingNodes;
        List draggingOthers;
        List bounds;

        List movingEdges;
        List nonMovingEdges;
        List points;

        public DragMemento(List draggingNodes, List draggingOthers, List movingEdges,
            List nonMovingEdges) {
            bounds = new ArrayList(draggingOthers.size());

            this.draggingOthers = draggingOthers;
            Iterator otherIt = draggingOthers.iterator();
            while (otherIt.hasNext()) {
                Fig fig = (Fig) otherIt.next();
                Rectangle rect = fig.getBounds();
                bounds.add(rect);
            }

        }

        public void undo() {
            UndoManager.getInstance().addMementoLock(this);
            Iterator boundsIt = bounds.iterator();

            // Create an array to store each node's current boundaries
            List oldBounds = new ArrayList(draggingOthers.size());


            Iterator otherIt = draggingOthers.iterator();
            while (otherIt.hasNext()) {
                Fig fig = (Fig) otherIt.next();
                Rectangle rect = (Rectangle) boundsIt.next();
                // Save the current boundaries for redo
                oldBounds.add(fig.getBounds());
                fig.setBounds(rect);
                fig.damage();
            }

            // Set the undo boundaries to the boundaries we just replaced
            bounds = oldBounds;

            UndoManager.getInstance().removeMementoLock(this);
        }

        public void redo() {
            // Simply undo the previous undo
            undo();
        }

        public String toString() {
            return (isStartChain() ? "*" : " ") + "DragMemento";
        }
    }


    class SelectionMemento extends Memento {

        ArrayList prevSelections;

        public SelectionMemento() {
            prevSelections = new ArrayList(selections);
        }

        public void undo() {
            ArrayList curSelections = new ArrayList(selections);
            selections = prevSelections;
            prevSelections = curSelections;
            _editor.damageAll();
        }

        public void redo() {
            undo();
        }

        public String toString() {
            return (isStartChain() ? "*" : " ") + "SelectionMemento";
        }
    }
} /* end class SelectionManager */

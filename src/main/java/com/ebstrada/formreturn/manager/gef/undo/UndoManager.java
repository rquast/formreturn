package com.ebstrada.formreturn.manager.gef.undo;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import com.ebstrada.formreturn.manager.gef.base.Editor;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.ui.Main;

/**
 * Manages stacks of Mementos to undo and redo.
 *
 * @author Bob Tarling
 */
public class UndoManager {

    private final static String TRUE = Boolean.TRUE.toString();
    private final static String FALSE = Boolean.FALSE.toString();

    private int undoMax = 100;
    private int undoChainCount = 0;
    private int redoChainCount = 0;

    private Vector mementoLocks = new Vector();

    private Vector listeners = new Vector();

    private boolean newChain = true;

    private boolean undoInProgress = false;

    // TODO: A MementoChainStack may produce some reasuable code for
    // the undoStack and the redoStack/
    protected Vector undoStack = new Vector();
    protected Vector redoStack = new Vector();

    /**
     * Default to the standard undo manager but applications can set this
     * themseleves by calling setInstance with some extension of UndoManager.
     */
    private static UndoManager instance = new UndoManager();

    public UndoManager() {
        super();
    }

    public static void setInstance(UndoManager manager) {
        manager.listeners = instance.listeners;
        instance = manager;
    }

    public static UndoManager getInstance() {

        Editor editor = Globals.curEditor();

        if (editor != null && editor.getUndoManager() != null) {
            return editor.getUndoManager();
        } else {
            return instance;
        }

    }

    /**
     * Adds a new memento to the undo stack.
     *
     * @param memento the memento.
     */
    public void addMemento(Memento memento) {
        if (undoMax == 0) {
            return;
        }
        // Flag the memento as to whether it is first in a chain
        memento.startChain = newChain;
        if (newChain) {
            // If the memento is the first then consider that
            // there is a new chain being received and clear
            // the redos
            emptyRedo();
            incrementUndoChainCount();
            newChain = false;
            if (undoChainCount > undoMax) {
                // TODO The undo stack is full, dispose
                // of the oldest chain.
            }
        }
        undoStack.add(memento);
    }

    public void setUndoMax(int max) {
        undoMax = max;
    }

    /**
     * Undo the most recent chain of mementos received by the undo stack
     */
    synchronized public void undo() {
        if (undoStack.size() <= 0) {
            fireAllEvents();
            return;
        }
        undoInProgress = true;
        Memento memento;
        boolean startChain = false;
        do {
            memento = pop(undoStack);
            startChain = memento.isStartChain();
            undo(memento);
        } while (!startChain);
        decrementUndoChainCount();
        incrementRedoChainCount();
        undoInProgress = false;
    }

    /**
     * Undo a single memento
     *
     * @param memento
     */
    protected void undo(Memento memento) {
        memento.undo();
        redoStack.add(memento);
    }

    /**
     * Redo the most recent chain of mementos received by the undo stack
     */
    synchronized public void redo() {
        if (redoStack.size() <= 0) {
            fireAllEvents();
            return;
        }
        undoInProgress = true;
        do {
            Memento memento = pop(redoStack);
            redo(memento);
        } while (redoStack.size() > 0 && !((Memento) redoStack
            .get(redoStack.size() - 1)).startChain);
        incrementUndoChainCount();
        decrementRedoChainCount();
        undoInProgress = false;
    }

    /**
     * Undo a single memento
     *
     * @param memento
     */
    protected void redo(Memento memento) {
        memento.redo();
        undoStack.add(memento);
    }

    /**
     * Empty all undoable items from the UndoManager
     */
    public void emptyUndo() {
        if (undoChainCount > 0) {
            emptyStack(undoStack);
            undoChainCount = 0;
            fireCanUndo();
        }
    }

    /**
     * Empty all redoable items from the UndoManager
     */
    private void emptyRedo() {
        if (redoChainCount > 0) {
            emptyStack(redoStack);
            redoChainCount = 0;
            fireCanRedo();
        }
    }

    /**
     * Empty all undoable and redoable items from the UndoManager
     */
    public void empty() {
        emptyUndo();
        emptyRedo();
    }

    /*
     * Fire all events when switching tabs
     */
    public void fireAllEvents() {
        fireCanUndo();
        fireCanRedo();
    }

    /**
     * Instructs the UndoManager that the sequence of mementos recieved up until
     * the next call to newChain all represent one chain of mementos (ie one
     * undoable user interaction).
     */
    public void startChain() {
        newChain = true;
    }

    public void reset() {
        empty();
        startChain();
    }

    /**
     * Empty a list stack disposing of all mementos.
     *
     * @param list the list of mementos
     */
    private void emptyStack(List list) {
        for (int i = 0; i < list.size(); ++i) {
            ((Memento) list.get(i)).dispose();
        }
        list.clear();
    }

    private Memento pop(Vector stack) {
        return (Memento) stack.remove(stack.size() - 1);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.listeners.add(listener);
    }

    private void fireCanUndo() {
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            PropertyChangeListener listener = (PropertyChangeListener) i.next();
            listener.propertyChange(
                new PropertyChangeEvent(this, "canUndo", "", getBoolString(undoChainCount > 0)));
        }
    }

    private void fireCanRedo() {
        Iterator i = listeners.iterator();
        while (i.hasNext()) {
            PropertyChangeListener listener = (PropertyChangeListener) i.next();
            listener.propertyChange(
                new PropertyChangeEvent(this, "canRedo", "", getBoolString(redoChainCount > 0)));
        }
    }

    private void incrementUndoChainCount() {
        ++undoChainCount;
        fireCanUndo();
    }

    private void decrementUndoChainCount() {
        --undoChainCount;
        fireCanUndo();
    }

    private void incrementRedoChainCount() {
        ++redoChainCount;
        fireCanRedo();
    }

    private void decrementRedoChainCount() {
        --redoChainCount;
        fireCanRedo();
    }

    /**
     * Convert a boolean value to a String. This method can be dropped when we
     * move to JRE1.4
     *
     * @param b a boolean
     * @return "true" or "false"
     */
    private String getBoolString(boolean b) {
        return b ? TRUE : FALSE;
    }

    public boolean isUndoInProgress() {
        return undoInProgress;
    }

    /**
     * Determine if mementos are generated.
     *
     * @return true is mementos are generated.
     */
    public boolean isGenerateMementos() {
        return (mementoLocks.size() == 0);
    }

    /**
     * @param generateMementos
     * @deprecated use addMementoLock
     */
    public void setGenerateMementos(boolean generateMementos) {
    }

    /**
     * Maintain list of objects that have requested memento generation to be
     * disabled.
     *
     * @param lockOwner object that requested a lock on new mementos
     */

    public void addMementoLock(Object lockOwner) {
        this.mementoLocks.add(lockOwner);
    }

    public void removeMementoLock(Object lockOwner) {
        this.mementoLocks.remove(lockOwner);
    }

}

package com.ebstrada.formreturn.manager.gef.undo;

/**
 * The base class of all mementos. Any undoable methods should create a concrete
 * instance that will undo (and possibly redo) that method.
 *
 * @author Bob Tarling
 */
public abstract class Memento {
    /**
     * Set by the undo framework to flag the first memento of a chain of
     * mementos that represent a single user interaction with the application.
     *
     * @param b
     * true if this memento is the start of a chain.
     */
    boolean startChain;

    /**
     * Determine if this is the start of a chain of mementos
     *
     * @return true if this is the start of a memento chain
     */
    protected boolean isStartChain() {
        return startChain;
    }

    /**
     * To be implemented on the concrete memento to undo an instruction
     */
    public abstract void undo();

    /**
     * To be implemented on the concrete memento to redo an instruction
     */
    public abstract void redo();

    /**
     * To be implemented on the concrete memento to dispose of any resources
     */
    public void dispose() {
    }

    public String toString() {
        return (isStartChain() ? "*" : " ") + this.getClass().getName();
    }
}

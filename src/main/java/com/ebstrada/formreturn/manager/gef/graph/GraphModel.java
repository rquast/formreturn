package com.ebstrada.formreturn.manager.gef.graph;

/**
 * This interface provides a facade to a net-level representation. Similiar in
 * concept to the Swing class TreeModel.
 * <p>
 * <p>
 * The idea is not to have a widget (like JGraph) storing all the information
 * that it should display, and the programmer having to keep the widget's data
 * in synch with the application's data. Instead, the programmer defines a Model
 * class that gives the widget access to the application data. That way there is
 * only one copy of the data and nothing can get out of synch. If you don't have
 * your own application data objects, there is a Default implementation of the
 * Model that will store it for you.
 * <p>
 * <p>
 * Instead of asking application programmers to subclass their data objects from
 * some predefined base class (like NetNode), this interface allows the use of
 * any application object as a node, port, or edge. This makes it much easier to
 * add a visualization to an existing application.
 */

public interface GraphModel extends BaseGraphModel {
}

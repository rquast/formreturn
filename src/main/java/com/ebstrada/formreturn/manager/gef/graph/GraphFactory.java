package com.ebstrada.formreturn.manager.gef.graph;

public interface GraphFactory {
    public GraphModel makeGraphModel();

    public Object makeNode();

    public Object makeEdge();
}

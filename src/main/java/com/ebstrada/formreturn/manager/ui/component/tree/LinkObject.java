package com.ebstrada.formreturn.manager.ui.component.tree;

public class LinkObject implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;

    private final Object obj;

    public LinkObject(String name, Object obj) {
        this.name = name;
        this.obj = obj;
    }

    public Object getObject() {
        return obj;
    }

    public String toString() {
        return name;
    }

}

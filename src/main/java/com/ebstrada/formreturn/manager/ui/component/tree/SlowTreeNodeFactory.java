package com.ebstrada.formreturn.manager.ui.component.tree;

public class SlowTreeNodeFactory implements TreeNodeFactory {

    private final TreeNodeFactory factory;

    private final int delay;

    public SlowTreeNodeFactory(TreeNodeFactory factory, int delay) {
        this.factory = factory;
        this.delay = delay;
    }

    public RecordTreeNode[] createChildren(Object userObject) throws Exception {
        RecordTreeNode[] children = factory.createChildren(userObject);
        // simulate a delay per child
        Thread.sleep((long) (1000 + Math.random() * children.length * delay));
        return children;
    }

}

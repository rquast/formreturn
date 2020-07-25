package com.ebstrada.formreturn.manager.ui.component.tree;

import javax.swing.tree.DefaultMutableTreeNode;

import com.ebstrada.formreturn.manager.ui.component.tree.node.RecordNode;

public class RecordTreeNode extends DefaultMutableTreeNode {

    private static final long serialVersionUID = 1L;

    private RecordTreeNode[] children;

    public RecordTreeNode(LinkObject link, boolean b) {
        super(link, b);
    }

    public RecordTreeNode(RecordNode node, boolean b) {
        super(node, b);
    }

    public RecordTreeNode(String str, boolean b) {
        super(str, b);
    }

    public RecordTreeNode[] getChildren() {
        return children;
    }

    public void setChildren(RecordTreeNode[] children) {
        this.children = children;
    }

}

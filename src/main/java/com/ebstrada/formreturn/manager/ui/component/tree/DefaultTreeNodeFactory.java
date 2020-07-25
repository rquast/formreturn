package com.ebstrada.formreturn.manager.ui.component.tree;

import com.ebstrada.formreturn.manager.ui.component.tree.node.RecordNode;
import com.ebstrada.formreturn.manager.ui.component.tree.node.PublicationRootNode;

public class DefaultTreeNodeFactory implements TreeNodeFactory {

    private PublicationRootNode rootNode;

    public DefaultTreeNodeFactory() {
        this.rootNode = new PublicationRootNode();
    }

    public DefaultTreeNodeFactory(RecordNode recordNode) {
        this.rootNode = (PublicationRootNode) recordNode;
    }

    public RecordTreeNode[] createChildren(Object userObject) throws Exception {
        RecordNode parent;
        if (userObject instanceof LinkObject) {
            LinkObject link = (LinkObject) userObject;
            parent = (RecordNode) link.getObject();
        } else {
            parent = this.rootNode;
        }

        int count = (parent != null) ? parent.getChildCount() : 0;
        RecordTreeNode[] children = new RecordTreeNode[count];
        for (int i = 0; i < count; i++) {
            RecordNode child = parent.getChild(i);
            if (!(child.isChildNodesLoaded())) {
                child.loadChildren();
            }
            LinkObject link = new LinkObject(child.toString(), child);
            boolean leaf = child.isLeafNode();
            children[i] = new RecordTreeNode(link, !leaf);
        }
        return children;
    }
}

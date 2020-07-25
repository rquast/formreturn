package com.ebstrada.formreturn.manager.ui.component.tree.node;

import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JPopupMenu;

import com.ebstrada.formreturn.manager.ui.component.tree.RecordTree;
import com.ebstrada.formreturn.manager.ui.component.tree.RecordTreeNode;

abstract public class RecordNode {

    private Vector<RecordNode> childNodes = new Vector<RecordNode>();
    protected boolean childNodesLoaded = false;
    private long recordId;

    public RecordNode getChild(int index) {
        return childNodes.get(index);
    }

    public boolean hasChildren() throws Exception {
        if (getChildCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    abstract public void loadChildren() throws Exception;

    abstract public String toString();

    abstract public Icon getIcon();

    abstract public JPopupMenu getPopupMenu(RecordTree tree, RecordTreeNode node);

    public int getChildCount() throws Exception {
        if (childNodesLoaded == false) {
            loadChildren();
        }
        return childNodes.size();
    }

    public boolean isLeafNode() throws Exception {
        if (hasChildren()) {
            return false;
        } else {
            return true;
        }
    }

    public int getChildIndex(RecordNode child) {
        for (RecordNode childNode : childNodes) {
            if (childNode.getRecordId() == child.getRecordId()) {
                return childNodes.indexOf(childNode);
            }
        }
        return -1;
    }

    public long getRecordId() {
        return this.recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    public void addChildNode(RecordNode child) {
        childNodes.add(child);
    }

    public void removeChildNode(RecordNode child) {
        childNodes.remove(child);
    }

    public void removeChildNode(int index) {
        childNodes.remove(index);
    }

    public void resetChildNodes() {
        childNodes = new Vector<RecordNode>();
        childNodesLoaded = false;
    }

    public boolean isChildNodesLoaded() {
        return childNodesLoaded;
    }

    public void setChildNodesLoaded(boolean childNodesLoaded) {
        this.childNodesLoaded = childNodesLoaded;
    }

    public RecordTreeNode[] getChildren() throws Exception {
        if (childNodesLoaded == false) {
            loadChildren();
        }
        RecordTreeNode[] dmtn = new RecordTreeNode[childNodes.size()];
        for (int i = 0; i < dmtn.length; ++i) {
            RecordNode childNode = childNodes.get(i);
            dmtn[i] = new RecordTreeNode(childNode, !(childNode.isLeafNode()));
        }
        return dmtn;
    }

}

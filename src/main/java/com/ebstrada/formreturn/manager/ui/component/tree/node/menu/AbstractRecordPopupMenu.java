package com.ebstrada.formreturn.manager.ui.component.tree.node.menu;

import java.awt.event.ActionEvent;

import javax.swing.JPopupMenu;

import com.ebstrada.formreturn.manager.ui.component.tree.RecordTree;
import com.ebstrada.formreturn.manager.ui.component.tree.RecordTreeNode;
import com.ebstrada.formreturn.manager.ui.component.tree.node.RecordNode;

public class AbstractRecordPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 1L;

    private RecordNode recordNode;
    private RecordTree tree;
    private RecordTreeNode node;

    public AbstractRecordPopupMenu(RecordNode recordNode, RecordTree tree, RecordTreeNode node) {
        this.recordNode = recordNode;
        this.tree = tree;
        this.node = node;
    }

    protected void expandAllMenuItemActionPerformed(ActionEvent e) {
        tree.expandAll(node);
    }

}

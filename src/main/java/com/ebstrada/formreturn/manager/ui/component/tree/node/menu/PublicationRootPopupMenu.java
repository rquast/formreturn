package com.ebstrada.formreturn.manager.ui.component.tree.node.menu;

import java.awt.event.*;
import javax.swing.*;

import com.ebstrada.formreturn.manager.ui.component.tree.RecordTree;
import com.ebstrada.formreturn.manager.ui.component.tree.RecordTreeNode;
import com.ebstrada.formreturn.manager.ui.component.tree.node.PublicationRootNode;

public class PublicationRootPopupMenu extends AbstractRecordPopupMenu {

    private static final long serialVersionUID = 1L;

    private PublicationRootNode publicationRootNode;

    public PublicationRootPopupMenu(PublicationRootNode publicationRootNode, RecordTree tree,
        RecordTreeNode node) {
        super(publicationRootNode, tree, node);
        this.publicationRootNode = publicationRootNode;
        initComponents();
    }

    @Override protected void expandAllMenuItemActionPerformed(ActionEvent e) {
        super.expandAllMenuItemActionPerformed(e);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        expandAllMenuItem = new JMenuItem();

        //======== this ========

        //---- expandAllMenuItem ----
        expandAllMenuItem.setText("Expand All");
        expandAllMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                expandAllMenuItemActionPerformed(e);
            }
        });
        add(expandAllMenuItem);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JMenuItem expandAllMenuItem;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

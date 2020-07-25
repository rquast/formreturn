package com.ebstrada.formreturn.manager.ui.component.tree.node.menu;

import java.awt.event.*;
import javax.swing.*;

import com.ebstrada.formreturn.manager.ui.component.tree.RecordTree;
import com.ebstrada.formreturn.manager.ui.component.tree.RecordTreeNode;
import com.ebstrada.formreturn.manager.ui.component.tree.node.FormNode;

public class FormPopupMenu extends AbstractRecordPopupMenu {

    private static final long serialVersionUID = 1L;

    private FormNode formNode;

    public FormPopupMenu(FormNode formNode, RecordTree tree, RecordTreeNode node) {
        super(formNode, tree, node);
        this.formNode = formNode;
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

package com.ebstrada.formreturn.manager.ui.component.tree;

import java.awt.Component;
import java.awt.event.*;
import java.util.Enumeration;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.*;
import javax.swing.tree.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.component.tree.node.RecordNode;
import com.ebstrada.formreturn.manager.ui.component.tree.node.PublicationRootNode;

public class RecordTree extends JTree {

    private static final long serialVersionUID = 1L;

    private transient SwingWorker<RecordTreeNode[], Void> worker;

    private TreeNodeFactory factory;


    private class IconNodeRenderer extends DefaultTreeCellRenderer {

        private static final long serialVersionUID = 1L;

        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {

            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
            RecordNode recordNode = null;
            if (userObject instanceof LinkObject) {
                LinkObject link = (LinkObject) userObject;
                recordNode = (RecordNode) link.getObject();
            } else if (userObject instanceof String) {
                recordNode = null;
            } else {
                recordNode = (RecordNode) userObject;
            }

            if (recordNode != null) {
                Icon icon = recordNode.getIcon();
                setIcon(icon);
            } else {
                setIcon(null);
            }

            return this;
        }
    }

    public RecordTree() {
        initComponents();
        setCellRenderer(new IconNodeRenderer());
        setSource(null);
    }

    public void setSource(Object source) {
        stopWorker();
        RecordTreeNode root = createRoot(source);
        DefaultTreeModel model = (DefaultTreeModel) getModel();
        model.setRoot(root);

        /*
         * Since nodes are added dynamically in this application, the only true
         * leaf nodes are nodes that don't allow children to be added. (By
         * default, askAllowsChildren is false and all nodes without children
         * are considered to be leaves.)
         *
         * But there's a complication: when the tree structure changes, JTree
         * pre-expands the root node unless it's a leaf. To avoid having the
         * root pre-expanded, we set askAllowsChildren *after* assigning the
         * new root.
         */

        model.setAsksAllowsChildren(true);
    }

    protected RecordTreeNode createRoot(Object source) {
        PublicationRootNode rootNode = new PublicationRootNode();
        factory = new DefaultTreeNodeFactory(rootNode);

        // this is a wrapper - use for testing with latency
        // factory = new SlowTreeNodeFactory(new DefaultTreeNodeFactory(rootNode), 1000);

        RecordTreeNode dmtn = new RecordTreeNode(rootNode, true);
        if (factory != null) {
            startWorker(factory, dmtn, false);
        }

        return dmtn;
    }

    private void thisTreeExpanded(final TreeExpansionEvent e) {
        stopWorker();
        RecordTreeNode node = (RecordTreeNode) e.getPath().getLastPathComponent();
        if (factory != null) {
            startWorker(factory, node, false);
        }
    }

    private void thisTreeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
        // TODO add your code here
        Object source = e.getSource();
    }

    public void expandAll(RecordTreeNode node) {

        // no matter what we do, we can't do an expand all - something to do
        // with swingworkers and JTrees
        // maybe try this out - http://www.jroller.com/Thierry/entry/swing_lazy_loading_in_a

        // expandAll(this, new TreePath(node), true);

        stopWorker();
        if (factory != null) {
            startWorker(factory, node, true);
        }
    }

    private static void expandAll(final JTree tree, final TreePath parent, final boolean expand) {
        // Traverse children
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TreeNode node = (TreeNode) parent.getLastPathComponent();
                if (node.getChildCount() >= 0) {
                    for (Enumeration e = node.children(); e.hasMoreElements(); ) {
                        TreeNode n = (TreeNode) e.nextElement();
                        TreePath path = parent.pathByAddingChild(n);
                        expandAll(tree, path, expand);
                    }
                }

                // Expansion or collapse must be done bottom-up
                if (expand) {
                    tree.expandPath(parent);
                } else {
                    tree.collapsePath(parent);
                }
            }
        });
    }

    protected void stopWorker() {
        if (worker != null) {
            worker.cancel(true);
            // worker set to null in finished
        }
    }

    protected RecordTreeNode createLoadingNode() {
        return new RecordTreeNode(Localizer.localize("UI", "LoadingPleaseWaitMessage"), false);
    }

    protected RecordTreeNode createErrorLoadingNode() {
        return new RecordTreeNode(Localizer.localize("UI", "ErrorLoadingMessage"), false);
    }

    private void setLoading(RecordTreeNode parent) {
        TreeModel model = getModel();
        if (model instanceof DefaultTreeModel) {
            DefaultTreeModel defaultModel = (DefaultTreeModel) model;
            int[] indices = new int[parent.getChildCount()];
            for (int i = 0; i < indices.length; i++) {
                indices[i] = i;
            }
            defaultModel.nodesWereInserted(parent, indices);
        }
    }

    public RecordNode getSelectedRecordNode() {
        RecordTreeNode rtn = (RecordTreeNode) this.getSelectionPath().getLastPathComponent();
        Object userObject = rtn.getUserObject();
        RecordNode recordNode = null;
        if (userObject instanceof LinkObject) {
            LinkObject link = (LinkObject) userObject;
            recordNode = (RecordNode) link.getObject();
        } else if (userObject instanceof String) {
            recordNode = null;
        } else {
            recordNode = (RecordNode) userObject;
        }
        return recordNode;
    }

    protected void setChildren(RecordTreeNode parent, RecordTreeNode... nodes) {
        if (nodes == null) {

        }
        TreeModel model = getModel();
        if (model instanceof DefaultTreeModel) {
            DefaultTreeModel defaultModel = (DefaultTreeModel) model;
            int childCount = parent.getChildCount();
            if (childCount > 0) {
                for (int i = 0; i < childCount; i++) {
                    defaultModel.removeNodeFromParent((MutableTreeNode) parent.getChildAt(0));
                }
            }
            for (int i = 0; i < nodes.length; i++) {
                defaultModel.insertNodeInto(nodes[i], parent, i);
            }
        }
    }

    protected RecordTreeNode[] getChildren(TreeNodeFactory fac, RecordTreeNode parent,
        boolean expandAll) throws Exception {
        Object userObject = parent.getUserObject();
        if (expandAll) {
            RecordTreeNode[] children = fac.createChildren(userObject);
            if (children != null) {
                for (RecordTreeNode child : children) {
                    child.setChildren(getChildren(fac, child, expandAll));
                }
            }
            return children;
        } else {
            return fac.createChildren(userObject);
        }
    }

    protected void loadChildren(final RecordTreeNode parent, final RecordTreeNode[] children,
        final boolean expandAll) {

        final JTree tree = this;

        for (int i = 0; i < children.length; i++) {
            parent.insert(children[i], i);
            if (children[i].getChildren() != null && expandAll) {
                loadChildren(children[i], children[i].getChildren(), expandAll);
            }
        }
        ((DefaultTreeModel) tree.getModel()).nodeStructureChanged(parent);

    }

    protected void startWorker(final TreeNodeFactory fac, final RecordTreeNode parent,
        final boolean expandAll) {

        setChildren(parent, createLoadingNode());
        setLoading(parent);

        worker = new SwingWorker<RecordTreeNode[], Void>() {

            protected RecordTreeNode[] doInBackground() {
                try {
                    RecordTreeNode[] children = getChildren(fac, parent, expandAll);
                    return children;
                } catch (Exception ex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                    return null;
                }
            }

            protected void done() {

                if (worker == this) {
                    worker = null;
                }

                try {
                    /*
                     * Get the children created by the factory and insert them
                     * into the local tree model.
                     */
                    final RecordTreeNode[] children = get();
                    if (children == null) {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                parent.removeAllChildren();
                                ((DefaultTreeModel) getModel()).nodeStructureChanged(parent);
                                setChildren(parent, createErrorLoadingNode());
                                setLoading(parent);
                            }
                        });
                        return;
                    } else {
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                parent.removeAllChildren();
                                loadChildren(parent, children, expandAll);
                            }
                        });
                    }
                } catch (CancellationException ex) {
                } catch (ExecutionException ex) {
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                } catch (InterruptedException ex) {
                    // event-dispatch thread won't be interrupted
                    throw new IllegalStateException(ex + "");
                } catch (Exception ex) {
                    parent.removeAllChildren();
                    com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
                }
            }

        };

        worker.execute();

    }

    private void thisTreeCollapsed(TreeExpansionEvent e) {
        stopWorker();
        RecordTreeNode node = (RecordTreeNode) e.getPath().getLastPathComponent();
        node.removeAllChildren();
        DefaultTreeModel model = (DefaultTreeModel) getModel();

        /*
         * To avoid having JTree re-expand the root node, we disable
         * ask-allows-children when we notify JTree about the new node
         * structure.
         */

        model.setAsksAllowsChildren(false);
        model.nodeStructureChanged(node);
        model.setAsksAllowsChildren(true);
    }

    private void showPopupMenu(Component c, int x, int y, RecordNode recordNode,
        RecordTreeNode node) {

        // TODO: MAKE A POPUP MENU FOR BETTER NAVIGATION OF THE TREE

        // JPopupMenu popup = recordNode.getPopupMenu(this, node);
        // popup.show(c, x, y);
    }

    private void thisMousePressed(MouseEvent e) {
        int selRow = getRowForLocation(e.getX(), e.getY());
        TreePath selPath = getPathForLocation(e.getX(), e.getY());
        if (selRow != -1) {
            if (e.isPopupTrigger()) {
                RecordTreeNode node = (RecordTreeNode) selPath.getLastPathComponent();

                Object userObject = node.getUserObject();
                RecordNode recordNode = null;
                if (userObject instanceof LinkObject) {
                    LinkObject link = (LinkObject) userObject;
                    recordNode = (RecordNode) link.getObject();
                } else if (userObject instanceof String) {
                    recordNode = null;
                } else {
                    recordNode = (RecordNode) userObject;
                }

                if (recordNode != null) {
                    showPopupMenu(e.getComponent(), e.getX(), e.getY(), recordNode, node);
                }

                e.consume();
            }
        }
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        setShowsRootHandles(true);
        //---- this ----
        addTreeExpansionListener(new TreeExpansionListener() {
            public void treeExpanded(TreeExpansionEvent e) {
                thisTreeExpanded(e);
            }

            public void treeCollapsed(TreeExpansionEvent e) {
                thisTreeCollapsed(e);
            }
        });
        addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                thisMousePressed(e);
            }
        });
        addTreeWillExpandListener(new TreeWillExpandListener() {
            public void treeWillExpand(TreeExpansionEvent e) throws ExpandVetoException {
                thisTreeWillExpand(e);
            }

            public void treeWillCollapse(TreeExpansionEvent e) throws ExpandVetoException {
            }
        });
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

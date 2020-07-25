package com.ebstrada.formreturn.manager.ui.component.tree;

public interface TreeNodeFactory {
    RecordTreeNode[] createChildren(Object userObject) throws Exception;
}

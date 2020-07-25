// http://www.devx.com/getHelpOn/10MinuteSolution/20425
// courtesy of Daniel F. Savarese

package com.ebstrada.formreturn.manager.ui.editor.table;

import javax.swing.table.DefaultTableModel;

public class JTableComponentModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    public JTableComponentModel() {
        super();
    }

    public JTableComponentModel(Object[][] obj, String[] str) {
        super(obj, str);
    }

    public JTableComponentModel(String[] str, int rowCount) {
        super(str, rowCount);
    }

    @SuppressWarnings("unchecked") @Override public Class getColumnClass(int column) {
        return super.getValueAt(0, column).getClass();
    }

}

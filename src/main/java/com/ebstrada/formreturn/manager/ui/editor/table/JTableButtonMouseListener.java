// http://www.devx.com/getHelpOn/10MinuteSolution/20425
// courtesy of Daniel F. Savarese

package com.ebstrada.formreturn.manager.ui.editor.table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

public class JTableButtonMouseListener implements MouseListener {

    private JTable __table;

    private void __forwardEventToButton(MouseEvent e) {

        TableColumnModel columnModel = __table.getColumnModel();
        int column = columnModel.getColumnIndexAtX(e.getX());
        int row = e.getY() / __table.getRowHeight();
        Object value;
        JButton button;
        MouseEvent buttonEvent;

        if (row >= __table.getRowCount() || row < 0 || column >= __table.getColumnCount()
            || column < 0) {
            return;
        }

        value = __table.getValueAt(row, column);

        if (!(value instanceof JButton)) {
            return;
        }

        button = (JButton) value;

        buttonEvent = SwingUtilities.convertMouseEvent(__table, e, button);
        button.dispatchEvent(buttonEvent);
        __table.repaint();

    }

    public JTableButtonMouseListener(JTable table) {
        __table = table;
    }

    public void mouseClicked(MouseEvent e) {
        __forwardEventToButton(e);
    }

    public void mouseEntered(MouseEvent e) {
        __forwardEventToButton(e);
    }

    public void mouseExited(MouseEvent e) {
        __forwardEventToButton(e);
    }

    public void mousePressed(MouseEvent e) {
        __forwardEventToButton(e);
    }

    public void mouseReleased(MouseEvent e) {
        __forwardEventToButton(e);
    }

}

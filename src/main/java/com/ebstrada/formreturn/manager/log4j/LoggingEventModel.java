package com.ebstrada.formreturn.manager.log4j;

import java.text.DateFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.AbstractListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import org.apache.log4j.spi.LoggingEvent;

/**
 * This class implements a ListModel and a TableModel for log4j LoggingEvents.<br>
 * The TableModel has fixed columns, use
 * <code>getColumnName()<code to find out the names.<br>
 * The LoggingEventModel is used internally in the ComponentAppender and the DialogAppender.
 *
 * @author <a HREF="mailto:V.Mentzner@psi-bt.de">Volker Mentzner</a>
 */
public class LoggingEventModel extends AbstractListModel implements TableModel {

    private static final long serialVersionUID = 1L;

    Vector<Object> objects;

    Object selectedObject;

    protected String tableColumns[] =
        {"priority", "message", "category", "date_default", "time_default", "date_short",
            "time_short", "date_long", "time_long", "date_full", "time_full", "thread", "ndc",
            "file", "line", "method"};

    protected EventListenerList tableListenerList = new EventListenerList();

    /**
     * Constructs an empty LoggingEventModel object.
     */
    public LoggingEventModel() {
        objects = new Vector<Object>();
    }

    /**
     * Sets the selected item.
     *
     * @param anObject
     */
    public void setSelectedItem(Object anObject) {
        if ((selectedObject != null && !selectedObject.equals(anObject))
            || selectedObject == null && anObject != null) {
            selectedObject = anObject;
            fireContentsChanged(this, -1, -1);
            fireTableDataChanged();
        }
    }

    /**
     * Returns the selected item.
     *
     * @return the selected item
     */
    public Object getSelectedItem() {
        return selectedObject;
    }

    /**
     * Returns the length of the list.
     *
     * @return the length of the list
     */
    public int getSize() {
        return objects.size();
    }

    /**
     * Returns the value at the specified index.
     *
     * @param index -
     *              position of value
     * @return the value at the specified index
     */
    public Object getElementAt(int index) {
        if (index >= 0 && index < objects.size()) {
            return objects.elementAt(index);
        } else {
            return null;
        }
    }

    /**
     * Returns the index-position of the specified object in the list.
     *
     * @param anObject
     * @return an int representing the index position, where 0 is the first
     * position
     */
    public int getIndexOf(Object anObject) {
        return objects.indexOf(anObject);
    }

    /**
     * Adds an item to the end of the model.
     *
     * @param anObject -
     *                 the Object to be added
     */
    public void addElement(Object anObject) {
        // objects.addElement(anObject);
        objects.insertElementAt(anObject, 0); // reverse list.
        fireIntervalAdded(this, objects.size() - 1, objects.size() - 1);
        fireTableRowsInserted(objects.size() - 1, objects.size() - 1);
        if (objects.size() == 1 && selectedObject == null && anObject != null) {
            setSelectedItem(anObject);
        }
    }

    /**
     * Adds an item at a specific index
     *
     * @param anObject -
     *                 the Object to be added
     * @param index    -
     *                 location to add the object
     */
    public void insertElementAt(Object anObject, int index) {
        objects.insertElementAt(anObject, index);
        fireIntervalAdded(this, index, index);
        fireTableRowsInserted(index, index);
    }

    /**
     * Removes an item at a specific index
     *
     * @param index -
     *              location of object to be removed
     */
    public void removeElementAt(int index) {
        if (getElementAt(index) == selectedObject) {
            if (index == 0) {
                setSelectedItem(getSize() == 1 ? null : getElementAt(index + 1));
            } else {
                setSelectedItem(getElementAt(index - 1));
            }
        }

        objects.removeElementAt(index);

        fireIntervalRemoved(this, index, index);
        fireTableRowsDeleted(index, index);
    }

    /**
     * Removes an item from the model.
     *
     * @param anObject -
     *                 the Object to be removed
     */
    public void removeElement(Object anObject) {
        int index = objects.indexOf(anObject);
        if (index != -1) {
            removeElementAt(index);
        }
    }

    /**
     * Empties the list.
     */
    public void removeAllElements() {
        if (objects.size() > 0) {
            int firstIndex = 0;
            int lastIndex = objects.size() - 1;
            objects.removeAllElements();
            selectedObject = null;
            fireIntervalRemoved(this, firstIndex, lastIndex);
            fireTableDataChanged();
        }
    }

    /**
     * Returns the number of rows in this data table.
     *
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return objects.size();
    }

    /**
     * Returns the number of columns in this data table.
     *
     * @return the number of columns in the model
     */
    public int getColumnCount() {
        return tableColumns.length;
    }

    /**
     * Returns a default name for the column with column number
     *
     * @param column the column being queried
     * @return a string containing the default name of <code>column</code>
     */
    public String getColumnName(int column) {
        if ((column >= 0) && (column < tableColumns.length)) {
            return tableColumns[column];
        } else {
            return null;
        }
    }

    /**
     * Returns <code>Object.class</code> regardless of
     * <code>columnIndex</code>.
     *
     * @param columnIndex the column being queried
     * @return the Object.class
     */
    @SuppressWarnings("unchecked") public Class getColumnClass(int columnIndex) {
        return Object.class;
    }

    /**
     * Returns false. This is the default implementation for all cells.
     *
     * @param rowIndex    the row being queried
     * @param columnIndex the column being queried
     * @return false
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    /**
     * Returns an attribute value for the cell at <code>row</code> and
     * <code>column</code>.
     *
     * @param row    the row whose value is to be queried
     * @param column the column whose value is to be queried
     * @return the value Object at the specified cell
     */
    public Object getValueAt(int row, int column) {
        LoggingEvent loggingEvent = (LoggingEvent) objects.elementAt(row);
        DateFormat df;
        switch (column) {
            case 0:
                return loggingEvent.getLevel().toString();
            case 1:
                return loggingEvent.getRenderedMessage();
            case 2:
                return loggingEvent.getLoggerName();
            case 3:
                df = DateFormat.getDateInstance(DateFormat.DEFAULT);
                return df.format(new Date(loggingEvent.timeStamp));
            case 4:
                df = DateFormat.getTimeInstance(DateFormat.DEFAULT);
                return df.format(new Date(loggingEvent.timeStamp));
            case 5:
                df = DateFormat.getDateInstance(DateFormat.SHORT);
                return df.format(new Date(loggingEvent.timeStamp));
            case 6:
                df = DateFormat.getTimeInstance(DateFormat.SHORT);
                return df.format(new Date(loggingEvent.timeStamp));
            case 7:
                df = DateFormat.getDateInstance(DateFormat.LONG);
                return df.format(new Date(loggingEvent.timeStamp));
            case 8:
                df = DateFormat.getTimeInstance(DateFormat.LONG);
                return df.format(new Date(loggingEvent.timeStamp));
            case 9:
                df = DateFormat.getDateInstance(DateFormat.FULL);
                return df.format(new Date(loggingEvent.timeStamp));
            case 10:
                df = DateFormat.getTimeInstance(DateFormat.FULL);
                return df.format(new Date(loggingEvent.timeStamp));
            case 11:
                return loggingEvent.getThreadName();
            case 12:
                return loggingEvent.getNDC();
            case 13:
                return loggingEvent.getLocationInformation().getFileName();
            case 14:
                return loggingEvent.getLocationInformation().getLineNumber();
            case 15:
                return loggingEvent.getLocationInformation().getMethodName();
            default:
                return null;
        }
    }

    /**
     * This empty implementation is provided so users don't have to implement
     * this method if their data model is not editable.
     *
     * @param aValue      value to assign to cell
     * @param rowIndex    row of cell
     * @param columnIndex column of cell
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
    }

    /**
     * Adds a listener to the list that's notified each time a change to the
     * data model occurs.
     *
     * @param l -
     *          the TableModelListener
     */
    public void addTableModelListener(TableModelListener l) {
        tableListenerList.add(TableModelListener.class, l);
    }

    /**
     * Removes a listener from the list that's notified each time a change to
     * the data model occurs.
     *
     * @param l -
     *          the TableModelListener
     */
    public void removeTableModelListener(TableModelListener l) {
        tableListenerList.remove(TableModelListener.class, l);
    }

    /**
     * Notifies all listeners that all cell values in the table's rows may have
     * changed. The number of rows may also have changed and the
     * <code>JTable</code> should redraw the table from scratch. The structure
     * of the table (as in the order of the columns) is assumed to be the same.
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableDataChanged() {
        fireTableChanged(new TableModelEvent(this));
    }

    /**
     * Notifies all listeners that the table's structure has changed. The number
     * of columns in the table, and the names and types of the new columns may
     * be different from the previous state. If the <code>JTable</code>
     * receives this event and its <code>autoCreateColumnsFromModel</code>
     * flag is set it discards any table columns that it had and reallocates
     * default columns in the order they appear in the model. This is the same
     * as calling <code>setModel(TableModel)</code> on the <code>JTable</code>.
     *
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableStructureChanged() {
        fireTableChanged(new TableModelEvent(this, TableModelEvent.HEADER_ROW));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been inserted.
     *
     * @param firstRow the first row
     * @param lastRow  the last row
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsInserted(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow, TableModelEvent.ALL_COLUMNS,
            TableModelEvent.INSERT));
    }

    /**
     * Notifies all listeners that rows in the range
     * <code>[firstRow, lastRow]</code>, inclusive, have been deleted.
     *
     * @param firstRow the first row
     * @param lastRow  the last row
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableRowsDeleted(int firstRow, int lastRow) {
        fireTableChanged(new TableModelEvent(this, firstRow, lastRow, TableModelEvent.ALL_COLUMNS,
            TableModelEvent.DELETE));
    }

    /**
     * Forwards the given notification event to all
     * <code>TableModelListeners</code> that registered themselves as
     * listeners for this table model.
     *
     * @param e the event to be forwarded
     * @see #addTableModelListener
     * @see TableModelEvent
     * @see EventListenerList
     */
    public void fireTableChanged(TableModelEvent e) {
        // Guaranteed to return a non-null array
        Object[] listeners = tableListenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == TableModelListener.class) {
                ((TableModelListener) listeners[i + 1]).tableChanged(e);
            }
        }
    }
}

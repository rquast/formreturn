package com.ebstrada.formreturn.manager.ui.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;

public class SortedComboBoxModel extends DefaultComboBoxModel {

    private static final long serialVersionUID = 1L;

    public SortedComboBoxModel() {
        super();
    }

    public SortedComboBoxModel(Object[] items) {
        super();

        Arrays.sort(items);
        int size = items.length;

        for (int i = 0; i < size; i++) {
            insertElementAt(items[i], 0);
        }

        setSelectedItem(items[0]);
    }

    public SortedComboBoxModel(Vector items) {
        super();

        Collections.sort(items);
        int size = items.size();

        for (int i = 0; i < size; i++) {
            insertElementAt(items.elementAt(i), 0);
        }

        setSelectedItem(items.elementAt(0));
    }

    public void addElement(Object element) {
        insertElementAt(element, 0);
    }

    public void insertElementAt(Object element, int index) {
        int size = getSize();

        // Determine where to insert element to keep list in sorted order

        for (index = 0; index < size; index++) {
            Comparable c = (Comparable) getElementAt(index);

            if (c.compareTo(element) > 0)
                break;
        }

        super.insertElementAt(element, index);
    }
}

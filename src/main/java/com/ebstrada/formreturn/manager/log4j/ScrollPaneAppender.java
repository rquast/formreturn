package com.ebstrada.formreturn.manager.log4j;

import java.awt.Component;

import org.apache.log4j.spi.LoggingEvent;

/**
 * This class implements a log appender for the log4j logging system. It can log
 * to AbstractAppenderScrollPane and derived classes.
 *
 * @author <a HREF="mailto:V.Mentzner@psi-bt.de">Volker Mentzner</a>
 */
public class ScrollPaneAppender extends ComponentAppender {

    /**
     * Creates a new object with 1 possible entry.
     */
    public ScrollPaneAppender() {
        this(null);
    }

    /**
     * Creates a new object for component comp with 1 possible entry.
     *
     * @param comp -
     *             component to show the logging information
     */
    public ScrollPaneAppender(Component comp) {
        this(comp, 1);
    }

    /**
     * Creates a new object for component comp with <code>maxEntries</code>
     * possible entries.
     *
     * @param comp       -
     *                   component to show the logging information
     * @param maxEntries -
     *                   maximum number of logging entries
     */
    public ScrollPaneAppender(Component comp, int maxEntries) {
        super(comp, maxEntries);
    }

    /**
     * Initializes a new component value
     *
     * @param comp -
     *             the component to initialize
     */
    @Override protected void initializeNewComponent(Component comp) {
        if (comp instanceof AbstractAppenderScrollPane) {
            ((AbstractAppenderScrollPane) comp).setAppender(this);
        }
    }

    /**
     * Sets the maximum number of logging entries.
     *
     * @param value -
     *              maximum number of logging entries. This value is ignored
     *              if the component supports just 1 line.
     */
    @Override public void setMaxEntries(int value) {
        if (comp instanceof AbstractAppenderScrollPane) {
            ((AbstractAppenderScrollPane) comp).setMaxEntries(value);
        }
        super.setMaxEntries(value);
    }

    /**
     * Appends the logging information to the UI element.
     *
     * @param event -
     *              logging event information
     */
    @Override public void append(LoggingEvent event) {
        if (comp instanceof AbstractAppenderScrollPane) {
            ((AbstractAppenderScrollPane) comp).addElement(event);
        } else {
            super.append(event);
        }
    }

    /**
     * Removes all logging entries from the UI elements
     */
    @Override public void reset() {
        super.reset();
        if (comp instanceof AbstractAppenderScrollPane) {
            ((AbstractAppenderScrollPane) comp).removeAllElements();
        }
    }

}

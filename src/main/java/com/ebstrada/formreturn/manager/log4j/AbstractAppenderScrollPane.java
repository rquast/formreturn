package com.ebstrada.formreturn.manager.log4j;

import javax.swing.JScrollPane;

import org.apache.log4j.spi.LoggingEvent;

public class AbstractAppenderScrollPane extends JScrollPane {

    private static final long serialVersionUID = 1L;

    protected LoggingEventModel logModel;

    protected int maxEntries;

    private ComponentAppender appender;

    public AbstractAppenderScrollPane(int maxEntries) {
        super();
        this.maxEntries = maxEntries;
        appender = null;
        logModel = new LoggingEventModel();
    }

    public void setMaxEntries(int value) {
        int toomuch = logModel.getSize() - value;

        for (int i = 0; i < toomuch; i++) {
            logModel.removeElementAt(0);
        }

        maxEntries = value;
    }

    public ComponentAppender getAppender() {
        return appender;
    }

    public void setAppender(ComponentAppender appender) {
        this.appender = appender;
    }

    public void removeElementAt(int index) {
        logModel.removeElementAt(index);
    }

    public void removeAllElements() {
        logModel.removeAllElements();
    }

    public void addElement(LoggingEvent event) {
        if (logModel.getSize() == maxEntries) {
            logModel.removeElementAt(0);
        }
        logModel.addElement(event);
    }

}

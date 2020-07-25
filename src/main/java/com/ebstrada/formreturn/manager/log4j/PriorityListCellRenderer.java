package com.ebstrada.formreturn.manager.log4j;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.UIManager;

import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

/**
 * This class implements a ListCellRenderer for logging priorities.
 *
 * @author <a HREF="mailto:V.Mentzner@psi-bt.de">Volker Mentzner</a>
 */
public class PriorityListCellRenderer extends DefaultListCellRenderer {

    private static final long serialVersionUID = 1L;

    private boolean showIcons;

    private boolean showPriorityColors;

    private ComponentAppender appender;

    /**
     * Creates a new PriorityListCellRenderer that shows a simple list
     */
    public PriorityListCellRenderer() {
        this(false, false, null);
    }

    /**
     * Creates a new PriorityListCellRenderer that can show a colored list with
     * icons
     */
    public PriorityListCellRenderer(boolean showIcons, boolean showPriorityColors,
        ComponentAppender appender) {
        super();
        this.showIcons = showIcons;
        this.showPriorityColors = showPriorityColors;
        this.appender = appender;
    }

    /**
     * Returns the current showIcons value.
     *
     * @return the current showIcons value
     */
    public boolean getShowIcons() {
        return showIcons;
    }

    /**
     * Sets a new showIcons value.
     *
     * @param showIcons -
     *                  the new value
     */
    public void setShowIcons(boolean showIcons) {
        this.showIcons = showIcons;
    }

    /**
     * Returns the current showPriorityColors value.
     *
     * @return the current showPriorityColors value
     */
    public boolean getShowPriorityColors() {
        return showPriorityColors;
    }

    /**
     * Sets a new showPriorityColors value.
     *
     * @param showPriorityColors -
     *                           the new value
     */
    public void setShowPriorityColors(boolean showPriorityColors) {
        this.showPriorityColors = showPriorityColors;
    }

    /**
     * Returns the current appender value.
     *
     * @return the current appender value
     */
    public ComponentAppender getAppender() {
        return appender;
    }

    /**
     * Sets a new appender value.
     *
     * @param appender -
     *                 the new value
     */
    public void setAppender(ComponentAppender appender) {
        this.appender = appender;
    }

    /**
     * Return a component that has been configured to display the specified
     * value.
     *
     * @param list         -
     *                     The JList we're painting.
     * @param value        -
     *                     The value returned by list.getModel().getElementAt(index).
     * @param index        -
     *                     The cells index.
     * @param isSelected   -
     *                     True if the specified cell was selected.
     * @param cellHasFocus -
     *                     True if the specified cell has the focus.
     */
    @Override public Component getListCellRendererComponent(JList list, Object value, int index,
        boolean isSelected, boolean cellHasFocus) {
        if ((value instanceof LoggingEvent) && (appender != null)) {
            LoggingEvent event = (LoggingEvent) value;
            setComponentOrientation(list.getComponentOrientation());
            if (showPriorityColors) {
                if (isSelected) {
                    setBackground(appender.getForeground(event.getLevel()));
                    setForeground(appender.getBackground(event.getLevel()));
                } else {
                    setBackground(appender.getBackground(event.getLevel()));
                    setForeground(appender.getForeground(event.getLevel()));
                }
            } else {
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
            }
            if (list.getModel() instanceof LoggingEventModel) {
                Layout layout = appender.getLayout();
                setText(layout.format(event));
            } else {
                setText(event.getRenderedMessage());
            }
            if (showIcons) {
                setIcon(appender.getIcon(event.getLevel()));
            } else {
                setIcon(null);
            }
            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setBorder((cellHasFocus) ?
                UIManager.getBorder("List.focusCellHighlightBorder") :
                DefaultListCellRenderer.noFocusBorder);
        } else {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }

        return this;
    }
}

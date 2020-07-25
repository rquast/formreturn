package com.ebstrada.formreturn.manager.log4j;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.component.JStatusBar;
import com.ebstrada.formreturn.manager.ui.frame.ErrorLogFrame;
import com.ebstrada.formreturn.manager.ui.panel.DesktopDockingPanel;

public class ListAppenderScrollPane extends AbstractAppenderScrollPane {

    private static final long serialVersionUID = 1L;

    protected JList jlist;

    private JFrame parentFrame;

    private JScrollPane scrollPane;

    public ListAppenderScrollPane(JFrame frame, int maxEntries) {
        super(maxEntries);
        parentFrame = frame;
        init();
    }

    protected void init() {
        jlist = new JList();
        jlist.setModel(super.logModel);
        jlist.setCellRenderer(new PriorityListCellRenderer(true, true, getAppender()));
        jlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scrollPane = new JScrollPane(jlist);
        scrollPane.setPreferredSize(new Dimension(400, 200));
        scrollPane.setViewportView(jlist);
        scrollPane.setViewportBorder(new EmptyBorder(0, 0, 0, 0));

        jlist.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (((LoggingEventModel) jlist.getModel()).getRowCount() > 0) {
                    ((Main) parentFrame).activateErrorLogFrame();
                    ((ErrorLogFrame) ((Main) parentFrame).getErrorLogFrame())
                        .setErrorLogModel(jlist.getModel());
                    ((ErrorLogFrame) ((Main) parentFrame).getErrorLogFrame()).setErrorObject(
                        jlist.getModel().getElementAt(((JList) e.getSource()).getSelectedIndex()));
                }
            }
        });

        LoggingEventModel lem = (LoggingEventModel) jlist.getModel();

        lem.addListDataListener(new ListDataListener() {
            public void contentsChanged(ListDataEvent e) {
            }

            public void intervalAdded(ListDataEvent e) {

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {

                        LoggingEventModel loggingEventModel = (LoggingEventModel) jlist.getModel();
                        LoggingEvent loggingEvent =
                            (LoggingEvent) loggingEventModel.getElementAt(0);

                        if (loggingEvent.getLevel() == Level.ERROR
                            || loggingEvent.getLevel() == Level.FATAL || Main.getInstance()
                            .isDebugMode()) {
                            // TODO: change this code to make sure system console
                            // appears if minimized
                            if (Main.getInstance().isDebugMode()) {
                                Main.getInstance().getDesktopDockingPanel()
                                    .setSystemConsolePanelState(DesktopDockingPanel.PANEL_RESTORED);
                            }

                            // ((Main)
                            // parentFrame).getSystemConsoleScrollPane().setSelectedIndex(2);
                            ((Main) parentFrame).activateErrorLogFrameNoSwitch();

                            ((ErrorLogFrame) ((Main) parentFrame).getErrorLogFrame())
                                .setErrorLogModel(jlist.getModel());
                            ((ErrorLogFrame) ((Main) parentFrame).getErrorLogFrame())
                                .setErrorObject(jlist.getModel().getElementAt(0));
                            JStatusBar.reset();
                        }

                    }
                });

            }

            public void intervalRemoved(ListDataEvent e) {
            }
        });

    }

    @Override public void setAppender(ComponentAppender appender) {
        super.setAppender(appender);
        ListCellRenderer lcr = jlist.getCellRenderer();
        if (lcr instanceof PriorityListCellRenderer) {
            ((PriorityListCellRenderer) lcr).setAppender(appender);
        }
    }

    protected void deleteEntry() {
        int size = super.logModel.getSize();
        if (size > 0) {
            int i = jlist.getSelectedIndex();
            if (i >= 0) {
                super.logModel.removeElementAt(i);
            }
        }
    }

    protected void deleteAllEntries() {
        super.logModel.removeAllElements();
    }

    public JList getList() {
        return jlist;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void setScrollPane(JScrollPane scrollPane) {
        this.scrollPane = scrollPane;
    }

}

package com.ebstrada.formreturn.manager.ui.frame;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import javax.swing.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.spi.LoggingEvent;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.log4j.ListAppenderScrollPane;
import com.ebstrada.formreturn.manager.log4j.LoggingEventModel;
import com.ebstrada.formreturn.manager.ui.Main;

public class ErrorLogFrame extends JPanel implements ClipboardOwner {

    private static final long serialVersionUID = 1L;

    private LoggingEventModel loggingEventModel;

    private LoggingEvent selectedEvent;

    private JFrame mainFrame;

    public ErrorLogFrame(JFrame frame) {
        mainFrame = frame;
        initComponents();
        setName(Localizer.localize("UI", "ErrorLogFrameTitle"));
        messageSplitPane.setDividerLocation(100);
        scrollPane1.getHorizontalScrollBar().setValue(0);
        scrollPane2.getHorizontalScrollBar().setValue(0);
    }

    private void button2ActionPerformed(ActionEvent e) {
        JScrollPane jsp = ((Main) mainFrame).getSystemConsoleScrollPane();
        JList jlist = ((ListAppenderScrollPane) jsp).getList();
        if (jlist.getSelectedIndex() == -1) {
            loggingEventModel.removeElementAt(0);
        } else {
            loggingEventModel.removeElementAt(jlist.getSelectedIndex());
        }
        Main.getInstance().deactivateErrorLogFrame();
    }

    private void button1ActionPerformed(ActionEvent e) {
        Main.getInstance().deactivateErrorLogFrame();
    }

    private void copyToClipboardButtonActionPerformed(ActionEvent e) {

        String propertiesString = "";

        propertiesString += textPane2.getText() + "\n\n";

        String lineEnding = System.getProperty("line.separator");

        Object[] keys = System.getProperties().keySet().toArray();
        Object[] values = System.getProperties().values().toArray();

        for (int i = 0; i < keys.length; i++) {
            propertiesString += keys[i] + " = " + values[i] + lineEnding;
        }

        setClipboardContents(propertiesString);
    }

    public void setClipboardContents(String aString) {
        StringSelection stringSelection = new StringSelection(aString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }



    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        panel3 = new JPanel();
        deleteMessageButton = new JButton();
        copyToClipboardButton = new JButton();
        closeButton = new JButton();
        panel2 = new JPanel();
        messageTypeLabel = new JLabel();
        errorType = new JLabel();
        messageTimeLabel = new JLabel();
        errorTime = new JLabel();
        panel1 = new JPanel();
        messageLabel = new JLabel();
        messageSplitPane = new JSplitPane();
        scrollPane2 = new JScrollPane();
        textPane1 = new JTextPane();
        scrollPane1 = new JScrollPane();
        textPane2 = new JTextPane();

        //======== this ========
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {0.0, 1.0, 0.0, 1.0E-4};

        //======== panel3 ========
        {
            panel3.setBorder(new EmptyBorder(8, 8, 8, 8));
            panel3.setLayout(new GridBagLayout());
            ((GridBagLayout) panel3.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
            ((GridBagLayout) panel3.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) panel3.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 0.0, 0.0, 1.0E-4};
            ((GridBagLayout) panel3.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- deleteMessageButton ----
            deleteMessageButton.setFont(UIManager.getFont("Button.font"));
            deleteMessageButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    button2ActionPerformed(e);
                }
            });
            deleteMessageButton
                .setText(Localizer.localize("UI", "ErrorLogFrameDeleteMessageButtonText"));
            panel3.add(deleteMessageButton,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

            //---- copyToClipboardButton ----
            copyToClipboardButton.setFont(UIManager.getFont("Button.font"));
            copyToClipboardButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    copyToClipboardButtonActionPerformed(e);
                }
            });
            copyToClipboardButton.setText(Localizer.localize("UI", "CopyToClipboardButtonText"));
            panel3.add(copyToClipboardButton,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

            //---- closeButton ----
            closeButton.setFont(UIManager.getFont("Button.font"));
            closeButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    button1ActionPerformed(e);
                }
            });
            closeButton.setText(Localizer.localize("UI", "CloseButtonText"));
            panel3.add(closeButton,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel3, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //======== panel2 ========
        {
            panel2.setBorder(new EmptyBorder(8, 8, 8, 8));
            panel2.setLayout(new GridBagLayout());
            ((GridBagLayout) panel2.getLayout()).columnWidths = new int[] {0, 0, 15, 0, 0, 0, 0};
            ((GridBagLayout) panel2.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) panel2.getLayout()).columnWeights =
                new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel2.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- messageTypeLabel ----
            messageTypeLabel.setFont(UIManager.getFont("Label.font"));
            messageTypeLabel.setText(Localizer.localize("UI", "ErrorLogFrameMessageTypeLabel"));
            panel2.add(messageTypeLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 5), 0, 0));
            panel2.add(errorType,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

            //---- messageTimeLabel ----
            messageTimeLabel.setFont(UIManager.getFont("Label.font"));
            messageTimeLabel.setText(Localizer.localize("UI", "ErrorLogFrameMessageTimeLabel"));
            panel2.add(messageTimeLabel,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- errorTime ----
            errorTime.setFont(UIManager.getFont("Label.font"));
            panel2.add(errorTime,
                new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
        }
        add(panel2, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //======== panel1 ========
        {
            panel1.setBorder(new EmptyBorder(8, 8, 8, 8));
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {0.0, 1.0, 1.0E-4};

            //---- messageLabel ----
            messageLabel.setFont(UIManager.getFont("TitledBorder.font"));
            messageLabel.setText(Localizer.localize("UI", "ErrorLogFrameMessageLabel"));
            panel1.add(messageLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 0), 0, 0));

            //======== messageSplitPane ========
            {
                messageSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
                messageSplitPane.setOneTouchExpandable(true);

                //======== scrollPane2 ========
                {

                    //---- textPane1 ----
                    textPane1.setFont(UIManager.getFont("TextPane.font"));
                    scrollPane2.setViewportView(textPane1);
                }
                messageSplitPane.setTopComponent(scrollPane2);

                //======== scrollPane1 ========
                {

                    //---- textPane2 ----
                    textPane2.setFont(UIManager.getFont("TextPane.font"));
                    scrollPane1.setViewportView(textPane2);
                }
                messageSplitPane.setBottomComponent(scrollPane1);
            }
            panel1.add(messageSplitPane,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(panel1, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // //GEN-END:initComponents
    }

    public void setErrorLogModel(ListModel lem) {
        loggingEventModel = (LoggingEventModel) lem;
    }

    public LoggingEventModel getErrorLogModel() {
        return loggingEventModel;
    }

    public void setErrorObject(Object obj) {
        selectedEvent = (LoggingEvent) obj;
        refresh();
    }

    public LoggingEvent getErrorObject() {
        return selectedEvent;
    }

    public void refresh() {

        if (selectedEvent != null) {
            try {
                Throwable ex = selectedEvent.getThrowableInformation().getThrowable();
                if (ex != null) {
                    textPane1.setText(ex.getMessage() + "\n\n");
                } else {
                    textPane1.setText("");
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ex.printStackTrace(new PrintStream(baos, true));
                textPane2.setText(baos.toString());
                errorType.setText(selectedEvent.getLevel().toString());
                Calendar gpsTime = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
                gpsTime.setTimeInMillis(selectedEvent.timeStamp);
                errorTime.setText(gpsTime.getTime().toString());
            } catch (Exception e) {
                // TODO: handle exception
            }

            scrollPane1.getHorizontalScrollBar().setValue(0);
            scrollPane2.getHorizontalScrollBar().setValue(0);

        }

    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel panel3;
    private JButton deleteMessageButton;
    private JButton copyToClipboardButton;
    private JButton closeButton;
    private JPanel panel2;
    private JLabel messageTypeLabel;
    private JLabel errorType;
    private JLabel messageTimeLabel;
    private JLabel errorTime;
    private JPanel panel1;
    private JLabel messageLabel;
    private JSplitPane messageSplitPane;
    private JScrollPane scrollPane2;
    private JTextPane textPane1;
    private JScrollPane scrollPane1;
    private JTextPane textPane2;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // do nothing
    }
}

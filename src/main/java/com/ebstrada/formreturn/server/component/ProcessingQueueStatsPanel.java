package com.ebstrada.formreturn.server.component;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;

import com.ebstrada.formreturn.manager.gef.util.Localizer;

public class ProcessingQueueStatsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private static final int MAX_LINES = 500;

    private int imagesInQueue = 0;
    private int secondsLeft = 0;

    private Timer statusUpdateTimer;

    private double lastProcessTime;
    private long lastProcessTimeSetAt;

    private String filename;

    public ProcessingQueueStatsPanel() {
        initComponents();
        startTimers();
    }

    public void startTimers() {
        statusUpdateTimer = new Timer(1000, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                updateStatsAction();
            }
        });
        statusUpdateTimer.start();
    }

    public void updateStatsAction() {
        setImagesInQueueStatus(getImagesInQueue() + "");

        long currentTime = System.currentTimeMillis();

        long minusDifference = currentTime - lastProcessTimeSetAt;

        long secondsLeft = (long) ((lastProcessTime * getImagesInQueue()) - minusDifference) / 1000;
        if (secondsLeft <= 0) {
            secondsLeft = 0;
            setEstimatedTimeLeftStatusLabel("0");
            setFilenameStatusLabel(null);
            return;
        }

        setEstimatedTimeLeftStatusLabel(secondsToDHMS(secondsLeft));
        setFilenameStatusLabel(getFilename());

    }

    public String secondsToDHMS(long time) {

        long seconds = time % 60;
        long minutes = (time / 60) % 60;
        long hours = (time / 3600) % 24;
        long days = time / 86400;

        String uptime = "";
        if (days > 0) {
            uptime += String.format("%02d", days) + ":";
        }
        if (hours > 0) {
            uptime += String.format("%02d", hours) + ":";
        }
        if (minutes > 0) {
            uptime += String.format("%02d", minutes) + ":";
        }
        uptime += String.format("%02d", seconds);

        return uptime;

    }

    public void setImagesInQueueStatus(final String status) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                imagesInQueueStatusLabel.setText(status);
            }
        });
    }

    public void setEstimatedTimeLeftStatusLabel(final String status) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                estimatedTimeLeftStatusLabel.setText(status);
            }
        });
    }

    public void setFilenameStatusLabel(final String status) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                filenameStatusLabel.setText(status);
            }
        });
    }

    public void appendProcessingQueueStatusTextArea(final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Element root = processingQueueStatsTextArea.getDocument().getDefaultRootElement();
                while (root.getElementCount() > MAX_LINES) {
                    Element firstLine = root.getElement(0);
                    try {
                        processingQueueStatsTextArea.getDocument()
                            .remove(0, firstLine.getEndOffset());
                    } catch (BadLocationException ble) {
                    }
                }
                processingQueueStatsTextArea.append(message);
            }
        });
    }

    public int getImagesInQueue() {
        return imagesInQueue;
    }

    public void setImagesInQueue(int imagesInQueue) {
        this.imagesInQueue = imagesInQueue;
    }

    public int getSecondsLeft() {
        return secondsLeft;
    }

    public void setSecondsLeft(int secondsLeft) {
        this.secondsLeft = secondsLeft;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        scrollPane1 = new JScrollPane();
        processingQueueStatsTextArea = new JTextArea();
        panel1 = new JPanel();
        imagesInQueueLabel = new JLabel();
        imagesInQueueStatusLabel = new JLabel();
        estimatedTimeLeftLabel = new JLabel();
        estimatedTimeLeftStatusLabel = new JLabel();
        filenameLabel = new JLabel();
        filenameStatusLabel = new JLabel();

        //======== this ========
        setOpaque(false);
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 250, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 0.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

        //======== scrollPane1 ========
        {

            //---- processingQueueStatsTextArea ----
            processingQueueStatsTextArea.setFont(UIManager.getFont("TextArea.font"));
            processingQueueStatsTextArea.setEditable(false);
            scrollPane1.setViewportView(processingQueueStatsTextArea);
        }
        add(scrollPane1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

        //======== panel1 ========
        {
            panel1.setOpaque(false);
            panel1.setBorder(new EmptyBorder(0, 5, 0, 0));
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};

            //---- imagesInQueueLabel ----
            imagesInQueueLabel.setFont(UIManager.getFont("Label.font"));
            imagesInQueueLabel
                .setText(Localizer.localize("Server", "ServerFrameImagesInQueueLabel"));
            panel1.add(imagesInQueueLabel,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

            //---- imagesInQueueStatusLabel ----
            imagesInQueueStatusLabel.setFont(UIManager.getFont("Label.font"));
            panel1.add(imagesInQueueStatusLabel,
                new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

            //---- estimatedTimeLeftLabel ----
            estimatedTimeLeftLabel.setFont(UIManager.getFont("Label.font"));
            estimatedTimeLeftLabel
                .setText(Localizer.localize("Server", "ServerFrameEstimatedTimeLeftLabel"));
            panel1.add(estimatedTimeLeftLabel,
                new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

            //---- estimatedTimeLeftStatusLabel ----
            estimatedTimeLeftStatusLabel.setFont(UIManager.getFont("Label.font"));
            panel1.add(estimatedTimeLeftStatusLabel,
                new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));

            //---- filenameLabel ----
            filenameLabel.setFont(UIManager.getFont("Label.font"));
            filenameLabel.setText(Localizer.localize("Server", "ServerFrameFilenameLabel"));
            panel1.add(filenameLabel,
                new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

            //---- filenameStatusLabel ----
            filenameStatusLabel.setFont(UIManager.getFont("Label.font"));
            panel1.add(filenameStatusLabel,
                new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 5, 5), 0, 0));
        }
        add(panel1, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JScrollPane scrollPane1;
    private JTextArea processingQueueStatsTextArea;
    private JPanel panel1;
    private JLabel imagesInQueueLabel;
    private JLabel imagesInQueueStatusLabel;
    private JLabel estimatedTimeLeftLabel;
    private JLabel estimatedTimeLeftStatusLabel;
    private JLabel filenameLabel;
    private JLabel filenameStatusLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public void setLastProcessTime(double lastProcessTime) {
        this.lastProcessTime = lastProcessTime;
        this.lastProcessTimeSetAt = System.currentTimeMillis();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

}


package com.ebstrada.formreturn.manager.log4j;

import java.awt.Color;
import java.awt.Component;
import java.awt.TextComponent;
import java.util.Hashtable;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

public class ComponentAppender extends AppenderSkeleton {

    protected Component comp;

    protected int entries;

    protected int maxEntries;

    private Hashtable<Level, ImageIcon> LevelIcon;

    private Hashtable<Level, MutableAttributeSet> attributes;

    public static Appender getAppender(String appenderName, String categoryName) {
        Appender result = null;
        Logger testcat;
        if (categoryName != null) {
            testcat = LogManager.exists(categoryName);
            if (testcat != null) {
                result = testcat.getAppender(appenderName);
            }
        }
        if (result == null) {
            testcat = Logger.getRootLogger();
            result = testcat.getAppender(appenderName);
        }
        return result;
    }

    public static Appender getAppender(String appenderName) {
        return ComponentAppender.getAppender(appenderName, null);
    }

    public ComponentAppender() {
        this(null);
    }

    public ComponentAppender(Component comp) {
        this(comp, 1);
    }

    public ComponentAppender(Component comp, int maxEntries) {
        MutableAttributeSet attSet;

        entries = 0;
        this.maxEntries = maxEntries;
        Level[] prios = new Level[] {Level.FATAL, Level.ERROR, Level.WARN, Level.INFO, Level.DEBUG};
        LevelIcon = new Hashtable<Level, ImageIcon>();
        attributes = new Hashtable<Level, MutableAttributeSet>();

        // default initialize colors and icons
        for (int i = 0; i < prios.length; i++) {
            attSet = new SimpleAttributeSet();
            StyleConstants.setBackground(attSet, Color.white);
            StyleConstants.setForeground(attSet, Color.black);
            attributes.put(prios[i], attSet);
        }

        // Get current classloader
        ClassLoader cl = this.getClass().getClassLoader();
        // Create icons
        try {
            attSet = attributes.get(Level.FATAL);
            StyleConstants.setForeground(attSet, Color.red);
            attributes.put(Level.FATAL, attSet);
            LevelIcon.put(Level.FATAL, new ImageIcon(
                cl.getResource("com/ebstrada/formreturn/manager/log4j/icons/fatal.png")));
        } catch (Exception ex2) {
        }
        try {
            attSet = attributes.get(Level.ERROR);
            StyleConstants.setForeground(attSet, Color.red);
            attributes.put(Level.ERROR, attSet);
            LevelIcon.put(Level.ERROR, new ImageIcon(
                cl.getResource("com/ebstrada/formreturn/manager/log4j/icons/error.png")));
        } catch (Exception ex2) {
        }
        try {
            LevelIcon.put(Level.WARN, new ImageIcon(
                cl.getResource("com/ebstrada/formreturn/manager/log4j/icons/warn.png")));
        } catch (Exception ex2) {
        }
        try {
            attSet = attributes.get(Level.INFO);
            StyleConstants.setForeground(attSet, Color.blue);
            attributes.put(Level.INFO, attSet);
            LevelIcon.put(Level.INFO, new ImageIcon(
                cl.getResource("com/ebstrada/formreturn/manager/log4j/icons/info.png")));
        } catch (Exception ex2) {
        }
        try {
            attSet = attributes.get(Level.DEBUG);
            StyleConstants.setForeground(attSet, Color.gray);
            attributes.put(Level.DEBUG, attSet);
            LevelIcon.put(Level.DEBUG, new ImageIcon(
                cl.getResource("com/ebstrada/formreturn/manager/log4j/icons/debug.png")));
        } catch (Exception ex2) {
        }
        setComponent(comp);
    }

    protected void initializeNewComponent(Component comp) {
        if (comp instanceof JComboBox) {
            ((JComboBox) comp).setModel(new DefaultComboBoxModel());
        }
        if (comp instanceof JList) {
            ((JList) comp).setModel(new LoggingEventModel());
            ((JList) comp).setCellRenderer(new PriorityListCellRenderer(true, true, this));
        }
        if (comp instanceof JTable) {
            ((JTable) comp).setModel(new LoggingEventModel());
        }
    }

    public Component getComponent() {
        return comp;
    }

    public void setComponent(Component comp) {
        if ((comp instanceof JTextArea) || (comp instanceof JTextPane)) {
            layout = new PatternLayout("%n%m");
        } else {
            layout = new PatternLayout("%m");
        }
        this.comp = comp;
        initializeNewComponent(comp);
    }

    public int getMaxEntries() {
        return maxEntries;
    }

    public void setMaxEntries(int value) {
        if (entries > value) {
            // the new maxEntry value is smaller than the actual entry
            // counter
            // we have to delete the oldest entries
            int toomuch = entries - value;

            if (comp instanceof JTextPane) {
                JTextPane textPane = (JTextPane) comp;
                try {
                    StyledDocument doc = textPane.getStyledDocument();
                    if (entries == maxEntries) {
                        Element element = doc.getParagraphElement(0);
                        int startOfs = element.getStartOffset();
                        element = doc.getParagraphElement(toomuch - 1);
                        int endOfs = element.getEndOffset();
                        doc.remove(startOfs, endOfs - startOfs);
                    }
                } catch (Exception x) {
                    x.printStackTrace();
                }
            } else if (comp instanceof JTextArea) {
                JTextArea textArea = (JTextArea) comp;
                try {
                    Document doc = textArea.getDocument();
                    int endOfs = textArea.getLineEndOffset(toomuch - 1);
                    int docLen = doc.getLength();
                    textArea.getText();
                    if (docLen < endOfs) {
                        doc.remove(0, docLen);
                    } else {
                        doc.remove(0, endOfs);
                    }
                    textArea.setCaretPosition(doc.getLength());
                } catch (Exception x) {
                }
            } else if (comp instanceof JComboBox) {
                DefaultComboBoxModel model = (DefaultComboBoxModel) ((JComboBox) comp).getModel();
                for (int i = 0; i < toomuch; i++) {
                    model.removeElementAt(0);
                }
            } else if (comp instanceof JList) {
                LoggingEventModel model = (LoggingEventModel) ((JList) comp).getModel();
                for (int i = 0; i < toomuch; i++) {
                    model.removeElementAt(0);
                }
            } else if (comp instanceof JTable) {
                LoggingEventModel model = (LoggingEventModel) ((JTable) comp).getModel();
                for (int i = 0; i < toomuch; i++) {
                    model.removeElementAt(0);
                }
            } else if (comp instanceof java.awt.List) {
                for (int i = 0; i < toomuch; i++) {
                    ((java.awt.List) comp).remove(0);
                }
            }

            entries = value;
        }
        maxEntries = value;
    }

    public ImageIcon getIcon(Level prio) {
        return LevelIcon.get(prio);
    }

    public void setIcon(Level prio, ImageIcon icon) {
        LevelIcon.put(prio, icon);
    }

    public Color getBackground(Level prio) {
        MutableAttributeSet attSet = attributes.get(prio);
        if (attSet == null) {
            return Color.WHITE;
        }
        return StyleConstants.getBackground(attSet);
    }

    public void setBackground(Level prio, Color background) {
        MutableAttributeSet attSet = attributes.get(prio);
        StyleConstants.setBackground(attSet, background);
        attributes.put(prio, attSet);
    }

    public Color getForeground(Level prio) {
        MutableAttributeSet attSet = attributes.get(prio);
        if (attSet == null) {
            return Color.BLACK;
        }
        return StyleConstants.getForeground(attSet);
    }

    public void setForeground(Level prio, Color foreground) {
        MutableAttributeSet attSet = attributes.get(prio);
        StyleConstants.setForeground(attSet, foreground);
        attributes.put(prio, attSet);
    }

    public void setForegroundFATAL(String foreground) {
        setForeground(Level.FATAL, ComponentAppender.parseRGBColor(foreground));
    }

    public void setForegroundERROR(String foreground) {
        setForeground(Level.ERROR, ComponentAppender.parseRGBColor(foreground));
    }

    public void setForegroundWARN(String foreground) {
        setForeground(Level.WARN, ComponentAppender.parseRGBColor(foreground));
    }

    public void setForegroundINFO(String foreground) {
        setForeground(Level.INFO, ComponentAppender.parseRGBColor(foreground));
    }

    public void setForegroundDEBUG(String foreground) {
        setForeground(Level.DEBUG, ComponentAppender.parseRGBColor(foreground));
    }

    public void setBackgroundFATAL(String background) {
        setBackground(Level.FATAL, ComponentAppender.parseRGBColor(background));
    }

    public void setBackgroundERROR(String background) {
        setBackground(Level.ERROR, ComponentAppender.parseRGBColor(background));
    }

    public void setBackgroundWARN(String background) {
        setBackground(Level.WARN, ComponentAppender.parseRGBColor(background));
    }

    public void setBackgroundINFO(String background) {
        setBackground(Level.INFO, ComponentAppender.parseRGBColor(background));
    }

    public void setBackgroundDEBUG(String background) {
        setBackground(Level.DEBUG, ComponentAppender.parseRGBColor(background));
    }

    static public Color parseRGBColor(String rgb) {
        java.util.StringTokenizer st = new java.util.StringTokenizer(rgb, ",");
        int val[] = {255, 255, 255, 255};
        int i = 0;
        while ((st.hasMoreTokens()) && (i < 4)) {
            val[i] = Integer.parseInt(st.nextToken());
            i++;
        }
        return new Color(val[0], val[1], val[2], val[3]);
    }

    public boolean requiresLayout() {
        return true;
    }

    @Override public void append(LoggingEvent event) {
        String text = layout.format(event);
        // swing components
        if (comp instanceof JLabel) {
            ((JLabel) comp).setText(text);
        } else if (comp instanceof JTextPane) {
            JTextPane textPane = (JTextPane) comp;
            try {
                StyledDocument doc = textPane.getStyledDocument();
                if (entries == maxEntries) {
                    // Delete 1 line
                    Element element = doc.getParagraphElement(0);
                    int startOfs = element.getStartOffset();
                    int endOfs = element.getEndOffset();
                    doc.remove(startOfs, endOfs - startOfs);
                    entries -= 1;
                }
                // insert colored message text
                MutableAttributeSet attSet = attributes.get(event.getLevel());
                doc.insertString(doc.getLength(), text, attSet);
                // delete the linefeed before the first row
                if (entries == 0) {
                    doc.remove(1, 1);
                }
                textPane.setCaretPosition(doc.getLength());
            } catch (Exception x) {
                x.printStackTrace();
            }
            ;
            entries += 1;
        } else if (comp instanceof JTextArea) {
            JTextArea textArea = (JTextArea) comp;
            try {
                Document doc = textArea.getDocument();
                if (entries == maxEntries) {
                    // Delete 1 line
                    int endOfs = textArea.getLineEndOffset(0);
                    int docLen = doc.getLength();
                    textArea.getText();
                    if (docLen < endOfs) {
                        doc.remove(0, docLen);
                    } else {
                        doc.remove(0, endOfs);
                    }
                    entries -= 1;
                }
                textArea.append(text);
                if (entries == 0) {
                    doc.remove(1, 1);
                }
                textArea.setCaretPosition(doc.getLength());
            } catch (Exception x) {
            }
            ;
            entries += 1;
        } else if (comp instanceof JTextComponent) {
            ((JTextComponent) comp).setText(text);
        } else if (comp instanceof JComboBox) {
            DefaultComboBoxModel model = (DefaultComboBoxModel) ((JComboBox) comp).getModel();
            if (entries == maxEntries) {
                model.removeElementAt(0);
                entries -= 1;
            }
            model.addElement(text);
            entries += 1;
            ((JComboBox) comp).setSelectedIndex(entries - 1);
        } else if (comp instanceof JList) {
            LoggingEventModel model = (LoggingEventModel) ((JList) comp).getModel();
            if (entries == maxEntries) {
                model.removeElementAt(0);
                entries -= 1;
            }
            model.addElement(event);
            entries += 1;
        } else if (comp instanceof JTable) {
            LoggingEventModel model = (LoggingEventModel) ((JTable) comp).getModel();
            if (entries == maxEntries) {
                model.removeElementAt(0);
                entries -= 1;
            }
            model.addElement(event);
            entries += 1;
        }
        // awt components
        else if (comp instanceof java.awt.Label) {
            ((java.awt.Label) comp).setText(text);
        } else if (comp instanceof java.awt.List) {
            if (entries == maxEntries) {
                ((java.awt.List) comp).remove(0);
                entries -= 1;
            }
            ((java.awt.List) comp).add(text);
            entries += 1;
        } else if (comp instanceof TextComponent) {
            ((TextComponent) comp).setText(text);
        }
    }

    public void reset() {
        // swing components
        if (comp instanceof JLabel) {
            ((JLabel) comp).setText("");
        } else if (comp instanceof JTextComponent) { // includes JTextArea
            // and JTextPane!
            ((JTextComponent) comp).setText("");
        } else if (comp instanceof JComboBox) {
            DefaultComboBoxModel model = (DefaultComboBoxModel) ((JComboBox) comp).getModel();
            model.removeAllElements();
        } else if (comp instanceof JList) {
            LoggingEventModel model = (LoggingEventModel) ((JList) comp).getModel();
            model.removeAllElements();
        } else if (comp instanceof JTable) {
            LoggingEventModel model = (LoggingEventModel) ((JTable) comp).getModel();
            int i;
            for (i = 0; i < model.getRowCount(); i++) {
                model.removeElementAt(i);
            }
        }
        // awt components
        else if (comp instanceof java.awt.Label) {
            ((java.awt.Label) comp).setText("");
        } else if (comp instanceof java.awt.List) {
            ((java.awt.List) comp).removeAll();
        } else if (comp instanceof java.awt.TextComponent) {
            ((java.awt.TextComponent) comp).setText("");
        }
        entries = 0;
    }

    public void close() {
        reset();
    }
}

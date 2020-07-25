package com.ebstrada.formreturn.manager.ui.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.ChangeListener;

public interface DesktopTabbedPane extends MouseListener, MouseMotionListener {

    public abstract void paintComponent(Graphics g);

    public abstract JPanel getSelectedFrame();

    public abstract Component add(Component comp, int index);

    public abstract void add(Component comp, Object constraint);

    public abstract void setSelectedComponent(Component comp);

    public abstract JPanel[] getAllFrames();

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void setOpaque(boolean b);

    public abstract void remove(Component comp);

    public abstract Component getSelectedComponent();

    public abstract void remove(int overTabIndex);

    public abstract Component getComponentAt(int index);

    public abstract int getTabCount();

    public abstract void setBackground(Color color);

    public abstract int getSelectedIndex();

    public abstract void setTitleAt(int index, String title);

    public abstract String getTitleAt(int index);

    public abstract Component add(Component comp);

    public abstract void setBorder(Border border);

    public abstract void addMouseListener(MouseListener l);

    public abstract void addMouseMotionListener(MouseMotionListener l);

    public abstract void addChangeListener(ChangeListener changeListener);

    public abstract void addCloseListener(CloseListener closeListener);

    public abstract void removeCloseListener(CloseListener l);

    public abstract int getIndexOfComponent(JComponent component);

    public abstract Cursor getCursor();

    public abstract void setCursor(Cursor cursor);

}

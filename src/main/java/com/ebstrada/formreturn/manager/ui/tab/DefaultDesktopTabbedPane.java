package com.ebstrada.formreturn.manager.ui.tab;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.EventListener;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

@SuppressWarnings("serial") public class DefaultDesktopTabbedPane extends JTabbedPane
    implements DesktopTabbedPane {

    private DesktopTabbedPaneBackground desktopTabbedPaneBackground;

    public DefaultDesktopTabbedPane(String country, String language) {
        desktopTabbedPaneBackground = new DesktopTabbedPaneBackground(this, country, language);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (getTabCount() == 0) {
            desktopTabbedPaneBackground.paintComponent(g, getVisibleRect());
        }
    }

    public JPanel getSelectedFrame() {
        return (JPanel) this.getSelectedComponent();
    }

    public int getIndexOfComponent(JComponent component) {
        return this.indexOfComponent(component);
    }

    public JPanel[] getAllFrames() {

        JPanel[] jparray = new JPanel[this.getTabCount()];

        for (int i = 0; i < this.getTabCount(); i++) {
            jparray[i] = (JPanel) this.getComponentAt(i);
        }

        return jparray;

    }

    public void add(Component comp, Object constraint) {
        super.add(comp, constraint);
        initTabComponent(this.indexOfComponent(comp));
    }

    public Component add(Component comp, int index) {
        Component retComp = super.add(comp, index);
        initTabComponent(index);
        return retComp;
    }

    private void initTabComponent(int i) {
        setTabComponentAt(i, new DefaultDesktopTabbedPaneComponent(this));
    }

    public synchronized void addCloseListener(CloseListener l) {
        listenerList.add(CloseListener.class, l);
    }

    public synchronized void removeCloseListener(CloseListener l) {
        listenerList.remove(CloseListener.class, l);
    }

    public void fireCloseTabEvent(ActionEvent e, int overTabIndex) {
        EventListener closeListeners[] = getListeners(CloseListener.class);
        for (int i = 0; i < closeListeners.length; i++) {
            CloseListener cl = (CloseListener) closeListeners[i];
            cl.closeOperation(cl, e, overTabIndex);
        }
    }

    public void setTitleAt(int index, String title) {
        super.setTitleAt(index, title);
        DefaultDesktopTabbedPaneComponent component =
            (DefaultDesktopTabbedPaneComponent) getTabComponentAt(index);
        component.revalidate();
    }

    @Override public void mouseClicked(MouseEvent e) {
        desktopTabbedPaneBackground.mouseClicked(e);
    }

    @Override public void mousePressed(MouseEvent e) {
    }

    @Override public void mouseReleased(MouseEvent e) {
    }

    @Override public void mouseEntered(MouseEvent e) {
    }

    @Override public void mouseExited(MouseEvent e) {
    }

    @Override public void mouseDragged(MouseEvent e) {
    }

    @Override public void mouseMoved(MouseEvent e) {
        desktopTabbedPaneBackground.mouseMoved(e, this);
    }

}

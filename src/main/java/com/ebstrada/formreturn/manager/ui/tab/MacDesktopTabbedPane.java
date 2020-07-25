package com.ebstrada.formreturn.manager.ui.tab;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;

@SuppressWarnings("serial") public final class MacDesktopTabbedPane extends CloseTabbedPane
    implements DesktopTabbedPane {

    private DesktopTabbedPaneBackground desktopTabbedPaneBackground;

    public MacDesktopTabbedPane(String country, String language) {
        super();
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

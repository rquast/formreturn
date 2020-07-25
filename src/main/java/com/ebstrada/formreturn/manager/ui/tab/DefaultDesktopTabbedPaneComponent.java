package com.ebstrada.formreturn.manager.ui.tab;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;

public class DefaultDesktopTabbedPaneComponent extends JPanel {

    private static final long serialVersionUID = 1L;

    private final DefaultDesktopTabbedPane pane;

    // 14x14
    private Image closeHover = (new ImageIcon(
        getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/close_hover.png")))
        .getImage();

    private Image close = (new ImageIcon(
        getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/close.png"))).getImage();

    private Image closePressed = (new ImageIcon(
        getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/close_pressed.png")))
        .getImage();

    public DefaultDesktopTabbedPaneComponent(final DefaultDesktopTabbedPane pane) {

        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        this.pane = pane;
        setOpaque(false);

        JLabel label = new JLabel() {
            private static final long serialVersionUID = 1L;

            public String getText() {
                int i = pane.indexOfTabComponent(DefaultDesktopTabbedPaneComponent.this);
                if (i != -1) {
                    return pane.getTitleAt(i);
                }
                return null;
            }
        };

        Font tabbedPaneFont = UIManager.getFont("TabbedPane.font");
        label.setFont(tabbedPaneFont);

        add(label);

        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));

        JButton button = new TabButton();
        add(button);

        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }

    private class TabButton extends JButton implements ActionListener {

        private static final long serialVersionUID = 1L;

        public TabButton() {
            int size = 14;
            setPreferredSize(new Dimension(size, size));
            setUI(new BasicButtonUI());
            setContentAreaFilled(false);
            setFocusable(false);
            setBorder(null);
            setBorderPainted(false);
            addMouseListener(buttonMouseListener);
            setRolloverEnabled(true);
            addActionListener(this);
        }

        public void actionPerformed(ActionEvent e) {
            int i = pane.indexOfTabComponent(DefaultDesktopTabbedPaneComponent.this);
            if (i != -1) {
                pane.fireCloseTabEvent(e, i);
            }
        }

        public void updateUI() {
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();

            if (getModel().isRollover() && !(getModel().isPressed())) {
                g2.drawImage(closeHover, 0, 0, 14, 14, null);
            } else if (getModel().isPressed()) {
                g2.drawImage(closePressed, 0, 0, 14, 14, null);
            } else {
                g2.drawImage(close, 0, 0, 14, 14, null);
            }
            g2.dispose();
        }
    }


    private final static MouseListener buttonMouseListener = new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(true);
            }
        }

        public void mouseExited(MouseEvent e) {
            Component component = e.getComponent();
            if (component instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) component;
                button.setBorderPainted(false);
            }
        }
    };

}

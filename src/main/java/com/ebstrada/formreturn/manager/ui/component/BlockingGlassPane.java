package com.ebstrada.formreturn.manager.ui.component;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.KeyEventDispatcher;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.FocusManager;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class BlockingGlassPane extends JPanel {

    private static final long serialVersionUID = 1L;
    private int blockCount = 0;
    private BlockMouse blockMouse = new BlockMouse();
    private BlockKeys blockKeys = new BlockKeys();

    /**
     * Constructor.
     */
    public BlockingGlassPane() {
        setOpaque(false);
        addMouseListener(blockMouse);
    }

    /**
     * Start or end blocking.
     *
     * @param block should blocking be started or ended
     */
    public void block(boolean block) {
        if (block) {
            if (blockCount == 0) {
                setVisible(true);

                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                FocusManager.getCurrentManager().addKeyEventDispatcher(blockKeys);
            }
            blockCount++;
        } else {
            blockCount--;
            if (blockCount == 0) {
                FocusManager.getCurrentManager().removeKeyEventDispatcher(blockKeys);

                setCursor(Cursor.getDefaultCursor());

                setVisible(false);
            }
        }
    }

    /**
     * Test if this glasspane is blocked.
     *
     * @return <code>true</code> if currently blocked
     */
    public boolean isBlocked() {
        return blockCount > 0;
    }

    /**
     * The key dispatcher to block the keys.
     */
    private class BlockKeys implements KeyEventDispatcher {
        public boolean dispatchKeyEvent(KeyEvent ev) {
            Component source = ev.getComponent();
            if (source != null && SwingUtilities.isDescendingFrom(source, getParent())) {
                Toolkit.getDefaultToolkit().beep();
                ev.consume();
                return true;
            }
            return false;
        }
    }


    /**
     * The mouse listener used to block the mouse.
     */
    private class BlockMouse extends MouseAdapter implements MouseListener {
        public void mouseClicked(MouseEvent ev) {
            Toolkit.getDefaultToolkit().beep();
        }
    }
}

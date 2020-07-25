package com.ebstrada.formreturn.manager.gef.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicToggleButtonUI;

import com.ebstrada.formreturn.manager.gef.base.Editor;
import com.ebstrada.formreturn.manager.gef.base.Globals;
import com.ebstrada.formreturn.manager.gef.base.SetModeAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.gef.util.ResourceLoader;
import com.ebstrada.formreturn.manager.ui.Main;

public abstract class Palette extends JPanel implements MouseListener {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private static final long serialVersionUID = 1L;

    protected Vector<JToggleButton> _lockable = new Vector<JToggleButton>();

    protected Vector<JToggleButton> _modeButtons = new Vector<JToggleButton>();

    abstract public void defineButtons();

    private int orientation = HORIZONTAL;

    public Palette(int orientation) {

        this.orientation = orientation;

        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {1.0, 1.0};

    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void addSeparator() {
        JSeparator separator = new JSeparator();
        if (this.orientation == VERTICAL) {
            separator.setOrientation(JSeparator.VERTICAL);
            separator.setMaximumSize(new Dimension(12, Integer.MAX_VALUE));
            separator.setBorder(new EmptyBorder(0, 6, 0, 6));
        } else {
            separator.setOrientation(JSeparator.HORIZONTAL);
            separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 12));
            separator.setBorder(new EmptyBorder(6, 0, 6, 0));
        }

        // add(separator);
    }

    @Override public Component add(Component comp) {
        if (comp instanceof JToggleButton) {
            JToggleButton button = (JToggleButton) comp;
            Action action = button.getAction();
            if (action instanceof SetModeAction) {
                _modeButtons.addElement(button);
            }
            if (action instanceof SetModeAction) {
                _lockable.addElement(button);
            }
            button.addMouseListener(this);
        }
        GridBagConstraints constraints = new GridBagConstraints();
        if (this.orientation == VERTICAL) {
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.fill = GridBagConstraints.BOTH;
            constraints.anchor = GridBagConstraints.SOUTH;
        } else {
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.WEST;
        }
        add(comp, constraints);
        return comp;
    }

    public JToggleButton addToggle(Action a) {
        String name = (String) a.getValue(Action.NAME);
        Icon icon = (Icon) a.getValue(Action.SMALL_ICON);
        return addToggle(a, name, icon);
    }

    public JToggleButton addToggle(Action a, String name, Icon icon) {
        icon = ResourceLoader.lookupIconResource(name, name);
        JToggleButton b = new JToggleButton(icon);
        b.setName(null);

        if (name.equals("RRect")) {
            name = Localizer.localize("GefBase", "RRect");
        }
        b.setUI(new BasicToggleButtonUI());
        b.setToolTipText(name + " ");
        b.setEnabled(a.isEnabled());
        b.setText(name);
        b.setIcon(icon);

        if (!Main.MAC_OS_X) {
            b.setFont(UIManager.getFont("Button.font"));
        } else {
            b.setFont(UIManager.getFont("Button.font").deriveFont(10.0f));
        }
        if (this.orientation == VERTICAL) {
            b.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(0, 8, 0, 0)));
        } else {
            b.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.GRAY),
                BorderFactory.createEmptyBorder(4, 4, 4, 4)));
        }
        b.setBackground(null);
        if (!Main.MAC_OS_X) {
            b.setMargin(new Insets(0, 0, 0, 0));
        }
        b.setIconTextGap(8);
        if (this.orientation == VERTICAL) {
            b.setHorizontalAlignment(SwingConstants.LEFT);
            b.setHorizontalTextPosition(SwingConstants.RIGHT);
        } else {
            b.setHorizontalTextPosition(SwingConstants.CENTER);
            b.setVerticalTextPosition(SwingConstants.BOTTOM);
        }
        b.addActionListener(a);
        if (a instanceof SetModeAction) {
            _modeButtons.addElement(b);
        }
        if (a instanceof SetModeAction) {
            _lockable.addElement(b);
        }
        b.addMouseListener(this);
        add(b);

        return b;
    }

    // //////////////////////////////////////////////////////////////
    // MouseListener implementation

    public void mouseEntered(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
    }

    public void mousePressed(MouseEvent me) {
    }

    public void mouseReleased(MouseEvent me) {

        Object src = me.getSource();
        if (isModeButton(src)) {
            unpressAllButtonsExcept(src);
            Editor ce = Globals.curEditor();
            if (ce != null) {
                ce.finishMode();
            }
            Globals.setSticky(false);
        }
        if (me.getClickCount() >= 2) {
            if (!(src instanceof JToggleButton)) {
                return;
            }
            JToggleButton b = (JToggleButton) src;
            if (canLock(b)) {
                b.setSelected(true);
                b.getModel().setPressed(true);
                b.setBorderPainted(true);
                b.setContentAreaFilled(true);
                b.setOpaque(true);
                Globals.setSticky(true);
            }
        } else {
            if (src instanceof JToggleButton && isModeButton(src)) {
                JToggleButton b = (JToggleButton) src;
                b.setSelected(true);
                b.getModel().setPressed(true);
                b.setBorderPainted(true);
                b.setContentAreaFilled(true);
                b.setOpaque(true);
            }
        }

    }

    public void mouseClicked(MouseEvent me) {

    }

    protected boolean canLock(Object b) {
        return _lockable.contains(b);
    }

    protected boolean isModeButton(Object b) {
        return _modeButtons.contains(b);
    }

    protected void unpressAllButtonsExcept(Object src) {
        int size = getComponentCount();
        for (int i = 0; i < size; i++) {
            Component c = getComponent(i);
            if (!(c instanceof JToggleButton)) {
                continue;
            }
            if (c == src) {
                continue;
            }
            ((JToggleButton) c).setSelected(false);
            ((JToggleButton) c).getModel().setPressed(false);
            ((JToggleButton) c).setBorderPainted(false);
            ((JToggleButton) c).setContentAreaFilled(false);
            ((JToggleButton) c).setOpaque(false);
        }
    }

    public void unpressAllButtons() {

        if (Globals.getSticky() == true) {
            return;
        }

        int size = getComponentCount();
        for (int i = 0; i < size; i++) {
            Component c = getComponent(i);
            if (!(c instanceof JToggleButton)) {
                continue;
            }
            ((JToggleButton) c).setSelected(false);
            ((JToggleButton) c).getModel().setPressed(false);
            ((JToggleButton) c).setBorderPainted(false);
            ((JToggleButton) c).setContentAreaFilled(false);
            ((JToggleButton) c).setOpaque(false);

        }
        // press the first button (usually ModeSelect)
        for (int i = 0; i < size; i++) {
            Component c = getComponent(i);
            if (!(c instanceof JToggleButton)) {
                continue;
            }
            JToggleButton select = (JToggleButton) c;
            select.setSelected(true);
            select.getModel().setPressed(true);
            select.setBorderPainted(true);
            select.setContentAreaFilled(true);
            select.setOpaque(true);

            return;
        }
    }

}

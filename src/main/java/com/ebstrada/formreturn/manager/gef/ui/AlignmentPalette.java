package com.ebstrada.formreturn.manager.gef.ui;

import java.awt.*;
import javax.swing.*;

import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import com.ebstrada.formreturn.manager.gef.base.AlignAction;
import com.ebstrada.formreturn.manager.gef.base.DistributeAction;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.gef.util.ResourceLoader;

public class AlignmentPalette extends JPanel {

    private static final long serialVersionUID = 1L;

    public AlignmentPalette() {
        initComponents();
        defineButtons();
    }

    public JButton addBase(Action a) {
        String name = (String) a.getValue(Action.NAME);
        Icon icon = (Icon) a.getValue(Action.SMALL_ICON);
        if (icon == null) {
            return add(a, Localizer.localize("GefBase", name), name);
        } else {
            return add(a, Localizer.localize("GefBase", name), icon);
        }
    }

    public JButton add(Action a, String name, String iconResourceStr) {
        Icon icon = ResourceLoader.lookupIconResource(iconResourceStr, name);
        return add(a, name, icon);
    }

    public JButton add(Action a, String name, Icon icon) {
        JButton b = new JButton();
        b.setAction(a);
        b.setName(null);
        b.setIcon(icon);
        b.setText(null);
        b.setToolTipText(name + " ");
        b.setContentAreaFilled(false);
        b.setOpaque(false);
        b.setBorderPainted(false);
        b.setMargin(new Insets(0, 6, 0, 6));
        b.setBorder(new EmptyBorder(0, 6, 0, 6));
        add(b);
        return b;
    }

    public void addSeparator() {
        JSeparator separator = new JSeparator();
        separator.setOrientation(JSeparator.VERTICAL);
        separator.setMaximumSize(new Dimension(12, Integer.MAX_VALUE));
        separator.setBorder(new EmptyBorder(0, 6, 0, 6));
        add(separator);
    }

    public void defineButtons() {

        addSeparator();
        addBase(new AlignAction(AlignAction.ALIGN_TOPS));
        addBase(new AlignAction(AlignAction.ALIGN_BOTTOMS));
        addBase(new AlignAction(AlignAction.ALIGN_LEFTS));
        addBase(new AlignAction(AlignAction.ALIGN_RIGHTS));
        addSeparator();
        addBase(new AlignAction(AlignAction.ALIGN_H_CENTERS));
        addBase(new AlignAction(AlignAction.ALIGN_V_CENTERS));
        addSeparator();
        addBase(new DistributeAction(DistributeAction.H_SPACING));
        addBase(new DistributeAction(DistributeAction.V_SPACING));
        addSeparator();

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents

        //======== this ========
        setOpaque(false);
        setBorder(null);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

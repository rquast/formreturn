package com.ebstrada.formreturn.manager.gef.ui.laf;

import javax.swing.UIManager;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.macosx.MacOSXLookAndFeelAddons;


public class LinuxLAF implements ApplicationLAF {

    @Override public void setLAF() {
        System.setProperty("awt.useSystemAAFontSettings", "on");

        try {
            UIManager.getLookAndFeelDefaults()
                    .put(JXTaskPane.uiClassID, "org.jdesktop.swingx.plaf.misc.GlossyTaskPaneUI");

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            LookAndFeelAddons.setAddon(MacOSXLookAndFeelAddons.class);
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        }
    }

}

package com.ebstrada.formreturn.manager.ui.component;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Locale;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JToolTip;
import javax.swing.ToolTipManager;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.Misc;

public class JHelpLabel extends JLabel {

    private String helpGUID = "";

    private static final long serialVersionUID = 1L;

    public JHelpLabel() {
        super();
        ToolTipManager.sharedInstance().setInitialDelay(0);
        ToolTipManager.sharedInstance().setDismissDelay(15000);
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                openGUID();
            }
        });
        if (this.getIcon() != null) {
            setToolTipText(Localizer.localize("UI", "ClickHereForHelpToolTip"));
        } else {
            setToolTipText(null);
        }
    }

    public JToolTip createToolTip() {
        if (getToolTipText() != null) {
            return new JScrollableToolTip(300, 100);
        } else {
            return null;
        }
    }

    protected void openGUID() {
        if (helpGUID.length() > 0) {
            String url = "file://" + Misc.getHelpDirectory() + "/?topic=" + helpGUID;
            Misc.openURL(url);
        }
    }

    public void setHelpGUID(String helpGUID) {
        this.helpGUID = helpGUID;
        setToolTipText(Localizer.localize("HelpLabel", helpGUID));
    }

}

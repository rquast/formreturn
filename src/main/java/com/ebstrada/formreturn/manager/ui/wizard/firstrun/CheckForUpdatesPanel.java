package com.ebstrada.formreturn.manager.ui.wizard.firstrun;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.frame.WizardDialog;
import com.ebstrada.formreturn.manager.ui.wizard.IWizardPanel;

public class CheckForUpdatesPanel extends JPanel implements IWizardPanel {

    private static final long serialVersionUID = 1L;
    private HashMap<String, Object> settings;

    public static final String CHECK_FOR_UDPATES_KEY = "check_updates";

    public CheckForUpdatesPanel(HashMap<String, Object> settings) {
        this.settings = settings;
        initComponents();
        restoreSettings();
    }

    private void restoreSettings() {
        if (settings.containsKey(CHECK_FOR_UDPATES_KEY)) {
            if (((Boolean) settings.get(CHECK_FOR_UDPATES_KEY)).equals(true)) {
                this.checkForUpdatesRadioButton.setSelected(true);
            } else {
                this.doNotCheckForUpdatesRadioButton.setSelected(true);
            }
        }
    }

    private void updateSelection() {
        if (this.checkForUpdatesRadioButton.isSelected()) {
            settings.put(CHECK_FOR_UDPATES_KEY, new Boolean(true));
        } else {
            settings.put(CHECK_FOR_UDPATES_KEY, new Boolean(false));
        }
    }

    private void checkForUpdatesRadioButtonActionPerformed(ActionEvent e) {
        updateSelection();
    }

    private void doNotCheckForUpdatesRadioButtonActionPerformed(ActionEvent e) {
        updateSelection();
    }

    @Override public IWizardPanel next() throws Exception {
        throw new Exception();
    }

    @Override public IWizardPanel back() throws Exception {
        updateSelection();
        return new SelectPageSizePanel(settings);
    }

    @Override public void finish() throws Exception {
        updateSelection();
    }

    @Override public void cancel() throws Exception {
        throw new Exception();
    }

    @Override public ArrayList<Integer> getActiveButtons() {
        ArrayList<Integer> activeButtons = new ArrayList<Integer>();
        activeButtons.add(WizardDialog.BACK);
        activeButtons.add(WizardDialog.FINISH);
        return activeButtons;
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        headingLabel = new JLabel();
        contentLabel = new JLabel();
        radioButtonPanel = new JPanel();
        checkForUpdatesRadioButton = new JRadioButton();
        doNotCheckForUpdatesRadioButton = new JRadioButton();

        //======== this ========
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setOpaque(false);
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0, 0, 0, 0, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights = new double[] {0.0, 0.0, 1.0, 0.0, 1.0, 1.0E-4};

        //---- headingLabel ----
        headingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headingLabel.setText(Localizer.localize("UI", "SetupWizardUpdatesHeadingText"));
        add(headingLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //---- contentLabel ----
        contentLabel.setVerticalAlignment(SwingConstants.TOP);
        contentLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
        contentLabel.setFont(UIManager.getFont("Label.font"));
        contentLabel.setText(Localizer.localize("UI", "SetupWizardUpdatesContentText"));
        add(contentLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //======== radioButtonPanel ========
        {
            radioButtonPanel.setBorder(null);
            radioButtonPanel.setOpaque(false);
            radioButtonPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) radioButtonPanel.getLayout()).columnWidths = new int[] {0, 0, 0, 0};
            ((GridBagLayout) radioButtonPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout) radioButtonPanel.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) radioButtonPanel.getLayout()).rowWeights =
                new double[] {0.0, 0.0, 1.0E-4};

            //---- checkForUpdatesRadioButton ----
            checkForUpdatesRadioButton.setSelected(true);
            checkForUpdatesRadioButton.setFont(UIManager.getFont("RadioButton.font"));
            checkForUpdatesRadioButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    checkForUpdatesRadioButtonActionPerformed(e);
                }
            });
            checkForUpdatesRadioButton
                .setText(Localizer.localize("UI", "SetupWizardCheckForUpdatesText"));
            radioButtonPanel.add(checkForUpdatesRadioButton,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 5, 5), 0, 0));

            //---- doNotCheckForUpdatesRadioButton ----
            doNotCheckForUpdatesRadioButton.setFont(UIManager.getFont("RadioButton.font"));
            doNotCheckForUpdatesRadioButton.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    doNotCheckForUpdatesRadioButtonActionPerformed(e);
                }
            });
            doNotCheckForUpdatesRadioButton
                .setText(Localizer.localize("UI", "SetupWizardDoNotCheckForUpdatesText"));
            radioButtonPanel.add(doNotCheckForUpdatesRadioButton,
                new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 5), 0, 0));
        }
        add(radioButtonPanel,
            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //---- checkForUpdatesButtonGroup ----
        ButtonGroup checkForUpdatesButtonGroup = new ButtonGroup();
        checkForUpdatesButtonGroup.add(checkForUpdatesRadioButton);
        checkForUpdatesButtonGroup.add(doNotCheckForUpdatesRadioButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel headingLabel;
    private JLabel contentLabel;
    private JPanel radioButtonPanel;
    private JRadioButton checkForUpdatesRadioButton;
    private JRadioButton doNotCheckForUpdatesRadioButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

}

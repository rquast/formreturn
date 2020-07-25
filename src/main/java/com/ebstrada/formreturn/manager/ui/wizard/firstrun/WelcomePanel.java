package com.ebstrada.formreturn.manager.ui.wizard.firstrun;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.font.FontLocaleUtil;
import com.ebstrada.formreturn.manager.gef.font.FontLocalesImpl;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.frame.WizardDialog;
import com.ebstrada.formreturn.manager.ui.wizard.IWizardPanel;
import com.ebstrada.formreturn.manager.util.AvailableLanguages;
import com.ebstrada.formreturn.manager.util.preferences.PreferencesManager;
import com.ebstrada.formreturn.manager.util.preferences.persistence.ApplicationStatePreferences;

public class WelcomePanel extends JPanel implements IWizardPanel {

    private static final long serialVersionUID = 1L;
    private AvailableLanguages availableLanguages;
    private HashMap<String, Object> settings;

    public static final String LANGUAGE_KEY = "com/ebstrada/formreturn/language";

    public WelcomePanel(HashMap<String, Object> settings) {
        this.settings = settings;
        initComponents();
        restoreSettings();
    }

    private void restoreSettings() {

        this.availableLanguages = new AvailableLanguages();

        ApplicationStatePreferences applicationState = PreferencesManager.getApplicationState();

        // restore the language combo box.
        languageComboBox.setModel(availableLanguages.getLanguageComboBoxModel());

        String locale = FontLocaleUtil.getFontLocale(Locale.getDefault()).name();

        if (settings.containsKey(LANGUAGE_KEY)) {
            locale = (String) settings.get(LANGUAGE_KEY);
        } else if (applicationState != null && applicationState.getLocale() != null) {
            locale = applicationState.getLocale();
        }

        int i = 0;
        for (FontLocalesImpl fontLocale : FontLocalesImpl.values()) {
            if (fontLocale.name().equals(locale)) {
                languageComboBox.setSelectedIndex(i);
                break;
            }
            ++i;
        }

    }


    private void setLanguage() {
        String locale = availableLanguages.getCodeAtIndex(languageComboBox.getSelectedIndex());
        settings.put(LANGUAGE_KEY, locale);
        FontLocalesImpl fontLocales = FontLocaleUtil.getFontLocale(locale);
        Locale.setDefault(fontLocales.getLocale());
        if (Localizer.getCurrentLocale() != Locale.getDefault()) {
            Localizer.addLocale(Locale.getDefault());
            Localizer.switchCurrentLocale(Locale.getDefault());
        }
    }

    private void languageComboBoxItemStateChanged(ItemEvent e) {
        setLanguage();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        headingLabel = new JLabel();
        contentPanel = new JPanel();
        contentLabel = new JLabel();
        iconLabel = new JLabel();
        languageSelectionPanel = new JPanel();
        languageHeadingLabel = new JLabel();
        languageComboBox = new JComboBox();
        worldLabel = new JLabel();

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
        headingLabel.setMinimumSize(null);
        headingLabel.setPreferredSize(null);
        headingLabel.setMaximumSize(null);
        headingLabel.setText(Localizer.localize("UI", "SetupWizardWelcomeHeadingText"));
        add(headingLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //======== contentPanel ========
        {
            contentPanel.setOpaque(false);
            contentPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) contentPanel.getLayout()).columnWidths = new int[] {0, 0, 0};
            ((GridBagLayout) contentPanel.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) contentPanel.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 1.0E-4};
            ((GridBagLayout) contentPanel.getLayout()).rowWeights = new double[] {0.0, 1.0E-4};

            //---- contentLabel ----
            contentLabel.setVerticalAlignment(SwingConstants.TOP);
            contentLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
            contentLabel.setMaximumSize(null);
            contentLabel.setMinimumSize(null);
            contentLabel.setPreferredSize(null);
            contentLabel.setFont(UIManager.getFont("Label.font"));
            contentLabel.setText(Localizer.localize("UI", "SetupWizardWelcomeContentText"));
            contentPanel.add(contentLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

            //---- iconLabel ----
            iconLabel.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/images/logoIcon.png")));
            iconLabel.setFont(UIManager.getFont("Label.font"));
            contentPanel.add(iconLabel,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(contentPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //======== languageSelectionPanel ========
        {
            languageSelectionPanel.setOpaque(false);
            languageSelectionPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) languageSelectionPanel.getLayout()).columnWidths =
                new int[] {0, 0, 0, 0, 0, 0};
            ((GridBagLayout) languageSelectionPanel.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) languageSelectionPanel.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 0.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) languageSelectionPanel.getLayout()).rowWeights =
                new double[] {0.0, 1.0E-4};

            //---- languageHeadingLabel ----
            languageHeadingLabel.setFont(UIManager.getFont("Label.font"));
            languageHeadingLabel.setText(Localizer.localize("UI", "SetupWizardLanguageLabelText"));
            languageSelectionPanel.add(languageHeadingLabel,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));

            //---- languageComboBox ----
            languageComboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            languageComboBox.setFont(UIManager.getFont("ComboBox.font"));
            languageComboBox.setMinimumSize(new Dimension(200, 25));
            languageComboBox.setPreferredSize(null);
            languageComboBox.setMaximumSize(null);
            languageComboBox.addItemListener(new ItemListener() {
                @Override public void itemStateChanged(ItemEvent e) {
                    languageComboBoxItemStateChanged(e);
                }
            });
            languageSelectionPanel.add(languageComboBox,
                new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- worldLabel ----
            worldLabel.setIcon(new ImageIcon(
                getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/world.png")));
            worldLabel.setFont(UIManager.getFont("Label.font"));
            languageSelectionPanel.add(worldLabel,
                new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
        }
        add(languageSelectionPanel,
            new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel headingLabel;
    private JPanel contentPanel;
    private JLabel contentLabel;
    private JLabel iconLabel;
    private JPanel languageSelectionPanel;
    private JLabel languageHeadingLabel;
    private JComboBox languageComboBox;
    private JLabel worldLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    @Override public IWizardPanel next() throws Exception {
        setLanguage();
        return new SelectPageSizePanel(settings);

    }

    @Override public IWizardPanel back() throws Exception {
        throw new Exception();
    }

    @Override public void finish() throws Exception {
        throw new Exception();
    }

    @Override public void cancel() throws Exception {
        throw new Exception();
    }

    @Override public ArrayList<Integer> getActiveButtons() {
        ArrayList<Integer> activeButtons = new ArrayList<Integer>();
        activeButtons.add(WizardDialog.NEXT);
        return activeButtons;
    }

}

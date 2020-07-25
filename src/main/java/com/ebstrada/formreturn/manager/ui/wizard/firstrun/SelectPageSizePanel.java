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
import com.ebstrada.formreturn.manager.util.Misc;

public class SelectPageSizePanel extends JPanel implements IWizardPanel {

    public static final String PAGE_SIZE_KEY = "page_size";

    public static final String CJK_SUPPORT_KEY = "cjk_support";

    private static final long serialVersionUID = 1L;

    private HashMap<String, Object> settings;

    public SelectPageSizePanel(HashMap<String, Object> settings) {
        this.settings = settings;
        initComponents();
        restoreSettings();
    }

    private void restoreSettings() {
        if (settings.get(PAGE_SIZE_KEY) != null) {
            String pageSize = (String) settings.get(PAGE_SIZE_KEY);
            this.paperSizeComboBox.setSelectedItem(pageSize);
        } else {
            if (Misc.isA4Paper()) {
                this.paperSizeComboBox.setSelectedIndex(0);
            } else {
                this.paperSizeComboBox.setSelectedIndex(1);
            }
        }
    }

    @Override public IWizardPanel next() throws Exception {
        setPaperSize();
        setCJKSupport();
        return new CheckForUpdatesPanel(settings);

    }

    private void setCJKSupport() {
        settings.put(CJK_SUPPORT_KEY, this.useCJKFontRadioButton.isSelected());
    }

    @Override public IWizardPanel back() throws Exception {
        setPaperSize();
        setCJKSupport();
        return new WelcomePanel(settings);
    }

    @Override public void finish() throws Exception {
        // TODO Auto-generated method stub

    }

    @Override public void cancel() throws Exception {
        throw new Exception();
    }

    @Override public ArrayList<Integer> getActiveButtons() {
        ArrayList<Integer> activeButtons = new ArrayList<Integer>();
        activeButtons.add(WizardDialog.BACK);
        activeButtons.add(WizardDialog.NEXT);
        return activeButtons;
    }

    private void paperSizeComboBoxActionPerformed(ActionEvent e) {
        setPaperSize();
    }

    private void setPaperSize() {
        settings.put(PAGE_SIZE_KEY, this.paperSizeComboBox.getSelectedItem());
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        cjkHeadingLabel = new JLabel();
        cjkFontPanel = new JPanel();
        useCJKFontRadioButton = new JRadioButton();
        useVeraFontRadioButton = new JRadioButton();
        headingLabel = new JLabel();
        contentLabel = new JLabel();
        pageSizeComboBoxPanel = new JPanel();
        defaultPaperSizeLabel = new JLabel();
        paperSizeComboBox = new JComboBox();

        //======== this ========
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setOpaque(false);
        setLayout(new GridBagLayout());
        ((GridBagLayout) getLayout()).columnWidths = new int[] {0, 0};
        ((GridBagLayout) getLayout()).rowHeights = new int[] {0, 0, 10, 0, 0, 0, 10, 0};
        ((GridBagLayout) getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
        ((GridBagLayout) getLayout()).rowWeights =
            new double[] {0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 1.0E-4};

        //---- cjkHeadingLabel ----
        cjkHeadingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        cjkHeadingLabel.setText("<html><body><strong>" + Localizer.localize("UI", "CJKHeadingText")
            + "</strong></body></html>");
        add(cjkHeadingLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //======== cjkFontPanel ========
        {
            cjkFontPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
            cjkFontPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) cjkFontPanel.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) cjkFontPanel.getLayout()).rowHeights = new int[] {0, 0, 0};
            ((GridBagLayout) cjkFontPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) cjkFontPanel.getLayout()).rowWeights = new double[] {1.0, 1.0, 1.0E-4};

            //---- useCJKFontRadioButton ----
            useCJKFontRadioButton.setFont(UIManager.getFont("RadioButton.font"));
            useCJKFontRadioButton.setText(Localizer.localize("UI", "UseCJKFontRadioButtonText"));
            cjkFontPanel.add(useCJKFontRadioButton,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- useVeraFontRadioButton ----
            useVeraFontRadioButton.setSelected(true);
            useVeraFontRadioButton.setFont(UIManager.getFont("RadioButton.font"));
            useVeraFontRadioButton.setText(Localizer.localize("UI", "UseVeraFontRadioButtonText"));
            cjkFontPanel.add(useVeraFontRadioButton,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.NORTH,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        add(cjkFontPanel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //---- headingLabel ----
        headingLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headingLabel.setText(Localizer.localize("UI", "SetupWizardPageSizeHeadingText"));
        add(headingLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //---- contentLabel ----
        contentLabel.setVerticalAlignment(SwingConstants.TOP);
        contentLabel.setBorder(new EmptyBorder(15, 0, 0, 0));
        contentLabel.setFont(UIManager.getFont("Label.font"));
        contentLabel.setText(Localizer.localize("UI", "SetupWizardPageSizeContentText"));
        add(contentLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
            GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //======== pageSizeComboBoxPanel ========
        {
            pageSizeComboBoxPanel.setBorder(null);
            pageSizeComboBoxPanel.setOpaque(false);
            pageSizeComboBoxPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) pageSizeComboBoxPanel.getLayout()).columnWidths =
                new int[] {0, 0, 0, 0};
            ((GridBagLayout) pageSizeComboBoxPanel.getLayout()).rowHeights = new int[] {0, 0};
            ((GridBagLayout) pageSizeComboBoxPanel.getLayout()).columnWeights =
                new double[] {1.0, 0.0, 1.0, 1.0E-4};
            ((GridBagLayout) pageSizeComboBoxPanel.getLayout()).rowWeights =
                new double[] {0.0, 1.0E-4};

            //---- defaultPaperSizeLabel ----
            defaultPaperSizeLabel.setFont(UIManager.getFont("Label.font"));
            defaultPaperSizeLabel
                .setText(Localizer.localize("UI", "SetupWizardDefaultPaperSizeLabel"));
            pageSizeComboBoxPanel.add(defaultPaperSizeLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.EAST,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 5), 0, 0));

            //---- paperSizeComboBox ----
            paperSizeComboBox.setModel(new DefaultComboBoxModel(new String[] {"A4", "Letter"}));
            paperSizeComboBox.setPrototypeDisplayValue("xxxxxxxxxxxxxxxxxxxxx");
            paperSizeComboBox.setFont(UIManager.getFont("ComboBox.font"));
            paperSizeComboBox.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e) {
                    paperSizeComboBoxActionPerformed(e);
                }
            });
            pageSizeComboBoxPanel.add(paperSizeComboBox,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                    GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 5), 0, 0));
        }
        add(pageSizeComboBoxPanel,
            new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        //---- cjkButtonGroup ----
        ButtonGroup cjkButtonGroup = new ButtonGroup();
        cjkButtonGroup.add(useCJKFontRadioButton);
        cjkButtonGroup.add(useVeraFontRadioButton);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    private JLabel cjkHeadingLabel;
    private JPanel cjkFontPanel;
    private JRadioButton useCJKFontRadioButton;
    private JRadioButton useVeraFontRadioButton;
    private JLabel headingLabel;
    private JLabel contentLabel;
    private JPanel pageSizeComboBoxPanel;
    private JLabel defaultPaperSizeLabel;
    private JComboBox paperSizeComboBox;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


}

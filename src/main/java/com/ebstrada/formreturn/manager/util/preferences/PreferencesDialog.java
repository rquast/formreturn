package com.ebstrada.formreturn.manager.util.preferences;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.component.GradientHeaderUI;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.util.preferences.panel.CleanupPreferencesPanel;
import com.ebstrada.formreturn.manager.util.preferences.panel.DatabasePreferencesPanel;
import com.ebstrada.formreturn.manager.util.preferences.panel.EditorPreferencesPanel;
import com.ebstrada.formreturn.manager.util.preferences.panel.ExportPreferencesPanel;
import com.ebstrada.formreturn.manager.util.preferences.panel.FormCapturePreferencesPanel;
import com.ebstrada.formreturn.manager.util.preferences.panel.GeneralPreferencesPanel;
import com.ebstrada.formreturn.manager.util.preferences.panel.PublisherPreferencesPanel;

public class PreferencesDialog extends JFrame {

    private static final long serialVersionUID = 1L;

    public static final int GENERAL_PREFERENCES = 1;
    public static final int DATABASE_PREFERENCES = 2;
    public static final int FORM_CAPTURE_PREFERENCES = 3;
    public static final int EDITOR_PREFERENCES = 4;
    public static final int PUBLISHER_PREFERENCES = 5;
    public static final int EXPORT_PREFERENCES = 6;
    public static final int IMAGE_CLEANUP_PREFERENCES = 7;

    public PreferencesDialog() {
        initComponents();
        headerPanel.setUI(new GradientHeaderUI());
    }

    public void switchToPanel(int selectedPanelType) {

        JPanel preferencesPanel = null;

        switch (selectedPanelType) {

            case GENERAL_PREFERENCES:
                setTitle(Localizer.localize("Util", "PreferencesDialogGeneralPreferencesTitle"));
                preferencesPanel = new GeneralPreferencesPanel();
                break;
            case DATABASE_PREFERENCES:
                setTitle(Localizer.localize("Util", "PreferencesDialogDatabasePreferencesTitle"));
                preferencesPanel = new DatabasePreferencesPanel();
                break;
            case IMAGE_CLEANUP_PREFERENCES:
                setTitle(Localizer.localize("Util", "PreferencesDialogCleanupPreferencesTitle"));
                preferencesPanel = new CleanupPreferencesPanel();
                break;
            case FORM_CAPTURE_PREFERENCES:
                setTitle(Localizer.localize("Util", "PreferencesDialogCapturePreferencesTitle"));
                preferencesPanel = new FormCapturePreferencesPanel();
                break;
            case EDITOR_PREFERENCES:
                setTitle(Localizer.localize("Util", "PreferencesDialogEditorPreferencesTitle"));
                preferencesPanel = new EditorPreferencesPanel(this);
                break;
            case PUBLISHER_PREFERENCES:
                setTitle(Localizer.localize("Util", "PreferencesDialogPublishingPreferencesTitle"));
                preferencesPanel = new PublisherPreferencesPanel();
                break;
            case EXPORT_PREFERENCES:
                setTitle(Localizer.localize("Util", "PreferencesDialogExportPreferencesTitle"));
                preferencesPanel = new ExportPreferencesPanel();
                break;
            default:
                setTitle(Localizer.localize("Util", "PreferencesDialogGeneralPreferencesTitle"));
                preferencesPanel = new GeneralPreferencesPanel();
        }

        resizeAndDisplayPanel(preferencesPanel);

    }

    private void resizeAndDisplayPanel(JPanel preferencesPanel) {
        dialogPane.removeAll();
        if (Main.MAC_OS_X) {
            preferencesPanel.setBackground(new Color(235, 235, 235));
        }
        dialogPane.add(preferencesPanel, BorderLayout.CENTER);
        dialogPane.revalidate();
        dialogPane.repaint();
    }

    private void generalToggleButtonActionPerformed(ActionEvent e) {
        switchToPanel(GENERAL_PREFERENCES);
    }

    private void databaseToggleButtonActionPerformed(ActionEvent e) {
        switchToPanel(DATABASE_PREFERENCES);
    }

    private void formCaptureToggleButtonActionPerformed(ActionEvent e) {
        switchToPanel(FORM_CAPTURE_PREFERENCES);
    }

    private void editorToggleButtonActionPerformed(ActionEvent e) {
        switchToPanel(EDITOR_PREFERENCES);
    }

    private void publisherToggleButtonActionPerformed(ActionEvent e) {
        switchToPanel(PUBLISHER_PREFERENCES);
    }

    private void exportToggleButtonActionPerformed(ActionEvent e) {
        switchToPanel(EXPORT_PREFERENCES);
    }

    private void cleanupToggleButtonActionPerformed(ActionEvent e) {
        switchToPanel(IMAGE_CLEANUP_PREFERENCES);
    }

    private void databaseToggleButtonFocusGained(FocusEvent e) {
        databaseToggleButton.setBackground(new Color(240, 240, 240));
        databaseToggleButton.setOpaque(true);
    }

    private void databaseToggleButtonFocusLost(FocusEvent e) {
        databaseToggleButton.setBackground(null);
        databaseToggleButton.setOpaque(false);
    }

    private void generalToggleButtonFocusGained(FocusEvent e) {
        generalToggleButton.setBackground(new Color(240, 240, 240));
        generalToggleButton.setOpaque(true);
    }

    private void generalToggleButtonFocusLost(FocusEvent e) {
        generalToggleButton.setBackground(null);
        generalToggleButton.setOpaque(false);
    }

    private void editorToggleButtonFocusGained(FocusEvent e) {
        editorToggleButton.setBackground(new Color(240, 240, 240));
        editorToggleButton.setOpaque(true);
    }

    private void editorToggleButtonFocusLost(FocusEvent e) {
        editorToggleButton.setBackground(null);
        editorToggleButton.setOpaque(false);
    }

    private void publisherToggleButtonFocusGained(FocusEvent e) {
        publisherToggleButton.setBackground(new Color(240, 240, 240));
        publisherToggleButton.setOpaque(true);
    }

    private void publisherToggleButtonFocusLost(FocusEvent e) {
        publisherToggleButton.setBackground(null);
        publisherToggleButton.setOpaque(false);
    }

    private void cleanupToggleButtonFocusGained(FocusEvent e) {
        cleanupToggleButton.setBackground(new Color(240, 240, 240));
        cleanupToggleButton.setOpaque(true);
    }

    private void cleanupToggleButtonFocusLost(FocusEvent e) {
        cleanupToggleButton.setBackground(null);
        cleanupToggleButton.setOpaque(false);
    }

    private void formCaptureToggleButtonFocusGained(FocusEvent e) {
        formCaptureToggleButton.setBackground(new Color(240, 240, 240));
        formCaptureToggleButton.setOpaque(true);
    }

    private void formCaptureToggleButtonFocusLost(FocusEvent e) {
        formCaptureToggleButton.setBackground(null);
        formCaptureToggleButton.setOpaque(false);
    }

    private void exportToggleButtonFocusGained(FocusEvent e) {
        exportToggleButton.setBackground(new Color(240, 240, 240));
        exportToggleButton.setOpaque(true);
    }

    private void exportToggleButtonFocusLost(FocusEvent e) {
        exportToggleButton.setBackground(null);
        exportToggleButton.setOpaque(false);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        headerPanel = new JPanel();
        panel1 = new JPanel();
        generalToggleButton = new JButton();
        separator1 = new JSeparator();
        databaseToggleButton = new JButton();
        editorToggleButton = new JButton();
        publisherToggleButton = new JButton();
        cleanupToggleButton = new JButton();
        formCaptureToggleButton = new JButton();
        exportToggleButton = new JButton();
        dialogPane = new JPanel();

        //======== this ========
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(new ImageIcon(
            getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/frmanager_16x16.png"))
            .getImage());
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        this.setTitle(Localizer.localize("Util", "PreferencesDialogGeneralPreferencesTitle"));

        //======== headerPanel ========
        {
            headerPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
            headerPanel.setLayout(new GridBagLayout());
            ((GridBagLayout) headerPanel.getLayout()).columnWidths = new int[] {0, 0};
            ((GridBagLayout) headerPanel.getLayout()).rowHeights = new int[] {60, 0};
            ((GridBagLayout) headerPanel.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) headerPanel.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

            //======== panel1 ========
            {
                panel1.setOpaque(false);
                panel1.setLayout(new GridBagLayout());
                ((GridBagLayout) panel1.getLayout()).columnWidths =
                    new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
                ((GridBagLayout) panel1.getLayout()).rowHeights = new int[] {0, 0};
                ((GridBagLayout) panel1.getLayout()).columnWeights =
                    new double[] {1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0E-4};
                ((GridBagLayout) panel1.getLayout()).rowWeights = new double[] {1.0, 1.0E-4};

                //---- generalToggleButton ----
                generalToggleButton.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/preferences/brick.png")));
                generalToggleButton.setFont(UIManager.getFont("Button.font"));
                generalToggleButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        generalToggleButtonActionPerformed(e);
                    }
                });
                generalToggleButton.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) {
                        generalToggleButtonFocusGained(e);
                    }

                    @Override public void focusLost(FocusEvent e) {
                        generalToggleButtonFocusLost(e);
                    }
                });
                generalToggleButton.setHorizontalTextPosition(SwingConstants.CENTER);
                generalToggleButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                generalToggleButton
                    .setText(Localizer.localize("Util", "PreferencesDialogGeneralButtonText"));
                panel1.add(generalToggleButton,
                    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 8), 0, 0));

                //---- separator1 ----
                separator1.setOrientation(SwingConstants.VERTICAL);
                panel1.add(separator1,
                    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.VERTICAL, new Insets(0, 0, 0, 8), 0, 0));

                //---- databaseToggleButton ----
                databaseToggleButton.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/preferences/database_gear.png")));
                databaseToggleButton.setFont(UIManager.getFont("Button.font"));
                databaseToggleButton.setRolloverEnabled(true);
                databaseToggleButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        databaseToggleButtonActionPerformed(e);
                    }
                });
                databaseToggleButton.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) {
                        databaseToggleButtonFocusGained(e);
                    }

                    @Override public void focusLost(FocusEvent e) {
                        databaseToggleButtonFocusLost(e);
                    }
                });
                databaseToggleButton.setHorizontalTextPosition(SwingConstants.CENTER);
                databaseToggleButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                databaseToggleButton
                    .setText(Localizer.localize("Util", "PreferencesDialogDatabaseButtonText"));

                panel1.add(databaseToggleButton,
                    new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 8), 0, 0));

                //---- editorToggleButton ----
                editorToggleButton.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/preferences/page_white_paintbrush.png")));
                editorToggleButton.setFont(UIManager.getFont("Button.font"));
                editorToggleButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        editorToggleButtonActionPerformed(e);
                    }
                });
                editorToggleButton.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) {
                        editorToggleButtonFocusGained(e);
                    }

                    @Override public void focusLost(FocusEvent e) {
                        editorToggleButtonFocusLost(e);
                    }
                });
                editorToggleButton.setHorizontalTextPosition(SwingConstants.CENTER);
                editorToggleButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                editorToggleButton
                    .setText(Localizer.localize("Util", "PreferencesDialogEditorButtonText"));

                panel1.add(editorToggleButton,
                    new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 8), 0, 0));

                //---- publisherToggleButton ----
                publisherToggleButton.setFont(UIManager.getFont("Button.font"));
                publisherToggleButton.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/preferences/page_white_stack.png")));
                publisherToggleButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        publisherToggleButtonActionPerformed(e);
                    }
                });
                publisherToggleButton.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) {
                        publisherToggleButtonFocusGained(e);
                    }

                    @Override public void focusLost(FocusEvent e) {
                        publisherToggleButtonFocusLost(e);
                    }
                });
                publisherToggleButton.setHorizontalTextPosition(SwingConstants.CENTER);
                publisherToggleButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                publisherToggleButton
                    .setText(Localizer.localize("Util", "PreferencesDialogPublishingButtonText"));
                panel1.add(publisherToggleButton,
                    new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 8), 0, 0));

                //---- cleanupToggleButton ----
                cleanupToggleButton.setIcon(new ImageIcon(
                    getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/wand.png")));
                cleanupToggleButton.setFont(UIManager.getFont("Button.font"));
                cleanupToggleButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        cleanupToggleButtonActionPerformed(e);
                    }
                });
                cleanupToggleButton.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) {
                        cleanupToggleButtonFocusGained(e);
                    }

                    @Override public void focusLost(FocusEvent e) {
                        cleanupToggleButtonFocusLost(e);
                    }
                });
                cleanupToggleButton.setHorizontalTextPosition(SwingConstants.CENTER);
                cleanupToggleButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                cleanupToggleButton
                    .setText(Localizer.localize("Util", "PreferencesDialogCleanupButtonText"));

                panel1.add(cleanupToggleButton,
                    new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 8), 0, 0));

                //---- formCaptureToggleButton ----
                formCaptureToggleButton.setIcon(new ImageIcon(getClass().getResource(
                    "/com/ebstrada/formreturn/manager/ui/icons/preferences/page_white_camera.png")));
                formCaptureToggleButton.setFont(UIManager.getFont("Button.font"));
                formCaptureToggleButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        formCaptureToggleButtonActionPerformed(e);
                    }
                });
                formCaptureToggleButton.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) {
                        formCaptureToggleButtonFocusGained(e);
                    }

                    @Override public void focusLost(FocusEvent e) {
                        formCaptureToggleButtonFocusLost(e);
                    }
                });
                formCaptureToggleButton.setHorizontalTextPosition(SwingConstants.CENTER);
                formCaptureToggleButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                formCaptureToggleButton
                    .setText(Localizer.localize("Util", "PreferencesDialogFormCaptureButtonText"));

                panel1.add(formCaptureToggleButton,
                    new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 8), 0, 0));

                //---- exportToggleButton ----
                exportToggleButton.setIcon(new ImageIcon(getClass()
                    .getResource("/com/ebstrada/formreturn/manager/ui/icons/table_go.png")));
                exportToggleButton.setFont(UIManager.getFont("Button.font"));
                exportToggleButton.addActionListener(new ActionListener() {
                    @Override public void actionPerformed(ActionEvent e) {
                        exportToggleButtonActionPerformed(e);
                    }
                });
                exportToggleButton.addFocusListener(new FocusAdapter() {
                    @Override public void focusGained(FocusEvent e) {
                        exportToggleButtonFocusGained(e);
                    }

                    @Override public void focusLost(FocusEvent e) {
                        exportToggleButtonFocusLost(e);
                    }
                });
                exportToggleButton.setHorizontalTextPosition(SwingConstants.CENTER);
                exportToggleButton.setVerticalTextPosition(SwingConstants.BOTTOM);
                exportToggleButton
                    .setText(Localizer.localize("Util", "PreferencesDialogExportButtonText"));

                panel1.add(exportToggleButton,
                    new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                        GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
            }
            headerPanel.add(panel1,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(headerPanel, BorderLayout.NORTH);

        //======== dialogPane ========
        {
            dialogPane.setBorder(new MatteBorder(1, 0, 0, 0, Color.darkGray));
            dialogPane.setLayout(new BorderLayout());
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        setSize(780, 650);
        setLocationRelativeTo(null);

        //---- preferencesButtonGroup ----
        ButtonGroup preferencesButtonGroup = new ButtonGroup();
        preferencesButtonGroup.add(generalToggleButton);
        preferencesButtonGroup.add(databaseToggleButton);
        preferencesButtonGroup.add(editorToggleButton);
        preferencesButtonGroup.add(publisherToggleButton);
        preferencesButtonGroup.add(cleanupToggleButton);
        preferencesButtonGroup.add(formCaptureToggleButton);
        preferencesButtonGroup.add(exportToggleButton);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel headerPanel;
    private JPanel panel1;
    private JButton generalToggleButton;
    private JSeparator separator1;
    private JButton databaseToggleButton;
    private JButton editorToggleButton;
    private JButton publisherToggleButton;
    private JButton cleanupToggleButton;
    private JButton formCaptureToggleButton;
    private JButton exportToggleButton;
    private JPanel dialogPane;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public void resetButtons() {
        generalToggleButton.setSelected(true);
        generalToggleButton.getModel().setSelected(true);
        generalToggleButton.getModel().setArmed(true);
        generalToggleButton.requestFocus();
        switchToPanel(GENERAL_PREFERENCES);
    }

    public void selectDatabasePanel() {
        databaseToggleButton.setSelected(true);
        databaseToggleButton.getModel().setSelected(true);
        databaseToggleButton.getModel().setArmed(true);
        databaseToggleButton.requestFocus();
        switchToPanel(DATABASE_PREFERENCES);
    }

}

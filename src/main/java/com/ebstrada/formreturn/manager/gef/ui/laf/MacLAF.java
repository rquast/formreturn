package com.ebstrada.formreturn.manager.gef.ui.laf;

import java.awt.Font;

import javax.swing.UIManager;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.plaf.LookAndFeelAddons;
import org.jdesktop.swingx.plaf.macosx.MacOSXLookAndFeelAddons;

public class MacLAF implements ApplicationLAF {

    @Override public void setLAF() {
        System.setProperty("awt.useSystemAAFontSettings", "on");
        try {
            // Label.font
            Font oldLabelFont = UIManager.getFont("Label.font");
            UIManager.put("Label.font", oldLabelFont.deriveFont(Font.PLAIN, 11.0f));

            // Button.font
            Font oldButtonFont = UIManager.getFont("Button.font");
            UIManager.put("Button.font", oldButtonFont.deriveFont(Font.PLAIN, 11.0f));

            // CheckBox.font
            Font oldCheckBoxFont = UIManager.getFont("CheckBox.font");
            UIManager.put("CheckBox.font", oldCheckBoxFont.deriveFont(Font.PLAIN, 11.0f));

            // RadioButton.font
            Font oldRadioButtonFont = UIManager.getFont("RadioButton.font");
            UIManager.put("RadioButton.font", oldRadioButtonFont.deriveFont(Font.PLAIN, 11.0f));

            // ComboBox.font
            Font oldComboBoxFont = UIManager.getFont("ComboBox.font");
            UIManager.put("ComboBox.font", oldComboBoxFont.deriveFont(Font.PLAIN, 11.0f));

            // ColorChooser.font
            Font oldColorChooserFont = UIManager.getFont("ColorChooser.font");
            UIManager.put("ColorChooser.font", oldColorChooserFont.deriveFont(Font.PLAIN, 11.0f));

            // InternalFrame.titleFont
            // Font oldInternalFrameFont = UIManager.getFont("InternalFrame.font");
            // UIManager.put("InternalFrame.font", oldInternalFrameFont.deriveFont(Font.PLAIN, 11.0f));

            // List.font
            Font oldListFont = UIManager.getFont("List.font");
            UIManager.put("List.font", oldListFont.deriveFont(Font.PLAIN, 11.0f));

            // OptionPane.font
            Font oldOptionPaneFont = UIManager.getFont("OptionPane.font");
            UIManager.put("OptionPane.font", oldOptionPaneFont.deriveFont(Font.PLAIN, 12.0f));
            UIManager
                .put("OptionPane.messageFont", oldOptionPaneFont.deriveFont(Font.PLAIN, 12.0f));
            UIManager.put("OptionPane.buttonFont", oldButtonFont.deriveFont(Font.PLAIN, 11.0f));

            // Panel.font
            Font oldPanelFont = UIManager.getFont("Panel.font");
            UIManager.put("Panel.font", oldPanelFont.deriveFont(Font.PLAIN, 11.0f));

            // ProgressBar.font
            Font oldProgressBarFont = UIManager.getFont("ProgressBar.font");
            UIManager.put("ProgressBar.font", oldProgressBarFont.deriveFont(Font.PLAIN, 11.0f));

            // ScrollPane.font
            Font oldScrollPaneFont = UIManager.getFont("ScrollPane.font");
            UIManager.put("ScrollPane.font", oldScrollPaneFont.deriveFont(Font.PLAIN, 11.0f));

            // Viewport.font
            Font oldViewportFont = UIManager.getFont("Viewport.font");
            UIManager.put("Viewport.font", oldViewportFont.deriveFont(Font.PLAIN, 11.0f));

            // TextPane.font
            Font oldTextPaneFont = UIManager.getFont("TextPane.font");
            UIManager.put("TextPane.font", oldTextPaneFont.deriveFont(Font.PLAIN, 11.0f));

            // EditorPane.font
            Font oldEditorPaneFont = UIManager.getFont("EditorPane.font");
            UIManager.put("EditorPane.font", oldEditorPaneFont.deriveFont(Font.PLAIN, 11.0f));

            // ToolTip.font
            Font oldToolTipFont = UIManager.getFont("ToolTip.font");
            UIManager.put("ToolTip.font", oldToolTipFont.deriveFont(Font.PLAIN, 11.0f));

            // Tree.font
            Font oldTreeFont = UIManager.getFont("Tree.font");
            UIManager.put("Tree.font", oldTreeFont.deriveFont(Font.PLAIN, 11.0f));

            // ToggleButton.font
            Font oldToggleButtonFont = UIManager.getFont("ToggleButton.font");
            UIManager.put("ToggleButton.font", oldToggleButtonFont.deriveFont(Font.PLAIN, 11.0f));

            // TabbedPane.font
            Font oldTabbedPaneFont = UIManager.getFont("TabbedPane.font");
            UIManager.put("TabbedPane.font", oldTabbedPaneFont.deriveFont(Font.PLAIN, 13.0f));

            // Table.font
            Font oldTableFont = UIManager.getFont("Table.font");
            UIManager.put("Table.font", oldTableFont.deriveFont(Font.PLAIN, 11.0f));

            // TextField.font
            Font oldTextFieldFont = UIManager.getFont("TextField.font");
            UIManager.put("TextField.font", oldTextFieldFont.deriveFont(Font.PLAIN, 11.0f));

            // PasswordField.font
            Font oldPasswordFieldFont = UIManager.getFont("PasswordField.font");
            UIManager.put("PasswordField.font", oldPasswordFieldFont.deriveFont(Font.PLAIN, 11.0f));

            // TextArea.font
            Font oldTextAreaFont = UIManager.getFont("TextArea.font");
            UIManager.put("TextArea.font", oldTextAreaFont.deriveFont(Font.PLAIN, 11.0f));

            // ToolBar.font
            Font oldToolBarFont = UIManager.getFont("ToolBar.font");
            UIManager.put("ToolBar.font", oldToolBarFont.deriveFont(Font.PLAIN, 11.0f));

            // TableHeader.font
            Font oldTableHeaderFont = UIManager.getFont("TableHeader.font");
            UIManager.put("TableHeader.font", oldTableHeaderFont.deriveFont(Font.PLAIN, 11.0f));

            // Spinner.font
            Font oldSpinnerFont = UIManager.getFont("Spinner.font");
            UIManager.put("Spinner.font", oldSpinnerFont.deriveFont(Font.PLAIN, 11.0f));

            // TitledBorder.font
            Font oldTitledBorderFont = UIManager.getFont("TitledBorder.font");
            UIManager.put("TitledBorder.font", oldTitledBorderFont.deriveFont(Font.BOLD, 11.0f));

            // MenuItem.font
            Font oldMenuItemFont = UIManager.getFont("MenuItem.font");
            UIManager.put("MenuItem.font", oldMenuItemFont.deriveFont(Font.PLAIN, 12.0f));

            // Menu.font
            Font oldMenuFont = UIManager.getFont("Menu.font");
            UIManager.put("Menu.font", oldMenuFont.deriveFont(Font.PLAIN, 12.0f));

            // PopupMenu.font
            Font oldPopupMenuFont = UIManager.getFont("PopupMenu.font");
            UIManager.put("PopupMenu.font", oldPopupMenuFont.deriveFont(Font.PLAIN, 12.0f));

            UIManager.getLookAndFeelDefaults()
                .put(JXTaskPane.uiClassID, "org.jdesktop.swingx.plaf.misc.GlossyTaskPaneUI");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            LookAndFeelAddons.setAddon(MacOSXLookAndFeelAddons.class);
        } catch (Exception ex) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(ex);
        }
    }

}

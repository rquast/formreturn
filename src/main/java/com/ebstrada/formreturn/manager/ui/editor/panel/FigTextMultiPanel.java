package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.*;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ebstrada.formreturn.manager.gef.font.CachedFont;
import com.ebstrada.formreturn.manager.gef.font.CachedFontManager;
import com.ebstrada.formreturn.manager.gef.presentation.FigText;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.dialog.ColorChooser;
import com.ebstrada.formreturn.manager.util.Swatch;
import org.jdesktop.swingx.*;

public class FigTextMultiPanel extends EditorMultiPanel {

    private static final long serialVersionUID = 1L;

    private Vector selectedElements;

    private DefaultComboBoxModel cachedFontList;

    private CachedFontManager cachedFontManager;

    private boolean initialized = false;

    private Icon errorIcon = new ImageIcon(
        getClass().getResource("/com/ebstrada/formreturn/manager/ui/icons/error.png"));

    public FigTextMultiPanel() {

        // get font list
        cachedFontManager = Main.getCachedFontManager();
        cachedFontList = cachedFontManager.getCachedFontList();

        initComponents();

    }

    @Override public void updatePanel() {
        updateFontComboBoxes(selectedElements);
        updateAlignmentComboBox(selectedElements);
        initialized = true;
    }

    public String getFontFamily(Vector selectedElements) throws Exception {

        if (selectedElements == null || selectedElements.size() <= 0) {
            return "";
        }

        FigText firstFig = (FigText) selectedElements.get(0);
        String fontFamilyName = firstFig.getFontFamily();
        for (Object o : selectedElements) {

            FigText fig = (FigText) o;

            if (!(fig.getFontFamily().equals(fontFamilyName))) {
                throw new Exception(
                    Localizer.localize("UI", "TextAreasFontFamilyNameDiffersMessage"));
            }

            fontFamilyName = fig.getFontFamily();

        }

        return fontFamilyName;

    }

    public int getFontStyle(Vector selectedElements) throws Exception {
        if (selectedElements == null || selectedElements.size() <= 0) {
            return Font.PLAIN;
        }

        FigText firstFig = (FigText) selectedElements.get(0);
        int fontStyle = firstFig.getFontStyle();
        for (Object o : selectedElements) {

            FigText fig = (FigText) o;

            if (fig.getFontStyle() != fontStyle) {
                throw new Exception(Localizer.localize("UI", "TextAreasFontStyleDiffersMessage"));
            }

            fontStyle = fig.getFontStyle();

        }

        return fontStyle;

    }

    public void updateFontComboBoxes(Vector selectedElements) {

        if (initialized) {
            return;
        }

        String fontFamily = "";
        int fontFamilyIndex = -1;

        try {
            fontFamily = getFontFamily(selectedElements);
            fontFamilyIndex =
                ((DefaultComboBoxModel) fontFamilyComboBox.getModel()).getIndexOf(fontFamily);
            fontFamilyComboBox.setSelectedIndex(fontFamilyIndex);
        } catch (Exception ex) {
            DefaultComboBoxModel fontFamilyComboBoxModel =
                (DefaultComboBoxModel) fontFamilyComboBox.getModel();
            int indexOfFontFamilyDiffers =
                fontFamilyComboBoxModel.getIndexOf(Localizer.localize("UI", "DiffersText"));
            if (indexOfFontFamilyDiffers == -1) {
                // add the new item to the combo list and select it
                fontFamilyComboBox.addItem(Localizer.localize("UI", "DiffersText"));
                fontFamilyComboBox.setSelectedItem(Localizer.localize("UI", "DiffersText"));
            } else {
                // just select it
                fontFamilyComboBox.setSelectedItem(Localizer.localize("UI", "DiffersText"));
            }

        }

        fontFamilyComboBox.updateUI();

        int fontStyle;
        String selectedFontStyleItem = null;
        try {
            fontStyle = getFontStyle(selectedElements);
            switch (fontStyle) {
                case Font.BOLD:
                    selectedFontStyleItem = "Bold";
                    break;
                case Font.ITALIC:
                    selectedFontStyleItem = "Italic";
                    break;
                case Font.BOLD + Font.ITALIC:
                    selectedFontStyleItem = "Bold & Italic";
                    break;
                default:
                    selectedFontStyleItem = "Plain";
            }
            fontStyleComboBox.setSelectedItem(selectedFontStyleItem);
        } catch (Exception e) {
            selectedFontStyleItem = Localizer.localize("UI", "DiffersText");
            DefaultComboBoxModel fontStyleComboBoxModel =
                (DefaultComboBoxModel) fontStyleComboBox.getModel();
            int indexOfFontStyleDiffers =
                fontStyleComboBoxModel.getIndexOf(Localizer.localize("UI", "DiffersText"));
            if (indexOfFontStyleDiffers == -1) {
                fontStyleComboBox.addItem(Localizer.localize("UI", "DiffersText"));
                fontStyleComboBox.setSelectedItem(Localizer.localize("UI", "DiffersText"));
            } else {
                fontStyleComboBox.setSelectedItem(Localizer.localize("UI", "DiffersText"));
            }
        }

        fontStyleComboBox.updateUI();

        try {
            if (getFontSize() % 1 == 0.0f) {
                fontSizeComboBox.setSelectedItem(new Float(getFontSize()));
            } else {
                fontSizeComboBox.setSelectedItem(getFontSize() + "");
            }
        } catch (Exception ex) {
            fontSizeComboBox.setSelectedItem(Localizer.localize("UI", "DiffersText"));
        }

        fontSizeComboBox.updateUI();

        initialized = true;

    }

    public void updateAlignmentComboBox(Vector selectedFigs) {

        if (selectedFigs == null || selectedFigs.size() <= 0) {
            return;
        }

        FigText firstFig = (FigText) selectedFigs.get(0);
        int justification = firstFig.getJustification();
        boolean alignmentDiffers = false;

        for (Object o : selectedFigs) {
            FigText fig = (FigText) o;
            if (fig.getJustification() != justification) {
                alignmentDiffers = true;
                break;
            }
            justification = fig.getJustification();
        }

        if (alignmentDiffers) {
            DefaultComboBoxModel dcbm = (DefaultComboBoxModel) alignmentComboBox.getModel();
            int indexOfDiffers = dcbm.getIndexOf(Localizer.localize("UI", "DiffersText"));
            if (indexOfDiffers == -1) {
                alignmentComboBox.addItem(Localizer.localize("UI", "DiffersText"));
            }
            alignmentComboBox.setSelectedItem(Localizer.localize("UI", "DiffersText"));
            alignmentComboBox.updateUI();
        } else {
            alignmentComboBox.setSelectedItem(firstFig.getJustificationByName());
            alignmentComboBox.updateUI();
        }

    }

    @Override public void setSelectedElements(Vector selectedFigs) {
        selectedElements = selectedFigs;
        updateColorButtons(selectedFigs);
        updateFilledComboBox(selectedFigs);
    }

    private void updateColorButtons(Vector selectedFigs) {

        if (selectedFigs == null || selectedFigs.size() <= 0) {
            return;
        }

        FigText firstFig = (FigText) selectedFigs.get(0);
        Color backgroundColor = firstFig.getFillColor();
        boolean backgroundDiffers = false;
        Color foregroundColor = firstFig.getLineColor();
        boolean foregroundDiffers = false;
        for (Object o : selectedFigs) {
            FigText fig = (FigText) o;
            if (fig.getFillColor() != backgroundColor) {
                backgroundDiffers = true;
                break;
            }
        }
        for (Object o : selectedFigs) {
            FigText fig = (FigText) o;
            if (fig.getLineColor() != foregroundColor) {
                foregroundDiffers = true;
                break;
            }
        }
        if (!backgroundDiffers) {
            backgroundColorButton
                .setText(Localizer.localize("UI", "TextAreasTextBackgroundButtonText"));
            backgroundColorButton.setToolTipText(null);
            backgroundColorButton.setIcon(Swatch.forColor(backgroundColor));
        } else {
            backgroundColorButton
                .setText(Localizer.localize("UI", "TextAreasTextBackgroundButtonText"));
            backgroundColorButton.setToolTipText(
                Localizer.localize("UI", "TextAreasTextBackgroundButtonToolTipText"));
            backgroundColorButton.setIcon(errorIcon);
        }


        if (!foregroundDiffers) {
            foregroundColorButton.setText(Localizer.localize("UI", "TextAreasTextColorButtonText"));
            foregroundColorButton.setToolTipText(null);
            foregroundColorButton.setIcon(Swatch.forColor(foregroundColor));
        } else {
            foregroundColorButton.setText(Localizer.localize("UI", "TextAreasTextColorButtonText"));
            foregroundColorButton
                .setToolTipText(Localizer.localize("UI", "TextAreasTextColorButtonToolTipText"));
            foregroundColorButton.setIcon(errorIcon);
        }

    }

    private void updateFilledComboBox(Vector selectedFigs) {
        if (selectedFigs.size() <= 0) {
            return;
        }

        FigText firstFig = (FigText) selectedFigs.get(0);
        boolean isFilled = firstFig.getFilled();
        boolean filledDiffers = false;
        for (Object o : selectedFigs) {

            FigText fig = (FigText) o;

            if (fig.getFilled() != isFilled) {
                filledDiffers = true;
                break;
            }

            isFilled = fig.getFilled();

        }

        if (filledDiffers) {
            this.backgroundFilledComboBox.setSelectedIndex(2);
        } else if (isFilled) {
            this.backgroundFilledComboBox.setSelectedIndex(1);
        } else {
            this.backgroundFilledComboBox.setSelectedIndex(0);
        }
    }

    private void fontFamilyComboBoxActionPerformed(ActionEvent e) {
        String fontFamily = (String) ((JComboBox) e.getSource()).getSelectedItem();

        if (fontFamily != null && fontFamily != Localizer.localize("UI", "DiffersText")) {
            fontStyleComboBox.setModel(
                cachedFontManager.getCachedFontFamily(fontFamily).getAvailableStylesList());
            fontStyleComboBox.setSelectedIndex(0);
            for (Object o : selectedElements) {
                FigText fig = (FigText) o;
                setFont(fontFamily, fig.getFontSize(), fig.getFontStyle(), fig);
            }
        }
    }

    private String getSelectedFontFamily() {
        return (String) fontFamilyComboBox.getSelectedItem();
    }

    private int getSelectedFontStyle() {
        String fontStyle = (String) fontStyleComboBox.getSelectedItem();
        return getStyleByStyleName(fontStyle);
    }

    private int getStyleByStyleName(String fontStyle) {
        int style = Font.PLAIN;
        if (fontStyle != null) {
            if (fontStyle.equalsIgnoreCase("plain")) {
                style = Font.PLAIN;
            }
            if (fontStyle.equalsIgnoreCase("italic")) {
                style = Font.ITALIC;
            } else if (fontStyle.equalsIgnoreCase("bold")) {
                style = Font.BOLD;
            } else if (fontStyle.equalsIgnoreCase("bold & italic")) {
                style = Font.BOLD + Font.ITALIC;
            }
        }
        return style;
    }

    @Override public void removeListeners() {
        ActionListener[] actionListeners = fontFamilyComboBox.getActionListeners();
        for (int i = 0; i < actionListeners.length; i++) {
            fontFamilyComboBox.removeActionListener(actionListeners[i]);
        }

        actionListeners = fontSizeComboBox.getActionListeners();
        for (int i = 0; i < actionListeners.length; i++) {
            fontSizeComboBox.removeActionListener(actionListeners[i]);
        }

        // reset dropdowns
        fontFamilyComboBox.setSelectedIndex(-1);
        fontSizeComboBox.setSelectedIndex(-1);
    }

    private void fontSizeComboBoxActionPerformed(ActionEvent e) {

        float fontSize = 0.0f;

        if (fontSizeComboBox != null) {
            if (fontSizeComboBox.getSelectedItem() instanceof String) {
                String fontSizeString = (String) fontSizeComboBox.getSelectedItem();
                if (fontSizeString == Localizer.localize("UI", "DiffersText")) {
                    return;
                }
                try {
                    fontSize = Float.parseFloat(fontSizeString);
                } catch (Exception ex) {
                    return;
                }
            } else {
                fontSize = (Float) fontSizeComboBox.getSelectedItem();
            }
        }

        try {
            if (isFontSizeValid(fontSize)) {
                for (Object o : selectedElements) {
                    FigText fig = (FigText) o;
                    fig.setFontSize(fontSize);
                    fig.damage();
                }
            } else if (getFontSize() != fontSize && !isFontSizeValid(fontSize)) {
                revertFontSize();
            }
        } catch (Exception ex) {
            // do nothing
        }

    }

    private void revertFontSize() throws Exception {
        fontSizeComboBox.setSelectedItem(new Float(getFontSize()));
        updateUI();
    }


    private float getFontSize() throws Exception {
        if (selectedElements == null || selectedElements.size() <= 0) {
            return 10.0f;
        }
        FigText firstElement = (FigText) selectedElements.get(0);
        float fontSize = firstElement.getFontSize();
        for (Object o : selectedElements) {
            FigText fig = (FigText) o;
            if (fig.getFontSize() != fontSize) {
                throw new Exception(Localizer.localize("UI", "TextAreasFontSizeDiffersMessage"));
            }
            fontSize = fig.getFontSize();
        }
        return fontSize;
    }

    private void setFont(String fontFamily, float size, int style, FigText fig) {
        CachedFont selectedCachedFont = cachedFontManager.getCachedFont(style, fontFamily);
        if (selectedCachedFont == null) {
            return;
        }
        Font selectedFont = selectedCachedFont.getFont();
        if (selectedFont == null) {
            return;
        }
        fig.setFont(selectedFont.deriveFont(size));
        fig.damage();
    }

    private boolean isFontSizeValid(float fontSize) {
        if (fontSize >= 4.0f && fontSize <= 1000.0f) {
            return true;
        }

        return false;
    }

    private void alignmentComboBoxActionPerformed(ActionEvent e) {
        String alignment = (String) ((JComboBox) e.getSource()).getSelectedItem();
        for (Object o : selectedElements) {
            FigText fig = (FigText) o;
            fig.setJustificationByName(alignment);
            fig.damage();
        }
    }

    private void foregroundColorButtonActionPerformed(ActionEvent e) {
        ColorChooser colorChooserDialog =
            new ColorChooser(Main.getInstance(), selectedElements, true);
        colorChooserDialog.setTitle(Localizer.localize("UI", "TextColorChooserTitle"));
        colorChooserDialog.setVisible(true);
        colorChooserDialog.setModal(true);
        updateColorButtons(this.selectedElements);
    }

    private void backgroundColorButtonActionPerformed(ActionEvent e) {
        ColorChooser colorChooserDialog =
            new ColorChooser(Main.getInstance(), selectedElements, false);
        colorChooserDialog.setTitle(Localizer.localize("UI", "BackgroundColorChooserTitle"));
        colorChooserDialog.setVisible(true);
        colorChooserDialog.setModal(true);
        updateColorButtons(this.selectedElements);
    }

    private void fontStyleComboBoxActionPerformed(ActionEvent e) {

        if (!initialized) {
            return;
        }

        for (Object o : selectedElements) {
            FigText fig = (FigText) o;
            setFont(getSelectedFontFamily(), fig.getFontSize(), getSelectedFontStyle(), fig);
        }
    }

    private void backgroundFilledComboBoxActionPerformed(ActionEvent e) {

        if (selectedElements.size() <= 0) {
            return;
        }

        for (Object o : selectedElements) {
            FigText selectedElement = (FigText) o;
            if (this.backgroundFilledComboBox.getSelectedIndex() == 1) {
                selectedElement.setFilled(true);
            } else if (this.backgroundFilledComboBox.getSelectedIndex() == 0) {
                selectedElement.setFilled(false);
            }
            selectedElement.damage();
        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY
        // //GEN-BEGIN:initComponents
        panel1 = new JPanel();
        fontFamilyLabel = new JLabel();
        fontFamilyComboBox = new JComboBox();
        fontStyleLabel = new JLabel();
        fontStyleComboBox = new JComboBox();
        fontSizeLabel = new JLabel();
        fontSizeComboBox = new JComboBox();
        alignmentLabel = new JLabel();
        alignmentComboBox = new JComboBox();
        colorLabel = new JLabel();
        foregroundColorButton = new JButton();
        backgroundColorButton = new JButton();
        backgroundLabel = new JLabel();
        backgroundFilledComboBox = new JComboBox();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setMaximumSize(new Dimension(150, 2147483647));
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        this.setTitle(Localizer.localize("UI", "TextAreasPanelTitle"));

        //======== panel1 ========
        {
            panel1.setBorder(null);
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {97, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights =
                new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
                    1.0E-4};

            //---- fontFamilyLabel ----
            fontFamilyLabel.setFont(UIManager.getFont("Label.font"));
            fontFamilyLabel.setText(Localizer.localize("UI", "TextAreasFontFamilyLabel"));
            panel1.add(fontFamilyLabel,
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- fontFamilyComboBox ----
            fontFamilyComboBox.setMaximumSize(new Dimension(145, 32767));
            fontFamilyComboBox.setFont(UIManager.getFont("ComboBox.font"));
            fontFamilyComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fontFamilyComboBoxActionPerformed(e);
                }
            });
            fontFamilyComboBox.setModel(cachedFontList);
            panel1.add(fontFamilyComboBox,
                new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- fontStyleLabel ----
            fontStyleLabel.setFont(UIManager.getFont("Label.font"));
            fontStyleLabel.setText(Localizer.localize("UI", "TextAreasFontStyleLabel"));
            panel1.add(fontStyleLabel,
                new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- fontStyleComboBox ----
            fontStyleComboBox.setFont(UIManager.getFont("ComboBox.font"));
            fontStyleComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fontStyleComboBoxActionPerformed(e);
                }
            });
            panel1.add(fontStyleComboBox,
                new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- fontSizeLabel ----
            fontSizeLabel.setFont(UIManager.getFont("Label.font"));
            fontSizeLabel.setText(Localizer.localize("UI", "TextAreasFontSizeLabel"));
            panel1.add(fontSizeLabel,
                new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- fontSizeComboBox ----
            fontSizeComboBox.setEditable(true);
            fontSizeComboBox.setModel(new DefaultComboBoxModel(
                new String[] {"4", "6", "8", "9", "10", "11", "12", "13", "14", "16", "18", "20",
                    "22", "24", "28", "32", "36", "40", "48", "56", "64", "72", "144"}));
            fontSizeComboBox.setSelectedIndex(4);
            fontSizeComboBox.setMinimumSize(new Dimension(80, 22));
            fontSizeComboBox.setMaximumSize(new Dimension(145, 32767));
            fontSizeComboBox.setFont(UIManager.getFont("ComboBox.font"));
            fontSizeComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fontSizeComboBoxActionPerformed(e);
                }
            });
            panel1.add(fontSizeComboBox,
                new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- alignmentLabel ----
            alignmentLabel.setFont(UIManager.getFont("Label.font"));
            alignmentLabel.setText(Localizer.localize("UI", "TextAreasAlignmentLabel"));
            panel1.add(alignmentLabel,
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- alignmentComboBox ----
            alignmentComboBox.setModel(
                new DefaultComboBoxModel(new String[] {"Left", "Center", "Right", "Justified"}));
            alignmentComboBox.setFont(UIManager.getFont("ComboBox.font"));
            alignmentComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    alignmentComboBoxActionPerformed(e);
                }
            });
            panel1.add(alignmentComboBox,
                new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- colorLabel ----
            colorLabel.setFont(UIManager.getFont("Label.font"));
            colorLabel.setText(Localizer.localize("UI", "TextAreasColorLabel"));
            panel1.add(colorLabel,
                new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- foregroundColorButton ----
            foregroundColorButton.setMargin(new Insets(2, 5, 2, 5));
            foregroundColorButton.setFont(UIManager.getFont("Button.font"));
            foregroundColorButton.setIconTextGap(5);
            foregroundColorButton.setFocusPainted(false);
            foregroundColorButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    foregroundColorButtonActionPerformed(e);
                }
            });
		/*
		if ( selectedElement != null ) {
			foregroundColorButton.setIcon(com.ebstrada.formreturn.manager.util.Swatch.forColor(selectedElement.getLineColor()));
		}
		*/
            foregroundColorButton.setText(Localizer.localize("UI", "TextAreasTextColorButtonText"));
            panel1.add(foregroundColorButton,
                new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- backgroundColorButton ----
            backgroundColorButton.setMargin(new Insets(2, 5, 2, 5));
            backgroundColorButton.setFont(UIManager.getFont("Button.font"));
            backgroundColorButton.setIconTextGap(5);
            backgroundColorButton.setFocusPainted(false);
            backgroundColorButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    backgroundColorButtonActionPerformed(e);
                }
            });
		/*
		if ( selectedElement != null ) {
			backgroundColorButton.setIcon(com.ebstrada.formreturn.manager.util.Swatch.forColor(selectedElement.getFillColor()));
		}
		*/
            backgroundColorButton
                .setText(Localizer.localize("UI", "TextAreasTextBackgroundButtonText"));
            panel1.add(backgroundColorButton,
                new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- backgroundLabel ----
            backgroundLabel.setFont(UIManager.getFont("Label.font"));
            backgroundLabel.setText(Localizer.localize("UI", "TextAreasBackgroundLabel"));
            panel1.add(backgroundLabel,
                new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- backgroundFilledComboBox ----
            backgroundFilledComboBox.setModel(new DefaultComboBoxModel(
                new String[] {"Not Filled", "Filled", "Different Values"}));
            backgroundFilledComboBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    backgroundFilledComboBoxActionPerformed(e);
                }
            });
            panel1.add(backgroundFilledComboBox,
                new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
        }
        contentPane.add(panel1);
        // //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY
    // //GEN-BEGIN:variables
    private JPanel panel1;
    private JLabel fontFamilyLabel;
    private JComboBox fontFamilyComboBox;
    private JLabel fontStyleLabel;
    private JComboBox fontStyleComboBox;
    private JLabel fontSizeLabel;
    private JComboBox fontSizeComboBox;
    private JLabel alignmentLabel;
    private JComboBox alignmentComboBox;
    private JLabel colorLabel;
    private JButton foregroundColorButton;
    private JButton backgroundColorButton;
    private JLabel backgroundLabel;
    private JComboBox backgroundFilledComboBox;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public void cancelEmbed() {
        // embedFontCheckBox.setSelected(false);
    }

}

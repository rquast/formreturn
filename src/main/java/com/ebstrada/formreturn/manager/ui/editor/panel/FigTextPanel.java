package com.ebstrada.formreturn.manager.ui.editor.panel;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import javax.swing.*;

import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ebstrada.formreturn.manager.gef.font.CachedFont;
import com.ebstrada.formreturn.manager.gef.font.CachedFontManager;
import com.ebstrada.formreturn.manager.gef.presentation.Fig;
import com.ebstrada.formreturn.manager.gef.presentation.FigText;
import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.persistence.xstream.Document;
import com.ebstrada.formreturn.manager.ui.Main;
import com.ebstrada.formreturn.manager.ui.editor.dialog.ColorChooser;
import com.ebstrada.formreturn.manager.ui.editor.dialog.EmbeddedNoticeDialog;
import com.ebstrada.formreturn.manager.ui.editor.dialog.TextPropertyDialog;
import com.ebstrada.formreturn.manager.util.Swatch;
import org.jdesktop.swingx.*;

public class FigTextPanel extends EditorPanel {

    private static final long serialVersionUID = 1L;

    private Fig selectedElement;

    private DefaultComboBoxModel cachedFontList;

    private CachedFontManager cachedFontManager;

    private boolean initialized = false;

    public FigTextPanel() {

        // get font list
        cachedFontManager = Main.getCachedFontManager();
        cachedFontList = cachedFontManager.getCachedFontList();

        initComponents();

    }

    @Override public void updatePanel() {
        String fontFamily = ((FigText) selectedElement).getFontFamily();

        int fontStyle = ((FigText) selectedElement).getFontStyle();

        String localizedFontFamily = cachedFontManager.getLocalizedCachedFontFamilyName(fontFamily);

        int fontFamilyIndex =
            ((DefaultComboBoxModel) fontFamilyComboBox.getModel()).getIndexOf(localizedFontFamily);
        String selectedFontStyleItem = null;
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

        if (fontFamilyIndex >= 0 && fontFamilyComboBox.getSelectedIndex() < 0 || !initialized) {
            fontFamilyComboBox.setSelectedIndex(fontFamilyIndex);
            if (getFontSize() % 1 == 0.0f) {
                fontSizeComboBox.setSelectedItem(new Float(getFontSize()));
            } else {
                fontSizeComboBox.setSelectedItem(getFontSize() + "");
            }
            fontStyleComboBox.setSelectedItem(selectedFontStyleItem);

            fontFamilyComboBox.updateUI();
            fontSizeComboBox.updateUI();
            fontStyleComboBox.updateUI();
        }
        alignmentComboBox.setSelectedItem(((FigText) selectedElement).getJustificationByName());
        alignmentComboBox.updateUI();
        initialized = true;
        if (selectedElement != null) {
            foregroundColorButton.setIcon(Swatch.forColor(selectedElement.getLineColor()));
            backgroundColorButton.setIcon(Swatch.forColor(selectedElement.getFillColor()));
            int fsType = ((FigText) selectedElement).getFsType();
            if (fsType >= 4 && fsType < 8) {
                embedFontCheckBox.setSelected(false);
                embedFontCheckBox.setEnabled(false);
            } else {
                embedFontCheckBox.setSelected(((FigText) selectedElement).isEmbedded());
                embedFontCheckBox.setEnabled(true);
            }
        }
    }

    @Override public void setSelectedElement(Fig selectedFig) {
        selectedElement = selectedFig;
        filledCheckBox.setSelected(selectedElement.getFilled());
    }

    private void updateColors() {
        foregroundColorButton.setIcon(Swatch.forColor(selectedElement.getLineColor()));
        backgroundColorButton.setIcon(Swatch.forColor(selectedElement.getFillColor()));
    }

    private void fontFamilyComboBoxActionPerformed(ActionEvent e) {
        String fontFamily = (String) ((JComboBox) e.getSource()).getSelectedItem();

        if (fontFamily != null) {
            fontStyleComboBox.setModel(cachedFontManager.getLocalizedCachedFontFamily(fontFamily)
                .getAvailableStylesList());
            fontStyleComboBox.setSelectedIndex(0);
            setFont(fontFamily, getFontSize(), getSelectedFontStyle());
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
                fontSize = Float.parseFloat((String) fontSizeComboBox.getSelectedItem());
            } else {
                fontSize = (Float) fontSizeComboBox.getSelectedItem();
            }
        }

        if (isFontSizeValid(fontSize)) {
            ((FigText) selectedElement).setFontSize(fontSize);
            selectedElement.damage();
        } else if (getFontSize() != fontSize && !isFontSizeValid(fontSize)) {
            revertFontSize();
        }
    }

    private void revertFontSize() {
        fontSizeComboBox.setSelectedItem(new Float(getFontSize()));
        updateUI();
    }

    private float getFontSize() {
        if (selectedElement == null) {
            return 10.0f;
        }

        return ((FigText) selectedElement).getFontSize();
    }

    private void setFont(String fontFamily, float size, int style) {
        CachedFont selectedCachedFont = cachedFontManager.getLocalizedCachedFont(style, fontFamily);
        Font selectedFont = selectedCachedFont.getFont();
        ((FigText) selectedElement).setFont(selectedFont.deriveFont(size));
        int fsType = ((FigText) selectedElement).getFsType();
        if (fsType >= 4 && fsType < 8) {
            embedFontCheckBox.setSelected(false);
            embedFontCheckBox.setEnabled(false);
        } else {
            embedFontCheckBox.setSelected(false);
            embedFontCheckBox.setEnabled(true);
        }
        selectedElement.damage();
    }

    public CachedFont getCachedFont() {
        return cachedFontManager.getLocalizedCachedFont(((FigText) selectedElement).getFontStyle(),
            ((FigText) selectedElement).getFontFamily());
    }

    private boolean isFontSizeValid(float fontSize) {
        if (fontSize >= 4.0f && fontSize <= 1000.0f) {
            return true;
        }

        return false;
    }

    private void alignmentComboBoxActionPerformed(ActionEvent e) {
        String alignment = (String) ((JComboBox) e.getSource()).getSelectedItem();
        ((FigText) selectedElement).setJustificationByName(alignment);
        selectedElement.damage();
    }

    private void editContentButtonActionPerformed(ActionEvent e) {
        TextPropertyDialog tpd = new TextPropertyDialog((FigText) selectedElement);
        tpd.setAlignment(((FigText) selectedElement).getJustification());
        tpd.setVisible(true);
    }

    private void foregroundColorButtonActionPerformed(ActionEvent e) {
        ColorChooser colorChooserDialog =
            new ColorChooser(Main.getInstance(), selectedElement, true);
        colorChooserDialog.setTitle(Localizer.localize("UI", "TextColorChooserTitle"));
        colorChooserDialog.setModal(true);
        colorChooserDialog.setVisible(true);
        updateColors();
    }

    private void backgroundColorButtonActionPerformed(ActionEvent e) {
        ColorChooser colorChooserDialog =
            new ColorChooser(Main.getInstance(), selectedElement, false);
        colorChooserDialog.setTitle(Localizer.localize("UI", "BackgroundColorChooserTitle"));
        colorChooserDialog.setModal(true);
        colorChooserDialog.setVisible(true);
        updateColors();
    }

    private void filledCheckBoxActionPerformed(ActionEvent e) {
        selectedElement.setFilled(filledCheckBox.isSelected());
        selectedElement.damage();
    }

    private void fontStyleComboBoxActionPerformed(ActionEvent e) {
        setFont(getSelectedFontFamily(), getFontSize(), getSelectedFontStyle());
    }

    private void embedFontCheckBoxActionPerformed(ActionEvent e) {
        if (embedFontCheckBox.isSelected()) {
            EmbeddedNoticeDialog emnd = new EmbeddedNoticeDialog(Main.getInstance(), this);
            emnd.setVisible(true);
        }
    }

    public void embedFont() {
        ((FigText) selectedElement).setEmbedded(embedFontCheckBox.isSelected());
        Document document = getDocument();
        String fontFileName;
        try {
            fontFileName =
                document.addFont(((FigText) selectedElement).getFontFile(), getWorkingDirName());
            ((FigText) selectedElement).setFontFileName(fontFileName);
        } catch (NoSuchAlgorithmException e1) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
        } catch (IOException e1) {
            com.ebstrada.formreturn.manager.util.Misc.printStackTrace(e1);
        }
    }

    private Document getDocument() {
        return selectedElement.getGraph().getDocument();
    }

    private String getWorkingDirName() {
        return selectedElement.getGraph().getDocumentPackage().getWorkingDirName();
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
        embeddingLabel = new JLabel();
        embedFontCheckBox = new JCheckBox();
        alignmentLabel = new JLabel();
        alignmentComboBox = new JComboBox();
        colorLabel = new JLabel();
        foregroundColorButton = new JButton();
        backgroundColorButton = new JButton();
        backgroundLabel = new JLabel();
        filledCheckBox = new JCheckBox();
        editContentLabel = new JLabel();
        editContentButton = new JButton();

        //======== this ========
        setAlignmentX(0.0F);
        setAlignmentY(0.0F);
        setMaximumSize(new Dimension(150, 2147483647));
        setScrollOnExpand(true);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        this.setTitle(Localizer.localize("UI", "TextAreaPanelTitle"));

        //======== panel1 ========
        {
            panel1.setBorder(null);
            panel1.setOpaque(false);
            panel1.setLayout(new GridBagLayout());
            ((GridBagLayout) panel1.getLayout()).columnWidths = new int[] {97, 0};
            ((GridBagLayout) panel1.getLayout()).rowHeights =
                new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 0, 25, 0, 0};
            ((GridBagLayout) panel1.getLayout()).columnWeights = new double[] {1.0, 1.0E-4};
            ((GridBagLayout) panel1.getLayout()).rowWeights =
                new double[] {1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
                    1.0, 1.0, 1.0, 1.0E-4};

            //---- fontFamilyLabel ----
            fontFamilyLabel.setFont(UIManager.getFont("Label.font"));
            fontFamilyLabel.setText(Localizer.localize("UI", "TextAreaFontFamilyLabel"));
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
            fontStyleLabel.setText(Localizer.localize("UI", "TextAreaFontStyleLabel"));
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
            fontSizeLabel.setText(Localizer.localize("UI", "TextAreaFontSizeLabel"));
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

            //---- embeddingLabel ----
            embeddingLabel.setFont(UIManager.getFont("Label.font"));
            embeddingLabel.setText(Localizer.localize("UI", "TextAreaEmbeddingLabel"));
            panel1.add(embeddingLabel,
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- embedFontCheckBox ----
            embedFontCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            embedFontCheckBox.setOpaque(false);
            embedFontCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    embedFontCheckBoxActionPerformed(e);
                }
            });
            embedFontCheckBox.setText(Localizer.localize("UI", "TextAreaEmbedFontCheckBox"));
            panel1.add(embedFontCheckBox,
                new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- alignmentLabel ----
            alignmentLabel.setFont(UIManager.getFont("Label.font"));
            alignmentLabel.setText(Localizer.localize("UI", "TextAreaAlignmentLabel"));
            panel1.add(alignmentLabel,
                new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
                new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- colorLabel ----
            colorLabel.setFont(UIManager.getFont("Label.font"));
            colorLabel.setText(Localizer.localize("UI", "TextAreaColorLabel"));
            panel1.add(colorLabel,
                new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
            if (selectedElement != null) {
                foregroundColorButton.setIcon(com.ebstrada.formreturn.manager.util.Swatch
                    .forColor(selectedElement.getLineColor()));
            }
            foregroundColorButton.setText(Localizer.localize("UI", "TextAreaTextColorButtonText"));
            panel1.add(foregroundColorButton,
                new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
            if (selectedElement != null) {
                backgroundColorButton.setIcon(com.ebstrada.formreturn.manager.util.Swatch
                    .forColor(selectedElement.getFillColor()));
            }
            backgroundColorButton
                .setText(Localizer.localize("UI", "TextAreaBackgroundColorButtonText"));
            panel1.add(backgroundColorButton,
                new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- backgroundLabel ----
            backgroundLabel.setFont(UIManager.getFont("Label.font"));
            backgroundLabel.setText(Localizer.localize("UI", "TextAreaBackgroundLabel"));
            panel1.add(backgroundLabel,
                new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- filledCheckBox ----
            filledCheckBox.setFont(UIManager.getFont("CheckBox.font"));
            filledCheckBox.setBackground(null);
            filledCheckBox.setOpaque(false);
            filledCheckBox.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    filledCheckBoxActionPerformed(e);
                }
            });
            filledCheckBox.setText(Localizer.localize("UI", "TextAreaFilledCheckBox"));
            panel1.add(filledCheckBox,
                new GridBagConstraints(0, 14, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- editContentLabel ----
            editContentLabel.setFont(UIManager.getFont("Label.font"));
            editContentLabel.setText(Localizer.localize("UI", "TextAreaEditContentLabel"));
            panel1.add(editContentLabel,
                new GridBagConstraints(0, 15, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.HORIZONTAL, new Insets(0, 0, 5, 0), 0, 0));

            //---- editContentButton ----
            editContentButton.setFocusPainted(false);
            editContentButton.setFont(UIManager.getFont("Button.font"));
            editContentButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editContentButtonActionPerformed(e);
                }
            });
            editContentButton.setText(Localizer.localize("UI", "TextAreaEditContentButtonText"));
            panel1.add(editContentButton,
                new GridBagConstraints(0, 16, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
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
    private JLabel embeddingLabel;
    private JCheckBox embedFontCheckBox;
    private JLabel alignmentLabel;
    private JComboBox alignmentComboBox;
    private JLabel colorLabel;
    private JButton foregroundColorButton;
    private JButton backgroundColorButton;
    private JLabel backgroundLabel;
    private JCheckBox filledCheckBox;
    private JLabel editContentLabel;
    private JButton editContentButton;
    // JFormDesigner - End of variables declaration //GEN-END:variables

    public void cancelEmbed() {
        embedFontCheckBox.setSelected(false);
    }

}

package com.ebstrada.formreturn.manager.ui.editor.component;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class CheckBoxPreviewLabel extends JLabel {

    private static final long serialVersionUID = 1L;
    private int columnCount = 3;
    private int rowCount = 1;
    private float boxWeight = 1.0f;
    private int boxWidth = 16;
    private int boxHeight = 9;
    private int horizontalSpace = 12;
    private int verticalSpace = 10;
    private int widthRoundness = 65;
    private int heightRoundness = 65;
    private int fontDarkness = 100;
    private float fontSize = 6.0f;

    private String[][] checkboxValues = new String[1][5];

    private boolean updatePreview = false;
    private boolean showText;

    public CheckBoxPreviewLabel() {
        super();
        checkboxValues[0] = new String[] {"A", "B", "C", "D", "E"};
        updatePreview = true;
        setFontSize(fontSize);
    }

    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (updatePreview == false) {
            return;
        }

        Font font = getFont();

        int padding = Math.round(getBoxWeight());

        double arcWidth;
        double arcHeight;

        if (boxWidth > boxHeight) {

            arcWidth = ((double) widthRoundness / 100) * boxHeight;
            arcHeight = ((double) heightRoundness / 100) * boxHeight;

        } else {

            arcWidth = ((double) widthRoundness / 100) * boxWidth;
            arcHeight = ((double) heightRoundness / 100) * boxWidth;

        }

        String[][] checkboxValues = getCheckboxValues();

        int _w = (boxWidth * columnCount) + (horizontalSpace * (columnCount - 1)) + (padding * 2);
        int _h = (boxHeight * rowCount) + (verticalSpace * (rowCount - 1)) + (padding * 2);

        BufferedImage previewImage = (BufferedImage) (createImage(_w, _h));
        Graphics grx = previewImage.createGraphics();

        ((Graphics2D) grx)
            .setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        grx.setColor(Color.WHITE);
        grx.fillRect(0, 0, _w, _h);

        FontMetrics fontMetrics = ((Graphics) grx).getFontMetrics();

        if (font != null) {
            ((Graphics) grx).setFont(font);
            fontMetrics = ((Graphics) grx).getFontMetrics(font);
        }

        int _x = 0;
        int _y = 0;

        Stroke originalStroke = ((Graphics2D) grx).getStroke();
        ((Graphics2D) grx).setStroke(getStroke());

        for (int rowNumber = 1; rowNumber <= rowCount; rowNumber++) {

            for (int columnNumber = 1; columnNumber <= columnCount; columnNumber++) {

                int characterWidth =
                    fontMetrics.stringWidth(checkboxValues[rowNumber - 1][columnNumber - 1]);
                int characterHeight = fontMetrics.getAscent();

                int x = _x + padding + (boxWidth * (columnNumber - 1)) + (horizontalSpace * (
                    columnNumber - 1));
                int y = _y + padding + (boxHeight * (rowNumber - 1)) + (verticalSpace * (rowNumber
                    - 1));

                int characterX = x + ((boxWidth / 2) - (characterWidth / 2));
                int characterY = y + boxHeight - ((boxHeight - characterHeight) / 2) - (
                    Math.round(getBoxWeight()) / 2);

                grx.setColor(Color.BLACK);
                grx.drawRoundRect(x, y, boxWidth, boxHeight, (int) arcWidth, (int) arcHeight);
                grx.setColor(new Color(fontDarkness, fontDarkness, fontDarkness));
                if (showText) {
                    grx.drawString(checkboxValues[rowNumber - 1][columnNumber - 1], characterX,
                        characterY);
                }
                grx.setColor(Color.BLACK);

            }
        }

        ((Graphics2D) grx).setStroke(originalStroke);

        setIcon(new ImageIcon(previewImage));
        grx.dispose();
        updatePreview = false;
    }

    public Stroke getStroke() {
        BasicStroke stroke = null;
        stroke = new BasicStroke(getBoxWeight(), BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
        return stroke;
    }

    private String[][] getCheckboxValues() {
        return this.checkboxValues;
    }

    public void setWidthRoundness(int widthRoundness) {
        this.widthRoundness = widthRoundness;
    }

    private float getBoxWeight() {
        return this.boxWeight;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public void setBoxWeight(float boxWeight) {
        this.boxWeight = boxWeight;
    }

    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    public void setBoxHeight(int boxHeight) {
        this.boxHeight = boxHeight;
    }

    public void setHorizontalSpace(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
    }

    public void setVerticalSpace(int verticalSpace) {
        this.verticalSpace = verticalSpace;
    }

    public void setCheckboxValues(String[][] checkboxValues) {
        this.checkboxValues = checkboxValues;
    }

    public void updatePreview() {
        updatePreview = true;
        repaint();
    }

    public void setHeightRoundness(int heightRoundness) {
        this.heightRoundness = heightRoundness;
    }

    public void setFontSize(float checkboxFontSize) {
        this.fontSize = checkboxFontSize;
        setFont(getFont().deriveFont(checkboxFontSize));
    }

    public void setFontDarkness(int fontDarkness) {
        this.fontDarkness = fontDarkness;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

}

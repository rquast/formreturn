package com.ebstrada.formreturn.manager.ui.editor.persistence;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("markAreaPresetStyle") public class MarkAreaPresetStyle implements NoObfuscation {

    private String name = "";

    private int boxWidth = 16;
    private int boxHeight = 9;
    private float boxWeight = 1.0f;
    private int widthRoundness = 65;
    private int heightRoundness = 65;
    private int horizontalSpace = 12;
    private int verticalSpace = 10;
    private int fontDarkness = 100;
    private float fontSize = 6.0f;
    private boolean showText = true;
    private String aggregationRule = "";
    private boolean combineColumnCharacters = false;
    private String combinedColumnReadDirection = "LR";
    private boolean reconciliationKey = false;

    private String fieldname = Localizer.localize("UI", "QuestionPrefix");

    private int rowCount;
    private int columnCount;

    private String[][] checkboxValues;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBoxWidth() {
        return boxWidth;
    }

    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    public int getBoxHeight() {
        return boxHeight;
    }

    public void setBoxHeight(int boxHeight) {
        this.boxHeight = boxHeight;
    }

    public float getBoxWeight() {
        return boxWeight;
    }

    public void setBoxWeight(float boxWeight) {
        this.boxWeight = boxWeight;
    }

    public int getWidthRoundness() {
        return widthRoundness;
    }

    public void setWidthRoundness(int widthRoundness) {
        this.widthRoundness = widthRoundness;
    }

    public int getHeightRoundness() {
        return heightRoundness;
    }

    public void setHeightRoundness(int heightRoundness) {
        this.heightRoundness = heightRoundness;
    }

    public int getHorizontalSpace() {
        return horizontalSpace;
    }

    public void setHorizontalSpace(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
    }

    public int getVerticalSpace() {
        return verticalSpace;
    }

    public void setVerticalSpace(int verticalSpace) {
        this.verticalSpace = verticalSpace;
    }

    public int getFontDarkness() {
        return fontDarkness;
    }

    public void setFontDarkness(int fontDarkness) {
        this.fontDarkness = fontDarkness;
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
    }

    public String getAggregationRule() {
        return aggregationRule;
    }

    public void setAggregationRule(String aggregationRule) {
        this.aggregationRule = aggregationRule;
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setRowCount(int rowCount) {
        this.rowCount = rowCount;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public void setCheckboxValues(String[][] checkboxValues) {
        this.checkboxValues = checkboxValues;
    }

    public String[][] getCheckboxValues() {
        return checkboxValues;
    }

    /*
     * @deprecated
     */
    public String getFieldname() {
        return fieldname;
    }

    /*
     * @deprecated
     */
    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public boolean isCombineColumnCharacters() {
        return combineColumnCharacters;
    }

    public void setCombineColumnCharacters(boolean combineColumnCharacters) {
        this.combineColumnCharacters = combineColumnCharacters;
    }

    public boolean isReconciliationKey() {
        return reconciliationKey;
    }

    public void setReconciliationKey(boolean reconciliationKey) {
        this.reconciliationKey = reconciliationKey;
    }

    public String getCombinedColumnReadDirection() {
        return this.combinedColumnReadDirection;
    }

    public void setCombinedColumnReadDirection(String combinedColumnReadDirection) {
        this.combinedColumnReadDirection = combinedColumnReadDirection;
    }

}

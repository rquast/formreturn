package com.ebstrada.formreturn.manager.gef.undo.memento;

import com.ebstrada.formreturn.manager.gef.presentation.FigCheckbox;
import com.ebstrada.formreturn.manager.gef.undo.Memento;

public class CheckboxMemento extends Memento {

    public String[][] oldCheckboxValues;

    public FigCheckbox figCheckbox;

    private String[][] newCheckboxValues;

    private int oldBoxWidth;

    private int oldBoxHeight;

    private int oldWidthRoundness;

    private int oldHeightRoundness;

    private int oldFontDarkness;

    private float oldBoxWeight;

    private float oldCheckboxFontSize;

    private int oldHorizontalSpace;

    private int oldVerticalSpace;

    private String oldFieldname;

    private String oldAggregationRule;

    private boolean oldShowText;

    private boolean oldCombineColumnCharacters;

    private boolean oldReconciliationKey;

    private String oldMarkFieldname;

    private int oldMarkFieldnameOrderIndex;

    private int oldFieldnameOrderIndex;

    private int newBoxWidth;

    private int newBoxHeight;

    private int newWidthRoundness;

    private int newHeightRoundness;

    private int newFontDarkness;

    private float newBoxWeight;

    private float newCheckboxFontSize;

    private int newHorizontalSpace;

    private int newVerticalSpace;

    private String newFieldname;

    private String newAggregationRule;

    private boolean newShowText;

    private boolean newCombineColumnCharacters;

    private boolean newReconciliationKey;

    private String newMarkFieldname;

    private int newMarkFieldnameOrderIndex;

    private int newFieldnameOrderIndex;

    private String oldColumnReadDirection;

    private String newColumnReadDirection;

    public CheckboxMemento(FigCheckbox figCheckbox) {
        this.figCheckbox = figCheckbox;
    }

    public void undo() {
        figCheckbox.setCheckboxValues(oldCheckboxValues);
        figCheckbox.setBoxWidth(oldBoxWidth);
        figCheckbox.setBoxHeight(oldBoxHeight);
        figCheckbox.setWidthRoundness(oldWidthRoundness);
        figCheckbox.setHeightRoundness(oldHeightRoundness);
        figCheckbox.setFontDarkness(oldFontDarkness);
        figCheckbox.setBoxWeight(oldBoxWeight);
        figCheckbox.setFontSize(oldCheckboxFontSize);
        figCheckbox.setHorizontalSpace(oldHorizontalSpace);
        figCheckbox.setVerticalSpace(oldVerticalSpace);
        figCheckbox.setFieldname(oldFieldname);
        figCheckbox.setAggregationRule(oldAggregationRule);
        figCheckbox.setShowText(oldShowText);
        figCheckbox.setCombineColumnCharacters(oldCombineColumnCharacters);
        figCheckbox.setReconciliationKey(oldReconciliationKey);
        figCheckbox.setMarkFieldname(oldMarkFieldname);
        figCheckbox.setMarkFieldnameOrderIndex(oldMarkFieldnameOrderIndex);
        figCheckbox.setFieldnameOrderIndex(oldFieldnameOrderIndex);
        figCheckbox.damage();
        figCheckbox.firePropChange("undo", null, null);
    }

    public void redo() {
        figCheckbox.setCheckboxValues(newCheckboxValues);
        figCheckbox.setBoxWidth(newBoxWidth);
        figCheckbox.setBoxHeight(newBoxHeight);
        figCheckbox.setWidthRoundness(newWidthRoundness);
        figCheckbox.setHeightRoundness(newHeightRoundness);
        figCheckbox.setFontDarkness(newFontDarkness);
        figCheckbox.setBoxWeight(newBoxWeight);
        figCheckbox.setFontSize(newCheckboxFontSize);
        figCheckbox.setHorizontalSpace(newHorizontalSpace);
        figCheckbox.setVerticalSpace(newVerticalSpace);
        figCheckbox.setFieldname(newFieldname);
        figCheckbox.setAggregationRule(newAggregationRule);
        figCheckbox.setShowText(newShowText);
        figCheckbox.setCombineColumnCharacters(oldCombineColumnCharacters);
        figCheckbox.setReconciliationKey(oldReconciliationKey);
        figCheckbox.setMarkFieldname(newMarkFieldname);
        figCheckbox.setMarkFieldnameOrderIndex(newMarkFieldnameOrderIndex);
        figCheckbox.setFieldnameOrderIndex(newFieldnameOrderIndex);
        figCheckbox.damage();
        figCheckbox.firePropChange("redo", null, null);
    }

    public void dispose() {
    }

    public String toString() {
        return (isStartChain() ? "*" : " ") + "BarcodeMemento";
    }

    public String[][] getOldCheckboxValues() {
        return oldCheckboxValues;
    }

    public void setOldCheckboxValues(String[][] strings) {
        this.oldCheckboxValues = strings;
    }

    public void setNewCheckboxValues(String[][] newCheckboxValues) {
        this.newCheckboxValues = newCheckboxValues;
    }

    public void setNewBoxWidth(int newBoxWidth) {
        this.newBoxWidth = newBoxWidth;
    }

    public void setNewBoxHeight(int newBoxHeight) {
        this.newBoxHeight = newBoxHeight;
    }

    public void setNewWidthRoundness(int newWidthRoundness) {
        this.newWidthRoundness = newWidthRoundness;
    }

    public void setNewHeightRoundness(int newHeightRoundness) {
        this.newHeightRoundness = newHeightRoundness;
    }

    public void setNewFontDarkness(int newFontDarkness) {
        this.newFontDarkness = newFontDarkness;
    }

    public void setNewBoxWeight(float newBoxWeight) {
        this.newBoxWeight = newBoxWeight;
    }

    public void setNewFontSize(float newCheckboxFontSize) {
        this.newCheckboxFontSize = newCheckboxFontSize;
    }

    public void setNewHorizontalSpace(int newHorizontalSpace) {
        this.newHorizontalSpace = newHorizontalSpace;
    }

    public void setNewVerticalSpace(int newVerticalSpace) {
        this.newVerticalSpace = newVerticalSpace;
    }

    public void setNewFieldname(String newFieldname) {
        this.newFieldname = newFieldname;
    }

    public void setNewAggregationRule(String newAggregationRule) {
        this.newAggregationRule = newAggregationRule;
    }

    public void setNewShowText(boolean newShowText) {
        this.newShowText = newShowText;
    }

    public int getOldBoxWidth() {
        return oldBoxWidth;
    }

    public void setOldBoxWidth(int oldBoxWidth) {
        this.oldBoxWidth = oldBoxWidth;
    }

    public int getOldBoxHeight() {
        return oldBoxHeight;
    }

    public void setOldBoxHeight(int oldBoxHeight) {
        this.oldBoxHeight = oldBoxHeight;
    }

    public int getOldWidthRoundness() {
        return oldWidthRoundness;
    }

    public void setOldWidthRoundness(int oldWidthRoundness) {
        this.oldWidthRoundness = oldWidthRoundness;
    }

    public int getOldHeightRoundness() {
        return oldHeightRoundness;
    }

    public void setOldHeightRoundness(int oldHeightRoundness) {
        this.oldHeightRoundness = oldHeightRoundness;
    }

    public int getOldFontDarkness() {
        return oldFontDarkness;
    }

    public void setOldFontDarkness(int oldFontDarkness) {
        this.oldFontDarkness = oldFontDarkness;
    }

    public float getOldBoxWeight() {
        return oldBoxWeight;
    }

    public void setOldBoxWeight(float oldBoxWeight) {
        this.oldBoxWeight = oldBoxWeight;
    }

    public float getOldCheckboxFontSize() {
        return oldCheckboxFontSize;
    }

    public void setOldCheckboxFontSize(float oldCheckboxFontSize) {
        this.oldCheckboxFontSize = oldCheckboxFontSize;
    }

    public int getOldHorizontalSpace() {
        return oldHorizontalSpace;
    }

    public void setOldHorizontalSpace(int oldHorizontalSpace) {
        this.oldHorizontalSpace = oldHorizontalSpace;
    }

    public int getOldVerticalSpace() {
        return oldVerticalSpace;
    }

    public void setOldVerticalSpace(int oldVerticalSpace) {
        this.oldVerticalSpace = oldVerticalSpace;
    }

    public String getOldFieldname() {
        return oldFieldname;
    }

    public void setOldFieldname(String oldFieldname) {
        this.oldFieldname = oldFieldname;
    }

    public String getOldAggregationRule() {
        return oldAggregationRule;
    }

    public void setOldAggregationRule(String oldAggregationRule) {
        this.oldAggregationRule = oldAggregationRule;
    }

    public boolean isOldShowText() {
        return oldShowText;
    }

    public void setOldShowText(boolean oldShowText) {
        this.oldShowText = oldShowText;
    }

    public float getNewCheckboxFontSize() {
        return newCheckboxFontSize;
    }

    public void setNewCheckboxFontSize(float newCheckboxFontSize) {
        this.newCheckboxFontSize = newCheckboxFontSize;
    }

    public String[][] getNewCheckboxValues() {
        return newCheckboxValues;
    }

    public int getNewBoxWidth() {
        return newBoxWidth;
    }

    public int getNewBoxHeight() {
        return newBoxHeight;
    }

    public int getNewWidthRoundness() {
        return newWidthRoundness;
    }

    public int getNewHeightRoundness() {
        return newHeightRoundness;
    }

    public int getNewFontDarkness() {
        return newFontDarkness;
    }

    public float getNewBoxWeight() {
        return newBoxWeight;
    }

    public int getNewHorizontalSpace() {
        return newHorizontalSpace;
    }

    public int getNewVerticalSpace() {
        return newVerticalSpace;
    }

    public String getNewFieldname() {
        return newFieldname;
    }

    public String getNewAggregationRule() {
        return newAggregationRule;
    }

    public boolean isNewShowText() {
        return newShowText;
    }

    public boolean isOldCombineColumnCharacters() {
        return oldCombineColumnCharacters;
    }

    public void setOldCombineColumnCharacters(boolean oldCombineColumnCharacters) {
        this.oldCombineColumnCharacters = oldCombineColumnCharacters;
    }

    public boolean isOldReconciliationKey() {
        return oldReconciliationKey;
    }

    public void setOldReconciliationKey(boolean oldReconciliationKey) {
        this.oldReconciliationKey = oldReconciliationKey;
    }

    public boolean isNewCombineColumnCharacters() {
        return newCombineColumnCharacters;
    }

    public void setNewCombineColumnCharacters(boolean newCombineColumnCharacters) {
        this.newCombineColumnCharacters = newCombineColumnCharacters;
    }

    public boolean isNewReconciliationKey() {
        return newReconciliationKey;
    }

    public void setNewReconciliationKey(boolean newReconciliationKey) {
        this.newReconciliationKey = newReconciliationKey;
    }

    public String getOldMarkFieldname() {
        return oldMarkFieldname;
    }

    public void setOldMarkFieldname(String oldMarkFieldname) {
        this.oldMarkFieldname = oldMarkFieldname;
    }

    public int getOldMarkFieldnameOrderIndex() {
        return oldMarkFieldnameOrderIndex;
    }

    public void setOldMarkFieldnameOrderIndex(int oldMarkFieldnameOrderIndex) {
        this.oldMarkFieldnameOrderIndex = oldMarkFieldnameOrderIndex;
    }

    public int getOldFieldnameOrderIndex() {
        return oldFieldnameOrderIndex;
    }

    public void setOldFieldnameOrderIndex(int oldFieldnameOrderIndex) {
        this.oldFieldnameOrderIndex = oldFieldnameOrderIndex;
    }

    public String getNewMarkFieldname() {
        return newMarkFieldname;
    }

    public void setNewMarkFieldname(String newMarkFieldname) {
        this.newMarkFieldname = newMarkFieldname;
    }

    public int getNewMarkFieldnameOrderIndex() {
        return newMarkFieldnameOrderIndex;
    }

    public void setNewMarkFieldnameOrderIndex(int newMarkFieldnameOrderIndex) {
        this.newMarkFieldnameOrderIndex = newMarkFieldnameOrderIndex;
    }

    public int getNewFieldnameOrderIndex() {
        return newFieldnameOrderIndex;
    }

    public void setNewFieldnameOrderIndex(int newFieldnameOrderIndex) {
        this.newFieldnameOrderIndex = newFieldnameOrderIndex;
    }

    public void setOldCombinedColumnReadDirection(String columnReadDirection) {
        this.oldColumnReadDirection = columnReadDirection;
    }

    public String getOldColumnReadDirection() {
        return this.oldColumnReadDirection;
    }

    public void setNewCombinedColumnReadDirection(String columnReadDirection) {
        this.newColumnReadDirection = columnReadDirection;
    }

    public String getNewColumnReadDirection() {
        return this.newColumnReadDirection;
    }

}

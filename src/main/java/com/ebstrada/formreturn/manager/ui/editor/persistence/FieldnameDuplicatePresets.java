package com.ebstrada.formreturn.manager.ui.editor.persistence;

import com.ebstrada.formreturn.manager.gef.util.Localizer;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("fieldnameDuplicatePresets") public class FieldnameDuplicatePresets
    implements NoObfuscation {

    public transient static final int DIRECTION_TOP_TO_BOTTOM_LEFT_TO_RIGHT = 0;
    public transient static final int DIRECTION_LEFT_TO_RIGHT_TOP_TO_BOTTOM = 1;

    private String fieldname = Localizer.localize("UI", "QuestionPrefix");

    private String barcodeFieldname = Localizer.localize("UI", "BarcodeFieldnamePrefix");

    private int verticalDuplicates = 1;
    private int horizontalDuplicates = 1;
    private int verticalSpacing = 20;
    private int horizontalSpacing = 20;

    private int counterStart = 1;

    private int namingDirection = DIRECTION_TOP_TO_BOTTOM_LEFT_TO_RIGHT;

    public String getFieldname() {
        return fieldname;
    }

    public void setFieldname(String fieldname) {
        this.fieldname = fieldname;
    }

    public int getVerticalDuplicates() {
        return verticalDuplicates;
    }

    public void setVerticalDuplicates(int verticalDuplicates) {
        this.verticalDuplicates = verticalDuplicates;
    }

    public int getHorizontalDuplicates() {
        return horizontalDuplicates;
    }

    public void setHorizontalDuplicates(int horizontalDuplicates) {
        this.horizontalDuplicates = horizontalDuplicates;
    }

    public int getVerticalSpacing() {
        return verticalSpacing;
    }

    public void setVerticalSpacing(int verticalSpacing) {
        this.verticalSpacing = verticalSpacing;
    }

    public int getHorizontalSpacing() {
        return horizontalSpacing;
    }

    public void setHorizontalSpacing(int horizontalSpacing) {
        this.horizontalSpacing = horizontalSpacing;
    }

    public int getCounterStart() {
        return counterStart;
    }

    public void setCounterStart(int counterStart) {
        this.counterStart = counterStart;
    }

    public int getNamingDirection() {
        return namingDirection;
    }

    public void setNamingDirection(int namingDirection) {
        this.namingDirection = namingDirection;
    }

    public String getBarcodeFieldname() {
        return barcodeFieldname;
    }

}

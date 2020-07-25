package com.ebstrada.formreturn.manager.ui.editor.persistence;

import javax.swing.DefaultComboBoxModel;

import com.ebstrada.formreturn.manager.persistence.jpa.GradingRule;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("gradingRule") public class MarkingRule implements NoObfuscation, Cloneable {

    public transient static final int QUALIFIER_GREATER_THAN_OR_EQUAL_TO = 0;

    public transient static final int QUALIFIER_LESS_THAN = 1;

    public transient static final int QUALIFIER_EQUAL_TO = 2;

    public transient static final int THRESHOLD_IS_MARK = 0;

    public transient static final int THRESHOLD_IS_PERCENTAGE = 1;

    private double threshold;

    private int thresholdType;

    private int qualifier;

    private transient static final String[] QUALIFIERS =
        new String[] {"is greater than or equal to", "is less than", "is equal to"};

    private String grade;

    public int getQualifier() {
        return qualifier;
    }

    public void setQualifier(int qualifier) {
        this.qualifier = qualifier;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public int getThresholdType() {
        return thresholdType;
    }

    public void setThresholdType(int thresholdType) {
        this.thresholdType = thresholdType;
    }

    public static DefaultComboBoxModel getComboBoxModel() {

        DefaultComboBoxModel dcbm = new DefaultComboBoxModel();

        for (String qualifierDescription : QUALIFIERS) {
            dcbm.addElement(qualifierDescription);
        }

        return dcbm;

    }

    public GradingRule getGradingRule() {
        GradingRule gradingRule = new GradingRule();
        gradingRule.setQualifier((short) getQualifier());
        gradingRule.setGrade(getGrade());
        gradingRule.setThreshold(getThreshold());
        gradingRule.setThresholdType((short) getThresholdType());
        return gradingRule;
    }

    public void restore(GradingRule gradingRule) {
        setGrade(gradingRule.getGrade());
        setQualifier(gradingRule.getQualifier());
        setThreshold(gradingRule.getThreshold());
        setThresholdType(gradingRule.getThresholdType());
    }

    public MarkingRule clone() {
        try {
            return (MarkingRule) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String toString() {
        return "If score " + QUALIFIERS[qualifier] + " " + this.threshold + ((this.thresholdType
            == THRESHOLD_IS_PERCENTAGE) ? "%" : "") + ", grading is \"" + this.grade + "\"";
    }

}

package com.ebstrada.formreturn.manager.ui.editor.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

import com.ebstrada.formreturn.manager.persistence.jpa.Grading;
import com.ebstrada.formreturn.manager.persistence.jpa.GradingRule;
import com.ebstrada.formreturn.manager.util.NoObfuscation;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("markingProperties") public class MarkingProperties
    implements NoObfuscation, Cloneable {

    private double totalPossibleScore = 0;

    private ArrayList<MarkingRule> gradingRules = new ArrayList<MarkingRule>();

    public MarkingProperties clone() {
        MarkingProperties clone = null;

        try {
            clone = (MarkingProperties) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }

        ArrayList<MarkingRule> cloneGradingRules = new ArrayList<MarkingRule>();

        for (MarkingRule gradingRule : this.gradingRules) {
            cloneGradingRules.add((MarkingRule) gradingRule.clone());
        }

        clone.setGradingRules(cloneGradingRules);

        return clone;
    }

    public void restore(Grading grading) {
        List<GradingRule> grc = grading.getGradingRuleCollection();
        this.gradingRules = new ArrayList<MarkingRule>(grc.size());
        this.gradingRules.ensureCapacity(grc.size());
        for (GradingRule gradingRule : grc) {
            MarkingRule markingRule = new MarkingRule();
            markingRule.restore(gradingRule);
            this.gradingRules.add(gradingRule.getOrderIndex(), markingRule);
        }
        this.totalPossibleScore = grading.getTotalPossibleScore();
    }

    public ArrayList<MarkingRule> getGradingRules() {
        return gradingRules;
    }

    public void setGradingRules(ArrayList<MarkingRule> gradingRules) {
        this.gradingRules = gradingRules;
    }

    public double getTotalPossibleScore() {
        return totalPossibleScore;
    }

    public void setTotalPossibleScore(double totalPossibleScore) {
        this.totalPossibleScore = totalPossibleScore;
    }

    public DefaultListModel getGradingRulesListModel() {
        DefaultListModel dlm = new DefaultListModel();

        for (MarkingRule gradingRule : this.gradingRules) {
            dlm.addElement(gradingRule);
        }

        return dlm;
    }

}

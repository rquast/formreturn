package com.ebstrada.aggregation;

import com.ebstrada.aggregation.Rule;
import com.ebstrada.aggregation.Selection;
import com.ebstrada.aggregation.exception.ErrorFlagException;
import com.ebstrada.aggregation.exception.InvalidRulePartException;
import com.ebstrada.aggregation.exception.NoMatchException;

public class Aggregation {
    
    private Rule rule;
    private Selection selection;

    public double getAggregate() throws NoMatchException, ErrorFlagException, InvalidRulePartException {
	return rule.calculate(selection).getScore();
    }

    public Rule getRule() {
	return this.rule;
    }

    public Selection getSelection() {
	return this.selection;
    }

    public void setRule(Rule rule) {
	this.rule = rule;
    }

    public void setSelection(Selection selection) {
	this.selection = selection;
    }

}

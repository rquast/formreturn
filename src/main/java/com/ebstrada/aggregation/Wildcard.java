package com.ebstrada.aggregation;

import com.ebstrada.aggregation.IConditionPart;
import com.ebstrada.aggregation.Selection;
import com.ebstrada.aggregation.exception.InvalidRulePartException;

public class Wildcard implements IConditionPart {
    
    private boolean negated = false;
    
    private int count = 0;

    @Override
    public boolean isNegated() {
	return this.negated;
    }

    @Override
    public boolean match(Selection selectionValues) {
	boolean result = false;
	if (selectionValues.size() >= this.count) {
	    result = true;
	}
	if (negated) {
	    return !result;
	} else {
	    return result;
	}
    }

    @Override
    public void parse(String conditionPartStr) throws InvalidRulePartException {
	for (int i = 0; i < conditionPartStr.length(); ++i) {
	    if (conditionPartStr.charAt(i) == '\'') {
		++count;
	    }
	}
    }

    @Override
    public void setNegated(boolean negated) {
	this.negated = negated;
    }

}

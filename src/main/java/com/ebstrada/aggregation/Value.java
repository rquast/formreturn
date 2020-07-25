package com.ebstrada.aggregation;

import com.ebstrada.aggregation.IConditionPart;
import com.ebstrada.aggregation.Selection;

public class Value implements IConditionPart {
    
    private String value;
    
    private boolean negated = false;

    public String getValue() {
        return value;
    }

    @Override
    public boolean isNegated() {
	return this.negated;
    }

    @Override
    public boolean match(Selection selectionValues) {
	for (String selectionValue: selectionValues) {
	    if ( negated ) {
		if (value.equalsIgnoreCase(selectionValue.trim()) == false) {
		    return true;
		}
	    } else {
		if (value.equalsIgnoreCase(selectionValue.trim()) == true) {
		    return true;
		}
	    }
	}
	return false;
    }

    @Override
    public void parse(String conditionPartStr) {
	if (conditionPartStr.startsWith("!")) {
	    this.negated = true;
	    this.value = conditionPartStr.replaceFirst("!", "");
	} else {
	    this.value = conditionPartStr;
	}
    }

    @Override
    public void setNegated(boolean negated) {
	this.negated = negated;
    }

}

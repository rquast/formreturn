package com.ebstrada.aggregation;

import com.ebstrada.aggregation.AndCondition;
import com.ebstrada.aggregation.Selection;
import com.ebstrada.aggregation.exception.ErrorFlagException;
import com.ebstrada.aggregation.exception.InvalidRulePartException;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class OrCondition extends ArrayList<AndCondition> {
    
    private ArrayList<AndCondition> andConditions = new ArrayList<AndCondition>();

    public boolean match(Selection selection) throws ErrorFlagException, InvalidRulePartException {
	for (AndCondition andCondition: andConditions) {
	    if (andCondition.match(selection)) {
		return true;
	    }
	}
	return false;
    }

    public void parse(String conditionStr) throws InvalidRulePartException {
	if ( conditionStr == null ) {
	    throw new InvalidRulePartException();
	}
	for (String andConditionStr: conditionStr.split("\\|")) {
	    AndCondition andCondition = new AndCondition();
	    andCondition.parse(andConditionStr);
	    andConditions.add(andCondition);
	}
    }
    
}

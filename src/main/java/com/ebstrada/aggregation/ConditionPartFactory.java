package com.ebstrada.aggregation;

import com.ebstrada.aggregation.FunctionFactory;
import com.ebstrada.aggregation.IConditionPart;
import com.ebstrada.aggregation.Value;
import com.ebstrada.aggregation.Wildcard;
import com.ebstrada.aggregation.exception.InvalidRulePartException;

public class ConditionPartFactory {

    public static IConditionPart getConditionPart(String conditionPartStr) throws InvalidRulePartException {
	conditionPartStr = conditionPartStr.trim();
	if ((conditionPartStr.startsWith("!!!") || conditionPartStr.startsWith("!!")) && conditionPartStr.endsWith("!!")) {
	    return FunctionFactory.getFunction(conditionPartStr);
	} else if (conditionPartStr.startsWith("'")) {
	    Wildcard wildcard = new Wildcard();
	    wildcard.parse(conditionPartStr);
	    return wildcard;
	} else {
	    Value value = new Value();
	    value.parse(conditionPartStr);
	    return value;
	}
    }
    
}

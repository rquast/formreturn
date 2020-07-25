package com.ebstrada.aggregation;

import com.ebstrada.aggregation.AbstractFunction;
import com.ebstrada.aggregation.Selection;
import com.ebstrada.aggregation.exception.InvalidRulePartException;

import java.util.ArrayList;

public class FunctionRangeBetween extends AbstractFunction {
    
    private ArrayList<Double> range;

    @Override
    public boolean match(Selection selectionValues) {
	if ( selectionValues.size() <= 0 ) {
	    return negated ? true: false;
	}
	for ( String str: selectionValues ) {
	    double value;
	    try {
		if ( str.contains("/") ) {
		    String[] divisionStr = str.split("/");
		    double dividend = Double.parseDouble(divisionStr[0].trim());
		    double divisor = Double.parseDouble(divisionStr[1].trim());
		    value = dividend / divisor;
		} else {
		    value = Double.parseDouble(str.trim());
		}
	    } catch (Exception ex) {
		return negated ? true: false;
	    }
	    if ( value >= range.get(0) && value <= range.get(1) ) {
		continue;
	    } else {
		return negated ? true: false;
	    }
	}
	return negated ? false: true;
    }

    @Override
    public void parse(String conditionPartStr) throws InvalidRulePartException {
	conditionPartStr = conditionPartStr.trim();
	range = parseRangeFunctionParameter(conditionPartStr);
    }

}

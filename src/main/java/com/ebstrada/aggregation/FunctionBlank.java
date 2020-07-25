package com.ebstrada.aggregation;

import com.ebstrada.aggregation.AbstractFunction;
import com.ebstrada.aggregation.Selection;

public class FunctionBlank extends AbstractFunction {

    @Override
    public boolean match(Selection selectionValues) {
	if (selectionValues == null || selectionValues.size() <= 0) {
	    return negated ? false : true;
	} else {
	    return negated ? true : false;
	}
    }

    @Override
    public void parse(String conditionPartStr) {
	// not required for blank.
    }

}

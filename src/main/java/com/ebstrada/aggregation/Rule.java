package com.ebstrada.aggregation;

import com.ebstrada.aggregation.Result;
import com.ebstrada.aggregation.RulePart;
import com.ebstrada.aggregation.Selection;
import com.ebstrada.aggregation.exception.ErrorFlagException;
import com.ebstrada.aggregation.exception.InvalidRulePartException;
import com.ebstrada.aggregation.exception.NoMatchException;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class Rule extends ArrayList<RulePart> {

    public Rule() {
	super();
    }
    
    public Rule(String ruleStr) throws InvalidRulePartException {
	super();
	parse(ruleStr);
    }

    public Result calculate(Selection selection) throws NoMatchException, ErrorFlagException, InvalidRulePartException {
	for (RulePart rulePart: this) {
	    if (rulePart.size() == 0 && lastIndexOf(rulePart) == (size() - 1)) {
		return rulePart.getResult();
	    } else if (rulePart.match(selection)) {
		return rulePart.getResult();
	    } 
	}
	throw new NoMatchException();
    }
    
    public void parse(String ruleStr) throws InvalidRulePartException {
	clear();
	String[] ruleStrArr = ruleStr.split("\\:");
	if (ruleStrArr != null && ruleStrArr.length > 0) {
	    for (String ruleStrPart : ruleStrArr) {
		RulePart rulePart = new RulePart();
		rulePart.parse(ruleStrPart);
		add(rulePart);
	    }
	} else {
	    RulePart rulePart = new RulePart();
	    rulePart.parse(ruleStr);
	    add(rulePart);
	}
	
    }

}

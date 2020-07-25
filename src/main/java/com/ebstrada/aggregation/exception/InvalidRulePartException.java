package com.ebstrada.aggregation.exception;

import com.ebstrada.aggregation.i18n.Localizer;

@SuppressWarnings("serial")
public class InvalidRulePartException extends Exception {
    
    public InvalidRulePartException() {
	super();
    }

    public InvalidRulePartException(Exception ex) {
	super(ex);
    }
    
    @Override
    public String getMessage() {
	return Localizer.localize("InvalidRulePartExceptionMessage");
    }

}

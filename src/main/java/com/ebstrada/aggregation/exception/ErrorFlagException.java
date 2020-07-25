package com.ebstrada.aggregation.exception;

import com.ebstrada.aggregation.i18n.Localizer;

@SuppressWarnings("serial")
public class ErrorFlagException extends Exception {
    
    @Override
    public String getMessage() {
	return Localizer.localize("ErrorFlagExceptionMessage");
    }
    
}

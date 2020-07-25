package com.ebstrada.aggregation;

import com.ebstrada.aggregation.exception.ErrorFlagException;

public class Result {
    
    private double score;
    
    private ErrorFlagException flagException;

    public double getScore() throws ErrorFlagException {
	if (this.flagException != null) {
	    throw this.flagException;
	}
        return score;
    }

    public void setException(ErrorFlagException flagException) {
	this.flagException = flagException;
    }

    public void setScore(double score) {
	this.score = score;
    }

}

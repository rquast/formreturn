package com.ebstrada.aggregation;

import org.junit.After;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ebstrada.aggregation.exception.ErrorFlagException;

@RunWith(JUnit4.class)
public class ResultTest {

    @org.junit.Rule
    public ExpectedException exception = ExpectedException.none();

    @After
    public void tearDown() throws Exception {
        exception = ExpectedException.none();
    }

    @Test
    public void testUserSpecifiedFlagException() throws Exception {
        exception.expect(ErrorFlagException.class);
        Result result = new Result();
        result.setException(new ErrorFlagException());
        result.getScore();
    }
}

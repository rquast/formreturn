package com.ebstrada.aggregation;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class OrConditionTest {

    private OrCondition orCondition;

    @Before
    public void setUp() throws Exception {
        orCondition = new OrCondition();
    }

    @Test
    public void testMatch() throws Exception {
        Selection selection = new Selection(new String[]{"A"});
        orCondition.parse("A");
        assertTrue(orCondition.match(selection));
    }

    @Test
    public void testParseComplex() throws Exception {
        orCondition.parse("A,B,C|B,A|!C");
    }

}

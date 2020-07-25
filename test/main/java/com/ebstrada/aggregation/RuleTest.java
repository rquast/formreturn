package com.ebstrada.aggregation;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ebstrada.aggregation.exception.InvalidRulePartException;

@RunWith(JUnit4.class)
public class RuleTest {

    @org.junit.Rule
    public ExpectedException exception = ExpectedException.none();

    @After
    public void tearDown() throws Exception {
        exception = ExpectedException.none();
    }

    @Test
    public void testParseCorrect() throws Exception {
        Rule rule = new Rule();
        rule.parse("A?+1:B?+1:C?-1:0");
        assertEquals(4, rule.size());
    }

    @Test
    public void testParseInvalidRule1() throws Exception {
        exception.expect(InvalidRulePartException.class);
        Rule rule = new Rule();
        rule.parse("A:+1:B?+1:C?-1");
    }

}

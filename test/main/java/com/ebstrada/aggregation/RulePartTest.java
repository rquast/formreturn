package com.ebstrada.aggregation;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.ebstrada.aggregation.exception.InvalidRulePartException;

@RunWith(JUnit4.class)
public class RulePartTest {

    @org.junit.Rule
    public ExpectedException exception = ExpectedException.none();
    private RulePart rulePart;

    @Before
    public void setUp() throws Exception {
        rulePart = new RulePart();
    }

    @After
    public void tearDown() throws Exception {
        exception = ExpectedException.none();
        rulePart = null;
    }

    @Test
    public void testParseCorrect1() throws Exception {
        rulePart.parse("A?+1");
    }

    @Test
    public void testParseCorrect2() throws Exception {
        rulePart.parse("A?1");
    }

    @Test
    public void testParseIncorrect2() throws Exception {
        exception.expect(InvalidRulePartException.class);
        rulePart.parse("?");
    }

    @Test
    public void testParseIncorrect3() throws Exception {
        exception.expect(InvalidRulePartException.class);
        rulePart.parse(" ?");
    }

    @Test
    public void testParseIncorrect4() throws Exception {
        exception.expect(InvalidRulePartException.class);
        rulePart.parse(" ? ");
    }

    // test for no question mark
    @Test
    public void testParseIncorrect5() throws Exception {
        exception.expect(InvalidRulePartException.class);
        rulePart.parse("A1");
    }

}

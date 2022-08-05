package com.foros.util;

import java.math.BigDecimal;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@Category( Unit.class )
public class EqualsUtilTest {
    @Test
    public void equalsComparing() {
        assertTrue(EqualsUtil.equalsBigDecimal(new BigDecimal(10.01), new BigDecimal(10.01)));
        assertTrue(EqualsUtil.equalsBigDecimal(new BigDecimal(10.010), new BigDecimal(10.01)));
        assertTrue(EqualsUtil.equalsBigDecimal(null, null));
        assertFalse(EqualsUtil.equalsBigDecimal(new BigDecimal(10.010), null));
        assertFalse(EqualsUtil.equalsBigDecimal(null, new BigDecimal(10.010)));
    }

    @Test
    public void stringEquals() {
        assertTrue(EqualsUtil.stringEquals(null, null));
        assertTrue(EqualsUtil.stringEquals(null, ""));
        assertTrue(EqualsUtil.stringEquals("", null));
        assertTrue(EqualsUtil.stringEquals("  \t\n\r", ""));
        assertTrue(EqualsUtil.stringEquals(" x ", "x"));
        assertFalse(EqualsUtil.stringEquals(" x1 ", "x"));
    }

}

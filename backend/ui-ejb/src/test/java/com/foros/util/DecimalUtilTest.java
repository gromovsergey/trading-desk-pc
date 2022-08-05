package com.foros.util;

import java.math.BigDecimal;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@Category( Unit.class )
public class DecimalUtilTest {
    @Test
    public void notNull() {
        assertEquals(BigDecimal.ONE, DecimalUtil.notNull(BigDecimal.ONE));
        assertEquals(BigDecimal.ZERO, DecimalUtil.notNull(BigDecimal.ZERO));
        assertEquals(BigDecimal.ZERO, DecimalUtil.notNull(null));
    }

    @Test
    public void zeroOrNull() {
        assertTrue(DecimalUtil.isZeroOrNull(BigDecimal.ZERO));
        assertTrue(DecimalUtil.isZeroOrNull(null));
        assertFalse(DecimalUtil.isZeroOrNull(BigDecimal.ONE));
    }
}

package com.foros.util;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;

public class PairUtilTest {
    @Test
    @Category(Unit.class)
    public void createAsString() {
        assertEquals("201_test", PairUtil.createAsString("201", "test"));
        assertEquals("201_", PairUtil.createAsString("201", ""));
        assertEquals("201_  ", PairUtil.createAsString("201", "  "));
        assertEquals("201_", PairUtil.createAsString("201", null));
        assertEquals("100_aaa", PairUtil.createAsString(100L, "aaa"));
        assertEquals("20_bbb", PairUtil.createAsString(20, "bbb"));
        try {
            assertEquals("20_bbb", PairUtil.createAsString(null, "bbb"));
            fail("IllegalArgumentException must be thrown");
        } catch (IllegalArgumentException e) {
        }
    }

    @Test
    @Category(Unit.class)
    public void parseIdNamePair() {
        assertEquals("201", PairUtil.parseIdNamePair("201_acc").getValue());
        assertEquals("acc", PairUtil.parseIdNamePair("201_acc").getName());
        assertEquals("201", PairUtil.parseIdNamePair("201_").getValue());
        assertEquals("201", PairUtil.parseIdNamePair("201").getValue());
        assertEquals("   ", PairUtil.parseIdNamePair("201_   ").getName());
        assertEquals(null, PairUtil.parseIdNamePair("201").getName());
        assertEquals("   ", PairUtil.parseIdNamePair("   ").getValue());
        assertEquals("", PairUtil.parseIdNamePair("").getValue());
        assertEquals(null, PairUtil.parseIdNamePair(null).getName());
    }

    @Test
    @Category(Unit.class)
    public void validatePair() {
        assertTrue(PairUtil.validatePair("201_acc"));
        assertTrue(PairUtil.validatePair("201_"));
        assertFalse(PairUtil.validatePair("201"));
        assertTrue(PairUtil.validatePair(" _ "));
        assertFalse(PairUtil.validatePair(""));
        assertFalse(PairUtil.validatePair(null));
    }

    @Test
    @Category(Unit.class)
    public void fetchId() {
        assertEquals(Long.valueOf(201), PairUtil.fetchId("201_acc"));
        assertEquals(Long.valueOf(201), PairUtil.fetchId("201_"));
        assertEquals(Long.valueOf(201), PairUtil.fetchId("201"));

        try {
            assertEquals(Long.valueOf(201), PairUtil.fetchId(""));
            fail();
        } catch (NumberFormatException e) {
        }

        try {
            assertEquals(Long.valueOf(201), PairUtil.fetchId(null));
            fail();
        } catch (NumberFormatException e) {
        }
    }
}

package com.foros.util;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

public class AuditHelperTest {
    @Test
    @Category(Unit.class)
    public void toHexString() {
        assertEquals("1", AuditHelper.toHexString("01"));
        assertEquals("F", AuditHelper.toHexString("15"));
        assertEquals("37", AuditHelper.toHexString("55"));
    }

    @Test
    @Category(Unit.class)
    public void toTimeString() {
        assertEquals("00:01", AuditHelper.toTimeString("01"));
        assertEquals("00:55", AuditHelper.toTimeString("55"));
        assertEquals("05:00", AuditHelper.toTimeString("300"));
        int val = 555;
        assertEquals(String.format("%02d:%02d", val / 60, val % 60), String.format("%02d", val / 60) + ":" + String.format("%02d", val % 60));
    }
}

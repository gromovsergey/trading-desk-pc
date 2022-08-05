package com.foros.util;

import com.foros.AbstractUnitTest;

import org.junit.Assert;
import org.junit.Test;

public class RegularChecksUtilTest extends AbstractUnitTest {

    @Test
    public void testDueCaption() {
        assertEqualsText("now", RegularChecksUtil.getDueCaption(0.5, true));
        assertEqualsText("today", RegularChecksUtil.getDueCaption(0.5, false));
        assertEqualsText("now", RegularChecksUtil.getDueCaption(1, true));
        assertEqualsText("today", RegularChecksUtil.getDueCaption(1, false));
        assertEqualsText("1 hour(s) ago", RegularChecksUtil.getDueCaption(1.1, true));
        assertEqualsText("1 hour(s) ago", RegularChecksUtil.getDueCaption(1.9, true));
        assertEqualsText("23 hour(s) ago", RegularChecksUtil.getDueCaption(23.9, true));
        assertEqualsText("today", RegularChecksUtil.getDueCaption(23.9, false));
        assertEqualsText("today", RegularChecksUtil.getDueCaption(24, false));
        assertEqualsText("1 day(s) ago", RegularChecksUtil.getDueCaption(24, true));
        assertEqualsText("1 day(s) ago", RegularChecksUtil.getDueCaption(24.1, false));
        assertEqualsText("1 day(s) ago", RegularChecksUtil.getDueCaption(47.9, true));
        assertEqualsText("1 day(s) ago", RegularChecksUtil.getDueCaption(47.9, false));
        assertEqualsText("2 day(s) ago", RegularChecksUtil.getDueCaption(48, true));
        assertEqualsText("2 day(s) ago", RegularChecksUtil.getDueCaption(48, false));
    }

    private void assertEqualsText(String expected, String message) {
        message = message.replaceAll("<.*?>", "");
        Assert.assertEquals(expected, message);
    }

}

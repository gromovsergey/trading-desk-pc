package com.foros.util;

import group.Unit;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class VATValidatorTest extends Assert {
    private static final String COUNTRY_GB = "GB";
    private static final String COUNTRY_NON_GB = "RU";

    @Test
    public void testIsValid() {
        // GB
        assertFalse(VATValidator.isValid(COUNTRY_GB, "GB123 1234 23"));
        assertFalse(VATValidator.isValid(COUNTRY_GB, "GB923 1234 23 235"));
        assertTrue(VATValidator.isValid(COUNTRY_GB, "GB858 8719 55 235"));
        assertTrue(VATValidator.isValid(COUNTRY_GB, ""));
        assertFalse(VATValidator.isValid(COUNTRY_GB, "TR123"));
        assertTrue(VATValidator.isValid(COUNTRY_GB, "GB123 1234 27"));

        // Russia
        assertTrue(VATValidator.isValid(COUNTRY_NON_GB, "BE62510007547061"));
        assertTrue(VATValidator.isValid(COUNTRY_NON_GB, "12345"));
    }
}

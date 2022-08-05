package com.foros.util;

import java.util.Arrays;

import group.Unit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

public class StringOptionEnumValueComparatorTest {
    @Test
    @Category(Unit.class)
    public void compare() {
        String [] input = {"1abc", "abc", "10abc", "1bcd", "012abc" ,"bcd"};
        String [] expected = {"1abc", "1bcd", "10abc", "012abc", "abc", "bcd"};
        Arrays.sort(input, new StringOptionEnumValueComparator());
        Assert.assertTrue(Arrays.equals(input, expected));
    }
}

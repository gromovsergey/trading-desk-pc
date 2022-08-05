package com.foros.util.url;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;

@Category(Unit.class)
public class TriggerNormalizationTest {
    @Test
    public void validTriggerUrls() {
        assertEquals("google.com", TriggerNormalization.normalizeURL("http://google.com"));
        assertEquals("google.com", TriggerNormalization.normalizeURL("www.google.com"));
        assertEquals("google.com", TriggerNormalization.normalizeURL("http://www.google.com"));
        assertEquals("google.com", TriggerNormalization.normalizeURL("google.com/"));
        assertEquals("google.com", TriggerNormalization.normalizeURL("google.com:80"));
        assertEquals("google.com", TriggerNormalization.normalizeURL("google.com#anchor"));
    }
}

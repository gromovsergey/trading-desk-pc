package com.foros.model;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@Category(Unit.class)
public class FlagsTest {
    @Test
    public void flags() {
        Flags f = new Flags();
        assertEquals(0, f.intValue());

        f = new Flags(0xFF);
        assertEquals(0xFF, f.intValue());

        f = new Flags().set(0x08, true);
        assertEquals(0x08, f.intValue());
        assertTrue(f.isChanged(0x08));
        assertFalse(f.isChanged(~0x08));

        f = f.set(0xF0, Integer.MAX_VALUE);
        assertEquals(0xF8, f.intValue());
        assertTrue(f.isChanged(0xF8));
        assertTrue(f.isChanged(0xF0));
        assertTrue(f.isChanged(0x08));
        assertFalse(f.isChanged(~0xFF)) ;

        f = f.set(new Flags().set(0xffffffff, 0));
        assertEquals(0, f.intValue());
        assertTrue(f.isChanged(0xffffffff));
    }
}

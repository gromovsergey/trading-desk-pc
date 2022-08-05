package com.foros.cache.generic;

import group.Unit;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class TagTest {

    @Test
    public void testTagEquals() throws Exception {

        EntityIdTag tag1 = EntityIdTag.create("String", 10L);
        EntityIdTag tag2 = EntityIdTag.create("String", 10L);

        Assert.assertTrue(tag1.hashCode() == tag2.hashCode());
        Assert.assertTrue(tag1.equals(tag1));
        Assert.assertTrue(tag1.equals(tag2));
        Assert.assertTrue(tag2.equals(tag1));
    }

}

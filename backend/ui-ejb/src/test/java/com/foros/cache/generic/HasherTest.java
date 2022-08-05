package com.foros.cache.generic;

import com.foros.cache.generic.hasher.Md5Hasher;
import com.foros.cache.generic.serializer.ProtostuffSerializer;
import com.foros.cache.generic.serializer.Serializer;

import group.Unit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class HasherTest extends Assert {

    @Test
    public void testHasher() {
        Serializer serializer = new ProtostuffSerializer();
        Md5Hasher hasher = new Md5Hasher(serializer);

        String hash1 = hasher.hash(key(1, 2, 3));
        String hash2 = hasher.hash(key(new Object[] {1, 2}, 3));
        String hash3 = hasher.hash(key(1, new Object[] {2, 3}));

        assertFalse(hash1.equals(hash2));
        assertFalse(hash2.equals(hash3));
        assertFalse(hash3.equals(hash1));

        String hash1_2 = hasher.hash(key(1, 2, 3));
        String hash2_2 = hasher.hash(key(new Object[] {1, 2}, 3));
        String hash3_2 = hasher.hash(key(1, new Object[] {2, 3}));

        assertTrue(hash1.equals(hash1_2));
        assertTrue(hash2.equals(hash2_2));
        assertTrue(hash3.equals(hash3_2));
    }

    @Test
    public void testDifferentTypes() {
        Serializer serializer = new ProtostuffSerializer();
        Md5Hasher hasher = new Md5Hasher(serializer);

        String content = "a";
        String hash1 = hasher.hash(new TestKey(content));
        String hash2 = hasher.hash(new TestKey2(content));

        assertFalse(hash1.equals(hash2));
    }

    private Object key(Object... values) {
        return new TestKey(values);
    }

    private static class TestKey {
        /** @noinspection UnusedDeclaration*/
        private final Object key;

        public TestKey(Object... key) {
            this.key = key;
        }
    }


    // should be exactly the same as TestKey1
    private static class TestKey2 {
        /** @noinspection UnusedDeclaration*/
        private final Object key;

        public TestKey2(Object... key) {
            this.key = key;
        }
    }

}

package com.foros.cache.generic.serializer;

import com.foros.cache.generic.EntityIdTag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import group.Unit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(Unit.class)
public class ProtostuffSerializerTest {
    @Test
    public void testTag() {
        EntityIdTag tag = EntityIdTag.create("Entity", 23456L);
        ProtostuffSerializer serializer = new ProtostuffSerializer();

        byte[] buf = serializer.serialize(tag);
        System.out.println(Arrays.toString(buf));

        assertNotNull(buf);
        assertTrue(buf.length > 0);

        EntityIdTag deserialized = serializer.deserialize(EntityIdTag.class, buf);

        assertEquals(tag, deserialized);
    }

    private static Long LONG_VALUE = 1234567890L;
    private static String STR_VALUE = "abc...xyz";

    private static class Holder {
        Object content;
    }

    @Test
    public void testSerializeSimpleList() {
        Holder holder = new Holder();
        List list = new ArrayList();
        list.add(LONG_VALUE);
        list.add(STR_VALUE);
        holder.content = list;
        Serializer serializer = new ProtostuffSerializer();
        byte[] bytes = serializer.serialize(holder);
        holder = serializer.deserialize(Holder.class, bytes);
        assertNotNull(holder);
        list = (List) holder.content;
        assertNotNull(list);
        assertEquals(list.size(), 2);
        assertEquals(list.get(0), LONG_VALUE);
        assertEquals(list.get(1), STR_VALUE);
    }

    @Test
    public void testSerializeSimpleArray() {
        Holder holder = new Holder();
        Object[] array = {LONG_VALUE, STR_VALUE};
        holder.content = array;
        Serializer serializer = new ProtostuffSerializer();
        byte[] bytes = serializer.serialize(holder);
        holder = serializer.deserialize(Holder.class, bytes);
        assertNotNull(holder);
        array = (Object[]) holder.content;
        assertNotNull(array);
        assertEquals(array.length, 2);
        assertEquals(array[0], LONG_VALUE);
        assertEquals(array[1], STR_VALUE);
    }

    @Test
    public void testSerializeComplexArray() {
        List<Long> list = new ArrayList<Long>();
        list.add(LONG_VALUE);
        Object[] array = {LONG_VALUE, STR_VALUE, list};
        Holder holder = new Holder();
        holder.content = new Object[] {array};
        Serializer serializer = new ProtostuffSerializer();
        byte[] bytes = serializer.serialize(holder);
        holder = serializer.deserialize(Holder.class, bytes);
        assertNotNull(holder);
        array = (Object[]) holder.content;
        assertNotNull(array);
        assertEquals(array.length, 1);
        array = (Object[]) array[0];
        assertNotNull(array);
        assertEquals(array.length, 3);
        assertEquals(array[0], LONG_VALUE);
        assertEquals(array[1], STR_VALUE);
        list = (List<Long>) array[2];
        assertNotNull(list);
        assertEquals(list.size(), 1);
        assertEquals(list.get(0), LONG_VALUE);
    }


}

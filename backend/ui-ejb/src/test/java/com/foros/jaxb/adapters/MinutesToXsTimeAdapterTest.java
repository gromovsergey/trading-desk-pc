package com.foros.jaxb.adapters;

import group.Jaxb;
import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category( { Unit.class, Jaxb.class } )
public class MinutesToXsTimeAdapterTest {
    private MinutesToXsTimeAdapter adapter = new MinutesToXsTimeAdapter();

    @Test
    public void marshalUnmarshal() throws Exception {
        doMarshalUnmarshal(null, null);
        doMarshalUnmarshal("00:00:00", 0);
        doMarshalUnmarshal("10:25:00", 10 * 60 + 25);

        // seconds are ignored
        assertEquals(10 * 60 + 25, adapter.unmarshal("10:25:11").intValue());
    }

    private void doMarshalUnmarshal(String str, Integer i) throws Exception {
        assertEquals(str, adapter.marshal(i));
        assertEquals(i, adapter.unmarshal(str));
    }
}

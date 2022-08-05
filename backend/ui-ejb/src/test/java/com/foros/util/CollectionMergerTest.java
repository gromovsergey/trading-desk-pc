package com.foros.util;

import java.util.ArrayList;
import java.util.List;

import group.Unit;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;

@Category( Unit.class )
public class CollectionMergerTest {
    @Test
    public void collectionMerger() {
        merge(generateIntegerList(100), generateIntegerList(50));
        merge(generateIntegerList(50), generateIntegerList(100));
    }

    private void merge(final List<Integer> oi1, final List<Integer> oi2) {
        (new CollectionMerger<Integer>(oi1, oi2) {
            @Override
            protected Object getId(Integer integer, int index) {
                assertEquals(integer.intValue(), index);
                return index;
            }

            @Override
            protected void update(Integer persistent, Integer updated) {
                assertEquals(persistent, updated);
            }

            @Override
            protected boolean add(Integer updated) {
                return true;
            }

            @Override
            protected boolean delete(Integer tp) {
                return true;
            }
        }).merge();

        assertEquals(oi1, oi2);
    }

    private List<Integer> generateIntegerList(int size) {
        ArrayList<Integer> list = new ArrayList<Integer>(size);
        for (int i = 0; i < size; i++) {
             list.add(i);
        }
        return list;
    }
}

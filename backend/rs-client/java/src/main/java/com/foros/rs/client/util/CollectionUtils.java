package com.foros.rs.client.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CollectionUtils {

    public static <T> List<T> filterNotNull(List<T> list) {
        ArrayList<T> result = new ArrayList<>();

        for (T t : list) {
            if (t != null) {
                result.add(t);
            }
        }

        return result;
    }

    public static <T> void removeNotNull(List<T> list) {
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T next = iterator.next();
            if (next == null) {
                iterator.remove();
            }
        }
    }

    public static List<Long> newLongListWithSize(int size) {
        return new ArrayList<>(Arrays.asList(new Long[size]));
    }

    public static <T> List<T> copyExcludeIndexes(List<T> list, Set<Integer> excludeIndexes) {
        ArrayList<T> result = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            if (!excludeIndexes.contains(i)) {
                result.add(list.get(i));
            }
        }

        return result;
    }
}

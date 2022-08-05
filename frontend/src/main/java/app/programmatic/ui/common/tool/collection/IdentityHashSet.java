package app.programmatic.ui.common.tool.collection;

import java.util.IdentityHashMap;

public class IdentityHashSet<T> {
    private IdentityHashMap<T, Void> internal = new IdentityHashMap<>();

    public boolean add(T element) {
        if (!internal.containsKey(element)) {
            internal.put(element, null);
            return true;
        }
        return false;
    }
}
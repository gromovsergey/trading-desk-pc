package com.foros.util.jpa;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

/**
 * Most useful for paging implementation.
 */
public class DetachedList<T> extends AbstractList<T> {
    private final List<T> list;
    private final int total;
    private final boolean totalHasMore;

    public DetachedList() {
        this(Collections.<T>emptyList(), 0, false);
    }

    public DetachedList(List<T> list, int total) {
        this(list, total, false);
    }

    public DetachedList(List<T> list, int total, boolean totalHasMore) {
        this.totalHasMore = totalHasMore;
        this.list  = Collections.unmodifiableList(list);
        this.total = total;
    }

    public int getTotal() {
        return total;
    }

    public boolean isSizeReduced() {
        return total > list.size();
    }

    @Override
    public T get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    public boolean isTotalHasMore() {
        return totalHasMore;
    }
}

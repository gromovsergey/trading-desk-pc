package com.foros.session.query;

import com.foros.session.bulk.Paging;

import java.util.AbstractList;
import java.util.Collections;
import java.util.List;

public class PartialList<T> extends AbstractList<T> {

    private static final PartialList EMPTY_LIST = new PartialList(0, 0, Collections.emptyList());

    private List<T> values;
    private int total;
    private int from;
    private Paging paging;

    public PartialList(int total, int from, List<T> values) {
        this.values = values;
        this.total = total;
        this.from = from;
    }

    public PartialList(int total, Paging paging, List<T> values) {
        this.values = values;
        this.total = total;
        this.from = paging.getFirst();
        this.paging = new Paging(paging.getFirst(), paging.getCount());
    }

    public int getTotal() {
        return total;
    }

    public int getFrom() {
        return from;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }

    public static <T> PartialList<T> emptyList() {
        return EMPTY_LIST;
    }

    @Override
    public T get(int index) {
        return values.get(index);
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }
}

package com.foros.util;

public class OrderedEntry<T> implements Comparable<OrderedEntry> {

    private long order;
    private T entry;

    public OrderedEntry(long order, T entry) {
        this.order = order;
        this.entry = entry;
    }

    public T getEntry() {
        return entry;
    }

    @Override
    public boolean equals(Object o) {
        return this == o || o instanceof OrderedEntry && order == ((OrderedEntry) o).order;
    }

    @Override
    public int hashCode() {
        return (int) (order ^ (order >>> 32));
    }

    @Override
    public int compareTo(OrderedEntry o) {
        return Long.valueOf(order).compareTo(o.order);
    }
}

package com.foros.util.changes;

import com.foros.model.EntityBase;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class ChangesSupportSet<T> extends AbstractSet<T> implements ChangesSupportCollection {

    private EntityBase parent;
    private String propertyName;
    private Set<T> values;

    public static <T> Set<T> wrap(EntityBase parent, String propertyName, Set<T> values) {
        if (values == null) {
            return null;
        }

        return new ChangesSupportSet<T>(parent, propertyName, values);
    }

    public static <T> Set<T> unwrap(Set<T> set) {
        Set<T> res = set;
        while (res instanceof ChangesSupportSet) {
            //noinspection unchecked
            res = ((ChangesSupportSet) res).values;
        }
        return res;
    }

    public ChangesSupportSet(EntityBase parent, String propertyName, Set<T> values) {
        if (values == null) {
            throw new NullPointerException("Values can't be null!");
        }

        this.parent = parent;
        this.propertyName = propertyName;
        this.values = values;
    }

    @Override
    public boolean add(T t) {
        makeItChanged();
        return values.add(t);
    }

    @Override
    public Iterator<T> iterator() {
        return new IteratorWrapper<T>(values.iterator());
    }

    @Override
    public int size() {
        return values.size();
    }

    private void makeItChanged() {
        parent.registerChange(propertyName);
    }

    @Override
    public Collection getOriginalCollection() {
        return values;
    }

    private class IteratorWrapper<T> implements Iterator<T> {

        private Iterator<T> iterator;

        public IteratorWrapper(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public T next() {
            return iterator.next();
        }

        @Override
        public void remove() {
            makeItChanged();
            iterator.remove();
        }
    }
}

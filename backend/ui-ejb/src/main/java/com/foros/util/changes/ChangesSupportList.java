package com.foros.util.changes;

import com.foros.model.EntityBase;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

public class ChangesSupportList<T> extends AbstractList<T> implements ChangesSupportCollection<T> {

    private EntityBase parent;
    private String propertyName;
    private List<T> values;

    public ChangesSupportList(EntityBase parent, String propertyName, List<T> values) {
        if (values == null) {
            throw new NullPointerException("Values can't be null!");
        }

        this.parent = parent;
        this.propertyName = propertyName;
        this.values = values;
    }

    @Override
    public void add(int index, T element) {
        makeItChanged();
        values.add(index, element);
    }

    @Override
    public T set(int index, T element) {
        makeItChanged();
        return values.set(index, element);
    }

    @Override
    public T remove(int index) {
        makeItChanged();
        return values.remove(index);
    }

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public T get(int index) {
        return values.get(index);
    }

    private void makeItChanged() {
        parent.registerChange(propertyName);
    }

    @Override
    public Collection<T> getOriginalCollection() {
        return values;
    }
}

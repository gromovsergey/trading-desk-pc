package com.foros.rs.client.util;

import com.foros.rs.client.model.entity.EntityBase;

import java.util.ArrayList;
import java.util.List;

public class AccumulateResultCallback<E extends EntityBase> implements ResultCallback<E> {

    private List<E> entities = new ArrayList<>();

    @Override
    public void result(List<E> result) {
        entities.addAll(result);
    }

    public List<E> getEntities() {
        return entities;
    }

}

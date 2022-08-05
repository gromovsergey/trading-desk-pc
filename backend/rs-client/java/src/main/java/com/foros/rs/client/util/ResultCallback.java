package com.foros.rs.client.util;

import com.foros.rs.client.model.entity.EntityBase;

import java.util.List;

public interface ResultCallback<E extends EntityBase> {

    void result(List<E> result);

}

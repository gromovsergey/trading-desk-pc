package com.foros.rs.client.util;

import com.foros.rs.client.model.entity.EntityBase;
import com.foros.rs.client.model.operation.Result;

public interface FetchOperationEvents<E extends EntityBase> {

    void onBefore();

    void onResult(Result<E> result);

    void onAfter(int count);

}

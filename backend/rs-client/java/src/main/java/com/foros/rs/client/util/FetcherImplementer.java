package com.foros.rs.client.util;

import com.foros.rs.client.model.entity.EntityBase;
import com.foros.rs.client.model.operation.Result;

public interface FetcherImplementer<S, E extends EntityBase> {

    Result<E> fetch(S selector);

}

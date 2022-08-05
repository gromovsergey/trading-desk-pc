package com.foros.rs.client.util;

import com.foros.rs.client.model.operation.PagingSelector;

public interface PagingAccessor<S> {

    PagingSelector getPaging(S selector);

    S setPaging(S selector, PagingSelector pagingSelector);

}

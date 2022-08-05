package com.foros.rs.client.model;

import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.PagingAccessor;

public class PagingSelectorContainerAccessor<P extends PagingSelectorContainer> implements PagingAccessor<P> {

    @Override
    public PagingSelector getPaging(P selector) {
        return selector.getPaging();
    }

    @Override
    public P setPaging(P selector, PagingSelector pagingSelector) {
        selector.setPaging(pagingSelector);
        return selector;
    }
}

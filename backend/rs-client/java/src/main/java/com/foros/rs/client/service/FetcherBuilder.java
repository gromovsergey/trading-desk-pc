package com.foros.rs.client.service;

import com.foros.rs.client.model.entity.EntityBase;
import com.foros.rs.client.util.Fetcher;

public interface FetcherBuilder<S, E extends EntityBase> {

    Fetcher<S, E> fetcher();

}

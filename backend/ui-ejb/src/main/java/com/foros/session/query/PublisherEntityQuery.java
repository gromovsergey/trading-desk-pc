package com.foros.session.query;

import com.foros.model.Status;

import java.util.Collection;
import java.util.Set;

public interface PublisherEntityQuery<T extends PublisherEntityQuery> extends BusinessQuery {

    T publishers(Collection<Long> publisherIds);

    T statuses(Collection<Status> statuses);

    T nonDeleted();

    T managed(Long userId);

    T restrictByInternalAccountIds(Set<Long> accountIds);

    T restrictByAccountRole();

    T asProperties(String... properties);

    T asNamedTO(String id, String name);

    T addDefaultOrder();

    T restrict();
}
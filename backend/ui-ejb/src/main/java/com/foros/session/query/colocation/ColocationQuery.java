package com.foros.session.query.colocation;

import com.foros.model.Status;
import com.foros.session.query.BusinessQuery;

import java.util.Collection;

public interface ColocationQuery extends BusinessQuery {

    ColocationQuery accounts(Collection<Long> accountIds);

    ColocationQuery colocations(Collection<Long> colocationIds);

    ColocationQuery statuses(Collection<Status> statuses);

    ColocationQuery name(String name);

    ColocationQuery addDefaultOrder();

    ColocationQuery restrict();

    ColocationQuery managed(Long userId);

    ColocationQuery restrictByInternalAccountIds(Collection<Long> accountIds);
}

package com.foros.session.query.action;

import com.foros.session.query.AdvertiserEntityQuery;

import java.util.Collection;

public interface ActionQuery extends AdvertiserEntityQuery<ActionQuery> {

    ActionQuery actions(Collection<Long> ids);

}

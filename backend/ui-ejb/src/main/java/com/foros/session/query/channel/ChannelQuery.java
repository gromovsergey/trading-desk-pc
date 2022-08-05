package com.foros.session.query.channel;

import com.foros.model.DisplayStatus;
import com.foros.model.channel.Channel;
import com.foros.session.channel.ChannelVisibilityCriteria;
import com.foros.session.query.BusinessQuery;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ChannelQuery<T extends ChannelQuery> extends BusinessQuery {

    T name(String name);

    T account(Long id);

    T accounts(Collection<Long> id);

    T channel(Long id);

    T channels(List<Long> id);

    T country(String code);

    T displayStatus(DisplayStatus... statuses);

    T visibility(ChannelVisibilityCriteria visibilityCriteria);

    T notDeleted();

    T matchedIds(List<Long> ids);

    T asBean();

    T asTO();

    T nameWithEscape(String name);

    T existingByName(Set<Channel> channels);

    T asNamedTO(String id, String name);

    T countries(List<String> codes);
}

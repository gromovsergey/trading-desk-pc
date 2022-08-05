package com.foros.session.query.channel;

import com.foros.model.Identifiable;
import com.foros.session.query.AbstractEntityTransformer;
import com.foros.util.CollectionUtils;
import com.foros.util.bean.Filter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class DistinctChannelTOTransformer<T extends Identifiable> extends AbstractEntityTransformer<T> {
    @SuppressWarnings("unchecked")
    @Override
    public List<T> transformList(List list) {
        return distinctList((List<T>)list);
    }

    protected List<T> distinctList(List<T> list) {
        final List<T> result = new ArrayList<T>(list);

        CollectionUtils.filter(result, new Filter<T>() {
            private Set<Long> channelIds = new HashSet<Long>(result.size());

            @Override
            public boolean accept(T channelTO) {
                return channelIds.add(channelTO.getId());
            }
        });

        return result;
    }
}

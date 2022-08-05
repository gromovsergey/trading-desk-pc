package com.foros.session.query.channel;

import com.foros.session.query.SqlOrder;
import com.foros.util.StringUtil;

import java.util.Set;

import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;

public class DiscoverChannelListsQueryImpl
        extends AbstractChannelQuery<DiscoverChannelListsQuery> implements DiscoverChannelListsQuery {

    public DiscoverChannelListsQueryImpl() {
        super();
        getCriteria()
                .add(Restrictions.in("this.class", new String[]{"L"}));
    }

    @Override
    protected ResultTransformer createTOTransformer() {
        return new DiscoverChannelListsTOTransformer();
    }

    @Override
    protected ProjectionList createTOProjections() {
        return createDefaultTOProjections();
    }

    @Override
    public DiscoverChannelListsQuery orderByName() {
       getCriteria()
                .addOrder(SqlOrder.asc("this.name"));

        return this;
    }

    @Override
    public DiscoverChannelListsQuery language(String language) {
        if (StringUtil.isPropertyNotEmpty(language)) {
            if (!"none".equals(language)) {
                getCriteria().add(Restrictions.eq("language", language));
            } else {
                getCriteria().add(Restrictions.isNull("language"));
            }
        }

        return self();

    }
    
    @Override
    public DiscoverChannelListsQuery restrictByAccountIds(Set<Long> accessAccountIds) {
        getCriteria()
            .add(Restrictions.in("account.id", accessAccountIds)
        );
        return self();
    }

}

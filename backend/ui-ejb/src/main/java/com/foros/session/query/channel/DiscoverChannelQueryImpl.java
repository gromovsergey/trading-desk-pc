package com.foros.session.query.channel;

import java.util.Set;
import org.hibernate.FetchMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;

public class DiscoverChannelQueryImpl
        extends AbstractChannelQuery<DiscoverChannelQuery> implements DiscoverChannelQuery {

    public DiscoverChannelQueryImpl() {
        super();
        getCriteria()
                .add(Restrictions.in("this.class", new String[]{"D"}))
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
                
        getCriteria()
                .getDataCriteria()
                .createAlias("categories", "categories", CriteriaSpecification.LEFT_JOIN);
        
    }

    @Override
    protected ResultTransformer createTOTransformer() {
        throw new UnsupportedOperationException("DiscoverChannelQueryImpl.createTOTransformer() not supported!");
    }

    @Override
    protected ProjectionList createTOProjections() {
        throw new UnsupportedOperationException("DiscoverChannelQueryImpl.createTOProjections() not supported!");
    }

    @Override
    public DiscoverChannelQuery restrictByInternalAccountIds(Set<Long> accessAccountIds) {
        getCriteria()
            .add(Restrictions.in("account.id", accessAccountIds)
        );
        return self();
    }
}

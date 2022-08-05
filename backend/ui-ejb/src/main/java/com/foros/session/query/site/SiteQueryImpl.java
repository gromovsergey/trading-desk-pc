package com.foros.session.query.site;

import com.foros.model.site.Site;
import com.foros.session.query.PublisherEntityQueryImpl;
import com.foros.session.query.criteria.CompositeCriteria;
import com.foros.session.query.criteria.PaginationCriteria;
import com.foros.session.query.criterion.AnyCriterion;

import java.util.Collection;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

public class SiteQueryImpl extends PublisherEntityQueryImpl<SiteQuery> implements SiteQuery {

    private static PaginationCriteria createCriteria() {
        return new CompositeCriteria.Builder()
                .dataCriteria(prepareCriteria())
                .build();
    }

    private static DetachedCriteria prepareCriteria() {
        return DetachedCriteria
                .forClass(Site.class)
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                .createAlias("account", "account");
    }

    public SiteQueryImpl() {
        super(createCriteria());
    }

    @Override
    public SiteQuery sites(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("this.id", ids));
        }
        return this;
    }

    @Override
    public SiteQuery addDefaultOrder() {
        getCriteria().addOrder(Order.asc("account.id")).addOrder(Order.asc("this.id"));
        return self();
    }

}

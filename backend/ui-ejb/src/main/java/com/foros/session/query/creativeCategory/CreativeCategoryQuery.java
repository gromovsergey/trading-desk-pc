package com.foros.session.query.creativeCategory;

import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.session.query.BusinessQueryImpl;
import com.foros.session.query.criteria.CompositeCriteria;
import com.foros.session.query.criteria.PaginationCriteria;
import com.foros.session.query.criterion.AnyCriterion;

import java.util.Collection;
import org.hibernate.FetchMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class CreativeCategoryQuery extends BusinessQueryImpl {

    public CreativeCategoryQuery() {
        super(createCriteria());
    }

    private static DetachedCriteria prepareCriteria() {
        return DetachedCriteria.forClass(CreativeCategory.class);
    }

    private static DetachedCriteria prepareDataCriteria() {
        return DetachedCriteria.forClass(CreativeCategory.class)
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                .setFetchMode("rtbCategories", FetchMode.JOIN)
                .setFetchMode("rtbCategories.rtbConnector", FetchMode.JOIN);
    }

    private static PaginationCriteria createCriteria() {
        return new CompositeCriteria.Builder()
                .idCriteria(prepareCriteria())
                .countCriteria(prepareCriteria())
                .dataCriteria(prepareDataCriteria())
                .build();
    }

    public CreativeCategoryQuery categories(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("this.id", ids));
        }
        return this;
    }

    public CreativeCategoryQuery type(CreativeCategoryType type) {
        if (type != null) {
            getCriteria().add(Restrictions.eq("this.type", type));
        }
        return this;
    }

    public CreativeCategoryQuery addDefaultOrder() {
        getCriteria().addOrder(Order.asc("this.defaultName"));
        return this;
    }
}

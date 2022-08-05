package com.foros.session.query.campaign;

import com.foros.model.Status;
import com.foros.model.creative.Creative;
import com.foros.session.query.AdvertiserEntityQueryImpl;
import com.foros.session.query.criteria.CompositeCriteria;
import com.foros.session.query.criteria.PaginationCriteria;
import com.foros.session.query.criterion.AnyCriterion;

import java.util.Collection;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;

public class YandexCreativeQuery extends AdvertiserEntityQueryImpl<YandexCreativeQuery> {

    public YandexCreativeQuery() {
        super(createCriteria());
    }

    private static PaginationCriteria createCriteria() {
        DetachedCriteria criteria = DetachedCriteria
            .forClass(Creative.class)
            .setProjection(createProjections())
            .setResultTransformer(new YandexCreativeTransformer())
            .createAlias("account", "account");

        return new CompositeCriteria.Builder()
                .dataCriteria(criteria)
                .build();
    }

    private static ProjectionList createProjections() {
        return Projections.projectionList()
            .add(Projections.id().as("id"))
            .add(Projections.property("template.id").as("templateId"))
            .add(Projections.property("account.id").as("accountId"))
            .add(Projections.property("tnsBrand.id").as("tnsBrandId"));
    }

    public YandexCreativeQuery creatives(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria()
                .add(AnyCriterion.anyId("this.id", ids));
        }

        return this;
    }

    @Override
    public YandexCreativeQuery addDefaultOrder() {
        getCriteria().addOrder(Order.asc("id"));
        return this;
    }

    @Override
    public YandexCreativeQuery statuses(Collection<Status> statuses) {
        return this;
    }


}

package com.foros.session.query.campaign;

import com.foros.model.Status;
import com.foros.model.creative.Creative;
import com.foros.session.query.AdvertiserEntityQueryImpl;
import com.foros.session.query.criteria.CompositeCriteria;
import com.foros.session.query.criteria.PaginationCriteria;
import com.foros.session.query.criterion.AnyCriterion;

import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

public class CreativeQuery extends AdvertiserEntityQueryImpl<CreativeQuery> {

    public CreativeQuery() {
        super(createCriteria());
    }

    private static DetachedCriteria prepareCriteria() {
        return DetachedCriteria.forClass(Creative.class)
                .setProjection(createProjections())
                .setResultTransformer(new CreativeTransformer())
                .createAlias("account", "account");
    }

    private static PaginationCriteria createCriteria() {
        return new CompositeCriteria.Builder()
                .dataCriteria(prepareCriteria())
                .countCriteria(prepareCriteria())
                .build();
    }

    private static ProjectionList createProjections() {
        return Projections.projectionList()
                .add(Projections.id().as("id"))
                .add(Projections.property("name").as("name"))
                .add(Projections.property("size.id").as("sizeId"))
                .add(Projections.property("template.id").as("templateId"))
                .add(Projections.property("version").as("version"))
                .add(Projections.property("status").as("status"))
                .add(Projections.property("qaStatus").as("qaStatus"))
                .add(Projections.property("displayStatusId").as("displayStatusId"))
                .add(Projections.property("flags").as("flags"))
                .add(Projections.property("qaUser.id").as("qaUserId"))
                .add(Projections.property("qaDate").as("qaDate"))
                .add(Projections.property("qaDescription").as("qaDescription"))
                .add(Projections.property("account.id").as("accountId"));
    }

    public CreativeQuery creatives(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria()
                    .add(AnyCriterion.anyId("this.id", ids));
        }

        return this;
    }

    @Override
    public CreativeQuery addDefaultOrder() {
        getCriteria().addOrder(Order.asc("id"));
        return this;
    }

    @Override
    public CreativeQuery statuses(Collection<Status> statuses) {
        if (statuses != null && !statuses.isEmpty()) {
            getCriteria()
                    .add(Restrictions.in("status", Status.getStatusCodes(statuses)));
        }

        return this;
    }

    public CreativeQuery sizes(Collection<Long> sizes) {
        if (sizes != null && !sizes.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("size.id", sizes));
        }

        return this;
    }

    public CreativeQuery excludeSizes(Collection<Long> sizes) {
        if (sizes != null && !sizes.isEmpty()) {
            getCriteria().add(Restrictions.not(AnyCriterion.anyId("size.id", sizes)));
        }

        return this;
    }

    public CreativeQuery templates(List<Long> templates) {
        if (templates != null && !templates.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("template.id", templates));
        }

        return this;
    }

    public CreativeQuery excludeTemplates(List<Long> templates) {
        if (templates != null && !templates.isEmpty()) {
            getCriteria().add(Restrictions.not(AnyCriterion.anyId("template.id", templates)));
        }

        return this;
    }

}

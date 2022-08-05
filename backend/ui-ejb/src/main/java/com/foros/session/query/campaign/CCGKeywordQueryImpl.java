package com.foros.session.query.campaign;

import com.foros.model.Status;
import com.foros.model.campaign.CCGKeyword;
import com.foros.session.query.AdvertiserEntityQueryImpl;
import com.foros.session.query.criteria.CompositeCriteria;
import com.foros.session.query.criteria.PaginationCriteria;
import com.foros.session.query.criterion.AnyCriterion;

import java.util.Collection;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class CCGKeywordQueryImpl extends AdvertiserEntityQueryImpl<CCGKeywordQuery> implements CCGKeywordQuery {

    public CCGKeywordQueryImpl() {
        super(createCriteria());
    }

    private static DetachedCriteria prepareCriteria() {
        return DetachedCriteria.forClass(CCGKeyword.class)
                .createAlias("creativeGroup", "creativeGroup")
                .createAlias("creativeGroup.campaign", "campaign")
                .createAlias("creativeGroup.campaign.account", "account");
    }

    private static PaginationCriteria createCriteria() {
        return new CompositeCriteria.Builder()
                .dataCriteria(prepareCriteria())
                .countCriteria(prepareCriteria())
                .build();
    }

    @Override
    public CCGKeywordQuery campaigns(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria()
                    .add(AnyCriterion.anyId("campaign.id", ids));
        }

        return this;
    }

    @Override
    public CCGKeywordQuery creativeGroups(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria()
                    .add(AnyCriterion.anyId("this.creativeGroup.id", ids));
        }

        return this;
    }

    @Override
    public CCGKeywordQuery keywords(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria()
                    .add(AnyCriterion.anyId("this.id", ids));
        }

        return this;
    }

    @Override
    public CCGKeywordQuery existingByKeyword(List<CCGKeyword> keywords) {
        Disjunction disjunction = Restrictions.disjunction();
        for (CCGKeyword keyword : keywords) {
            LogicalExpression expression = Restrictions.and(
                    Restrictions.eq("this.creativeGroup.id", keyword.getCreativeGroup().getId()),
                    Restrictions.eq("this.originalKeyword", keyword.getOriginalKeyword())
            );
            disjunction.add(expression);
        }
        getCriteria().add(disjunction);

        return this;
    }

    @Override
    public CCGKeywordQuery addDefaultOrder() {
        getCriteria().addOrder(Order.asc("originalKeyword").ignoreCase()).addOrder(Order.asc("this.id"));
        return this;
    }

    @Override
    public CCGKeywordQuery nonDeleted() {
        super.nonDeleted();
        getCriteria()
                .add(Restrictions.ne("campaign.status", Status.DELETED.getLetter()));
        getCriteria()
                .add(Restrictions.ne("creativeGroup.status", Status.DELETED.getLetter()));

        return this;
    }
}

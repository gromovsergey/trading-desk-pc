package com.foros.session.query.action;

import com.foros.model.action.Action;
import com.foros.session.query.AdvertiserEntityQueryImpl;
import com.foros.session.query.criteria.CompositeCriteria;
import com.foros.session.query.criteria.PaginationCriteria;
import com.foros.session.query.criterion.AnyCriterion;

import java.util.Collection;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class ActionQueryImpl extends AdvertiserEntityQueryImpl<ActionQuery> implements ActionQuery {

    public ActionQueryImpl() {
        super(createCriteria());
    }

    private static DetachedCriteria prepareCriteria() {
        return DetachedCriteria
            .forClass(Action.class)
            .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
            .createAlias("account", "account");
    }

    private static PaginationCriteria createCriteria() {
        return new CompositeCriteria.Builder()
                .dataCriteria(prepareCriteria())
                .countCriteria(prepareCriteria())
                .build();
    }

    @Override
    public ActionQuery addDefaultOrder() {
        getCriteria().addOrder(Order.asc("id"));
        return this;
    }

    @Override
    public ActionQuery actions(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("this.id", ids));
        }
        return this;
    }

    public ActionQuery existingByName(Collection<Action> actions) {
        Disjunction disjunction = Restrictions.disjunction();
        for (Action action : actions) {
            LogicalExpression expression = Restrictions.and(
                Restrictions.eq("this.account.id", action.getAccount().getId()),
                Restrictions.eq("this.name", action.getName())
                );
            disjunction.add(expression);
        }
        getCriteria().add(disjunction);

        return this;
    }

}

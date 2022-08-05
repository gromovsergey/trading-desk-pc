package com.foros.session.query.colocation;

import com.foros.model.Status;
import com.foros.model.isp.Colocation;
import com.foros.session.CurrentUserService;
import com.foros.session.ServiceLocator;
import com.foros.session.query.BusinessQueryImpl;
import com.foros.session.query.criteria.CompositeCriteria;
import com.foros.session.query.criteria.PaginationCriteria;
import com.foros.session.query.criterion.AnyCriterion;
import com.foros.util.CollectionUtils;

import java.util.Collection;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

public class ColocationQueryImpl extends BusinessQueryImpl implements ColocationQuery {

    public ColocationQueryImpl() {
        super(createCriteria());
    }

    private static PaginationCriteria createCriteria() {
        return new CompositeCriteria.Builder()
                .dataCriteria(createDefaultCampaignCreativeCriteria())
                .build();
    }

    private static DetachedCriteria createDefaultCampaignCreativeCriteria() {
        return DetachedCriteria
                .forClass(Colocation.class)
                .createAlias("this.account", "account")
                .setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
    }

    @Override
    public ColocationQueryImpl accounts(Collection<Long> accountIds) {
        if (!CollectionUtils.isNullOrEmpty(accountIds)) {
            getCriteria()
                    .add(AnyCriterion.anyId("account.id", accountIds));
        }

        return this;
    }

    @Override
    public ColocationQueryImpl colocations(Collection<Long> colocationIds) {
        if (!CollectionUtils.isNullOrEmpty(colocationIds)) {
            getCriteria()
                    .add(AnyCriterion.anyId("this.id", colocationIds));
        }

        return this;
    }

    @Override
    public ColocationQueryImpl statuses(Collection<Status> statuses) {
        if (!CollectionUtils.isNullOrEmpty(statuses)) {
            getCriteria()
                    .add(Restrictions.in("this.status", Status.getStatusCodes(statuses)));
        }

        return this;
    }

    @Override
    public ColocationQueryImpl name(String name) {
        if (name != null) {
            getCriteria()
                    .add(Restrictions.eq("this.name", name));
        }

        return this;
    }

    @Override
    public ColocationQueryImpl addDefaultOrder() {
        getCriteria().addOrder(Order.asc("account.id")).addOrder(Order.asc("id"));
        return this;
    }

    @Override
    public ColocationQueryImpl restrict() {
        CurrentUserService currentUserService = ServiceLocator.getInstance().lookup(CurrentUserService.class);

        if (currentUserService.isISPAccountManager()) {
            managed(currentUserService.getUserId());
        }

        if (currentUserService.isInternalWithRestrictedAccess()) {
            restrictByInternalAccountIds(currentUserService.getAccessAccountIds());
        }

        return this;
    }

    @Override
    public ColocationQueryImpl managed(Long userId) {
        if (userId != null) {
            getCriteria()
                    .add(Restrictions.eq("account.accountManager.id", userId));
        }

        return this;
    }

    @Override
    public ColocationQueryImpl restrictByInternalAccountIds(Collection<Long> accountIds) {
        if (!CollectionUtils.isNullOrEmpty(accountIds)) {
            getCriteria()
                    .add(AnyCriterion.anyId("account.internalAccount.id", accountIds));
        }
        return this;
    }
}

package com.foros.session.query;

import com.foros.model.Status;
import com.foros.security.AccountRole;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.session.CurrentUserService;
import com.foros.session.ServiceLocator;
import com.foros.session.bulk.IdNameTO;
import com.foros.session.query.criteria.PaginationCriteria;
import com.foros.session.query.criterion.AnyCriterion;
import com.foros.util.EntityUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;

public abstract class AdvertiserEntityQueryImpl<T extends AdvertiserEntityQuery>
        extends BusinessQueryImpl implements AdvertiserEntityQuery<T> {

    public AdvertiserEntityQueryImpl(PaginationCriteria criteria) {
        super(criteria
            .createAlias("account.agency", "agency", CriteriaSpecification.LEFT_JOIN)
            .createAlias("account.accountManager", "accountManager", CriteriaSpecification.LEFT_JOIN)
            .createAlias("agency.accountManager", "agencyAccountManager", CriteriaSpecification.LEFT_JOIN));
    }

    @Override
    public T agency(Long accountId) {
        if (accountId != null) {
            getCriteria()
                .add(Restrictions.eq("agency.id", accountId));
        }

        return self();
    }

    @Override
    public T advertisers(Collection<Long> advertiserIds) {
        if (advertiserIds != null && !advertiserIds.isEmpty()) {
            getCriteria()
                .add(AnyCriterion.anyId("account.id", advertiserIds));
        }

        return self();
    }

    @Override
    public T statuses(Collection<Status> statuses) {
        if (statuses != null && !statuses.isEmpty()) {
            getCriteria()
                .add(Restrictions.in("this.status", Status.getStatusCodes(statuses)));
        }

        return self();
    }

    @Override
    public T nonDeleted() {
        getCriteria()
            .add(Restrictions.ne("this.status", Status.DELETED.getLetter()))
            .add(Restrictions.ne("account.status", Status.DELETED.getLetter()));

        return self();
    }

    @Override
    public T managed(Long userId) {
        if (userId != null) {
            getCriteria()
                .add(Restrictions.or(
                    Restrictions.eq("accountManager.id", userId),
                    Restrictions.eq("agencyAccountManager.id", userId)
                    ));
        }

        return self();
    }

    @Override
    public T restrictByInternalAccountIds(Set<Long> accountIds) {
        getCriteria()
            .add(AnyCriterion.anyId("account.internalAccount.id", accountIds));
        return self();
    }

    @Override
    public T restrictByAccountRole() {
        ApplicationPrincipal principal = SecurityContext.getPrincipal();
        AccountRole accountRole = SecurityContext.getAccountRole();

        switch (accountRole) {
            case AGENCY:
                nonDeleted();
                agency(principal.getAccountId());
                break;
            case ADVERTISER:
                nonDeleted();
                advertisers(Arrays.asList(principal.getAccountId()));
                break;
            case INTERNAL:
                // no restrictions
                break;
            default:
                throw new IllegalArgumentException("Role is unsupported " + accountRole);
        }

        return self();
    }

    @Override
    public T asProperties(String... properties) {
        ProjectionList projectionList = Projections.projectionList();
        for (String property : properties) {
            projectionList.add(Projections.property(property));
        }
        getCriteria().setProjection(projectionList);
        return self();
    }

    @Override
    public T asNamedTO(String id, String name) {
        getCriteria().setProjection(
            Projections.projectionList()
                .add(Projections.property(id))
                .add(Projections.property(name))
            );
        getCriteria().setResultTransformer(new AliasToBeanConstructorResultTransformer(IdNameTO.CONSTRUCTOR));

        return self();
    }

    @Override
    public T addDefaultOrder() {
        getCriteria().addOrder(Order.asc("name").ignoreCase()).addOrder(Order.asc("id"));
        return self();
    }

    @Override
    public T restrict() {
        CurrentUserService currentUserService = ServiceLocator.getInstance().lookup(CurrentUserService.class);

        restrictByAccountRole();

        if (currentUserService.isAdvertiserAccountManager()) {
            managed(currentUserService.getUserId());
        }

        if (currentUserService.isInternalWithRestrictedAccess()) {
            restrictByInternalAccountIds(currentUserService.getAccessAccountIds());
        }

        if (currentUserService.isAdvertiserLevelRestricted()) {
            advertisers(EntityUtils.getEntityIds(currentUserService.getUser().getAdvertisers()));
        }

        return self();
    }

    @SuppressWarnings({ "unchecked" })
    protected T self() {
        return (T) this;
    }

}

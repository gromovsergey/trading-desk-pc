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

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;

public class PublisherEntityQueryImpl<T extends PublisherEntityQuery>
        extends BusinessQueryImpl implements PublisherEntityQuery<T> {

    public PublisherEntityQueryImpl(PaginationCriteria criteria) {
        super(criteria.createAlias("account.accountManager", "accountManager", CriteriaSpecification.LEFT_JOIN));
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
                .add(Restrictions.eq("accountManager.id", userId));
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

        if (accountRole == AccountRole.PUBLISHER) {
            publishers(Arrays.asList(principal.getAccountId()));
        }

        if (accountRole != AccountRole.INTERNAL) {
            nonDeleted();
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

        if (currentUserService.isPublisherAccountManager()) {
            managed(currentUserService.getUserId());
        }

        if (currentUserService.isInternalWithRestrictedAccess()) {
            restrictByInternalAccountIds(currentUserService.getAccessAccountIds());
        }

        return self();
    }

    @SuppressWarnings({ "unchecked" })
    protected T self() {
        return (T) this;
    }

    @Override
    public T publishers(Collection<Long> publisherIds) {
        if (publisherIds != null && !publisherIds.isEmpty()) {
            getCriteria()
                .add(AnyCriterion.anyId("account.id", publisherIds));
        }

        return self();
    }
}

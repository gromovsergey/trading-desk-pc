package com.foros.session.query.channel;

import com.foros.model.channel.ChannelVisibility;
import com.foros.persistence.hibernate.criterion.SQLWithPropertyNamesCriterion;
import com.foros.security.AccountRole;
import com.foros.session.channel.ChannelVisibilityCriteria;
import com.foros.session.channel.service.AdvertisingChannelType;
import com.foros.session.query.criterion.AnyCriterion;
import org.hibernate.FetchMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;


public class AdvertisingChannelQueryImpl
        extends AbstractChannelQuery<AdvertisingChannelQuery> implements AdvertisingChannelQuery {

    public AdvertisingChannelQueryImpl() {
        super();
        getCriteria()
                .add(Restrictions.in("this.class", new String[] {"E", "B", "A"}))
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);

        getCriteria().setFetchMode("categories", FetchMode.JOIN);
    }

    @Override
    protected ResultTransformer createTOTransformer() {
        return new ChannelTOTransformer();
    }

    @Override
    public AdvertisingChannelQuery managedOrPublic(Long userId, Long accountId) {
        getCriteria()
                .add(Restrictions.or(
                        Restrictions.or(Restrictions.eq("account.accountManager.id", userId), Restrictions.eq("account.id", accountId)),
                        Restrictions.in("this.visibility", ChannelVisibilityCriteria.NON_PRIVATE.getVisibilities())
                ));

        return this;
    }

    @Override
    public AdvertisingChannelQuery restrictedByAccountIdsOrPublic(Set<Long> accountIds) {
        getCriteria()
            .add(Restrictions.or(
                            Restrictions.or(
                                    AnyCriterion.anyId("account.internalAccount.id", accountIds),
                                    Restrictions.in("this.visibility", ChannelVisibilityCriteria.NON_PRIVATE.getVisibilities())),
                            Restrictions.or(
                                    AnyCriterion.anyId("account.id", accountIds),
                                    Restrictions.in("this.visibility", ChannelVisibilityCriteria.NON_PRIVATE.getVisibilities())))
            );
        return this;
    }

    @Override
    public AdvertisingChannelQuery ownedOrPublic(Long accountId) {
        getCriteria()
                .add(Restrictions.or(
                        Restrictions.eq("account.id", accountId),
                        Restrictions.in("this.visibility", ChannelVisibilityCriteria.NON_PRIVATE.getVisibilities())
                ));

        return this;
    }

    @Override
    public AdvertisingChannelQuery byAccountRolesOrPublic(List<AccountRole> roles) {
        getCriteria()
                .add(Restrictions.or(
                        Restrictions.in("account.role", roles),
                        Restrictions.in("this.visibility", ChannelVisibilityCriteria.NON_PRIVATE.getVisibilities())
                ));

        return this;
    }

    @Override
    public AdvertisingChannelQuery type(Collection<AdvertisingChannelType> types) {
        if (types != null && types.size() > 0) {
            getCriteria().add(Restrictions.in("this.class", AdvertisingChannelType.aliases(types)));
        }

        return this;
    }

    @Override
    public AdvertisingChannelQuery visibility(Collection<ChannelVisibility> visibility) {
        if (!CollectionUtils.isEmpty(visibility)) {
            getCriteria().add(Restrictions.in("this.visibility", visibility));
        }

        return self();
    }

    @Override
    public AdvertisingChannelQuery excludeTestAccounts() {
        getCriteria().add(
                new SQLWithPropertyNamesCriterion("MOD(account.flags, 2) = 0", new String[] { "account.flags" }));
        return this;

    }

    @Override
    public AdvertisingChannelQuery onlyTestAccounts() {
        getCriteria().add(
                new SQLWithPropertyNamesCriterion("MOD(account.flags, 2) = 1", new String[] { "account.flags" }));
        return this;
    }

    @Override
    public AdvertisingChannelQuery hasCategoryChannel(Long categoryChannelId) {
        if (categoryChannelId != null) {
            getCriteria()
                    .createAlias("this.categories", "categoryChannel", CriteriaSpecification.LEFT_JOIN)
                    .add(Restrictions.eq("categoryChannel.id", categoryChannelId));
        }
        return this;
    }

    @Override
    public AdvertisingChannelQuery orderByName() {
        getCriteria()
                .addOrder(Order.asc("account.name").ignoreCase())
                .addOrder(Order.asc("this.name").ignoreCase());
        return this;
    }

    @Override
    protected ProjectionList createTOProjections() {
        return createDefaultTOProjections();
    }
}

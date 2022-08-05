package com.foros.session.query.campaign;

import com.foros.model.campaign.CampaignCreative;
import com.foros.session.query.AdvertiserEntityQueryImpl;
import com.foros.session.query.criteria.CompositeCriteria;
import com.foros.session.query.criteria.PaginationCriteria;
import com.foros.session.query.criterion.AnyCriterion;

import java.util.Collection;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;

public class CampaignCreativeQuery extends AdvertiserEntityQueryImpl<CampaignCreativeQuery> {

    public CampaignCreativeQuery() {
        super(createCriteria());
    }

    private static PaginationCriteria createCriteria() {
        return new CompositeCriteria.Builder()
                .dataCriteria(createDefaultCampaignCreativeCriteria())
                .countCriteria(createDefaultCampaignCreativeCriteria())
                .build();
    }

    private static DetachedCriteria createDefaultCampaignCreativeCriteria() {
        return DetachedCriteria
                .forClass(CampaignCreative.class)
                .createAlias("creative", "creative")
                .createAlias("creativeGroup", "creativeGroup")
                .createAlias("creativeGroup.campaign", "campaign")
                .createAlias("creativeGroup.campaign.account", "account")
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
    }

    public CampaignCreativeQuery campaigns(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("creativeGroup.campaign.id", ids));
        }
        return this;
    }

    public CampaignCreativeQuery creativeGroups(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("creativeGroup.id", ids));
        }
        return this;
    }

    public CampaignCreativeQuery creatives(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("creative.id", ids));
        }
        return this;
    }

    public CampaignCreativeQuery campaignCreatives(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("this.id", ids));
        }
        return this;
    }

    @Override
    public CampaignCreativeQuery addDefaultOrder() {
        getCriteria().addOrder(Order.asc("id"));
        return this;
    }
}

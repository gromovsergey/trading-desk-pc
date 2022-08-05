package com.foros.session.query.campaign;

import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignType;
import com.foros.session.query.AdvertiserEntityQueryImpl;
import com.foros.session.query.criteria.CompositeCriteria;
import com.foros.session.query.criteria.PaginationCriteria;
import com.foros.session.query.criterion.AnyCriterion;

import java.util.Collection;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;

public class CampaignQueryImpl extends AdvertiserEntityQueryImpl<CampaignQuery> implements CampaignQuery {

    public CampaignQueryImpl() {
        super(createCriteria());
    }

    private static DetachedCriteria prepareCriteria() {
        return DetachedCriteria
                .forClass(Campaign.class)
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY)
                .createAlias("account", "account");
    }

    private static PaginationCriteria createCriteria() {
        PaginationCriteria paginationCriteria =
                new CompositeCriteria.Builder()
                        .dataCriteria(prepareCriteria())
                        .idCriteria(prepareCriteria())
                        .countCriteria(prepareCriteria())
                        .build();
        paginationCriteria.getDataCriteria()
                .createAlias("campaignSchedules", "campaignSchedules", CriteriaSpecification.LEFT_JOIN)
                .createAlias("excludedChannels", "excludedChannels", CriteriaSpecification.LEFT_JOIN);
        return paginationCriteria;
    }

    @Override
    public CampaignQuery campaigns(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("this.id", ids));
        }
        return this;
    }

    @Override
    public CampaignQuery type(CampaignType campaignType) {
        if (campaignType != null) {
            getCriteria().add(Restrictions.eq("this.campaignType", campaignType));
        }
        return this;
    }

    @Override
    public CampaignQuery existingByName(Collection<Campaign> campaigns) {
        Disjunction disjunction = Restrictions.disjunction();
        for (Campaign campaign : campaigns) {
            LogicalExpression expression = Restrictions.and(
                    Restrictions.eq("this.account.id", campaign.getAccount().getId()),
                    Restrictions.eq("this.name", campaign.getName())
            );
            disjunction.add(expression);
        }
        getCriteria().add(disjunction);

        return this;
    }
}

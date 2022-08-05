package com.foros.session.query.campaign;

import com.foros.model.Status;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignType;
import com.foros.model.campaign.TGTType;
import com.foros.session.query.AdvertiserEntityQueryImpl;
import com.foros.session.query.criteria.CompositeCriteria;
import com.foros.session.query.criteria.PaginationCriteria;
import com.foros.session.query.criterion.AnyCriterion;

import java.util.Collection;

import org.hibernate.FetchMode;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Restrictions;

public class CampaignCreativeGroupQueryImpl extends AdvertiserEntityQueryImpl<CampaignCreativeGroupQuery>
        implements CampaignCreativeGroupQuery {

    public CampaignCreativeGroupQueryImpl() {
        super(createCriteria());
    }

    private static DetachedCriteria prepareCriteria() {
        return DetachedCriteria
                .forClass(CampaignCreativeGroup.class)
                .createAlias("campaign", "campaign")
                .createAlias("campaign.account", "account")
                .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
    }

    private static PaginationCriteria createCriteria() {
        PaginationCriteria paginationCriteria = new CompositeCriteria.Builder()
                .dataCriteria(prepareCriteria())
                .idCriteria(prepareCriteria())
                .countCriteria(prepareCriteria())
                .build();
        paginationCriteria
                .getDataCriteria()
                .createAlias("ccgSchedules", "ccgSchedules", CriteriaSpecification.LEFT_JOIN)
                .createAlias("actions", "actions", CriteriaSpecification.LEFT_JOIN)
                .setFetchMode("deviceChannels", FetchMode.JOIN);
        return paginationCriteria;
    }

    @Override
    public CampaignCreativeGroupQuery campaigns(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("this.campaign.id", ids));
        }
        return this;
    }

    @Override
    public CampaignCreativeGroupQuery actions(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("this.actions.id", ids));
        }
        return this;
    }

    @Override
    public CampaignCreativeGroupQuery creativeGroups(Collection<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            getCriteria().add(AnyCriterion.anyId("this.id", ids));
        }
        return this;
    }

    @Override
    public CampaignCreativeGroupQuery geoChannels() {
        getCriteria().setFetchMode("geoChannels", FetchMode.JOIN);
        return this;
    }

    @Override
    public CampaignCreativeGroupQuery geoChannelsExcluded() {
        getCriteria().setFetchMode("geoChannelsExcluded", FetchMode.JOIN);
        return this;
    }

    @Override
    public CampaignCreativeGroupQuery colocations() {
        getCriteria().setFetchMode("colocations", FetchMode.JOIN);
        return this;
    }

    @Override
    public CampaignCreativeGroupQuery sites() {
        getCriteria().setFetchMode("sites", FetchMode.JOIN);
        return this;
    }

    @Override
    public CampaignCreativeGroupQuery type(CampaignType campaignType) {
        if (campaignType != null) {
            getCriteria().add(Restrictions.eq("this.ccgType", CCGType.valueOf(campaignType).getLetter()));
        }
        return this;
    }

    @Override
    public CampaignCreativeGroupQuery keyword() {
        getCriteria().add(Restrictions.eq("this.tgtType", TGTType.KEYWORD.getLetter()));
        return this;
    }

    public CampaignCreativeGroupQuery existingByName(Collection<CampaignCreativeGroup> groups) {
        Disjunction disjunction = Restrictions.disjunction();
        for (CampaignCreativeGroup group : groups) {
            LogicalExpression expression;
            expression = Restrictions.and(
                    Restrictions.eq("this.campaign.id", group.getCampaign().getId()),
                    Restrictions.eq("name", group.getName())
            );
            disjunction.add(expression);
        }
        getCriteria().add(disjunction);

        return this;
    }

    public CampaignCreativeGroupQuery deviceChannel(Long id) {
        getCriteria()
            .createAlias("this.deviceChannels", "deviceChannel")
            .add(Restrictions.eq("deviceChannel.id", id));
        return this;
    }

    @Override
    public CampaignCreativeGroupQuery nonDeleted() {
        super.nonDeleted();
        getCriteria().add(Restrictions.ne("campaign.status", Status.DELETED.getLetter()));
        return this;
    }
}

package com.foros.session.campaign;

import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.session.CurrentUserService;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
public class CreativeGroupRestrictions {
    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @EJB
    private CurrentUserService currentUserService;

    @Restriction
    public boolean canUpdateChannelTarget(CampaignCreativeGroup group) {
        return canEditGroup(group);
    }

    @Restriction
    public boolean canViewUserSampleGroups(CampaignCreativeGroup group) {
        return currentUserService.isInternal() && canViewGroup(group);
    }

    @Restriction
    public boolean canUpdateUserSampleGroups(CampaignCreativeGroup group) {
        return currentUserService.isInternal() && canEditGroup(group);
    }

    @Restriction
    public boolean canUpdateGeoTarget(CampaignCreativeGroup group) {
        return canEditGroup(group);
    }

    @Restriction
    public boolean canUpdateDeviceTargeting(CampaignCreativeGroup group) {
        return canEditGroup(group);
    }

    private boolean canViewAccountRole(CampaignCreativeGroup group) {
        AdvertisingAccountBase account;
        if (group.getAccount().getAgency() == null) {
            account = group.getAccount();
        } else {
            account = group.getAccount().getAgency();
        }
        return advertisingChannelRestrictions.canView(account.getRole());
    }

    private boolean canViewGroup(CampaignCreativeGroup group) {
        return canViewAccountRole(group) && advertiserEntityRestrictions.canView(group);
    }

    private boolean canEditGroup(CampaignCreativeGroup group) {
        return canViewAccountRole(group) && advertiserEntityRestrictions.canUpdate(group);
    }

    @Restriction
    public boolean canViewExpressionPerformance(CampaignCreativeGroup group) {
        return advertisingChannelRestrictions.canViewContent(group.getChannel()) && advertiserEntityRestrictions.canView(group);
    }
}

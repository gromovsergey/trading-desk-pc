package com.foros.action.campaign.campaignGroup;

import com.foros.framework.support.RequestContextsAware;
import com.foros.model.action.Action;
import com.foros.model.campaign.CCGSchedule;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CcgRate;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.campaign.WeekSchedule;
import com.foros.model.isp.Colocation;
import com.foros.model.site.Site;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.EntityTO;
import com.foros.session.NameTOComparator;
import com.foros.session.campaign.ISPColocationTO;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.util.EntityUtils;
import com.foros.util.context.RequestContexts;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;


public abstract class SaveCampaignGroupBaseAction extends EditSaveCampaignGroupActionBase implements RequestContextsAware {
    private Set<Long> selectedColocationIds;
    private Campaign campaign;

    @EJB
    private DeviceChannelService deviceChannelService;

    public SaveCampaignGroupBaseAction() {
        campaignCreativeGroup = new CampaignCreativeGroup();
    }

    protected void prepareDeviceChannels() {
        campaignCreativeGroupService.create(campaignCreativeGroup);
    }

    @Override
    public void validate() {
        if (groupLinkedToCampaignEndDateFlag != null) {
            campaignCreativeGroup.setLinkedToCampaignEndDateFlag(groupLinkedToCampaignEndDateFlag);
        } else {
            addFieldError("dateEnd", getText("errors.field.required"));
        }

        campaignCreativeGroup.setCampaign(getCampaign());

        if (campaignCreativeGroup.getId() == null) {
            if (!campaignCreativeGroup.getAccount().isInternational()) {
                campaignCreativeGroup.setCountry(campaignCreativeGroup.getAccount().getCountry());
            }
        }

        prepareDefaultDate();

        if (hasErrors()) {
            prepareRotationCriteria();
        }
    }

    private void prepareDeliverySchedules() {
        Set<CCGSchedule> ccgScheduleSet = new LinkedHashSet<CCGSchedule>();
        if (campaignCreativeGroup.isDeliveryScheduleFlag()) {
            for (WeekSchedule schedule : getScheduleSet().getSchedules()) {
                CCGSchedule ccgSchedule = new CCGSchedule();
                ccgSchedule.setTimeFrom(schedule.getTimeFrom());
                ccgSchedule.setTimeTo(schedule.getTimeTo());
                ccgSchedule.setCampaignCreativeGroup(campaignCreativeGroup);
                ccgScheduleSet.add(ccgSchedule);
            }
        }
        campaignCreativeGroup.setCcgSchedules(ccgScheduleSet);
    }

    private void prepareSites() {
        if (canEditSites()) {
            if (campaignCreativeGroup.isIncludeSpecificSitesFlag()) {
                Set<Site> sites = new LinkedHashSet<>();
                if (selectedSites != null && !selectedSites.isEmpty()) {
                    for (Long siteId : selectedSites) {
                        sites.add(new Site(siteId));
                    }
                }
                campaignCreativeGroup.setSites(sites);
            } else {
                campaignCreativeGroup.setSites(Collections.<Site> emptySet());
            }
        }
    }

    private void prepareColocations() {
        if (isInternal()) {
            if (campaignCreativeGroup.isIspColocationTargetingFlag()) {
                Set<Colocation> colocations = new HashSet<Colocation>();
                if (selectedColocationIds != null && !selectedColocationIds.isEmpty()) {
                    for (Long colocationId : selectedColocationIds) {
                        Colocation colocation = new Colocation();
                        colocation.setId(colocationId);
                        colocations.add(colocation);
                    }
                }
                campaignCreativeGroup.setColocations(colocations);
            } else {
                campaignCreativeGroup.setColocations(Collections.<Colocation> emptySet());
            }
        }
    }

    private void prepareConversionsTracking() {
        if (conversionTrackingIds == null || conversionTrackingIds.isEmpty()) {
            campaignCreativeGroup.setActions(Collections.<Action>emptySet());
        } else {
            Set<Action> conversions = new HashSet<Action>(conversionTrackingIds.size());
            for (Long conversionId : conversionTrackingIds) {
                Action conversion = new Action();
                conversion.setId(conversionId);
                conversions.add(conversion);
            }
            campaignCreativeGroup.setActions(conversions);
        }
    }

    private void prepareRates() {
        CcgRate rate = campaignCreativeGroup.getCcgRate();
        if (rate == null || rate.getRateType() == null) {
            return;
        }

        switch (rate.getRateType()) {
            case CPM:
                rate.setCpc(BigDecimal.ZERO);
                rate.setCpa(BigDecimal.ZERO);
                break;
            case CPC:
                rate.setCpm(BigDecimal.ZERO);
                rate.setCpa(BigDecimal.ZERO);
                break;
            case CPA:
                rate.setCpc(BigDecimal.ZERO);
                rate.setCpm(BigDecimal.ZERO);
                break;
        }
    }

    private void prepareDefaultDate() {
        defaultDateEnd.setDate(new Date(), getTimeZone(), CurrentUserSettingsHolder.getLocale());
    }

    private void prepareMinUidAge() {
        if (campaignCreativeGroup.getMinUidAge() == null) {
            campaignCreativeGroup.setMinUidAge(0L);
        }
    }

    public CampaignCreativeGroup prepareModel() {
        prepareDeliverySchedules();
        prepareDefaultDate();
        prepareRates();
        prepareSites();
        prepareColocations();
        prepareMinUidAge();
        prepareConversionsTracking();
        return campaignCreativeGroup;
    }

    public Set<Long> getSelectedColocationIds() {
        return selectedColocationIds;
    }

    public void setSelectedColocationIds(Set<Long> selectedColocationIds) {
        this.selectedColocationIds = selectedColocationIds;
    }

    public List<ISPColocationTO> getGroupColocations() {
        if (groupColocations == null) {
            if (selectedColocationIds != null && !selectedColocationIds.isEmpty()) {
                groupColocations = new LinkedList<ISPColocationTO>(campaignCreativeGroupService.findColocationsByIds(selectedColocationIds));
                Collections.sort(groupColocations, new NameTOComparator<EntityTO>());
                EntityUtils.applyStatusRules(groupColocations, null, true);
            }
        }
        return groupColocations;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(getCampaign().getAccount());
    }

    public Campaign getCampaign() {
        if (campaign == null) {
            campaign = campaignService.find(campaignId);
        }
        return campaign;
    }
}

package com.foros.action.campaign.campaignGroup;

import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.action.campaign.DateTimeBean;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.Country;
import com.foros.model.action.Action;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.OptInStatusTargeting;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.EntityTO;
import com.foros.session.NameTOComparator;
import com.foros.session.campaign.ISPColocationTO;
import com.foros.util.EntityUtils;
import com.foros.util.context.RequestContexts;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import javax.persistence.EntityNotFoundException;


public class EditCampaignGroupAction extends EditSaveCampaignGroupActionBase implements RequestContextsAware, BreadcrumbsSupport {
    private Long id;
    private Campaign campaign;

    protected Breadcrumbs breadcrumbs = new Breadcrumbs();

    @ReadOnly
    public String edit() {
        campaignCreativeGroup = campaignCreativeGroupService.view(id);
        campaignId = campaignCreativeGroup.getCampaign().getId();

        if (!checkGroupType(campaignCreativeGroup.getCcgType())) {
            throw new EntityNotFoundException("Entity with id = " + id + " not found");
        }

        prepareForEdit();

        breadcrumbs.add(new CampaignBreadcrumbsElement(campaignCreativeGroup.getCampaign())).add(new CampaignGroupBreadcrumbsElement(campaignCreativeGroup)).add(ActionBreadcrumbs.EDIT);

        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "AdvertiserEntity.createDisplayGroup", parameters = "find('Campaign', #target.campaignId)")
    public String createDisplay() {
        createGroup(CCGType.DISPLAY, TGTType.CHANNEL);

        breadcrumbs.add(new CampaignBreadcrumbsElement(campaignCreativeGroup.getCampaign())).add("campaign.breadcrumbs.campaign.groups.createDisplay");

        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "AdvertiserEntity.createKeywordTargetedTextGroup", parameters = "find('Campaign', #target.campaignId)")
    public String createText() {
        createGroup(CCGType.TEXT, TGTType.KEYWORD);
        breadcrumbs.add(new CampaignBreadcrumbsElement(campaignCreativeGroup.getCampaign())).add("campaign.breadcrumbs.campaign.groups.createText");
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "AdvertiserEntity.createChannelTargetedTextGroup", parameters = "find('Campaign', #target.campaignId)")
    public String createChannelTargetedText() {
        createGroup(CCGType.TEXT, TGTType.CHANNEL);
        breadcrumbs.add(new CampaignBreadcrumbsElement(campaignCreativeGroup.getCampaign())).add("campaign.breadcrumbs.campaign.groups.createChannelText");
        return SUCCESS;
    }

    public Campaign getCampaign() {
        if (campaign == null) {
            campaign = campaignService.find(campaignId);
        }
        return campaign;
    }

    protected String getCountryCode() {
        return getCampaign().getAccount().getCountry().getCountryCode();
    }

    private void createGroup(CCGType ccgType, TGTType tgtType) {
        campaignCreativeGroup = new CampaignCreativeGroup();
        campaignCreativeGroup.setCampaign(getCampaign());
        campaignCreativeGroup.setOptInStatusTargeting(OptInStatusTargeting.newDefaultValue());
        campaignCreativeGroup.setCcgType(ccgType);
        campaignCreativeGroup.setTgtType(tgtType);
        campaignCreativeGroup.setCountry(new Country(getCountryCode()));

        prepareForEdit();
    }

    private void prepareForEdit() {
        // selectedSites parameter can present if there was version error. It should be cleared to populate new list from DB
        selectedSites = null;

        prepareDates();

        prepareFlags();

        prepareMinUidAge();

        if (hasErrors() && getFieldErrors().containsKey("version")) {
            getScheduleSet().getSchedules().clear();
            getScheduleSet().getSchedules().addAll(campaignCreativeGroup.getCcgSchedules());
        }

        prepareRotationCriteria();

        conversionsTrackingFlag = null;
        loadConversionTrackingIds();
    }

    private void prepareMinUidAge() {
        if (campaignCreativeGroup.getMinUidAge() != null && campaignCreativeGroup.getMinUidAge().equals(0L)) {
            campaignCreativeGroup.setMinUidAge(null);
        }
    }

    private void prepareFlags() {
        if (campaignCreativeGroup.getId() != null) {
            groupLinkedToCampaignEndDateFlag = campaignCreativeGroup.isLinkedToCampaignEndDateFlag();
        }
    }

    private void prepareDates() {
        TimeZone timeZone = TimeZone.getTimeZone(campaignCreativeGroup.getAccount().getTimezone().getKey());
        Locale locale = CurrentUserSettingsHolder.getLocale();

        campaignDateStart = new DateTimeBean();
        campaignDateEnd = new DateTimeBean();
        campaignDateStart.setDate(campaignCreativeGroup.getCampaign().getDateStart(), timeZone, locale);
        campaignDateEnd.setDate(campaignCreativeGroup.getCampaign().getDateEnd(), timeZone, locale);

        Date startDate = campaignCreativeGroup.getCalculatedStartDate();
        Date endDate = campaignCreativeGroup.getCalculatedEndDate();

        if (startDate == null) {
            startDate = new Date();
        }
        campaignCreativeGroup.setDateStart(startDate);

        defaultDateEnd.setDate(new Date(), timeZone, locale);

        groupDateStart.setDate(startDate, timeZone, locale);
        groupDateEnd.setDate(endDate, timeZone, locale);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public CampaignCreativeGroup getExistingGroup() {
        return campaignCreativeGroup;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(campaignCreativeGroup.getAccount());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }

    public List<ISPColocationTO> getGroupColocations() {
        if (groupColocations == null) {
            if (getExistingGroup().getId() != null) {
                groupColocations = new LinkedList<ISPColocationTO>(campaignCreativeGroupService.findLinkedColocations(getExistingGroup().getId()));
                Collections.sort(groupColocations, new NameTOComparator<EntityTO>());
                EntityUtils.applyStatusRules(groupColocations, null, true);
            }
        }
        return groupColocations;
    }

    private void loadConversionTrackingIds() {
        conversionTrackingIds = new HashSet<>(campaignCreativeGroup.getActions().size());
        for (Action conversion: campaignCreativeGroup.getActions()) {
            conversionTrackingIds.add(conversion.getId());
        }
    }
}

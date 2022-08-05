package com.foros.action.campaign.campaignGroup;

import com.foros.action.campaign.DateTimeBean;
import com.foros.framework.support.TimeZoneAware;
import com.foros.model.action.Action;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.security.AccountType;
import com.foros.restriction.RestrictionService;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.EntityTO;
import com.foros.session.NameTOComparator;
import com.foros.session.action.ActionService;
import com.foros.session.campaign.CampaignService;
import com.foros.session.campaign.ISPColocationTO;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;
import com.foros.util.mapper.Converter;
import com.foros.util.mapper.Mapper;
import com.foros.util.mapper.Pair;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.ejb.EJB;

public abstract class EditSaveCampaignGroupActionBase extends CampaignGroupActionSupport implements TimeZoneAware {
    @EJB
    protected CampaignService campaignService;

    @EJB
    private RestrictionService restrictionService;

    @EJB
    private ActionService actionService;

    protected Long campaignId;
    protected DateTimeBean campaignDateStart;
    protected DateTimeBean campaignDateEnd;
    protected DateTimeBean groupDateStart = new DateTimeBean();
    protected DateTimeBean groupDateEnd = new DateTimeBean();
    protected DateTimeBean defaultDateEnd = new DateTimeBean();
    protected Boolean groupLinkedToCampaignEndDateFlag;

    protected Set<Long> selectedSites;
    protected List<ISPColocationTO> groupColocations;
    protected Set<Long> conversionTrackingIds;
    private List<Action> availableConversions;

    private List<RateType> rateTypes;
    private String thousandSeparator;
    private String decimalSeparator;
    private List<EntityTO> availableSites;
    private List<EntityTO> groupSites;

    private Boolean minUidAgeFlag;
    protected Boolean conversionsTrackingFlag;

    public abstract CampaignCreativeGroup getExistingGroup();

    public boolean canEditSites() {
        return (restrictionService.isPermitted("AdvertiserEntity.editSiteTargeting", getExistingGroup().getAccount())) &&
                getExistingGroup().getTgtType() == TGTType.CHANNEL;
    }

    @Override
    public TimeZone getTimeZone() {
        return TimeZone.getTimeZone(getExistingGroup().getAccount().getTimezone().getKey());
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public DateTimeBean getCampaignDateStart() {
        if (campaignDateStart != null) {
            return campaignDateStart;
        }

        TimeZone timeZone = TimeZone.getTimeZone(getExistingGroup().getAccount().getTimezone().getKey());
        Locale locale = CurrentUserSettingsHolder.getLocale();

        campaignDateStart = new DateTimeBean();
        campaignDateStart.setDate(getExistingGroup().getCampaign().getDateStart(), timeZone, locale);

        return campaignDateStart;
    }

    public DateTimeBean getCampaignDateEnd() {
        if (campaignDateEnd != null) {
            return campaignDateEnd;
        }

        TimeZone timeZone = TimeZone.getTimeZone(getExistingGroup().getAccount().getTimezone().getKey());
        Locale locale = CurrentUserSettingsHolder.getLocale();

        campaignDateEnd = new DateTimeBean();
        campaignDateEnd.setDate(getExistingGroup().getCampaign().getDateEnd(), timeZone, locale);

        return campaignDateEnd;
    }

    public DateTimeBean getGroupDateStart() {
        return groupDateStart;
    }

    public void setGroupDateStart(DateTimeBean groupDateStart) {
        this.groupDateStart = groupDateStart;
    }

    public DateTimeBean getGroupDateEnd() {
        return groupDateEnd;
    }

    public void setGroupDateEnd(DateTimeBean groupDateEnd) {
        this.groupDateEnd = groupDateEnd;
    }

    public DateTimeBean getDefaultDateEnd() {
        return defaultDateEnd;
    }

    public void setDefaultDateEnd(DateTimeBean defaultDateEnd) {
        this.defaultDateEnd = defaultDateEnd;
    }

    public Boolean getGroupLinkedToCampaignEndDateFlag() {
        return groupLinkedToCampaignEndDateFlag;
    }

    public void setGroupLinkedToCampaignEndDateFlag(Boolean groupLinkedToCampaignEndDateFlag) {
        this.groupLinkedToCampaignEndDateFlag = groupLinkedToCampaignEndDateFlag;
    }

    public Set<Long> getSelectedSites() {
        return selectedSites;
    }

    public void setSelectedSites(Set<Long> selectedSites) {
        this.selectedSites = selectedSites;
    }

    public List<RateType> getRateTypes() {
        if (rateTypes != null) {
            return rateTypes;
        }

        CampaignCreativeGroup existingGroup = getExistingGroup();

        AccountType groupAccountType = existingGroup.getAccount().getAccountType();
        rateTypes = groupAccountType.getAllowedRateTypes(existingGroup.getCcgType(), existingGroup.getTgtType());

        return rateTypes;
    }

    public String getThousandSeparator() {
        if (thousandSeparator != null) {
            return thousandSeparator;
        }

        Locale locale = CurrentUserSettingsHolder.getLocale();
        NumberFormat nf = NumberFormat.getInstance(locale);

        thousandSeparator = String.valueOf(((DecimalFormat)nf).getDecimalFormatSymbols().getGroupingSeparator());

        return thousandSeparator;
    }

    public String getDecimalSeparator() {
        if (decimalSeparator != null) {
            return decimalSeparator;
        }

        Locale locale = CurrentUserSettingsHolder.getLocale();
        NumberFormat nf = NumberFormat.getInstance(locale);

        decimalSeparator = String.valueOf(((DecimalFormat)nf).getDecimalFormatSymbols().getDecimalSeparator());

        return decimalSeparator;
    }

    public List<EntityTO> getAvailableSites() {
        if (availableSites != null) {
            return availableSites;
        }

        prepareSites();

        return availableSites;
    }

    public List<EntityTO> getGroupSites() {
        if (groupSites != null) {
            return groupSites;
        }

        prepareSites();

        return groupSites;
    }

    private void prepareSites() {
        Mapper<EntityTO, Long, EntityTO> siteTOMapper = new Mapper<EntityTO, Long, EntityTO>() {
            @Override
            public Pair<Long, EntityTO> item(EntityTO value) {
                return new Pair<>(value.getId(), value);
            }
        };

        Set<EntityTO> allSites = new HashSet<>(campaignCreativeGroupService.fetchTargetableSites(getExistingGroup().getAccount().isTestFlag(), getExistingGroup().getAccount().getCountry().getCountryCode()));

        groupSites = new LinkedList<>();
        availableSites = new LinkedList<>();

        Collection<EntityTO> sites = new LinkedList<>(), campaignGroupSites = new LinkedList<>();
        if (getExistingGroup().getId() != null) {
            campaignGroupSites = campaignCreativeGroupService.fetchLinkedSites(getExistingGroup().getId(), true);
            allSites.addAll(campaignGroupSites);
        }

        final Map<Long, EntityTO> allSiteTOMap = CollectionUtils.map(siteTOMapper, allSites);

        if (selectedSites == null || selectedSites.isEmpty()) {
            if (getExistingGroup().getId() != null
                    && !getFieldErrors().containsKey("sites")) {
                sites = campaignGroupSites;
            }
        } else {
            sites = CollectionUtils.convert(new Converter<Long, EntityTO>() {
                @Override
                public EntityTO item(Long value) {
                    return allSiteTOMap.get(value);
                }
            }, selectedSites);
        }

        availableSites.addAll(new LinkedList<>(allSites));
        groupSites.addAll(new LinkedList<>(sites));

        Comparator<EntityTO> comparator = new NameTOComparator<>();

        Collections.sort(groupSites, comparator);
        EntityUtils.applyStatusRules(groupSites, null, true);
        Collections.sort(availableSites, comparator);
    }

    public boolean isOptInStatusTargetingFlag() {
        return campaignCreativeGroup.getOptInStatusTargeting() != null;
    }

    /** @noinspection UnusedParameters*/
    public void setOptInStatusTargetingFlag(boolean enabled) {
        // struts will do the rest (create OptInStatusTargeting and set it's content other optInStatusTargeting.* parameters)
        campaignCreativeGroup.registerChange("optInStatusTargeting");
    }

    public Boolean getMinUidAgeFlag() {
        if (minUidAgeFlag == null) {
            minUidAgeFlag = campaignCreativeGroup.getMinUidAge() > 0;
        }
        return minUidAgeFlag;
    }

    public void setMinUidAgeFlag(Boolean minUidAgeFlag) {
        this.minUidAgeFlag = minUidAgeFlag;
    }

    protected void prepareRotationCriteria() {
        if (campaignCreativeGroup.getRotationCriteria() == null) {
            campaignCreativeGroup.setRotationCriteria(1L);
        }
    }

    public List<Action> getAvailableConversions() {
        if (availableConversions == null) {
            loadAvailableConversions();
        }
        return availableConversions;
    }

    private void loadAvailableConversions() {
        availableConversions = actionService.findNonDeletedByAccountId(campaignCreativeGroup.getAccount().getId());
    }

    public Boolean getConversionsTrackingFlag() {
        if (conversionsTrackingFlag == null) {
            conversionsTrackingFlag = new Boolean(!conversionTrackingIds.isEmpty());
        }
        return conversionsTrackingFlag;
    }

    public Set<Long> getCheckedConversionTrackingIds() {
        return conversionTrackingIds;
    }

    public void setConversionTrackingIds(Set<Long> conversionTrackingIds) {
        this.conversionTrackingIds = conversionTrackingIds;
    }

    public void setConversionsTrackingFlag(Boolean conversionsTrackingFlag) {
        this.conversionsTrackingFlag = conversionsTrackingFlag;
    }

    public boolean isWizardFunctionalityEnabled() {
        return false;
    }

}

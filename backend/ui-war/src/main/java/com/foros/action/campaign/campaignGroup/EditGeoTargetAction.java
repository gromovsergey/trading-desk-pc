package com.foros.action.campaign.campaignGroup;

import com.foros.action.BaseActionSupport;
import com.foros.action.admin.geoChannel.GeoChannelHelper;
import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.cache.application.CountryCO;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.Country;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.GeoChannel;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.account.AccountService;
import com.foros.session.admin.country.CountryService;
import com.foros.session.campaign.CCGKeywordService;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.channel.geo.GeoChannelService;
import com.foros.util.CountryHelper;
import com.foros.util.context.RequestContexts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class EditGeoTargetAction extends BaseActionSupport implements RequestContextsAware, ModelDriven<CampaignCreativeGroup>, BreadcrumbsSupport {
    @EJB
    private AccountService accountService;
    @EJB
    private CountryService countryService;
    @EJB
    private CampaignCreativeGroupService groupService; 
    @EJB
    private GeoChannelService geoChannelService; 
    @EJB
    private CCGKeywordService keywordService;

    private CampaignCreativeGroup group = new CampaignCreativeGroup();
    private AdvertisingAccountBase existingAccount = new AdvertiserAccount();
    private List<CountryCO> countries;
    private Collection<GeoChannel> geoChannels;
    private Collection<GeoChannel> states;
    private Collection<GeoChannel> cities;
    private String stateLabel;
    private String cityLabel;
    private Boolean allowCountryChange;
    private Boolean cityOrAddressFlag = true;

    public EditGeoTargetAction() {
        geoChannels = new ArrayList<>();
    }

    @ReadOnly
    @Restrict(restriction = "CreativeGroup.updateGeoTarget", parameters = "find('CampaignCreativeGroup', #target.model.id)")
    public String edit() {
        group = groupService.view(getModel().getId());
        if (isInternational()) {
            setCountries(CountryHelper.sort(countryService.getIndex()));
        }
        geoChannels = GeoChannelHelper.appendStatusSuffixAndSortForGeoTarget(group.getGeoChannels());
        states = geoChannelService.getStates(group.getCountry());
        cities = geoChannelService.getOrphanCities(group.getCountry());
        Country country = countryService.find(group.getCountry().getCountryCode());
        GeoChannelHelper helper = new GeoChannelHelper(country);
        setStateLabel(helper.getStateLabel());
        setCityLabel(helper.getCityLabel());
        return SUCCESS;
    }

    private void setStateLabel(String stateLabel) {
        this.stateLabel = stateLabel; 
    }

    public String getStateLabel() {
        return stateLabel;
    }

    private void setCityLabel(String cityLabel) {
        this.cityLabel = cityLabel;
    }

    public String getCityLabel() {
        return cityLabel;
    }

    public boolean isInternational() {
        return getModel().getAccount().isInternational();
    }

    public Collection<GeoChannel> getGeoChannels() {
        return geoChannels;
    }

    public List<CountryCO> getCountries() {
        return countries;
    }

    public Collection<GeoChannel> getStates() {
        return states;
    }

    public Collection<GeoChannel> getCities() {
        return cities;
    }

    public void setCountries(List<CountryCO> countries) {
        this.countries = countries;
    }

    public CampaignCreativeGroup getGroup() {
        return group;
    }

    @Override
    public CampaignCreativeGroup getModel() {
        return group;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(getExistingAccount());
    }
    

    public AdvertisingAccountBase getExistingAccount() {
        if (existingAccount.getId() == null && group.getAccount() != null) {
            existingAccount = (AdvertisingAccountBase)accountService.find(group.getAccount().getId());
        }
        return existingAccount;
    }

    public boolean isAllowCountryChange() {
        if (allowCountryChange != null) {
            return allowCountryChange;
        }

        CampaignCreativeGroup campaignGroup = getGroup();

        Long campaignGroupId = campaignGroup.getId();

        if (campaignGroupId == null) {
            allowCountryChange = true;

            return allowCountryChange;
        }

        allowCountryChange = (campaignGroup.getTgtType() == TGTType.CHANNEL && campaignGroup.getChannelTarget() != ChannelTarget.TARGETED)
                || (campaignGroup.getTgtType() == TGTType.KEYWORD && keywordService.findAll(campaignGroupId).isEmpty());

        return allowCountryChange;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs()
                .add(new CampaignBreadcrumbsElement(group.getCampaign()))
                .add(new CampaignGroupBreadcrumbsElement(group))
                .add("ccg.userSampleGroups.edit");
    }

    public Boolean getCityOrAddressFlag() {
        return cityOrAddressFlag;
    }

    public void setCityOrAddressFlag(Boolean cityOrAddressFlag) {
        this.cityOrAddressFlag = cityOrAddressFlag;
    }


}

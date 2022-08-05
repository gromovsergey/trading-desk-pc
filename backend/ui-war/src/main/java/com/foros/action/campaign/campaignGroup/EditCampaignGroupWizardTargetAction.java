package com.foros.action.campaign.campaignGroup;

import com.foros.action.BaseActionSupport;
import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.cache.application.CountryCO;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.campaign.Campaign;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.admin.country.CountryService;
import com.foros.session.campaign.CampaignService;
import com.foros.util.CountryHelper;
import com.foros.util.context.RequestContexts;

import java.util.Collection;
import java.util.List;
import javax.ejb.EJB;

public class EditCampaignGroupWizardTargetAction extends BaseActionSupport implements RequestContextsAware, BreadcrumbsSupport {
    private Long campaignId;
    private Campaign campaign;
    private String countryCode;
    private List<CountryCO> countries;
    private String namingConvention;
    private Breadcrumbs breadcrumbs = new Breadcrumbs();
    private NamingConventionHelper namingConventionHelper;

    @EJB
    private CampaignService campaignService;

    @EJB
    private CountryService countryService;

    @ReadOnly
    @Restrict(restriction = "AdvertiserEntity.createDisplayGroup", parameters = "#target.campaign")
    public String createDisplay() {
        breadcrumbs.add(new CampaignBreadcrumbsElement(getCampaign())).add("campaign.breadcrumbs.campaign.groups.createDisplayWizard.step1");
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "AdvertiserEntity.createChannelTargetedTextGroup", parameters = "#target.campaign")
    public String createChannelTargetedText() {
        breadcrumbs.add(new CampaignBreadcrumbsElement(getCampaign())).add("campaign.breadcrumbs.campaign.groups.createChannelTextWizard.step1");
        return SUCCESS;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public Campaign getCampaign() {
        if (campaign == null) {
            campaign = campaignService.find(campaignId);
        }
        return campaign;
    }

    public String getCountryCode() {
        if(countryCode==null) {
            countryCode = getCampaign().getAccount().getCountry().getCountryCode();
        }
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public List<CountryCO> getCountries() {
        if (countries == null) {
            Collection<CountryCO> countryIndex = countryService.getIndex();
            countries = CountryHelper.sort(countryIndex);
        }
        return countries;
    }

    public String getNamingConvention() {
        return namingConvention;
    }

    public void setNamingConvention(String namingConvention) {
        this.namingConvention = namingConvention;
    }

    public List<String> getPredefinedNamingConventions() {
        return getNamingConventionHelper().getPredefinedNamingConventions();
    }

    public String getDefaultNamingConvention() {
        return getPredefinedNamingConventions().get(0);
    }

    public String getCustomizableNamingConvention() {
        return getNamingConventionHelper().getCustomizableNamingConvention();
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(getCampaign().getAccount());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }

    private NamingConventionHelper getNamingConventionHelper() {
        if (namingConventionHelper == null) {
            namingConventionHelper = new NamingConventionHelper(getCampaign().getAccount().getName(), getCampaign().getName());
        }

        return namingConventionHelper;
    }
}

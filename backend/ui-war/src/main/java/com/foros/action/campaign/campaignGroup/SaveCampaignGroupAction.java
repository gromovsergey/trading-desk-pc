package com.foros.action.campaign.campaignGroup;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.model.isp.Colocation;
import com.foros.model.site.Site;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.session.colocation.ColocationService;
import com.foros.session.site.SiteService;
import com.foros.util.CollectionUtils;
import com.foros.validation.annotation.Validate;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import javax.ejb.EJB;
import javax.persistence.EntityNotFoundException;
import java.util.Iterator;
import java.util.List;

@Validations(
    conversionErrorFields = {
        @ConversionErrorFieldValidator(fieldName = "budget", key = "errors.field.number"),
        @ConversionErrorFieldValidator(fieldName = "dailyBudget", key = "errors.field.number"),
        @ConversionErrorFieldValidator(fieldName = "frequencyCap.periodSpan.value", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowLengthSpan.value", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowCount", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "frequencyCap.lifeCount", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "dateStart", key = "errors.field.date"),
        @ConversionErrorFieldValidator(fieldName = "dateEnd", key = "errors.field.date"),
        @ConversionErrorFieldValidator(fieldName = "ccgRate.cpm", key = "errors.field.number"),
        @ConversionErrorFieldValidator(fieldName = "ccgRate.cpc", key = "errors.field.number"),
        @ConversionErrorFieldValidator(fieldName = "ccgRate.cpa", key = "errors.field.number"),
        @ConversionErrorFieldValidator(fieldName = "minCtrGoal", key = "errors.field.number"),
        @ConversionErrorFieldValidator(fieldName = "rotationCriteria", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "minUidAge", key = "errors.field.integer")
    }
)
public class SaveCampaignGroupAction extends SaveCampaignGroupBaseAction implements BreadcrumbsSupport {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add(
                    "sites[(#index)]", "'sites'",
                    "messageForSite(groups[0], violation.message)"
            )
            .add(
                    "colocations[(#index)]", "'colocations'",
                    "messageForColocation(groups[0], violation.message)"
            )
            .rules();

    private CampaignCreativeGroup existingGroup;
    private Campaign existingCampaign;

    @EJB
    private DeviceChannelService deviceChannelService;

    @EJB
    private SiteService siteService;

    @EJB
    private ColocationService colocationService;

    @Validate(validation = "CampaignCreativeGroup.update", parameters = "#target.prepareModel()")
    public String update() {
        if (hasErrors()) {
            return INPUT;
        }
        campaignCreativeGroup.setCountry(getExistingGroup().getCountry());
        prepareModel();

        campaignCreativeGroupService.update(campaignCreativeGroup);

        return SUCCESS;
    }

    @Validate(validation = "CampaignCreativeGroup.create", parameters = "#target.prepareModel()")
    public String create() throws Exception {
        if (hasErrors()) {
            return INPUT;
        }
        campaignCreativeGroup.setCountry(campaignCreativeGroup.getAccount().getCountry());
        prepareModel();

        campaignCreativeGroupService.create(campaignCreativeGroup);

        return SUCCESS;
    }

    @Override
    public void validate() {
        if (campaignCreativeGroup.isIspColocationTargetingFlag() &&
                CollectionUtils.isNullOrEmpty(getSelectedColocationIds())) {
            addFieldError("colocations", getText("errors.field.required"));
        }
        if (campaignCreativeGroup.isIncludeSpecificSitesFlag() &&
                CollectionUtils.isNullOrEmpty(getSelectedSites())) {
            addFieldError("sites", getText("errors.field.required"));
        }
        if (hasErrors()) {
            campaignCreativeGroup.setCountry(campaignCreativeGroup.getId() == null ?
                    getExistingGroup().getAccount().getCountry() : getExistingGroup().getCountry());
        }
        super.validate();
    }

    @Override
    public CampaignCreativeGroup getExistingGroup() {
        if (existingGroup != null) {
            return existingGroup;
        }

        if (campaignCreativeGroup.getId() != null) {
            existingGroup = campaignCreativeGroupService.find(campaignCreativeGroup.getId());

            if (existingCampaign == null) {
                existingCampaign = existingGroup.getCampaign();
            }
        } else {
            existingGroup = campaignCreativeGroup;
            existingGroup.setCampaign(getCampaign());
        }

        return existingGroup;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        if (campaignCreativeGroup.getId() != null) {
            breadcrumbs.add(new CampaignBreadcrumbsElement(getCampaign())).add(new CampaignGroupBreadcrumbsElement(getExistingGroup())).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs.add(new CampaignBreadcrumbsElement(getCampaign()));
            if (campaignCreativeGroup.getCcgType() == CCGType.DISPLAY) {
                breadcrumbs.add("campaign.breadcrumbs.campaign.groups.createDisplay");
            } else if (campaignCreativeGroup.getTgtType() == TGTType.CHANNEL) {
                breadcrumbs.add("campaign.breadcrumbs.campaign.groups.createChannelText");
            } else {
                breadcrumbs.add("campaign.breadcrumbs.campaign.groups.createText");
            }
        }
        return breadcrumbs;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public String messageForSite(int index, String message) {
        Site site = findSiteByIndex(index);
        if (site == null) {
            return Integer.toString(index + 1) + ": " + message;
        } else {
            return "\"" + site.getAccount().getName() + " / " + site.getName() + "\": " + message;
        }
    }

    private Site findSiteByIndex(int index) {
        Iterator<Site> siteIterator = getModel().getSites().iterator();
        for (int i = 0; siteIterator.hasNext(); i++) {
            Site site = siteIterator.next();
            if (i == index) {
                try {
                    return siteService.find(site.getId());
                } catch (EntityNotFoundException e) {
                    return null;
                }
            }
        }
        return null;
    }

    public String messageForColocation(int index, String message) {
        Colocation colocation = findColocationByIndex(index);
        if (colocation == null) {
            return Integer.toString(index + 1) + ": " + message;
        } else {
            return "\"" + colocation.getAccount().getName() + " / " + colocation.getName() + "\": " + message;
        }
    }

    private Colocation findColocationByIndex(int index) {
        Iterator<Colocation> colocationIterator = getModel().getColocations().iterator();
        for (int i = 0; colocationIterator.hasNext(); i++) {
            Colocation colocation = colocationIterator.next();
            if (i == index) {
                try {
                    return colocationService.find(colocation.getId());
                } catch (EntityNotFoundException e) {
                    return null;
                }
            }
        }
        return null;
    }
}

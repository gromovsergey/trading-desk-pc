package com.foros.action.campaign.creative;

import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.action.campaign.campaignGroup.CampaignGroupBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.breadcrumbs.SimpleTextBreadcrumbsElement;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.restriction.annotation.Restrict;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

public class SaveCampaignCreativeAction extends EditSaveCampaignCreativeActionSupport implements BreadcrumbsSupport {

    public SaveCampaignCreativeAction() {
        campaignCreative = new CampaignCreative();
    }

    @Validations(
            conversionErrorFields = {
                    @ConversionErrorFieldValidator(fieldName = "weight", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "frequencyCap.periodSpan.value", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowLengthSpan.value", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowCount", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "frequencyCap.lifeCount", key = "errors.field.integer")
            }
    )
    @Validate(validation = "CampaignCreative.create", parameters = "#target.model")
    public String create() {
        campaignCreative.setCreativeGroup(new CampaignCreativeGroup(ccgId));
        campaignCreativeService.create(campaignCreative);

        return SUCCESS;
    }

    @Validations(
            conversionErrorFields = {
                    @ConversionErrorFieldValidator(fieldName = "weight", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "frequencyCap.periodSpan.value", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowLengthSpan.value", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowCount", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "frequencyCap.lifeCount", key = "errors.field.integer")
            }
    )
    @Validate(validation = "CampaignCreative.update", parameters = "#target.model")
    public String update() {
        campaignCreativeService.update(campaignCreative);
        return SUCCESS;
    }

    @Restrict(restriction = "AdvertiserEntity.view", parameters = "find('Creative', #target.model.creative.id)")
    public String changeCreative() {
        return SUCCESS;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs()
                .add(new CampaignBreadcrumbsElement(getExistingGroup().getCampaign()))
                .add(new CampaignGroupBreadcrumbsElement(getExistingGroup()));
        if (campaignCreative.getId() != null) {
            breadcrumbs.add(new CreativeLinkBreadcrumbsElement(getModel())).add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs.add(new SimpleTextBreadcrumbsElement("campaign.breadcrumbs.group.createCreativeLink"));
        }

        return breadcrumbs;
    }
}

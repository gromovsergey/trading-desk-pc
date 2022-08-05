package com.foros.action.admin.country.ctra;

import com.foros.action.admin.country.CountriesBreadcrumbsElement;
import com.foros.action.admin.country.CountryBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.util.StringUtil;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;

public class SaveCTRAlgorithmDataAction extends CTRAlgorithmActionSupport implements BreadcrumbsSupport {

    private static final String CAMPAIGN_IDS_SPLIT_PATTERN = "[\\s,]+";

    private Collection<Long> parsedCampaignExclusions;

    @Override
    public void validate() {
        if (StringUtil.isPropertyNotEmpty(campaignExclusionsText)) {
            String[] mayBeIds = campaignExclusionsText.split(CAMPAIGN_IDS_SPLIT_PATTERN);
            for (String mayBeAnId : mayBeIds) {
                try {
                    Long.parseLong(mayBeAnId);
                } catch (NumberFormatException e) {
                    addFieldError("campaignExclusions", getText("ctrAlgorithmData.byCampaign.badIdError", new String[]{mayBeAnId}));
                }
            }
        }
    }

    @Validations(
            conversionErrorFields = {
                    @ConversionErrorFieldValidator(fieldName = "clicksInterval1Days", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "clicksInterval1Weight", key = "errors.field.positiveNumber"),
                    @ConversionErrorFieldValidator(fieldName = "clicksInterval2Days", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "clicksInterval2Weight", key = "errors.field.positiveNumber"),
                    @ConversionErrorFieldValidator(fieldName = "clicksInterval3Weight", key = "errors.field.positiveNumber"),
                    @ConversionErrorFieldValidator(fieldName = "impsInterval1Days", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "impsInterval1Weight", key = "errors.field.positiveNumber"),
                    @ConversionErrorFieldValidator(fieldName = "impsInterval2Days", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "impsInterval2Weight", key = "errors.field.positiveNumber"),
                    @ConversionErrorFieldValidator(fieldName = "impsInterval3Weight", key = "errors.field.positiveNumber"),

                    @ConversionErrorFieldValidator(fieldName = "pubCTRDefaultPercent", key = "errors.field.positiveNumber"),
                    @ConversionErrorFieldValidator(fieldName = "sysCTRLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "pubCTRLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "siteCTRLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "tagCTRLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "kwtgCTRDefaultPercent", key = "errors.field.positiveNumber"),
                    @ConversionErrorFieldValidator(fieldName = "sysKwtgCTRLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "keywordCTRLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "ccgkeywordKwCTRLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "ccgkeywordTgCTRLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "towRawPercent", key = "errors.field.positiveNumber"),
                    @ConversionErrorFieldValidator(fieldName = "sysTOWLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "campaignTOWLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "tgTOWLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "keywordTOWLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "ccgkeywordKwTOWLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "ccgkeywordTgTOWLevel", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "chtgCTRDefaultPercent", key = "errors.field.positiveNumber"),
                    @ConversionErrorFieldValidator(fieldName = "chdgCTRDefaultPercent", key = "errors.field.positiveNumber"),
                    @ConversionErrorFieldValidator(fieldName = "cpcRandomImps", key = "errors.field.integer"),
                    @ConversionErrorFieldValidator(fieldName = "cpaRandomImps", key = "errors.field.integer")
            }
    )
    @Validate(validation = "CTRAlgorithm.save",
            parameters = {"#target.model", "#target.advertiserExclusionsIds", "#target.parsedCampaignExclusions"})
    public String save() {
        ctrAlgorithmService.save(data, advertiserExclusionsIds, getParsedCampaignExclusions());

        return SUCCESS;
    }

    public Collection<Long> getAdvertiserExclusionsIds() {
        return advertiserExclusionsIds;
    }

    public Collection<Long> getParsedCampaignExclusions() {
        if (parsedCampaignExclusions != null) {
            return parsedCampaignExclusions;
        }

        if (getFieldErrors().containsKey("campaignExclusions")) {
            parsedCampaignExclusions = Collections.emptySet();
        } else {
            parsedCampaignExclusions = new LinkedHashSet<Long>();

            if (StringUtil.isPropertyNotEmpty(campaignExclusionsText)) {
                String[] campaignIds = campaignExclusionsText.split(CAMPAIGN_IDS_SPLIT_PATTERN);
                for (String campaignIdString : campaignIds) {
                    parsedCampaignExclusions.add(Long.parseLong(campaignIdString));
                }
            }
        }

        return parsedCampaignExclusions;
    }

    public void setCampaignExclusionsText(String campaignExclusionsText) {
        this.campaignExclusionsText = campaignExclusionsText;
    }

    public void setAdvertiserExclusionsIds(Collection<Long> advertiserExclusionsIds) {
        this.advertiserExclusionsIds = advertiserExclusionsIds;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new CountriesBreadcrumbsElement()).add(new CountryBreadcrumbsElement(data.getCountry())).add(new CTRAlgorithmBreadcrumbsElement(data.getCountry())).add(ActionBreadcrumbs.EDIT);
    }
}

package com.foros.action.campaign.campaignGroup;

import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.cache.application.CountryCO;
import com.foros.framework.ReadOnly;
import com.foros.model.Country;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignType;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.campaign.OptInStatusTargeting;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.Channel;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.admin.country.CountryService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.util.CountryHelper;
import com.foros.util.StringUtil;
import com.foros.util.context.RequestContexts;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationService;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.strategy.ValidationStrategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;


public class EditCampaignGroupWizardSettingsAction extends EditCampaignGroupAction {
    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("channelNames", "'channelTargetsList'", "violation.message")
            .add("channelNames[(#index)]", "'channelTargetsList'", "channelNameError(groups[0], violation.message)")
            .rules();

    private static final String DISPLAY_BREADCRUMBS_KEY = "campaign.breadcrumbs.campaign.groups.createDisplayWizard.";
    private static final String TEXT_BREADCRUMBS_KEY = "campaign.breadcrumbs.campaign.groups.createChannelTextWizard.";

    private String countryCode;
    private List<CountryCO> countries;
    private String namingConvention;
    private String channelTargetsList;
    private List<? extends Channel> targetContextList;
    private String channelTargetIds;
    private NamingConventionHelper namingConventionHelper;
    private String customizableNamingConvention;
    private String defaultNamingConvention;
    private boolean navigateBack;

    @EJB
    SearchChannelService searchChannelService;

    @EJB
    private ValidationService validationService;

    @EJB
    private CountryService countryService;

    @ReadOnly
    public String createDisplay() {
        if (isNavigateBack()) {
            restoreStep1();
            return INPUT;
        }

        String result = super.createDisplay();
        breadcrumbs = new Breadcrumbs().add(new CampaignBreadcrumbsElement(getCampaign())).add(DISPLAY_BREADCRUMBS_KEY + "step2");
        return result;
    }

    @ReadOnly
    @Restrict(restriction = "AdvertiserEntity.createChannelTargetedTextGroup", parameters = "#target.campaign")
    public String createChannelTargetedText() {
        if (isNavigateBack()) {
            restoreStep1();
            return INPUT;
        }

        String result = super.createChannelTargetedText();
        breadcrumbs = new Breadcrumbs().add(new CampaignBreadcrumbsElement(getCampaign())).add(TEXT_BREADCRUMBS_KEY + "step2");
        return result;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(getCampaign().getAccount());
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

    public String getChannelTargetsList() {
        return channelTargetsList;
    }

    public void setChannelTargetsList(String channelTargetsList) {
        this.channelTargetsList = channelTargetsList;
    }

    public List<String> getPredefinedNamingConventions() {
        return getNamingConventionHelper().getPredefinedNamingConventions();
    }

    public String getDefaultNamingConvention() {
        return defaultNamingConvention;
    }

    public String getCustomizableNamingConvention() {
        return customizableNamingConvention;
    }

    public String getChannelTargetIds() {
        if (channelTargetIds == null) {
            List<String> targetIds = new ArrayList<>();
            for (Channel channel : getTargetContextList()) {
                targetIds.add(String.valueOf(channel.getId()));
            }

            channelTargetIds = StringUtil.join(targetIds);
        }
        return channelTargetIds;
    }

    @Override
    public boolean isWizardFunctionalityEnabled() {
        return true;
    }

    public boolean isNavigateBack() {
        return navigateBack;
    }

    public void setNavigateBack(boolean navigateBack) {
        this.navigateBack = navigateBack;
    }

    private void restoreStep1() {
        fillStep1NamingConventions();
        fillStep1Breadcrumbs();
    }

    private void fillStep1NamingConventions() {
        for (String predefinedConvention : getPredefinedNamingConventions()) {
            if (predefinedConvention.equals(getNamingConvention())) {
                defaultNamingConvention = predefinedConvention;
                customizableNamingConvention = getNamingConventionHelper().getCustomizableNamingConvention();
                return;
            }
        }
        defaultNamingConvention = getNamingConvention();
        customizableNamingConvention = getNamingConvention();
    }

    private void fillStep1Breadcrumbs() {
        String breadcrumbsKey = (getCampaign().getCampaignType() == CampaignType.DISPLAY ?
                DISPLAY_BREADCRUMBS_KEY : TEXT_BREADCRUMBS_KEY) + "step1";
        breadcrumbs = new Breadcrumbs().add(new CampaignBreadcrumbsElement(getCampaign())).add(breadcrumbsKey);
    }

    private NamingConventionHelper getNamingConventionHelper() {
        if (namingConventionHelper == null) {
            namingConventionHelper = new NamingConventionHelper(getCampaign().getAccount().getName(), getCampaign().getName());
        }
        return namingConventionHelper;
    }

    @Override
    public void validate() {
        if (isNavigateBack()) {
            return;
        }

        if (getCampaign().getCampaignType() == CampaignType.DISPLAY) {
            createGroup(CCGType.DISPLAY, TGTType.CHANNEL);
        } else {
            createGroup(CCGType.TEXT, TGTType.CHANNEL);
        }

        validateChannelList();
        validateNamingConvention();

        if (hasErrors()) {
            restoreStep1();
        }
    }

    private void validateChannelList() {
        try {
            getTargetContextList();
        } catch (ConstraintViolationException e) {
            restoreStep1();
            throw e;
        }
    }

    private void validateNamingConvention() {
        if (!getNamingConventionHelper().isNamingConventionValid(getNamingConvention())) {
            addFieldError("namingConvention", getText("campaign.group.target.macro.required", new String[] { getNamingConventionHelper().getChannelMacroName() }));
        }

        Set<String> existingCCGNames = new HashSet<>(campaignCreativeGroupService.findCCGNamesByCampaign(getCampaign().getId()));
        StringBuilder existingNamesBuilder = new StringBuilder();
        boolean first = true;
        for (Channel channel : getTargetContextList()) {
            String newCCGName = getNamingConventionHelper().getFinalName(getNamingConvention(), channel.getName());
            if (existingCCGNames.contains(newCCGName)) {
                if (!first) {
                    existingNamesBuilder.append(", ");
                }
                first = false;
                existingNamesBuilder.append(newCCGName);
            } else {
                getModel().setName(newCCGName);
                ValidationContext context = validationService.validate(ValidationStrategies.create(), "Bean.bean", null, getModel(), ValidationMode.CREATE);
                for (ConstraintViolation violation : context.getConstraintViolations()) {
                    if (violation.getPropertyPath().toString().equals("name")) {
                        addFieldError("namingConvention", newCCGName + ": " + violation.getMessage());
                    }
                }
            }
        }
        String existingNames = existingNamesBuilder.toString();
        if (!existingNames.isEmpty()) {
            addFieldError("namingConvention", getText("errors.duplicate.names", new String[] { existingNames }));
        }
    }

    private List<? extends Channel> getTargetContextList() {
        if (targetContextList == null) {
            targetContextList = searchChannelService.resolveChannelTargets(
                    getChannelTargetsListAsList(), getCurrentChannelAccount(), new Country(getCountryCode()));
        }
        return targetContextList;
    }

    private AdvertisingAccountBase getCurrentChannelAccount() {
        return getCampaign().getAccount().getAgency() == null ? getCampaign().getAccount() :
                getCampaign().getAccount().getAgency();
    }

    private List<String> getChannelTargetsListAsList() {
        return new ArrayList<String>(Arrays.asList(StringUtil.splitAndTrim(getChannelTargetsList())));
    }

    protected void createGroup(CCGType ccgType, TGTType tgtType) {
        campaignCreativeGroup = new CampaignCreativeGroup();
        campaignCreativeGroup.setCampaign(getCampaign());
        campaignCreativeGroup.setOptInStatusTargeting(OptInStatusTargeting.newDefaultValue());
        campaignCreativeGroup.setCcgType(ccgType);
        campaignCreativeGroup.setTgtType(tgtType);
        campaignCreativeGroup.setChannelTarget(ChannelTarget.TARGETED);
        campaignCreativeGroup.setCountry(new Country(getCountryCode()));
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public String channelNameError(int index, String message) {
        List<String> names = getChannelTargetsListAsList();
        String name = names.get(index);
        return getText("errors.fieldError", new String[]{name, message});
    }
}

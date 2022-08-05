package com.foros.action.campaign.campaignGroup;

import com.foros.action.campaign.CampaignBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.channel.Channel;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.util.EntityUtils;
import com.foros.util.StringUtil;
import com.foros.util.context.RequestContexts;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;


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
                @ConversionErrorFieldValidator(fieldName = "rotationCriteria", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "minUidAge", key = "errors.field.integer")
        }
)
public class SaveCampaignGroupWizardAction extends SaveCampaignGroupBaseAction implements RequestContextsAware, BreadcrumbsSupport {
    private CampaignCreativeGroup dummyExistingGroup;
    private NamingConventionHelper namingConventionHelper;
    private String namingConvention;
    private String channelTargetsList;
    private String channelTargetIds;
    private Set<Long> channelTargetIdsList;
    private Set<String> fieldErrors = new HashSet<>();

    @EJB
    private SearchChannelService searchChannelService;

    @Override
    public void validate() {
        // Has errors, that cannot occur in normal data flow ?
        if (!getNamingConventionHelper().isNamingConventionValid(getNamingConvention()) ||
            !hasValidChannelIds()) {
                throw new AccessControlException("Access is forbidden!");
        }

        super.validate();
    }

    public String create() throws Exception {
        prepareModel();
        campaignCreativeGroupService.createAll(cloneCCGs());

        return SUCCESS;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(getCampaign().getAccount());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs().add(new CampaignBreadcrumbsElement(getCampaign()));
        if (campaignCreativeGroup.getCcgType() == CCGType.DISPLAY) {
            breadcrumbs.add("campaign.breadcrumbs.campaign.groups.createDisplayWizard.step2");
        } else {
            breadcrumbs.add("campaign.breadcrumbs.campaign.groups.createChannelTextWizard.step2");
        }
        return breadcrumbs;
    }

    public String getNamingConvention() {
        return namingConvention;
    }

    public void setNamingConvention(String namingConvention) {
        this.namingConvention = namingConvention;
    }

    public String getChannelTargetIds() {
        return channelTargetIds;
    }

    public void setChannelTargetIds(String channelTargetIds) {
        this.channelTargetIds = channelTargetIds;
    }

    public String getChannelTargetsList() {
        return channelTargetsList;
    }

    public void setChannelTargetsList(String channelTargetsList) {
        this.channelTargetsList = channelTargetsList;
    }

    public Set<Long> getChannelTargetIdsList() {
        if (channelTargetIdsList == null) {
            String[] idsAsStrings = StringUtil.splitAndTrim(getChannelTargetIds());
            this.channelTargetIdsList = new HashSet<>(idsAsStrings.length);
            for (String idAsString : idsAsStrings) {
                this.channelTargetIdsList.add(Long.parseLong(idAsString));
            }
        }

        return channelTargetIdsList;
    }

    @Override
    public CampaignCreativeGroup getExistingGroup() {
        if (dummyExistingGroup == null) {
            dummyExistingGroup = campaignCreativeGroup;
            dummyExistingGroup.setCampaign(getCampaign());
        }

        return dummyExistingGroup;
    }

    @Override
    public void addFieldError(String fieldName, String errorMessage) {
        String fieldError = fieldName + "::" + errorMessage;
        if (!fieldErrors.contains(fieldError)) {
            fieldErrors.add(fieldError);
            super.addFieldError(fieldName, errorMessage);
        }
    }

    @Override
    public boolean isWizardFunctionalityEnabled() {
        return true;
    }

    @Override
    public CampaignCreativeGroup prepareModel() {
        campaignCreativeGroup.setCountry(campaignCreativeGroup.getAccount().getCountry());
        return super.prepareModel();
    }

    private List<CampaignCreativeGroup> cloneCCGs() {
        Set<Long> targetIds = getChannelTargetIdsList();
        List<CampaignCreativeGroup> result = new ArrayList<>(targetIds.size());
        for (Long targetId : targetIds) {
            CampaignCreativeGroup newGroup = EntityUtils.clone(getModel());
            Collection<String> changes = campaignCreativeGroup.getChanges();
            newGroup.registerChange(changes.toArray(new String[changes.size()]));

            Channel channel = searchChannelService.findChannelTarget(targetId);
            newGroup.setChannel(channel);
            newGroup.setChannelTarget(ChannelTarget.TARGETED);

            if (newGroup.getAccount().isInternational()) {
                newGroup.setCountry(channel.getCountry());
            }

            newGroup.setName(getNamingConventionHelper().getFinalName(getNamingConvention(), newGroup.getChannel().getName()));

            result.add(newGroup);
        }

        return result;
    }

    private NamingConventionHelper getNamingConventionHelper() {
        if (namingConventionHelper == null) {
            namingConventionHelper = new NamingConventionHelper(getCampaign().getAccount().getName(), getCampaign().getName());
        }

        return namingConventionHelper;
    }

    private boolean hasValidChannelIds() {
        if (StringUtil.isPropertyEmpty(getChannelTargetIds())) {
            return false;
        }

        try {
            getChannelTargetIdsList();
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}

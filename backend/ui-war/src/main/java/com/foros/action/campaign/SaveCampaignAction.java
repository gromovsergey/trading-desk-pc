package com.foros.action.campaign;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignSchedule;
import com.foros.model.campaign.CampaignType;
import com.foros.model.campaign.WeekSchedule;
import com.foros.model.channel.Channel;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.validation.annotation.Validate;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.text.ParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import javax.ejb.EJB;

@Validations(
    conversionErrorFields = {
        @ConversionErrorFieldValidator(fieldName = "budget", key = "errors.field.number"),
        @ConversionErrorFieldValidator(fieldName = "dailyBudget", key = "errors.field.number"),
        @ConversionErrorFieldValidator(fieldName = "maxPubShare", key = "errors.field.number"),
        @ConversionErrorFieldValidator(fieldName = "frequencyCap.periodSpan.value", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowLengthSpan.value", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "frequencyCap.windowCount", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "frequencyCap.lifeCount", key = "errors.field.integer"),
        @ConversionErrorFieldValidator(fieldName = "dateStart", key = "errors.field.date"),
        @ConversionErrorFieldValidator(fieldName = "dateEnd", key = "errors.field.date")
    }
)
public class SaveCampaignAction extends EditSaveCampaignActionBase {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("channelNames", "'excludedChannels'", "violation.message")
            .add("channelNames[(#index)]", "'excludedChannels'", "channelNameError(groups[0], violation.message)")
            .add("excludedChannels[(#index)]", "'excludedChannels'", "channelNameError(groups[0], violation.message)")
            .rules();

    @EJB
    private SearchChannelService searchChannelService;
    private LinkedHashSet<Channel> excludedChannelsResolved = new LinkedHashSet<>();

    public SaveCampaignAction() {
        campaign = new Campaign();
        campaign.setAccount(new AdvertiserAccount());
    }

    @Validate(validation = "Campaign.update", parameters = "#target.prepareModel()")
    public String update() {
        if (hasErrors()) {
            return INPUT;
        }

        prepareForSave();

        campaignService.update(campaign);

        return SUCCESS;
    }

    @Validate(validation = "Campaign.create", parameters = "#target.prepareModel()")
    public String create() throws Exception {
        if (hasErrors()) {
            return INPUT;
        }

        prepareForSave();

        campaign.setCampaignType(CampaignType.byLetter(type));
        campaign.setAccount(new AdvertiserAccount(getCurrentAdvertiserId()));
        campaignService.create(campaign);

        return SUCCESS;
    }

    private void prepareForSave() {
        TimeZone timeZone = getTimeZone();
        Locale locale = CurrentUserSettingsHolder.getLocale();

        try {
            campaign.setDateStart(selectedDateStart.getDate(timeZone, locale));
        } catch (ParseException e) {
            addFieldError("dateStart", getText("errors.field.date"));
        }

        if (dateEndSet) {
            try {
                campaign.setDateEnd(selectedDateEnd.getDate(timeZone, locale));
            } catch (ParseException e) {
                addFieldError("dateEnd", getText("errors.field.date"));
            }
        } else {
            campaign.setDateEnd(null);
        }

        if (campaign.getId() == null) {
            campaign.setCampaignType(CampaignType.byLetter(type));
        }

        prepareDeliverySchedules();
        prepareBudget();

        prepareSalesManager();
        campaign.setExcludedChannels(excludedChannelsResolved);
    }

    private void prepareSalesManager() {
        if (campaign.getSalesManager() != null && campaign.getSalesManager().getId() == null) {
            campaign.setSalesManager(null);
        }
    }

    private void prepareBudget() {
        if (getBudgetType() == CampaignBudgetType.UNLIMITED) {
            campaign.setBudget(null);
        }
    }

    @SuppressWarnings("UnusedDeclaration") //used for web @Validate
    public Campaign prepareModel() {
        prepareDeliverySchedules();
        campaign.setCampaignType(CampaignType.byLetter(type));
        prepareBudget();
        prepareSalesManager();
        campaign.setAccount(new AdvertiserAccount(getCurrentAdvertiserId()));
        campaign.setExcludedChannels(excludedChannelsResolved);
        return campaign;
    }

    private void prepareDeliverySchedules() {
        Set<CampaignSchedule> campaignScheduleSet = new LinkedHashSet<>();

        if (isDeliverySchedule) {
            for (WeekSchedule schedule : getScheduleSet().getSchedules()) {
                CampaignSchedule campaignSchedule = new CampaignSchedule();
                campaignSchedule.setTimeFrom(schedule.getTimeFrom());
                campaignSchedule.setTimeTo(schedule.getTimeTo());
                campaignSchedule.setCampaign(campaign);
                campaignScheduleSet.add(campaignSchedule);
            }
        }

        campaign.setCampaignSchedules(campaignScheduleSet);
    }

    public String viewAffectedCCGSs() {
        return SUCCESS;
    }

    @Override
    public void validate() {
        if (dateEndSet == null) {
            addFieldError("dateEndSet", getText("errors.field.required"));
        }
        if (isDeliverySchedule() && getScheduleSet().isEmpty()) {
            addFieldError("deliverySchedule", getText("errors.delivery.slot.required"));
        }
        if (getBudgetType() == CampaignBudgetType.LIMITED && campaign.getBudget() == null
                && !getFieldErrors().containsKey("budget")) {
            addFieldError("budget", getText("errors.field.required"));
        }

        if (getExcludedChannelsList().size() > 0) {
            List<? extends Channel> channels = searchChannelService.resolveChannelTargets(
                    getExcludedChannelsList(), getStandaloneAccount(), getStandaloneAccount().getCountry());
            excludedChannelsResolved = new LinkedHashSet<Channel>(channels);
        }
    }

    @Override
    public Campaign getBreadcrumbsEntity() {
        return campaignService.find(campaign.getId());
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public String channelNameError(int index, String message) {
        String name = getExcludedChannelsList().get(index);
        return getText("errors.fieldError", new String[]{name, message});
    }
}

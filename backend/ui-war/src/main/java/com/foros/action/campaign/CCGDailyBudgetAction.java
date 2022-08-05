package com.foros.action.campaign;

import com.foros.action.BaseActionSupport;
import com.foros.model.account.Account;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignService;
import com.foros.util.AccountUtil;
import com.foros.util.DateHelper;
import com.foros.util.StringUtil;
import com.foros.web.taglib.NumberFormatter;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(
    conversionErrorFields = {
        @ConversionErrorFieldValidator(fieldName = "dailyBudget", key = "errors.field.number"),
        @ConversionErrorFieldValidator(fieldName = "totalBudget", key = "errors.field.number")
    }
)
public class CCGDailyBudgetAction extends BaseActionSupport {
    @EJB
    private CampaignCreativeGroupService service;
    @EJB
    private CampaignService campaignService;

    private Long ccgId;
    private Long campaignId;
    private Long accountId;
    private BigDecimal totalBudget;
    private BigDecimal dailyBudget;
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String text;

    public String checkDailyBudget() {
        text = "true";

        Locale locale = CurrentUserSettingsHolder.getLocale();
        TimeZone timeZone;

        if (ccgId != null) {
            CampaignCreativeGroup ccg = service.find(ccgId);
            timeZone = TimeZone.getTimeZone(ccg.getAccount().getTimezone().getKey());
        } else if (campaignId != null){
            Campaign campaign = campaignService.find(campaignId);
            timeZone = TimeZone.getTimeZone(campaign.getAccount().getTimezone().getKey());
        } else {
            Account account = AccountUtil.extractAccount(accountId);
            timeZone = TimeZone.getTimeZone(account.getTimezone().getKey());
        }

        Date startDate;
        Date endDate;

        try {
            startDate = getParsedDate(this.startDate, this.startTime, timeZone, locale);
            endDate = getParsedDate(this.endDate, this.endTime, timeZone, locale);
        } catch (ParseException e) {
            return SUCCESS;
        }

        if (startDate == null) {
            return SUCCESS;
        }

        if (totalBudget != null && dailyBudget != null &&
            !service.checkFixedDailyBudget(ccgId, campaignId, accountId, totalBudget, dailyBudget, startDate, endDate)) {
            text = "false";
        }
        
        return SUCCESS;
    }

    public String updateDynamicBudget() {
        text = "";

        Locale locale = CurrentUserSettingsHolder.getLocale();
        TimeZone timeZone;
        String accountCurrencyCode;

        if (ccgId != null) {
            CampaignCreativeGroup ccg = service.find(ccgId);
            timeZone = TimeZone.getTimeZone(ccg.getAccount().getTimezone().getKey());
            accountCurrencyCode = ccg.getAccount().getCurrency().getCurrencyCode();
        } else if (campaignId != null) {
            Campaign campaign = campaignService.find(campaignId);
            timeZone = TimeZone.getTimeZone(campaign.getAccount().getTimezone().getKey());
            accountCurrencyCode = campaign.getAccount().getCurrency().getCurrencyCode();
        } else {
            Account account = AccountUtil.extractAccount(accountId);
            timeZone = TimeZone.getTimeZone(account.getTimezone().getKey());
            accountCurrencyCode = account.getCurrency().getCurrencyCode();
        }

        Date startDate;
        Date endDate;

        try {
            startDate = getParsedDate(this.startDate, this.startTime, timeZone, locale);
            endDate = getParsedDate(this.endDate, this.endTime, timeZone, locale);
        } catch (ParseException e) {
            return SUCCESS;
        }

        if (startDate == null) {
            return SUCCESS;
        }

        BigDecimal dynamicDailyBudget = service.calculateDynamicDailyBudget(ccgId, campaignId,
                accountId, totalBudget, startDate, endDate);

        startDate = DateHelper.clearTime(startDate, timeZone);
        Date today = DateHelper.clearTime(new Date(), timeZone);

        if (today.before(startDate)) {
            today = startDate;
        }

        String formattedBudget = NumberFormatter.formatCurrency(dynamicDailyBudget, accountCurrencyCode);

        String formattedDate = DateHelper.formatDate(today, DateFormat.SHORT, timeZone, locale);

        text = StringUtil.getLocalizedString("ccg.daily.budget.dynamic", new String[] {formattedBudget, formattedDate});

        return SUCCESS;
    }

    private Date getParsedDate(String datePart, String timePart, TimeZone timeZone, Locale locale) throws ParseException {
        DateTimeBean startDateBean = new DateTimeBean();
        startDateBean.setDatePart(datePart);
        startDateBean.setTimePart(timePart);

        if (startDateBean.getIsEmpty()) {
            return null;
        }

        return startDateBean.getDate(timeZone, locale);
    }

    public Long getCcgId() {
        return ccgId;
    }

    public void setCcgId(Long ccgId) {
        this.ccgId = ccgId;
    }

    public Long getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Long campaignId) {
        this.campaignId = campaignId;
    }

    public BigDecimal getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(BigDecimal totalBudget) {
        this.totalBudget = totalBudget;
    }

    public BigDecimal getDailyBudget() {
        return dailyBudget;
    }

    public void setDailyBudget(BigDecimal dailyBudget) {
        this.dailyBudget = dailyBudget;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}

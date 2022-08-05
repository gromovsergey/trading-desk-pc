package com.foros.action.campaign;

import com.foros.framework.ReadOnly;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignType;
import com.foros.model.channel.Channel;
import com.foros.restriction.annotation.Restrict;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.campaign.CampaignUtil;
import com.foros.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import javax.ejb.EJB;

public class EditCampaignAction extends EditSaveCampaignActionBase {
    @EJB
    private WalledGardenService walledGardenService;

    private Long id;
    private Long advertiserId;

    @ReadOnly
    public String edit() {
        campaign = campaignService.findForEdit(id);
        existingAccount = campaign.getAccount();
        editBudget = CampaignUtil.canUpdateBudget(campaign);
        prepareForEdit();
        setBudgetType(campaign.getBudget() == null ? CampaignBudgetType.UNLIMITED : CampaignBudgetType.LIMITED);
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "Campaign.create", parameters = "#target.createEmptyCampaign()")
    public String create() {
        campaign = createEmptyCampaign();
        editBudget = CampaignUtil.canCreateBudget(getExistingAccount());

        if (!prepareForEdit()) {
            return ERROR;
        }

        if (isWalledGardenEnabled()) {
            campaign.setMarketplaceType(walledGardenService.findByAdvertiser(getCurrentAdvertiserId()).getAgencyMarketplaceType());
        }
        setBudgetType(CampaignBudgetType.LIMITED);
        return SUCCESS;
    }

    public Campaign createEmptyCampaign() {
        Campaign result = new Campaign();
        Long advId;

        if (advertiserId != null) {
            advId = advertiserId;
        } else {
            advId = SecurityContext.getPrincipal().getAccountId();
        }

        result.setAccount(new AdvertiserAccount(advId));
        result.setCampaignType(CampaignType.byLetter(type));
        return result;
    }

    private boolean prepareForEdit() {
        TimeZone timeZone = getTimeZone();
        Locale locale = CurrentUserSettingsHolder.getLocale();

        Date now = new Date();

        if (campaign.getDateStart() != null) {
            selectedDateStart.setDate(campaign.getDateStart(), timeZone, locale);
        } else if (campaign.getId() == null) {
            campaign.setDateStart(now);
            selectedDateStart.setDate(now, timeZone, locale);
        }

        if (campaign.getDateEnd() != null) {
            selectedDateEnd.setDate(campaign.getDateEnd(), timeZone, locale);
            dateEndSet = true;
        } else if (campaign.getId() != null) {
            dateEndSet = false;
        } else {
            campaign.setDateEnd(now);
            selectedDateEnd.setDate(now, timeZone, locale);
            dateEndSet = true;
        }

        if (campaign.getId() != null) {
            type = campaign.getCampaignType().getLetter();
        } else if (type != 'D' && type != 'T') {
            return false;
        }

        if (hasErrors() && getFieldErrors().containsKey("version")) {
            getScheduleSet().getSchedules().clear();
            getScheduleSet().getSchedules().addAll(campaign.getCampaignSchedules());
        }

        setDeliverySchedule(!getScheduleSet().isEmpty());

        prepareExcludedChannels();

        return true;
    }

    private void prepareExcludedChannels() {
        Set<? extends Channel> channels = campaign.getExcludedChannels();
        Long defaultAccountId = getStandaloneAccount().getId();
        ArrayList<String> names = new ArrayList<String>(channels.size());
        for (Channel channel : channels) {
            StringBuilder sb = new StringBuilder(channels.size() * 50);
            if (!defaultAccountId.equals(channel.getAccount().getId())) {
                sb.append(channel.getAccount().getName());
                sb.append('|');
            }
            sb.append(channel.getName());
            names.add(sb.toString());
        }
        Collections.sort(names, StringUtil.getLexicalComparator());
        setExcludedChannels(StringUtil.join(names));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    @Override
    public Campaign getBreadcrumbsEntity() {
        return campaign;
    }
}

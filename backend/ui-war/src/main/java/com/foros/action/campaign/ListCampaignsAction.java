package com.foros.action.campaign;

import com.foros.action.BaseActionSupport;
import com.foros.action.IdNameBean;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.DisplayStatus;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.SecurityContext;
import com.foros.session.account.AccountService;
import com.foros.session.campaign.CampaignService;
import com.foros.util.DateHelper;
import com.foros.util.context.AdvertiserContext;
import com.foros.util.context.RequestContexts;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.ejb.EJB;

public class ListCampaignsAction extends BaseActionSupport implements RequestContextsAware {
    @EJB
    private AccountService accountService;

    @EJB
    private CampaignService campaignService;

    private Long advertiserId;

    private Map<IdNameBean, List<CampaignCreativeGroupRowTO>> ccgTree;

    @ReadOnly
    public String list() {
        if (advertiserId == null && !SecurityContext.isInternal()) {
            advertiserId = SecurityContext.getPrincipal().getAccountId();
        }

        return SUCCESS;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public Map<IdNameBean, List<CampaignCreativeGroupRowTO>> getCcgTree() {
        if (ccgTree != null) {
            return ccgTree;
        }

        AdvertiserAccount account = accountService.findAdvertiserAccount(advertiserId);

        List<Object[]> rawCCGData = campaignService.getPendingCCGTreeRawDataForAccount(account);

        if (rawCCGData != null) {
            ccgTree = arrangeCampaignsTree(rawCCGData);
        } else {
            ccgTree = Collections.emptyMap();
        }

        return ccgTree;
    }

    private Map<IdNameBean, List<CampaignCreativeGroupRowTO>> arrangeCampaignsTree(List<Object[]> rawData) {
        Map<IdNameBean, List<CampaignCreativeGroupRowTO>> resultCCGTree = new LinkedHashMap<IdNameBean, List<CampaignCreativeGroupRowTO>>();

        Map<Long, TimeZone> timeZoneMap = new HashMap<Long, TimeZone>();

        for (Object[] dataRow : rawData) {
            Long campaignId = ((Number)dataRow[0]).longValue();
            String campaignName = (String) dataRow[1];
            DisplayStatus campaignDisplayStatus = Campaign.getDisplayStatus(((Number) dataRow[2]).longValue());
            IdNameBean campaign = new CampaignRowTO(campaignId, campaignName, campaignDisplayStatus);
            List<CampaignCreativeGroupRowTO> ccgBranch = resultCCGTree.get(campaign);

            if (ccgBranch == null) {
                ccgBranch = new LinkedList<CampaignCreativeGroupRowTO>();
                resultCCGTree.put(campaign, ccgBranch);
            }

            TimeZone accountTimeZone = timeZoneMap.get(campaignId);

            if (accountTimeZone == null) {
                accountTimeZone = TimeZone.getTimeZone(campaignService.find(campaignId).getAccount().getTimezone().getKey());
                timeZoneMap.put(campaignId, accountTimeZone);
            }

            CampaignCreativeGroupRowTO ccg = extractCCGRowTO(dataRow, accountTimeZone, CurrentUserSettingsHolder.getLocale());

            if (ccg != null) {
                ccgBranch.add(ccg);
            }
        }

        return resultCCGTree;
    }

    private CampaignCreativeGroupRowTO extractCCGRowTO(Object[] dataRow, TimeZone timeZone, Locale locale) {
        Object idObject = dataRow[3];
        CampaignCreativeGroupRowTO ccg = null;

        if (idObject != null) {
            String ccgId = idObject.toString();
            String ccgName = (String)dataRow[4];
            String ccgType = (String)dataRow[5];
            String ccgPageExtension = CCGType.valueOf(ccgType.charAt(0)).getPageExtension();

            DisplayStatus ccgDisplayStatus = CampaignCreativeGroup.getDisplayStatus(((Number) dataRow[6]).longValue());

            String impressionsCount = dataRow[7].toString();

            Date startDate = (Date) dataRow[8];
            String startDateString = DateHelper.formatDateTime(startDate, timeZone, locale);
            Date endDate = (Date) dataRow[9];
            String endDateString = DateHelper.formatDateTime(endDate, timeZone, locale);
            String dates = (endDate != null ) ? (startDate != null ? (startDateString + " - ") : "")
                    + endDateString : startDate != null ? startDateString : "";

            ccg = new CampaignCreativeGroupRowTO(ccgId, ccgName, ccgDisplayStatus, impressionsCount, dates, ccgPageExtension);
        }

        return ccg;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        AdvertiserContext advertiserContext = contexts.getAdvertiserContext();

        if (advertiserId == null && !SecurityContext.isInternal()) {
            advertiserId = SecurityContext.getPrincipal().getAccountId();
        }

        advertiserContext.switchTo(advertiserId);
    }

    private static class CampaignRowTO extends IdNameBean {
        private DisplayStatus displayStatus;

        public CampaignRowTO(Long id, String name, DisplayStatus displayStatus) {
            super(id.toString(), name);
            this.displayStatus = displayStatus;
        }

        public DisplayStatus getDisplayStatus() {
            return displayStatus;
        }

        public void setDisplayStatus(DisplayStatus displayStatus) {
            this.displayStatus = displayStatus;
        }
    }
}

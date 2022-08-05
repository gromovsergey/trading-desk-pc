package com.foros.action.reporting.advertiser;

import com.opensymphony.xwork2.ModelDriven;
import com.foros.action.BaseActionSupport;
import com.foros.action.reporting.treeFilter.TreeFilterActionSupport.TreeFilterHelper;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.AgencySelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.reporting.meta.ReportMetaData;
import com.foros.reporting.meta.olap.OlapColumn;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignService;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserMeta;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportParameters;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportService;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportState;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.util.context.AdvertiserContext;
import com.foros.util.context.RequestContexts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import org.apache.commons.collections.CollectionUtils;
import org.joda.time.LocalDate;

public abstract class ViewOlapAdvertiserReportActionBase
        extends BaseActionSupport
        implements ModelDriven<OlapAdvertiserReportParameters>, RequestContextsAware, AgencySelfIdAware, AdvertiserSelfIdAware {
    @EJB
    private WalledGardenService walledGardenService;

    @EJB
    private CampaignService campaignService;

    @EJB
    private CampaignCreativeGroupService groupService;

    private OlapAdvertiserReportState reportState;

    private OlapAdvertiserReportParameters parameters = new OlapAdvertiserReportParameters();
    private boolean switchContext;
    private AdvertiserContext advertiserContext;
    private Collection<OlapDetailLevel> detailLevels;
    private List<Long> selectedIds = new ArrayList<>();
    private boolean useRecommended = false;

    protected abstract OlapAdvertiserReportService getService();

    @ReadOnly
    public String view() throws Exception {
        parameters.setReportType(OlapDetailLevel.Campaign);

        initSelection();

        LocalDate yesterday = new LocalDate().minusDays(1);
        parameters.setDateRange(new DateRange(yesterday, yesterday));
        reportState = getService().getReportState(parameters, true);

        if (isCreditedColumnsAvailable()) {
            doUseRecommended();
        }

        return "success";
    }

    private void initSelection() {
        Campaign campaign = null;
        CampaignCreativeGroup ccg = null;
        AdvertiserAccount advertiserAccount = null;
        AdvertisingAccountBase contextAccount = null;

        if (!parameters.getCcgIds().isEmpty()) {
            ccg = groupService.find(parameters.getCcgIds().get(0));
            campaign = ccg.getCampaign();
            parameters.setReportType(getGroupLevel());
        } else if (!parameters.getCampaignIds().isEmpty()) {
            campaign = campaignService.find(parameters.getCampaignIds().get(0));
        }

        if (campaign != null) {
            advertiserAccount = campaign.getAccount();
        }

        if (advertiserAccount != null) {
            if (advertiserAccount.isStandalone()) {
                contextAccount = advertiserAccount;
            } else {
                contextAccount = advertiserAccount.getAgency();
            }
        }

        if (advertiserAccount != null) {
            TreeFilterHelper.addIdOrNull(selectedIds, advertiserAccount, campaign, ccg);
        }

        if (contextAccount != null) {
            parameters.setAccountId(contextAccount.getId());
        }

        if (parameters.getAccountId() != null) {
            switchContext = true;
            parameters.setCostAndRates(null);
        } else {
            // it's better to set account id to first available value (since it may be used to determine available columns)
            // but it's not important for now (it doesn't used in for OlapDetailLevel.Campaign)
            parameters.setCostAndRates(OlapAdvertiserReportParameters.CostAndRates.NET);
        }
    }
    protected abstract OlapDetailLevel getGroupLevel();

    @ReadOnly
    public String changeReportType() throws Exception {
        parameters.setColumns(new HashSet<String>());
        reportState = getService().getReportState(parameters, true);
        if (isCreditedColumnsAvailable()) {
            doUseRecommended();
        }
        return "success";
    }

    @ReadOnly
    public String redrawColumns() throws Exception {
        reportState = getService().getReportState(parameters, false);
        return "success";
    }

    @ReadOnly
    public String changeDateTime() throws Exception {
        reportState = getService().getReportState(parameters, false);
        if (useRecommended) {
            doUseRecommended();
        }
        return "success";
    }

    private void doUseRecommended() {
        Set<OlapColumn> recommended = getService().getRecommendedColumns(parameters);

        ReportMetaData<OlapColumn> newSelected = reportState.getSelected()
                .exclude(OlapAdvertiserMeta.CREDITED_COLUMNS)
                .include(recommended);

        reportState.setSelected(newSelected);
    }


    public OlapAdvertiserReportState getReportState() {
        return reportState;
    }

    @Override
    public OlapAdvertiserReportParameters getModel() {
        return parameters;
    }

    public boolean isSwitchContext() {
        return switchContext;
    }

    public void setSwitchContext(boolean switchContext) {
        this.switchContext = switchContext;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        if (switchContext) {
            advertiserContext = contexts.getAdvertiserContext();
            advertiserContext.switchTo(parameters.getAccountId());
        }
    }

    public Collection<OlapDetailLevel> getDetailLevels() {
        if (detailLevels != null) {
            return detailLevels;
        }

        boolean removeAdvertiserLevel = advertiserContext != null && !advertiserContext.isAgencyContext();

        if (removeAdvertiserLevel) {
            detailLevels = new ArrayList<OlapDetailLevel>(getAllDetailLevels());
            detailLevels.remove(OlapDetailLevel.Advertiser);
        } else {
            detailLevels = getAllDetailLevels();
        }

        return detailLevels;
    }

    protected abstract Collection<OlapDetailLevel> getAllDetailLevels();

    @Override
    public void setAdvertiserId(Long advertiserId) {
        parameters.setAccountId(advertiserId);
    }

    @Override
    public void setAgencyId(Long agencyId) {
        parameters.setAccountId(agencyId);
    }

    public List<Long> getSelectedIds() {
        return selectedIds;
    }

    public boolean isAgencyWalledGarden() {
        return walledGardenService.isAgencyWalledGarden(parameters.getAccountId());
    }

    private boolean isCreditedColumnsAvailable() {
        List<OlapColumn> metricsColumns = reportState.getAvailable().getMetricsColumns();
        return CollectionUtils.containsAny(metricsColumns, OlapAdvertiserMeta.CREDITED_COLUMNS);
    }

    public boolean isUseRecommended() {
        return useRecommended;
    }

    public void setUseRecommended(boolean useRecommended) {
        this.useRecommended = useRecommended;
    }
}

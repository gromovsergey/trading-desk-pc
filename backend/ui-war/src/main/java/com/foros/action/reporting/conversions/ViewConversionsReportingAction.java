package com.foros.action.reporting.conversions;

import com.foros.action.BaseActionSupport;
import com.foros.action.reporting.treeFilter.TreeFilterActionSupport.TreeFilterHelper;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.AgencySelfIdAware;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.reporting.meta.DbColumn;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.account.AccountService;
import com.foros.session.action.ActionService;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignService;
import com.foros.session.reporting.conversions.ConversionsMeta;
import com.foros.session.reporting.conversions.ConversionsReportParameters;
import com.foros.util.context.AdvertiserContext;
import com.foros.util.context.RequestContexts;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class ViewConversionsReportingAction extends BaseActionSupport
        implements RequestContextsAware, AgencySelfIdAware, AdvertiserSelfIdAware, ModelDriven<ConversionsReportParameters> {

    @EJB
    private ActionService actionService;

    @EJB
    private AccountService accountService;

    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @EJB
    private CampaignService campaignService;

    @EJB
    private CurrentUserService currentUserService;

    private ConversionsReportParameters parameters = new ConversionsReportParameters();

    private List<Long> byConversionSelectedIds = new ArrayList<>();
    private List<Long> byCampaignSelectedIds = new ArrayList<>();

    private boolean showPublisher;

    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'conversions'")
    public String view() throws Exception {
        initParameters();
        return SUCCESS;
    }

    private void initParameters() {
        Account account = null;
        if (!parameters.getConversionIds().isEmpty()) {
            Action action = actionService.findById(parameters.getConversionIds().get(0));
            account = action.getAccount();
        }

        if (!parameters.getGroupIds().isEmpty()) {
            CampaignCreativeGroup group = campaignCreativeGroupService.find(parameters.getGroupIds().get(0));
            account = group.getAccount();
            parameters.getCampaignIds().add(group.getCampaign().getId());
        } else if (!parameters.getCampaignIds().isEmpty()) {
            Campaign campaign = campaignService.find(parameters.getCampaignIds().get(0));
            account = campaign.getAccount();
        }

        if (account == null) {
            account = accountService.find(parameters.getAccountId());
        }
        updateAccountParameters(account);
        showPublisher = currentUserService.isInternal() || account.isPubConversionReportFlag();

        updateCampaignSelectedIds();
        updateConversionSelectedIds();
    }

    public boolean isShowPublisher() {
        return showPublisher;
    }

    private void updateConversionSelectedIds() {
        TreeFilterHelper.addIdOrNull(byConversionSelectedIds, parameters.getConversionAdvertiserIds());
        TreeFilterHelper.addIdOrNull(byConversionSelectedIds, parameters.getConversionIds());
    }

    private void updateCampaignSelectedIds() {
        TreeFilterHelper.addIdOrNull(byCampaignSelectedIds, parameters.getCampaignAdvertiserIds());
        TreeFilterHelper.addIdOrNull(byCampaignSelectedIds, parameters.getCampaignIds());
        TreeFilterHelper.addIdOrNull(byCampaignSelectedIds, parameters.getGroupIds());
        TreeFilterHelper.addIdOrNull(byCampaignSelectedIds, parameters.getCreativeIds());

    }

    private void updateAccountParameters(Account account) {
        if (account instanceof AdvertiserAccount && ((AdvertiserAccount) account).isInAgencyAdvertiser()) {
            parameters.getCampaignAdvertiserIds().add(account.getId());
            parameters.getConversionAdvertiserIds().add(account.getId());
            parameters.setAccountId(((AdvertiserAccount) account).getAgency().getId());
        } else {
            parameters.setAccountId(account.getId());
        }
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        parameters.setAccountId(advertiserId);
    }

    @Override
    public void setAgencyId(Long agencyId) {
        Long accountIdFormWeb = parameters.getAccountId();
        if (accountIdFormWeb != null && !agencyId.equals(parameters.getAccountId())) {
            parameters.getCampaignAdvertiserIds().add(accountIdFormWeb);
            parameters.getConversionAdvertiserIds().add(accountIdFormWeb);
        }
        parameters.setAccountId(agencyId);
    }

    @Override
    public ConversionsReportParameters getModel() {
        return parameters;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        AdvertiserContext advertiserContext = contexts.getAdvertiserContext();
        if (!parameters.getCampaignAdvertiserIds().isEmpty()) {
            advertiserContext.switchTo(parameters.getCampaignAdvertiserIds().get(0));
        } else {
            advertiserContext.switchTo(parameters.getAccountId());
        }
    }

    public boolean selected(String columnKey) {
        return ConversionsMeta.META.resolve(parameters).retain(ConversionsMeta.NOT_DEFAULT).contains(columnKey);
    }

    public boolean available(String columnKey) {
        return !ConversionsMeta.META.resolve(parameters)
                .retain(ConversionsMeta.POST_IMP_CONV, ConversionsMeta.POST_CLICK_CONV)
                .contains(columnKey);
    }

    public List<Long> getByConversionSelectedIds() {
        return TreeFilterHelper.getSelectedIds(byConversionSelectedIds);
    }

    public List<String> getAllColumns() {
        return new ArrayList<String>() {
            {
                for (DbColumn column : ConversionsMeta.META_BY_DATE.getOutputColumns()) {
                    add(column.getNameKey());
                }

                for (DbColumn column : ConversionsMeta.META_BY_DATE.getMetricsColumns()) {
                    add(column.getNameKey());
                }
            }
        };
    }


    public List<Long> getByCampaignSelectedIds() {
        return TreeFilterHelper.getSelectedIds(byCampaignSelectedIds);
    }
}

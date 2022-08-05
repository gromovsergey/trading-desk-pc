package com.foros.action.reporting.conversionPixels;

import com.foros.action.BaseActionSupport;
import com.foros.action.reporting.treeFilter.TreeFilterActionSupport.TreeFilterHelper;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.account.AccountService;
import com.foros.session.action.ActionService;
import com.foros.session.reporting.conversionPixels.ConversionPixelsReportParameters;
import com.foros.util.context.AdvertiserContext;
import com.foros.util.context.RequestContexts;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class ViewConversionPixelsReportingAction extends BaseActionSupport
        implements RequestContextsAware, ModelDriven<ConversionPixelsReportParameters> {

    @EJB
    private ActionService actionService;

    @EJB
    private AccountService accountService;

    private ConversionPixelsReportParameters parameters = new ConversionPixelsReportParameters();

    private List<Long> selectedIds = new ArrayList<>();

    @ReadOnly
    @Restrict(restriction = "Report.run", parameters = "'conversionPixels'")
    public String view() throws Exception {
        initParameters();
        return SUCCESS;
    }

    private void initParameters() {
        Account account = null;
        if (!parameters.getConversionIds().isEmpty()) {
            Action action = actionService.findById(parameters.getConversionIds().get(0));
            account = action.getAccount();
        } else {
            account = accountService.find(parameters.getAccountId());
        }

        updateAccountParameters(account);

        TreeFilterHelper.addIdOrNull(selectedIds, parameters.getConversionAdvertiserIds());
        TreeFilterHelper.addIdOrNull(selectedIds, parameters.getConversionIds());
    }

    private void updateAccountParameters(Account account) {
        if (account instanceof AdvertiserAccount && ((AdvertiserAccount) account).isInAgencyAdvertiser()) {
            parameters.getConversionAdvertiserIds().add(account.getId());
            parameters.setAccountId(((AdvertiserAccount) account).getAgency().getId());
        } else {
            parameters.setAccountId(account.getId());
        }
    }

    @Override
    public ConversionPixelsReportParameters getModel() {
        return parameters;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        AdvertiserContext advertiserContext = contexts.getAdvertiserContext();
        advertiserContext.switchTo(parameters.getAccountId());
    }

    public List<Long> getSelectedIds() {
        return TreeFilterHelper.getSelectedIds(selectedIds);
    }
}

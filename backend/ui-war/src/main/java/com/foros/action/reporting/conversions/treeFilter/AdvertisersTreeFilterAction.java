package com.foros.action.reporting.conversions.treeFilter;

import com.foros.session.TreeFilterElementTO;
import com.foros.session.account.AccountService;

import java.util.List;

import javax.ejb.EJB;

public class AdvertisersTreeFilterAction extends ConversrionsReportAbstractTreeFilterAction {

    @EJB
    private AccountService accountService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        return accountService.searchAdvertisersWithCampaigns(ownerId, null);
    }
    @Override
    public String getParameterName() {
        return "campaignAdvertiserIds";
    }

    @Override
    protected int getLevel() {
        return 0;
    }

}

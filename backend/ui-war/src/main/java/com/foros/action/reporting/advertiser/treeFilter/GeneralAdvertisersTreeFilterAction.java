package com.foros.action.reporting.advertiser.treeFilter;

import com.foros.session.TreeFilterElementTO;
import com.foros.session.account.AccountService;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;

import javax.ejb.EJB;
import java.util.List;

public class GeneralAdvertisersTreeFilterAction extends AdvertiserReportAbstractTreeFilterAction {

    @EJB
    private AccountService accountService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        return accountService.searchAdvertisersWithCampaigns(ownerId, null);
    }

    @Override
    protected OlapDetailLevel.Filter getCurrentLevel() {
        return OlapDetailLevel.Filter.Advertiser;
    }

    @Override
    public String getParameterName() {
        return "advertiserIds";
    }
}

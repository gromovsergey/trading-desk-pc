package com.foros.action.reporting.conversionPixels.treeFilter;

import com.foros.action.reporting.conversions.treeFilter.ConversrionsReportAbstractTreeFilterAction;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.account.AccountService;

import java.util.List;

import javax.ejb.EJB;

public class AdvertisersTreeFilterAction extends ConversrionsReportAbstractTreeFilterAction {

    @EJB
    private AccountService accountService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        return accountService.searchAdvertisersWithConversions(ownerId);
    }

    @Override
    public String getParameterName() {
        return "conversionAdvertiserIds";
    }

    @Override
    protected int getLevel() {
        return 0;
    }
}


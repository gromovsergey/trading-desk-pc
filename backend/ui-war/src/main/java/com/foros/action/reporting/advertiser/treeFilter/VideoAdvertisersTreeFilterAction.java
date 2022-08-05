package com.foros.action.reporting.advertiser.treeFilter;

import com.foros.model.creative.SizeType;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.account.AccountService;
import com.foros.session.creative.SizeTypeService;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;

import java.util.List;
import javax.ejb.EJB;

public class VideoAdvertisersTreeFilterAction extends AdvertiserReportAbstractTreeFilterAction {
    
    @EJB
    private AccountService accountService;

    @EJB
    private SizeTypeService sizeTypeService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        SizeType sizeType = sizeTypeService.findByName("Video");
        return accountService.searchAdvertisersBySizeTypeWithCampaigns(ownerId, sizeType.getId());
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

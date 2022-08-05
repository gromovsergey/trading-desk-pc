package com.foros.action.reporting.advertiser.treeFilter;

import com.foros.model.creative.SizeType;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignService;
import com.foros.session.creative.SizeTypeService;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;

import java.util.List;
import javax.ejb.EJB;

public class VideoCampaignsTreeFilterAction extends AdvertiserReportAbstractTreeFilterAction {

    @EJB
    private CampaignService campaignService;

    @EJB
    private SizeTypeService sizeTypeService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        SizeType sizeType = sizeTypeService.findByName("Video");
        return campaignService.searchCampaignsBySizeType(ownerId, sizeType.getId(), true);
    }

    @Override
    public String getParameterName() {
        return "campaignIds";
    }

    @Override
    protected OlapDetailLevel.Filter getCurrentLevel() {
        return OlapDetailLevel.Filter.Campaign;
    }
}

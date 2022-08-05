package com.foros.action.reporting.advertiser.treeFilter;

import com.foros.model.creative.SizeType;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignCreativeService;
import com.foros.session.creative.SizeTypeService;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;

import java.util.List;
import javax.ejb.EJB;

public class VideoCreativesTreeFilterAction extends AdvertiserReportAbstractTreeFilterAction {

    @EJB
    private CampaignCreativeService campaignCreativeService;

    @EJB
    private SizeTypeService sizeTypeService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        SizeType sizeType = sizeTypeService.findByName("Video");
        return campaignCreativeService.searchCreativesBySizeType(ownerId, sizeType.getId());
    }

    @Override
    public String getParameterName() {
        return "campaignCreativeIds";
    }

    @Override
    protected OlapDetailLevel.Filter getCurrentLevel() {
        return OlapDetailLevel.Filter.Creative;
    }
}

package com.foros.action.reporting.advertiser.treeFilter;

import com.foros.model.creative.SizeType;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.creative.SizeTypeService;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;

import java.util.List;
import javax.ejb.EJB;

public class VideoGroupsTreeFilterAction extends AdvertiserReportAbstractTreeFilterAction {

    @EJB
    private CampaignCreativeGroupService ccgService;

    @EJB
    private SizeTypeService sizeTypeService;

    @Override
    protected List<TreeFilterElementTO> generateOptions() {
        SizeType sizeType = sizeTypeService.findByName("Video");
        return ccgService.searchGroupsBySizeType(ownerId, sizeType.getId());
    }

    @Override
    public String getParameterName() {
        return "ccgIds";
    }

    @Override
    protected OlapDetailLevel.Filter getCurrentLevel() {
        return OlapDetailLevel.Filter.Group;
    }
}
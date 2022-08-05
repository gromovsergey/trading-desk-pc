package com.foros.action.admin.deviceChannel;

import com.foros.action.SearchForm;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.Platform;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.channel.service.PlatformService;
import com.foros.session.query.PartialList;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.ejb.EJB;

public class ViewDeviceChannelAction extends DeviceChannelActionSupport implements BreadcrumbsSupport {

    @EJB
    protected PlatformService platformService;

    private PartialList<CampaignCreativeGroup> associatedCampaignCreativeGroups;
    private SearchForm searchForm = new SearchForm();
    private Map<Long, Platform> platformMap;

    @ReadOnly
    @Restrict(restriction = "DeviceChannel.view")
    public String view() {
        Long channelId = getModel().getId();
        deviceChannel = deviceChannelService.view(channelId);
        loadPlatformExpression();
        searchForm.setPage(1L);
        searchForm.setPageSize(100);
        searchAssociatedCampaigns();
        childrenChannels = deviceChannelService.getChannelList(channelId);
        parentLocations = deviceChannelService.getChannelAncestorsChain(channelId, false);
        return SUCCESS;
    }

    @ReadOnly
    @Restrict(restriction = "DeviceChannel.view")
    public String searchAssociatedCampaigns() {
        associatedCampaignCreativeGroups = deviceChannelService.searchAssociatedCampaigns(deviceChannel.getId(),
                searchForm.getFirstResultCount(), searchForm.getPageSize());
        searchForm.setTotal((long) associatedCampaignCreativeGroups.getTotal());
        return SUCCESS;
    }

    private void loadPlatformExpression() {
        Collection<Platform> resultList = platformService.findByExpression(deviceChannel.getExpression());
        Map<Long, Platform> result = new LinkedHashMap<Long, Platform>();
        for (Platform platform : resultList) {
            result.put(platform.getId(), platform);
        }
        platformMap = result;
    }

    public Platform platformMap(Long id) {
        return platformMap.get(id);
    }

    public SearchForm getSearchForm() {
        return searchForm;
    }

    public void setSearchForm(SearchForm searchForm) {
        this.searchForm = searchForm;
    }

    public Long getPage() {
        return searchForm.getPage();
    }

    public void setPage(Long page) {
        searchForm.setPage(page);
    }

    public PartialList<CampaignCreativeGroup> getAssociatedCampaignCreativeGroups() {
        return associatedCampaignCreativeGroups;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return new Breadcrumbs().add(new DeviceChannelsBreadcrumbsElement()).add(new DeviceChannelBreadcrumbsElement(deviceChannel));
    }
}

package com.foros.rs.client.model.device;

import com.foros.rs.client.model.PagingSelectorContainer;
import com.foros.rs.client.model.entity.Status;
import com.foros.rs.client.model.operation.PagingSelector;
import com.foros.rs.client.util.QueryEntity;
import com.foros.rs.client.util.QueryParameter;

import java.util.List;

@QueryEntity
public class DeviceChannelSelector implements PagingSelectorContainer {

    @QueryParameter("paging")
    private PagingSelector paging;

    @QueryParameter("channel.ids")
    private List<Long> channelIds;

    @QueryParameter("parentChannel.ids")
    private List<Long> parentChannelIds;

    @QueryParameter("channel.statuses")
    private List<Status> channelStatuses;

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public List<Long> getParentChannelIds() {
        return parentChannelIds;
    }

    public void setParentChannelIds(List<Long> parentChannelIds) {
        this.parentChannelIds = parentChannelIds;
    }

    public List<Status> getChannelStatuses() {
        return channelStatuses;
    }

    public void setChannelStatuses(List<Status> channelStatuses) {
        this.channelStatuses = channelStatuses;
    }

    @Override
    public PagingSelector getPaging() {
        return this.paging;
    }

    @Override
    public void setPaging(PagingSelector paging) {
        this.paging = paging;
    }
}

package com.foros.session.channel.service;

import com.foros.model.Status;
import com.foros.model.channel.DeviceChannel;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Selector;
import com.foros.validation.constraint.IdCollectionConstraint;

import java.util.List;

public class DeviceChannelSelector implements Selector<DeviceChannel> {

    @IdCollectionConstraint
    private List<Long> channelIds;
    @IdCollectionConstraint
    private List<Long> parentChannelIds;
    private List<Status> channelStatuses;
    private Paging paging;

    public List<Long> getChannelIds() {
        return channelIds;
    }

    public List<Long> getParentChannelIds() {
        return parentChannelIds;
    }

    public List<Status> getChannelStatuses() {
        return channelStatuses;
    }

    @Override
    public Paging getPaging() {
        return paging;
    }

    public static class Builder {
        private List<Long> channelIds;
        private List<Long> parentChannelIds;
        private List<Status> channelStatuses;
        private Paging paging;

        public Builder channelIds(List<Long> channelIds) {
            this.channelIds = channelIds;
            return this;
        }

        public Builder parentChannelIds(List<Long> parentChannelIds) {
            this.parentChannelIds = parentChannelIds;
            return this;
        }

        public Builder channelStatuses(List<Status> channelStatuses) {
            this.channelStatuses = channelStatuses;
            return this;
        }

        public Builder paging(Paging paging) {
            this.paging = paging;
            return this;
        }

        public DeviceChannelSelector build() {
            return new DeviceChannelSelector(this);
        }
    }

    private DeviceChannelSelector(Builder builder) {
        this.channelIds = builder.channelIds;
        this.parentChannelIds = builder.parentChannelIds;
        this.channelStatuses = builder.channelStatuses;
        this.paging = builder.paging;
    }
}

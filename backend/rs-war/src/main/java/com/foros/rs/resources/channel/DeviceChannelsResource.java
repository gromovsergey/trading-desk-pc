package com.foros.rs.resources.channel;

import com.foros.model.Status;
import com.foros.model.channel.ApiDeviceChannelTO;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.channel.service.DeviceChannelSelector;
import com.foros.session.channel.service.SearchChannelService;

import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Produces(MediaType.APPLICATION_XML)
@Path("/channels/device/")
public class DeviceChannelsResource {

    @EJB
    private SearchChannelService searchChannelService;

    @GET
    public Result<ApiDeviceChannelTO> get(
            @QueryParam("channel.ids") List<Long> channelIds,
            @QueryParam("channel.statuses") List<Status> channelStatuses,
            @QueryParam("parentChannel.ids") List<Long> parentChannelIds,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
    ) {

        DeviceChannelSelector.Builder builder = new DeviceChannelSelector.Builder()
                .channelIds(channelIds)
                .parentChannelIds(parentChannelIds)
                .channelStatuses(channelStatuses);

        if (pagingFirst != null || pagingCount != null) {
            builder.paging(new Paging(pagingFirst, pagingCount));
        }

        Result<ApiDeviceChannelTO> channels = searchChannelService.getDeviceChannels(builder.build());
        return channels;
    }
}

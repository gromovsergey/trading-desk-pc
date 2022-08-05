package com.foros.rs.resources.channel;

import com.foros.model.channel.ApiGeoChannelTO;
import com.foros.model.channel.GeoType;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.GeoChannelSelector;
import com.foros.session.channel.service.SearchChannelService;

import java.security.AccessControlException;
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
@Path("/channels/geo/")
public class GeoChannelsResource {

    @EJB
    private SearchChannelService searchChannelService;

    @EJB
    private CurrentUserService currentUserService;

    @GET
    public Result<ApiGeoChannelTO> get(
            @QueryParam("channel.ids") List<Long> channelIds,
            @QueryParam("countryCodes") List<String> countryCodes,
            @QueryParam("parentChannel.ids") List<Long> parentChannelIds,
            @QueryParam("geoTypes") List<GeoType> geoTypes,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
            ) {

        if (currentUserService.getAccountRole() != AccountRole.INTERNAL) {
            throw new AccessControlException("Access is forbidden");
        }

        GeoChannelSelector.Builder builder = new GeoChannelSelector.Builder()
                .channelIds(channelIds)
                .countryCodes(countryCodes)
                .parentChannelIds(parentChannelIds)
                .geoTypes(geoTypes);

        if (pagingFirst != null || pagingCount != null) {
            builder.paging(new Paging(pagingFirst, pagingCount));
        }

        Result<ApiGeoChannelTO> channels = searchChannelService.getGeoChannels(builder.build());
        return channels;
    }

}

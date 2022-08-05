package com.foros.rs.resources.channel;

import com.foros.model.channel.DiscoverChannel;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.DiscoverChannelSelector;
import com.foros.session.channel.service.DiscoverChannelService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.validation.constraint.violation.parsing.ParseErrorsSupport;

import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_XML)
@Path("/channels/discover/")
public class DiscoverChannelResource {

    @EJB
    private DiscoverChannelService discoverChannelService;

    @EJB
    private SearchChannelService searchChannelService;

    @GET
    public Result<DiscoverChannel> get(
            @QueryParam("account.ids") List<Long> accountId,
            @QueryParam("channel.ids") List<Long> channelId,
            @QueryParam("name") String name,
            @QueryParam("countryCode") String countryCode,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
    ) {
        DiscoverChannelSelector channelSelector = new DiscoverChannelSelector();

        channelSelector.setAccountIds(accountId);
        channelSelector.setName(name);
        channelSelector.setCountryCode(countryCode);
        channelSelector.setChannelIds(channelId);

        if (pagingFirst != null || pagingCount != null) {
            channelSelector.setPaging(new Paging(pagingFirst, pagingCount));
        }

        Result<DiscoverChannel> channels = searchChannelService.getDiscover(channelSelector);
        return channels;
    }

    @POST
    public OperationsResult perform(Operations<DiscoverChannel> channelOperations) throws Exception {
        ParseErrorsSupport.throwIfAnyErrorsPresent(channelOperations);
        return discoverChannelService.perform(channelOperations);
    }

}

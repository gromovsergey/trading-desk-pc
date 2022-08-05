package com.foros.rs.resources.channel;

import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.service.RemoteServiceException;
import com.foros.session.CurrentUserService;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.ChannelSelector;
import com.foros.session.channel.service.AdvertisingChannelType;
import com.foros.session.channel.service.BulkChannelService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.validation.constraint.violation.ConstraintViolationException;
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
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
@Path("/channels/advertising/")
public class AdvertisingChannelsResource {

    @EJB
    private SearchChannelService searchChannelService;

    @EJB
    private BulkChannelService bulkChannelService;

    @EJB
    private CurrentUserService currentUserService;

    @GET
    public Result<Channel> get(
            @QueryParam("channel.ids") List<Long> channelId,
            @QueryParam("account.ids") List<Long> accountId,
            @QueryParam("name") String name,
            @QueryParam("countryCode") String countryCode,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount,
            @QueryParam("contents") String content,
            @QueryParam("visibility") List<ChannelVisibility> visibility,
            @QueryParam("type") List<AdvertisingChannelType> types
    ) {
        ChannelSelector channelSelector = new ChannelSelector();

        channelSelector.setChannelIds(channelId);
        channelSelector.setAccountIds(accountId);
        channelSelector.setName(name);
        channelSelector.setCountryCode(countryCode);
        channelSelector.setContent(content);
        channelSelector.setVisibility(visibility);
        channelSelector.setTypes(types);

        if (pagingFirst != null || pagingCount != null) {
            channelSelector.setPaging(new Paging(pagingFirst, pagingCount));
        }

        try {
            Result<Channel> channels = searchChannelService.get(channelSelector);
            return channels;
        } catch (RemoteServiceException e) {
            throw ConstraintViolationException.newBuilder("errors.serviceIsNotAvailable")
                    .withParameters("{channel.channelSearchService}")
                    .build();
        }
    }

    @POST
    public OperationsResult perform(Operations<Channel> channelOperations) throws Exception {
        ParseErrorsSupport.throwIfAnyErrorsPresent(channelOperations);
        return bulkChannelService.perform(channelOperations);
    }

}

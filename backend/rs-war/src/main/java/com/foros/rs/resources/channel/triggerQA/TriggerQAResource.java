package com.foros.rs.resources.channel.triggerQA;

import com.foros.model.ApproveStatus;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.channel.triggerQA.TriggerQASelector;
import com.foros.session.channel.triggerQA.TriggerQAService;
import com.foros.session.channel.triggerQA.TriggerQATO;
import com.foros.validation.constraint.violation.parsing.ParseErrorsSupport;

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
@Path("/channels/triggerQA/")
public class TriggerQAResource {
    @EJB
    private TriggerQAService triggerQAService;

    @EJB
    private CampaignService campaignService;

    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @EJB
    private SearchChannelService searchChannelService;

    @GET
    public Result<TriggerQATO> get(
            @QueryParam("campaign.id") Long campaignId,
            @QueryParam("group.id") Long groupId,
            @QueryParam("channel.id") Long channelId,
            @QueryParam("trigger.status") ApproveStatus triggerStatus,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
    ) {
        TriggerQASelector triggerQASelector = new TriggerQASelector(campaignId, groupId, channelId, triggerStatus, pagingFirst, pagingCount);
        return triggerQAService.get(triggerQASelector);
    }

    @POST
    public OperationsResult perform(Operations<TriggerQATO> triggerOperations) throws Exception {
        ParseErrorsSupport.throwIfAnyErrorsPresent(triggerOperations);
        return triggerQAService.perform(triggerOperations);
    }
}

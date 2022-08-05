package com.foros.rs.resources.campaign;

import com.foros.model.Status;
import com.foros.model.campaign.CampaignCreative;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.CampaignCreativeService;
import com.foros.session.campaign.bulk.CreativeLinkSelector;
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
@Path("/creativeLinks/")
public class CreativeLinksResource {

    @EJB
    private CampaignCreativeService campaignCreativeService;

    @GET
    public Result<CampaignCreative> get(
            @QueryParam("advertiser.ids") List<Long> accountIds,
            @QueryParam("campaign.ids") List<Long> campaignIds,
            @QueryParam("group.ids") List<Long> ccgIds,
            @QueryParam("creative.ids") List<Long> creativeIds,
            @QueryParam("link.ids") List<Long> creativeLinkIds,
            @QueryParam("link.statuses") List<Status> creativeLinkStatuses,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
    ) {
        CreativeLinkSelector creativeLinkSelector = new CreativeLinkSelector();

        creativeLinkSelector.setAdvertiserIds(accountIds);
        creativeLinkSelector.setCampaigns(campaignIds);
        creativeLinkSelector.setCreativeGroups(ccgIds);
        creativeLinkSelector.setCreatives(creativeIds);
        creativeLinkSelector.setCreativeLinks(creativeLinkIds);
        creativeLinkSelector.setStatuses(creativeLinkStatuses);

        if (pagingFirst != null || pagingCount != null) {
            creativeLinkSelector.setPaging(new Paging(pagingFirst, pagingCount));
        }

        return campaignCreativeService.get(creativeLinkSelector);
    }

    @POST
    public OperationsResult perform(Operations<CampaignCreative> creativeLinkOperations) {
        ParseErrorsSupport.throwIfAnyErrorsPresent(creativeLinkOperations);
        return campaignCreativeService.perform(creativeLinkOperations);
    }
}

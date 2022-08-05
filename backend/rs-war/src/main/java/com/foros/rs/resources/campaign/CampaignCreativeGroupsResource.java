package com.foros.rs.resources.campaign;

import com.foros.model.Status;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.bulk.CampaignCreativeGroupSelector;
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
@Path("/creativeGroups/")
public class CampaignCreativeGroupsResource {

    @EJB
    private CampaignCreativeGroupService groupService;

    @GET
    public Result<CampaignCreativeGroup> get(
            @QueryParam("advertiser.ids") List<Long> accountIds,
            @QueryParam("campaign.ids") List<Long> campaignIds,
            @QueryParam("campaign.type") CampaignType campaignType,
            @QueryParam("group.ids") List<Long> ccgIds,
            @QueryParam("group.statuses") List<Status> ccgStatuses,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
    ) {
        CampaignCreativeGroupSelector ccgSelector = new CampaignCreativeGroupSelector();

        ccgSelector.setAdvertiserIds(accountIds);
        ccgSelector.setCampaigns(campaignIds);
        ccgSelector.setCampaignType(campaignType);
        ccgSelector.setCreativeGroups(ccgIds);
        ccgSelector.setStatuses(ccgStatuses);

        if (pagingFirst != null || pagingCount != null) {
            ccgSelector.setPaging(new Paging(pagingFirst, pagingCount));
        }

        return groupService.get(ccgSelector);
    }

    @POST
    public OperationsResult perform(Operations<CampaignCreativeGroup> ccgOperations) {
        ParseErrorsSupport.throwIfAnyErrorsPresent(ccgOperations);
        return groupService.perform(ccgOperations);
    }
}

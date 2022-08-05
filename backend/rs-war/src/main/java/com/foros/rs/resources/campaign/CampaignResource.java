package com.foros.rs.resources.campaign;

import com.foros.model.Status;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.CampaignRestrictions;
import com.foros.session.campaign.CampaignService;
import com.foros.session.campaign.bulk.CampaignSelector;
import com.foros.validation.constraint.violation.parsing.ParseErrorsSupport;

import java.util.ArrayList;
import java.util.Collections;
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
@Path("/campaigns/")
public class CampaignResource {

    @EJB
    private CampaignService campaignService;

    @EJB
    private CampaignRestrictions campaignRestrictions;

    @GET
    public Result<Campaign> get(
            @QueryParam("advertiser.ids") List<Long> accountIds,
            @QueryParam("campaign.ids") List<Long> campaignIds,
            @QueryParam("campaign.statuses") List<Status> campaignStatuses,
            @QueryParam("campaign.type") CampaignType campaignType,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
    ) {
        CampaignSelector campaignSelector = new CampaignSelector();

        campaignSelector.setAdvertiserIds(accountIds);
        campaignSelector.setCampaigns(campaignIds);
        campaignSelector.setStatuses(campaignStatuses);
        campaignSelector.setCampaignType(campaignType);

        if (pagingFirst != null || pagingCount != null) {
            campaignSelector.setPaging(new Paging(pagingFirst, pagingCount));
        }

        Result<Campaign> result = campaignService.get(campaignSelector); 
        for (Campaign c: result.getEntities()) {
            if (!campaignRestrictions.canViewCommission(c)) {
                c.setCommission(null);
            }
        }
        return result;
    }

    @POST
    @Path("makecopy")
    public OperationsResult makeCopyCampaign(Long sourceCampaignId) {
        if (sourceCampaignId == null) {
            return null;
        }

        Long newCampaignId = campaignService.createCopy(sourceCampaignId);
        Campaign newCampaign = campaignService.find(newCampaignId);

        ArrayList createdIds = new ArrayList(newCampaign.getCreativeGroups().size() + 1);
        createdIds.add(newCampaignId);
        for (CampaignCreativeGroup ccg: newCampaign.getCreativeGroups()) {
            createdIds.add(ccg.getId());
        }

        return new OperationsResult(createdIds);
    }

    @POST
    public OperationsResult perform(Operations<Campaign> campaignOperations) {
        ParseErrorsSupport.throwIfAnyErrorsPresent(campaignOperations);
        return campaignService.perform(campaignOperations);
    }
}

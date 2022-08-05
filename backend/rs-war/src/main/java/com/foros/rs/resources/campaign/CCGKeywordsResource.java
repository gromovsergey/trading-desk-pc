package com.foros.rs.resources.campaign;

import com.foros.model.Status;
import com.foros.model.campaign.CCGKeyword;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.CCGKeywordService;
import com.foros.session.campaign.bulk.CCGKeywordSelector;
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
@Path("/keywords/")
public class CCGKeywordsResource {

    @EJB
    private CCGKeywordService keywordService;

    @GET
    public Result<CCGKeyword> get(
            @QueryParam("advertiser.ids") List<Long> accountIds,
            @QueryParam("campaign.ids") List<Long> campaignIds,
            @QueryParam("group.ids") List<Long> ccgIds,
            @QueryParam("keyword.ids") List<Long> keywordIds,
            @QueryParam("keyword.statuses") List<Status> keywordStatuses,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
    ) {
        CCGKeywordSelector keywordSelector = new CCGKeywordSelector();

        keywordSelector.setAdvertiserIds(accountIds);
        keywordSelector.setCampaigns(campaignIds);
        keywordSelector.setCreativeGroups(ccgIds);
        keywordSelector.setKeywords(keywordIds);
        keywordSelector.setStatuses(keywordStatuses);

        if (pagingFirst != null || pagingCount != null) {
            keywordSelector.setPaging(new Paging(pagingFirst, pagingCount));
        }

        return keywordService.get(keywordSelector);
    }

    @POST
    public OperationsResult perform(Operations<CCGKeyword> operations) {
        ParseErrorsSupport.throwIfAnyErrorsPresent(operations);
        return keywordService.perform(operations);
    }

}

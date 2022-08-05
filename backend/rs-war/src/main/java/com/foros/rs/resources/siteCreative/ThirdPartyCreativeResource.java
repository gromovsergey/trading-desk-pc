package com.foros.rs.resources.siteCreative;

import com.foros.model.site.SiteCreativeApprovalStatus;
import com.foros.model.site.ThirdPartyCreative;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.query.PartialList;
import com.foros.session.site.SiteService;
import com.foros.session.site.creativeApproval.CreativeExclusionBySiteSelector;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalService;
import com.foros.session.site.creativeApproval.ThirdPartyCreativesUpdateOperations;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.constraint.violation.parsing.ParseErrorsSupport;

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import java.util.HashSet;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@RequestScoped
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
@Path("/thirdPartyCreatives/")
public class ThirdPartyCreativeResource {
    @EJB
    private SiteCreativeApprovalService siteCreativeApprovalService;

    @EJB
    private SiteService siteService;

    @GET
    public Result<ThirdPartyCreative> get(
            @QueryParam("site.id") Long siteId,
            @QueryParam("creative.id") List<Long> creativeIds,
            @QueryParam("status") List<SiteCreativeApprovalStatus> statuses,
            @QueryParam("pendingThirdPartyApproval") Boolean pendingThirdPartyApproval,
            @QueryParam("hasThirdPartyId") Boolean hasThirdPartyId,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
    ) {
        CreativeExclusionBySiteSelector selector = new CreativeExclusionBySiteSelector();
        selector.setSiteId(siteId);
        selector.setPendingThirdPartyApproval(pendingThirdPartyApproval);
        selector.setHasThirdPartyId(hasThirdPartyId);

        if (!statuses.isEmpty()) {
            selector.setApprovals(new HashSet<SiteCreativeApprovalStatus>(statuses));
        }

        if (!creativeIds.isEmpty()) {
            selector.setCreativeIds(new HashSet<Long>(creativeIds));
        }

        if (pagingFirst != null || pagingCount != null) {
            selector.setPaging(new Paging(pagingFirst, pagingCount));
        }

        PartialList<ThirdPartyCreative> res = siteCreativeApprovalService.searchThirdParty(selector);
        return new Result<>(res);
    }

    @POST
    public Response perform(ThirdPartyCreativesUpdateOperations operations) {
        ParseErrorsSupport.throwIfAnyErrorsPresent(operations);
        throwIfSiteInvalid(operations);
        siteCreativeApprovalService.perform(operations);
        return new ResponseBuilderImpl().status(204).build();
    }

    private void throwIfSiteInvalid(ThirdPartyCreativesUpdateOperations operations) {
        try {
            siteService.find(operations.getSiteId());
        } catch (EntityNotFoundException e) {
            throw ConstraintViolationException.newBuilder("errors.entity.notFound")
                    .withPath("site.id")
                    .withError(BusinessErrors.ENTITY_NOT_FOUND)
                    .withValue(operations.getSiteId())
                    .build();
        }
    }
}

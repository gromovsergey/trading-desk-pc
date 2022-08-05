package com.foros.rs.resources.siteCreative;

import com.foros.model.creative.CreativeSize;
import com.foros.model.site.SiteCreativeApprovalStatus;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.query.PartialList;
import com.foros.session.site.creativeApproval.CreativeExclusionBySiteSelector;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalOperations;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalService;
import com.foros.session.site.creativeApproval.SiteCreativeApprovalTO;
import com.foros.util.StringUtil;
import com.foros.validation.constraint.violation.parsing.ParseErrorsSupport;

import com.sun.jersey.core.spi.factory.ResponseBuilderImpl;
import java.sql.Timestamp;
import java.util.HashSet;
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
import javax.ws.rs.core.Response;

@RequestScoped
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
@Path("/siteCreativeApprovals/")
public class SiteCreativeApprovalsResource {

    @EJB
    private SiteCreativeApprovalService siteCreativeApprovalService;

    @EJB
    private CreativeSizeService creativeSizeService;

    @GET
    public Result<SiteCreativeApprovalTO> get(
            @QueryParam("site.id") Long siteId,
            @QueryParam("status") List<SiteCreativeApprovalStatus> statuses,
            @QueryParam("creative.updatedSince") Timestamp minCreativeVersion,
            @QueryParam("creative.id") List<Long> creativeIds,
            @QueryParam("size.name") String sizeName,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
    ) {
        CreativeExclusionBySiteSelector selector = new CreativeExclusionBySiteSelector();
        selector.setSiteId(siteId);

        if (!statuses.isEmpty()) {
            selector.setApprovals(new HashSet<SiteCreativeApprovalStatus>(statuses));
        }

        selector.setMinCreativeVersion(minCreativeVersion);

        if (!creativeIds.isEmpty()) {
            selector.setCreativeIds(new HashSet<Long>(creativeIds));
        }

        if (StringUtil.isPropertyNotEmpty(sizeName)) {
            CreativeSize size = creativeSizeService.findByName(sizeName);
            if (size != null) {
                selector.setSizeId(size.getId());
            } else {
                return new Result<>(PartialList.<SiteCreativeApprovalTO>emptyList());
            }
        }

        if (pagingFirst != null || pagingCount != null) {
            selector.setPaging(new Paging(pagingFirst, pagingCount));
        }

        PartialList<SiteCreativeApprovalTO> res = siteCreativeApprovalService.searchCreativeApprovals(selector);
        return new Result<>(res);
    }

    @POST
    public Response perform(SiteCreativeApprovalOperations approvalOperations) {
        ParseErrorsSupport.throwIfAnyErrorsPresent(approvalOperations);
        siteCreativeApprovalService.perform(approvalOperations);
        return new ResponseBuilderImpl().status(204).build();
    }
}

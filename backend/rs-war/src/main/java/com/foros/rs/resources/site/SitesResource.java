package com.foros.rs.resources.site;

import com.foros.model.Status;
import com.foros.model.site.Site;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.site.SiteSelector;
import com.foros.session.site.SiteService;

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
@Path("/sites/")
public class SitesResource {

    @EJB
    private SiteService siteService;

    @EJB
    private CurrentUserService currentUserService;

    @GET
    public Result<Site> get(
            @QueryParam("site.ids") List<Long> siteIds,
            @QueryParam("account.ids") List<Long> accountIds,
            @QueryParam("site.statuses") List<Status> siteStatuses,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
            ) {

        if (currentUserService.getAccountRole() != AccountRole.INTERNAL) {
            throw new AccessControlException("Access is forbidden");
        }

        SiteSelector.Builder builder = new SiteSelector.Builder().accountIds(accountIds).siteIds(siteIds).siteStatuses(siteStatuses);

        if (pagingFirst != null || pagingCount != null) {
            builder.paging(new Paging(pagingFirst, pagingCount));
        }

        return siteService.get(builder.build());
    }

}

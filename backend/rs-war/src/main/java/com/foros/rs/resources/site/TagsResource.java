package com.foros.rs.resources.site;

import com.foros.model.Status;
import com.foros.model.site.Tag;
import com.foros.model.site.TagEffectiveSizes;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.site.TagSelector;
import com.foros.session.site.TagsService;

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
@Path("/tags/")
public class TagsResource {

    @EJB
    private TagsService tagsService;

    @EJB
    private CurrentUserService currentUserService;

    @GET
    public Result<Tag> get(
            @QueryParam("tag.ids") List<Long> tagIds,
            @QueryParam("site.ids") List<Long> siteIds,
            @QueryParam("tag.statuses") List<Status> tagStatuses,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
    ) {

        if (currentUserService.getAccountRole() != AccountRole.INTERNAL) {
            throw new AccessControlException("Access is forbidden");
        }

        TagSelector.Builder builder = new TagSelector.Builder().tagIds(tagIds).siteIds(siteIds).tagStatuses(tagStatuses);

        if (pagingFirst != null || pagingCount != null) {
            builder.paging(new Paging(pagingFirst, pagingCount));
        }

        return tagsService.get(builder.build());
    }

    @GET
    @Path("effectiveSizes")
    public TagEffectiveSizes getEffectiveSizes(
            @QueryParam("tag.id") Long tagId) {

        if (currentUserService.getAccountRole() != AccountRole.INTERNAL) {
            throw new AccessControlException("Access is forbidden");
        }

        return tagsService.getEffectiveSizes(tagId);
    }
}

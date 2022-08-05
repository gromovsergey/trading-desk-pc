package com.foros.rs.resources.creativeCategory;

import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.session.bulk.Result;
import com.foros.session.creative.CreativeCategorySelector;
import com.foros.session.creative.CreativeCategoryService;

import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
@Path("/creativeCategories/")
public class CreativeCategoryResource {

    @EJB
    private CreativeCategoryService creativeCategoryService;

    @GET
    public Result<CreativeCategory> get(
            @QueryParam("ids") List<Long> ids,
            @QueryParam("type") CreativeCategoryType type,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
    ) {
        CreativeCategorySelector.Builder builder = new CreativeCategorySelector.Builder().ids(ids).type(type)
                .first(pagingFirst).count(pagingCount);
        return creativeCategoryService.get(builder.build());
    }
}

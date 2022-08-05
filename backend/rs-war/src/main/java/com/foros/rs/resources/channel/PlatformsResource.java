package com.foros.rs.resources.channel;

import com.foros.model.channel.Platform;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.channel.service.PlatformService;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Produces(MediaType.APPLICATION_XML)
@Path("/platforms")
public class PlatformsResource {

    @EJB
    private PlatformService platformService;

    @GET
    public Result<Platform> get(
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
    ) {
        return platformService.get(new Paging(pagingFirst, pagingCount));
    }

}

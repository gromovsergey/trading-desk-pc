package com.foros.rs.resources.colocation;

import com.foros.model.Status;
import com.foros.model.isp.Colocation;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.colocation.ColocationSelector;
import com.foros.session.colocation.ColocationService;
import com.foros.session.query.PartialList;

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
@Path("/colocations/")
public class ColocationResource {
    @EJB
    private ColocationService colocationService;

    @GET
    public Result<Colocation> get(@QueryParam("colocation.name") String name,
                                  @QueryParam("colocation.ids") List<Long> colocationIds,
                                  @QueryParam("account.ids") List<Long> accountIds,
                                  @QueryParam("colocation.statuses") List<Status> statuses,
                                  @QueryParam("paging.first") Integer pagingFirst,
                                  @QueryParam("paging.count") Integer pagingCount) {
        ColocationSelector selector = new ColocationSelector();

        selector.setName(name);
        selector.setColocationIds(colocationIds);
        selector.setAccountIds(accountIds);
        selector.setStatuses(statuses);

        if (pagingFirst != null || pagingCount != null) {
            selector.setPaging(new Paging(pagingFirst, pagingCount));
        }

        PartialList<Colocation> colocations = colocationService.get(selector);
        return new Result<>(colocations);
    }
}

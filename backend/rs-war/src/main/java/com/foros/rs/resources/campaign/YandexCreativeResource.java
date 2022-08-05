package com.foros.rs.resources.campaign;

import com.foros.model.creative.YandexCreativeTO;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.session.campaign.bulk.YandexCreativeSelector;
import com.foros.session.creative.DisplayCreativeService;

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
@Path("/yandexCreatives/")
public class YandexCreativeResource {

    @EJB
    private DisplayCreativeService displayCreativeService;

    @GET
    public Result<YandexCreativeTO> get(@QueryParam("creative.ids") List<Long> creativeIds,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount) {
        YandexCreativeSelector selector = new YandexCreativeSelector();

        selector.setCreatives(creativeIds);
        if (pagingFirst != null || pagingCount != null) {
            selector.setPaging(new Paging(pagingFirst, pagingCount));
        }

        return displayCreativeService.getYandexCreativeTO(selector);
    }
}

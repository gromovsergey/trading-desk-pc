package com.foros.rs.resources.template;

import com.foros.model.creative.CreativeSize;
import com.foros.session.creative.CreativeSizeService;

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
@Path("/creativeSize/")
public class CreativeSizeResource {
    @EJB
    private CreativeSizeService creativeSizeService;

    @GET
    public CreativeSize get(@QueryParam("id") Long id) {
        CreativeSize size = creativeSizeService.view(id);
        XmlEntityUtils.removeVersionRecursively(size);
        return size;
    }
}

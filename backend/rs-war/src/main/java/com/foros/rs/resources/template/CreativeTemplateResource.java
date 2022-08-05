package com.foros.rs.resources.template;

import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Template;
import com.foros.session.template.TemplateService;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityNotFoundException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@RequestScoped
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
@Path("/creativeTemplate/")
public class CreativeTemplateResource {
    @EJB
    private TemplateService templateService;

    @GET
    public CreativeTemplate get(@QueryParam("id") Long id) {
        Template result = templateService.view(id);
        if (!(result instanceof CreativeTemplate)) {
            throw new EntityNotFoundException("Template with id=" + id + " not found");
        }
        XmlEntityUtils.removeVersionRecursively(result);

        return (CreativeTemplate)result;
    }
}

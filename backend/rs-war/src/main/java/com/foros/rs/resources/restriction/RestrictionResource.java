package com.foros.rs.resources.restriction;

import com.foros.model.restriction.Predicates;
import com.foros.model.restriction.RestrictionCommandsOperation;
import com.foros.session.restriction.LookupRestrictionService;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Path;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@RequestScoped
@Produces(MediaType.APPLICATION_XML)
@Path("/restriction")
public class RestrictionResource {

    @EJB
    private LookupRestrictionService lookupRestrictionService;

    @POST
    public Predicates get(RestrictionCommandsOperation operation) {
        return lookupRestrictionService.lookupAndInvoke(operation);
    }
}

package com.foros.rs.resources.action;

import com.foros.jaxb.adapters.CampaignGroupLink;
import com.foros.model.Status;
import com.foros.model.action.Action;
import com.foros.session.action.ActionSelector;
import com.foros.session.action.ActionService;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Paging;
import com.foros.session.bulk.Result;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.constraint.violation.parsing.ParseErrorsSupport;

import java.util.Collection;
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
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@RequestScoped
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
@Path("/conversions/")
public class ActionsResource {

    @EJB
    private ActionService actionService;

    @GET
    public Result<Action> get(
            @QueryParam("advertiser.ids") List<Long> accountIds,
            @QueryParam("conversion.ids") List<Long> actionIds,
            @QueryParam("conversion.statuses") List<Status> actionStatuses,
            @QueryParam("paging.first") Integer pagingFirst,
            @QueryParam("paging.count") Integer pagingCount
            ) {
        ActionSelector selector = new ActionSelector();

        selector.setAdvertiserIds(accountIds);
        selector.setActionIds(actionIds);
        selector.setActionStatuses(actionStatuses);

        if (pagingFirst != null || pagingCount != null) {
            selector.setPaging(new Paging(pagingFirst, pagingCount));
        }

        return actionService.get(selector);
    }

    @POST
    public OperationsResult perform(Operations<Action> operations) {
        ParseErrorsSupport.throwIfAnyErrorsPresent(operations);
        return actionService.perform(operations);
    }

    @GET
    @Path("/associations/")
    public AssociationTO getAssociations(
            @QueryParam("conversion.id") Long actionId) {
        if (actionId == null) {
            throw ConstraintViolationException.newBuilder("errors.required")
                .withParameters("conversion.id")
                .withPath("conversion.id")
                .build();
        }

        return new AssociationTO(actionService.getAssociations(actionId));
    }

    @XmlRootElement(name = "conversionAssociations")
    @XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
    public static class AssociationTO {

        private Collection<CampaignGroupLink> groups;

        public AssociationTO() {
        }

        public AssociationTO(Collection<CampaignGroupLink> groups) {
            this.groups = groups;
        }

        @XmlElementWrapper(name = "creativeGroups")
        @XmlElement(name = "creativeGroup")
        public Collection<CampaignGroupLink> getGroups() {
            return groups;
        }

    }
}

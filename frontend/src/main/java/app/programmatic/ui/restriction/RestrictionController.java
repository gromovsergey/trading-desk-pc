package app.programmatic.ui.restriction;

import app.programmatic.ui.flight.service.FlightServiceInternal;
import app.programmatic.ui.flight.service.LineItemServiceInternal;
import app.programmatic.ui.restriction.model.Restriction;
import app.programmatic.ui.restriction.model.RestrictionCommandBuilder;
import app.programmatic.ui.common.restriction.service.LocalRestrictionServiceImpl;
import app.programmatic.ui.restriction.service.RestrictionService;
import app.programmatic.ui.restriction.tool.RestrictionDefinitions;
import app.programmatic.ui.restriction.view.RestrictionResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class RestrictionController {

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private LocalRestrictionServiceImpl localRestrictionService;

    @Autowired
    private FlightServiceInternal flightServiceInternal;

    @Autowired
    private LineItemServiceInternal lineItemServiceInternal;

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/restriction", produces = "application/json")
    public List<RestrictionResponse> isAllowed(@RequestParam(value = "name") String name, @RequestBody List<Long> entityIds)
            throws HttpMessageNotReadableException {

        // Old UI Restriction Service
        Restriction restriction = RestrictionDefinitions.find(name);
        if (restriction == null) {
            throw new HttpMessageNotReadableException("Can't find restriction: " + name);
        }

        if (entityIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> translatedIds = translateIds(name, entityIds);

        RestrictionCommandBuilder commandBuilder = new RestrictionCommandBuilder();
        translatedIds.stream().forEach(id ->
                commandBuilder.add(restriction, id)
        );

        Iterator<Long> initialIdsIt = entityIds.iterator();
        return restrictionService.isPermitted(commandBuilder).stream()
                .map(b -> new RestrictionResponse(initialIdsIt.next(), b))
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/restriction", produces = "application/json")
    public Boolean isAllowed0(@RequestParam(value = "name") String name) throws HttpMessageNotReadableException {
        Restriction restriction = RestrictionDefinitions.find(name);
        if (restriction == null) {
            throw new HttpMessageNotReadableException("Can't find restriction: " + name);
        }

        RestrictionCommandBuilder commandBuilder = new RestrictionCommandBuilder();
        commandBuilder.add(restriction);

        return restrictionService.isPermitted(commandBuilder).get(0);
    }

    private List<Long> translateIds(String restrictionName, List<Long> entityIds) {
        if (restrictionName.equals("flight.edit") ||
                restrictionName.equals("flight.changeStatus") ||
                restrictionName.equals("lineItem.create")) {
            return flightServiceInternal.fetchCampaignIds(entityIds);
        } else if (restrictionName.equals("lineItem.edit") ||
                restrictionName.equals("lineItem.status")) {
            return lineItemServiceInternal.fetchCcgIds(entityIds);

        }
        return entityIds;
    }
}

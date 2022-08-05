package app.programmatic.ui.creativelink;

import static app.programmatic.ui.common.config.ApplicationConstants.MAX_RESULTS_SIZE;

import app.programmatic.ui.common.validation.exception.EntityNotFoundException;
import app.programmatic.ui.creative.dao.model.CreativeStat;
import app.programmatic.ui.creative.service.CreativeService;
import app.programmatic.ui.creativelink.dao.model.CreativeLinkOperation;
import app.programmatic.ui.creativelink.dao.model.CreativeLinkStat;
import app.programmatic.ui.creativelink.service.CreativeLinkService;
import app.programmatic.ui.creativelink.view.DisplayStatusesView;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.service.FlightService;
import app.programmatic.ui.flight.service.LineItemService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.MissingServletRequestParameterException;
import app.programmatic.ui.flight.tool.FlightLineItemsInfo;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class CreativeLinkController {

    @Autowired
    private CreativeLinkService creativeLinkService;

    @Autowired
    private CreativeService creativeService;

    @Autowired
    private LineItemService lineItemService;

    @Autowired
    private FlightService flightService;


    @RequestMapping(method = RequestMethod.GET, path = "/rest/creativeLink/stat", produces = "application/json")
    public List<? extends CreativeLinkStat> getCreativeLinkStat(@RequestParam(value = "lineItemId", required = false) Long lineItemId,
                                                                @RequestParam(value = "flightId", required = false) Long flightId)
                                                            throws MissingServletRequestParameterException {
        if (lineItemId == null && flightId == null) {
            throw new MissingServletRequestParameterException("flightId or lineItemId", "long");
        }

        LineItem lineItem;
        if (lineItemId == null) {
            FlightLineItemsInfo liInfo = new FlightLineItemsInfo(flightId, lineItemService);
            if (!liInfo.isDefaultLineItemExist()) {
                return getCreativeLinkStatByFlightId(flightId);
            }
            lineItem = liInfo.getDefaultLineItem();
        } else {
            lineItem = lineItemService.find(lineItemId);
        }

        return creativeLinkService.getStatsByCcgId(lineItem.getCcgId());
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/creativeLink/operation", produces = "application/json")
    public DisplayStatusesView creativeLinkOperation(@RequestParam(value = "name") CreativeLinkOperation operation,
                                                     @RequestParam(value = "creativeId") Long creativeId,
                                                     @RequestParam(value = "lineItemId", required = false) Long lineItemId,
                                                     @RequestParam(value = "flightId", required = false) Long flightId)
                                                throws MissingServletRequestParameterException {
        if (lineItemId == null && flightId == null) {
            throw new MissingServletRequestParameterException("flightId or lineItemId", "long");
        }

        LineItem lineItem;
        if (lineItemId != null) {
            lineItem = lineItemService.find(lineItemId);
        } else {
            FlightLineItemsInfo liInfo = new FlightLineItemsInfo(flightId, lineItemService);
            if (!liInfo.isDefaultLineItemExist()) {
                throw new EntityNotFoundException(flightId);
            }
            lineItem = liInfo.getDefaultLineItem();
        }

        DisplayStatusesView result = new DisplayStatusesView();
        result.setCreativeLinkDispayStatus(
                creativeLinkService.changeStatusByCreativeId(lineItem.getCcgId(), creativeId, operation));
        result.setLineItemDisplayStatus(lineItemService.find(lineItem.getId()).getMajorStatus());
        result.setFlightDisplayStatus(flightService.find(lineItem.getFlightId()).getMajorStatus());

        return result;
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/flight/linkCreatives", produces = "application/json")
    public void linkCreativesToFlight(@RequestParam(value = "flightId") Long flightId,
                                                     @RequestBody List<Long> creativeIds) {
        flightService.linkCreatives(flightId, creativeIds);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/lineItem/linkCreatives", produces = "application/json")
    public void linkCreativesToLineItem(@RequestParam(value = "lineItemId") Long lineItemId,
                                        @RequestBody List<Long> creativeIds) {
        lineItemService.linkCreatives(lineItemId, creativeIds != null ? creativeIds : Collections.emptyList());
    }

    private List<? extends CreativeLinkStat> getCreativeLinkStatByFlightId(Long flightId) {
        List<CreativeStat> creativeStats = creativeService.getDisplayCreativesByIds(
                flightService.findCreativeIds(flightId).getCreativeIds(), MAX_RESULTS_SIZE);

        return creativeStats.stream()
                .map( creative -> {
                    CreativeLinkStat result = new CreativeLinkStat();

                    result.setCreativeId(creative.getId());
                    result.setCreativeName(creative.getName());
                    result.setCreativeDisplayStatus(creative.getDisplayStatus());
                    result.setDisplayStatus(creative.getDisplayStatus());
                    result.setSizeId(creative.getSizeId());
                    result.setSizeName(creative.getSizeName());
                    result.setTemplateId(creative.getTemplateId());
                    result.setTemplateName(creative.getTemplateName());

                    return result;})
                .collect(Collectors.toList());
    }
}

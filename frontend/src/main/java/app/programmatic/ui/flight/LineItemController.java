package app.programmatic.ui.flight;

import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.OperationForm;
import app.programmatic.ui.common.model.StatusOperation;
import app.programmatic.ui.common.model.StatusOperationEditMode;
import app.programmatic.ui.common.tool.formatting.DateTimeFormatterWrapper;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.FlightLineItems;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.dao.model.stat.FlightBaseStat;
import app.programmatic.ui.flight.service.FlightChannelService;
import app.programmatic.ui.flight.service.FlightLineItemCouplingService;
import app.programmatic.ui.flight.service.LineItemService;
import app.programmatic.ui.flight.tool.FlightPrePersistHelper;
import app.programmatic.ui.flight.view.FlightBaseView;
import app.programmatic.ui.flight.view.FlightLineItemsView;
import app.programmatic.ui.flight.view.FlightView;
import app.programmatic.ui.flight.view.LineItemView;
import app.programmatic.ui.geo.service.GeoService;
import app.programmatic.ui.site.dao.model.Site;
import app.programmatic.ui.site.dao.model.SiteStat;
import app.programmatic.ui.site.service.SiteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class LineItemController extends FlightBaseController {

    @Autowired
    private LineItemService lineItemService;

    @Autowired
    private FlightLineItemCouplingService fliService;

    @Autowired
    private GeoService geoService;

    @Autowired
    private SiteService siteService;


    @Override
    public FlightChannelService getFlightChannelService() {
        return lineItemService;
    }

    @Override
    protected Long fetchAccountId(FlightBaseView lineItemView) {
        return lineItemService.findAccountIdByFlightId(((LineItemView) lineItemView).getFlightId());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/lineItem", produces = "application/json")
    public FlightLineItemsView getLineItems(@RequestParam(value = "lineItemId", required = false) Long lineItemId,
                                            @RequestParam(value = "flightId", required = false) Long flightId)
            throws MissingServletRequestParameterException {
        if (lineItemId != null && flightId != null ||
                lineItemId == null && flightId == null) {
            throw new MissingServletRequestParameterException("lineItemId|flightId", "Long");
        }

        FlightLineItems flightLineItems = lineItemId != null ? lineItemService.findEffectiveEager(lineItemId) :
                lineItemService.findEffectiveEagerByFlightId(flightId);
        Flight flight = flightLineItems.getFlight();
        FlightView flightView = new FlightView(flight, getWhiteList(flight), getBlackList(flight));
        return new FlightLineItemsView(
                flightView,
                flightLineItems.getLineItems().stream()
                        .map(lineItem -> {
                            List<String> whiteList = getWhiteList(lineItem);
                            List<String> blackList = getBlackList(lineItem);
                            return new LineItemView(flight, lineItem, whiteList, blackList,
                                    getViewResetAwareProps(flightView.getWhiteListAsList(), flightView.getBlackListAsList(),
                                            whiteList, blackList));
                        })
                        .collect(Collectors.toList()));
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/lineItem", produces = "application/json")
    public Long createLineItem(@RequestBody LineItemView lineItemView) {
        LineItem lineItem = toLineItem(lineItemView);
        FlightPrePersistHelper.setLineItemDefaults(lineItem);
        FlightPrePersistHelper.prePersistLineItem(lineItem, channelService, siteService);
        FlightPrePersistHelper.prePersistAddresses(lineItem, lineItemView, geoService);

        return withValidationAliases(
                () -> fliService.createLineItemAndUnSyncDefault(lineItem).getId());
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/lineItem", produces = "application/json")
    public void updateLineItem(@RequestBody LineItemView lineItemView) {
        LineItem lineItem = toLineItem(lineItemView);
        FlightPrePersistHelper.prePersistLineItem(lineItem, channelService, siteService);
        FlightPrePersistHelper.prePersistAddresses(lineItem, lineItemView, geoService);

        withValidationAliases(() -> lineItemService.update(lineItem));
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/lineItem/stat", produces = "application/json")
    public FlightBaseStat getLineItemStats(@RequestParam(value = "lineItemId") Long lineItemId) {
        return lineItemService.getStat(lineItemId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/lineItem/stat/list", produces = "application/json")
    public List<FlightBaseStat> getFlightLineItemsStats(
            @RequestParam(value = "flightId") Long flightId,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr) {
        if (startDateStr == null || endDateStr == null) {
            return lineItemService.getFlightStat(flightId, null, null);
        }

        LocalDateTime startDate;
        LocalDateTime endDate;
        try {
            startDate = DateTimeFormatterWrapper.parseDateTime(startDateStr);
            endDate = DateTimeFormatterWrapper.parseDateTime(endDateStr);
        } catch (DateTimeParseException e) {
            throw new MethodArgumentTypeMismatchException(startDateStr + "-" + endDateStr, LocalDateTime.class, "dateStart - dateEnd", null, e);
        }

        return lineItemService.getFlightStat(flightId, startDate, endDate);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/rest/lineItem", produces = "application/json")
    public MajorDisplayStatus deleteLineItem(@RequestParam(value = "lineItemId") Long lineItemId) {
        return fliService.deleteLineItemsAndSyncDefault(Collections.singletonList(lineItemId)).get(0);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/lineItem/linkSites", produces = "application/json")
    public void linkSites(@RequestParam(value = "lineItemId") Long lineItemId,
                          @RequestBody List<Long> siteIds) {
        lineItemService.linkSites(lineItemId, siteIds != null ? siteIds : Collections.emptyList());
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/lineItem/linkConversions", produces = "application/json")
    public void linkConversions(@RequestParam(value = "lineItemId") Long lineItemId,
                                @RequestBody List<Long> conversionIds) {
        lineItemService.linkConversions(lineItemId, conversionIds != null ? conversionIds : Collections.emptyList());
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/lineItem/operation", produces = "application/json")
    public Object doFlightOperation(@RequestParam(value = "name") StatusOperation operation,
                                    @RequestParam(value = "lineItemIds") List<Long> lineItemIds,
                                    @RequestParam(value = "editMode", required = false) StatusOperationEditMode editMode,
                                    @RequestBody(required = false) OperationForm operationForm) {
        switch (operation) {
            case ACTIVATE:
                return lineItemService.activate(lineItemIds);
            case INACTIVATE:
                return lineItemService.inactivate(lineItemIds);
            case DELETE:
                return fliService.deleteLineItemsAndSyncDefault(lineItemIds);
            case SITE:
                switch (editMode) {
                    case SET:
                        lineItemService.setSites(lineItemIds, operationForm.getSiteIds());
                        return true;
                    case ADD:
                        lineItemService.addSites(lineItemIds, operationForm.getSiteIds());
                        return true;
                    case DELETE:
                        lineItemService.deleteSites(lineItemIds, operationForm.getSiteIds());
                        return true;
                }
            case GEO:
                switch (editMode) {
                    case SET:
                        lineItemService.setGeo(lineItemIds, operationForm.getExcludedGeoChannelIds(), operationForm.getGeoChannelIds());
                        return true;
                    case ADD:
                        lineItemService.addGeo(lineItemIds, operationForm.getExcludedGeoChannelIds(), operationForm.getGeoChannelIds());
                        return true;
                    case DELETE:
                        lineItemService.deleteGeo(lineItemIds, operationForm.getExcludedGeoChannelIds(), operationForm.getGeoChannelIds());
                        return true;
                }
        }

        throw new RuntimeException("Unexpected operation: " + operation);
    }


    @RequestMapping(method = RequestMethod.POST, path = "/rest/lineItem/makeCopy", produces = "application/json")
    public FlightLineItemsView makeLineItemCopy(@RequestParam(value = "lineItemId") Long lineItemId) throws MissingServletRequestParameterException {

        if (lineItemId == null) {
            throw new MissingServletRequestParameterException("lineItemId", "Long");
        }
        return makeCopyLineItems(lineItemId, null, null, null);
    }

    private LineItem toLineItem(LineItemView lineItemView) {
        LineItem lineItem = lineItemView.buildLineItem();
        setUrlsChannels(lineItem, lineItemView);
        return lineItem;
    }

}

package app.programmatic.ui.flight;

import app.programmatic.ui.common.model.StatusOperation;
import app.programmatic.ui.common.tool.formatting.DateTimeFormatterWrapper;
import app.programmatic.ui.common.validation.exception.EntityNotFoundException;
import app.programmatic.ui.common.view.IdName;
import app.programmatic.ui.flight.dao.dto.UpdateLineItemsPartDto;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.FrequencyCap;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.dao.model.Opportunity;
import app.programmatic.ui.flight.dao.model.chart.ChartData;
import app.programmatic.ui.flight.dao.model.chart.ChartMetric;
import app.programmatic.ui.flight.dao.model.chart.ChartObject;
import app.programmatic.ui.flight.dao.model.chart.ChartType;
import app.programmatic.ui.flight.dao.model.stat.FlightBaseStat;
import app.programmatic.ui.flight.dao.model.stat.FlightDashboardStat;
import app.programmatic.ui.flight.service.*;
import app.programmatic.ui.flight.tool.FlightPrePersistHelper;
import app.programmatic.ui.flight.view.FlightBaseView;
import app.programmatic.ui.flight.view.FlightView;
import app.programmatic.ui.geo.service.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLConnection;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class FlightController extends FlightBaseController {
    @Value("${flight.start-days-ago-limit}")
    private int flightStartDaysAgoLimit;

    @Autowired
    private FlightService flightService;

    @Autowired
    private LineItemService lineItemService;

    @Autowired
    private FlightChartService chartService;

    @Autowired
    private GeoService geoService;

    @Autowired
    private FlightTransferPartService transferPartService;

    @Override
    protected Long fetchAccountId(FlightBaseView flightView) {
        Long flightId = ((FlightView) flightView).getId();
        if (flightId == null) {
            return ((FlightView) flightView).getAccountId();
        }
        return flightService.findAccountIdByFlightId(flightId);
    }


    public FlightChannelService getFlightChannelService() {
        return flightService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/flight/stat", produces = "application/json")
    public FlightBaseStat getFlightStats(@RequestParam(value = "flightId") Long flightId) {
        return flightService.getStat(flightId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/flight/stat/list", produces = "application/json")
    public List<FlightBaseStat> getAccountFlightsStats(
            @RequestParam(value = "accountId") Long accountId,
            @RequestParam(value = "startDate", required = false) String startDateStr,
            @RequestParam(value = "endDate", required = false) String endDateStr) {

        if (startDateStr == null || endDateStr == null) {
            return flightService.getAccountStat(accountId, null, null);
        }

        LocalDateTime startDate;
        LocalDateTime endDate;
        try {
            startDate = DateTimeFormatterWrapper.parseDateTime(startDateStr);
            endDate = DateTimeFormatterWrapper.parseDateTime(endDateStr);
        } catch (DateTimeParseException e) {
            throw new MethodArgumentTypeMismatchException(startDateStr + "-" + endDateStr, LocalDateTime.class, "dateStart - dateEnd", null, e);
        }

        return flightService.getAccountStat(accountId, startDate, endDate);
    }

    @Deprecated // ToDo: not needed, must be removed
    @RequestMapping(method = RequestMethod.GET, path = "/rest/lineItem/list", produces = "application/json")
    public List<IdName> getAccountLineItemList(
            @RequestParam(value = "accountId") Long accountId) {

        List<IdName> flights = flightService.getAccountFlightList(accountId);
        List<IdName> lis = flights.stream()
                .flatMap(idName -> lineItemService.findEagerByFlightId(idName.getId()).stream())
                .map(li -> new IdName(li.getId(), li.getName()))
                .collect(Collectors.toList());
        return lis;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/flight/list", produces = "application/json")
    public List<IdName> getAccountFlightList(
            @RequestParam(value = "accountId") Long accountId) {

        return flightService.getAccountFlightList(accountId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/flight", produces = "application/json")
    public Long createFlight(@RequestBody FlightView flightView) {
        Flight flight = toFlight(flightView);
        FlightPrePersistHelper.setFlightDefaults(flight);
        FlightPrePersistHelper.prePersistFlight(flight);
        FlightPrePersistHelper.prePersistAddresses(flight, flightView, geoService);

        Flight updated = withValidationAliases(() -> flightService.create(flight));
        flightService.updateAllocationAndCampaignRelation(updated);

        return updated.getId();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/flight", produces = "application/json")
    public FlightView getFlight(@RequestParam(value = "flightId") Long flightId) {
        Flight flight = flightService.findEager(flightId);
        if (flight == null) {
            throw new EntityNotFoundException(flightId);
        }

        return new FlightView(flight, getWhiteList(flight), getBlackList(flight));
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/flight", produces = "application/json")
    public void updateFlight(@RequestBody FlightView flightView) throws MissingServletRequestParameterException {
        Flight flight = toFlight(flightView);
        FlightPrePersistHelper.prePersistFlight(flight);
        FlightPrePersistHelper.prePersistAddresses(flight, flightView, geoService);

        Flight updated = withValidationAliases(() -> flightService.update(flight));
        flightService.updateAllocationAndCampaignRelation(updated);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/flight/setPartAll", produces = "application/json")
    @Transactional
    public void updateLineItemsByPartFromFlight(@RequestBody UpdateLineItemsPartDto itemsPartDto) {
        if (itemsPartDto.getFlightPart() != null && itemsPartDto.getFlightPart() != null && itemsPartDto.getAccountId() != null) {
            Flight flight = flightService.find(itemsPartDto.getFlightId());
            if (flight.getId() != null) {
                List<LineItem> lineItems = lineItemService.findEagerByFlightId(flight.getId());
                if (lineItems != null) {
                    for (LineItem lineItem : lineItems) {
                        flightService.updateLineItemsByPartWithoutSave(flight, lineItem, itemsPartDto.getFlightPart(), itemsPartDto.getAccountId());
                        withValidationAliases(() -> lineItemService.update(lineItem));
                    }
                }
            }
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/rest/flight", produces = "application/json")
    public void deleteFlight(@RequestParam(value = "flightId") Long flightId) {
        flightService.delete(flightId);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/flight/linkSites", produces = "application/json")
    public void linkSites(@RequestParam(value = "flightId") Long flightId,
                          @RequestBody List<Long> siteIds) {
        flightService.linkSites(flightId, siteIds != null ? siteIds : Collections.emptyList());
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/flight/linkConversions", produces = "application/json")
    public void linkConversions(@RequestParam(value = "flightId") Long flightId,
                                @RequestBody List<Long> conversionIds) {
        flightService.linkConversions(flightId, conversionIds != null ? conversionIds : Collections.emptyList());
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/flight/operation", produces = "application/json")
    public Object doFlightOperation(@RequestParam(value = "name") StatusOperation operation,
                                    @RequestParam(value = "flightId") Long flightId) {
        switch (operation) {
            case ACTIVATE:
                flightService.activate(flightId);
                break;
            case INACTIVATE:
                flightService.inactivate(flightId);
                break;
            case DELETE:
                flightService.delete(flightId);
                break;
            default:
                throw new RuntimeException("Unexpected operation: " + operation);
        }

        return flightService.find(flightId).getMajorStatus();
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/flight/chart", produces = "application/json")
    public ChartData buildChart(@RequestParam(value = "entityId") Long flightBaseId,
                                @RequestParam(value = "chartObject") ChartObject object,
                                @RequestParam(value = "chartMetric") ChartMetric metric,
                                @RequestParam(value = "chartType") ChartType type,
                                @RequestParam(value = "dateStart", required = false) String dateStartStr,
                                @RequestParam(value = "dateEnd", required = false) String dateEndStr) {
        LocalDateTime dateStart;
        LocalDateTime dateEnd;
        try {
            dateStart = DateTimeFormatterWrapper.parseDateTime(dateStartStr);
            dateEnd = DateTimeFormatterWrapper.parseDateTime(dateEndStr);
        } catch (DateTimeParseException e) {
            throw new MethodArgumentTypeMismatchException(dateStartStr + "-" + dateEndStr, LocalDateTime.class, "dateStart - dateEnd", null, e);
        }
        dateEnd = dateEnd != null ? dateEnd : LocalDateTime.now();

        return chartService.buildChart(flightBaseId, object, metric, type, dateStart, dateEnd);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/flight/listAttachments", produces = "application/json")
    public List<String> listAttachments(@RequestParam(value = "flightId") Long flightId) {
        return flightService.listAttachments(flightId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/flight/uploadAttachment")
    public void uploadAttachment(@RequestParam(value = "flightId") Long flightId,
                                 @RequestParam("file") MultipartFile file) {
        flightService.uploadIoAttachment(file, flightId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/flight/downloadAttachment")
    public ResponseEntity downloadAttachment(@RequestParam(value = "flightId") Long flightId,
                                             @RequestParam(value = "name") String name,
                                             @RequestParam(value = "base64", required = false) Boolean isBase64Required) {
        byte[] contents = flightService.downloadAttachment(name, flightId);

        HttpHeaders headers = new HttpHeaders();
        if (isBase64Required != null && isBase64Required) {
            contents = Base64.getEncoder().encode(contents);
            headers.set("Content-Transfer-Encoding", "BASE64");
        } else {
            headers.setContentDispositionFormData("attachment", name);
        }

        String mimeType = URLConnection.guessContentTypeFromName(name);
        if (mimeType != null) {
            headers.setContentType(MediaType.valueOf(mimeType));
        }
        headers.setContentLength(contents.length);
        return new ResponseEntity<>(
                contents,
                headers,
                HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/rest/flight/attachment", produces = "application/json")
    public void deleteAttachment(@RequestParam(value = "flightId") Long flightId,
                                 @RequestParam(value = "name") String name) {
        flightService.deleteAttachment(name, flightId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/dashboard/stat", produces = "application/json")
    public List<FlightDashboardStat> getFlightDashboardStats() {
        return flightService.getDashboardStats(flightStartDaysAgoLimit);
    }


    @RequestMapping(method = RequestMethod.POST, path = "/rest/flight/makeCopy", produces = "application/json")
    public FlightView makeFlightCopy(@RequestParam(value = "flightId") Long flightId) throws MissingServletRequestParameterException {

        FlightView sourceFlightView = getFlight(flightId);

        sourceFlightView.setName(getNewFlightOrLineItemName(sourceFlightView.getName()));
        sourceFlightView.setId(null);
        sourceFlightView.setIoId(null);

        Flight flight = toFlight(sourceFlightView);
        Opportunity opportunity = flight.getOpportunity();
        if (opportunity != null) {
            opportunity.setId(null);
            flight.setOpportunity(opportunity);
        }
        FrequencyCap frequencyCap = flight.getFrequencyCap();
        if (frequencyCap != null) {
            frequencyCap.setId(null);
            flight.setFrequencyCap(frequencyCap);
        }

        FlightPrePersistHelper.setFlightDefaults(flight);
        FlightPrePersistHelper.prePersistFlight(flight);
        FlightPrePersistHelper.prePersistAddresses(flight, sourceFlightView, geoService);

        Flight updatedFlight = withValidationAliases(() -> flightService.create(flight, false));
        flightService.updateAllocationAndCampaignRelation(updatedFlight);
        FlightView updatedFlightView = new FlightView(updatedFlight, getWhiteList(updatedFlight), getBlackList(updatedFlight));

        makeCopyLineItems(null, flightId, updatedFlight, updatedFlightView);

        return updatedFlightView;
    }

    private Flight toFlight(FlightView flightView) {
        Flight flight = flightView.buildFlight();
        setUrlsChannels(flight, flightView);

        return flight;
    }
}

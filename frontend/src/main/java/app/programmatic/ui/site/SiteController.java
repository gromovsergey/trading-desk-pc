package app.programmatic.ui.site;

import static app.programmatic.ui.common.config.ApplicationConstants.MAX_RESULTS_SIZE;

import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.common.view.IdName;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.SiteIdsProjection;
import app.programmatic.ui.flight.service.FlightService;
import app.programmatic.ui.flight.service.LineItemService;
import app.programmatic.ui.flight.tool.FlightLineItemsInfo;
import app.programmatic.ui.site.dao.model.Site;
import app.programmatic.ui.site.service.SiteService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


@RestController
public class SiteController {

    @Autowired
    private SiteService siteService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private FlightService flightService;

    @Autowired
    private LineItemService lineItemService;


    @RequestMapping(method = RequestMethod.GET, path = "/rest/site", produces = "application/json")
    public List<? extends Site> getAdvertiserCreatives(@RequestParam(value = "accountId") Long accountId) {
        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
        return siteService.findSitesByCountry(account.getCountryCode(), MAX_RESULTS_SIZE);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/site/stat", produces = "application/json")
    public Collection<? extends Site> getSiteStat(@RequestParam(value = "lineItemId", required = false) Long lineItemId,
                                                  @RequestParam(value = "flightId", required = false) Long flightId)
            throws MissingServletRequestParameterException {
        if (lineItemId == null && flightId == null) {
            throw new MissingServletRequestParameterException("flightId or lineItemId", "long");
        }

        if (lineItemId == null) {
            FlightLineItemsInfo liInfo = new FlightLineItemsInfo(flightId, lineItemService);
            if (!liInfo.isDefaultLineItemExist()) {
                SiteIdsProjection siteIdsProjection = flightService.findSiteIds(flightId);
                if (siteIdsProjection.getSiteIds().isEmpty()) {
                    return Collections.emptyList();
                }

                Flight flight = flightService.find(flightId);
                AdvertisingAccount account = accountService.findAdvertisingUnchecked(flight.getOpportunity().getAccountId());
                return siteService.findSitesByCountryAndIds(account.getCountryCode(), siteIdsProjection.getSiteIds(), MAX_RESULTS_SIZE);
            }
            lineItemId = liInfo.getDefaultLineItemId();
        }

        return siteService.getStatsByLineItemId(lineItemId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/site/list", produces = "application/json")
    public List<IdName> getSitesByAccountId(@RequestParam(value = "accountId") Long accountId) {
        return siteService.findSitesByAccountId(accountId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/tag/list", produces = "application/json")
    public List<IdName> findTagsBySiteId(@RequestParam(value = "siteId") Long siteId) {
        return siteService.findTagsBySiteId(siteId);
    }
}

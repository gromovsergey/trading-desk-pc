package app.programmatic.ui.flight.service;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static app.programmatic.ui.common.config.TestEnvironment.FLIGHT_WITH_SPENT_BUDGET_ID;
import static app.programmatic.ui.common.config.TestEnvironment.WORKING_COUNTRY;
import static app.programmatic.ui.common.config.TestEnvironment.WORKING_LANG;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.DATE_END;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.DEVICE_CHANNEL_IDS;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.FREQUENCY_CAP;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.GEO_CHANNEL_IDS;
import static app.programmatic.ui.flight.service.testhelper.FlightServiceIntegrationTestHelper.*;
import static app.programmatic.ui.flight.service.testhelper.FlightServiceIntegrationTestHelper.DEFAULT_FLIGHT_NAME;
import static app.programmatic.ui.flight.service.testhelper.FlightServiceIntegrationTestHelper.createFlight;

import com.foros.rs.client.model.advertising.campaign.Campaign;
import com.foros.rs.client.model.advertising.campaign.CampaignCreativeGroup;
import com.foros.rs.client.model.advertising.campaign.CreativeLink;
import com.foros.rs.client.model.advertising.campaign.FrequencyCap;
import com.foros.rs.client.model.entity.Status;
import app.programmatic.ui.account.service.SearchAccountService;
import app.programmatic.ui.campaign.service.CampaignService;
import app.programmatic.ui.ccg.service.CcgService;
import app.programmatic.ui.channel.dao.model.Channel;
import app.programmatic.ui.channel.service.ChannelService;
import app.programmatic.ui.common.aspect.prePersistProcessor.PrePersistProcessorContext;
import app.programmatic.ui.common.config.TestConfig;
import app.programmatic.ui.common.i18n.MessageInterpolator;
import app.programmatic.ui.common.foros.service.TestCurUserTokenKeyService;
import app.programmatic.ui.common.tool.javabean.emptyValues.ConfigurableEmptyValuesStrategy;
import app.programmatic.ui.common.testtools.TestEnvironment;
import app.programmatic.ui.common.testtools.TestEnvironmentVariables;
import app.programmatic.ui.creative.dao.model.CreativeStat;
import app.programmatic.ui.creative.service.CreativeService;
import app.programmatic.ui.creativelink.service.CreativeLinkService;
import app.programmatic.ui.device.dao.model.DeviceNode;
import app.programmatic.ui.device.service.DeviceService;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.dao.model.stat.FlightBaseStat;
import app.programmatic.ui.flight.view.FlightView;
import app.programmatic.ui.geo.dao.model.Location;
import app.programmatic.ui.geo.service.GeoService;
import app.programmatic.ui.site.dao.model.Site;
import app.programmatic.ui.site.service.SiteService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = NONE, classes = {TestConfig.class})
public class FlightServiceIntegrationTest extends Assert {
    @Autowired
    private TestCurUserTokenKeyService curUserTokenKeyService;

    @Autowired
    private SearchAccountService searchAccountService;

    @Autowired
    private ChannelService channelService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private CreativeService creativeService;

    @Autowired
    private CreativeLinkService creativeLinkService;

    @Autowired
    private GeoService geoService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private CcgService ccgService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private LineItemServiceInternal lineItemService;

    @Autowired
    private FlightServiceInternal flightServiceInternal;

    @Autowired
    private PrePersistProcessorContext entityProcessorContext;


    private TestEnvironmentVariables vars;


    @Before
    public void initialize() {
        vars = TestEnvironment.initialize(curUserTokenKeyService, searchAccountService);
    }

    @Test
    public void testCreateMinimal() {
        createMinimalFlightAndCheck("Minimal ");
    }

    @Test
    public void testCreateUpdate() {
        Flight flight = createFlight(vars.getAccountId(), vars.getTimestamp());
        flight = flightServiceInternal.create(flight);
        assertNotNull(flight);
        assertNotNull(flight.getId());

        Flight toUpdate = flightWithAllUpdatedFields(flight);
        Flight updated = flightServiceInternal.update(toUpdate);
        assertNotNull(updated);
        assertEquals(flight.getId(), updated.getId());
    }

    @Test
    public void testLinkAdvChannels() {
        Flight flight = createMinimalFlightAndCheck("Adv Link ");

        List<Channel> channels = channelService.findByAccountId(vars.getAgencyId());
        assertFalse(channels.isEmpty());
        Set<Long> channelIds = channels.stream()
                .map( c -> c.getId() )
                .limit(10)
                .collect(Collectors.toSet());

        flightServiceInternal.linkAdvertisingChannels(flight.getId(), new ArrayList<>(channelIds), false);
        LineItem lineItem = fetchDefLIAndCheck(flight);
        CampaignCreativeGroup ccg = ccgService.find(lineItem.getCcgId());
        assertNotNull(ccg);
        assertNotNull("Target channel must exist, because adv channels selected", ccg.getChannel());

        flightServiceInternal.linkAdvertisingChannels(flight.getId(), Collections.emptyList(), false);
        lineItem = fetchDefLIAndCheck(flight);
        ccg = ccgService.find(lineItem.getCcgId());
        assertNotNull(ccg);
        assertNull("Target channel must not exist, because there are no adv channels selected", ccg.getChannel());
    }

    @Test
    public void testLinkSites() {
        Flight flight = createMinimalFlightAndCheck("Sites Link ");

        List<Site> sites = siteService.findSitesByCountry(WORKING_COUNTRY, 3);
        assertFalse(sites.isEmpty());
        Set<Long> sitesIds = sites.stream().map( s -> s.getSiteId() ).collect(Collectors.toSet());

        flightServiceInternal.linkSites(flight.getId(), new ArrayList<>(sitesIds));
        LineItem lineItem = fetchDefLIAndCheck(flight);
        CampaignCreativeGroup ccg = ccgService.find(lineItem.getCcgId());
        assertNotNull(ccg);
        assertEquals("Selected sites must be linked", sitesIds.size(), ccg.getSites().size());

        flightServiceInternal.linkSites(flight.getId(), Collections.emptyList());
        lineItem = fetchDefLIAndCheck(flight);
        ccg = ccgService.find(lineItem.getCcgId());
        assertNotNull(ccg);
        assertTrue("Sites must not be linked", ccg.getSites().isEmpty());
    }

    @Test
    public void testLinkCreatives() {
        Flight flight = createMinimalFlightAndCheck("Creatives Link ");

        List<CreativeStat> creatives = creativeService.getDisplayCreatives(vars.getAccountId(), 3);
        assertFalse(creatives.isEmpty());
        Set<Long> creativeIds = creatives.stream().map( c -> c.getId() ).collect(Collectors.toSet());

        flightServiceInternal.linkCreatives(flight.getId(), new ArrayList<>(creativeIds));
        LineItem lineItem = fetchDefLIAndCheck(flight);
        List<CreativeLink> creativeLinks = creativeLinkService.findByCcgId(lineItem.getCcgId());
        assertEquals("Selected creatives must be linked", creativeIds.size(), creativeLinks.size());

        flightServiceInternal.linkCreatives(flight.getId(), Collections.emptyList());
        lineItem = fetchDefLIAndCheck(flight);
        creativeLinks = creativeLinkService.findByCcgId(lineItem.getCcgId());
        assertTrue("Creatives must not be linked", creativeLinks.stream().allMatch( cl -> cl.getStatus() == Status.DELETED));
    }

    @Test
    public void testReSetEmptyValues() {
        Collection<Location> locations = geoService.searchLocations("london", WORKING_COUNTRY, WORKING_LANG);
        assertFalse("Test requires at least 1 geo location", locations.isEmpty());
        List<Long> locationIds = locations.stream().map( l -> l.getId() ).sorted().collect(Collectors.toList());

        Collection<DeviceNode> deviceNodes = deviceService.getAvailableDevicesByAccountId(vars.getAgencyId());
        assertTrue("Test requires at least 2 root nodes to have ability to deselect", deviceNodes.size() > 1);
        List<Long> deviceIds = Collections.singletonList(deviceNodes.iterator().next().getId());

        Flight flight = createMinimalFlight(vars.getAccountId(), DEFAULT_FLIGHT_NAME + "Re-set empty", vars.getTimestamp());
        flight.setDateEnd(LocalDate.now().plusDays(30));
        flight.setFrequencyCap(createFrequencyCap());
        flight.setGeoChannelIds(locationIds);
        flight.setDeviceChannelIds(deviceIds);

        Flight persistedFlight = flightServiceInternal.create(flight);
        assertNotNull(persistedFlight);
        assertNotNull(persistedFlight.getId());

        Campaign campaign = campaignService.find(flightServiceInternal.fetchCampaignId(persistedFlight));
        assertNotNull(campaign);
        assertNotNull(campaign.getDateEnd());
        assertTrue(checkFrequencyCapNotNull(campaign.getFrequencyCap()));

        LineItem persistedLi = lineItemService.findByFlightId(persistedFlight.getId()).get(0);
        CampaignCreativeGroup ccg = ccgService.find(persistedLi.getCcgId());
        assertEquals(locationIds, ccg.getGeoChannels().stream().map(g -> g.getId()).sorted().collect(Collectors.toList()));
        assertEquals(deviceIds, ccg.getDeviceChannels().stream().map(g -> g.getId()).sorted().collect(Collectors.toList()));

        persistedFlight.setBudget(BigDecimal.TEN.add(persistedFlight.getBudget()));
        persistedFlight.setGeoChannelIds(getRidOfPersistentList(persistedFlight.getGeoChannelIds()));
        persistedFlight.setDeviceChannelIds(getRidOfPersistentList(persistedFlight.getDeviceChannelIds()));
        entityProcessorContext.setEmptyValuesStrategy(new ConfigurableEmptyValuesStrategy(
                Arrays.asList(DATE_END, FREQUENCY_CAP, GEO_CHANNEL_IDS, DEVICE_CHANNEL_IDS).stream()
                                                .map( p -> p.getName() )
                                                .collect(Collectors.toSet())));
        flightServiceInternal.update(persistedFlight);

        campaign = campaignService.find(flightServiceInternal.fetchCampaignId(persistedFlight));
        assertNotNull(campaign);
        assertNull(campaign.getDateEnd());
        assertTrue(checkFrequencyCapNull(campaign.getFrequencyCap()));

        ccg = ccgService.find(persistedLi.getCcgId());
        assertTrue("Geo channels were unlinked, so empty list expected", ccg.getGeoChannels().isEmpty());
        assertTrue("Device channels were unlinked, so empty list expected", ccg.getDeviceChannels().isEmpty());
        assertTrue("Budget should be updated", persistedFlight.getOpportunity().getAmount().compareTo(ccg.getBudget()) == 0);
    }

    @Test
    public void testUpdateBudgetWhenSomeSpent() {
        Flight flight = flightServiceInternal.findEager(FLIGHT_WITH_SPENT_BUDGET_ID);
        FlightBaseStat flightStat = flightServiceInternal.getStat(FLIGHT_WITH_SPENT_BUDGET_ID);

        assertTrue("Spent budget must be positive", flightStat.getSpentBudget().compareTo(BigDecimal.TEN) > 0);
        assertTrue("Budget must be greater than Spent budget", flight.getBudget().compareTo(flightStat.getSpentBudget()) > 0);

        Flight toUpdate = flightWithAllUpdatedFields(flight);
        toUpdate.setBudget(flightStat.getSpentBudget().subtract(BigDecimal.ONE));

        try {
            flightServiceInternal.update(flight);
        } catch (ConstraintViolationException e) {
            assertEquals(e.getConstraintViolations().size(), 1);

            ConstraintViolation violation = e.getConstraintViolations().iterator().next();
            assertTrue("Violation of 'budget' field expected",
                    violation.getPropertyPath().toString().endsWith(".budget"));

            String violationMsg = MessageInterpolator.getDefaultMessageInterpolator().interpolate(
                    "flight.budget.error.lowerThanSpent", flightStat.getSpentBudget().toString()
            );
            assertTrue("Violation about 'spent amount' expected", violation.getMessage().equals(violationMsg));
        }
    }

    @Test
    public void testFlightLockReentrant() {
        Flight flight = createFlight(vars.getAccountId(), "Flight Reentrant Lock Test", vars.getTimestamp());
        Flight persisted = flightServiceInternal.create(flight);
        assertNotNull(flight);
        assertNotNull(flight.getId());

        flightServiceInternal.runWithFlightLock( flight.getId(), () -> {
            Flight toUpdate = flightWithAllUpdatedFields(persisted);
            Flight updated = flightServiceInternal.update(toUpdate);
            assertNotNull(updated);
            assertEquals(persisted.getId(), updated.getId());
        });
    }

    @Test
    public void testFindForView() {
        Flight flight = createFlight(vars.getAccountId(), "FlightServiceIntegrationTest.testFind", vars.getTimestamp());
        flight = flightServiceInternal.create(flight);
        assertNotNull(flight);
        assertNotNull(flight.getId());

        flight = flightServiceInternal.findEager(flight.getId());
        new FlightView(flight, Collections.emptyList(), Collections.emptyList());
    }

    private Flight createMinimalFlightAndCheck(String nameSuffix) {
        Flight flight = createMinimalFlight(vars.getAccountId(), DEFAULT_FLIGHT_NAME + nameSuffix, vars.getTimestamp());
        flight = flightServiceInternal.create(flight);
        assertNotNull(flight);
        assertNotNull(flight.getId());

        return flight;
    }

    private LineItem fetchDefLIAndCheck(Flight flight) {
        List<LineItem> lineItems = lineItemService.findByFlightId(flight.getId());
        assertEquals("The only default LI should be created", 1, lineItems.size());

        return lineItems.get(0);
    }

    private static boolean checkFrequencyCapNull(FrequencyCap fc) {
        return fc == null || (
                fc.getWindowCount() == null &&
                fc.getPeriod() == null &&
                fc.getLifeCount() == null &&
                fc.getWindowLength() == null
        );
    }

    private static boolean checkFrequencyCapNotNull(FrequencyCap fc) {
        return fc != null &&
                fc.getWindowCount() != null &&
                fc.getPeriod() != null &&
                fc.getLifeCount() != null &&
                fc.getWindowLength() != null;
    }

    private static <T> List<T> getRidOfPersistentList(List<T> persistentBag) {
        return persistentBag.stream().collect(Collectors.toList());
    }
}

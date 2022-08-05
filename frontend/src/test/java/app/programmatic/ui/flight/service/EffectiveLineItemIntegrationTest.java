package app.programmatic.ui.flight.service;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.NONE;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.BLACK_LIST_ID;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.BUDGET;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.CHANNEL_IDS;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.FREQUENCY_CAP;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.MIN_CTR_GOAL;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.RATE_TYPE;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.RATE_VALUE;
import static app.programmatic.ui.flight.service.testhelper.FlightServiceIntegrationTestHelper.createFlight;

import app.programmatic.ui.account.service.SearchAccountService;
import app.programmatic.ui.common.aspect.prePersistProcessor.PrePersistProcessorContext;
import app.programmatic.ui.common.config.TestConfig;
import app.programmatic.ui.common.model.EntityBase;
import app.programmatic.ui.common.model.VersionEntityBase;
import app.programmatic.ui.common.foros.service.TestCurUserTokenKeyService;
import app.programmatic.ui.common.tool.javabean.emptyValues.ConfigurableEmptyValuesStrategy;
import app.programmatic.ui.common.testtools.TestEnvironment;
import app.programmatic.ui.common.testtools.TestEnvironmentVariables;
import app.programmatic.ui.common.tool.javabean.JavaBeanAccessor;
import app.programmatic.ui.common.tool.javabean.JavaBeanUtils;
import app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.FlightBase;
import app.programmatic.ui.flight.dao.model.FlightLineItems;
import app.programmatic.ui.flight.dao.model.FlightSchedule;
import app.programmatic.ui.flight.dao.model.FrequencyCap;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.tool.EffectiveLineItemTool;
import app.programmatic.ui.flight.tool.LineItemBuilder;

import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = NONE, classes = {TestConfig.class})
public class EffectiveLineItemIntegrationTest extends Assert {
    @Autowired
    private TestCurUserTokenKeyService curUserTokenKeyService;

    @Autowired
    private SearchAccountService searchAccountService;

    @Autowired
    private FlightServiceInternal flightService;

    @Autowired
    private LineItemServiceInternal lineItemService;

    @Autowired
    private FlightLineItemCouplingService fliService;

    @Autowired
    private PrePersistProcessorContext entityProcessorContext;


    private TestEnvironmentVariables vars;


    @Before
    public void initialize() {
        vars = TestEnvironment.initialize(curUserTokenKeyService, searchAccountService);
    }

    @Test
    public void testCreateUpdate() {
        Flight flight = createFlight(vars.getAccountId(), vars.getTimestamp());
        flight = flightService.create(flight);

        checkCreateFlight(flight);

        Long defaultLiId = checkDefaultLi(flight);

        List<String> propsFromFlight = Arrays.asList(
                BLACK_LIST_ID.getName(),
                FREQUENCY_CAP.getName(),
                CHANNEL_IDS.getName(),
                RATE_TYPE.getName(),
                RATE_VALUE.getName(),
                BUDGET.getName(),
                MIN_CTR_GOAL.getName()
        );

        LineItem lineItem = newLiWithoutLinks(flight);
        lineItem.setPropertiesSource(EffectiveLineItemTool.buildPropsSource(propsFromFlight));

        LineItem lineItem2 = fliService.createLineItemAndUnSyncDefault(lineItem);
        entityProcessorContext.setEmptyValuesStrategy(new ConfigurableEmptyValuesStrategy(
                Collections.singleton(ConfigurableLineItemProperty.DATE_END.getName())));
        lineItemService.update(lineItem2);

        checkCustomLi(lineItem2.getId(), propsFromFlight);

        checkDefaultLiUnSync(defaultLiId, flight.getId());
    }

    private LineItem newLiWithoutLinks(Flight flight) {
        LineItem lineItem = LineItemBuilder.defaultLineItem(flight);
        lineItem.setName("EffectiveLineItemIntegrationTest LI w/o links " + vars.getTimestamp());

        lineItem.setDateStart(flight.getDateEnd());
        lineItem.setDateEnd(flight.getDateEnd());

        lineItem.setCreativeIds(Collections.emptyList());
        lineItem.setChannelIds(Collections.emptyList());
        lineItem.setGeoChannelIds(Collections.emptyList());
        lineItem.setExcludedGeoChannelIds(Collections.emptyList());
        lineItem.setSchedules(Collections.emptySet());
        lineItem.setConversionIds(Collections.emptyList());
        lineItem.setSiteIds(Collections.emptyList());

        return lineItem;
    }

    private void checkCreateFlight(Flight flight) {
        assertNotNull(flight);
        assertNotNull(flight.getId());
        assertEquals("Flight properties can be fethed from the flight only", Integer.valueOf(0), flight.getPropertiesSource());

        FlightLineItems flightLineItems = lineItemService.findEffectiveEagerByFlightId(flight.getId());
        assertEquals("Only default LI must exists", 1, flightLineItems.getLineItems().size());
    }

    private Long checkDefaultLi(Flight flight) {
        FlightLineItems flightLineItems = lineItemService.findEffectiveEagerByFlightId(flight.getId());
        assertEquals("Only default LI must exists", 1, flightLineItems.getLineItems().size());

        LineItem defaultLineItem = flightLineItems.getLineItems().get(0);
        List<String> defPropsWithFlightValues = EffectiveLineItemTool.getPropNamesFromFlight(defaultLineItem);
        List<String> absentPropNames = Arrays.asList(ConfigurableLineItemProperty.values()).stream()
                .map( e -> e.getName() )
                .filter( s -> !defPropsWithFlightValues.contains(s) )
                .collect(Collectors.toList());

        assertTrue("Default LI must take all values from Flight, but following absent: " + absentPropNames.stream().collect(Collectors.joining(", ")),
                absentPropNames.isEmpty());

        return defaultLineItem.getId();
    }

    private void checkDefaultLiUnSync(Long liId, Long flightId) {
        LineItem lineItem = lineItemService.findEager(liId);
        Flight flight = flightService.findEager(flightId);

        assertTrue("Now default Line Item must be un-synced", lineItem.getPropertiesSource().equals(0));

        JavaBeanAccessor<FlightBase> accessor = JavaBeanUtils.createJavaBeanAccessor(FlightBase.class, VersionEntityBase.class);
        Set<String> diffProperties = accessor.findDifferencies(lineItem, flight);
        diffProperties.remove("id");
        if (frequencyCapsEqual(lineItem.getFrequencyCap(), flight.getFrequencyCap())) {
            diffProperties.remove("frequencyCap");
        }
        if (collectionsEqual(lineItem.getSchedules(), flight.getSchedules())) {
            diffProperties.remove("schedules");
        }

        assertTrue(diffProperties.stream()
                    .collect(Collectors.joining(", ", "This props must be equal: ", "")),
                diffProperties.isEmpty());
    }

    private boolean frequencyCapsEqual(FrequencyCap v1, FrequencyCap v2) {
        JavaBeanAccessor<FrequencyCap> accessor = JavaBeanUtils.createJavaBeanAccessor(FrequencyCap.class, VersionEntityBase.class);
        Set<String> diffProperties = accessor.findDifferencies(v1, v2);
        return diffProperties.size() == 1 && diffProperties.iterator().next().equals("id");
    }

    private boolean schedulesEqual(FlightSchedule v1, FlightSchedule v2) {
        JavaBeanAccessor<FlightSchedule> accessor = JavaBeanUtils.createJavaBeanAccessor(FlightSchedule.class, EntityBase.class);
        Set<String> diffProperties = accessor.findDifferencies(v1, v2);
        return diffProperties.size() == 2 && diffProperties.contains("id") && diffProperties.contains("flight");
    }

    private boolean collectionsEqual(Collection<FlightSchedule> v1, Collection<FlightSchedule> v2) {
        if (v1.size() != v2.size()) {
            return false;
        }

        Iterator<FlightSchedule> it1 = v1.iterator();
        Iterator<FlightSchedule> it2 = v2.iterator();
        while (it1.hasNext()) {
            if (!schedulesEqual(it1.next(), it2.next())) {
                return false;
            }
        }

        return true;
    }

    private void checkCustomLi(Long newLiId, List<String> propsFromFlight) {
        LineItem newLineItem = lineItemService.findEffectiveEager(newLiId).getLineItems().get(0);

        List<String> newPropsWithFlightValues = EffectiveLineItemTool.getPropNamesFromFlight(newLineItem);
        List<String> absentPropNames = propsFromFlight.stream()
                .filter( s -> !newPropsWithFlightValues.contains(s) )
                .collect(Collectors.toList());
        assertTrue("Some values from Flight absent: " + absentPropNames.stream().collect(Collectors.joining(", ")),
                absentPropNames.isEmpty());

        List<String> excessivePropNames = newPropsWithFlightValues.stream()
                .filter( s -> !propsFromFlight.contains(s) )
                .collect(Collectors.toList());
        assertTrue("Some values from Flight are unexpected: " + excessivePropNames.stream().collect(Collectors.joining(", ")),
                excessivePropNames.isEmpty());

        assertNull("FieldsToReset must be cleared", newLineItem.getDateEnd());
    }
}

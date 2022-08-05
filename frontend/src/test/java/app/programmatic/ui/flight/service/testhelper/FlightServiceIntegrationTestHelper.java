package app.programmatic.ui.flight.service.testhelper;

import app.programmatic.ui.campaign.dao.model.CampaignDisplayStatus;
import app.programmatic.ui.common.model.RateType;
import app.programmatic.ui.flight.dao.model.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class FlightServiceIntegrationTestHelper {
    public static final String DEFAULT_FLIGHT_NAME = "Unit Test Flight ";
    public static final BigDecimal DEFAULT_FLIGHT_BUDGET = BigDecimal.valueOf(10000);

    public static Flight createMinimalFlight(Long accountId, LocalDateTime timestamp) {
        return createMinimalFlight(accountId, null, timestamp);
    }

    public static Flight createMinimalFlight(Long accountId, String name, LocalDateTime timestamp) {
        Flight result = new Flight();

        result.setOpportunity(createOpportunity(result, accountId, name, timestamp));
        result.setBudget(result.getOpportunity().getAmount());

        result.setDateStart(LocalDate.now());
        result.setBidStrategy(BidStrategy.CTR_BY_AMOUNT);
        result.setMinCtrGoal(BigDecimal.valueOf(0.1));
        result.setRateType(RateType.CPC);
        result.setRateValue(BigDecimal.valueOf(0.1));
        result.setDeliveryPacing(DeliveryPacing.U);
        result.setDisplayStatus(CampaignDisplayStatus.INACTIVE);
        result.setImpressionsPacing(TargetingPacing.U);
        result.setClicksPacing(TargetingPacing.U);

        result.setChannelIds(Collections.emptyList());
        result.setGeoChannelIds(Collections.emptyList());
        result.setExcludedGeoChannelIds(Collections.emptyList());
        result.setDeviceChannelIds(Collections.emptyList());
        result.setCreativeIds(Collections.emptyList());
        result.setSiteIds(Collections.emptyList());
        result.setConversionIds(Collections.emptyList());

        return result;
    }

    public static Flight createFlight(Long accountId, LocalDateTime timestamp) {
        return createFlight(accountId, null, timestamp);
    }

    public static Flight createFlight(Long accountId, String name, LocalDateTime timestamp) {
        Flight result = createMinimalFlight(accountId, name, timestamp);

        result.setFrequencyCap(createFrequencyCap());

        result.setDateEnd(LocalDate.now().plusDays(30));
        result.setSchedules(Collections.singleton(createFlightSchedule(result)));

        return result;
    }

    public static Opportunity createOpportunity(Flight flight, Long accountId, String name, LocalDateTime timestamp) {
        Opportunity result = new Opportunity();

        result.setFlight(flight);
        result.setName(DEFAULT_FLIGHT_NAME + timestamp);
        result.setAmount(DEFAULT_FLIGHT_BUDGET);
        result.setAccountId(accountId);
        result.setIoNumber((name != null ? name : DEFAULT_FLIGHT_NAME) + timestamp);

        return result;
    }

    public static void updateOpportunity(Opportunity opportunity, LocalDateTime timestamp) {
        opportunity.setName(opportunity.getName() + timestamp);
        opportunity.setAmount(BigDecimal.TEN.add(opportunity.getAmount()));
        opportunity.setIoNumber(opportunity.getName() + timestamp);
    }

    public static FrequencyCap createFrequencyCap() {
        FrequencyCap result = new FrequencyCap();

        result.setPeriod(Long.valueOf(13));
        result.setWindowLength(Long.valueOf(13));
        result.setWindowCount(13);
        result.setLifeCount(13);

        return result;
    }

    public static void updateFrequencyCap(FrequencyCap freqCap) {
        if (freqCap == null) {
            return;
        }

        freqCap.setPeriod(updateNullable(freqCap.getPeriod(), v -> Long.valueOf(13) + v));
        freqCap.setWindowLength(updateNullable(freqCap.getWindowLength(), v -> Long.valueOf(13) + v));
        freqCap.setWindowCount(updateNullable(freqCap.getWindowCount(), v -> 13 + v));
        freqCap.setLifeCount(updateNullable(freqCap.getLifeCount(), v -> 13 + v));
    }

    public static FlightSchedule createFlightSchedule(Flight flight) {
        return createFlightSchedule(flight, 0l);
    }

    public static FlightSchedule createFlightSchedule(Flight flight, long start) {
        FlightSchedule result = new FlightSchedule();

        result.setFlight(flight);
        result.setTimeFrom(start + 0l);
        result.setTimeTo(start + 1439l);

        return result;
    }

    public static Flight flightWithAllUpdatedFields(Flight flight) {
        LocalDateTime timestamp = LocalDateTime.now();

        flight.setId(flight.getId());
        flight.setDateStart(flight.getDateStart().plusDays(1));
        flight.setDateEnd(updateNullable(flight.getDateEnd(), date -> date.plusDays(1)));
        flight.setBidStrategy(changeBidStrategy(flight.getBidStrategy()));
        flight.setMinCtrGoal(BigDecimal.valueOf(0.1).add(flight.getMinCtrGoal()));
        flight.setRateType(changeRateType(flight.getRateType()));
        flight.setRateValue(BigDecimal.valueOf(0.1).add(flight.getRateValue()));
        flight.setDeliveryPacing(changeDeliveryPacing(flight.getDeliveryPacing()));
        flight.setDisplayStatus(changeDisplayStatus(flight.getDisplayStatus()));
        flight.setSchedules(changeSchedules(flight.getSchedules(), flight));

        updateFrequencyCap(flight.getFrequencyCap());
        updateOpportunity(flight.getOpportunity(), timestamp);
        flight.setBudget(flight.getOpportunity().getAmount());

        flight.setChannelIds(flight.getChannelIds());
        flight.setGeoChannelIds(flight.getGeoChannelIds());
        flight.setExcludedGeoChannelIds(flight.getExcludedGeoChannelIds());
        flight.setDeviceChannelIds(flight.getDeviceChannelIds());
        flight.setCreativeIds(flight.getCreativeIds());
        flight.setSiteIds(flight.getSiteIds());
        flight.setConversionIds(flight.getConversionIds());

        return flight;
    }

    private static BidStrategy changeBidStrategy(BidStrategy existing) {
        switch (existing) {
            case CTR_BY_PREDICTION:
                return BidStrategy.CTR_BY_AMOUNT;
            default:
                return BidStrategy.CTR_BY_PREDICTION;
        }
    }

    private static RateType changeRateType(RateType existing) {
        switch (existing) {
            case CPC:
                return RateType.CPM;
            default:
                return RateType.CPC;
        }
    }

    private static DeliveryPacing changeDeliveryPacing(DeliveryPacing existing) {
        switch (existing) {
            case U:
                return DeliveryPacing.D;
            default:
                return DeliveryPacing.U;
        }
    }

    private static CampaignDisplayStatus changeDisplayStatus(CampaignDisplayStatus existing) {
        switch (existing) {
            case INACTIVE:
                return CampaignDisplayStatus.LIVE;
            default:
                return CampaignDisplayStatus.INACTIVE;
        }
    }

    private static Set<FlightSchedule> changeSchedules(Set<FlightSchedule> existing, Flight flight) {
        if (existing.isEmpty() || existing.size() > 1) {
            return Collections.singleton(createFlightSchedule(flight));
        }

        Set<FlightSchedule> result = new HashSet<>(2);
        result.add(createFlightSchedule(flight, 1440));
        result.add(createFlightSchedule(flight, 2880));

        return result;
    }

    private static <T> T updateNullable(T src, ObjectUpdater<T> updater) {
        return src == null ? null : updater.update(src);
    }

    private interface ObjectUpdater<T> {
        T update(T src);
    }
}

package app.programmatic.ui.flight.service;

import app.programmatic.ui.flight.dao.model.Flight;

import java.util.List;
import java.util.concurrent.Callable;

public interface FlightServiceInternal extends FlightService {
    Long fetchCampaignId(Flight flight);

    List<Long> fetchCampaignIds(List<Long> flightIds);

    void runWithFlightLock(Long flightId, Runnable runnable);

    <T> T runWithFlightLock(Long flightId, Callable<T> callable);
}

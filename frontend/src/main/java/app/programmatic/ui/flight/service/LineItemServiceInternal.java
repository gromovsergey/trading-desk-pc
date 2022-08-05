package app.programmatic.ui.flight.service;

import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.LineItem;

import java.util.List;
import java.util.concurrent.Callable;

public interface LineItemServiceInternal extends LineItemService {

    LineItem findEager(Long id);

    List<LineItem> findByFlightId(Long flightId);

    LineItem createInternal(LineItem lineItem);

    void updateDefaultValues(LineItem lineItem);

    void linkAdvertisingChannelsFromFlight(Flight owner);

    void linkSitesFromFlight(Flight owner);

    void linkConversionsFromFlight(Flight owner);

    void linkCreativesFromFlight(Flight owner);

    void activateInternal(Long lineItemId);

    List<Long> fetchCcgIds(List<Long> lineItemIds);

    Long fetchOwnerId(Long lineItemId);

    void runWithFlightLock(Long flightId, Runnable runnable);

    <T> T runWithFlightLock(Long flightId, Callable<T> callable);
}

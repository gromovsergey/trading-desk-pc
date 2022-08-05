package app.programmatic.ui.flight.service;

import app.programmatic.ui.flight.tool.BlackWhiteIds;

import java.util.List;

public interface FlightChannelService {

    Long findAccountIdByFlightId(Long flightId);

    BlackWhiteIds createUrlsChannels(Long accountId, List<String> whiteUrls, List<String> blackUrls);

    void updateUrlsChannels(List<String> whiteUrls, Long whiteId, List<String> blackUrls, Long blackId);
}

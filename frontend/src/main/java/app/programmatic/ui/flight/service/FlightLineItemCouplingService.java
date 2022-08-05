package app.programmatic.ui.flight.service;

import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.flight.dao.model.LineItem;

import java.util.List;

public interface FlightLineItemCouplingService {

    LineItem createLineItemAndUnSyncDefault(LineItem lineItem);

    List<MajorDisplayStatus> deleteLineItemsAndSyncDefault(List<Long> lineItemIds);

    void linkAdvertisingChannels(Long flightId, List<Long> channelIds, boolean linkSpecialChannelFlag);
}

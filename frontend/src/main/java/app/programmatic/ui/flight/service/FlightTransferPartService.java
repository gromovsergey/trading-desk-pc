package app.programmatic.ui.flight.service;

import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.LineItem;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FlightTransferPartService {
    void general(Flight flight, LineItem lineItem);
    void blackUrlList(Long accountId, Flight flight, LineItem lineItem, List<String> flightBlackList, List<String> lineItemWhiteList);
    void whiteUrlList(Long accountId, Flight flight, LineItem lineItem, List<String> flightWhiteList, List<String> lineItemBlackList);
    void geo(LineItem lineItem, List<Long> flightExcludedGeoChannelIds, List<Long> flightGeoChannelIds);
    void defaultSetting(Flight flight, LineItem lineItem);
    void audit(Flight flight, LineItem lineItem);
    void ssp(Flight flight, LineItem lineItem);
    void creative(Flight flight, LineItem lineItem);
}

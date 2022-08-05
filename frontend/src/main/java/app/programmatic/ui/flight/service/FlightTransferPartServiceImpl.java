package app.programmatic.ui.flight.service;

import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.tool.BlackWhiteIds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class FlightTransferPartServiceImpl implements FlightTransferPartService {

    @Autowired
    private LineItemService lineItemService;

    @Override
    public void general(Flight flight, LineItem lineItem) {
        lineItem.setBudget(flight.getBudget());
        lineItem.setDailyBudget(flight.getDailyBudget());
        lineItem.setDateStart(flight.getDateStart());
        lineItem.setDateEnd(flight.getDateEnd());
        lineItem.setBidStrategy(flight.getBidStrategy());
        lineItem.setMinCtrGoal(flight.getMinCtrGoal());
        lineItem.setRateType(flight.getRateType());
        lineItem.setRateValue(flight.getRateValue());
        //lineItem.setDeliveryPacing(flight.getDeliveryPacing());//TODO Точно ли?
    }

    @Override
    public void blackUrlList(Long accountId, Flight flight, LineItem lineItem, List<String> flightBlackList, List<String> lineItemWhiteList) {
        BlackWhiteIds urlsChannels;
        if (lineItem.getBlackListId() == null) {
            urlsChannels = lineItemService.createUrlsChannels(accountId, lineItemWhiteList, flightBlackList);
            lineItem.setBlackListId(urlsChannels.getBlackListId());
            lineItem.setWhiteListId(urlsChannels.getWhiteListId());
        } else {
            lineItemService.updateUrlsChannels(lineItemWhiteList, lineItem.getWhiteListId(), flightBlackList, lineItem.getBlackListId());
        }
    }

    @Override
    public void whiteUrlList(Long accountId, Flight flight, LineItem lineItem, List<String> flightWhiteList, List<String> lineItemBlackList) {
        BlackWhiteIds urlsChannels;
        if (lineItem.getWhiteListId() == null) {
            urlsChannels = lineItemService.createUrlsChannels(accountId, flightWhiteList, lineItemBlackList);
            lineItem.setBlackListId(urlsChannels.getBlackListId());
            lineItem.setWhiteListId(urlsChannels.getWhiteListId());
        } else {
            lineItemService.updateUrlsChannels(flightWhiteList, lineItem.getWhiteListId(), lineItemBlackList, lineItem.getBlackListId());
        }
    }

    @Override
    @Transactional
    public void geo(LineItem lineItem, List<Long> flightExcludedGeoChannelIds, List<Long> flightGeoChannelIds) {
        lineItemService.linkGeo(lineItem, new ArrayList<>(flightExcludedGeoChannelIds), new ArrayList<>(flightGeoChannelIds));
    }

    @Override
    public void defaultSetting(Flight flight, LineItem lineItem) {
        audit(flight, lineItem);
        ssp(flight, lineItem);
        creative(flight, lineItem);
    }

    @Override
    public void audit(Flight flight, LineItem lineItem) {
        lineItem.setChannelIds(new ArrayList<>(flight.getChannelIds()));
    }

    @Override
    public void ssp(Flight flight, LineItem lineItem) {
        lineItem.setSiteIds(new ArrayList<>(flight.getSiteIds()));
    }

    @Override
    public void creative(Flight flight, LineItem lineItem) {
        lineItem.setCreativeIds(new ArrayList<>(flight.getCreativeIds()));
    }

}

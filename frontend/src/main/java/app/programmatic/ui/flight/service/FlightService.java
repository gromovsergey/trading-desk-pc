package app.programmatic.ui.flight.service;

import app.programmatic.ui.common.view.IdName;
import app.programmatic.ui.flight.dao.model.*;
import app.programmatic.ui.flight.dao.model.stat.FlightBaseStat;
import app.programmatic.ui.flight.dao.model.stat.FlightDashboardStat;
import app.programmatic.ui.flight.validation.ValidateFlight;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;


public interface FlightService extends FlightChannelService {

    List<FlightBaseStat> getAccountStat(Long accountId, LocalDateTime startDate, LocalDateTime endDate);

    List<IdName> getAccountFlightList(Long accountId);

    FlightBaseStat getStat(Long id);

    List<FlightDashboardStat> getDashboardStats(Integer days_back_count);

    Flight find(Long id);

    Flight findEager(Long id);
    
    CreativeIdsProjection findCreativeIds(Long flightId);

    SiteIdsProjection findSiteIds(Long flightId);

    Flight create(@ValidateFlight("create") Flight flight, boolean createInternalLineItem);

    Flight create(@ValidateFlight("create") Flight flight);

    Flight update(@ValidateFlight("update") Flight flight);

    void activate(@ValidateFlight("activate") Long id);

    void inactivate(@ValidateFlight("inactivate") Long id);

    void delete(@ValidateFlight("delete") Long id);

    void linkAdvertisingChannels(Long flightId,
                                 @ValidateFlight("linkAdvertisingChannels") List<Long> channelIds,
                                 boolean linkSpecialChannelFlag);

    void linkSites(Long flightId, List<Long> siteIds);

    void linkConversions(Long flightId, List<Long> conversionIds);

    void linkCreatives(Long flightId, List<Long> creativeIds);

    void uploadIoAttachment(MultipartFile file, Long flightId);

    List<String> listAttachments(Long flightId);

    byte[] downloadAttachment(String name, Long flightId);

    void deleteAttachment(String name, Long flightId);

    void updateAllocationAndCampaignRelation(Flight flight);

    void updateLineItemsByPartWithoutSave(Flight flight, LineItem lineItem, FlightPart flightPart, Long accountId);
}

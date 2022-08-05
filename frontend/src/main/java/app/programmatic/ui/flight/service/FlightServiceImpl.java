package app.programmatic.ui.flight.service;

import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.authorization.service.AuthorizationService;
import app.programmatic.ui.campaign.dao.model.CampaignDisplayStatus;
import app.programmatic.ui.campaign.dao.model.CampaignFlightPart;
import app.programmatic.ui.campaign.service.CampaignService;
import app.programmatic.ui.ccg.dao.model.CcgDisplayStatus;
import app.programmatic.ui.changetrack.dao.model.TableName;
import app.programmatic.ui.changetrack.service.ChangeTrackerService;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationsAware;
import app.programmatic.ui.common.aspect.prePersistProcessor.annotation.PrePersistAwareMethod;
import app.programmatic.ui.common.aspect.prePersistProcessor.annotation.PrePersistAwareService;
import app.programmatic.ui.common.datasource.DataSourceService;
import app.programmatic.ui.common.i18n.MessageInterpolator;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.validation.exception.EntityNotFoundException;
import app.programmatic.ui.common.view.IdName;
import app.programmatic.ui.file.service.FileService;
import app.programmatic.ui.flight.dao.CampaignAllocationRepository;
import app.programmatic.ui.flight.dao.FlightRepository;
import app.programmatic.ui.flight.dao.model.*;
import app.programmatic.ui.flight.dao.model.stat.FlightBaseStat;
import app.programmatic.ui.flight.dao.model.stat.FlightDashboardStat;
import app.programmatic.ui.flight.dao.model.stat.FlightDashboardStatInnerDto;
import app.programmatic.ui.flight.dao.model.stat.DisplayStatusAndFlightIdDto;
import app.programmatic.ui.flight.tool.CampaignBuilder;
import app.programmatic.ui.flight.tool.FlightLineItemsInfo;
import app.programmatic.ui.flight.tool.LineItemBuilder;
import app.programmatic.ui.restriction.model.Restriction;
import app.programmatic.ui.restriction.service.RestrictionService;
import app.programmatic.ui.user.dao.model.User;
import com.foros.rs.client.model.advertising.campaign.Campaign;
import com.foros.rs.client.model.advertising.channel.BehavioralChannel;
import com.foros.rs.client.model.entity.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static app.programmatic.ui.account.dao.model.AccountRole.INTERNAL;


@Service
@PrePersistAwareService(storedValueGetter = "find")
@Validated
public class FlightServiceImpl extends FlightBaseServiceImpl implements FlightServiceInternal {
    private static final Logger logger = Logger.getLogger(FlightServiceImpl.class.getName());

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private CampaignAllocationRepository campaignAllocationRepository;

    @Autowired
    private LineItemServiceInternal lineItemService;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private CampaignService campaignService;

    @Autowired
    private FileService fileService;

    @Autowired
    private RestrictionService restrictionService;

    @Autowired
    private ChangeTrackerService changeTrackerService;

    @Autowired
    private DataSourceService dsService;

    @Autowired
    private FlightTransferPartService transferPartService;

    @Override
    public List<FlightBaseStat> getAccountStat(Long accountId, LocalDateTime startDate, LocalDateTime endDate) {
        return jdbcOperations.query("select * from statqueries.flight_dashboard(?::date, ?::date, ?::int, ?, ?)",
                new Object[]{
                        startDate == null ? null : Timestamp.valueOf(startDate),
                        endDate == null ? null : Timestamp.valueOf(endDate),
                        accountId,
                        Boolean.TRUE,
                        Boolean.FALSE // ToDo: get from user,
                },
                (ResultSet rs, int index) -> rsToStat(rs)
        );
    }

    @Override
    public List<IdName> getAccountFlightList(Long accountId) {
        return jdbcOperations.query("select * from statqueries.get_flights_by_account(?::int)",
                new Object[]{accountId},
                (ResultSet rs, int index) -> new IdName(rs.getLong("flight_id"), rs.getString("name"))
        );
    }

    @Override
    public FlightBaseStat getStat(Long id) {
        return jdbcOperations.queryForObject("select * from statqueries.flight_total_stats(?::int)",
                new Object[]{id},
                (ResultSet rs, int index) -> {
                    FlightBaseStat stat = rsToStat(rs);
                    stat.setBudget(rs.getBigDecimal("budget"));
                    stat.setSpentBudget(rs.getBigDecimal("spent_budget"));
                    stat.setPostImpConv(rs.getLong("post_imp_conv"));
                    stat.setPostClickConv(rs.getLong("post_click_conv"));

                    return stat;
                });
    }


    @Override
    public List<FlightDashboardStat> getDashboardStats(Integer days_back_count) {
        User user = authorizationService.getAuthUser();
        boolean isInternal = user.getUserRole().getAccountRole() == INTERNAL;
        Long accountIdFilter = isInternal ? null : user.getAccountId();

        return dsService.executeWithAuth(jdbcOperations, () -> getDashboardStatsImpl(accountIdFilter, days_back_count));
    }

    private List<FlightDashboardStat> getDashboardStatsImpl(Long accountIdFilter, Integer days_back_count) {
        FlightDashboardStatInnerDto statInnerDto = getNotFullDashboardStatsImpl(accountIdFilter, days_back_count);
        Map<Long, DisplayStatusAndFlightIdDto> lineItemInfo = getMapLineItemInfo(statInnerDto.getLineItemIds());
        Map<Long, DisplayStatusAndFlightIdDto> mapFlightInfo = getMapFlightInfo(statInnerDto.getFlightIds());
        List<FlightDashboardStat> stats = statInnerDto.getFlightDashboardStats();
        for (FlightDashboardStat flightDashboardStat : stats) {
            if (flightDashboardStat.isFlight()) {
                flightDashboardStat.setDisplayStatus(mapFlightInfo != null ? mapFlightInfo.get(flightDashboardStat.getFlightId()).getStatus() : null);
                flightDashboardStat.setFlightName(mapFlightInfo != null ? mapFlightInfo.get(flightDashboardStat.getFlightId()).getName() : null);
            } else {
                flightDashboardStat.setDisplayStatus(lineItemInfo != null ? lineItemInfo.get(flightDashboardStat.getFlightId()).getStatus() : null);
                flightDashboardStat.setFlightName(lineItemInfo != null ? lineItemInfo.get(flightDashboardStat.getFlightId()).getName() : null);
            }
        }

        return statInnerDto.getFlightDashboardStats();
    }

    private Map<Long, DisplayStatusAndFlightIdDto> getMapLineItemInfo(List<Long> lineItemIds) {
        if (lineItemIds.isEmpty()) return null;
        String inSql = String.join(",", Collections.nCopies(lineItemIds.size(), "?"));
        List<DisplayStatusAndFlightIdDto> query = jdbcOperations.query(
                String.format("SELECT f.flight_id, ccg.display_status_id, ccg.name " +
                        "FROM flight f " +
                        "JOIN flightccg cc on cc.flight_id = f.flight_id " +
                        "JOIN campaigncreativegroup ccg on ccg.ccg_id = cc.ccg_id " +
                        "WHERE f.flight_id IN (%s)", inSql), lineItemIds.toArray(),
                (ResultSet rs, int index) -> {
                    CcgDisplayStatus ccgDisplayStatus = CcgDisplayStatus.valueOf(rs.getInt("display_status_id"));
                    return new DisplayStatusAndFlightIdDto(
                            rs.getLong("flight_id"),
                            (ccgDisplayStatus != null ? ccgDisplayStatus.getMajorStatus() : null),
                            rs.getString("name"));
                });
        return query.stream().collect(Collectors.toMap(DisplayStatusAndFlightIdDto::getFlightId, dto -> dto));
    }

    private Map<Long, DisplayStatusAndFlightIdDto> getMapFlightInfo(List<Long> flightIds) {
        if (flightIds.isEmpty()) return null;
        String inSql = String.join(",", Collections.nCopies(flightIds.size(), "?"));
        List<DisplayStatusAndFlightIdDto> query = jdbcOperations.query(
                String.format("SELECT f.flight_id, c.display_status_id, c.name " +
                                "FROM flight f " +
                                "join campaignallocation ca on ca.io_id = f.io_id " +
                                "join campaign c on c.campaign_id = ca.campaign_id " +
                                "WHERE f.flight_id IN (%s) " +
                                "group by f.flight_id, c.campaign_id " +
                                "having count(c.display_status_id)=1 ", inSql), flightIds.toArray(),
                (ResultSet rs, int index) -> {
                    CampaignDisplayStatus campaignDisplayStatus = CampaignDisplayStatus.valueOf(rs.getInt("display_status_id"));
                    return new DisplayStatusAndFlightIdDto(
                            rs.getLong("flight_id"),
                            (campaignDisplayStatus!= null ? campaignDisplayStatus.getMajorStatus() : null),
                            rs.getString("name")
                            );
                });
        return query.stream().collect(Collectors.toMap(DisplayStatusAndFlightIdDto::getFlightId, dto -> dto));
    }

    private FlightDashboardStatInnerDto getNotFullDashboardStatsImpl(Long accountIdFilter, Integer days_back_count) {
        AtomicReference<Long> count = new AtomicReference<>(0L);
        List<Long> flightIds = new ArrayList<>();
        List<Long> lineItemIds = new ArrayList<>();
        List<FlightDashboardStat> statList = jdbcOperations.query("SELECT " +
                        "a.name AS advertiser_name, " +
                        "a.account_id AS advertiser_id, " +
                        "ac.name AS agency_name, " +
                        "ac.account_id AS agency_id, " +
                        "f.flight_id AS flight_id, " +
                        "f.parent_id AS parent_id, " +
                        "f.version AS version " +
                        "FROM flight f " +
                        "LEFT JOIN account a on f.account_id = a.account_id " +
                        "LEFT JOIN account ac on ac.account_id = a.agency_account_id " +
                        "WHERE ((?::int IS NULL) OR (ac.account_id = ?::int) OR (a.account_id = ?::int)) " +
                        "AND (f.parent_id IS NULL OR f.parent_id IN (SELECT parent_id FROM flight WHERE parent_id IS NOT NULL GROUP BY parent_id HAVING count(parent_id)>1)) " +
                        "AND (f.version BETWEEN now()::timestamp - (interval '" + days_back_count + " day') AND now()::timestamp) " +
                        "ORDER BY f.version desc;",
                new Object[]{accountIdFilter, accountIdFilter, accountIdFilter}, //days_back_count
                (ResultSet rs, int index) -> {
                    count.getAndSet(count.get() + 1);
                    FlightDashboardStat stat = new FlightDashboardStat();

                    stat.setAgencyName(rs.getString("agency_name"));
                    stat.setAgencyId(rs.getLong("agency_id"));
                    stat.setAdvertiserName(rs.getString("advertiser_name"));
                    stat.setAdvertiserId(rs.getLong("advertiser_id"));
                    stat.setFlightId(rs.getLong("flight_id"));
                    stat.setVersion(rs.getTimestamp("version"));

                    if (rs.getObject("parent_id") != null) {
                        stat.setIsFlight(false);
                        lineItemIds.add(rs.getLong("flight_id"));
                    } else {
                        stat.setIsFlight(true);
                        flightIds.add(rs.getLong("flight_id"));
                    }

                    return stat;
                });

        return new FlightDashboardStatInnerDto(statList, count.get(), flightIds, lineItemIds);
    }

    private String alertReasonToString(Integer alertReasonId, CampaignDisplayStatus status) {
        String alertReasonKey;
        switch (alertReasonId) {
            case 0:
                alertReasonKey = status.getDescriptionKey();
                break;
            case 1:
                alertReasonKey = "flight.alertReason.statisticAlert";
                break;
            case 2:
                alertReasonKey = "flight.alertReason.newFlightAlert";
                break;
            default:
                throw new IllegalArgumentException("Unexpected alert reason id: " + alertReasonId);
        }

        return MessageInterpolator.getDefaultMessageInterpolator().interpolate(alertReasonKey);
    }

    @Override
    public Flight find(Long id) {
        Flight flight = id == null ? null : flightRepository.findById(id).orElse(null);
        if (flight == null) {
            return null;
        }

        Long campaignId = fetchCampaignId(flight);
        setCampaignPart(flight, campaignService.findFlightPart(campaignId));
        return flight;
    }

    @Override
    @Transactional(readOnly = true)
    public Flight findEager(Long id) {
        Flight flight = find(id);
        if (flight == null) {
            return null;
        }

        forFindEager(flight);
        return flight;
    }

    @Override
    public CreativeIdsProjection findCreativeIds(Long flightId) {
        return flightRepository.findCreativeIdsById(flightId);
    }

    @Override
    public SiteIdsProjection findSiteIds(Long flightId) {
        return flightRepository.findSiteIdsById(flightId);
    }

    @Override
    @Transactional
    @PrePersistAwareMethod
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.ForosFlightViolationsServiceImpl")
    public Flight create(Flight flight, boolean createInternalLineItem) {
        setDefaultDevicesIfNotSet(flight, flight.getOpportunity().getAccountId());

        Flight persisted = flightRepository.save(flight);
        Long campaignId = updateFlightCampaign(persisted);

        campaignAllocationRepository.save(newAllocation(campaignId, persisted.getOpportunity()));
        if (createInternalLineItem) lineItemService.createInternal(LineItemBuilder.defaultLineItem(persisted));
        return persisted;
    }

    @Override
    @Transactional
    @PrePersistAwareMethod
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.ForosFlightViolationsServiceImpl")
    public Flight create(Flight flight) {
        return create(flight, true);
    }

    @Override
    @Transactional
    @PrePersistAwareMethod
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.ForosFlightViolationsServiceImpl")
    public Flight update(Flight flight) {
        return runWithFlightLock(flight.getId(), () -> updateImpl(flight));
    }

    private Flight updateImpl(Flight flight) {
        Flight existing = flightRepository.findById(flight.getId()).orElse(null);

        flight.getOpportunity().setCampaignAllocations(existing.getOpportunity().getCampaignAllocations());
        setDefaultDevicesIfNotSet(flight);
        setIdsForExistingSchedules(flight, existing);

        Long campaignId = updateFlightCampaign(flight);
        Flight persisted = flightRepository.save(flight);
        updateFlightAllocation(persisted, campaignId);

        List<LineItem> lineItems = lineItemService.findByFlightId(flight.getId());
        lineItems.forEach(lineItem -> lineItemService.updateDefaultValues(lineItem));

        return persisted;
    }

    @Override
    @Transactional(readOnly = true)
    public void delete(Long id) {
        runWithFlightLock(id, () -> deleteImpl(id));
    }

    private void deleteImpl(Long id) {
        Flight flight = flightRepository.findById(id).orElse(null);
        updateFlightCampaignStatus(flight, Status.DELETED);
    }

    @Override
    @Transactional(readOnly = true)
    public void inactivate(Long id) {
        runWithFlightLock(id, () -> inactivateImpl(id));
    }

    private void inactivateImpl(Long id) {
        Flight flight = flightRepository.findById(id).orElse(null);
        updateFlightCampaignStatus(flight, Status.INACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public void activate(Long id) {
        runWithFlightLock(id, () -> activateImpl(id));
    }

    private void activateImpl(Long id) {
        Flight flight = flightRepository.findById(id).orElse(null);
        FlightLineItemsInfo liInfo = new FlightLineItemsInfo(flight.getId(), lineItemService);
        if (liInfo.isDefaultLineItemExist()) {
            // We should activate default line item automatically
            lineItemService.activateInternal(liInfo.getDefaultLineItemId());
        }

        updateFlightCampaignStatus(flight, Status.ACTIVE);
    }

    @Override
    @Transactional
    public void linkAdvertisingChannels(Long flightId, List<Long> channelIds, boolean linkSpecialChannelFlag) {
        runWithFlightLock(flightId,
                () -> linkAdvertisingChannelsImpl(flightId, channelIds, linkSpecialChannelFlag));
    }

    public void linkAdvertisingChannelsImpl(Long flightId, List<Long> channelIds, boolean linkSpecialChannelFlag) {
        Flight existing = flightRepository.findById(flightId).orElse(null);
        List<Long> existingIds = existing.getChannelIds();
        if (existingIds.size() == channelIds.size() &&
                existingIds.containsAll(channelIds) &&
                existing.getSpecialChannelLinked().equals(linkSpecialChannelFlag)) {
            return;
        }

        existing.setSpecialChannelLinked(linkSpecialChannelFlag);
        existing.setChannelIds(channelIds);
        lineItemService.linkAdvertisingChannelsFromFlight(existing);
    }

    @Override
    @Transactional
    public void linkSites(Long flightId, List<Long> siteIds) {
        runWithFlightLock(flightId,
                () -> linkImpl(flightId,
                        siteIds,
                        flight -> flight.getSiteIds(),
                        (flight, ids) -> flight.setSiteIds(ids),
                        (service, flight) -> service.linkSitesFromFlight(flight)));
    }

    @Override
    @Transactional
    public void linkConversions(Long flightId, List<Long> conversionIds) {
        runWithFlightLock(flightId,
                () -> linkImpl(flightId,
                        conversionIds,
                        flight -> flight.getConversionIds(),
                        (flight, ids) -> flight.setConversionIds(ids),
                        (service, flight) -> service.linkConversionsFromFlight(flight)));
    }

    @Override
    @Transactional
    public void linkCreatives(Long flightId, List<Long> creativeIds) {
        runWithFlightLock(flightId,
                () -> linkImpl(flightId,
                        creativeIds,
                        flight -> flight.getCreativeIds(),
                        (flight, ids) -> flight.setCreativeIds(ids),
                        (service, flight) -> service.linkCreativesFromFlight(flight)));
    }

    private void linkImpl(Long flightId, List<Long> ids, FlightBaseLinksGetter getter, FlightBaseLinksSetter setter, LineItemServiceCall service) {
        Flight flight = flightRepository.findById(flightId).orElse(null);
        List<Long> existingIds = getter.get(flight);
        if (existingIds.size() == ids.size() &&
                existingIds.containsAll(ids)) {
            return;
        }

        setter.set(flight, ids);
        service.call(lineItemService, flight);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    public void uploadIoAttachment(MultipartFile file, Long flightId) {
        Flight flight = findForAttachments(flightId);
        restrictionService.throwIfNotPermitted(Restriction.UPDATE_CAMPAIGN, fetchCampaignId(flight));

        try {
            fileService.uploadToIoRootAsAdmin(file, flight.getOpportunity().getAccountId(), flight.getOpportunity().getId(), "");
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    public List<String> listAttachments(Long flightId) {
        Flight flight = findForAttachments(flightId);
        restrictionService.throwIfNotPermitted(Restriction.VIEW_CAMPAIGN, fetchCampaignId(flight));

        try {
            return fileService.listFromIoRootAsAdmin(flight.getOpportunity().getAccountId(), flight.getOpportunity().getId(), "");
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    public byte[] downloadAttachment(String name, Long flightId) {
        Flight flight = findForAttachments(flightId);
        restrictionService.throwIfNotPermitted(Restriction.VIEW_CAMPAIGN, fetchCampaignId(flight));

        return fileService.downloadFromIoRootAsAdmin(flight.getOpportunity().getAccountId(), flight.getOpportunity().getId(), name);
    }

    @Override
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.AttachmentsValidationServiceImpl")
    public void deleteAttachment(String name, Long flightId) {
        Flight flight = findForAttachments(flightId);
        restrictionService.throwIfNotPermitted(Restriction.UPDATE_CAMPAIGN, fetchCampaignId(flight));

        try {
            fileService.deleteFromIoRootAsAdmin(flight.getOpportunity().getAccountId(), flight.getOpportunity().getId(), name);
        } catch (IOException e) {
            // Any exceptions are unexpected here
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Long> fetchCampaignIds(List<Long> flightIds) {
        Iterator<Flight> flightIterator = flightRepository.findAllById(flightIds).iterator();
        HashMap<Long, Long> resultMap = new HashMap<>(flightIds.size());
        while (flightIterator.hasNext()) {
            Flight flight = flightIterator.next();
            resultMap.put(flight.getId(), fetchCampaignId(flight));
        }

        return flightIds.stream()
                .map(id -> resultMap.get(id))
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void updateAllocationAndCampaignRelation(Flight flight) {
        runWithFlightLock(flight.getId(), () -> updateAllocationAndCampaignRelationImpl(flight));
    }

    private void updateAllocationAndCampaignRelationImpl(Flight flight) {
        Long campaignId = fetchCampaignId(flight);

        jdbcOperations.execute(String.format("select campaign_util.oncampaignallocationchanged(%d)", campaignId));

        changeTrackerService.saveChange(TableName.CAMPAIGN, campaignId);
    }

    private Flight findForAttachments(Long flightId) {
        Flight flight = flightId == null ? null : flightRepository.findById(flightId).orElse(null);
        if (flight == null) {
            throw new EntityNotFoundException(flightId);
        }

        return flight;
    }

    private static FlightBaseStat rsToStat(ResultSet rs) throws SQLException {
        FlightBaseStat stat = new FlightBaseStat();

        stat.setId(rs.getLong("flight_id"));
        stat.setName(rs.getString("name"));
        stat.setDisplayStatus(CampaignDisplayStatus.valueOf(rs.getInt("display_status_id")).getMajorStatus());
        stat.setRequests(rs.getLong("requests"));
        stat.setImpressions(rs.getLong("imps"));
        stat.setClicks(rs.getLong("clicks"));
        stat.setCtr(rs.getBigDecimal("ctr"));
        stat.setEcpm(rs.getBigDecimal("ecpm"));
        stat.setTotalCost(rs.getBigDecimal("total_cost"));

        return stat;
    }

    private void updateFlightAllocation(Flight flight, Long campaignId) {
        CampaignAllocation allocation;
        if (!flight.getOpportunity().getCampaignAllocations().isEmpty()) {
            allocation = flight.getOpportunity().getCampaignAllocations().iterator().next();
            BigDecimal newAmount = flight.getOpportunity().getAmount();
            if (allocation.getAmount().compareTo(newAmount) == 0) {
                return;
            }
            allocation.setAmount(newAmount);
            allocation.setStatus(CampaignAllocationStatus.A);
        } else {
            allocation = newAllocation(campaignId, flight.getOpportunity());
        }

        campaignAllocationRepository.save(allocation);
    }

    private Long updateFlightCampaign(Flight flight) {
        Long existingCampaignId = fetchCampaignId(flightRepository.findById(flight.getId()).orElse(null));
        Campaign existingCampaign = existingCampaignId == null ? null : campaignService.find(existingCampaignId);

        AdvertisingAccount account = accountService.findAdvertisingUnchecked(flight.getOpportunity().getAccountId());
        Campaign result = CampaignBuilder.build(flight,
                authorizationService.getAuthUserInfo().getId(),
                account.getTimeZone(),
                existingCampaign);
        return campaignService.createOrUpdate(result);
    }

    private void updateFlightCampaignStatus(Flight flight, Status status) {
        Long campaignId = fetchCampaignId(flight);
        if (campaignId != null) {
            updateCampaignStatus(campaignId, status);
        }
    }

    private void updateCampaignStatus(Long campaignId, Status status) {
        Campaign campaign = campaignService.find(campaignId);
        campaign.setStatus(status);
        campaignService.createOrUpdate(campaign);
    }

    private CampaignAllocation newAllocation(Long campaignId, Opportunity opportunity) {
        CampaignAllocation allocation = new CampaignAllocation();
        allocation.setCampaignId(campaignId);
        allocation.setOpportunity(opportunity);
        allocation.setAmount(opportunity.getAmount());
        allocation.setOrder(1L);
        allocation.setStatus(CampaignAllocationStatus.A);

        opportunity.getCampaignAllocations().add(allocation);

        return allocation;
    }

    private static void setCampaignPart(Flight flight, CampaignFlightPart campaignPart) {
        if (campaignPart == null) {
            logger.log(Level.WARNING, "Flight has no Campaign, flight id = " + flight.getId());
            return;
        }

        flight.setDisplayStatus(campaignPart.getDisplayStatus());
    }

    private interface LineItemServiceCall {
        void call(LineItemServiceInternal service, Flight flight);
    }

    @Override
    public void updateLineItemsByPartWithoutSave(Flight flight, LineItem lineItem, FlightPart flightPart, Long accountId) {
        switch (flightPart) {
            case GENERAL:
                transferPartService.general(flight, lineItem);
                break;
            case BLACK_LIST:
                transferPartService.blackUrlList(accountId, flight, lineItem, getBlackList(flight), getWhiteList(lineItem));
                break;
            case WHILE_LIST:
                transferPartService.whiteUrlList(accountId, flight, lineItem, getWhiteList(flight), getBlackList(lineItem));
                break;
            case GEO:
                transferPartService.geo(lineItem, flight.getExcludedGeoChannelIds(), flight.getGeoChannelIds());
                break;
            case DEFAULT_SETTING:
                transferPartService.defaultSetting(flight, lineItem);
                break;
            case AUDIT_SEGMENT:
                transferPartService.audit(flight, lineItem);
                break;
            case SSP:
                transferPartService.ssp(flight, lineItem);
                break;
            case CREATIVE:
                transferPartService.creative(flight, lineItem);
                break;
        }
    }

    protected List<String> getWhiteList(FlightBase flightBase) {
        return getList(flightBase.getWhiteListId());
    }

    protected List<String> getBlackList(FlightBase flightBase) {
        return getList(flightBase.getBlackListId());
    }

    private List<String> getList(Long listChannelId) {
        if (listChannelId == null) {
            return Collections.emptyList();
        }

        BehavioralChannel channel = channelService.findBehavioralUnchecked(listChannelId);
        return channel.getUrls().getPositive();
    }
}

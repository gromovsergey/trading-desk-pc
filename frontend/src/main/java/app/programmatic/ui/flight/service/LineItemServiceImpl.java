package app.programmatic.ui.flight.service;

import static app.programmatic.ui.common.model.MajorDisplayStatus.DELETED;

import app.programmatic.ui.geo.service.GeoService;
import app.programmatic.ui.site.dao.model.Site;
import app.programmatic.ui.site.dao.model.SiteStat;
import app.programmatic.ui.site.service.SiteService;
import com.foros.rs.client.model.advertising.campaign.CampaignCreativeGroup;
import com.foros.rs.client.model.advertising.campaign.CreativeLink;
import com.foros.rs.client.model.entity.EntityLink;
import com.foros.rs.client.model.entity.Status;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.ccg.dao.model.CcgDisplayStatus;
import app.programmatic.ui.ccg.dao.model.CcgLineItemPart;
import app.programmatic.ui.ccg.service.CcgService;
import app.programmatic.ui.common.aspect.prePersistProcessor.annotation.PrePersistAwareMethod;
import app.programmatic.ui.common.aspect.prePersistProcessor.annotation.PrePersistAwareService;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationsAware;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.tool.foros.ForosHelper;
import app.programmatic.ui.common.validation.exception.EntityNotFoundException;
import app.programmatic.ui.creativelink.service.CreativeLinkService;
import app.programmatic.ui.flight.dao.FlightRepository;
import app.programmatic.ui.flight.dao.LineItemRepository;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.FlightLineItems;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.dao.model.stat.FlightBaseStat;
import app.programmatic.ui.flight.tool.CcgBuilder;
import app.programmatic.ui.flight.tool.CcgChannelHelper;
import app.programmatic.ui.flight.tool.CcgChannelHelper.AdvertisingAccountFetcher;
import app.programmatic.ui.flight.tool.EffectiveLineItemTool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


@Service
@PrePersistAwareService(storedValueGetter = "find")
@Validated
public class LineItemServiceImpl extends FlightBaseServiceImpl implements LineItemServiceInternal {
    private static final Logger logger = Logger.getLogger(LineItemServiceImpl.class.getName());

    @Autowired
    private LineItemRepository lineItemRepository;

    @Autowired
    private FlightRepository flightRepository;

    @Autowired
    private CcgService ccgService;

    @Autowired
    private GeoService geoService;

    @Autowired
    private CreativeLinkService creativeLinkService;

    @Autowired
    private SiteService siteService;

    @Override
    public LineItem find(Long id) {
        LineItem lineItem = id == null ? null : lineItemRepository.findById(id).orElse(null);
        if (lineItem != null) {
            setCcgPart(lineItem, ccgService.findLineItemPart(lineItem.getCcgId()));
        }

        if (lineItem == null || lineItem.getMajorStatus() == DELETED) {
            throw new EntityNotFoundException(id);
        }

        return lineItem;
    }

    @Override
    @Transactional(readOnly = true)
    public LineItem findEager(Long id) {
        LineItem lineItem = find(id);
        if (lineItem == null) {
            return null;
        }

        forFindEager(lineItem);
        return lineItem;
    }

    @Override
    @Transactional(readOnly = true)
    public FlightLineItems findEffectiveEager(Long id) {
        LineItem lineItem = find(id);
        forFindEager(lineItem);

        Flight owner = flightRepository.findById(lineItem.getFlightId()).orElse(null);
        forFindEager(owner);

        return new FlightLineItems(owner, EffectiveLineItemTool.buildEffective(lineItem, owner));
    }

    @Override
    public List<FlightBaseStat> getFlightStat(Long flightId, LocalDateTime startDate, LocalDateTime endDate) {
        return jdbcOperations.query("select * from statqueries.line_item_dashboard(?::date, ?::date, ?::int, ?, ?)",
                new Object[]{
                        startDate == null ? null : Timestamp.valueOf(startDate),
                        endDate == null ? null : Timestamp.valueOf(endDate),
                        flightId,
                        Boolean.TRUE,
                        Boolean.FALSE // ToDo: get from user
                },
                (ResultSet rs, int index) -> rsToStat(rs)
        );
    }

    @Override
    public FlightBaseStat getStat(Long id) {
        return jdbcOperations.queryForObject("select * from statqueries.line_item_total_stats(?::int)",
                new Object[]{id},
                (ResultSet rs, int index) -> {
                    FlightBaseStat stat = rsToStat(rs);
                    stat.setBudget(rs.getBigDecimal("budget"));
                    stat.setSpentBudget(rs.getBigDecimal("total_cost"));
                    stat.setPostImpConv(rs.getLong("post_imp_conv"));
                    stat.setPostClickConv(rs.getLong("post_click_conv"));

                    return stat;
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<LineItem> findEagerByFlightId(Long flightId) {
        return findByFlightId(flightId).stream()
                .map(lineItem -> {
                    forFindEager(lineItem);
                    return lineItem;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<LineItem> findByFlightId(Long flightId) {
        List<LineItem> lineItems = lineItemRepository.findByFlightId(flightId);
        Map<Long, CcgLineItemPart> ccgParts = (ccgService.findLineItemPart(
                lineItems.stream()
                        .map(item -> item.getCcgId())
                        .collect(Collectors.toList())))
                .stream()
                .collect(Collectors.toMap(e -> e.getCcgId(), e -> e));
        lineItems.forEach(item -> setCcgPart(item, ccgParts.get(item.getCcgId())));
        return lineItems.stream()
                .filter(lineItem -> DELETED != lineItem.getMajorStatus())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FlightLineItems findEffectiveEagerByFlightId(Long flightId) {
        Flight owner = flightRepository.findById(flightId).orElse(null);
        forFindEager(owner);
        return new FlightLineItems(owner, findEagerByFlightId(flightId).stream()
                .map(li -> EffectiveLineItemTool.buildEffective(li, owner))
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional
    @PrePersistAwareMethod
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.ForosCcgViolationsServiceImpl")
    public LineItem create(LineItem lineItem) {
        return runWithFlightLock(lineItem.getFlightId(), () -> createImpl(lineItem));
    }

    private LineItem createImpl(LineItem lineItem) {
        Flight owner = flightRepository.findById(lineItem.getFlightId()).orElse(null);
        setDefaultDevicesIfNotSet(lineItem, owner.getOpportunity().getAccountId());
        if (lineItem.getBudget() == null) {
            lineItem.setBudget(owner.getOpportunity().getAmount());
        }
        Long ccgId = createOrUpdateLineItemCcg(lineItem);
        lineItem.setCcgId(ccgId);
        lineItem.setAccountId(owner.getAccountId());

        LineItem result = lineItemRepository.save(lineItem);
        return result;
    }

    @Override
    @Transactional
    @PrePersistAwareMethod
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.ForosCcgViolationsServiceImpl")
    public LineItem createInternal(LineItem lineItem) {
        return createImpl(lineItem);
    }

    @Override
    @Transactional
    @PrePersistAwareMethod
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.ForosCcgViolationsServiceImpl")
    public LineItem update(LineItem lineItem) {
        return runWithFlightLock(lineItem.getFlightId(), () -> updateImpl(lineItem));
    }

    @Override
    @Transactional
    @PrePersistAwareMethod
    public void updateDefaultValues(LineItem lineItem) {
        updateImpl(lineItem);
    }

    @Override
    @Transactional(readOnly = true)
    public void activateInternal(Long lineItemId) {
        updateLineItemCcgStatus(Collections.singletonList(lineItemId), Status.ACTIVE);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MajorDisplayStatus> activate(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        return runWithFlightLock(fetchOwnerId(ids.get(0)),
                () -> updateLineItemCcgStatus(ids, Status.ACTIVE));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MajorDisplayStatus> inactivate(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        return runWithFlightLock(fetchOwnerId(ids.get(0)),
                () -> updateLineItemCcgStatus(ids, Status.INACTIVE));
    }

    @Override
    @Transactional
    public List<MajorDisplayStatus> delete(List<Long> ids) {
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        return runWithFlightLock(fetchOwnerId(ids.get(0)),
                () -> updateLineItemCcgStatus(ids, Status.DELETED));
    }

    @Override
    @Transactional
    public void linkAdvertisingChannels(Long id, List<Long> channelIds, boolean linkSpecialChannelFlag) {
        runWithFlightLock(fetchOwnerId(id),
                () -> linkAdvertisingChannelsImpl(id, channelIds, linkSpecialChannelFlag));
    }

    @Override
    public List<Long> fetchLineItemIds(Long flightId) {
        return jdbcOperations.query("select flight_id from flight f " +
                        "  inner join flightccg fg using(flight_id) " +
                        "  inner join campaigncreativegroup c using(ccg_id) " +
                        "  where c.status != 'D' and parent_id = ?",
                new Object[]{flightId},
                (ResultSet rs, int ind) -> rs.getLong("flight_id"));
    }

    @Override
    public List<Long> fetchCcgIds(List<Long> lineItemIds) {
        Iterator<LineItem> lineItemIterator = lineItemRepository.findAllById(lineItemIds).iterator();
        HashMap<Long, Long> resultMap = new HashMap<>(lineItemIds.size());
        while (lineItemIterator.hasNext()) {
            LineItem lineItem = lineItemIterator.next();
            resultMap.put(lineItem.getId(), lineItem.getCcgId());
        }

        return lineItemIds.stream()
                .map(id -> resultMap.get(id))
                .collect(Collectors.toList());
    }

    private void linkAdvertisingChannelsImpl(Long lineItemId, List<Long> channelIds, boolean linkSpecialChannelFlag) {
        LineItem lineItem = find(lineItemId);
        if (lineItem.getChannelIds().size() == channelIds.size() &&
                lineItem.getChannelIds().containsAll(channelIds) &&
                lineItem.getSpecialChannelLinked().equals(linkSpecialChannelFlag)) {
            return;
        }

        linkSpecialChannelsImpl(lineItem, linkSpecialChannelFlag);
        linkAdvertisingChannelsImpl(lineItem, channelIds);
    }

    private void linkAdvertisingChannelsImpl(LineItem lineItem, List<Long> channelIds) {
        Flight owner = flightRepository.findById(lineItem.getFlightId()).orElse(null);
        Long newCcgChannelId = updateAdvertisingChannels(
                lineItem,
                channelIds,
                lineItem.getSpecialChannelLinked() ? lineItem.getSpecialChannelId() : null,
                lineItem.getWhiteListId() != null ? lineItem.getWhiteListId() : owner.getWhiteListId(),
                lineItem.getBlackListId() != null ? lineItem.getBlackListId() : owner.getBlackListId(),
                () -> accountService.findAdvertisingUnchecked(owner.getOpportunity().getAccountId()));

        if (lineItem.getCcgChannelId() != null ? !lineItem.getCcgChannelId().equals(newCcgChannelId) : newCcgChannelId != null) {
            CampaignCreativeGroup ccg = new CampaignCreativeGroup();
            ccg.setId(lineItem.getCcgId());
            CcgBuilder.updateChannelTarget(newCcgChannelId, ccg);

            ccgService.createOrUpdate(ccg);
        }

        lineItem.setChannelIds(channelIds);
    }

    private void linkSpecialChannelsImpl(LineItem lineItem, boolean linkSpecialChannelFlag) {
        if (linkSpecialChannelFlag && lineItem.getSpecialChannelId() == null) {
            Long specialChannelId = channelService.createSpecialChannel(lineItem.getId());
            lineItem.setSpecialChannelId(specialChannelId);
        }
        lineItem.setSpecialChannelLinked(linkSpecialChannelFlag);
    }

    private Long updateAdvertisingChannels(LineItem lineItem, List<Long> channelIds, Long specialChannelId,
                                           Long whiteListId, Long blackListId, AdvertisingAccountFetcher accountFetcher) {
        CcgChannelHelper ccgChannelHelper = new CcgChannelHelper(channelService, lineItem, accountFetcher);
        Long result = ccgChannelHelper.saveChannelList(channelIds, specialChannelId, whiteListId, blackListId);
        if (ccgChannelHelper.getId() != null && !ccgChannelHelper.getId().equals(result)) {
            ccgChannelHelper.deleteExisting();
        }
        return result;
    }

    @Override
    @Transactional
    public void linkAdvertisingChannelsFromFlight(Flight owner) {
        List<LineItem> lineItems = findByFlightId(owner.getId());
        ArrayList<CampaignCreativeGroup> ccgsToUpdate = new ArrayList<>(lineItems.size());
        for (LineItem lineItem : lineItems) {
            if (!lineItem.getChannelIds().isEmpty()) {
                continue;
            }
            Long newCcgChannelId = updateAdvertisingChannels(
                    lineItem,
                    owner.getChannelIds(),
                    lineItem.getSpecialChannelLinked() ? lineItem.getSpecialChannelId() : null,
                    lineItem.getWhiteListId() != null ? lineItem.getWhiteListId() : owner.getWhiteListId(),
                    lineItem.getBlackListId() != null ? lineItem.getBlackListId() : owner.getBlackListId(),
                    () -> accountService.findAdvertisingUnchecked(owner.getOpportunity().getAccountId()));

            if (lineItem.getCcgChannelId() != null ? !lineItem.getCcgChannelId().equals(newCcgChannelId) : newCcgChannelId != null) {
                CampaignCreativeGroup ccg = new CampaignCreativeGroup();
                ccg.setId(lineItem.getCcgId());
                CcgBuilder.updateChannelTarget(newCcgChannelId, ccg);

                ccgsToUpdate.add(ccg);
            }
        }

        ccgService.createOrUpdate(ccgsToUpdate);
    }

    @Override
    @Transactional
    public void linkSites(Long id, List<Long> siteIds) {
        runWithFlightLock(fetchOwnerId(id),
                () -> linkImpl(id,
                        siteIds,
                        lineItem -> lineItem.getSiteIds(),
                        (lineItem, ids) -> lineItem.setSiteIds(ids),
                        (ccg, links) -> ccg.setSites(links)));
    }

    @Override
    @Transactional
    public void setSites(List<Long> ids, List<Long> siteIds) {
        for (Long lineItemId : ids) {
            linkSites(lineItemId, siteIds);
        }
    }

    @Override
    @Transactional
    public void addSites(List<Long> ids, List<Long> siteIds) {
        for (Long lineItemId : ids) {
            List<SiteStat> siteCurrentStat = siteService.getStatsByLineItemId(lineItemId);
            List<Long> siteCurrentIds = siteCurrentStat.stream()
                    .map(Site::getSiteId)
                    .collect(Collectors.toList());
            for (Long siteId : siteIds) {
                if (!siteCurrentIds.contains(siteId)) siteCurrentIds.add(siteId);
            }
            if ((long) siteCurrentStat.size() != (long) siteCurrentIds.size()) linkSites(lineItemId, siteCurrentIds);
        }
    }

    @Override
    @Transactional
    public void deleteSites(List<Long> ids, List<Long> siteIds) {
        for (Long lineItemId : ids) {
            List<Long> siteCurrentIds = siteService.getStatsByLineItemId(lineItemId)
                    .stream()
                    .map(Site::getSiteId)
                    .filter(siteId -> !siteIds.contains(siteId))
                    .collect(Collectors.toList());
            linkSites(lineItemId, siteCurrentIds);
        }
    }

    @Override
    @Transactional
    public void linkGeo(LineItem lineItem, List<Long> excludedGeoChannelIds, List<Long> geoChannelIds) {
        runWithFlightLock(fetchOwnerId(lineItem.getId()),
                () -> linkImplGeo(lineItem,
                        excludedGeoChannelIds,
                        geoChannelIds,
                        CampaignCreativeGroup::setGeoChannelsExcluded,
                        CampaignCreativeGroup::setGeoChannels
                )
        );
    }

    @Override
    @Transactional
    public void setGeo(List<Long> ids, List<Long> excludedGeoChannelIds, List<Long> geoChannelIds) {
        for (Long lineItemId : ids) {
            LineItem lineItem = find(lineItemId);
            linkGeo(lineItem, excludedGeoChannelIds, geoChannelIds);
        }
    }

    @Override
    @Transactional
    public void addGeo(List<Long> ids, List<Long> excludedGeoChannelIds, List<Long> geoChannelIds) {
        for (Long lineItemId : ids) {
            LineItem lineItem = find(lineItemId);
            List<Long> geoCurrentChannelIds = lineItem.getGeoChannelIds();
            List<Long> geoCurrentExcludedChannelIds = lineItem.getExcludedGeoChannelIds();

            for (Long excludedGeoId : excludedGeoChannelIds) {
                if (!geoCurrentExcludedChannelIds.contains(excludedGeoId)) geoCurrentExcludedChannelIds.add(excludedGeoId);
            }
            for (Long geoId : geoChannelIds) {
                if (!geoCurrentChannelIds.contains(geoId)) geoCurrentChannelIds.add(geoId);
            }
            linkGeo(lineItem, geoCurrentExcludedChannelIds, geoCurrentChannelIds);
        }
    }

    @Override
    @Transactional
    public void deleteGeo(List<Long> ids, List<Long> excludedGeoChannelIds, List<Long> geoChannelIds) {
        for (Long lineItemId : ids) {
            LineItem lineItem = find(lineItemId);
            List<Long> channelGeoIds = lineItem.getGeoChannelIds()
                    .stream()
                    .filter(id -> !geoChannelIds.contains(id))
                    .collect(Collectors.toList());
            List<Long> excludedGeoIds = lineItem.getExcludedGeoChannelIds()
                    .stream()
                    .filter(id -> !excludedGeoChannelIds.contains(id))
                    .collect(Collectors.toList());
            linkGeo(lineItem, excludedGeoIds, channelGeoIds);
        }
    }

    @Override
    @Transactional
    public void linkConversions(Long id, List<Long> conversionIds) {
        runWithFlightLock(fetchOwnerId(id),
                () -> linkImpl(id,
                        conversionIds,
                        lineItem -> lineItem.getConversionIds(),
                        (lineItem, ids) -> lineItem.setConversionIds(ids),
                        (ccg, links) -> ccg.setConversions(links)));
    }

    @Override
    @Transactional
    public void linkCreatives(Long id, List<Long> creativeIds) {
        runWithFlightLock(fetchOwnerId(id), () -> linkCreativesImpl(id, creativeIds));
    }

    private void linkCreativesImpl(Long id, List<Long> creativeIds) {
        LineItem lineItem = find(id);
        if (lineItem.getCreativeIds().size() == creativeIds.size() &&
                lineItem.getCreativeIds().containsAll(creativeIds)) {
            return;
        }

        updateCcgCreativeLinks(lineItem.getCcgId(), creativeIds);

        lineItem.setCreativeIds(creativeIds);
        lineItemRepository.save(lineItem);
    }

    private void updateCcgCreativeLinks(Long ccgId, List<Long> creativeIds) {
        List<CreativeLink> existings = creativeLinkService.findByCcgId(ccgId);
        HashSet<Long> creativeIdsSet = new HashSet<>(creativeIds);

        ArrayList<CreativeLink> toUpdate = new ArrayList<>(creativeIds.size() + existings.size());
        for (CreativeLink existing : existings) {
            if (creativeIdsSet.remove(existing.getCreative().getId())) {
                if (existing.getStatus() == Status.DELETED) {
                    existing.setStatus(Status.ACTIVE);
                }
                toUpdate.add(existing);
            } else if (existing.getStatus() != Status.DELETED) {
                existing.setStatus(Status.DELETED);
                toUpdate.add(existing);
            }
        }
        for (Long creativeId : creativeIdsSet) {
            CreativeLink linkToUpdate = new CreativeLink();
            linkToUpdate.setStatus(Status.ACTIVE);
            linkToUpdate.setCreative(ForosHelper.createEntityLink(creativeId));
            linkToUpdate.setCreativeGroup(ForosHelper.createEntityLink(ccgId));
            linkToUpdate.setWeight(1L);

            toUpdate.add(linkToUpdate);
        }

        creativeLinkService.createOrUpdate(toUpdate);
    }

    private void linkImpl(Long lineItemId, List<Long> ids, FlightBaseLinksGetter getter, FlightBaseLinksSetter setter, CcgLinksUpdater ccgUpdater) {
        LineItem lineItem = find(lineItemId);
        List<Long> lineItemIds = getter.get(lineItem);
        if (lineItemIds.size() == ids.size() &&
                lineItemIds.containsAll(ids)) {
            return;
        }

        updateCcgLinks(lineItem.getCcgId(), ids, ccgUpdater); //TODO Зачем CcgId при обновлении site/Нужно ли для geo также обновлять. Для чего это.

        setter.set(lineItem, ids);
        lineItemRepository.save(lineItem);
    }


    private void linkImplGeo(LineItem lineItem, List<Long> excludedGeoChannelIds, List<Long> geoChannelIds, CcgLinksUpdater ccgUpdaterExcludedGeo, CcgLinksUpdater ccgUpdaterGeo) {
        List<Long> oldGeoChannelIds = lineItem.getGeoChannelIds();
        List<Long> oldExcludedGeoChannelIds = lineItem.getExcludedGeoChannelIds();

        if (oldGeoChannelIds.size() == geoChannelIds.size() && oldGeoChannelIds.containsAll(geoChannelIds) &&
                oldExcludedGeoChannelIds.size() == excludedGeoChannelIds.size() && oldExcludedGeoChannelIds.containsAll(excludedGeoChannelIds)) {
            return;
        }

        //updateCcgLinks(lineItem.getCcgId(), excludedGeoChannelIds, ccgUpdaterExcludedGeo); //TODO Зачем CcgId при обновлении site/Нужно ли для geo также обновлять. Для чего это.
        //updateCcgLinks(lineItem.getCcgId(), geoChannelIds, ccgUpdaterGeo); //TODO Зачем CcgId при обновлении site/Нужно ли для geo также обновлять. Для чего это.

        lineItem.setGeoChannelIds(geoChannelIds);
        lineItem.setExcludedGeoChannelIds(excludedGeoChannelIds);
        lineItemRepository.save(lineItem);
    }

    private void updateCcgLinks(Long ccgId, List<Long> ids, CcgLinksUpdater ccgUpdater) {
        CampaignCreativeGroup ccg = ccgService.find(ccgId);
        ccgUpdater.update(ccg, ids.stream().map(ForosHelper::createEntityLink)
                .collect(Collectors.toList()));
        ccgService.createOrUpdate(ccg);
    }

    @Override
    @Transactional
    public void linkSitesFromFlight(Flight owner) {
        linkFromFlightImpl(owner,
                flightBase -> flightBase.getSiteIds(),
                (ccg, links) -> ccg.setSites(links));
    }

    @Override
    @Transactional
    public void linkConversionsFromFlight(Flight owner) {
        linkFromFlightImpl(owner,
                flightBase -> flightBase.getConversionIds(),
                (ccg, links) -> ccg.setConversions(links));
    }

    @Override
    @Transactional
    public void linkCreativesFromFlight(Flight owner) {
        for (LineItem lineItem : findByFlightId(owner.getId())) {
            if (!lineItem.getCreativeIds().isEmpty()) {
                continue;
            }
            updateCcgCreativeLinks(lineItem.getCcgId(), owner.getCreativeIds());
        }
    }

    private void linkFromFlightImpl(Flight owner, FlightBaseLinksGetter getter, CcgLinksUpdater ccgUpdater) {
        List<Long> ownerIds = getter.get(owner);
        for (LineItem lineItem : findByFlightId(owner.getId())) {
            if (!getter.get(lineItem).isEmpty()) {
                continue;
            }
            updateCcgLinks(lineItem.getCcgId(), ownerIds, ccgUpdater);
        }
    }

    private LineItem updateImpl(LineItem lineItem) {
        setDefaultDevicesIfNotSet(lineItem);
        setIdsForExistingSchedules(lineItem, lineItemRepository.findById(lineItem.getId()).orElse(null));
        createOrUpdateLineItemCcg(lineItem);
        LineItem result = lineItemRepository.save(lineItem);
        return result;
    }

    private Long createOrUpdateLineItemCcg(LineItem lineItem) {
        CampaignCreativeGroup existing = lineItem.getCcgId() == null ? null :
                ccgService.find(lineItem.getCcgId());

        Flight owner = flightRepository.findById(lineItem.getFlightId()).orElse(null);
        LineItem effectiveLineItem = EffectiveLineItemTool.buildEffective(lineItem, owner);

        AdvertisingAccount account = accountService.findAdvertisingUnchecked(owner.getOpportunity().getAccountId());

        Long expressionChannelId = updateAdvertisingChannels(effectiveLineItem,
                effectiveLineItem.getChannelIds(),
                lineItem.getSpecialChannelLinked() ? lineItem.getSpecialChannelId() : null,
                effectiveLineItem.getWhiteListId(),
                effectiveLineItem.getBlackListId(),
                () -> account);

        CampaignCreativeGroup ccg = CcgBuilder.build(
                effectiveLineItem, fetchCampaignId(owner), expressionChannelId, account.getCountryCode(), account.getTimeZone(), existing);
        Long result = ccgService.createOrUpdate(ccg);

        updateCcgCreativeLinks(result, effectiveLineItem.getCreativeIds());

        return result;
    }

    private static void setCcgPart(LineItem lineItem, CcgLineItemPart ccgPart) {
        if (ccgPart == null) {
            logger.log(Level.WARNING, "Line Item has no CCG, line item id = " + lineItem.getId());
            return;
        }

        lineItem.setName(ccgPart.getName());
        lineItem.setBudget(ccgPart.getBudget());
        lineItem.setAccountId(ccgPart.getAccountId());
        lineItem.setCcgChannelId(ccgPart.getCcgChannelId());
        lineItem.setCcgVersion(ccgPart.getVersion());
        lineItem.setDisplayStatus(ccgPart.getDisplayStatus());
    }

    private static FlightBaseStat rsToStat(ResultSet rs) throws SQLException {
        FlightBaseStat stat = new FlightBaseStat();

        stat.setId(rs.getLong("flight_id"));
        stat.setName(rs.getString("name"));
        stat.setDisplayStatus(CcgDisplayStatus.valueOf(rs.getInt("display_status_id")).getMajorStatus());
        stat.setRequests(rs.getLong("requests"));
        stat.setImpressions(rs.getLong("imps"));
        stat.setClicks(rs.getLong("clicks"));
        stat.setCtr(rs.getBigDecimal("ctr"));
        stat.setEcpm(rs.getBigDecimal("ecpm"));
        stat.setTotalCost(rs.getBigDecimal("total_cost"));

        return stat;
    }

    private List<MajorDisplayStatus> updateLineItemCcgStatus(List<Long> lineItemIds, Status status) {
        Iterable<LineItem> lineItems = lineItemRepository.findAllById(lineItemIds);
        List<Long> ccgIds = new ArrayList<>(lineItemIds.size());
        for (LineItem lineItem : lineItems) {
            ccgIds.add(lineItem.getCcgId());
        }

        List<CampaignCreativeGroup> toUpdate = ccgService.findAll(ccgIds).stream()
                .filter(ccg -> ccg.getStatus() != status)
                .map(ccg -> {
                    ccg.setStatus(status);
                    return ccg;
                })
                .collect(Collectors.toList());

        ccgService.createOrUpdate(toUpdate);

        Array array = jdbcOperations.execute((Connection con) -> con.createArrayOf("integer", ccgIds.toArray()));
        return jdbcOperations.query("select display_status_id from campaigncreativegroup where ccg_id = any(?)",
                new Object[]{array},
                (ResultSet rs, int ind) ->
                        CcgDisplayStatus.valueOf((rs.getInt("display_status_id")))
                                .getMajorStatus()
        );
    }

    @Override
    public Long fetchOwnerId(Long lineItemId) {
        return jdbcOperations.queryForObject("select parent_id from flight where flight_id = ?",
                new Object[]{lineItemId},
                Long.class);
    }

    private interface CcgLinksUpdater {
        void update(CampaignCreativeGroup ccg, List<EntityLink> links);
    }
}

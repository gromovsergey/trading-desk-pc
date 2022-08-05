package app.programmatic.ui.flight.service;

import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.BUDGET;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.DAILY_BUDGET;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.FREQUENCY_CAP;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.SCHEDULES;
import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.SPECIAL_CHANNEL_LINKED;

import com.foros.rs.client.model.advertising.channel.BehavioralChannel;
import app.programmatic.ui.channel.service.ChannelService;
import app.programmatic.ui.common.aspect.prePersistProcessor.annotation.PrePersistAwareMethod;
import app.programmatic.ui.common.aspect.prePersistProcessor.annotation.PrePersistAwareService;
import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.model.VersionEntityBase;
import app.programmatic.ui.common.tool.javabean.JavaBeanAccessor;
import app.programmatic.ui.common.tool.javabean.JavaBeanUtils;
import app.programmatic.ui.flight.dao.FlightScheduleRepository;
import app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty;
import app.programmatic.ui.flight.dao.model.DeliveryPacing;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.FlightBase;
import app.programmatic.ui.flight.dao.model.FlightSchedule;
import app.programmatic.ui.flight.dao.model.FrequencyCap;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.tool.BlackWhiteIds;
import app.programmatic.ui.flight.tool.EffectiveLineItemTool;
import app.programmatic.ui.flight.tool.FlightLineItemsInfo;
import app.programmatic.ui.flight.tool.LineItemBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@PrePersistAwareService(storedValueGetter = "findLineItem")
public class FlightLineItemCouplingServiceImpl implements FlightLineItemCouplingService {
    private static final List<ConfigurableLineItemProperty> EXCLUDED_LI_PROPS =
            Arrays.asList(BUDGET, FREQUENCY_CAP, SCHEDULES, DAILY_BUDGET, SPECIAL_CHANNEL_LINKED);
    private static final List<ConfigurableLineItemProperty> INCLUDED_LI_PROPS =
            Arrays.asList(ConfigurableLineItemProperty.values()).stream()
                      .filter( p -> !EXCLUDED_LI_PROPS.contains(p) )
                      .collect(Collectors.toList());

    private final FlightServiceInternal flightService;
    private final LineItemServiceInternal lineItemService;
    private final ChannelService channelService;
    private final FlightScheduleRepository flightScheduleRepository;

    @Autowired
    public FlightLineItemCouplingServiceImpl(FlightServiceInternal flightService,
                                             LineItemServiceInternal lineItemService,
                                             ChannelService channelService,
                                             FlightScheduleRepository flightScheduleRepository) {
        this.flightService = flightService;
        this.lineItemService = lineItemService;
        this.channelService = channelService;
        this.flightScheduleRepository = flightScheduleRepository;
    }

    @Override
    @Transactional
    @PrePersistAwareMethod
    public LineItem createLineItemAndUnSyncDefault(LineItem lineItem) {
        if (lineItem.getFlightId() == null) {
            // Entity is invalid, so only Validation Service will proceed
            lineItemService.create(lineItem);
            throw new RuntimeException("Invalid entity was created!");
        }

        return lineItemService.runWithFlightLock(
                lineItem.getFlightId(), () -> createLineItemAndUnsyncDefaultImpl(lineItem));
    }

    private LineItem createLineItemAndUnsyncDefaultImpl(LineItem lineItem) {
        FlightLineItemsInfo liInfo = new FlightLineItemsInfo(lineItem.getFlightId(), lineItemService);

        LineItem result = lineItemService.create(lineItem);
        if (result.getSpecialChannelLinked() && result.getSpecialChannelId() == null) {
            result.setSpecialChannelId(channelService.createSpecialChannel(result.getId()));
        }
        if (!liInfo.isDefaultLineItemExist()) {
            return result;
        }

        // Moving flight values to default line item
        Flight owner = flightService.find(lineItem.getFlightId());
        copyValuesFromFlight(owner, liInfo.getDefaultLineItem());

        return result;
    }

    @Override
    @Transactional
    public List<MajorDisplayStatus> deleteLineItemsAndSyncDefault(List<Long> lineItemIds) {
        if (lineItemIds.isEmpty()) {
            return Collections.emptyList();
        }

        Long ownerId = lineItemService.fetchOwnerId(lineItemIds.get(0));
        return lineItemService.runWithFlightLock(ownerId, () -> deleteLineItemsAndSyncDefaultImpl(ownerId, lineItemIds));
    }

    @Override
    @Transactional
    public void linkAdvertisingChannels(Long flightId, List<Long> channelIds, boolean linkSpecialChannelFlag) {
        lineItemService.runWithFlightLock(flightId, () -> linkAdvertisingChannelsImpl(flightId, channelIds, linkSpecialChannelFlag));
    }

    public void linkAdvertisingChannelsImpl(Long flightId, List<Long> channelIds, boolean linkSpecialChannelFlag) {
        FlightLineItemsInfo liInfo = new FlightLineItemsInfo(flightId, lineItemService);
        if (liInfo.isDefaultLineItemExist()) {
            LineItem defaultLineItem = liInfo.getDefaultLineItem();
            lineItemService.linkAdvertisingChannels(
                    defaultLineItem.getId(),
                    defaultLineItem.getChannelIds(),
                    linkSpecialChannelFlag
            );
        }

        flightService.linkAdvertisingChannels(flightId, channelIds, linkSpecialChannelFlag);
    }

    private List<MajorDisplayStatus> deleteLineItemsAndSyncDefaultImpl(Long ownerId, List<Long> lineItemIds) {
        List<MajorDisplayStatus> result = lineItemService.delete(lineItemIds);

        List<Long> actualLineItemIds = lineItemService.fetchLineItemIds(ownerId);
        int actualLineItemIdsSize = actualLineItemIds.size();
        if (actualLineItemIdsSize >= 2) {
            return result;
        }

        Flight owner = flightService.find(ownerId);

        if (actualLineItemIdsSize == 0) {

            // We should create default line item automatically
            LineItem defaultLineItem = lineItemService.createInternal(LineItemBuilder.defaultLineItem(owner));
            result.add(defaultLineItem.getMajorStatus());

            return result;
        }

        // Case FlightLineItemsInfo.isDefaultLineItemExist() == true
        // we should transfer values from the last LI
        LineItem single = lineItemService.find(actualLineItemIds.get(0));
        moveValuesToFlight(single, owner);

        // LI should be updated automatically as we inside transaction and
        // flight's changes will be propagated to owned LI
        flightService.update(owner);

        return result;
    }

    private void moveValuesToFlight(LineItem source, Flight target) {
        JavaBeanAccessor<FlightBase> flightBaseAccessor = JavaBeanUtils.createJavaEntityBeanAccessor(FlightBase.class);
        for (ConfigurableLineItemProperty propConf : INCLUDED_LI_PROPS) {
            flightBaseAccessor.moveNotEmptyValueFromTo(source, target, propConf.getName(), propConf.getPropertyCreator());
        }

        moveDailyBudgetToFlight(source, target);
        moveBudgetToFlight(source, target);
        moveFrequencyCapToFlight(source, target);
        moveSchedulesToFlight(source, target);
        target.setSpecialChannelLinked(source.getSpecialChannelLinked());

        List<String> propertyNames = EnumSet.allOf(ConfigurableLineItemProperty.class).stream()
                .map( e -> e.getName() )
                .collect(Collectors.toList());
        source.setPropertiesSource(EffectiveLineItemTool.buildPropsSource(propertyNames));
    }

    private static void moveDailyBudgetToFlight(LineItem source, Flight target) {
        if (target.getDeliveryPacing() != DeliveryPacing.F) {
            target.setDailyBudget(null);
        } else if (source.getDailyBudget() != null) {
            target.setDailyBudget(source.getDailyBudget());
        }
    }

    private static void moveBudgetToFlight(LineItem source, Flight target) {
        source.setBudget(null);
    }

    private static void moveFrequencyCapToFlight(LineItem source, Flight target) {
        if (source.getFrequencyCap() == null) {
            return;
        }
        if (target.getFrequencyCap() == null) {
            target.setFrequencyCap(source.getFrequencyCap());
        } else {
            target.getFrequencyCap().copyBusinessProperties(source.getFrequencyCap());
        }

        source.setFrequencyCap(null);
    }

    private void moveSchedulesToFlight(LineItem source, Flight target) {
        if (!source.getSchedules().isEmpty()) {
            flightScheduleRepository.deleteAll(target.getSchedules());
            target.setSchedules(source.getSchedules().stream()
                                    .map(s -> cloneFlightSchedule(s, target))
                                    .collect(Collectors.toSet()));
            source.setSchedules(Collections.emptySet());
        }
    }

    private  void copyValuesFromFlight(Flight source, LineItem target) {
        Long targetId = target.getId();
        Long targetSpecialChannelId = target.getSpecialChannelId();

        JavaBeanAccessor<FlightBase> accessor = JavaBeanUtils.createJavaBeanAccessor(FlightBase.class, VersionEntityBase.class);
        accessor.shallowCopyValuesFromTo(source, target);

        // Other entities should be cloned carefully
        target.setId(targetId);
        target.setSpecialChannelId(targetSpecialChannelId);
        target.setFrequencyCap(FrequencyCap.cloneBusinessProperties(source.getFrequencyCap()));
        target.setSchedules(source.getSchedules().stream()
                                .map( s -> cloneFlightSchedule(s, target) )
                                .collect(Collectors.toSet()));
        copyChannelLists(source, target);

        target.setPropertiesSource(Integer.valueOf(0));
    }

    private static FlightSchedule cloneFlightSchedule(FlightSchedule source, FlightBase flightBase) {
        FlightSchedule result = FlightSchedule.cloneBusinessProperties(source);
        result.setFlight(flightBase);
        return result;
    }

    private void copyChannelLists(Flight owner, LineItem lineItem) {
        if (owner.getWhiteListId() == null && owner.getBlackListId() == null) {
            lineItem.setWhiteListId(null);
            lineItem.setBlackListId(null);
            return;
        }

        BlackWhiteIds tmp = lineItemService.createUrlsChannels(owner.getOpportunity().getAccountId(),
                fetchUrlList(owner.getWhiteListId()), fetchUrlList(owner.getBlackListId()));
        lineItem.setWhiteListId(tmp.getWhiteListId());
        lineItem.setBlackListId(tmp.getBlackListId());
    }

    private List<String> fetchUrlList(Long channelId) {
        if (channelId == null) {
            return Collections.emptyList();
        }

        BehavioralChannel channel = channelService.findBehavioralUnchecked(channelId);
        return channel.getUrls().getPositive();
    }

    public LineItem findLineItem(Long id) {
        return lineItemService.find(id);
    }
}

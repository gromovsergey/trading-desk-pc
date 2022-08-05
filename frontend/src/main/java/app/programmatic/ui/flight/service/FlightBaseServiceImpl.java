package app.programmatic.ui.flight.service;

import com.foros.rs.client.model.advertising.channel.BehavioralChannel;
import com.foros.rs.client.model.advertising.channel.BehavioralParameters;
import com.foros.rs.client.model.channel.BehavioralParametersTriggerType;
import com.foros.rs.client.model.channel.TriggersType;
import com.foros.rs.client.model.entity.EntityLink;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.channel.service.ChannelService;
import app.programmatic.ui.common.aspect.forosApiViolation.ForosApiViolationsAware;
import app.programmatic.ui.device.dao.model.RootDevices;
import app.programmatic.ui.device.service.DeviceService;
import app.programmatic.ui.flight.dao.FlightScheduleRepository;
import app.programmatic.ui.flight.dao.model.Flight;
import app.programmatic.ui.flight.dao.model.FlightBase;
import app.programmatic.ui.flight.dao.model.FlightSchedule;
import app.programmatic.ui.flight.tool.BlackWhiteIds;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.validation.ValidationException;


public abstract class FlightBaseServiceImpl {
    private static final ConcurrentHashMap<Long, Boolean> flightProcesses = new ConcurrentHashMap<>();
    private static final ThreadLocal<Boolean> isRecursiveCall = ThreadLocal.withInitial( () -> Boolean.FALSE );

    private static final String WHITE_LIST_PREFIX = "White List";
    private static final String BLACK_LIST_PREFIX = "Black List";

    @Autowired
    protected ChannelService channelService;

    @Autowired
    protected DeviceService deviceService;

    @Autowired
    protected AccountService accountService;

    @Autowired
    protected JdbcOperations jdbcOperations;

    @Autowired
    protected FlightScheduleRepository flightScheduleRepository;

    @Transactional
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.BlackWhiteValidationServiceImpl")
    public BlackWhiteIds createUrlsChannels(Long accountId, List<String> whiteUrls, List<String> blackUrls) {
        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
        return createUrlsChannels(account, whiteUrls, blackUrls);
    }

    @Transactional
    @ForosApiViolationsAware("app.programmatic.ui.flight.validation.BlackWhiteValidationServiceImpl")
    public void updateUrlsChannels(List<String> whiteUrls, Long whiteId, List<String> blackUrls, Long blackId) {
        List<Long> ids = Arrays.asList( whiteId, blackId ).stream()
                .filter( id -> id != null)
                .collect(Collectors.toList());
        if (ids.isEmpty()) {
            return;
        }

        ArrayList<BehavioralChannel> toUpdate = new ArrayList<>(2);

        List<BehavioralChannel> channels = channelService.findAllBehavioral(ids);
        for (BehavioralChannel channel : channels) {
            if (channel.getId().equals(whiteId) && setIfUrlsChanged(channel, whiteUrls)) {
                toUpdate.add(channel);
            }
            else if (channel.getId().equals(blackId) && setIfUrlsChanged(channel, blackUrls)) {
                toUpdate.add(channel);
            }
        }

        channelService.createOrUpdate(toUpdate);
    }

    public void runWithFlightLock(Long flightId, Runnable runnable) {
        runWithFlightLock(flightId,
                new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        runnable.run();
                        return null;
                    }
                });
    }

    public <T> T runWithFlightLock(Long flightId, Callable<T> callable) {
        Boolean isRecursive = isRecursiveCall.get();
        if (!isRecursive && flightProcesses.putIfAbsent(flightId, Boolean.TRUE) != null) {
            throw new ValidationException("Currently this flight is under processing and can not be modified");
        }

        isRecursiveCall.set(Boolean.TRUE);

        try {
            return callable.call();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (!isRecursive) {
                isRecursiveCall.set(Boolean.FALSE);
                flightProcesses.remove(flightId);
            }
        }
    }

    public Long fetchCampaignId(Flight flight) {
        Set<Long> campaignIds = flight.getOpportunity().getCampaignAllocations()
                .stream()
                .map(campaignAllocation -> campaignAllocation.getCampaignId())
                .collect(Collectors.toSet());
        int linkedCampaignsNumber = campaignIds.size();
        if (linkedCampaignsNumber > 1) {
            throw new RuntimeException("Flight must not be linked with multiple campaigns. Current number = " +
                    linkedCampaignsNumber + ". Flight id = " + flight.getId());
        }

        return linkedCampaignsNumber == 0 ? null : campaignIds.iterator().next();
    }

    protected void forFindEager(FlightBase flightBase) {
        flightBase.getDeviceChannelIds().size();
        flightBase.getGeoChannelIds().size();
        flightBase.getExcludedGeoChannelIds().size();
        flightBase.getCreativeIds().size();
        flightBase.getChannelIds().size();
        flightBase.getSiteIds().size();
        flightBase.getConversionIds().size();
        flightBase.getSchedules().size();
    }

    private BlackWhiteIds createUrlsChannels(AdvertisingAccount account, List<String> whiteUrls, List<String> blackUrls) {
        boolean isWhiteResult = true;
        List<BehavioralChannel> channels = new ArrayList(2);
        if (!whiteUrls.isEmpty()) {
            channels.add(defaultBehavioralChannel(account, WHITE_LIST_PREFIX, whiteUrls));
            isWhiteResult = true;
        }
        if (!blackUrls.isEmpty()) {
            channels.add(defaultBehavioralChannel(account, BLACK_LIST_PREFIX, blackUrls));
            isWhiteResult = false;
        }
        if (channels.isEmpty()) {
            return new BlackWhiteIds(null, null);
        }

        List<Long> result = channelService.createOrUpdate(channels);
        if (result.size() == 2) {
            return new BlackWhiteIds(result.get(0), result.get(1));
        }

        return new BlackWhiteIds(isWhiteResult ? result.get(0) : null,
                isWhiteResult ? null : result.get(0));
    }

    protected static Long fetchStandaloneAccountId(AdvertisingAccount account) {
        return account.getAgencyId() != null ? account.getAgencyId() : account.getId();
    }

    public Long findAccountIdByFlightId(Long flightId) {
        return jdbcOperations.queryForObject("select account_id from flight where flight_id = ?",
                new Object[] { flightId },
                Long.class);
    }

    private BehavioralChannel defaultBehavioralChannel(AdvertisingAccount account, String namePrefix, List<String> urls) {
        EntityLink accountLink = new EntityLink();
        accountLink.setId(fetchStandaloneAccountId(account));

        TriggersType urlTriggers = new TriggersType();
        urlTriggers.setPositive(urls);

        BehavioralChannel result = new BehavioralChannel();

        result.setAccount(accountLink);
        result.setName(String.format("%s URLs Channel (auto-generated by UI at %s)", namePrefix, LocalDateTime.now().toString()));
        result.setVisibility("PRI");
        result.setCountry(account.getCountryCode());
        result.setUrls(urlTriggers);
        result.setBehavioralParameters(behavioralParamsByUrls(urls));

        return result;
    }

    private static boolean setIfUrlsChanged(BehavioralChannel channel, List<String> urls) {
        List<String> existingUrls = channel.getUrls().getPositive();
        if (existingUrls.size() != urls.size() || !existingUrls.containsAll(urls)) {
            channel.getUrls().setPositive(urls);
            channel.setBehavioralParameters(behavioralParamsByUrls(urls));
            return true;
        }
        return false;
    }

    private static List<BehavioralParameters> behavioralParamsByUrls(List<String> urls) {
        if (urls.isEmpty()) {
            return Collections.emptyList();
        }

        BehavioralParameters behavioralParameters = new BehavioralParameters();
        behavioralParameters.setMinimumVisits(1l);
        behavioralParameters.setTimeFrom(0l);
        behavioralParameters.setTimeTo(0l);
        behavioralParameters.setTriggerType(BehavioralParametersTriggerType.U);

        return Collections.singletonList(behavioralParameters);
    }

    protected interface FlightBaseLinksGetter {
        List<Long> get(FlightBase flightBase);
    }

    protected interface FlightBaseLinksSetter {
        void set(FlightBase flightBase, List<Long> links);
    }

    protected void setDefaultDevicesIfNotSet(FlightBase flightBase) {
        setDefaultDevicesIfNotSet(flightBase, null);
    }

    protected void setDefaultDevicesIfNotSet(FlightBase flightBase, Long accountId) {
        if (!flightBase.getDeviceChannelIds().isEmpty()) {
            return;
        }

        RootDevices rootDevices = accountId != null ? deviceService.availableAccountRootDevices(accountId) :
                deviceService.availableRootDevices(flightBase.getId());

        if (rootDevices.isAllAvailable()) {
            return;
        }

        flightBase.setDeviceChannelIds(rootDevices.getAvailable().stream()
                                            .map( d -> d.getId() )
                                            .collect(Collectors.toList()));
    }

    protected void setIdsForExistingSchedules(FlightBase flightBase, FlightBase existingBase) {
        HashSet<Long> usedIds = new HashSet<>(existingBase.getSchedules().size());
        for (FlightSchedule schedule : flightBase.getSchedules()) {
            for (FlightSchedule existing : existingBase.getSchedules()) {
                if (existing.getTimeFrom().equals(schedule.getTimeFrom()) &&
                        existing.getTimeTo().equals(schedule.getTimeTo())) {
                    schedule.setId(existing.getId());
                    usedIds.add(existing.getId());
                    break;
                }
            }
        }

        flightScheduleRepository.deleteAll(existingBase.getSchedules().stream()
                .filter( s -> !usedIds.contains(s.getId()) )
                .collect(Collectors.toList())
        );
    }
}

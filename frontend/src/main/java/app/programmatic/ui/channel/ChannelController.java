package app.programmatic.ui.channel;

import com.foros.rs.client.model.advertising.channel.BehavioralChannel;
import com.foros.rs.client.model.advertising.channel.ExpressionChannel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.multipart.MultipartFile;
import app.programmatic.ui.account.dao.model.Account;
import app.programmatic.ui.account.dao.model.AdvertisingAccount;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.channel.dao.model.*;
import app.programmatic.ui.channel.restriction.ChannelRestrictions;
import app.programmatic.ui.channel.service.ChannelService;
import app.programmatic.ui.channel.tool.ChannelBuilder;
import app.programmatic.ui.channel.view.DisplayStatusesView;
import app.programmatic.ui.channel.view.ExpressionChannelView;
import app.programmatic.ui.channel.view.FlightAdvertisingChannelsView;
import app.programmatic.ui.common.error.ForbiddenException;
import app.programmatic.ui.flight.dao.model.LineItem;
import app.programmatic.ui.flight.service.FlightLineItemCouplingService;
import app.programmatic.ui.flight.service.FlightService;
import app.programmatic.ui.flight.service.LineItemService;
import app.programmatic.ui.flight.tool.FlightLineItemsInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.net.URLConnection;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@RestController
public class ChannelController {
    private static final int MAX_CHANNEL_ROWS = 100;
    private final String OWN_CHANNEL_SOURCE;

    private final ChannelService channelService;
    private final ChannelRestrictions channelRestrictions;
    private final AccountService accountService;
    private final FlightService flightService;
    private final FlightLineItemCouplingService flightLineItemCouplingService;
    private final LineItemService lineItemService;

    @Autowired
    public ChannelController(@Value("${backend.ownChannelSource}") String ownChannelSource,
                             ChannelService channelService,
                             ChannelRestrictions channelRestrictions,
                             AccountService accountService,
                             FlightService flightService,
                             FlightLineItemCouplingService flightLineItemCouplingService,
                             LineItemService lineItemService) {
        this.OWN_CHANNEL_SOURCE = ownChannelSource;
        this.channelService = channelService;
        this.channelRestrictions = channelRestrictions;
        this.accountService = accountService;
        this.flightService = flightService;
        this.flightLineItemCouplingService = flightLineItemCouplingService;
        this.lineItemService = lineItemService;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel/behavioral", produces = "application/json")
    public BehavioralChannel findBehavioralChannel(@RequestParam(value = "channelId") Long channnelId) {
        BehavioralChannel channel = channelService.findBehavioralUnchecked(channnelId);
        // Don't want to put restriction into Service, because currently OLD UI REST API is used for fetching
        // ToDo: put restriction to the right place
        if (!channelRestrictions.canViewContent(channel)) {
            throw new ForbiddenException();
        }

        return channel;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel/expression", produces = "application/json")
    public ExpressionChannelView findExpressionChannel(@RequestParam(value = "channelId") Long channnelId) {
        ExpressionChannel channel = channelService.findExpressionUnchecked(channnelId);
        // Don't want to put restriction into Service, because currently OLD UI REST API is used for fetching
        // ToDo: put restriction to the right place
        if (!channelRestrictions.canViewContent(channel)) {
            throw new ForbiddenException();
        }

        ExpressionChannelView channelView = ChannelBuilder.buildExpressionChannelView(channel);

        List<Long> channelIds = new ArrayList<>();
        Stream.concat(channelView.getIncludedChannels().stream(), channelView.getExcludedChannels().stream())
                .forEach(list -> list.stream()
                        .forEach(item -> channelIds.add(item.getId())));

        Map<Long, Channel> channelsMap = channelService.findByIdsWithUniqUsersInfoUnrestricted(channelIds).stream()
                .collect(Collectors.toMap(Channel::getId, Function.identity()));

        channelView.setIncludedChannels(fillChannelsByIds(channelView.getIncludedChannels(), channelsMap));
        channelView.setExcludedChannels(fillChannelsByIds(channelView.getExcludedChannels(), channelsMap));

        return channelView;
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel/behavioral/stat", produces = "application/json")
    public BehavioralChannelStat fetchBehavioralChannelStat(@RequestParam(value = "channelId") Long channnelId) {
        return channelService.fetchBehavioralStat(channnelId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel/expression/stat", produces = "application/json")
    public ExpressionChannelStat fetchExpressionChannelStat(@RequestParam(value = "channelId") Long channnelId) {
        return channelService.fetchExpressionStat(channnelId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel", produces = "application/json")
    public Collection<Channel> findChannels(@RequestParam(value = "accountId") Long accountId,
                                            @RequestParam(value = "name", required = false) String name) {
        if (name == null || name.isEmpty()) {
            return channelService.findByAccountId(accountId);
        }
        AdvertisingAccount account = accountService.findAdvertisingUnchecked(accountId);
        return channelService.searchByName(account.getCountryCode(), accountId, name);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel/expression/list", produces = "application/json")
    public Collection<Channel> findExpressionChannels(@RequestParam(value = "accountId") Long accountId,
                                                      @RequestParam(value = "countryCode") String countryCode,
                                                      @RequestParam(value = "name") String name) {
        return channelService.searchByName(countryCode, accountId, name);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel/external/list", produces = "application/json")
    public List<Channel> findChannelsByIdsForExternal(@RequestParam(value = "channelIds") List<Long> channelIds,
                                           @RequestParam(value = "extAccountId") Long extAccountId) {
        if (channelIds.size() > MAX_CHANNEL_ROWS) {
            throw new HttpMessageNotReadableException("Size of channel ids list must be less or equal " + MAX_CHANNEL_ROWS);
        }

        return channelService.findByIdsForExternal(extAccountId, channelIds);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel/search", produces = "application/json")
    public ChannelList findAllChannels(@RequestParam(value = "name", required = false) String name,
                                               @RequestParam(value = "accountId", required = false) Long accountId,
                                               @RequestParam(value = "type", required = false) ChannelType type,
                                               @RequestParam(value = "visibility", required = false) ChannelVisibility visibility) {
        List<Channel> channels = channelService.findAllChannels(name, accountId, type, visibility);
        if (channels.size() > MAX_CHANNEL_ROWS) {
            return new ChannelList(channels.subList(0, MAX_CHANNEL_ROWS), true);
        }
        return new ChannelList(channels, false);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel/stat", produces = "application/json")
    public Collection<? extends Channel> getStatsByLineItemId(@RequestParam(value = "lineItemId", required = false) Long lineItemId,
                                                              @RequestParam(value = "flightId", required = false) Long flightId)
                                                throws MissingServletRequestParameterException {
        if (lineItemId == null && flightId == null) {
            throw new MissingServletRequestParameterException("flightId or lineItemId", "long");
        }

        if (lineItemId == null) {
            FlightLineItemsInfo liInfo = new FlightLineItemsInfo(flightId, lineItemService);
            if (!liInfo.isDefaultLineItemExist()) {
                return channelService.getStatsByFlightId(flightId);
            }
            lineItemId = liInfo.getDefaultLineItemId();
        }

        return channelService.getStatsByLineItemId(lineItemId);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/channel/searchNames", produces = "application/json")
    public Collection<ChannelNameId> searchExistence(@RequestBody Set<ChannelName> names) {
        return channelService.searchByNames(names);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/channel/operation", produces = "application/json")
    public DisplayStatusesView channelOperation(@RequestParam(value = "name") ChannelOperation operation,
                                               @RequestParam(value = "channelId") Long channelId,
                                               @RequestParam(value = "flightId", required = false) Long flightId,
                                               @RequestParam(value = "lineItemId", required = false) Long lineItemId) {
        DisplayStatusesView result = new DisplayStatusesView();

        result.setChannelDisplayStatus(channelService.changeStatus(channelId, operation));

        if (lineItemId != null) {
            LineItem lineItem = lineItemService.find(lineItemId);
            flightId = lineItem.getFlightId();
            result.setLineItemDisplayStatus(lineItem.getMajorStatus());
        }

        if (flightId != null) {
            result.setFlightDisplayStatus(flightService.find(flightId).getMajorStatus());
        }

        return result;
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/channel/behavioral", produces = "application/json")
    public Long createBehavioralChannel(@RequestBody BehavioralChannel behavioralChannel) {
        return channelService.createOrUpdate(behavioralChannel);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/channel/behavioral", produces = "application/json")
    public Long updateBehavioralChannel(@RequestBody BehavioralChannel behavioralChannel) {
        return channelService.createOrUpdate(behavioralChannel);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/channel/expression", produces = "application/json")
    public Long createExpressionChannel(@RequestBody ExpressionChannelView expressionChannel) {
        return channelService.createOrUpdate(ChannelBuilder.buildExpressionChannel(expressionChannel));
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/channel/expression", produces = "application/json")
    public Long updateExpressionChannel(@RequestBody ExpressionChannelView expressionChannel) {
        return channelService.createOrUpdate(ChannelBuilder.buildExpressionChannel(expressionChannel));
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/flight/linkAdvertisingChannels", produces = "application/json")
    public void linkAdvertisingChannelsToFlight(@RequestParam(value = "flightId") Long flightId,
                                                               @RequestBody FlightAdvertisingChannelsView flightChannelsView) {
        flightLineItemCouplingService.linkAdvertisingChannels(flightId, flightChannelsView.getChannelIds(), flightChannelsView.getLinkSpecialChannelFlag());
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/rest/lineItem/linkAdvertisingChannels", produces = "application/json")
    public void linkAdvertisingChannelsToLineItem(@RequestParam(value = "lineItemId") Long lineItemId,
                                                                 @RequestBody FlightAdvertisingChannelsView flightChannelsView) {
        lineItemService.linkAdvertisingChannels(lineItemId, flightChannelsView.getChannelIds(), flightChannelsView.getLinkSpecialChannelFlag());
    }

    private static List<List<Channel>> fillChannelsByIds(List<List<Channel>> ids, Map<Long, Channel> channelsMap) {
        return ids.stream()
                .map(list -> list.stream()
                        .map(item -> channelsMap.get(item.getId()))
                        .collect(Collectors.toList())
                )
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel/account", produces = "application/json")
    public Account findAccount(@RequestParam(value = "accountId") Long accountId) {
        return accountService.findAccountUnchecked(accountId);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel/node/list", produces = "application/json")
    public Collection<ChannelNode> getChannelChildrenList(@RequestParam(value = "parentId") Long parentId,
                                                          @RequestParam(value = "language") String language) {
        return channelService.getChannelNodeList(parentId, language);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel/rubric/node/list", produces = "application/json")
    public Collection<ChannelNode> getChannelRubricNodeList(@RequestParam(value = "accountId") Long accountId,
                                                            @RequestParam(value = "source") String source,
                                                            @RequestParam(value = "country") String countryCode,
                                                            @RequestParam(value = "language") String language) {
        if (OWN_CHANNEL_SOURCE.equals(source)) {
            return channelService.findByAccountId(accountId).stream()
                    .map( c -> new ChannelNode(c.getId(), c.getName(), c.getName(), Boolean.FALSE) )
                    .collect(Collectors.toList());
        }

        return channelService.getChannelRubricNodeList(source, countryCode, language);
    }

    @RequestMapping(method = RequestMethod.POST, path = "/rest/channel/uploadChannelReport")
    public void uploadChannelReport(@RequestParam("file") MultipartFile file,
                                    @RequestParam(value = "accountId", required = false) Long accountId) {
        if (accountId == null) {
            channelService.uploadChannelReport(file);
        } else {
            channelService.uploadChannelReport(file, accountId);
        }
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel/downloadChannelReport")
    public ResponseEntity downloadChannelReport(@RequestParam(value = "name") String name,
                                                @RequestParam(value = "accountId", required = false) Long accountId) {
        byte[] contents;
        if (accountId == null) {
            contents = channelService.downloadChannelReport(name);
        } else {
            contents = channelService.downloadChannelReport(name, accountId);
        }

        HttpHeaders headers = new HttpHeaders();
        String mimeType = URLConnection.guessContentTypeFromName(name);
        if (mimeType != null) {
            headers.setContentType(MediaType.valueOf(mimeType));
        }
        headers.setContentLength(contents.length);

        return new ResponseEntity<>(
                contents,
                headers,
                HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/rest/channel/channelReportList", produces = "application/json")
    public List<String> channelReportList(@RequestParam(value = "accountId", required = false) Long accountId) {
        if (accountId == null) {
            return channelService.channelReportList();
        } else {
            return channelService.channelReportList(accountId);
        }
    }
}

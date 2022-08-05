package app.programmatic.ui.flight;

import static app.programmatic.ui.flight.tool.FlightPrePersistHelper.PATH_ALIASES;

import app.programmatic.ui.ccg.dao.model.CcgDisplayStatus;
import app.programmatic.ui.flight.dao.model.*;
import app.programmatic.ui.flight.service.FlightLineItemCouplingService;
import app.programmatic.ui.flight.service.LineItemService;
import app.programmatic.ui.flight.view.FlightLineItemsView;
import app.programmatic.ui.flight.view.FlightView;
import app.programmatic.ui.flight.view.LineItemView;
import com.foros.rs.client.model.advertising.channel.BehavioralChannel;
import app.programmatic.ui.account.service.AccountService;
import app.programmatic.ui.channel.service.ChannelService;
import app.programmatic.ui.common.validation.pathalias.ValidationPathAliasesServiceConfigurator;
import app.programmatic.ui.flight.service.FlightChannelService;
import app.programmatic.ui.flight.tool.BlackWhiteIds;
import app.programmatic.ui.flight.view.FlightBaseView;
import app.programmatic.ui.restriction.model.Restriction;
import app.programmatic.ui.restriction.service.RestrictionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public abstract class FlightBaseController {

    @Autowired
    private LineItemService lineItemService;

    @Autowired
    private FlightLineItemCouplingService fliService;

    @Autowired
    protected ValidationPathAliasesServiceConfigurator validationPathAliasesServiceConfigurator;

    @Autowired
    protected ChannelService channelService;

    @Autowired
    protected AccountService accountService;

    @Autowired
    protected RestrictionService restrictionService;


    protected abstract FlightChannelService getFlightChannelService();

    protected abstract <T extends FlightBaseView> Long fetchAccountId(T flightBaseView);

    protected <T> T withValidationAliases(Callable<T> callable) {
        validationPathAliasesServiceConfigurator.configure(PATH_ALIASES);
        try {
            return callable.call();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            validationPathAliasesServiceConfigurator.clear();
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

    private BlackWhiteIds updateChannelLists(FlightBaseView flightBaseView) {
        Long accountId = fetchAccountId(flightBaseView);
        // Let's temporary use this restriction
        restrictionService.throwIfNotPermitted(Restriction.CREATE_CAMPAIGN, accountId);

        Long whiteId = flightBaseView.getWhiteListId();
        Long blackId = flightBaseView.getBlackListId();

        boolean createWhiteList = flightBaseView.getWhiteListId() == null && !flightBaseView.getWhiteListAsList().isEmpty();
        boolean createBlackList = flightBaseView.getBlackListId() == null && !flightBaseView.getBlackListAsList().isEmpty();

        List<String> whiteListToCreate = createWhiteList ? flightBaseView.getWhiteListAsList() : Collections.emptyList();
        List<String> blackListToCreate = createBlackList ? flightBaseView.getBlackListAsList() : Collections.emptyList();
        if (createWhiteList || createBlackList) {
            BlackWhiteIds tmp = getFlightChannelService().createUrlsChannels(accountId, whiteListToCreate, blackListToCreate);
            whiteId = createWhiteList ? tmp.getWhiteListId() : whiteId;
            blackId = createBlackList ? tmp.getBlackListId() : blackId;
        }

        // To avoid logic over-complication lets update even after create
        getFlightChannelService().updateUrlsChannels(flightBaseView.getWhiteListAsList(), whiteId,
                flightBaseView.getBlackListAsList(), blackId);
        return new BlackWhiteIds(whiteId, blackId);
    }

    protected void setUrlsChannels(FlightBase flightBase, FlightBaseView flightBaseView) {
        BlackWhiteIds blackWhiteIds = updateChannelLists(flightBaseView);
        flightBase.setWhiteListId(blackWhiteIds.getWhiteListId());
        flightBase.setBlackListId(blackWhiteIds.getBlackListId());
    }


    public FlightLineItemsView makeCopyLineItems(Long lineItemId, Long flightId, Flight toFlight, FlightView toFlightView)
            throws MissingServletRequestParameterException {

        if (lineItemId != null && (flightId != null && toFlight != null) ||
                lineItemId == null && (flightId == null && toFlight == null)) {
            throw new MissingServletRequestParameterException("lineItemId|flightId|toFlightId", "Long");
        }

        Flight flight;
        FlightView flightView;
        FlightLineItems flightLineItems;

        if (lineItemId != null) {
            flightLineItems = lineItemService.findEffectiveEager(lineItemId);
            flight = flightLineItems.getFlight();
            flightView = new FlightView(flight, getWhiteList(flight), getBlackList(flight));
        } else {
            flightLineItems = lineItemService.findEffectiveEagerByFlightId(flightId);
            flight = toFlight;
            flightView = toFlightView;
        }

        List<LineItem> lineItemList = flightLineItems.getLineItems();
        Collections.reverse(lineItemList);

        return new FlightLineItemsView(
                flightView,
                lineItemList.stream()
                        .sorted(Comparator.comparing(LineItem::getId))
                        .map(lineItem -> {
                            lineItem.setFlightId(flight.getId());
                            lineItem.setId(null);
                            lineItem.setCcgId(null);
                            lineItem.setSpecialChannelId(null);

                            if (lineItemId != null) {
                                lineItem.setName(getNewFlightOrLineItemName(lineItem.getName()));
                            }

                            lineItem.setAccountId(flight.getAccountId());
                            lineItem.setDisplayStatus(CcgDisplayStatus.INACTIVE);
                            FrequencyCap frequencyCap = lineItem.getFrequencyCap();
                            if (frequencyCap != null) {
                                frequencyCap.setId(null);
                                lineItem.setFrequencyCap(frequencyCap);
                            }
                            LineItem lineItemUpdated = withValidationAliases(() -> fliService.createLineItemAndUnSyncDefault(lineItem));
                            List<String> whiteList = getWhiteList(lineItemUpdated);
                            List<String> blackList = getBlackList(lineItemUpdated);
                            return new LineItemView(flight, lineItemUpdated, whiteList, blackList,
                                    getViewResetAwareProps(flightView.getWhiteListAsList(), flightView.getBlackListAsList(),
                                            whiteList, blackList));
                        })
                        .collect(Collectors.toList()));

    }

    protected String getNewFlightOrLineItemName(String oldName) {
        String patternString = "[0-9]{4}-[0-9]{2}-[0-9]{2}(_|T)[0-9]{2}.*$";
        String dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss_SS"));
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(oldName);
        if (matcher.find()) {
            return oldName.replaceAll(patternString, dateNow) + (int) (Math.random() * 100);
        } else {
            return oldName + " " + dateNow + (int) (Math.random() * 100);
        }
    }


    protected List<String> getViewResetAwareProps(List<String> flightWhiteList, List<String> flightBlackList,
                                                  List<String> liWhiteList, List<String> liBlackList) {
        ArrayList<String> result = new ArrayList<>(2);

        if (flightWhiteList.size() != liWhiteList.size() || !flightWhiteList.containsAll(liWhiteList)) {
            result.add("whiteList");
        }

        if (flightBlackList.size() != liBlackList.size() || !flightBlackList.containsAll(liBlackList)) {
            result.add("blackList");
        }

        return result;
    }
}

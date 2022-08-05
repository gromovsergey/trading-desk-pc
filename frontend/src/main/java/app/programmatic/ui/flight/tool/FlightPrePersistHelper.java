package app.programmatic.ui.flight.tool;

import static app.programmatic.ui.common.config.ApplicationConstants.LOCALE_RU;

import app.programmatic.ui.campaign.dao.model.CampaignDisplayStatus;
import app.programmatic.ui.ccg.dao.model.CcgDisplayStatus;
import app.programmatic.ui.channel.service.ChannelService;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.common.validation.pathalias.ValidationPathAlias;
import app.programmatic.ui.flight.dao.model.*;
import app.programmatic.ui.flight.view.FlightBaseView;
import app.programmatic.ui.geo.dao.model.Address;
import app.programmatic.ui.geo.dao.model.AddressTO;
import app.programmatic.ui.geo.service.GeoService;
import app.programmatic.ui.site.service.SiteService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FlightPrePersistHelper {
    public static List<ValidationPathAlias> PATH_ALIASES = initPathAliases();

    public static void setFlightDefaults(Flight flight) {
        flight.setDisplayStatus(CampaignDisplayStatus.INACTIVE);
    }

    public static void setLineItemDefaults(LineItem lineItem) {
        lineItem.setDisplayStatus(CcgDisplayStatus.INACTIVE);
    }

    public static void prePersistFlight(Flight flight) {
        prePersist((FlightBase)flight);
    }

    public static void prePersistLineItem(LineItem lineItem,
                                          ChannelService channelService,
                                          SiteService siteService) {
        prePersist((FlightBase)lineItem);
        prePersistAdvertisingChannels(lineItem, channelService);
        prePersistSites(lineItem, siteService);
    }

    public static void prePersistAddresses(FlightBase flightBase, FlightBaseView flightBaseView, GeoService geoService) {
        flightBase.getGeoChannelIds().addAll(getGeoChannelIdList(flightBaseView.getAddresses(), flightBase.getGeoChannelIds(), geoService));
        flightBase.getExcludedGeoChannelIds().addAll(getGeoChannelIdList(flightBaseView.getExcludedAddresses(), flightBase.getExcludedGeoChannelIds(), geoService));
    }

    private static List<Long> getGeoChannelIdList(List<AddressTO> addresses, List<Long> channelIds, GeoService geoService) {
        List<Long> addressChannelIds = new ArrayList<>(addresses.size());
        for (AddressTO addressView : addresses) {
            if (addressView.getId() != null) {
                continue;
            }

            Long existingChannelId = geoService.findAddressChannel(addressView, LOCALE_RU.getCountry());
            if (existingChannelId != null) {
                if (!channelIds.contains(existingChannelId)) {
                    addressChannelIds.add(existingChannelId);
                }
                continue;
            }

            Address address = addressView.buildAddress();
            address.setName("ADDRESS " + System.currentTimeMillis());
            address.setCountryCode(LOCALE_RU.getCountry());
            address.setParentChannelId(geoService.getCountryChannelId(LOCALE_RU.getCountry()));
            addressChannelIds.add(geoService.createAddressChannel(address));
        }
        return addressChannelIds;
    }

    private static void prePersistAdvertisingChannels(LineItem lineItem, ChannelService channelService) {
        lineItem.setChannelIds(channelService.filterActive(lineItem.getChannelIds()));
    }

    private static void prePersistSites(LineItem lineItem, SiteService siteService) {
        lineItem.setSiteIds(siteService.filterActive(lineItem.getSiteIds()));
    }

    private static void prePersist(FlightBase flightBase) {
        if (flightBase.getDeliveryPacing() != DeliveryPacing.F) {
            flightBase.setDailyBudget(null);
        }

        if (flightBase.getImpressionsPacing() != TargetingPacing.F) {
            flightBase.setImpressionsDailyLimit(null);
        }

        if (flightBase.getImpressionsTotalLimit() != null
                && flightBase.getImpressionsTotalLimit().compareTo(BigDecimal.ZERO) <= 0) {
            flightBase.setImpressionsTotalLimit(null);
        }

        if (flightBase.getClicksPacing() != TargetingPacing.F) {
            flightBase.setClicksDailyLimit(null);
        }

        if (flightBase.getClicksTotalLimit() != null
                && flightBase.getClicksTotalLimit().compareTo(BigDecimal.ZERO) <= 0) {
            flightBase.setClicksTotalLimit(null);
        }

    }

    private static List<ValidationPathAlias> initPathAliases() {
        return Arrays.asList(
                new ValidationPathAlias[]{
                        new ValidationPathAlias("opportunity.amount", "budget"),
                        new ValidationPathAlias("opportunity.name", "name"),
                        new ValidationPathAlias("accountId", ConstraintViolationBuilder.GENERAL_ERROR_FIELD_NAME)
                });
    }
}

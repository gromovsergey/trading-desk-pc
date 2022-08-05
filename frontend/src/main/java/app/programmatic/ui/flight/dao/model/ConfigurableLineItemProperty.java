package app.programmatic.ui.flight.dao.model;

import static app.programmatic.ui.common.tool.javabean.JavaBeanAccessor.TypeCreator.DEFAULT_PROPERTY_CREATOR;
import static app.programmatic.ui.common.tool.javabean.JavaBeanAccessor.TypeCreator.LIST_PROPERTY_CREATOR;

import app.programmatic.ui.common.tool.javabean.JavaBeanAccessor;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public enum ConfigurableLineItemProperty {
    DATE_START("dateStart", DEFAULT_PROPERTY_CREATOR),
    DATE_END("dateEnd", DEFAULT_PROPERTY_CREATOR),
    DELIVERY_PACING("deliveryPacing", DEFAULT_PROPERTY_CREATOR),
    DAILY_BUDGET("dailyBudget", DEFAULT_PROPERTY_CREATOR),
    RATE_TYPE("rateType", DEFAULT_PROPERTY_CREATOR),
    RATE_VALUE("rateValue", DEFAULT_PROPERTY_CREATOR),
    MIN_CTR_GOAL("minCtrGoal", DEFAULT_PROPERTY_CREATOR),
    FREQUENCY_CAP("frequencyCap", fq -> FrequencyCap.cloneBusinessProperties((FrequencyCap)fq)),
    CHANNEL_IDS("channelIds", LIST_PROPERTY_CREATOR),
    GEO_CHANNEL_IDS("geoChannelIds", LIST_PROPERTY_CREATOR),
    EXCLUDED_GEO_CHANNEL_IDS("excludedGeoChannelIds", LIST_PROPERTY_CREATOR),
    DEVICE_CHANNEL_IDS("deviceChannelIds", LIST_PROPERTY_CREATOR),
    CREATIVE_IDS("creativeIds", LIST_PROPERTY_CREATOR),
    SITE_IDS("siteIds", LIST_PROPERTY_CREATOR),
    CONVERSION_IDS("conversionIds", LIST_PROPERTY_CREATOR),
    SCHEDULES("schedules", ss -> { if (ss == null) return Collections.emptySet();
                                  return ((Set)ss).stream().map( s -> FlightSchedule.cloneBusinessProperties((FlightSchedule)s) )
                                                           .collect(Collectors.toSet());}),
    WHITE_LIST_ID("whiteListId", DEFAULT_PROPERTY_CREATOR),
    BLACK_LIST_ID("blackListId", DEFAULT_PROPERTY_CREATOR),
    BUDGET("budget", DEFAULT_PROPERTY_CREATOR),
    SPECIAL_CHANNEL_LINKED("specialChannelLinked", DEFAULT_PROPERTY_CREATOR),
    IMPRESSIONS_PACING("impressionsPacing", DEFAULT_PROPERTY_CREATOR),
    CLICKS_PACING("clicksPacing", DEFAULT_PROPERTY_CREATOR),
    IMPRESSIONS_DAILY_LIMIT("impressionsDailyLimit", DEFAULT_PROPERTY_CREATOR),
    IMPRESSIONS_TOTAL_LIMIT("impressionsTotalLimit", DEFAULT_PROPERTY_CREATOR),
    CLICKS_DAILY_LIMIT("clicksDailyLimit", DEFAULT_PROPERTY_CREATOR),
    CLICKS_TOTAL_LIMIT("clicksTotalLimit", DEFAULT_PROPERTY_CREATOR)
    ;

    private final String name;
    private final JavaBeanAccessor.TypeCreator propertyCreator;

    ConfigurableLineItemProperty(String name, JavaBeanAccessor.TypeCreator propertyCreator) {
        this.name = name;
        this.propertyCreator = propertyCreator;
    }

    public String getName() {
        return name;
    }

    public JavaBeanAccessor.TypeCreator getPropertyCreator() {
        return propertyCreator;
    }
}

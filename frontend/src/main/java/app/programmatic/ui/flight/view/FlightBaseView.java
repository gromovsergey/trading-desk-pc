package app.programmatic.ui.flight.view;

import static app.programmatic.ui.flight.dao.model.ConfigurableLineItemProperty.FREQUENCY_CAP;

import app.programmatic.ui.common.model.MajorDisplayStatus;
import app.programmatic.ui.common.tool.converter.XmlDateTimeConverter;
import app.programmatic.ui.common.tool.serialization.JsonDateDeserializer;
import app.programmatic.ui.common.tool.serialization.JsonDateSerializer;
import app.programmatic.ui.common.validation.ConstraintViolationBuilder;
import app.programmatic.ui.flight.dao.model.*;
import app.programmatic.ui.flight.tool.FlightViewHelper;
import app.programmatic.ui.geo.dao.model.AddressTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class FlightBaseView {
    private Long id;
    private LocalDate dateStart;
    private LocalDate dateEnd;
    private MajorDisplayStatus displayStatus;
    private BidStrategy bidStrategy;
    private BigDecimal minCtrGoal;
    private String rateType;
    private BigDecimal rateValue;
    private DeliveryPacing deliveryPacing;
    private BigDecimal dailyBudget;
    private FrequencyCapView frequencyCap;
    private Long version;
    private String whiteList;
    private String blackList;
    private Long whiteListId;
    private Long blackListId;
    private List<Long> channelIds;
    private List<Long> geoChannelIds;
    private List<Long> excludedGeoChannelIds;
    private List<AddressTO> addresses;
    private List<AddressTO> excludedAddresses;
    private List<Long> deviceChannelIds;
    private List<Long> creativeIds;
    private List<Long> siteIds;
    private List<Long> conversionIds;
    private List<String> schedules;
    private Boolean specialChannelLinked;
    private TargetingPacing impressionsPacing;
    private BigDecimal impressionsDailyLimit;
    private BigDecimal impressionsTotalLimit;
    private TargetingPacing clicksPacing;
    private BigDecimal clicksDailyLimit;
    private BigDecimal clicksTotalLimit;
    private List<String> emptyProps;

    public FlightBaseView() {
    }

    public FlightBaseView(FlightBase flightBase, List<String> whiteList, List<String> blackList) {
        this.id = flightBase.getId();
        this.dateStart = flightBase.getDateStart();
        this.dateEnd = flightBase.getDateEnd();
        this.displayStatus = flightBase.getMajorStatus();
        this.bidStrategy = flightBase.getBidStrategy();
        this.minCtrGoal = flightBase.getMinCtrGoal();
        this.rateType = FlightViewHelper.fromRateType(flightBase.getRateType());
        this.rateValue = flightBase.getRateValue();
        this.deliveryPacing = flightBase.getDeliveryPacing();
        this.dailyBudget = flightBase.getDailyBudget();
        this.impressionsPacing = flightBase.getImpressionsPacing();
        this.impressionsDailyLimit = flightBase.getImpressionsDailyLimit();
        this.impressionsTotalLimit = flightBase.getImpressionsTotalLimit();
        this.clicksPacing = flightBase.getClicksPacing();
        this.clicksDailyLimit = flightBase.getClicksDailyLimit();
        this.clicksTotalLimit = flightBase.getClicksTotalLimit();
        this.frequencyCap = flightBase.getFrequencyCap() != null ? new FrequencyCapView(flightBase.getFrequencyCap()) : null;
        this.version = XmlDateTimeConverter.convertToEpochTime(flightBase.getVersion());
        this.whiteList = whiteList.stream().collect(Collectors.joining("\n"));
        this.blackList = blackList.stream().collect(Collectors.joining("\n"));
        this.whiteListId = flightBase.getWhiteListId();
        this.blackListId = flightBase.getBlackListId();
        this.channelIds = flightBase.getChannelIds();
        this.geoChannelIds = flightBase.getGeoChannelIds();
        this.excludedGeoChannelIds = flightBase.getExcludedGeoChannelIds();
        this.deviceChannelIds = flightBase.getDeviceChannelIds();
        this.creativeIds = flightBase.getCreativeIds();
        this.siteIds = flightBase.getSiteIds();
        this.conversionIds = flightBase.getConversionIds();
        this.schedules = flightBase.getSchedules().stream()
                .map( schedule -> schedule.getTimeFrom() + ":" + schedule.getTimeTo() )
                .collect(Collectors.toList());
        this.specialChannelLinked = flightBase.getSpecialChannelLinked();
    }

    protected void buildFlightBase(FlightBase target) {
        target.setId(id);
        target.setDateStart(dateStart);
        target.setDateEnd(dateEnd);
        target.setBidStrategy(bidStrategy);
        target.setMinCtrGoal(minCtrGoal);
        target.setRateType(FlightViewHelper.toRateType(rateType));
        target.setRateValue(rateValue);
        target.setDeliveryPacing(deliveryPacing);
        target.setDailyBudget(dailyBudget);
        target.setImpressionsPacing(impressionsPacing);
        target.setImpressionsDailyLimit(impressionsDailyLimit);
        target.setImpressionsTotalLimit(impressionsTotalLimit);
        target.setClicksPacing(clicksPacing);
        target.setClicksDailyLimit(clicksDailyLimit);
        target.setClicksTotalLimit(clicksTotalLimit);
        target.setFrequencyCap(buildFrequencyCap(frequencyCap));
        target.setVersion(XmlDateTimeConverter.convertEpochToTimestamp(version));
        target.setWhiteListId(whiteListId);
        target.setBlackListId(blackListId);
        target.setChannelIds(getChannelIds());
        target.setGeoChannelIds(getGeoChannelIds());
        target.setExcludedGeoChannelIds(getExcludedGeoChannelIds());
        target.setDeviceChannelIds(getDeviceChannelIds());
        target.setCreativeIds(getCreativeIds());
        target.setSiteIds(getSiteIds());
        target.setConversionIds(getConversionIds());
        target.setSchedules(getSchedules().stream()
                .map(schedule -> buildFlightSchedule(schedule, target))
                .collect(Collectors.toSet()));
        target.setSpecialChannelLinked(isSpecialChannelLinked());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    public LocalDate getDateStart() {
        return dateStart;
    }

    @JsonDeserialize(using = JsonDateDeserializer.class)
    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    @JsonSerialize(using = JsonDateSerializer.class)
    public LocalDate getDateEnd() {
        return dateEnd;
    }

    @JsonDeserialize(using = JsonDateDeserializer.class)
    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }

    public MajorDisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(MajorDisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    public BidStrategy getBidStrategy() {
        return bidStrategy;
    }

    public void setBidStrategy(BidStrategy bidStrategy) {
        this.bidStrategy = bidStrategy;
    }

    public BigDecimal getMinCtrGoal() {
        return minCtrGoal;
    }

    public void setMinCtrGoal(BigDecimal minCtrGoal) {
        this.minCtrGoal = minCtrGoal;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public BigDecimal getRateValue() {
        return rateValue;
    }

    public void setRateValue(BigDecimal rateValue) {
        this.rateValue = rateValue;
    }

    public DeliveryPacing getDeliveryPacing() {
        return deliveryPacing;
    }

    public void setDeliveryPacing(DeliveryPacing deliveryPacing) {
        this.deliveryPacing = deliveryPacing;
    }

    public BigDecimal getDailyBudget() {
        return dailyBudget;
    }

    public void setDailyBudget(BigDecimal dailyBudget) {
        this.dailyBudget = dailyBudget;
    }

    public FrequencyCapView getFrequencyCap() {
        return frequencyCap;
    }

    public void setFrequencyCap(FrequencyCapView frequencyCap) {
        this.frequencyCap = frequencyCap;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @JsonIgnore
    public List<String> getWhiteListAsList() {
        return split(whiteList);
    }

    public String getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(String whiteList) {
        this.whiteList = whiteList;
    }

    @JsonIgnore
    public List<String> getBlackListAsList() {
        return split(blackList);
    }

    public String getBlackList() {
        return blackList;
    }

    public void setBlackList(String blackList) {
        this.blackList = blackList;
    }

    public Long getWhiteListId() {
        return whiteListId;
    }

    public void setWhiteListId(Long whiteListId) {
        this.whiteListId = whiteListId;
    }

    public Long getBlackListId() {
        return blackListId;
    }

    public void setBlackListId(Long blackListId) {
        this.blackListId = blackListId;
    }

    public List<Long> getChannelIds() {
        return nullToEmpty(channelIds);
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public List<Long> getGeoChannelIds() {
        return nullToEmpty(geoChannelIds);
    }

    public void setGeoChannelIds(List<Long> geoChannelIds) {
        this.geoChannelIds = geoChannelIds;
    }

    public List<Long> getExcludedGeoChannelIds() {
        return excludedGeoChannelIds;
    }

    public void setExcludedGeoChannelIds(List<Long> excludedGeoChannelIds) {
        this.excludedGeoChannelIds = excludedGeoChannelIds;
    }

    public List<AddressTO> getAddresses() {
        return nullToEmpty(addresses);
    }

    public void setAddresses(List<AddressTO> addresses) {
        this.addresses = addresses;
    }

    public List<AddressTO> getExcludedAddresses() {
        return nullToEmpty(excludedAddresses);
    }

    public void setExcludedAddresses(List<AddressTO> excludedAddresses) {
        this.excludedAddresses = excludedAddresses;
    }

    public List<Long> getDeviceChannelIds() {
        return nullToEmpty(deviceChannelIds);
    }

    public void setDeviceChannelIds(List<Long> deviceChannelIds) {
        this.deviceChannelIds = deviceChannelIds;
    }

    public List<Long> getCreativeIds() {
        return nullToEmpty(creativeIds);
    }

    public void setCreativeIds(List<Long> creativeIds) {
        this.creativeIds = creativeIds;
    }

    public List<Long> getSiteIds() {
        return nullToEmpty(siteIds);
    }

    public void setSiteIds(List<Long> siteIds) {
        this.siteIds = siteIds;
    }

    public List<Long> getConversionIds() {
        return nullToEmpty(conversionIds);
    }

    public void setConversionIds(List<Long> conversionIds) {
        this.conversionIds = conversionIds;
    }

    public List<String> getSchedules() {
        return nullToEmpty(schedules);
    }

    public void setSchedules(List<String> schedules) {
        this.schedules = schedules;
    }

    public Boolean isSpecialChannelLinked() {
        return specialChannelLinked;
    }

    public void setSpecialChannelLinked(Boolean specialChannelLinked) {
        this.specialChannelLinked = specialChannelLinked;
    }

    public TargetingPacing getImpressionsPacing() {
        return impressionsPacing;
    }

    public void setImpressionsPacing(TargetingPacing impressionsPacing) {
        this.impressionsPacing = impressionsPacing;
    }

    public BigDecimal getImpressionsDailyLimit() {
        return impressionsDailyLimit;
    }

    public void setImpressionsDailyLimit(BigDecimal impressionsDailyLimit) {
        this.impressionsDailyLimit = impressionsDailyLimit;
    }

    public BigDecimal getImpressionsTotalLimit() {
        return impressionsTotalLimit;
    }

    public void setImpressionsTotalLimit(BigDecimal impressionsTotalLimit) {
        this.impressionsTotalLimit = impressionsTotalLimit;
    }

    public TargetingPacing getClicksPacing() {
        return clicksPacing;
    }

    public void setClicksPacing(TargetingPacing clicksPacing) {
        this.clicksPacing = clicksPacing;
    }

    public BigDecimal getClicksTotalLimit() {
        return clicksTotalLimit;
    }

    public void setClicksTotalLimit(BigDecimal clicksTotalLimit) {
        this.clicksTotalLimit = clicksTotalLimit;
    }

    public BigDecimal getClicksDailyLimit() {
        return clicksDailyLimit;
    }

    public void setClicksDailyLimit(BigDecimal clicksDailyLimit) {
        this.clicksDailyLimit = clicksDailyLimit;
    }

    public List<String> getEmptyProps() {
        if (emptyProps == null) {
            emptyProps = new ArrayList<>();
        }
        return emptyProps;
    }

    public void setEmptyProps(List<String> emptyProps) {
        this.emptyProps = emptyProps;
    }

    protected static <T extends List> T nullToEmpty(T src) {
        if (src != null) {
            return src;
        }
        return (T)Collections.emptyList();
    }

    private FrequencyCap buildFrequencyCap(FrequencyCapView frequencyCapView) {
        if (frequencyCapView == null ||
                frequencyCapView.getLifeCount() == null &&
                (frequencyCapView.getPeriod() == null || frequencyCapView.getPeriod().getValue() == null) &&
                frequencyCapView.getWindowCount() == null &&
                (frequencyCapView.getWindowLength() == null || frequencyCapView.getWindowLength().getValue() == null)) {
            getEmptyProps().add(FREQUENCY_CAP.getName());
            return null;
        }
        return frequencyCapView.buildFrequencyCap();
    }

    private List<String> split(String src) {
        if (src == null || src.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return Arrays.asList(src.split("\n"));
    }

    private static FlightSchedule buildFlightSchedule(String scheduleString, FlightBase target) {
        String[] fromTo = scheduleString == null ? null : scheduleString.split(":");
        if (fromTo == null || fromTo.length != 2) {
            ConstraintViolationBuilder builder = new ConstraintViolationBuilder<String>();
            builder.addViolationMessage("schedules", "Value '" + scheduleString + "' in unexpected format");
            builder.throwExpectedException();
        }

        FlightSchedule result = new FlightSchedule();
        result.setTimeFrom(fromTo[0] == null ? null : Long.valueOf(fromTo[0]));
        result.setTimeTo(fromTo[1] == null ? null : Long.valueOf(fromTo[1]));
        result.setFlight(target);

        return result;
    }
}

package app.programmatic.ui.flight.dao.model;

import app.programmatic.ui.common.model.RateType;
import app.programmatic.ui.common.model.Statusable;
import app.programmatic.ui.common.model.VersionEntityBase;

import org.hibernate.annotations.DiscriminatorFormula;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;


@Inheritance
@SecondaryTables({
    @SecondaryTable(name = "flightwhitelistchannel"),
    @SecondaryTable(name = "flightblacklistchannel")
})
@Table(name = "flight")
@NamedEntityGraphs({
    @NamedEntityGraph(name = "Flight.creativeIds", attributeNodes = @NamedAttributeNode("creativeIds")),
    @NamedEntityGraph(name = "Flight.siteIds", attributeNodes = @NamedAttributeNode("siteIds"))
})
@DiscriminatorFormula( "case when parent_id is null then 'Flight' ELSE 'LineItem' end" )
@Entity
public abstract class FlightBase extends VersionEntityBase<Long> implements Statusable,
        CreativeIdsProjection, SiteIdsProjection {

    @SequenceGenerator(name = "FlightGen", sequenceName = "flight_flight_id_seq", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FlightGen")
    @Column(name = "flight_id", nullable = false)
    private Long id;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "date_start")
    private LocalDate dateStart;

    @Column(name = "date_end")
    private LocalDate dateEnd;

    @Column(name = "bid_strategy", nullable = false)
    @Enumerated(EnumType.STRING)
    private BidStrategy bidStrategy;

    @Column(name = "min_ctr_goal", nullable = false)
    private BigDecimal minCtrGoal;

    @Column(name = "rate_type")
    @Enumerated(EnumType.STRING)
    private RateType rateType;

    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "1000000", inclusive = false)
    @Digits(integer=12, fraction=5)
    @Column(name = "rate_value")
    private BigDecimal rateValue;

    @Column(name = "delivery_pacing", nullable = false)
    @Enumerated(EnumType.STRING)
    private DeliveryPacing deliveryPacing;

    @DecimalMin("0")
    @Column(name = "daily_budget")
    private BigDecimal dailyBudget;

    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "1000000000", inclusive = false)
    @Digits(integer=14, fraction=5)
    @Column(name = "budget")
    private BigDecimal budget;

    @Valid
    @JoinColumn(name = "freq_cap_id", referencedColumnName = "freq_cap_id")
    @OneToOne(cascade = CascadeType.ALL)
    private FrequencyCap frequencyCap;

    @Column(table = "flightwhitelistchannel", name = "channel_id")
    private Long whiteListId;

    @Column(table = "flightblacklistchannel", name = "channel_id")
    private Long blackListId;

    @ElementCollection
    @CollectionTable(name="flightchannel", joinColumns=@JoinColumn(name="flight_id"))
    @Column(name="channel_id")
    private List<Long> channelIds;

    @ElementCollection
    @CollectionTable(name="flightgeochannel", joinColumns=@JoinColumn(name="flight_id"))
    @Column(name="geo_channel_id")
    private List<Long> geoChannelIds;

    @ElementCollection
    @CollectionTable(name="flightgeochannelexcluded", joinColumns=@JoinColumn(name="flight_id"))
    @Column(name="channel_id")
    private List<Long> excludedGeoChannelIds;

    @ElementCollection
    @CollectionTable(name="flightdevicechannel", joinColumns=@JoinColumn(name="flight_id"))
    @Column(name="device_channel_id")
    private List<Long> deviceChannelIds;

    @ElementCollection
    @CollectionTable(name="flightcreative", joinColumns=@JoinColumn(name="flight_id"))
    @Column(name="creative_id")
    private List<Long> creativeIds;

    @ElementCollection
    @CollectionTable(name="flightsite", joinColumns=@JoinColumn(name="flight_id"))
    @Column(name="site_id")
    private List<Long> siteIds;

    @ElementCollection
    @CollectionTable(name="flightaction", joinColumns=@JoinColumn(name="flight_id"))
    @Column(name="action_id")
    private List<Long> conversionIds;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "flight")
    @OrderBy("timeFrom ASC")
    private Set<FlightSchedule> schedules = new LinkedHashSet<>();

    @NotNull
    @Column(name = "special_channel_linked", nullable = false)
    private Boolean specialChannelLinked = Boolean.FALSE;

    @NotNull
    @Column(name = "property_source_flags", nullable = false)
    private Integer propertiesSource = Integer.valueOf(0);

    @Column(name = "impressions_pacing", nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetingPacing impressionsPacing;

    @Column(name = "clicks_pacing", nullable = false)
    @Enumerated(EnumType.STRING)
    private TargetingPacing clicksPacing;

    @DecimalMin("0")
    @Column(name = "impressions_daily_limit")
    private BigDecimal impressionsDailyLimit;

    @DecimalMin("0")
    @Column(name = "impressions_total_limit")
    private BigDecimal impressionsTotalLimit;

    @DecimalMin("0")
    @Column(name = "clicks_daily_limit")
    private BigDecimal clicksDailyLimit;

    @DecimalMin("0")
    @Column(name = "clicks_total_limit")
    private BigDecimal clicksTotalLimit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
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

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
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

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public FrequencyCap getFrequencyCap() {
        return frequencyCap;
    }

    public void setFrequencyCap(FrequencyCap frequencyCap) {
        this.frequencyCap = frequencyCap;
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
        return channelIds;
    }

    public void setChannelIds(List<Long> channelIds) {
        this.channelIds = channelIds;
    }

    public List<Long> getGeoChannelIds() {
        return geoChannelIds;
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

    public List<Long> getDeviceChannelIds() {
        return deviceChannelIds;
    }

    public void setDeviceChannelIds(List<Long> deviceChannelIds) {
        this.deviceChannelIds = deviceChannelIds;
    }

    @Override
    public List<Long> getCreativeIds() {
        return creativeIds;
    }

    public void setCreativeIds(List<Long> creativeIds) {
        this.creativeIds = creativeIds;
    }

    @Override
    public List<Long> getSiteIds() {
        return siteIds;
    }

    public void setSiteIds(List<Long> siteIds) {
        this.siteIds = siteIds;
    }

    public List<Long> getConversionIds() {
        return conversionIds;
    }

    public void setConversionIds(List<Long> conversionIds) {
        this.conversionIds = conversionIds;
    }

    public Set<FlightSchedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(Set<FlightSchedule> schedules) {
        this.schedules = schedules;
    }

    public Boolean getSpecialChannelLinked() {
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

    public TargetingPacing getClicksPacing() {
        return clicksPacing;
    }

    public void setClicksPacing(TargetingPacing clicksPacing) {
        this.clicksPacing = clicksPacing;
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

    public BigDecimal getClicksDailyLimit() {
        return clicksDailyLimit;
    }

    public void setClicksDailyLimit(BigDecimal clicksDailyLimit) {
        this.clicksDailyLimit = clicksDailyLimit;
    }

    public BigDecimal getClicksTotalLimit() {
        return clicksTotalLimit;
    }

    public void setClicksTotalLimit(BigDecimal clicksTotalLimit) {
        this.clicksTotalLimit = clicksTotalLimit;
    }

    public Integer getPropertiesSource() {
        return propertiesSource;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    /*
     * Currently, we do not allow to derive values from parent. The only exception is
     * Default Line Item, which is inaccessible directly
     */
    @Deprecated()
    public void setPropertiesSource(Integer propertiesSource) {
        this.propertiesSource = propertiesSource;
    }

    public abstract String getName();
}

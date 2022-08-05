package app.programmatic.ui.geo.dao.model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "CHANNEL")
public class Address {
    @SequenceGenerator(name = "ChannelGen", sequenceName = "channel_channel_id_seq", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ChannelGen")
    @Column(name = "channel_id", nullable = false, updatable = false)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Size(min = 1, max = 2)
    @Column(name = "country_code", nullable = false, updatable = false)
    private String countryCode;

    @NotNull
    @Column(name = "parent_channel_id", nullable = false, updatable = false)
    private Long parentChannelId;

    @NotNull
    @Column(name = "namespace", nullable = false, updatable = false)
    private Character namespace = 'G';

    @NotNull
    @Size(min = 1, max = 7)
    @Column(name = "geo_type", nullable = false, updatable = false)
    private String geoType = "ADDRESS";

    @NotNull
    @Size(min = 1, max = 2000)
    @Column(name = "address", nullable = false, updatable = false)
    private String address;

    @NotNull
    @Column(name = "LATITUDE", nullable = false, updatable = false)
    private BigDecimal latitude;

    @NotNull
    @Column(name = "LONGITUDE", nullable = false, updatable = false)
    private BigDecimal longitude;

    @NotNull
    @Column(name = "RADIUS", nullable = false, updatable = false)
    private Long radius;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "radius_units", nullable = false, updatable = false)
    private RadiusUnit radiusUnits;

    @NotNull
    @Column(name = "channel_type", nullable = false, updatable = false)
    private Character channelType = 'G';

    @NotNull
    @Column(name = "status", nullable = false)
    private Character status = 'A';

    @NotNull
    @Column(name = "flags", nullable = false)
    private long flags;

    @NotNull
    @Column(name = "qa_status", nullable = false)
    private Character qaStatus = 'A';

    @NotNull
    @Column(name = "display_status_id", nullable = false)
    private Long displayStatusId = 1L; // Live

    @Column(name = "visibility")
    @Enumerated(EnumType.STRING)
    private ChannelVisibility visibility = ChannelVisibility.PUB;

    @Column(name = "message_sent")
    private int messageSent;

    public Address() {
    }

    public Address(String address, BigDecimal latitude, BigDecimal longitude, Long radius, RadiusUnit radiusUnits) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.radiusUnits = radiusUnits;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public Long getParentChannelId() {
        return parentChannelId;
    }

    public void setParentChannelId(Long parentChannelId) {
        this.parentChannelId = parentChannelId;
    }

    public Character getNamespace() {
        return namespace;
    }

    public void setNamespace(Character namespace) {
        this.namespace = namespace;
    }

    public String getGeoType() {
        return geoType;
    }

    public void setGeoType(String geoType) {
        this.geoType = geoType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public Long getRadius() {
        return radius;
    }

    public void setRadius(Long radius) {
        this.radius = radius;
    }

    public RadiusUnit getRadiusUnits() {
        return radiusUnits;
    }

    public void setRadiusUnits(RadiusUnit radiusUnits) {
        this.radiusUnits = radiusUnits;
    }

    public Character getChannelType() {
        return channelType;
    }

    public void setChannelType(Character channelType) {
        this.channelType = channelType;
    }

    public Character getStatus() {
        return status;
    }

    public void setStatus(Character status) {
        this.status = status;
    }

    public long getFlags() {
        return flags;
    }

    public void setFlags(long flags) {
        this.flags = flags;
    }

    public Character getQaStatus() {
        return qaStatus;
    }

    public void setQaStatus(Character qaStatus) {
        this.qaStatus = qaStatus;
    }

    public Long getDisplayStatusId() {
        return displayStatusId;
    }

    public void setDisplayStatusId(Long displayStatusId) {
        this.displayStatusId = displayStatusId;
    }

    public ChannelVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(ChannelVisibility visibility) {
        this.visibility = visibility;
    }

    public int getMessageSent() {
        return messageSent;
    }

    public void setMessageSent(int messageSent) {
        this.messageSent = messageSent;
    }
}

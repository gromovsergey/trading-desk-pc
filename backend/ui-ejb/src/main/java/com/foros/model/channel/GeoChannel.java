package com.foros.model.channel;

import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.changes.inspection.changeNode.EmbeddedChange;
import com.foros.model.Country;
import com.foros.model.Status;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.util.StringUtil;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.copy.ShallowCollectionCloner;
import com.foros.validation.constraint.RequiredConstraint;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;


@Entity
@DiscriminatorValue("G")
@NamedQueries({
    @NamedQuery(name = "GeoChannel.findStates", query = "SELECT g FROM GeoChannel g where g.geoType = 'STATE' " +
            " and g.country.countryCode = :countryCode order by name"),
    @NamedQuery(name = "GeoChannel.orphanCities", query = "SELECT g FROM GeoChannel g where g.geoType = 'CITY' and g.status != 'D' " +
            " and g.parentChannel.geoType='CNTRY' and g.country.countryCode = :countryCode order by name"),
    @NamedQuery(name = "GeoChannel.addresses", query = "SELECT g FROM GeoChannel g where g.geoType = 'ADDRESS' " +
                "and g.country.countryCode = :countryCode and g.address = :address and g.coordinates.latitude = :latitude " +
                "and g.coordinates.longitude = :longitude and g.radius.distance = :radius and g.radius.radiusUnit = :radiusUnit")
})
public class GeoChannel extends Channel {
    @Column(name = "GEO_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    @RequiredConstraint
    private GeoType geoType = GeoType.CITY;

    @ChangesInspection(type = InspectionType.FIELD)
    @JoinColumn(name = "PARENT_CHANNEL_ID", referencedColumnName = "CHANNEL_ID")
    @ManyToOne(targetEntity = Channel.class)
    private GeoChannel parentChannel;

    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentChannel", cascade = {CascadeType.ALL})
    @OrderBy("name")
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Set<GeoChannel> childChannels = new LinkedHashSet<>();

    @JoinTable(name = "CCGGeoChannel",
            joinColumns = {@JoinColumn(name = "GEO_CHANNEL_ID", referencedColumnName = "CHANNEL_ID")},
            inverseJoinColumns = {@JoinColumn(name = "CCG_ID", referencedColumnName = "CCG_ID")}
    )
    @ManyToMany(fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    private Set<CampaignCreativeGroup> associatedCampaignCreativeGroups = new LinkedHashSet<>();

    @Column(name = "ADDRESS")
    @RequiredConstraint
    private String address;

    @Embedded
    @Audit(nodeFactory = EmbeddedChange.Factory.class)
    private Coordinates coordinates = new Coordinates();

    @Embedded
    @Audit(nodeFactory = EmbeddedChange.Factory.class)
    private Radius radius = new Radius();

    public GeoChannel() {
    }

    public GeoChannel(Long id) {
        this.setId(id);
    }

    public GeoChannel(Country country, String state) {
        this.geoType = GeoType.STATE;
        this.setCountry(country);
        this.setName(state);
    }

    public GeoChannel(Country country, GeoChannel parentChannel, String city) {
        this.geoType = GeoType.CITY;
        this.setCountry(country);
        this.setParentChannel(parentChannel);
        this.setName(city);
    }

    public GeoChannel getParentChannel() {
        return parentChannel;
    }

    public void setParentChannel(GeoChannel parentChannel) {
        this.parentChannel = parentChannel;
    }

    private GeoChannel findParentChannel(GeoType type) {
        GeoChannel currentParent = parentChannel;
        while (currentParent != null) {
            if (currentParent.getGeoType() == type) {
                return currentParent;
            }
            currentParent = currentParent.getParentChannel();
        }
        return null;
    }

    public GeoChannel getStateChannel() {
        return findParentChannel(GeoType.STATE);
    }

    public GeoChannel getCountryChannel() {
        return findParentChannel(GeoType.CNTRY);
    }

    public Set<GeoChannel> getChildChannels() {
        return new ChangesSupportSet<>(this, "childChannels", childChannels);
    }

    public void setChildChannels(Set<GeoChannel> childChannels) {
        this.childChannels = childChannels;
        this.registerChange("childChannels");
    }

    public GeoType getGeoType() {
        return geoType;
    }

    public void setGeoType(GeoType geoType) {
        this.geoType = geoType;
        registerChange("geoType");
    }

    public void setAssociatedCampaignCreativeGroups(Set<CampaignCreativeGroup> associatedCampaignCreativeGroups) {
        this.associatedCampaignCreativeGroups = associatedCampaignCreativeGroups;
        this.registerChange("associatedCampaignCreativeGroups");
    }

    @Override
    protected ChannelNamespace calculateNamespace() {
        return ChannelNamespace.GEO;
    }

    @Override
    public String getChannelType() {
        return CHANNEL_TYPE_GEO;
    }

    @Override
    public Status getInheritedStatus() {
        return getStatus().combine(getParentStatus());
    }

    @Override
    public Status getParentStatus() {
        return getParentChannel() != null ? getParentChannel().getStatus() : Status.ACTIVE;
    }

    @Override
    public boolean equals(Object o) {
        return (this == o);
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(this);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressText() {
        return StringUtil.getLocalizedString("channel.addressText", getRadius().getDistance(), getRadius().getRadiusUnit(), getAddress());
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
        registerChange("coordiantes");
    }

    public Radius getRadius() {
        return radius;
    }

    public void setRadius(Radius radius) {
        this.radius = radius;
        registerChange("radius");
    }
}

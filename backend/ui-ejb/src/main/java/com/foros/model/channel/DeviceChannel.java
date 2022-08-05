package com.foros.model.channel;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.model.Status;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.SizeConstraint;

import java.util.LinkedHashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@Entity
@DiscriminatorValue("V")
@NamedQueries({
        @NamedQuery(name = "DeviceChannel.findRootIdByName", query = "SELECT g.id FROM DeviceChannel g where name = :name and parentChannel is null"),
        @NamedQuery(name = "DeviceChannel.findByParentIdAndName", query = "SELECT g.id FROM DeviceChannel g where name = :name and parentChannel.id = :id")
})
@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE, Status.DELETED})
@XmlType(propOrder = {
        "expression"
})
public class DeviceChannel extends Channel {

    public static final String APPLICATIONS = "Applications";
    public static final String BROWSERS = "Browsers";
    public static final String NON_MOBILE_DEVICES_CHANNEL_NAME = "Non-mobile Devices";
    public static final String MOBILE_DEVICES_CHANNEL_NAME = "Mobile Devices";

    @RequiredConstraint
    @SizeConstraint(max = 1024, message="errors.expression.tooLarge")
    @Column(name = "EXPRESSION")
    private String expression;

    @ChangesInspection(type = InspectionType.FIELD)
    @JoinColumn(name = "PARENT_CHANNEL_ID", referencedColumnName = "CHANNEL_ID")
    @ManyToOne(targetEntity = Channel.class)
    private DeviceChannel parentChannel;

    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parentChannel", cascade = {CascadeType.ALL})
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Set<DeviceChannel> childChannels = new LinkedHashSet<DeviceChannel>();

    public DeviceChannel() {
    }

    public DeviceChannel(Long id) {
        setId(id);
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
        this.registerChange("expression");
    }

    @XmlTransient
    public DeviceChannel getParentChannel() {
        return parentChannel;
    }

    public void setParentChannel(DeviceChannel parentChannel) {
        this.parentChannel = parentChannel;
        this.registerChange("parentChannel");
    }

    @XmlTransient
    public Set<DeviceChannel> getChildChannels() {
        return new ChangesSupportSet<DeviceChannel>(this, "childChannels", childChannels);
    }

    public void setChildChannels(Set<DeviceChannel> childChannels) {
        this.childChannels = childChannels;
        this.registerChange("childChannels");
    }

    @Override
    protected ChannelNamespace calculateNamespace() {
        return ChannelNamespace.DEVICE;
    }

    @Override
    public String getChannelType() {
        return CHANNEL_TYPE_DEVICE;
    }

    public boolean isNonMobilesChannel() {
        return getName().equals(NON_MOBILE_DEVICES_CHANNEL_NAME) && getParentChannel() != null && getParentChannel().isBrowsers();
    }

    public boolean isMobilesChannel() {
        return getName().equals(MOBILE_DEVICES_CHANNEL_NAME) && getParentChannel() != null && getParentChannel().isBrowsers();
    }

    public boolean isBrowsers() {
        return getName().equals(BROWSERS) && getParentChannel() == null;
    }

    public boolean isApplications() {
        return getName().equals(APPLICATIONS) && getParentChannel() == null;
    }

    @Override
    public Status getInheritedStatus() {
        return getStatus();
    }

    @Override
    public Status getParentStatus() {
        if (getParentChannel() == null) {
            return getStatus();
        }
        return getParentChannel().getStatus();
    }
}

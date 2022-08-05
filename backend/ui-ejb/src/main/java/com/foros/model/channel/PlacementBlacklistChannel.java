package com.foros.model.channel;

import com.foros.annotations.AllowedStatuses;
import com.foros.model.Status;
import com.foros.model.channel.placementsBlacklist.PlacementBlacklist;
import com.foros.util.changes.ChangesSupportSet;

import java.sql.Timestamp;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;


@Entity
@DiscriminatorValue("P")
@AllowedStatuses(values = {Status.ACTIVE, Status.DELETED})
public class PlacementBlacklistChannel extends Channel {

    @Transient
    private Set<PlacementBlacklist> placementsBlacklist = new LinkedHashSet<>();

    @Column(name = "SIZE_ID")
    private Long sizeId;

    @Column(name = "TRIGGERS_VERSION")
    private Timestamp triggersVersion;

    @Override
    public String getChannelType() {
        return CHANNEL_TYPE_PLACEMENT_BLACKLIST;
    }

    @Override
    protected ChannelNamespace calculateNamespace() {
        return ChannelNamespace.SPECIAL;
    }

    public Set<PlacementBlacklist> getPlacementsBlacklist() {
        return ChangesSupportSet.wrap(this, "placementsBlacklist", placementsBlacklist);
    }

    public void setPlacementsBlacklist(Set<PlacementBlacklist> placementsBlacklist) {
        registerChange("placementsBlacklist");
        this.placementsBlacklist = placementsBlacklist;
    }

    public Long getSizeId() {
        return sizeId;
    }

    public void setSizeId(Long sizeId) {
        registerChange("sizeId");
        this.sizeId = sizeId;
    }

    public Timestamp getTriggersVersion() {
        return triggersVersion;
    }

    public void setTriggersVersion(Timestamp triggersVersion) {
        registerChange("triggersVersion");
        this.triggersVersion = triggersVersion;
    }
}


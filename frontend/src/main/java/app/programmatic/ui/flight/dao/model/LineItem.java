package app.programmatic.ui.flight.dao.model;

import app.programmatic.ui.ccg.dao.model.CcgDisplayStatus;
import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.sql.Timestamp;
import javax.persistence.*;


@SecondaryTable(name = "flightccg")
@Entity
@NamedEntityGraphs({
        @NamedEntityGraph(name = "LineItem.specialChannelId", attributeNodes = @NamedAttributeNode("creativeIds"))
})
@DiscriminatorValue("LineItem")
public class LineItem extends FlightBase implements SpecialChannelIdProjection {

    @Column(name = "parent_id", updatable = false)
    private Long flightId;

    @Column(name="ccg_id", table="flightccg")
    private Long ccgId;

    @Column(name = "special_channel_id")
    private Long specialChannelId;

    @Transient
    private String name;
    @Transient
    private Long ccgChannelId;
    @Transient
    private Timestamp ccgVersion;
    @Transient
    private CcgDisplayStatus displayStatus;


    public Long getFlightId() {
        return flightId;
    }

    public void setFlightId(Long flightId) {
        this.flightId = flightId;
    }

    public Long getCcgId() {
        return ccgId;
    }

    public void setCcgId(Long ccgId) {
        this.ccgId = ccgId;
    }

    @Override
    public Long getSpecialChannelId() {
        return specialChannelId;
    }

    public void setSpecialChannelId(Long specialChannelId) {
        this.specialChannelId = specialChannelId;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCcgChannelId() {
        return ccgChannelId;
    }

    public void setCcgChannelId(Long ccgChannelId) {
        this.ccgChannelId = ccgChannelId;
    }

    public Timestamp getCcgVersion() {
        return ccgVersion;
    }

    public void setCcgVersion(Timestamp ccgVersion) {
        this.ccgVersion = ccgVersion;
    }

    @Override
    public MajorDisplayStatus getMajorStatus() {
        return displayStatus == null ? null : displayStatus.getMajorStatus();
    }

    public CcgDisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(CcgDisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }
}

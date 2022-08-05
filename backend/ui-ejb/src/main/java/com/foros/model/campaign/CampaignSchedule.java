package com.foros.model.campaign;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "CAMPAIGNSCHEDULE")
public class CampaignSchedule extends EntityBase implements Identifiable, WeekSchedule {

    @SequenceGenerator(name = "CampaignScheduleGen", sequenceName = "CAMPAIGNSCHEDULE_SCHEDULE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CampaignScheduleGen")
    @Column(name = "SCHEDULE_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "CAMPAIGN_ID", referencedColumnName = "CAMPAIGN_ID")
    @ManyToOne
    private Campaign campaign;

    @Column(name = "TIME_FROM", nullable = false)
    private Long timeFrom;

    @Column(name = "TIME_TO", nullable = false)
    private Long timeTo;

    public CampaignSchedule() {

    }

    @XmlTransient
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @XmlElement
    public Long getTimeFrom() {
        return timeFrom;
    }

    public void setTimeFrom(Long timeFrom) {
        this.timeFrom = timeFrom;
        this.registerChange("timeFrom");
    }

    @XmlElement
    public Long getTimeTo() {
        return timeTo;
    }

    public void setTimeTo(Long timeTo) {
        this.timeTo = timeTo;
        this.registerChange("timeTo");

    }

    @XmlTransient
    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
        this.registerChange("campaign");
    }

    @Override
    public String toString() {
        return "com.foros.model.campaign.CampaignSchedule [id=" + getId() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CampaignSchedule)) {
            return false;
        }

        CampaignSchedule that = (CampaignSchedule) o;
        if (this.getId() != null && that.getId() != null) {
            if (this.getId().equals(that.getId())) {
                return true;
            }
            return false;
        }

        if (this.getTimeFrom().equals(that.getTimeFrom()) && this.getTimeTo().equals(that.getTimeTo())) {
            return true;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + (getId() == null ? 0 : getId().hashCode());
        hash = 31 * hash + (getTimeFrom() == null ? 0 : getTimeFrom().hashCode());
        hash = 31 * hash + (getTimeTo() == null ? 0 : getTimeTo().hashCode());
        return hash;
    }
}

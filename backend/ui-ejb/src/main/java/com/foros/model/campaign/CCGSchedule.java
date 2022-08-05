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
import org.apache.commons.lang.ObjectUtils;

@Entity
@Table(name = "CCGSCHEDULE")
public class CCGSchedule extends EntityBase implements Identifiable, WeekSchedule {

    @SequenceGenerator(name = "CCGScheduleGen", sequenceName = "CCGSCHEDULE_SCHEDULE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CCGScheduleGen")
    @Column(name = "SCHEDULE_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "CCG_ID", referencedColumnName = "CCG_ID")
    @ManyToOne
    private CampaignCreativeGroup campaignCreativeGroup;

    @Column(name = "TIME_FROM", nullable = false)
    private Long timeFrom;

    @Column(name = "TIME_TO", nullable = false)
    private Long timeTo;

    public CCGSchedule() {

    }

    @Override
    @XmlTransient
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @XmlTransient
    public CampaignCreativeGroup getCampaignCreativeGroup() {
        return campaignCreativeGroup;
    }

    public void setCampaignCreativeGroup(CampaignCreativeGroup campaignCreativeGroup) {
        this.registerChange("campaignCreativeGroup");
        this.campaignCreativeGroup = campaignCreativeGroup;
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

    @Override
    public String toString() {
        return "com.foros.model.campaign.CCGSchedule [id=" + getId() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CCGSchedule)) {
            return false;
        }

        CCGSchedule that = (CCGSchedule) o;

        if (this.getId() != null && that.getId() != null) {
            if (this.getId().equals(that.getId())) {
                return true;
            }
            return false;
        }

        if (ObjectUtils.equals(this.getTimeFrom(), that.getTimeFrom()) && ObjectUtils.equals(this.getTimeTo(), that.getTimeTo())) {
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

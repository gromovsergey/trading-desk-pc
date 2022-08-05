package com.foros.model.channel;


import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(propOrder = {
        "priority",
        "matchMarker",
        "matchRegexp",
        "outputRegexp"
})
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@Entity
@Table(name = "PLATFORMDETECTOR")
public class PlatformDetector  extends EntityBase implements Identifiable {

    @Id
    @Column(name = "platform_detector_id", nullable = false, insertable = false, updatable = false)
    private Long id;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "platform_id", referencedColumnName = "platform_id", insertable = false, updatable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Platform platform;

    @Column(name = "priority", nullable = false)
    private Long priority;

    @Column(name = "match_marker", nullable = true)
    private String matchMarker;

    @Column(name = "match_regexp", nullable = true)
    private String matchRegexp;

    @Column(name = "output_regexp", nullable = true)
    private String outputRegexp;

    public PlatformDetector() {
    }

    @XmlTransient
    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @XmlTransient
    public Platform getPlatform() {
        return this.platform;
    }

    public void setPlatform(Platform platform) {
        this.platform = platform;
        this.registerChange("platform");
    }

    public Long getPriority() {
        return this.priority;
    }

    public void setPriority(Long priority) {
        this.priority = priority;
        this.registerChange("priority");
    }

    public String getMatchMarker() {
        return this.matchMarker;
    }

    public void setMatchMarker(String matchMarker) {
        this.matchMarker = matchMarker;
        this.registerChange("matchMarker");
    }

    public String getMatchRegexp() {
        return this.matchRegexp;
    }

    public void setMatchRegexp(String matchRegexp) {
        this.matchRegexp = matchRegexp;
        this.registerChange("matchRegexp");
    }

    public String getOutputRegexp() {
        return this.outputRegexp;
    }

    public void setOutputRegexp(String outputRegexp) {
        this.outputRegexp = outputRegexp;
        this.registerChange("outputRegexp");
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlatformDetector)) {
            return false;
        }

        PlatformDetector platformDetector = (PlatformDetector) o;
        return getId() != null ? getId().equals(platformDetector.getId()) : platformDetector.getId() == null;
    }
}

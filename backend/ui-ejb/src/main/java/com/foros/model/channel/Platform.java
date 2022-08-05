package com.foros.model.channel;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import com.foros.util.changes.ChangesSupportSet;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OrderBy;

@XmlRootElement(name = "platform")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@XmlType(propOrder = {
        "id",
        "name",
        "type",
        "platformDetectors"
})
@Entity
@Table(name = "PLATFORM")
public class Platform extends EntityBase implements Identifiable {
    @Id
    @Column(name = "platform_id", nullable = false, insertable = false, updatable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private String type;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "platform")
    @CopyPolicy(strategy = CopyStrategy.DEEP, type = LinkedHashSet.class)
    @Fetch(FetchMode.SUBSELECT)
    @Cascade(org.hibernate.annotations.CascadeType.DETACH)
    @OrderBy(clause = "priority")
    private Set<PlatformDetector> platformDetectors = new LinkedHashSet<PlatformDetector>();

    public Platform() {
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
        this.registerChange("type");
    }

    @XmlElementWrapper(name = "detectors")
    @XmlElement(name = "detector")
    public Set<PlatformDetector> getPlatformDetectors() {
        return new ChangesSupportSet<PlatformDetector>(this, "platformDetectors", platformDetectors);
    }

    public void setPlatformDetectors(Set<PlatformDetector> platformDetectors) {
        this.platformDetectors = platformDetectors;
        this.registerChange("platformDetectors");
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
        if (!(o instanceof Platform)) {
            return false;
        }

        Platform platform = (Platform) o;
        return getId() != null ? getId().equals(platform.getId()) : platform.getId() == null;
    }
}

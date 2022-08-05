package com.foros.model;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.jaxb.adapters.TimestampXmlAdapter;

import java.sql.Timestamp;
import javax.annotation.security.PermitAll;
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@MappedSuperclass
@XmlType(propOrder = {"version"})
public class VersionEntityBase extends EntityBase {
    @Column(name = "VERSION")
    @Version
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Timestamp version;

    @XmlElement(name = "updated")
    @XmlJavaTypeAdapter(TimestampXmlAdapter.class)
    public Timestamp getVersion() {
        return version;
    }

    @PermitAll
    public void setVersion(Timestamp version) {
        this.version = version;
        this.registerChange("version");
    }
}

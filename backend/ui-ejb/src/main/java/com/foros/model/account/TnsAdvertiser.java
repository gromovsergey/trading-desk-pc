package com.foros.model.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.foros.model.EntityBase;
import com.foros.model.IdNameEntity;
import com.foros.validation.constraint.IdConstraint;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@XmlType
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"englishName", "path", "kind", "etag"})
public class TnsAdvertiser extends EntityBase implements IdNameEntity {

    public static final String COUNTRY_CODE = "RU";

    @Id
    @Column(name = "TNS_ADVERTISER_ID", nullable = false)
    @IdConstraint
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    public TnsAdvertiser() {
    }

    @Override
    @XmlElement(name = "Nbm")
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @Override
    @XmlElement(name = "Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TnsAdvertiser)) {
            return false;
        }

        TnsAdvertiser other = (TnsAdvertiser) object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }

        return true;
    }

}

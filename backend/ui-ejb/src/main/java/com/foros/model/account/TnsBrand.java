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
import javax.xml.bind.annotation.XmlType;

@Entity
@XmlType
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties({"englishName", "path", "kind", "etag"})
public class TnsBrand extends EntityBase implements IdNameEntity {

    @Id
    @Column(name = "TNS_BRAND_ID", nullable = false)
    @IdConstraint
    private Long id;

    @Column(name = "PARENT_ID", nullable = false)
    private Long parentId;

    @Column(name = "NAME", nullable = false)
    private String name;

    public TnsBrand() {
    }

    public TnsBrand(Long tnsBrandId) {
        this.id = tnsBrandId;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }


    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
        this.registerChange("parentId");
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
        if (!(object instanceof TnsBrand)) {
            return false;
        }

        TnsBrand other = (TnsBrand) object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }

        return true;
    }

}

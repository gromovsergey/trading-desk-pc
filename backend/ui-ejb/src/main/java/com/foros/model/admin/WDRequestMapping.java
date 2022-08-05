package com.foros.model.admin;

import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.validation.constraint.ByteLengthConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.PatternConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.SizeConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "WDREQUESTMAPPING")
@NamedQueries({
    @NamedQuery(name = "WDRequestMapping.findAll", query = "SELECT w FROM WDRequestMapping w ORDER BY UPPER(w.name)")
})
public class WDRequestMapping extends VersionEntityBase implements Serializable, Identifiable {
    @SequenceGenerator(name = "WDRequestMappingGen", sequenceName = "WDREQUESTMAPPING_WD_REQ_MAPPING_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "WDRequestMappingGen")
    @Column(name = "WD_REQ_MAPPING_ID", nullable = false)
    @IdConstraint
    private Long id;

    @PatternConstraint(regexp = "^[a-zA-Z0-9]*$", message="errors.field.onlyLatinOrDigits")
    @RequiredConstraint
    @SizeConstraint(max = 100)
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "DESCRIPTION")
    @StringSizeConstraint(size = 2000)
    private String description;

    @Column(name = "REQUEST", nullable = false)
    @RequiredConstraint
    @ByteLengthConstraint(length = 4000)
    private String protocolRequest;

    public WDRequestMapping() {
    }

    public WDRequestMapping(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.registerChange("description");
    }

    public String getProtocolRequest() {
        return protocolRequest;
    }

    public void setProtocolRequest(String protocolRequest) {
        this.protocolRequest = protocolRequest;
        this.registerChange("protocolRequest");
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WDRequestMapping)) {
            return false;
        }

        WDRequestMapping other = (WDRequestMapping) obj;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);

        return hash;
    }
}

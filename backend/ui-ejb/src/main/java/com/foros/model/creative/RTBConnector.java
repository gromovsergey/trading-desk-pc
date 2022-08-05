package com.foros.model.creative;

import com.foros.model.Identifiable;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "RTBCONNECTOR")
public class RTBConnector implements Identifiable, Serializable {
    @Id
    @GeneratedValue(generator = "RTBConnector")
    @GenericGenerator(name = "RTBConnector", strategy = "com.foros.persistence.hibernate.BulkSequenceGenerator", parameters = {
            @Parameter(name = "sequenceName", value = "RTBCONNECTOR_RTB_ID_SEQ"),
            @Parameter(name = "allocationSize", value = "20")
    })
    @IdConstraint
    @Column(name = "RTB_ID", nullable = false)
    private Long id;

    @RequiredConstraint
    @StringSizeConstraint(size = 50)
    @Column(name = "NAME")
    private String name;

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
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
        if (!(object instanceof RTBConnector)) {
            return false;
        }

        RTBConnector other = (RTBConnector) object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }

        return true;
    }
}

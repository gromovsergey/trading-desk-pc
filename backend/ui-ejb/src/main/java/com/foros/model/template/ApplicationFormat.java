package com.foros.model.template;

import com.foros.model.IdNameEntity;
import com.foros.model.VersionEntityBase;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.RequiredConstraint;

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
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@Entity
@Table(name = "APPFORMAT")
@NamedQueries({
    @NamedQuery(name = "ApplicationFormat.findAll", query = "SELECT a FROM ApplicationFormat a ORDER BY a.name"),
    @NamedQuery(name = "ApplicationFormat.findById", query = "SELECT a FROM ApplicationFormat a WHERE a.id = :id"),
    @NamedQuery(name = "ApplicationFormat.findByName", query = "SELECT a FROM ApplicationFormat a WHERE a.name = :name"),
    @NamedQuery(name = "ApplicationFormat.findByMimeType", query = "SELECT a FROM ApplicationFormat a WHERE a.mimeType = :mimeType")
})
@XmlType(propOrder = {
        "id",
        "name",
        "mimeType"
})
@XmlAccessorType(XmlAccessType.NONE)
public class ApplicationFormat extends VersionEntityBase implements Serializable, IdNameEntity {
    public static final String JS = "js";
    public static final String HTML = "html";

    public static final String DISCOVER_CUSTOMIZATION_FORMAT = "Discover Customization";
    public static final String DISCOVER_TAG_FORMAT = "Discover Tag";
    public static final String PREVIEW_FORMAT = "preview";

    @SequenceGenerator(name = "AppFormatGen", sequenceName = "APPFORMAT_APP_FORMAT_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AppFormatGen")
    @Column(name = "APP_FORMAT_ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    @NameConstraint
    @RequiredConstraint
    private String name;

    @Column(name = "MIME_TYPE")
    @RequiredConstraint
    @NameConstraint
    private String mimeType;

    public ApplicationFormat() {
    }

    public ApplicationFormat(Long id) {
        this.id = id;
    }

    public ApplicationFormat(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    @XmlElement
    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @XmlElement
    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    @XmlElement
    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
        this.registerChange("mimeType");
    }

    @Transient
    public String getDefaultName() {
        return getName();
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
        if (!(object instanceof ApplicationFormat)) {
            return false;
        }

        ApplicationFormat other = (ApplicationFormat)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.template.AppFormat[id=" + getId() + "]";
    }
}

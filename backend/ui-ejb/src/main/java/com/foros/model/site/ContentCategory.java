package com.foros.model.site;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.model.Country;
import com.foros.model.IdNameEntity;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.util.HashUtil;
import org.apache.commons.lang.ObjectUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "CONTENTCATEGORY")
public class ContentCategory extends VersionEntityBase implements Serializable, Identifiable, IdNameEntity {
    @SequenceGenerator(name = "ContentCategoryGen", sequenceName = "CONTENTCATEGORY_CONTENT_CATEGORY_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ContentCategoryGen")
    @Column(name = "CONTENT_CATEGORY_ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @ChangesInspection(type = InspectionType.NONE)
    @JoinColumn(name = "COUNTRY_CODE", referencedColumnName = "COUNTRY_CODE")
    @ManyToOne
    private Country country;

    public ContentCategory() {
    }

    public ContentCategory(Long id, String name) {
        this.setId(id);
        this.setName(name);
    }

    public Long getId() {
        return this.id;
    }

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

    public Country getCountry() {
        return this.country;
    }

    public void setCountry(Country country) {
        this.country = country;
        this.registerChange("country");
    }

    @Override
    public int hashCode() {
        return HashUtil.calculateHash(getId(), getName(), getCountry());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof ContentCategory)) {
            return false;
        }

        ContentCategory that = (ContentCategory)o;
        if (this.getId() != null && that.getId() != null) {
            if (this.getId().equals(that.getId())) {
                return true;
            }
            return false;
        }

        if (!ObjectUtils.equals(this.getName(), that.getName())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getCountry(), that.getCountry())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.site.ContentCategory[id=" + getId() + "]";
    }

}

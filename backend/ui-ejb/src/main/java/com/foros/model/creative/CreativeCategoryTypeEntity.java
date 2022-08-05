package com.foros.model.creative;

import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.util.changes.ChangesSupportList;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.apache.commons.lang.ObjectUtils;

/**
* Used for Creative Category audit log. Versioned because otherwise Hibernate doesn't raise onFushDirty event. 
*/
@Entity
@Table(name = "CREATIVECATEGORYTYPE")
@NamedQueries(
      {
    @NamedQuery(name = "CreativeCategoryTypeEntity.findById", query = "SELECT cct FROM CreativeCategoryTypeEntity cct WHERE cct.id = :id")
  })
public class CreativeCategoryTypeEntity extends VersionEntityBase implements Identifiable {

    @Id
    @Column(name = "CCT_ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "CCT_ID", referencedColumnName = "CCT_ID", insertable = false, updatable = false)
    private List<CreativeCategory> categories = new LinkedList<CreativeCategory>();

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    public String getName() {
        return name;
    }

    public void setCategories(List<CreativeCategory> categories) {
        this.categories = categories;
        this.registerChange("categories");
    }

    public List<CreativeCategory> getCategories() {
        return new ChangesSupportList<CreativeCategory>(this, "categories", categories);
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof CreativeCategoryTypeEntity)) {
            return false;
        }

        CreativeCategoryTypeEntity other = (CreativeCategoryTypeEntity) object;
        if (!ObjectUtils.equals(this.getId(), other.getId())) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.creative.CreativeCategoryTypeEntity[id=" + getId() + "]";
    }
}

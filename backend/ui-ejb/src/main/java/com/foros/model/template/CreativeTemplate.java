package com.foros.model.template;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.jaxb.adapters.CreativeCategoryXmlAdapter;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.util.changes.ChangesSupportSet;
import com.foros.util.copy.ShallowCollectionCloner;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@DiscriminatorValue("CREATIVE")
@NamedQueries({
    @NamedQuery(name = "CreativeTemplate.findAll", query = "SELECT NEW com.foros.model.template.TemplateTO(ct.id, ct.defaultName, ct.status) FROM CreativeTemplate ct "),
    @NamedQuery(name = "CreativeTemplate.findAllNonDeleted", query = "SELECT NEW com.foros.model.template.TemplateTO(ct.id, ct.defaultName, ct.status) FROM CreativeTemplate ct where status <> 'D'"),
    @NamedQuery(name = "CreativeTemplate.findByCategory", query = "SELECT ct FROM CreativeTemplate ct WHERE :category MEMBER OF ct.categories")
})
@XmlRootElement(name = "creativeTemplate")
@XmlType(propOrder = {
        "expandable",
        "categories"
})
@XmlAccessorType(XmlAccessType.NONE)
public class CreativeTemplate extends Template {
    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Set<Creative> creatives = new HashSet<Creative>();

    @JoinTable(name = "CREATIVECATEGORY_TEMPLATE",
      joinColumns = {@JoinColumn(name = "TEMPLATE_ID", referencedColumnName = "TEMPLATE_ID")},
      inverseJoinColumns = {@JoinColumn(name = "CREATIVE_CATEGORY_ID", referencedColumnName = "CREATIVE_CATEGORY_ID")})
    @ManyToMany(fetch = FetchType.EAGER)
    @CopyPolicy(strategy = CopyStrategy.CLONE, type = LinkedHashSet.class, cloner = ShallowCollectionCloner.class)
    private Set<CreativeCategory> categories = new LinkedHashSet<CreativeCategory>();

    @Column(name = "EXPANDABLE")
    private boolean expandable = false;

    public CreativeTemplate() {

    }

    public CreativeTemplate(Long id) {
        super(id);
    }

    public Set<Creative> getCreatives() {
        return new ChangesSupportSet<Creative>(this, "creatives", creatives);
    }

    public void setCreatives(Set<Creative> creatives) {
        this.creatives = creatives;
        this.registerChange("creatives");
    }

    @XmlElement(name = "category")
    @XmlElementWrapper(name = "categories")
    @XmlJavaTypeAdapter(CreativeCategoryXmlAdapter.class)
    public Set<CreativeCategory> getCategories() {
        return new ChangesSupportSet<CreativeCategory>(this, "categories", categories);
    }

    public void setCategories(Set<CreativeCategory> categories) {
        this.categories = categories;
        this.registerChange("categories");
    }

    @XmlElement
    public boolean isExpandable() {
        return expandable;
    }

    public void setExpandable(boolean expandable) {
        this.expandable = expandable;
        this.registerChange("expandable");
    }

    @Override
    public String toString() {
        return "com.foros.model.template.CreativeTemplate[id=" + getId() + "]";
    }
}

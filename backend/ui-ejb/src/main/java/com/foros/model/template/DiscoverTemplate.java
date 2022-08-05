package com.foros.model.template;

import com.foros.annotations.ChangesInspection;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.annotations.InspectionType;
import com.foros.model.site.WDTag;
import com.foros.util.changes.ChangesSupportSet;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

@Entity
@DiscriminatorValue("DISCOVER")
@NamedQueries({
    @NamedQuery(name = "DiscoverTemplate.findAll", query = "SELECT NEW com.foros.model.template.TemplateTO(dt.id, dt.defaultName, dt.status) FROM DiscoverTemplate dt"),
    @NamedQuery(name = "DiscoverTemplate.findAllNonDeleted", query = "SELECT NEW com.foros.model.template.TemplateTO(dt.id, dt.defaultName, dt.status) FROM DiscoverTemplate dt where status <> 'D'"),
})
public class DiscoverTemplate extends Template {
    @ChangesInspection(type = InspectionType.NONE)
    @OneToMany(mappedBy = "template", fetch = FetchType.LAZY)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Set<WDTag> tags = new LinkedHashSet<WDTag>();

    public DiscoverTemplate() {
    }

    public DiscoverTemplate(Long id) {
        super(id);
    }

    public Set<WDTag> getTags() {
        return new ChangesSupportSet<WDTag>(this, "tags", tags);
    }

    public void setTags(Set<WDTag> tags) {
        this.tags = tags;
        this.registerChange("tags");
    }

    @Override
    public String toString() {
        return "com.foros.model.template.DiscoverTemplate[id=" + getId() + "]";
    }
}

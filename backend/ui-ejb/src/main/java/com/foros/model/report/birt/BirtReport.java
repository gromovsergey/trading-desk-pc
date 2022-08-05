package com.foros.model.report.birt;

import com.foros.model.IdNameEntity;
import com.foros.model.VersionEntityBase;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "BIRTREPORT")
@NamedQueries({
@NamedQuery(name = "BirtReport.getIndex", query = "SELECT NEW com.foros.session.birt.BirtReportTO(c.id, c.name) FROM BirtReport c WHERE c.type = com.foros.model.report.birt.BirtReportType.DEFAULT ORDER BY upper(c.name)")
})
public class BirtReport extends VersionEntityBase implements IdNameEntity {
    @SequenceGenerator(name = "BirtReportGen", sequenceName = "BIRTREPORT_BIRT_REPORT_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BirtReportGen")
    @Column(name = "BIRT_REPORT_ID", nullable = false)
    private Long id;

    @Column(name = "NAME", nullable = false)
    @NameConstraint
    @RequiredConstraint
    private String name;

    @Column(name = "INSTANCE_CACHE_TIME")
    private Long instanceCacheTime;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "BIRT_REPORT_TYPE_ID", nullable = false)
    private BirtReportType type = BirtReportType.DEFAULT;

    /**
     * Creates a new instance of Birt Report.
     */
    public BirtReport(){
    }

    /**
     * Gets the id of this Birt Report.
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the id of this Birt Report to the specified value.
     *
     * @param id new id
     */
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    /**
     * Gets the name of this Birt Report.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of this Birt Report to the specified value.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    public String getTemplateFile() {
        return "rpt_" + id + ".rptdesign";
    }

    public Long getInstanceCacheTime() {
        return instanceCacheTime;
    }

    public void setInstanceCacheTime(Long instanceStorageLife) {
        this.instanceCacheTime = instanceStorageLife;
    }


    public boolean isCachable() {
        return instanceCacheTime != null;
    }

    public BirtReportType getType() {
        return type;
    }

    public void setType(BirtReportType type) {
        this.type = type;
        this.registerChange("type");
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
        if (!(object instanceof BirtReport)) {
            return false;
        }
        BirtReport other = (BirtReport)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.report.custom.BirtReport[id=" + getId() + "]";
    }
}

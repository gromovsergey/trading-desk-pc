package com.foros.model.template;

import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.jaxb.adapters.CreativeSizeXmlAdapter;
import com.foros.model.Identifiable;
import com.foros.model.VersionEntityBase;
import com.foros.model.creative.CreativeSize;
import com.foros.util.FlagsUtil;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.ObjectUtils;

@Entity
@Table(name = "TEMPLATEFILE")
@NamedQueries({
    @NamedQuery(name = "CreativeTemplateFile.findByCreativeTemplFileId", query = "SELECT c FROM TemplateFile c WHERE c.id = :id"),
    @NamedQuery(name = "CreativeTemplateFile.findByTemplateFile", query = "SELECT c FROM TemplateFile c WHERE c.templateFile = :templateFile"),
    @NamedQuery(name = "CreativeTemplateFile.findByFlags", query = "SELECT c FROM TemplateFile c WHERE c.flags = :flags")
})
@XmlType(propOrder = {
        "id",
        "templateFile",
        "impressionsTrackFlag",
        "applicationFormat",
        "creativeSize",
        "type"
})
@XmlAccessorType(XmlAccessType.NONE)
public class TemplateFile extends VersionEntityBase implements Identifiable {
    public static long IMPRESSIONS_TRACK = 0x01;

    @IdConstraint
    @SequenceGenerator(name = "TemplateFileGen", sequenceName = "TEMPLATEFILE_TEMPLATE_FILE_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TemplateFileGen")
    @Column(name = "TEMPLATE_FILE_ID", nullable = false)
    @CopyPolicy(strategy = CopyStrategy.EXCLUDE)
    private Long id;

    @RequiredConstraint
    @StringSizeConstraint(size = 1024)
    @Column(name = "TEMPLATE_FILE", nullable = false)
    private String templateFile;
    
    @Column(name = "FLAGS")
    private Long flags = 0L;

    @HasIdConstraint
    @JoinColumn(name = "APP_FORMAT_ID", referencedColumnName = "APP_FORMAT_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private ApplicationFormat applicationFormat;

    @HasIdConstraint
    @JoinColumn(name = "SIZE_ID", referencedColumnName = "SIZE_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private CreativeSize creativeSize;
    
    @HasIdConstraint
    @JoinColumn(name = "TEMPLATE_ID", referencedColumnName = "TEMPLATE_ID", updatable = false)
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private Template template;
    
    @Column(name = "TEMPLATE_TYPE", nullable = false)
    private char type = 'T';

    public TemplateFile() {
    }

    public TemplateFile(Long id) {
        this.id = id;
    }

    public TemplateFile(Long id, String templateFile) {
        this.id = id;
        this.templateFile = templateFile;
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
    public String getTemplateFile() {
        return this.templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
        this.registerChange("templateFile");
    }

    public Long getFlags() {
        return this.flags;
    }

    public void setFlags(Long flags) {
        this.flags = flags;
        this.registerChange("flags");
    }

    @XmlElement
    public ApplicationFormat getApplicationFormat() {
        return this.applicationFormat;
    }

    public void setApplicationFormat(ApplicationFormat applicationFormat) {
        this.applicationFormat = applicationFormat;
        this.registerChange("applicationFormat");
    }

    @XmlElement
    @XmlJavaTypeAdapter(CreativeSizeXmlAdapter.class)
    public CreativeSize getCreativeSize() {
        return this.creativeSize;
    }

    public void setCreativeSize(CreativeSize creativeSize) {
        this.creativeSize = creativeSize;
        this.registerChange("creativeSize");
    }

    public Template getTemplate() {
        return this.template;
    }

    public void setTemplate(Template template) {
        this.template = template;
        this.registerChange("template");
    }

    @XmlElement
    public TemplateFileType getType() {
        return TemplateFileType.valueOf(type);
    }

    public void setType(TemplateFileType type) {
        this.type = type.getLetter();
        this.registerChange("type");
    }

    public String getFileDescription() {
        return "(" + (creativeSize == null ? "NULL" : creativeSize.getName()) + ", " + (applicationFormat == null ? "NULL" : applicationFormat.getName()) + ")";
    }

    public void setImpressionsTrackFlag(boolean flag) {
        setFlags(FlagsUtil.set(flags, IMPRESSIONS_TRACK, flag));
    }

    @XmlElement(name = "impressionsTrackFlag")
    public boolean isImpressionsTrackFlag() {
        return FlagsUtil.get(flags, IMPRESSIONS_TRACK);
    }

    public boolean compareFields(TemplateFile other) {
        if (applicationFormat != null ? !applicationFormat.equals(other.applicationFormat) : other.applicationFormat != null) {
            return false;
        }
        if (creativeSize != null ? !creativeSize.equals(other.creativeSize) : other.creativeSize != null) {
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

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TemplateFile)) {
            return false;
        }

        TemplateFile other = (TemplateFile)object;

        if (!ObjectUtils.equals(id, other.id) || id == null) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.template.TemplateFile[id=" + getId() + "]";
    }
}

package com.foros.session.site.creativeApproval;

import com.foros.jaxb.adapters.TimestampXmlAdapter;
import com.foros.session.bulk.IdNameTO;
import com.foros.session.site.CreativeCategoryRecType;

import java.sql.Timestamp;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlType(propOrder = {
        "id",
        "destinationUrl",
        "previewUrl",
        "version",
        "size",
        "visualCategories",
        "contentCategories"
})
public class CreativeForApprovalTO {
    private Long id;
    private String destinationUrl;
    private String previewUrl;
    private Timestamp version;
    private IdNameTO size;
    private Long templateId;
    private List<CreativeCategoryRecType> visualCategories;
    private List<CreativeCategoryRecType> contentCategories;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDestinationUrl() {
        return destinationUrl;
    }

    public void setDestinationUrl(String destinationUrl) {
        this.destinationUrl = destinationUrl;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    @XmlElement(name = "updated")
    @XmlJavaTypeAdapter(TimestampXmlAdapter.class)
    public Timestamp getVersion() {
        return version;
    }

    public void setVersion(Timestamp version) {
        this.version = version;
    }

    public IdNameTO getSize() {
        return size;
    }

    public void setSize(IdNameTO size) {
        this.size = size;
    }

    public Long getSizeId() {
        return size == null ? null : size.getId();
    }

    @XmlElement(name = "category")
    @XmlElementWrapper(name = "visualCategories")
    public List<CreativeCategoryRecType> getVisualCategories() {
        return visualCategories;
    }

    public void setVisualCategories(List<CreativeCategoryRecType> visualCategories) {
        this.visualCategories = visualCategories;
    }

    @XmlElement(name = "category")
    @XmlElementWrapper(name = "contentCategories")
    public List<CreativeCategoryRecType> getContentCategories() {
        return contentCategories;
    }

    public void setContentCategories(List<CreativeCategoryRecType> contentCategories) {
        this.contentCategories = contentCategories;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public long getTemplateId() {
        return templateId;
    }
}

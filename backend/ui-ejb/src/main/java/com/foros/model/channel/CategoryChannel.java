package com.foros.model.channel;

import com.foros.annotations.AllowedStatuses;
import com.foros.model.LocalizableName;
import com.foros.model.Status;
import com.foros.model.security.NotManagedEntity;
import com.foros.util.StringUtil;

import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@Entity
@DiscriminatorValue("C")
@AllowedStatuses(values = {Status.ACTIVE, Status.INACTIVE, Status.DELETED})
@XmlType
public class CategoryChannel extends Channel implements NotManagedEntity{
    public final static long HIDDEN = 0x04;

    @Column(name = "NEWSGATE_CATEGORY_NAME")
    private String newsgateCategoryName;

    @Column(name = "PARENT_CHANNEL_ID")
    private Long parentChannelId;

    @Override
    protected ChannelNamespace calculateNamespace() {
        return ChannelNamespace.CATEGORY;
    }

    public CategoryChannel() {
    }

    public LocalizableName getLocalizableName() {
        return new LocalizableName(getName(), "CategoryChannel." + getId());
    }

    public String getNewsgateCategoryName() {
        return newsgateCategoryName;
    }

    public void setNewsgateCategoryName(String newsgateCategoryName) {
        this.newsgateCategoryName = newsgateCategoryName;
        this.registerChange("newsgateCategoryName");
    }

    public Long getParentChannelId() {
        return parentChannelId;
    }

    public void setParentChannelId(Long parentChannelId) {
        this.parentChannelId = parentChannelId;
        this.registerChange("parentChannelId");
    }

    public boolean getIsHiddenChannel() {
        return (getFlags() & CategoryChannel.HIDDEN) == 0;
    }

    public void setIsHiddenChannel(boolean isHidden) {
        if (isHidden) {
            setFlags(getFlags() & (~CategoryChannel.HIDDEN));
        } else {
            setFlags(getFlags() | CategoryChannel.HIDDEN);
        }
    }

    public static String getResoureKey(String id) {
        return StringUtil.isPropertyEmpty(id) ? null : "CategoryChannel." + id;    
    }

    @Override
    @XmlTransient
    public Set<CategoryChannel> getCategories() {
        throw new UnsupportedOperationException("Category channels do not support categories");
    }

    @Override
    public void setCategories(Set<CategoryChannel> categories) {
        throw new UnsupportedOperationException("Category channels do not support categories");
    }

    @Override
    public String getChannelType() {
        return CHANNEL_TYPE_CATEGORY;
    }

}

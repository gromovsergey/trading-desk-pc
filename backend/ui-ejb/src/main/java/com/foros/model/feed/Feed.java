package com.foros.model.feed;

import com.foros.annotations.Audit;
import com.foros.annotations.ChangesInspection;
import com.foros.annotations.InspectionType;
import com.foros.audit.serialize.serializer.entity.FeedAuditSerializer;
import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import org.apache.commons.lang.ObjectUtils;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@Table(name = "FEED")
@Audit(serializer = FeedAuditSerializer.class)
public class Feed extends EntityBase implements Identifiable {

    @SequenceGenerator(name = "FeedGen", sequenceName = "FEED_FEED_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "FeedGen")
    @Column(name = "FEED_ID", nullable = false)
    private Long id;

    @ChangesInspection(type = InspectionType.NONE)
    @Column(name = "URL")
    private String url;

    public Feed() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
        this.registerChange("url");
    }

    @Override
    public String toString() {
        return "Feed[" + id + ", url=" + url + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Feed)) {
            return false;
        }

        Feed that = (Feed)o;
        if (this.getId() != null && that.getId() != null) {
            if (this.getId().equals(that.getId())) {
                return true;
            }
            return false;
        }

        if (!ObjectUtils.equals(this.getUrl(), that.getUrl())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}

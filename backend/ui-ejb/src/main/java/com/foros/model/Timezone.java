package com.foros.model;

import java.util.TimeZone;
import javax.persistence.*;
import java.io.Serializable;
import com.foros.annotations.Audit;
import com.foros.audit.serialize.serializer.entity.TimezoneAuditSerializer;

/**
 * Timezone
 *
 * @author Boris Vanin
 */
@Entity
@Table(name = "TIMEZONE")
@NamedQueries({
  @NamedQuery(name = "Timezone.findById", query = "SELECT tz FROM Timezone tz WHERE tz.id = :id"),
  @NamedQuery(name = "Timezone.findAll", query = "SELECT tz FROM Timezone tz")
})
@Audit(serializer = TimezoneAuditSerializer.class)
public class Timezone extends EntityBase implements Serializable, Identifiable {

    @Id
    @Column(name = "TIMEZONE_ID", nullable = false)
    private Long id;

    @Column(name = "TZNAME")
    private String key;

    public Timezone() {
    }

    public Timezone(Long id, String key) {
        this.id = id;
        this.key = key;
    }

    /**
     * @return timezone id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id timezone id
     */
    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    /**
     * @return key of timezone
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key key of timezone
     */
    public void setKey(String key) {
        this.key = key;
        this.registerChange("key");
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getKey() != null ? this.getKey().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if(this == object){
            return true;
        }

        if (!(object instanceof Timezone)) {
            return false;
        }

        Timezone other = (Timezone)object;

        if (this.getKey() == null || other.getKey() == null) {
            return false;
        }
        
        return this.getKey().equals(other.getKey());
    }

    @Override
    public String toString() {
        return "com.foros.model.Timezone[id=" + getId() + ", key=" + getKey() + "]";
    }

    public TimeZone toTimeZone() {
        return key == null ? null : TimeZone.getTimeZone(key);
    }
}
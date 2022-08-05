package com.foros.model.admin;

import com.foros.annotations.Audit;
import com.foros.changes.inspection.ChangeNode;
import com.foros.util.HashUtil;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import org.apache.commons.lang.ObjectUtils;

@Audit(nodeFactory = ChangeNode.NullFactory.class)
@Entity
@Table(name = "DYNAMICRESOURCES")
@NamedQueries( {
    @NamedQuery(name = "DynamicResource.findByKey", query = "SELECT r FROM DynamicResource r where r.key = :key"),
    @NamedQuery(name = "DynamicResource.findByLang",
            query = "SELECT r FROM DynamicResource r where lang = :lang")
})
@IdClass(DynamicResourceId.class)
public class DynamicResource {
    @Id
    @Column(nullable = false)
    private String key;

    @Column(nullable = false)
    private String value;

    @Id
    @Column(nullable = false)
    private String lang;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {


        this.lang = lang;
    }

    @Override
    public int hashCode() {
        return HashUtil.calculateHash(key, lang);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof DynamicResource)) {
            return false;
        }

        DynamicResource other = (DynamicResource) obj;

        if (!ObjectUtils.equals(this.getKey(), other.getKey())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getLang(), other.getLang())) {
            return false;
        }

        return true;
    }


}

package com.foros.model.admin;

import com.foros.model.VersionEntityBase;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;

/**
 *
 * @author alexey_koloskov
 */
@Entity
@Table(name = "ADSCONFIG")
@NamedQueries({
        @NamedQuery(name = "GlobalParam.findByValue", query = "select gp from GlobalParam gp where gp.value = :value")
})
public class GlobalParam extends VersionEntityBase implements Serializable {
    @Id
    @Column(name = "PARAM_NAME", nullable = false, length = 100)
    private String name;
    
    @Column(name = "PARAM_VALUE", length = 1000)
    private String value;

    public GlobalParam() {
    }

    public GlobalParam(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.registerChange("value");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        
        if (!(o instanceof GlobalParam)) {
            return false;
        }

        GlobalParam other = (GlobalParam)o;

        if (!ObjectUtils.equals(this.getName(), other.getName())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getValue(), other.getValue())) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = (getName() != null ? getName().hashCode() : 0);
        result = 31 * result + (getValue() != null ? getValue().hashCode() : 0);
        
        return result;
    }

    @Override
    public String toString() {
        return "com.foros.model.admin.@Override[name=" + getName() + "]";
    }
}

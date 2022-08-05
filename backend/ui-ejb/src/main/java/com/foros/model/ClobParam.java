package com.foros.model;


import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.io.Serializable;

import com.foros.util.HashUtil;
import org.apache.commons.lang.ObjectUtils;
import org.hibernate.annotations.Type;

@Entity
@Table(name = "CLOBPARAMS")
public class ClobParam extends VersionEntityBase implements Serializable {
    @EmbeddedId
    private ClobParamPK id;
    
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    @Column(name = "VALUE")
    private String value;

    public ClobParam() {
    }

    public ClobParam(ClobParamPK id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.registerChange("value");
    }

    @Override
    public int hashCode() {
        return HashUtil.calculateHash(id);
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ClobParam)) {
            return false;
        }

        ClobParam other = (ClobParam)object;
        
        if (!ObjectUtils.equals(this.getId(), other.getId()) ) {
            return false;
        }
        
        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.ClobParam[id=" + getId() + ", name=" + id.getType() + "]";
    }

    public ClobParamPK getId() {
        return id;
    }

    public void setId(ClobParamPK id) {
        this.id = id;
        this.registerChange("id");
    }
}

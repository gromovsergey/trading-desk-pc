package com.foros.model;

import com.foros.util.HashUtil;
import org.apache.commons.lang.ObjectUtils;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class ClobParamPK implements Serializable {
    @Column(name="ACCOUNT_ID", nullable = false)
    private Long accountId;

    @Column(name= "NAME", nullable = false)
    private String type;

    public ClobParamPK() {
    }

    public ClobParamPK(Long accountId, ClobParamType type) {
        this.accountId = accountId;
        this.type = type.name();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public int hashCode() {
        return HashUtil.calculateHash(accountId, type);
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ClobParamPK)) {
            return false;
        }

        ClobParamPK other = (ClobParamPK) object;

        if (!ObjectUtils.equals(this.getAccountId(), other.getAccountId())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getType(), other.getType())) {
            return false;
        }

        return true;
    }
}

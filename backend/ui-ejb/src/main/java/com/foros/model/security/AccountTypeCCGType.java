package com.foros.model.security;

import com.foros.model.EntityBase;
import com.foros.model.Identifiable;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

import org.apache.commons.lang.ObjectUtils;

/**
 * @author Vitaliy_Knyazev
 */
@Entity
@Table(name = "ACCOUNTTYPE_CCGTYPE")
public class AccountTypeCCGType extends EntityBase implements Serializable, Identifiable {
    @Id
    @SequenceGenerator(name = "AccountTypeCcgTypeGen", sequenceName = "ACCOUNTTYPE_CCGTYPE_ACCOUNTTYPE_CCGTYPE_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccountTypeCcgTypeGen")
    @Column(name = "ACCOUNTTYPE_CCGTYPE_ID", nullable = false)
    private Long id;

    @JoinColumn(name = "ACCOUNT_TYPE_ID", referencedColumnName = "ACCOUNT_TYPE_ID", nullable = false)
    @ManyToOne
    private AccountType accountType;

    @Column(name = "CCG_TYPE", nullable = false)
    private char ccgType;

    @Column(name = "TGT_TYPE", nullable = false)
    private char tgtType;

    @Column(name = "RATE_TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private RateType rateType;
    
    public AccountTypeCCGType() { 
    }

    public AccountTypeCCGType(AccountType accountType, CCGType ccgType, TGTType tgtType, RateType rateType) {
        this.accountType = accountType;
        this.ccgType = ccgType.getLetter();
        this.tgtType = tgtType.getLetter();
        this.rateType = rateType;
    }

    @XmlTransient
    public AccountType getAccountType() {
        return accountType;
    }

    public void setAccountType(AccountType accountType) {
        this.accountType = accountType;
        this.registerChange("accountType");
    }

    public CCGType getCcgType() {
        return CCGType.valueOf(ccgType);
    }

    public void setCcgType(CCGType ccgType) {
        this.ccgType = ccgType.getLetter();
        this.registerChange("ccgType");
    }

    public TGTType getTgtType() {
        return TGTType.valueOf(tgtType);
    }

    public void setTgtType(TGTType tgtType) {
        this.tgtType = tgtType.getLetter();
        this.registerChange("tgtType");
    }

    public RateType getRateType() {
        return rateType;
    }

    public void setRateType(RateType rateType) {
        this.rateType = rateType;
        this.registerChange("rateType");
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!(object instanceof AccountTypeCCGType)) {
            return false;
        }

        final AccountTypeCCGType other = (AccountTypeCCGType) object;
        if (!ObjectUtils.equals(this.getId(), other.getId())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getCcgType(), other.getCcgType())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getTgtType(), other.getTgtType())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getRateType(), other.getRateType())) {
            return false;
        }

        if (!ObjectUtils.equals(this.getAccountType(), other.getAccountType())) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "com.foros.model.security.AccountTypeCCGType[" +
            "ACCOUNT_TYPE_ID=" + getAccountType().getId() +
            ",CCG_TYPE=" + getCcgType() +
            ",TGT_TYPE=" + getTgtType() +
            ",RATE_TYPE=" + getRateType().getName() +
            "]";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }
}

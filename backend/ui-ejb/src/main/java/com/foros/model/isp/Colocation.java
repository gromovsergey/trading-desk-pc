package com.foros.model.isp;

import com.foros.annotations.AllowedStatuses;
import com.foros.annotations.CopyPolicy;
import com.foros.annotations.CopyStrategy;
import com.foros.jaxb.adapters.AccountLinkXmlAdapter;
import com.foros.model.DisplayStatus;
import com.foros.model.DisplayStatusEntity;
import com.foros.model.IdNameEntity;
import com.foros.model.Status;
import com.foros.model.StatusEntityBase;
import com.foros.model.account.IspAccount;
import com.foros.model.security.OwnedStatusable;
import com.foros.validation.annotation.CascadeValidation;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.RequiredConstraint;

import java.io.Serializable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@Entity
@Table(name = "COLOCATION")
@NamedQueries({
  @NamedQuery(name = "Colocation.findById", query = "SELECT c FROM Colocation c WHERE c.id = :id"),
  @NamedQuery(name = "Colocation.findByName", query = "SELECT c FROM Colocation c WHERE c.name = :name"),
  @NamedQuery(name = "Colocation.findByRevenueShare", query = "SELECT c FROM Colocation c WHERE c.colocationRate.revenueShare = :revenueShare"),
  @NamedQuery(name = "Colocation.entityTO.findByAccountId", query = "SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM Colocation c WHERE c.account.id = :accountId"),
  @NamedQuery(name = "Colocation.entityTO.findNonDeletedByAccountId", query = "SELECT NEW com.foros.session.EntityTO(c.id, c.name, c.status) FROM Colocation c WHERE c.account.id = :accountId and c.status <> 'D'")
})
@AllowedStatuses(values = {Status.ACTIVE, Status.DELETED})
@XmlType(propOrder = {
        "id",
        "name",
        "optOutServing",
        "account",
        "colocationRate"
})
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement(name = "colocation")
public class Colocation extends StatusEntityBase implements Serializable, DisplayStatusEntity, OwnedStatusable<IspAccount>, IdNameEntity {
    @SequenceGenerator(name = "ColocationGen", sequenceName = "COLOCATION_COLO_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ColocationGen")
    @Column(name = "COLO_ID", nullable = false)
    @IdConstraint
    private Long id;
    
    @Column(name = "NAME", nullable = false)
    @RequiredConstraint
    @NameConstraint
    private String name;

    @Column(name= "OPTOUT_SERVING")
    @Enumerated(EnumType.STRING)
    private ColocationOptOutServingType optOutServing = ColocationOptOutServingType.NON_OPTOUT;

    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")
    @ManyToOne
    @CopyPolicy(strategy = CopyStrategy.SHALLOW)
    private IspAccount account;
    
    @JoinColumn(name = "COLO_RATE_ID", referencedColumnName = "COLO_RATE_ID")
    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH})
    @CascadeValidation
    private ColocationRate colocationRate;

    public static final DisplayStatus LIVE = new DisplayStatus(1L, DisplayStatus.Major.LIVE, "colocation.displaystatus.live");
    public static final DisplayStatus DELETED = new DisplayStatus(2L, DisplayStatus.Major.DELETED, "colocation.displaystatus.deleted");

    public Colocation() {
    }

    @XmlElement
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    @XmlElement
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    @XmlElement(name = "adServingMode")
    public ColocationOptOutServingType getOptOutServing() {
        return optOutServing;
    }

    public void setOptOutServing(ColocationOptOutServingType optOutServing) {
        this.optOutServing = optOutServing;
        this.registerChange("optOutServing");
    }

    @Override
    @XmlElement
    @XmlJavaTypeAdapter(AccountLinkXmlAdapter.class)
    public IspAccount getAccount() {
        return this.account;
    }

    public void setAccount(IspAccount account) {
        this.account = account;
        this.registerChange("account");
    }

    @XmlElement(name = "rate")
    public ColocationRate getColocationRate() {
        return colocationRate;
    }

    public void setColocationRate(ColocationRate colocationRate) {
        this.colocationRate = colocationRate;
        this.registerChange("colocationRate");
    }

    public DisplayStatus getDisplayStatus() {
        return getDisplayStatus(getStatus());
    }

    public static DisplayStatus getDisplayStatus(Status status) {
        if (Status.ACTIVE.equals(status)) {
            return LIVE;
        } else if (Status.DELETED.equals(status)){
            return DELETED;
        } else {
            return null;
        }
    }

    @Override
    public Status getParentStatus() {
        return account.getInheritedStatus();
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Colocation)) {
            return false;
        }
        Colocation other = (Colocation)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.foros.model.isp.Colocation[id=" + getId() + "]";
    }

}

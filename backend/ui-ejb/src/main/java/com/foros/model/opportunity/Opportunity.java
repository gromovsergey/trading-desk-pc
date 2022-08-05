package com.foros.model.opportunity;

import com.foros.annotations.Audit;
import com.foros.changes.inspection.changeNode.custom.OpportunityEntityChange;
import com.foros.model.IdNameEntity;
import com.foros.model.VersionEntityBase;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.security.OwnedEntity;
import com.foros.validation.constraint.HasIdConstraint;
import com.foros.validation.constraint.IdConstraint;
import com.foros.validation.constraint.NameConstraint;
import com.foros.validation.constraint.NotNullConstraint;
import com.foros.validation.constraint.RequiredConstraint;
import com.foros.validation.constraint.StringSizeConstraint;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "INSERTIONORDER")
@NamedQueries({
        @NamedQuery(name = "Opportunity.findByAccountId",
                query = "SELECT o FROM Opportunity o" +
                        " WHERE o.account.id = :accountId" +
                        " ORDER BY o.amount desc")
})
@Audit(nodeFactory = OpportunityEntityChange.Factory.class)
public class Opportunity extends VersionEntityBase implements OwnedEntity<AdvertiserAccount>, Serializable, IdNameEntity {

    public static final BigDecimal AMOUNT_MAX = new BigDecimal("1000000000");

    @SequenceGenerator(name = "OpportunityGen", sequenceName = "INSERTIONORDER_IO_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OpportunityGen")
    @Column(name = "IO_ID", nullable = false)
    @IdConstraint
    private Long id;

    @NameConstraint
    @RequiredConstraint
    @Column(name = "NAME", nullable = false)
    private String name;

    @RequiredConstraint
    @Column(name = "AMOUNT", precision = 10, scale = 2)
    private BigDecimal amount;

    @NotNullConstraint
    @HasIdConstraint
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", updatable = false)
    @ManyToOne
    private AdvertiserAccount account;

    @StringSizeConstraint(size = 2000)
    @Column(name = "NOTES")
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(name = "PROBABILITY")
    @RequiredConstraint
    private Probability probability;

    @StringSizeConstraint(size = 50)
    @Column(name = "IO_NUMBER")
    private String ioNumber;

    @StringSizeConstraint(size = 50)
    @Column(name = "PO_NUMBER")
    private String poNumber;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
        this.registerChange("id");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.registerChange("name");
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
        this.registerChange("notes");
    }

    public Probability getProbability() {
        return probability;
    }

    public void setProbability(Probability probability) {
        this.probability = probability;
        this.registerChange("probability");
    }

    public String getIoNumber() {
        return ioNumber;
    }

    public void setIoNumber(String ioNumber) {
        this.ioNumber = ioNumber;
        this.registerChange("ioNumber");
    }

    public String getPoNumber() {
        return poNumber;
    }

    public void setPoNumber(String poNumber) {
        this.poNumber = poNumber;
        this.registerChange("poNumber");
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        this.registerChange("amount");
    }

    public AdvertiserAccount getAccount() {
        return account;
    }

    public void setAccount(AdvertiserAccount account) {
        this.account = account;
        this.registerChange("account");
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (this.getId() != null ? this.getId().hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Opportunity)) {
            return false;
        }

        Opportunity other = (Opportunity)object;
        if (this.getId() != other.getId() && (this.getId() == null || !this.getId().equals(other.getId()))) {
            return false;
        }
        return true;
    }
}

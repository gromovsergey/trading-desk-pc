package app.programmatic.ui.flight.dao.model;

import app.programmatic.ui.common.model.VersionEntityBase;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Entity
@Table(name = "INSERTIONORDER")
public class Opportunity extends VersionEntityBase<Long> {

    @SequenceGenerator(name = "OpportunityGen", sequenceName = "INSERTIONORDER_IO_ID_SEQ", allocationSize = 1)
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OpportunityGen")
    @Column(name = "IO_ID", updatable = false, nullable = false)
    private Long id;

    @OneToOne(mappedBy = "opportunity")
    private Flight flight;

    @NotNull
    @Size(min = 1, max = 200)
    @Column(name = "NAME", nullable = false)
    private String name;

    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    @DecimalMax(value = "1000000000", inclusive = false)
    @Digits(integer=14, fraction=5)
    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @NotNull
    @Column(name = "ACCOUNT_ID", updatable = false, nullable = false)
    private Long accountId;

    @Column(name = "NOTES", updatable = false)
    private String notes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "PROBABILITY", updatable = false, nullable = false)
    private Probability probability = Probability.IO_SIGNED;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "IO_NUMBER", nullable = false)
    private String ioNumber;

    @Column(name = "PO_NUMBER", updatable = false)
    private String poNumber;

    @Valid
    @OneToMany(mappedBy = "opportunity", fetch = FetchType.EAGER) // Logically this is 'one-to-one' association -> EAGER is acceptable
    private Set<CampaignAllocation> campaignAllocations = new LinkedHashSet<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getIoNumber() {
        return ioNumber;
    }

    public void setIoNumber(String ioNumber) {
        this.ioNumber = ioNumber;
    }

    public Set<CampaignAllocation> getCampaignAllocations() {
        return campaignAllocations;
    }

    public void setCampaignAllocations(Set<CampaignAllocation> campaignAllocations) {
        this.campaignAllocations = campaignAllocations;
    }
}

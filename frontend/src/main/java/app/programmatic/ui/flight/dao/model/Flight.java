package app.programmatic.ui.flight.dao.model;

import app.programmatic.ui.campaign.dao.model.CampaignDisplayStatus;
import app.programmatic.ui.common.model.MajorDisplayStatus;

import java.math.BigDecimal;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.Valid;


@Entity
@DiscriminatorValue("Flight")
public class Flight extends FlightBase {

    @Valid
    @NotNull
    @JoinColumn(name = "io_id", referencedColumnName = "io_id", nullable = false, updatable = false)
    @OneToOne(cascade = CascadeType.ALL)
    private Opportunity opportunity;

    @Transient
    private CampaignDisplayStatus displayStatus;


    public Opportunity getOpportunity() {
        return opportunity;
    }

    public void setOpportunity(Opportunity opportunity) {
        this.opportunity = opportunity;
    }

    @Override
    public BigDecimal getBudget() {
        return getOpportunity().getAmount();
    }

    @Override
    public void setBudget(BigDecimal budget) {
        getOpportunity().setAmount(budget);
        super.setBudget(budget);
    }

    @Override
    public String getName() {
        return opportunity.getName();
    }

    public CampaignDisplayStatus getDisplayStatus() {
        return displayStatus;
    }

    public void setDisplayStatus(CampaignDisplayStatus displayStatus) {
        this.displayStatus = displayStatus;
    }

    @Override
    public MajorDisplayStatus getMajorStatus() {
        return displayStatus == null ? null : displayStatus.getMajorStatus();
    }
}

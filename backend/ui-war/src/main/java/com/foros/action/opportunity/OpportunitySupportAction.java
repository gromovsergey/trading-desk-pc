package com.foros.action.opportunity;

import com.foros.action.BaseActionSupport;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.opportunity.Probability;
import com.foros.session.opportunity.OpportunityService;

import com.opensymphony.xwork2.ModelDriven;
import java.util.Collection;
import java.util.HashSet;
import javax.ejb.EJB;

public class OpportunitySupportAction extends BaseActionSupport implements ModelDriven<Opportunity> {

    @EJB
    protected OpportunityService opportunityService;

    protected Opportunity opportunity = newEntity();

    protected Collection<String> existingFiles;
    private Collection<Probability> availableProbabilities;

    public Collection<Probability> getAvailableProbabilities() {
        if (availableProbabilities == null) {
            availableProbabilities = opportunityService.getAvailableProbabilities(opportunity.getAccount().getId(), opportunity.getProbability());
        }
        return availableProbabilities;
    }

    @Override
    public Opportunity getModel() {
        return opportunity;
    }

    private Opportunity newEntity() {
        Opportunity opportunity = new Opportunity();
        opportunity.setAccount(new AdvertiserAccount());
        return opportunity;
    }

    public Collection<String> getExistingFiles() {
        if (existingFiles == null) {
            loadExistingFiles();
        }
        return existingFiles == null ? new HashSet<String>() : existingFiles;
    }

    public void setExistingFiles(Collection<String> existingFiles) {
        this.existingFiles = existingFiles;
    }

    protected void loadExistingFiles() {
        if (opportunity.getId() != null) {
            existingFiles = opportunityService.getIOFileNames(opportunity);
        }
    }
}

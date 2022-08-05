package com.foros.action.campaign;

import com.foros.action.BaseActionSupport;
import com.foros.session.campaign.CampaignService;

import java.util.Collection;
import javax.ejb.EJB;

public class BulkStatusCreativeGroupsAction extends BaseActionSupport {
    @EJB
    private CampaignService campaignService;

    private Long id;

    private Collection<Long> selectedGroups;

    private String declinationReason;

    public String activateGroups() {
        campaignService.activateGroups(id, selectedGroups);

        return SUCCESS;
    }

    public String inactivateGroups() {
        campaignService.inactivateGroups(id, selectedGroups);

        return SUCCESS;
    }

    public String approveGroups() {
        campaignService.approveGroups(id, selectedGroups);

        return SUCCESS;
    }

    public String declineGroups() {
        campaignService.declineGroups(id, selectedGroups, declinationReason);

        return SUCCESS;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Collection<Long> getSelectedGroups() {
        return selectedGroups;
    }

    public void setSelectedGroups(Collection<Long> selectedGroups) {
        this.selectedGroups = selectedGroups;
    }

    public String getDeclinationReason() {
        return declinationReason;
    }

    public void setDeclinationReason(String declinationReason) {
        this.declinationReason = declinationReason;
    }
}

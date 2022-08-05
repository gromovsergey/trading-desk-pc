package com.foros.action.campaign.campaignGroup;

import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AgencyAccount;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.util.AccountUtil;

import javax.ejb.EJB;

public class SaveTargetAction extends TargetSupportAction {

    @EJB
    private SearchChannelService channelService;

    public SaveTargetAction () {
        super();
        existingAccount = new AgencyAccount();
    }
    public String update() {
        prepare();
        groupService.updateTarget(group);
        return SUCCESS;
    }

    private void prepare() {
        existingAccount = (AdvertisingAccountBase) AccountUtil.extractAccount(existingAccount.getId());
        searchCriteria.populateConditionOfVisibility(existingAccount);
        if (group.getChannel().getId() != null) {
            CampaignCreativeGroup existingGroup = groupService.find(group.getId());
            group.setCampaign(existingGroup.getCampaign());
            group.setCcgType(existingGroup.getCcgType());
        }
    }
}

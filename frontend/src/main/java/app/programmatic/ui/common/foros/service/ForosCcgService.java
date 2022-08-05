package app.programmatic.ui.common.foros.service;

import com.foros.rs.client.service.CampaignCreativeGroupService;

public interface ForosCcgService {
    CampaignCreativeGroupService getCcgService();

    CampaignCreativeGroupService getAdminCcgService();
}

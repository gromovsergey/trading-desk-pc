package com.foros.action.xml.options;

import java.util.Collection;

import com.foros.session.EntityTO;

public class TextCampaignsXmlAction extends CampaignsXmlAction {

    @Override
    protected Collection<? extends EntityTO> getOptionsByAccount(Long accountId) {
        return getCampaignService().getTextCampaignsByAccount(accountId);
    }

}

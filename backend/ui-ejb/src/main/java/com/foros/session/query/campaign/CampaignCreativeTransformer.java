package com.foros.session.query.campaign;

import com.foros.model.ApproveStatus;
import com.foros.model.FrequencyCap;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.creative.Creative;
import com.foros.model.security.User;
import com.foros.session.query.AbstractEntityTransformer;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

public class CampaignCreativeTransformer extends AbstractEntityTransformer<CampaignCreative> {

    @Override
    protected CampaignCreative transform(Map<String, Object> values) {
        Long id = (Long) values.get("id");

        CampaignCreative cc = new CampaignCreative();
        cc.setId(id);
        cc.setCreativeGroup(new CampaignCreativeGroup((Long) values.get("groupId")));
        cc.setFrequencyCap((FrequencyCap) values.get("frequencyCap"));
        cc.setCreative(new Creative());
        cc.getCreative().setId((Long) values.get("creativeId"));
        cc.getCreative().setAccount(new AdvertiserAccount((Long)values.get("advertiserId")));
        cc.getCreative().setVersion((Timestamp) values.get("version"));
        cc.getCreative().setStatus(Status.valueOf((char) values.get("status")));
        cc.getCreative().setQaStatus(ApproveStatus.valueOf((char) values.get("qaStatus")));
        User qaUser = new User();
        qaUser.setId((Long) values.get("qaUserId"));
        cc.getCreative().setQaUser(qaUser);
        cc.getCreative().setQaDate((Date) values.get("qaDate"));
        cc.getCreative().setQaDescription((String) values.get("qaDescription"));

        return cc;
    }
}

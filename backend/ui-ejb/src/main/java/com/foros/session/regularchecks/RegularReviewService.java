package com.foros.session.regularchecks;

import com.foros.model.DisplayStatus;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.Channel;
import com.foros.session.query.PartialList;

import javax.ejb.Local;

/**
 * Service is responsible for querying channels and Campaign creative groups to be reviewed in recent time
 */
@Local
public interface RegularReviewService {

    public static final DisplayStatus[] CHANNEL_LIVE_STATUSES = new DisplayStatus[] {
            Channel.LIVE,
            Channel.LIVE_AMBER_PENDING_INACTIVATION,
            Channel.LIVE_CHANNELS_NEED_ATT,
            Channel.LIVE_PENDING_INACTIVATION,
            Channel.LIVE_TRIGGERS_NEED_ATT
    };

    public static DisplayStatus[] CCG_LIVE_STATUSES = {
            CampaignCreativeGroup.LIVE,
            CampaignCreativeGroup.LIVE_CHANNEL_TARGET_NEED_ATT,
            CampaignCreativeGroup.LIVE_KEYWORDS_NEED_ATT,
            CampaignCreativeGroup.LIVE_LINKED_CREATIVE_NEED_ATT,
            CampaignCreativeGroup.LIVE_LINKED_CREATIVES_AND_KW_NEED_ATT
    };

    PartialList<ReviewEntityTO> searchChannelsForReview(String countryCode, int firstRow, int maxResults);

    PartialList<ReviewEntityTO> searchCCGsForReview(String countryCode, int firstRow, int maxResults);

    void updateChannelCheck(Channel channel);

    void updateCCGCheck(CampaignCreativeGroup ccg);

}

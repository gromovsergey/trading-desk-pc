package com.foros.session.campaign;

import com.foros.model.campaign.CampaignCreativeGroup;

import java.util.Comparator;

import org.apache.commons.lang.ObjectUtils;

public class ChannelTargetComparator implements Comparator<CampaignCreativeGroup> {

    @Override
    public int compare(CampaignCreativeGroup o1, CampaignCreativeGroup o2) {
        if ((o1 == null) && (o2 == null))
            return 0;
        if ((o1 == null) || (o2 == null))
            return -1;

        if (!ObjectUtils.equals(o1.getChannelTarget(), o2.getChannelTarget())) {
            return -1;
        }

        Long ch1 = o1.getChannel() != null? o1.getChannel().getId(): null;
        Long ch2 = o2.getChannel() != null? o2.getChannel().getId(): null;

        return ObjectUtils.equals(ch1, ch2)? 0: -1;
    }
}

package com.foros.session.channel;

import com.foros.session.NamedTO;

import java.util.List;

public class PopulatedBehavioralChannelMatchInfo extends AbstractChannelMatchInfo {
    private List<NamedTO> ccgs;

    public PopulatedBehavioralChannelMatchInfo(NamedTO channel, List<PopulatedTriggerInfo> triggers, List<NamedTO> ccgs) {
        super(channel, triggers);
        this.ccgs = ccgs;
    }

    public List<NamedTO> getCcgs() {
        return ccgs;
    }

    public List<PopulatedTriggerInfo> getUrlKeywordTriggers() {
        return urlKeywordTriggers;
    }
}

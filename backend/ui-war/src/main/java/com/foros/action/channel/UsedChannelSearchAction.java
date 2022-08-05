package com.foros.action.channel;

import com.foros.framework.ReadOnly;
import com.foros.session.channel.ChannelCCGUsedTO;

import java.util.Collection;

public class UsedChannelSearchAction  extends ChannelSearchSupportAction {

    private Collection<ChannelCCGUsedTO> usedChannels;

    @ReadOnly
    public String searchUsedChannels() {
        prepare();
        usedChannels = channelService.findAccountCCGUsedChannels(account.getId(), country.getCountryCode());
        return SUCCESS;
    }

    public Collection<ChannelCCGUsedTO> getUsedChannels() {
        return usedChannels;
    }
}

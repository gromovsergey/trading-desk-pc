package com.foros.action.admin.discoverChannelList;

import com.foros.model.channel.DiscoverChannel;
import com.foros.util.UITimestamp;

import java.util.LinkedList;
import java.util.List;

public class EditDiscoverChannelListActionBase extends DiscoverChannelListActionSupport {
    private boolean existingContainsURLs = false;
    private List<DiscoverChannel> existingChannels = new LinkedList<DiscoverChannel>();

    void populateAlreadyExistingChannels() {
        if (existingChannels.size() > 0) {
            boolean containsURLs = false;
            for (int i = 0; i < existingChannels.size(); i++) {
                DiscoverChannel existingChannel = discoverChannelService.view(Long.valueOf(existingChannels.get(i).getId()));
                existingChannels.set(i, existingChannel);
                existingChannel.setVersion(new UITimestamp(existingChannel.getVersion()));
                if (!existingChannel.getUrls().isEmpty()) {
                    containsURLs = true;
                }
            }
            setExistingContainsURLs(containsURLs);
        }
    }

    public void setExistingContainsURLs(boolean existingContainsURLs) {
        this.existingContainsURLs = existingContainsURLs;
    }

    @SuppressWarnings("UnusedDeclaration")
    public boolean isExistingContainsURLs() {
        return existingContainsURLs;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<DiscoverChannel> getExistingChannels() {
        return existingChannels;
    }

    public void setExistingChannels(List<DiscoverChannel> existingChannels) {
        this.existingChannels = existingChannels;
    }
}

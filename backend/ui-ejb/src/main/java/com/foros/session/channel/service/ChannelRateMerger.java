package com.foros.session.channel.service;

import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelRate;

import java.util.Date;
import javax.persistence.EntityManager;

public abstract class ChannelRateMerger {

    public void merge(Channel channel, Channel existing, boolean mergeToExisting) {
        if (!channel.isChanged("channelRate")) {
            return;
        }

        ChannelRate existingRate = existing.getChannelRate();
        ChannelRate newRate = channel.getChannelRate();
        Channel channelToMerge = mergeToExisting? existing: channel;

        if (!equals(existingRate, newRate)) {
            if (newRate != null) {
                newRate.setEffectiveDate(new Date(System.currentTimeMillis()));
                newRate.setChannel(channelToMerge);
                newRate.setCurrency(existing.getAccount().getCurrency());
                getEm().persist(newRate);
            }

            channelToMerge.setChannelRate(newRate);
        } else {
            channelToMerge.setChannelRate(existingRate);
        }
        if (mergeToExisting) {
            channel.unregisterChange("channelRate");
        }
    }

    private boolean equals(ChannelRate existingRate, ChannelRate newRate) {
        if (existingRate == newRate) {
            return true;
        }

        if (existingRate == null) {
            return false;
        }

        return existingRate.getRate().compareTo(newRate.getRate()) == 0;
    }

    public abstract EntityManager getEm();
}

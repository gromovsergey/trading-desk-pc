package com.foros.session.channel.service;

import com.foros.model.account.Account;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.KeywordChannel;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.admin.categoryChannel.CategoryChannelRestrictions;
import com.foros.session.channel.geo.GeoChannelRestrictions;

import com.foros.session.restriction.EntityRestrictions;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Restrictions
public class ChannelRestrictions {

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @EJB
    private DeviceChannelRestrictions deviceChannelRestrictions;

    @EJB
    private DiscoverChannelRestrictions discoverChannelRestrictions;

    @EJB
    private GeoChannelRestrictions geoChannelRestrictions;

    @EJB
    private CategoryChannelRestrictions categoryChannelRestrictions;

    @EJB
    private KeywordChannelRestrictions keywordChannelRestrictions;

    @EJB
    private CurrentUserService currentUserService;

    @Restriction
    public boolean canViewCategories(Channel channel) {
        return currentUserService.isInternal() &&
                (channel instanceof BehavioralChannel || channel instanceof KeywordChannel ||
                        channel instanceof DiscoverChannel || channel instanceof DiscoverChannelList);
    }

    @Restriction
    public boolean canView(Channel channel) {
        if (channel instanceof BehavioralChannel || channel instanceof ExpressionChannel || channel instanceof AudienceChannel) {
            return advertisingChannelRestrictions.canView(channel);
        } else if (channel instanceof CategoryChannel) {
            return categoryChannelRestrictions.canView();
        } else if (channel instanceof DeviceChannel) {
            return deviceChannelRestrictions.canView();
        } else if (channel instanceof DiscoverChannel) {
            return discoverChannelRestrictions.canView((DiscoverChannel)channel);
        } else if (channel instanceof DiscoverChannelList) {
            return discoverChannelRestrictions.canView((DiscoverChannelList)channel);
        } else if (channel instanceof KeywordChannel) {
            return keywordChannelRestrictions.canView((KeywordChannel)channel);
        } else if (channel instanceof GeoChannel) {
            return geoChannelRestrictions.canView();
        }
        return false;
    }

    @Restriction
    public boolean canUpdate(Channel channel) {
        if (channel instanceof BehavioralChannel || channel instanceof ExpressionChannel) {
            return advertisingChannelRestrictions.canUpdate(channel);
        } else if (channel instanceof CategoryChannel) {
            return categoryChannelRestrictions.canUpdate((CategoryChannel) channel);
        } else if (channel instanceof DeviceChannel) {
            return deviceChannelRestrictions.canUpdate((DeviceChannel) channel);
        } else if (channel instanceof DiscoverChannel) {
            return discoverChannelRestrictions.canUpdate((DiscoverChannel) channel);
        } else if (channel instanceof DiscoverChannelList) {
            return discoverChannelRestrictions.canUpdate((DiscoverChannelList) channel);
        } else if (channel instanceof KeywordChannel) {
            return keywordChannelRestrictions.canUpdate((KeywordChannel) channel);
        }
        return false;
    }

    @Restriction
    public boolean canEditCategories(Channel channel) {
        if (channel instanceof BehavioralChannel || channel instanceof KeywordChannel) {
            return canUpdate(channel)
                   && categoryChannelRestrictions.canView();
        } else if (channel instanceof DiscoverChannel) {
            return discoverChannelRestrictions.canEditCategories((DiscoverChannel) channel);
        } else if (channel instanceof DiscoverChannelList) {
            return discoverChannelRestrictions.canEditCategories((DiscoverChannelList) channel);
        }
        return false;
    }

    @Restriction
    public boolean canFindBehavioralDiscoverChannels(Account account) {
        return currentUserService.inRole(AccountRole.INTERNAL, AccountRole.ADVERTISER, AccountRole.AGENCY, AccountRole.CMP)
                && entityRestrictions.canView(account);
    }
}

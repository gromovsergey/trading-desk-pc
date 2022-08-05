package com.foros.action.channel;

import com.foros.action.admin.categoryChannel.CategoryChannelBreadcrumbsElement;
import com.foros.action.admin.categoryChannel.CategoryChannelsBreadcrumbsElement;
import com.foros.action.admin.deviceChannel.DeviceChannelBreadcrumbsElement;
import com.foros.action.admin.deviceChannel.DeviceChannelsBreadcrumbsElement;
import com.foros.action.admin.discoverChannel.DiscoverChannelBreadcrumbsElement;
import com.foros.action.admin.discoverChannel.DiscoverChannelsBreadcrumbsElement;
import com.foros.action.admin.discoverChannelList.DiscoverChannelListBreadcrumbsElement;
import com.foros.action.admin.discoverChannelList.DiscoverChannelListsBreadcrumbsElement;
import com.foros.action.admin.geoChannel.GeoChannelBreadCrumbsElement;
import com.foros.action.admin.geoChannel.GeoChannelsBreadCrumbsElement;
import com.foros.action.admin.keywordChannel.KeywordChannelBreadcrumbsElement;
import com.foros.action.admin.keywordChannel.KeywordChannelsBreadcrumbsElement;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.breadcrumbs.EntityBreadcrumbsElement;
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
import com.foros.security.AccountRole;

public class ChannelBreadcrumbsElement extends EntityBreadcrumbsElement {
    public ChannelBreadcrumbsElement(BehavioralChannel behavioralChannel) {
        super("channel.breadcrumbs", behavioralChannel.getId(), behavioralChannel.getName(), "channel/BehavioralChannel/view");
    }

    public ChannelBreadcrumbsElement(ExpressionChannel expressionChannel) {
        super("channel.breadcrumbs", expressionChannel.getId(), expressionChannel.getName(), "channel/ExpressionChannel/view");
    }

    public ChannelBreadcrumbsElement(AudienceChannel audienceChannel) {
        super("channel.breadcrumbs", audienceChannel.getId(), audienceChannel.getName(), "channel/AudienceChannel/view");
    }

    public static Breadcrumbs getChannelBreadcrumbs(Channel model) {
        Breadcrumbs breadcrumbs = null;
        if (model instanceof KeywordChannel) {
            breadcrumbs = new Breadcrumbs().add(new KeywordChannelsBreadcrumbsElement()).add(new KeywordChannelBreadcrumbsElement((KeywordChannel) model));
        } else if (model instanceof DeviceChannel) {
            breadcrumbs = new Breadcrumbs().add(new DeviceChannelsBreadcrumbsElement()).add(new DeviceChannelBreadcrumbsElement((DeviceChannel) model));
        } else if (model instanceof CategoryChannel) {
            breadcrumbs = new Breadcrumbs().add(new CategoryChannelsBreadcrumbsElement()).add(new CategoryChannelBreadcrumbsElement((CategoryChannel) model));
        } else if (model instanceof DiscoverChannelList) {
            breadcrumbs = new Breadcrumbs().add(new DiscoverChannelListsBreadcrumbsElement()).add(new DiscoverChannelListBreadcrumbsElement((DiscoverChannelList) model));
        } else if (model instanceof DiscoverChannel) {
            breadcrumbs = new Breadcrumbs().add(new DiscoverChannelsBreadcrumbsElement()).add(new DiscoverChannelBreadcrumbsElement((DiscoverChannel) model));
        } else if (model instanceof BehavioralChannel) {
            BehavioralChannel behavioralChannel = (BehavioralChannel) model;
            if (behavioralChannel.getAccount().getRole() == AccountRole.INTERNAL) {
                breadcrumbs = new Breadcrumbs().add(new ChannelsBreadcrumbsElement()).add(new ChannelBreadcrumbsElement(behavioralChannel));
            } else {
                breadcrumbs = new Breadcrumbs().add(new ChannelBreadcrumbsElement(behavioralChannel));
            }
        } else if (model instanceof ExpressionChannel) {
            ExpressionChannel expressionChannel = (ExpressionChannel) model;
            if (expressionChannel.getAccount().getRole() == AccountRole.INTERNAL) {
                breadcrumbs = new Breadcrumbs().add(new ChannelsBreadcrumbsElement()).add(new ChannelBreadcrumbsElement(expressionChannel));
            } else {
                breadcrumbs = new Breadcrumbs().add(new ChannelBreadcrumbsElement(expressionChannel));
            }
        } else if (model instanceof AudienceChannel) {
            AudienceChannel audienceChannel = (AudienceChannel) model;
            if (audienceChannel.getAccount().getRole() == AccountRole.INTERNAL) {
                breadcrumbs = new Breadcrumbs().add(new ChannelsBreadcrumbsElement()).add(new ChannelBreadcrumbsElement(audienceChannel));
            } else {
                breadcrumbs = new Breadcrumbs().add(new ChannelBreadcrumbsElement(audienceChannel));
            }
        } else if (model instanceof GeoChannel) {
            breadcrumbs = new Breadcrumbs().add(new GeoChannelsBreadCrumbsElement()).add(new GeoChannelBreadCrumbsElement((GeoChannel) model));
        }

        return breadcrumbs;
    }
}

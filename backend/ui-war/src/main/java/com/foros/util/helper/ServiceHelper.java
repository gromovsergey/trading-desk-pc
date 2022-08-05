package com.foros.util.helper;

import com.foros.session.ServiceLocator;
import com.foros.session.account.AccountService;
import com.foros.session.action.ActionService;
import com.foros.session.admin.country.CountryService;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignService;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.colocation.ColocationService;
import com.foros.session.creative.DisplayCreativeService;
import com.foros.session.creative.CreativeSizeService;
import com.foros.session.security.UserService;
import com.foros.session.site.SiteService;
import com.foros.session.site.TagsService;

public class ServiceHelper {
    public static AccountService getAccountService() {
        return ServiceLocator.getInstance().lookup(AccountService.class);
    }

    public static SearchChannelService getSearchChannelService() {
        return ServiceLocator.getInstance().lookup(SearchChannelService.class);
    }

    public static CampaignService getCampaingService() {
        return ServiceLocator.getInstance().lookup(CampaignService.class);
    }

    public static UserService getUserService() {
        return ServiceLocator.getInstance().lookup(UserService.class);
    }

    public static CampaignCreativeGroupService getCampaignCreativeGroupService() {
        return ServiceLocator.getInstance().lookup(CampaignCreativeGroupService.class);
    }

    public static DisplayCreativeService getDisplayCreativeService() {
        return ServiceLocator.getInstance().lookup(DisplayCreativeService.class);
    }

    public static ActionService getActionService() {
        return ServiceLocator.getInstance().lookup(ActionService.class);
    }

    public static SiteService getSiteService() {
        return ServiceLocator.getInstance().lookup(SiteService.class);
    }

    public static CreativeSizeService getCreativeSizeService() {
        return ServiceLocator.getInstance().lookup(CreativeSizeService.class);
    }

    public static CountryService getCountryService() {
        return ServiceLocator.getInstance().lookup(CountryService.class);
    }

    static ColocationService getColocationService() {
        return ServiceLocator.getInstance().lookup(ColocationService.class);
    }

    public static TagsService getTagsService() {
        return ServiceLocator.getInstance().lookup(TagsService.class);
    }

    public static DeviceChannelService getDeviceService() {
        return ServiceLocator.getInstance().lookup(DeviceChannelService.class);
    }

}

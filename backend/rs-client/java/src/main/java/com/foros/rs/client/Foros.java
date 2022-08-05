package com.foros.rs.client;

import com.foros.rs.client.service.AccountService;
import com.foros.rs.client.service.AdvertisingChannelService;
import com.foros.rs.client.service.CCGKeywordService;
import com.foros.rs.client.service.CampaignCreativeGroupService;
import com.foros.rs.client.service.CampaignService;
import com.foros.rs.client.service.ColocationService;
import com.foros.rs.client.service.ConversionAssociationsService;
import com.foros.rs.client.service.ConversionService;
import com.foros.rs.client.service.CreativeCategoryService;
import com.foros.rs.client.service.CreativeLinkService;
import com.foros.rs.client.service.CreativeService;
import com.foros.rs.client.service.CreativeSizeService;
import com.foros.rs.client.service.CreativeTemplateService;
import com.foros.rs.client.service.CurrencyService;
import com.foros.rs.client.service.DeviceChannelService;
import com.foros.rs.client.service.DiscoverChannelService;
import com.foros.rs.client.service.FilesService;
import com.foros.rs.client.service.GeoChannelService;
import com.foros.rs.client.service.PlatformService;
import com.foros.rs.client.service.ReportService;
import com.foros.rs.client.service.RestrictionService;
import com.foros.rs.client.service.SiteCreativeApprovalService;
import com.foros.rs.client.service.SiteService;
import com.foros.rs.client.service.TagService;
import com.foros.rs.client.service.ThirdPartyCreativeService;
import com.foros.rs.client.service.TriggerQAService;
import com.foros.rs.client.service.YandexCreativeService;

public class Foros {

    private RsClient rsClient;

    public Foros(RsClient rsClient) {
        this.rsClient = rsClient;
    }

    public ReportService getReportService() {
        return new ReportService(this.rsClient);
    }

    public FilesService getFilesService() {
        return new FilesService(this.rsClient);
    }

    public CampaignService getCampaignService() {
        return new CampaignService(this.rsClient);
    }

    public SiteService getSiteService() {
        return new SiteService(this.rsClient);
    }

    public TagService getTagService() {
        return new TagService(this.rsClient);
    }

    public AccountService getAccountService() {
        return new AccountService(this.rsClient);
    }

    public CampaignCreativeGroupService getCampaignCreativeGroupService() {
        return new CampaignCreativeGroupService(this.rsClient);
    }

    public CreativeService getCreativeService() {
        return new CreativeService(this.rsClient);
    }

    public CreativeLinkService getCreativeLinkService() {
        return new CreativeLinkService(this.rsClient);
    }

    public CreativeSizeService getCreativeSizeService() {
        return new CreativeSizeService(this.rsClient);
    }

    public CreativeTemplateService getCreativeTemplateService() {
        return new CreativeTemplateService(this.rsClient);
    }

    public CreativeCategoryService getCreativeCategoryService() {
        return new CreativeCategoryService(this.rsClient);
    }

    public AdvertisingChannelService getAdvertisingChannelService() {
        return new AdvertisingChannelService(this.rsClient);
    }

    public DiscoverChannelService getDiscoverChannelService() {
        return new DiscoverChannelService(this.rsClient);
    }

    public CCGKeywordService getCCGKeywordService() {
        return new CCGKeywordService(this.rsClient);
    }

    public SiteCreativeApprovalService getSiteCreativeApprovalService() {
        return new SiteCreativeApprovalService(this.rsClient);
    }

    public TriggerQAService getTriggerQAService() {
        return new TriggerQAService(this.rsClient);
    }

    public ThirdPartyCreativeService getThirdPartyCreativeService() {
        return new ThirdPartyCreativeService(this.rsClient);
    }

    public ConversionService getConversionService() {
        return new ConversionService(this.rsClient);
    }

    public ConversionAssociationsService getConversionAssociationsService() {
        return new ConversionAssociationsService(this.rsClient);
    }

    public CurrencyService getCurrencyService() {
        return new CurrencyService(this.rsClient);
    }

    public YandexCreativeService getYandexCreativeService() {
        return new YandexCreativeService(this.rsClient);
    }

    public GeoChannelService getGeoChannelService() {
        return new GeoChannelService(rsClient);
    }

    public DeviceChannelService getDeviceChannelService() {
        return new DeviceChannelService(rsClient);
    }

    public ColocationService getColocationService() {
        return new ColocationService(this.rsClient);
    }

    public PlatformService getPlatformService() {
        return new PlatformService(this.rsClient);
    }

    public RestrictionService getRestrictionService() {
        return new RestrictionService(this.rsClient);
    }
}

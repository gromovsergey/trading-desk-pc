package com.foros.model.security;

import com.foros.model.Country;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.AgencyAccount;
import com.foros.model.account.CmpAccount;
import com.foros.model.account.InternalAccount;
import com.foros.model.account.IspAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.action.Action;
import com.foros.model.admin.FraudConditionWrapper;
import com.foros.model.admin.SearchEngine;
import com.foros.model.admin.WDFrequencyCapWrapper;
import com.foros.model.admin.WalledGarden;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignCreditContainerWrapper;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BannedChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.CategoryChannel;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.DiscoverChannelList;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.KeywordChannel;
import com.foros.model.channel.placementsBlacklist.PlacementsBlacklistWrapper;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryTypeEntity;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.SizeType;
import com.foros.model.ctra.CTRAlgorithmData;
import com.foros.model.currency.CurrencyExchangeAuditWrapper;
import com.foros.model.isp.Colocation;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.report.birt.BirtReport;
import com.foros.model.site.Site;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.DiscoverTemplate;
import com.foros.session.fileman.FileManager;
import com.foros.session.reporting.Report;

public enum ObjectType {
    User(0, User.class),
    Site(1, Site.class),
    Colocation(3, Colocation.class),
    Campaign(4, Campaign.class),
    CampaignCreativeGroup(5, CampaignCreativeGroup.class),
    CurrencyExchange(6, CurrencyExchangeAuditWrapper.class),
    Creative(7, Creative.class),
    CampaignCreative(9, CampaignCreative.class),
    CCGKeyword(10, CCGKeyword.class),
    Keyword(11, CCGKeyword.class), // duplicate?
    UserRole(12, UserRole.class),
    Action(13, Action.class),
    AgencyAccount(14, AgencyAccount.class),
    AdvertiserAccount(15, AdvertiserAccount.class),
    IspAccount(16, IspAccount.class),
    InternalAccount(17, InternalAccount.class),
    PublisherAccount(18, PublisherAccount.class),
    DiscoverChannel(19, DiscoverChannel.class),
    BehavioralChannel(20, BehavioralChannel.class),
    ExpressionChannel(21, ExpressionChannel.class),
    CategoryChannel(22, CategoryChannel.class),
    Tag(24, com.foros.model.site.Tag.class),
    WDTag(25, com.foros.model.site.WDTag.class),
    CmpAccount(26, CmpAccount.class),
    DiscoverChannelList(27, DiscoverChannelList.class),
    KeywordChannel(28, KeywordChannel.class),
    AccountType(29, AccountType.class),
    CreativeCategory(30, CreativeCategory.class),
    CreativeTemplate(31, CreativeTemplate.class),
    Country(32, Country.class),
    CreativeSize(33, CreativeSize.class),
    DiscoverTemplate(34, DiscoverTemplate.class),
    NoTrackingChannel(36, BannedChannel.class),
    NoAdvertisingChannel(37, BannedChannel.class),
    FraudCondition(38, FraudConditionWrapper.class),
    WDFrequencyCap(39, WDFrequencyCapWrapper.class),
    CTRAlgorithmData(40, CTRAlgorithmData.class),
    PredefinedReport(42, Report.class, false),
    BirtReport(43, BirtReport.class),
    Opportunity(45, Opportunity.class),
    SearchEngine(46, SearchEngine.class),
    DeviceChannel(49, DeviceChannel.class),
    CampaignCredit(50, AdvertisingAccountBase.class),
    WalledGarden(52, WalledGarden.class),
    FileManager(54, FileManager.class, false),
    SizeType(55, SizeType.class),
    AudienceChannel(56, AudienceChannel.class),
    PlacementsBlacklist(57, PlacementsBlacklistWrapper.class, false),
    GeoChannel(58, GeoChannel.class);

    private Integer id;
    private Class<?> clazz;
    private boolean isEntity;

    ObjectType(Integer id, Class<?> clazz) {
        this(id, clazz, true);
    }

    ObjectType(Integer id, Class<?> clazz, boolean isEntity) {
        this.id = id;
        this.clazz = clazz;
        this.isEntity = isEntity;
    }

    public Class<?> getObjectClass() {
        return clazz;
    }

    public String getName() {
        return this.name();
    }

    public boolean isEntity() {
        return isEntity;
    }

    public static ObjectType valueOf(Class<?> value) {
        if (value.equals(CreativeCategoryTypeEntity.class)) {
            return CreativeCategory;
        }

        if (value.equals(CampaignCreditContainerWrapper.class)) {
            return CampaignCredit;
        }


        if (value.equals(BirtReport.class)) {
            return BirtReport;
        }

        for (ObjectType type : ObjectType.values()) {
            if (value.equals(type.getObjectClass()))
                return type;
        }

        return null;
    }

    public static ObjectType valueOf(Integer id) {
        for (ObjectType type : ObjectType.values()) {
            if (type.getId().equals(id)) {
                return type;
            }
        }
        return null;
    }

    public Integer getId() {
        return id;
    }
}

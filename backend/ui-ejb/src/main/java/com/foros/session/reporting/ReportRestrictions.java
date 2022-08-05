package com.foros.session.reporting;

import static com.foros.security.AccountRole.ADVERTISER;
import static com.foros.security.AccountRole.AGENCY;
import static com.foros.security.AccountRole.CMP;
import static com.foros.security.AccountRole.INTERNAL;
import static com.foros.security.AccountRole.ISP;
import static com.foros.security.AccountRole.PUBLISHER;

import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.PublisherAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.KeywordChannel;
import com.foros.model.creative.CreativeSize;
import com.foros.model.security.OwnedStatusable;
import com.foros.model.security.UserRole;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.restriction.annotation.Permission;
import com.foros.restriction.annotation.Permissions;
import com.foros.restriction.annotation.Restriction;
import com.foros.restriction.annotation.Restrictions;
import com.foros.restriction.permission.PermissionService;
import com.foros.restriction.registry.PermissionDescriptor;
import com.foros.security.principal.SecurityContext;
import com.foros.session.account.AccountRestrictions;
import com.foros.session.account.AccountService;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.campaignAllocation.CampaignAllocationRestrictions;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;
import com.foros.session.channel.service.DiscoverChannelRestrictions;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportParameters;
import com.foros.session.reporting.channel.ChannelReportParameters;
import com.foros.session.reporting.channelInventoryForecast.ChannelInventoryForecastReportParameters;
import com.foros.session.reporting.channeltriggers.ChannelTriggersReportParameters;
import com.foros.session.reporting.inventoryEstimation.InventoryEstimationReportParameters;
import com.foros.session.reporting.publisher.PublisherReportParameters;
import com.foros.session.reporting.referrer.ReferrerReportParameters;
import com.foros.session.restriction.EntityRestrictions;
import com.foros.session.security.UserService;
import com.foros.util.CollectionUtils;
import com.foros.util.SQLUtil;
import com.foros.util.bean.Filter;
import com.foros.util.jpa.JpaQueryWrapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Restrictions
@Permissions({
        @Permission(objectType = "predefined_report_advertiser", action = "run", accountRoles = { INTERNAL, AGENCY, ADVERTISER }),
        @Permission(objectType = "predefined_report_textAdvertising", action = "run", accountRoles = { INTERNAL, AGENCY, ADVERTISER }),
        @Permission(objectType = "predefined_report_videoAdvertising", action = "run", accountRoles = { INTERNAL, AGENCY, ADVERTISER }),
        @Permission(objectType = "predefined_report_generalAdvertising", action = "run", accountRoles = { INTERNAL, AGENCY, ADVERTISER }),
        @Permission(objectType = "predefined_report_conversions", action = "run", accountRoles = { INTERNAL, AGENCY, ADVERTISER }),
        @Permission(objectType = "predefined_report_publisher", action = "run", accountRoles = { INTERNAL, PUBLISHER }),
        @Permission(objectType = "predefined_report_inventoryEstimation", action = "run", accountRoles = { INTERNAL, PUBLISHER }),
        @Permission(objectType = "predefined_report_ISP", action = "run", accountRoles = { INTERNAL, ISP }),
        @Permission(objectType = "predefined_report_invitations", action = "run", accountRoles = { INTERNAL, ISP }),
        @Permission(objectType = "predefined_report_webwise", action = "run", accountRoles = { INTERNAL, ISP }),
        @Permission(objectType = "predefined_report_channelUsage", action = "run", accountRoles = { INTERNAL, CMP }),
        @Permission(objectType = "predefined_report_custom", action = "run", accountRoles = { INTERNAL }),
        @Permission(objectType = "predefined_report_conversionPixels", action = "run", accountRoles = { INTERNAL }),
        @Permission(objectType = "predefined_report_siteChannels", action = "run", accountRoles = { INTERNAL }),
        @Permission(objectType = "predefined_report_referrer", action = "run", accountRoles = { INTERNAL, PUBLISHER }),
        @Permission(objectType = "predefined_report_audit", action = "run", accountRoles = { INTERNAL }),
        @Permission(objectType = "predefined_report_channelSites", action = "run", accountRoles = { INTERNAL }),
        @Permission(objectType = "predefined_report_channelTriggers", action = "run", accountRoles = { INTERNAL, AGENCY, ADVERTISER, CMP }),
        @Permission(objectType = "predefined_report_channel", action = "run", accountRoles = { INTERNAL, AGENCY, ADVERTISER, CMP }),
        @Permission(objectType = "predefined_report_channelInventory", action = "run", accountRoles = { INTERNAL, AGENCY, ADVERTISER }),
        @Permission(objectType = "predefined_report_userAgents", action = "run", accountRoles = { INTERNAL })
})
public class ReportRestrictions {

    @EJB
    private PermissionService permissionService;

    @EJB
    private UserService userService;

    @EJB
    private DiscoverChannelRestrictions discoverChannelRestrictions;

    @EJB
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private AccountRestrictions accountRestrictions;

    @EJB
    private AccountService accountService;

    @EJB
    private CampaignAllocationRestrictions campaignAllocationRestrictions;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @Restriction
    public boolean canRun(String reportName) {
        if ("campaignAllocationHistory".equals(reportName)) {
            return campaignAllocationRestrictions.canView();
        }
        return permissionService.isGranted("predefined_report_" + reportName, "run");
    }

    @Restriction
    public boolean canRunInternal(Long selfAccountId, String reportActionName) {
        if (!SecurityContext.isInternal()) {
            Long accountId = SecurityContext.getPrincipal().getAccountId();
            if (!accountId.equals(selfAccountId)) {
                return false;
            }
        }

        return canRun(reportActionName);
    }

    @Restriction
    public boolean canRunAny() {
        if (SecurityContext.isInternal()) {
            return true;
        }

        UserRole role = userService.getMyUser().getRole();
        Map<PermissionDescriptor, Map<String, Long>> policy = permissionService.getPolicy(role.getId());
        for (PermissionDescriptor permission : policy.keySet()) {
            if (permission.getObjectType().contains("predefined_report_")) {
                return true;
            }
        }

        return false;
    }

    @Restriction
    public boolean canRunAnyOf(String... reports) {
        for (String report : reports) {
            if (canRun(report)) {
                return true;
            }
        }
        return false;
    }

    @Restriction("ChannelTriggers.run")
    public boolean canRunChannelTriggersForChannel(Long accountId, Collection<Long> channelIds) {
        return checkChannels(accountId, channelIds, new Filter<Channel>() {
            @Override
            public boolean accept(Channel channel) {
                return canRunChannelTriggersForChannel(channel);
            }
        });
    }

    @Restriction("ChannelTriggers.run")
    public boolean canRunChannelTriggersForChannel(ChannelTriggersReportParameters parameters) {
        return checkChannels(parameters.getAccountId(), parameters.getChannelIds(), new Filter<Channel>() {
            @Override
            public boolean accept(Channel channel) {
                return canRunChannelTriggersForChannel(channel);
            }
        });
    }

    @Restriction("ChannelTriggers.run")
    public boolean canRunChannelTriggersForChannel(Channel channel) {
        if (!canRun("channelTriggers")) {
            return false;
        }

        return canViewChannel(channel);
    }

    @Restriction("ChannelTriggers.view")
    public boolean canViewChannelTriggersReport(Channel channel) {
        if (!canRun("channelTriggers")) {
            return false;
        }

        if (channel != null) {
            return canViewChannel(channel);
        }

        // channelId = null is acceptable
        return true;
    }

    private boolean canViewChannel(Channel channel) {
        if (channel instanceof DiscoverChannel) {
            return discoverChannelRestrictions.canView();
        } else if (channel instanceof BehavioralChannel) {
            return advertisingChannelRestrictions.canViewContent(channel);
        }

        return false;
    }

    @Restriction("Channel.run")
    public boolean canRunChannelReport(Channel channel) {
        if (!canRun("channel")) {
            return false;
        }

        return canViewForChannelReport(channel);
    }

    @Restriction("Channel.run")
    public boolean canRunChannelReport(ChannelReportParameters parameters) {
        if (!canRun("channel")) {
            return false;
        }

        if (parameters.getChannelId() == null) {
            return true;
        }
        Channel channel = em.find(Channel.class, parameters.getChannelId());
        return canViewForChannelReport(channel);
    }

    private boolean canViewForChannelReport(Channel channel) {
        if (channel instanceof BehavioralChannel
                || channel instanceof ExpressionChannel
                || channel instanceof AudienceChannel
                || channel instanceof DiscoverChannel
                || channel instanceof KeywordChannel
                || channel instanceof GeoChannel) {
            return canViewChannelForReports(channel);
        }

        return false;
    }

    @Restriction("ChannelUsage.run")
    public boolean canRunChannelUsageReport(Channel channel) {
        if (!canRun("channelUsage")) {
            return false;
        }

        if (!(channel instanceof BehavioralChannel
                || channel instanceof ExpressionChannel
                || channel instanceof KeywordChannel)) {
            return false;
        }

        return channel.getVisibility() == ChannelVisibility.CMP &&
                entityRestrictions.canViewBasic(channel) &&
                accountRestrictions.canView(channel.getAccount());
    }

    @Restriction("ChannelSites.run")
    public boolean canRunChannelSitesReport(Account account) {
        return entityRestrictions.canView(account);
    }

    @Restriction("ChannelSites.run")
    public boolean canRunChannelSitesReport(Channel channel) {
        if (!canRun("channelSites")) {
            return false;
        }

        if (channel instanceof BehavioralChannel
                || channel instanceof ExpressionChannel
                || channel instanceof AudienceChannel
                || channel instanceof KeywordChannel) {
            return canViewChannelForReports(channel);
        }

        return false;
    }

    @Restriction("ChannelInventory.run")
    public boolean canRunChannelInventoryReport(Channel channel) {
        if (!canRun("channelInventory")) {
            return false;
        }

        return canRunChannelInventoryReport(null, channel);
    }

    @Restriction("ChannelInventory.run")
    public boolean canRunChannelInventoryReport(ChannelInventoryForecastReportParameters parameters) {
        if (!canRun("channelInventory")) {
            return false;
        }

        ChannelInventoryForecastReportParameters.ChannelFilter filter = parameters.getChannelFilter();
        Long accountId = parameters.getAccountId();

        if (filter == null || accountId == null) {
            // validation will fail
            return true;
        }

        Account account = accountService.find(accountId);

        if (filter == ChannelInventoryForecastReportParameters.ChannelFilter.IDS) {
            return canRunChannelInventoryReport(accountId, parameters.getChannelIds());
        } else {
            return entityRestrictions.canViewBasic(account);
        }
    }

    @Restriction("ChannelInventory.view")
    public boolean canRunChannelInventoryReport(Account account, Channel channel) {
        if (!canRun("channelInventory")) {
            return false;
        }

        if (channel == null) {
            return true;
        }

        channel = em.find(Channel.class, channel.getId());
        if (!canRunChannelInventoryReportInternal(channel)) {
            return false;
        }

        if (account != null && !channel.getAccount().equals(account)) {
            return false;
        }

        return true;
    }

    private boolean canRunChannelInventoryReport(Long accountId, Collection<Long> channelIds) {
        return checkChannels(accountId, channelIds, new Filter<Channel>() {
            @Override
            public boolean accept(Channel channel) {
                return canRunChannelInventoryReportInternal(channel);
            }
        });
    }

    private boolean checkChannels(Long accountId, Collection<Long> channelIds, Filter<Channel> callback) {
        if (channelIds == null || channelIds.isEmpty()) {
            return true;
        }

        String query = "from Channel c where (account.id = :accountId) and "
                + SQLUtil.formatINClause("id", channelIds);
        List<Channel> channels = new JpaQueryWrapper<Channel>(em, query)
            .setParameter("accountId", accountId)
            .getResultList();

        if (channelIds.size() != channels.size()) {
            return false;
        }

        for (Channel channel : channels) {
            if (!callback.accept(channel)) {
                return false;
            }
        }

        return true;
    }

    private boolean canRunChannelInventoryReportInternal(Channel channel) {
        if (channel instanceof BehavioralChannel
                || channel instanceof ExpressionChannel
                || channel instanceof AudienceChannel) {
            return canViewChannelForReports(channel);
        }

        return false;
    }

    private boolean canViewChannelForReports(Channel ch) {
        if (ch.getVisibility() == ChannelVisibility.PUB || ch.getVisibility() == ChannelVisibility.CMP) {
            return entityRestrictions.canViewBasic(ch)
                    && (entityRestrictions.canView(INTERNAL)
                            || entityRestrictions.canView(CMP)
                            || entityRestrictions.canView(ADVERTISER)
                            || entityRestrictions.canView(AGENCY));
        } else {
            return entityRestrictions.canView(ch);
        }
    }

    @Restriction("Publisher.run")
    public boolean canRunPublisherReport(PublisherReportParameters param) {
        return canRunPublishersReport("publisher", param.getAccountId(), param.getSiteId(), param.getTagId());
    }

    @Restriction("Publisher.run")
    public boolean canRunPublisherReport(Long accountId) {
        return canRunPublishersReport(accountId, "publisher");
    }

    @Restriction("Publisher.run")
    public boolean canRunPublisherReport() {
        return canRunPublishersReport(null, "publisher");
    }

    private boolean canRunPublishersReport(Long accountId, String reportName) {
        if (!canRun(reportName)) {
            return false;
        }

        if (accountId != null) {
            PublisherAccount account = em.find(PublisherAccount.class, accountId);
            if (account != null && !entityRestrictions.canView(account)) {
                return false;
            }
        }

        return true;
    }

    @Restriction
    public boolean canRunAdvertiserReport(Long accountId) {
        if (!canRun("advertiser")) {
            return false;
        }

        Account account = accountService.find(accountId);
        return advertiserEntityRestrictions.canAccessDisplayAd(account) ||
                advertiserEntityRestrictions.canAccessChannelTargetedTextAd(account);
    }

    @Restriction
    public boolean canRunTextAdvertisingReport(Long accountId) {
        if (!canRun("textAdvertising")) {
            return false;
        }

        Account account = accountService.find(accountId);
        return advertiserEntityRestrictions.canAccessTextAd(account);
    }

    @Restriction
    public boolean canRunGeneralAdvertiserReport(Long accountId) {
        Account account = accountService.find(accountId);
        return canRunGeneralAdvertiserReport(account);
    }

    @Restriction
    public boolean canRunGeneralAdvertiserReport(Account account) {
        if (!canRun("generalAdvertising")) {
            return false;
        }

        return advertiserEntityRestrictions.canAccessDisplayAd(account) ||
                advertiserEntityRestrictions.canAccessChannelTargetedTextAd(account) ||
                advertiserEntityRestrictions.canAccessTextAd(account);
    }

    @Restriction("OlapDisplayAdvertiser.run")
    public boolean canRunDisplayAdvertiserReport(OlapAdvertiserReportParameters parameters) {
        if (!canRun("advertiser")) {
            return false;
        }
        return canRunAdvertiserReport(parameters);
    }

    @Restriction("OlapTextAdvertiser.run")
    public boolean canRunTextAdvertiserReport(OlapAdvertiserReportParameters parameters) {
        if (!canRun("textAdvertising")) {
            return false;
        }
        return canRunAdvertiserReport(parameters);
    }

    @Restriction("VideoAdvertiser.run")
    public boolean canRunVideoAdvertiserReport(OlapAdvertiserReportParameters parameters) {
        if (!canRun("videoAdvertising")) {
            return false;
        }
        return canRunAdvertiserReport(parameters);
    }

    @Restriction("GeneralAdvertiser.run")
    public boolean canRunGeneralAdvertiserReport(OlapAdvertiserReportParameters parameters) {
        if (!canRun("generalAdvertising")) {
            return false;
        }
        return canRunAdvertiserReport(parameters);
    }

    @Restriction
    public boolean canRunVideoAdvertisingReport(Long accountId) {
        if (!canRun("videoAdvertising")) {
            return false;
        }

        Account account = accountService.find(accountId);
        boolean isVideoSize = false;
        for (CreativeSize cs : account.getAccountType().getCreativeSizes()) {
            if ("Video".equals(cs.getSizeType().getDefaultName())) {
                isVideoSize = true;
                break;
            }
        }
        return isVideoSize && (advertiserEntityRestrictions.canAccessDisplayAd(account) ||
                advertiserEntityRestrictions.canAccessChannelTargetedTextAd(account));
    }


    private boolean canRunAdvertiserReport(OlapAdvertiserReportParameters parameters) {
        if (parameters.getAccountId() != null) {
            Account account = em.find(Account.class, parameters.getAccountId());
            if (account != null && !entityRestrictions.canView(account)) {
                return false;
            }
        }

        if (!canAccessAdvertiserFilterEntities(AdvertiserAccount.class, parameters.getAdvertiserIds()) ||
                !canAccessAdvertiserFilterEntities(Campaign.class, parameters.getCampaignIds()) ||
                !canAccessAdvertiserFilterEntities(CampaignCreativeGroup.class, parameters.getCcgIds()) ||
                !canAccessAdvertiserFilterEntities(CampaignCreative.class, parameters.getCampaignCreativeIds())) {
            return false;
        }

        return true;
    }

    private boolean canAccessAdvertiserFilterEntities(Class entityClass, Collection<Long> ids) {
        if (!CollectionUtils.isNullOrEmpty(ids)) {
            String query = "from " + entityClass.getSimpleName() + " c where " + SQLUtil.formatINClause("id", ids);
            List<OwnedStatusable> entities = new JpaQueryWrapper<OwnedStatusable>(em, query).getResultList();

            for (OwnedStatusable entity : entities) {
                if (!entityRestrictions.canAccess(entity)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Restriction("InventoryEstimation.run")
    public boolean canRunInventoryEstimationReport(Long accountId) {
        if (!canRunPublishersReport(accountId, "inventoryEstimation")) {
            return false;
        }
        return checkPublisherInventoryEstimationFlag(accountId);
    }

    @Restriction("InventoryEstimation.run")
    public boolean canRunInventoryEstimationReport(InventoryEstimationReportParameters parameters) {
        if (!canRunPublishersReport("inventoryEstimation", parameters.getAccountId(), parameters.getSiteId(), parameters.getTagId())) {
            return false;
        }
        return checkPublisherInventoryEstimationFlag(parameters.getAccountId());
    }

    private boolean checkPublisherInventoryEstimationFlag(Long accountId) {
        if (accountId != null) {
            PublisherAccount account = em.find(PublisherAccount.class, accountId);
            if (account != null && !account.getAccountType().isPublisherInventoryEstimationFlag()) {
                return false;
            }
        }
        return true;
    }

    private boolean canRunPublishersReport(String reportName, Long accountId, Long siteId, Long tagId) {
        if (!canRunPublishersReport(accountId, reportName)) {
            return false;
        }

        if (siteId != null) {
            Site site = em.find(Site.class, siteId);
            if (site != null && !entityRestrictions.canView(site)) {
                return false;
            }
        }

        if (tagId != null) {
            Tag tag = em.find(Tag.class, tagId);
            if (tag != null && !entityRestrictions.canView(tag)) {
                return false;
            }
        }

        return true;
    }

    @Restriction("Waterfall.run")
    public boolean canRunWaterfallReport() {
        return SecurityContext.isInternal();
    }

    @Restriction("Waterfall.run")
    public boolean canRunWaterfallReport(CampaignCreativeGroup group) {
        return canRunWaterfallReport() && (group.getChannel() != null || TGTType.KEYWORD.equals(group.getTgtType()));
    }

    @Restriction("AdvancedISPReports.run")
    public boolean canRunAdvancedISPReports(Long accountId) {
        Account account = accountService.findIspAccount(accountId);
        return entityRestrictions.canView(account)
                && (SecurityContext.isInternal() || account.getAccountType().isAdvancedReportsFlag());
    }

    @Restriction("PubAdvertisingReport.run")
    public boolean canRunPubAdvertisingReport(Long accountId) {
        PublisherAccount account = accountService.findPublisherAccount(accountId);
        return entityRestrictions.canView(account)
                && (SecurityContext.isInternal() || account.isPubAdvertisingReportFlag());
    }

    @Restriction("ReferrerReport.run")
    public boolean canRunReferrerReport(ReferrerReportParameters parameters) {
        if (!canRun("referrer")) {
            return false;
        }

        if (parameters.getAccountId() == null) {
            return true;
        }

        return canRunReferrerReport(parameters.getAccountId());
    }

    @Restriction("ReferrerReport.run")
    public boolean canRunReferrerReport(Long accountId) {
        if (!canRun("referrer")) {
            return false;
        }

        Account account = em.find(Account.class, accountId);
        if (account == null || !entityRestrictions.canView(account) || !(account instanceof PublisherAccount)) {
            return false;
        }

        return ((PublisherAccount) account).getSites().size() > 0
                && (SecurityContext.isInternal() || account.isReferrerReportFlag());
    }
}

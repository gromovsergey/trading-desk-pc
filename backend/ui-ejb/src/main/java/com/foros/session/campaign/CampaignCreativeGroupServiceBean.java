package com.foros.session.campaign;

import com.foros.cache.generic.CacheProviderService;
import com.foros.cache.generic.CacheRegion;
import com.foros.cache.generic.CreateValueCallback;
import com.foros.changes.CaptureChangesInterceptor;
import com.foros.model.ApproveStatus;
import com.foros.model.Country;
import com.foros.model.DisplayStatus;
import com.foros.model.Flags;
import com.foros.model.FrequencyCap;
import com.foros.model.Identifiable;
import com.foros.model.LocalizableName;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
import com.foros.model.action.ConversionCategory;
import com.foros.model.campaign.BidStrategy;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.CCGSchedule;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CcgRate;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.campaign.DeliveryPacing;
import com.foros.model.campaign.OptInStatusTargeting;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelExpressionLink;
import com.foros.model.channel.ChannelRate;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.GeoChannel;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.model.creative.Creative;
import com.foros.model.isp.Colocation;
import com.foros.model.security.AccountType;
import com.foros.model.security.ActionType;
import com.foros.model.site.Site;
import com.foros.persistence.hibernate.ManualFlushInterceptor;
import com.foros.reporting.RowTypes;
import com.foros.reporting.meta.ColumnOrder;
import com.foros.reporting.meta.DbColumn;
import com.foros.reporting.serializer.AuditResultHandlerWrapper;
import com.foros.reporting.serializer.SimpleReportData;
import com.foros.reporting.serializer.formatter.FormatterContext;
import com.foros.reporting.serializer.formatter.HtmlCellAccessor;
import com.foros.reporting.serializer.formatter.ValueFormatterSupport;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistries;
import com.foros.reporting.serializer.formatter.registry.ValueFormatterRegistry;
import com.foros.reporting.tools.query.parameters.usertype.PostgreLocalDateUserType;
import com.foros.restriction.RestrictionInterceptor;
import com.foros.restriction.RestrictionService;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.PersistenceExceptionInterceptor;
import com.foros.session.StatsDbQueryProvider;
import com.foros.session.TreeFilterElementTO;
import com.foros.session.TreeFilterElementTOConverter;
import com.foros.session.UploadContext;
import com.foros.session.UploadStatus;
import com.foros.session.UtilityService;
import com.foros.session.admin.CurrencyConverter;
import com.foros.session.admin.country.CountryService;
import com.foros.session.admin.currencyExchange.CurrencyExchangeService;
import com.foros.session.bulk.BulkOperation;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsResult;
import com.foros.session.bulk.Result;
import com.foros.session.cache.AutoFlushInterceptor;
import com.foros.session.campaign.ChartStats.ChartHelper;
import com.foros.session.campaign.ImpClickStatsTO.Builder;
import com.foros.session.campaign.bulk.CampaignCreativeGroupSelector;
import com.foros.session.campaign.ccg.expressionPerformance.ExpressionPerformanceReportMetaData;
import com.foros.session.campaign.ccg.expressionPerformance.ExpressionPerformanceReportParameters;
import com.foros.session.channel.exceptions.ChannelNotFoundExpressionException;
import com.foros.session.channel.exceptions.ExpressionConversionException;
import com.foros.session.channel.exceptions.UndistinguishableExpressionException;
import com.foros.session.channel.exceptions.UnreachableExpressionException;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.session.channel.service.ExpressionService;
import com.foros.session.channel.targeting.CCGTargetingStatsTO;
import com.foros.session.channel.targeting.TargetingChannelService;
import com.foros.session.channel.targeting.TargetingStatsTO;
import com.foros.session.creative.BaseLinkedTO;
import com.foros.session.creative.CreativeSetTO;
import com.foros.session.creative.LinkedCreativeTO;
import com.foros.session.db.DBConstraint;
import com.foros.session.frequencyCap.FrequencyCapMerger;
import com.foros.session.query.PartialList;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.campaign.CampaignCreativeGroupQuery;
import com.foros.session.query.campaign.CampaignCreativeGroupQueryImpl;
import com.foros.session.reporting.CommonAuditableReportSupport;
import com.foros.session.reporting.ReportType;
import com.foros.session.reporting.ReportsService;
import com.foros.session.security.AuditService;
import com.foros.session.security.UserService;
import com.foros.session.status.ApprovalService;
import com.foros.session.status.DisplayStatusService;
import com.foros.session.status.StatusService;
import com.foros.util.CollectionMerger;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;
import com.foros.util.PersistenceUtils;
import com.foros.util.SQLUtil;
import com.foros.util.UploadUtils;
import com.foros.util.VersionCollisionException;
import com.foros.util.bean.Filter;
import com.foros.util.comparator.StatusNameTOComparator;
import com.foros.util.jpa.JpaQueryWrapper;
import com.foros.util.mapper.Mapper;
import com.foros.util.mapper.Pair;
import com.foros.util.posgress.PGArray;
import com.foros.util.posgress.PGRow;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationInterceptor;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validate;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.violation.ConstraintViolationException;
import com.foros.validation.strategy.ValidationStrategies;
import com.foros.validation.util.DuplicateChecker;
import com.foros.validation.util.ValidationUtil;

import java.math.BigDecimal;
import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang.time.DateUtils;
import org.joda.time.LocalDate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

@Stateless(name = "CampaignCreativeGroupService")
@Interceptors({ RestrictionInterceptor.class, ValidationInterceptor.class, PersistenceExceptionInterceptor.class })
public class CampaignCreativeGroupServiceBean implements CampaignCreativeGroupService {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AuditService auditService;

    @EJB
    private ApprovalService approvalService;

    @EJB
    private StatusService statusService;

    @EJB
    private UtilityService utilityService;

    @EJB
    private CampaignService campaignService;

    @EJB
    private CCGKeywordService ccgKeywordService;

    @EJB
    private RestrictionService restrictionService;

    @EJB
    protected DisplayStatusService displayStatusService;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private CurrencyExchangeService currencyExchangeService;

    @EJB
    private QueryExecutorService executorService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private ValidationService validationService;

    @EJB
    private DeviceChannelService deviceChannelService;

    @EJB
    private TargetingChannelService targetingChannelService;

    @EJB
    private ExpressionService expressionService;

    @EJB
    private ReportsService reportsService;

    @EJB
    private StatsDbQueryProvider statDbQuery;

    @EJB
    private UserService userService;

    @EJB
    private CacheProviderService cacheProviderService;

    @EJB
    private CountryService countryService;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @EJB
    private QueryExecutorService queryExecutorService;

    @EJB
    private CampaignCreativeService campaignCreativeService;

    private class LinkedCreativesTOList extends LinkedList<BaseLinkedTO> {
        private boolean showPostImpConv;
        private boolean showPostClickConv;

        public boolean isShowPostImpConv() {
            return showPostImpConv;
        }
        public void setShowPostImpConv(boolean showPostImpConv) {
            this.showPostImpConv = showPostImpConv;
        }
        public boolean isShowPostClickConv() {
            return showPostClickConv;
        }
        public void setShowPostClickConv(boolean showPostClickConv) {
            this.showPostClickConv = showPostClickConv;
        }
    }

    private FrequencyCapMerger<CampaignCreativeGroup> frequencyCapMerger = new FrequencyCapMerger<CampaignCreativeGroup>() {
        @Override
        protected EntityManager getEm() {
            return em;
        }
    };

    private ScheduleMerger<CCGSchedule> ccgScheduleCapMerger = new ScheduleMerger<CCGSchedule>() {
        @Override
        protected EntityManager getEm() {
            return em;
        }
    };

    private void prePersist(CampaignCreativeGroup creativeGroup, CampaignCreativeGroup existingCCG, Campaign campaign) {

        // Fetch campaign
        creativeGroup.setCampaign(campaign);

        CCGType ccgType = CCGType.valueOf(campaign.getCampaignType());
        creativeGroup.setCcgType(ccgType);
        if (ccgType == CCGType.DISPLAY) {
            creativeGroup.setTgtType(TGTType.CHANNEL);
        }

        prePersistGeoChannels(creativeGroup);
        prePersistDeviceChannels(creativeGroup, existingCCG);

        // check country
        if (creativeGroup.isChanged("country")) {
            creativeGroup.setCountry(em.find(Country.class, creativeGroup.getCountry().getCountryCode()));
        }

        // frequency caps
        FrequencyCap frequencyCap = creativeGroup.getFrequencyCap();
        if (frequencyCap != null && frequencyCap.isEmpty()) {
            creativeGroup.setFrequencyCap(null);
        }

        // sites
        if (!advertiserEntityRestrictions.canEditSiteTargeting(creativeGroup.getAccount())) {
            creativeGroup.setSites(null);
            creativeGroup.unregisterChange("sites");
        }

        if (creativeGroup.isChanged("sites")) {
            Set<Site> sites = new LinkedHashSet<>();
            for (Site o : creativeGroup.getSites()) {
                Site site = em.getReference(Site.class, o.getId());
                sites.add(site);
            }
            creativeGroup.setSites(sites);
            creativeGroup.setIncludeSpecificSitesFlag(!CollectionUtils.isNullOrEmpty(creativeGroup.getSites()));
        } else {
            creativeGroup.unregisterChange("includeSpecificSites");
        }

        // colocations
        if (creativeGroup.isChanged("colocations")) {
            Set<Colocation> colocations = new LinkedHashSet<>();
            for (Colocation o : creativeGroup.getColocations()) {
                Colocation colocation = em.getReference(Colocation.class, o.getId());
                colocations.add(colocation);
            }
            creativeGroup.setColocations(colocations);
            creativeGroup.setIspColocationTargetingFlag(!CollectionUtils.isNullOrEmpty(creativeGroup.getColocations()));
        } else {
            creativeGroup.unregisterChange("ispColocationTargeting");
        }

        // conversions
        if (creativeGroup.isChanged("actions")) {
            Set<Action> conversions = new LinkedHashSet<>();
            for (Action o : creativeGroup.getActions()) {
                Action conversion = em.getReference(Action.class, o.getId());
                conversions.add(conversion);
            }
            creativeGroup.setActions(conversions);
        }

        if (creativeGroup.isChanged("deliveryPacing") && creativeGroup.getDeliveryPacing() != DeliveryPacing.FIXED) {
            creativeGroup.setDailyBudget(null);
        }

        // delivery period
        if (creativeGroup.isChanged("deliveryScheduleFlag") && creativeGroup.isDeliveryScheduleFlag()) {
            for (CCGSchedule ccgSchedule : creativeGroup.getCcgSchedules()) {
                ccgSchedule.setCampaignCreativeGroup(existingCCG == null ? creativeGroup : existingCCG);
            }
        }

        // channel target
        if (creativeGroup.isChanged("channel")) {
            Long channelId = creativeGroup.getChannel() != null ? creativeGroup.getChannel().getId() : null;
            Channel channel = channelId == null ? null : em.find(Channel.class, channelId);
            creativeGroup.setChannel(channel);
        }

        boolean linked;
        if (existingCCG != null) {
            linked = creativeGroup.isChanged("linkedToCampaignEndDateFlag") ? creativeGroup.isLinkedToCampaignEndDateFlag() : existingCCG.isLinkedToCampaignEndDateFlag();
        } else {
            linked = creativeGroup.isLinkedToCampaignEndDateFlag();
        }

        if (linked) {
            creativeGroup.setDateEnd(null);
        }

        // set optInStatusTargeting
        if (currentUserService.isExternal()) {
            campaign.unregisterChange("optInStatusTargeting");
        }

        if (!creativeGroup.isSequentialAdservingFlag()) {
            creativeGroup.setRotationCriteria(null);
        }

        // min uid age
        if (existingCCG == null) {
            if (!creativeGroup.isChanged("optInStatusTargeting")) {
                creativeGroup.setOptInStatusTargeting(OptInStatusTargeting.newDefaultValue());
            }
            if (!creativeGroup.isChanged("minUidAge")) {
                creativeGroup.setMinUidAge(0L);
            }
        } else if (creativeGroup.isChanged("optInStatusTargeting") &&
                   (creativeGroup.getOptInStatusTargeting() == null ||
                    !creativeGroup.getOptInStatusTargeting().isOptedInUsers()) &&
                   !creativeGroup.isChanged("minUidAge")) {
            creativeGroup.setMinUidAge(0L);
        }
    }

    private void prePersistGeoChannels(CampaignCreativeGroup group) {
        if (group.isChanged("geoChannels")) {
            Set<GeoChannel> persistedChannels = new LinkedHashSet<GeoChannel>();
            for (GeoChannel channel : group.getGeoChannels()) {
                GeoChannel persisted = em.find(GeoChannel.class, channel.getId());
                persistedChannels.add(persisted);
            }
            group.setGeoChannels(persistedChannels);
        }

        if (group.isChanged("geoChannelsExcluded")) {
            Set<GeoChannel> persistedChannelsExcluded = new LinkedHashSet<GeoChannel>();
            for (GeoChannel channel : group.getGeoChannelsExcluded()) {
                GeoChannel persisted = em.find(GeoChannel.class, channel.getId());
                persistedChannelsExcluded.add(persisted);
            }
            group.setGeoChannelsExcluded(persistedChannelsExcluded);
        }
    }

    private void prePersistDeviceChannels(CampaignCreativeGroup group, CampaignCreativeGroup existing) {
        AccountType accountType = group.getAccount().getAccountType();
        if (!group.isChanged("deviceChannels")) {
            if (existing != null) {
                return;
            }
            group.setDeviceChannels(getDefaultDeviceChannels(accountType));
        }

        if (group.getDeviceChannels().isEmpty()) {
            group.setDeviceChannels(getDefaultDeviceChannels(accountType));
        }

        Set<DeviceChannel> persistedChannels = deviceChannelService.getNormalizedDeviceChannelsCollection(
                EntityUtils.getEntityIds(group.getDeviceChannels()), getAllowedDeviceChannelIds(accountType));

        // If root channels ('Browsers', 'Applications') are chosen -> All devices channels chosen, so empty devices collection must be saved in DB
        group.getDeviceChannels().clear();
        if (!persistedChannels.contains(deviceChannelService.getBrowsersChannel()) ||
                !persistedChannels.contains(deviceChannelService.getApplicationsChannel())) {
            group.setDeviceChannels(persistedChannels);
        }
    }

    private Set<DeviceChannel> getDefaultDeviceChannels(AccountType accountType) {
        return new HashSet<>(accountType.getDeviceChannels());
    }

    private Set<Long> getAllowedDeviceChannelIds(AccountType accountType) {
        return EntityUtils.getEntityIds(accountType.getDeviceChannels());
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.createCopy", parameters = "find('CampaignCreativeGroup', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public Long createCopy(Long id) {
        CampaignCreativeGroup ccg = em.find(CampaignCreativeGroup.class, id);
        return createCopyInternal(ccg, ccg.getCampaign(), true);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public Long createCopyWithNewCampaign(Long id, Long campaignId) {
        CampaignCreativeGroup ccg = em.find(CampaignCreativeGroup.class, id);
        return createCopyInternal(ccg, em.getReference(Campaign.class, campaignId), false);
    }

    private Long createCopyInternal(CampaignCreativeGroup existingCreativeGroup, Campaign campaign, boolean calculateNameForCopy) {
        CampaignCreativeGroup newCreativeGroup = EntityUtils.clone(existingCreativeGroup);
        newCreativeGroup.setCampaign(campaign);
        newCreativeGroup.setStatus(Status.INACTIVE);

        if (calculateNameForCopy) {
            String name = utilityService.calculateNameForCopy(existingCreativeGroup, 100);
            newCreativeGroup.setName(name);
        }

        Set<CampaignCreative> newCampaignCreatives = newCreativeGroup.getCampaignCreatives();
        newCreativeGroup.setCampaignCreatives(new LinkedHashSet<CampaignCreative>());

        if (existingCreativeGroup.getCountry() != null) {
            newCreativeGroup.setCountry(em.getReference(Country.class, existingCreativeGroup.getCountry().getCountryCode()));
        }

        createInternal1(newCreativeGroup);

        for (CampaignCreative campaignCreative : newCampaignCreatives) {
            campaignCreative.setCreativeGroup(newCreativeGroup);
            em.persist(campaignCreative);
            newCreativeGroup.getCampaignCreatives().add(campaignCreative);
        }
        em.flush();

        if (CCGType.TEXT == existingCreativeGroup.getCcgType()) {
            ccgKeywordService.update(newCreativeGroup.getCcgKeywords(), newCreativeGroup.getId(), newCreativeGroup.getVersion());
        }

        return newCreativeGroup.getId();
    }

    private Object[] initCacheKey(CampaignCreativeGroup creativeGroup, boolean showSiteStats) {
        final boolean showColocationStats = currentUserService.isInternal();
        Set<Long> siteIds = new TreeSet<Long>(org.apache.commons.collections.CollectionUtils.collect(creativeGroup.getSites(), new TransformerImplementation()));
        Set<Long> colocationIds = new TreeSet<Long>(org.apache.commons.collections.CollectionUtils.collect(creativeGroup.getColocations(), new TransformerImplementation()));
        Object[] key = {
                showSiteStats,
                showColocationStats,
                creativeGroup.getTargetingChannelId(),
                siteIds,
                colocationIds,
                creativeGroup.getUserSampleGroupStart(),
                creativeGroup.getUserSampleGroupEnd(),
                creativeGroup.getCountry()
        };
        return key;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.create", parameters = "find('Campaign', #creativeGroup.campaign.id)")
    @Validate(validation = "CampaignCreativeGroup.create", parameters = "#creativeGroup")
    @Interceptors({ CaptureChangesInterceptor.class, AutoFlushInterceptor.class })
    public Long create(CampaignCreativeGroup creativeGroup) {
        creativeGroup.unregisterChange("status");
        return createInternal(creativeGroup);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.createCampaignCCGs", parameters = "#creativeGroups")
    @Validate(validation = "CampaignCreativeGroup.createAllTargeted", parameters = "#creativeGroups")
    @Interceptors({ CaptureChangesInterceptor.class, AutoFlushInterceptor.class })
    public void createAll(List<CampaignCreativeGroup> creativeGroups) {
        for (CampaignCreativeGroup creativeGroup : creativeGroups) {
            create(creativeGroup);
        }
    }

    private Long createInternal(CampaignCreativeGroup creativeGroup) {
        Campaign campaign = em.find(Campaign.class, creativeGroup.getCampaign().getId());
        prePersist(creativeGroup, null, campaign);

        return createInternal1(creativeGroup);
    }

    /**
     * Creates a new CCG
     * @param creativeGroup a new CCG to create. Corresponding referenced objects must be managed objects
     * @return id of created CCG
     */
    private Long createInternal1(CampaignCreativeGroup creativeGroup) {
        if (creativeGroup.getStatus() == null) {
            creativeGroup.setStatus(Status.INACTIVE);
        }
        creativeGroup.setQaStatus(ApproveStatus.APPROVED);
        creativeGroup.setQaUser(null);
        creativeGroup.setQaDate(null);
        creativeGroup.setQaDescription(null);
        creativeGroup.setDisplayStatus(CampaignCreativeGroup.INACTIVE);
        if (creativeGroup.getCcgType() == CCGType.TEXT) {
            creativeGroup.setDeliveryPacing(DeliveryPacing.FIXED);
        }

        CcgRate ccgRate = creativeGroup.getCcgRate();
        ccgRate.setEffectiveDate(new Date(System.currentTimeMillis()));
        creativeGroup.setCcgRate(null);

        Channel channel = creativeGroup.getChannel();
        creativeGroup.setChannel(null);

        if (creativeGroup.getChannelTarget() == null || creativeGroup.getChannelTarget() == ChannelTarget.TARGETED) {
            creativeGroup.setChannelTarget(ChannelTarget.NOT_SET);
        }

        auditService.audit(creativeGroup, ActionType.CREATE);

        em.persist(creativeGroup);

        em.persist(ccgRate);

        creativeGroup.setCcgRate(ccgRate);

        if (channel != null) {
            creativeGroup.setChannel(channel);
            creativeGroup.setChannelTarget(ChannelTarget.TARGETED);
        }

        statusService.makePendingOnChange(creativeGroup, creativeGroup.isChanged("status"));
        displayStatusService.update(creativeGroup);
        targetingChannelService.addToBulkLink(creativeGroup);

        return creativeGroup.getId();
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('CampaignCreativeGroup', #creativeGroup.id)")
    @Validate(validation = "CampaignCreativeGroup.update", parameters = "#creativeGroup")
    @Interceptors({ CaptureChangesInterceptor.class, AutoFlushInterceptor.class })
    public CampaignCreativeGroup update(CampaignCreativeGroup creativeGroup) {
        creativeGroup.unregisterChange(
            "status",
            "channel",
            "channelTarget",
            "geoChannels",
            "geoChannelsExcluded",
            "deviceChannels"
        );
        return updateInternal(creativeGroup);
    }

    private CampaignCreativeGroup updateInternal(final CampaignCreativeGroup creativeGroup) {
        // prevent coping
        creativeGroup.unregisterChange(
            "id",
            "ccgKeywords",
            "campaignCreatives",
            "ccgType",
            "tgtType",
            "qaUser",
            "qaDescription",
            "qaDate",
            "qaStatus",
            "campaign"
            );

        CampaignCreativeGroup existingCCG = find(creativeGroup.getId());
        if (!advertiserEntityRestrictions.canEditSiteTargeting(existingCCG.getAccount())) {
            creativeGroup.unregisterChange("sites");
        }


        if (!creativeGroup.isChanged("version")) {
            creativeGroup.setVersion(existingCCG.getVersion());
        }

        if (creativeGroup.getCcgType() == CCGType.TEXT) {
            creativeGroup.setDeliveryPacing(DeliveryPacing.FIXED);
        }

        prePersist(creativeGroup, existingCCG, existingCCG.getCampaign());
        creativeGroup.setCampaign(existingCCG.getCampaign());

        auditService.audit(existingCCG, ActionType.UPDATE);

        // Frequency Caps
        boolean isFrequencyCapChanged = frequencyCapMerger.merge(creativeGroup, existingCCG);

        // Rates
        if (creativeGroup.isChanged("ccgRate")) {
            CcgRate oldRate = existingCCG.getCcgRate();
            CcgRate newRate = creativeGroup.getCcgRate();
            newRate.setEffectiveDate(new Date());
            boolean rateIsChanged = !newRate.compareFields(oldRate);
            if (rateIsChanged) {
                if (RateType.CPM == oldRate.getRateType() && currentUserService.isExternal()) {
                    creativeGroup.setOptimizeCreativeWeightFlag(true);
                }
            } else {
                creativeGroup.unregisterChange("ccgRate");
            }
        }

        if (creativeGroup.isChanged("bidStrategy")) {
            BidStrategy newBidStrategy = creativeGroup.getBidStrategy();
            if (BidStrategy.MAXIMISE_REACH.equals(newBidStrategy)) {
                creativeGroup.unregisterChange("minCtrGoal");
            }
        }

        ccgScheduleCapMerger.merge(creativeGroup, existingCCG.getCcgSchedules(), creativeGroup.getCcgSchedules(), "ccgSchedules");

        if (creativeGroup.isChanged("sites")) {
            new CollectionMerger<>(existingCCG.getSites(), creativeGroup.getSites()).merge();
            creativeGroup.unregisterChange("sites");
        }

        if (creativeGroup.isChanged("colocations")) {
            new CollectionMerger<>(existingCCG.getColocations(), creativeGroup.getColocations()).merge();
            creativeGroup.unregisterChange("colocations");
        }

        if (creativeGroup.isChanged("actions")) {
            new CollectionMerger<>(existingCCG.getActions(), creativeGroup.getActions()).merge();
            creativeGroup.unregisterChange("actions");
        }

        if (creativeGroup.isChanged("geoChannels")) {
            new CollectionMerger<>(existingCCG.getGeoChannels(), creativeGroup.getGeoChannels()).merge();
            creativeGroup.unregisterChange("geoChannels");
        }

        if (creativeGroup.isChanged("geoChannelsExcluded")) {
            new CollectionMerger<>(existingCCG.getGeoChannelsExcluded(), creativeGroup.getGeoChannelsExcluded()).merge();
            creativeGroup.unregisterChange("geoChannelsExcluded");
        }


        if (creativeGroup.isChanged("deviceChannels")) {
            new CollectionMerger<>(existingCCG.getDeviceChannels(), creativeGroup.getDeviceChannels()).merge();
            creativeGroup.unregisterChange("deviceChannels");
        }

        if (creativeGroup.isChanged("dateStart") && existingCCG.getCalculatedStartDate().compareTo(creativeGroup.getDateStart()) == 0) {
            creativeGroup.unregisterChange("dateStart");
        }

        boolean isStatusChanged = creativeGroup.isChanged("status");
        boolean isExternalFlagsChanged = Flags.isDifferent(
                existingCCG.getFlagsObject(), creativeGroup.getFlagsObject(), CampaignCreativeGroup.EXTERNAL_MASK);
        boolean isInternalFlagsChanged = Flags.isDifferent(
                existingCCG.getFlagsObject(), creativeGroup.getFlagsObject(), CampaignCreativeGroup.INTERNAL_MASK);

        CampaignCreativeGroup mergedCCG = em.merge(creativeGroup);

        boolean isChangedExternal = isExternalFieldsChanged(mergedCCG) || isExternalFlagsChanged || isFrequencyCapChanged;

        boolean isChangedInternal = isInternalFieldsChanged(mergedCCG) || isInternalFlagsChanged;

        if (isChangedExternal || isStatusChanged) {
            //            statusService.makePendingOnChange(mergedCCG, isStatusChanged);
        }
        if (isChangedExternal || isChangedInternal) {
            approvalService.makePendingOnChange(mergedCCG);
        }

        displayStatusService.update(mergedCCG);

        if (mergedCCG.isChanged()) {
            PersistenceUtils.performHibernateLock(em, mergedCCG);
        }

        boolean isGeoChannelsChanged = mergedCCG.isChanged("geoChannels") || mergedCCG.isChanged("geoChannelsExcluded");
        boolean isDeviceChannelsChanged = mergedCCG.isChanged("deviceChannels");
        boolean isChannelTargetChanged = mergedCCG.isChanged("channel");
        if (isGeoChannelsChanged || isDeviceChannelsChanged || isChannelTargetChanged) {
            targetingChannelService.addToBulkLink(mergedCCG);
        }

        return mergedCCG;
    }

    private boolean isExternalFieldsChanged(CampaignCreativeGroup group) {
        boolean ccgChanged = group.isChanged(
            "name",
            "budget",
            "dailyBudget",
            "ccgRate",
            "country",
            "dateStart",
            "dateEnd",
            "ccgSchedules",
            "deliveryPacing"
            );
        return ccgChanged;
    }

    private boolean isInternalFieldsChanged(CampaignCreativeGroup group) {
        return group.isChanged("sites") || group.isChanged("colocations");
    }

    @Override
    @Restrict(restriction = "CreativeGroup.updateDeviceTargeting", parameters = "find('CampaignCreativeGroup', #group.id)")
    @Validate(validation = "CampaignCreativeGroup.updateDeviceTargeting", parameters = "#group")
    @Interceptors(CaptureChangesInterceptor.class)
    public void updateDeviceTargeting(CampaignCreativeGroup group) {
        CampaignCreativeGroup existing = find(group.getId());
        if (!existing.getVersion().equals(group.getVersion())) {
            throw new VersionCollisionException();
        }
        prePersistDeviceChannels(group, existing);
        existing.setDeviceChannels(group.getDeviceChannels());

        existing = em.merge(existing);
        auditService.audit(existing, ActionType.UPDATE);
        displayStatusService.update(existing);
        targetingChannelService.addToBulkLink(existing);
    }

    @Override
    @Restrict(restriction = "CreativeGroup.updateChannelTarget", parameters = "find('CampaignCreativeGroup', #group.id)")
    @Validate(validation = "CampaignCreativeGroup.updateTarget", parameters = "#group")
    @Interceptors(CaptureChangesInterceptor.class)
    public void updateTarget(CampaignCreativeGroup group) {
        CampaignCreativeGroup existing;
        Long channelId = group.getChannel() != null ? group.getChannel().getId() : null;
        Channel channel = channelId == null ? null : em.find(Channel.class, channelId);
        group.setChannel(channel);

        existing = em.merge(group);
        auditService.audit(existing, ActionType.UPDATE);
        displayStatusService.update(existing);
        targetingChannelService.addToBulkLink(existing);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.delete", parameters = "find('CampaignCreativeGroup', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void delete(Long id) {
        CampaignCreativeGroup ccg = find(id);
        delete(ccg);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.undelete", parameters = "find('CampaignCreativeGroup', #id)")
    @Interceptors({ CaptureChangesInterceptor.class, AutoFlushInterceptor.class })
    public void undelete(Long id) {
        CampaignCreativeGroup ccg = find(id);
        undelete(ccg);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.inactivate", parameters = "find('CampaignCreativeGroup', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void inactivate(Long id) {
        CampaignCreativeGroup ccg = find(id);
        inactivate(ccg);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.activate", parameters = "find('CampaignCreativeGroup', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void activate(Long id) {
        CampaignCreativeGroup ccg = find(id);
        activate(ccg);
    }

    private void activate(CampaignCreativeGroup ccg) {
        statusService.activate(ccg);
    }

    private void inactivate(CampaignCreativeGroup ccg) {
        statusService.inactivate(ccg);
    }

    private void delete(CampaignCreativeGroup ccg) {
        statusService.delete(ccg);
    }

    private void undelete(CampaignCreativeGroup ccg) {
        statusService.undelete(ccg);
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.approve", parameters = "find('CampaignCreativeGroup', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void approve(Long id) {
        approvalService.approve(find(id));
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.decline", parameters = "find('CampaignCreativeGroup', #id)")
    @Interceptors(CaptureChangesInterceptor.class)
    public void decline(Long id, String dsc) {
        approvalService.decline(find(id), dsc);
    }

    @Override
    public CampaignCreativeGroup find(Long id) {
        CampaignCreativeGroup res = em.find(CampaignCreativeGroup.class, id);
        if (res == null) {
            throw new EntityNotFoundException("CampaignCreativeGroup with id=" + id + " not found");
        }
        PersistenceUtils.initialize(res.getCcgSchedules());
        return res;
    }

    @Override
    public CampaignCreativeGroup findWithCC(Long id) {
        CampaignCreativeGroup ccg = find(id);
        PersistenceUtils.initialize(ccg.getCampaignCreatives());
        return ccg;
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "find('CampaignCreativeGroup', #id)")
    public CampaignCreativeGroup view(Long id) {
        CampaignCreativeGroup ccg = find(id);
        PersistenceUtils.initialize(ccg.getCcgSchedules());
        PersistenceUtils.initialize(ccg.getGeoChannels());
        PersistenceUtils.initialize(ccg.getGeoChannelsExcluded());
        PersistenceUtils.initialize(ccg.getAccount().getAccountType().getDeviceChannels());
        PersistenceUtils.initialize(ccg.getActions());
        PersistenceUtils.initialize(ccg.getDeviceChannels());
        return ccg;
    }

    @Override
    public void refresh(Long id) {
        CampaignCreativeGroup ccg = find(id);
        em.refresh(ccg);
    }

    @Override
    public List<CombinedCCGKeywordTO> getLinkedKeywords(Long ccgId, LocalDate dateFrom, LocalDate dateTo) {
        String sql = "select * from statqueries.linked_keywords(?, ?, ?, ?)";

        // OUI-22680: set fraction digits for ecpm
        CampaignCreativeGroup ccg = find(ccgId);
        int fractionDigits = ccg.getAccount().getCurrency().getFractionDigits();

        KeywordRowCallbackHandler handler = new KeywordRowCallbackHandler(fractionDigits);
        jdbcTemplate.query(sql,
            new Object[] {
                    ccgId,
                    dateFrom,
                    dateTo,
                    userService.getMyUser().isDeletedObjectsVisible()
            },
            new int[] {
                    Types.INTEGER,
                    Types.DATE,
                    Types.DATE,
                    Types.BOOLEAN
            },
            handler);

        return handler.getKeywords();
    }

    private final class TransformerImplementation implements Transformer {
        @Override
        public Object transform(Object input) {
            return ((Identifiable) input).getId();
        }
    }

    private class KeywordRowCallbackHandler implements RowCallbackHandler {
        private LinkedHashMap<String, CombinedCCGKeywordTO> keywordsMap = new LinkedHashMap<>();
        private int fractionDigits;

        public KeywordRowCallbackHandler(int fractionDigits) {
            this.fractionDigits = fractionDigits;
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            long id = rs.getLong("ccg_keyword_id");
            String originalKeyword = rs.getString("original_keyword");
            BigDecimal maxCpcBid = rs.getBigDecimal("max_cpc_bid");
            BigDecimal imps = rs.getBigDecimal("imps");
            BigDecimal clicks = rs.getBigDecimal("clicks");
            BigDecimal ctr = rs.getBigDecimal("ctr");
            BigDecimal ecpm = rs.getBigDecimal("ecpm");
            BigDecimal cost = rs.getBigDecimal("cost");
            DisplayStatus displayStatusId = CCGKeyword.getDisplayStatus(rs.getLong("display_status_id"));
            Long channelId = rs.getLong("channel_id");
            BigDecimal averageActualCpc = rs.getBigDecimal("average_actual_cpc");
            BigDecimal audience = rs.getBigDecimal("audience_uu");
            KeywordTriggerType triggerType = KeywordTriggerType.byLetter(rs.getString("trigger_type").charAt(0));

            CombinedCCGKeywordTO combinedKeyword = keywordsMap.get(originalKeyword);
            if (combinedKeyword == null) {
                combinedKeyword = new CombinedCCGKeywordTO(originalKeyword, fractionDigits);
                keywordsMap.put(originalKeyword, combinedKeyword);
            }

            CCGKeywordTO keyword = new CCGKeywordTO(
                id,
                channelId,
                triggerType,
                maxCpcBid,
                imps,
                clicks,
                ctr,
                ecpm,
                cost,
                averageActualCpc,
                audience,
                displayStatusId,
                originalKeyword.charAt(0) == '-'
                    );

            combinedKeyword.getKeywords().add(keyword);
        }

        public List<CombinedCCGKeywordTO> getKeywords() {
            return new ArrayList<>(keywordsMap.values());
        }
    }

    @Override
    public List<LinkedConversionTO> getLinkedConversions(Long ccgId, LocalDate dateFrom, LocalDate dateTo) {
        boolean showDeleted = userService.getMyUser().isDeletedObjectsVisible();

        return jdbcTemplate.query("select * from statqueries.actionstats_for_ccg(?, ?, ?, ?)",
                new Object[] {
                        ccgId,
                        dateFrom,
                        dateTo,
                        showDeleted
                },
                new int[] {
                        Types.INTEGER,
                        Types.DATE,
                        Types.DATE,
                        Types.BOOLEAN
                },
                new RowMapper<LinkedConversionTO>() {
                    @Override
                    public LinkedConversionTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new LinkedConversionTO.Builder()
                                .id(rs.getLong("action_id"))
                                .name(rs.getString("name"))
                                .status(Status.valueOf(rs.getString("action_status").charAt(0)))
                                .displayStatus(Action.getDisplayStatus(rs.getLong("action_display_status_id")))
                                .category(ConversionCategory.valueOf((int) rs.getLong("conv_category_id")))
                                .impConv(rs.getLong("imp_conv"))
                                .impCR(rs.getBigDecimal("imp_cr"))
                                .clickConv(rs.getLong("click_conv"))
                                .clickCR(rs.getBigDecimal("click_cr")).build();
                    }
                });
    }

    @Override
    public PartialCreativeSetTOList getLinkedCreatives(Long ccgId, LocalDate dateFrom, LocalDate dateTo, int from, int count) {
        CampaignCreativeGroup campaignCreativeGroup = find(ccgId);
        boolean isGross = campaignCreativeGroup.getAccount().getAccountType().isInputRatesAndAmountsFlag();
        boolean showDeleted = userService.getMyUser().isDeletedObjectsVisible();
        int total = getLinkedCreativesNumber(ccgId, showDeleted);

        if (total == 0 || from > total) {
            return PartialCreativeSetTOList.emptyList();
        }

        LinkedCreativesTOList linkedCreatives = getLinkedCreativeTO(ccgId, dateFrom, dateTo, from, count, isGross, showDeleted);

        List<CreativeSetTO> creativeSets = new ArrayList<>();
        if (campaignCreativeGroup.isSequentialAdservingFlag()) {
            Map<Long, CreativeSetTO> creativeSetMap = getCreativeSetTO(ccgId, dateFrom, dateTo, isGross, showDeleted);

            for (BaseLinkedTO baseLinkedTO : linkedCreatives) {
                CreativeSetTO setTO = creativeSetMap.get(baseLinkedTO.getSetNumber());
                if (setTO != null) {
                    setTO.addTO(baseLinkedTO);
                }
            }

            creativeSets.addAll(creativeSetMap.values());

            org.apache.commons.collections.CollectionUtils.filter(creativeSets, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    return !((CreativeSetTO) object).getLinkedTOs().isEmpty();
                }
            });
        } else {
            CreativeSetTO creativeSetTO = new CreativeSetTO(new ImpClickStatsTO.Builder());
            creativeSetTO.getLinkedTOs().addAll(linkedCreatives);
            creativeSets.add(creativeSetTO);
        }

        return new PartialCreativeSetTOList(total, from, creativeSets, linkedCreatives.isShowPostImpConv(), linkedCreatives.isShowPostClickConv());
    }

    private Map<Long, CreativeSetTO> getCreativeSetTO(Long ccgId, LocalDate dateFrom, LocalDate dateTo, boolean isGross, Boolean showDeleted) {
        Map<Long, CreativeSetTO> result = new LinkedHashMap<>();

        SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "select * from statqueries.get_setstats_for_ccg(?::int, ?, ?, ?)",
                ccgId, dateFrom, dateTo, showDeleted
        );

        while (rs.next()) {
            long setNumber = rs.getLong("set_number");
            BigDecimal inventoryCost = rs.getBigDecimal("adv_amount");
            if (isGross) {
                inventoryCost = inventoryCost.add(rs.getBigDecimal("adv_comm_amount"));
            }

            Builder builder = new ImpClickStatsTO.Builder()
                    .imps(rs.getLong("imps"))
                    .clicks(rs.getLong("clicks"));
            CreativeSetTO cc = new CreativeSetTO(builder);
            cc.setSetNumber(setNumber);
            cc.setInventoryCost(inventoryCost);
            cc.setTargetingCost(rs.getBigDecimal("adv_amount_cmp"));
            cc.setCreditUsed(rs.getBigDecimal("campaign_credit_used"));
            result.put(setNumber, cc);
        }

        return result;
    }

    private LinkedCreativesTOList getLinkedCreativeTO(Long ccgId, LocalDate dateFrom, LocalDate dateTo, int from, int count, boolean isGross, boolean showDeleted) {
        final LinkedCreativesTOList result = new LinkedCreativesTOList();

        jdbcTemplate.query("select * from statqueries.cc_stats_for_ccg(?, ?, ?, ?, ?, ?, ?)",
                new Object[]{
                        ccgId,
                        dateFrom,
                        dateTo,
                        showDeleted,
                        isGross,
                        from,
                        count
                },
                new int[]{
                        Types.INTEGER,
                        Types.DATE,
                        Types.DATE,
                        Types.BOOLEAN,
                        Types.BOOLEAN,
                        Types.INTEGER,
                        Types.INTEGER
                },
                new RowMapper<Object>() {
                    @Override
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {

                        ImpClickStatsTO.Builder builder =  new ImpClickStatsTO.Builder()
                        .imps(rs.getLong("imps"))
                        .clicks(rs.getLong("clicks"))
                        .postImpConv(rs.getLong("imp_conv"));
                        if (!rs.wasNull()) {
                            result.setShowPostImpConv(true);
                        }

                        builder.postClickConv(rs.getLong("click_conv"));
                        if (!rs.wasNull()) {
                            result.setShowPostClickConv(true);
                        }

                        LinkedCreativeTO cc = new LinkedCreativeTO(builder);
                        cc.setId(rs.getLong("cc_id"));
                        cc.setCreativeName(rs.getString("creative_name"));
                        cc.setCreativeId(rs.getLong("creative_id"));
                        cc.setDisplayStatus(CampaignCreative.getDisplayStatus(rs.getLong("cc_display_status_id")));
                        cc.setCreativeDisplayStatus(Creative.getDisplayStatus(rs.getLong("creative_display_status_id")));
                        cc.setStatus(Status.valueOf(rs.getString("creative_status").charAt(0)));
                        cc.setSizeId(rs.getLong("size_id"));
                        cc.setSizeName(new LocalizableName(rs.getString("size_name"), "CreativeSize." + cc.getSizeId()));
                        cc.setInventoryCost(rs.getBigDecimal("adv_amount"));
                        cc.setTargetingCost(rs.getBigDecimal("adv_amount_cmp"));
                        cc.setCreditUsed(rs.getBigDecimal("campaign_credit_used"));
                        cc.setUniqueUsers(rs.getLong("unique_users"));
                        cc.setSetNumber(rs.getLong("set_number"));
                        cc.setTemplateId(rs.getLong("template_id"));

                        result.add(cc);
                        return null;
                    }
                });
        return result;
    }

    private int getLinkedCreativesNumber(Long ccgId, Boolean showDeleted) {
        return jdbcTemplate.queryForObject("select * from entityqueries.cc_count_for_ccg(?::int, ?)",
                new Object[] { ccgId, showDeleted },
                Integer.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    @Restrict(restriction = "Entity.view", parameters = "find('Campaign', #campaignId)")
    public List<EntityTO> getIndex(Long campaignId) {
        Query q;
        if (userService.getMyUser().isDeletedObjectsVisible()) {
            q = em.createNamedQuery("CampaignCreativeGroup.entityTO.findByCampaignId");
        } else {
            q = em.createNamedQuery("CampaignCreativeGroup.entityTO.findByCampaignIdForExternal");
        }

        return q.setParameter("campaignId", campaignId).getResultList();
    }

    @Override
    public List<EntityTO> getIndexByIds(Collection<Long> ccgIds) {
        //noinspection unchecked
        return em.createQuery("select new com.foros.session.EntityTO(c.id, c.name, c.status) " +
                              " from CampaignCreativeGroup c WHERE " + SQLUtil.formatINClause("c.id", ccgIds))
                            .getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    @Restrict(restriction = "Entity.view", parameters = "find('Campaign', #campaignId)")
    public List<EntityTO> getIndexByTargetType(Long campaignId, TGTType targetType) {
        Query q;
        if (userService.getMyUser().isDeletedObjectsVisible()) {
            q = em.createNamedQuery("CampaignCreativeGroup.entityTO.findByCampaignIdAndTargetType");
        } else {
            q = em.createNamedQuery("CampaignCreativeGroup.entityTO.findByCampaignIdAndTargetTypeForExternal");
        }
        return q.setParameter("campaignId", campaignId).setParameter("targetType", targetType).getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    /**
     * @param filterByCountry is added to optionally exclude sites of publishers whose country differs from site advertiser one @see OUI-24235 and
     */
    public Collection<EntityTO> fetchLinkedSites(Long ccgId, boolean filterByCountry) {
        String query =
                "select s.site_id, s.name sitename, s.status, a.name accountname, a.status accountstatus" +
                "  from  CCGSITE cs " +
                "  left join site s on s.site_id = cs.site_id " +
                "  join Account a on a.account_id = s.account_id  " +
                "  where cs.ccg_id = ?::int " +
                (filterByCountry ? " and a.country_code = ?" : "");

        List<Object> params = new ArrayList<>(2);
        params.add(ccgId);
        if (filterByCountry) {
            params.add(find(ccgId).getAccount().getCountry().getCountryCode());
        }

        return jdbcTemplate.query(
                query,
                params.toArray(),
                new RowMapper<EntityTO>() {
                    @Override
                    public EntityTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Long siteId = rs.getLong(1);
                        String siteName = rs.getString(2);
                        String accountName = rs.getString(4);
                        char accountStatus = rs.getString(5).charAt(0);
                        accountName = EntityUtils.appendStatusSuffix(accountName, Status.valueOf(accountStatus));
                        String name = accountName + " / " + siteName;
                        char status = rs.getString(3).charAt(0);

                        return new EntityTO(siteId, name, status);
                    }
                });
    }

    @Override
    @SuppressWarnings("unchecked")
    public Collection<EntityTO> fetchTargetableSites(final boolean testAccount, String countryCode) {
        String query =
                "select s.site_id, s.name sitename,  a.name accountname, s.status status" +
                "  from Site s join Account a on a.account_id=s.account_id" +
                "  where s.status <> 'D'" +
                "  and a.status <> 'D'" +
                "  and a.country_code = ? " +
                (testAccount ? "" : " and not (a.flags & " + Account.TEST_FLAG + ")::bool");

        return jdbcTemplate.query(
                query,
                new Object[]{countryCode},
                new RowMapper<EntityTO>() {
                    @Override
                    public EntityTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        EntityTO site = new EntityTO();
                        site.setId(rs.getLong(1));
                        site.setName(rs.getString(3) + " / " + rs.getString(2));
                        site.setStatus(Status.valueOf(rs.getString(4).charAt(0)));

                        return site;
                    }
                });
    }

    @Override
    public Collection<ISPColocationTO> findLinkedColocations(Long id) {
        String query =
            "select c.colo_id, c.name coloname, c.status, a.account_id, a.name accountname" +
            " from CCGColocation cc" +
            " join Colocation c on c.colo_id = cc.colo_id" +
            " join Account a on a.account_id = c.account_id" +
            " where cc.ccg_id = ?";
        return jdbcTemplate.query(query, new Object[]{id}, new ColocationRowMapper());
    }

    @Override
    public Collection<ISPColocationTO> findColocationsByIds(Set<Long> ids) {
        String query =
                "select c.colo_id, c.name coloname, c.status, a.account_id, a.name accountname " +
                " from Colocation c join Account a on a.account_id = c.account_id " +
                " where c.colo_id = any(?::int[])";
        return jdbcTemplate.query(
                query,
                new Object[]{jdbcTemplate.createArray("int", ids)},
                new ColocationRowMapper()
        );
    }

    @Override
    public Collection<ISPColocationTO> findColocationsByName(String name, String countryCode, boolean testAccount, int maxResults) {
        String query =
            " select c.colo_id, c.name coloname, c.status, account_id, a.name accountname" +
            "  from Colocation c join Account a using(account_id)" +
            "  where c.status not in ('I','D')" +
            "  and a.status not in ('I','D')" +
            "  and a.country_code = ?" +
            (testAccount ? "" : " and not (a.flags & " + Account.TEST_FLAG + ")::bool ") +
            "  and (upper(c.name) like ? or upper(a.name) like ?)" +
            "  order by a.name, c.name" +
            "  limit ?::int";

        String nameToSearch = SQLUtil.getLikeEscape(name);
        return jdbcTemplate.query(query,
                new Object[] {
                        countryCode,
                        nameToSearch,
                        nameToSearch,
                        maxResults
                },
                new ColocationRowMapper()
        );
    }

    @Override
    public CCGTargetingStatsTO fetchTargetingStats(final Long ccgId, boolean withSiteStats) {
        CampaignCreativeGroup creativeGroup = find(ccgId);

        CacheRegion region = cacheProviderService.getCache().getRegion("Report.TargetingStats");

        final boolean showSiteStats = isShowSiteStats(creativeGroup, withSiteStats);
        Object[] key = initCacheKey(creativeGroup, showSiteStats);

        final boolean showColocationStats = currentUserService.isInternal();
        final Object[] parameters = { ccgId, showSiteStats, showColocationStats };
        return region.get(key, new CreateValueCallback<CCGTargetingStatsTO, Object[]>() {
            @Override
            public CCGTargetingStatsTO create(Object[] params, Collection<?> tags) {
                return jdbcTemplate.queryForObject(
                        "select * from Report.TargetingStats(?::int, ?::bool, ?::bool)",
                        parameters,
                        new RowMapper<CCGTargetingStatsTO>() {
                            @Override
                            public CCGTargetingStatsTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                                CCGTargetingStatsTO to = new CCGTargetingStatsTO();

                                to.setBehaviors(parseTO(rs.getString("behaviours")));
                                to.setCountry(parseTO(rs.getString("country")));
                                to.setGeolocations(parseMap(rs.getArray("geolocations")));
                                to.setDevices(parseMap(rs.getArray("devices")));
                                to.setColocations(parseTO(rs.getString("colos")));
                                to.setSites(parseMap(rs.getArray("sites")));
                                to.setUserSampleGroups(parseTO(rs.getString("userSampleGroups")));
                                to.setTotal(parseTO(rs.getString("total")));

                                return to;
                            }

                            Map<Long, TargetingStatsTO> parseMap(Array array) throws SQLException {
                                if (array == null) {
                                    return null;
                                }

                                return PGArray.read(array, new Mapper<PGRow, Long, TargetingStatsTO>() {
                                    @Override
                                    public Pair<Long, TargetingStatsTO> item(PGRow row) {
                                        return new Pair<Long, TargetingStatsTO>(
                                                row.getLong(0),
                                                new TargetingStatsTO(row.getLong(1), row.getLong(2))
                                        );
                                    }
                                });
                            }

                            TargetingStatsTO parseTO(String value) throws SQLException {
                                return value == null ? null :
                                        PGRow.read(value, new PGRow.Converter<TargetingStatsTO>() {
                                    @Override
                                    public TargetingStatsTO item(PGRow row) {
                                        return new TargetingStatsTO(row.getLong(0), row.getLong(1));
                                    }
                                });
                            }
                        }
                );
            }
        });
    }

    private boolean isShowSiteStats(CampaignCreativeGroup creativeGroup, boolean withSiteStats) {
        if (!withSiteStats) {
            return false;
        }

        if (currentUserService.isInternal()) {
            return true;
        }

        return advertiserEntityRestrictions.canEditSiteTargeting(creativeGroup.getAccount());
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view", parameters = "find('CampaignCreativeGroup', #ccgId)")
    public ChartStats getChartStats(Long ccgId, String xspec, String y1spec, String y2spec) {
        AdvertiserAccount ccgAccount = em.find(CampaignCreativeGroup.class, ccgId).getAccount();
        TimeZone ccgTimeZone = TimeZone.getTimeZone(ccgAccount.getTimezone().getKey());
        ChartStats.ChartHelper helper = new ChartHelper(ccgTimeZone, xspec, y1spec, y2spec);

        String sql = "select adv_sdate, " + helper.getY1spec() + ", " + helper.getY2spec() +
                " from statqueries.ccg_stats_daily(?, ?, ?)";

        List<ChartStats.ChartEntry> result = jdbcTemplate.query(sql,
            new Object[] {
                    ccgId,
                    helper.getFromDate(),
                    helper.getToDate()
            },
            new int[] {
                    Types.INTEGER,
                    Types.DATE,
                    Types.DATE,
            }, helper.getRowMapper());
        String currencyCode = ccgAccount.getCurrency().getCurrencyCode();

        return new ChartStats(result, TimeZone.getDefault(), y1spec, y2spec, currencyCode);
    }

    @Override
    public long getTotalImpressions(Long ccgId) {
        return jdbcTemplate.queryForObject(
                "select * from statqueries.ccg_total_impressions(?)",
                new Object[] {ccgId},
                new int[] {Types.INTEGER},
                Long.class);
    }

    @Override
    public boolean isBatchActionPossible(Collection<Long> ids, String action) {
        String restrinctionName = "AdvertiserEntity." + action.toLowerCase();
        Collection<CampaignCreativeGroup> ccgs = new JpaQueryWrapper<CampaignCreativeGroup>(em,
            "select ccg from CampaignCreativeGroup ccg where ccg.id in :ids")
            .setPrimitiveArrayParameter("ids", ids)
            .getResultList();

        for (CampaignCreativeGroup ccg : ccgs) {
            if (!restrictionService.isPermitted(restrinctionName, ccg)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public CCGLightWeightStatsTO getLightWeightStats(final Long ccgId, final boolean isGross) {
        return jdbcTemplate.queryForObject("select * from statqueries.ccg_total_stats(?, ?)",
                new Object[] {
                        ccgId,
                        isGross
                },
                new int[] {
                        Types.INTEGER,
                        Types.BOOLEAN
                },
                new RowMapper<CCGLightWeightStatsTO>() {
                    @Override
                    public CCGLightWeightStatsTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                        Builder builder = new ImpClickStatsTO.Builder()
                        .imps(rs.getLong("imps"))
                        .clicks(rs.getLong("clicks"))
                        .postClickConv(rs.getLong("click_conv"))
                        .isShowPostClickConv(!rs.wasNull())
                        .postImpConv(rs.getLong("imp_conv"))
                        .isShowPostImpConv(!rs.wasNull());

                        long totalUniqueUsers = rs.getLong("total_reach");
                        BigDecimal amount = rs.getBigDecimal("amount");
                        BigDecimal auctionCtr = rs.getBigDecimal("auction_ctr");
                        BigDecimal auctionEcpm = rs.getBigDecimal("ecpm");
                        long auctionsLost = rs.getLong("auctions_lost");
                        long selectionFailures = rs.getLong("selection_failures");

                        return new CCGLightWeightStatsTO(builder, totalUniqueUsers, amount, auctionCtr,
                                auctionEcpm, auctionsLost, selectionFailures);
                    }
                });
    }

    @Override
    public ChannelRatesTO getChannelTargetingRates(Long channelId) {
        List<ChannelRate> channelRates = getChannelRates(channelId);

        return CollectionUtils.isNullOrEmpty(channelRates) ? null : new ChannelRatesTO(channelRates);
    }

    @Override
    public ChannelRatesTO getCcgTargetingRates(Long ccgId, Long channelId) {
        List<ChannelRate> channelRates = getChannelRates(channelId);
        if (CollectionUtils.isNullOrEmpty(channelRates)) {
            return null;
        }

        CampaignCreativeGroup ccg = find(ccgId);
        CurrencyConverter currencyConverter = currencyExchangeService
            .getCrossRate(ccg.getAccount().getCurrency().getId(), new Date());

        return new ChannelRatesTO(ccg.getCcgRate(), channelRates, currencyConverter);
    }

    @SuppressWarnings("unchecked")
    private List<ChannelRate> getChannelRates(Long channelId) {
        Channel channel = em.find(Channel.class, channelId);

        if (channel == null) {
            return Collections.emptyList();
        }

        boolean isAdvertisingNotExpression = channel instanceof BehavioralChannel || channel instanceof AudienceChannel;
        if (channel.getVisibility() == ChannelVisibility.PUB ||
                (isAdvertisingNotExpression && channel.getVisibility() == ChannelVisibility.PRI)) {
            return Collections.emptyList();
        }

        if (channel.getVisibility() == ChannelVisibility.CMP) {
            return Collections.singletonList(channel.getChannelRate());
        }

        String query = "select * from entityqueries.get_recursive_channel_rate_list(?)";
        List<Long> res = jdbcTemplate.queryForList(query, Long.class, channel.getId());
        if (res.isEmpty()) {
            return Collections.emptyList();
        }

        List<ChannelRate> rates = em.createQuery("SELECT c FROM ChannelRate c WHERE c.id IN (:ids)")
            .setParameter("ids", res).getResultList();

        return rates;
    }

    @Override
    public BigDecimal calculateDynamicDailyBudget(Long ccgId, Long campaignId, Long accountId, BigDecimal budget, Date startDate, Date endDate) {
        TimeZone timeZone;
        Date campaignEndDate;
        String accountCurrency;
        Campaign campaign;

        if (ccgId != null) {
            CampaignCreativeGroup ccg = find(ccgId);
            campaign = ccg.getCampaign();
            timeZone = TimeZone.getTimeZone(ccg.getAccount().getTimezone().getKey());
            campaignEndDate = ccg.getCampaign().getDateEnd();
            accountCurrency = ccg.getAccount().getCurrency().getCurrencyCode();
        } else if (campaignId != null) {
            campaign = campaignService.find(campaignId);
            timeZone = TimeZone.getTimeZone(campaign.getAccount().getTimezone().getKey());
            campaignEndDate = campaign.getDateEnd();
            accountCurrency = campaign.getAccount().getCurrency().getCurrencyCode();
        } else if (accountId != null) {
            campaign = null;
            Account account = em.find(Account.class, accountId);
            timeZone = TimeZone.getTimeZone(account.getTimezone().getKey());
            campaignEndDate = endDate;
            accountCurrency = account.getCurrency().getCurrencyCode();
        } else {
            return null;
        }

        if (endDate == null) {
            endDate = campaignEndDate;

            if (endDate == null) {
                return null;
            }
        }

        Calendar c = Calendar.getInstance(timeZone);

        c.setTime(new Date());
        Date today = DateUtils.truncate(c, Calendar.DAY_OF_MONTH).getTime();

        c.setTime(startDate);
        startDate = DateUtils.truncate(c, Calendar.DAY_OF_MONTH).getTime();

        c.setTime(endDate);
        endDate = DateUtils.truncate(c, Calendar.DAY_OF_MONTH).getTime();

        if (today.before(startDate)) {
            today = startDate;
        }

        if (endDate.before(today)) {
            return BigDecimal.ZERO;
        }

        long daysLeft = (endDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24) + 1;
        if (budget == null) {
            budget = BigDecimal.ZERO;
        }

        BigDecimal remainingBudget;

        if (ccgId != null) {
            remainingBudget = budget.subtract(getSpentCCGBudget(ccgId, timeZone, today));
        } else if (campaignId != null) {
            BigDecimal campaignBudget = campaign.getTotalBudget();

            if (campaignBudget == null || campaignBudget.compareTo(BigDecimal.ZERO) == 0) {
                remainingBudget = budget;
            } else {
                BigDecimal campaignRemainingBudget = campaignBudget.subtract(getSpentCampaignBudget(campaignId, timeZone, today));

                if (campaignRemainingBudget.compareTo(budget) >= 0) {
                    remainingBudget = budget;
                } else {
                    remainingBudget = campaignRemainingBudget;
                }
            }
        } else {
            Account account = em.find(Account.class, accountId);
            if ((budget == null || budget.compareTo(BigDecimal.ZERO) == 0) && !account.getAccountType().getIoManagement()) {
                return null;
            }

            remainingBudget = budget;
        }

        int defaultFractionDigits = Currency.getInstance(accountCurrency).getDefaultFractionDigits();

        // Scaling of budget is explicitly increased if necessary to provide precise division result
        if (remainingBudget.scale() < defaultFractionDigits) {
            remainingBudget = remainingBudget.setScale(defaultFractionDigits, BigDecimal.ROUND_UNNECESSARY);
        }

        return remainingBudget.divide(BigDecimal.valueOf(daysLeft), BigDecimal.ROUND_HALF_UP);
    }

    @Override
    public boolean checkFixedDailyBudget(Long ccgId, Long campaignId, Long accountId, BigDecimal totalBudget, BigDecimal dailyBudget,
            Date startDate, Date endDate) {
        if (totalBudget == null || dailyBudget == null) {
            // This case is invalid, so do not show confusing warning about daily budget
            return true;
        }

        TimeZone timeZone;
        Date campaignEndDate;
        Campaign campaign;

        if (ccgId != null) {
            CampaignCreativeGroup ccg = find(ccgId);
            campaign = ccg.getCampaign();
            timeZone = TimeZone.getTimeZone(ccg.getAccount().getTimezone().getKey());
            campaignEndDate = ccg.getCampaign().getDateEnd();
        } else if (campaignId != null) {
            campaign = campaignService.find(campaignId);
            timeZone = TimeZone.getTimeZone(campaign.getAccount().getTimezone().getKey());
            campaignEndDate = campaign.getDateEnd();
        } else if (accountId != null) {
            campaignEndDate = endDate;
            Account account = em.find(Account.class, accountId);
            timeZone = TimeZone.getTimeZone(account.getTimezone().getKey());
            campaign = null;
        } else {
            // This case is invalid, so do not show confusing warning about daily budget
            return true;
        }

        if (endDate == null) {
            endDate = campaignEndDate;

            if (endDate == null) {
                // Any fixed daily budget is correct if CCG is not limited in time
                return true;
            }
        }

        Calendar c = Calendar.getInstance(timeZone);

        c.setTime(new Date());
        Date today = DateUtils.truncate(c, Calendar.DAY_OF_MONTH).getTime();

        c.setTime(startDate);
        startDate = DateUtils.truncate(c, Calendar.DAY_OF_MONTH).getTime();

        c.setTime(endDate);
        endDate = DateUtils.truncate(c, Calendar.DAY_OF_MONTH).getTime();

        if (today.before(startDate)) {
            today = startDate;
        }

        if (endDate.before(today)) {
            // This case is invalid, so do not show confusing warning about daily budget
            return true;
        }

        long daysLeft = (endDate.getTime() - today.getTime()) / (1000 * 60 * 60 * 24) + 1;

        BigDecimal remainingBudget;

        if (ccgId != null) {
            remainingBudget = totalBudget.subtract(getSpentCCGBudget(ccgId, timeZone, today));
        } else if (campaignId != null) {
            BigDecimal campaignBudget = campaign.getBudget();

            if (campaignBudget == null || campaignBudget.compareTo(BigDecimal.ZERO) == 0) {
                remainingBudget = totalBudget;
            } else {
                BigDecimal campaignRemainingBudget = campaignBudget.subtract(getSpentCampaignBudget(campaignId, timeZone, today));

                if (campaignRemainingBudget.compareTo(totalBudget) >= 0) {
                    remainingBudget = totalBudget;
                } else {
                    remainingBudget = campaignRemainingBudget;
                }
            }
        } else {
            if (totalBudget.compareTo(BigDecimal.ZERO) == 0) {
                return true;
            }

            remainingBudget = totalBudget;
        }

        return dailyBudget.multiply(BigDecimal.valueOf(daysLeft)).compareTo(remainingBudget) >= 0;
    }

    private BigDecimal getSpentCampaignBudget(Long campaignId, TimeZone timeZone, Date today) {
        return campaignService.getSpentCampaignBudget(campaignId, timeZone, today);
    }

    private BigDecimal getSpentCCGBudget(Long ccgId, TimeZone timeZone, Date today) {
        Calendar c = Calendar.getInstance(timeZone);
        c.setTime(today);
        String sql = "select * from statqueries.ccg_spent_budget(?, ?)";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{ccgId, c},
                new int[]{Types.INTEGER, Types.DATE},
                BigDecimal.class
        );
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void createOrUpdateAll(Long campaignId, Collection<CampaignCreativeGroup> creativeGroups) {
        if (creativeGroups.isEmpty()) {
            return;
        }

        // just fetch to put them all in persistent context
        em.createNamedQuery("CampaignCreativeGroup.findUndeletedByCampaignId")
            .setParameter("campaignId", campaignId)
            .getResultList();

        for (CampaignCreativeGroup group : creativeGroups) {
            if (UploadUtils.isLink(group)) {
                continue;
            }

            UploadUtils.throwIfErrors(group);
            if (group.getId() == null) {
                createInternal(group);
            }
            else {
                updateInternal(group);
            }
        }
        for (CampaignCreativeGroup group : creativeGroups) {
            campaignCreativeService.createOrUpdateAll(group.getId(), group.getCampaignCreatives());
            ccgKeywordService.createOrUpdateAll(group.getId(), group.getCcgKeywords());

                PersistenceUtils.flushAndClear(em, new Filter<Integer>() {
                    @Override
                    public boolean accept(Integer entitiesSize) {
                        return entitiesSize > 1000;
                    }
                });
            }

    }

    @Override
    public void validateAll(Campaign campaign, TGTType tgtType, Collection<CampaignCreativeGroup> creativeGroups) {
        if (creativeGroups.isEmpty()) {
            return;
        }

        Map<String, CampaignCreativeGroup> existingMap;
        if (campaign.getId() != null) {
            @SuppressWarnings("unchecked")
            List<CampaignCreativeGroup> existingCreativeGroups =
                    em.createNamedQuery("CampaignCreativeGroup.findUndeletedByCampaignId").setParameter("campaignId", campaign.getId()).getResultList();

            existingMap = new HashMap<>();
            for (CampaignCreativeGroup existing : existingCreativeGroups) {
                existingMap.put(existing.getName(), existing);
            }
        } else {
            existingMap = Collections.emptyMap();
        }

        DuplicateChecker.<CampaignCreativeGroup> createNameDuplicateChecker()
            .check(creativeGroups)
            .updateUploadStatus("name");

        ExpressionService.ConverterContext converterContext = expressionService.newContext();
        for (CampaignCreativeGroup group : creativeGroups) {
            UploadContext uploadStatus = UploadUtils.getUploadContext(group);
            CampaignCreativeGroup existing = existingMap.get(group.getName());

            //update entity
            if (existing != null) {
                group.setId(existing.getId());
                group.setVersion(existing.getVersion());
                group.setTgtType(existing.getTgtType());
                group.setCcgType(existing.getCcgType());
                if (group.getCountry() == null) {
                    Country country = new Country(existing.getCountry().getCountryCode());
                    group.setCountry(country);
                }
                uploadStatus.mergeStatus(UploadStatus.UPDATE);
            } else {
                uploadStatus.mergeStatus(UploadStatus.NEW);
            }

            if (group.getChannel() instanceof ChannelExpressionLink) {
                String expression = ((ChannelExpressionLink) group.getChannel()).getExpression();
                try {
                    String countryCode = null;
                    if (group.getCountry() != null) {
                        countryCode = group.getCountry().getCountryCode();
                    }
                    if (countryCode != null && em.find(Country.class, countryCode) != null) {
                        Long channelId = Long.valueOf(expressionService.convertFromHumanReadable(converterContext, expression, countryCode));
                        group.getChannel().setId(channelId);
                    }
                } catch (ExpressionConversionException e) {
                    ValidationContext context = ValidationUtil.validationContext(group).build();
                    if (e instanceof UnreachableExpressionException) {
                        context.addConstraintViolation("errors.wrong.cdml")
                            .withPath("channelTarget");
                    } else if (e instanceof ChannelNotFoundExpressionException) {
                        context.addConstraintViolation("errors.channelNotFound")
                            .withPath("channelTarget")
                            .withParameters(e.getName());
                    } else if (e instanceof UndistinguishableExpressionException) {
                        context.addConstraintViolation(e.getMessage())
                            .withPath("channelTarget")
                            .withParameters(e.getName());
                    } else {
                        context.addConstraintViolation("errors.expression")
                            .withPath("channelTarget")
                            .withParameters(expression);
                    }
                    UploadUtils.setErrors(group, context.getConstraintViolations());
                }
            }

            // validate
            if (!uploadStatus.isFatal()) {
                if (UploadUtils.isLink(group)) {
                    UploadUtils.setErrors(group, validationService.validate("CampaignCreativeGroup.link", group, tgtType).getConstraintViolations());
                } else {
                    ValidationContext context = validationService.validate(
                        ValidationStrategies.exclude(uploadStatus.getWrongPaths()), "CampaignCreativeGroup.createOrUpdate", group, campaign, tgtType);
                    UploadUtils.setErrors(group, context.getConstraintViolations());
                }
            }
            if (UploadUtils.isLinkWithErrors(group.getCampaign())) {
                uploadStatus.mergeStatus(UploadStatus.REJECTED);
            }

            campaignCreativeService.validateAll(group, group.getCampaignCreatives(), tgtType);

            ccgKeywordService.validateAll(group, group.getCcgKeywords(), tgtType);
        }
    }

    @Override
    @Restrict(restriction = "AdvertiserEntity.view")
    public Result<CampaignCreativeGroup> get(CampaignCreativeGroupSelector ccgSelector) {
        if (!currentUserService.isExternal()
                && CollectionUtils.isNullOrEmpty(ccgSelector.getAdvertiserIds())
                && CollectionUtils.isNullOrEmpty(ccgSelector.getCampaigns())
                && CollectionUtils.isNullOrEmpty(ccgSelector.getCreativeGroups())) {
            throw new ConstraintViolationException(BusinessErrors.FIELD_IS_REQUIRED, "errors.api.emptyCriteria.ccg");
        }

        boolean internal = currentUserService.isInternal();

        CampaignCreativeGroupQuery query = createQuery(ccgSelector).geoChannels().geoChannelsExcluded();

        if (internal) {
            query.colocations();
        }

        PartialList<CampaignCreativeGroup> groups = query.executor(executorService)
            .partialList(ccgSelector.getPaging());

        em.clear();

        Map<Long, Set<Long>> ccgSites = new HashMap<>();

        for (CampaignCreativeGroup group : groups) {
            if (advertiserEntityRestrictions.canEditSiteTargeting(group.getAccount()) && TGTType.CHANNEL == group.getTgtType()) {
                Set<Long> siteIds = ccgSites.get(group.getId());
                if (siteIds == null) {
                    siteIds = new TreeSet<>();
                    ccgSites.put(group.getId(), siteIds);
                }
            }
        }
        if (!ccgSites.isEmpty()) {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(
                "select ccg_id, site_id from ccgsite where ccg_id = any(?::int[])",
                jdbcTemplate.createArray("int", ccgSites.keySet())
                );
            while (rs.next()) {
                ccgSites.get(rs.getLong(1)).add(rs.getLong(2));
            }
        }

        for (CampaignCreativeGroup group : groups) {
            SortedSet<CCGSchedule> sortedSchedules = new TreeSet<>(new ScheduleComparator());
            sortedSchedules.addAll(group.getCcgSchedules());
            group.setCcgSchedules(sortedSchedules);

            Set<Long> siteIds = ccgSites.get(group.getId());
            if (siteIds != null) {
                Set<Site> sites = new LinkedHashSet<>(siteIds.size());
                for (Long siteId : siteIds) {
                    sites.add(new Site(siteId));
                }
                group.setSites(sites);
            } else {
                group.setSites(null);
            }

            FrequencyCap frequencyCap = group.getFrequencyCap();
            if (frequencyCap != null) {
                frequencyCap.setVersion(null);
            }

            if (!internal) {
                group.setOptInStatusTargeting(null);
                group.setMinUidAge(null);
                group.setColocations(null);
            }

            if (!TGTType.CHANNEL.equals(group.getTgtType())) {
                group.setChannelTarget(null);
            }

            group.setDateStart(group.getCalculatedStartDate());
            group.setDateEnd(group.getCalculatedEndDate());
        }

        return new Result<>(groups);
    }

    private CampaignCreativeGroupQuery createQuery(CampaignCreativeGroupSelector ccgSelector) {
        return new CampaignCreativeGroupQueryImpl()
                .restrict()
                .type(ccgSelector.getCampaignType())
                .advertisers(ccgSelector.getAdvertiserIds())
                .campaigns(ccgSelector.getCampaigns())
                .creativeGroups(ccgSelector.getCreativeGroups())
                .statuses(ccgSelector.getStatuses())
                .addDefaultOrder();
    }

    @Override
    @Interceptors({ManualFlushInterceptor.class, CaptureChangesInterceptor.class})
    @Validate(validation = "Operations.integrity", parameters = { "#ccgOperations", "'campaignCreativeGroup'" })
    public OperationsResult perform(Operations<CampaignCreativeGroup> ccgOperations) {

        List<Long> ccgIds = prepareCCGIds(ccgOperations);
        if (!ccgIds.isEmpty()) {
            fetch(ccgIds);
        }

        for (Operation<CampaignCreativeGroup> ccgMergeOperation : ccgOperations.getOperations()) {
            prepareUpdateMergeOperation(ccgMergeOperation);
        }

        // validate
        validationService
            .validate("CampaignCreativeGroup.merge", ccgOperations)
            .throwIfHasViolations();

        List<Long> result = new ArrayList<>();
        for (Operation<CampaignCreativeGroup> ccgMergeOperation : ccgOperations.getOperations()) {
            result.add(processMergeOperation(ccgMergeOperation));
        }

        try {
            em.flush();
        } catch (PersistenceException e) {
            if (DBConstraint.CCG_NAME.match(e)) {
                validationService
                        .validateInNewTransaction("CampaignCreativeGroup.nameConstraintViolations", ccgOperations)
                        .throwIfHasViolations();
            }

            throw e;
        }

        return new OperationsResult(result);
    }

    @Override
    @Interceptors({ManualFlushInterceptor.class, CaptureChangesInterceptor.class})
    @Restrict(restriction = "AdvertiserEntity.update", parameters = "find('AdvertiserAccount', #advertiserId)")
    public void perform(Long advertiserId, List<Long> ccgIds, BulkOperation<CampaignCreativeGroup> operation) {

        if (!CollectionUtils.isNullOrEmpty(ccgIds)) {
            fetch(ccgIds);
        }

        ValidationContext performContext = validationService.validate("CampaignCreativeGroup.perform", advertiserId, ccgIds, operation);
        performContext.throwIfHasViolations();

        List<CampaignCreativeGroup> toUpdateList = new ArrayList<>(ccgIds.size());
        for (Long ccgId : ccgIds) {
            CampaignCreativeGroup group = em.find(CampaignCreativeGroup.class, ccgId);
            CampaignCreativeGroup toUpdate = new CampaignCreativeGroup(group.getId());
            operation.perform(group, toUpdate);
            toUpdateList.add(toUpdate);
        }

        ValidationContext updateContext = validationService.validate("CampaignCreativeGroup.update", toUpdateList);
        updateContext.addConstraintViolations(performContext.getConstraintViolations());
        updateContext.throwIfHasViolations();

        for (CampaignCreativeGroup group : toUpdateList) {
            updateInternal(group);
        }
    }

    private void fetch(List<Long> ccgIds) {
         new CampaignCreativeGroupQueryImpl()
                .creativeGroups(ccgIds)
                .executor(executorService)
                .list();
    }

    private List<Long> prepareCCGIds(Operations<CampaignCreativeGroup> ccgOperations) {
        List<Long> ccgIds = new ArrayList<>();
        for (Operation<CampaignCreativeGroup> operation : ccgOperations.getOperations()) {
            if (operation != null && operation.getEntity() != null && operation.getEntity().getId() != null) {
                ccgIds.add(operation.getEntity().getId());
            }
        }
        return ccgIds;
    }

    private Long processMergeOperation(Operation<CampaignCreativeGroup> mergeOperation) {
        CampaignCreativeGroup ccg = mergeOperation.getEntity();

        switch (mergeOperation.getOperationType()) {
        case CREATE:
            ccg.setId(null);
            return createInternal(ccg);
        case UPDATE:
            return updateInternal(ccg).getId();
        }

        throw new RuntimeException(mergeOperation.getOperationType() + " not supported!");
    }

    private void prepareUpdateMergeOperation(Operation<CampaignCreativeGroup> mergeOperation) {
        if (mergeOperation == null ||
                mergeOperation.getOperationType() == OperationType.CREATE ||
                mergeOperation.getEntity() == null) {
            return;
        }

        CampaignCreativeGroup ccg = mergeOperation.getEntity();

        if (ccg.isChanged("channelTarget") &&
                ccg.getChannelTarget() != ChannelTarget.TARGETED &&
                !ccg.isChanged("channel")) {
            ccg.setChannel(null);
        }
    }

    @Override
    @Restrict(restriction = "CreativeGroup.updateChannelTarget", parameters = "find('CampaignCreativeGroup', #id)")
    public CampaignCreativeGroup findForUpdateTarget(Long id) {
        return find(id);
    }

    @Override
    @Restrict(restriction = "CreativeGroup.updateUserSampleGroups", parameters = "find('CampaignCreativeGroup', #id)")
    public CampaignCreativeGroup findForUpdateUserSampleGroups(Long id) {
        return find(id);
    }

    @Override
    public boolean isFallingOutOfCampaignSchedule(Long ccgId) {
        String sql = "select count (distinct ccg.id) from CCGSchedule ccgs " +
                "inner join ccgs.campaignCreativeGroup ccg where ccg.id = :ccgId " +
                "and ccg.campaign.campaignSchedules.size > 0 " +
                "and not exists (select campaignSchedule from CampaignSchedule campaignSchedule where campaignSchedule.campaign.id = ccg.campaign.id " +
                "and (ccgs.timeFrom >= campaignSchedule.timeFrom and ccgs.timeTo <= campaignSchedule.timeTo))";

        Query query = em.createQuery(sql);
        query.setParameter("ccgId", ccgId);
        return ((Number) query.getSingleResult()).intValue() > 0;
    }

    @Override
    @Restrict(restriction = "Entity.access", parameters = "find('Campaign', #campaignId)")
    public List<TreeFilterElementTO> searchGroups(Long campaignId) {
        if (campaignId == null) {
            return new ArrayList<>();
        }

        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append("  ccg.ccg_id id, ccg.name, ccg.status, ccg.display_status_id, ");
        query.append("  EXISTS ( ");
        query.append("    SELECT * FROM CampaignCreative cc, Creative c ");
        query.append("    WHERE cc.creative_id = c.creative_id AND cc.ccg_id = ccg.ccg_id ");
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            query.append(" AND cc.status <> 'D' AND c.status <> 'D'");
        }
        query.append("  ) hasChildren ");
        query.append("FROM CampaignCreativeGroup ccg ");
        query.append("WHERE ccg.campaign_id = ");
        query.append(campaignId);
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            query.append(" AND ccg.status <> 'D'");
        }

        List<TreeFilterElementTO> result =jdbcTemplate.query(
                query.toString(),
                new TreeFilterElementTOConverter(CampaignCreativeGroup.displayStatusMap)
        );

        Collections.sort(result, new StatusNameTOComparator<TreeFilterElementTO>());
        return result;
    }

    @Override
    public List<TreeFilterElementTO> searchGroupsBySizeType(Long campaignId, Long sizeTypeId) {
        if (campaignId == null || sizeTypeId == null) {
            return new ArrayList<>();
        }

        StringBuilder query = new StringBuilder();
        query.append("SELECT ");
        query.append("  ccg.ccg_id id, ccg.name, ccg.status, ccg.display_status_id, ");
        query.append("  EXISTS ( ");
        query.append("    SELECT * FROM CampaignCreative cc, Creative c ");
        query.append("    WHERE cc.creative_id = c.creative_id AND cc.ccg_id = ccg.ccg_id ");
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            query.append(" AND cc.status <> 'D' AND c.status <> 'D'");
        }
        query.append("  ) hasChildren ");
        query.append("FROM CampaignCreativeGroup ccg ");
        query.append("WHERE ccg.campaign_id = ? ");
        query.append("AND EXISTS (select 1 from campaigncreative cc " +
                "WHERE cc.ccg_id = ccg.ccg_id and exists (select 1 from creative c " +
                        "WHERE c.creative_id = cc.creative_id AND exists (select 1 from CreativeSize cz " +
                                "WHERE cz.size_type_id=? AND c.size_id = cz.size_id)))");
        if (!userService.getMyUser().isDeletedObjectsVisible()) {
            query.append(" AND ccg.status <> 'D'");
        }

        List<TreeFilterElementTO> result = jdbcTemplate.query(
                query.toString(),
                new Object[]{campaignId, sizeTypeId},
                new TreeFilterElementTOConverter(CampaignCreativeGroup.displayStatusMap)
        );

        Collections.sort(result, new StatusNameTOComparator<>());
        return result;
    }

    @Override
    @Restrict(restriction = "CreativeGroup.updateUserSampleGroups", parameters = "find('CampaignCreativeGroup', #group.id)")
    @Validate(validation = "CampaignCreativeGroup.updateUserSampleGroups", parameters = "#group")
    @Interceptors(CaptureChangesInterceptor.class)
    public void updateUserSampleGroups(CampaignCreativeGroup group) {
        CampaignCreativeGroup existing = find(group.getId());
        existing.setVersion(group.getVersion());
        existing.setUserSampleGroupEnd(group.getUserSampleGroupEnd());
        existing.setUserSampleGroupStart(group.getUserSampleGroupStart());

        existing = em.merge(existing);
        auditService.audit(existing, ActionType.UPDATE);
        displayStatusService.update(existing);
    }

    @Override
    @Restrict(restriction = "CreativeGroup.viewExpressionPerformance", parameters = "find('CampaignCreativeGroup', #parameters.ccgId)")
    public SimpleReportData getExpressionPerformanceReportData(ExpressionPerformanceReportParameters parameters) {
        ExpressionPartFormatter formatter = new ExpressionPartFormatter(expressionService, restrictionService);
        ValueFormatterRegistry registry = ValueFormatterRegistries.registry()
            .column(ExpressionPerformanceReportMetaData.EXPRESSION, formatter);

        SimpleReportData data = new SimpleReportData();
        AuditResultHandlerWrapper serializer = reportsService.createHtmlSerializer(data);
        serializer.registry(registry, RowTypes.data());

        reportsService.executeWithoutAudit(new Report(parameters, serializer, false));
        return data;
    }

    private class Report extends CommonAuditableReportSupport<ExpressionPerformanceReportParameters> {
        public Report(ExpressionPerformanceReportParameters parameters, AuditResultHandlerWrapper handler, boolean executeSummary) {
            super(parameters, handler, executeSummary);
        }

        @Override
        public void prepare() {
            metaData = ExpressionPerformanceReportMetaData.META_DATA.resolve(parameters);
            metaData = metaData.orderById(parameters.getSortColumn());
            ColumnOrder<DbColumn> columnOrder = metaData.getSortColumns().get(0);

            query = statDbQuery.queryFunction("statqueries.expression_performance")
                .parameter("p_ccg_id", parameters.getCcgId(), Types.INTEGER)
                .parameter("p_from_date", parameters.getDateRange().getBegin(), PostgreLocalDateUserType.INSTANCE)
                .parameter("p_to_date", parameters.getDateRange().getEnd(), PostgreLocalDateUserType.INSTANCE)
                .parameter("p_sort_column", columnOrder.getColumn().getResultSetName(), Types.VARCHAR)
                .parameter("p_sort_order", columnOrder.getOrder().name(), Types.VARCHAR);
        }

        @Override
        public ReportType getReportType() {
            return null;
        }
    }

    private static class ExpressionPartFormatter extends ValueFormatterSupport<String> {
        private ExpressionService expressionService;
        private RestrictionService restrictionService;

        public ExpressionPartFormatter(ExpressionService expressionService, RestrictionService restrictionService) {
            super();

            this.expressionService = expressionService;
            this.restrictionService = restrictionService;
        }

        @Override
        public void formatHtml(HtmlCellAccessor cellAccessor, String value, FormatterContext context) {
            try {
                String expr = expressionService.convertToHumanReadableWithLinks(value, "../../../channel/view.action", restrictionService);
                cellAccessor.setHtml(expr);
            } catch (ExpressionConversionException e) {
                throw new IllegalArgumentException(e);
            }
        }

        @Override
        public String formatText(String value, FormatterContext context) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean hasNoFreqCapWarningForCcg(CampaignCreativeGroup group) {
        if (group.getAccount().getInheritedStatus() != Status.ACTIVE) {
            return false;
        }
        return jdbcTemplate.queryForObject("select * from entityqueries.get_no_freq_cap_warning_for_ccg(?::int)",
                new Object[] { group.getId() }, Boolean.class);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    @Restrict(restriction = "CreativeGroup.updateGeoTarget", parameters = "find('CampaignCreativeGroup', #group.id)")
    @Validate(validation = "CampaignCreativeGroup.updateGeoTarget", parameters = {"#group", "#geoChannels"})
    public void updateGeoTarget(CampaignCreativeGroup group, List<GeoChannel> geoChannels) {
        CampaignCreativeGroup existing = find(group.getId());
        if (!existing.getVersion().equals(group.getVersion())) {
            throw new VersionCollisionException();
        }
        existing.setCountry(countryService.find(group.getCountry().getCountryCode()));
        group.setGeoChannels(new HashSet<GeoChannel>(geoChannels));
        prePersistGeoChannels(group);
        new CollectionMerger<GeoChannel>(existing.getGeoChannels(), group.getGeoChannels()).merge();
        auditService.audit(existing, ActionType.UPDATE);
        targetingChannelService.addToBulkLink(existing);
    }

    @Override
    @Interceptors(CaptureChangesInterceptor.class)
    public void linkConversions(Collection<Long> ids, Collection<Long> conversionIds) {
        Set<Action> actions = new HashSet<>(conversionIds.size());
        if (!CollectionUtils.isNullOrEmpty(conversionIds)) {
            Query query = em.createQuery("SELECT a FROM Action a WHERE a.id in :conversionIds and a.status <> 'D'");
            query.setParameter("conversionIds", conversionIds);
            actions.addAll(query.getResultList());
        }

        if (actions.isEmpty()) {
            return;
        }

        Collection<CampaignCreativeGroup> groups = new ArrayList<>(ids.size());
        if (!CollectionUtils.isNullOrEmpty(ids)) {
            List<CampaignCreativeGroup> list = new CampaignCreativeGroupQueryImpl()
                .creativeGroups(ids)
                .nonDeleted()
                    .executor(queryExecutorService)
                    .list();
            groups.addAll(list);
        }

        for (CampaignCreativeGroup ccg : groups) {
            if (!restrictionService.isPermitted("AdvertiserEntity.update", ccg)) {
                continue;
            }

            actions.addAll(ccg.getActions());
            ccg.setActions(actions);

            auditService.audit(ccg, ActionType.UPDATE);
            em.merge(ccg);
        }
    }

    @Override
    public List<String> findCCGNamesByCampaign(Long campaignId) {
        return em.createQuery("SELECT c.name FROM CampaignCreativeGroup c WHERE c.campaign.id = :campaignId AND c.status <> 'D'", String.class)
                .setParameter("campaignId", campaignId)
                .getResultList();
    }

    private static class ColocationRowMapper implements RowMapper<ISPColocationTO> {
        @Override
        public ISPColocationTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            Long coloId = rs.getLong(1);
            String name = rs.getString(2);
            char status = rs.getString(3).charAt(0);
            Long accountId = rs.getLong(4);
            String accountName = rs.getString(5);
            return new ISPColocationTO(coloId, name, status, accountId, accountName);
        }
    }
}

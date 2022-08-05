package com.foros.session.reporting;

import com.foros.jaxb.adapters.AdvertiserReportColumnsAdapter;
import com.foros.model.Identifiable;
import com.foros.model.Status;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.account.PublisherAccount;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CampaignType;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelVisibility;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.channel.KeywordChannel;
import com.foros.model.security.OwnedStatusable;
import com.foros.model.site.Site;
import com.foros.model.site.Tag;
import com.foros.security.AccountRole;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.UtilityService;
import com.foros.session.action.ActionService;
import com.foros.session.admin.country.CountryService;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.reporting.activeAdvertisers.ActiveAdvertisersReportParameters;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportParameters;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportService;
import com.foros.session.reporting.advertiser.olap.OlapAdvertiserReportState;
import com.foros.session.reporting.advertiser.olap.OlapDetailLevel;
import com.foros.session.reporting.advertiser.olap.OlapDisplayAdvertiserReportService;
import com.foros.session.reporting.advertiser.olap.OlapTextAdvertiserReportService;
import com.foros.session.reporting.campaignOverview.CampaignOverviewReportParameters;
import com.foros.session.reporting.channel.ChannelReportParameters;
import com.foros.session.reporting.channelInventoryForecast.ChannelInventoryForecastReportParameters;
import com.foros.session.reporting.channelSites.ChannelSitesReportParameters;
import com.foros.session.reporting.channelUsage.ChannelUsageReportParameters;
import com.foros.session.reporting.channeltriggers.ChannelTriggersReportParameters;
import com.foros.session.reporting.conversionPixels.ConversionPixelsReportParameters;
import com.foros.session.reporting.conversions.ConversionsReportParameters;
import com.foros.session.reporting.custom.CustomReportParameters;
import com.foros.session.reporting.inventoryEstimation.InventoryEstimationReportParameters;
import com.foros.session.reporting.invitations.InvitationsReportParameters;
import com.foros.session.reporting.isp.ISPReportParameters;
import com.foros.session.reporting.parameters.DateRange;
import com.foros.session.reporting.profiling.ProfilingReportParameters;
import com.foros.session.reporting.pubAdvertising.PubAdvertisingReportParameters;
import com.foros.session.reporting.publisher.PublisherMeta;
import com.foros.session.reporting.publisher.PublisherReportParameters;
import com.foros.session.reporting.publisherOverview.PublisherOverviewReportParameters;
import com.foros.session.reporting.referrer.ReferrerReportParameters;
import com.foros.session.reporting.siteChannels.SiteChannelsReportParameters;
import com.foros.session.reporting.webwise.WebwiseReportParameters;
import com.foros.session.restriction.EntityRestrictions;
import com.foros.util.CollectionUtils;
import com.foros.util.DateHelper;
import com.foros.util.EntityUtils;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.joda.time.LocalDate;

@LocalBean
@Stateless
@Validations
public class ReportingValidations {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private UtilityService utilityService;

    @EJB
    private WalledGardenService walledGardenService;

    @EJB
    private OlapTextAdvertiserReportService olapTextAdvertiserReportService;

    @EJB
    private OlapDisplayAdvertiserReportService olapDisplayAdvertiserReportService;

    @EJB
    private ActionService actionService;

    @EJB
    private EntityRestrictions entityRestrictions;

    @EJB
    private CountryService countryService;

    @Validation
    public void validateCustom(ValidationContext context, @ValidateBean CustomReportParameters parameters) {
        validateDates(context, parameters.getDateRange());

        if (currentUserService.isInternalWithRestrictedAccess()) {
            Set<Long> accessAccountIds = currentUserService.getAccessAccountIds();
            if (accessAccountIds != null) {
                String countryCode = parameters.getCountryCode();
                Query query = em.createQuery(
                    "select a from Account a where a.id in (:accountIds) and a.country.countryCode=:countryCode");
                query.setParameter("accountIds", accessAccountIds);
                query.setParameter("countryCode", countryCode);
                if (query.getResultList().isEmpty()) {
                    context.addConstraintViolation("errors.field.invalid")
                        .withPath("countryCode")
                        .withValue(countryCode);
                }
                if (!CustomReportParameters.NONE_ID.equals(parameters.getAgencyId())) {
                    validateAccountId(context, "agencyId", parameters.getAgencyId(), accessAccountIds);
                }
                validateAccountId(context, "advertiserId", parameters.getAdvertiserId(), accessAccountIds);
                validateAccountId(context, "ispId", parameters.getIspId(), accessAccountIds);
                validateAccountId(context, "publisherId", parameters.getPublisherId(), accessAccountIds);
            }
        }

        String currencyCode = parameters.getOutputCurrencyCode();
        if (StringUtil.isPropertyNotEmpty(currencyCode) && !"USD".equals(currencyCode)) {
            context.addConstraintViolation("errors.field.invalid")
                .withPath("outputCurrencyCode")
                .withValue(currencyCode);
        }
    }

    private void validateAccountId(ValidationContext context, String path, Long accountId, Set<Long> accessAccountIds) {
        if (context.isReachable(path) && accountId != null) {
            Query query = em.createQuery(
                "select a from ExternalAccount a where a.id=:accountId and a.internalAccount.id in (:accountIds)");
            query.setParameter("accountId", accountId);
            query.setParameter("accountIds", accessAccountIds);
            if (query.getResultList().isEmpty()) {
                context.addConstraintViolation("errors.field.invalid")
                    .withPath(path);
            }
        }
    }

    @Validation
    public void validateReferrer(ValidationContext context, @ValidateBean ReferrerReportParameters parameters) {

        if (parameters.getSiteId() == null &&
                parameters.getTagId() == null) {
            context
                .addConstraintViolation("errors.referrerReport.filters.siteId")
                .withPath("siteId");

            context
                .addConstraintViolation("errors.referrerReport.filters.siteId")
                .withPath("tagId");
        }

        Site site = null;
        if (parameters.getSiteId() != null && context.props("siteId").reachableAndNoViolations()) {
            site = em.find(Site.class, parameters.getSiteId());
        }
        Tag tag = null;
        if (parameters.getTagId() != null && context.props("tagId").reachableAndNoViolations()) {
            tag = em.find(Tag.class, parameters.getTagId());
        }

        //Validate Tag
        validateReferrerEntity(context, parameters, tag, "tagId", parameters.getTagId());

        //Validate Site
        validateReferrerEntity(context, parameters, site, "siteId", parameters.getSiteId());
        if (context.props("tagId", "siteId").all().reachableAndNoViolations()) {
            validateUserSite(context, parameters.getSiteId() == null ? tag.getSite().getId() : parameters.getSiteId());
        }


        if (context.props("tagId", "siteId").all().reachableAndNoViolations() && site != null && tag != null &&
                !site.equals(tag.getSite())) {
            context.addConstraintViolation("errors.referrerReport.filters.belongSite")
                .withPath("tagId")
                .withValue(parameters.getTagId());
        }

        //Validate Dates
        validateDates(context, parameters.getDateRange());
        validateOneMonthBack(context, parameters.getDateRange());
    }

    private <T extends OwnedStatusable & Identifiable> void validateReferrerEntity(ValidationContext context, ReferrerReportParameters parameters, T entity, String path, Long id) {
        if (context.props(path).reachableAndNoViolations() && id != null) {
            if (entity == null || !entityRestrictions.canView(entity) ||
                    (currentUserService.getUser().isDeletedObjectsVisible() && entity.getStatus() == Status.DELETED)) {
                context
                    .addConstraintViolation("errors.entity.notFound")
                    .withPath(path)
                    .withValue(id);
            } else {
                validateParentLink(context, parameters, entity, path);
            }
        }

    }

    private <T extends OwnedStatusable<PublisherAccount> & Identifiable> void validateParentLink(ValidationContext context, ReferrerReportParameters parameters, T entity, String path) {
        if (context.props("accountId").reachableAndNoViolations()
                && !entity.getAccount().getId().equals(parameters.getAccountId())) {
            context.addConstraintViolation("errors.referrerReport.filters.belongAccount")
                .withPath(path)
                .withValue(entity.getId());
        }
    }

    @Validation
    public void validateSiteChannels(ValidationContext context, @ValidateBean SiteChannelsReportParameters parameters) {
        if (context.props("accountId").reachableAndNoViolations() && parameters.getAccountId() != null && !utilityService.isEntityExists(PublisherAccount.class, parameters.getAccountId())) {
            context
                .addConstraintViolation("errors.entity.notFound")
                .withPath("accountId")
                .withValue(parameters.getAccountId());
        }

        if (context.props("siteId").reachableAndNoViolations() && parameters.getSiteId() != null && !utilityService.isEntityExists(Site.class, parameters.getSiteId())) {
            context
                .addConstraintViolation("errors.entity.notFound")
                .withPath("siteId")
                .withValue(parameters.getSiteId());
        }

        if (context.props("tagId").reachableAndNoViolations() && parameters.getTagId() != null && !utilityService.isEntityExists(Tag.class, parameters.getTagId())) {
            context
                .addConstraintViolation("errors.entity.notFound")
                .withPath("tagId")
                .withValue(parameters.getTagId());
        }

        validateDates(context, parameters.getDateRange());
        validateOneMonthBack(context, parameters.getDateRange());
    }

    @Validation
    public void validateChannelTriggers(ValidationContext context, @ValidateBean ChannelTriggersReportParameters parameters) {
        validateDates(context, parameters.getDateRange());
        validateOneMonthBack(context, parameters.getDateRange());
    }

    @Validation
    public void validatePublisher(ValidationContext context, @ValidateBean PublisherReportParameters parameters) {
        validateDates(context, parameters.getDateRange());
        validatePublisherColumns(context, parameters);
        validateUserSite(context, parameters.getSiteId());
    }

    private void validatePublisherColumns(ValidationContext context, PublisherReportParameters parameters) {
        if (!context.props("columns").reachableAndNoViolations()) {
            return;
        }

        boolean isWalledGarden = walledGardenService.isPublisherWalledGarden(parameters.getAccountId());
        List<String> mandatoryColumns;
        if (isWalledGarden) {
            mandatoryColumns = ReportingHelper.getColumnNamesList(PublisherMeta.MANDATORY_WG_COLUMNS);
        } else {
            mandatoryColumns = ReportingHelper.getColumnNamesList(PublisherMeta.MANDATORY_NON_WG_COLUMNS);
        }

        List<String> columns = parameters.getColumns();
        if (!columns.containsAll(mandatoryColumns)) {
            context
                .addConstraintViolation("errors.field.invalid")
                .withPath("columns")
                .withValue(parameters.getColumns());
        }
    }

    @Validation
    public void validateTextAdvertiser(ValidationContext context, @ValidateBean OlapAdvertiserReportParameters parameters) {
        validateAdvertiser(context, parameters, olapTextAdvertiserReportService, true);
    }

    @Validation
    public void validateDisplayAdvertiser(ValidationContext context, @ValidateBean OlapAdvertiserReportParameters parameters) {
        validateAdvertiser(context, parameters, olapDisplayAdvertiserReportService, false);
    }

    @Validation
    public void validateGeneralAdvertiser(ValidationContext context, @ValidateBean OlapAdvertiserReportParameters parameters) {
        validateAdvertiser(context, parameters, olapDisplayAdvertiserReportService, null);
    }

    @Validation
    public void validateChannelUsage(ValidationContext context, @ValidateBean ChannelUsageReportParameters parameters) {
        if (parameters.getChannelId() != null) {
            Channel c = em.find(Channel.class, parameters.getChannelId());

            boolean valid = c != null && c.getAccount().getId().equals(parameters.getAccountId());

            if (valid) {
                valid = c instanceof BehavioralChannel || c instanceof ExpressionChannel || c instanceof KeywordChannel;
                valid &= c.getVisibility() == ChannelVisibility.CMP;
                valid &= currentUserService.isInternal() || c.getStatus() != Status.DELETED;
            }

            if (!valid) {
                context
                    .addConstraintViolation("report.channelUsage.invalidChannel")
                    .withPath("channelId");
            }
        }
    }

    private void validateAdvertiser(ValidationContext context, OlapAdvertiserReportParameters parameters, OlapAdvertiserReportService reportService, Boolean isText) {
        if (currentUserService.isExternal() && parameters.getCostAndRates() != null) {
            context
                .addConstraintViolation("errors.advertiserReport.costAndRates")
                .withPath("costAndRates")
                .withValue(parameters.getCostAndRates());
        }

        validateOlapAdvertiserFilters(context, parameters, isText);
        validateDates(context, parameters.getDateRange());
        validateReportType(context, parameters.getReportType(), isText);
        validateOlapColumns(context, parameters, reportService);
    }

    private void validateReportType(ValidationContext context, OlapDetailLevel reportType, Boolean isText) {
        if (!context.props("reportType").noViolations()) {
            return;
        }

        boolean descriptionIsNull = isText == null && reportType.getGeneralDescription() == null;
        if (isText != null) {
            descriptionIsNull = isText && reportType.getTextDescription() == null ||
                    !isText && reportType.getDisplayDescription() == null;
        }
        if (descriptionIsNull) {
            context.addConstraintViolation("errors.field.invalid")
                .withPath("reportType")
                .withValue(reportType);
        }
    }

    private void validateOlapColumns(ValidationContext context, OlapAdvertiserReportParameters parameters, OlapAdvertiserReportService olapAdvertiserReportService) {
        if (!context.props("reportType", "dateRange", "dateRange.begin", "dateRange.end").noViolations()) {
            return;
        }

        OlapAdvertiserReportState reportState = olapAdvertiserReportService.getReportState(parameters, false);

        if (reportState.getSelected().getMetricsColumns().isEmpty()) {
            context
                .addConstraintViolation("report.advertising.noColumnsSelected")
                .withPath("columns");
        } else {
            int i = 0;
            for (String column : parameters.getColumns()) {
                if (!reportState.availableOrFixed(column)) {
                    context.createSubContext(parameters.getColumns(), "columns", i)
                        .addConstraintViolation("errors.advertiserReport.column.wrong")
                        .withValue(AdvertiserReportColumnsAdapter.toAPIName(column));
                }
                i++;
            }
        }
    }

    private void validateOlapAdvertiserFilters(ValidationContext context, OlapAdvertiserReportParameters parameters, Boolean isText) {
        if (parameters.getAccountId() == null) {
            return;
        }

        // Validate account
        Long accountId = parameters.getAccountId();
        Account account = em.find(Account.class, accountId);
        if (account == null) {
            context
                .addConstraintViolation("errors.entity.notFound")
                .withPath("accountId")
                .withValue(accountId);
        } else if (!AccountRole.AGENCY.equals(account.getRole()) && !AccountRole.ADVERTISER.equals(account.getRole()) ||
                AccountRole.ADVERTISER.equals(account.getRole()) && !((AdvertiserAccount) account).isStandalone() ||
                OlapDetailLevel.Advertiser == parameters.getReportType() && !AccountRole.AGENCY.equals(account.getRole())) {
            context
                .addConstraintViolation("errors.advertiserReport.advertiser.invalid.role")
                .withPath("accountId")
                .withValue(accountId);
        }

        if (context.props("reportType").noViolations()) {
            Set<OlapDetailLevel.Filter> availableFilters = parameters.getReportType().getAvailableFilters();

            // Validate advertisers
            if (!CollectionUtils.isNullOrEmpty(parameters.getAdvertiserIds())) {
                if (!availableFilters.contains(OlapDetailLevel.Filter.Advertiser)) {
                    context
                        .addConstraintViolation("errors.field.null")
                        .withPath("advertiserIds")
                        .withValue(parameters.getAdvertiserIds());
                } else {
                    for (Long advertiserId : parameters.getAdvertiserIds()) {
                        validateAdvertisers(context, accountId, advertiserId);
                    }
                }
            }

            // Validate campaigns
            if (!CollectionUtils.isNullOrEmpty(parameters.getCampaignIds())) {
                if (!availableFilters.contains(OlapDetailLevel.Filter.Campaign)) {
                    context
                        .addConstraintViolation("errors.field.null")
                        .withPath("campaignIds")
                        .withValue(parameters.getCampaignIds());
                } else {
                    for (Long campaignId : parameters.getCampaignIds()) {
                        validateCampaigns(context, isText, accountId, campaignId);
                    }
                }
            }

            // Validate groups
            if (!CollectionUtils.isNullOrEmpty(parameters.getCcgIds())) {
                if (!availableFilters.contains(OlapDetailLevel.Filter.Group)) {
                    context
                        .addConstraintViolation("errors.field.null")
                        .withPath("ccgIds")
                        .withValue(parameters.getCcgIds());
                } else {
                    for (Long ccgId : parameters.getCcgIds()) {
                        validateGroups(context, isText, accountId, ccgId);
                    }
                }
            }

            // Validate creatives
            if (!CollectionUtils.isNullOrEmpty(parameters.getCampaignCreativeIds())) {
                if (!availableFilters.contains(OlapDetailLevel.Filter.Creative)) {
                    context
                        .addConstraintViolation("errors.field.null")
                        .withPath("creativeLinkIds")
                        .withValue(parameters.getCampaignCreativeIds());
                } else {
                    for (Long campaignCreativeId : parameters.getCampaignCreativeIds()) {
                        validateCreatives(context, isText, accountId, "creativeLinkIds", campaignCreativeId);
                    }
                }

                // Validate keywords
                if (parameters.getKeyword() != null) {
                    if (!availableFilters.contains(OlapDetailLevel.Filter.Keyword)) {
                        context
                            .addConstraintViolation("errors.field.null")
                            .withPath("keyword")
                            .withValue(parameters.getKeyword());
                    }
                }
            }
        }
    }


    private void validateCreatives(ValidationContext context, Boolean isText, Long accountId, String creativesPath, Long campaignCreativeId) {
        if (validateNullValue(context, campaignCreativeId, creativesPath)) {
            return;
        }
        CampaignCreative campaignCreative = em.find(CampaignCreative.class, campaignCreativeId);
        boolean failed = checkAdvertiserFilterEntity(context, campaignCreative, campaignCreativeId, creativesPath, accountId);
        if (!failed && isText != null) {
            if (isText && !campaignCreative.getCreative().isTextCreative()) {
                context
                    .addConstraintViolation("errors.advertiserReport.creative.text")
                    .withPath(creativesPath)
                    .withValue(campaignCreativeId);
            } else if (!isText && campaignCreative.getCreative().isTextCreative()) {
                context
                    .addConstraintViolation("errors.advertiserReport.creative.display")
                    .withPath(creativesPath)
                    .withValue(campaignCreativeId);
            }
        }
    }

    private void validateGroups(ValidationContext context, Boolean isText, Long accountId, Long ccgId) {
        if (validateNullValue(context, ccgId, "ccgIds")) {
            return;
        }
        CampaignCreativeGroup ccg = em.find(CampaignCreativeGroup.class, ccgId);
        boolean failed = checkAdvertiserFilterEntity(context, ccg, ccgId, "adGroupIds", accountId);
        if (!failed && isText != null) {
            if (isText && ccg.getCcgType() != CCGType.TEXT) {
                context
                    .addConstraintViolation("errors.advertiserReport.group.text")
                    .withPath("ccgIds")
                    .withValue(ccgId);
            } else if (!isText && ccg.getCcgType() == CCGType.TEXT) {
                context
                    .addConstraintViolation("errors.advertiserReport.group.display")
                    .withPath("ccgIds")
                    .withValue(ccgId);
            }
        }
    }

    private void validateCampaigns(ValidationContext context, Boolean isText, Long accountId, Long campaignId) {
        if (validateNullValue(context, campaignId, "campaignIds")) {
            return;
        }
        Campaign campaign = em.find(Campaign.class, campaignId);
        boolean failed = checkAdvertiserFilterEntity(context, campaign, campaignId, "campaignIds", accountId);
        if (!failed && isText != null) {
            if (isText && campaign.getCampaignType() != CampaignType.TEXT) {
                context
                    .addConstraintViolation("errors.advertiserReport.campaign.text")
                    .withPath("campaignIds")
                    .withValue(campaignId);
            } else if (!isText && campaign.getCampaignType() == CampaignType.TEXT) {
                context
                    .addConstraintViolation("errors.advertiserReport.campaign.display")
                    .withPath("campaignIds")
                    .withValue(campaignId);
            }
        }
    }

    private boolean validateNullValue(ValidationContext context, Long id, String path) {
        if (id == null) {
            context
                .addConstraintViolation("errors.field.required")
                .withPath(path)
                .withValue(null);
            return true;
        }
        return false;

    }

    private void validateAdvertisers(ValidationContext context, Long accountId, Long advertiserId) {
        if (validateNullValue(context, advertiserId, "advertiserIds")) {
            return;
        }
        AdvertiserAccount advertiser = em.find(AdvertiserAccount.class, advertiserId);
        boolean failed = checkAdvertiserFilterEntity(context, advertiser, advertiserId, "advertiserIds", accountId);
        if (!failed && advertiser.isStandalone()) {
            context
                .addConstraintViolation("errors.advertiserReport.advertiser.standalone")
                .withPath("advertiserIds")
                .withValue(advertiserId);
        }
    }

    private boolean checkAdvertiserFilterEntity(ValidationContext context, OwnedStatusable entity, Long id, String path, Long accountId) {

        if (entity == null) {
            context
                .addConstraintViolation("errors.entity.notFound")
                .withPath(path)
                .withValue(id);
            return true;
        }

        Long currentAccountId;
        if (entity.getAccount() instanceof AdvertiserAccount && ((AdvertiserAccount) entity.getAccount()).isInAgencyAdvertiser()) {
            currentAccountId = ((AdvertiserAccount) entity.getAccount()).getAgency().getId();
        } else {
            currentAccountId = entity.getAccount().getId();
        }

        if (!accountId.equals(currentAccountId)) {
            context.addConstraintViolation("errors.advertiserReport.filters.belongAccount")
                .withPath(path)
                .withValue(id);
            return true;
        }

        return false;
    }

    @Validation
    public void validateChannel(ValidationContext context, @ValidateBean ChannelReportParameters parameters) {
        validateDates(context, parameters.getDateRange());
    }

    @Validation
    public void validateChannelSites(ValidationContext context, @ValidateBean ChannelSitesReportParameters parameters) {
        validateDates(context, parameters.getDateRange());
        validateOneMonthBack(context, parameters.getDateRange());
        validateAccount(context, parameters.getAccountId());

        Long channelId = parameters.getChannelId();
        if (context.props("channelId").reachableAndNoViolations() && channelId != null && !utilityService.isEntityExists(Channel.class, channelId)) {
            context
                .addConstraintViolation("errors.entity.notFound")
                .withPath("channelId")
                .withValue(channelId);
        }

    }

    private void validateAccount(ValidationContext context, Long accountId) {
        if (accountId == null) {
            context
                .addConstraintViolation("errors.field.required")
                .withPath("accountId");
            return;
        }

        Account account = em.find(Account.class, accountId);
        if (account == null) {
            context
                .addConstraintViolation("errors.entity.notFound")
                .withPath("accountId")
                .withValue(accountId);
        }
    }

    private void validateConversionReportParameters(ValidationContext context, DateRange dateRange, Long accountId, Collection<Long> advertiserIds,
            List<Long> campaignIds, List<Long> groupIds, List<Long> campaignCreativeIds, List<Long> conversionIds) {
        validateDates(context, dateRange);

        AdvertisingAccountBase account = (accountId == null) ? null : em.find(AdvertisingAccountBase.class, accountId);
        if (account == null) {
            context
                .addConstraintViolation("errors.entity.notFound")
                .withPath("accountId");
        }

        if (advertiserIds != null) {
            for (Long advertiserId : advertiserIds) {
                validateAdvertisers(context, accountId, advertiserId);
            }
        }

        if (campaignIds != null) {
            for (Long campaignId : campaignIds) {
                validateCampaigns(context, null, accountId, campaignId);
            }
        }

        if (groupIds != null) {
            for (Long groupId : groupIds) {
                validateGroups(context, null, accountId, groupId);
            }
        }

        if (campaignCreativeIds != null) {
            for (Long creativeId : campaignCreativeIds) {
                validateCreatives(context, null, accountId, "campaignCreativeIds", creativeId);
            }
        }

        if (conversionIds != null && !conversionIds.isEmpty()) {
            List<EntityTO> acceptedActions = actionService.findEntityTOByMultipleParameters(accountId, null, null, true);
            final Set<Long> acceptedIds = EntityUtils.getEntityIds(acceptedActions);
            for (Long actionId : conversionIds) {
                if (!acceptedIds.contains(actionId)) {
                    context.addConstraintViolation("errors.advertiserReport.filters.belongAccount")
                        .withPath("actionIds")
                        .withValue(actionId);
                    return;
                }
            }
        }

    }

    @Validation
    public void validateConversions(ValidationContext context, @ValidateBean ConversionsReportParameters parameters) {
        Set<Long> accounts = new LinkedHashSet<Long>();
        accounts.addAll(parameters.getCampaignAdvertiserIds());
        accounts.addAll(parameters.getConversionAdvertiserIds());
        validateConversionReportParameters(
            context,
            parameters.getDateRange(),
            parameters.getAccountId(),
            accounts,
            parameters.getCampaignIds(),
            parameters.getGroupIds(),
            parameters.getCreativeIds(),
                parameters.getConversionIds()
        );
    }

    @Validation
    public void validateConversionPixels(ValidationContext context, @ValidateBean ConversionPixelsReportParameters parameters) {
        validateConversionReportParameters(
            context,
            parameters.getDateRange(),
            parameters.getAccountId(),
            parameters.getConversionAdvertiserIds(),
            null,
            null,
            null,
                parameters.getConversionIds()
        );
    }

    @Validation(value = "ISP")
    public void validateIsp(ValidationContext context, @ValidateBean ISPReportParameters parameters) {
        validateDates(context, parameters.getDateRange());
    }

    @Validation
    public void validateChannelInventory(ValidationContext context, @ValidateBean ChannelInventoryForecastReportParameters parameters) {
        if (context.props("channelFilter", "channelIds", "accountId").haveViolations()) {
            return;
        }

        switch (parameters.getChannelFilter()) {
        case IDS:
            if (parameters.getChannelIds().isEmpty()) {
                context.addConstraintViolation("errors.field.channelTriggers.channelsEmpty")
                    .withPath("channelIds");
            }
            break;
        default:
            if (!parameters.getChannelIds().isEmpty()) {
                context.addConstraintViolation("errors.field.empty")
                    .withPath("channelIds");
            }
            break;
        }
    }

    @Validation
    public void validateWebwise(ValidationContext context, @ValidateBean WebwiseReportParameters parameters) {
        validateDates(context, parameters.getDateRange());
    }

    @Validation
    public void validateActiveAdvertisersReport(ValidationContext context, @ValidateBean ActiveAdvertisersReportParameters parameters) {
        validateDates(context, parameters.getDateRange());
    }

    @Validation
    public void validatePubAdvertisingReport(ValidationContext context, @ValidateBean PubAdvertisingReportParameters parameters) {
        validateDates(context, parameters.getDateRange());
    }

    @Validation
    public void validateCampaignOverviewReport(ValidationContext context, @ValidateBean CampaignOverviewReportParameters parameters) {
        validateDates(context, parameters.getDateRange());
    }

    @Validation
    public void validatePublisherOverviewReport(ValidationContext context, @ValidateBean PublisherOverviewReportParameters parameters) {
        validateDates(context, parameters.getDateRange());
    }

    @Validation
    public void validateProfilingReport(ValidationContext context, @ValidateBean ProfilingReportParameters parameters) {
        validateDates(context, parameters.getDateRange());
    }

    private void validateDates(ValidationContext context, DateRange dateRange) {
        if (!context.props("dateRange", "dateRange.begin", "dateRange.end").reachableAndNoViolations()) {
            return;
        }

        if (dateRange.getEnd().compareTo(dateRange.getBegin()) < 0) {
            context
                .addConstraintViolation("report.invalid.range")
                .withValue(dateRange)
                .withPath("dateRange");
        }
    }

    private void validateOneMonthBack(ValidationContext context, DateRange dateRange) {
        if (!context.props("dateRange").reachableAndNoViolations()) {
            return;
        }

        LocalDate fullMonthBack = DateHelper.fullMonthBack(new LocalDate());

        if (dateRange.getBegin().compareTo(fullMonthBack) < 0) {
            context
                .addConstraintViolation("errors.channelTriggersReport.fromDate")
                .withValue(dateRange)
                .withPath("dateRange");
        }
    }

    @Validation
    public void validateInvitations(ValidationContext context, @ValidateBean InvitationsReportParameters parameters) {
        validateDates(context, parameters.getDateRange());

        if (context.props("accountId").reachableAndNoViolations()) {
            Account account = em.find(Account.class, parameters.getAccountId());
            if (account == null) {
                context
                    .addConstraintViolation("errors.entity.notFound")
                    .withPath("accountId")
                    .withValue(parameters.getAccountId());
            }
        }
    }

    @Validation
    public void validateInventoryEstimation(ValidationContext context, @ValidateBean InventoryEstimationReportParameters parameters) {
        validateDates(context, parameters.getDateRange());

        if (parameters.getReservedPremium() != null && parameters.getReservedPremium().scale() > 2) {
            context
                .addConstraintViolation("errors.field.maxFractionDigits")
                .withPath("reservedPremium")
                .withParameters("2");
        }

        Long accountId = parameters.getAccountId();
        if (context.props("accountId").reachableAndNoViolations() && !utilityService.isEntityExists(Account.class, accountId)) {
            context
                .addConstraintViolation("errors.entity.notFound")
                .withPath("accountId")
                .withValue(accountId);
        }

        if (!context.props("accountId").reachableAndNoViolations()) {
            return;
        }

        validateUserSite(context, parameters.getSiteId());

        Long siteId = parameters.getSiteId();
        if (siteId != null) {
            Site site = em.find(Site.class, siteId);
            boolean validSite = site != null &&
                    site.getAccount().getId().equals(parameters.getAccountId());
            if (!validSite) {
                context
                    .addConstraintViolation("errors.entity.notFound")
                    .withPath("siteId")
                    .withValue(siteId);
                return;
            }
        }

        Long tagId = parameters.getTagId();
        if (tagId != null) {
            Tag tag = em.find(Tag.class, tagId);
            boolean validTag = tag != null &&
                    tag.getSite().getId().equals(parameters.getSiteId());
            if (!validTag) {
                context
                    .addConstraintViolation("errors.entity.notFound")
                    .withPath("tagId")
                    .withValue(tagId);
                return;
            }
        }
    }

    private void validateUserSite(ValidationContext context, Long siteId) {
        if (!currentUserService.isSiteLevelRestricted() || siteId == null) {
            return;
        }
        if (!currentUserService.isSiteAccessGranted(em.find(Site.class, siteId))) {
            context
                    .addConstraintViolation("errors.entity.notFound")
                    .withPath("siteId")
                    .withValue(siteId);
        }
    }
}

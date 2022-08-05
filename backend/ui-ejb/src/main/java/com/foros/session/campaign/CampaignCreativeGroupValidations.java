package com.foros.session.campaign;

import com.foros.model.Country;
import com.foros.model.FrequencyCap;
import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.AdvertisingAccountBase;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.CcgRate;
import com.foros.model.campaign.ChannelTarget;
import com.foros.model.campaign.DeliveryPacing;
import com.foros.model.campaign.OptInStatusTargeting;
import com.foros.model.campaign.RateType;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DeviceChannel;
import com.foros.model.channel.GeoChannel;
import com.foros.model.isp.Colocation;
import com.foros.model.security.AccountType;
import com.foros.model.site.Site;
import com.foros.session.BeanValidations;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.bulk.BulkOperation;
import com.foros.session.bulk.IdNameTO;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsValidations;
import com.foros.session.campaign.bulk.BulkUtil;
import com.foros.session.campaign.bulk.NameUniquenessFilter;
import com.foros.session.campaign.ccg.bulk.DevicesOperationSupport;
import com.foros.session.campaign.ccg.bulk.GeoTargetOperationSupport;
import com.foros.session.campaign.ccg.bulk.RemoveDevicesOperation;
import com.foros.session.campaign.ccg.bulk.SetBidStrategyOperation;
import com.foros.session.campaign.ccg.bulk.SetDevicesOperation;
import com.foros.session.campaign.ccg.bulk.SetFrequencyCapOperation;
import com.foros.session.campaign.ccg.bulk.SetRateOperation;
import com.foros.session.campaign.ccg.bulk.SetSitesOperation;
import com.foros.session.campaign.ccg.bulk.SitesOperationSupport;
import com.foros.session.channel.ExpressionChannelValidations;
import com.foros.session.channel.service.DeviceChannelService;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.campaign.CampaignCreativeGroupQueryImpl;
import com.foros.util.CollectionUtils;
import com.foros.util.EntityUtils;
import com.foros.util.UploadUtils;
import com.foros.util.tree.TreeNode;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationException;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.LinkValidator;
import com.foros.validation.constraint.validator.RangeValidator;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.util.DuplicateChecker;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.ObjectUtils;

@LocalBean
@Stateless
@Validations
public class CampaignCreativeGroupValidations {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private WalledGardenService walledGardenService;

    @EJB
    private BeansValidationService beanValidationService;

    @EJB
    private BeanValidations beanValidations;

    @EJB
    private ValidationService validationService;

    @EJB
    private QueryExecutorService executorService;

    @EJB
    private OperationsValidations operationsValidations;

    @EJB
    private ExpressionChannelValidations expressionChannelValidations;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private CampaignValidations campaignValidations;

    @EJB
    private DeviceChannelService deviceChannelService;

    @Validation
    public void validateCreateOrUpdate(ValidationContext validationContext, CampaignCreativeGroup group, Campaign campaign, TGTType tgtType) {
        Campaign existingCampaign = null;

        ValidationContext context = validationContext.createSubContext(group);

        CampaignCreativeGroup existing = find(group);
        validate(context, existing == null ? OperationType.CREATE : OperationType.UPDATE, group, existing, campaign);

        if (group.getCampaign()!= null && group.getCampaign().getId()!=null) {
            existingCampaign = em.find(Campaign.class, group.getCampaign().getId());
        }

        AdvertiserAccount account;
        if (existing != null) {
            account = existing.getAccount();
        } else if (existingCampaign != null) {
            account = existingCampaign.getAccount();
        } else {
            account = em.find(AdvertiserAccount.class, group.getAccount().getId());
        }
        validateDeviceTargeting(context, group, account.getAccountType());
        validateDeliverySchedule(context, group, existing, existingCampaign == null ? campaign : existingCampaign);
        validateTypes(context, group, existing, campaign, CCGType.TEXT, tgtType);

        if (TGTType.CHANNEL == tgtType) {
            validateTarget(context, group, existing, account);
        }
    }

    private CampaignCreativeGroup find(CampaignCreativeGroup group) {
        return group.getId() == null ? null : em.find(CampaignCreativeGroup.class, group.getId());
    }

    @Validation
    public void validateCreate(ValidationContext context, CampaignCreativeGroup group) {
        Campaign campaign = em.find(Campaign.class, group.getCampaign().getId());
        validate(context, OperationType.CREATE, group, null, campaign);
        validateDeviceTargeting(context, group, campaign.getAccount().getAccountType());
        validateDeliverySchedule(context, group, null, campaign);
    }

    @Validation
    public void validateCreateAllTargeted(ValidationContext context, List<CampaignCreativeGroup> groups) {
        for (CampaignCreativeGroup group : groups) {
            validateCreate(context, group);
            validateTarget(context, group, null, group.getAccount());
        }
    }

    @Validation
    public void validateUpdate(ValidationContext context, CampaignCreativeGroup group) {
        CampaignCreativeGroup existing = find(group);
        validate(context, OperationType.UPDATE, group, existing, existing.getCampaign());
        validateDeliverySchedule(context, group, existing, existing.getCampaign());
    }

    @Validation
    public void validateDeliverySchedule(ValidationContext context, CampaignCreativeGroup group, CampaignCreativeGroup existing, Campaign campaign) {
        if (context.isReachable("deliveryScheduleFlag") && group.isDeliveryScheduleFlag() && group.getCcgSchedules().isEmpty()) {
            context.addConstraintViolation("deliverySchedule.campaignCreativeGroup.errors")
                   .withPath("deliverySchedule");
            return;
        }

        if(context.isReachable("ccgSchedules") && group.getCcgSchedules() != null &&
                !group.getCcgSchedules().isEmpty() && !group.isDeliveryScheduleFlag() ) {
            context.addConstraintViolation("deliverySchedule.deliveryFlag.required")
                   .withPath("deliverySchedule");
            return;
        }

        if(context.isReachable("ccgSchedules")) {
            if (!campaign.getCampaignSchedules().isEmpty() && !ScheduleHelper.containsChildSchedule(campaign.getCampaignSchedules(), group.getCcgSchedules())) {
                context.addConstraintViolation("errors.deliverySchedule.conflicted")
                        .withPath("conflictedDeliverySchedule");
            }
            campaignValidations.validateDeliverySchedule(context, group.getCcgSchedules());
        } else {
            if (!campaign.getCampaignSchedules().isEmpty() && existing  != null && !ScheduleHelper.containsChildSchedule(campaign.getCampaignSchedules(), existing.getCcgSchedules())) {
                context.addConstraintViolation("errors.deliverySchedule.conflicted")
                        .withPath("conflictedDeliverySchedule");
            }

        }

    }

    @Validation
    public void validateUpdateTarget(ValidationContext context, CampaignCreativeGroup group) {
        CampaignCreativeGroup existing = find(group);
        validateTarget(context, group, existing, existing.getAccount());
    }

    @Validation
    public void validateCreateTarget(ValidationContext context, CampaignCreativeGroup group) {
        validateTarget(context, group, null, group.getAccount());
    }

    private void validateTarget(ValidationContext context, CampaignCreativeGroup group, CampaignCreativeGroup existing, AdvertiserAccount account) {
        if(!context.isReachable("channel") && !context.isReachable("channelTarget")) {
            return;
        }

        if (group.getChannel() != null || group.getChannelTarget() != null) {
            CCGType ccgType = existing != null ? existing.getCcgType() : group.getCcgType();
            if (ccgType == CCGType.TEXT) {
                TGTType tgtType = existing != null ? existing.getTgtType() : group.getTgtType();
                if (tgtType != TGTType.CHANNEL || !account.getAccountType().isAllowTextChannelAdvertisingFlag()) {
                    context
                            .addConstraintViolation("ccg.error.target.type")
                            .withPath("ccgType");
                }
            }
        }

        ChannelTarget channelTarget = null;
        if(context.isReachable("channelTarget")) {
            channelTarget = group.getChannelTarget();
        } else if (existing != null) {
            channelTarget = existing.getChannelTarget();
        }

        Channel channel = null;
        if (context.isReachable("channel")) {
            if (EntityUtils.isIdentifiable(group.getChannel())) {
                if (channelTarget == ChannelTarget.TARGETED) {
                    beanValidations.linkValidator(context, Channel.class)
                            .withPath("channel")
                            .validate(group.getChannel());
                    if (context.hasViolation("channel")) {
                        return;
                    }
                }
                channel = em.find(Channel.class, group.getChannel().getId());
            }
        } else if (existing != null) {
            channel = existing.getChannel();
        }

        if (channel != null) {
            AdvertisingAccountBase channelOwnerAccount;
            if (account.getAgency() != null) {
                channelOwnerAccount = account.getAgency();
            } else {
                channelOwnerAccount = account;
            }
            expressionChannelValidations.validateChannel(context.createSubContext(channel, "channelTarget"),
                    channelOwnerAccount, chooseCountry(group, existing), channel, "channel");
            if (channelTarget != ChannelTarget.TARGETED){
                context
                        .addConstraintViolation("ccg.error.target")
                        .withPath("channelTarget");
            }
        } else if (channelTarget == ChannelTarget.TARGETED){
            context
                    .addConstraintViolation("ccg.error.target")
                    .withPath("channelTarget");
        }

        if (existing != null) {
            validateVersion(context, group, existing);
        }
    }

    @Validation
    public void validateUpdateUserSampleGroups(ValidationContext context,
            @ValidateBean(ValidationMode.UPDATE) CampaignCreativeGroup group) {
        CampaignCreativeGroup existing = find(group);
        long userSampleStart = group.getUserSampleGroupStart() == null ? 0 : group.getUserSampleGroupStart();
        long userSampleEnd = group.getUserSampleGroupEnd() == null ? 0 : group.getUserSampleGroupEnd();

        if (context.props("userSampleGroupStart").reachableAndNoViolations()
                && context.props("userSampleGroupEnd").reachableAndNoViolations()) {
            if ((userSampleStart * userSampleEnd == 0) && (userSampleStart != 0 || userSampleEnd != 0)) {
                context.addConstraintViolation("ccg.error.userSampleGroups.required").withPath("userSampleGroups");
            }

            if (userSampleStart > userSampleEnd) {
                context.addConstraintViolation("ccg.error.userSampleGroups").withPath("userSampleGroups");
            }
        }

        validateVersion(context, group, existing);
    }

    @Validation
    public void validateUpdateGeoTarget(ValidationContext context, CampaignCreativeGroup group, List<GeoChannel> geoChannels) {
        CampaignCreativeGroup existing = find(group);
        validateGeoTargetingList(context, existing, geoChannels, null, chooseCountry(group, existing));
    }

    private void validateGeoTargeting(ValidationContext context, CampaignCreativeGroup group, CampaignCreativeGroup existing) {
        if (!context.isReachable("geoChannels") && !context.isReachable("geoChannelsExcluded")) {
            return;
        }
        validateGeoTargetingList(context, existing, group.getGeoChannels(), group.getGeoChannelsExcluded(), chooseCountry(group, existing));
    }

    private Country chooseCountry(CampaignCreativeGroup group, CampaignCreativeGroup existing) {
        return existing == null || group.isChanged("country") ? group.getCountry() : existing.getCountry();
    }

    private void validateGeoTargetingList(ValidationContext context, CampaignCreativeGroup existing, Collection<GeoChannel> geoChannels,
                                          Collection<GeoChannel> geoChannelsExcluded, Country country) {
        validateGeoTargetingList(context, existing != null ? existing.getGeoChannels() : null, geoChannels, country, "geoChannels");
        validateGeoTargetingList(context, existing != null ? existing.getGeoChannelsExcluded() : null, geoChannelsExcluded, country, "geoChannelsExcluded");
    }

    private void validateGeoTargetingList(ValidationContext context, Collection<GeoChannel> existingGeoChannels,
                                          Collection<GeoChannel> geoChannels, Country country, String path) {
        Map<Long, GeoChannel> index = existingGeoChannels != null ? EntityUtils.mapEntityIds(existingGeoChannels) : Collections.<Long, GeoChannel>emptyMap();
        if (geoChannels != null) {
            Iterator<GeoChannel> iterator = geoChannels.iterator();
            for (int i = 0; iterator.hasNext(); i++) {
                GeoChannel channel = iterator.next();
                ValidationContext subContext = context.createSubContext(channel, path, i);
                LinkValidator<GeoChannel> linkValidator = beanValidations.linkValidator(subContext, GeoChannel.class);
                linkValidator
                        .withRequired(true)
                        .withCheckDeleted(index.get(channel.getId()))
                        .validate(channel);

                if (subContext.hasViolations()) {
                    continue;
                }
                GeoChannel persistentChannel = linkValidator.getEntity();

                if (country == null) {
                    country = persistentChannel.getCountry();
                } else if (!country.getCountryCode().equals(persistentChannel.getCountry().getCountryCode())) {
                    subContext.addConstraintViolation("ccg.error.geoTarget.sameCountry");
                }
            }
        }
    }

    private void validateDeviceTargeting(ValidationContext context, CampaignCreativeGroup group, AccountType accountType) {
        if (!context.isReachable("deviceChannels")) {
            return;
        }
        Set<DeviceChannel> groupChannels = new HashSet<>();
        for (DeviceChannel dc: group.getDeviceChannels()) {
            DeviceChannel existing = em.find(DeviceChannel.class, dc.getId());
            if (existing == null) {
                context.addConstraintViolation("ccg.error.deviceTargeting.channelNotFound")
                    .withParameters(dc.getId())
                    .withPath("deviceTargetingOptions");
                return;
            }
            groupChannels.add(existing);
        }
        Set<DeviceChannel> accountTypeChannels = accountType.getDeviceChannels();
        validateDeviceChannelsInAccountType(context, groupChannels, accountTypeChannels);

        Set<DeviceChannel> normalizedGroupChannels = deviceChannelService.getNormalizedDeviceChannelsCollection(
                EntityUtils.getEntityIds(groupChannels), EntityUtils.getEntityIds(accountTypeChannels));

        validateChildChannelsStatus(context, normalizedGroupChannels, deviceChannelService.getBrowsersTreeRoot(), false);
        validateChildChannelsStatus(context, normalizedGroupChannels, deviceChannelService.getApplicationsTreeRoot(), false);
    }

    @Validation
    public void validateUpdateDeviceTargeting(ValidationContext context, CampaignCreativeGroup group) {
        CampaignCreativeGroup existing = find(group);
        validateVersion(context, group, existing);
        if (group.getDeviceChannels().size() == 0) {
            context.addConstraintViolation("ccg.error.deviceTargeting.mustBeSelected")
                .withPath("deviceTargetingOptions");
        } else {
            validateDeviceTargeting(context, group, existing.getAccount().getAccountType());
        }
    }

    private boolean validateDeviceChannelsInAccountType(ValidationContext context, Set<DeviceChannel> groupChannels, Set<DeviceChannel> allowedChannels) {
        for (DeviceChannel groupChannel: groupChannels) {
            boolean allowed = false;
            for (DeviceChannel allowedChannel: allowedChannels) {
                if (allowedChannel.getId().equals(groupChannel.getId())) {
                    allowed = true;
                    break;
                }
            }
            if (!allowed) {
                context.addConstraintViolation("ccg.error.deviceTargeting.notAllowedByAccountType")
                       .withParameters(groupChannel.getName())
                        .withPath("deviceTargetingOptions");
                return false;
            }
        }
        return true;
    }

    private boolean validateChildChannelsStatus(ValidationContext context, Set<DeviceChannel> groupChannels, TreeNode<EntityTO> current, boolean parentNotLive) {
        boolean currentSelected = false;
        for (DeviceChannel groupChannel: groupChannels) {
            if (groupChannel.getId().equals(current.getElement().getId())) {
                currentSelected = true;
                break;
            }
        }
        boolean currentNotLive = !current.getElement().getStatus().equals(Status.ACTIVE);
        if (currentSelected) {
            if (currentNotLive) {
                context.addConstraintViolation("ccg.error.deviceTargeting.notLive")
                        .withParameters(current.getElement().getName())
                        .withPath("deviceTargetingOptions");
                return false;
            }
            if (parentNotLive) {
                context.addConstraintViolation("ccg.error.deviceTargeting.childOfNotLiveCannotBeSelected")
                    .withPath("deviceTargetingOptions");
                return false;
            }
        }
        for (TreeNode<EntityTO> child: current.getChildren()) {
            if (!validateChildChannelsStatus(context, groupChannels, child, currentNotLive || parentNotLive)) {
                return false;
            }
        }
        return true;
    }

    private void validateVersion(ValidationContext context, CampaignCreativeGroup group, CampaignCreativeGroup existing) {
        if (context.isReachable("version") && !ObjectUtils.equals(group.getVersion(), existing.getVersion())) {
            context.addConstraintViolation("errors.version")
                .withValue(group.getVersion())
                .withPath("version");
        }
    }

    @Validation
    public void validateMerge(ValidationContext context, Operations<CampaignCreativeGroup> ccgOperations) {
        DuplicateChecker<Operation<CampaignCreativeGroup>> duplicateIdChecker =
                DuplicateChecker.createOperationDuplicateChecker();
        DuplicateChecker<Operation<CampaignCreativeGroup>> duplicateNameChecker =
                DuplicateChecker.create(new OperationNameFetcher());

        int index = 0;
        for (Operation<CampaignCreativeGroup> mergeOperation : ccgOperations.getOperations()) {
            ValidationContext operationContext = context.createSubContext(mergeOperation, "operations", index++);

            operationsValidations.validateOperation(operationContext, mergeOperation, "campaignCreativeGroup");
            if (operationContext.hasViolations()) {
                continue;
            }

            CampaignCreativeGroup group = mergeOperation.getEntity();

            OperationType operationType = mergeOperation.getOperationType();

            ValidationContext groupContext = operationContext
                    .subContext(group)
                    .withPath("campaignCreativeGroup")
                    .withMode(operationType.toValidationMode())
                    .build();

            if (!canMerge(group, operationType, groupContext)) {
                continue;
            }

            CampaignCreativeGroup ccg = mergeOperation.getEntity();
            CampaignCreativeGroup existing;
            Campaign campaign;

            switch (operationType) {
                case CREATE:
                    existing = null;
                    campaign = em.find(Campaign.class, group.getCampaign().getId());
                    break;
                case UPDATE:
                    existing = find(group);
                    campaign = existing.getCampaign();
                    break;
                default:
                    throw new RuntimeException();
            }
            ccg.setCampaign(campaign);

            duplicateIdChecker.check(groupContext, "id", mergeOperation);

            if (ccg.isChanged("name") && ccg.getName() != null) {
                duplicateNameChecker.check(groupContext, "name", mergeOperation);
            }

            validate(groupContext, operationType, group, existing, campaign);
            validateGeoTargeting(groupContext, group, existing);
            validateDeviceTargeting(groupContext, group, campaign.getAccount().getAccountType());
            validateDeliverySchedule(groupContext, group, existing, campaign);
            validateTypes(groupContext, ccg, existing, campaign, null, null);

            validateTarget(groupContext, group, existing, campaign.getAccount());
        }
    }

    private boolean canMerge(CampaignCreativeGroup group, OperationType operationType, ValidationContext context) {
        ValidationContext subContext = context.createSubContext();
        advertiserEntityRestrictions.canMerge(subContext, group, operationType);
        return subContext.ok();
    }

    private void validate(ValidationContext validationContext, OperationType operationType, CampaignCreativeGroup group, CampaignCreativeGroup existing, Campaign campaign) {
        ValidationContext context = validationContext
                .subContext(group)
                .withMode(operationType.toValidationMode())
                .build();

        beanValidationService.validate(context);

        TGTType tgtType = existing == null ? group.getTgtType() : existing.getTgtType();
        CCGType ccgType = UploadUtils.isLinkWithErrors(campaign) ? null : CCGType.valueOf(campaign.getCampaignType());
        if (ccgType == null && !UploadUtils.getUploadContext(campaign).hasErrors()) {
            throw new ValidationException("Should be error for campaign");
        }

        // validate tgtType
        if (context.isReachable("tgtType")) {
            if (ccgType == CCGType.TEXT && !group.isChanged("tgtType")) {
                context
                        .addConstraintViolation("errors.field.required")
                        .withPath("tgtType");
            }
            if (ccgType == CCGType.DISPLAY && tgtType != TGTType.CHANNEL) {
                context
                        .addConstraintViolation("ccg.error.target.type")
                        .withPath("ccgType");
            }
        }

        AdvertiserAccount account = BulkUtil.findAccount(em, campaign);
        AccountType accountType = null;

        if (account != null) {
            accountType = account.getAccountType();
        }

        Integer maxFractionDigits = null;
        if (account != null) {
            maxFractionDigits = account.getCurrency().getFractionDigits();
        }

        // validate Ad Group Rate
        if (context.props("ccgRate").reachableAndNoViolations()) {
            validateCcgRate(context, maxFractionDigits, group.getCcgRate());
        }

        CcgRate ccgRate = context.isReachable("ccgRate") || existing == null ? group.getCcgRate() : existing.getCcgRate();
        if (ccgType != null) {
            if (tgtType != null && ccgRate != null && ccgRate.getRateType() != null && accountType != null) {
                if (!accountType.isRateAllowed(ccgType, tgtType, ccgRate.getRateType())) {
                    context
                            .addConstraintViolation("errors.rateTypeIsNotAllowed")
                            .withPath("ccgRate");
                }
            } else {
                context.setValidationIncomplete();
            }
        }

        if (account != null && walledGardenService.isAdvertiserWalledGarden(account.getId())) {
            if (ccgType == CCGType.TEXT) {
                context
                        .addConstraintViolation("errors.textGroupsNotAllowedWalledGarden")
                        .withPath("ccgType");
            } else if (ccgRate != null && !Arrays.asList(RateType.CPM, RateType.CPC).contains(ccgRate.getRateType())) {
                context
                        .addConstraintViolation("errors.walledGardenNotAllowedRateType")
                        .withPath("ccgType");
            }
        }

        // minimum CTR Goal
        if (context.isReachable("minCtrGoal")) {
            BigDecimal minCtrGoal = group.getMinCtrGoal();
            validateMinCtrGoal(context, minCtrGoal);
        }

        // frequency cap
        if (context.isReachable("frequencyCap")) {
            FrequencyCap frequencyCap = group.getFrequencyCap();
            if (frequencyCap != null) {
                ValidationContext subContext = context.createSubContext(frequencyCap, "frequencyCap");
                validationService.validateWithContext(subContext, "FrequencyCap.update", frequencyCap);
            }
        }

        // budget
        if (context.isReachable("budget")) {
            BigDecimal budget = group.getBudget();
            if (budget == null) {
                if (ccgType == CCGType.DISPLAY) {
                    context.addConstraintViolation("errors.field.required")
                            .withPath("budget");
                }
            } else {
                if (maxFractionDigits != null) {
                    context.validator(RangeValidator.class)
                            .withMin(BigDecimal.ZERO, maxFractionDigits)
                            .withMax(Campaign.BUDGET_MAX, maxFractionDigits)
                            .withPath("budget")
                            .validate(budget);

                    context.validator(FractionDigitsValidator.class)
                            .withPath("budget")
                            .withFraction(maxFractionDigits)
                            .validate(budget);
                } else {
                    context.setValidationIncomplete();
                }
            }
        }

        Date campaignDateStart = campaign != null ? campaign.getDateStart() : null;
        Date campaignDateEnd = campaign != null ? campaign.getDateEnd() : null;

        boolean linkedToCampaignEndDateFlag;
        if (context.isReachable("linkedToCampaignEndDateFlag")) {
            linkedToCampaignEndDateFlag = group.isLinkedToCampaignEndDateFlag();
        } else {
            linkedToCampaignEndDateFlag = existing.isLinkedToCampaignEndDateFlag();
        }

        Date effectiveDateEnd;
        if (linkedToCampaignEndDateFlag) {
            effectiveDateEnd = campaignDateEnd;
        } else {
            effectiveDateEnd = context.isReachable("dateEnd") ? group.getDateEnd() : existing.getDateEnd();
        }

        if (!linkedToCampaignEndDateFlag && effectiveDateEnd == null) {
            context
                .addConstraintViolation("errors.field.required")
                .withPath("dateEnd");
        }

        Date effectiveDateStart = context.isReachable("dateStart") || existing ==null ?
                group.getDateStart() : existing.getDateStart();

        validateDeliveryPacing(context, ccgType, maxFractionDigits, effectiveDateEnd, group, existing);

        // validate dates
        if (context.isReachable("dateStart") || context.isReachable("dateEnd")) {
            validateDates(context, effectiveDateEnd, effectiveDateStart, "dateEnd", "{ccg.dateEnd.fullName}", "{ccg.dateStart.fullName}");
        }
        if (context.isReachable("dateStart")) {
            validateDates(context, effectiveDateStart, campaignDateStart, "dateStart", "{ccg.dateStart.fullName}", "{campaign.dateStart.fullName}");
        }
        if (context.isReachable("dateEnd")) {
            validateDates(context, campaignDateEnd, effectiveDateEnd, "dateEnd", "{campaign.dateEnd.fullName}", "{ccg.dateEnd.fullName}");
        }

        // validate Ad Group Country Targeting
        validateCountry(context, group, existing, account);

        StatusValidationUtil.validateStatus(context, group, existing);

        if (operationType == OperationType.UPDATE) {
            validateVersion(context, group, existing);
        }

        validateOptInStatusTargeting(context, group, existing);


        // validate sites
        if (context.isReachable("sites") || context.isReachable("includeSpecificSites")) {
            validateSites(group, existing, context, account, tgtType);
        }

        // validate colocations
        if (context.isReachable("colocations")) {
            Set<Colocation> colocations = group.getColocations();

            if (colocations != null) {
                Country groupCountry = group.getId() != null ?
                        chooseCountry(group, existing) :
                        account.getCountry();

                int index = 0;
                for (Colocation colocation : colocations) {

                    ValidationContext colocationContext = context.createSubContext(colocation, "colocations", index++);

                    LinkValidator<Colocation> linkValidator = beanValidations.linkValidator(colocationContext, Colocation.class);

                    linkValidator.withRequired(true).validate(colocation);

                    if (colocationContext.hasViolations() ||
                        (existing != null && existing.getColocations().contains(colocation))) {
                        continue;
                    }

                    Colocation persistentColocation = linkValidator.getEntity();

                    if (!persistentColocation.getAccount().getCountry().equals(groupCountry)) {
                        colocationContext
                                .addConstraintViolation("ccg.error.colocation.countryIsNotSame");
                    }

                    if (!account.isTestFlag() && persistentColocation.getAccount().isTestFlag()) {
                        colocationContext
                                .addConstraintViolation("ccg.error.colocation.test");
                    }

                    if (persistentColocation.getInheritedStatus() == Status.DELETED) {
                        colocationContext
                                .addConstraintViolation("ccg.error.colocation.deleted");
                    }
                }
            }
        }

        if (context.isReachable("sequentialAdservingFlag") && group.isSequentialAdservingFlag()) {
            if (group.getRotationCriteria() == null) {
                context.addConstraintViolation("errors.field.required")
                    .withPath("rotationCriteria");
            }
        }
    }

    private void validateSites(CampaignCreativeGroup group, CampaignCreativeGroup existing, ValidationContext context, AdvertiserAccount account, TGTType tgtType) {
        Set<Site> sites;
        if (context.isReachable("sites")) {
            sites = group.getSites();
        } else {
            sites = existing.getSites();
        }

        if (group.isChanged("sites") && !CollectionUtils.isNullOrEmpty(sites) &&
                (!advertiserEntityRestrictions.canEditSiteTargeting(account) || !TGTType.CHANNEL.equals(tgtType))) {
            context.addConstraintViolation("errors.siteTargetingIsNotAllowed").withPath("sites");
        }

        if (context.isReachable("sites") && sites != null) {
            validateSitesList(context, account, sites, existing == null ? null : existing.getSites());
        }
    }

    private void validateSitesList(ValidationContext context, AdvertiserAccount account,
                                   Collection<Site> sites, Collection<Site> existing) {
        int index = 0;
        for (Site site : sites) {

            ValidationContext siteContext = context.createSubContext(site, "sites", index++);

            LinkValidator<Site> linkValidator = beanValidations.linkValidator(siteContext, Site.class);

            linkValidator.withRequired(true).validate(site);

            if (siteContext.hasViolations() || (existing != null && existing.contains(site))) {
                continue;
            }

            Site persistentSite = linkValidator.getEntity();

            if (!persistentSite.getAccount().getCountry().equals(account.getCountry())) {
                siteContext.addConstraintViolation("ccg.error.site.countryIsNotSame");
            }
            if (!account.isTestFlag() && persistentSite.getAccount().isTestFlag()) {
                siteContext.addConstraintViolation("ccg.error.site.test");
            }
            if (persistentSite.getInheritedStatus() == Status.DELETED) {
                siteContext.addConstraintViolation("ccg.error.site.deleted");
            }
        }
    }

    private void validateCcgRate(ValidationContext context, Integer maxFractionDigits, CcgRate ccgRate) {
        BigDecimal rate = ccgRate != null ? ccgRate.getValue() : null;

        if (rate == null) {
            context
                    .addConstraintViolation("errors.field.required")
                    .withPath("ccgRate");
        } else {
            if (maxFractionDigits != null) {
                context
                    .validator(FractionDigitsValidator.class)
                    .withPath("ccgRate")
                    .withFraction(maxFractionDigits)
                    .validate(rate);

                context.validator(RangeValidator.class)
                        .withMin(BigDecimal.ZERO, maxFractionDigits)
                        .withMax(new BigDecimal("10000000"), maxFractionDigits)
                        .withPath("ccgRate")
                        .validate(rate);
            } else {
                context.setValidationIncomplete();
            }
        }
    }

    private void validateMinCtrGoal(ValidationContext context, BigDecimal minCtrGoal) {
        context
                .validator(FractionDigitsValidator.class)
                .withFraction(3)
                .withPath("minCtrGoal")
                .validate(minCtrGoal);

        context.
                validator(RangeValidator.class)
                .withMin(BigDecimal.ZERO)
                .withMax(new BigDecimal(100))
                .withPath("minCtrGoal")
                .validate(minCtrGoal);
    }

    private void validateOptInStatusTargeting(ValidationContext context, CampaignCreativeGroup group, CampaignCreativeGroup existing) {
        if (!context.props("optInStatusTargeting", "minUidAge").any().reachable()) {
            return;
        }

        if (group.getOptInStatusTargeting() != null) {
            beanValidationService.validate(
                    context.subContext(group.getOptInStatusTargeting())
                            .withPath("optInStatusTargeting")
                            .build()
            );
        }

        if (context.props("optInStatusTargeting", "minUidAge").any().haveViolations()) {
            return;
        }

        OptInStatusTargeting optInStatusTargeting;
        if (existing == null) {
            if (group.isChanged("optInStatusTargeting")) {
                optInStatusTargeting = group.getOptInStatusTargeting();
            } else {
                optInStatusTargeting = OptInStatusTargeting.newDefaultValue();
            }
        } else {
            if (context.isReachable("optInStatusTargeting")) {
                optInStatusTargeting = group.getOptInStatusTargeting();
            } else {
                optInStatusTargeting = existing.getOptInStatusTargeting();
            }
        }

        if (optInStatusTargeting != null && !optInStatusTargeting.isEnabled()) {
            context.addConstraintViolation("ccg.error.optInStatusTargeting")
                   .withPath("optInStatusTargeting");
        }

        if (group.isChanged("minUidAge")) {

            if (group.getMinUidAge() == null) {
                context.addConstraintViolation("errors.field.required")
                        .withPath("minUidAge");
            } else if ((optInStatusTargeting == null || !optInStatusTargeting.isOptedInUsers()) &&
                       group.getMinUidAge() > 0) {
                context.addConstraintViolation("ccg.error.optInStatusTargeting.optedIn.minUidAge.invalid")
                        .withPath("minUidAge");
            }
        }
    }

    private void validateCountry(ValidationContext context, CampaignCreativeGroup group, CampaignCreativeGroup existing, AdvertiserAccount account) {
        if (context.isReachable("country")) {
            Country newCountry = group.getCountry();

            beanValidations.linkValidator(context, Country.class)
                .withPath("country")
                .withRequired(true)
                .validate(newCountry);

            if (context.hasViolation("country")) {
                return;
            }

            if (account != null && !account.isInternational() && !account.getCountry().equals(newCountry)) {
                context
                        .addConstraintViolation("errors.canNotChange")
                        .withParameters("{ccg.country}", account.getCountry().getCountryCode())
                        .withPath("country");
            } else if (existing != null && !newCountry.equals(existing.getCountry())) {
                switch (existing.getTgtType()) {
                    case CHANNEL:
                        if (ChannelTarget.TARGETED.equals(existing.getChannelTarget())) {
                            context
                                    .addConstraintViolation("ccg.error.country.canNotChangeTargeted")
                                    .withPath("country");
                        } else if (!existing.getGeoChannels().isEmpty() || !existing.getGeoChannelsExcluded().isEmpty()) {
                            context
                                    .addConstraintViolation("ccg.error.country.canNotChangeGeoChannel")
                                    .withPath("country");
                        }
                        break;
                    case KEYWORD:
                        if (!existing.getCcgKeywords().isEmpty()) {
                            context
                                    .addConstraintViolation("ccg.error.country.canNotChangeKeywords")
                                    .withPath("country");
                        }
                        break;
                }
            }
        }
    }

    @Validation
    public void validateLink(ValidationContext context, CampaignCreativeGroup group, TGTType tgtType) {
        if (UploadUtils.isLinkWithErrors(group.getCampaign())) {
            // parent campaign is invalid link so CCG link validation meaningless
            return;
        }

        if (group.getId() == null) {
            context
                .addConstraintViolation("campaign.csv.errors.ccgNotExist")
                .withPath("name");
            return;
        }

        CampaignCreativeGroup existing = em.find(CampaignCreativeGroup.class, group.getId());
        validateTypes(context, group, existing, existing.getCampaign(), CCGType.TEXT, tgtType);
    }

    @Validation
    public void validateNameConstraintViolations(ValidationContext context, Operations<CampaignCreativeGroup> ccgOperations) {
        NameUniquenessFilter<CampaignCreativeGroup> filter = new NameUniquenessFilter<CampaignCreativeGroup>();

        Set<CampaignCreativeGroup> toBeChecked = new HashSet<CampaignCreativeGroup>();
        for (Operation<CampaignCreativeGroup> operation : ccgOperations.getOperations()) {
            if (filter.accept(operation)) {
                toBeChecked.add(operation.getEntity());
            }
        }

        List<IdNameTO> duplicated = new CampaignCreativeGroupQueryImpl()
                .existingByName(toBeChecked)
                .asNamedTO("campaign.id", "name")
                .executor(executorService)
                .list();

        HashSet<IdNameTO> duplicatedSet = new HashSet<IdNameTO>(duplicated);

        int i = 0;
        for (Operation<CampaignCreativeGroup> operation : ccgOperations.getOperations()) {
            CampaignCreativeGroup ccg = operation.getEntity();
            if (toBeChecked.contains(ccg) && duplicatedSet.contains(new IdNameTO(ccg.getCampaign().getId(), ccg.getName()))) {
                context
                    .createSubContext(operation, "operations", i++)
                    .createSubContext(ccg, "campaignCreativeGroup")
                    .addConstraintViolation("errors.duplicate")
                    .withPath("name")
                    .withParameters("{ccg.name}");
            }
        }
    }

    private void validateTypes(ValidationContext context, CampaignCreativeGroup group, CampaignCreativeGroup existing, Campaign campaign, CCGType ccgType, TGTType tgtType) {
        if (ccgType != null) {
            CCGType type = UploadUtils.isLinkWithErrors(campaign) ? null : CCGType.valueOf(campaign.getCampaignType());
            if (type != null && !ccgType.equals(type)) {
                if (CCGType.TEXT.equals(ccgType)) {
                    context
                            .addConstraintViolation("campaign.csv.errors.ccgNotText")
                            .withPath("ccgType")
                            .withValue(type);
                } else {
                    context
                            .addConstraintViolation("errors.field.invalid")
                            .withPath("ccgType")
                            .withValue(type);
                }
            }
        }

        if (tgtType != null) {
            TGTType type = context.isReachable("tgtType") ? group.getTgtType() : existing.getTgtType();
            if (!tgtType.equals(type)) {
                context
                        .addConstraintViolation("campaign.csv.errors.tgtType." + tgtType.name())
                        .withPath("tgtType")
                        .withValue(type);
            }
        } else if (context.isReachable("tgtType") && !group.isChanged("tgtType")) {
            context
                    .addConstraintViolation("errors.field.required")
                    .withPath("tgtType");
        }
    }

    private void validateDates(ValidationContext context, Date date1, Date date2, String path, String fieldKey1, String fieldKey2) {
        if (date1 != null && date2 != null && date1.before(date2)) {
            context
            .addConstraintViolation("errors.dates")
            .withParameters(fieldKey2, fieldKey1)
            .withPath(path);
        }
    }

    @Validation
    public void  validatePerform(ValidationContext context, Long advertiserId, List<Long> ccgIds, BulkOperation<CampaignCreativeGroup> operation) {
        validateIds(context.createSubContext(ccgIds, "groups"), advertiserId, ccgIds);
        validateOperation(context.createSubContext(operation, "operation"), advertiserId, ccgIds, operation);
    }

    private void validateOperation(
            ValidationContext context,
            Long advertiserId,
            List<Long> ccgIds,
            BulkOperation<CampaignCreativeGroup> operation) {
        AdvertiserAccount account = em.find(AdvertiserAccount.class, advertiserId);

        if (operation instanceof SetFrequencyCapOperation) {
            FrequencyCap frequencyCap = ((SetFrequencyCapOperation) operation).getFrequencyCap();
            context = context.createSubContext(frequencyCap, "frequencyCap");
            validationService.validateWithContext(context, "FrequencyCap.update", frequencyCap);
        } else if (operation instanceof SetRateOperation) {
            CcgRate ccgRate = ((SetRateOperation) operation).getRate();
            context = context.createSubContext(ccgRate, "ccgRate");
            validateCcgRate(context, account.getCurrency().getFractionDigits(), ccgRate);
        } else if (operation instanceof SetBidStrategyOperation) {
            BigDecimal minCtrGoal = ((SetBidStrategyOperation) operation).getMinCtrGoal();
            context = context.createSubContext(minCtrGoal, "minCtrGoal");
            validateMinCtrGoal(context, minCtrGoal);
        } else if (operation instanceof SitesOperationSupport) {
            Collection<Site> sites = ((SitesOperationSupport) operation).getSites();
            ValidationContext subContext = context.createSubContext(sites, "sites");
            if (sites.isEmpty()) {
                boolean emptyAllowed = false;
                if (operation instanceof SetSitesOperation ) {
                    emptyAllowed = !((SetSitesOperation) operation).isIncludeSpecificSitesFlag();
                }
                if (!emptyAllowed) {
                    subContext.addConstraintViolation("errors.field.required");
                }
            } else {
                validateSitesList(subContext, account, sites, null);
            }
        } else if (operation instanceof DevicesOperationSupport) {
            validateDevicesOperation(context, account, ccgIds, operation);
        } else if (operation instanceof GeoTargetOperationSupport) {
            Set<GeoChannel> geoChannels = ((GeoTargetOperationSupport) operation).getGeoChannels();
            ValidationContext subContext = context.createSubContext(geoChannels, "geoChannels");
            validateGeoTargetingList(subContext, null, geoChannels, null, account.isInternational() ? null : account.getCountry());
        } else {
            context.addConstraintViolation("errors.field.invalid").withPath("operation");
        }
    }

    private void validateDevicesOperation(
            ValidationContext context,
            AdvertiserAccount account,
            List<Long> ccgIds,
            BulkOperation<CampaignCreativeGroup> operation) {
        Set<DeviceChannel> deviceChannels = ((DevicesOperationSupport) operation).getDeviceChannels();
        for (DeviceChannel dc : deviceChannels) {
            DeviceChannel existing = em.find(DeviceChannel.class, dc.getId());
            if (existing == null) {
                context.addConstraintViolation("ccg.error.deviceTargeting.channelNotFound")
                        .withParameters(dc.getId())
                        .withPath("deviceTargetingOptions");
                return;
            }
        }
        ValidationContext subContext = context.createSubContext(deviceChannels, "devices");
        Set<DeviceChannel> accountTypeChannels = account.getAccountType().getDeviceChannels();
        TreeNode<EntityTO> browsersRoot = deviceChannelService.getBrowsersTreeRoot();
        TreeNode<EntityTO> applicationsRoot = deviceChannelService.getApplicationsTreeRoot();

        if (operation instanceof SetDevicesOperation) {
            if (deviceChannels.size() == 0) {
                subContext.addConstraintViolation("ccg.error.deviceTargeting.mustBeSelected").withPath("deviceTargetingOptions");
            }
        }

        for (Long ccgId : ccgIds) {
            CampaignCreativeGroup existing = em.find(CampaignCreativeGroup.class, ccgId);
            Set<DeviceChannel> deviceChannelsToUpdate = ((DevicesOperationSupport) operation).getChannelsToUpdate(existing);

            if (operation instanceof RemoveDevicesOperation && deviceChannelsToUpdate.size() == 0 && existing.getStatus() != Status.DELETED) {
                subContext.addConstraintViolation("ccg.error.deviceTargeting.mustBeSelectedForgroup").withParameters(existing.getName())
                        .withPath("deviceTargetingOptions");
            }

            validateDeviceChannelsInAccountType(subContext, deviceChannelsToUpdate, accountTypeChannels);
            validateChildChannelsStatus(subContext, deviceChannelsToUpdate, browsersRoot, false);
            validateChildChannelsStatus(subContext, deviceChannelsToUpdate, applicationsRoot, false);
        }
    }

    private void validateIds(ValidationContext context, Long advertiserId, List<Long> ccgIds) {
        if (ccgIds == null || ccgIds.isEmpty()) {
            context.addConstraintViolation("errors.field.required");
            return;

        }

        for (int i = 0; i < ccgIds.size(); i++) {
            Long ccgId = ccgIds.get(i);
            ValidationContext subContext = context.subContext(ccgId).withIndex(i).build();
            CampaignCreativeGroup ccg = em.find(CampaignCreativeGroup.class, ccgId);
            if (ccg == null) {
                subContext.addConstraintViolation("errors.entity.notFound");
                continue;
            }

            if (!ccg.getAccount().getId().equals(advertiserId)) {
                subContext.addConstraintViolation("errors.field.invalid");
                continue;
            }

            advertiserEntityRestrictions.canUpdate(subContext, ccg);
        }
    }

    @Validation
    public void validateUpdate(ValidationContext context, List<CampaignCreativeGroup> ccgs) {
        for (int i = 0; i < ccgs.size(); i++) {
            CampaignCreativeGroup ccg = ccgs.get(i);
            ValidationContext groupContext = context.subContext(ccg)
                    .withPath("groups")
                    .withIndex(i)
                    .build();
            validateUpdate(groupContext, ccg);
        }
    }

    public class OperationNameFetcher implements DuplicateChecker.IdentifierFetcher<Operation<CampaignCreativeGroup>> {
        @Override
        public Object fetch(Operation<CampaignCreativeGroup> operation) {
            CampaignCreativeGroup entity = operation.getEntity();
            return new IdNameTO(entity.getCampaign().getId(), entity.getName());
        }
    }

    private void validateDeliveryPacing(ValidationContext context, CCGType ccgType, Integer maxFractionDigits,
                                        Date effectiveDateEnd, CampaignCreativeGroup group, CampaignCreativeGroup existing) {
        DeliveryPacing deliveryPacing;
        BigDecimal dailyBudget;

        if (existing == null) {
            // For DISPLAY default value is set in constructor
            // For TEXT default is DeliveryPacing.FIXED
            deliveryPacing = !group.isChanged("deliveryPacing") && ccgType == CCGType.TEXT ? DeliveryPacing.FIXED :
                    group.getDeliveryPacing();
            dailyBudget = group.getDailyBudget();
        } else {
            deliveryPacing = group.isChanged("deliveryPacing") ? group.getDeliveryPacing() : existing.getDeliveryPacing();
            dailyBudget = group.isChanged("dailyBudget") ? group.getDailyBudget() : existing.getDailyBudget();
        }

        if (deliveryPacing == null || ccgType == CCGType.TEXT && deliveryPacing != DeliveryPacing.FIXED) {
            context.addConstraintViolation("ccg.error.invalidDeliveryPacing")
                    .withPath("deliveryPacing")
                    .withValue(group.getDeliveryPacing())
                    .withError(BusinessErrors.CCG_ERROR);
            return;
        }

        switch (deliveryPacing) {

            case FIXED:
                if (dailyBudget == null) {
                    context.addConstraintViolation("errors.field.required")
                            .withPath("dailyBudget");
                    break;
                }
                if (maxFractionDigits != null) {
                    context.validator(FractionDigitsValidator.class)
                            .withPath("dailyBudget")
                            .withFraction(maxFractionDigits)
                            .validate(dailyBudget);

                    if (context.props("budget").reachableAndNoViolations()) {
                        if (group.getBudget() != null) {
                            context.validator(RangeValidator.class)
                                    .withMin(BigDecimal.ZERO, maxFractionDigits)
                                    .withMax(group.getBudget())
                                    .withPath("dailyBudget")
                                    .validate(dailyBudget);
                        } else {
                            context.validator(RangeValidator.class)
                                    .withMin(BigDecimal.ZERO, maxFractionDigits)
                                    .withMax(Campaign.BUDGET_MAX, maxFractionDigits)
                                    .withPath("dailyBudget")
                                    .validate(dailyBudget);
                        }
                    }
                    break;
                }

                context.setValidationIncomplete();
                break;

            case DYNAMIC:
                checkDailyBudgetIsEmpty(context, group);

                if (effectiveDateEnd == null) {
                    context.addConstraintViolation("ccg.deliveryPacing.dynamic.noEndDate")
                            .withPath("deliveryPacing");
                }

                break;

            case UNRESTRICTED:
                checkDailyBudgetIsEmpty(context, group);
                break;
        }
    }

    private void checkDailyBudgetIsEmpty(ValidationContext context, CampaignCreativeGroup group) {
        boolean isDailyBudgetReachable = context.isReachable("dailyBudget");
        BigDecimal dailyBudget = group.getDailyBudget();

        if (isDailyBudgetReachable && dailyBudget != null) {
            context
                    .addConstraintViolation("ccg.deliveryPacing.fixedDailyBudgetIsNotAllowed")
                    .withPath("dailyBudget");
        }
    }
}

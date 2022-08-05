package com.foros.session.campaign;

import com.foros.model.FrequencyCap;
import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.account.MarketplaceType;
import com.foros.model.admin.WalledGarden;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignType;
import com.foros.model.campaign.DeliveryPacing;
import com.foros.model.campaign.WeekSchedule;
import com.foros.model.channel.Channel;
import com.foros.model.security.User;
import com.foros.session.BeanValidations;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.account.AccountService;
import com.foros.session.admin.walledGarden.WalledGardenService;
import com.foros.session.bulk.*;
import com.foros.session.campaign.bulk.BulkUtil;
import com.foros.session.campaign.bulk.NameUniquenessFilter;
import com.foros.session.channel.ExpressionChannelValidations;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.campaign.CampaignQueryImpl;
import com.foros.util.CollectionUtils;
import com.foros.util.StringUtil;
import com.foros.util.bean.Filter;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.LinkValidator;
import com.foros.validation.constraint.validator.RangeValidator;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.util.DuplicateChecker;
import com.foros.validation.util.EntityIdFetcher;
import org.apache.commons.lang.ObjectUtils;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.*;

@LocalBean
@Stateless
@Validations
public class CampaignValidations {
    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private CampaignService campaignService;

    @EJB
    private WalledGardenService walledGardenService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

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
    private CurrentUserService currentUserService;

    @EJB
    private AccountService accountService;

    @EJB
    private ExpressionChannelValidations expressionChannelValidations;

    @Validation
    public void validateMerge(ValidationContext context, Operations<Campaign> campaignOperations) {
        int index = 0;

        NameUniquenessFilter<Campaign> filter = new NameUniquenessFilter<Campaign>();

        for (Operation<Campaign> mergeOperation : campaignOperations.getOperations()) {
            ValidationContext operationContext = context.createSubContext(mergeOperation, "operations", index++);

            if (!validateOperation(operationContext, mergeOperation, "campaign")) {
                continue;
            }

            Campaign campaign = mergeOperation.getEntity();

            OperationType operationType = mergeOperation.getOperationType();

            ValidationContext campaignContext = operationContext
                    .subContext(campaign)
                    .withPath("campaign")
                    .withMode(operationType.toValidationMode())
                    .build();

            if (!validateMerge(campaignContext, campaign, operationType)) {
                filter.ignore(campaign);
                continue;
            }

            Campaign existing = campaign.getId() == null ? null : campaignService.find(campaign.getId());
            validate(campaignContext, operationType, campaign, existing);
        }

        DuplicateChecker.<Campaign>createOperationDuplicateChecker()
                .check(campaignOperations.getOperations())
                .createConstraintViolations(context, "operations[{0}].campaign", "id");


        DuplicateChecker.create(new OperationNameFetcher(), filter)
                .check(campaignOperations.getOperations())
                .createConstraintViolations(context, "operations[{0}].campaign", "name");

    }

    private boolean validateOperation(ValidationContext operationContext, Operation<Campaign> mergeOperation, String entityPath) {
        operationsValidations.validateOperation(operationContext, mergeOperation, entityPath);
        return !operationContext.hasViolations();
    }

    private boolean validateMerge(ValidationContext context, Campaign campaign, OperationType operationType) {
        advertiserEntityRestrictions.canMerge(context, campaign, operationType);
        return context.ok();
    }

    @Validation
    public void validateView(ValidationContext context, Campaign campaign) {
        advertiserEntityRestrictions.canView(context, campaign);
    }

    @Validation
    public void validateCreate(ValidationContext context, Campaign campaign) {
        validate(context
                .subContext(campaign)
                .withMode(ValidationMode.CREATE)
                .build(), OperationType.CREATE, campaign, null);
    }

    @Validation
    public void validateUpdate(ValidationContext context, Campaign campaign) {
        Campaign existing = campaignService.find(campaign.getId());
        validate(context
                .subContext(campaign)
                .withMode(ValidationMode.UPDATE)
                .build(), OperationType.UPDATE, campaign, existing);
    }

    private void validate(ValidationContext context, OperationType operationType, Campaign campaign, Campaign existing) {
        beanValidationService.validate(context);

        if (operationType == OperationType.CREATE) {
            validateAccount(context, campaign, existing);
        }
        validateMaxPubShare(context, campaign);
        validateDates(context, campaign, existing);
        validateUsers(context, campaign, existing);
        validateBudget(context, campaign, existing);
        validateDeliveryPacing(context, campaign, existing);
        validateWalledGarden(context, campaign, existing);
        validateFrequencyCap(context, campaign, existing);
        if (operationType == OperationType.UPDATE) {
            validateVersion(context, campaign, existing);
        }

        validateDeliverySchedule(context, campaign.getCampaignSchedules());

        validateExcludedChannels(context, campaign, existing);
    }

    private void validateExcludedChannels(ValidationContext context, Campaign campaign, Campaign existing) {
        if(!context.isReachable("excludedChannels")) {
            return;
        }

        AdvertiserAccount account = BulkUtil.findAccount(em, campaign);
        if (account == null) {
            context.setValidationIncomplete();
            return;
        }

        DuplicateChecker<Channel> duplicateChecker = DuplicateChecker.create(new EntityIdFetcher<Channel>());
        int i = 0;
        for (Channel channel : campaign.getExcludedChannels()) {
            ValidationContext subContext = context.createSubContext(channel.getId(), "excludedChannels", i++);

            if (!duplicateChecker.check(subContext, "", channel)) {
                continue;
            }

            LinkValidator<Channel> channelLinkValidator = beanValidations.linkValidator(subContext, Channel.class);
            channelLinkValidator
                    .withRequired(true)
                    .validate(channel);

            if (subContext.hasViolations()) {
                continue;
            }

            expressionChannelValidations.validateChannel(
                    subContext,
                    account.isStandalone() ? account : account.getAgency(),
                    account.getCountry(),
                    channelLinkValidator.getEntity(),
                    "channel"
            );
        }
    }

    private void validateAccount(ValidationContext context, Campaign campaign, Campaign existing) {
        if (context.isReachable("account")) {
            AdvertiserAccount account = campaign.getAccount();
            beanValidations.linkValidator(context, AdvertiserAccount.class)
                    .withPath("account")
                    .validate(account);
        }
    }

    public void validateDeliverySchedule(ValidationContext context, Collection<? extends WeekSchedule> weekSchedules) {
        if (weekSchedules != null) {
            int maxTime = 10079; //(24 * 7 * 60) - 1  total minutes in a week (starting from 0) = 10079

            for (WeekSchedule schedule : weekSchedules) {
                if (schedule == null) {
                    context.addConstraintViolation("errors.field.required")
                            .withPath("deliverySchedule");
                    continue;
                }

                if (schedule.getTimeFrom() == null) {
                    context.addConstraintViolation("errors.required")
                            .withParameters("{deliverySchedule.timeFrom}")
                            .withPath("deliverySchedule");
                } else if (schedule.getTimeFrom() < 0 || schedule.getTimeFrom() > maxTime - 1) {
                    context.addConstraintViolation("errors.field.range")
                            .withParameters(0, maxTime)
                            .withPath("deliverySchedule");
                } else if (schedule.getTimeFrom()%30L != 0) {
                    context.addConstraintViolation("campaign.errors.period.halfhour.timeFrom")
                            .withParameters("{deliverySchedule.timeFrom}")
                            .withPath("deliverySchedule");
                }

                if (schedule.getTimeTo() == null) {
                    context.addConstraintViolation("errors.required")
                            .withParameters("{deliverySchedule.timeTo}")
                            .withPath("deliverySchedule");
                } else if (schedule.getTimeTo() < 1 || schedule.getTimeTo() > maxTime) {
                    context.addConstraintViolation("errors.field.range")
                            .withParameters(1, maxTime)
                            .withPath("deliverySchedule");
                } else if ((schedule.getTimeTo() + 1)%30L != 0) {
                    context.addConstraintViolation("campaign.errors.period.halfhour.timeTo")
                            .withParameters("{deliverySchedule.timeTo}")
                            .withPath("deliverySchedule");
                }

                if (schedule.getTimeFrom() != null & schedule.getTimeTo() != null) {
                    if (schedule.getTimeFrom() >= schedule.getTimeTo()) {
                        context.addConstraintViolation("errors.dates")
                                .withParameters("{deliverySchedule.timeTo}", "{deliverySchedule.timeFrom}")
                                .withPath("deliverySchedule");
                    }

                    for (WeekSchedule schedule2 : weekSchedules) {
                        if (schedule == schedule2 || schedule2 == null ||
                                schedule2.getTimeFrom() == null || schedule2.getTimeTo() == null) {
                            continue;
                        }
                        if (hasIntersection(schedule, schedule2)) {
                            context.addConstraintViolation("errors.intersection")
                                    .withParameters(schedule.getTimeFrom() + ":" + schedule.getTimeTo() + "," +
                                            schedule2.getTimeFrom() + ":" + schedule2.getTimeTo())
                                    .withPath("deliverySchedule");
                        }
                    }
                }
            }
        }
    }

    private boolean hasIntersection(WeekSchedule schedule1, WeekSchedule schedule2) {
        if (schedule1.getTimeFrom().equals(schedule2.getTimeFrom()) && schedule1.getTimeTo().equals(schedule2.getTimeTo())) {
            return true;
        }

        if (schedule1.getTimeFrom() > schedule2.getTimeFrom() && schedule1.getTimeTo() < schedule2.getTimeTo()) {
            return true;
        }

        if (schedule1.getTimeFrom() < schedule2.getTimeFrom() && schedule1.getTimeTo() > schedule2.getTimeTo()) {
            return true;
        }

        if (schedule1.getTimeFrom() < schedule2.getTimeTo() && schedule1.getTimeTo() > schedule2.getTimeFrom()) {
            return true;
        }

        if (schedule2.getTimeTo() > schedule1.getTimeFrom() && schedule2.getTimeFrom() < schedule1.getTimeTo()) {
            return true;
        }
        return false;
    }

    private void validateFrequencyCap(ValidationContext context, Campaign campaign, Campaign existing) {
        CampaignType campaignType = existing == null ? campaign.getCampaignType() : existing.getCampaignType();
        if (campaignType == null) {
            return;
        }

        if (context.isReachable("frequencyCap")) {
            FrequencyCap frequencyCap = campaign.getFrequencyCap();
            if (frequencyCap != null) {
                context = context.createSubContext(frequencyCap, "frequencyCap");
                validationService.validateWithContext(context, "FrequencyCap.update", frequencyCap);
            }
        }
    }

    private void validateDeliveryPacing(ValidationContext context, Campaign campaign, Campaign existing) {
        DeliveryPacing pacing = context.isReachable("deliveryPacing") ? campaign.getDeliveryPacing() : existing.getDeliveryPacing();
        BigDecimal budget = context.isReachable("budget") || existing == null ? campaign.getBudget() : existing.getBudget();

        if (pacing == DeliveryPacing.UNRESTRICTED) {
            checkDailyBudgetIsEmpty(context, campaign);
        } else if (pacing == DeliveryPacing.FIXED) {
            checkDailyBudget(context, campaign, existing);
        } else if (pacing == DeliveryPacing.DYNAMIC) {
            checkDailyBudgetIsEmpty(context, campaign);

            AdvertiserAccount account = BulkUtil.findAccount(em, campaign);
            if (CampaignUtil.canChangeBudget(account, existing) && Campaign.isBudgetUnlimited(budget, campaign.getCampaignType())) {
                context
                        .addConstraintViolation("errors.dynamicDeliveryPacingIsNotAllowed")
                        .withPath("dynamicDailyBudget");
            }

            Date dateEnd = context.isReachable("dateEnd") ? campaign.getDateEnd() : (existing != null ? existing.getDateEnd() : null);
            if (dateEnd == null) {
                context
                        .addConstraintViolation("ccg.deliveryPacing.dynamic.noEndDate")
                        .withPath("deliveryPacing")
                        .withError(BusinessErrors.CAMPAIGN_DELIVERY_PACING_DYNAMIC_NO_END_DATE);
            }
        }
    }

    private void checkDailyBudgetIsEmpty(ValidationContext context, Campaign campaign) {
        boolean isDailyBudgetReachable = context.isReachable("dailyBudget");
        BigDecimal dailyBudget = campaign.getDailyBudget();

        if (isDailyBudgetReachable && dailyBudget != null) {
            context
                    .addConstraintViolation("ccg.deliveryPacing.fixedDailyBudgetIsNotAllowed")
                    .withPath("dailyBudget");
        }
    }

    private void checkDailyBudget(ValidationContext context, Campaign campaign, Campaign existing) {
        boolean isDailyBudgetReachable = context.isReachable("dailyBudget");
        BigDecimal dailyBudget = isDailyBudgetReachable || existing == null ? campaign.getDailyBudget() : existing.getDailyBudget();

        if (dailyBudget == null) {
            context
                    .addConstraintViolation("errors.field.required")
                    .withPath("dailyBudget");
            return;
        }

        AdvertiserAccount account = BulkUtil.findAccount(em, campaign);
        Integer maxFractionDigits = null;
        if (account != null) {
            maxFractionDigits = account.getCurrency().getFractionDigits();
        }

        if (maxFractionDigits != null) {
            context.validator(FractionDigitsValidator.class)
                    .withPath("dailyBudget")
                    .withFraction(maxFractionDigits)
                    .validate(dailyBudget);

            context.validator(RangeValidator.class)
                    .withPath("dailyBudget")
                    .withMin(BigDecimal.ZERO, maxFractionDigits)
                    .withMax(Campaign.BUDGET_MAX, maxFractionDigits)
                    .validate(dailyBudget);
        } else {
            context.setValidationIncomplete();
        }
    }

    private void validateVersion(ValidationContext context, Campaign campaign, Campaign existing) {
        if (context.isReachable("version") && !ObjectUtils.equals(campaign.getVersion(), existing.getVersion())) {
            context.addConstraintViolation("errors.version")
                .withValue(campaign.getVersion())
                .withPath("version");
        }
    }

    @Validation
    public void validateCreateOrUpdate(ValidationContext context, Campaign campaign) {
        Campaign existing = campaign.getId() != null ? campaignService.find(campaign.getId()) : null;
        OperationType operationType = existing == null ? OperationType.CREATE : OperationType.UPDATE;
        ValidationContext campaignContext = context
                .subContext(campaign)
                .withMode(operationType.toValidationMode())
                .build();
        validate(campaignContext, operationType, campaign, existing);

        if (operationType == OperationType.CREATE) {
            validateCreativeGroups(campaignContext, campaign);
        }

        validateTextCampaignType(campaignContext, campaign, existing);
    }

    @Validation
    public void validateLink(ValidationContext context, Campaign campaign) {
        if (campaign.getId() == null) {
        	if (StringUtil.isPropertyNotEmpty(campaign.getName())) {
	            context
	                    .addConstraintViolation("campaign.csv.errors.campaignNotExist")
	                    .withPath("name");
        	}
            return;
        }

        Campaign existing = em.find(Campaign.class, campaign.getId());
        validateTextCampaignType(context, campaign, existing);
    }

    private void validateTextCampaignType(ValidationContext context, Campaign campaign, Campaign existing) {
        CampaignType campaignType = existing == null ? campaign.getCampaignType() : existing.getCampaignType();
        if (campaignType != CampaignType.TEXT) {
            context.addConstraintViolation("campaign.csv.errors.campaignNotText");
        }
    }

    private void validateCreativeGroups(ValidationContext context, Campaign campaign) {
        if (campaign.getCreativeGroups().isEmpty()) {
            context.addConstraintViolation("campaign.csv.errors.newCampaignWithoutCCG");
        }
    }

    private void validateMaxPubShare(ValidationContext context, Campaign entity) {
        if (!context.isReachable("maxPubShare")) {
            return;
        }

        if (!advertiserEntityRestrictions.canAdvanced()) {
            entity.setMaxPubShare(BigDecimal.ONE);
            entity.unregisterChange("maxPubShare");
            return;
        }

        context.validator(RangeValidator.class)
            .withMin(new BigDecimal("0.1"))
            .withMax(BigDecimal.ONE)
            .withPath("maxPubShare")
            .validate(entity.getMaxPubShare());

        context.validator(FractionDigitsValidator.class)
            .withFraction(1)
            .withPath("maxPubShare")
            .validate(entity.getMaxPubShare());
    }

    private void validateDates(ValidationContext context, Campaign campaign, Campaign existing) {
        // validate dates
        if (context.isReachable("dateEnd")
                && !isEndDateCleanAllowed(campaign, existing)
                && campaign.getDateEnd() == null) {
            context
                    .addConstraintViolation("errors.field.required")
                    .withPath("dateEnd")
                    .withValue(campaign.getDateEnd());
        }

        Date dateStart = campaign.isChanged("dateStart") ? campaign.getDateStart() : (existing != null ? existing.getDateStart() : null);
        Date dateEnd = campaign.isChanged("dateEnd") ? campaign.getDateEnd() : (existing != null ? existing.getDateEnd(): null);

        if (context.isReachable("dateStart") && dateStart != null && dateEnd != null && dateEnd.before(dateStart)) {
            context
                    .addConstraintViolation("errors.dates")
                    .withParameters("{campaign.dateStart}", "{campaign.dateEnd}")
                    .withPath("dateEnd")
                    .withValue(dateStart);
        }
    }

    private boolean isEndDateCleanAllowed(Campaign campaign, Campaign existing) {
        return existing == null || existing.getDateEnd() == null || campaign.getDateEnd() != null
                || campaignService.isEndDateCleanAllowed(campaign.getId());
    }

    private void validateUsers(ValidationContext context, Campaign campaign, Campaign existing) {
        // validate users
        AdvertiserAccount account = BulkUtil.findAccount(em, campaign);

        if (campaign.isChanged("salesManager")) {
            if (currentUserService.isExternal()) {
                context
                        .addConstraintViolation("errors.forbidden")
                        .withPath("salesManager")
                        .withValue(campaign.getSalesManager());
            } else {
                LinkValidator<User> validator =
                        beanValidations.linkValidator(context, User.class)
                                .withPath("salesManager")
                                .withRequired(false)
                                .withCheckDeleted(existing == null ? null : existing.getSalesManager());

                validator
                        .validate(campaign.getSalesManager());

                final User salesManager = validator.getEntity();

                if (salesManager != null && salesManager.getId() != null) {
                    List<EntityTO> salesManagers = accountService.getSalesManagers(account);
                    EntityTO found = CollectionUtils.find(salesManagers, new Filter<EntityTO>() {
                        @Override
                        public boolean accept(EntityTO element) {
                            return element.getId().equals(salesManager.getId());
                        }
                    });
                    if (found == null) {
                        context
                                .addConstraintViolation("errors.field.invalid")
                                .withPath("salesManager")
                                .withValue(campaign.getSalesManager());
                    }
                }
            }
        }
    }

    private void validateBudget(ValidationContext context, Campaign campaign, Campaign existing) {

        if (!context.isReachable("budget")) {
            return;
        }

        AdvertiserAccount account = BulkUtil.findAccount(em, campaign);
        if (account == null) {
            return;
        }

        if (!CampaignUtil.canChangeBudget(account, existing)) {
            if (campaign.isChanged("budget")) {
                context.addConstraintViolation("errors.field.canNotChange")
                        .withPath("budget");
            }
        } else {
            if (existing == null && !campaign.isChanged("budget")) {
                // budget must be explicitly set n creation
                context.addConstraintViolation("errors.field.required")
                        .withPath("budget");
            } else if (campaign.getBudget() == null) {
                // unlimited budget
                CampaignType campaignType = existing == null ? campaign.getCampaignType() : existing.getCampaignType();
                if (campaignType == CampaignType.TEXT) {
                    context.addConstraintViolation("errors.field.required")
                           .withPath("budget");
                }
            } else {
                int fractionDigits = account.getCurrency().getFractionDigits();

                context.validator(FractionDigitsValidator.class)
                        .withPath("budget")
                        .withFraction(fractionDigits)
                        .validate(campaign.getBudget());

                context.validator(RangeValidator.class)
                        .withMin(BigDecimal.ZERO, fractionDigits)
                        .withMax(Campaign.BUDGET_MAX, fractionDigits)
                        .withPath("budget")
                        .validate(campaign.getBudget());
            }
        }
    }

    private void validateWalledGarden(ValidationContext context, Campaign campaign, Campaign existing) {
        Account account = existing == null ? campaign.getAccount() : existing.getAccount();
        if (context.isReachable("marketplaceType") && account != null) {
            WalledGarden wg = walledGardenService.findByAdvertiser(account.getId());
            boolean isWalledGardenEnabled = wg != null;

            if (isWalledGardenEnabled) {
                if (campaign.getMarketplaceType() == MarketplaceType.NOT_SET) {
                    context
                        .addConstraintViolation("WalledGarden.validation.marketplace")
                        .withPath("marketplaceType");
                }
            } else {
                campaign.setMarketplaceType(MarketplaceType.NOT_SET);
                campaign.unregisterChange("marketplaceType");
            }
        }
    }

    @Validation
    public void validateNameConstraintViolations(ValidationContext context, Operations<Campaign> campaignOperations) {
        NameUniquenessFilter<Campaign> filter = new NameUniquenessFilter<Campaign>();

        Set<Campaign> toBeChecked = new HashSet<Campaign>();
        for (Operation<Campaign> operation : campaignOperations.getOperations()) {
            if (filter.accept(operation)) {
                toBeChecked.add(operation.getEntity());
            }
        }

        List<IdNameTO> duplicated = new CampaignQueryImpl()
                .existingByName(toBeChecked)
                .asNamedTO("account.id", "name")
                .executor(executorService)
                .list();

        Set<IdNameTO> duplicatedSet = new HashSet<IdNameTO>(duplicated);

        int i = 0;
        for (Operation<Campaign> operation : campaignOperations.getOperations()) {
            Campaign campaign = operation.getEntity();
            IdNameTO to = new IdNameTO(campaign.getAccount().getId(), campaign.getName());
            if (toBeChecked.contains(campaign) && duplicatedSet.contains(to)) {
                context
                    .createSubContext(operation, "operations", i++)
                    .createSubContext(campaign, "campaign")
                    .addConstraintViolation("errors.duplicate")
                    .withPath("name")
                    .withParameters("{campaign.name}");
            }
        }
    }

    public static class OperationNameFetcher implements DuplicateChecker.IdentifierFetcher<Operation<Campaign>> {
        @Override
        public Object fetch(Operation<Campaign> operation) {
            Campaign campaign = operation.getEntity();
            return new IdNameTO(campaign.getAccount().getId(), campaign.getName());
        }
    }
}

package com.foros.session.campaignAllocation;

import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.model.campaign.CampaignAllocationStatus;
import com.foros.model.opportunity.Opportunity;
import com.foros.session.campaign.CampaignService;
import com.foros.util.EntityUtils;
import com.foros.util.NumberUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.RangeValidator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Validations
public class CampaignAllocationsValidations {
    public static final int MAX_ALLOCATIONS_COUNT = 4;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private BeansValidationService beanValidationService;

    @EJB
    private CampaignAllocationService campaignAllocationService;

    @EJB
    private CampaignService campaignService;

    @Validation
    public void validateCreateUpdate(ValidationContext context, Campaign campaign) {
        if (!context.isReachable("campaignAllocations")) {
            return;
        }

        Campaign existingCampaign = em.find(Campaign.class, campaign.getId());
        Map<Long, CampaignAllocation> existingAllocations = EntityUtils.mapEntityIds(existingCampaign.getAllocations());

        // Check deleted
        int count = 0;
        Map<Long, CampaignAllocation> allocations = EntityUtils.mapEntityIds(campaign.getAllocations());
        for (Long id : existingAllocations.keySet()) {
            CampaignAllocation existingAllocation = existingAllocations.get(id);

            // do not delete allocations with spent amount
            if (allocations.get(id) == null) {
                if (CampaignAllocationStatus.ACTIVE.equals(existingAllocation.getStatus())
                        && existingAllocation.getUtilizedAmount().compareTo(BigDecimal.ZERO) > 0) {
                    context.addConstraintViolation("campaignAllocation.errors.illegalDelete")
                            .withPath("campaignAllocations")
                            .withValue(id);
                    break;
                }
            } else {
                count ++;
            }
        }
        if (context.hasViolations()) {
            return;
        }

        for (CampaignAllocation allocation : campaign.getAllocations()) {
            if (allocation.getId() == null) {
                count ++;
            }
        }

        if (count > MAX_ALLOCATIONS_COUNT) {
            context.addConstraintViolation("campaignAllocation.errors.maxSize")
                    .withParameters(MAX_ALLOCATIONS_COUNT)
                    .withPath("campaignAllocations.size");
        }

        Map<Long, OpportunityTO> opportunities = campaignAllocationService.getOpportunitiesMap(existingCampaign.getAccount().getId());
        Set<Long> usedOrders = new HashSet<>();

        int i = 0;
        for (CampaignAllocation allocation : campaign.getAllocations()) {
            ValidationContext subContext = context.createSubContext(allocation, "campaignAllocations[" + i++ + "]");

            // basic validation using entity's field constraints
            beanValidationService.validate(subContext);
            if (subContext.hasViolations()) {
                continue;
            }

            Long opportunityId = allocation.getOpportunity().getId();
            OpportunityTO opportunityTo = opportunities.get(opportunityId);
            if (opportunityTo == null) {
                subContext.addConstraintViolation("campaignAllocation.errors.opportunity.invalid").withPath("opportunity");
                continue;
            }

            // new allocations
            if (allocation.getId() == null) {
                // can't have the same order
                // but, they can have orders used in persisted allocations
                if (!usedOrders.add(allocation.getOrder())) {
                    subContext.addConstraintViolation("campaignAllocation.errors.duplicateOrder").withPath("order");
                }

                if (allocation.getOrder() > count) {
                    subContext.addConstraintViolation("errors.field.range").withParameters(1, count).withPath("order");
                }

                // new allocations can't be linked to a depleted IO
                if (BigDecimal.ZERO.compareTo(opportunityTo.getAvailableAmount()) >= 0) {
                    subContext.addConstraintViolation("campaignAllocation.errors.opportunity.invalid").withPath("opportunity");
                    continue;
                }
            }

            // check amount
            int fractionDigits = existingCampaign.getAccount().getCurrency().getFractionDigits();
            subContext.validator(FractionDigitsValidator.class).withPath("amount")
                    .withFraction(fractionDigits).validate(allocation.getAmount());

            BigDecimal minAmount = NumberUtil.addFraction(BigDecimal.ZERO, fractionDigits);
            CampaignAllocation existingAllocation = existingAllocations.get(allocation.getId());
            if (existingAllocation != null && existingAllocation.getUtilizedAmount().compareTo(minAmount) > 0) {
                minAmount = existingAllocation.getUtilizedAmount().setScale(fractionDigits, RoundingMode.CEILING);
            }

            BigDecimal maxAmount = opportunityTo.getAmount();
            subContext.validator(RangeValidator.class)
                    .withMin(minAmount).withMax(maxAmount)
                    .withPath("amount").validate(allocation.getAmount());
        }

        validateCampaignBudget(context, campaign, existingCampaign);
    }

    private void validateCampaignBudget(ValidationContext context, Campaign campaign, Campaign existing) {
        if (context.hasViolations()) {
            return;
        }

        BigDecimal activeBudget = BigDecimal.ZERO;
        for (CampaignAllocation allocation : campaign.getAllocations()) {
            activeBudget = activeBudget.add(allocation.getAmount());
        }

        BigDecimal endedBudged = campaignAllocationService.getEndedBudget(existing.getId());

        BigDecimal budget = endedBudged.add(activeBudget);
        if (budget.compareTo(Campaign.BUDGET_MAX) >= 0) {
            context.addConstraintViolation("campaignAllocation.errors.campaign.budget")
                   .withParameters(NumberUtil.subtractFraction(Campaign.BUDGET_MAX, existing.getAccount().getCurrency().getFractionDigits()))
                   .withValue(budget);
        }
    }

}

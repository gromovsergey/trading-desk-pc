package com.foros.session.campaignCredit;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCredit;
import com.foros.model.campaign.CampaignCreditAllocation;
import com.foros.session.BeanValidations;
import com.foros.util.NumberUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.LinkValidator;
import com.foros.validation.strategy.ValidationMode;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;

@LocalBean
@Stateless
@Validations
public class CampaignCreditAllocationValidations {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private BeanValidations beanValidations;

    @EJB
    private CampaignCreditAllocationService campaignCreditAllocationService;

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) CampaignCreditAllocation allocation) {
        CampaignCredit existingCampaignCredit = em.find(CampaignCredit.class, allocation.getCampaignCredit().getId());
        validateCampaignCredit(context, allocation);
        validateCampaign(context, allocation, existingCampaignCredit);
        validateAmount(context, allocation, null, existingCampaignCredit);
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) CampaignCreditAllocation allocation) {
        CampaignCreditAllocation existing = campaignCreditAllocationService.find(allocation.getId());
        validateAmount(context, allocation, existing, existing.getCampaignCredit());
    }

    private void validateCampaignCredit(ValidationContext context, CampaignCreditAllocation allocation) {
        beanValidations.linkValidator(context, CampaignCredit.class)
                .withPath("campaignCredit")
                .validate(allocation.getCampaignCredit());
    }

    private void validateCampaign(ValidationContext context, CampaignCreditAllocation allocation, CampaignCredit campaignCredit) {
        if (allocation.getCampaign() != null) {
            LinkValidator<Campaign> validator =
                    beanValidations.linkValidator(context, Campaign.class)
                            .withPath("campaign")
                            .withCheckDeleted(null);
            validator.validate(allocation.getCampaign());

            Campaign campaign = validator.getEntity();
            if (campaign != null) {
                if (campaignCredit != null) {
                    AdvertiserAccount campaignAccount = campaign.getAccount();
                    if ((!campaignAccount.isStandalone() && !campaignAccount.getAgency().getId().equals(campaignCredit.getAccount().getId())) ||
                            (campaignAccount.isStandalone() && !campaignAccount.getId().equals(campaignCredit.getAccount().getId()))) {
                        context.addConstraintViolation("CampaignCreditAllocation.errors.campaign.sameAdvertiser")
                                .withPath("campaign");
                    }

                    if (campaignCredit.getAdvertiser() != null && !campaign.getAccount().getId().equals(campaignCredit.getAdvertiser().getId())) {
                        context.addConstraintViolation("CampaignCreditAllocation.errors.campaign.sameAdvertiser")
                                .withPath("campaign");
                    }
                }

                for (CampaignCreditAllocation campaignAllocation : campaign.getCreditAllocations()) {
                    if (campaignAllocation.getAllocatedAmount().compareTo(campaignAllocation.getUsedAmount()) > 0) {
                        context.addConstraintViolation("CampaignCreditAllocation.errors.campaign.otherAllocationsExist")
                                .withPath("campaign");
                        break;
                    }
                }
            }
        }
    }

    private void validateAmount(ValidationContext context, CampaignCreditAllocation allocation,
                                CampaignCreditAllocation existing, CampaignCredit campaignCredit) {
        if (context.isReachable("allocatedAmount") && allocation.getAllocatedAmount() != null) {
            int fractionDigits = campaignCredit.getAccount().getCurrency().getFractionDigits();

            context.validator(FractionDigitsValidator.class)
                    .withPath("allocatedAmount")
                    .withFraction(fractionDigits)
                    .validate(allocation.getAllocatedAmount());

            if (existing == null && allocation.getAllocatedAmount().compareTo(BigDecimal.ZERO) <= 0) {
                context.addConstraintViolation("errors.field.greater")
                        .withParameters(0)
                        .withPath("allocatedAmount");
            }

            // The next validations are:
            // Utilised Amount <= Allocation Amount <= Amount
            if (existing != null && allocation.getAllocatedAmount().compareTo(existing.getUsedAmount()) < 0) {
                context.addConstraintViolation("errors.field.less")
                        .withParameters(NumberUtil.formatNumber(existing.getUsedAmount(), fractionDigits))
                        .withPath("allocatedAmount");
            }

            BigDecimal maxAmount = campaignCredit.getAmount();
            if (allocation.getAllocatedAmount().compareTo(maxAmount) > 0) {
                context.addConstraintViolation("errors.field.notgreater")
                        .withParameters(maxAmount)
                        .withPath("allocatedAmount");
            }

            if (existing != null && allocation.getAllocatedAmount().compareTo(existing.getUsedAmount()) > 0) {
                for (CampaignCreditAllocation campaignAllocation : existing.getCampaign().getCreditAllocations()) {
                    if (!campaignAllocation.getId().equals(existing.getId()) &&
                            campaignAllocation.getAllocatedAmount().compareTo(campaignAllocation.getUsedAmount()) > 0) {
                        context
                                .addConstraintViolation("CampaignCreditAllocation.errors.campaign.otherAllocationsExist")
                                .withPath("allocatedAmount");
                    }
                }
            }
        }
    }
}

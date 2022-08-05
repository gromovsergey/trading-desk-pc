package com.foros.session.campaignCredit;

import com.foros.model.account.Account;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CampaignCredit;
import com.foros.security.AccountRole;
import com.foros.session.BeanValidations;
import com.foros.util.NumberUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.LinkValidator;
import com.foros.validation.constraint.validator.RangeValidator;
import com.foros.validation.strategy.ValidationMode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Validations
public class CampaignCreditValidations {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private BeanValidations beanValidations;

    @EJB
    protected CampaignCreditService campaignCreditService;

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) CampaignCredit campaignCredit) {
        validate(context, campaignCredit, null);
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) CampaignCredit campaignCredit) {
        CampaignCredit existing = campaignCreditService.find(campaignCredit.getId());
        validate(context, campaignCredit, existing);
    }

    private void validate(ValidationContext context, CampaignCredit campaignCredit, CampaignCredit existing) {
        Account account = existing != null ? existing.getAccount() : em.find(Account.class, campaignCredit.getAccount().getId());

        validateAdvertiser(context, campaignCredit, existing, account);
        validateAmount(context, campaignCredit, existing, account);
    }

    private void validateAdvertiser(ValidationContext context, CampaignCredit campaignCredit, CampaignCredit existing, Account account) {
        if (context.isReachable("advertiser")) {
            if (!AccountRole.AGENCY.equals(account.getRole()) && campaignCredit.getAdvertiser() != null) {
                context
                        .addConstraintViolation("errors.field.null")
                        .withPath("advertiser")
                        .withValue(campaignCredit.getAdvertiser());
                return;
            }

            if (campaignCredit.getAdvertiser() != null && campaignCredit.getAdvertiser().getId() != null) {
                LinkValidator<AdvertiserAccount> validator =
                        beanValidations.
                                linkValidator(context, AdvertiserAccount.class)
                                .withPath("advertiser")
                                .withCheckDeleted(existing == null ? null : existing.getAdvertiser());

                validator.validate(campaignCredit.getAdvertiser());

                AdvertiserAccount advertiser = validator.getEntity();
                if (advertiser != null && (advertiser.getAgency() == null || !advertiser.getAgency().getId().equals(account.getId()))) {
                    context
                            .addConstraintViolation("CampaignCredit.errors.advertiser")
                            .withPath("advertiser")
                            .withValue(campaignCredit.getAdvertiser());
                }

                if (existing != null) {
                    List<Long> allocationsAdvertiserIds = campaignCreditService.getAllocationsAdvertiserIds(campaignCredit.getId());

                    // if allocations set for more than one advertiser, advertiser should be empty
                    if (allocationsAdvertiserIds.size() > 1) {
                        context
                                .addConstraintViolation("CampaignCredit.errors.advertiser")
                                .withPath("advertiser")
                                .withValue(campaignCredit.getAdvertiser());
                    }

                    // if allocations set for one advertiser, advertiser should be empty or the same
                    if (allocationsAdvertiserIds.size() == 1 && !allocationsAdvertiserIds.get(0).equals(campaignCredit.getAdvertiser().getId())) {
                        context
                                .addConstraintViolation("CampaignCredit.errors.advertiser")
                                .withPath("advertiser")
                                .withValue(campaignCredit.getAdvertiser());
                    }
                }
            }
        }
    }

    /** https://confluence.ocslab.com/display/TDOC/Campaign+Credit+Allocation+Edit+Screen */
    private void validateAmount(ValidationContext context, CampaignCredit campaignCredit, CampaignCredit existing, Account account) {
        if (context.isReachable("amount")) {
            int fractionDigits = account.getCurrency().getFractionDigits();

            context
                    .validator(FractionDigitsValidator.class)
                    .withPath("amount")
                    .withFraction(fractionDigits)
                    .validate(campaignCredit.getAmount());

            BigDecimal minAmount = BigDecimal.ZERO;
            if (existing != null) {
                // [ Amount ] >= max (Utilised Amount, max (Allocations Amount))
                CampaignCreditStatsTO stats = campaignCreditService.getStats(existing.getId());
                minAmount = stats.getSpentAmount().setScale(fractionDigits, RoundingMode.CEILING);;
                minAmount = minAmount.max(stats.getMaxAllocationAmount().setScale(fractionDigits, RoundingMode.CEILING));
            }

            if (minAmount.compareTo(BigDecimal.ZERO) == 0) {
                minAmount = NumberUtil.addFraction(BigDecimal.ZERO, fractionDigits);
            }

            context.
                    validator(RangeValidator.class)
                    .withMin(minAmount)
                    .withPath("amount")
                    .validate(campaignCredit.getAmount());
            context.
                    validator(RangeValidator.class)
                    .withMax(CampaignCredit.AMOUNT_MAX, fractionDigits)
                    .withPath("amount")
                    .validate(campaignCredit.getAmount());
        }
    }

    @Validation
    public void validateDelete(ValidationContext context, Long campaignCreditId) {
        CampaignCredit campaignCredit = em.find(CampaignCredit.class, campaignCreditId);
        if (campaignCredit != null && campaignCredit.getAllocations().size() > 0) {
            context.
                    addConstraintViolation("CampaignCredit.errors.illegalDelete")
                    .withPath("id");
        }
    }
}

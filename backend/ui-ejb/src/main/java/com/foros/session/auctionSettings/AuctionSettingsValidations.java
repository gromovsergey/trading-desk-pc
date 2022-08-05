package com.foros.session.auctionSettings;

import com.foros.model.account.AccountAuctionSettings;
import com.foros.model.site.TagAuctionSettings;
import com.foros.session.account.AccountService;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.RangeValidator;
import com.foros.validation.strategy.ValidationMode;

import java.math.BigDecimal;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class AuctionSettingsValidations {

    @EJB
    private AccountService accountService;

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) AccountAuctionSettings auctionSettings) {
        validateSumOfAllocations(context,
                auctionSettings.getMaxEcpmShare(),
                auctionSettings.getPropProbabilityShare(),
                auctionSettings.getRandomShare());
        validateMaxRandomCPM(context, auctionSettings);
    }

    private void validateMaxRandomCPM(ValidationContext context, AccountAuctionSettings auctionSettings) {
        int maxFractionDigits = accountService.findInternalAccount(auctionSettings.getId()).getCurrency().getFractionDigits();
        BigDecimal rate = auctionSettings.getMaxRandomCpm();
        context
                .validator(FractionDigitsValidator.class)
                .withPath("maxRandomCpm")
                .withFraction(maxFractionDigits)
                .validate(rate);

        context.validator(RangeValidator.class)
                .withMin(BigDecimal.ZERO)
                .withMax(new BigDecimal("10000000"), maxFractionDigits)
                .withPath("maxRandomCpm")
                .validate(rate);
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) TagAuctionSettings auctionSettings) {
        if (!(auctionSettings.isAllAllocationsNull() || (
                auctionSettings.getMaxEcpmShare() != null &&
                        auctionSettings.getPropProbabilityShare() != null &&
                        auctionSettings.getRandomShare() != null
        ))) {
            context.addConstraintViolation("AuctionSettings.validation.fillAllOrLeaveBlank").withPath("allocations");
        }
        if (!context.hasViolation("allocations") && auctionSettings.getMaxEcpmShare() != null) {
            validateSumOfAllocations(context,
                    auctionSettings.getMaxEcpmShare(),
                    auctionSettings.getPropProbabilityShare(),
                    auctionSettings.getRandomShare());
        }
    }

    private void validateSumOfAllocations(ValidationContext context,
                                          BigDecimal maxEcpmShare,
                                          BigDecimal propProbabilityShare,
                                          BigDecimal randomShare) {
        if (context.props("maxEcpmShare", "propProbabilityShare", "randomShare").reachableAndNoViolations() &&
                maxEcpmShare.add(propProbabilityShare).add(randomShare).compareTo(BigDecimal.valueOf(100)) != 0) {
            context.addConstraintViolation("AuctionSettings.validation.wrongSumOfAllocations").withPath("allocations");
        }
    }
}

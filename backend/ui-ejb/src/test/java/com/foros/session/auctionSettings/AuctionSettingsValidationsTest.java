package com.foros.session.auctionSettings;

import com.foros.AbstractValidationsTest;
import com.foros.model.account.AccountAuctionSettings;
import com.foros.model.account.InternalAccount;
import com.foros.model.site.TagAuctionSettings;
import com.foros.test.factory.InternalAccountTestFactory;
import com.foros.validation.ValidationContext;

import group.Validation;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;

@Category({Validation.class})
public class AuctionSettingsValidationsTest extends AbstractValidationsTest {

    @Autowired
    private InternalAccountTestFactory internalAccountTF;

    @Autowired
    private AuctionSettingsValidations auctionSettingsValidations;

    @Test
    public void testValidateUpdateAccountAuctionSettings() {
        InternalAccount internal = internalAccountTF.createPersistent();
        AccountAuctionSettings auctionSettings = new AccountAuctionSettings(internal.getId(), false);
        auctionSettings.setMaxEcpmShare(BigDecimal.valueOf(80));
        auctionSettings.setPropProbabilityShare(BigDecimal.valueOf(10));
        auctionSettings.setRandomShare(BigDecimal.valueOf(10));
        auctionSettings.setMaxRandomCpm(BigDecimal.ONE);

        ValidationContext context = createContext();
        auctionSettingsValidations.validateUpdate(context, auctionSettings);
        violations = context.getConstraintViolations();
        assertViolationsCount(0);

        auctionSettings.setRandomShare(BigDecimal.valueOf(11));

        context = createContext();
        auctionSettingsValidations.validateUpdate(context, auctionSettings);
        violations = context.getConstraintViolations();
        assertViolationsCount(1);
        assertHasViolation("allocations");
    }

    @Test
    public void testValidateUpdateTagAuctionSettings() {
        TagAuctionSettings auctionSettings = new TagAuctionSettings();
        auctionSettings.setMaxEcpmShare(BigDecimal.valueOf(80));
        auctionSettings.setPropProbabilityShare(BigDecimal.valueOf(10));
        auctionSettings.setRandomShare(BigDecimal.valueOf(10));

        ValidationContext context = createContext();
        auctionSettingsValidations.validateUpdate(context, auctionSettings);
        violations = context.getConstraintViolations();
        assertViolationsCount(0);

        auctionSettings.setRandomShare(BigDecimal.valueOf(11));

        context = createContext();
        auctionSettingsValidations.validateUpdate(context, auctionSettings);
        violations = context.getConstraintViolations();
        assertViolationsCount(1);
        assertHasViolation("allocations");

        auctionSettings.setRandomShare(null);

        context = createContext();
        auctionSettingsValidations.validateUpdate(context, auctionSettings);
        violations = context.getConstraintViolations();
        assertViolationsCount(1);
        assertHasViolation("allocations");
    }
}

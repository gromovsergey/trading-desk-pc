package com.foros.session.admin.accountType;

import static com.foros.session.admin.accountType.AccountTypeHelper.getNonTextCreativeSizes;
import static com.foros.session.admin.accountType.AccountTypeHelper.getNonTextCreativeTemplates;

import com.foros.model.creative.CreativeSize;
import com.foros.model.security.AccountType;
import com.foros.model.template.Template;
import com.foros.model.time.TimeSpan;
import com.foros.security.AccountRole;
import com.foros.session.BaseValidations;
import com.foros.session.admin.walledGarden.IllegalWalledGardenAgencyTypeException;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

@LocalBean
@Stateless
@Validations
public class AccountTypeValidations {

    @EJB
    private AccountTypeService service;

    @EJB
    private BaseValidations baseValidations;

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) AccountType accountType) {
        validateCreativeSizes(context, accountType, null);
        validateCreativeTemplates(context, accountType, null);
        validateDiscoverTemplates(context, accountType, null);
        validateKeywordAndUrlLimits(context, accountType);
        validateFlags(context, accountType, accountType.getAccountRole());
        validateIoManagement(context, accountType, accountType.getAccountRole());
        validateDeviceTargeting(context, accountType);
        validateCheckIntervals(context, accountType);
    }

    private void validateCheckIntervals(ValidationContext context, AccountType accountType) {


        if (accountType.isChannelCheck()) {
            TimeSpan chFC = accountType.getChannelFirstCheck();
            TimeSpan chSC = accountType.getChannelSecondCheck();
            TimeSpan chTC = accountType.getChannelThirdCheck();

            Map<String, TimeSpan> channelMap = new LinkedHashMap<String, TimeSpan>();
            channelMap.put("channelFirstCheck", chFC);
            channelMap.put("channelSecondCheck", chSC);
            channelMap.put("channelThirdCheck", chTC);

            validateIntervals(context, channelMap, "channel");
        }


        if (accountType.isCampaignCheck()) {

            TimeSpan cmFC = accountType.getCampaignFirstCheck();
            TimeSpan cmSC = accountType.getCampaignSecondCheck();
            TimeSpan cmTC = accountType.getCampaignThirdCheck();

            Map<String, TimeSpan> campMap = new LinkedHashMap<String, TimeSpan>();
            campMap.put("campaignFirstCheck", cmFC);
            campMap.put("campaignSecondCheck", cmSC);
            campMap.put("campaignThirdCheck", cmTC);

            validateIntervals(context, campMap, "campaign");

        }
    }

    private void validateIntervals(ValidationContext context, Map<String, TimeSpan> values, String prefix) {

        String prevKey = null;

        for (String key : values.keySet()) {
            TimeSpan value = values.get(key);
            if (value == null || value.getValueInSeconds() == null) {
                context.addConstraintViolation("errors.field.required").withPath(key);
            } else if (value.getValueInSeconds().longValue() > 31 * 24 * 3600) {
                context.addConstraintViolation("AccountType.error." + prefix + "CheckRange").withPath(key);

            } else if (value.getValueInSeconds().longValue() <= 0) {
                context.addConstraintViolation("errors.field.less").withParameters("0").withPath(key);
            } else if (prevKey != null) {
                TimeSpan prevValue = values.get(prevKey);
                if (prevValue != null && prevValue.getValueInSeconds() != null
                        && value.getValueInSeconds().compareTo(prevValue.getValueInSeconds()) <= 0) {
                    context.addConstraintViolation("AccountType.error." + key).withPath(key);
                }
            }
            prevKey = key;
        }

    }

    private void validateIoManagement(ValidationContext context, AccountType accountType, AccountRole accountRole) {
        if ((AccountRole.AGENCY.equals(accountRole) || AccountRole.ADVERTISER.equals(accountRole))
                && accountType.getIoManagement() == null) {
            context
                .addConstraintViolation("errors.field.required")
                .withPath("ioManagement");
        }
    }

    private void validateKeywordAndUrlLimits(ValidationContext context, AccountType accountType) {
        AccountRole role = accountType.getAccountRole();
        if (role == AccountRole.ADVERTISER || role == AccountRole.AGENCY || role == AccountRole.CMP) {
            if (accountType.getMaxKeywordLength() == null) {
                context.addConstraintViolation("errors.field.required")
                .withPath("maxKeywordLength");
            }
            if (accountType.getMaxUrlLength() == null) {
                context.addConstraintViolation("errors.field.required")
                .withPath("maxUrlLength");
            }
            if ((role == AccountRole.ADVERTISER || role == AccountRole.AGENCY)
                    && accountType.getMaxKeywordsPerGroup() == null) {
                context.addConstraintViolation("errors.field.required")
                .withPath("maxKeywordsPerGroup");

            }
            if (accountType.getMaxKeywordsPerChannel() == null) {
                context.addConstraintViolation("errors.field.required")
                .withPath("maxKeywordsPerChannel");
            }
            if (accountType.getMaxUrlsPerChannel() == null) {
                context.addConstraintViolation("errors.field.required")
                .withPath("maxUrlsPerChannel");
            }
        } else {
            accountType.setMaxKeywordLength(null);
            accountType.setMaxUrlLength(null);
            accountType.setMaxKeywordsPerGroup(null);
            accountType.setMaxKeywordsPerChannel(null);
            accountType.setMaxUrlsPerChannel(null);
        }
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) AccountType accountType) {
        AccountType existingAccountType = service.view(accountType.getId());
        if (accountType.getAccountRole() != existingAccountType.getAccountRole()) {
            throw new RuntimeException("Account Role can not be changed");
        }
        baseValidations.validateVersion(context, accountType, existingAccountType);
        validateCreativeSizes(context, accountType, existingAccountType);
        validateCreativeTemplates(context, accountType, existingAccountType);
        validateDiscoverTemplates(context, accountType, existingAccountType);
        validateKeywordAndUrlLimits(context, accountType);
        validateFlags(context, accountType, existingAccountType.getAccountRole());
        validateIoManagement(context, accountType, accountType.getAccountRole());
        checkFieldChanges(context, accountType, existingAccountType);
        validateDeviceTargeting(context, accountType);
        validateCheckIntervals(context, accountType);
    }

    private void validateDeviceTargeting(ValidationContext context, AccountType accountType) {
        if (AccountRole.AGENCY.equals(accountType.getAccountRole()) || AccountRole.ADVERTISER.equals(accountType.getAccountRole())) {
            if (accountType.getDeviceChannels().size() == 0) {
                context.addConstraintViolation("AccountType.deviceTargeting.error.atLeastOneChannelNeeded")
                .withPath("deviceTargetingOptions");
                return;
            }
        }
    }

    private void validateCreativeSizes(ValidationContext context, AccountType accountType, AccountType existingAccountType) {
        boolean isAdvertiser = AccountRole.ADVERTISER.equals(accountType.getAccountRole());
        boolean isPublisher = AccountRole.PUBLISHER.equals(accountType.getAccountRole());
        boolean isAgency = AccountRole.AGENCY.equals(accountType.getAccountRole());

        if (isAdvertiser || isPublisher || isAgency) {
            Set<CreativeSize> nonTextSizes = getNonTextCreativeSizes(accountType);

            if (nonTextSizes == null || nonTextSizes.isEmpty()) {
                context
                        .addConstraintViolation("errors.field.required")
                        .withPath("AccountType.sizes.notAvailable");
                return;
            }
        }
    }

    private void validateCreativeTemplates(ValidationContext context, AccountType accountType, AccountType existingAccountType) {
        boolean isAdvertiser = AccountRole.ADVERTISER.equals(accountType.getAccountRole());
        boolean isAgency = AccountRole.AGENCY.equals(accountType.getAccountRole());

        if (isAdvertiser || isAgency) {

            if (!accountType.isAllowDisplayAdvertisingFlag() && !accountType.isAllowTextAdvertisingFlag()) {
                context
                        .addConstraintViolation("errors.field.required")
                        .withPath("AccountType.rateType.notAvailable");
            }

            Set<Template> templates = getNonTextCreativeTemplates(accountType);
            if (templates == null || templates.isEmpty()) {
                context
                        .addConstraintViolation("errors.field.required")
                        .withPath("AccountType.templates.notAvailable");
                return;
            }
        }
    }

    private void validateDiscoverTemplates(ValidationContext context, AccountType accountType, AccountType existingAccountType) {
        if (!accountType.isWdTagsFlag()) {
            return;
        }

        Set<Template> templates = accountType.getTemplates();

        if (templates == null || templates.isEmpty()) {
            context
                    .addConstraintViolation("errors.field.required")
                    .withPath("AccountType.discoverTemplates.notAvailable");
            return;
        }
    }

    private void checkFieldChanges(ValidationContext context, AccountType accountType, AccountType existingAccountType) {
        try {
            service.validateFieldChanges(existingAccountType, accountType);
        } catch (IllegalAccountTypeChangeException e) {
            for (String field : e.getFields()) {
                context
                    .addConstraintViolation("AccountType.invalid.field")
                    .withParameters("{"+field+"}");
            }
        } catch(IllegalWalledGardenAgencyTypeException ex) {
            context
                    .addConstraintViolation("WalledGarden.validation.agency.accounttype")
                    .withPath("walledGardenAgencyType");

        }

    }

    private void validateFlags(ValidationContext context, AccountType accountType, AccountRole role) {
        if (AccountRole.PUBLISHER.equals(role)) {
            if (accountType.getShowIframeTag() == null) {
                context
                        .addConstraintViolation("errors.field.required")
                        .withPath("AccountType.showIframeTag");
            }
            if (accountType.getShowBrowserPassbackTag() == null) {
                context
                        .addConstraintViolation("errors.field.required")
                        .withPath("AccountType.showBrowserPassbackTag");
            }
        }
    }
}

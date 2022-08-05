package com.foros.session.opportunity;

import static com.foros.config.ConfigParameters.DEFAULT_MAX_UPLOAD_SIZE;
import static com.foros.util.StringUtil.addComma;
import static com.foros.util.StringUtil.isPropertyEmpty;
import com.foros.config.ConfigService;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CampaignAllocation;
import com.foros.model.campaign.CampaignAllocationSumTO;
import com.foros.model.opportunity.Opportunity;
import com.foros.model.opportunity.Probability;
import com.foros.session.fileman.BadFileNameException;
import com.foros.session.fileman.FileUtils;
import com.foros.session.fileman.restrictions.FileNameRestriction;
import com.foros.session.fileman.restrictions.FileNameRestrictionImpl;
import com.foros.util.NumberUtil;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.RangeValidator;
import com.foros.validation.constraint.violation.ConstraintViolation;
import com.foros.validation.strategy.ValidationMode;

import java.io.File;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
public class OpportunityValidations {

    private FileNameRestriction fileNameRestriction = FileNameRestrictionImpl.INSTANCE;
    
    private static final String ALLOWED_FILE_TYPES = "pdf,png,jpg,gif";

    @EJB
    private OpportunityService opportunityService;

    @EJB
    private ConfigService configService;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;


    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) Opportunity opportunity, Map<String, File> ioFiles) {
        validateAmount(context, opportunity, null);
        validateProbability(context, opportunity, null);
        if (opportunity.getProbability() != null && OpportunityHelper.canCreateIOFile(opportunity)) {
            validateIOFiles(context, opportunity, ioFiles);
        }
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) Opportunity opportunity, Map<String, File> ioFiles) {
        Opportunity existing = opportunityService.find(opportunity.getId());
        validateAmount(context, opportunity, existing);
        validateProbability(context, opportunity, existing);
        if (opportunity.getProbability() == Probability.IO_SIGNED) {
            validateIOFiles(context, opportunity, ioFiles);
        }
    }

    private void validateProbability(ValidationContext context, Opportunity opportunity, Opportunity existing) {
        if (hasProbabilityViolations(context, opportunity)) {
            return;
        }

        Long accountId;
        if (existing == null) {
            accountId = opportunity.getAccount().getId();
        } else {
            accountId = existing.getAccount().getId();
        }

        Collection<Probability> availableProbabilities = opportunityService.getAvailableProbabilities(accountId, opportunity.getProbability());

        if (!availableProbabilities.contains(opportunity.getProbability())) {
            context.addConstraintViolation("opportunity.notPermitted.probability")
                    .withPath("probability");
        }
    }

    private boolean hasProbabilityViolations(ValidationContext context, Opportunity opportunity) {
        if (context.isReachable("probability")
                && opportunity.getProbability() != null
                && !hasViolation("probability", context.getConstraintViolations())) {
            return false;
        }
        return true;
    }

    private void validateIOFiles(ValidationContext context, Opportunity opportunity, Map<String, File> ioFiles) {
        if (!hasProbabilityViolations(context, opportunity)) {
            if (isPropertyEmpty(opportunity.getIoNumber())) {
                context.addConstraintViolation("errors.field.required")
                        .withPath("ioNumber");
            }
            if (ioFiles.isEmpty()) {
                context.addConstraintViolation("errors.field.required")
                        .withPath("ioFiles");
            } else if (!ioFiles.isEmpty()) {
                validateFiles(context, ioFiles);
            }
        }
    }

    private void validateFiles(ValidationContext context, Map<String, File> ioFiles) {
        StringBuilder invalidFileNames = new StringBuilder();
        StringBuilder invalidFileSizes = new StringBuilder();
        StringBuilder invalidExtensions = new StringBuilder();

        boolean isDuplicate = false;
        Set<String> fileNames = new HashSet<String>();
        
        List<String> allowedFileTypes = FileUtils.fileTypesToMimeTypes(Arrays.asList(StringUtil.splitByComma(ALLOWED_FILE_TYPES)));

        for (Map.Entry<String, File> file : ioFiles.entrySet()) {
            try {
                fileNameRestriction.checkFileName("", file.getKey(), false);
            } catch (BadFileNameException e) {
                invalidFileNames.append(addComma(true, file.getKey()));
            }

            if (!FileUtils.isAllowedFileExtension(file.getKey(), allowedFileTypes)) {
                invalidExtensions.append(addComma(true, file.getKey()));
            }

            if (file.getValue().length() > configService.get(DEFAULT_MAX_UPLOAD_SIZE)) {
                invalidFileSizes.append(addComma(true, file.getKey()));
            }

            if (!isDuplicate && !fileNames.add(file.getKey())) {
                isDuplicate = true;
            }
        }

        if (invalidFileNames.length() > 0) {
            context.addConstraintViolation("opportunity.invalid.filename")
                    .withParameters(invalidFileNames.substring(1));
        }
        if (invalidFileSizes.length() > 0) {
            context.addConstraintViolation("opportunity.notPermitted.filesize")
                    .withParameters(invalidFileSizes.substring(1));
        }
        if (invalidExtensions.length() > 0) {
            context.addConstraintViolation("opportunity.notPermitted.fileExtensions")
                    .withParameters(invalidExtensions.substring(1));
        }
        if (isDuplicate) {
            context.addConstraintViolation("opportunity.duplicate.ioFiles");
        }
    }

    private boolean hasViolation(String path, Set<ConstraintViolation> violations) {
        for (ConstraintViolation violation : violations) {
            if (ObjectUtils.equals(path, violation.getPropertyPath().toString())) {
                return true;
            }
        }
        return false;
    }

    /** https://confluence.ocslab.com/display/TDOC/Opportunities+Edit+Screen */
    private void validateAmount(ValidationContext context, Opportunity opportunity, Opportunity existing) {
        if (!context.isReachable("amount")) {
            return;
        }

        AdvertiserAccount account = em.getReference(AdvertiserAccount.class, opportunity.getAccount().getId());

        // Decimal precision defined by account currency.
        Integer maxFractionDigits = null;
        if (account != null) {
            maxFractionDigits = account.getCurrency().getFractionDigits();
        }

        if (maxFractionDigits == null) {
            context.setValidationIncomplete();
            return;
        }

        BigDecimal minAmount = BigDecimal.ZERO;

        if (existing != null && (opportunity.getProbability() == Probability.AWAITING_GO_LIVE || opportunity.getProbability() == Probability.LIVE)) {
            // utilized amount taken in account regardless current IO Management flag
            minAmount = getMinAmount(opportunity);
        }

        if (minAmount.compareTo(BigDecimal.ZERO) == 0) {
            minAmount = NumberUtil.addFraction(minAmount, maxFractionDigits);
        }

        context.validator(FractionDigitsValidator.class)
                .withPath("amount")
                .withFraction(maxFractionDigits)
                .validate(opportunity.getAmount());

        context.validator(RangeValidator.class)
                .withPath("amount")
                .withMin(minAmount)
                .withMax(Opportunity.AMOUNT_MAX, maxFractionDigits)
                .validate(opportunity.getAmount());
    }

    private BigDecimal getMinAmount(Opportunity opportunity) {
        // I/O management = Enabled:
        // Amount >= max(Spent Amount, max for each campaign(value allocated by campaign from this opportunity) )
        List<CampaignAllocation> tos = opportunityService.getCampaignAllocations(opportunity.getId());

        BigDecimal spentAmount = BigDecimal.ZERO;
        for (CampaignAllocation allocation : tos) {
            spentAmount = spentAmount.add(allocation.getUtilizedAmount());
        }

        BigDecimal maxAllocatedAmount = BigDecimal.ZERO;
        for (CampaignAllocation to : tos) {
            maxAllocatedAmount = maxAllocatedAmount.max(to.getAmount());
        }

        return spentAmount.max(maxAllocatedAmount);
    }
}

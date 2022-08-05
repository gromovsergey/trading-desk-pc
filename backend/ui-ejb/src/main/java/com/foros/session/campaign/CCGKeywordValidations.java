package com.foros.session.campaign;

import com.foros.model.Status;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.model.channel.KeywordTriggerType;
import com.foros.session.BeanValidations;
import com.foros.session.LoggingJdbcTemplate;
import com.foros.session.UploadStatus;
import com.foros.session.bulk.IdNameTO;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsValidations;
import com.foros.session.campaign.bulk.BulkUtil;
import com.foros.session.channel.TriggerListValidationRules;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.campaign.CCGKeywordQueryImpl;
import com.foros.util.MultiKey;
import com.foros.util.StringUtil;
import com.foros.util.TriggerUtil;
import com.foros.util.UploadUtils;
import com.foros.util.bean.Filter;
import com.foros.util.unixcommons.TriggerNormalization;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.RangeValidator;
import com.foros.validation.util.DuplicateChecker;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang.ObjectUtils;

@LocalBean
@Stateless
@Validations
public class CCGKeywordValidations {
    private static final Pattern SEARCH_SQUARE_BRACKETS_PATTERN = Pattern.compile("^-?\\[[^\\[\\]]+\\]$|^[^\\[\\]]+$");

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private BeansValidationService beanValidationService;

    @EJB
    private QueryExecutorService executorService;

    @EJB
    private OperationsValidations operationsValidations;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private BeanValidations beanValidations;

    @EJB
    private LoggingJdbcTemplate jdbcTemplate;

    @Validation
    public void validateCount(ValidationContext validationContext, CampaignCreativeGroup ccg, Collection<CCGKeyword> ccgKeywords, Collection<CCGKeyword> existingKeywords) {
        int keywordsCount = 0;
        for (CCGKeyword ccgKeyword : ccgKeywords) {
            if (UploadUtils.getUploadContext(ccgKeyword).getStatus() == UploadStatus.NEW) {
                keywordsCount++;
            }
        }

        ValidationContext context = validationContext.createSubContext(ccg);

        for (CCGKeyword ccgKeyword : existingKeywords) {
            if (ccgKeyword.getStatus() != Status.DELETED) {
                keywordsCount++;
            }
        }

        TriggerListValidationRules rules = new TriggerListValidationRules(BulkUtil.findAccount(em, ccg));
        if (!rules.isValidMaxKeywordsPerGroup(keywordsCount)) {
            context.addConstraintViolation("errors.ccgKeywordsCount")
                .withParameters(rules.getMaxKeywordsPerGroup())
                .withPath("ccgKeywords");
        }
    }

    @Validation
    public void validateUndelete(ValidationContext validationContext, Collection<Long> ids, Long ccgId) {
        String query = "select count(ccg_keyword_id) from ccgkeyword " +
                " where (status <> 'D' or (status = 'D' and  ccg_keyword_id = any(?))) and ccg_id = ?";

        Integer keywordsCount = jdbcTemplate.queryForObject(
                query,
                Integer.class,
                jdbcTemplate.createArray("int", ids),
                ccgId
        );

        CampaignCreativeGroup group = em.find(CampaignCreativeGroup.class, ccgId);

        ValidationContext context = validationContext.createSubContext(group);

        TriggerListValidationRules rules = new TriggerListValidationRules(group.getAccount());
        if (!rules.isValidMaxKeywordsPerGroup(keywordsCount)) {
            context.addConstraintViolation("errors.ccgKeywordsCount")
                    .withParameters(rules.getMaxKeywordsPerGroup())
                    .withPath("ccgKeywords");
        }
    }

    @Validation
    public void validateCreateOrUpdateAll(ValidationContext validationContext, Collection<CCGKeyword> ccgKeywords, Long ccgId) {
        CampaignCreativeGroup ccg = em.find(CampaignCreativeGroup.class, ccgId);

        ValidationContext context = validationContext.createSubContext(ccgKeywords);

        for (CCGKeyword keyword : ccgKeywords) {
            keyword.setCreativeGroup(ccg);
        }

        HashMap<String, CCGKeyword> existingByKeyword = new HashMap<>(ccg.getCcgKeywords().size());
        for (CCGKeyword keyword : ccg.getCcgKeywords()) {
            existingByKeyword.put(keyword.getOriginalKeyword(), keyword);
        }

        TriggerListValidationRules rules = new TriggerListValidationRules(ccg.getAccount());
        if (!rules.isValidMaxKeywordsPerGroup(ccgKeywords.size())) {
            context.addConstraintViolation("errors.ccgKeywordsCount")
                .withParameters(rules.getMaxKeywordsPerGroup())
                .withPath("ccgKeywords");
        }

        DuplicateChecker<CCGKeyword> checker = DuplicateChecker.create(CCGKeyword.IDENTIFIER_FETCHER, new Filter<CCGKeyword>() {
            @Override
            public boolean accept(CCGKeyword element) {
                return element != null && StringUtil.isPropertyNotEmpty(element.getOriginalKeyword())
                        && element.getTriggerType() != null;
            }
        });

        int index=0;
        for (CCGKeyword keyword : ccgKeywords) {
            CCGKeyword existing = existingByKeyword.get(keyword.getOriginalKeyword());

            OperationType operationType = existing == null ? OperationType.CREATE : OperationType.UPDATE;

            ValidationContext subContext = context
                    .subContext(keyword)
                    .withPath("ccgKeywords")
                    .withIndex(index++)
                    .withMode(operationType.toValidationMode())
                    .build();

            if (!checker.check(keyword)){
                subContext
                        .addConstraintViolation("errors.keyword.duplicate")
                        .withParameters(keyword.getOriginalKeyword(), keyword.getTriggerType().getName())
                        .withPath("originalKeyword");
            }

            validate(subContext, operationType, keyword, existing, ccg);
        }
    }

    @Validation
    public void validateMerge(ValidationContext context, Operations<CCGKeyword> operations) {
        int i = 0;
        for (Operation<CCGKeyword> operation : operations.getOperations()) {
            ValidationContext operationContext = context.createSubContext(operation, "operations", i++);

            operationsValidations.validateOperation(operationContext, operation, "ccgKeyword");
            if (operationContext.hasViolations()) {
                continue;
            }

            CCGKeyword keyword = operation.getEntity();

            OperationType operationType = operation.getOperationType();

            ValidationContext keywordContext = operationContext
                    .subContext(keyword)
                    .withPath("ccgKeyword")
                    .withMode(operationType.toValidationMode())
                    .build();

            if (!validateIds(keywordContext, keyword, operationType)) {
                continue;
            }

            if (!canMerge(keywordContext, keyword, operationType)) {
                continue;
            }

            CCGKeyword existing;
            CampaignCreativeGroup ccg;
            switch (operationType) {
                case CREATE:
                    existing = null;
                    ccg = em.find(CampaignCreativeGroup.class, keyword.getCreativeGroup().getId());
                    ccg.getCcgKeywords().add(keyword);
                    break;
                case UPDATE:
                    existing = em.find(CCGKeyword.class, keyword.getId());
                    ccg = existing.getCreativeGroup();
                    Set<CCGKeyword> ccgKeywords = ccg.getCcgKeywords();
                    ccgKeywords.remove(existing);
                    ccgKeywords.add(keyword);
                    break;
                default:
                    throw new RuntimeException();
            }

            TriggerListValidationRules rules = new TriggerListValidationRules(ccg.getAccount());
            if (!rules.isValidMaxKeywordsPerGroup(ccg.getCcgKeywords())) {
                keywordContext.addConstraintViolation("errors.ccgKeywordsCount")
                        .withParameters(rules.getMaxKeywordsPerGroup());
            } else {
                validate(keywordContext, operationType, keyword, existing, ccg);

                if (operationType == OperationType.UPDATE) {
                    validateVersion(keywordContext, keyword, existing);
                }
            }
        }

        DuplicateChecker.<CCGKeyword>createOperationDuplicateChecker()
                .check(operations.getOperations())
                .createConstraintViolations(context, "operations[{0}].ccgKeyword", "id");

        DuplicateChecker.create(new OperationKeywordNameFetcher(), new NameUniquenessFilter())
                .check(operations.getOperations())
                .createConstraintViolations(context, "operations[{0}].ccgKeyword", "originalKeyword", "errors.ccgkeyword.duplicate");

    }

    private boolean canMerge(ValidationContext context, CCGKeyword keyword, OperationType operationType) {
        ValidationContext subContext = context.createSubContext();
        advertiserEntityRestrictions.canMerge(subContext, keyword, operationType);
        return subContext.ok();
    }

    private void validateVersion(ValidationContext context, CCGKeyword keyword, CCGKeyword existing) {
        if (context.isReachable("version") && !ObjectUtils.equals(keyword.getVersion(), existing.getVersion())) {
            context.addConstraintViolation("errors.version")
                .withValue(keyword.getVersion())
                .withPath("version");
        }
    }

    private boolean validateIds(ValidationContext keywordContext, CCGKeyword keyword, OperationType operationType) {
        switch (operationType) {
            case CREATE:
                beanValidations.linkValidator(keywordContext, CampaignCreativeGroup.class)
                    .withPath("creativeGroup")
                    .validate(keyword.getCreativeGroup());
                break;
            case UPDATE:
                beanValidations.linkValidator(keywordContext, CCGKeyword.class)
                    .validate(keyword);
                break;
        }

        return !keywordContext.hasViolations();
    }

    @Validation
    public void validateCreateOrUpdate(ValidationContext validationContext, CCGKeyword keyword, CampaignCreativeGroup ccg, TGTType tgtType) {
        ValidationContext context = validationContext.createSubContext(keyword);

        CCGKeyword existing = keyword.getId() == null ? null : em.find(CCGKeyword.class, keyword.getId());
        OperationType operationType = existing == null ? OperationType.CREATE : OperationType.UPDATE;
        validate(context
                .subContext(keyword)
                .withMode(operationType.toValidationMode())
                .build(), operationType, keyword, existing, ccg);

        if (!tgtType.equals(ccg.getTgtType())) {
            context
            .addConstraintViolation("campaign.csv.errors.tgtType." + tgtType.name());
        }


    }

    @Validation
    public void validateKeywordConstraintViolations(ValidationContext context, Operations<CCGKeyword> operations) {
        List<CCGKeyword> toBeCreated = new LinkedList<CCGKeyword>();
        for (Operation<CCGKeyword> operation : operations.getOperations()) {
            if (operation.getOperationType() == OperationType.CREATE) {
                toBeCreated.add(operation.getEntity());
            }
        }

        List<IdNameTO> duplicated = new CCGKeywordQueryImpl()
                .existingByKeyword(toBeCreated)
                .asNamedTO("creativeGroup.id", "originalKeyword")
                .executor(executorService)
                .list();

        HashSet<IdNameTO> duplicatedSet = new HashSet<IdNameTO>(duplicated);

        int i = 0;
        for (Operation<CCGKeyword> operation : operations.getOperations()) {
            CCGKeyword keyword = operation.getEntity();
            String originalKeyword = keyword.getOriginalKeyword();
            if (duplicatedSet.contains(new IdNameTO(keyword.getCreativeGroup().getId(), keyword.getOriginalKeyword()))) {
                String msg = StringUtil.getLocalizedString("ccgKeyword.originalKeyword");
                context
                    .createSubContext(operation, "operations", i++)
                    .createSubContext(keyword, "ccgKeyword")
                    .addConstraintViolation("errors.duplicate")
                    .withParameters(msg + " " + originalKeyword)
                    .withPath("originalKeyword")
                    .withValue(originalKeyword);
            }
        }
    }


    private void validateTGTType(ValidationContext context, CampaignCreativeGroup ccg) {
        if (ccg != null && TGTType.KEYWORD != ccg.getTgtType()) {
            context.addConstraintViolation("ccg.keyword.onlyKeywordTargetedCCG");
        }
    }

    private void validate(ValidationContext context, OperationType operationType, CCGKeyword keyword, CCGKeyword existing, CampaignCreativeGroup ccg) {

        beanValidationService.validate(context);

        if (context.isReachable("originalKeyword")) {
            validateOriginalKeyword(context, keyword, existing, ccg);
        }

        if (context.isReachable("maxCpcBid")) {
            validateCpcBid(context, keyword);
        }

        // negative keywords can't have Bid or Click URL
        BigDecimal maxCpcBid = context.isReachable("maxCpcBid") || existing == null ? keyword.getMaxCpcBid() : existing.getMaxCpcBid();
        String clickURL = context.isReachable("clickURL") || existing == null ? keyword.getClickURL() : existing.getClickURL();
        String originalKeyword = context.isReachable("originalKeyword") || existing == null ? keyword.getOriginalKeyword() : existing.getOriginalKeyword();
        boolean negative = context.isReachable("originalKeyword") || existing == null ? keyword.isNegative() : existing.isNegative();

        if (negative && (maxCpcBid != null || clickURL != null)) {
            context
                    .addConstraintViolation("errors.negative.keywords.CPCorURL")
                    .withParameters(originalKeyword)
                    .withValue(originalKeyword)
                    .withPath("originalKeyword");
        }

        if (operationType == OperationType.CREATE) {
            validateTGTType(context, ccg);
        }
    }

    private void validateCpcBid(ValidationContext context, CCGKeyword keyword) {
        BigDecimal maxCpcBid = keyword.getMaxCpcBid();

        AdvertiserAccount account = BulkUtil.findAccount(em, keyword);
        if (account != null) {
            int fractionDigits = account.getCurrency().getFractionDigits();

            context
                    .validator(RangeValidator.class)
                    .withMin(BigDecimal.ZERO, fractionDigits)
                    .withMax(new BigDecimal("10000000"), fractionDigits)
                    .withPath("maxCpcBid")
                    .validate(maxCpcBid);

            context
                    .validator(FractionDigitsValidator.class)
                    .withPath("maxCpcBid")
                    .withFraction(fractionDigits)
                    .validate(maxCpcBid);
        }
    }

    private void validateOriginalKeyword(ValidationContext context, CCGKeyword keyword, CCGKeyword existing, CampaignCreativeGroup ccg) {
        String original = keyword.getOriginalKeyword();

        if (StringUtil.isPropertyEmpty(original)) {
            return;
        }

        String trimmed = original.trim();
        keyword.setOriginalKeyword(trimmed);

        if (trimmed.contains("\n") || trimmed.contains("**")) {
            addKeywordIsInvalid(context, original);
        }

        if ("-".equals(trimmed)) {
            context
                    .addConstraintViolation("errors.emptyNegativeKeyword")
                    .withPath("originalKeyword")
                    .withValue(original);
        }

        if (!SEARCH_SQUARE_BRACKETS_PATTERN.matcher(trimmed).matches()) {
            context
                    .addConstraintViolation("errors.keyword.squareBracketsIncorrectPlace")
                    .withParameters(original)
                    .withPath("originalKeyword")
                    .withValue(original);
        }

        if (trimmed.startsWith("[") && keyword.getTriggerType() != KeywordTriggerType.SEARCH_KEYWORD) {
            context
                    .addConstraintViolation("errors.keyword.squareBracketsNotAllowed")
                    .withParameters(original)
                    .withPath("originalKeyword")
                    .withValue(original);
        }

        try {
            if (trimmed.startsWith("-")) {
                trimmed = trimmed.substring(1);
            }
            String normalizedKeyword = TriggerNormalization.normalizeKeyword(ccg.getCountry().getCountryCode(), trimmed);
            if (StringUtil.isPropertyEmpty(normalizedKeyword)) {
                addKeywordIsInvalid(context, original);
            }
        } catch (Exception e) {
            addKeywordIsInvalid(context, original);
        }

        TriggerListValidationRules rules = new TriggerListValidationRules(BulkUtil.findAccount(em, ccg));
        if (StringUtil.getBytesCount(trimmed) > TriggerUtil.MAX_KEYWORD_LENGTH) {
            context.addConstraintViolation("errors.field.invalidMaxLengthExc")
                .withPath("originalKeyword");
        } else if (trimmed.length() > rules.getMaxKeywordLength()) {
            context.addConstraintViolation("errors.field.maxlength")
                .withParameters(rules.getMaxKeywordLength())
                .withPath("originalKeyword");
        }

        if (existing != null && !existing.getOriginalKeyword().equals(keyword.getOriginalKeyword())) {
            context.addConstraintViolation("errors.field.canNotChange")
                .withPath("originalKeyword")
                .withValue(keyword.getOriginalKeyword());
        }
    }

    private void addKeywordIsInvalid(ValidationContext context, String originalKeyword) {
        context
                .addConstraintViolation("errors.invalidKeyword")
                .withPath("originalKeyword")
                .withParameters(originalKeyword)
                .withValue(originalKeyword);
    }

    private static class NameUniquenessFilter implements Filter<Operation<CCGKeyword>> {
        @Override
        public boolean accept(Operation<CCGKeyword> operation) {
            CCGKeyword entity = operation.getEntity();
            return operation.getOperationType() == OperationType.CREATE && entity.getOriginalKeyword() != null;
        }
    }

    private static class OperationKeywordNameFetcher implements DuplicateChecker.IdentifierFetcher<Operation<CCGKeyword>> {
        @Override
        public Object fetch(Operation<CCGKeyword> operation) {
            CCGKeyword keyword = operation.getEntity();
            return new MultiKey(keyword.getCreativeGroup().getId(), keyword.getOriginalKeyword(), keyword.getTriggerType());
        }
    }
}

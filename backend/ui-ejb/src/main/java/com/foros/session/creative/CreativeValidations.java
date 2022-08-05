package com.foros.session.creative;

import com.foros.config.ConfigService;
import com.foros.model.account.AdvertiserAccount;
import com.foros.model.creative.Creative;
import com.foros.model.creative.CreativeCategory;
import com.foros.model.creative.CreativeCategoryType;
import com.foros.model.creative.CreativeOptionValue;
import com.foros.model.creative.CreativeSize;
import com.foros.model.creative.TextCreativeOption;
import com.foros.model.security.AccountType;
import com.foros.model.template.CreativeTemplate;
import com.foros.model.template.Option;
import com.foros.model.template.OptionGroupState;
import com.foros.model.template.OptionGroupType;
import com.foros.model.template.OptionValueUtils;
import com.foros.model.template.Template;
import com.foros.session.BeanValidations;
import com.foros.session.CurrentUserService;
import com.foros.session.UrlValidations;
import com.foros.session.account.AccountService;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsValidations;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.campaign.CampaignCreativeService;
import com.foros.session.fileman.BadNameException;
import com.foros.session.fileman.OnNoProviderRoot;
import com.foros.session.fileman.PathProvider;
import com.foros.session.fileman.PathProviderService;
import com.foros.session.template.OptionGroupStateHelper;
import com.foros.session.template.OptionService;
import com.foros.session.template.OptionValueValidations;
import com.foros.session.template.TemplateService;
import com.foros.util.StringUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.util.DuplicateChecker;
import com.foros.validation.util.EntityIdFetcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
public class CreativeValidations {
    @EJB
    private DisplayCreativeService displayCreativeService;

    @EJB
    private AccountService accountService;

    @EJB
    private TemplateService templateService;

    @EJB
    private CreativeSizeService sizeService;

    @EJB
    private OptionValueValidations optionValueValidations;

    @EJB
    private OperationsValidations operationsValidations;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private CampaignCreativeService campaignCreativeService;

    @EJB
    private PathProviderService pathProviderService;

    @EJB
    private ConfigService config;

    @EJB
    private UrlValidations urlValidations;

    @EJB
    private OptionService optionService;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private BeanValidations beanValidations;


    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    private static final String TAG_NAME_VALIDATION_REGEXP = "^[\\p{L}\\p{M}*\\p{Nd}\\.\\-& ]+$";

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) Creative creative) {
        validateSave(context, creative, null);
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) Creative creative) {
        Creative existing = em.find(Creative.class, creative.getId());
        if (existing == null) {
            context.addConstraintViolation("errors.entity.notFound")
                    .withPath("id");
            return;
        }
        Long creativeAccountId = creative.getAccount() != null ? creative.getAccount().getId() : null;
        if (creativeAccountId != null && !creativeAccountId.equals(existing.getAccount().getId())) {
            context.addConstraintViolation("error.operation.not.permitted");
            return;
        }
        validateSave(context, creative, existing);
        validateVersion(context, creative, existing);
    }

    private void validateVersion(ValidationContext context, Creative creative, Creative existing) {
        if (context.isReachable("version") && !ObjectUtils.equals(creative.getVersion(), existing.getVersion())) {
            context.addConstraintViolation("errors.version")
                .withValue(creative.getVersion())
                .withPath("version");
        }
    }

    @Validation
    public void validateCreateOrUpdate(ValidationContext context, @ValidateBean Creative creative, Long accountId) {
        ValidationContext creativeContext = context.createSubContext(creative);
        AdvertiserAccount account = em.find(AdvertiserAccount.class, accountId);
        for (CreativeOptionValue optionValue : creative.getOptions()) {
            validateTextOption(creativeContext, account, prepareTextOptionId(optionValue), TextCreativeOption.byToken(optionValue.getOption().getToken()));
        }
    }

    private CreativeOptionValue prepareTextOptionId(CreativeOptionValue optionValue) {
        if (optionValue.getOptionId() == null) {
            Option option = optionService.findByTokenFromTextTemplate(optionValue.getOption().getToken());
            if (option != null) {
                optionValue.getOption().setId(option.getId());
            }
        }
        return optionValue;
    }

    private void validateSave(ValidationContext context, Creative creative, Creative existing) {
        AdvertiserAccount advertiserAccount = getAccount(creative, existing);

        AccountType accountType = advertiserAccount.getAccountType();

        validateTemplate(context, creative, existing, accountType);
        validateSize(context, creative, existing, accountType);

        if (!context.props("size", "template").reachableAndNoViolations()) {
            return;
        }

        // check size <-> template compatibility
        if (creative.getSize().isText() != creative.getTemplate().isText()) {
            context.addConstraintViolation("creative.sizeTemplateIncompatible.error");
            return;
        }

        if (creative.isTextCreative()) {
            validateTextCreativeOptions(context, creative, advertiserAccount);
        } else {
            if (creative.isExpandable() && creative.getExpansion() == null) {
                context.addConstraintViolation("errors.field.required").withPath("expansion");
            }

            validateExpandability(context, creative);

            if (context.props("size", "template", "options").reachableAndNoViolations()) {
                validateOptions(context, creative, existing);
            }
        }
        validateCreativeCategories(context, creative);
    }

    private void validateSize(ValidationContext context, Creative creative, Creative existing, AccountType accountType) {
        if (!context.isReachable("size")) {
            return;
        }

        ValidationContext sizeContext = context.createSubContext(creative.getSize(), "size");

        CreativeSize size = beanValidations.linkValidator(sizeContext, CreativeSize.class)
                .withRequired(true)
                .withCheckDeleted(existing == null ? null : existing.getSize())
                .validate(creative.getSize())
                .getEntity();

        if (sizeContext.hasViolations()) {
            return;
        }

        // text creative can't become display and vice versa
        if (existing != null && existing.getSize().isText() != size.isText()) {
            sizeContext.addConstraintViolation("errors.field.invalid")
                    .withValue(creative.getSize());
            return;
        }

        if (!(size.isText() ? accountType.isAllowTextAdvertisingFlag() : accountType.isAllowDisplayAdvertisingFlag() && accountType.getCreativeSizes().contains(size))) {
            sizeContext.addConstraintViolation("creative.wrongSize.error")
                    .withValue(creative.getSize());
            return;
        }

        creative.setSize(size);
    }

    private void validateTemplate(ValidationContext context, Creative creative, Creative existing, AccountType accountType) {
        if (!context.isReachable("template")) {
            return;
        }

        ValidationContext templateContext = context.createSubContext(creative.getTemplate(), "template");

        CreativeTemplate template =beanValidations.linkValidator(templateContext, CreativeTemplate.class)
                .withRequired(true)
                .withCheckDeleted(existing == null ? null : existing.getTemplate())
                .validate(creative.getTemplate())
                .getEntity();

        if (templateContext.hasViolations()) {
            return;
        }

        // text creative can't become display and vice versa
        if (existing != null && existing.getTemplate().isText() != template.isText()) {
            templateContext.addConstraintViolation("errors.field.invalid")
                    .withValue(creative.getTemplate());
            return;
        }

        if (!(template.isText() ? accountType.isAllowTextAdvertisingFlag() : accountType.isAllowDisplayAdvertisingFlag() && accountType.getTemplates().contains(template))) {
            templateContext.addConstraintViolation("creative.wrongTemplate.error")
                    .withValue(creative.getTemplate());
            return;
        }

        creative.setTemplate(template);
    }

    private AdvertiserAccount getAccount(Creative creative, Creative existing) {
        AdvertiserAccount advertiserAccount;
        if (existing == null) {
            advertiserAccount = accountService.findAdvertiserAccount(creative.getAccount().getId());
        } else {
            advertiserAccount = existing.getAccount();
        }
        return advertiserAccount;
    }

    private void validateExpandability(ValidationContext context, Creative creative) {
        if (!creative.isChanged("expansion", "expandable")) {
            return;
        }

        if (!creative.isChanged("expandable")) {
            context.addConstraintViolation("errors.field.required").withPath("expandable");
            return;
        }

        if (creative.isExpandable() && !creative.isChanged("expansion")) {
            context.addConstraintViolation("errors.field.required").withPath("expansion");
            return;
        }

        if (creative.isExpandable() && !creative.getSize().getExpansions().contains(creative.getExpansion())) {
            if (creative.getTemplate().isExpandable()) {
                if (!context.hasViolation("expansion")) {
                    context.addConstraintViolation("creative.wrongExpansion.expandableSize").withPath("expandableSize");
                }
            } else {
                context.addConstraintViolation("creative.wrongExpansion.size").withPath("size");
            }
        }

        if (creative.getExpansion() != null || creative.isExpandable()) {
            if (!creative.getTemplate().isExpandable()) {
                context.addConstraintViolation("creative.wrongExpansion.template").withPath("template");
            } else if (!creative.getSize().isExpandable()) {
                context.addConstraintViolation("creative.wrongExpansion.size").withPath("size");
            }
        }
    }

    private void validateOptions(ValidationContext context, Creative creative, Creative existing) {
        AdvertiserAccount account = getAccount(creative, existing);

        Map<Long, Option> allowedOptions = new HashMap<>();
        for (Option option : creative.getTemplate().getAdvertiserOptions()) {
            allowedOptions.put(option.getId(), option);
        }
        for (Option option : creative.getSize().getAdvertiserOptions()) {
            allowedOptions.put(option.getId(), option);
        }

        Map<Long, OptionGroupState> statesByOptionId = OptionGroupStateHelper.getGroupsStatesByOptionId(
            creative.getGroupStates(), creative.getTemplate(), creative.getSize(), OptionGroupType.Advertiser);

        validateOptions(context, account, creative.getOptions(), allowedOptions, statesByOptionId,
                Collections.<Long, TextCreativeOption>emptyMap());
    }

    private void validateOptions(ValidationContext context, AdvertiserAccount account,
                                 Set<CreativeOptionValue> optionValues,
                                 Map<Long, Option> allowedOptions,
                                 Map<Long, OptionGroupState> statesByOptionId,
                                 Map<Long, TextCreativeOption> textOptions) {

        DuplicateChecker<Option> checker = DuplicateChecker.create(new EntityIdFetcher<Option>());

        int index = 0;
        for (CreativeOptionValue optionValue : optionValues) {

            if (optionValue.getOption() == null || optionValue.getOption().getId() == null) {
                context.addConstraintViolation("creative.option.unresolved")
                        .withPath("options").withParameters(index);
                continue;
            }

            if (!checker.check(optionValue.getOption())) {
                context.addConstraintViolation("creative.option.duplicate")
                        .withPath("options[" + optionValue.getOption().getId() + "].id");
                continue;
            }

            Option option = allowedOptions.remove(optionValue.getOptionId());

            if (option == null) {
                context.addConstraintViolation("creative.option.notFound")
                        .withPath("options[" + optionValue.getOption().getId() + "].value")
                        .withValue(optionValue.getOptionId());
            } else {
                TextCreativeOption textCreativeOption = textOptions.get(optionValue.getOptionId());
                if (textCreativeOption != null) {
                    validateTextOption(context, account, optionValue, textCreativeOption);
                } else {
                    OptionGroupState groupState = statesByOptionId.get(option.getId());
                    if (OptionGroupStateHelper.isGroupEnabled(option.getOptionGroup(), groupState)) {
                        optionValueValidations.validateOptionValue(context, optionValue, account);
                    }
                }
            }
        }

        final boolean externalUser = currentUserService.isExternal();

        for (Option option : allowedOptions.values()) {
            if (!option.isRequired()) {
                continue;
            }
            if (!StringUtil.isPropertyEmpty(option.getDefaultValue())) {
                continue;
            }
            if (externalUser && option.isInternalUse()) {
                continue;
            }
            context.addConstraintViolation("errors.field.required")
                    .withPath("options[" + option.getId() + "].value");
        }
    }

    private void validateCreativeCategories(ValidationContext context, Creative creative) {
        if (!context.isReachable("categories")) {
            return;
        }

        boolean hasVisual = false;
        boolean hasContent = false;
        DuplicateChecker<CreativeCategory> checker = DuplicateChecker.create(new EntityIdFetcher<CreativeCategory>());

        int i = 0;
        for (CreativeCategory category : creative.getCategories()) {
            ValidationContext categoryContext = context
                    .subContext(category)
                    .withPath("categories")
                    .withIndex(i++)
                    .build();

            if (category.getId() == null) {
                // this is a newly added tag
                if (creative.isTextCreative()) {
                    categoryContext.addConstraintViolation("CreativeCategory.errors.contentOnly").withPath("id");
                } else {
                    validateTagName(context, category.getDefaultName());
                }
                continue;
            } else {
                if (!checker.check(category)) {
                    categoryContext.addConstraintViolation("errors.duplicate.id").withPath("id");
                }
            }

            if (category.getType() == CreativeCategoryType.TAG) {
                if (creative.isTextCreative()) {
                    categoryContext.addConstraintViolation("CreativeCategory.errors.contentOnly").withPath("id");
                }
                continue;
            }

            if (category.getId() == null) {
                categoryContext.addConstraintViolation("errors.field.required").withPath("id");
                continue;
            }

            CreativeCategory existing = em.find(CreativeCategory.class, category.getId());

            if (existing == null) {
                categoryContext.addConstraintViolation("errors.entity.notFound");
                continue;
            }

            if (existing.getType()!=CreativeCategoryType.CONTENT && creative.isTextCreative()) {
                categoryContext.addConstraintViolation("CreativeCategory.errors.contentOnly").withPath("id");
            }

            if (existing.getType() == CreativeCategoryType.VISUAL) {
                hasVisual = true;
            }
            if (existing.getType() == CreativeCategoryType.CONTENT) {
                hasContent = true;
            }
        }

        if (!hasVisual && !creative.isTextCreative()) {
            CreativeTemplate template = null;
            if (context.props("template").noViolations()) {
                template = em.find(CreativeTemplate.class, creative.getTemplate().getId());
            }
            if (template == null || template.getCategories().isEmpty()) {
                context.addConstraintViolation("errors.field.required").withPath("visualCategories");
            }
        }

        if (!hasContent && !creative.isTextCreative()) {
            context.addConstraintViolation("errors.field.required").withPath("contentCategories");
        }
    }

    public void validateTagName(ValidationContext context, String tagName) {
        if (!context.hasViolation("selectedTags")) {
            if (StringUtil.isPropertyEmpty(tagName)) {
                context.addConstraintViolation("errors.field.required").withPath("selectedTags");
            } else if (tagName.length() > 100) {
                context.addConstraintViolation("errors.field.maxlength").withParameters(100).withPath("selectedTags");
            } else if (!tagName.matches(TAG_NAME_VALIDATION_REGEXP)) {
                context.addConstraintViolation("errors.field.categoryNameSymbols").withParameters(tagName).withPath("selectedTags");
            }
        }
    }

    @Validation
    public void validateMerge(ValidationContext context, Operations<Creative> operations) {
        DuplicateChecker<Operation<Creative>> duplicateIdChecker = DuplicateChecker.create(new DuplicateChecker.OperationIdFetcher<Creative>());

        int index = 0;

        for (Operation<Creative> mergeOperation : operations.getOperations()) {
            ValidationContext operationContext = context.createSubContext(mergeOperation, "operations", index++);

            if (!validateOperation(operationContext, mergeOperation, "creative")) {
                continue;
            }

            Creative creative = mergeOperation.getEntity();

            OperationType operationType = mergeOperation.getOperationType();

            duplicateIdChecker.check(operationContext, "creative.id", mergeOperation);

            if (operationContext.hasViolations()) {
                continue;
            }

            ValidationContext creativeContext = operationContext
                .subContext(creative)
                .withPath("creative")
                .withMode(operationType.toValidationMode())
                .build();

            if (!validateMerge(creativeContext, creative, operationType)) {
                continue;
            }

            beanValidations.validateContext(creativeContext);

            switch (operationType) {
            case CREATE:
                validateCreate(creativeContext, creative);
                break;
            case UPDATE:
                validateUpdate(creativeContext, creative);
                break;
            }
        }
    }

    private boolean validateOperation(ValidationContext operationContext, Operation<Creative> mergeOperation, String entityPath) {
        operationsValidations.validateOperation(operationContext, mergeOperation, entityPath);
        return !operationContext.hasViolations();
    }

    private boolean validateMerge(ValidationContext context, Creative creative, OperationType operationType) {
        advertiserEntityRestrictions.canMerge(context, creative, operationType);
        return context.ok();
    }

    @Validation
    public void validateIntegrity(ValidationContext context, Operations<?> mergeOperations, String entityPath) {
        int index = 0;
        for (Operation<?> mergeOperation : mergeOperations.getOperations()) {
            ValidationContext operationContext = context.createSubContext(mergeOperation, "operations", index++);
            operationsValidations.validateOperation(operationContext, mergeOperation, entityPath);
            if (!operationContext.ok()) {
                continue;
            }

            validateIntegrity(operationContext, (Creative)mergeOperation.getEntity());
        }
    }

    private void validateIntegrity(ValidationContext operationContext, Creative creative) {
        if (!creative.isChanged("template", "size", "options", "enableAllAvailableSizes", "sizeTypes", "tagSizes")) {
            return;
        }

        List<String> wrongPaths = new ArrayList<>(6);

        Template template = null;
        if (creative.isChanged("template") && creative.getTemplate().getId() != null) {
            template = templateService.findById(creative.getTemplate().getId());
        } else {
            wrongPaths.add("template");
        }

        CreativeSize size = null;
        if (creative.isChanged("size") && creative.getSize().getId() != null) {
            size = sizeService.findById(creative.getSize().getId());
        } else {
            wrongPaths.add("size");
        }

        if (!creative.isChanged("options")) {
            wrongPaths.add("options");
        }

        if (template != null && template.isText() && size != null && size.isText()) {
            boolean sizesTypesRequired;
            if (creative.isChanged("enableAllAvailableSizes")) {
                sizesTypesRequired = !creative.isEnableAllAvailableSizes();
            } else {
                wrongPaths.add("enableAllAvailableSizes");
                sizesTypesRequired = true;
            }

            if (sizesTypesRequired) {
                if (!creative.isChanged("sizeTypes")) {
                    wrongPaths.add("sizeTypes");
                }
                if (!creative.isChanged("tagSizes")) {
                    wrongPaths.add("tagSizes");
                }
            }
        }

        if (wrongPaths.isEmpty()) {
            return;
        }

        ValidationContext creativeContext = operationContext
                .subContext(creative)
                .withPath("creative")
                .build();

        for (String path : wrongPaths) {
            creativeContext.addConstraintViolation("errors.field.required")
                    .withPath(path);
        }
    }

    private void validateTextCreativeOptions(ValidationContext context, Creative creative, AdvertiserAccount account) {
        Set<CreativeSize> usedSizes = campaignCreativeService.getEffectiveTagSizes(creative, account);
        Map<Long, OptionGroupState> statesByOptionId = OptionGroupStateHelper.getGroupsStatesByOptionId(
            creative.getGroupStates(), Collections.<CreativeTemplate> emptyList(), usedSizes, OptionGroupType.Advertiser);

        Map<Long, Option> allowedOptions = new HashMap<>();
        for (CreativeSize size : usedSizes) {
            for (Option option : size.getAdvertiserOptions()) {
                allowedOptions.put(option.getId(), option);
            }
        }

        for (Option option : creative.getSize().getAdvertiserOptions()) {
            allowedOptions.put(option.getId(), option);
        }

        Map<Long, TextCreativeOption> textOptions = new HashMap<>();
        for (Option option : templateService.findTextTemplate().getAdvertiserOptions()) {
            TextCreativeOption textCreativeOption = TextCreativeOption.byTokenOptional(option.getToken());
            if (textCreativeOption != null) {
                textOptions.put(option.getId(), textCreativeOption);
            }
            allowedOptions.put(option.getId(), option);
        }

        validateOptions(context, account, creative.getOptions(), allowedOptions, statesByOptionId, textOptions);
    }

    public void validateTextOption(ValidationContext context, AdvertiserAccount account, CreativeOptionValue optionValue, TextCreativeOption textCreativeOption) {
        if (textCreativeOption == TextCreativeOption.IMAGE_FILE) {
            validateImageFile(context, optionValue.getValue(),
                    "options[" + optionValue.getOptionId() + "].value", account);
            return;
        }

        optionValueValidations.validateOptionValue(context, optionValue, account);
    }

    private void validateImageFile(ValidationContext context, String value, String fieldName, AdvertiserAccount account) {
        if (!StringUtil.isPropertyEmpty(value)) {
            String rootName = OptionValueUtils.getTextAdImagesRoot(config, account);
            PathProvider pathProvider = pathProviderService.getCreatives().getNested(rootName, OnNoProviderRoot.AutoCreate);
            try {
                if (!pathProviderService.createFileSystem(pathProvider).checkExist(value)) {
                    context
                        .addConstraintViolation("errors.fileexist")
                        .withParameters(value)
                        .withPath(fieldName);
                }
            } catch (BadNameException e) {
                context
                    .addConstraintViolation("errors.invalidfile")
                    .withParameters(value)
                    .withPath(fieldName);
            }
        }
    }
}

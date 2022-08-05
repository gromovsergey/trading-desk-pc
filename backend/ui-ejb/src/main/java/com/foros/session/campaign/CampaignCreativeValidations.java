package com.foros.session.campaign;

import com.foros.model.FrequencyCap;
import com.foros.model.Status;
import com.foros.model.campaign.CCGType;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.campaign.TGTType;
import com.foros.model.creative.Creative;
import com.foros.model.security.OwnedStatusable;
import com.foros.session.BeanValidations;
import com.foros.session.CurrentUserService;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsValidations;
import com.foros.session.creative.CreativeValidations;
import com.foros.validation.ValidationContext;
import com.foros.validation.ValidationService;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;
import com.foros.validation.constraint.validator.LinkValidator;
import com.foros.validation.constraint.validator.RangeValidator;
import com.foros.validation.constraint.validator.RequiredValidator;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.util.DuplicateChecker;

import java.math.BigDecimal;
import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.ObjectUtils;

@LocalBean
@Stateless
@Validations
public class CampaignCreativeValidations {

    private static final BigDecimal MAX_WEIGHT = BigDecimal.valueOf(64000l);
    private static final BigDecimal MIN_WEIGHT = BigDecimal.ONE;

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private ValidationService validationService;

    @EJB
    private BeanValidations beanValidations;

    @EJB
    private OperationsValidations operationsValidations;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private CreativeValidations creativeValidations;

    @EJB
    private BeansValidationService beanValidationService;


    @Validation
    public void validateCreate(ValidationContext context,
            @ValidateBean(ValidationMode.CREATE) CampaignCreative campaignCreative) {
        validatesSave(context, campaignCreative, false);
    }

    private void validatesSave(ValidationContext context, CampaignCreative campaignCreative, boolean fromBulk) {
        if (currentUserService.isExternal()) {
            validateForExternal(context, campaignCreative, fromBulk);
        }
        validateFrequencyCap(context, campaignCreative.getFrequencyCap());
    }

    private void validateForExternal(ValidationContext context, CampaignCreative campaignCreative, boolean fromBulk) {
        Creative creative = campaignCreative.getCreative();
        if (!fromBulk) {
            creative = em.find(Creative.class, creative.getId());
        }
        if (creative.isTextCreative()) {
            if (campaignCreative.isChanged("weight")) {
                context
                    .addConstraintViolation("errors.invalidInput")
                    .withPath("weight");
            }
        }

    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) CampaignCreative campaignCreative) {
        CampaignCreative existingCampaignCreative = em.find(CampaignCreative.class, campaignCreative.getId());
        validateFrequencyCap(context, campaignCreative.getFrequencyCap());
        validateVersion(context, campaignCreative, existingCampaignCreative);
    }

    @Validation
    public void validateCreateCreativeWithLinks(ValidationContext context, Creative creative, CampaignCreative campaignCreative) {
        ValidationContext subContext = context
            .subContext(creative)
            .withMode(ValidationMode.CREATE)
            .build();

        beanValidationService.validate(subContext);
        creativeValidations.validateCreate(subContext, creative);

        context.validator(RequiredValidator.class).
            withPath("weight").
            withMessage("errors.field.required").
            validate(campaignCreative.getWeight());
        context.validator(RangeValidator.class)
            .withMin(MIN_WEIGHT)
            .withMax(MAX_WEIGHT)
            .withPath("weight")
            .validate(campaignCreative.getWeight());
        validatesSave(subContext, campaignCreative, true);
    }

    @Validation
    public void validateCreateOrUpdate(ValidationContext validationContext, CampaignCreative campaignCreative, CampaignCreativeGroup group, TGTType tgtType) {
        ValidationContext context = validationContext.createSubContext(campaignCreative);

        if (campaignCreative.getId() != null) {
            CampaignCreative existing = em.find(CampaignCreative.class, campaignCreative.getId());

            // To avoid change creative in campaignCreative see DisplayCreativeServiceBean.createOrUpdateAll
            if (existing != null && campaignCreative.getCreative().getId() == null) {
                campaignCreative.getCreative().setId(existing.getCreative().getId());
            }

            if (existing == null) {
                context
                    .addConstraintViolation("campaign.csv.errors.textAdNotExist")
                    .withPath("id");
                return;
            } else if (!existing.getCreativeGroup().getId().equals(group.getId())) {
                context
                    .addConstraintViolation("campaign.csv.errors.textAd.idGroupInconsistency");
            } else if (!existing.getCreative().getId().equals(campaignCreative.getCreative().getId())) {
                context
                    .addConstraintViolation("campaign.csv.errors.textAd.idCreativeInconsistency");
            } else if (existing.getStatus() == Status.DELETED) {
                context
                    .addConstraintViolation("errors.entity.deleted");
            }
        }

        if (!tgtType.equals(group.getTgtType())) {
            context
                .addConstraintViolation("campaign.csv.errors.tgtType." + tgtType.name());
        }

        validate(context, campaignCreative.getId() == null ? OperationType.CREATE : OperationType.UPDATE, campaignCreative, group);
    }

    private void validate(ValidationContext context, OperationType operationType, CampaignCreative campaignCreative, CampaignCreativeGroup group) {
        ValidationContext subContext = context
            .subContext(campaignCreative)
            .withMode(operationType.toValidationMode())
            .build();

        validateFrequencyCap(subContext, campaignCreative.getFrequencyCap());

        if (operationType == OperationType.CREATE) {
            validateCCGType(subContext, campaignCreative, group);
        }

        CampaignCreative existing = find(campaignCreative);
        if (operationType == OperationType.UPDATE) {
            validateVersion(subContext, campaignCreative, existing);
        }

        StatusValidationUtil.validateStatus(subContext, campaignCreative, existing);

    }

    private void validateCCGType(ValidationContext context, CampaignCreative campaignCreative, CampaignCreativeGroup ccg) {
        if (!CCGType.TEXT.equals(ccg.getCcgType()) && campaignCreative.getCreative().isTextCreative()) {
            context
                .addConstraintViolation("campaign.csv.errors.ccgNotText");
        }
    }

    private CampaignCreative find(CampaignCreative campaignCreative) {
        if (campaignCreative.getId() != null) {
            return em.find(CampaignCreative.class, campaignCreative.getId());
        }
        return null;
    }

    private void validateVersion(ValidationContext context, CampaignCreative campaignCreative, CampaignCreative existing) {
        if (context.isReachable("version") && !ObjectUtils.equals(campaignCreative.getVersion(), existing.getVersion())) {
            context.addConstraintViolation("errors.version")
                .withValue(campaignCreative.getVersion())
                .withPath("version");
        }
    }

    private void validateFrequencyCap(ValidationContext context, FrequencyCap frequencyCap) {
        if (context.isReachable("frequencyCap")) {
            if (frequencyCap != null) {
                context = context.createSubContext(frequencyCap, "frequencyCap");
                validationService.validateWithContext(context, "FrequencyCap.update", frequencyCap);
            }
        }
    }

    @Validation
    public void validateCreateAll(ValidationContext context, Long advertiserId, Collection<Long> creativeIds, Collection<Long> groupIds, final Boolean isDisplay) {
        validateIds(context.createSubContext(creativeIds, "creatives"), advertiserId, creativeIds, Creative.class, new Checker<Creative>() {

            @Override
            boolean isInvalid(Creative entity) {
                return isDisplay ? entity.isTextCreative() : !entity.isTextCreative();
            }
        });
        validateIds(context.createSubContext(groupIds, "groups"), advertiserId, groupIds, CampaignCreativeGroup.class, new Checker<CampaignCreativeGroup>() {

            @Override
            boolean isInvalid(CampaignCreativeGroup entity) {
                return isDisplay ? CCGType.DISPLAY != entity.getCcgType() : CCGType.DISPLAY == entity.getCcgType();
            }
        });
    }

    private static abstract class Checker<T> {
        void check(T entity, ValidationContext context) {
            if (isInvalid(entity)) {
                context.addConstraintViolation("errors.field.invalid");
            }
        }

        abstract boolean isInvalid(T entity);
    }

    private <T extends OwnedStatusable> void validateIds(ValidationContext context, Long advertiserId, Collection<Long> ids, Class<T> clazz, Checker<T> typeChecker) {
        if (ids == null || ids.isEmpty()) {
            context.addConstraintViolation("errors.field.required");
            return;

        }

        int i = 0;
        for (Long id : ids) {
            ValidationContext subContext = context.subContext(id).withIndex(i++).build();
            T entity = em.find(clazz, id);
            if (entity == null) {
                subContext.addConstraintViolation("errors.entity.notFound");
                continue;
            }

            if (!entity.getAccount().getId().equals(advertiserId)) {
                subContext.addConstraintViolation("errors.field.invalid");
                continue;
            }

            typeChecker.check(entity, subContext);

            advertiserEntityRestrictions.canCreate(subContext, entity);
        }
    }

    @Validation
    public void validateMerge(ValidationContext context, Operations<CampaignCreative> operations) {
        DuplicateChecker<Operation<CampaignCreative>> duplicateIdChecker =
                DuplicateChecker.create(new DuplicateChecker.OperationIdFetcher<CampaignCreative>());

        int index = 0;

        for (Operation<CampaignCreative> mergeOperation : operations.getOperations()) {

            ValidationContext operationContext = context.createSubContext(mergeOperation, "operations", index++);

            duplicateIdChecker.check(operationContext, "campaignCreative.id", mergeOperation);

            if (!validateOperation(operationContext, mergeOperation, "campaignCreative")) {
                continue;
            }

            CampaignCreative campaignCreative = mergeOperation.getEntity();

            OperationType operationType = mergeOperation.getOperationType();

            ValidationContext ccContext = operationContext
                .subContext(campaignCreative)
                .withPath("campaignCreative")
                .withMode(operationType.toValidationMode())
                .build();

            if (!validateMerge(ccContext, campaignCreative, operationType)) {
                continue;
            }

            if (!validateConsistency(ccContext, campaignCreative)) {
                continue;
            }

            switch (operationType) {
            case CREATE:
                validateCreate(ccContext, campaignCreative);
                break;
            case UPDATE:
                validateUpdate(ccContext, campaignCreative);
                break;
            }
        }
    }

    private boolean validateOperation(ValidationContext operationContext, Operation<CampaignCreative> mergeOperation, String entityPath) {
        operationsValidations.validateOperation(operationContext, mergeOperation, entityPath);
        return !operationContext.hasViolations();
    }

    private boolean validateMerge(ValidationContext context, CampaignCreative creative, OperationType operationType) {
        advertiserEntityRestrictions.canMerge(context, creative, operationType);
        return context.ok();
    }

    private boolean validateConsistency(ValidationContext context, CampaignCreative campaignCreative) {
        CampaignCreative existing = campaignCreative.getId() == null ? null :
                em.find(CampaignCreative.class, campaignCreative.getId());

        CampaignCreativeGroup ccg = null;
        Creative creative = null;

        if (context.isReachable("creativeGroup")) {
            LinkValidator<CampaignCreativeGroup> ccgValidator =
                    beanValidations.linkValidator(context, CampaignCreativeGroup.class);

            ccgValidator
                    .withRequired(true)
                    .withPath("creativeGroup")
                    .validate(campaignCreative.getCreativeGroup());

            if (!context.hasViolation("creativeGroup")) {
                ccg = ccgValidator.getEntity();
            }
        } else if (existing != null) {
            ccg = existing.getCreativeGroup();
        }

        if (context.isReachable("creative")) {
            LinkValidator<Creative> creativeValidator =
                    beanValidations.linkValidator(context, Creative.class);

            creativeValidator
                    .withRequired(true)
                    .withPath("creative")
                    .validate(campaignCreative.getCreative());

            if (!context.hasViolation("creative")) {
                creative = creativeValidator.getEntity();
            }
        } else if (existing != null) {
            creative = existing.getCreative();
        }

        if (ccg != null && creative != null) {
            if (creative.isTextCreative() && CCGType.DISPLAY == ccg.getCcgType()) {
                context.addConstraintViolation("ccg.error.textCreativeDisplayGroup").withPath("creative");
                return false;
            }
            if (!creative.isTextCreative() && CCGType.TEXT == ccg.getCcgType()) {
                context.addConstraintViolation("ccg.error.displayCreativeTextGroup").withPath("creative");
                return false;
            }
        }

        return true;
    }

}

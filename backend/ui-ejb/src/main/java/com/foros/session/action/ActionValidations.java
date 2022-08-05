package com.foros.session.action;

import com.foros.model.account.AdvertiserAccount;
import com.foros.model.action.Action;
import com.foros.session.bulk.IdNameTO;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsValidations;
import com.foros.session.campaign.AdvertiserEntityRestrictions;
import com.foros.session.campaign.bulk.NameUniquenessFilter;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.action.ActionQueryImpl;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;
import com.foros.validation.constraint.validator.FractionDigitsValidator;
import com.foros.validation.constraint.validator.RangeValidator;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.util.DuplicateChecker;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
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
public class ActionValidations {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;
    @EJB
    private ActionService actionService;

    @EJB
    private AdvertiserEntityRestrictions advertiserEntityRestrictions;

    @EJB
    private OperationsValidations operationsValidations;

    @EJB
    private BeansValidationService beanValidationService;

    @EJB
    private QueryExecutorService executorService;

    @Validation
    public void validateCreate(ValidationContext context, @ValidateBean(ValidationMode.CREATE) Action action) {
        validateValue(context, action);
    }

    @Validation
    public void validateUpdate(ValidationContext context, @ValidateBean(ValidationMode.UPDATE) Action action) {
        validateValue(context, action);
    }

    private void validateValue(ValidationContext context, Action action) {
        if (!context.isReachable("value")) {
            return;
        }
        if (action.getValue() != null) {
            AdvertiserAccount account = em.find(AdvertiserAccount.class, action.getAccount().getId());
            int fractionDigits = account.getCurrency().getFractionDigits();
            context.validator(FractionDigitsValidator.class)
                .withPath("value")
                .withFraction(fractionDigits)
                .validate(action.getValue());

            context.validator(RangeValidator.class)
                .withMin(BigDecimal.ZERO)
                .withMax(Action.VALUE_MAX, fractionDigits)
                .withPath("value")
                .validate(action.getValue());
        }
    }

    private void validate(ValidationContext context, OperationType operationType, Action action, Action existing) {
        beanValidationService.validate(context);

        validateValue(context, action);
        if (operationType == OperationType.UPDATE) {
            validateVersion(context, action, existing);
        }
    }

    private void validateVersion(ValidationContext context, Action action, Action existing) {
        if (context.isReachable("version") && !ObjectUtils.equals(action.getVersion(), existing.getVersion())) {
            context.addConstraintViolation("errors.version")
                .withValue(action.getVersion())
                .withPath("version");
        }
    }

    @Validation
    public void validateMerge(ValidationContext context, Operations<Action> operations) {
        int index = 0;

        NameUniquenessFilter<Action> filter = new NameUniquenessFilter<>();
        DuplicateChecker<Operation<Action>> duplicateIdChecker =
                DuplicateChecker.create(new DuplicateChecker.OperationIdFetcher<Action>());
        DuplicateChecker<Operation<Action>> duplicateNameChecker =
                DuplicateChecker.create(new OperationNameFetcher());

        for (Operation<Action> mergeOperation : operations.getOperations()) {
            ValidationContext operationContext = context.createSubContext(mergeOperation, "operations", index++);


            if (!validateOperation(operationContext, mergeOperation, "conversion")) {
                continue;
            }

            Action action = mergeOperation.getEntity();

            OperationType operationType = mergeOperation.getOperationType();

            ValidationContext actionContext = operationContext
                .subContext(action)
                .withPath("conversion")
                .withMode(operationType.toValidationMode())
                .build();


            if (!validateMerge(actionContext, action, operationType)) {
                filter.ignore(action);
                continue;
            }

            if (action.isChanged("name") && action.getName() != null) {
                duplicateNameChecker.check(actionContext, "name", mergeOperation);
            }
            duplicateIdChecker.check(operationContext, "conversion.id", mergeOperation);

            Action existing = action.getId() == null ? null : actionService.findById(action.getId());
            validate(actionContext, operationType, action, existing);
        }
    }

    private boolean validateOperation(ValidationContext operationContext, Operation<Action> mergeOperation, String entityPath) {
        operationsValidations.validateOperation(operationContext, mergeOperation, entityPath);
        return !operationContext.hasViolations();
    }

    private boolean validateMerge(ValidationContext context, Action action, OperationType operationType) {
        advertiserEntityRestrictions.canMerge(context, action, operationType);
        return context.ok();
    }

    public static class OperationNameFetcher implements DuplicateChecker.IdentifierFetcher<Operation<Action>> {
        @Override
        public Object fetch(Operation<Action> operation) {
            Action action = operation.getEntity();
            return new IdNameTO(action.getAccount().getId(), action.getName());
        }
    }

    @Validation
    public void validateNameConstraintViolations(ValidationContext context, Operations<Action> operations) {
        NameUniquenessFilter<Action> filter = new NameUniquenessFilter<>();

        Set<Action> toBeChecked = new HashSet<>();
        for (Operation<Action> operation : operations.getOperations()) {
            if (filter.accept(operation)) {
                toBeChecked.add(operation.getEntity());
            }
        }

        List<IdNameTO> duplicated = new ActionQueryImpl()
            .existingByName(toBeChecked)
            .asNamedTO("account.id", "name")
            .executor(executorService)
            .list();

        Set<IdNameTO> duplicatedSet = new HashSet<IdNameTO>(duplicated);

        int i = 0;
        for (Operation<Action> operation : operations.getOperations()) {
            Action action = operation.getEntity();
            IdNameTO to = new IdNameTO(action.getAccount().getId(), action.getName());
            if (toBeChecked.contains(action) && duplicatedSet.contains(to)) {
                context
                    .createSubContext(operation, "operations", i++)
                    .createSubContext(action, "conversion")
                    .addConstraintViolation("errors.duplicate")
                    .withPath("name")
                    .withParameters("{Action.entityName}");
            }
        }
    }

}

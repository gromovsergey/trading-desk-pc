package com.foros.session.channel;

import com.foros.model.account.Account;
import com.foros.model.channel.BehavioralParametersList;
import com.foros.model.channel.DiscoverChannel;
import com.foros.session.BaseValidations;
import com.foros.session.BeanValidations;
import com.foros.session.admin.categoryChannel.CategoryChannelValidations;
import com.foros.session.bulk.IdNameTO;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsValidations;
import com.foros.session.campaign.bulk.NameUniquenessFilter;
import com.foros.session.channel.service.DiscoverChannelRestrictions;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.bean.BeansValidationService;
import com.foros.validation.strategy.ValidationMode;
import com.foros.validation.util.DuplicateChecker;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Validations
public class DiscoverChannelValidations extends CommonChannelValidations {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private BeansValidationService beanValidationService;
    
    @EJB
    private BeanValidations beanValidations;

    @EJB
    private OperationsValidations operationsValidations;

    @EJB
    private DiscoverChannelRestrictions discoverChannelRestrictions;

    @EJB
    private BaseTriggerListValidations baseTriggerListValidations;

    @EJB
    private BaseDiscoverChannelValidations baseDiscoverValidations;

    @EJB
    private BaseValidations baseValidations;
    
    @EJB
    private CategoryChannelValidations categoryValidations;

    @EJB
    private LanguageChannelValidations languageChannelValidations;

    @Validation
    public void validateMerge(ValidationContext context, Operations<DiscoverChannel> channelOperations) {
        NameUniquenessFilter<DiscoverChannel> filter = new NameUniquenessFilter<DiscoverChannel>();
        int index = 0;
        for (Operation<DiscoverChannel> mergeOperation : channelOperations.getOperations()) {
            ValidationContext operationContext = context.createSubContext(mergeOperation, "operations", index++);

            operationsValidations.validateOperation(operationContext, mergeOperation, "discoverChannel");
            if (operationContext.hasViolations()) {
                continue;
            }

            DiscoverChannel channel = mergeOperation.getEntity();

            OperationType operationType = mergeOperation.getOperationType();

            ValidationContext channelContext = operationContext
                    .subContext(channel)
                    .withPath("discoverChannel")
                    .withMode(operationType.toValidationMode())
                    .build();

            if (!validateIds(channelContext, channel, operationType)) {
                filter.ignore(channel);
                continue;
            }
            
            if (!discoverChannelRestrictions.canMerge(channel, operationType)) {
                operationContext.addConstraintViolation("errors.forbidden");
                filter.ignore(channel);
                continue;
            }
            
            DiscoverChannel existing;

            switch (operationType) {
                case CREATE:
                    existing = null;
                    break;
                case UPDATE:
                    existing = find(channel);
                    if (existing != null && existing.getChannelList() != null) {
                        context.addConstraintViolation("DiscoverChannel.errors.notLinked").withPath("channelList").withValue(existing.getChannelList());
                        return;
                    }
                    break;
                default:
                    throw new RuntimeException();
            }

            validate(channelContext
                    .subContext(channel)
                    .withMode(operationType.toValidationMode())
                    .build(), channel, existing);
        }

        DuplicateChecker.<DiscoverChannel>createOperationDuplicateChecker()
            .check(channelOperations.getOperations())
            .createConstraintViolations(context, "operations[{0}].discoverChannel", "id");
        
        DuplicateChecker.create(new OperationNameFetcher(), filter)
            .check(channelOperations.getOperations())
            .createConstraintViolations(context, "operations[{0}].discoverChannel", "name");
    }
    

    private DiscoverChannel find(DiscoverChannel channel) {
        return channel.getId() == null ? null : em.find(DiscoverChannel.class, channel.getId());
    }

    private boolean validateIds(ValidationContext groupContext, DiscoverChannel channel, OperationType operationType) {
        switch (operationType) {
            case CREATE:
                beanValidations.linkValidator(groupContext, Account.class)
                    .withPath("account")
                    .withRequired(true)
                    .validate(channel.getAccount());
                break;
            case UPDATE:
                beanValidations.linkValidator(groupContext, DiscoverChannel.class)
                    .validate(channel);
                break;
        }

        return !groupContext.hasViolations();
    }

    @Validation
    public void validateCreate(ValidationContext context, DiscoverChannel channel) {
        validate(context
                .subContext(channel)
                .withMode(ValidationMode.CREATE)
                .build(),
                channel, null);
    }

    @Validation
    public void validateUpdate(ValidationContext context, DiscoverChannel channel) {
        DiscoverChannel existing = find(channel);
        if (existing != null && existing.getChannelList() != null && channel.getChannelList() != null &&
                !existing.getChannelList().getId().equals(channel.getChannelList().getId())) {
            context.addConstraintViolation("DiscoverChannel.errors.notLinked").withPath("channelList").withValue(existing.getChannelList());
            return;
        }
        validate(context
                .subContext(channel)
                .withMode(ValidationMode.UPDATE)
                .build(),
                channel, existing);
    }

    private void validate(ValidationContext context, DiscoverChannel channel, DiscoverChannel existing) {
        validateChannelName(context, channel);
        beanValidationService.validate(context);
        baseDiscoverValidations.validateBehavioralParameters(context, channel);
        baseDiscoverValidations.validateCountry(context, channel);
        languageChannelValidations.validate(context, channel);
        baseTriggerListValidations.validate(context, channel, existing);
        baseValidations.validateVersion(context, channel, existing);
        validateTriggersRequired(context, channel, existing);
        categoryValidations.validateCategories(context, channel);
    }

    public void validateListChild(ValidationContext context, DiscoverChannel channel) {
        validateChannelName(context, channel);
        beanValidationService.validate(context);
        baseTriggerListValidations.validate(context, channel, null);
    }

    public class OperationNameFetcher implements DuplicateChecker.IdentifierFetcher<Operation<DiscoverChannel>> {
        @Override
        public Object fetch(Operation<DiscoverChannel> operation) {
            DiscoverChannel entity = operation.getEntity();
            return new IdNameTO(entity.getAccount().getId(), entity.getName());
        }
    }

    public void validateTriggersRequired(ValidationContext context, DiscoverChannel channel, DiscoverChannel existing) {

        if (!context.props("behavParamsList", "urls", "keywords").reachableAndNoViolations()) {
            return;
        }

        BehavioralParametersList effectiveList = context.isReachable("behavParamsList") ?
                channel.getBehavParamsList() : existing == null ? null : existing.getBehavParamsList();

        if (effectiveList == null || effectiveList.getId() == null) {
            return;
        }

        effectiveList = em.find(BehavioralParametersList.class, effectiveList.getId());

        if (effectiveList == null) {
            return;
        }

        baseTriggerListValidations.validateTriggersRequired(context, channel, existing, effectiveList.getBehavioralParameters());
    }
}

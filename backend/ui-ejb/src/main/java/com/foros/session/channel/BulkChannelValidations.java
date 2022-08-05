package com.foros.session.channel;

import com.foros.model.account.Account;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.ChannelNamespace;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.session.BeanValidations;
import com.foros.session.bulk.IdNameTO;
import com.foros.session.bulk.Operation;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationsValidations;
import com.foros.session.campaign.bulk.NameUniquenessFilter;
import com.foros.session.channel.service.AdvertisingChannelRestrictions;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.session.query.QueryExecutorService;
import com.foros.session.query.channel.AdvertisingChannelQueryImpl;
import com.foros.session.query.channel.ChannelQuery;
import com.foros.session.query.channel.DiscoverChannelQueryImpl;
import com.foros.session.query.channel.DistinctChannelTOTransformer;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.util.DuplicateChecker;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.hibernate.criterion.ProjectionList;
import org.hibernate.transform.ResultTransformer;

@LocalBean
@Stateless
@Validations
public class BulkChannelValidations {

    public static class ChannelTOTransformer extends DistinctChannelTOTransformer<OwnedNamedTO> {

        @Override
        protected OwnedNamedTO transform(Map<String, Object> values) {
            OwnedNamedTO result = new OwnedNamedTO(
                (Long) values.get("id"),
                (String) values.get("name"),
                (Long) values.get("accountId"));
            return result;
        }

    }

    @EJB
    private BehavioralChannelValidations behavioralChannelValidations;

    @EJB
    private ExpressionChannelValidations expressionChannelValidations;

    @EJB
    private AudienceChannelValidations audienceChannelValidations;

    @EJB
    private OperationsValidations operationsValidations;

    @EJB
    private AdvertisingChannelRestrictions advertisingChannelRestrictions;

    @EJB
    private QueryExecutorService executorService;

    @EJB
    private BeanValidations beanValidations;

    @EJB
    private SearchChannelService searchChannelService;

    @Validation
    public void validateMerge(ValidationContext context, Operations<Channel> channelOperations) {
        NameUniquenessFilter<Channel> filter = new NameUniquenessFilter<Channel>();

        DuplicateChecker<Operation<Channel>> duplicateIdChecker = DuplicateChecker.createOperationDuplicateChecker();

        DuplicateChecker<Operation<Channel>> duplicateNameChecker = DuplicateChecker.create(new OperationNameFetcher(), filter);

        int index = 0;
        for (Operation<Channel> mergeOperation : channelOperations.getOperations()) {
            ValidationContext operationContext = context.createSubContext(mergeOperation, "operations", index++);

            Channel channel = mergeOperation.getEntity();
            String contextName = getChannelPath(channel);

            operationsValidations.validateOperation(operationContext, mergeOperation, contextName);
            if (operationContext.hasViolations()) {
                continue;
            }

            OperationType operationType = mergeOperation.getOperationType();

            ValidationContext channelContext = operationContext
                    .subContext(channel)
                    .withPath(contextName)
                    .withMode(operationType.toValidationMode())
                    .build();

            if (!validateIds(channelContext, channel, operationType)) {
                filter.ignore(channel);
                continue;
            }

            switch (operationType) {
                case CREATE:
                    channelValidateCreate(channelContext, channel);
                    break;
                case UPDATE:
                    channelValidateUpdate(channelContext, channel);
                    break;
                default:
                    throw new RuntimeException();
            }

            if (!channelContext.hasViolation("account")) {
                if (!advertisingChannelRestrictions.canMerge(channel, operationType)) {
                    operationContext.addConstraintViolation("errors.forbidden");
                    filter.ignore(channel);
                    continue;
                }
                if (channel.isChanged("name") && channel.getName() != null) {
                    duplicateNameChecker.check(channelContext, "name", mergeOperation);
                }
            }

            duplicateIdChecker.check(channelContext, "id", mergeOperation);
        }
    }

    private String getChannelPath(Channel channel) {
        if (channel instanceof ExpressionChannel) {
            return "expressionChannel";
        }
        if (channel instanceof BehavioralChannel) {
            return "behavioralChannel";
        }
        if (channel instanceof AudienceChannel) {
            return "audienceChannel";
        }
        if (channel instanceof DiscoverChannel) {
            return "discoverChannel";
        }

        throw new IllegalArgumentException("channel:" + channel);
    }

    private boolean validateIds(ValidationContext groupContext, Channel channel, OperationType operationType) {
        switch (operationType) {
            case CREATE:
                beanValidations.linkValidator(groupContext, Account.class)
                        .withPath("account")
                        .validate(channel.getAccount());
                break;
            case UPDATE:
                beanValidations.linkValidator(groupContext, (Class<Channel>) channel.getClass())
                        .validate(channel);
                break;
        }

        return !groupContext.hasViolations();
    }

    @Validation
    public void validateCountryNameConstraintViolations(ValidationContext context, ChannelNamespace namespace, Operations<Channel> operations) {
        Set<Channel> toBeChecked = new HashSet<Channel>();
        for (Operation<Channel> operation : operations.getOperations()) {
            Channel channel = operation.getEntity();
            if (isConstraintViolationPossible(operation)) {
                toBeChecked.add(channel);
            }
        }

        ChannelQuery query;
        switch (namespace) {
            case ADVERTISING:
            query = new AdvertisingChannelQueryImpl() {
                @Override
                protected ResultTransformer createTOTransformer() {
                    return new ChannelTOTransformer();
                }
            };
                break;
            case DISCOVER:
            query = new DiscoverChannelQueryImpl() {
                @Override
                protected ResultTransformer createTOTransformer() {
                    return new ChannelTOTransformer();
                }

                @Override
                protected ProjectionList createTOProjections() {
                    return createDefaultTOProjections();
                }
            };
                break;
            default:
                throw new IllegalArgumentException("namespace: " + namespace);
        }

        List<OwnedNamedTO> duplicated = query
            .existingByName(toBeChecked)
            .asTO()
            .executor(executorService)
            .list();

        Map<IdNameTO, Long> duplicatedMap = new HashMap<>();
        for (OwnedNamedTO ownedNamedTO : duplicated) {
            duplicatedMap.put(new IdNameTO(ownedNamedTO.getAccountId(), ownedNamedTO.getName()), ownedNamedTO.getId());
        }

        int i = -1;
        for (Operation<Channel> operation : operations.getOperations()) {
            i += 1;
            Channel channel = operation.getEntity();
            IdNameTO newNamedTO = new IdNameTO(channel.getAccount().getId(), channel.getName());
            Long existingChannelId = duplicatedMap.get(newNamedTO);
            if (existingChannelId != null && !existingChannelId.equals(channel.getId())) {
                context
                    .createSubContext(operation, "operations", i)
                    .createSubContext(channel, getChannelPath(channel))
                    .addConstraintViolation("errors.duplicate")
                    .withPath("name")
                    .withParameters("{channel.name}");
            }
        }
    }

    private boolean isConstraintViolationPossible(Operation<Channel> operation) {
        if (operation.getOperationType() == OperationType.CREATE) {
            return true;
        }

        return operation.getEntity().isChanged("name", "country");
    }

    @Validation
    public void validateCreateOrUpdate(ValidationContext context, Channel channel) {
        Channel existing = channel.getId() != null ? searchChannelService.find(channel.getId()) : null;
        OperationType operationType = existing == null ? OperationType.CREATE : OperationType.UPDATE;
        ValidationContext channelContext = context
                .subContext(channel)
                .withMode(operationType.toValidationMode())
                .build();

        if (existing == null) {
            channelValidateCreate(channelContext, channel);
        } else {
            channelValidateUpdate(channelContext, channel);
        }
    }

    public class OperationNameFetcher implements DuplicateChecker.IdentifierFetcher<Operation<Channel>> {
        @Override
        public Object fetch(Operation<Channel> operation) {
            Channel entity = operation.getEntity();
            return new IdNameTO(entity.getAccount().getId(), entity.getName());
        }
    }

    private void channelValidateCreate(ValidationContext context, Channel channel) {
        if (channel instanceof ExpressionChannel) {
            expressionChannelValidations.validateCreate(context, (ExpressionChannel) channel);
        } else if (channel instanceof BehavioralChannel) {
            behavioralChannelValidations.validateCreate(context, (BehavioralChannel) channel);
        } else if (channel instanceof AudienceChannel) {
            audienceChannelValidations.validateCreate(context, (AudienceChannel) channel);
        }
    }

    private void channelValidateUpdate(ValidationContext context, Channel channel) {
        if (channel instanceof ExpressionChannel) {
            expressionChannelValidations.validateUpdate(context, (ExpressionChannel) channel);
        } else if (channel instanceof BehavioralChannel) {
            behavioralChannelValidations.validateUpdate(context, (BehavioralChannel) channel);
        } else if (channel instanceof AudienceChannel) {
            audienceChannelValidations.validateUpdate(context, (AudienceChannel) channel);
        }
    }
}

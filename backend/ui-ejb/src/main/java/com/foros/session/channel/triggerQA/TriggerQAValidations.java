package com.foros.session.channel.triggerQA;

import com.foros.session.bulk.Operation;
import com.foros.session.bulk.Operations;
import com.foros.session.bulk.OperationType;
import com.foros.session.bulk.OperationsValidations;
import com.foros.session.campaign.CampaignCreativeGroupService;
import com.foros.session.campaign.CampaignService;
import com.foros.session.channel.service.SearchChannelService;
import com.foros.util.SQLUtil;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.code.BusinessErrors;
import com.foros.validation.util.DuplicateChecker;
import com.foros.validation.util.EntityIdFetcher;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;


@LocalBean
@Stateless
@Validations
public class TriggerQAValidations {

    @EJB
    private OperationsValidations operationsValidations;

    @PersistenceContext
    private EntityManager em;

    @EJB
    private CampaignService campaignService;

    @EJB
    private CampaignCreativeGroupService campaignCreativeGroupService;

    @EJB
    private SearchChannelService searchChannelService;

    @Validation
    public void validateGet(ValidationContext context, TriggerQASelector selector) {
        int suppliedRequiredParams = 0;
        suppliedRequiredParams += selector.getCampaignId() == null ? 0 : 1;
        suppliedRequiredParams += selector.getCcgId() == null ? 0 : 1;
        suppliedRequiredParams += selector.getChannelId() == null ? 0 : 1;
        if (suppliedRequiredParams != 1) {
            context.addConstraintViolation("errors.api.incorrectCriteria.triggerQA")
                   .withError(BusinessErrors.OPERATION_NOT_PERMITTED);
            return;
        }

        Long id = null;
        try {
            if (selector.getCampaignId() != null) {
                id = selector.getCampaignId();
                campaignService.find(selector.getCampaignId());
            } else if (selector.getCcgId() != null) {
                id = selector.getCcgId();
                campaignCreativeGroupService.find(selector.getCcgId());
            } else {
                id = selector.getChannelId();
                searchChannelService.find(selector.getChannelId());
            }
        } catch (EntityNotFoundException e) {
            context.addConstraintViolation("errors.entity.notFound")
                   .withValue(id);
            return;
        }
    }

    @Validation
    public void  validatePerform(ValidationContext context, Operations<TriggerQATO> operations) {
        DuplicateChecker<TriggerQATO> duplicateChecker = DuplicateChecker
                .create(new EntityIdFetcher<TriggerQATO>())
                .withTemplate("errors.duplicate.id");
        Set<Long> existingIds = existingTriggerIds(operations);

        int operationNum = 0;
        for (Operation<TriggerQATO> operation : operations.getOperations()) {
            ValidationContext operationContext = context.createSubContext(operation, "operations", operationNum++);

            operationsValidations.validateOperation(operationContext, operation, "qaTrigger");

            if (operationContext.hasViolations()) {
                continue;
            }

            if (operation.getOperationType() != OperationType.UPDATE) {
                operationContext.addConstraintViolation("error.operation.not.permitted");
                continue;
            }

            TriggerQATO triggerQa = operation.getEntity();
            ValidationContext triggerContext = operationContext.createSubContext(triggerQa, "qaTrigger");
            if (triggerQa.getId() == null) {
                triggerContext.addConstraintViolation("errors.field.required")
                        .withPath("id");
            } else {
                if (!existingIds.contains(triggerQa.getId())) {
                    triggerContext.addConstraintViolation("errors.entity.notFound")
                            .withPath("id")
                            .withValue(triggerQa.getId());
                }
                if (!duplicateChecker.check(triggerContext, "id", triggerQa)) {
                    continue;
                }
            }

            if (triggerQa.getQaStatus() == null) {
                triggerContext.addConstraintViolation("errors.field.required")
                    .withPath("status");
            }
        }
    }

    private Set<Long> existingTriggerIds(Operations<TriggerQATO> operations) {
        Set<Long> suppliedIds = new HashSet<>(operations.getOperations().size());
        for (Operation<TriggerQATO> operation : operations.getOperations()) {
            suppliedIds.add(operation.getEntity().getId());
        }
        if(suppliedIds.isEmpty()) {
            return Collections.emptySet();
        }
        Query q = em.createNativeQuery("SELECT trigger_id FROM triggers WHERE " + SQLUtil.formatINClause("trigger_id", suppliedIds));
        Set<Long> result = new HashSet<>(suppliedIds.size());
        for (Object value : q.getResultList()) {
            result.add(((Number)value).longValue());
        }
        return result;
    }
}

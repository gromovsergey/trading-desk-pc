package com.foros.session.bulk;

import com.foros.model.action.Action;
import com.foros.model.campaign.CCGKeyword;
import com.foros.model.campaign.Campaign;
import com.foros.model.campaign.CampaignCreative;
import com.foros.model.campaign.CampaignCreativeGroup;
import com.foros.model.channel.AudienceChannel;
import com.foros.model.channel.BehavioralChannel;
import com.foros.model.channel.Channel;
import com.foros.model.channel.DiscoverChannel;
import com.foros.model.channel.ExpressionChannel;
import com.foros.model.creative.Creative;
import com.foros.session.channel.triggerQA.TriggerQATO;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.constraint.validator.EntityIntegrityValidator;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.Map;

@LocalBean
@Stateless
@Validations
public class OperationsValidations {

    private Map<String, EntityIntegrityValidator> entityIntegrityValidators = getEntityIntegrityValidators();

    private Map<String, EntityIntegrityValidator> getEntityIntegrityValidators() {
        Map<String, EntityIntegrityValidator> result = new HashMap<>();

        result.put("campaign", new EntityIntegrityValidator<>(Campaign.class));
        result.put("campaignCreativeGroup", new EntityIntegrityValidator<>(CampaignCreativeGroup.class));
        result.put("ccgKeyword", new EntityIntegrityValidator<>(CCGKeyword.class));
        result.put("creative", new EntityIntegrityValidator<>(Creative.class));
        result.put("creativeLink", new EntityIntegrityValidator<>(CampaignCreative.class));
        result.put("campaignCreative", new EntityIntegrityValidator<>(CampaignCreative.class));
        result.put("channel", new EntityIntegrityValidator<>(Channel.class));
        result.put("discoverChannel", new EntityIntegrityValidator<>(DiscoverChannel.class));
        result.put("expressionChannel", new EntityIntegrityValidator<>(ExpressionChannel.class));
        result.put("behavioralChannel", new EntityIntegrityValidator<>(BehavioralChannel.class));
        result.put("audienceChannel", new EntityIntegrityValidator<>(AudienceChannel.class));
        result.put("qaTrigger", new EntityIntegrityValidator<>(TriggerQATO.class));
        result.put("conversion", new EntityIntegrityValidator<>(Action.class));

        return result;
    }

    public boolean validateOperation(ValidationContext operationContext, Operation<?> mergeOperation, String entityPath) {
        if (mergeOperation == null) {
            operationContext.addConstraintViolation("errors.field.required");
            return true;
        }
        if (mergeOperation.getOperationType() == null) {
            operationContext
                 .addConstraintViolation("errors.field.required")
                 .withPath("operationType");
        }

        entityIntegrityValidators.get(entityPath)
                .withPath(entityPath)
                .withContext(operationContext)
                .validate(mergeOperation.getEntity());

        return !operationContext.hasViolations();

    }

    @Validation
    public void validateIntegrity(ValidationContext context, Operations<?> mergeOperations, String entityPath) {
        int index = 0;
        for (Operation<?> mergeOperation : mergeOperations.getOperations()) {
            ValidationContext operationContext = context.createSubContext(mergeOperation, "operations", index++);
            validateOperation(operationContext, mergeOperation, entityPath);
        }
    }
}

package com.foros.session.template;

import com.foros.config.ConfigService;
import com.foros.model.template.OptionGroup;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;

@LocalBean
@Stateless
@Validations
public class OptionGroupValidations {

    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    ConfigService configService;

    @Validation
    public void validateCreate(
            ValidationContext context,
            @ValidateBean(ValidationMode.CREATE) OptionGroup optionGroup) {
        if (!context.hasViolation("type")) {
            validateSortOrder(context, optionGroup, optionGroup.getSortOrder());
        }
    }

    @Validation
    public void validateUpdate(ValidationContext context,
            @ValidateBean(ValidationMode.UPDATE) OptionGroup optionGroup) {
        OptionGroup existing = em.find(OptionGroup.class, optionGroup.getId());
        if (existing == null) { 
            throw new EntityNotFoundException("Option Group with id=" + optionGroup.getId() + " not found");
        }
        validateSortOrder(context, existing, optionGroup.getSortOrder());
    }
    
    private void validateSortOrder(ValidationContext context, OptionGroup optionGroup, Long sortOrder) {
        if (!context.isReachable("sortOrder")) {
            return;
        }

        int size = 0;
        if ((optionGroup.getCreativeSize() != null) && (optionGroup.getCreativeSize().getId() != null)) {
            switch (optionGroup.getType()) {
                case Advertiser:
                    size = optionGroup.getCreativeSize().getAdvertiserOptionGroups().size();
                    break;
                case Publisher:
                    size = optionGroup.getCreativeSize().getPublisherOptionGroups().size();
                    break;
                case Hidden:
                    size = optionGroup.getCreativeSize().getHiddenOptionGroups().size();
                    break;
            }
        } else if ((optionGroup.getTemplate() != null) && (optionGroup.getTemplate().getId() != null)) {
            switch (optionGroup.getType()) {
                case Advertiser:
                    size = optionGroup.getTemplate().getAdvertiserOptionGroups().size();
                    break;
                case Publisher:
                    size = optionGroup.getTemplate().getPublisherOptionGroups().size();
                    break;
                case Hidden:
                    size = optionGroup.getTemplate().getHiddenOptionGroups().size();
                    break;
            }
        }

        if (sortOrder > size + 1) {
            context
            .addConstraintViolation("errors.field.required")
            .withPath("sortOrder")
            .withValue(sortOrder);
        }

    }
}


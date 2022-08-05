package com.foros.session.admin.categoryChannel;

import com.foros.model.Status;
import com.foros.model.channel.TriggersChannel;
import com.foros.model.channel.CategoryChannel;
import com.foros.session.BeanValidations;
import com.foros.session.CurrentUserService;
import com.foros.session.EntityTO;
import com.foros.session.channel.CommonChannelValidations;
import com.foros.validation.ValidationContext;
import com.foros.validation.annotation.ValidateBean;
import com.foros.validation.annotation.Validation;
import com.foros.validation.annotation.Validations;
import com.foros.validation.strategy.ValidationMode;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Set;

@LocalBean
@Stateless
@Validations
public class CategoryChannelValidations extends CommonChannelValidations {
    @PersistenceContext(unitName = "AdServerPU")
    private EntityManager em;

    @EJB
    private CurrentUserService currentUserService;

    @EJB
    private CategoryChannelService categoryChannelService;

    @EJB
    private BeanValidations beanValidations;

    @Validation
    public void validateCreate(ValidationContext context,
            @ValidateBean(ValidationMode.CREATE) CategoryChannel categoryChannel) {
        validateChannelName(context, categoryChannel);
    }

    @Validation
    public void validateUpdate(ValidationContext context,
            @ValidateBean(ValidationMode.UPDATE) CategoryChannel categoryChannel) {
        validateChannelName(context, categoryChannel);
    }

    public void validateCategories(ValidationContext context, TriggersChannel channel) {
        if (context.isReachable("categories") && currentUserService.isInternal()) {
            Set<CategoryChannel> categories = channel.getCategories();
            for (CategoryChannel categoryChannel : categories) {
                beanValidations.linkValidator(context, CategoryChannel.class)
                    .withRequired(true)
                    .withCheckDeleted(null)
                    .withPath("categories")
                    .validate(categoryChannel);

                if (context.props("categories").reachableAndNoViolations() && isParentDeleted(categoryChannel)) {
                    context.addConstraintViolation("channel.errors.invalidCategory")
                            .withValue(categoryChannel)
                            .withPath("categories");
                }
            }
        }
    }

    private boolean isParentDeleted(CategoryChannel channel) {
        List<EntityTO> ancestors = categoryChannelService.getChannelAncestorsChain(channel.getId(), false);
        for (EntityTO ancestor : ancestors) {
            if (ancestor.getStatus() == Status.DELETED) {
                return true;
            }
        }
        return false;
    }
}

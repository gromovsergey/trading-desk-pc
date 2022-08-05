package com.foros.action.action;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.action.Action;
import com.foros.model.action.ConversionCategory;
import com.foros.session.action.ActionService;
import com.foros.util.context.RequestContexts;
import com.foros.validation.annotation.Validate;
import com.foros.validation.strategy.ValidationMode;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(
    conversionErrorFields = {
        @ConversionErrorFieldValidator(fieldName = "impWindow", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "clickWindow", key = "errors.field.integer"),
                @ConversionErrorFieldValidator(fieldName = "value", key = "errors.field.integer")
    }
)
public class SaveActionAction extends BaseActionAction implements ModelDriven<Action>, RequestContextsAware, BreadcrumbsSupport {

    @EJB
    private ActionService actionService;

    private Action action = new Action();

    @Validate(validation = "Action.create", parameters = "#target.model")
    public String create() {
        actionService.create(action);
        return SUCCESS;
    }

    @Validate(validation = "Action.update", parameters = "#target.model")
    public String update() {
        actionService.update(action);
        return SUCCESS;
    }

    @Override
    public Action getModel() {
        return action;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.getAdvertiserContext().switchTo(action.getAccount().getId());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = new Breadcrumbs();
        if (action.getId() != null) {
            final Action persistent = actionService.findById(action.getId());
            breadcrumbs.add(new ActionBreadcrumbsElement(persistent)).add(ActionBreadcrumbs.EDIT);
        }

        return breadcrumbs;
    }

    public void setConversionCategoryId(Integer id) {
        getModel().setConversionCategory(ConversionCategory.valueOf(id));
    }

    public ValidationMode getValidationMode() {
        return getModel().getId() == null ? ValidationMode.CREATE : ValidationMode.UPDATE;
    }
}

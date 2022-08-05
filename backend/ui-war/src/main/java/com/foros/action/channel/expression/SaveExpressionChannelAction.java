package com.foros.action.channel.expression;

import com.foros.action.channel.ChannelBreadcrumbsElement;
import com.foros.action.channel.ChannelsBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.account.Account;
import com.foros.model.account.GenericAccount;
import com.foros.model.channel.ExpressionChannel;
import com.foros.security.AccountRole;
import com.foros.session.channel.exceptions.ChannelNotFoundExpressionException;
import com.foros.session.channel.exceptions.ExpressionConversionException;
import com.foros.session.channel.exceptions.UndistinguishableExpressionException;
import com.foros.session.channel.exceptions.UnreachableExpressionException;
import com.foros.session.channel.service.ExpressionChannelService;
import com.foros.session.channel.service.ExpressionService;
import com.foros.util.AccountUtil;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRule;
import com.foros.validation.constraint.violation.matcher.ConstraintViolationRulesBuilder;
import com.foros.validation.annotation.Validate;

import java.util.List;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;

@Validations(
        conversionErrorFields = {
                @ConversionErrorFieldValidator(fieldName = "channelRateValue", key = "errors.field.number")
        }
)
public class SaveExpressionChannelAction extends SearchChannelSupport implements BreadcrumbsSupport {

    private static final List<ConstraintViolationRule> RULES = new ConstraintViolationRulesBuilder()
            .add("channelRate.value", "'channelRateValue'", "violation.message")
            .rules();

    @EJB
    private ExpressionChannelService expressionChannelService;

    @EJB
    private ExpressionService expressionService;

    private Account existingAccount;

    private String humanExpression; 

    public SaveExpressionChannelAction() {
        model = new ExpressionChannel();
        model.setAccount(new GenericAccount());
    }

    public String create() throws ExpressionConversionException {
        prepare();
        if (hasErrors()) {
            return INPUT;
        }
        expressionChannelService.create(model);
        return SUCCESS;
    }

    private void prepare() {
        prepareAccount();
        prepareExpression();
        searchCriteria.populateConditionOfVisibility(getExistingAccount());
    }

    private void prepareExpression() {
        try {
            model.setExpression(expressionService.convertFromHumanReadable(humanExpression, model.getCountry().getCountryCode()));
        } catch (UnreachableExpressionException e) {
            addFieldError("expression", getText("errors.wrong.cdml"));
        } catch (ChannelNotFoundExpressionException e) {
            addFieldError("expression", getText("errors.channelNotFound", new String[]{e.getName()}));
        } catch (UndistinguishableExpressionException e) {
            addFieldError("expression", getText(e.getMessage(), new String[]{e.getName()}));
        } catch (ExpressionConversionException e) {
            addFieldError("expression", getText("errors.expression", new String[]{model.getExpression()}));
        }
    }
    
    private void prepareAccount() {
        model.setAccount(AccountUtil.extractAccount(model.getAccount().getId()));
    }
    

    public String update() throws ExpressionConversionException {
        prepare();
        if (hasErrors()) {
            return INPUT;
        }
        expressionChannelService.update(model);
        return SUCCESS;
    }

    @Validate(validation = "ExpressionChannel.submitToCmp", parameters = "#target.prepareCmpModel()")
    public String submitToCmp() {
        prepareCmpModel();
        expressionChannelService.submitToCmp(model);
        return SUCCESS;
    }

    @Validate(validation = "ExpressionChannel.update", parameters = "#target.prepareCmpModel()")
    public String updateCmp() {
        prepareCmpModel();
        expressionChannelService.update(model);
        return SUCCESS;
    }
    
    public String convertCDMLtoHumanReadable(String expression) {
        try {
            return expressionService.convertToHumanReadable(expression);
        } catch (ExpressionConversionException e) {
            return expression;
        }
    }

    @Override
    public Account getExistingAccount() {
        if (existingAccount == null) {
            existingAccount = accountService.find(model.getAccount().getId());
        }
        return existingAccount;
    }

    @Override
    public List<ConstraintViolationRule> getConstraintViolationRules() {
        return RULES;
    }

    public void setHumanExpression(String humanExpression) {
        this.humanExpression = humanExpression;
    }

    public String getHumanExpression() {
        return humanExpression;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = null;
        if (model.getId() != null) {
            ExpressionChannel persistent = expressionChannelService.find(model.getId());
            breadcrumbs = ChannelBreadcrumbsElement.getChannelBreadcrumbs(persistent).add(ActionBreadcrumbs.EDIT);
        } else {
            if (getExistingAccount().getRole() == AccountRole.INTERNAL) {
                breadcrumbs = new Breadcrumbs().add(new ChannelsBreadcrumbsElement()).add(ActionBreadcrumbs.CREATE);
            }
        }

        return breadcrumbs;
    }
}

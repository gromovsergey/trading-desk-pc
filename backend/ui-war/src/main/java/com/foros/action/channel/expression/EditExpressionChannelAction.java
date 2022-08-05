package com.foros.action.channel.expression;

import com.foros.action.channel.ChannelBreadcrumbsElement;
import com.foros.action.channel.SubmitToCmpBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.account.Account;
import com.foros.security.AccountRole;
import com.foros.session.channel.exceptions.ExpressionConversionException;
import com.foros.session.channel.service.ExpressionChannelService;
import com.foros.session.channel.service.ExpressionService;

import javax.ejb.EJB;

public class EditExpressionChannelAction extends SearchChannelSupport implements BreadcrumbsSupport {

    @EJB
    protected ExpressionChannelService expressionChannelService;

    @EJB
    private ExpressionService expressionService;

    // param
    private Long id;

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    public String edit() {
        editProcess();
        breadcrumbs = ChannelBreadcrumbsElement.getChannelBreadcrumbs(getModel()).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    @ReadOnly
    public String submitCmp() {
        editProcess();
        breadcrumbs = ChannelBreadcrumbsElement.getChannelBreadcrumbs(getModel()).add(new SubmitToCmpBreadcrumbsElement());
        return SUCCESS;
    }

    private void editProcess() {
        model = expressionChannelService.findForUpdate(id);
        searchCriteria.populateConditionOfVisibility(getExistingAccount());
    }

    public boolean isUsedAvailable() {
        return model.getAccount().getRole() == AccountRole.AGENCY || model.getAccount().getRole() == AccountRole.ADVERTISER;
    }

    @ReadOnly
    public String editCmp() {
        model = expressionChannelService.findForUpdate(id);
        populateRate();
        breadcrumbs = ChannelBreadcrumbsElement.getChannelBreadcrumbs(getModel()).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public Account getExistingAccount() {
        return model.getAccount();
    }

    public String getHumanExpression() {
        try {
            return expressionService.convertToHumanReadable(model.getExpression());
        } catch (ExpressionConversionException e) {
            return model.getExpression();
        }
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}

package com.foros.action.action;

import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.action.Action;
import com.foros.restriction.annotation.Restrict;
import com.foros.session.account.AccountService;
import com.foros.session.action.ActionService;
import com.foros.util.EntityUtils;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;

import com.opensymphony.xwork2.ModelDriven;

public class EditActionAction extends BaseActionAction implements RequestContextsAware, AdvertiserSelfIdAware, ModelDriven<Action>, BreadcrumbsSupport {

    // parameters
    private Long id;
    private Long advertiserId;

    @EJB
    private ActionService actionService;

    @EJB
    private AccountService accountService;


    private Action action;

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    @Restrict(restriction = "AdvertiserEntity.create", parameters = "find('AdvertiserAccount', #target.advertiserId)")
    public String create() {
        action = createEmptyAction();
        return SUCCESS;
    }

    @ReadOnly
    public String edit() {
        action = EntityUtils.applyOwnerStatusRule(actionService.view(id));
        breadcrumbs = new Breadcrumbs().add(new ActionBreadcrumbsElement(action)).add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }

    private Action createEmptyAction() {
        Action result = new Action();
        result.setAccount(accountService.findAdvertiserAccount(advertiserId));
        result.setImpWindow(7);
        result.setClickWindow(30);
        return result;
    }

    @Override
    public void switchContext(RequestContexts context) {
        context.getAdvertiserContext().switchTo(action.getAccount().getId());
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void setAdvertiserId(Long advertiserId) {
        this.advertiserId = advertiserId;
    }

    public Long getAdvertiserId() {
        return advertiserId;
    }

    @Override
    public Action getModel() {
        return action;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}

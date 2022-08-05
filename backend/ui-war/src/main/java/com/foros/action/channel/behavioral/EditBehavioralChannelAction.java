package com.foros.action.channel.behavioral;

import com.foros.action.channel.ChannelBreadcrumbsElement;
import com.foros.action.channel.SubmitToCmpBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.BreadcrumbsSupport;
import com.foros.model.account.Account;
import com.foros.model.channel.BehavioralChannel;
import com.foros.security.AccountRole;
import com.foros.session.channel.service.BehavioralChannelService;

import javax.ejb.EJB;

public class EditBehavioralChannelAction extends EditChannelSupport<BehavioralChannel> implements BreadcrumbsSupport {

    @EJB
    protected BehavioralChannelService behavioralChannelService;

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
        model = behavioralChannelService.findForUpdate(id);
        loadTriggers();
        loadAvailableLanguages();
    }

    @ReadOnly
    public String editCmp() {
        model = behavioralChannelService.findForUpdate(id);
        populateRate();
        loadAvailableLanguages();
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

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}

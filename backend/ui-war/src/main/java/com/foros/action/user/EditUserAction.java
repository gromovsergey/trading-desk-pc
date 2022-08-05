package com.foros.action.user;

import com.foros.action.account.InternalAccountBreadcrumbsElement;
import com.foros.action.account.InternalAccountsBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.model.security.Language;
import com.foros.model.security.User;
import com.foros.security.AccountRole;
import com.foros.security.AuthenticationType;
import com.foros.session.account.AccountService;
import com.foros.util.context.RequestContexts;

import javax.ejb.EJB;

public class EditUserAction extends EditSaveUserActionSupport implements RequestContextsAware {
    @EJB
    private AccountService accountService;

    protected Long id;

    private Long accountId;

    private Breadcrumbs breadcrumbs;

    @ReadOnly
    public String edit() {
        user = userService.view(id);
        if (user.getAccount().getRole() == AccountRole.INTERNAL) {
            breadcrumbs = buildInternalUserEditBreadcrumbs(user);
        } else {
            breadcrumbs = new Breadcrumbs().add(isInternal() ? new ExternalUserBreadcrumbsElement(user) : new MyUserBreadcrumbsElement(user));
        }
        breadcrumbs.add(ActionBreadcrumbs.EDIT);
        return SUCCESS;
    }


    Breadcrumbs buildInternalUserEditBreadcrumbs(User entity) {
        return new Breadcrumbs()
                .add(new InternalAccountsBreadcrumbsElement())
                .add(new InternalAccountBreadcrumbsElement(entity.getAccount()))
                .add(new InternalUserBreadcrumbsElement(entity));
    }

    @ReadOnly
    public String create() {
        Account account = accountId != null ? accountService.find(accountId) : accountService.getMyAccount();
        user.setAuthType(account.getRole() == AccountRole.INTERNAL ? AuthenticationType.LDAP : AuthenticationType.PSWD);
        user.setAccount(account);
        if (!getRoles().isEmpty()) {
            user.setRole(getRoles().get(0));
        }

        String lang = account.getCountry().getLanguage();
        if (lang != null) {
            user.setLanguage(Language.valueOfCode(lang));
        }
        if (account.getRole() == AccountRole.INTERNAL) {
            breadcrumbs = new Breadcrumbs().add(new InternalAccountsBreadcrumbsElement())
                    .add(new InternalAccountBreadcrumbsElement(user.getAccount()))
                    .add("account.headers.user.create");
        }
        return SUCCESS;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.switchTo(user.getAccount());
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        return breadcrumbs;
    }
}

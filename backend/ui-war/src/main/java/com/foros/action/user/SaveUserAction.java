package com.foros.action.user;

import com.foros.action.account.InternalAccountBreadcrumbsElement;
import com.foros.action.account.InternalAccountsBreadcrumbsElement;
import com.foros.breadcrumbs.ActionBreadcrumbs;
import com.foros.breadcrumbs.Breadcrumbs;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.security.User;
import com.foros.model.site.Site;
import com.foros.security.AccountRole;
import com.foros.security.AuthenticationType;
import com.foros.session.security.UserService;
import com.foros.util.FlagsUtil;
import com.foros.util.MailHelper;
import com.foros.util.RequestUtil;
import com.foros.validation.annotation.Validate;

import com.opensymphony.xwork2.validator.annotations.ConversionErrorFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.interceptor.ServletRequestAware;

@Validations(
    conversionErrorFields = {
        @ConversionErrorFieldValidator(fieldName="maxCreditLimit", key="errors.field.number")
    }
)
public class SaveUserAction extends EditSaveUserActionSupport implements ServletRequestAware, RequestContextsAware {

    private User existingUser;

    private Boolean passwordSent = null;

    private boolean mailFailure;

    private HttpServletRequest request;

    public String updateMaxCreditLimit() throws Exception {
        User currUser = getExistingUser();
        currUser.setMaxCreditLimit(user.getMaxCreditLimit());
        currUser.setVersion(user.getVersion());
        user = currUser;
        userService.updateMaxCreditLimit(user);
        return SUCCESS;
    }

    @Validate(validation = "User.update", parameters = "#target.model")
    public String update() throws Exception {
        User currUser = getExistingUser();

        preSave();

        if (AccountRole.PUBLISHER.equals(currUser.getAccount().getRole())) {
            user.registerChange("sites");
        }

        String newPassword = createNewPassword();
        String forosBaseUrl = RequestUtil.getBaseUrl(request);
        userService.update(user);

        if (!currUser.getEmail().equals(user.getEmail()) && AuthenticationType.PSWD.equals(user.getAuthType())) {
            MailHelper.sendUserUpdateMail(user, newPassword, forosBaseUrl);

            if (StringUtils.isNotEmpty(newPassword)) {
                passwordSent = user.isMailSent();
            } else {
                mailFailure = !user.isMailSent();
            }
        } else if (StringUtils.isNotEmpty(newPassword)) {// Changing Auth type from NO_LOGIN to PASSWORD with no email change
            MailHelper.sendNewPasswordMail(user, newPassword, forosBaseUrl);
            passwordSent = user.isMailSent();
        }

        return getResult();
    }

    @Validate(validation = "User.create", parameters = "#target.model")
    public String create() throws Exception {
        preSave();

        String newPassword = createNewPassword();
        String forosBaseUrl = RequestUtil.getBaseUrl(request);

        userService.create(user).toString();

        if (AuthenticationType.PSWD.equals(user.getAuthType())) {
            MailHelper.sendWelcomeMail(user, newPassword, forosBaseUrl);

            if (StringUtils.isNotEmpty(newPassword)) {
                passwordSent = user.isMailSent();
            } else {
                mailFailure = !user.isMailSent();
            }
        }
        return getResult();
    }

    private String createNewPassword() {
        String newPassword = null;
        if (userService.isCreateNewPassword(user)) {
            newPassword = userService.generatePassword(UserService.GENPASSWORD_LENGTH);
            user.setNewPassword(userService.hashPassword(newPassword));
        } else {
            user.setNewPassword(null);
        }
        return newPassword;
    }

    private String getResult() {
        if (passwordSent != null) {
            return "success.password_sent";
        }

        if (mailFailure) {
            return "success.mail_failure";
        }

        return SUCCESS;
    }

    private void preSave() {
        if (!AuthenticationType.LDAP.equals(user.getAuthType())) {
            user.setDn("");
        }
    }

    public void setAdvLevelAccessFlag(boolean flag) {
        user.setFlags(FlagsUtil.set(user.getFlags(), User.ADV_LEVEL_ACCESS_FLAG, flag));
    }

    public void setSiteLevelAccessFlag(boolean flag) {
        user.setFlags(FlagsUtil.set(user.getFlags(), User.SITE_LEVEL_ACCESS_FLAG, flag));
    }

    public void setSelectedSites(Collection<Long> selectedSites) {
        Set<Site> sites;
        if (selectedSites != null) {
            sites = new LinkedHashSet<Site>(selectedSites.size());
            for (Long id : selectedSites) {
                sites.add(new Site(id));
            }
        } else {
            sites = Collections.emptySet();
        }
        user.setSites(sites);
    }

    public Boolean getPasswordSent() {
        return passwordSent;
    }

    public boolean isMailFailure() {
        return mailFailure;
    }

    public User getExistingUser() {
        if (existingUser == null && user.getId() != null) {
            existingUser = userService.find(user.getId());
        }
        return existingUser;
    }

    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest) {
        request = httpServletRequest;
    }

    @Override
    public Breadcrumbs getBreadcrumbs() {
        Breadcrumbs breadcrumbs = null;
        if (user.getAccount().getRole() == AccountRole.INTERNAL) {
            User internalUser = user;
            if (user.getId() != null) {
                internalUser = userService.find(user.getId());
            }
            breadcrumbs = buildInternalUserBreadcrumbs(internalUser);
        } else if (user.getId() != null) {
            User persistent = userService.find(user.getId());
            breadcrumbs = new Breadcrumbs()
                    .add(isInternal() ? new ExternalUserBreadcrumbsElement(persistent) : new MyUserBreadcrumbsElement(persistent))
                    .add(ActionBreadcrumbs.EDIT);
        }
        return breadcrumbs;
    }

    Breadcrumbs buildInternalUserBreadcrumbs(User entity) {
        Breadcrumbs breadcrumbs = new Breadcrumbs()
                .add(new InternalAccountsBreadcrumbsElement())
                .add(new InternalAccountBreadcrumbsElement(entity.getAccount()));
        if (entity.getId() != null) {
            User persistent = userService.find(entity.getId());
            breadcrumbs.add(new InternalUserBreadcrumbsElement(persistent));
            breadcrumbs.add(ActionBreadcrumbs.EDIT);
        } else {
            breadcrumbs.add("account.headers.user.create");
        }
        return breadcrumbs;
    }
}

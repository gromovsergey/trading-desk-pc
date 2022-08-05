package com.foros.action.user;

import com.foros.model.account.GenericAccount;
import com.foros.model.security.User;
import com.foros.util.FlagsUtil;
import com.foros.util.MailHelper;
import com.foros.util.RequestUtil;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

public class SaveMyPreferencesAction extends UserActionSupport implements ServletRequestAware {
    private HttpServletRequest request;

    private boolean deletedObjectsVisible;

    public SaveMyPreferencesAction() {
        user.setAccount(new GenericAccount());
    }

    public boolean isDeletedObjectsVisible() {
        return deletedObjectsVisible;
    }

    public void setDeletedObjectsVisible(boolean deletedObjectsVisible) {
        this.deletedObjectsVisible = deletedObjectsVisible;
    }

    public String save() {
        User persistentUser = userService.getMyUser();
        boolean isEmailChanged = !persistentUser.getEmail().equals(user.getEmail());

        persistentUser.setEmail(user.getEmail());
        persistentUser.setFirstName(user.getFirstName());
        persistentUser.setLastName(user.getLastName());
        persistentUser.setPhone(user.getPhone());
        persistentUser.setVersion(user.getVersion());
        persistentUser.setLanguage(user.getLanguage());
        persistentUser.setJobTitle(user.getJobTitle());
        persistentUser.setFlags(FlagsUtil.set(persistentUser.getFlags(), User.IS_DELETED_OBJECTS_VISIBLE, deletedObjectsVisible));

        userService.updateMyPreferences(persistentUser);

        if (isEmailChanged) {
            MailHelper.sendUserUpdateMail(persistentUser, null, RequestUtil.getBaseUrl(request));
        }

        if (isEmailChanged && !persistentUser.isMailSent()) {
            return "success.mail_failure";
        }
        return SUCCESS;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }
}

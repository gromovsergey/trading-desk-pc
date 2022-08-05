package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.session.security.UserRestrictions;

import javax.ejb.EJB;


public class FinanceUserCheckXMLAction extends AbstractXmlAction<Boolean> {

    private String userRoleId;

    @EJB
    private UserRestrictions userRestrictions;

    @Override
    protected Boolean generateModel() throws ProcessException {
        return userRestrictions.canUpdateMaxCreditLimit(Long.valueOf(getUserRoleId()));
    }

    public String getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(String userRoleId) {
        this.userRoleId = userRoleId;
    }
}

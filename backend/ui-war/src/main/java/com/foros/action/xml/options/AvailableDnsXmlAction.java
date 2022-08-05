package com.foros.action.xml.options;

import com.foros.action.xml.ProcessException;
import com.foros.action.xml.options.converter.NameValuePairConverter;
import com.foros.model.security.UserRole;
import com.foros.session.admin.userRole.UserRoleService;
import com.foros.session.security.LdapService;
import com.foros.util.NameValuePair;

import java.util.Collection;

import javax.ejb.EJB;

import com.opensymphony.xwork2.validator.annotations.RequiredFieldValidator;

public class AvailableDnsXmlAction extends AbstractOptionsAction<NameValuePair<String, String>> {
    @EJB
    private LdapService ldapService;
    @EJB
    private UserRoleService userRoleService;

    private String roleId;

    public AvailableDnsXmlAction() {
        super(new NameValuePairConverter(false));
    }

    @RequiredFieldValidator(key = "errors.required", message = "roleId")
    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    @Override
    protected Collection<NameValuePair<String, String>> getOptions() throws ProcessException {
        UserRole userRole = userRoleService.findById(Long.valueOf(getRoleId()));
        return ldapService.findDnsForRole(userRole);
    }
}

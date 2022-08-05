package com.foros.action.xml.sundry;

import com.foros.action.xml.AbstractXmlAction;
import com.foros.action.xml.ProcessException;
import com.foros.action.xml.model.UserData;
import com.foros.session.security.LdapService;

import javax.ejb.EJB;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import com.opensymphony.xwork2.validator.annotations.RequiredStringValidator;

public class UserDataXmlAction extends AbstractXmlAction<UserData> {

    private String dn;
    @EJB
    private LdapService ldapService;

    @RequiredStringValidator(key = "errors.required", message = "dn")
    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    private String getLdapAttrValue(Attributes attrs, String attrName) throws NamingException {
        final Attribute attribute = attrs.get(attrName);

        if (attribute != null) {
            return attribute.get().toString();
        } else {
            return "";
        }
    }
    
    public UserData generateModel() throws ProcessException {
        Attributes attrs = ldapService.getAttrsByDn(dn);

        if (attrs != null) {
            try {
                String firstName = getLdapAttrValue(attrs, LdapService.LDAP_FIRSTNAME);
                String lastName = getLdapAttrValue(attrs, LdapService.LDAP_LASTNAME);
                String email = getLdapAttrValue(attrs, LdapService.LDAP_EMAIL);
                String phone = getLdapAttrValue(attrs, LdapService.LDAP_PHONE);
                String jobTitle = getLdapAttrValue(attrs, LdapService.LDAP_JOBTITLE);
                return new UserData(firstName, lastName, email, jobTitle, phone);
            } catch (NamingException e) {
                throw new ProcessException("Can't fetch user data with dn = " + dn, e);
            }
        } else {
            throw new ProcessException("Can't find user with dn = " + dn);
        }
    }

}

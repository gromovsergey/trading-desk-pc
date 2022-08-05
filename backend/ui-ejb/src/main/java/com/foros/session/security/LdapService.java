package com.foros.session.security;

import com.foros.model.security.UserRole;
import com.foros.util.NameValuePair;

import javax.ejb.Local;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import java.util.List;

@Local
public interface LdapService {
    public String LDAP_LASTNAME = "sn";
    public String LDAP_FIRSTNAME = "givenName";
    public String LDAP_JOBTITLE = "title";
    public String LDAP_PHONE = "telephoneNumber";
    public String LDAP_EMAIL = "mail";
    public String LDAP_LOGIN = "uid";
    public String LDAP_PASSWORD = "userPassword";
    public String LDAP_UID = "cn";
    public String LDAP_USERBASEDN = "ou=People";

    List<NameValuePair<String, String>> findDnsForRole(UserRole role);

    Attributes getAttrsByDn(String dn);

    boolean userExists(String name, String password, String role) throws NamingException;
}

package app.programmatic.ui.authentication.service;

import javax.naming.NamingException;


public interface LdapService {
    boolean userExists(String name, String password, String role) throws NamingException;
}

package app.programmatic.ui.authentication.service;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;


public class LdapServiceImpl implements LdapService {
    private String ldapBaseDn;
    private String ldapDirectory;
    private Long ldapConnectionTimeout;

    public void init(String ldapBaseDn, String ldapDirectory, Long ldapConnectionTimeout) {
        this.ldapBaseDn = ldapBaseDn;
        this.ldapDirectory = ldapDirectory;
        this.ldapConnectionTimeout = ldapConnectionTimeout;
    }

    @Override
    public boolean userExists(String name, String password, String role) throws NamingException {
        SearchControls controls = new SearchControls();
        controls.setReturningAttributes(new String[]{"cn"});
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        DirContext userContext = getUserContext(name, password);
        if (userContext == null) {
            return false;
        }
        NamingEnumeration results = userContext.search(
                "cn=" + role + ",ou=Security,ou=Group",
                "(&(memberUid=" + extractMemberUid(name) + "))",
                controls
        );
        return results.hasMore();
    }

    private DirContext getUserContext(String login, String password) {
        try {
            return getContextImpl(login + "," + ldapBaseDn, password);
        } catch (NamingException e) {
            return null;
        }
    }

    private DirContext getContextImpl(String user, String password) throws NamingException {
        Hashtable<String, String> env = new Hashtable<String, String>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapDirectory + (ldapBaseDn == null ? "" : "/" + ldapBaseDn));
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        if (user != null) {
            env.put(Context.SECURITY_PRINCIPAL, user);
        }

        if (password != null) {
            env.put(Context.SECURITY_CREDENTIALS, password);
        }

        env.put("com.sun.jndi.ldap.connect.timeout", String.valueOf(ldapConnectionTimeout));

        return new InitialDirContext(env);
    }

    private String extractMemberUid(String loginDn) {
        String[] groups = loginDn.split(",");
        for (String group : groups) {
            if (group.startsWith("uid=")) {
                return group.substring(4);
            }
        }
        return "";
    }
}


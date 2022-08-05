package com.foros.session.security;

import com.foros.config.ConfigParameters;
import com.foros.config.ConfigService;
import com.foros.model.security.UserRole;
import com.foros.util.NameValuePair;
import com.foros.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import org.apache.commons.collections.iterators.EnumerationIterator;

@Stateless(name = "LdapService")
public class LdapServiceBean implements LdapService {

    private static final Logger logger = Logger.getLogger(LdapServiceBean.class.getName());

    @EJB
    private ConfigService configService;

    @Override
    public List<NameValuePair<String, String>> findDnsForRole(UserRole role) {
        if (role == null || StringUtil.isPropertyEmpty(role.getLdapDn())) {
            return Collections.emptyList();
        }

        DirContext context = getSearchContext();

        try {

            return findDns(role, context);

        } catch (NamingException ex) {
            throw new EJBException("Unable to list free DNs", ex);
        } finally {
            try {
                if (context != null) {
                    context.close();
                }
            } catch (NamingException ex) {
                ex.printStackTrace();
            }
        }
    }

    private List<NameValuePair<String, String>> findDns(UserRole role, DirContext context) throws NamingException {
        SearchControls searchControls = createSearchControls();

        String ldapTree = LDAP_UID + "=" + escapeDN(role.getLdapDn()) + ",ou=Security,ou=Group";

        if (!isNodePresent(ldapTree, context)) {
            logger.log(Level.INFO, "LDAP path={0} not found", ldapTree);
            return Collections.emptyList();
        }

        Attribute uidAttributes = context.getAttributes(ldapTree).get("memberUid");

        if (uidAttributes == null) {
            return Collections.emptyList();
        }

        List<NameValuePair<String, String>> result = findMembers(searchControls, context, uidAttributes);

        Collections.sort(result, NameValuePair.nameValuePairComparatorByName);

        return result;
    }

    private SearchControls createSearchControls() {
        SearchControls searchControls = new SearchControls();

        searchControls.setReturningAttributes(new String[]{
                LDAP_LOGIN,
                LDAP_LASTNAME,
                LDAP_FIRSTNAME
        });

        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        return searchControls;
    }

    private List<NameValuePair<String, String>> findMembers(SearchControls searchControls, DirContext context, Attribute uidAttributes) throws NamingException {
        List<NameValuePair<String, String>> result = new ArrayList<NameValuePair<String, String>>();

        for (String uid : new AttributeIterable(uidAttributes)) {

            if (uid != null) {
                Iterable<SearchResult> results = searchByUid(searchControls, context, uid);

                for (SearchResult record : results) {
                    record.setRelative(true);

                    Attributes userAttrs = record.getAttributes();

                    String lastName = getAttributeValue(userAttrs, LDAP_LASTNAME);
                    String firstName = getAttributeValue(userAttrs, LDAP_FIRSTNAME);

                    result.add(createMemberInfo(record, lastName, firstName));
                }
            }
        }

        return result;
    }

    private Iterable<SearchResult> searchByUid(SearchControls sc, DirContext ctx, String uid) throws NamingException {
        NamingEnumeration<SearchResult> searchResult = ctx.search(LDAP_USERBASEDN, "(&(objectclass=person)&(active=TRUE)&(uid=" + uid + "))", sc);
        return new IterableIterator<SearchResult>(new EnumerationIterator(searchResult));
    }

    private NameValuePair<String, String> createMemberInfo(SearchResult record, String lastName, String firstName) {
        String name = (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
        String value = record.getName() + (LDAP_USERBASEDN.trim().equals("") ? "" : ",") + LDAP_USERBASEDN;

        return new NameValuePair<String, String>(name, value);
    }

    private String getAttributeValue(Attributes attributes, String attributeName) throws NamingException {
        return attributes.get(attributeName) == null ? "" : (String) attributes.get(attributeName).get();
    }

    private DirContext getSearchContext() {
        try {
            return getContextImpl(null, null);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }

    private DirContext getUserContext(String login, String password) {
        String base = configService.get(ConfigParameters.LDAP_BASE_DN);
        try {
            return getContextImpl(login + "," + base, password);
        } catch (NamingException e) {
            return null;
        }
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

    private String extractMemberUid(String loginDn) {
        String[] groups = loginDn.split(",");
        for (String group : groups) {
            if (group.startsWith("uid=")) {
                return group.substring(4);
            }
        }
        return "";
    }

    @Override
    public Attributes getAttrsByDn(String dn) {
        if (StringUtil.isPropertyNotEmpty(dn)) {
            DirContext ctx = getSearchContext();
            try {
                if (isNodePresent(dn, ctx)) {
                    return ctx.getAttributes(dn);
                }
            } catch (NamingException ex) {
                throw new EJBException("Unable to list free DNs", ex);
            } finally {
                try {
                    if (ctx != null) {
                        ctx.close();
                    }
                } catch (NamingException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return null;
    }

    private boolean isNodePresent(String nodePath, DirContext targetContext) {
        try {
            targetContext.getAttributes(nodePath);
            return true;
        } catch (NamingException e) {
            return false;
        }
    }

    /* From https://www.owasp.org/index.php/Preventing_LDAP_Injection_in_Java */
    private String escapeDN(String name) {
        StringBuilder sb = new StringBuilder();
        if ((name.length() > 0) && ((name.charAt(0) == ' ') || (name.charAt(0) == '#'))) {
            sb.append('\\'); // add the leading backslash if needed
        }
        for (int i = 0; i < name.length(); i++) {
            char curChar = name.charAt(i);
            switch (curChar) {
                case '\\':
                    sb.append("\\\\");
                    break;
                case ',':
                    sb.append("\\,");
                    break;
                case '+':
                    sb.append("\\+");
                    break;
                case '"':
                    sb.append("\\\"");
                    break;
                case '<':
                    sb.append("\\<");
                    break;
                case '>':
                    sb.append("\\>");
                    break;
                case ';':
                    sb.append("\\;");
                    break;
                default:
                    sb.append(curChar);
            }
        }
        if ((name.length() > 1) && (name.charAt(name.length() - 1) == ' ')) {
            sb.insert(sb.length() - 1, '\\'); // add the trailing backslash if needed
        }
        return sb.toString();
    }

    private DirContext getContextImpl(String user, String password) throws NamingException {
        String directory = configService.get(ConfigParameters.LDAP_DIRECTORY);
        String base = configService.get(ConfigParameters.LDAP_BASE_DN);
        Long ldapConnectionTimeout = configService.get(ConfigParameters.LDAP_CONNECTION_TIMEOUT);

        Hashtable<String, String> env = new Hashtable<String, String>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, directory + (base == null ? "" : "/" + base));
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

    private static class AttributeIterable implements Iterable<String> {

        private Attribute attribute;

        private AttributeIterable(Attribute attribute) {
            this.attribute = attribute;
        }

        @Override
        public Iterator<String> iterator() {
            try {
                return new EnumerationIterator(attribute.getAll());
            } catch (NamingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class IterableIterator<T> implements Iterable<T> {

        private Iterator<T> iterator;

        private IterableIterator(Iterator<T> iterator) {
            this.iterator = iterator;
        }

        @Override
        public Iterator<T> iterator() {
            return iterator;
        }
    }
}

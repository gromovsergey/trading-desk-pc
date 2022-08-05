package com.foros.security.principal;

import com.foros.security.AccountRole;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class SecurityPrincipal extends AbstractAuthenticationToken implements Serializable, ApplicationPrincipal {

    private final String token;

    private final Long userCredentialId;
    private final Long userId;
    private final Long accountId;
    private final Long userRoleId;
    private final Long accountRoleId;
    private final String name;
    private final String remoteUserIP;

    public SecurityPrincipal(String token, String name, Long userId, Long userCredentialId, Long accountId, Long userRoleId,
                             Long accountRoleId, String remoteUserIP) {
        super(createAuthorityByGroups(accountRoleId));
        super.setAuthenticated(true);
        this.token = token;
        this.name = name;
        this.userId = userId;
        this.userCredentialId = userCredentialId;
        this.accountId = accountId;
        this.userRoleId = userRoleId;
        this.accountRoleId = accountRoleId;
        this.remoteUserIP = remoteUserIP;
    }

    public boolean isAnonymous() {
        return userId == null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getToken() {
        return token;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public Long getUserRoleId() {
        return userRoleId;
    }

    public Long getAccountRoleId() {
        return accountRoleId;
    }

    public String getRemoteUserIP() {
        return remoteUserIP;
    }

    public Long getUserCredentialId() {
        return userCredentialId;
    }

    private static Collection<? extends GrantedAuthority> createAuthorityByGroups(Long accountRoleId) {
        if (accountRoleId == null) {
            return Collections.emptyList();
        }

        String group = AccountRole.valueOf(accountRoleId.intValue()).name();
        return Collections.singleton(new SimpleGrantedAuthority(group));
    }

    @Override
    public Object getCredentials() {
        return userCredentialId;
    }

    @Override
    public Object getPrincipal() {
        return name;
    }

    @Override
    public int hashCode() {
        return token != null ? token.hashCode() : 0;
    }

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        
        if (other instanceof SecurityPrincipal) {
            SecurityPrincipal otherSp = (SecurityPrincipal) other;

            return this.token.equals(otherSp.token);
        }

        return false;
    }


}

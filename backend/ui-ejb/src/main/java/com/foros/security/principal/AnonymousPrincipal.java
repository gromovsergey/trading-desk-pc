package com.foros.security.principal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class AnonymousPrincipal extends AnonymousAuthenticationToken implements ApplicationPrincipal {

    public static final String ANONYMOUS = "ANONYMOUS";

    private String remoteAddress;

    public AnonymousPrincipal(String remoteAddress) {
        super("anonymous", "anonymous", Arrays.asList(new SimpleGrantedAuthority(ANONYMOUS)));
        this.remoteAddress = remoteAddress;
    }

    @Override
    public String getToken() {
        return null;
    }

    @Override
    public Long getUserId() {
        return null;
    }

    @Override
    public Long getUserCredentialId() {
        return null;
    }

    @Override
    public Long getAccountId() {
        return null;
    }

    @Override
    public String getRemoteUserIP() {
        return remoteAddress;
    }

    @Override
    public Long getUserRoleId() {
        return null;
    }

    @Override
    public Long getAccountRoleId() {
        return null;
    }

    @Override
    public boolean isAnonymous() {
        return true;
    }
}

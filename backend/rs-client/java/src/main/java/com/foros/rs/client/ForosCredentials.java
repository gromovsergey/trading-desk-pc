package com.foros.rs.client;

import java.security.Principal;
import javax.crypto.SecretKey;
import org.apache.http.auth.BasicUserPrincipal;
import org.apache.http.auth.Credentials;

public class ForosCredentials implements Credentials {
    private final Principal principal;
    private final SecretKey key;

    public ForosCredentials(String token, SecretKey key) {
        this.principal = new BasicUserPrincipal(token);
        this.key = key;
    }

    @Override
    public Principal getUserPrincipal() {
        return principal;
    }

    @Override
    public String getPassword() {
        return null;
    }

    public SecretKey getKey() {
        return key;
    }
}

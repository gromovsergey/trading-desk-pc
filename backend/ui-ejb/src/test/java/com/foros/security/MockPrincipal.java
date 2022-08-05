package com.foros.security;

import com.foros.security.principal.SecurityPrincipal;


public class MockPrincipal extends SecurityPrincipal {

    public MockPrincipal() {
        super(null, null, null, null, null, null, null, null);
    }

    public MockPrincipal(String name, Long userId, Long accountId, Long userRoleId, Long accountRoleId) {
        super(null, name, userId, null, accountId, userRoleId, accountRoleId, "127.0.0.1");
    }

    @Override
    public boolean isAnonymous() {
        return false;
    }

    @Override
    public Long getUserCredentialId() {
        throw new UnsupportedOperationException("getUserCredentialId is not supported in mock");
    }
}

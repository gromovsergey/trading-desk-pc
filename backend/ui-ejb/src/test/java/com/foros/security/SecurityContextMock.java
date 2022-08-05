package com.foros.security;

import com.foros.model.security.User;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.PrincipalProvider;
import com.foros.security.principal.SecurityContext;
import com.foros.security.principal.SecurityPrincipal;

/**
 * Mock for SecurityContext.
 * Allows to provide custom user for security checks.
 */
public class SecurityContextMock {

    private static SecurityContextMock mockInstance = new SecurityContextMock();

    private PrincipalProviderMock principalProvider = new PrincipalProviderMock();
    
    private SecurityContextMock() {
        SecurityContext.setPrincipalProvider(principalProvider);
    }

    /**
     * You should run SecurityContextMock.getInstance().tearDown() after end of test
     * to clear security context for other tests
     */
    public static SecurityContextMock getInstance() {
        return mockInstance;
    }

    public void tearDown() {
        principalProvider.setPrincipal(null);
    }

    public SecurityPrincipal getPrincipal() {
        return (SecurityPrincipal) principalProvider.getPrincipal();
    }

    public void setPrincipal(ApplicationPrincipal principal) {
        this.principalProvider.setPrincipal(principal);
    }

    public void setPrincipal(User user) {
        setPrincipal(new SecurityPrincipal(
                null,
                null,
                user.getId(),
                user.getUserCredential() != null ? user.getUserCredential().getId() : null,
                user.getAccount().getId(),
                user.getRole().getId(),
                (long) user.getAccount().getRole().getId(),
                "127.0.0.1"
        ));
    }

    private class PrincipalProviderMock implements PrincipalProvider {

        private ApplicationPrincipal principal;

        @Override
        public void setPrincipal(ApplicationPrincipal principal) {
            this.principal = principal;
        }

        @Override
        public ApplicationPrincipal getPrincipal() {
            return principal;
        }

    }
}

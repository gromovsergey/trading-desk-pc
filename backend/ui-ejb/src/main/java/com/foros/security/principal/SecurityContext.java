package com.foros.security.principal;

import com.foros.security.AccountRole;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityContext {

    private static PrincipalProvider principalProvider = new SpringSecurityPrincipalProvider();

    protected SecurityContext() {
    }

    public static void setPrincipalProvider(PrincipalProvider principalProvider) {
        SecurityContext.principalProvider = principalProvider;
    }

    public static ApplicationPrincipal getPrincipal() {
        return principalProvider.getPrincipal();
    }

    public static boolean isAuthenticatedAndNotAnonymous() {
        ApplicationPrincipal principal = getPrincipal();
        return principal != null && !principal.isAnonymous();
    }

    public static void setPrincipal(ApplicationPrincipal principal) {
        principalProvider.setPrincipal(principal);
    }

    public static boolean isUserInRole(AccountRole role) {
        return isUserInOneOfRoles(role);
    }

    public static boolean isInternal() {
        return isUserInRole(AccountRole.INTERNAL);
    }

    public static boolean isAgency() {
        return isUserInRole(AccountRole.AGENCY);
    }

    public static boolean isAdvertiser() {
        return isUserInRole(AccountRole.ADVERTISER);
    }

    public static boolean isAgencyOrAdvertiser() {
        return isUserInOneOfRoles(AccountRole.AGENCY, AccountRole.ADVERTISER);
    }

    public static boolean isPublisher() {
        return isUserInRole(AccountRole.PUBLISHER);
    }

    public static boolean isCmp() {
        return isUserInRole(AccountRole.CMP);
    }

    public static boolean isIsp() {
        return isUserInRole(AccountRole.ISP);
    }

    public static AccountRole getAccountRole() {
        return AccountRole.valueOf(getPrincipal().getAccountRoleId().intValue());
    }

    public static boolean isUserInOneOfRoles(AccountRole... roles) {
        ApplicationPrincipal principal = getPrincipal();

        if (principal == null || principal.getAccountRoleId()==null) {
            return false;
        }

        for (AccountRole role : roles) {
            if (principal.getAccountRoleId() == role.ordinal()) {
                return true;
            }
        }

        return false;
    }

    public static void clearPrincipal() {
        principalProvider.setPrincipal(null);
    }

    private static class SpringSecurityPrincipalProvider implements PrincipalProvider {
        @Override
        public ApplicationPrincipal getPrincipal() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof ApplicationPrincipal) {
                return (ApplicationPrincipal) authentication;
            }

            return null;
        }

        @Override
        public void setPrincipal(ApplicationPrincipal principal) {
            SecurityContextHolder.getContext().setAuthentication(principal);
        }
    }
}

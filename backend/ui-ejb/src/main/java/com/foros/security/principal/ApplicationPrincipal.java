package com.foros.security.principal;

import org.springframework.security.core.Authentication;

public interface ApplicationPrincipal extends Authentication, Tokenable {

    Long getUserId();

    Long getUserCredentialId();

    Long getAccountId();

    String getRemoteUserIP();

    Long getUserRoleId();

    Long getAccountRoleId();

    boolean isAnonymous();

}

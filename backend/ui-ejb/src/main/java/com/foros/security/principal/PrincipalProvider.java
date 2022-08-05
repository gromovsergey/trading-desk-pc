package com.foros.security.principal;

public interface PrincipalProvider {

    ApplicationPrincipal getPrincipal();

    void setPrincipal(ApplicationPrincipal principal);

}

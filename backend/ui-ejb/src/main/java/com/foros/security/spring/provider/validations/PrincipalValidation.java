package com.foros.security.spring.provider.validations;

import com.foros.security.principal.ApplicationPrincipal;
import javax.servlet.http.HttpServletRequest;

public interface PrincipalValidation {

    boolean isValid(HttpServletRequest request, ApplicationPrincipal principal);

}

package com.foros.security.spring.provider.validations;

import com.foros.security.principal.ApplicationPrincipal;

import javax.servlet.http.HttpServletRequest;

public abstract class PrincipalValidations {

    public static PrincipalValidation constantIpValidation() {
        return new PrincipalValidation() {
            @Override
            public boolean isValid(HttpServletRequest request, ApplicationPrincipal principal) {
                return request.getRemoteAddr().equals(principal.getRemoteUserIP());
            }
        };
    }

}

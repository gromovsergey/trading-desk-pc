package com.foros.web.security;

import com.foros.security.AccountRole;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.security.spring.ForosAccessDeniedHandler;
import com.foros.session.security.UserService;
import com.foros.util.ContextUrlHelper;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

public class UIAccessDeniedHandler implements AccessDeniedHandler {

    private final AccessDeniedHandler defaultHandler = new ForosAccessDeniedHandler();

    @Autowired
    private AuthenticationEntryPoint entryPoint;

    @Autowired
    private UserService userService;


    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (canBeSwitched(request)) {
            defaultHandler.handle(request, response, accessDeniedException);
        } else {
            entryPoint.commence(request, response, new AccessDeniedAuthenticationException(accessDeniedException));
        }
    }

    private boolean canBeSwitched(HttpServletRequest request) {
        if (SecurityContext.isInternal()) {
            return false;
        }

        Set<AccountRole> contextAccountRoles = ContextUrlHelper.getContextAccountRoles(request.getRequestURI());
        if (contextAccountRoles.equals(Collections.singleton(AccountRole.INTERNAL))) {
            return false;
        }

        ApplicationPrincipal principal = SecurityContext.getPrincipal();
        if (principal == null || principal.isAnonymous()) {
            return false;
        }

        Set<AccountRole> switchableRoles = userService.findSwitchableRoles(principal.getUserId());
        if (switchableRoles.size() <= 1) {
            return false;
        }

        return !CollectionUtils.intersection(contextAccountRoles, switchableRoles).isEmpty();
    }
}

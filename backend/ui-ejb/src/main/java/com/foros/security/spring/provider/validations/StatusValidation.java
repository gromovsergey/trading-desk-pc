package com.foros.security.spring.provider.validations;

import com.foros.model.Status;
import com.foros.model.security.User;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.session.security.UserService;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;

public class StatusValidation implements PrincipalValidation {

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(HttpServletRequest request, ApplicationPrincipal principal) {
        try {
            User user = userService.find(principal.getUserId());
            return Status.INACTIVE != user.getStatus()
                    && Status.INACTIVE != user.getAccount().getStatus();
        } catch (EntityNotFoundException e) {
            return false;
        }
    }
}

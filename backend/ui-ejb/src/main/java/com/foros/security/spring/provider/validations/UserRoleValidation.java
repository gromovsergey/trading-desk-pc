package com.foros.security.spring.provider.validations;

import com.foros.model.security.User;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.session.security.UserService;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;


public class UserRoleValidation implements PrincipalValidation {

    @Autowired
    private UserService userService;

    @Override
    public boolean isValid(HttpServletRequest request, ApplicationPrincipal principal) {
        try {
            User user = userService.find(principal.getUserId());
            return user.getRole().getId().equals(principal.getUserRoleId());
        } catch (EntityNotFoundException exception) {
            return false;
        }
    }

}

package com.foros.web.filters;

import static com.foros.util.UIConstants.CONTEXT_REQUEST_PARAMETER;
import com.foros.config.ConfigService;
import com.foros.framework.PWSHelper;
import com.foros.model.security.User;
import com.foros.security.currentuser.CurrentUserSettingsHolder;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;
import com.foros.session.security.UserService;
import com.foros.util.LocaleHelper;
import com.foros.util.context.ContextUtil;
import com.foros.util.context.SessionContexts;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

public class EnvironmentFilter extends OncePerRequestFilter {

    @Autowired
    private ConfigService configService;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        initializeRequestEnvironment(request);

        filterChain.doFilter(request, response);

        SessionContexts.detachContext(request);
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return false;
    }

    private void initializeRequestEnvironment(HttpServletRequest request) {
        ApplicationPrincipal principal = SecurityContext.getPrincipal();
        CurrentUserSettingsHolder.Settings settings = CurrentUserSettingsHolder.getSettings();

        request.setAttribute("_principal", principal);
        request.setAttribute("_userSettings", settings);

        if (principal != null && !principal.isAnonymous()) {
            List<User> switchableUsers = userService.findSwitchableUsers(principal.getUserCredentialId());

            UserAndSwitchableUsers userAndSwitchableUsers = findActiveAndSwitchableUsers(switchableUsers, principal.getUserId());

            request.setAttribute("_user", userAndSwitchableUsers.getUser());
            request.setAttribute("_switchableUsers", userAndSwitchableUsers.getSwitchableUsers());
            request.setAttribute(CONTEXT_REQUEST_PARAMETER, ContextUtil.getRolePath());
        }

        LocaleHelper.setLocale(settings.getLocale(), request);

        String sessionToken = PWSHelper.getSessionToken(request.getSession(false));

        if (sessionToken == null) {
            PWSHelper.saveToken(request);
        }

    }

    private UserAndSwitchableUsers findActiveAndSwitchableUsers(List<User> users, Long userId) {
        User currentUser = null;

        ArrayList<User> result = new ArrayList<User>();

        for (User user : users) {
            if (user.getId().equals(userId)) {
                currentUser = user;
            } else {
                result.add(user);
            }
        }

        if (currentUser == null) {
            throw new IllegalStateException("Switchable users must contains active user");
        }

        Collections.sort(result, new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getAccount().getName().compareTo(o2.getAccount().getName());
            }
        });

        return new UserAndSwitchableUsers(currentUser, result);
    }

    private static class UserAndSwitchableUsers {
        private User user;
        private List<User> switchableUsers;

        private UserAndSwitchableUsers(User user, List<User> switchableUsers) {
            this.user = user;
            this.switchableUsers = switchableUsers;
        }

        private User getUser() {
            return user;
        }

        private List<User> getSwitchableUsers() {
            return switchableUsers;
        }
    }
}

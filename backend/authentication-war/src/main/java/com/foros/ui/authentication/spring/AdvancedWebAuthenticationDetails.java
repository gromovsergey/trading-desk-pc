package com.foros.ui.authentication.spring;

import com.foros.security.spring.LastSwitchedUserHelper;
import javax.servlet.http.HttpServletRequest;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class AdvancedWebAuthenticationDetails extends WebAuthenticationDetails {

    private Long lastUsedUserId;

    public AdvancedWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        this.lastUsedUserId = LastSwitchedUserHelper.getLastSwitchedUser(request);
    }

    public Long getLastUsedUserId() {
        return lastUsedUserId;
    }

}

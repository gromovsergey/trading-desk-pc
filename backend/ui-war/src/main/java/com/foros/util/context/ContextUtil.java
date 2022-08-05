package com.foros.util.context;

import com.foros.security.AccountRole;
import com.foros.security.principal.SecurityContext;

public class ContextUtil {
    private ContextUtil() {
    }

    public static String getRolePath() {
        if (!SecurityContext.isAuthenticatedAndNotAnonymous()) {
            return null;
        }

        AccountRole role = SecurityContext.getAccountRole();

        switch (role) {
            case INTERNAL:
                return "/admin";
            case ADVERTISER:
            case AGENCY:
                return "/advertiser";
            case PUBLISHER:
                return "/publisher";
            case ISP:
                return "/isp";
            case CMP:
                return "/cmp";
            default:
                throw new IllegalStateException("Unknown account role " + role);
        }
    }
}

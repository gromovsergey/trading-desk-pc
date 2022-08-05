package com.foros.action.user;

import com.foros.model.security.User;

public abstract class ExternalUserBreadcrumbsElementBase extends UserBreadcrumbsElement {
    public ExternalUserBreadcrumbsElementBase(User user, String basePath) {
        super(user, "account.breadcrumbs.user", buildPath(user,basePath));
    }

    private static String buildPath(User user, String basePath) {
        StringBuilder path = new StringBuilder(basePath);
        switch (user.getAccount().getRole()) {
            case ADVERTISER:
            case AGENCY:
                path.append("advertiser");
                break;
            case PUBLISHER:
                path.append("publisher");
                break;
            case ISP:
                path.append("isp");
                break;
            case CMP:
                path.append("cmp");
        }
        return path.append("View").toString();
    }
}

package com.foros.action.user;

import com.foros.framework.ReadOnly;
import com.foros.framework.support.RequestContextsAware;
import com.foros.model.account.Account;
import com.foros.util.context.RequestContexts;

public class ViewUserAction extends UserActionSupport implements RequestContextsAware {

    private Long id;

    @ReadOnly
    public String view() {
        user = userService.view(id);
        return SUCCESS;
    }

    @ReadOnly
    public String selectView() {
        user = userService.view(id);
        Account account = user.getAccount();
        switch (account.getRole()) {
            case ADVERTISER:
            case AGENCY:
                return "success.advertiser";
            case ISP:
                return "success.isp";
            case PUBLISHER:
                return "success.publisher";
            case CMP:
                return "success.cmp";
            default:
                return "success.internal";
        }

    }

    @Override
    public void switchContext(RequestContexts contexts) {
        contexts.switchTo(user.getAccount());
    }

    public void setId(Long id) {
        this.id = id;
    }
}

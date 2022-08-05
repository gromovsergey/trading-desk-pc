package com.foros.framework;

import com.foros.framework.support.AdvertiserSelfIdAware;
import com.foros.framework.support.AgencySelfIdAware;
import com.foros.framework.support.CmpSelfIdAware;
import com.foros.framework.support.IspSelfIdAware;
import com.foros.framework.support.PublisherSelfIdAware;
import com.foros.security.AccountRole;
import com.foros.security.principal.ApplicationPrincipal;
import com.foros.security.principal.SecurityContext;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;

public class SelfIdentifierInterceptor extends AbstractInterceptor {

    public String intercept(ActionInvocation invocation) throws Exception {
        Object action = invocation.getAction();

        if (SecurityContext.isAuthenticatedAndNotAnonymous()) {
            AccountRole accountRole = SecurityContext.getAccountRole();

            if (accountRole != AccountRole.INTERNAL) {
                Long accountId = SecurityContext.getPrincipal().getAccountId();

                if (accountRole == AccountRole.AGENCY && action instanceof AgencySelfIdAware) {
                    ((AgencySelfIdAware) action).setAgencyId(accountId);
                }

                if (accountRole == AccountRole.ADVERTISER && action instanceof AdvertiserSelfIdAware) {
                    ((AdvertiserSelfIdAware) action).setAdvertiserId(accountId);
                }

                if (accountRole == AccountRole.PUBLISHER && action instanceof PublisherSelfIdAware) {
                    ((PublisherSelfIdAware) action).setPublisherId(accountId);
                }

                if (accountRole == AccountRole.ISP && action instanceof IspSelfIdAware) {
                    ((IspSelfIdAware) action).setIspId(accountId);
                }

                if (accountRole == AccountRole.CMP && action instanceof CmpSelfIdAware) {
                    ((CmpSelfIdAware) action).setCmpId(accountId);
                }
            
            }

        }
        return invocation.invoke();
    }

}

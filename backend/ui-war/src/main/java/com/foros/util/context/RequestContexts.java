package com.foros.util.context;

import com.foros.model.account.Account;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.ObjectUtils;

public class RequestContexts extends Contexts {
    private static final String REQUEST_CONTEXT_KEY = "requestContexts";

    private boolean standalone = false;
    private SessionContexts sessionContexts;
    private ContextBase activeContext;

    public RequestContexts(SessionContexts sessionContexts) {
        this.sessionContexts = sessionContexts;
        
        getAdvertiserContext().setListener(new AdvertiserContextListener());
        getIspContext().setListener(new IspContextListener());
        getPublisherContext().setListener(new PublisherContextListener());
        getCmpContext().setListener(new CmpContextListener());
    }

    public boolean isStandalone() {
        return standalone;
    }

    public void setStandalone(boolean standalone) {
        this.standalone = standalone;
    }

    public static RequestContexts getRequestContexts(HttpServletRequest request) {
        RequestContexts contexts = (RequestContexts) request.getAttribute(REQUEST_CONTEXT_KEY);
        if (contexts == null) {
            contexts = createRequestContext(request);
            request.setAttribute(REQUEST_CONTEXT_KEY, contexts);
        }
        return contexts;
    }

    @SuppressWarnings({"unchecked"})
    public void switchTo(Account account) {
        ContextBase context = getContext(account.getRole());
        if (context != null) {
            context.switchTo(account);
        }
    }

    public boolean isSet() {
        return activeContext != null && activeContext.isSet();
    }

    private static RequestContexts createRequestContext(HttpServletRequest request) {
        RequestContexts requestContext = new RequestContexts(SessionContexts.getSessionContexts(request));
        requestContext.switchToDefaults();
        return requestContext;
    }

    private abstract class SwitchContextListenerBase implements SwitchContextListener {

        private Long lastAccountId;

        @Override
        public final void onSwitchTo(ContextBase context) {
            if (standalone) {
                // session context should not be changed on displaying standalone page
                return;
            }

            if (activeContext != null && activeContext != context) {
                throw new RuntimeException("Another context already switched for this request");
            }

            chechCanBeSwitched(context);

            updateSessionContext(context);

            lastAccountId = context.getAccountId();
            activeContext = context;
        }

        protected void chechCanBeSwitched(ContextBase context) {
            if (lastAccountId != null && !ObjectUtils.equals(lastAccountId, context.getAccountId())) {
                throw new RuntimeException("Request context was already switched to different account");
            }
        }

        protected abstract void updateSessionContext(ContextBase context);
    }

    private class IspContextListener extends SwitchContextListenerBase {
        @Override
        public void updateSessionContext(ContextBase context) {
            sessionContexts.getIspContext().switchTo(context.getAccountId());
        }
    }

    private class PublisherContextListener extends SwitchContextListenerBase {
        @Override
        public void updateSessionContext(ContextBase context) {
            sessionContexts.getPublisherContext().switchTo(context.getAccountId());
        }
    }

    private class CmpContextListener extends SwitchContextListenerBase {
        @Override
        public void updateSessionContext(ContextBase context) {
            sessionContexts.getCmpContext().switchTo(context.getAccountId());
        }
    }

    private class AdvertiserContextListener extends SwitchContextListenerBase {
        private Long lastAgencyAdvertiserId;

        @Override
        protected void chechCanBeSwitched(ContextBase context) {
            super.chechCanBeSwitched(context);
            AdvertiserContext requestContext = (AdvertiserContext) context;

            if (lastAgencyAdvertiserId == null || !requestContext.isAgencyAdvertiserSet()) {
                return;
            }

            if (!ObjectUtils.equals(lastAgencyAdvertiserId, requestContext.getAgencyAdvertiserId())) {
                throw new RuntimeException("Agency advertser was aleady switched");
            }
        }

        @Override
        public void updateSessionContext(ContextBase context) {
            AdvertiserContext requestContext = (AdvertiserContext) context;
            AdvertiserContext sessionContext = sessionContexts.getAdvertiserContext();

            // do nothing if session is already switched to request account id and agency advertiser doesn't set
            // it helps us to keep switched agency advertiser in session context
            if (!(sessionContext.isSwitchedTo(requestContext.getAccountId()) && !requestContext.isAgencyAdvertiserSet())) {
                sessionContexts.setAdvertiserContext(Contexts.clone((AdvertiserContext) context));
            }

            if (requestContext.isAgencyAdvertiserSet()) {
                lastAgencyAdvertiserId = requestContext.getAgencyAdvertiserId();
            }
        }
    }
}

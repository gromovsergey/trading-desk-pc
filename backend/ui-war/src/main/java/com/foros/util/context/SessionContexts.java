package com.foros.util.context;

import com.foros.security.AccountRole;

import java.io.Serializable;
import javax.servlet.http.HttpServletRequest;

public class SessionContexts extends Contexts implements Serializable {
    private static final String SESSION_CONTEXT_KEY = "foros.context.Contexts.session";

    public SessionContexts() {
        setAdvertiserContext(new AdvertiserContext());
        setIspContext(new IspContext());
        setPublisherContext(new PublisherContext());
    }

    public static SessionContexts getSessionContexts(HttpServletRequest request) {
        SessionContexts contexts = (SessionContexts) request.getAttribute(SESSION_CONTEXT_KEY);

        if (contexts == null) {
            contexts = createContexts(request);
            request.setAttribute(SESSION_CONTEXT_KEY, contexts);
        }

        return contexts;
    }

    public static void detachContext(HttpServletRequest request) {
        Contexts requestContext = (Contexts) request.getAttribute(SESSION_CONTEXT_KEY);
        if (requestContext == null) {
            return;
        }
        request.getSession().setAttribute(SESSION_CONTEXT_KEY, requestContext);
        request.setAttribute(SESSION_CONTEXT_KEY, null);
    }

    private static SessionContexts createContexts(HttpServletRequest request) {
        SessionContexts contexts = clone((SessionContexts) request.getSession().getAttribute(SESSION_CONTEXT_KEY));
        
        if (contexts == null) {
            contexts = new SessionContexts();
            contexts.switchToDefaults();
        }

        return contexts;
    }

    public void clear(AccountRole role) {
        ContextBase context = getContext(role);

        if (context != null) {
            context.clear();
        }
    }
}

package app.programmatic.ui.common.interceptor;

import app.programmatic.ui.authorization.service.AuthorizationServiceConfigurator;
import app.programmatic.ui.common.foros.service.ForosServiceConfigurator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class AuthorizationInterceptor implements HandlerInterceptor {
    private static final String AUTH_HEADER = "Authorization";

    @Value("${usersession.timeoutInMinutes}")
    private long USER_SESSION_TIMEOUT_IN_MINUTES;

    @Autowired
    private ForosServiceConfigurator forosServiceConfigurator;

    @Autowired
    private AuthorizationServiceConfigurator authorizationServiceConfigurator;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (    request.getRequestURI() == null ||
                !request.getRequestURI().startsWith("/rest/") ||
                request.getRequestURI().startsWith("/rest/dynamicConfig/properties") ||
                request.getRequestURI().equals("/rest/login") ||
                request.getRequestURI().equals("/rest/invitation/contractRequest") ||
                request.getMethod().equals(HttpMethod.OPTIONS.name())) {
            authorizationServiceConfigurator.configureAnonymous(request.getRemoteAddr());
            return true;
        }

        String[] keyTokenArray = null;
        String keyTokenPair = request.getHeader(AUTH_HEADER);

        boolean authProvided = keyTokenPair != null;
        if (authProvided) {
            keyTokenArray = keyTokenPair.split(":");
            authProvided = keyTokenArray.length == 2;
        }

        if (authProvided) {
            authProvided = authorizationServiceConfigurator.configure(
                    keyTokenArray[0], request.getRemoteAddr(), USER_SESSION_TIMEOUT_IN_MINUTES)
                != null;
        }

        if (!authProvided) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        forosServiceConfigurator.configure(keyTokenArray[0], keyTokenArray[1]);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        authorizationServiceConfigurator.cleanUp();
        forosServiceConfigurator.cleanUp();
    }
}

package com.foros.action.context;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;
import com.foros.session.security.UserService;
import com.foros.util.context.RequestContexts;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

// todo!!!!!!!!!!!!!!!!!!!!!!!!
public class SwitchContextAction extends BaseActionSupport implements ServletRequestAware, ServletResponseAware {
    private String requestedURL;
    private Long userIdToSwitch;

    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;

    @EJB
    private UserService userService;


    @ReadOnly
    public String execute() {
        if (userIdToSwitch != null) {
            //SecurityContext.switchSSOUser(userIdToSwitch);
            //SSOSwitchHelper.saveLastSwitchedUser(httpResponse, userIdToSwitch);
            RequestContexts.getRequestContexts(httpRequest).switchTo(userService.find(userIdToSwitch).getAccount());

            if (requestedURL != null) {
                return SUCCESS;
            } else {
                return INPUT;
            }
        }

        return INPUT;
    }

    public void setRequestedURL(String requestedURL) {
        this.requestedURL = requestedURL;
    }

    public String getRequestedURL() {
        return requestedURL;
    }

    @Override
    public void setServletRequest(HttpServletRequest httpRequest) {
        this.httpRequest = httpRequest;
    }

    public Long getUserIdToSwitch() {
        return userIdToSwitch;
    }

    public void setUserIdToSwitch(Long userIdToSwitch) {
        this.userIdToSwitch = userIdToSwitch;
    }

    @Override
    public void setServletResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

}

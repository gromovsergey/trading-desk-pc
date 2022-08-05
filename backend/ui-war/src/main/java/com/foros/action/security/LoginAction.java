package com.foros.action.security;

import com.foros.action.BaseActionSupport;
import com.foros.framework.ReadOnly;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;

public class LoginAction extends BaseActionSupport implements ServletRequestAware, ServletResponseAware{
    private HttpServletRequest request;

    private HttpServletResponse response;

    @ReadOnly
    public String loginPage() {
        response.setStatus(401);
        return SUCCESS;
    }

    @ReadOnly
    public String loginError() {
        response.setStatus(401);
        request.setAttribute("WRONG_LOGIN","true");
        return SUCCESS;
    }

    @ReadOnly
    public String logout(){
        HttpSession session = request.getSession(true);
        session.invalidate();
        return SUCCESS;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }
}

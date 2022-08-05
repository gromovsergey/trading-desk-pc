package com.foros.action.excel;

import java.io.IOException;
import javax.servlet.GenericServlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class OpenFromExcelServlet extends GenericServlet {

    @Override
    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) req;
        String queryString = request.getQueryString();
        String path = request.getRequestURI().substring(6);
        req.setAttribute("target", path + (queryString == null ? "" : "?" + queryString));
        req.getRequestDispatcher("/redirect.jsp").forward(req, res);
    }
}

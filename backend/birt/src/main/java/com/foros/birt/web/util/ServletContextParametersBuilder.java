package com.foros.birt.web.util;

import javax.servlet.ServletContext;

public class ServletContextParametersBuilder {

    private ServletContext servletContext;

    public ServletContextParametersBuilder(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public ServletContextParametersBuilder add(String name, String value) {
        servletContext.setInitParameter(name, value);
        return this;
    }

}

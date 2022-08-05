package com.foros.util.customization;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.web.filter.GenericFilterBean;

public class CustomizationFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            CustomizationHelper.setCustomizationName(request);
            chain.doFilter(request, response);
        } finally {
            CustomizationHelper.clearCustomizationName();
        }
    }
}

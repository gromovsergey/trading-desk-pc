package com.foros.ui.authentication.controller;

import com.foros.security.spring.utils.RedirectStrategies;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ReturnController {

    private RedirectStrategy redirectStrategy = RedirectStrategies.createRelativeStrategy();

    @RequestMapping("j_authenticate")
    public void redirect(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(value = "j_return_url", required = false) String returnUrl) throws IOException {

        if (returnUrl == null || returnUrl.isEmpty()) {
            returnUrl = "/";
        }

        redirectStrategy.sendRedirect(request, response, returnUrl);
    }
}
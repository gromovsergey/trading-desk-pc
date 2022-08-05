package com.foros.ui.authentication.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @RequestMapping("/")
    public String index() {
        return "redirect:../";
    }

    @RequestMapping("/error404")
    public String error404(Model model) {
        model.addAttribute("errorCode", "404");
        return "error";
    }

    @RequestMapping("/error500")
    public String error500(Model model) {
        model.addAttribute("errorCode", "500");
        return "error";
    }

    @RequestMapping("/login")
    public String login() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            return index();
        }
        return "login";
    }

}

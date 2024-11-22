package com.projetpedagogique.pegagogicalplatform.Controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login/login";
    }


    @GetMapping("/logout")
    public String logout(HttpServletRequest request) throws ServletException {

        request.logout();
        return "redirect:/login";
    }
}

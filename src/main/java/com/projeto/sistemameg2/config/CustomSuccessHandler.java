package com.projeto.sistemameg2.config;



import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        var authorities = authentication.getAuthorities();
        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_GERENTE"))) {
            response.sendRedirect("/admin/dashboard");
        } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_VENDEDOR"))) {
            response.sendRedirect("/vendedor/dashboard");
        } else {
            response.sendRedirect("/");
        }
    }
}

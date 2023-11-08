package com.api.filter;


import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.api.entity.Consume;
import com.api.entity.Consume.ConsumeStatus;
import com.api.entity.UserRole;
import com.api.repository.ConsumeRepo;
import com.api.service.impl.JwtService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    ConsumeRepo userRepo;
    @Autowired
    private JwtService jwtService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        String redirectUrl = null;
        if (authentication.getPrincipal() instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

           
            String email = oidcUser.getEmail();
            String name = oidcUser.getFullName();

            String username = (email != null) ? email : oidcUser.getAttributes().get("login") + "@gmail.com";
            List<Consume> byEmail = userRepo.findByEmail(username);
            if (byEmail.isEmpty()) {
                Consume consume = new Consume();
                consume.setEmail(username);
                consume.setFirstName(name);
                consume.setMobileNumber(null);
                consume.setPassword(null);
                consume.setRole(UserRole.ROLE_USER);
                consume.setConsumeStatus(ConsumeStatus.Active);
                userRepo.save(consume);
            }
            String jwtToken = jwtService.generateToken(email, email);
           
            redirectUrl = "/auth-success?jwtToken=" + jwtToken;
        } else if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
            DefaultOAuth2User userDetails = (DefaultOAuth2User) authentication.getPrincipal();

         
            String email = (String) userDetails.getAttribute("email");
            String name = (String) userDetails.getAttribute("name");

      
            String username = (email != null) ? email : userDetails.getAttributes().get("login") + "@gmail.com";
            List<Consume> byEmail = userRepo.findByEmail(username);
            if (byEmail.isEmpty()) {
                Consume consume = new Consume();
                consume.setEmail(username);
                consume.setFirstName(name);
                consume.setPassword(null);
                consume.setMobileNumber(null);
                consume.setRole(UserRole.ROLE_USER);
                consume.setConsumeStatus(ConsumeStatus.Active);
                userRepo.save(consume);
            }
            String jwtToken = jwtService.generateToken(username, username);
           
            redirectUrl = "/auth-success?jwtToken=" + jwtToken;
        }

        new DefaultRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}

package com.api.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.api.filter.JwtAuthFilter;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  

    @Autowired
    AuthenticationSuccessHandler successHandler;

    @Bean
    public JwtAuthFilter authFilter() {
        return new JwtAuthFilter();
    }

    @Bean
    public UserInfoUserDetailsService userDetailsService() {
        return new UserInfoUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

       @Bean
  public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
      http.csrf((csrf) -> csrf.disable())   
      .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
          CorsConfiguration config = new CorsConfiguration();
          config.setAllowedOrigins(Arrays.asList("*"));
          config.setAllowedOriginPatterns(Arrays.asList("*"));
          config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH","*"));
          config.setAllowCredentials(true);
          config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type","*"));
          config.setMaxAge(3600L);
          return config;
      }))

         .authorizeHttpRequests(authorizeRequests ->
              authorizeRequests
                  .requestMatchers("/api/Consume/register", "/api/Consume/login", "/api/Consume/forgotpassword",
                		  "/api/Consume/resetpassword", "/register/oauth", "/github/**", "login/oauth2/**", "/auth-success/**").permitAll()
                  .requestMatchers( "/api/**").authenticated()
          )
          .sessionManagement(sessionManagement ->
              sessionManagement
                  .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
          )
      
          .addFilterBefore(authFilter(), UsernamePasswordAuthenticationFilter.class)
          .oauth2Login(oauth2Login ->
              oauth2Login
                  .successHandler(successHandler)
          );

      return http.build();
  }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("https://dailybooks.io/**","http://localhost:3021/**")); // Add only the allowed origin
//        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type","*"));
//        configuration.setAllowCredentials(true);
//
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    
//    @Bean
//    public CorsFilter corsFilter() {
//        return new CorsFilter(corsConfigurationSource());
//    }
}

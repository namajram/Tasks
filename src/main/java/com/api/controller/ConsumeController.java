package com.api.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.dto.AuthRequest;
import com.api.dto.Changepassword;
import com.api.dto.ConsumeDTO;
import com.api.dto.Email;
import com.api.dto.PasswordReset;
import com.api.dto.UserRoleDTO;
import com.api.entity.Consume;
import com.api.service.ConsumeService;
import com.api.service.impl.JwtService;

import jakarta.mail.MessagingException;

@RestController
  
public class ConsumeController {
	
	 private static final Logger logger = LoggerFactory.getLogger(ConsumeController.class);

    private JwtService jwtService;
    private AuthenticationManager authenticationManager;
    private ConsumeService ConsumeService;  

    
    public ConsumeController(JwtService jwtService, AuthenticationManager authenticationManager, ConsumeService ConsumeService) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.ConsumeService = ConsumeService;
    }
   
    
    
    @PostMapping("/api/Consume/login")
    public ResponseEntity<?> authenticateAndGetToken(@RequestBody AuthRequest authRequest) {
        try {
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getEmail(), authRequest.getPassword()));

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                String token = jwtService.generateToken(userDetails.getUsername(), userDetails.getUsername());
                return ResponseEntity.status(HttpStatus.ACCEPTED).body(token);
            } else {
                throw new UsernameNotFoundException("Invalid email or mobile number");
            }
        } catch (Exception e) {
            logger.error("Error in /api/Consume/login", e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @GetMapping("/api/Consume/page-sort")
    public ResponseEntity<?> getConsumesPageSort(
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "userId") String sortField,
            @RequestParam(defaultValue = "asc") String sortDirection) {
        try {
            List<Consume> Consumes = ConsumeService.getConsumersPageSort(pageNo, pageSize, sortField, sortDirection);
            if (Consumes.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Consumes);
        } catch (Exception e) {
            logger.error("Error in /api/Consume/page-sort", e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }
    
    @PostMapping("/api/Consume/register")
    public ResponseEntity<?> createConsume(@RequestBody ConsumeDTO ConsumeDTO) {
        try {
            Consume createdConsume = ConsumeService.createConsume(ConsumeDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdConsume);
        } catch (Exception e) {
            logger.error("Error in /api/Consume/register", e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @GetMapping("/auth-success")
    public ResponseEntity<?> handleAuthenticationSuccess(@RequestParam("jwtToken") String jwtToken,
                                                        Authentication authentication) {
        try {
            
            if (jwtToken == null) {
                if (authentication.getPrincipal() instanceof DefaultOAuth2User) {
                    DefaultOAuth2User userDetails = (DefaultOAuth2User) authentication.getPrincipal();
                    OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

                    String email = oidcUser.getEmail();

                    String jwtToken1 = jwtService.generateToken(email, email);
                    return ResponseEntity.ok(jwtToken1);
                } else {
                    throw new UsernameNotFoundException("Invalid Credentials");
                }
            }
            return ResponseEntity.ok(jwtToken);
        } catch (Exception e) {
            logger.error("Error in /auth-success", e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @PostMapping("/api/Consume/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                jwtService.revokeToken(token);
                return ResponseEntity.ok("Logged out successfully");
            }
            return ResponseEntity.badRequest().body("Invalid token");
        } catch (Exception e) {
            logger.error("Error in /api/Consume/logout", e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    
    @GetMapping("/api/Consume/all")
    public ResponseEntity<?> getAllConsumes() {
        try {
            List<Consume> Consumes = ConsumeService.getAllConsumers();
            if (Consumes.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Consumes);
        } catch (Exception e) {
            logger.error("Error in /api/Consume/all", e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @DeleteMapping("/api/Consume/{ConsumeId}")
    public ResponseEntity<?> deleteConsume(@PathVariable long ConsumeId) {
        try {
            boolean deleted = ConsumeService.deleteConsumer(ConsumeId);
            if (deleted) {
                return ResponseEntity.ok("Consume deleted successfully");
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error in /api/Consume/{ConsumeId}:"+ConsumeId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @GetMapping("/api/Consume/{ConsumeId}")
    public ResponseEntity<?> getConsumeById(@PathVariable long ConsumeId) {
        try {
            Consume Consume = ConsumeService.getConsumerById(ConsumeId);
            if (Consume == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(Consume);
        } catch (Exception e) {
            logger.error("Error in /api/Consume/{ConsumeId}:"+ConsumeId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }
    @PutMapping("/api/Consume/{ConsumeId}")
    public ResponseEntity<?> updateConsume(@PathVariable long ConsumeId, @RequestBody ConsumeDTO ConsumeDTO) {
        try {
            Consume updatedConsume = ConsumeService.updateConsumer(ConsumeId, ConsumeDTO);
            if (updatedConsume == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedConsume);
        } catch (Exception e) {
            logger.error("Error in /api/Consume/{ConsumeId}:"+ConsumeId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @PostMapping("/api/Consume/changepassword/{ConsumeId}")
    public ResponseEntity<?> changePasswordByConsumeId(@RequestBody Changepassword changepassword, @PathVariable Long ConsumeId) {
        try {
            String changePasswordForConsume = ConsumeService.changePasswordByUserId(changepassword, ConsumeId);
            return ResponseEntity.ok(changePasswordForConsume);
        } catch (Exception e) {
            logger.error("Error in /api/Consume/changepassword/{ConsumeId}:"+ConsumeId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @PostMapping("/api/Consume/changepassword")
    public ResponseEntity<?> changePasswordByConsumeEmail(@RequestBody Changepassword changepassword, @RequestParam(required = true) String email) {
        try {
            String changePasswordForConsume = ConsumeService.changePasswordByUserEmail(changepassword, email);
            return ResponseEntity.ok(changePasswordForConsume);
        } catch (Exception e) {
            logger.error("Error in /api/Consume/changepassword", e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @PostMapping("/api/Consume/forgotpassword")
    public ResponseEntity<?> sendForgotPasswordOTP(@RequestBody Email email) {
        try {
            String sendForgotPasswordOTP = ConsumeService.sendForgotPasswordOTP(email);
            return ResponseEntity.ok(sendForgotPasswordOTP);
        } catch (Exception e) {
            logger.error("Error in /api/Consume/forgotpassword", e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @PostMapping("/api/Consume/resetpassword")
    public ResponseEntity<?> resetPasswordWithOTP(@RequestBody PasswordReset passwordReset, @RequestParam(required = true) String email) {
        try {
            ConsumeService.resetPasswordWithOTP(email, passwordReset);
            return ResponseEntity.ok("Your password has been reset successfully");
        } catch (Exception e) {
            logger.error("Error in /api/Consume/resetpassword", e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

 
    @PatchMapping("/api/Consume/changerole")
    public ResponseEntity<?> changeUserRole(@RequestBody UserRoleDTO userDTO) {
        try {
            if (userDTO.getUserId() == null && userDTO.getEmail() == null) {
                return ResponseEntity.badRequest().body("Provide either userId or email");
            }
            Consume updatedConsume = ConsumeService.changeUserRole(userDTO);
            return ResponseEntity.ok(updatedConsume);
        } catch (Exception e) {
            logger.error("Error in /api/Consume/changerole", e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }
   
  
}

package com.api.filter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.api.config.UserInfoUserDetails;
import com.api.entity.Consume;
import com.api.repository.ConsumeRepo;

@Component
public class UsernamePwdAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ConsumeRepo consumeRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        List<Consume> user = getUserByUsernameOrMobileNumber(username);

        if (user != null && !user.isEmpty()) {
            if (passwordEncoder.matches(password, user.get(0).getPassword())) {
            	UserDetails userDetails = new UserInfoUserDetails(user);
            	return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());

            } else {
                throw new BadCredentialsException("Invalid password");
            }
        } else {
            throw new BadCredentialsException("Invalid email or mobile number");
        }}


    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    private  List<Consume> getUserByUsernameOrMobileNumber(String username) {
       try {
    	List<Consume> user=null;

         if (user ==null) {
	          user = consumeRepo.findByEmail(username);
	    }
         if(user.isEmpty()) {
		        user = consumeRepo.findByMobileNumber(Long.parseLong(username));
		    }
       
        return user;}
       catch (Exception e) {
    	   throw new BadCredentialsException("Invalid email or mobile number");
	}
    }
}

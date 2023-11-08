package com.api.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.api.entity.Consume;
import com.api.repository.ConsumeRepo;
@Service
public class UserInfoUserDetailsService implements UserDetailsService {

	@Autowired
	private ConsumeRepo userRepo;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//	
//    List<User> users = userRepo.findByEmailOrMobileNumber(username, Long.parseLong(username));
//        
//        if (users.isEmpty()) {
//            throw new UsernameNotFoundException("Invalid email or mobile number");
//        }
		  
		List<Consume> users = null;

		    if (users ==null) {
		          users = userRepo.findByEmail(username);
		    }
		    if(users.isEmpty()) {
		        users = userRepo.findByMobileNumber(Long.parseLong(username));
		    }

		    if (users.isEmpty()) {
		        throw new UsernameNotFoundException("Invalid email or mobile number");
		    }
		    return new UserInfoUserDetails(users);
	}
}

package com.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.entity.Consume;


public interface ConsumeRepo extends JpaRepository<Consume, Long> {
	 List<Consume> findByEmail(String email);
	 List<Consume> findByMobileNumber(Long mobileNumber);
	 List<Consume> findByEmailOrMobileNumber(String email, Long mobileNumber);
}

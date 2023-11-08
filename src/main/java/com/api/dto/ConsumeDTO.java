package com.api.dto;

import org.springframework.stereotype.Component;

import com.api.entity.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConsumeDTO {
	private String firstName;
	private String lastName;
	private String email;
	private Long mobileNumber;
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UserRole role;
}

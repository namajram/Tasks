package com.api.dto;

import com.api.entity.UserRole;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserRoleDTO {
	private Long userId;
    private String email;
    private UserRole role;
}

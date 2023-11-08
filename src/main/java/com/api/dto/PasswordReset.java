package com.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordReset {

	private String otp;
	private String newPassword;
	private String confirmNewPassword;
}

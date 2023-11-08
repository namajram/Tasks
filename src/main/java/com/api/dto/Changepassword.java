package com.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Changepassword {

	private String oldPassword;
	private String newPassword;
	private String confirmNewPassword;
	
}

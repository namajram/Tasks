package com.api.entity;

public enum UserRole {
ROLE_USER,
ROLE_ADMIN;
	
	
	 public String getAuthority() {
	        return name();
	    }

}

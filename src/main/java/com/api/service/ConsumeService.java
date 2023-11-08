package com.api.service;

import java.util.List;

import com.api.dto.Changepassword;
import com.api.dto.ConsumeDTO;
import com.api.dto.Email;
import com.api.dto.PasswordReset;
import com.api.dto.UserRoleDTO;
import com.api.entity.Consume;

import jakarta.mail.MessagingException;


public interface ConsumeService  {

	Consume createConsume(ConsumeDTO consumeDTO);

	List<Consume> getAllConsumers();

	Consume updateConsumer(long consumerId, ConsumeDTO consumeDTO);

	boolean deleteConsumer(long consumerId);

	String changePasswordByUserId(Changepassword changepassword, Long consumerId);

	Consume getConsumerById(long consumerId);

	String changePasswordByUserEmail(Changepassword changepassword, String email);

	String sendForgotPasswordOTP(Email email) throws MessagingException;

	Consume changeUserRole(UserRoleDTO userDTO);

	List<Consume> getConsumersPageSort(int pageNo, int pageSize, String sortField, String sortDirection);

	Consume getConsumerByEmail(String email);

	void resetPasswordWithOTP(String email, PasswordReset reset);


}

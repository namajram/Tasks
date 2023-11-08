package com.api.service.impl;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.dto.Changepassword;
import com.api.dto.Email;
import com.api.dto.PasswordReset;
import com.api.dto.ConsumeDTO; 
import com.api.dto.UserRoleDTO;
import com.api.entity.Consume; 
import com.api.entity.UserRole;
import com.api.exception.EmailAlreadyExistsException;
import com.api.exception.ResourceNotFoundException;
import com.api.repository.ConsumeRepo;
import com.api.service.ConsumeService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class ConsumeServiceImpl implements ConsumeService { 

    private final ConsumeRepo consumeRepo;
    private final JavaMailSender mailSender;

    
    public ConsumeServiceImpl(ConsumeRepo consumeRepo, JavaMailSender mailSender) {
        super();
        this.consumeRepo = consumeRepo;
        this.mailSender = mailSender;
    }

    @Autowired
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public Consume createConsume(ConsumeDTO consumeDTO) {
        Consume consume = new Consume();
        List<Consume> findByEmail = consumeRepo.findByEmail(consumeDTO.getEmail());
        if (!findByEmail.isEmpty()) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        consume.setEmail(consumeDTO.getEmail());
        consume.setFirstName(consumeDTO.getFirstName());
        if (consumeDTO.getRole() == null) {
            consume.setRole(UserRole.ROLE_USER);
        } else {
            consume.setRole(consumeDTO.getRole());
        }

        consume.setLastName(consumeDTO.getLastName());
        consume.setMobileNumber(consumeDTO.getMobileNumber());

        consume.setPassword(passwordEncoder().encode(consumeDTO.getPassword()));

        return consumeRepo.save(consume);
    }

    @Override
    public List<Consume> getAllConsumers() { 
        List<Consume> consumers = consumeRepo.findAll();
        if (consumers.isEmpty()) {
            throw new ResourceNotFoundException("Consumer DB is Empty");
        }
        return consumers;
    }

    @Override
    public Consume updateConsumer(long consumerId, ConsumeDTO consumeDTO) { 
        Consume existingConsumer = consumeRepo.findById(consumerId)
                .orElseThrow(() -> new ResourceNotFoundException("Consumer Id not found:" + consumerId));
        if (!consumeDTO.getEmail().equals(existingConsumer.getEmail())) {
            List<Consume> findByEmail = consumeRepo.findByEmail(consumeDTO.getEmail());
            if (!findByEmail.isEmpty()) {
                throw new EmailAlreadyExistsException("Email already exists");
            }
        }
        if (!consumeDTO.getMobileNumber().equals(existingConsumer.getMobileNumber())) {
            List<Consume> findByMobileNumber = consumeRepo.findByMobileNumber(consumeDTO.getMobileNumber());
            if (!findByMobileNumber.isEmpty()) {
                throw new EmailAlreadyExistsException("Mobile Number already exists");
            }
        }
        if (consumeDTO.getEmail() != null) {
            existingConsumer.setEmail(consumeDTO.getEmail());
        }
        if (consumeDTO.getFirstName() != null) {
            existingConsumer.setFirstName(consumeDTO.getFirstName());
        }
        if (consumeDTO.getLastName() != null) {
            existingConsumer.setLastName(consumeDTO.getLastName());
        }
        if (consumeDTO.getMobileNumber() != 0) {
            existingConsumer.setMobileNumber(consumeDTO.getMobileNumber());
        }
        return consumeRepo.save(existingConsumer);
    }

    @Override
    public boolean deleteConsumer(long consumerId) {
        Optional<Consume> optionalConsumer = consumeRepo.findById(consumerId);
        if (optionalConsumer.isPresent()) {
            consumeRepo.delete(optionalConsumer.get());
            return true;
        } else {
            throw new ResourceNotFoundException("Consumer Id not found in DB:" + consumerId);
        }
    }
    @Override
    public Consume getConsumerById(long consumerId) { 
        Optional<Consume> optionalConsumer = consumeRepo.findById(consumerId);
        return optionalConsumer.orElseThrow(() -> new ResourceNotFoundException("Consumer Id not found in DB:" + consumerId));
    }

    @Override
    public String changePasswordByUserId(Changepassword changepassword, Long consumerId) { 
        Consume consumer = consumeRepo.findById(consumerId)
                .orElseThrow(() -> new ResourceNotFoundException("Consumer Id not found in DB:" + consumerId));
        if (passwordEncoder().matches(changepassword.getOldPassword(), consumer.getPassword())) {
            if (changepassword.getNewPassword().equals(changepassword.getConfirmNewPassword())) {
                consumer.setPassword(passwordEncoder().encode(changepassword.getNewPassword()));
                consumeRepo.save(consumer);
                return "Your password has been changed successfully";
            } else {
                throw new IllegalArgumentException("New password and confirm new password do not match");
            }
        } else {
            throw new BadCredentialsException("Invalid Old password");
        }
    }

    @Override
    public String changePasswordByUserEmail(Changepassword changepassword, String email) {
        List<Consume> consumers = consumeRepo.findByEmail(email);
        if (consumers.isEmpty()) {
            throw new ResourceNotFoundException("Email not found in consumer DB:" + email);
        }

        Consume consumer = consumers.get(0);
        if (passwordEncoder().matches(changepassword.getOldPassword(), consumer.getPassword())) {
            if (changepassword.getNewPassword().equals(changepassword.getConfirmNewPassword())) {
                consumer.setPassword(passwordEncoder().encode(changepassword.getNewPassword()));
                consumeRepo.save(consumer);
                return "Your password has been changed successfully";
            } else {
                throw new IllegalArgumentException("New password and confirm new password do not match");
            }
        } else {
            throw new BadCredentialsException("Invalid Old password");
        }
    }

    @Override
    @Transactional
    public String sendForgotPasswordOTP(Email email) throws MessagingException{
        List<Consume> consumers = consumeRepo.findByEmail(email.getEmail());

        if (consumers.isEmpty()) {
            throw new ResourceNotFoundException("Email not found in consumer DB:" + email.getEmail());
        }

        Consume consumer = consumers.get(0);

        String otp = generateOTP();
        consumer.setOtp(otp);
        consumer.setOtpGeneratedTime(LocalDateTime.now());
        consumeRepo.save(consumer);

        sendOTP(consumer.getEmail(), otp);

        return "OTP sent successfully. Please reset your password within 5 minutes.";
    }


    private void sendOTP(String email, String otp) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Your OTP for Password Reset");

        String body = "Hello " + email + ",\n\n" +
                "Your OTP for password reset is: " + otp + "\n\n" +
                "OTP is valid for 5 minutes.";
        mimeMessageHelper.setText(body);

        mailSender.send(mimeMessage);
    }

    private String generateOTP() {
        return new DecimalFormat("000000").format(new Random().nextInt(999999));
    }

    @Override
    public void resetPasswordWithOTP(String email, PasswordReset reset) { 
        List<Consume> consumers = consumeRepo.findByEmail(email);
        if (consumers.isEmpty()) {
            throw new ResourceNotFoundException("Email not found in consumer DB" + email);
        }

        Consume consumer = consumers.get(0);

        if (consumer.getOtp() == null || !consumer.getOtp().equals(reset.getOtp())) {
            throw new BadCredentialsException("Invalid OTP");
        }

        LocalDateTime otpGeneratedTime = consumer.getOtpGeneratedTime();
        LocalDateTime currentDateTime = LocalDateTime.now();

        if (otpGeneratedTime.plusMinutes(5).isBefore(currentDateTime)) {
            throw new BadCredentialsException("Your OTP has expired");
        }

        if (!reset.getNewPassword().equals(reset.getConfirmNewPassword())) {
            throw new IllegalArgumentException("New password and confirm new password do not match");
        }

        consumer.setPassword(passwordEncoder().encode(reset.getNewPassword()));
        consumer.setOtp(null);
        consumer.setOtpGeneratedTime(null);
        consumeRepo.save(consumer);
    }

    @Override
    public Consume getConsumerByEmail(String email) { 
    	List<Consume> consumers = consumeRepo.findByEmail(email);
        if (consumers == null || consumers.isEmpty()) {
            throw new ResourceNotFoundException("Consumer not found with email:" + email);
        }
        Consume consumer = consumers.get(0);
        return consumer;
    }

    @Override
    public List<Consume> getConsumersPageSort(int pageNo, int pageSize, String sortField, String sortDirection) { 
    	Sort sort = sortDirection.equalsIgnoreCase("asc") ? Sort.by(sortField).ascending() :
                Sort.by(sortField).descending();
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, sort);
        Page<Consume> page = consumeRepo.findAll(pageable);

        return page.getContent();
    }

    @Override
    public Consume changeUserRole(UserRoleDTO userDTO) {
        Consume consumer = null;

        if (userDTO.getUserId() != null) {
            // Change role by userId
            consumer = changeUserRoleByUserId(userDTO.getUserId(), userDTO.getRole());
        } else if (userDTO.getEmail() != null) {
            // Change role by email
            consumer = changeUserRoleByEmail(userDTO.getEmail(), userDTO.getRole());
        }

        return consumer;
    }

    private Consume changeUserRoleByUserId(long consumerId, UserRole newRole) {
        Consume existingConsumer = consumeRepo.findById(consumerId)
                .orElseThrow(() -> new ResourceNotFoundException("Consumer Id not found: " + consumerId));

        existingConsumer.setRole(newRole);
        return consumeRepo.save(existingConsumer);
    }

    private Consume changeUserRoleByEmail(String email, UserRole newRole) {
        List<Consume> consumers = consumeRepo.findByEmail(email);

        if (consumers.isEmpty()) {
            throw new ResourceNotFoundException("Email not found in consumer DB: " + email);
        }

        Consume consumer = consumers.get(0);
        consumer.setRole(newRole);
        return consumeRepo.save(consumer);
    }
}
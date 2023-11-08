package com.api.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"mobileNumber", "email"}))
@JsonInclude(Include.NON_EMPTY)
public class Consume {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long consumeId;

    //@NotEmpty(message = "First name is required")
    
    private String firstName;

    private String lastName;

    @Column(nullable = false, unique = true)
    @NotEmpty(message = "Email is required")
    private String email;
    
   // @Column(nullable = false, unique = true)
   // @NotEmpty(message = "Mobile Number is required")
    
    private Long mobileNumber;
    @Enumerated(EnumType.STRING)
    private ConsumeStatus consumeStatus;

    //@Size(min = 8, message = "Password must contain at least 8 characters")
    //@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Password must contain at least one letter and one number")
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    
    @JsonIgnore
    private UserRole role;

    @JsonIgnore
    private String otp;
    @JsonIgnore
    private LocalDateTime otpGeneratedTime;
    

    @JsonIgnore
    @OneToMany(mappedBy = "consume", cascade = CascadeType.ALL, fetch = FetchType.LAZY)    
    private List<Cart> cart=new ArrayList<>();
    
    @JsonIgnore
    @OneToMany(mappedBy = "consume", cascade = CascadeType.ALL, fetch = FetchType.LAZY)    
    private List<Orders> order=new ArrayList<>();

    public enum ConsumeStatus {
        Active, Inactive
    }
}

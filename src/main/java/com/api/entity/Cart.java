package com.api.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "cart"})
    private Product product;

    @NotNull(message = "Quantity is required")
    private int quantity;

    @NotNull(message = "Product price is required")
    private Float productPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "cart"})
    private Consume consume;
}


package com.api.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "order_carts")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "orderCartItem"})
    private Product product;

    private Integer quantity;
    private Float productPrice;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    private LocalDate expectedDelivery;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnoreProperties(value = {"handler", "hibernateLazyInitializer", "orderCartItem"})
    private Orders order;


    public enum OrderStatus {
        Cancelled, Delivered, Problem, Processing, Returned
    }
}

package com.api.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long productId;

    @NotNull(message = "Product name is required")
    private String productName;
    private String productBrand;
    private String created;

    @NotNull(message = "Price is required")
    private Float price;

    @NotNull(message = "Product quantity is required")
    private int productQuantity;
    private String description;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private String images;
    
    @JsonIgnore
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)    
    private List<Cart> cart=new ArrayList<>();
    
 
}

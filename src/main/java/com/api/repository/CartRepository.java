package com.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.entity.Cart;
import java.util.List;
import com.api.entity.Consume;



public interface CartRepository extends JpaRepository<Cart, Long> {

List<Cart> findByConsume(Consume consume);
}

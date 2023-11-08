package com.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.entity.Orders;
import com.api.entity.OrderCartItem;

public interface OrderCartItemRepository extends JpaRepository<OrderCartItem, Long>{

	List<OrderCartItem> findByOrder(Orders order);

}

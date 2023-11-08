package com.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.entity.Orders;
import java.util.List;
import com.api.entity.Consume;


public interface OrderRepository extends JpaRepository<Orders, Long>{
	List<Orders> findByConsume(Consume consume);

}

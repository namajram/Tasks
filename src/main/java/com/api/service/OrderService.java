package com.api.service;

import java.util.List;

import com.api.entity.Orders;

public interface OrderService {



	Orders getOrderById(Long orderId);

	List<Orders> getOrdersByConsume(Long consumeId);

	void deleteOrder(Long orderId);

	Orders updateOrder(Long orderId, Orders updatedOrder);


	Orders addOrder(Long consumeId);

}

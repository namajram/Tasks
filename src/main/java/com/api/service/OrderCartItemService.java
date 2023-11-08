package com.api.service;

import java.util.List;

import com.api.entity.OrderCartItem;

public interface OrderCartItemService {

	OrderCartItem updateOrderCartItem(Long orderCartItemId, OrderCartItem updatedOrderCartItem);

	void deleteOrderCartItem(Long orderCartItemId);

	OrderCartItem getOrderCartItemById(Long orderCartItemId);

	List<OrderCartItem> getOrderCartItemsByOrder(Long orderId);

}

package com.api.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.api.entity.Orders;
import com.api.entity.OrderCartItem;
import com.api.exception.ResourceNotFoundException;
import com.api.repository.OrderCartItemRepository;
import com.api.repository.OrderRepository;
import com.api.service.OrderCartItemService;

@Service
public class OrderCartItemServiceImpl implements OrderCartItemService {
    private final OrderCartItemRepository orderCartItemRepository;
    private final OrderRepository orderRepository;

    public OrderCartItemServiceImpl(OrderCartItemRepository orderCartItemRepository, OrderRepository orderRepository) {
        this.orderCartItemRepository = orderCartItemRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderCartItem updateOrderCartItem(Long orderCartItemId, OrderCartItem updatedOrderCartItem) {
        OrderCartItem existingOrderCartItem = orderCartItemRepository.findById(orderCartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderCartItem not found with ID: " + orderCartItemId));

        
        existingOrderCartItem.setProduct(updatedOrderCartItem.getProduct());
        existingOrderCartItem.setQuantity(updatedOrderCartItem.getQuantity());
        existingOrderCartItem.setProductPrice(updatedOrderCartItem.getProductPrice());
        existingOrderCartItem.setOrderStatus(updatedOrderCartItem.getOrderStatus());
        existingOrderCartItem.setExpectedDelivery(updatedOrderCartItem.getExpectedDelivery());

      
        return orderCartItemRepository.save(existingOrderCartItem);
    }

    @Override
    public void deleteOrderCartItem(Long orderCartItemId) {
        OrderCartItem orderCartItem = orderCartItemRepository.findById(orderCartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderCartItem not found with ID: " + orderCartItemId));

        orderCartItemRepository.delete(orderCartItem);
    }

    @Override
    public OrderCartItem getOrderCartItemById(Long orderCartItemId) {
        return orderCartItemRepository.findById(orderCartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("OrderCartItem not found with ID: " + orderCartItemId));
    }

    @Override
    public List<OrderCartItem> getOrderCartItemsByOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        return orderCartItemRepository.findByOrder(order);
    }
}


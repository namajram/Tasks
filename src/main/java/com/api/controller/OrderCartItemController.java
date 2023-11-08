package com.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.entity.OrderCartItem;
import com.api.service.OrderCartItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.api.entity.OrderCartItem;
import com.api.service.OrderCartItemService;

@RestController
@RequestMapping("/api/order-cart-items")
public class OrderCartItemController {
    private static final Logger logger = LoggerFactory.getLogger(OrderCartItemController.class);

    private final OrderCartItemService orderCartItemService;

    @Autowired
    public OrderCartItemController(OrderCartItemService orderCartItemService) {
        this.orderCartItemService = orderCartItemService;
    }

    @PutMapping("/{orderCartItemId}")
    public ResponseEntity<?> updateOrderCartItem(
            @PathVariable Long orderCartItemId,
            @RequestBody OrderCartItem updatedOrderCartItem) {
        try {
            OrderCartItem updatedItem = orderCartItemService.updateOrderCartItem(orderCartItemId, updatedOrderCartItem);
            if (updatedItem == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedItem);
        } catch (Exception e) {
            logger.error("Error in PUT /api/order-cart-items/{orderCartItemId}:"+orderCartItemId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @DeleteMapping("/{orderCartItemId}")
    public ResponseEntity<?> deleteOrderCartItem(@PathVariable Long orderCartItemId) {
        try {
            orderCartItemService.deleteOrderCartItem(orderCartItemId);
            return ResponseEntity.ok("OrderCartItem with ID " + orderCartItemId + " has been deleted.");
        } catch (Exception e) {
            logger.error("Error in DELETE /api/order-cart-items/{orderCartItemId}:"+orderCartItemId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @GetMapping("/{orderCartItemId}")
    public ResponseEntity<?> getOrderCartItemById(@PathVariable Long orderCartItemId) {
        try {
            OrderCartItem orderCartItem = orderCartItemService.getOrderCartItemById(orderCartItemId);
            if (orderCartItem == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(orderCartItem);
        } catch (Exception e) {
            logger.error("Error in GET /api/order-cart-items/{orderCartItemId}:"+orderCartItemId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @GetMapping("/by-order/{orderId}")
    public ResponseEntity<?> getOrderCartItemsByOrder(@PathVariable Long orderId) {
        try {
            List<OrderCartItem> orderCartItems = orderCartItemService.getOrderCartItemsByOrder(orderId);
            if (orderCartItems.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(orderCartItems);
        } catch (Exception e) {
            logger.error("Error in GET /api/order-cart-items/by-order/{orderId}:"+orderId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }
}

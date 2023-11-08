package com.api.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.entity.Orders;
import com.api.service.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @PostMapping("/{consumeId}")
    public ResponseEntity<?> addOrder(@PathVariable Long consumeId) {
        try {
            Orders order = orderService.addOrder(consumeId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error in POST /api/order/{consumeId}:"+consumeId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Long orderId) {
        try {
            Orders order = orderService.getOrderById(orderId);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error in GET /api/order/{orderId}:"+orderId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @GetMapping("/consumer/{consumeId}")
    public ResponseEntity<?> getOrdersByConsumer(@PathVariable Long consumeId) {
        try {
            List<Orders> orders = orderService.getOrdersByConsume(consumeId);
            if (orders.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            logger.error("Error in GET /api/order/consumer/{consumeId}:"+consumeId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<?> updateOrder(@PathVariable Long orderId, @RequestBody Orders updatedOrder) {
        try {
            Orders order = orderService.updateOrder(orderId, updatedOrder);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            logger.error("Error in PUT /api/order/{orderId}:"+orderId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error in DELETE /api/order/{orderId}:"+orderId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }
}

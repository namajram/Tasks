package com.api.controller;

import com.api.entity.Cart;
import com.api.service.CartService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/cart")
public class CartController {
    private static final Logger logger = LoggerFactory.getLogger(CartController.class);

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<?> getCartItem(@PathVariable Long cartId) {
        try {
            Cart cartItem = cartService.getCartItemById(cartId);
            if (cartItem == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(cartItem);
            }
        } catch (Exception e) {
            logger.error("Error in GET /api/cart/{cartId}:"+cartId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @GetMapping("/consume/{consumeId}")
    public ResponseEntity<?> getUserCartItems(@PathVariable Long consumeId) {
        try {
            List<Cart> cartItems = cartService.getUserCartItems(consumeId);
            if (cartItems.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(cartItems);
            }
        } catch (Exception e) {
            logger.error("Error in GET /api/cart/consume/{consumeId}:consumeId="+consumeId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> addCartItem(@RequestParam long productId, @RequestParam int quantity, @RequestParam Long consumeId) {
        try {
            Cart addedCartItem = cartService.addCartItem(productId, quantity, consumeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(addedCartItem);
        } catch (Exception e) {
            logger.error("Error in POST /api/cart:consumeId="+consumeId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @PutMapping("/{cartId}")
    public ResponseEntity<?> updateCartItem(@PathVariable Long cartId, @RequestParam int newQuantity, @RequestParam Long productId, @RequestParam Long consumeId) {
        try {
            Cart updatedCartItem = cartService.updateCartItemQuantity(cartId, newQuantity, productId, consumeId);
            if (updatedCartItem == null) {
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.ok(updatedCartItem);
            }
        } catch (Exception e) {
            logger.error("Error in PUT /api/cart/{cartId}:"+cartId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> deleteCartItem(@PathVariable Long cartId) {
        try {
            cartService.deleteCartItem(cartId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error in DELETE /api/cart/{cartId}:"+cartId, e.getMessage());
            throw new RuntimeException( e.getMessage());
        }
    }
}
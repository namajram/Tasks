package com.api.service;

import java.util.List;

import com.api.entity.Cart;

public interface CartService {

	Cart getCartItemById(Long cartId);

	List<Cart> getUserCartItems(Long consumeId);

	void deleteCartItem(Long cartId);

	Cart updateCartItemQuantity(Long cartId, int newQuantity, Long productId, Long consumeId);

	Cart addCartItem(long productId, int quantity, Long consumeId);

	
}

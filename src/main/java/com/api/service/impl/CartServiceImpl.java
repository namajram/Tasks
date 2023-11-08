package com.api.service.impl;

import java.lang.ModuleLayer.Controller;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.api.entity.Cart;
import com.api.entity.Consume;
import com.api.entity.Product;
import com.api.exception.ResourceNotFoundException;
import com.api.repository.CartRepository;
import com.api.repository.ConsumeRepo;
import com.api.repository.ProductRepository;
import com.api.service.CartService;

@Service
public class CartServiceImpl implements CartService {

	private final CartRepository cartRepository;
	private final ProductRepository productRepository;
	 private final ConsumeRepo consumeRepo;
	 Logger logger = LoggerFactory.getLogger(CartServiceImpl.class);


	public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository,
			ConsumeRepo consumeRepo) {
		super();
		this.cartRepository = cartRepository;
		this.productRepository = productRepository;
		this.consumeRepo = consumeRepo;
	}

	@Override
	public Cart addCartItem(long productId, int quantity, Long consumeId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product Id not found: " + productId));
		  Consume consume = consumeRepo.findById(consumeId)
	        		.orElseThrow(() -> new ResourceNotFoundException("Consumer Id not found:" + consumeId));
		Float price = product.getPrice();
		Cart newItem = new Cart();
		newItem.setProduct(product);
		newItem.setQuantity(quantity);
		newItem.setConsume(consume);
		newItem.setProductPrice(price * quantity);

		return cartRepository.save(newItem);
	}

	@Override
	public Cart updateCartItemQuantity(Long cartId, int newQuantity, Long productId, Long consumeId) {
		Cart item = cartRepository.findById(cartId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new ResourceNotFoundException("Product Id not found: " + productId));
		  Consume consume = consumeRepo.findById(consumeId)
	        		.orElseThrow(() -> new ResourceNotFoundException("Consumer Id not found:" + consumeId));

		Float price = product.getPrice();
		item.setProduct(product);
		item.setQuantity(newQuantity);
		item.setConsume(consume);
		item.setProductPrice(price * newQuantity);
		Cart cart = cartRepository.save(item);
		return cart;
	}

	@Override
	public void deleteCartItem(Long cartId) {
		Cart item = cartRepository.findById(cartId)
				.orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
		cartRepository.delete(item);
	}

	@Override
	public Cart getCartItemById(Long cartId) {
		return cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));
	}

	@Override
	public List<Cart> getUserCartItems(Long consumeId) {
		 Consume consume = consumeRepo.findById(consumeId)
	        		.orElseThrow(() -> new ResourceNotFoundException("Consumer Id not found:" + consumeId));
		List<Cart> cartItems = cartRepository.findByConsume(consume);
				if (cartItems.isEmpty()) {
			throw new ResourceNotFoundException("Cart items not found");
		}
		return cartItems;
	}
}

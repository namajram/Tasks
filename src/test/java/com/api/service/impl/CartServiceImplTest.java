package com.api.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.api.entity.Cart;
import com.api.entity.Consume;
import com.api.entity.Product;
import com.api.repository.CartRepository;
import com.api.repository.ConsumeRepo;
import com.api.repository.ProductRepository;



@WebMvcTest(CartServiceImplTest.class)
@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ConsumeRepo consumeRepo;

    @InjectMocks
    private CartServiceImpl cartService;
	@Autowired
	private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
    	MockitoAnnotations.initMocks(this);
    	cartService = new CartServiceImpl(cartRepository,productRepository,consumeRepo);
	    this.mockMvc = MockMvcBuilders.standaloneSetup(cartService).build();
    }

    @Test
    void testAddCartItem() {
        long productId = 1L;
        int quantity = 2;
        long consumeId = 1L;
        Long cartId = 1L;
        Cart updatedCart = createCart(cartId);

        Product product = new Product();
        product.setProductId(productId);
        product.setPrice(10.0f);

        Mockito.when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(product));
        Mockito.when(consumeRepo.findById(consumeId)).thenReturn(java.util.Optional.of(new Consume()));
        Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenReturn(updatedCart);

        Cart newItem = cartService.addCartItem(productId, quantity, consumeId);


        Assertions.assertEquals(quantity, newItem.getQuantity());

    }

    @Test
    void testUpdateCartItemQuantity() {
        Cart cartItem = createCart(1L);
        int newQuantity = 5;
        Long productId = 1L;
        Long consumeId = 1L;


        Product product = new Product();
        product.setPrice(10.0f);

        Mockito.when(cartRepository.findById(cartItem.getCartId())).thenReturn(Optional.of(cartItem));
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        Mockito.when(consumeRepo.findById(consumeId)).thenReturn(Optional.of(new Consume()));
        Mockito.when(cartRepository.save(Mockito.any(Cart.class))).thenReturn(cartItem);

        Cart updatedItem = cartService.updateCartItemQuantity(cartItem.getCartId(), newQuantity,productId, consumeId);

        Assertions.assertEquals(newQuantity, updatedItem.getQuantity());
    }


    @Test
    void testDeleteCartItem() {
       
        Cart cartItem = createCart(1L);

     
        Mockito.when(cartRepository.findById(cartItem.getCartId())).thenReturn(Optional.of(cartItem));

        
        cartService.deleteCartItem(cartItem.getCartId());

        
        verify(cartRepository, times(1)).deleteById(cartItem.getCartId());
    }

    @Test
    void testGetCartItemById() {
       
        Cart cartItem = createCart(1L);

      
        Mockito.when(cartRepository.findById(cartItem.getCartId())).thenReturn(Optional.of(cartItem));

       
        Cart retrievedItem = cartService.getCartItemById(cartItem.getCartId());

    
        org.junit.jupiter.api.Assertions.assertEquals(cartItem, retrievedItem);
    }

    @Test
    void testGetUserCartItems() {
       
        Consume consume = new Consume();
        consume.setConsumeId(1L);

       
        Cart cartItem = createCart(1L);
        cartItem.setConsume(consume);

        List<Cart> cartItems = Collections.singletonList(cartItem);

       
        Mockito.when(consumeRepo.findById(consume.getConsumeId())).thenReturn(Optional.of(consume));
        Mockito.when(cartRepository.findByConsume(consume)).thenReturn(cartItems);

  
        List<Cart> retrievedCartItems = cartService.getUserCartItems(consume.getConsumeId());

       
        org.junit.jupiter.api.Assertions.assertEquals(cartItems, retrievedCartItems);
    }
    



    
    private Cart createCart(Long cartId) {
        Cart cartItem = new Cart();
        cartItem.setCartId(cartId);
        cartItem.setProduct(new Product());
        cartItem.setQuantity(1);
        cartItem.setProductPrice(10.0f);
        cartItem.setConsume(new Consume());
        return cartItem;
    }
}

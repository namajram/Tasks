package com.api.controller;

import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.api.entity.Cart;
import com.api.entity.Consume;
import com.api.entity.Product;
import com.api.service.CartService;



@WebMvcTest(CartControllerTest.class)
@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    private CartController cartController;
    
	@Autowired
	private MockMvc mockMvc;
	

	@BeforeEach
	void setUp() {
	    MockitoAnnotations.initMocks(this);
	    cartController = new CartController(cartService);
	    this.mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
	}

    @Test
    void testGetCartItem() throws Exception {
       
        Long cartId = 1L;
        Cart cartItem = createCart(cartId);
        Mockito.when(cartService.getCartItemById(cartId)).thenReturn(cartItem);

       
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart/" + cartId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartId").value(cartId)) 
                .andDo(MockMvcResultHandlers.print());
    }
    
    @Test
    void testAddCartItem() throws Exception {
       
        Long productId = 1L;
        int quantity = 2;
        Long consumeId = 1L;
        Cart cartItem = createCart(1L);
        when(cartService.addCartItem(productId, quantity, consumeId)).thenReturn(cartItem);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cart")
                .param("productId", productId.toString())
                .param("quantity", String.valueOf(quantity))
                .param("consumeId", consumeId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartId").exists()) // Adjust this to match your JSON structure
                .andDo(MockMvcResultHandlers.print());
    }
    @Test
    void testUpdateCartItem() throws Exception {
        Long cartId = 1L;
        int newQuantity = 3;
        Long productId = 2L;
        Long consumeId = 3L;
        Cart updatedCart = createCart(cartId); // Replace with your expected updated cart

        when(cartService.updateCartItemQuantity(cartId, newQuantity, productId, consumeId)).thenReturn(updatedCart);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/cart/" + cartId)
                .param("newQuantity", String.valueOf(newQuantity))
                .param("productId", productId.toString())
                .param("consumeId", consumeId.toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.cartId").value(cartId)) 
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(newQuantity)) 
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void testDeleteCartItem() throws Exception {
        Long cartId = 1L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/cart/" + cartId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());
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

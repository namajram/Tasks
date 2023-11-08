package com.api.service.impl;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.api.entity.Cart;
import com.api.entity.Consume;
import com.api.entity.Orders;
import com.api.entity.OrderCartItem;
import com.api.entity.Product;
import com.api.exception.EmailAlreadyExistsException;
import com.api.exception.ResourceNotFoundException;
import com.api.repository.CartRepository;
import com.api.repository.ConsumeRepo;
import com.api.repository.OrderCartItemRepository;
import com.api.repository.OrderRepository;
import com.api.repository.ProductRepository;
import com.api.service.OrderService;
@Service
public class OrderServiceImpl implements OrderService {


    private final OrderRepository orderRepository;
    private final ConsumeRepo consumeRepo;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderCartItemRepository cartItemRepository;


	public OrderServiceImpl(OrderRepository orderRepository, ConsumeRepo consumeRepo, CartRepository cartRepository,
			ProductRepository productRepository, OrderCartItemRepository cartItemRepository) {
		super();
		this.orderRepository = orderRepository;
		this.consumeRepo = consumeRepo;
		this.cartRepository = cartRepository;
		this.productRepository = productRepository;
		this.cartItemRepository = cartItemRepository;
	}
	
	@Override
	public Orders addOrder(Long consumeId) {
	    Consume consume = consumeRepo.findById(consumeId)
	            .orElseThrow(() -> new ResourceNotFoundException("Consumer Id not found:" + consumeId));

	    List<Cart> carts = cartRepository.findByConsume(consume);

	    if (carts.isEmpty()) {
	        throw new ResourceNotFoundException("Cart is empty for the consumer");
	    }

	    float totalAmount = 0;
	    List<OrderCartItem> orderCartItems = new ArrayList<>();

	    for (Cart cart : carts) {
	        Product product = productRepository.findById(cart.getProduct().getProductId())
	                .orElseThrow(() -> new ResourceNotFoundException("Product Id not found: " + cart.getProduct().getProductId()));

	        int quantity = product.getProductQuantity();
	        int cartQuantity = cart.getQuantity();

	        if (quantity < cartQuantity) {
	            throw new EmailAlreadyExistsException("Product is not available in sufficient quantity");
	        }

	        product.setProductQuantity(quantity - cartQuantity);
	        productRepository.save(product);

	        OrderCartItem orderCartItem = new OrderCartItem();
	       
	        orderCartItem.setProduct(product);
	        orderCartItem.setQuantity(cartQuantity);
	        orderCartItem.setProductPrice(cart.getProductPrice());
	        orderCartItem.setOrderStatus(OrderCartItem.OrderStatus.Processing);

	        totalAmount += cart.getProductPrice();
	        orderCartItems.add(orderCartItem);
	        cartRepository.delete(cart);
	    }

	    Orders order = new Orders();
	    order.setCreated(LocalDateTime.now());
	    order.setTotalAmountOrder(totalAmount);
	    order.setOrderCartItem(orderCartItems);
	    order.setConsume(consume);
	    Orders save = orderRepository.save(order);
	    
	    for (OrderCartItem orderCartItem : orderCartItems) {
	    	orderCartItem.setOrder(save);
	    	cartItemRepository.save(orderCartItem);
		}
	    Orders orderById = getOrderById(save.getOrderId());
	    return orderById;
	}

	@Override
	public Orders getOrderById(Long orderId) {
                return orderRepository.findById(orderId)
                        .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
            }
	   @Override
	    public List<Orders> getOrdersByConsume(Long consumeId) {
	        Consume consume = consumeRepo.findById(consumeId)
	                .orElseThrow(() -> new ResourceNotFoundException("Consumer not found with ID: " + consumeId));
	        
	        List<Orders> orders = orderRepository.findByConsume(consume);

	        if (orders.isEmpty()) {
	            throw new ResourceNotFoundException("No orders found for the given consumer");
	        }

	        return orders;
	    }
	 

	   @Override
	   public Orders updateOrder(Long orderId, Orders updatedOrder) {
	       Orders order = orderRepository.findById(orderId)
	               .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

	       List<OrderCartItem> orderCartItems = order.getOrderCartItem();
	       List<OrderCartItem> updatedCartItems = updatedOrder.getOrderCartItem();

	       // Use a map to quickly look up updated items by cartId
	       Map<Long, OrderCartItem> updatedItemsMap = updatedCartItems.stream()
	               .collect(Collectors.toMap(OrderCartItem::getId, Function.identity()));

	      
	       orderCartItems.forEach(cartItem -> {
	           OrderCartItem updatedCartItem = updatedItemsMap.get(cartItem.getId());
	           if (updatedCartItem != null) {
	               cartItem.setExpectedDelivery(updatedCartItem.getExpectedDelivery());
	               cartItem.setOrderStatus(updatedCartItem.getOrderStatus());
	           }
	       });

	       
	       orderRepository.save(order);

	       return order;
	   }

	    @Override
	    public void deleteOrder(Long orderId) {
	        Orders order = orderRepository.findById(orderId)
	                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

	        orderRepository.delete(order);
	    }
	
    }
        
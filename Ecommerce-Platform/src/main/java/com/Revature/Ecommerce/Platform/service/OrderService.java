package com.Revature.Ecommerce.Platform.service;

import com.Revature.Ecommerce.Platform.models.*;
import com.Revature.Ecommerce.Platform.models.*;
import com.Revature.Ecommerce.Platform.repository.*;
import com.Revature.Ecommerce.Platform.CustomExceptions.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    //ordering the complete product cart
    public Order placeOrder(Long userId, Long addressId) {

        log.info("Placing order for user {}", userId);

        Cart cart = cartService.getOrCreateCart(userId);

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cart is empty");
        }

        double subtotal = cart.getTotalPrice();
        double discount = 0.0;
        double totalAmount = subtotal - discount;

        Order order = Order.builder()
                .userId(userId)
                .subtotal(subtotal)
                .discount(discount)
                .totalAmount(totalAmount)
                .status(OrderStatus.PLACED)
                .orderDate(LocalDateTime.now())
                .addressId(addressId)
                .build();

        Order savedOrder = orderRepository.save(order);

        for (CartItem item : cart.getItems()) {
            Products product = productRepository.findById(item.getProductId()).orElseThrow(() -> new ProductNotFoundException("Product not found"));
            if(item.getQuantity()>product.getStock()) {
                throw new QuantityExceedStockException("Selected Quantity is more than the available product stock");
            }
            product.setStock(product.getStock() - item.getQuantity());
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .orderItemId(savedOrder.getOrderId())
                    .productId(product.getId())
                    .productName(product.getName())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .sellerId(product.getSellerId())
                    .city(product.getCity())
                    .order(savedOrder)
                    .build();

            orderItemRepository.save(orderItem);
        }

        cartService.clearCart(userId);

        log.info("Order placed successfully: {}", savedOrder.getOrderId());

        return savedOrder;
    }

    //buuying the product directly without adding it to the cart
    public Order buyNow(Long userId, String productId, int quantity, Long addressId){
        log.info("Buy Now triggered for user {} product {}", userId, productId);
        if(quantity<=0){
            throw new InvalidRequestException("Quantity must be greater than 0");
        }
        Products product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product not found"));
        if(quantity > product.getStock()){
            throw new InvalidRequestException("Not enough stock");
        }

        double subtotal = product.getPrice() * quantity;
        double discount = 0.0;
        double totalAmount = subtotal - discount;

        Order order = Order.builder()
                .userId(userId)
                .subtotal(subtotal)
                .discount(discount)
                .totalAmount(totalAmount)
                .status(OrderStatus.PLACED)
                .orderDate(LocalDateTime.now())
                .addressId(addressId)
                .build();

        Order savedOrder = orderRepository.save(order);

        OrderItem item = OrderItem.builder()
                .orderItemId(savedOrder.getOrderId())
                .productId(product.getId())
                .productName(product.getName())
                .quantity(quantity)
                .price(product.getPrice())
                .sellerId(product.getSellerId())
                .city(product.getCity())
                .build();

        orderItemRepository.save(item);

        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        log.info("Buy Now order placed: {}", savedOrder.getOrderId());

        return savedOrder;
    }

    //viewing all the orders
    public List<Order> getOrdersByUser(Long userId) {
        log.info("Fetching orders for user {}", userId);
        return orderRepository.findByUserId(userId);
    }

    //tracking the orders
    public Map<String, Object> getOrderDetails(Long orderId) {
        Order order=orderRepository.findById(orderId).orElseThrow(()->new OrderNotFoundException("Order not found"));
        List<OrderItem> items = orderItemRepository.findByOrderOrderId(orderId);;
        Map<String, Object> response = new HashMap<>();
        response.put("order", order);
        response.put("items", items);
        return response;
    }

    //cancelling the order
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(()->new OrderNotFoundException("Order not found"));
        if(order.getStatus()==OrderStatus.SHIPPED || order.getStatus()==OrderStatus.DELIVERED){
            throw new OrderCancelAfterShippingEXception("Cannot cancel after shipping");
        }
        order.setStatus(OrderStatus.CANCELLED);
        log.info("Order {} cancelled", orderId);
        return orderRepository.save(order);
    }

    //cancelling specific item from the cart when entire cart is ordered
    public Order cancelOrderItem(Long orderId, String productId) {
        //extracting the cart of the user
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if(order.getStatus()==OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED){
            throw new OrderCancelAfterShippingEXception("Cannot cancel after shipping");
        }

        List<OrderItem> items = orderItemRepository.findByOrderOrderId(orderId);;

        boolean found = false;
        double refundAmount = 0.0;

        for (OrderItem item : items) {

            if (item.getProductId().equals(productId) && !item.isCancelled()) {

                item.setCancelled(true);
                orderItemRepository.save(item);

                refundAmount += item.getPrice() * item.getQuantity();
                found = true;

                // Restore stock
                Products product = productRepository.findById(productId)
                        .orElseThrow(() -> new ProductNotFoundException("Product not found"));

                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);

                log.info("Cancelled item {} from order {}", productId, orderId);
            }
        }

        if (!found) {
            throw new CartItemNotFoundException("Item not found in order");
        }

        // updating the total amount after deducting the cancelled product amoiunt
        double newFinalAmount = order.getTotalAmount() - refundAmount;

        order.setTotalAmount(newFinalAmount);
        order.setTotalAmount(newFinalAmount);

        // Check if all items cancelled
        boolean allCancelled = items.stream().allMatch(OrderItem::isCancelled);

        if (allCancelled) {
            order.setStatus(OrderStatus.CANCELLED);
        }

        return orderRepository.save(order);
    }


}
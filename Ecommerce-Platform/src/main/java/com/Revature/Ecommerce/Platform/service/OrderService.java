package com.Revature.Ecommerce.Platform.service;

import com.Revature.Ecommerce.Platform.dto.OrderItemDTO;
import com.Revature.Ecommerce.Platform.dto.OrderResponseDTO;
import com.Revature.Ecommerce.Platform.models.*;
import com.Revature.Ecommerce.Platform.models.*;
import com.Revature.Ecommerce.Platform.repository.*;
import com.Revature.Ecommerce.Platform.CustomExceptions.*;

import jakarta.transaction.Transactional;
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

    public OrderResponseDTO mapToDTO(Order order) {

        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .productId(item.getProductId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .totalPrice(item.getPrice() * item.getQuantity())
                        .cancelled(item.isCancelled())
                        .build())
                .toList();

        return OrderResponseDTO.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .subtotal(order.getSubtotal())
                .discount(order.getDiscount())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .orderDate(order.getOrderDate())
                .addressId(order.getAddressId())
                .items(itemDTOs)
                .build();
    }

    //ordering the complete product cart
    @Transactional
    public OrderResponseDTO placeOrder(Long userId, Long addressId) {
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

        return mapToDTO(savedOrder);
    }

    //buuying the product directly without adding it to the cart
    @Transactional
    public OrderResponseDTO buyNow(Long userId, String productId, int quantity, Long addressId){

        log.info("Buy Now triggered for user {} product {}", userId, productId);

        if(quantity <= 0){
            throw new InvalidRequestException("Quantity must be greater than 0");
        }

        Products product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        if(product.getStock() <= 0){
            throw new InvalidRequestException("Product is out of stock");
        }

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
                .productId(product.getId())
                .productName(product.getName())
                .quantity(quantity)
                .price(product.getPrice())
                .sellerId(product.getSellerId())
                .city(product.getCity())
                .order(savedOrder)   // ⚠️ IMPORTANT (you missed this earlier)
                .build();

        orderItemRepository.save(item);

        // attach item to order (important for DTO mapping)
        savedOrder.setItems(List.of(item));

        // reduce stock
        product.setStock(product.getStock() - quantity);
        productRepository.save(product);

        log.info("Buy Now order placed: {}", savedOrder.getOrderId());

        return mapToDTO(savedOrder);
    }

    //viewing all the orders
    public List<OrderResponseDTO> getOrdersByUser(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    //tracking the orders
    public OrderResponseDTO getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        return mapToDTO(order);
    }

    //cancelling the order
    @Transactional
    public OrderResponseDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));
        if (order.getStatus() == OrderStatus.SHIPPED || order.getStatus() == OrderStatus.DELIVERED) {
            throw new OrderCancelAfterShippingEXception("Cannot cancel after shipping");
        }
        List<OrderItem> items = orderItemRepository.findByOrderOrderId(orderId);
        for(OrderItem item : items){
            if(!item.isCancelled()){
                Products product = productRepository.findById(item.getProductId())
                        .orElseThrow(() -> new ProductNotFoundException("Product not found"));
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
                item.setCancelled(true);
                orderItemRepository.save(item);
            }
        }
        order.setStatus(OrderStatus.CANCELLED);
        Order savedOrder = orderRepository.save(order);
        return mapToDTO(savedOrder);
    }

    //cancelling specific item from the cart when entire cart is ordered
    @Transactional
    public OrderResponseDTO cancelOrderItem(Long orderId, String productId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found"));

        if (order.getStatus() == OrderStatus.SHIPPED ||
                order.getStatus() == OrderStatus.DELIVERED) {
            throw new OrderCancelAfterShippingEXception("Cannot cancel after shipping");
        }

        List<OrderItem> items = orderItemRepository.findByOrderOrderId(orderId);

        boolean found = false;
        double refundAmount = 0.0;

        for (OrderItem item : items) {

            if (item.getProductId().equals(productId)) {

                if (item.isCancelled()) {
                    throw new InvalidRequestException("Item already cancelled");
                }

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

        // Update total amount
        double newFinalAmount = order.getTotalAmount() - refundAmount;
        order.setTotalAmount(newFinalAmount);   // ✅ fixed duplicate line

        // Check if all items cancelled
        boolean allCancelled = items.stream().allMatch(OrderItem::isCancelled);

        if (allCancelled) {
            order.setStatus(OrderStatus.CANCELLED);
        }

        Order savedOrder = orderRepository.save(order);

        return mapToDTO(savedOrder);   // 🔥 only change needed for DTO
    }

}
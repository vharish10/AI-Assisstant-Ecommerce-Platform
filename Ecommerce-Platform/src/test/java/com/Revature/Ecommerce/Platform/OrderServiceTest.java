package com.Revature.Ecommerce.Platform;

import com.Revature.Ecommerce.Platform.models.*;
import com.Revature.Ecommerce.Platform.repository.*;
import com.Revature.Ecommerce.Platform.CustomExceptions.*;

import com.Revature.Ecommerce.Platform.service.CartService;
import com.Revature.Ecommerce.Platform.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    private Cart cart;
    private CartItem cartItem;
    private Products product;

    @BeforeEach
    void setup() {
        cartItem = CartItem.builder()
                .productId("p1")
                .price(100.0)
                .quantity(2)
                .build();

        cart = Cart.builder()
                .userId(1L)
                .items(List.of(cartItem))
                .totalPrice(200.0)
                .build();

        product = Products.builder()
                .id("p1")
                .name("Phone")
                .price(100.0)
                .stock(10)
                .sellerId(1L)
                .city("Chennai")
                .build();
    }

    @Test           //Order Success
    void testplaceOrdersuccess() {
        when(cartService.getOrCreateCart(1L)).thenReturn(cart);
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.placeOrder(1L, 101L);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any());
        verify(orderItemRepository, times(1)).save(any());
    }

    @Test
    void testplaceOrderEmptyCart() {
        cart.setItems(new ArrayList<>());
        when(cartService.getOrCreateCart(1L)).thenReturn(cart);
        assertThrows(EmptyCartException.class,()->orderService.placeOrder(1L, 101L));
    }

    @Test
    void testPlaceOrderProductNotFound() {
        when(cartService.getOrCreateCart(1L)).thenReturn(cart);
        when(productRepository.findById("p1")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class,()->orderService.placeOrder(1L, 101L));
    }

    @Test
    void testPlaceOrderInsufficientStock() {
        product.setStock(1);

        when(cartService.getOrCreateCart(1L)).thenReturn(cart);
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        assertThrows(RuntimeException.class,
                () -> orderService.placeOrder(1L, 101L));
    }

    @Test
    void testBuyNowSuccess() {
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.buyNow(1L, "p1", 2, 101L);

        assertNotNull(result);
        verify(orderRepository).save(any());
        verify(orderItemRepository).save(any());
    }

    @Test
    void testBuyNowInvalidQuantity() {
        assertThrows(InvalidRequestException.class, () -> orderService.buyNow(1L, "p1", 0, 101L));
    }

    @Test
    void testBuyNowProductNotFound() {
        when(productRepository.findById("p1")).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> orderService.buyNow(1L, "p1", 1, 101L));
    }

    @Test
    void testBuyNowInsufficientStock() {
        product.setStock(1);
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        assertThrows(InvalidRequestException.class,()->orderService.buyNow(1L, "p1", 5, 101L));
    }

    @Test
    void testGetOrderDetailsSuccess() {
        Order order = Order.builder().orderId(1L).build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderOrderId(1L)).thenReturn(List.of());

        Map<String, Object> result = orderService.getOrderDetails(1L);

        assertNotNull(result);
        assertTrue(result.containsKey("order"));
        assertTrue(result.containsKey("items"));
    }

    @Test
    void testGetOrderDetailsNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class,()->orderService.getOrderDetails(1L));
    }

    @Test
    void testCancelOrderSuccess() {
        Order order = Order.builder()
                .orderId(1L)
                .status(OrderStatus.PLACED)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        Order result = orderService.cancelOrder(1L);

        assertEquals(OrderStatus.CANCELLED, result.getStatus());
    }

    @Test
    void testCancelOrderAfterShipped() {
        Order order = Order.builder()
                .orderId(1L)
                .status(OrderStatus.SHIPPED)
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(OrderCancelAfterShippingEXception.class,()->orderService.cancelOrder(1L));
    }

    @Test
    void testCancelOrderItemSuccess() {
        Order order = Order.builder()
                .orderId(1L)
                .status(OrderStatus.PLACED)
                .totalAmount(200.0)
                .build();

        OrderItem item = OrderItem.builder()
                .productId("p1")
                .price(100.0)
                .quantity(1)
                .isCancelled(false)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderOrderId(1L)).thenReturn(List.of(item));
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenReturn(order);

        Order result = orderService.cancelOrderItem(1L, "p1");

        assertNotNull(result);
        verify(orderItemRepository).save(any());
    }

    @Test
    void testCancelOrderItemNotFound() {
        Order order = Order.builder()
                .orderId(1L)
                .status(OrderStatus.PLACED)
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderOrderId(1L)).thenReturn(new ArrayList<>());

        assertThrows(CartItemNotFoundException.class,()->orderService.cancelOrderItem(1L, "p1"));
    }

    @Test
    void testCancelOrderItemAfterShipped() {
        Order order = Order.builder()
                .orderId(1L)
                .status(OrderStatus.SHIPPED)
                .build();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        assertThrows(OrderCancelAfterShippingEXception.class,()->orderService.cancelOrderItem(1L, "p1"));
    }
}
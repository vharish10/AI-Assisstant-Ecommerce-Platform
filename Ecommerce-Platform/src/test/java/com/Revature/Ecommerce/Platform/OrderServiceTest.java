package com.Revature.Ecommerce.Platform;

import com.Revature.Ecommerce.Platform.dto.OrderResponseDTO;
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

        when(orderRepository.save(any())).thenAnswer(invocation -> {
            Order o = invocation.getArgument(0);
            o.setOrderId(1L);
            o.setItems(new ArrayList<>());   // ✅ prevent NPE
            return o;
        });

        when(orderItemRepository.save(any())).thenAnswer(invocation -> {
            OrderItem item = invocation.getArgument(0);
            Order order = item.getOrder();
            if (order.getItems() == null) {
                order.setItems(new ArrayList<>());
            }
            order.getItems().add(item);   // ✅ attach items for DTO
            return item;
        });

        OrderResponseDTO result = orderService.placeOrder(1L, 101L);

        assertNotNull(result);
        verify(orderRepository, times(1)).save(any());
        verify(orderItemRepository, times(1)).save(any());
    }

    @Test           //placing an order when cart is empty
    void testplaceOrderEmptyCart() {
        //making the cart empty
        cart.setItems(new ArrayList<>());
        when(cartService.getOrCreateCart(1L)).thenReturn(cart);
        assertThrows(EmptyCartException.class,()->orderService.placeOrder(1L, 101L));
    }

    @Test           //trying to place an order product is not found
    void testPlaceOrderProductNotFound() {
        when(cartService.getOrCreateCart(1L)).thenReturn(cart);
        when(productRepository.findById("p1")).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class,()->orderService.placeOrder(1L, 101L));
    }

    @Test           //placing an order when available stock is less the product quantity selected
    void testPlaceOrderInsufficientStock() {
        product.setStock(1);
        when(cartService.getOrCreateCart(1L)).thenReturn(cart);
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        assertThrows(QuantityExceedStockException.class, () -> orderService.placeOrder(1L, 101L));
    }

    @Test           //buying only one product without adding it to the cart
    void testBuyNowSuccess() {
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));
        when(orderItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        OrderResponseDTO result = orderService.buyNow(1L, "p1", 2, 101L);

        assertNotNull(result);
        verify(orderRepository).save(any());
        verify(orderItemRepository).save(any());
    }

    @Test           //Quantity validation
    void testBuyNowInvalidQuantity() {
        assertThrows(InvalidRequestException.class,()->orderService.buyNow(1L, "p1", 0, 101L));
    }

    @Test           //trying to buy the product that is unavailable
    void testBuyNowProductNotFound() {
        when(productRepository.findById("p1")).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> orderService.buyNow(1L, "p1", 1, 101L));
    }

    @Test               //trying to buy one product when the product stock is unavailable orlimited
    void testBuyNowInsufficientStock() {
        product.setStock(1);
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        assertThrows(InvalidRequestException.class,()->orderService.buyNow(1L, "p1", 5, 101L));
    }

    @Test           //order detatils
    void testGetOrderDetailsSuccess() {
        Order order = Order.builder()
                .orderId(1L)
                .status(OrderStatus.PLACED)   // ✅ required
                .items(new ArrayList<>())     // ✅ required
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponseDTO result = orderService.getOrderDetails(1L);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
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
                .items(new ArrayList<>())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderItemRepository.findByOrderOrderId(1L)).thenReturn(new ArrayList<>());
        when(orderRepository.save(any())).thenReturn(order);

        OrderResponseDTO result = orderService.cancelOrder(1L);

        assertEquals("CANCELLED", result.getStatus());
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
                .items(new ArrayList<>())
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

        OrderResponseDTO result = orderService.cancelOrderItem(1L, "p1");

        assertNotNull(result);
        verify(orderItemRepository).save(any());
    }

    @Test
    void testCancelOrderItemNotFound() {
        Order order = Order.builder()
                .orderId(1L)
                .status(OrderStatus.PLACED)
                .items(new ArrayList<>())
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
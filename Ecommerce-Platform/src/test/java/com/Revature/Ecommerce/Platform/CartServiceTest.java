package com.Revature.Ecommerce.Platform;

import com.Revature.Ecommerce.Platform.CustomExceptions.*;
import com.Revature.Ecommerce.Platform.dto.CartResponseDTO;
import com.Revature.Ecommerce.Platform.models.Cart;
import com.Revature.Ecommerce.Platform.models.Products;
import com.Revature.Ecommerce.Platform.repository.CartRepository;
import com.Revature.Ecommerce.Platform.repository.ProductRepository;
import com.Revature.Ecommerce.Platform.service.CartService;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService service;
    private Cart cart;
    private Products product;

    @BeforeEach
    void setUp() {
        product = Products.builder()
                .id("p1")
                .name("IPhone")
                .price(50000.0)
                .stock(10)
                .build();
    }

    @Test               //Creation of cart if not exist
    void testCreateCart(){
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArgument(0));

        cart=service.getOrCreateCart(1L);
        System.out.println("Cart:"+cart);
        assertNotNull(cart);
        assertEquals(0,cart.getItems().size());
    }

    @Test       //Checking if we can add a product to the cart
    void testAddToCartNewItem() {
        //stubbing the mopckrepositories
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

        CartResponseDTO cart = service.addToCart(1L, "p1", 2);

        System.out.println("Cart:"+cart);

        assertEquals(1,cart.getItems().size());
        assertEquals(2,cart.getItems().get(0).getQuantity());
        assertEquals(100000.0,cart.getTotalPrice());
    }

    @Test           //checking if multiple products can be added into the cart
    void testAddMultipleProductsToCart() {
        Cart storedCart = new Cart();
        //stubbing the findByUserId cart so that if cart already exist  then use that , else create an empty cart
        when(cartRepository.findByUserId(1L)).thenAnswer(invocation->{
            if (storedCart.getItems() == null || storedCart.getItems().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(storedCart);
        });
        //stubbing the save method in cartRepository
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart saved = invocation.getArgument(0);
            storedCart.setUserId(saved.getUserId());
            storedCart.setItems(saved.getItems());
            storedCart.setTotalPrice(saved.getTotalPrice());
            return storedCart;
        });

        Products p1 = Products.builder()
                .id("p1")
                .name("iPhone")
                .price(50000.0)
                .stock(10)
                .build();

        Products p2 = Products.builder()
                .id("p2")
                .name("Samsung")
                .price(30000.0)
                .stock(10)
                .build();

        when(productRepository.findById("p1")).thenReturn(Optional.of(p1));
        when(productRepository.findById("p2")).thenReturn(Optional.of(p2));

        service.addToCart(1L, "p1", 2);
        CartResponseDTO cart = service.addToCart(1L, "p2", 1);

        System.out.println("Cart: " + cart);

        assertEquals(2, cart.getItems().size());
        assertEquals(130000.0, cart.getTotalPrice());
    }

    @Test
    void testViewCartSuccess() {
        Cart storedCart=new Cart();
        when(cartRepository.findByUserId(1L)).thenAnswer(invocation -> {
            if(storedCart.getItems() == null || storedCart.getItems().isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(storedCart);
        });

        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart saved = invocation.getArgument(0);
            storedCart.setUserId(saved.getUserId());
            storedCart.setItems(saved.getItems());
            storedCart.setTotalPrice(saved.getTotalPrice());
            return storedCart;
        });

        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        service.addToCart(1L, "p1", 2);
        CartResponseDTO result = service.viewCart(1L);
        assertNotNull(result);
        assertEquals(1L, result.getUserId());
        assertEquals(1, result.getItems().size());
    }

    @Test
    void testClearCartSuccess() {
        Cart storedCart = new Cart();
        when(cartRepository.findByUserId(1L))
                .thenAnswer(invocation -> {
                    if (storedCart.getUserId() == null) {
                        return Optional.empty();
                    }
                    return Optional.of(storedCart);
                });

        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> {
                    Cart saved = invocation.getArgument(0);
                    storedCart.setUserId(saved.getUserId());
                    storedCart.setItems(saved.getItems());
                    storedCart.setTotalPrice(saved.getTotalPrice());
                    return storedCart;
                });

        when(productRepository.findById("p1")).thenReturn(Optional.of(product));

        service.addToCart(1L, "p1", 2);
        System.out.println("Cart:"+storedCart);
        service.clearCart(1L);
        CartResponseDTO result = service.viewCart(1L);
        System.out.println("Cart: " + result);
        assertNotNull(result);
        assertTrue(result.getItems().isEmpty());
        assertEquals(0.0, result.getTotalPrice());
    }

    @Test
    void testRemoveItem(){
        Cart storedCart = new Cart();
        when(cartRepository.findByUserId(1L)).thenAnswer(invocation -> {
            if (storedCart.getUserId() == null) {
                return Optional.empty();
            }
            return Optional.of(storedCart);
        });
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> {
            Cart saved = invocation.getArgument(0);
            storedCart.setUserId(saved.getUserId());
            storedCart.setItems(saved.getItems());
            storedCart.setTotalPrice(saved.getTotalPrice());
            return storedCart;
        });
        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        service.addToCart(1L, "p1", 2);
        System.out.println("Cart:"+storedCart);
        CartResponseDTO result = service.removeItem(1L,"p1");
        System.out.println("Cart:"+result);
        assertEquals(1L,result.getUserId());
    }

    @Test
    void testRemoveItemCartNotFound() {
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertThrows(CartNotFoundException.class,
                ()->{service.removeItem(1L, "p1");
                });
    }

    @Test           //test for product quantity updates
    void testUpdateCartProductQuantity(){
        Cart storedCart = new Cart();
        when(cartRepository.findByUserId(1L))
                .thenAnswer(invocation -> {
                    if (storedCart.getUserId() == null) {
                        return Optional.empty();
                    }
                    return Optional.of(storedCart);
                });

        when(cartRepository.save(any(Cart.class)))
                .thenAnswer(invocation -> {
                    Cart saved = invocation.getArgument(0);
                    storedCart.setUserId(saved.getUserId());
                    storedCart.setItems(saved.getItems());
                    storedCart.setTotalPrice(saved.getTotalPrice());
                    return storedCart;
                });

        when(productRepository.findById("p1")).thenReturn(Optional.of(product));
        service.addToCart(1L,"p1",3);
        System.out.println("Cart:"+storedCart);
        int oldQuantity = storedCart.getItems().get(0).getQuantity();
        CartResponseDTO result = service.updateQuantity(1L,"p1",1);
        System.out.println("Cart:"+result);
        int newQuantity = result.getItems().get(0).getQuantity();
        assertNotEquals(oldQuantity,newQuantity);
        assertEquals(1, newQuantity);
    }

    @Test           //Test for throwing RuntimeException when the product quantity in cart is less than quantity to be deleted
    void testThrowProductStockNotEnough(){
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.empty());
        assertThrows(CartNotFoundException.class, () -> {
            service.updateQuantity(1L, "p1", 5);
        });
    }
}
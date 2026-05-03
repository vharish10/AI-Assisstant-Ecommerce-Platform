package com.Revature.Ecommerce.Platform.CustomExceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message){
        super(message);
    }
}

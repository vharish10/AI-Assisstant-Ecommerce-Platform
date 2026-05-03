package com.Revature.Ecommerce.Platform.CustomExceptions;

public class OrderCancelAfterShippingEXception extends RuntimeException{
    public OrderCancelAfterShippingEXception(String message){
        super(message);
    }
}
